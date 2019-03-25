package com.ys.idatrix.metacube.metamanage.service;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Preconditions;
import com.ys.idatrix.metacube.api.beans.DatabaseTypeEnum;
import com.ys.idatrix.metacube.api.beans.PageResultBean;
import com.ys.idatrix.metacube.common.enums.DataStatusEnum;
import com.ys.idatrix.metacube.common.utils.UserUtils;
import com.ys.idatrix.metacube.metamanage.domain.Metadata;
import com.ys.idatrix.metacube.metamanage.domain.TableColumn;
import com.ys.idatrix.metacube.metamanage.domain.ViewDetail;
import com.ys.idatrix.metacube.metamanage.mapper.MetadataMapper;
import com.ys.idatrix.metacube.metamanage.mapper.ViewDetailMapper;
import com.ys.idatrix.metacube.metamanage.vo.request.DBViewVO;
import com.ys.idatrix.metacube.metamanage.vo.request.MetadataSearchVo;
import com.ys.idatrix.metacube.metamanage.vo.request.ViewVO;
import com.ys.idatrix.metacube.sysmanage.service.ThemeService;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @ClassName ViewService
 * @Description
 * @Author ouyang
 * @Date
 */
@Transactional
public interface ViewService {

    // 搜索
    default PageResultBean<ViewVO> search(MetadataSearchVo searchVO) {
        Preconditions.checkNotNull(searchVO, "请求参数为空");
        if (searchVO.getRenterId() == null) {
            searchVO.setRenterId(UserUtils.getRenterId());
        }
        // 分页
        PageHelper.startPage(searchVO.getPageNum(), searchVO.getPageSize());
        List<Metadata> list = getMetadataMapper().search(searchVO);
        PageInfo<Metadata> pageInfo = new PageInfo<>(list);

        // 遍历封装成需要的对象
        List<ViewVO> result = new ArrayList<>();
        for (Metadata metadata : list) {
            ViewVO vo = new ViewVO();
            BeanUtils.copyProperties(metadata, vo);
            result.add(vo);
        }
        return PageResultBean.builder(pageInfo.getTotal(), result, searchVO.getPageNum(), searchVO.getPageSize());
    }

    // 根据ID查询视图
    default DBViewVO searchById(Long viewId) {
        // 视图基本信息
        Metadata view = getMetadataMapper().findById(viewId);
        // view详细信息
        ViewDetail viewDetail = getViewDetailMapper().findByViewId(view.getId());
        DBViewVO result = new DBViewVO(view, viewDetail);
        return result;
    }

    // 保存视图,并且生效到数据库中
    default void add(DBViewVO view) {
        // 修改当前表为生效状态
        view.setStatus(DataStatusEnum.VALID.getValue());
        // 保存到元数据数据库中
        addView(view);
        // 视图生效到数据库并生成快照信息
        generateOrUpdateView(view.getId());
        // 数据地图保存视图节点
        getGraphSyncService().graphSaveViewNode(view.getId());
    }
    
    // 保存直采视图
    default void addMiningView(DBViewVO view ) {
        // 修改当前表为生效状态
        view.setStatus(DataStatusEnum.VALID.getValue());
        // 保存到元数据数据库中
        addView(view);
        // 保存快照信息
        saveViewSnapshot(view.getId());
        // 保存视图列信息到数据库
        Metadata meta = getMetadataMapper().findById(view.getId());
		if( meta != null ) {
			saveOrUpdateViewColumns(meta);
		}
        // 数据地图保存视图节点
        getGraphSyncService().graphSaveViewNode(view.getId());
    }

    // 修改视图,并且生效到数据库中
    default void update(DBViewVO view) {
        // 修改当前表为生效状态
        view.setStatus(DataStatusEnum.VALID.getValue());
        // 修改元数据信息
        updateView(view);
        // 视图修改生效到数据库并生成快照信息
        generateOrUpdateView(view.getId());
    }

    // 保存草稿，不生效
    default void addDraft(DBViewVO view) {
        // 标识当前为草稿，不生效
        view.setStatus(DataStatusEnum.DRAFT.getValue());
        // 保存到元数据数据库中
        addView(view);
    }

    // 修改草稿，不生效
    default void updateDraft(DBViewVO view) {
        // 修改为草稿或修改草稿，标识不生效
        view.setStatus(DataStatusEnum.DRAFT.getValue());
        // 修改
        updateView(view);
    }

