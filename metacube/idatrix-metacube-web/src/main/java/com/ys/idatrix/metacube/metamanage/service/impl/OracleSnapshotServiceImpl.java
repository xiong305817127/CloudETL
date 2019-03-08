package com.ys.idatrix.metacube.metamanage.service.impl;

import com.ys.idatrix.metacube.metamanage.domain.*;
import com.ys.idatrix.metacube.metamanage.mapper.*;
import com.ys.idatrix.metacube.metamanage.service.OracleSnapshotService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName OracleSnapshotServiceImpl
 * @Description oracle 快照服务层实现类
 * @Author ouyang
 * @Date
 */
@Slf4j
@Transactional
@Service
public class OracleSnapshotServiceImpl implements OracleSnapshotService {

    @Autowired
    private SnapshotMetadataMapper snapshotMetadataMapper;

    @Autowired
    private SnapshotTableColumnMapper snapshotTableColumnMapper;

    @Autowired
    private SnapshotTablePkOracleMapper snapshotTablePkOracleMapper;

    @Autowired
    private SnapshotTableIdxOracleMapper snapshotTableIdxOracleMapper;

    @Autowired
    private SnapshotTableUnOracleMapper snapshotTableUnOracleMapper;

    @Autowired
    private SnapshotTableChOracleMapper snapshotTableChOracleMapper;

    @Autowired
    private SnapshotTableFkOracleMapper snapshotTableFkOracleMapper;

    @Autowired
    private SnapshotTableSetOracleMapper snapshotTableSetOracleMapper;


    @Override
    public void generateSnapshot(Metadata table, List<TableColumn> columnList, TablePkOracle primaryKey, List<TableIdxOracle> indexList, List<TableUnOracle> uniqueList, List<TableChOracle> checkList, List<TableFkOracle> foreignKeyList, TableSetOracle setting, String details) {
        // 快照基本信息
        SnapshotMetadata snapshotMetadata = new SnapshotMetadata();
        BeanUtils.copyProperties(table, snapshotMetadata);
        snapshotMetadata.setMetaId(table.getId()); // table id
        snapshotMetadata.setVersion(table.getVersion()); // 当前快照版本
        snapshotMetadata.setDetails(details); // 变更详情
        snapshotMetadata.setId(null);
        snapshotMetadataMapper.insertSelective(snapshotMetadata);

        // 字段快照信息
        List<SnapshotTableColumn> snapshotColList = new ArrayList<>(); // 要新增的列快照信息
        for (TableColumn tableColumn : columnList) {
            SnapshotTableColumn snapshotTableColumn = new SnapshotTableColumn();
            BeanUtils.copyProperties(tableColumn, snapshotTableColumn);
            snapshotTableColumn.setVersion(snapshotMetadata.getVersion()); // 版本号
            snapshotTableColumn.setColumnId(tableColumn.getId()); // 列id
            snapshotColList.add(snapshotTableColumn);
        }
        snapshotTableColumnMapper.batchInsert(snapshotColList);

        // 主键快照信息
        SnapshotTablePkOracle snapshotPk = new SnapshotTablePkOracle();
        BeanUtils.copyProperties(primaryKey, snapshotPk);
        snapshotPk.setId(null);
        snapshotPk.setPkId(primaryKey.getId());
        snapshotPk.setVersions(snapshotMetadata.getVersion());
        snapshotTablePkOracleMapper.insertSelective(snapshotPk);

        // 索引快照信息
        if (CollectionUtils.isNotEmpty(indexList)) {
            List<SnapshotTableIdxOracle> snapshotIndexList = new ArrayList<>(); // 要新增的列快照信息
            for (TableIdxOracle index : indexList) {
                SnapshotTableIdxOracle snapshotIndex = new SnapshotTableIdxOracle();
                BeanUtils.copyProperties(index, snapshotIndex);
                snapshotIndex.setIndexId(index.getId());
                snapshotIndex.setVersions(snapshotMetadata.getVersion());
                snapshotIndexList.add(snapshotIndex);
            }
            snapshotTableIdxOracleMapper.batchInsert(snapshotIndexList);
        }

        // 唯一约束快照信息
        if (CollectionUtils.isNotEmpty(uniqueList)) {
            List<SnapshotTableUnOracle> snapshotUniqueList = new ArrayList<>();
            for (TableUnOracle unique : uniqueList) {
                SnapshotTableUnOracle snapshotUnique = new SnapshotTableUnOracle();
                BeanUtils.copyProperties(unique, snapshotUnique);
                snapshotUnique.setUnId(unique.getId());
                snapshotUnique.setVersions(snapshotMetadata.getVersion());
                snapshotUniqueList.add(snapshotUnique);
            }
            snapshotTableUnOracleMapper.batchInsert(snapshotUniqueList);
        }

        // 检查约束快照信息
        if (CollectionUtils.isNotEmpty(checkList)) {
            List<SnapshotTableChOracle> snapshotCheckList = new ArrayList<>();
            for (TableChOracle check : checkList) {
                SnapshotTableChOracle snapshotCheck = new SnapshotTableChOracle();
                BeanUtils.copyProperties(check, snapshotCheck);
                snapshotCheck.setChId(check.getId());
                snapshotCheck.setVersions(snapshotMetadata.getVersion());
                snapshotCheckList.add(snapshotCheck);
            }
            snapshotTableChOracleMapper.batchInsert(snapshotCheckList);
        }

        // 外键快照信息
        if (CollectionUtils.isNotEmpty(foreignKeyList)) {
            List<SnapshotTableFkOracle> snapshotForeignKeyList = new ArrayList<>();
            for (TableFkOracle foreignKey : foreignKeyList) {
                SnapshotTableFkOracle snapshotForeignKey = new SnapshotTableFkOracle();
                BeanUtils.copyProperties(foreignKey, snapshotForeignKey);
                snapshotForeignKey.setFkId(foreignKey.getId());
                snapshotForeignKey.setVersions(snapshotMetadata.getVersion());
                snapshotForeignKeyList.add(snapshotForeignKey);
            }
            snapshotTableFkOracleMapper.batchInsert(snapshotForeignKeyList);
        }

        // 表设置快照信息
        SnapshotTableSetOracle snapshotTableSet = new SnapshotTableSetOracle();
        BeanUtils.copyProperties(setting, snapshotTableSet);
        snapshotTableSet.setId(null);
        snapshotTableSet.setSetId(setting.getId());
        snapshotTableSet.setVersions(snapshotMetadata.getVersion());
        snapshotTableSetOracleMapper.insertSelective(snapshotTableSet);
    }

