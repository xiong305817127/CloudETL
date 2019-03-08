package com.ys.idatrix.metacube.metamanage.service;

import com.ys.idatrix.metacube.metamanage.domain.*;

import java.util.List;

/**
 * @ClassName MysqlSnapshotService
 * @Description mysql 快照 api
 * @Author ouyang
 * @Date
 */
public interface MysqlSnapshotService {

    void generateCreateTableSnapshot(Metadata table, List<TableColumn> columnList, List<TableIdxMysql> tableIndexList, List<TableFkMysql> tableFkMysqlList, String details);

    // 获取元数据某个版本的基本信息
    Metadata getSnapshotTableInfoByTableId(Long tableId, Integer version);

    // 获取表某个版本的字段列表
    List<TableColumn> getSnapshotColumnListByTableId(Long tableId, Integer version);

    // 获取表某个版本的索引列表
    List<TableIdxMysql> getSnapshotIndexListByTableId(Long tableId, Integer version);

    // 获取表某个版本的外键列表
    List<TableFkMysql> getSnapshotForeignKeyListByTableId(Long tableId, Integer version);

    // 获取某个版本下的某个字段
    SnapshotTableColumn getSnapshotColumn(Long columnId, Integer version);


    // ================== view

    // 保存视图快照信息
    void createViewSnapshot(Metadata view, ViewDetail viewDetail, String details);

    // 获取某个版本下的视图详情信息
    ViewDetail getSnapshotViewDetail(Long viewId, Integer version);


}