    // 删除视图或草稿
    default void delete(List<Long> idList) {
        List<String> viewNames = new ArrayList<>();
        // 保存下某个表信息
        Metadata copy = null;
        for (Long id : idList) {
            Metadata view = getMetadataMapper().findById(id);
            // 删除视图或草稿视图
            getMetadataMapper().delete(id);
            // 删除实体详情
            getViewDetailMapper().deleteByViewId(id);
            // 使用主题递减
            getThemeService().decreaseProgressively(view.getThemeId());
            // 如果当前为草稿，删除就此结束
            if (view.getStatus() == DataStatusEnum.DRAFT.getValue()) {
                continue;
            }
            // 删除数据地图的视图节点
            getGraphSyncService().graphDeleteViewNode(id);
            // 如果为直采
            if (view.getIsGather()) {
                continue;
            }
            // 如果不为草稿，也不为直采，需要删除数据库中的视图
            viewNames.add(view.getName());
            // 记录下一个数据
            if (copy == null) {
                copy = view;
            }
        }
        // 将删除生效到数据库中
        if (CollectionUtils.isNotEmpty(viewNames)) {
            deleteGoToDatabase(viewNames, copy);
        }
    }

    default void addView(DBViewVO view) {
        // 补全参数
        view.setDatabaseType(getDatabaseType().getCode()); // 数据库类型
        view.setResourceType(2); // 资源是视图
        view.setVersion(1); // 版本号
        Long renterId = UserUtils.getRenterId(); // 当前租户id
        String creator = UserUtils.getUserName();// 当前创建人
        Date createTime = new Date();// 当前创建时间
        view.setRenterId(renterId);
        view.setCreator(creator);
        view.setCreateTime(createTime);
        view.setModifier(creator);
        view.setModifyTime(createTime);
        if (view.getIsGather() == null) {
            view.setIsGather(false); // 非直采数据
        }

        // 参数校验
        validatedView(view);

        // view base info insert
        Metadata metadata = new Metadata();
        BeanUtils.copyProperties(view, metadata);
        getMetadataMapper().insertSelective(metadata);

        view.setId(metadata.getId()); // 保存下viewId

        // view detail info insert
        ViewDetail detail = view.getViewDetail();
        detail.setViewId(view.getId());
        getViewDetailMapper().insertSelective(detail);

        // 主题使用次数递增
        getThemeService().increaseProgressively(view.getThemeId());
    }

    default void updateView(DBViewVO viewVo) {
        // 补全参数
        Metadata metadata = getMetadataMapper().findById(viewVo.getId());
        if (metadata.getStatus().equals(DataStatusEnum.VALID.getValue())) {
            viewVo.setVersion(metadata.getVersion() + 1); // 如果之前不是草稿，修改即版本号加1
        }
        String modifier = UserUtils.getUserName();
        Date modifyTime = new Date();
        Metadata view = new Metadata();
        BeanUtils.copyProperties(viewVo, view);
        view.setModifier(modifier);
        view.setModifyTime(modifyTime);

        // 参数校验
        validatedView(viewVo);

        // 视图基本信息修改
        getMetadataMapper().updateByPrimaryKeySelective(view);

        // 修改详情
        ViewDetail detail = viewVo.getViewDetail();
        getViewDetailMapper().updateByPrimaryKeySelective(detail);

        // 主题使用次数修改
        if (!metadata.getThemeId().equals(viewVo.getThemeId())) {
            // 先递减
            getThemeService().decreaseProgressively(metadata.getThemeId());
            // 再递增
            getThemeService().increaseProgressively(metadata.getThemeId());
        }
    }

    // 保存或更新视图的字段
    default void saveOrUpdateViewColumns(Metadata view) {
        // 先删除所有的视图字段，后新增
        // 删除
        getColumnService().deleteByTableId(view.getId());
        // 新增
        String creator = UserUtils.getUserName();
        Date createTime = new Date();
        List<TableColumn> viewColumns = getDirectMiningService().getViewColumns(view.getSchemaId(), view.getName(), null);
        getColumnService().insertColumnList(viewColumns, view.getId(), creator, createTime);
    }

    MetadataMapper getMetadataMapper();

    ViewDetailMapper getViewDetailMapper();

    McDirectMiningService getDirectMiningService();

    TableColumnService getColumnService();

    ThemeService getThemeService();

    DatabaseTypeEnum getDatabaseType();

    GraphSyncService getGraphSyncService();

    // 校验视图
    void validatedView(DBViewVO viewVo);

    // 生成或修改视图生效到数据库中
    void generateOrUpdateView(Long id);

    // 删除生效到数据库中
    void deleteGoToDatabase(List<String> viewNames, Metadata copy);

    // 保存视图快照信息
    default void saveViewSnapshot(Long id) {
        // view 基本信息
        Metadata view = getMetadataMapper().findById(id);
        // view详细信息
        ViewDetail viewDetail = getViewDetailMapper().findByViewId(view.getId());
        // 视图生效到数据库后，再去生成快照信息
        getSnapshotService().createViewSnapshot(view, viewDetail, "直采");
    }

    MysqlSnapshotService getSnapshotService();
}