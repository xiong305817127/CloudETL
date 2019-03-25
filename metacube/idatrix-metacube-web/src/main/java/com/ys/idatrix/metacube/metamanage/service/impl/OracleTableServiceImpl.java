package com.ys.idatrix.metacube.metamanage.service.impl;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.alibaba.dubbo.common.utils.StringUtils;
import com.google.common.collect.Lists;
import com.ys.idatrix.db.api.common.RespResult;
import com.ys.idatrix.db.api.rdb.dto.RdbLinkDto;
import com.ys.idatrix.db.api.rdb.service.OracleService;
import com.ys.idatrix.db.api.sql.dto.SqlQueryRespDto;
import com.ys.idatrix.graph.service.api.dto.edge.FkRelationshipDto;
import com.ys.idatrix.graph.service.api.dto.node.TableNodeDto;
import com.ys.idatrix.metacube.api.beans.DatabaseTypeEnum;
import com.ys.idatrix.metacube.common.enums.DBEnum;
import com.ys.idatrix.metacube.common.enums.DataStatusEnum;
import com.ys.idatrix.metacube.common.exception.MetaDataException;
import com.ys.idatrix.metacube.common.utils.UserUtils;
import com.ys.idatrix.metacube.metamanage.domain.*;
import com.ys.idatrix.metacube.metamanage.mapper.*;
import com.ys.idatrix.metacube.metamanage.service.*;
import com.ys.idatrix.metacube.metamanage.vo.request.*;
import com.ys.idatrix.metacube.sysmanage.service.ThemeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName OracleTableServiceImpl
 * @Description oracle 服务层api实现类
 * @Author ouyang
 * @Date
 */
@Slf4j
@Transactional
@Service
public class OracleTableServiceImpl implements OracleTableService {

    @Autowired
    private ThemeService themeService;

    @Autowired
    @Qualifier("oracleSchemaService")
    private McSchemaService schemaService;

    @Autowired
    private OracleValidatedService validatedService;

    @Autowired
    private TableColumnService columnService;

    @Autowired
    private OracleDDLService oracleDDLService;

    @Autowired
    private OracleService oracleService;

    @Autowired
    private TagService tagService;

    @Autowired
    private OracleSnapshotService snapshotService;

    @Autowired
    private MetadataMapper metadataMapper;

    @Autowired
    private TableColumnMapper tableColumnMapper;

    @Autowired
    private TableIdxOracleMapper indexMapper;

    @Autowired
    private TableUnOracleMapper uniqueMapper;

    @Autowired
    private TableChOracleMapper checkMapper;

    @Autowired
    private TablePkOracleMapper primaryKeyMapper;

    @Autowired
    private TableFkOracleMapper foreignKeyMapper;

    @Autowired
    private TableSetOracleMapper settingMapper;

    @Autowired
    private GraphSyncService graphSyncService;

    @Override
    public MetadataMapper getMetadataMapper() {
        return metadataMapper;
    }

    @Override
    public OracleTableVO searchById(Long tableId) {
        Metadata metadata = metadataMapper.selectByPrimaryKey(tableId); // 表基本信息
        List<TableColumn> columnList = tableColumnMapper
                .findTableColumnListByTableId(tableId); // 表字段信息
        TablePkOracle primaryKey = primaryKeyMapper.findByTableId(tableId);// 主键信息
        List<TableIdxOracle> indexList = indexMapper.findByTableId(tableId);// 索引信息
        List<TableUnOracle> uniqueList = uniqueMapper.findByTableId(tableId); // 唯一约束信息
        List<TableChOracle> checkList = checkMapper.findByTableId(tableId); // 检查约束信息
        List<TableFkOracle> foreignKeyList = foreignKeyMapper.findByTableId(tableId);// 外键信息
        TableSetOracle setting = settingMapper.findByTableId(tableId);// 表设置信息
        // 参数补充
        tableReplenish(columnList, indexList, uniqueList, foreignKeyList);
        // 封装，返回数据
        OracleTableVO result = new OracleTableVO(metadata, columnList, primaryKey, indexList,
                foreignKeyList, uniqueList, checkList, setting);
        return result;
    }

    @Override
    public List<String> findTablespaceListBySchemaId(Long schemaId) {
        List<String> nameList = new ArrayList<>();

        // 获取连接信息
        Metadata metadata = new Metadata();
        metadata.setSchemaId(schemaId);
        RdbLinkDto config = oracleDDLService.getConnectionConfig(metadata);
        // 调用db proxy查询表空间
        RespResult<SqlQueryRespDto> result = oracleService.selectTableSpace(config);
        if (result.isSuccess() && result.getData() != null && CollectionUtils
                .isNotEmpty(result.getData().getColumns())) {
            SqlQueryRespDto dto = result.getData();
            List<Map<String, Object>> list = dto.getData(); // 数据
            String tablespaceName = dto.getColumns().get(0); // 列
            for (Map<String, Object> stringObjectMap : list) {
                String name = (String) stringObjectMap.get(tablespaceName);
                nameList.add(name);
            }
        }
        return nameList;
    }

    @Override
    public List<String> findSequenceListBySchemaId(Long schemaId) {
        List<String> sequenceList = new ArrayList<>();

        Metadata metadata = new Metadata();
        metadata.setSchemaId(schemaId);
        // 连接参数
        RdbLinkDto config = oracleDDLService.getConnectionConfig(metadata);
        RespResult<SqlQueryRespDto> result = oracleService.selectSequenceList(config);
        if (!result.isSuccess()) {
            throw new MetaDataException(result.getMsg());
        }
        SqlQueryRespDto dto = result.getData();
        List<Map<String, Object>> list = dto.getData();// 数据
        if (CollectionUtils.isEmpty(list)) {
            return sequenceList;
        }
        String SEQUENCENAME = result.getData().getColumns().get(0);// 列
        for (Map<String, Object> stringObjectMap : list) {
            String name = (String) stringObjectMap.get(SEQUENCENAME);
            sequenceList.add(name);
        }
        return sequenceList;
    }

