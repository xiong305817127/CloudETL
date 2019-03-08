package com.ys.idatrix.metacube.metamanage.service.impl;

import com.ys.idatrix.metacube.metamanage.domain.*;
import com.ys.idatrix.metacube.metamanage.mapper.*;
import com.ys.idatrix.metacube.metamanage.service.MysqlSnapshotService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName MysqlSnapshotServiceImpl
 * @Description mysql 快照信息实现类
 * @Author ouyang
 * @Date
 */
@Transactional
@Service
public class MysqlSnapshotServiceImpl implements MysqlSnapshotService {

    @Autowired
    private SnapshotMetadataMapper snapshotMetadataMapper;

    @Autowired
    private SnapshotTableColumnMapper snapshotTableColumnMapper;

    @Autowired
    private SnapshotTableIdxMysqlMapper snapshotTableIdxMysqlMapper;

    @Autowired
    private SnapshotTableFkMysqlMapper snapshotTableFkMysqlMapper;

    @Autowired
    private SnapshotViewDetailMapper snapshotViewDetailMapper;

    @Override
    public void generateCreateTableSnapshot(Metadata table, List<TableColumn> columnList, List<TableIdxMysql> tableIndexList, List<TableFkMysql> tableFkMysqlList, String details) {
        // 基本快照信息
        SnapshotMetadata snapshotMetadata = new SnapshotMetadata();
        BeanUtils.copyProperties(table, snapshotMetadata);
        snapshotMetadata.setMetaId(table.getId()); // table id
        snapshotMetadata.setVersion(table.getVersion()); // 当前快照版本
        snapshotMetadata.setDetails(details); // 变更详情
        snapshotMetadata.setId(null);
        snapshotMetadataMapper.insertSelective(snapshotMetadata);

        // 列快照信息
        List<SnapshotTableColumn> snapshotColList = new ArrayList<>(); // 要新增的列快照信息
        for (TableColumn tableColumn : columnList) {
            SnapshotTableColumn snapshotTableColumn = new SnapshotTableColumn();
            BeanUtils.copyProperties(tableColumn, snapshotTableColumn);
            snapshotTableColumn.setVersion(snapshotMetadata.getVersion()); // 版本号
            snapshotTableColumn.setColumnId(tableColumn.getId()); // 列id
            snapshotColList.add(snapshotTableColumn);
        }
        snapshotTableColumnMapper.batchInsert(snapshotColList);

        // 索引快照信息
        if (CollectionUtils.isNotEmpty(tableIndexList)) {
            List<SnapshotTableIdxMysql> snapshotIdxList = new ArrayList<>();
            for (TableIdxMysql index : tableIndexList) {
                SnapshotTableIdxMysql snapshotIdx = new SnapshotTableIdxMysql();
                BeanUtils.copyProperties(index, snapshotIdx);
                snapshotIdx.setVersion(snapshotMetadata.getVersion()); // 版本号
                snapshotIdx.setIndexId(index.getId());// 索引id
                snapshotIdxList.add(snapshotIdx);
            }
            snapshotTableIdxMysqlMapper.batchInsert(snapshotIdxList);
        }

        // 外键快照信息
        if (CollectionUtils.isNotEmpty(tableFkMysqlList)) {
            List<SnapshotTableFkMysql> snapshotFKList = new ArrayList<>();
            for (TableFkMysql tableFk : tableFkMysqlList) {
                SnapshotTableFkMysql snapshotFk = new SnapshotTableFkMysql();
                BeanUtils.copyProperties(tableFk, snapshotFk);
                snapshotFk.setVersion(snapshotMetadata.getVersion()); // 版本号
                snapshotFk.setFkId(tableFk.getId());// 外键id
                snapshotFKList.add(snapshotFk);
            }
            snapshotTableFkMysqlMapper.batchInsert(snapshotFKList);
        }
    }

    @Override
    public Metadata getSnapshotTableInfoByTableId(Long tableId, Integer version) {
        SnapshotMetadata snapshotTable = snapshotMetadataMapper.selectByTableIdAndVersion(tableId, version);
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
    public List<TableIdxMysql> getSnapshotIndexListByTableId(Long tableId, Integer version) {
        List<TableIdxMysql> list = new ArrayList<>();
        List<SnapshotTableIdxMysql> snapshotIndexList = snapshotTableIdxMysqlMapper.selectListByTableIdAndVersion(tableId, version);
        for (SnapshotTableIdxMysql snapshotIndex : snapshotIndexList) {
            TableIdxMysql index = new TableIdxMysql();
            BeanUtils.copyProperties(snapshotIndex, index);
            index.setId(snapshotIndex.getIndexId());
            list.add(index);
        }
        return list;
    }

    @Override
    public List<TableFkMysql> getSnapshotForeignKeyListByTableId(Long tableId, Integer version) {
        List<TableFkMysql> list = new ArrayList<>();
        List<SnapshotTableFkMysql> snapshotForeignKeyList = snapshotTableFkMysqlMapper.selectListByTableIdAndVersion(tableId, version);
        for (SnapshotTableFkMysql snapshotForeignKey : snapshotForeignKeyList) {
            TableFkMysql foreignKey = new TableFkMysql();
            BeanUtils.copyProperties(snapshotForeignKey,foreignKey);
            foreignKey.setId(snapshotForeignKey.getFkId());
            list.add(foreignKey);
        }
        return list;
    }

    @Override
    public SnapshotTableColumn getSnapshotColumn(Long columnId, Integer version) {
        SnapshotTableColumn snapshotColumn = snapshotTableColumnMapper.findByColumnIdAndVersion(columnId, version);
        return snapshotColumn;
    }

    @Override
    public void createViewSnapshot(Metadata view, ViewDetail viewDetail, String details) {
        // 视图基本快照信息
        SnapshotMetadata snapshotMetadata = new SnapshotMetadata();
        BeanUtils.copyProperties(view, snapshotMetadata);
        snapshotMetadata.setMetaId(view.getId()); // view id
        snapshotMetadata.setVersion(view.getVersion()); // 当前快照版本
        snapshotMetadata.setDetails(details); // 变更详情
        snapshotMetadata.setId(null);
        snapshotMetadataMapper.insertSelective(snapshotMetadata);

        // 视图详情快照信息
        SnapshotViewDetail snapshotViewDetail = new SnapshotViewDetail();
        BeanUtils.copyProperties(viewDetail, snapshotViewDetail);
        snapshotViewDetail.setId(null);
        snapshotViewDetail.setViewDetailId(viewDetail.getId());
        snapshotViewDetail.setVersion(view.getVersion()); // 版本号
        snapshotViewDetailMapper.insertSelective(snapshotViewDetail);
    }

    @Override
    public ViewDetail getSnapshotViewDetail(Long viewId, Integer version) {
        SnapshotViewDetail snapshotViewDetail = snapshotViewDetailMapper.findByViewIdAndVersion(viewId, version);
        ViewDetail viewDetail = new ViewDetail();
        BeanUtils.copyProperties(snapshotViewDetail, viewDetail);
        viewDetail.setId(snapshotViewDetail.getViewId());
        return viewDetail;
    }

}
