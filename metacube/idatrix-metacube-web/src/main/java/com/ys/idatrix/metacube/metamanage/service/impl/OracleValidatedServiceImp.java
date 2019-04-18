package com.ys.idatrix.metacube.metamanage.service.impl;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.alibaba.fastjson.JSON;
import com.google.common.base.Joiner;
import com.ys.idatrix.db.api.common.RespResult;
import com.ys.idatrix.db.api.rdb.dto.RdbLinkDto;
import com.ys.idatrix.db.api.rdb.service.OracleService;
import com.ys.idatrix.db.api.sql.dto.SqlQueryRespDto;
import com.ys.idatrix.metacube.api.beans.DatabaseTypeEnum;
import com.ys.idatrix.metacube.common.enums.DBEnum;
import com.ys.idatrix.metacube.common.enums.DataStatusEnum;
import com.ys.idatrix.metacube.common.exception.MetaDataException;
import com.ys.idatrix.metacube.metamanage.domain.*;
import com.ys.idatrix.metacube.metamanage.mapper.*;
import com.ys.idatrix.metacube.metamanage.service.*;
import com.ys.idatrix.metacube.metamanage.vo.request.DBViewVO;
import com.ys.idatrix.metacube.metamanage.vo.request.OracleTableVO;
import com.ys.idatrix.metacube.metamanage.vo.request.TableConstraintVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName OracleValidatedServiceImp
 * @Description oracle 校验实现类
 * @Author ouyang
 * @Date
 */
@Slf4j
@Service
public class OracleValidatedServiceImp implements OracleValidatedService {

    /**
     * 第一步校验：元数据数据库中校验 第二部校验：实体数据库中校验
     */
    private static final String EXISTS_CODE = "exists";

    @Autowired
    @Qualifier("oracleSchemaService")
    private McSchemaService schemaService;

    @Autowired
    private OracleService oracleService;

    @Autowired
    private MetadataService metadataService;

    @Autowired
    private OracleSnapshotService snapshotService;

    @Autowired
    private TablePkOracleMapper primaryKeyMapper;

    @Autowired
    private OracleDDLService oracleDDLService;

    @Autowired
    private TableIdxOracleMapper indexMapper;

    @Autowired
    private TableUnOracleMapper uniqueMapper;

    @Autowired
    private TableChOracleMapper checkMapper;

    @Autowired
    private TableFkOracleMapper foreignKeyMapper;

    @Autowired
    private TableColumnMapper columnMapper;

    // 字段数据类型list
    private static List<String> tableDataTypeList
            = Arrays.stream(DBEnum.OracleTableDataType.values())
            .map(mysqlTableDataType -> mysqlTableDataType.name()).collect(Collectors.toList());

    // 索引类型list
    private static List<String> indexTypeList
            = Arrays.stream(DBEnum.OracleIndexType.values())
            .map(oracleIndexTypeEnum -> oracleIndexTypeEnum.name()).collect(Collectors.toList());

    // 删除或修改时触发事件list
    private static List<String> affairList
            = Arrays.stream(DBEnum.OracleFKTriggerAffairEnum.values()).map(affair -> affair.name())
            .collect(Collectors.toList());

    @Override
    public void validatedTableBaseInfo(OracleTableVO oracleTable) {
        if (!oracleTable.getDatabaseType().equals(DatabaseTypeEnum.ORACLE.getCode())) {
            // 当前数据库不为 oracle
            throw new MetaDataException("500", "错误的数据库类型");
        }
        if (!oracleTable.getResourceType().equals(1)) {
            // 当前资源类型不为 表
            throw new MetaDataException("500", "错误的资源类型");
        }

        McSchemaPO schema = schemaService.findById(oracleTable.getSchemaId());
        if (schema == null) {
            throw new MetaDataException("异常的模式");
        }

        Metadata metadata = new Metadata();
        metadata.setId(oracleTable.getId());// 如果是修改，那么排除当前表数据
        metadata.setSchemaId(oracleTable.getSchemaId());
        metadata.setName(oracleTable.getName());
        metadata.setDatabaseType(DatabaseTypeEnum.ORACLE.getCode()); // oracle
        metadata.setResourceType(1); // 1:表

        // 判断实体表名是否重复
        if (metadataService.findByMetadata(metadata) > 0) {
            throw new MetaDataException("500", "当前表英文名已经被占用（表已创建或在草稿箱中）");
        }

        // 同模式下表名和视图名不能重复
        metadata.setResourceType(2); // 当前为视图
        if (metadataService.findByMetadata(metadata) > 0) {
            throw new MetaDataException("500", "当前表英文名已经被视图占用（视图已创建或在草稿箱中）");
        }

        /*// 判断表中文名是否重复
        if (StringUtils.isNotBlank(oracleTable.getIdentification())) {
            metadata.setResourceType(1); // 当前为表
            metadata.setName(null);
            metadata.setIdentification(oracleTable.getIdentification());
            if (metadataService.findByMetadata(metadata) > 0) {
                throw new MetaDataException("500", "当前表中文名已经被占用（表已创建或在草稿箱中）");
            }
        }*/

        // 如果为直采或草稿，则不需要去实体数据库校验
        if (oracleTable.getIsGather() || oracleTable.getStatus().equals(DataStatusEnum.DRAFT.getValue())) {
            return;
        }

        if (oracleTable.getVersion() > 1) { // 版本号大于1则表示修改
            // 如果为修改，则查询出前一个版本的表基本信息，如果表名没有修改，则不需要去数据库中查询了。
            Metadata snapshotTable = snapshotService
                    .getSnapshotMetadataInfoByMetadataId(oracleTable.getId(),
                            oracleTable.getVersion() - 1);
            // 如果表名没有修改，则不需要到数据库验证
            if (snapshotTable.getName().equals(oracleTable.getName())) {
                return;
            }
        }

        // 连接信息
        RdbLinkDto config = oracleDDLService.getConnectionConfig(metadata);

        // 判断表名是否已被存在的表占用
        RespResult<Boolean> tableNameExists = oracleService
                .tableNameExists(oracleTable.getName(), config);
        log.info("tableNameExists result:" + JSON.toJSONString(tableNameExists, true));
        if (tableNameExists.isSuccess() && EXISTS_CODE.equals(tableNameExists.getMsg())) {
            throw new MetaDataException("500", "数据库中已存在此表名：" + oracleTable.getName());
        }

        // 判断表名是否已被存在的视图占用
        RespResult<Boolean> viewNameExists = oracleService.viewNameExists(oracleTable.getName(), config);
        log.info("viewNameExists result:" + JSON.toJSONString(viewNameExists, true));
        if (viewNameExists.isSuccess() && EXISTS_CODE.equals(viewNameExists.getMsg())) {
            throw new MetaDataException("500", "数据库中已存在此表名：" + oracleTable.getName());
        }
    }