    @Override
    public List<TableConstraintVO> findConstraintByTableId(Long tableId) {
        List<TableConstraintVO> result = new ArrayList<>();

        // 当前表的所有字段
        List<TableColumn> columnList = tableColumnMapper.findTableColumnListByTableId(tableId);
        Map<Long, TableColumn> columnIdMap =
                columnList.stream()
                        .collect(Collectors.toMap((key -> key.getId()), (value -> value)));

        // 主键约束
        TablePkOracle primaryKey = primaryKeyMapper.findByTableId(tableId);
        if (primaryKey != null && primaryKey.getSequenceStatus() != 1) {
            TableConstraintVO constraint = new TableConstraintVO();
            constraint.setId(primaryKey.getId());
            constraint.setConstraintName(primaryKey.getName());

            List<String> columnIdList = new ArrayList<>();
            List<String> columnNameList = new ArrayList<>();
            for (TableColumn column : columnList) {
                if (column.getIsPk()) {
                    columnIdList.add(column.getId() + "");
                    columnNameList.add(column.getColumnName());
                }
            }
            String ids = StringUtils.join(columnIdList, ",");
            String names = StringUtils.join(columnNameList, ",");
            constraint.setColumnIds(ids);
            constraint.setColumnNames(names);
            constraint.setType(DBEnum.ConstraintTypeEnum.PRIMARY_KEY.getCode());
            result.add(constraint);
        }

        // 唯一约束
        List<TableUnOracle> uniqueList = uniqueMapper.findByTableId(tableId);
        for (TableUnOracle unique : uniqueList) {
            // 禁用的约束不能使用
            if (!uniqueList.isEmpty()) {
                continue;
            }
            TableConstraintVO constraint = new TableConstraintVO();
            constraint.setId(primaryKey.getId());
            constraint.setConstraintName(unique.getName());
            constraint.setColumnIds(unique.getColumnIds());
            String[] columnIds = unique.getColumnIds().split(",");
            List<String> columnNameList = new ArrayList<>();
            for (String columnId : columnIds) {
                TableColumn column = columnIdMap.get(Long.parseLong(columnId));
                columnNameList.add(column.getColumnName());
            }
            constraint.setColumnNames(StringUtils.join(columnNameList, ","));
            constraint.setType(DBEnum.ConstraintTypeEnum.UNIQUE.getCode());
            result.add(constraint);
        }
        return result;
    }

    @Override
    public void add(OracleTableVO oracleTable) {
        // 修改当前表为生效状态
        oracleTable.setStatus(DataStatusEnum.VALID.getValue());
        // 新增
        addTable(oracleTable);
        // 生效
        generateOrUpdateEntityTable(oracleTable.getId());
        // 表同步到数据地图
        graphSyncService.graphSaveTableNode(oracleTable.getId());
        // 表外键同步到数据地图
        graphSaveOrUpdateFkRlat(oracleTable.getId());
    }

    @Override
    public  void addMiningTable(OracleTableVO oracleTable ) {
    	// 修改当前表为生效状态
        oracleTable.setStatus(DataStatusEnum.VALID.getValue());
        // 新增
        addTable(oracleTable);
        // 生成快照版本
        Long tableId = oracleTable.getId();
        Metadata metadata = metadataMapper.selectByPrimaryKey(tableId); // 表基本信息
        List<TableColumn> columnList = tableColumnMapper
                .findTableColumnListByTableId(tableId); // 表字段信息
        TablePkOracle primaryKey = primaryKeyMapper.findByTableId(tableId);// 主键信息
        List<TableIdxOracle> indexList = indexMapper.findByTableId(tableId);// 索引信息
        List<TableUnOracle> uniqueList = uniqueMapper.findByTableId(tableId); // 唯一约束信息
        List<TableChOracle> checkList = checkMapper.findByTableId(tableId); // 检查约束信息
        List<TableFkOracle> foreignKeyList = foreignKeyMapper.findByTableId(tableId);// 外键信息
        TableSetOracle setting = settingMapper.findByTableId(tableId);// 表设置信息
        snapshotService.generateSnapshot(metadata, columnList, primaryKey, indexList, uniqueList, checkList, foreignKeyList, setting, "直采");
    	// 表同步到数据地图
		graphSyncService.graphSaveTableNode(oracleTable.getId());
        // 表外键同步到数据地图
        graphSaveOrUpdateFkRlat(oracleTable.getId());
    }

    @Override
    public void update(OracleTableVO oracleTable) {
        // 修改当前表为生效状态
        oracleTable.setStatus(DataStatusEnum.VALID.getValue());
        // 修改
        updateTable(oracleTable);
        // 生效
        generateOrUpdateEntityTable(oracleTable.getId());
        // 表外键同步到数据地图
        graphSaveOrUpdateFkRlat(oracleTable.getId());
    }

    @Override
    public void addDraft(OracleTableVO oracleTable) {
        // 保存为草稿，不做生效动作
        oracleTable.setStatus(DataStatusEnum.DRAFT.getValue());
        // 新增
        addTable(oracleTable);
    }

    @Override
    public void updateDraft(OracleTableVO oracleTable) {
        // 保存为草稿，不做生效动作
        oracleTable.setStatus(DataStatusEnum.DRAFT.getValue());
        // 修改
        updateTable(oracleTable);
    }

