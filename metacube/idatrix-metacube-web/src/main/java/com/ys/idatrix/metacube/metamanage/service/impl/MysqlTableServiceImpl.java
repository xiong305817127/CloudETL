package com.ys.idatrix.metacube.metamanage.service.impl;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Preconditions;
import com.ys.idatrix.metacube.api.beans.DatabaseTypeEnum;
import com.ys.idatrix.metacube.api.beans.PageResultBean;
import com.ys.idatrix.metacube.common.enums.DataStatusEnum;
import com.ys.idatrix.metacube.common.exception.MetaDataException;
import com.ys.idatrix.metacube.common.utils.UserUtils;
import com.ys.idatrix.metacube.metamanage.domain.*;
import com.ys.idatrix.metacube.metamanage.mapper.MetadataMapper;
import com.ys.idatrix.metacube.metamanage.mapper.TableColumnMapper;
import com.ys.idatrix.metacube.metamanage.mapper.TableFkMysqlMapper;
import com.ys.idatrix.metacube.metamanage.mapper.TableIdxMysqlMapper;
import com.ys.idatrix.metacube.metamanage.service.*;
import com.ys.idatrix.metacube.metamanage.vo.request.AlterSqlVO;
import com.ys.idatrix.metacube.metamanage.vo.request.MetadataSearchVo;
import com.ys.idatrix.metacube.metamanage.vo.request.MySqlTableVO;
import com.ys.idatrix.metacube.metamanage.vo.request.TableVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName MysqlTableServiceImpl
 * @Description mysql table 服务实现类
 * @Author ouyang
 * @Date
 */
@Slf4j
@Transactional
@Service
public class MysqlTableServiceImpl implements MysqlTableService {

    @Autowired
    private ThemeService themeService;

    @Autowired
    @Qualifier("mySqlSchemaService")
    private McSchemaService schemaService;

    @Autowired
    private MysqlValidatedService validatedService;

    @Autowired
    private TableColumnService columnService;

    @Autowired
    private TagService tagService;

    @Autowired
    private MySqlDDLService mySqlDDLService;

    @Autowired
    private MysqlSnapshotService mysqlSnapshotService;

    @Autowired
    private GraphSyncService graphSyncService;

    @Autowired
    private MetadataMapper metadataMapper;

    @Autowired
    private TableColumnMapper tableColumnMapper;

    @Autowired
    private TableFkMysqlMapper tableFkMysqlMapper;

    @Autowired
    private TableIdxMysqlMapper tableIdxMysqlMapper;

