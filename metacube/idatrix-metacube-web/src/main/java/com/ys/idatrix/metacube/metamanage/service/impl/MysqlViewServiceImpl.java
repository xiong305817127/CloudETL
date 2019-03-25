package com.ys.idatrix.metacube.metamanage.service.impl;

import com.ys.idatrix.metacube.api.beans.DatabaseTypeEnum;
import com.ys.idatrix.metacube.metamanage.domain.Metadata;
import com.ys.idatrix.metacube.metamanage.domain.ViewDetail;
import com.ys.idatrix.metacube.metamanage.mapper.MetadataMapper;
import com.ys.idatrix.metacube.metamanage.mapper.ViewDetailMapper;
import com.ys.idatrix.metacube.metamanage.service.*;
import com.ys.idatrix.metacube.metamanage.vo.request.AlterSqlVO;
import com.ys.idatrix.metacube.metamanage.vo.request.DBViewVO;
import com.ys.idatrix.metacube.sysmanage.service.ThemeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName MysqlViewServiceImpl
 * @Description mysql 视图服务层api实现
 * @Author ouyang
 * @Date
 */
@Slf4j
@Transactional
@Service("mysqlViewService")
public class MysqlViewServiceImpl implements ViewService, ApplicationContextAware {

    @Autowired
    private GraphSyncService graphSyncService;

    @Autowired
    private TableColumnService columnService;

    @Autowired
    private MysqlValidatedService validatedService;

    @Autowired
    private MysqlSnapshotService snapshotService;

    @Autowired
    private MySqlDDLService mySqlDDLService;

    @Autowired
    private ThemeService themeService;

    @Autowired
    private MetadataMapper metadataMapper;

    @Autowired
    private ViewDetailMapper viewDetailMapper;

    private ApplicationContext applicationContext;

    private McDirectMiningService directMiningService;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public MetadataMapper getMetadataMapper() {
        return metadataMapper;
    }

    @Override
    public ViewDetailMapper getViewDetailMapper() {
        return viewDetailMapper;
    }

    @Override
    public McDirectMiningService getDirectMiningService() {
        if(directMiningService == null) {
            directMiningService = applicationContext.getBean(McDirectMiningService.class);
        }
        return directMiningService;
    }

    @Override
    public TableColumnService getColumnService() {
        return columnService;
    }

    @Override
    public ThemeService getThemeService() {
        return themeService;
    }

    @Override
    public DatabaseTypeEnum getDatabaseType() {
        return DatabaseTypeEnum.MYSQL;
    }

    @Override
    public GraphSyncService getGraphSyncService() {
        return graphSyncService;
    }

    @Override
    public void validatedView(DBViewVO viewVo) {
        validatedService.validatedView(viewVo);
    }

    @Override
    public MysqlSnapshotService getSnapshotService() {
        return snapshotService;
    }

    @Override
    public void generateOrUpdateView(Long id) {
        // view 基本信息
        Metadata view = metadataMapper.findById(id);
        // view详细信息
        ViewDetail viewDetail = viewDetailMapper.findByViewId(view.getId());
        // 快照详情
        String details = "初始化视图";
        // 要执行的sql
        ArrayList<String> commands = null;

        if (view.getVersion() <= 1) { // create
            String sql = mySqlDDLService.getCreateOrUpdateViewSql(view.getName(), viewDetail);
            log.info("createViewSql：{}", sql);
            commands = new ArrayList<>();
            commands.add(sql);
        } else { // update
            // 查询出上一版本数据
            Integer version = view.getVersion() - 1;
            Metadata snapshotViewInfo = snapshotService.getSnapshotTableInfoByTableId(id, version); // 旧版本视图基本信息
            ViewDetail snapshotViewDetail = snapshotService.getSnapshotViewDetail(id, version); // 旧版本视图详情信息
            DBViewVO snapshotMySqlView = new DBViewVO(snapshotViewInfo, snapshotViewDetail); // 旧版本视图信息

            DBViewVO newMySqlView = new DBViewVO(view, viewDetail); // 新版本视图信息

            // 获取修改视图的sql
            AlterSqlVO alterSqlVo = mySqlDDLService.getAlterViewSql(snapshotMySqlView, newMySqlView);
            details = alterSqlVo.getMessage();
            if (StringUtils.isBlank(details)) {
                details = "当前数据没有任何的修改";
            }
            List<String> changeSql = alterSqlVo.getChangeSql();
            commands = new ArrayList<>();
            commands.addAll(changeSql);
        }

        // 视图生效到数据库
        mySqlDDLService.goToDatabase(view, commands);

        // 视图生效到数据库后，再去生成快照信息
        snapshotService.createViewSnapshot(view, viewDetail, details);

        // 生效完成后需要将视图所能查询的字段保存到元数据数据库中
        saveOrUpdateViewColumns(view);
    }

    @Override
    public void deleteGoToDatabase(List<String> viewNames, Metadata copy) {
        // 获取drop的语句
        List<String> commands = mySqlDDLService.getDropViewSql(viewNames);
        // 生效到数据库中
        mySqlDDLService.goToDatabase(copy, commands);
    }

}