    @Override
    public void delete(List<Long> idList) {
        if (CollectionUtils.isEmpty(idList)) {
            throw new MetaDataException("要删除的数据不能为空");
        }
        Metadata tableCopy = null;
        List<String> tableNames = new ArrayList<>();
        for (Long id : idList) {
            Metadata table = metadataMapper.findById(id);
            // 删除表或草稿表
            metadataMapper.delete(id);
            // 删除表关联的字段
            tableColumnMapper.deleteByTableId(id);
            // 删除主键
            primaryKeyMapper.deleteByTableId(id);
            // 删除表设置
            settingMapper.deleteByTableId(id);
            // 删除索引
            indexMapper.deleteByTableId(id);
            // 删除唯一约束
            uniqueMapper.deleteByTableId(id);
            // 删除检查约束
            checkMapper.deleteByTableId(id);
            // 删除外键
            foreignKeyMapper.deleteByTableId(id);
            // 使用主题递减
            themeService.decreaseProgressively(table.getThemeId());
            // 如果当前为草稿，删除就此结束
            if (table.getStatus() == DataStatusEnum.DRAFT.getValue()) {
                continue;
            }
            // 删除同步到数据地图
            graphSyncService.graphDeleteTableNode(id);
            // 如果为直采
            if (table.getIsGather()) {
                continue;
            }
            // 如果为表，需要删除实体表
            if (tableCopy == null) { // ids必须是同一个schema下的
                tableCopy = table;
            }
            tableNames.add(table.getName()); // 保存下表名
        }
        if(CollectionUtils.isNotEmpty(tableNames)) {
            // 获取删除语句
            List<String> list = oracleDDLService.getDeleteTableSql(tableNames);
            ArrayList arrayList = new ArrayList();
            arrayList.addAll(list);
            // 删除生效到数据库中
            oracleDDLService.goToDatabase(tableCopy, arrayList);
        }
    }

    @Override
    public List<TableVO> searchBySchemaId(Long schemaId) {
        MetadataSearchVo searchVo = new MetadataSearchVo();
        searchVo.setSchemaId(schemaId); // 模式ID
        searchVo.setDatabaseType(DatabaseTypeEnum.ORACLE.getCode());
        searchVo.setResourceType(1); // 表
        List<Metadata> list = metadataMapper.searchList(searchVo);
        // 遍历封装成需要的对象
        List<TableVO> result = new ArrayList<>();
        for (Metadata metadata : list) {
            TableVO vo = new TableVO();
            BeanUtils.copyProperties(metadata, vo);
            result.add(vo);
        }
        return result;
    }

    // 生效
    private void generateOrUpdateEntityTable(Long tableId) {
        // 当前表最新数据
        Metadata metadata = metadataMapper.selectByPrimaryKey(tableId); // 表基本信息
        List<TableColumn> columnList = tableColumnMapper
                .findTableColumnListByTableId(tableId); // 表字段信息
        TablePkOracle primaryKey = primaryKeyMapper.findByTableId(tableId);// 主键信息
        List<TableIdxOracle> indexList = indexMapper.findByTableId(tableId);// 索引信息
        List<TableUnOracle> uniqueList = uniqueMapper.findByTableId(tableId); // 唯一约束信息
        List<TableChOracle> checkList = checkMapper.findByTableId(tableId); // 检查约束信息
        List<TableFkOracle> foreignKeyList = foreignKeyMapper.findByTableId(tableId);// 外键信息
        TableSetOracle setting = settingMapper.findByTableId(tableId);// 表设置信息

        // 要执行的sql
        ArrayList<String> commands = null;
        // 版本变更记录
        String details = "初始化表";
        AlterSqlVO sqlVO = null;

        if (metadata.getVersion() <= 1) { // 新建
            OracleTableVO table = new OracleTableVO();
            BeanUtils.copyProperties(metadata, table);
            table.setColumnList(columnList);
            table.setPrimaryKey(primaryKey);
            table.setIndexList(indexList);
            table.setUniqueList(uniqueList);
            table.setCheckList(checkList);
            table.setForeignKeyList(foreignKeyList);
            table.setTableSetting(setting);
            // 获取修改的sql
            sqlVO = oracleDDLService.getCreateTableSql(table);
            List<String> addSql = sqlVO.getAddSql();
            commands = new ArrayList<>();
            commands.addAll(addSql);
            log.info("create table sql：{}", commands);
        } else { // 修改
            // 将最新数据封装到对象中
            OracleTableVO newTable = new OracleTableVO(metadata, columnList, primaryKey, indexList, foreignKeyList, uniqueList, checkList, setting);

            // 获取旧版本表数据
            Integer versions = metadata.getVersion() - 1;
            Metadata snapshotTable = snapshotService.getSnapshotMetadataInfoByMetadataId(tableId, versions); // 基本信息
            List<TableColumn> snapshotColumnList = snapshotService.getSnapshotColumnListByTableId(tableId, versions); // 字段信息
            TablePkOracle snapshotPrimaryKey = snapshotService.getSnapshotPrimaryKeyByTableId(tableId, versions); // 主键信息
            List<TableIdxOracle> snapshotIndexList = snapshotService.getSnapshotIndexListByTableId(tableId, versions); // 索引信息
            List<TableUnOracle> snapshotUniqueList = snapshotService.getSnapshotUniqueListByTableId(tableId, versions); // 唯一约束信息
            List<TableChOracle> snapshotCheckList = snapshotService.getSnapshotCheckListByTableId(tableId, versions); // 检查约束信息
            List<TableFkOracle> snapshotForeignKeyList = snapshotService.getSnapshotForeignKeyListByTableId(tableId, versions); // 外键约束信息
            TableSetOracle snapshotSetting = snapshotService.getSnapshotTableSettingByTableId(tableId, versions); // 表设置信息
            OracleTableVO oldTable = new OracleTableVO(snapshotTable, snapshotColumnList, snapshotPrimaryKey, snapshotIndexList, snapshotForeignKeyList, snapshotUniqueList, snapshotCheckList, snapshotSetting);

            // 获取alter table sql
            sqlVO = oracleDDLService.getAlterTableSql(newTable, oldTable);
            List<String> changeSql = sqlVO.getChangeSql();
            commands = new ArrayList<>();
            commands.addAll(changeSql);
            log.info("alter table sql：{}", commands);
            details = sqlVO.getMessage();
        }

        // 生效到数据库中
        oracleDDLService.goToDatabase(metadata, commands);

        // 特殊sql的处理
        if (CollectionUtils.isNotEmpty(sqlVO.getSpecialSql())) {
            oracleDDLService.specialSqlGoToDatabase(metadata, sqlVO.getSpecialSql());
        }

        // 生成快照版本
        snapshotService.generateSnapshot(metadata, columnList, primaryKey, indexList, uniqueList, checkList, foreignKeyList, setting, details);
    }

