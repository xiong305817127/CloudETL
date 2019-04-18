package com.ys.idatrix.metacube.metamanage.service.impl;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.google.common.base.Joiner;
import com.ys.idatrix.metacube.api.beans.DatabaseTypeEnum;
import com.ys.idatrix.metacube.common.enums.DBEnum;
import com.ys.idatrix.metacube.common.exception.MetaDataException;
import com.ys.idatrix.metacube.metamanage.domain.*;
import com.ys.idatrix.metacube.metamanage.mapper.MetadataMapper;
import com.ys.idatrix.metacube.metamanage.mapper.TableColumnMapper;
import com.ys.idatrix.metacube.metamanage.mapper.TableFkMysqlMapper;
import com.ys.idatrix.metacube.metamanage.mapper.TableIdxMysqlMapper;
import com.ys.idatrix.metacube.metamanage.service.McSchemaService;
import com.ys.idatrix.metacube.metamanage.service.MetadataService;
import com.ys.idatrix.metacube.metamanage.service.MysqlSnapshotService;
import com.ys.idatrix.metacube.metamanage.service.MysqlValidatedService;
import com.ys.idatrix.metacube.metamanage.vo.request.DBViewVO;
import com.ys.idatrix.metacube.metamanage.vo.request.MySqlTableVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName MysqlValidatedServiceImpl
 * @Description mysql校验实现类
 * @Author ouyang
 * @Date
 */
@Service
public class MysqlValidatedServiceImpl implements MysqlValidatedService {

    @Autowired
    @Qualifier("mySqlSchemaService")
    private McSchemaService schemaService;

    @Autowired
    private MysqlSnapshotService mysqlSnapshotService;

    @Autowired
    private MetadataService metadataService;

    @Autowired
    private MetadataMapper metadataMapper;

    @Autowired
    private TableColumnMapper tableColumnMapper;

    @Autowired
    private TableFkMysqlMapper tableFkMysqlMapper;

    @Autowired
    private TableIdxMysqlMapper tableIdxMysqlMapper;

    // 字段数据类型list
    private static List<String> tableDataTypeList
            = Arrays.stream(DBEnum.MysqlTableDataType.values()).map(mysqlTableDataType -> mysqlTableDataType.name()).collect(Collectors.toList());

    // 索引类型list
    private static List<String> indexTypeList
            = Arrays.stream(DBEnum.MysqlIndexTypeEnum.values()).map(mysqlIndexTypeEnum -> mysqlIndexTypeEnum.name()).collect(Collectors.toList());

    // 索引方法list
    private static List<String> indexMethodList
            = Arrays.stream(DBEnum.MysqlIndexMethodEnum.values()).map(mysqlIndexMethodEnum -> mysqlIndexMethodEnum.name()).collect(Collectors.toList());

    // 删除或修改时触发事件list
    private static List<String> affairList
            = Arrays.stream(DBEnum.MysqlFKTriggerAffairEnum.values()).map(affair -> affair.name()).collect(Collectors.toList());

    private static List<String> algorithmList =
            Arrays.stream(DBEnum.MysqlViewAlgorithm.values()).map(algorithm -> algorithm.name()).collect(Collectors.toList());

    private static List<String> securityList =
            Arrays.stream(DBEnum.MysqlViewSecurity.values()).map(security -> security.name()).collect(Collectors.toList());

    private static List<String> checkOptionList =
            Arrays.stream(DBEnum.MysqlViewCheckOption.values()).map(checkOption -> checkOption.name()).collect(Collectors.toList());