    @Override
    public void validatedTableColumn(List<TableColumn> tableColumnList, Integer versions) {
        /**
         * 考虑情况：
         * 字段名重复
         * 字段数据类型错误
         */
        if (versions == 1) {// 互斥条件，新增时字段不能为空，修改时，字段可以为空
            if (CollectionUtils.isEmpty(tableColumnList)) {
                throw new MetaDataException("字段不能为空");
            }
        }
        // 修改的话把需要删除的列剔除掉，不是修改默认为0
        tableColumnList = tableColumnList.stream().filter(property -> property.getStatus() != 3)
                .collect(Collectors.toList());

        List<String> columnNameList = new ArrayList<>(); // 字段 name listByPage
        for (TableColumn column : tableColumnList) {
            // 当前表中，字段名不能重复
            if (columnNameList.contains(column.getColumnName())) {
                throw new MetaDataException("500", "字段名重复：" + column.getColumnName());
            }

            // 当前字段数据类型是存在的
            if (!tableDataTypeList.stream().anyMatch(
                    tableDataType -> tableDataType.equalsIgnoreCase(column.getColumnType()))) {
                throw new MetaDataException("500", "字段数据类型错误，字段名：" + column.getColumnName());
            }

            // 当前字段为主键
            if (column.getIsPk()) {
                // 主键必须不能为null
                if (column.getIsNull()) {
                    throw new MetaDataException("字段错误，主键不能为空");
                }
            }
            columnNameList.add(column.getColumnName());
        }
    }

    @Override
    public void validatedTablePrimaryKey(Metadata table, TablePkOracle primaryKey,
            List<TableColumn> columnList) {
        // 当前字段中是否有主键
        List<Long> columnIdList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(columnList)) {
            for (TableColumn column : columnList) {
                if (column.getIsPk()) {
                    columnIdList.add(column.getId());
                }
            }
        }

        if (primaryKey.getSequenceStatus() == 1) { // 标识为无主键
            if (columnIdList.size() > 0) { // 但实际又有主键
                throw new MetaDataException("主键或状态错误");
            }
            return;
        }

        // 主键字段有多个，不能设置序列
        if (primaryKey.getSequenceStatus() > 2 && columnIdList.size() > 1) {
            throw new MetaDataException("错误的序列设置，主键字段有多个");
        }

        // 主键约束名不能为空
        if (StringUtils.isBlank(primaryKey.getName())) {
            throw new MetaDataException("当前已选主键，主键约束名不能为空");
        }

        // 主键名在模式下唯一，主键会自动创建唯一索引和检查约束（主键不能为null），索引名即是主键名
        // 主键对应的约束名是否已经存在，需要在主键表，唯一约束表，检查约束表中判断
        TablePkOracle pk = new TablePkOracle();
        primaryKey.setTableId(table.getId());
        pk.setId(primaryKey.getId());
        pk.setName(primaryKey.getName());
        pk.setTableId(primaryKey.getTableId());
        if (primaryKeyMapper.find(pk) > 0) {
            throw new MetaDataException("主键约束名已存在，请修改");
        }

        TableUnOracle un = new TableUnOracle();
        un.setName(primaryKey.getName());
        un.setTableId(table.getId());
        if (uniqueMapper.find(un) > 0) {
            throw new MetaDataException("主键约束名已存在，请修改");
        }

        TableChOracle ch = new TableChOracle();
        ch.setName(primaryKey.getName());
        ch.setTableId(table.getId());
        if (checkMapper.find(ch) > 0) {
            throw new MetaDataException("主键约束名已存在，请修改");
        }

        TableFkOracle fk = new TableFkOracle();
        fk.setName(primaryKey.getName());
        fk.setTableId(table.getId());
        if (foreignKeyMapper.find(fk) > 0) {
            throw new MetaDataException("主键约束名已存在，请修改");
        }

        // 主键对应的唯一索引名是否已经存在
        TableIdxOracle idx = new TableIdxOracle();
        idx.setIndexName(primaryKey.getName());
        idx.setTableId(table.getId());
        if (indexMapper.find(idx) > 0) {
            throw new MetaDataException("主键对应的索引名已存在，请修改");
        }

        // 当前为草稿或直采，则不需要去实体数据库校验
        if (table.getIsGather() || table.getStatus() == 0) {
            return;
        }

        // 连接参数
        RdbLinkDto config = oracleDDLService.getConnectionConfig(table);