    @Override
    public void addTable(OracleTableVO oracleTable) {
        // 补全参数
        oracleTable.setDatabaseType(DatabaseTypeEnum.ORACLE.getCode()); // oracle类型
        oracleTable.setResourceType(1); // 资源是表
        oracleTable.setVersion(1); // 版本号
        Long renterId = UserUtils.getRenterId(); // 当前租户id
        String creator = UserUtils.getUserName(); // 当前创建人
        Date createTime = new Date(); // 当前创建时间
        oracleTable.setRenterId(renterId);
        oracleTable.setCreator(creator);
        oracleTable.setCreateTime(createTime);
        oracleTable.setModifier(creator);
        oracleTable.setModifyTime(createTime);
        if (oracleTable.getIsGather() == null) {
            oracleTable.setIsGather(false); // 非直采数据
        }

        // 校验基本信息
        validatedService.validatedTableBaseInfo(oracleTable);

        Metadata table = new Metadata();
        BeanUtils.copyProperties(oracleTable, table);
        // table insert
        metadataMapper.insertSelective(table);
        oracleTable.setId(table.getId());

        // 新增字段
        insertTableColumn(table, oracleTable.getColumnList(), creator, createTime);

        // 新增主键
        insertPrimaryKey(table, oracleTable.getPrimaryKey(), oracleTable.getColumnList(), creator, createTime);

        // 新增索引
        insertTableIndex(oracleTable, oracleTable.getIndexList(), oracleTable.getColumnList(), creator, createTime);

        // 新增唯一约束
        insertTableUnique(oracleTable, oracleTable.getUniqueList(), creator, createTime);

        // 新增检查约束
        insertTableCheck(oracleTable, oracleTable.getCheckList(), creator, createTime);

        // 新增外键
        insertTableForeignKey(oracleTable, oracleTable.getForeignKeyList(), creator, createTime);

        // 新增表设置
        insertTableSetting(oracleTable, oracleTable.getTableSetting(), creator, createTime);

        // 生成标签
        tagService.insertTags(table.getTags(), renterId, creator, createTime);

        // 主题使用次数递增
        themeService.increaseProgressively(oracleTable.getThemeId());
    }

    private void insertTableSetting(OracleTableVO oracleTable, TableSetOracle tableSetting, String creator, Date createTime) {
        // 参数校验
        validatedService.validatedTableSetting(oracleTable, tableSetting);
        // 新增
        insertTableSetting(oracleTable.getId(), tableSetting, creator, createTime);
    }

    private void insertTableSetting(Long tableId, TableSetOracle setting, String creator, Date createTime) {
        setting.setTableId(tableId);
        setting.setCreator(creator);
        setting.setCreateTime(createTime);
        setting.setModifier(creator);
        setting.setModifyTime(createTime);
        settingMapper.insertSelective(setting);
    }

    private void insertTableCheck(OracleTableVO oracleTable, List<TableChOracle> checkList, String creator, Date createTime) {
        if (CollectionUtils.isEmpty(checkList)) {
            return;
        }
        // 校验
        validatedService.validatedTableCheck(oracleTable, checkList, oracleTable.getUniqueList(), oracleTable.getPrimaryKey(), oracleTable.getVersion());
        // 新增
        insertCheckList(oracleTable.getId(), checkList, creator, createTime);
    }

    private void insertCheckList(Long tableId, List<TableChOracle> checkList, String creator, Date createTime) {
        int maxLocation = checkMapper.selectMaxLocationByTableId(tableId);
        for (TableChOracle check : checkList) {
            check.setTableId(tableId);
            check.setLocation(++maxLocation);
            check.setCreator(creator);
            check.setCreateTime(createTime);
            check.setModifier(creator);
            check.setModifyTime(createTime);
            checkMapper.insertSelective(check);
        }
    }

    private void insertTableUnique(OracleTableVO oracleTable, List<TableUnOracle> uniqueList, String creator, Date createTime) {
        if (CollectionUtils.isEmpty(uniqueList)) {
            return;
        }
        // 校验
        validatedService.validatedTableUnique(oracleTable, uniqueList, oracleTable.getPrimaryKey(), oracleTable.getColumnList(), oracleTable.getVersion());
        // 新增
        insertUniqueList(oracleTable.getId(), uniqueList, creator, createTime);
    }