    @Override
    public PageResultBean<TableVO> search(MetadataSearchVo searchVO) {
        Preconditions.checkNotNull(searchVO, "请求参数为空");
        /*if (searchVO.getRenterId() == null) {
            searchVO.setRenterId(UserUtils.getRenterId());
        }*/
        // 分页
        PageHelper.startPage(searchVO.getPageNum(), searchVO.getPageSize());
        List<Metadata> list = metadataMapper.search(searchVO);
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

    @Override
    public MySqlTableVO searchById(Long tableId) {
        Metadata table = metadataMapper.selectByPrimaryKey(tableId); // 表基本信息
        List<TableColumn> columnList = tableColumnMapper.findTableColumnListByTableId(table.getId()); // 表字段信息
        List<TableIdxMysql> tableIndexList = tableIdxMysqlMapper.findIndexListByTableId(table.getId()); // 表索引信息
        List<TableFkMysql> tableFkMysqlList = tableFkMysqlMapper.findListByTableId(table.getId()); // 表外键信息
        // 外键参数补充
        foreignKeyReplenish(tableFkMysqlList);
        // 将数据封装到对象中
        MySqlTableVO result = new MySqlTableVO(table, columnList, tableIndexList, tableFkMysqlList);
        return result;
    }

    @Override
    public List<TableVO> searchBySchemaId(Long schemaId) {
        MetadataSearchVo searchVO = new MetadataSearchVo();
        searchVO.setDatabaseType(DatabaseTypeEnum.MYSQL.getCode()); // 当前为mysql
        searchVO.setResourceType(1); // 1 为表
        searchVO.setSchemaId(schemaId); // 模式id
        List<Metadata> list = metadataMapper.searchList(searchVO);
        // 遍历封装成需要的对象
        List<TableVO> result = new ArrayList<>();
        for (Metadata metadata : list) {
            TableVO vo = new TableVO();
            BeanUtils.copyProperties(metadata, vo);
            result.add(vo);
        }
        return result;
    }

    @Override
    public void add(MySqlTableVO mysqlTable) {
        // 修改当前表为生效状态
        mysqlTable.setStatus(DataStatusEnum.VALID.getValue());
        // 新增
        addTable(mysqlTable);
        // 生效到实体表，如果生效失败，抛出异常给前台，如果成功，生成版本信息
        generateOrUpdateEntityTable(mysqlTable.getId());
        // 表同步到数据地图
        graphSyncService.graphSaveTableNode(mysqlTable.getId());
    }

    @Override
    public void addTable(MySqlTableVO mysqlTable) {
        /**
         * 1.校验表参数
         * 2.将表信息新增到平台库中
         * 3.将表实际创建到客户数据源中
         */
        // 补全参数
        mysqlTable.setDatabaseType(DatabaseTypeEnum.MYSQL.getCode()); // mysql类型
        mysqlTable.setResourceType(1); // 资源是表
        mysqlTable.setVersion(1); // 版本号
        Long renterId = UserUtils.getRenterId(); // 当前租户id
        String creator = UserUtils.getUserName();// 当前创建人
        Date createTime = new Date();// 当前创建时间
        mysqlTable.setRenterId(renterId);
        mysqlTable.setCreator(creator);
        mysqlTable.setCreateTime(createTime);
        mysqlTable.setModifier(creator);
        mysqlTable.setModifyTime(createTime);
        if (mysqlTable.getIsGather() == null) {
            mysqlTable.setIsGather(false); // 非直采数据
        }

        // 校验表的基本信息
        validatedService.validatedTableBaseInfo(mysqlTable);

        // table insert
        Metadata table = new Metadata();
        BeanUtils.copyProperties(mysqlTable, table);
        metadataMapper.insertSelective(table);
        mysqlTable.setId(table.getId());

        Long tableId = table.getId();

        // 新增字段
        insertTableColumn(table, mysqlTable.getTableColumnList(), creator, createTime);

        // 新增索引
        insertTableIndex(mysqlTable.getTableIndexList(), mysqlTable.getTableColumnList(), tableId, creator, createTime);

        // 新增外键
        insertTableForeignKey(mysqlTable.getTableFkMysqlList(), mysqlTable.getTableColumnList(), mysqlTable.getTableIndexList(), tableId, creator, createTime);

        // 生成标签
        tagService.insertTags(table.getTags(), renterId, creator, createTime);

        // 主题使用次数递增
        themeService.increaseProgressively(mysqlTable.getThemeId());
    }

    // ====================================
    // ====================================
    //                insert
    // ====================================
    // ====================================

    // insert table column
    private void insertTableColumn(Metadata table, List<TableColumn> tableColumnList, String creator, Date createTime) {
        // 校验表字段
        validatedService.validatedTableColumn(tableColumnList, false);
        // 遍历去新增
        columnService.insertColumnList(tableColumnList, table.getId(), creator, createTime);
    }

    private void insertColumn(List<TableColumn> tableColumnList, Long tableId, String creator, Date createTime) {
        int maxLocation = tableColumnMapper.selectMaxLocationByTableId(tableId);
        for (TableColumn column : tableColumnList) {
            // 补全参数
            column.setTableId(tableId);
            column.setCreator(creator);
            column.setCreateTime(createTime);
            column.setModifier(creator);
            column.setModifyTime(createTime);
            column.setIsDeleted(false);
            column.setLocation(++maxLocation);
            // insert table column
            tableColumnMapper.insertSelective(column);
        }
    }

    // insert table index
    private void insertTableIndex(List<TableIdxMysql> tableIndexList, List<TableColumn> tableColumnList, Long tableId, String creator, Date createTime) {
        if (CollectionUtils.isEmpty(tableIndexList)) {
            return;
        }
        // 校验索引
        validatedService.validatedTableIndex(tableIndexList, tableColumnList, false);

        // 新增
        insertIndex(tableIndexList, tableId, creator, createTime);
    }

    public void insertIndex(List<TableIdxMysql> tableIndexList, Long tableId, String creator, Date createTime) {
        int maxLocation = tableIdxMysqlMapper.selectMaxLocationByTableId(tableId);
        for (TableIdxMysql index : tableIndexList) {
            index.setTableId(tableId); // 表id
            index.setCreator(creator);
            index.setCreateTime(createTime);
            index.setModifier(creator);
            index.setModifyTime(createTime);
            index.setIsDeleted(false);
            index.setLocation(++maxLocation);
            // insert table index
            tableIdxMysqlMapper.insertSelective(index);
        }
    }

    // insert table ForeignKey
    private void insertTableForeignKey(List<TableFkMysql> tableFkMysqlList, List<TableColumn> tableColumnList, List<TableIdxMysql> tableIndexList, Long tableId, String creator, Date createTime) {
        if (CollectionUtils.isEmpty(tableFkMysqlList)) {
            return;
        }

        // 校验外键
        validatedService.validatedTableForeignKey(tableFkMysqlList, tableColumnList, tableIndexList, tableId, creator, createTime, false);

        // insert
        insertForeignKey(tableFkMysqlList, tableId, creator, createTime);
    }

    public void insertForeignKey(List<TableFkMysql> tableFkMysqlList, Long tableId, String creator, Date createTime) {
        int maxLocation = tableFkMysqlMapper.selectMaxLocationByTableId(tableId);
        // 遍历外键，尝试去insert
        for (TableFkMysql foreignKey : tableFkMysqlList) {
            // 补全参数
            foreignKey.setTableId(tableId);
            foreignKey.setCreator(creator);
            foreignKey.setCreateTime(createTime);
            foreignKey.setModifier(creator);
            foreignKey.setModifyTime(createTime);
            foreignKey.setIsDeleted(false);
            foreignKey.setLocation(++maxLocation);
            // insert table ForeignKey
            tableFkMysqlMapper.insertSelective(foreignKey);
        }
    }

    @Override
    public void update(MySqlTableVO mysqlTable) {// 修改表基本信息
        // 修改当前表为生效状态
        mysqlTable.setStatus(DataStatusEnum.VALID.getValue());
        // 修改表信息
        updateTable(mysqlTable);
        // 修改生效到实体表，如果修改生效失败，抛出异常给前台，如果成功，生成版本信息
        generateOrUpdateEntityTable(mysqlTable.getId());
    }

    public void updateTable(MySqlTableVO mysqlTable) {
        // 参数补齐
        Metadata metadata = metadataMapper.findById(mysqlTable.getId());
        if (metadata.getStatus().equals(DataStatusEnum.VALID.getValue())) {
            mysqlTable.setVersion(metadata.getVersion() + 1); // 如果之前不是草稿，修改即版本号加1
        }
        Metadata table = new Metadata();
        BeanUtils.copyProperties(mysqlTable, table);
        String modifier = UserUtils.getUserName();
        Date modifyTime = new Date();
        table.setModifier(modifier);
        table.setModifyTime(modifyTime);

        // 校验表的基本信息
        validatedService.validatedTableBaseInfo(mysqlTable);

        // 修改表基本信息
        metadataMapper.updateByPrimaryKeySelective(table);

        // 修改表字段
        updateTableColumn(table, mysqlTable.getTableColumnList(), modifier, modifyTime);

        // 修改索引
        updateTableIndex(table, mysqlTable.getTableIndexList(), modifier, modifyTime);

        // 修改外键
        updateTableForeignKey(table, mysqlTable.getTableFkMysqlList(), modifier, modifyTime);

        // 生成标签
        tagService.insertTags(table.getTags(), UserUtils.getRenterId(), modifier, modifyTime);

        // 主题使用次数修改
        if (!metadata.getThemeId().equals(mysqlTable.getThemeId())) {
            // 先递减
            themeService.decreaseProgressively(metadata.getThemeId());
            // 再递增
            themeService.increaseProgressively(mysqlTable.getThemeId());
        }
    }

    private void updateTableColumn(Metadata table, List<TableColumn> tableColumnList, String modifier, Date modifyTime) {
        Long tableId = table.getId();
        if (tableColumnList == null) {
            // 当前字段没有任何修改，直接返回
            return;
        }
        // 当前数据库中所有的字段信息
        List<TableColumn> allTableColumn = tableColumnMapper.findTableColumnListByTableId(tableId);
        // 先删再增，获取出当前表最新的字段列表
        allTableColumn.removeIf(property -> tableColumnList.stream().map(prop -> prop.getId()).collect(Collectors.toList()).contains(property.getId()));
        allTableColumn.addAll(tableColumnList);

        // 校验表字段
        validatedService.validatedTableColumn(allTableColumn, true);

        // 要新增的字段
        List<TableColumn> addList = new ArrayList<>();

        // 根据状态分别操作动作
        for (TableColumn tableColumn : tableColumnList) {
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
        // 遍历去新增
        columnService.insertColumnList(addList, tableId, modifier, modifyTime);
    }

    private void updateTableIndex(Metadata table, List<TableIdxMysql> tableIndexList, String modifier, Date modifyTime) {
        Long tableId = table.getId();
        /**
         * 情况：
         * 1.字段被删除索引自动删除（一个字段对应一个索引，直接删除索引）
         * 2.字段修改类型后，索引当前类型是否还支持(主要考虑 FULLTEXT 类型索引)
         */
        // 当前表所有的索引
        List<TableIdxMysql> allIndexList = tableIdxMysqlMapper.findIndexListByTableId(tableId);
        if (allIndexList == null) {
            allIndexList = new ArrayList<>();
        }
        if (tableIndexList != null) { // 不等于空，那么表示有修改
            // 先删再增，获取当前表最新的索引
            allIndexList.removeIf(property -> tableIndexList.stream().map(prop -> prop.getId()).collect(Collectors.toList()).contains(property.getId()));
            allIndexList.addAll(tableIndexList);
        }

        if (CollectionUtils.isEmpty(allIndexList)) {
            return;
        }

        // 当前表最新的所有的字段
        List<TableColumn> tableColumnList = tableColumnMapper.findTableColumnListByTableId(tableId);

        // 校验索引，就算索引没有被修改，也需要检查，不确定字段是否被修改了。
        validatedService.validatedTableIndex(allIndexList, tableColumnList, true);

        if (tableIndexList != null) {
            List<TableIdxMysql> addList = new ArrayList<>(); // 要新增的列表
            for (TableIdxMysql index : tableIndexList) {
                if (index.getStatus() == 1) { // add，新增索引，和 新增逻辑一致
                    addList.add(index);
                } else if (index.getStatus() == 2) { // update
                    index.setModifier(modifier);
                    index.setModifyTime(modifyTime);
                    tableIdxMysqlMapper.updateByPrimaryKeySelective(index);
                } else if (index.getStatus() == 3) { // delete，直接删除，不考虑情况
                    tableIdxMysqlMapper.delete(index.getId());
                }
            }
            // insert
            insertIndex(addList, tableId, modifier, modifyTime);
        }
    }

    private void updateTableForeignKey(Metadata table, List<TableFkMysql> tableFkMysqlList, String modifier, Date modifyTime) {
        Long tableId = table.getId();
        // 当前数据库中所有的外键数据
        List<TableFkMysql> allForeignKey = tableFkMysqlMapper.findListByTableId(tableId);
        if (allForeignKey == null) {
            allForeignKey = new ArrayList<>();
        }
        if (tableFkMysqlList != null) {// 不等于空，那么表示有修改
            // 先删再增，获取出当前表最新的外键数据
            allForeignKey.removeIf(property -> tableFkMysqlList.stream().map(prop -> prop.getId()).collect(Collectors.toList()).contains(property.getId()));
            allForeignKey.addAll(tableFkMysqlList);
        }

        if (CollectionUtils.isEmpty(allForeignKey)) {
            return;
        }

        // 当前表最新的所有的字段
        List<TableColumn> columnList = tableColumnMapper.findTableColumnListByTableId(tableId);

        // 当前表最新的所有索引
        List<TableIdxMysql> indexList = tableIdxMysqlMapper.findIndexListByTableId(tableId);

        // 校验外键，外键没有被修改，也需要检查，不确定相关字段是否修改
        validatedService.validatedTableForeignKey(allForeignKey, columnList, indexList, tableId, modifier, modifyTime, true);

        if (tableFkMysqlList != null) {
            List<TableFkMysql> addList = new ArrayList<>(); // 新增列表
            for (TableFkMysql foreignKey : tableFkMysqlList) {
                if (foreignKey.getStatus() == 1) { // add
                    addList.add(foreignKey);
                } else if (foreignKey.getStatus() == 2) { // update
                    foreignKey.setModifier(modifier);
                    foreignKey.setModifyTime(modifyTime);
                    tableFkMysqlMapper.updateByPrimaryKeySelective(foreignKey);
                } else if (foreignKey.getStatus() == 3) { // delete
                    tableFkMysqlMapper.delete(foreignKey.getId());
                }
            }
            // insert
            insertForeignKey(addList, tableId, modifier, modifyTime);
        }
    }

    @Override
    public void addDraft(MySqlTableVO mysqlTable) {
        // 将数据标记为草稿，但该校验的逻辑都还需要校验，不去做生效动作和版本
        mysqlTable.setStatus(DataStatusEnum.DRAFT.getValue());
        // 新增进元数据数据库中
        addTable(mysqlTable);
    }

    @Override
    public void updateDraft(MySqlTableVO mysqlTable) {
        // 标识当前为草稿
        mysqlTable.setStatus(DataStatusEnum.DRAFT.getValue());
        // 修改
        updateTable(mysqlTable);
    }

    @Override
    public void delete(List<Long> idList) {
        if (CollectionUtils.isEmpty(idList)) {
            throw new MetaDataException("要删除的数据不能为空");
        }
        // 需要实际去删除实体表的数据
        List<String> removeTableNames = new ArrayList<>();
        // 保存下某个表信息
        Metadata tableCopy = null;
        for (Long tableId : idList) {
            Metadata table = metadataMapper.findById(tableId);
            // 删除表或草稿表
            metadataMapper.delete(tableId);
            // 删除表关联的字段
            tableColumnMapper.deleteByTableId(tableId);
            // 删除表关联的索引
            tableIdxMysqlMapper.deleteByTableId(tableId);
            // 删除表关联的外键
            tableFkMysqlMapper.deleteByTableId(tableId);
            // 使用主题递减
            themeService.decreaseProgressively(table.getThemeId());
            // 如果当前为草稿，删除就此结束
            if (table.getStatus() == DataStatusEnum.DRAFT.getValue()) {
                continue;
            }
            // 删除同步到数据地图
            graphSyncService.graphDeleteTableNode(tableId);
            // 如果为直采
            if (table.getIsGather()) {
                continue;
            }
            if (tableCopy == null) {
                tableCopy = table;
            }
            // 如果为表，是否需要删除实体表
            removeTableNames.add(table.getName());
        }
        // 获取删除语句
        List<String> list = mySqlDDLService.getDeleteTableSql(removeTableNames);
        // 删除生效到数据库中
        mySqlDDLService.goToDatabase(tableCopy, list);
    }

    @Override
    public void generateOrUpdateEntityTable(Long tableId) {
        /**
         * 获取执行的sql
         * 调用db proxy 执行sql
         * 执行sql成功后，生成快照
         */
        // TODO 如果生效失败，抛出生效失败的详细情况，并且由前台抛出是否将当前数据转换成草稿，前台根据code来做处理，如果是普通逻辑失败返回500，如果是生效失败返回501

        // 当前表最新数据
        Metadata table = metadataMapper.selectByPrimaryKey(tableId); // 表基本信息
        List<TableColumn> columnList = tableColumnMapper.findTableColumnListByTableId(table.getId()); // 表字段信息
        List<TableIdxMysql> tableIndexList = tableIdxMysqlMapper.findIndexListByTableId(table.getId()); // 表索引信息
        List<TableFkMysql> tableFkMysqlList = tableFkMysqlMapper.findListByTableId(table.getId()); // 表外键信息

        ArrayList<String> commands = null;
        String details = "初始化表";

        if (table.getVersion() <= 1) { // 新建
            // 获取create table sql
            commands = mySqlDDLService.getCreateTableSql(table, columnList, tableIndexList, tableFkMysqlList);
            log.info("createTableSql：{}", commands);
        } else { // 修改
            // 将最新数据封装到对象中
            MySqlTableVO newTable = new MySqlTableVO(table, columnList, tableIndexList, tableFkMysqlList);

            // 获取旧版本的表所有信息
            Integer version = table.getVersion() - 1;
            Metadata snapshotTableInfo = mysqlSnapshotService.getSnapshotTableInfoByTableId(tableId, version); // 旧版本表基本信息
            List<TableColumn> snapshotColumnList = mysqlSnapshotService.getSnapshotColumnListByTableId(tableId, version); // 旧版本表字段信息
            List<TableIdxMysql> snapshotIndexList = mysqlSnapshotService.getSnapshotIndexListByTableId(tableId, version); // 旧版本表索引信息
            List<TableFkMysql> snapshotForeignKeyList = mysqlSnapshotService.getSnapshotForeignKeyListByTableId(tableId, version); // 旧版本表外键信息
            MySqlTableVO snapshotTable = new MySqlTableVO(snapshotTableInfo, snapshotColumnList, snapshotIndexList, snapshotForeignKeyList);

            // 获取alter table sql
            AlterSqlVO alterTableSql = mySqlDDLService.getAlterTableSql(newTable, snapshotTable);
            List<String> changeSql = alterTableSql.getChangeSql();
            commands = new ArrayList<>();
            commands.addAll(changeSql);
            log.info("alterTableSql：{}", commands);
            details = alterTableSql.getMessage();
        }

        // 生效到数据库中
        mySqlDDLService.goToDatabase(table, commands);

        // 生成快照版本
        mysqlSnapshotService.generateCreateTableSnapshot(table, columnList, tableIndexList, tableFkMysqlList, details);
    }

    private void foreignKeyReplenish(List<TableFkMysql> foreignKeyList) {
        if (CollectionUtils.isEmpty(foreignKeyList)) {
            return;
        }
        // 当前参考模式只能是当前模式
        McSchemaPO schema = schemaService.findById(foreignKeyList.get(0).getReferenceSchemaId());
        for (TableFkMysql foreignKey : foreignKeyList) {
            // 模式名
            foreignKey.setReferenceSchemaName(schema.getName());
            // 表名
            Metadata referenceMetadata = metadataMapper.selectByPrimaryKey(foreignKey.getReferenceTableId());
            foreignKey.setReferenceTableName(referenceMetadata.getName());
            // 字段名
            String[] referenceColumnIdArr = foreignKey.getReferenceColumn().split(",");
            List<TableColumn> referenceColumnList = columnService.getTableColumnListByIdList(Arrays.asList(referenceColumnIdArr));
            List<String> columnNames = new ArrayList<>();
            referenceColumnList.forEach(value -> {
                columnNames.add(value.getColumnName());
            });
            foreignKey.setReferenceColumnNames(String.join(",", columnNames));
        }
    }

}