        // 当修改时，需要将前一个版本的数据取出，比对主键名或系列名是否有所修改，如果没有则不需要判断
        if (table.getVersion() > 1) { // 修改
            TablePkOracle snapshotPrimaryKey = snapshotService
                    .getSnapshotPrimaryKeyByTableId(table.getId(), table.getVersion() - 1);
            if (!snapshotPrimaryKey.getName().equals(primaryKey.getName())) {// 主键名被修改
                // 判断数据库中主键约束名是否存在
                RespResult<Boolean> primaryKeyName = oracleService
                        .constraintsNameExists(primaryKey.getName(), config);
                if (primaryKeyName.isSuccess() && EXISTS_CODE.equals(primaryKeyName.getMsg())) {
                    throw new MetaDataException("数据库中约束名：" + primaryKeyName + " 已经使用");
                }
            }
            if (!snapshotPrimaryKey.getSequenceStatus().equals(primaryKey.getSequenceStatus()) ||
                    (snapshotPrimaryKey.getSequenceName() != null && !snapshotPrimaryKey
                            .getSequenceName().equals(primaryKey.getSequenceName()))) { // 序列有所修改
                // 序列未填充
                if (primaryKey.getSequenceStatus() == 2) {
                    return;
                }
                // 获取数据库当前可以使用的序列列表
                List<String> sequenceList = new ArrayList<>();
                RespResult<SqlQueryRespDto> result = oracleService.selectSequenceList(config);
                List<Map<String, Object>> list = (List<Map<String, Object>>) result.getData(); // 数据
                String SEQUENCENAME = result.getData().getColumns().get(0);// 列
                for (Map<String, Object> stringObjectMap : list) {
                    String name = (String) stringObjectMap.get(SEQUENCENAME);
                    sequenceList.add(name);
                }

                if (primaryKey.getSequenceStatus() == 3) { // 标识从新序列填充
                    if (sequenceList.contains(primaryKey.getSequenceName())) {
                        throw new MetaDataException("主键设置错误，当前序列名已存在数据库中");
                    }
                }

                if (primaryKey.getSequenceStatus() == 4) { // 从已有序列填充，但序列名却不存在
                    if (!sequenceList.contains(primaryKey.getSequenceName())) {
                        throw new MetaDataException("主键设置错误，当前序列名不存在数据库中");
                    }
                }
            }
        } else {// 新增
            // 判断数据库中主键约束名是否存在
            RespResult<Boolean> primaryKeyName = oracleService
                    .constraintsNameExists(primaryKey.getName(), config);
            if (primaryKeyName.isSuccess() && EXISTS_CODE.equals(primaryKeyName.getMsg())) {
                throw new MetaDataException("500", "数据库中约束名：" + primaryKeyName + " 已经使用");
            }
            // 序列未填充
            if (primaryKey.getSequenceStatus() == 2) {
                return;
            }
            // 获取数据库当前可以使用的序列列表
            List<String> sequenceList = new ArrayList<>();
            RespResult<SqlQueryRespDto> result = oracleService.selectSequenceList(config);
            List<Map<String, Object>> list = (List<Map<String, Object>>) result.getData(); // 数据
            String SEQUENCENAME = result.getData().getColumns().get(0);// 列
            for (Map<String, Object> stringObjectMap : list) {
                String name = (String) stringObjectMap.get(SEQUENCENAME);
                sequenceList.add(name);
            }

            if (primaryKey.getSequenceStatus() == 3) { // 标识从新序列填充
                if (sequenceList.contains(primaryKey.getSequenceName())) {
                    throw new MetaDataException("主键设置错误，当前序列名已存在数据库中");
                }
            }

            if (primaryKey.getSequenceStatus() == 4) { // 从已有序列填充，但序列名却不存在
                if (!sequenceList.contains(primaryKey.getSequenceName())) {
                    throw new MetaDataException("主键设置错误，当前序列名不存在数据库中");
                }
            }
        }
    }

    @Override
    public void validatedTableIndex(OracleTableVO table, List<TableIdxOracle> indexList,
            List<TableColumn> columnList, Integer versions) {
        List<String> indexNameList = new ArrayList<>(); // 索引名name listByPage
        List<String> indexColumnIdList = new ArrayList<>(); // 索引关联字段 listByPage（关联字段列表不能重复）

        // 剔除删除的索引
        indexList = indexList.stream().filter(property -> property.getStatus() != 3)
                .collect(Collectors.toList());

        // 当前表所有的字段，以name为key，新增时用到
        Map<String, TableColumn> columnMap =
                columnList.stream()
                        .collect(Collectors.toMap((key -> key.getColumnName()), (value -> value)));

        // TODO 索引需要将主键也加进来

        // 当前表的所有字段，以id为key，修改时才会用到
        Map<Long, TableColumn> columnIdMap = null;
        // 快照版本的索引（上一个版本的数据）
        Map<Long, TableIdxOracle> snapshotIndexMap = null;

        if (versions > 1) { // 是否为修改,
            columnIdMap = columnList.stream()
                    .collect(Collectors.toMap((key -> key.getId()), (value -> value)));
            if(table.getVersion() > 1) {
                List<TableIdxOracle> snapshotIndexList = snapshotService
                        .getSnapshotIndexListByTableId(table.getId(), table.getVersion() - 1);
                snapshotIndexMap = snapshotIndexList.stream()
                        .collect(Collectors.toMap((key -> key.getId()), (value -> value)));
            }
        }

        for (TableIdxOracle index : indexList) {
            // 当前模式内，索引名不能重复，元数据校验
            if (indexNameList.contains(index.getIndexName())) {
                throw new MetaDataException("索引名已存在，索引名：" + index.getIndexName());
            }

            TablePkOracle pk = new TablePkOracle();
            pk.setName(index.getIndexName());
            pk.setTableId(table.getId());
            if (primaryKeyMapper.find(pk) > 0) {
                throw new MetaDataException("索引名已存在，索引名：" + index.getIndexName());
            }

            TableIdxOracle idx = new TableIdxOracle();
            idx.setId(index.getId());
            idx.setIndexName(index.getIndexName());
            idx.setTableId(table.getId());
            if (indexMapper.find(idx) > 0) {
                throw new MetaDataException("索引名已存在，索引名：" + index.getIndexName());
            }

            // 判断索引类型是否存在
            if (!indexTypeList.stream()
                    .anyMatch(indexType -> indexType.equalsIgnoreCase(index.getIndexType()))) {
                throw new MetaDataException("500", "错误的索引类型，索引名：" + index.getIndexName());
            }

            // 校验索引字段
            if (versions > 1 && index.getStatus() == 0) { // 如果是修改状态，但是当前索引是没有修改的
                String[] idArr = index.getColumnIds().split(",");
                String[] sortArr = index.getColumnSort().split(",");// 字段对应的排序
                if (idArr.length != sortArr.length) {
                    throw new MetaDataException("错误的索引，字段对应的排序异常");
                }
                for (String colId : idArr) {
                    Long columnId = Long.parseLong(colId);
                    TableColumn column = columnIdMap.get(columnId);
                    if (column == null) {
                        throw new MetaDataException("索引有错误的关联字段，索引名：" + index.getIndexName());
                    }
                    // TODO 当前字段类型是否支持当前索引类型，也许修改了字段但没有修改索引

                }
            } else {
                // 校验索引字段并且补全字段参数
                List<String> columnIds = new ArrayList<>(); // 字段id listByPage，新增或修改索引信息后，都是后台自己拼装id
                String[] columnNameArr = index.getColumnNames().split(","); // 字段 name 数组
                String[] sortArr = index.getColumnSort().split(",");// 字段对应的排序
                if (columnNameArr.length != sortArr.length) {
                    throw new MetaDataException("错误的索引，字段对应的排序异常，索引名：" + index.getIndexName());
                }

                for (String columnName : columnNameArr) {
                    TableColumn tableCol = columnMap.get(columnName);
                    if (tableCol == null) {
                        throw new MetaDataException("索引有错误的关联字段，索引名：" + index.getIndexName());
                    }
                    // TODO 当前字段类型是否支持当前索引类型

                    columnIds.add(tableCol.getId() + "");
                }
                if (CollectionUtils.isEmpty(columnIds)) {
                    throw new MetaDataException("索引有错误的关联字段，索引名：" + index.getIndexName());
                }
                String columnIdStr = Joiner.on(",").join(columnIds);
                index.setColumnIds(columnIdStr); // 补全字段id
            }

            // 索引对应的字段是不能重复的
            if (indexColumnIdList.contains(index.getColumnIds())) {
                throw new MetaDataException("500", "索引对应字段重复，索引名：" + index.getIndexName());
            }

            // 连接参数
            Metadata metadata = new Metadata();
            metadata.setSchemaId(table.getSchemaId());
            RdbLinkDto config = oracleDDLService.getConnectionConfig(metadata);

            // 如果表空间为null，则设置默认的表空间
            if (StringUtils.isEmpty(index.getTablespace())) {
                if (config.getUsername().equalsIgnoreCase("sys") || config.getUsername()
                        .equalsIgnoreCase("system")) {
                    index.setTablespace("SYSTEM");
                } else {
                    index.setTablespace("USERS");
                }
            }

            // 如果模式为null，则设置默认的模式
            if (StringUtils.isEmpty(index.getSchemaName())) {
                // 默认模式即是当前模式，也就是当前登录的用户
                index.setSchemaName(config.getUsername().toUpperCase());
            }

            indexColumnIdList.add(index.getColumnIds());
            indexNameList.add(index.getIndexName());

            // 如果为直采或草稿则不需要去实体数据库校验
            if (table.getIsGather() || table.getStatus().equals(DataStatusEnum.DRAFT.getValue())) {
                continue;
            }
            // 如果为修改，但是当前索引并没被修改，则不需要去实体数据库校验
            if (versions > 1 && index.getStatus() == 0) {
                continue;
            }
            // 如果是修改（做修改动作，而且当前索引也被标识为修改），这里需要去查询快照版本，如果和快照版本索引名一致，则不需要查询数据库
            if (versions > 1 && index.getStatus() == 2) {
                TableIdxOracle snapshotIndex = snapshotIndexMap.get(index.getId());
                if (snapshotIndex != null && snapshotIndex.getIndexName()
                        .equals(index.getIndexName())) { // 索引名没有修改
                    continue;
                }
            }
            // 索引名当前模式是否存在
            RespResult<Boolean> indexNameExists = oracleService
                    .indexNameExists(index.getIndexName(), config);
            log.info("indexNameExists result:" + JSON.toJSONString(indexNameExists, true));
            if (indexNameExists.isSuccess() && EXISTS_CODE.equals(indexNameExists.getMsg())) {
                throw new MetaDataException("500", "数据库中已存在此索引名：" + index.getIndexName());
            }
        }
    }

    @Override
    public void validatedTableUnique(OracleTableVO table, List<TableUnOracle> uniqueList,
            TablePkOracle primaryKey, List<TableColumn> columnList, Integer versions) {
        // 约束名：主键约束，唯一约束，检查约束，外键约束在oracle中都是一张表内的，
        List<String> constraintNameList = new ArrayList<>(); // 约束名name listByPage
        List<String> uniqueColumnIdList = new ArrayList<>(); // 约束关联字段 listByPage（关联字段列表不能重复）

        // 约束名在模式内唯一，将主键约束名新增进去
        if (primaryKey != null && primaryKey.getSequenceStatus() != 1) {
            constraintNameList.add(primaryKey.getName());
        }

        // 当前表所有的字段，以name为key，新增时用到
        Map<String, TableColumn> columnMap =
                columnList.stream()
                        .collect(Collectors.toMap((key -> key.getColumnName()), (value -> value)));
        // 剔除删除的数据
        uniqueList = uniqueList.stream().filter(property -> property.getStatus() != 3)
                .collect(Collectors.toList());

        Map<Long, TableColumn> columnIdMap = null;
        // 快照版本的唯一约束（上一个版本的数据）
        Map<Long, TableUnOracle> snapshotUniqueMap = null;

        if (versions > 1) { // 是否为修改
            columnIdMap = columnList.stream()
                    .collect(Collectors.toMap((key -> key.getId()), (value -> value)));
            List<TableUnOracle> snapshotUniqueList = snapshotService
                    .getSnapshotUniqueListByTableId(table.getId(), table.getVersion() - 1);
            snapshotUniqueMap = snapshotUniqueList.stream()
                    .collect(Collectors.toMap((key -> key.getId()), (value -> value)));
        }

        for (TableUnOracle unique : uniqueList) {
            // 当前模式，约束名不能重复，元数据校验
            if (constraintNameList.contains(unique.getName())) {
                throw new MetaDataException("唯一约束，约束名已经存在：" + unique.getName());
            }

            TablePkOracle pk = new TablePkOracle();
            pk.setName(unique.getName());
            pk.setTableId(table.getId());
            if (primaryKeyMapper.find(pk) > 0) {
                throw new MetaDataException("唯一约束，约束名已经存在：" + unique.getName());
            }

            TableUnOracle un = new TableUnOracle();
            un.setId(unique.getId());
            un.setName(unique.getName());
            un.setTableId(table.getId());
            if (uniqueMapper.find(un) > 0) {
                throw new MetaDataException("唯一约束，约束名已经存在：" + unique.getName());
            }

            TableChOracle ch = new TableChOracle();
            ch.setName(unique.getName());
            ch.setTableId(table.getId());
            if (checkMapper.find(ch) > 0) {
                throw new MetaDataException("唯一约束，约束名已经存在：" + unique.getName());
            }

            TableFkOracle fk = new TableFkOracle();
            fk.setName(unique.getName());
            fk.setTableId(table.getId());
            if (foreignKeyMapper.find(fk) > 0) {
                throw new MetaDataException("唯一约束，约束名已经存在：" + unique.getName());
            }

            // 校验约束字段并补全字段参数
            if (versions > 1 && unique.getStatus() == 0) { // 如果是修改状态，但是当前唯一约束是没有修改的
                String[] idArr = unique.getColumnIds().split(",");
                for (String colId : idArr) {
                    Long columnId = Long.parseLong(colId);
                    TableColumn column = columnIdMap.get(columnId);
                    if (column == null) {
                        throw new MetaDataException("唯一约束有错误的关联字段，约束名：" + unique.getName());
                    }
                }
            } else {
                List<String> columnIds = new ArrayList<>(); // 字段id listByPage，新增或修改索引信息后，都是后台自己拼装id
                String[] columnNameArr = unique.getColumnNames().split(","); // 字段 name 数组
                for (String columnName : columnNameArr) {
                    TableColumn tableCol = columnMap.get(columnName);
                    if (tableCol == null) {
                        throw new MetaDataException("唯一约束有错误的关联字段，约束名：" + unique.getName());
                    }
                    columnIds.add(tableCol.getId() + "");
                }
                if (CollectionUtils.isEmpty(columnIds)) {
                    throw new MetaDataException("唯一约束有错误的关联字段，约束名：" + unique.getName());
                }
                String columnIdStr = Joiner.on(",").join(columnIds);
                unique.setColumnIds(columnIdStr); // 补全字段id
            }

            if (uniqueColumnIdList.contains(unique.getColumnIds())) {
                throw new MetaDataException("唯一约束，字段列表已经关联了约束，约束名：" + unique.getName());
            }

            uniqueColumnIdList.add(unique.getColumnIds());
            constraintNameList.add(unique.getName());

            // 如果为直采或草稿则不需要去实体数据库校验
            if (table.getIsGather() || table.getStatus() == 0) {
                continue;
            }
            // 如果为修改，但是当前约束并没被修改，则不需要去实体数据库校验
            if (versions > 1 && unique.getStatus() == 0) {
                continue;
            }
            // 如果是修改动作，约束也被标识为修改了，需要去查询快照版本，如果和快照版本约束名一致，则不需要查询数据库
            if (versions > 1 && unique.getStatus() == 2) {
                TableUnOracle snapshotUnique = snapshotUniqueMap.get(unique.getId());
                if (snapshotUnique != null && snapshotUnique.getName().equals(unique.getName())) {
                    continue;
                }
            }

            // 连接参数
            Metadata metadata = new Metadata();
            metadata.setSchemaId(table.getSchemaId());
            RdbLinkDto config = oracleDDLService.getConnectionConfig(metadata);
            // 约束名当前模式是否存在
            RespResult<Boolean> constraintsNameExists = oracleService
                    .constraintsNameExists(unique.getName(), config);
            log.info("constraintsNameExists result:" + JSON
                    .toJSONString(constraintsNameExists, true));
            if (constraintsNameExists.isSuccess() && EXISTS_CODE
                    .equals(constraintsNameExists.getMsg())) {
                throw new MetaDataException("500", "数据库中已存在此约束名：" + unique.getName());
            }
        }
    }

    @Override
    public void validatedTableCheck(OracleTableVO table, List<TableChOracle> checkList,
            List<TableUnOracle> uniqueList, TablePkOracle primaryKey, Integer versions) {
        List<String> constraintNameList = new ArrayList<>(); // 约束名name listByPage

        // 将主键约束名新增进去
        if (primaryKey != null && primaryKey.getSequenceStatus() != 1) { // 如果有主键
            constraintNameList.add(primaryKey.getName());
        }

        // 将唯一约束名新增进去
        if (CollectionUtils.isNotEmpty(uniqueList)) {
            for (TableUnOracle unique : uniqueList) {
                constraintNameList.add(unique.getName());
            }
        }

        // 剔除删除的数据
        checkList = checkList.stream().filter(property -> property.getStatus() != 3)
                .collect(Collectors.toList());

        // 老版本的检查约束列表
        Map<Long, TableChOracle> snapshotCheckMap = null;

        if (versions > 1) { // 是否为修改
            List<TableChOracle> snapshotCheckList = snapshotService
                    .getSnapshotCheckListByTableId(table.getId(), table.getVersion() - 1);
            snapshotCheckMap = snapshotCheckList.stream()
                    .collect(Collectors.toMap((key -> key.getId()), (value -> value)));
        }

        for (TableChOracle check : checkList) {
            // 当前模式，检查约束名不能重复，元数据校验
            if (constraintNameList.contains(check.getName())) {
                throw new MetaDataException("检查约束，约束名已经存在：" + check.getName());
            }

            TablePkOracle pk = new TablePkOracle();
            pk.setName(check.getName());
            pk.setTableId(table.getId());
            if (primaryKeyMapper.find(pk) > 0) {
                throw new MetaDataException("检查约束，约束名已经存在：" + check.getName());
            }

            TableUnOracle un = new TableUnOracle();
            un.setName(check.getName());
            un.setTableId(table.getId());
            if (uniqueMapper.find(un) > 0) {
                throw new MetaDataException("检查约束，约束名已经存在：" + check.getName());
            }

            TableChOracle ch = new TableChOracle();
            ch.setId(check.getId());
            ch.setName(check.getName());
            ch.setTableId(table.getId());
            if (checkMapper.find(ch) > 0) {
                throw new MetaDataException("检查约束，约束名已经存在：" + check.getName());
            }

            TableFkOracle fk = new TableFkOracle();
            fk.setName(check.getName());
            fk.setTableId(table.getId());
            if (foreignKeyMapper.find(fk) > 0) {
                throw new MetaDataException("检查约束，约束名已经存在：" + check.getName());
            }

            // TODO 校验sql是否正确

            // 如果为直采或草稿则不需要去实体数据库校验
            if (table.getIsGather() || table.getStatus() == 0) {
                continue;
            }
            // 如果为修改，但是当前约束并没被修改，则不需要去实体数据库校验
            if (versions > 1 && check.getStatus() == 0) {
                continue;
            }
            if (versions > 1 && check.getStatus() == 2) {
                // 如果是修改，则先比对上个版本和当前版本的检查约束name有没有改变，没有就不需要校验了
                TableChOracle snapshotCheck = snapshotCheckMap.get(check.getId());
                if (snapshotCheck != null && snapshotCheck.getName().equals(check.getName())) {
                    continue;
                }
            }
            // 连接参数
            Metadata metadata = new Metadata();
            metadata.setSchemaId(table.getSchemaId());
            RdbLinkDto config = oracleDDLService.getConnectionConfig(metadata);
            // 约束名当前模式是否存在
            RespResult<Boolean> constraintsNameExists = oracleService
                    .constraintsNameExists(check.getName(), config);
            log.info("constraintsNameExists result:" + JSON
                    .toJSONString(constraintsNameExists, true));
            if (constraintsNameExists.isSuccess() && EXISTS_CODE
                    .equals(constraintsNameExists.getMsg())) {
                throw new MetaDataException("500", "检查约束，数据库中已存在此约束名：" + check.getName());
            }
        }

    }

    @Override
    public void validatedTableForeignKey(OracleTableVO table, List<TableFkOracle> foreignKeyList,
            List<TableColumn> columnList, List<TableUnOracle> uniqueList,
            List<TableChOracle> checkList, TablePkOracle primaryKey, Integer versions) {
        List<String> constraintNameList = new ArrayList<>(); // 约束名name listByPage

        // 将主键约束名新增进去
        if (primaryKey != null && primaryKey.getSequenceStatus() != 1) { // 如果有主键
            constraintNameList.add(primaryKey.getName());
        }

        // 将唯一约束名新增进去
        if (CollectionUtils.isNotEmpty(uniqueList)) {
            for (TableUnOracle unique : uniqueList) {
                constraintNameList.add(unique.getName());
            }
        }

        // 将检查约束新增进去
        if (CollectionUtils.isNotEmpty(checkList)) {
            for (TableChOracle check : checkList) {
                constraintNameList.add(check.getName());
            }
        }

        // 当前表所有的列，以name为key，新增外键时使用
        Map<String, TableColumn> columnMap =
                columnList.stream()
                        .collect(Collectors.toMap((key -> key.getColumnName()), (value -> value)));

        // 剔除删除数据
        foreignKeyList = foreignKeyList.stream().filter(property -> property.getStatus() != 3)
                .collect(Collectors.toList());

        // 当前表的所有列，以id为key，修改外键时使用
        Map<Long, TableColumn> columnIdMap = null;
        // 老版本的外键约束列表
        Map<Long, TableFkOracle> snapshotForeignKeyMap = null;

        // 如果是修改，则将删除的外键剔除
        if (versions > 1) {
            columnIdMap = columnList.stream()
                    .collect(Collectors.toMap((key -> key.getId()), (value -> value)));
            List<TableFkOracle> snapshotForeignKeyList = snapshotService
                    .getSnapshotForeignKeyListByTableId(table.getId(), table.getVersion() - 1);
            snapshotForeignKeyMap = snapshotForeignKeyList.stream()
                    .collect(Collectors.toMap((key -> key.getId()), (value -> value)));
        }

        for (TableFkOracle foreignKey : foreignKeyList) {
            // 模式内约束名唯一
            if (constraintNameList.contains(foreignKey.getName())) {
                throw new MetaDataException("500", "外键约束，约束名已经存在：" + foreignKey.getName());
            }

            TablePkOracle pk = new TablePkOracle();
            pk.setName(foreignKey.getName());
            pk.setTableId(table.getId());
            if (primaryKeyMapper.find(pk) > 0) {
                throw new MetaDataException("外键约束，约束名已经存在：" + foreignKey.getName());
            }

            TableUnOracle un = new TableUnOracle();
            un.setName(foreignKey.getName());
            un.setTableId(table.getId());
            if (uniqueMapper.find(un) > 0) {
                throw new MetaDataException("外键约束，约束名已经存在：" + foreignKey.getName());
            }

            TableChOracle ch = new TableChOracle();
            ch.setName(foreignKey.getName());
            ch.setTableId(table.getId());
            if (checkMapper.find(ch) > 0) {
                throw new MetaDataException("外键约束，约束名已经存在：" + foreignKey.getName());
            }

            TableFkOracle fk = new TableFkOracle();
            fk.setId(foreignKey.getId());
            fk.setName(foreignKey.getName());
            fk.setTableId(table.getId());
            if (foreignKeyMapper.find(fk) > 0) {
                throw new MetaDataException("外键约束，约束名已经存在：" + foreignKey.getName());
            }

            // 删除触发事件类型是否正确
            if (StringUtils.isNotBlank(foreignKey.getDeleteTrigger())) {
                if (!affairList.stream().anyMatch(
                        affair -> affair.equalsIgnoreCase(foreignKey.getDeleteTrigger()))) {
                    throw new MetaDataException("错误的删除事件类型，外键名：" + foreignKey.getName());
                }
            }

            // 当前只能参考当前模式下的表
            foreignKey.setReferenceSchemaId(table.getSchemaId());
            Long referenceTableId = foreignKey.getReferenceTableId(); // 参考表id
            Metadata referenceMetadata = new Metadata();
            referenceMetadata.setSchemaId(table.getSchemaId());
            referenceMetadata.setId(referenceTableId);

            if (metadataService.findByMetadata(referenceMetadata) <= 0) {
                throw new MetaDataException("错误的参考表");
            }

            // 参考表的所有列
            List<TableColumn> referenceTableColumnList = columnMapper
                    .findTableColumnListByTableId(foreignKey.getReferenceTableId());
            Map<Long, TableColumn> referenceTableColumnMap =
                    referenceTableColumnList.stream()
                            .collect(Collectors.toMap((key -> key.getId()), (value -> value)));

            // 参考的约束类
            TableConstraintVO referenceConstraint = null;
            Long restrainId = foreignKey.getReferenceRestrain(); // 参考的约束id
            Integer restrainType = foreignKey.getReferenceRestrainType(); // 参考的约束类型
            if (restrainType == null) {
                throw new MetaDataException("参考的约束类型");
            }
            if (restrainType.equals(DBEnum.ConstraintTypeEnum.PRIMARY_KEY.getCode())) {
                // 主键约束
                TablePkOracle referencePk = primaryKeyMapper.selectByPrimaryKey(restrainId);
                if (referencePk.getIsDeleted()) {
                    throw new MetaDataException("当前参考约束已被删除或已被禁用");
                }
                referenceConstraint = new TableConstraintVO();
                referenceConstraint.setConstraintName(referencePk.getName());
                List<String> columnIdList = new ArrayList<>();
                for (TableColumn column : referenceTableColumnList) {
                    if (column.getIsPk()) {
                        columnIdList.add(column.getId() + "");
                    }
                }
                referenceConstraint.setColumnIds(StringUtils.join(columnIdList, ","));
            } else if (restrainType.equals(DBEnum.ConstraintTypeEnum.UNIQUE.getCode())) {
                // 唯一约束
                TableUnOracle referenceUnique = uniqueMapper.selectByPrimaryKey(restrainId);
                if (referenceUnique.getIsDeleted() || !referenceUnique.getIsEnabled()) {
                    throw new MetaDataException("当前参考约束已被删除或已被禁用");
                }
                referenceConstraint = new TableConstraintVO();
                referenceConstraint.setConstraintName(referenceUnique.getName());
                referenceConstraint.setColumnIds(referenceUnique.getColumnIds());
            }
            if (referenceConstraint == null) {
                throw new MetaDataException("错误的参考约束");
            }
            // 参考字段id str
            String referenceColumn = foreignKey.getReferenceColumn();
            if (!referenceConstraint.getColumnIds().equals(referenceColumn)) {
                throw new MetaDataException("错误的参考字段");
            }
            // 参考字段id数组
            String[] referenceColumnIdArr = foreignKey.getReferenceColumn().split(",");

            if (versions > 1 && foreignKey.getStatus() == 0) {// 没有修改的数据
                // 外键关联的字段 必须和 参考的字段 数量一致
                String[] columnIdArr = foreignKey.getColumnIds().split(",");
                if (columnIdArr.length <= 0 || (columnIdArr.length
                        != referenceColumnIdArr.length)) {
                    throw new MetaDataException("500", "错误的关联字段，外键名：" + foreignKey.getName());
                }

                // 外键关联字段是否存在
                for (String columnId : columnIdArr) {
                    TableColumn column = columnIdMap.get(Long.parseLong(columnId));
                    if (column == null) {
                        throw new MetaDataException("错误的关联字段，外键名：" + foreignKey.getName());
                    }
                }

                // 参考字段与关联字段类型需要一致
                for (int i = 0; i < referenceColumnIdArr.length; i++) {
                    String referenceColumnId = referenceColumnIdArr[i]; // 参考字段
                    TableColumn referenceCol = referenceTableColumnMap
                            .get(Long.parseLong(referenceColumnId));
                    if (org.apache.commons.lang3.StringUtils.isBlank(referenceColumnId)
                            || referenceCol == null) {
                        throw new MetaDataException("外键名：" + foreignKey.getName() + "，有错误的参考列，请检查");
                    }

                    String tableColId = columnIdArr[i]; // 参考列对应的关联列
                    TableColumn tableCol = columnIdMap.get(Long.parseLong(tableColId));
                    if (!tableCol.getColumnType().equals(referenceCol.getColumnType())) {
                        throw new MetaDataException(
                                "外键名：" + foreignKey.getName() + "，外建关联字段和外键参考字段数据类型不一致，请检查");
                    }
                }
            } else {
                // 外键关联的字段 必须和 参考的字段 数量一致
                String[] columnNameArr = foreignKey.getColumnNames().split(",");// 关联字段 name 数组
                if (columnNameArr.length != referenceColumnIdArr.length) {
                    throw new MetaDataException("500", "错误的关联字段，外键名：" + foreignKey.getName());
                }

                // 校验并且获取关联字段的id
                List<String> columnIds = new ArrayList<>(); // 字段id listByPage，新增页面传递过来时字段是没有id的
                for (String columnName : columnNameArr) {
                    // 外键关联字段是否存在
                    TableColumn tableCol = columnMap.get(columnName);
                    if (tableCol == null) {
                        throw new MetaDataException("外键有错误的关联字段，外键名：" + foreignKey.getName());
                    }
                    columnIds.add(tableCol.getId() + "");
                }
                if (CollectionUtils.isEmpty(columnIds)) {
                    throw new MetaDataException("外键有错误的关联字段，外键名：" + foreignKey.getName());
                }
                String columnIdStr = Joiner.on(",").join(columnIds); // 当前外键关联的字段
                // 补全关联字段id
                foreignKey.setColumnIds(columnIdStr);

                // 参考字段与关联字段类型需要一致
                for (int i = 0; i < referenceColumnIdArr.length; i++) {
                    String referenceColumnId = referenceColumnIdArr[i]; // 参考字段
                    TableColumn referenceCol = referenceTableColumnMap
                            .get(Long.parseLong(referenceColumnId));
                    if (StringUtils.isBlank(referenceColumnId) || referenceCol == null) {
                        throw new MetaDataException("外键名：" + foreignKey.getName() + "，有错误的参考列，请检查");
                    }
                    String tableColId = columnNameArr[i]; // 参考列对应的关联列
                    TableColumn tableCol = columnMap.get(tableColId);
                    if (!tableCol.getColumnType().equalsIgnoreCase(referenceCol.getColumnType())) {
                        throw new MetaDataException(
                                "外键名：" + foreignKey.getName() + "，外建关联字段和外键参考字段数据类型不一致，请检查");
                    }
                }
            }

            constraintNameList.add(foreignKey.getName());

            // 如果为直采或草稿则不需要去实体数据库校验
            if (table.getIsGather() || table.getStatus() == 0) {
                continue;
            }
            // 如果为修改，但是当前约束并没被修改，则不需要去实体数据库校验
            if (versions > 1 && foreignKey.getStatus() == 0) {
                continue;
            }
            // 如果标识为修改，外键状态也是修改，则查询快照信息，如果约束名没有修改则不需要去实体数据库校验
            if (versions > 1 && foreignKey.getStatus() == 2) {
                if (foreignKey.getId() == null) {
                    throw new MetaDataException("错误的状态");
                }
                TableFkOracle snapshotForeignKey = snapshotForeignKeyMap.get(foreignKey.getId());
                if (snapshotForeignKey != null && snapshotForeignKey.getName()
                        .equals(foreignKey.getName())) {
                    continue;
                }
            }

            // 连接参数
            Metadata metadata = new Metadata();
            metadata.setSchemaId(table.getSchemaId());
            RdbLinkDto config = oracleDDLService.getConnectionConfig(metadata);

            // 约束名当前模式是否存在
            RespResult<Boolean> constraintsNameExists = oracleService
                    .constraintsNameExists(foreignKey.getName(), config);
            log.info("constraintsNameExists result:" + JSON
                    .toJSONString(constraintsNameExists, true));
            if (constraintsNameExists.isSuccess() && EXISTS_CODE
                    .equals(constraintsNameExists.getMsg())) {
                throw new MetaDataException("500", "外键约束，数据库中已存在此约束名：" + foreignKey.getName());
            }
        }
    }

    @Override
    public void validatedTableSetting(OracleTableVO table, TableSetOracle setting) {
        Metadata metadata = new Metadata();
        metadata.setSchemaId(table.getSchemaId());
        RdbLinkDto config = oracleDDLService.getConnectionConfig(metadata);

        // 如果表空间为空
        if (StringUtils.isEmpty(setting.getTablespace())) {
            if (config.getUsername().equalsIgnoreCase("sys") || config.getUsername()
                    .equalsIgnoreCase("system")) {
                setting.setTablespace("SYSTEM");
            } else {
                setting.setTablespace("USERS");
            }
        }
    }

    @Override
    public void validatedView(DBViewVO view, Integer versions) {
        if (!view.getDatabaseType().equals(DatabaseTypeEnum.ORACLE.getCode())) {
            // 当前数据库不为 oracle
            throw new MetaDataException("500", "错误的数据库类型");
        }
        if (!view.getResourceType().equals(2)) {
            // 当前资源类型不为 视图
            throw new MetaDataException("500", "错误的资源类型");
        }

        McSchemaPO schema = schemaService.findById(view.getSchemaId());
        if (schema == null) {
            throw new MetaDataException("异常的模式信息");
        }

        Metadata metadata = new Metadata();
        metadata.setId(view.getId());// 如果是修改，那么排除当前表数据
        metadata.setSchemaId(view.getSchemaId());
        metadata.setDatabaseType(DatabaseTypeEnum.ORACLE.getCode()); // oracle 类型
        metadata.setResourceType(2); // 2:视图

        // 判断视图名是否重复
        metadata.setName(view.getName());
        if (metadataService.findByMetadata(metadata) > 0) {
            throw new MetaDataException("500", "当前视图英文名已经被占用（视图已创建或在草稿箱中）");
        }

        /*// 判断表中文名是否重复
        metadata.setName(null);
        metadata.setIdentification(view.getIdentification());
        if (metadataService.findByMetadata(metadata) > 0) {
            throw new MetaDataException("500", "当前视图英文中文名已经被占用（视图已创建或在草稿箱中）");
        }*/

        ViewDetail viewDetail = view.getViewDetail();
        if (viewDetail == null) {
            throw new MetaDataException("500", "视图详情不能为空");
        }

        // 如果为草稿则不需要去实体数据库校验
        if (view.getStatus() == 0) {
            return;
        }
        // 如果为修改，则查询出前一个版本的表基本信息，如果视图名没有修改，则不需要去数据库中查询了。
        if (versions > 1) {
            Metadata snapshotTable = snapshotService
                    .getSnapshotMetadataInfoByMetadataId(view.getId(), view.getVersion() - 1);
            // 如果 snapshotTable 等于null，则可能是草稿做生效动作
            if (snapshotTable != null && snapshotTable.getName().equals(view.getName())) {
                return;
            }
        }

        RdbLinkDto config = oracleDDLService.getConnectionConfig(metadata);
        // 判断视图名是否已被存在的视图占用
        RespResult<Boolean> viewNameExists = oracleService.viewNameExists(view.getName(), config);
        log.info("nameExists result:" + JSON.toJSONString(viewNameExists, true));
        if (viewNameExists.isSuccess() && EXISTS_CODE.equals(viewNameExists.getMsg())) {
            throw new MetaDataException("500", "数据库中已存在此视图名：" + view.getName());
        }

        // 判断表名是否已被存在的表占用
        RespResult<Boolean> tableNameExists = oracleService.tableNameExists(view.getName(), config);
        log.info("tableNameExists result:" + JSON.toJSONString(tableNameExists, true));
        if (tableNameExists.isSuccess() && EXISTS_CODE.equals(tableNameExists.getMsg())) {
            throw new MetaDataException("500", "数据库中已存在此视图名：" + view.getName());
        }
    }

}