    private void insertUniqueList(Long tableId, List<TableUnOracle> uniqueList, String creator, Date createTime) {
        int maxLocation = uniqueMapper.selectMaxLocationByTableId(tableId);
        for (TableUnOracle unique : uniqueList) {
            unique.setTableId(tableId);
            unique.setLocation(++maxLocation);
            unique.setCreator(creator);
            unique.setCreateTime(createTime);
            unique.setModifier(creator);
            unique.setModifyTime(createTime);
            uniqueMapper.insertSelective(unique);
        }
    }

    private void insertPrimaryKey(Metadata table, TablePkOracle primaryKey, List<TableColumn> columnList, String creator, Date createTime) {
        // 校验
        validatedService.validatedTablePrimaryKey(table, primaryKey, columnList);
        // 新增主键
        insertPrimaryKey(table.getId(), primaryKey, creator, createTime);
    }

    private void insertPrimaryKey(Long tableId, TablePkOracle primaryKey, String creator, Date createTime) {
        primaryKey.setTableId(tableId);
        primaryKey.setCreator(creator);
        primaryKey.setCreateTime(createTime);
        primaryKey.setModifier(creator);
        primaryKey.setModifyTime(createTime);
        primaryKeyMapper.insertSelective(primaryKey);
    }

    private void insertTableForeignKey(OracleTableVO table, List<TableFkOracle> foreignKeyList, String creator, Date createTime) {
        if (CollectionUtils.isEmpty(foreignKeyList)) {
            return;
        }
        // 校验
        validatedService.validatedTableForeignKey(table, foreignKeyList, table.getColumnList(), table.getUniqueList(),
                table.getCheckList(), table.getPrimaryKey(), table.getVersion());
        // 新建
        insertForeignKeyList(table.getId(), foreignKeyList, creator, createTime);
    }

    private void insertForeignKeyList(Long tableId, List<TableFkOracle> foreignKeyList, String creator, Date createTime) {
        int maxLocation = foreignKeyMapper.selectMaxLocationByTableId(tableId);
        for (TableFkOracle foreignKey : foreignKeyList) {
            // 参数补齐
            foreignKey.setTableId(tableId);
            foreignKey.setLocation(++maxLocation);
            foreignKey.setCreator(creator);
            foreignKey.setCreateTime(createTime);
            foreignKey.setModifier(creator);
            foreignKey.setModifyTime(createTime);
            foreignKeyMapper.insert(foreignKey);
        }
    }

    private void insertTableIndex(OracleTableVO table, List<TableIdxOracle> indexList, List<TableColumn> tableColumnList, String creator, Date createTime) {
        if (CollectionUtils.isEmpty(indexList)) {
            return;
        }
        // 校验
        validatedService.validatedTableIndex(table, indexList, tableColumnList, table.getVersion());
        // 新增
        insertIndexList(table.getId(), indexList, creator, createTime);
    }

    private void insertIndexList(Long tableId, List<TableIdxOracle> indexList, String creator, Date createTime) {
        int maxLocation = indexMapper.selectMaxLocationByTableId(tableId);
        for (TableIdxOracle index : indexList) {
            // 参数补齐
            index.setTableId(tableId);
            index.setLocation(++maxLocation);
            index.setCreator(creator);
            index.setCreateTime(createTime);
            index.setModifier(creator);
            index.setModifyTime(createTime);
            index.setIsDeleted(false);
            indexMapper.insertSelective(index);
        }
    }

    private void insertTableColumn(Metadata table, List<TableColumn> tableColumnList, String creator, Date createTime) {
        // 校验表字段
        validatedService.validatedTableColumn(tableColumnList, table.getVersion());
        // 遍历去新增
        columnService.insertColumnList(tableColumnList, table.getId(), creator, createTime);
    }

    private void updateTable(OracleTableVO oracleTable) {
        // 参数补齐
        Metadata metadata = metadataMapper.findById(oracleTable.getId());
        if (metadata.getStatus().equals(DataStatusEnum.VALID.getValue())) {
            oracleTable.setVersion(metadata.getVersion() + 1); // 如果之前不是草稿，修改即版本号加1
        }
        Metadata table = new Metadata();
        BeanUtils.copyProperties(oracleTable, table);
        String modifier = UserUtils.getUserName();
        Date modifyTime = new Date();
        table.setModifier(modifier);
        table.setModifyTime(modifyTime);

        // 校验表的基本信息
        validatedService.validatedTableBaseInfo(oracleTable);
        // 修改表基本信息
        metadataMapper.updateByPrimaryKeySelective(table);

        // 修改表字段
        updateTableColumn(table, oracleTable.getColumnList(), modifier, modifyTime);

        // 修改主键
        updatePrimaryKey(table, oracleTable.getPrimaryKey(), modifier, modifyTime);

        // 修改索引
        updateTableIndex(oracleTable, oracleTable.getIndexList(), modifier, modifyTime);

        // 修改唯一约束
        updateTableUnique(oracleTable, oracleTable.getUniqueList(), modifier, modifyTime);

        // 修改检查约束
        updateTableCheck(oracleTable, oracleTable.getCheckList(), modifier, modifyTime);

        // 修改外键
        updateTableForeignKey(oracleTable, oracleTable.getForeignKeyList(), modifier, modifyTime);

        // 修改表设置
        updateTableSetting(oracleTable, oracleTable.getTableSetting(), modifier, modifyTime);

        // 生成标签
        tagService.insertTags(table.getTags(), UserUtils.getRenterId(), modifier, modifyTime);

        // 主题使用次数修改
        if (!metadata.getThemeId().equals(oracleTable.getThemeId())) {
            // 先递减
            themeService.decreaseProgressively(metadata.getThemeId());
            // 再递增
            themeService.increaseProgressively(oracleTable.getThemeId());
        }
    }