    @Override
    public void validatedTableBaseInfo(MySqlTableVO mysqlTable) {
        if (!mysqlTable.getDatabaseType().equals(DatabaseTypeEnum.MYSQL.getCode())) {
            // 当前数据库不为 mysql
            throw new MetaDataException("500", "错误的数据库类型");
        }
        if (!mysqlTable.getResourceType().equals(1)) {
            // 当前资源类型不为 表
            throw new MetaDataException("500", "错误的资源类型");
        }

        McSchemaPO schema = schemaService.findById(mysqlTable.getSchemaId());
        if (schema == null) {
            throw new MetaDataException("异常的模式");
        }

        Metadata metadata = new Metadata();
        metadata.setId(mysqlTable.getId());// 如果是修改，那么排除当前表数据
        metadata.setSchemaId(mysqlTable.getSchemaId()); // schemaId
        metadata.setName(mysqlTable.getName()); // 表名
        metadata.setDatabaseType(DatabaseTypeEnum.MYSQL.getCode()); // mysql
        metadata.setResourceType(1); // 当前为表

        // 同模式下表名和视图名不能重复
        if (metadataService.findByMetadata(metadata) > 0) {
            throw new MetaDataException("500", "当前表英文名已经被占用（表已创建或在草稿箱中）");
        }
        metadata.setResourceType(2); // 当前为视图
        if (metadataService.findByMetadata(metadata) > 0) {
            throw new MetaDataException("500", "当前表英文名已经被视图占用（视图已创建或在草稿箱中）");
        }

        /*// 判断表中文名是否重复
        metadata.setResourceType(1); // 当前为表
        metadata.setName(null);
        metadata.setIdentification(mysqlTable.getIdentification());
        if (metadataService.findByMetadata(metadata) > 0) {
            throw new MetaDataException("500", "当前表中文名已经被占用（表已创建或在草稿箱中）");
        }*/
    }

    @Override
    public void validatedTableColumn(List<TableColumn> tableColumnList, Boolean hasFilter) {
        /**
         * 考虑情况：
         * 字段名重复
         * 字段数据类型错误
         * 自增时类型是否支持
         * 自增字段必须对应一个索引，而且时非全文索引
         */
        if (!hasFilter) {// 互斥条件，新增时字段不能为空，修改时，修改字段可以为空
            if (CollectionUtils.isEmpty(tableColumnList)) {
                throw new MetaDataException("字段不能为空");
            }
        }

        if (hasFilter) { // 是否为修改，如果是修改的话把需要删除的列剔除掉
            tableColumnList = tableColumnList.stream().filter(property -> property.getStatus() != 3).collect(Collectors.toList());
        }

        List<String> columnNameList = new ArrayList<>(); // 字段 name list
        int autoIncrementColumn = 0; // 自增的字段
        for (TableColumn column : tableColumnList) {
            // 当前表中，字段名不能重复
            if (columnNameList.contains(column.getColumnName())) {
                throw new MetaDataException("500", "字段名重复：" + column.getColumnName());
            }

            // 当前字段数据类型是存在的
            if (!tableDataTypeList.stream().anyMatch(tableDataType -> tableDataType.equalsIgnoreCase(column.getColumnType()))) {
                throw new MetaDataException("500", "字段数据类型错误，字段名：" + column.getColumnName());
            }

            // 字段类型
            String columnType = column.getColumnType();
            String columnTypeUpperCase = columnType.toUpperCase();

            // 如果当前字段类型没有精度则精度设空
            if (!(DBEnum.MysqlTableDataType.NUMERIC.getName().equals(columnTypeUpperCase) ||
                    DBEnum.MysqlTableDataType.DECIMAL.getName().equals(columnTypeUpperCase) ||
                    DBEnum.MysqlTableDataType.DOUBLE.getName().equals(columnTypeUpperCase) ||
                    DBEnum.MysqlTableDataType.FLOAT.getName().equals(columnTypeUpperCase) ||
                    DBEnum.MysqlTableDataType.REAL.getName().equals(columnTypeUpperCase))) {
                column.setTypePrecision(null);
            }

            // auto_increment还会区分InnoDB和MyISAM，myisam可以使用多个字段作为一个 auto_increment，而innodb不行

            // 当前字段如果是自增的，类型是否支持（不是主键也可以自增长）
            if (column.getIsAutoIncrement()) {
                if (!(DBEnum.MysqlTableDataType.TINYINT.name().equals(columnTypeUpperCase) ||
                        DBEnum.MysqlTableDataType.SMALLINT.name().equals(columnTypeUpperCase) ||
                        DBEnum.MysqlTableDataType.NUMERIC.name().equals(columnTypeUpperCase) ||
                        DBEnum.MysqlTableDataType.DECIMAL.name().equals(columnTypeUpperCase) ||
                        DBEnum.MysqlTableDataType.DOUBLE.name().equals(columnTypeUpperCase) ||
                        DBEnum.MysqlTableDataType.FLOAT.name().equals(columnTypeUpperCase) ||
                        DBEnum.MysqlTableDataType.BIGINT.name().equals(columnTypeUpperCase) ||
                        DBEnum.MysqlTableDataType.INT.name().equals(columnTypeUpperCase) ||
                        DBEnum.MysqlTableDataType.INTEGER.name().equals(columnType) ||
                        DBEnum.MysqlTableDataType.MEDIUMINT.name().equals(columnTypeUpperCase))) {
                    throw new MetaDataException("自增长不支持的数据类型，数据类型：" + column.getColumnType());
                }
                autoIncrementColumn++;
            }

            if(autoIncrementColumn > 1) {
                throw new MetaDataException("当前自增长只支持一个字段");
            }
            /**
             * TODO 当前字段自增，那么当前字段必须对应着索引，组合索引也可以，但是对索引类型有要求，必须不是全文索引,主键自动创建了唯一索引，并且不为null。
             * 注意：自增需要先有索引后才能设置为自增
             */
            columnNameList.add(column.getColumnName());
        }
    }