    @Override
    public Metadata getSnapshotMetadataInfoByMetadataId(Long metadataId, Integer version) {
        SnapshotMetadata snapshotTable = snapshotMetadataMapper.selectByTableIdAndVersion(metadataId, version);
        Metadata metadata = new Metadata();
        BeanUtils.copyProperties(snapshotTable, metadata);
        metadata.setId(snapshotTable.getMetaId());
        return metadata;
    }

    @Override
    public List<TableColumn> getSnapshotColumnListByTableId(Long tableId, Integer version) {
        List<TableColumn> columnList = new ArrayList();
        List<SnapshotTableColumn> snapshotTableColumnList = snapshotTableColumnMapper.selectListByTableIdAndVersion(tableId, version);
        for (SnapshotTableColumn snapshotColumn : snapshotTableColumnList) {
            TableColumn column = new TableColumn();
            BeanUtils.copyProperties(snapshotColumn, column);
            column.setId(snapshotColumn.getColumnId());
            columnList.add(column);
        }
        return columnList;
    }

    @Override
    public TablePkOracle getSnapshotPrimaryKeyByTableId(Long tableId, Integer version) {
        SnapshotTablePkOracle snapshotPrimaryKey = snapshotTablePkOracleMapper.selectByTableIdAndVersion(tableId, version);
        TablePkOracle primaryKey = new TablePkOracle();
        BeanUtils.copyProperties(snapshotPrimaryKey, primaryKey);
        primaryKey.setId(snapshotPrimaryKey.getPkId());
        return primaryKey;
    }

    @Override
    public List<TableIdxOracle> getSnapshotIndexListByTableId(Long tableId, Integer version) {
        List<TableIdxOracle> result = new ArrayList<>();
        List<SnapshotTableIdxOracle> snapshotIndexList = snapshotTableIdxOracleMapper.selectByTableIdAndVersion(tableId, version);
        for (SnapshotTableIdxOracle snapshotIndex : snapshotIndexList) {
            TableIdxOracle index = new TableIdxOracle();
            BeanUtils.copyProperties(snapshotIndex, index);
            index.setId(snapshotIndex.getIndexId());
            result.add(index);
        }
        return result;
    }

    @Override
    public List<TableUnOracle> getSnapshotUniqueListByTableId(Long tableId, Integer version) {
        List<TableUnOracle> result = new ArrayList<>();
        List<SnapshotTableUnOracle> snapshotUniqueList = snapshotTableUnOracleMapper.selectByTableIdAndVersion(tableId, version);
        for (SnapshotTableUnOracle snapshotUnique : snapshotUniqueList) {
            TableUnOracle unique = new TableUnOracle();
            BeanUtils.copyProperties(snapshotUnique, unique);
            unique.setId(snapshotUnique.getUnId());
            result.add(unique);
        }
        return result;
    }

    @Override
    public List<TableChOracle> getSnapshotCheckListByTableId(Long tableId, Integer version) {
        List<TableChOracle> result = new ArrayList<>();
        List<SnapshotTableChOracle> snapshotCheckList = snapshotTableChOracleMapper.selectByTableIdAndVersion(tableId, version);
        for (SnapshotTableChOracle snapshotCheck : snapshotCheckList) {
            TableChOracle check = new TableChOracle();
            BeanUtils.copyProperties(snapshotCheck, check);
            check.setId(snapshotCheck.getChId());
            result.add(check);
        }
        return result;
    }

    @Override
    public List<TableFkOracle> getSnapshotForeignKeyListByTableId(Long tableId, Integer version) {
        List<TableFkOracle> result = new ArrayList<>();
        List<SnapshotTableFkOracle> snapshotCheckList = snapshotTableFkOracleMapper.selectByTableIdAndVersion(tableId, version);
        for (SnapshotTableFkOracle snapshotForeignKey : snapshotCheckList) {
            TableFkOracle foreignKey = new TableFkOracle();
            BeanUtils.copyProperties(snapshotForeignKey, foreignKey);
            foreignKey.setId(snapshotForeignKey.getFkId());
            result.add(foreignKey);
        }
        return result;
    }

    @Override
    public TableSetOracle getSnapshotTableSettingByTableId(Long tableId, Integer versions) {
        SnapshotTableSetOracle snapshotTableSetting = snapshotTableSetOracleMapper.selectByTableIdAndVersion(tableId, versions);
        TableSetOracle setting = new TableSetOracle();
        BeanUtils.copyProperties(snapshotTableSetting, setting);
        setting.setId(snapshotTableSetting.getSetId());
        return setting;
    }

}