    private void updateTableSetting(OracleTableVO table, TableSetOracle tableSetting, String modifier, Date modifyTime) {
        // 校验
        validatedService.validatedTableSetting(table, tableSetting);
        // 修改
        tableSetting.setModifier(modifier);
        tableSetting.setModifyTime(modifyTime);
        settingMapper.updateByPrimaryKeySelective(tableSetting);
    }

    private void updateTableForeignKey(OracleTableVO table, List<TableFkOracle> foreignKeyList, String modifier, Date modifyTime) {
        List<TableFkOracle> allForeignKeyList = foreignKeyMapper.findByTableId(table.getId());
        if (allForeignKeyList == null) {
            allForeignKeyList = new ArrayList<>();
        }
        if (foreignKeyList != null) { // 不等于空，那么表示有修改
            // 先删再增，获取当前表最新的唯一约束列表
            allForeignKeyList.removeIf(property -> foreignKeyList.stream().map(prop -> prop.getId()).collect(Collectors.toList()).contains(property.getId()));
            allForeignKeyList.addAll(foreignKeyList);
        }
        if (CollectionUtils.isEmpty(allForeignKeyList)) {
            return;
        }

        // 最新的字段列表
        List<TableColumn> columnList = tableColumnMapper.findTableColumnListByTableId(table.getId());
        // 最新的主键设置
        TablePkOracle primaryKey = primaryKeyMapper.findByTableId(table.getId());
        // 最新的唯一约束
        List<TableUnOracle> uniqueList = uniqueMapper.findByTableId(table.getId());
        // 最新的检查约束
        List<TableChOracle> checkList = checkMapper.findByTableId(table.getId());

        // 校验
        validatedService.validatedTableForeignKey(table, foreignKeyList, columnList, uniqueList, checkList, primaryKey, table.getVersion());

        // 修改
        if (foreignKeyList != null) {
            List<TableFkOracle> addList = new ArrayList<>(); // 要新增的列表
            for (TableFkOracle foreignKey : foreignKeyList) {
                if (foreignKey.getStatus() == 1) { // add
                    addList.add(foreignKey);
                } else if (foreignKey.getStatus() == 2) { // update
                    foreignKey.setModifier(modifier);
                    foreignKey.setModifyTime(modifyTime);
                    foreignKeyMapper.updateByPrimaryKeySelective(foreignKey);
                } else if (foreignKey.getStatus() == 3) { // delete，直接删除，不考虑情况
                    foreignKeyMapper.deleteByPrimaryKey(foreignKey.getId());
                }
            }
            // insert
            insertForeignKeyList(table.getId(), addList, modifier, modifyTime);
        }
    }

    private void updateTableCheck(OracleTableVO table, List<TableChOracle> checkList, String modifier, Date modifyTime) {
        List<TableChOracle> allCheckList = checkMapper.findByTableId(table.getId());
        if (allCheckList == null) {
            allCheckList = new ArrayList<>();
        }
        if (checkList != null) { // 不等于空，那么表示有修改
            // 先删再增，获取当前表最新的唯一约束列表
            allCheckList.removeIf(property -> checkList.stream().map(prop -> prop.getId()).collect(Collectors.toList()).contains(property.getId()));
            allCheckList.addAll(checkList);
        }
        if (CollectionUtils.isEmpty(allCheckList)) {
            return;
        }

        // 最新的主键设置
        TablePkOracle primaryKey = primaryKeyMapper.findByTableId(table.getId());
        // 最新的唯一约束
        List<TableUnOracle> uniqueList = uniqueMapper.findByTableId(table.getId());

        // 校验
        validatedService.validatedTableCheck(table, allCheckList, uniqueList, primaryKey, table.getVersion());

        // 修改
        if (checkList != null) {
            List<TableChOracle> addList = new ArrayList<>(); // 要新增的列表
            for (TableChOracle check : checkList) {
                if (check.getStatus() == 1) { // add
                    addList.add(check);
                } else if (check.getStatus() == 2) { // update
                    check.setModifier(modifier);
                    check.setModifyTime(modifyTime);
                    checkMapper.updateByPrimaryKeySelective(check);
                } else if (check.getStatus() == 3) { // delete，直接删除，不考虑情况
                    checkMapper.deleteByPrimaryKey(check.getId());
                }
            }
            // insert
            insertCheckList(table.getId(), addList, modifier, modifyTime);
        }
    }

    private void updateTableUnique(OracleTableVO table, List<TableUnOracle> uniqueList, String modifier, Date modifyTime) {
        // 当前表所有的唯一约束
        List<TableUnOracle> allUniqueList = uniqueMapper.findByTableId(table.getId());
        if (allUniqueList == null) {
            allUniqueList = new ArrayList<>();
        }
        if (uniqueList != null) { // 不等于空，那么表示有修改
            // 先删再增，获取当前表最新的唯一约束列表
            allUniqueList.removeIf(property -> uniqueList.stream().map(prop -> prop.getId()).collect(Collectors.toList()).contains(property.getId()));
            allUniqueList.addAll(uniqueList);
        }
        if (CollectionUtils.isEmpty(allUniqueList)) {
            return;
        }

        // 最新的主键设置
        TablePkOracle primaryKey = primaryKeyMapper.findByTableId(table.getId());
        // 最新的字段列表
        List<TableColumn> columnList = tableColumnMapper.findTableColumnListByTableId(table.getId());
        // 校验唯一约束
        validatedService.validatedTableUnique(table, allUniqueList, primaryKey, columnList, table.getVersion());

        // 修改
        if (uniqueList != null) {
            List<TableUnOracle> addList = new ArrayList<>(); // 要新增的列表
            for (TableUnOracle unique : uniqueList) {
                if (unique.getStatus() == 1) { // add
                    addList.add(unique);
                } else if (unique.getStatus() == 2) { // update
                    unique.setModifier(modifier);
                    unique.setModifyTime(modifyTime);
                    uniqueMapper.updateByPrimaryKeySelective(unique);
                } else if (unique.getStatus() == 3) { // delete，直接删除，不考虑情况
                    uniqueMapper.deleteByPrimaryKey(unique.getId());
                }
            }
            // insert
            insertUniqueList(table.getId(), addList, modifier, modifyTime);
        }
    }