    @Override
    public void validatedTableIndex(List<TableIdxMysql> tableIndexList, List<TableColumn> tableColumnList, Boolean hasFilter) {
        /**
         * 考虑情况：
         * 索引重复
         * 索引对应的列是否存在
         * 字段对应的索引类型是否支持
         * 索引对应的列是否重复
         */
        List<String> indexNameList = new ArrayList<>(); // 索引名name listByPage
        List<String> indexColumnIdList = new ArrayList<>(); //索引字段 listByPage

        // 当前表所有的列，以name为key，新增时用到
        Map<String, TableColumn> columnMap =
                tableColumnList.stream().collect(Collectors.toMap((key -> key.getColumnName()), (value -> value)));

        // 当前表所有的列，以id为key，修改时用到
        Map<Long, TableColumn> columnIdMap = null;

        if (hasFilter) {
            tableIndexList = tableIndexList.stream().filter(property -> property.getStatus() != 3).collect(Collectors.toList());
            columnIdMap = tableColumnList.stream().collect(Collectors.toMap((key -> key.getId()), (value -> value)));
        }

        for (TableIdxMysql index : tableIndexList) {
            // 当前表中，索引名不能重复
            if (indexNameList.contains(index.getIndexName())) {
                throw new MetaDataException("500", "重复的索引名：" + index.getIndexName());
            }

            // 判断索引类型是否存在
            if (!indexTypeList.stream().anyMatch(indexType -> indexType.equalsIgnoreCase(index.getIndexType()))) {
                throw new MetaDataException("500", "错误的索引类型，索引名：" + index.getIndexName());
            }

            // 判断索引方法是否存在
            if (!indexMethodList.stream().anyMatch(indexMethod -> indexMethod.equalsIgnoreCase(index.getIndexMethod()))) {
                throw new MetaDataException("500", "错误的索引方法，索引名：" + index.getIndexName());
            }

            String indexType = index.getIndexType().toUpperCase(); // 当前索引类型

            if (hasFilter && index.getStatus() == 0) {// 当前索引信息没有被修改，数据从数据库查询出来的，没有names的值
                // 没有修改的索引信息值也需要拿出来校验下，判断字段对于的索引类型是否符合
                String[] colIdArr = index.getColumnIds().split(",");
                for (String columnId : colIdArr) {
                    TableColumn tableCol = columnIdMap.get(Long.parseLong(columnId));
                    if (tableCol == null) {
                        throw new MetaDataException("索引有错误的关联字段，索引名：" + index.getIndexName());
                    }
                    // 判断当前索引类型是否支持索引对于字段数据类型
                    // FULLTEXT is not support some data type
                    if (DBEnum.MysqlIndexTypeEnum.FULLTEXT.getName().equals(indexType)) {
                        if (! (DBEnum.MysqlTableDataType.CHAR.name().equalsIgnoreCase(tableCol.getColumnType()) ||
                                DBEnum.MysqlTableDataType.VARCHAR.name().equalsIgnoreCase(tableCol.getColumnType()) ||
                                DBEnum.MysqlTableDataType.TEXT.name().equalsIgnoreCase(tableCol.getColumnType()) ||
                                DBEnum.MysqlTableDataType.MEDIUMTEXT.name().equalsIgnoreCase(tableCol.getColumnType()) ||
                                DBEnum.MysqlTableDataType.LONGTEXT.name().equalsIgnoreCase(tableCol.getColumnType()) )) {
                            throw new MetaDataException("the index type FULLTEXT is not support data type:" + tableCol.getColumnType());
                        }
                    }
                }
            } else { // 新增或修改了当前索引信息，那么前台就会传递columnNames参数
                // 校验字段并且补全字段参数
                List<String> columnIds = new ArrayList<>(); // 字段id listByPage，新增或修改索引信息后，都是后台自己拼装id
                String[] columnNameArr = index.getColumnNames().split(","); // 字段 name 数组
                for (String columnName : columnNameArr) {
                    TableColumn tableCol = columnMap.get(columnName);
                    if (tableCol == null) {
                        throw new MetaDataException("索引有错误的关联字段，索引名：" + index.getIndexName());
                    }
                    columnIds.add(tableCol.getId() + "");

                    // 判断当前索引类型是否支持索引对于字段数据类型
                    // FULLTEXT is not support some data type
                    if (DBEnum.MysqlIndexTypeEnum.FULLTEXT.getName().equals(indexType)) {
                        if (!(DBEnum.MysqlTableDataType.CHAR.name().equalsIgnoreCase(tableCol.getColumnType()) ||
                                DBEnum.MysqlTableDataType.VARCHAR.name().equalsIgnoreCase(tableCol.getColumnType()) ||
                                DBEnum.MysqlTableDataType.TEXT.name().equalsIgnoreCase(tableCol.getColumnType()) ||
                                DBEnum.MysqlTableDataType.MEDIUMTEXT.name().equalsIgnoreCase(tableCol.getColumnType()) ||
                                DBEnum.MysqlTableDataType.LONGTEXT.name().equalsIgnoreCase(tableCol.getColumnType())) ) {
                            throw new MetaDataException("the index type FULLTEXT is not support data type:" + tableCol.getColumnType());
                        }
                    }

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

            indexNameList.add(index.getIndexName());
            indexColumnIdList.add(index.getColumnNames());
        }
    }

    @Override
    public void validatedTableForeignKey(List<TableFkMysql> tableFkMysqlList, List<TableColumn> tableColumnList, List<TableIdxMysql> tableIndexList, Long tableId, String creator, Date createTime, Boolean hasFilter) {
        /**
         *  外键名 模式内唯一
         *  删除时触发事件是否正常
         *  修改时触发事件是否正常
         *  参考表是否正常
         *  参考列是否正常
         *  关联字段与参考字段要一致（个数一致，类型一致）
         *  外键关联的字段必须要有索引，如果没有，新建或修改外键时，需要自动尝试生成一个索引。如果外键没有被修改，但是没有对应外键，则报错
         *  一旦字段被外键关联后，外键不修改（修改name也行），字段类型和长度都不可修改，外键修改了才能修改字段的类型和长度。
         */
        if (hasFilter) {
            tableFkMysqlList = tableFkMysqlList.stream().filter(property -> property.getStatus() != 3).collect(Collectors.toList());
        }

        // 当前表所有的列，以name为key，新增外键时使用
        Map<String, TableColumn> columnMap =
                tableColumnList.stream().collect(Collectors.toMap((key -> key.getColumnName()), (value -> value)));

        // 当前表中所有的主键id
        String pkColStr = "";
        for (TableColumn column : tableColumnList) {
            if (column.getIsPk()) {
                pkColStr += column.getId();
            }
        }

        // 当前表的所有列，以id为key，修改外键时使用
        Map<Long, TableColumn> columnIdMap = null;
        Metadata table = null; // 表基本信息，修改外键时使用
        if (hasFilter) {
            table = metadataMapper.findById(tableId);
            columnIdMap = tableColumnList.stream().collect(Collectors.toMap((key -> key.getId()), (value -> value)));
        }

        // 当前表的所有索引，以索引name为key
        Map<String, TableIdxMysql> indexMap =
                tableIndexList.stream().collect(Collectors.toMap((key -> key.getIndexName()), (value -> value)));

        // 当前表索引关联的所有字段id
        List<String> indexColumn = tableIdxMysqlMapper.findIndexColumnIdsByTable(tableId);

        for (TableFkMysql foreignKey : tableFkMysqlList) {
            // 当前模式中，外键名不能重复
            if (tableFkMysqlMapper.findByTableFkMysql(foreignKey) > 0) {
                throw new MetaDataException("500", "外键名重复：" + foreignKey.getName());
            }

            // 删除时触发事件错误
            if (!affairList.stream().anyMatch(affair -> affair.equalsIgnoreCase(foreignKey.getDeleteTrigger()))) {
                throw new MetaDataException("500", "删除时触发事件错误，外键名：" + foreignKey.getName());
            }

            // 修改时触发事件错误
            if (!affairList.stream().anyMatch(affair -> affair.equalsIgnoreCase(foreignKey.getUpdateTrigger()))) {
                throw new MetaDataException("500", "修改时触发事件错误，外键名：" + foreignKey.getName());
            }

            String[] referenceColumnIdArr = foreignKey.getReferenceColumn().split(","); // 参考 字段 数组

            Metadata referenceTable = metadataMapper.findById(foreignKey.getReferenceTableId());
            if (referenceTable == null) {
                throw new MetaDataException("参考表异常，外键名：" + foreignKey.getName());
            }

            // 参考表的所有列
            List<TableColumn> referenceTableColumnList = tableColumnMapper.findTableColumnListByTableId(foreignKey.getReferenceTableId());
            if (CollectionUtils.isEmpty(referenceTableColumnList)) {
                throw new MetaDataException("参考列异常，外键名：" + foreignKey.getName());
            }
            Map<Long, TableColumn> referenceTableColumnMap =
                    referenceTableColumnList.stream().collect(Collectors.toMap((key -> key.getId()), (value -> value)));

            if (hasFilter && foreignKey.getStatus() == 0) {// 没有修改的数据
                // 外键关联的字段 必须和 参考的字段 数量一致
                String[] columnIdArr = foreignKey.getColumnIds().split(",");
                if (columnIdArr.length <= 0 || (columnIdArr.length != referenceColumnIdArr.length)) {
                    throw new MetaDataException("500", "错误的关联字段，外键名：" + foreignKey.getName());
                }

                // 外键关联字段是否存在 || 外键所需索引是否存在
                for (String columnId : columnIdArr) {
                    TableColumn column = columnIdMap.get(Long.parseLong(columnId));
                    if (column == null) {
                        throw new MetaDataException("错误的关联字段，外键名：" + foreignKey.getName());
                    }
                    // 当前外键没有修改，所以字段（数据类型，长度）也不能修改
                    // 拿到之前版本的字段数据 和 当前最新字段比较
                    // 之前版本的字段数据
                    SnapshotTableColumn snapshotColumn =
                            mysqlSnapshotService.getSnapshotColumn(column.getId(), table.getVersion() - 1);
                    if (!snapshotColumn.getColumnType().equals(column.getColumnType()) || !snapshotColumn.getTypeLength().equals(column.getTypeLength())) {
                        throw new MetaDataException("当前外键名：" + foreignKey.getName() + "，关联的字段类型或数据长度异常");
                    }
                }

                // 外键关联字段是否存在索引，不存在索引便抛出异常（这里外键数据是不变的，之前一定时有索引才能建外键，如果现在没有了
                // 那么就是用户人工删除，不做自动新建外键了）
                Boolean isExistIndex = true;
                String columnIdStr = Joiner.on(",").join(columnIdArr);
                if (indexColumn == null || !indexColumn.contains(columnIdStr)) {
                    isExistIndex = false;
                }
                // 如果当前关联字段都是主键
                if (columnIdStr.equals(pkColStr)) {
                    isExistIndex = true;
                }
                if (isExistIndex) {
                    throw new MetaDataException("外键名：" + foreignKey.getName() + "，缺少必要的索引");
                }

                // 参考字段与关联字段类型需要一致
                for (int i = 0; i < referenceColumnIdArr.length; i++) {
                    String referenceColumnId = referenceColumnIdArr[i]; // 参考字段
                    TableColumn referenceCol = referenceTableColumnMap.get(Long.parseLong(referenceColumnId));
                    if (org.apache.commons.lang3.StringUtils.isBlank(referenceColumnId) || referenceCol == null) {
                        throw new MetaDataException("外键名：" + foreignKey.getName() + "，有错误的参考列，请检查");
                    }

                    String tableColId = columnIdArr[i]; // 参考列对应的关联列
                    TableColumn tableCol = columnIdMap.get(Long.parseLong(tableColId));
                    if (!tableCol.getColumnType().equals(referenceCol.getColumnType())) {
                        throw new MetaDataException("外键名：" + foreignKey.getName() + "，外建关联字段和外键参考字段数据类型不一致，请检查");
                    }
                }

            } else {// 新增或修改的数据（当前数据有 字段names，字段id可能是没有的，所以这里以names来做）
                String[] columnNameArr = foreignKey.getColumnNames().split(",");// 字段 name 数组
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

                // 当前外键关联的字段
                String columnIdStr = Joiner.on(",").join(columnIds);

                Boolean isExistIndex = false;
                // 外键关联字段是否存在索引，不存在索引需要自动生成一个索引
                if (indexColumn != null && indexColumn.contains(columnIdStr)) {
                    isExistIndex = true;
                }
                // 如果当前关联字段都是主键
                if (columnIdStr.equals(pkColStr)) {
                    isExistIndex = true;
                }
                if (!isExistIndex) {
                    // 当前外键关联字段没有需要的索引，尝试自动去新建索引
                    String indexName = StringUtils.join(columnNameArr, "_");
                    if (indexMap.get(indexName) != null) {
                        throw new MetaDataException("创建外键：" + foreignKey.getName() + "时，尝试创建外键必要索引失败，请手动创建所需索引");
                    }
                    TableIdxMysql index = new TableIdxMysql();
                    index.setIndexName(indexName); // 索引名
                    index.setColumnIds(columnIdStr); // 参考字段id，可能多个
                    index.setTableId(tableId); // 表id
                    index.setCreator(creator);
                    index.setCreateTime(createTime);
                    index.setModifier(creator);
                    index.setModifyTime(createTime);
                    index.setIsDeleted(false);
                    index.setLocation(tableIdxMysqlMapper.findMaxLocationByTable(tableId) + 1);// 索引位置
                    tableIdxMysqlMapper.insertSelective(index);
                    indexMap.put(indexName, index); // 新增进索引map中
                }
                // 补全关联字段id
                foreignKey.setColumnIds(columnIdStr);

                // 参考字段与关联字段类型需要一致
                for (int i = 0; i < referenceColumnIdArr.length; i++) {
                    String referenceColumnId = referenceColumnIdArr[i]; // 参考字段
                    TableColumn referenceCol = referenceTableColumnMap.get(Long.parseLong(referenceColumnId));
                    if (org.apache.commons.lang3.StringUtils.isBlank(referenceColumnId) || referenceCol == null) {
                        throw new MetaDataException("外键名：" + foreignKey.getName() + "，有错误的参考列，请检查");
                    }
                    String tableColId = columnNameArr[i]; // 参考列对应的关联列
                    TableColumn tableCol = columnMap.get(tableColId);
                    if (!tableCol.getColumnType().equalsIgnoreCase(referenceCol.getColumnType())) {
                        throw new MetaDataException("外键名：" + foreignKey.getName() + "，外建关联字段和外键参考字段数据类型不一致，请检查");
                    }
                }
            }
        }
    }

    @Override
    public void validatedView(DBViewVO view) {
        if (!view.getDatabaseType().equals(DatabaseTypeEnum.MYSQL.getCode())) {
            // 当前数据库不为 mysql
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
        metadata.setSchemaId(view.getSchemaId()); // schema id
        metadata.setDatabaseType(DatabaseTypeEnum.MYSQL.getCode()); // mysql类型

        // 同一个模式下表名和视图名不能重复
        // 判断实体名是否重复
        metadata.setName(view.getName());
        if (metadataService.findByMetadata(metadata) > 0) {
            throw new MetaDataException("500", "当前视图英文名已经被占用（视图已创建或在草稿箱中）");
        }

        /*// 判断视图中文名是否重复
        metadata.setResourceType(2); // 当前为视图
        metadata.setName(null);
        metadata.setIdentification(view.getIdentification());
        if (metadataService.findByMetadata(metadata) > 0) {
            throw new MetaDataException("500", "当前视图英文中文名已经被占用（视图已创建或在草稿箱中）");
        }*/

        ViewDetail viewDetail = view.getViewDetail();
        if (viewDetail == null) {
            throw new MetaDataException("500", "视图详情不能为空");
        }

        // mysql视图定义者补全 `wzl3`@`%`
        viewDetail.setDefiniens("`" + schema.getUsername() + "`@`%`");

        // mysql视图算法，如果为空，则设置默认算法，如果不为空，判断算法类型是否存在
        if (StringUtils.isBlank(viewDetail.getArithmetic())) {
            viewDetail.setArithmetic(DBEnum.MysqlViewAlgorithm.UNDEFINED.getName());
        } else {
            if (!algorithmList.stream().anyMatch(algorithm -> algorithm.equalsIgnoreCase(viewDetail.getArithmetic()))) {
                throw new MetaDataException("错误的视图算法类型");
            }
        }

        // mysql视图安全性，如果为空，则设置默认安全性，如果不为空，判断安全性类型是否存在
        if (StringUtils.isBlank(viewDetail.getSecurity())) {
            viewDetail.setSecurity(DBEnum.MysqlViewSecurity.DEFINER.getName());
        } else {
            if (!securityList.stream().anyMatch(security -> security.equalsIgnoreCase(viewDetail.getSecurity()))) {
                throw new MetaDataException("错误的视图安全类型");
            }
        }

        // mysql检查选项如果不为空，则判断检查选项类型是否存在
        if (StringUtils.isNotBlank(viewDetail.getCheckOption())) {
            if (!checkOptionList.stream().anyMatch(security -> security.equalsIgnoreCase(viewDetail.getCheckOption()))) {
                throw new MetaDataException("错误的视图检查选项类型");
            }
        }

    }
}