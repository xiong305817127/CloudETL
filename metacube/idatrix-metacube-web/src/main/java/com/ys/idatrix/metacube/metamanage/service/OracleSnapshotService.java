package com.ys.idatrix.metacube.metamanage.service;

import com.ys.idatrix.metacube.metamanage.domain.*;

import java.util.List;

/**
 * @ClassName OracleSnapshotService
 * @Description oracle 快照服务层
 * @Author ouyang
 * @Date
 */
public interface OracleSnapshotService {

    void generateSnapshot(Metadata metadata, List<TableColumn> columnList, TablePkOracle primaryKey, List<TableIdxOracle> indexList, List<TableUnOracle> uniqueList, List<TableChOracle> checkList, List<TableFkOracle> foreignKeyList, TableSetOracle setting, String details);

    // 获取元数据某个版本的基本信息
    Metadata getSnapshotMetadataInfoByMetadataId(Long metadataId, Integer version);

    // 获取表某个版本的字段列表
    List<TableColumn> getSnapshotColumnListByTableId(Long tableId, Integer version);

    // 获取表某个版本的主键设置
    TablePkOracle getSnapshotPrimaryKeyByTableId(Long tableId, Integer version);

    // 获取表某个版本的索引列表
    List<TableIdxOracle> getSnapshotIndexListByTableId(Long tableId, Integer version);

    // 获取表某个版本的唯一约束列表
    List<TableUnOracle> getSnapshotUniqueListByTableId(Long tableId, Integer version);

    // 获取表某个版本的检查约束列表
    List<TableChOracle> getSnapshotCheckListByTableId(Long tableId, Integer version);

    // 获取表某个版本的外键约束列表
    List<TableFkOracle> getSnapshotForeignKeyListByTableId(Long tableId, Integer version);

    // 获取表某个版本的表设置
    TableSetOracle getSnapshotTableSettingByTableId(Long tableId, Integer versions);
}