    private void updateTableIndex(OracleTableVO table, List<TableIdxOracle> indexList, String modifier, Date modifyTime) {
        // 当前表所有的索引
        List<TableIdxOracle> allIndexList = indexMapper.findByTableId(table.getId());
        if (allIndexList == null) {
            allIndexList = new ArrayList<>();
        }

        if (indexList != null) { // 不等于空，那么表示有修改
            // 先删再增，获取当前表最新的索引
            allIndexList.removeIf(property -> indexList.stream().map(prop -> prop.getId()).collect(Collectors.toList()).contains(property.getId()));
            allIndexList.addAll(indexList);
        }
        if (CollectionUtils.isEmpty(allIndexList)) {
            return;
        }
        // 当前表最新的所有的字段
        List<TableColumn> columnList = tableColumnMapper.findTableColumnListByTableId(table.getId());
        // 校验索引
        validatedService.validatedTableIndex(table, indexList, columnList, table.getVersion());

        // 修改
        if (indexList != null) {
            List<TableIdxOracle> addList = new ArrayList<>(); // 要新增的列表
            for (TableIdxOracle index : indexList) {
                if (index.getStatus() == 1) { // add
                    addList.add(index);
                } else if (index.getStatus() == 2) { // update
                    index.setModifier(modifier);
                    index.setModifyTime(modifyTime);
                    indexMapper.updateByPrimaryKeySelective(index);
                } else if (index.getStatus() == 3) { // delete，直接删除，不考虑情况
                    indexMapper.deleteByPrimaryKey(index.getId());
                }
            }
            // insert
            insertIndexList(table.getId(), addList, modifier, modifyTime);
        }
    }

    private void updatePrimaryKey(Metadata table, TablePkOracle primaryKey, String modifier, Date modifyTime) {
        // 当前表最新的所有的字段
        List<TableColumn> columnList = tableColumnMapper.findTableColumnListByTableId(table.getId());
        // 校验主键
        validatedService.validatedTablePrimaryKey(table, primaryKey, columnList);
        // 修改
        primaryKey.setModifier(modifier);
        primaryKey.setModifyTime(modifyTime);
        primaryKeyMapper.updateByPrimaryKeySelective(primaryKey);
    }

    private void updateTableColumn(Metadata table, List<TableColumn> columnList, String modifier, Date modifyTime) {
        if (columnList == null) {// 当前字段没有任何修改，直接返回
            return;
        }
        // 当前数据库中所有的字段信息
        List<TableColumn> allTableColumn = tableColumnMapper.findTableColumnListByTableId(table.getId());
        // 先删再增，获取出当前表最新的字段列表
        allTableColumn.removeIf(property -> columnList.stream().map(prop -> prop.getId()).collect(Collectors.toList()).contains(property.getId()));
        allTableColumn.addAll(columnList);

        // 校验表字段
        validatedService.validatedTableColumn(allTableColumn, table.getVersion());

        // 要新增的字段
        List<TableColumn> addList = new ArrayList<>();

        // 根据状态分别操作动作
        for (TableColumn tableColumn : columnList) {
            if (tableColumn.getStatus() == 1) { // add
                addList.add(tableColumn);
            } else if (tableColumn.getStatus() == 2) { // update
                tableColumn.setModifier(modifier);
                tableColumn.setModifyTime(modifyTime);
                tableColumnMapper.updateByPrimaryKeySelective(tableColumn);
            } else if (tableColumn.getStatus() == 3) { // delete
                tableColumnMapper.delete(tableColumn.getId());
            }
        }
        // insert
        columnService.insertColumnList(addList, table.getId(), modifier, modifyTime);
    }

