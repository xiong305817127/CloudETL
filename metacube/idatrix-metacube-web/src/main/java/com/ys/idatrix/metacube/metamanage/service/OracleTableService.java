package com.ys.idatrix.metacube.metamanage.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Preconditions;
import com.ys.idatrix.metacube.api.beans.PageResultBean;
import com.ys.idatrix.metacube.metamanage.domain.Metadata;
import com.ys.idatrix.metacube.metamanage.mapper.MetadataMapper;
import com.ys.idatrix.metacube.metamanage.vo.request.MetadataSearchVo;
import com.ys.idatrix.metacube.metamanage.vo.request.OracleTableVO;
import com.ys.idatrix.metacube.metamanage.vo.request.TableConstraintVO;
import com.ys.idatrix.metacube.metamanage.vo.request.TableVO;
import org.springframework.beans.BeanUtils;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName OracleTableService
 * @Description oracle table 服务层 API
 * @Author ouyang
 * @Date
 */
public interface OracleTableService {

    MetadataMapper getMetadataMapper();

    // 搜索
    default PageResultBean<TableVO> search(MetadataSearchVo searchVO) {
        Preconditions.checkNotNull(searchVO, "请求参数为空");
        /*if (searchVO.getRenterId() == null) {
            searchVO.setRenterId(UserUtils.getRenterId());
        }*/
        // 分页
        PageHelper.startPage(searchVO.getPageNum(), searchVO.getPageSize());
        List<Metadata> list = getMetadataMapper().search(searchVO);
        PageInfo<Metadata> pageInfo = new PageInfo<>(list);

        // 遍历封装成需要的对象
        List<TableVO> result = new ArrayList<>();
        for (Metadata metadata : list) {
            TableVO vo = new TableVO();
            BeanUtils.copyProperties(metadata, vo);
            result.add(vo);
        }
        return PageResultBean.builder(pageInfo.getTotal(), result, searchVO.getPageNum(), searchVO.getPageSize());
    }

    // 根据ID查询数据
    OracleTableVO searchById(Long tableId);

    // 根据schema查询实体数据库中的表空间
    List<String> findTablespaceListBySchemaId(Long schemaId);

    // 根据schema查询实体数据库中的序列
    List<String> findSequenceListBySchemaId(@NotNull Long schemaId);

    // 根据tableId查询可参考的约束
    List<TableConstraintVO> findConstraintByTableId(Long tableId);

    // 新增表数据 ，并生效
    void add(OracleTableVO oracleTable);
    
    // 新增直采数据库
    void addMiningTable(OracleTableVO oracleTable );
    
    // 新增表数据,只插入数据库不生成表
	 void addTable(OracleTableVO oracleTable) ;
	 
    // 修改表数据
    void update(OracleTableVO oracleTable);

    // 保存表为草稿
    void addDraft(OracleTableVO oracleTable);

    // 修改草稿表
    void updateDraft(OracleTableVO oracleTable);

    // 删除
    void delete(List<Long> idList);

    List<TableVO> searchBySchemaId(Long schemaId);
}