    private void tableReplenish(List<TableColumn> columnList, List<TableIdxOracle> indexList, List<TableUnOracle> uniqueList, List<TableFkOracle> foreignKeyList) {
        if(CollectionUtils.isEmpty(columnList)) {
            throw new MetaDataException("错误的表字段");
        }
        // 当前表所有的列
        Map<Long, TableColumn> columnMap =
                columnList.stream().collect(Collectors.toMap((key -> key.getId()), (value -> value)));

        // 索引字段补齐
        if(CollectionUtils.isNotEmpty(indexList)) {
            indexList.forEach(value -> {
                List<String> columnNames = new ArrayList<>();
                String[] colIdArr = value.getColumnIds().split(",");
                for (String id : colIdArr) {
                    columnNames.add(columnMap.get(Long.parseLong(id)).getColumnName());
                }
                value.setColumnNames(StringUtils.join(columnNames, ","));
            });
        }

        // 唯一约束字段补齐
        if(CollectionUtils.isNotEmpty(uniqueList)) {
            uniqueList.forEach(value -> {
                List<String> columnNames = new ArrayList<>();
                String[] colIdArr = value.getColumnIds().split(",");
                for (String id : colIdArr) {
                    columnNames.add(columnMap.get(Long.parseLong(id)).getColumnName());
                }
                value.setColumnNames(StringUtils.join(columnNames, ","));
            });
        }

        // 外键参数补齐
        if(CollectionUtils.isNotEmpty(foreignKeyList)) {
            // 当前参考模式只能是当前模式
            McSchemaPO schema = schemaService.findById(foreignKeyList.get(0).getReferenceSchemaId());
            foreignKeyList.forEach(value -> {
                // 参考模式名
                value.setReferenceSchemaName(schema.getName());

            // 参考表名
            Metadata metadata = metadataMapper.findById(value.getReferenceTableId());
            value.setReferenceTableName(metadata.getName());

                // 参考约束名
                String restrainName = null;
                if (DBEnum.ConstraintTypeEnum.PRIMARY_KEY.getCode() == value.getReferenceRestrainType()) {
                    // 主键约束
                    restrainName = primaryKeyMapper.selectByPrimaryKey(value.getReferenceRestrain()).getName();
                } else if (DBEnum.ConstraintTypeEnum.UNIQUE.getCode() == value.getReferenceRestrainType()) {
                    // 检查约束
                    restrainName = uniqueMapper.selectByPrimaryKey(value.getReferenceRestrain()).getName();
                }
                value.setReferenceRestrainName(restrainName);

                // 参考字段
                String[] referenceColumnIdArr = value.getReferenceColumn().split(",");
                List<TableColumn> referenceColumnList = columnService.getTableColumnListByIdList(Arrays.asList(referenceColumnIdArr));
                List<String> referenceColumnNames = new ArrayList<>();
                referenceColumnList.forEach(column -> {
                    referenceColumnNames.add(column.getColumnName());
                });
                value.setReferenceColumnNames(StringUtils.join(referenceColumnNames, ","));

                // 关联字段
                List<String> columnNames = new ArrayList<>();
                String[] colIdArr = value.getColumnIds().split(",");
                for (String id : colIdArr) {
                    columnNames.add(columnMap.get(Long.parseLong(id)).getColumnName());
                }
                value.setColumnNames(StringUtils.join(columnNames, ","));
            });
        }
    }


    private void graphSaveOrUpdateFkRlat(Long tableId) {
        Metadata metadata = metadataMapper.selectByPrimaryKey(tableId);
        // 当前表外键信息
        List<TableFkOracle> foreignKeyList =
                foreignKeyMapper.findByTableId(tableId);// 表外键信息
        if(metadata.getVersion() <= 1) { // 新增
            if(CollectionUtils.isNotEmpty(foreignKeyList)) {
                graphSaveFkRlat(tableId, foreignKeyList);
            }
        } else { // 修改
            Integer versions = metadata.getVersion() - 1;
            List<TableFkOracle> snapshotForeignKeyList =
                    snapshotService.getSnapshotForeignKeyListByTableId(tableId, versions); // 旧版本外键约束信息
            if(CollectionUtils.isEmpty(foreignKeyList) && CollectionUtils.isEmpty(snapshotForeignKeyList)) {
                return;
            }
            // 如果新或旧的外键集合其中一个为null,则主动一个空ArrayList,便于后续处理
            if (CollectionUtils.isEmpty(foreignKeyList)) {
                foreignKeyList = new ArrayList<>();
            }
            if (CollectionUtils.isEmpty(snapshotForeignKeyList)) {
                snapshotForeignKeyList = new ArrayList<>();
            }

            // copy一份不要修改到之前参数
            ArrayList<TableFkOracle> oldForeignKeyList = Lists.newArrayList(snapshotForeignKeyList);
            ArrayList<TableFkOracle> newForeignKeyList = Lists.newArrayList(foreignKeyList);

            // 在copy一份做参照
            ArrayList<TableFkOracle> oldCopy = Lists.newArrayList(snapshotForeignKeyList);
            ArrayList<TableFkOracle> newCopy = Lists.newArrayList(foreignKeyList);

            // 不变的索引bean集合 //求交集。自定义对象重写 hashcode 和 equals
            oldCopy.retainAll(newCopy);

            // 待删除的外键集合，解释：被修改或以被删除的外键
            oldForeignKeyList.removeAll(oldCopy);

            // 待新增的外键集合，解释：被修改或新增的外键
            newForeignKeyList.removeAll(oldCopy);

            // 删除
            for (TableFkOracle fk : oldForeignKeyList) {
                graphSyncService.deleteFkRlat(fk.getTableId(), fk.getName());
            }
            // 新增
            graphSaveFkRlat(tableId, newForeignKeyList);
        }
    }

    private void graphSaveFkRlat(Long tableId, List<TableFkOracle> foreignKeyList) {
        // 当前表
        TableNodeDto startNode = graphSyncService.getTableNodeDto(tableId);
        List<FkRelationshipDto> result = new ArrayList<>();
        // 遍历外键
        for (TableFkOracle foreignKey : foreignKeyList) {
            TableNodeDto endTableDto =
                    graphSyncService.getTableNodeDto(foreignKey.getReferenceTableId());// 外键表
            String fkName = foreignKey.getName(); // 外键名
            String[] columnIdArr = foreignKey.getColumnIds().split(",");// 当前表字段
            String[] referenceColumnIdArr = foreignKey.getReferenceColumn().split(","); // 参考表字段
            // 遍历字段
            for (int i = 0; i < columnIdArr.length; i++) {
                FkRelationshipDto dto = new FkRelationshipDto();
                dto.setStartNode(startNode);
                dto.setEndNode(endTableDto);
                dto.setFkName(fkName);
                // 字段信息
                TableColumn column = tableColumnMapper.selectByPrimaryKey(Long.parseLong(columnIdArr[i]));
                TableColumn referenceColumn = tableColumnMapper.selectByPrimaryKey(Long.parseLong(referenceColumnIdArr[i]));
                dto.setStartFieldName(column.getColumnName());
                dto.setEndFieldName(referenceColumn.getColumnName());
                result.add(dto);
            }
        }
        graphSyncService.saveFkRlat(result);
    }
}