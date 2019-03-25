package com.ys.idatrix.db.service.internal;

import com.google.common.collect.Lists;
import com.ys.idatrix.db.api.rdb.dto.*;
import com.ys.idatrix.db.dto.RdbColumnChanges;
import com.ys.idatrix.db.dto.RdbColumnCopy;
import com.ys.idatrix.db.exception.DbProxyException;
import com.ys.idatrix.db.util.CloneUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.text.MessageFormat;
import java.util.*;

/**
 * @ClassName: RdbDDLWrapper
 * IDBDAO的适配包装处理类：实现通用方法、实现子类无需实现接口的方法、定义抽象方法
 * @Description:
 * @Author: ZhouJian
 * @Date: 2017/12/22
 */
@Slf4j
public abstract class RdbDDLWrapper implements IRdbDDL {

    /**
     * 分隔符
     */
    protected final String SEPARATOR = ",";

    /**
     * 字符单引号
     */
    protected final String QUOTATION = "'";


    /************************************ 适配：适配包装方法 ************************************/
    /**
     * Oracle、DM7 公用
     *
     * @param rct
     * @return
     * @throws Exception
     */
    @Override
    public List<String> getCreateTableCommands(RdbCreateTable rct) {
        // 取出所有列
        ArrayList<RdbColumn> rdbColumns = rct.getRdbColumns();
        if (CollectionUtils.isEmpty(rdbColumns)) {
            throw new DbProxyException("create table, columns is null");
        }

        //表全名称。dm = schema.tableName
        String fullTableName = rct.getFullTableName();

        // 用来存放 主键SQL
        StringBuilder sbPK = null;

        // 取出主键
        RdbPrimaryKey primaryKey = rct.getPrimaryKey();
        if (null != primaryKey) {

            // 主键名字
            String primaryKeyName = primaryKey.getPrimaryKeyName();
            // 主键的集合
            List<RdbColumn> primaryKeys = primaryKey.getPrimaryKeys();

            // 有主键为 oracle/dm7 时
            if (CollectionUtils.isEmpty(primaryKeys) || StringUtils.isBlank(primaryKeyName)) {
                throw new DbProxyException("create table, primaryKeys is empty or primaryKeyName is null");
            }

            // 用来存放primary key
            sbPK = new StringBuilder();
            sbPK.append(" CONSTRAINT ").append(primaryKeyName).append("  PRIMARY KEY( ");

            for (RdbColumn pks : primaryKey.getPrimaryKeys()) {
                sbPK.append(pks.getColumnName()).append(SEPARATOR);
            }

            // 删除最后一个逗号
            if (sbPK.lastIndexOf(SEPARATOR) > -1) {
                sbPK.deleteCharAt(sbPK.lastIndexOf(SEPARATOR));
            }
            sbPK.append(")");
        }

        // 建表 SQL
        StringBuilder sb = new StringBuilder("CREATE TABLE ");

        sb.append(fullTableName).append(" ( ");

        // 用来存放 oracle/dm7 的字段注释,为保证执行的顺序 建表语句在注释语句之前执行
        ArrayList<String> columnComments = new ArrayList<>();

        for (RdbColumn RdbColumn : rdbColumns) {

            // 列名
            String columnName = RdbColumn.getColumnName();
            // 类型
            String dataType = RdbColumn.getDataType();
            // 类型范围
            String columnChamp = RdbColumn.getColumnChamp();
            // 注释
            String comment = RdbColumn.getComment();
            // 默认值
            String defaultValue = RdbColumn.getDefaultValue();

            // 判断列名是否为空
            if (StringUtils.isBlank(columnName)) {
                throw new DbProxyException("create table, columnName is null or \" \"");
            }

            sb.append(columnName + " ").append(getAndVerifiedDataType(dataType) + " ");


            // 是否有类型范围限制,有就加上,没有就不加
            if (StringUtils.isNotBlank(columnChamp)) {
                sb.append("(" + columnChamp + ") ");
            }

            // 判断是否为能为空
            if (RdbColumn.isHasNotNull()) {
                sb.append("NOT NULL ");
            }

            // 是否有默认值
            if (StringUtils.isNotEmpty(defaultValue)) {
                sb.append("DEFAULT '" + defaultValue + "' ");
            }

            // oracle/dm7 字段注释
            // 是否有注释
            if (StringUtils.isNotBlank(comment)) {
                // 添加字段注释
                // e.g: comment on column t2.age is '年龄';
                StringBuilder sComment = new StringBuilder("COMMENT ON COLUMN ");
                sComment.append(fullTableName)
                        .append(".")
                        .append(columnName)
                        .append(" IS '")
                        .append(comment)
                        .append("'");
                columnComments.add(sComment.toString());
            }

            sb.append(SEPARATOR);
        }

        //拼装主键
        assembledPK(sb, sbPK);

        // 执行的命令集合
        List<String> commands = new ArrayList<>();

        commands.add(sb.toString());

        // oracle/dm7 的表注释特殊处理
        if (StringUtils.isNotBlank(rct.getComment())) {
            StringBuilder sComment = new StringBuilder("COMMENT ON TABLE ");
            // e.g: COMMENT ON table t2 IS '个人信息';
            sComment.append(fullTableName)
                    .append(" IS '")
                    .append(rct.getComment())
                    .append("' ");
            commands.add(sComment.toString());
        }

        // 如果是 oracle/dm7 数据库 且字段有注释,单独执行SQL处理
        if (CollectionUtils.isNotEmpty(columnComments)) {
            commands.addAll(columnComments);
        }

        // 创建索引sql
        List<String> createIndexCommands = getCreateIndexCommands(fullTableName, rct.getIndices(), rdbColumns);
        if (CollectionUtils.isNotEmpty(createIndexCommands)) {
            commands.addAll(createIndexCommands);
        }

        return commands;
    }


    /**
     * 获取修改表语句
     * 所有数据源通用
     *
     * @param alterTable
     * @return
     * @throws Exception
     */
    @Override
    public List<String> getAlterTableCommands(RdbAlterTable alterTable) {
        List<String> allCommands = new ArrayList<>();

        //持久一份修改表的 新字段集合，修改索引用到。
        ArrayList<RdbColumn> persistColumns = CloneUtils.deepCopy(alterTable.getNewVersionColumns());

        //表全名称。dm = schema.tableName
        String fullTableName = alterTable.getFullTableName();

        //alter - 字段
        if (CollectionUtils.isNotEmpty(alterTable.getNewVersionColumns()) && CollectionUtils.isNotEmpty(alterTable.getRdbColumns())) {
            List<String> alterColumnCommands = getAlterColumnCommands(fullTableName, alterTable.getNewVersionColumns(), alterTable.getRdbColumns());
            if (CollectionUtils.isNotEmpty(alterColumnCommands)) {
                allCommands.addAll(alterColumnCommands);
            } else {
                log.warn("alter table, columns not change anything");
            }
        }

        //alter - 主键
        if (null != alterTable.getNewPrimaryKey() || null != alterTable.getPrimaryKey()) {
            List<String> alterPrimaryCommands = getAlterPrimaryKeyCommands(fullTableName, alterTable.getNewPrimaryKey(), alterTable.getPrimaryKey());
            if (CollectionUtils.isNotEmpty(alterPrimaryCommands)) {
                allCommands.addAll(alterPrimaryCommands);
            } else {
                log.warn("alter table, primary key not change anything");
            }
        }

        //alter - 索引
        if (CollectionUtils.isNotEmpty(alterTable.getNewVersionIndices()) && CollectionUtils.isNotEmpty(alterTable.getIndices())) {
            List<String> alterIndexCommands = getAlterIndexCommands(fullTableName, persistColumns,
                    alterTable.getNewVersionIndices(), alterTable.getIndices());
            if (CollectionUtils.isNotEmpty(alterIndexCommands)) {
                allCommands.addAll(alterIndexCommands);
            } else {
                log.warn("alter table, indices not change anything");
            }
        }

        // alter - 表注释
        if (StringUtils.isNotBlank(alterTable.getComment())) {
            String alterCommentSql = getAlterTableCommentSql();
            if (RdbEnum.DBType.MYSQL.name().equals(getDBType().name())) {
                fullTableName = addBackQuote(fullTableName);
            }
            allCommands.add(MessageFormat.format(alterCommentSql, fullTableName, QUOTATION, alterTable.getComment(), QUOTATION));
        }
        return allCommands;
    }


    /**
     * 获取修改主键sql语句
     *
     * @param tableName     --表全名
     * @param newPrimaryKey
     * @param oldPrimaryKey
     * @return
     * @throws Exception
     */
    private List<String> getAlterPrimaryKeyCommands(String tableName, RdbPrimaryKey newPrimaryKey, RdbPrimaryKey oldPrimaryKey) {
        if (StringUtils.isEmpty(tableName)) {
            throw new DbProxyException("alter Column, tableName is null");
        }

        if (null == newPrimaryKey && null == oldPrimaryKey) {
            throw new DbProxyException("alter Column, newPrimaryKey and oldPrimaryKey is null");
        }

        List<RdbColumn> oldPKColumns;
        if (null != oldPrimaryKey) {
            oldPKColumns = oldPrimaryKey.getPrimaryKeys();
        } else {
            oldPKColumns = new ArrayList<>();
        }

        List<RdbColumn> newPKColumns;
        if (null != newPrimaryKey) {
            newPKColumns = newPrimaryKey.getPrimaryKeys();
        } else {
            newPKColumns = new ArrayList<>();
        }

        if (CollectionUtils.isEmpty(oldPKColumns) && CollectionUtils.isEmpty(newPKColumns)) {
            throw new DbProxyException("alter Column, oldPKColumns and newPKColumns is null");
        }

        //比较新老版本主键字段,是否需要修改主键
        boolean hasPKAltered = false;
        if (newPKColumns.size() != oldPKColumns.size()) {
            hasPKAltered = true;
        } else {
            for (RdbColumn newPKColumn : newPKColumns) {
                if (!hasPKAltered && (CollectionUtils.isNotEmpty(oldPKColumns)
                        && oldPKColumns.stream().filter(oldPkColumn -> oldPkColumn.getColumnId().equalsIgnoreCase(newPKColumn.getColumnId())).count() == 0)) {
                    hasPKAltered = true;
                }
            }
        }

        List<String> commands = new ArrayList<>();
        if (hasPKAltered) {
            commands.addAll(getAlterPKCommands(tableName, oldPrimaryKey, newPrimaryKey));
        }

        return commands;
    }


    /**
     * 更改列操作生成的sql
     *
     * @param tableName         --表全名
     * @param newVersionColumns
     * @param oldVersionColumns
     * @return
     * @throws Exception
     */
    private List<String> getAlterColumnCommands(String tableName, ArrayList<RdbColumn> newVersionColumns, ArrayList<RdbColumn> oldVersionColumns) {
        if (StringUtils.isEmpty(tableName)) {
            throw new DbProxyException("alter Column, tableName is null");
        }
        if (CollectionUtils.isEmpty(newVersionColumns)) {
            throw new DbProxyException("alter Column, newVersionColumns is null");
        }
        if (CollectionUtils.isEmpty(oldVersionColumns)) {
            throw new DbProxyException("alter Column, oldVersionColumns is null");
        }

        // 取出column的version到新集合(column id 属性)
        // 旧表的列元数据的version集合
        ArrayList<String> oldColumnVersionList = getColumnVersionList(oldVersionColumns);
        // 新表的列元数据的version集合
        ArrayList<String> newColumnVersionList = getColumnVersionList(newVersionColumns);

        if (CollectionUtils.isEmpty(oldColumnVersionList)) {
            throw new DbProxyException("alter Column, column version is null");
        }

        // 复制旧表version
        ArrayList<String> oldCopy = Lists.newArrayList(oldColumnVersionList);

        // 复制新表version
        ArrayList<String> newCopy = Lists.newArrayList(newColumnVersionList);

        // oldCopy里面就为删除的column version
        oldCopy.removeAll(newCopy);

        // oldColumnVersionList里面就为 被修改或者未被修改的列
        oldColumnVersionList.removeAll(oldCopy);

        // newColumnVersionList 里面就为新增加的列
        newColumnVersionList.removeAll(oldColumnVersionList);

        // 删除的 列version
        ArrayList<String> beDeleteList = oldCopy;

        // 修改或者未改 列version 需要依次比较属性
        ArrayList<String> beModifyOrNorList = oldColumnVersionList;

        // 新增的 列version
        ArrayList<String> beAddList = newColumnVersionList;

        // 需要重写hashcode 和 equals
        // 返回被删除的列的bean集合
        ArrayList<RdbColumn> dropColumns = getAlterBeans(beDeleteList, oldVersionColumns);

        // 返回旧表修改或者未修改的bean集合
        ArrayList<RdbColumn> modifyOrNorOldColumns = getAlterBeans(beModifyOrNorList, oldVersionColumns);

        // 返回新增加的列的bean集合
        ArrayList<RdbColumn> addColumns = getAlterBeans(beAddList, newVersionColumns);

        // 返回新表的修改或者未修改的bean集合
        ArrayList<RdbColumn> modifyOrNorNewColumns = getAlterBeans(beModifyOrNorList, newVersionColumns);

        // 返回真正被修改的bean集合
        ArrayList<RdbColumnChanges> beModifyColumn = getBeModifyColumns(modifyOrNorOldColumns, modifyOrNorNewColumns);

        HashMap<String, RdbColumn> tempBc = new HashMap<>();
        // 存放所有的命令
        ArrayList<String> commands = new ArrayList<>();
        try {
            // 删除操作的commands 生成,放入list
            for (RdbColumn drop : dropColumns) {
                String dropColumnSql = dropColumn(tableName, drop);
                if (StringUtils.isNotBlank(dropColumnSql)) {
                    commands.add(dropColumnSql);
                }
            }

            // 增加操作的commands 生成,追加到list
            for (RdbColumn add : addColumns) {
                List<String> addColumnSql = addColumn(tableName, add);
                if (CollectionUtils.isNotEmpty(addColumnSql)) {
                    commands.addAll(addColumnSql);
                }
            }

            // 修改操作的commands 生成,追加到list
            for (RdbColumnChanges changeColumn : beModifyColumn) {
                if (changeColumn != null) {
                    int flag = changeColumn.getFlag();
                    RdbColumn newColumnBean = changeColumn.getNewColumnBean();
                    String oldColumnName = changeColumn.getOldColumnName();
                    if (null == newColumnBean) {
                        throw new Exception("alter Column,  column bean is null");
                    }

                    switch (flag) {
                        //修改列名
                        case 0:
                            String tempColumn = getNotRep(randomCreate(), tempBc);
                            // copy一个对象修改里面的列名位临时列名
                            RdbColumn deepCopyColumn = CloneUtils.deepCopy(newColumnBean);
                            deepCopyColumn.setColumnName(tempColumn);
                            tempBc.put(tempColumn, newColumnBean);

                            String renameColumnSql = renameColumn(tableName, oldColumnName, deepCopyColumn);

                            commands.add(renameColumnSql);
                            break;
                        //修改属性
                        case 1:
                            List<String> modifyColumnSQLs = modifyColumn(tableName, newColumnBean);
                            commands.addAll(modifyColumnSQLs);
                            break;
                        //修改列名+属性
                        case 2:
                            //这里先添加修改列名的修改 然后进行属性修改
                            // 获取临时的列名
                            tempColumn = getNotRep(randomCreate(), tempBc);

                            // 先改列名
                            // copy一个对象修改里面的列名位临时列名
                            RdbColumn tempColumnName = CloneUtils.deepCopy(newColumnBean);
                            tempColumnName.setColumnName(tempColumn);
                            tempBc.put(tempColumn, newColumnBean);

                            renameColumnSql = renameColumn(tableName, oldColumnName, tempColumnName);

                            commands.add(renameColumnSql);

                            // 克隆 设置随机的列名字
                            deepCopyColumn = CloneUtils.deepCopy(newColumnBean);
                            deepCopyColumn.setColumnName(tempColumn);
                            // 再改属性
                            modifyColumnSQLs = modifyColumn(tableName, deepCopyColumn);
                            commands.addAll(modifyColumnSQLs);
                            break;
                        default:
                            break;
                    }
                }
            }

            // 最后把名字改回最终的
            Set<Map.Entry<String, RdbColumn>> entrySet = tempBc.entrySet();
            if (CollectionUtils.isNotEmpty(entrySet)) {
                for (Map.Entry<String, RdbColumn> entry : entrySet) {
                    RdbColumn rc = entry.getValue();
                    String renameColumnSql = getRenameColumnCommand(tableName, entry.getKey(), rc);
                    commands.add(renameColumnSql);
                }
            }
        } catch (Exception e) {
            throw new DbProxyException(e.getMessage());
        }

        return commands;
    }


    /**
     * 获取每一列的版本号到集合中
     *
     * @param rcs
     * @return
     */
    private ArrayList<String> getColumnVersionList(List<RdbColumn> rcs) {
        ArrayList<String> reList = new ArrayList<>();
        for (RdbColumn RdbColumn : rcs) {
            if (null != RdbColumn && StringUtils.isNotBlank(RdbColumn.getColumnId())) {
                reList.add(RdbColumn.getColumnId());
            }
        }
        return reList;
    }


    /**
     * alter column的时候根据column version 返回column bean集合
     *
     * @param strList
     * @param rcsList
     * @return
     */
    private ArrayList<RdbColumn> getAlterBeans(ArrayList<String> strList, ArrayList<RdbColumn> rcsList) {
        ArrayList<RdbColumn> reList = new ArrayList<>();
        for (RdbColumn RdbColumn : rcsList) {
            String versionId = RdbColumn.getColumnId();
            if (StringUtils.isEmpty(versionId)) {
                log.info("versionId is null");
                return null;
            }
            for (String str : strList) {
                if (versionId.equalsIgnoreCase(str)) {
                    reList.add(RdbColumn);
                }
            }
        }

        return reList;
    }


    /**
     * 旧版本和新版本的对比,返回真正被修改的列bean集合
     *
     * @param oldRcs
     * @param newRcs
     * @return
     */
    private ArrayList<RdbColumnChanges> getBeModifyColumns(ArrayList<RdbColumn> oldRcs, ArrayList<RdbColumn> newRcs) {
        // -- 保留一份不变的 oldPersist newPersist
        ArrayList<RdbColumn> oldPersist = CloneUtils.deepCopy(oldRcs);
        ArrayList<RdbColumn> newPersist = CloneUtils.deepCopy(newRcs);

        ArrayList<RdbColumn> oldCopy = Lists.newArrayList(oldRcs);
        ArrayList<RdbColumn> newCopy = Lists.newArrayList(newRcs);

        // oldCopy 里面是没有被修改的列的bean
        oldCopy.retainAll(newCopy);

        // oldRcs 里面装的是旧版本被修改的列bean
        oldRcs.removeAll(oldCopy);

        // newRcs 里面装的是新版本被修改的列bean
        newRcs.removeAll(oldCopy);

        // 用来存放 copy的bean 两个求交集能得到只被修改属性的列
        ArrayList<RdbColumnCopy> rccsOld = new ArrayList<>();
        ArrayList<RdbColumnCopy> rccsNew = new ArrayList<>();

        // 修改了列名,修改了属性,修改了列名和属性的分支 分别对应flag 0,1,2
        for (RdbColumn rOld : oldRcs) {
            rccsOld.add(new RdbColumnCopy(rOld.getColumnId(), rOld.getColumnName()));
        }

        for (RdbColumn rNew : newRcs) {
            rccsNew.add(new RdbColumnCopy(rNew.getColumnId(), rNew.getColumnName()));
        }

        // 存放的是只被修改属性的列
        rccsOld.retainAll(rccsNew);

        // copy 列bean 旧表被修改的
        ArrayList<RdbColumn> oldModifyCopy = Lists.newArrayList(oldRcs);
        // copy 列bean 新表被修改的
        ArrayList<RdbColumn> newModifyCopy = Lists.newArrayList(newRcs);

        // remove掉旧版本的只修改属性的 留下只修改列或者既修改列又修改属性的
        for (RdbColumn os : oldRcs) {
            for (RdbColumnCopy ro : rccsOld) {
                if (os.getColumnId().equals(ro.getColumnId())) {
                    oldModifyCopy.remove(os);
                }
            }
        }

        // remove掉新版本的只修改属性的 留下只修改列或者既修改列又修改属性的
        for (RdbColumn os : newRcs) {
            for (RdbColumnCopy ro : rccsOld) {
                if (os.getColumnId().equals(ro.getColumnId())) {
                    newModifyCopy.remove(os);
                }
            }
        }

        // 用来存放 列的bean 其中列名设置为空, 两个集合交集得到的是 只被 修改列名的情况
        ArrayList<RdbColumn> rcsOld = new ArrayList<>();
        ArrayList<RdbColumn> rcsNew = new ArrayList<>();

        for (RdbColumn rc : oldModifyCopy) {
            rc.setColumnName(null);
            rcsOld.add(rc);
        }

        for (RdbColumn rc : newModifyCopy) {
            rc.setColumnName(null);
            rcsNew.add(rc);
        }

        // rcsOld 只修改了列名
        rcsOld.retainAll(rcsNew);

        // rcsNew 修改了列名以及属性
        rcsNew.removeAll(rcsOld);

        // 只被修改属性的列
        ArrayList<RdbColumnCopy> rcs = rccsOld;

        // 返回的list声明
        ArrayList<RdbColumnChanges> reList = new ArrayList<>();

        for (RdbColumn orc : oldPersist) {

            // 只修改了列名
            for (RdbColumn r : rcsOld) {
                if (r.getColumnId().equals(orc.getColumnId())) {
                    for (RdbColumn np : newPersist) {
                        if (np.getColumnId().equals(orc.getColumnId())) {
                            RdbColumnChanges rmc = new RdbColumnChanges(orc.getColumnName(), np, 0);
                            reList.add(rmc);
                        }
                    }
                }
            }

            // 只被修改属性的列
            for (RdbColumnCopy r : rcs) {
                if (r.getColumnId().equals(orc.getColumnId())) {
                    for (RdbColumn np : newPersist) {
                        if (np.getColumnId().equals(orc.getColumnId())) {
                            RdbColumnChanges rmc = new RdbColumnChanges(orc.getColumnName(), np, 1);
                            reList.add(rmc);
                        }
                    }
                }
            }

            // 修改了列名以及属性
            for (RdbColumn r : rcsNew) {
                if (r.getColumnId().equals(orc.getColumnId())) {
                    for (RdbColumn np : newPersist) {
                        if (np.getColumnId().equals(orc.getColumnId())) {
                            RdbColumnChanges rmc = new RdbColumnChanges(orc.getColumnName(), np, 2);
                            reList.add(rmc);
                        }
                    }
                }
            }

        }

        return reList;
    }


    /**
     * 删除一列
     *
     * @param RdbColumn
     * @param tableName
     * @return
     * @throws Exception
     */
    private String dropColumn(String tableName, RdbColumn RdbColumn)
            throws Exception {
        // 判断列bean是否为空
        if (null == RdbColumn) {
            throw new Exception("drop Column, column bean is null");
        }
        // 列名
        String columnName = RdbColumn.getColumnName();

        // 判断列名是否为空
        if (StringUtils.isEmpty(columnName)) {
            throw new Exception("drop Column, columnName is null");
        }
        return getDropColumnCommand(tableName, columnName);

    }


    /**
     * 增加一列
     *
     * @param tableName
     * @param rc
     * @return
     * @throws Exception
     */
    private List<String> addColumn(String tableName, RdbColumn rc)
            throws Exception {
        // 判断列bean是否为空
        if (null == rc) {
            throw new Exception("add Column, RdbColumn is null");
        }
        return getAddColumnCommands(tableName, rc);
    }


    /**
     * 修改一列
     *
     * @param tableName
     * @param rc
     * @return
     * @throws Exception
     */
    private List<String> modifyColumn(String tableName, RdbColumn rc)
            throws Exception {
        // 判断列bean是否为空
        if (null == rc) {
            throw new Exception("modify Column, RdbColumn is null");
        }

        return getModifyColumnCommands(tableName, rc);

    }


    /**
     * 重命名列名
     *
     * @param tableName
     * @param oldColumnName
     * @param rc
     * @return
     * @throws Exception
     */
    private String renameColumn(String tableName, String oldColumnName, RdbColumn rc) throws Exception {
        // 判断列bean是否为空
        if (null == rc) {
            throw new Exception("rename Column, RdbColumn is null");
        }
        // 新列名
        String newColumnName = rc.getColumnName();

        if (StringUtils.isBlank(oldColumnName)) {
            throw new Exception("rename Column, oldColumnName is null");
        }
        if (StringUtils.isBlank(newColumnName)) {
            throw new Exception("rename Column, newColumnName is null");
        }

        return getRenameColumnCommand(tableName, oldColumnName, rc);
    }


    /**
     * 获取不重复的
     *
     * @param temp
     * @param map
     * @return
     */
    private String getNotRep(String temp, HashMap<String, RdbColumn> map) {
        if (map.containsKey(temp)) {
            temp = randomCreate();
            getNotRep(temp, map);
        }
        return temp;
    }


    /**
     * 随机生成一个临时column 名称
     *
     * @return
     */
    private String randomCreate() {
        String base = "abcdefghijk";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < base.length(); i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }


    /**
     * 更改索引生成的sql
     *
     * @param tableName         --表全名
     * @param columns
     * @param newVersionIndices
     * @param oldVersionIndices
     * @return
     * @throws Exception
     */
    private List<String> getAlterIndexCommands(String tableName, List<RdbColumn> columns,
                                               ArrayList<RdbIndex> newVersionIndices, ArrayList<RdbIndex> oldVersionIndices) {
        if (StringUtils.isBlank(tableName)) {
            throw new DbProxyException("alter Index, tableName is null");
        }
        if (CollectionUtils.isEmpty(newVersionIndices) && CollectionUtils.isEmpty(oldVersionIndices)) {
            log.warn("alter Index, no index changed, oldVersionIndices and  newVersionIndices is null ");
            return null;
        }

        if (CollectionUtils.isEmpty(columns)) {
            throw new DbProxyException("alter Index, columns is null");
        }

        //如果新或旧的索引集合其中一个为null,则主动一个空ArrayList,便于后续处理
        if (null == oldVersionIndices) {
            oldVersionIndices = new ArrayList<>();
        }
        if (null == newVersionIndices) {
            newVersionIndices = new ArrayList<>();
        }

        ArrayList<RdbIndex> invariantIndices = Lists.newArrayList(oldVersionIndices);

        ArrayList<RdbIndex> newCopy = Lists.newArrayList(newVersionIndices);

        //不变的索引bean集合 //求交集。自定义对象重写 hashcode 和 equal
        invariantIndices.retainAll(newCopy);

        //待删除的索引集合
        oldVersionIndices.removeAll(invariantIndices);

        //待新增的索引集合
        newVersionIndices.removeAll(invariantIndices);

        List<String> commands = new ArrayList<>();

        //先删除索引
        List<String> dropIndexCommands = getDropIndexCommands(tableName, oldVersionIndices);
        if (CollectionUtils.isNotEmpty(dropIndexCommands)) {
            commands.addAll(dropIndexCommands);
        }

        //再新建索引
        List<String> addIndexCommands = getCreateIndexCommands(tableName, newVersionIndices, columns, RdbEnum.MysqlEngineType.INNODB);
        if (CollectionUtils.isNotEmpty(addIndexCommands)) {
            commands.addAll(addIndexCommands);
        }
        return commands;

    }


    /************************************ 子类通用方法 ************************************/

    /**
     * mysql 添加反引号。区分特殊字符与数据库保留字段
     *
     * @param value
     * @return
     */
    protected String addBackQuote(String value) {
        if (StringUtils.isNotBlank(value)) {
            String valueArray[] = value.split("\\.");
            StringBuilder valueSb = new StringBuilder();
            for (String str : valueArray) {
                valueSb.append("`").append(str).append("`").append(".");
            }
            valueSb.deleteCharAt(valueSb.lastIndexOf("."));
            return valueSb.toString();
        }
        return value;
    }


    /**
     * 拼装主键
     *
     * @param mainStr
     * @param pkStr
     */
    protected void assembledPK(StringBuilder mainStr, StringBuilder pkStr) {
        //是否存在主键
        if (null == pkStr) {
            // 删除最后一个逗号
            if (mainStr.lastIndexOf(SEPARATOR) > -1) {
                mainStr.deleteCharAt(mainStr.lastIndexOf(SEPARATOR));
            }
            mainStr.append(" ) ");
        } else {
            mainStr.append(pkStr).append(" )");
        }
    }


    /**
     * 根据不同数据源类型验证并获取数据类型
     *
     * @param dataType
     * @return
     */
    protected String getAndVerifiedDataType(String dataType) {
        // 判断列的类型是否为空
        if (null == dataType) {
            throw new DbProxyException("column dataType is null");
        }

        try {
            String realDataType = null;
            RdbEnum.DBType dbType = getDBType();
            switch (dbType) {
                case MYSQL:
                    realDataType = RdbEnum.MysqlDataType.valueOf(dataType).getName();
                    break;
                case ORACLE:
                    if (RdbEnum.OracleDataType.TIMESTAMP_WITH_TIME_ZONE.getName().equalsIgnoreCase(dataType.trim())
                            || RdbEnum.OracleDataType.TIMESTAMP_WITH_LOCAL_TIME_ZONE.getName().equalsIgnoreCase(dataType.trim())
                            || RdbEnum.OracleDataType.INTERVAL_DAY_TO_SECOND.getName().equalsIgnoreCase(dataType.trim())
                            || RdbEnum.OracleDataType.INTERVAL_YEAR_TO_MONTH.getName().equalsIgnoreCase(dataType.trim())) {
                        return dataType;
                    }
                    realDataType = RdbEnum.OracleDataType.valueOf(dataType).getName();
                    break;
                case DM7:
                    if (RdbEnum.DM7DataType.INTERVAL_YEAR.getName().equalsIgnoreCase(dataType.trim())
                            || RdbEnum.DM7DataType.INTERVAL_MONTH.getName().equalsIgnoreCase(dataType.trim())
                            || RdbEnum.DM7DataType.INTERVAL_DAY.getName().equalsIgnoreCase(dataType.trim())
                            || RdbEnum.DM7DataType.INTERVAL_HOUR.getName().equalsIgnoreCase(dataType.trim())
                            || RdbEnum.DM7DataType.INTERVAL_MINUTE.getName().equalsIgnoreCase(dataType.trim())
                            || RdbEnum.DM7DataType.INTERVAL_SECOND.getName().equalsIgnoreCase(dataType.trim())
                            || RdbEnum.DM7DataType.INTERVAL_YEAR_TO_MONTH.getName().equalsIgnoreCase(dataType.trim())
                            || RdbEnum.DM7DataType.INTERVAL_DAY_TO_HOUR.getName().equalsIgnoreCase(dataType.trim())
                            || RdbEnum.DM7DataType.INTERVAL_DAY_TO_MINUTE.getName().equalsIgnoreCase(dataType.trim())
                            || RdbEnum.DM7DataType.INTERVAL_DAY_TO_SECOND.getName().equalsIgnoreCase(dataType.trim())
                            || RdbEnum.DM7DataType.INTERVAL_HOUR_TO_MINUTE.getName().equalsIgnoreCase(dataType.trim())
                            || RdbEnum.DM7DataType.INTERVAL_HOUR_TO_SECOND.getName().equalsIgnoreCase(dataType.trim())
                            || RdbEnum.DM7DataType.INTERVAL_MINUTE_TO_SECOND.getName().equalsIgnoreCase(dataType.trim())
                            || RdbEnum.DM7DataType.TIME_WITH_TIME_ZONE.getName().equalsIgnoreCase(dataType.trim())
                            || RdbEnum.DM7DataType.DATETIME_WITH_TIME_ZONE.getName().equalsIgnoreCase(dataType.trim())
                            || RdbEnum.DM7DataType.TIMESTAMP_WITH_TIME_ZONE.getName().equalsIgnoreCase(dataType.trim())
                            || RdbEnum.DM7DataType.TIMESTAMP_WITH_LOCAL_TIME_ZONE.getName().equalsIgnoreCase(dataType.trim())) {
                        return dataType;
                    }
                    realDataType = RdbEnum.DM7DataType.valueOf(dataType).getName();
                    break;
            }

            return realDataType;

        } catch (Exception e) {
            log.error("字段数据类型[" + dataType + "]与当前数据源类型给定的数据类型不匹配", e);
            throw new DbProxyException("字段数据类型[" + dataType + "]与当前数据源类型给定的数据类型不匹配");
        }

    }


    /**
     * 根据不同数据源类型验证并获取索引类型
     *
     * @param indexType
     * @return
     * @throws Exception
     */
    protected String getAndVerifiedIndexType(String indexType) {
        // 判断索引是否为空
        if (null == indexType) {
            throw new DbProxyException("index indexType is null");
        }

        try {
            String realIndexType = null;
            RdbEnum.DBType dbType = getDBType();
            switch (dbType) {
                case MYSQL:
                    realIndexType = RdbEnum.MysqlIndexType.valueOf(indexType).getName();
                    break;
                case ORACLE:
                    realIndexType = RdbEnum.OracleIndexType.valueOf(indexType).getName();
                    break;
                case DM7:
                    realIndexType = RdbEnum.DM7IndexType.valueOf(indexType).getName();
                    break;
            }
            return realIndexType;
        } catch (Exception e) {
            log.error("索引类型[" + indexType + "]与当前数据源类型给定的索引类型不匹配", e);
            throw new DbProxyException("索引类型[" + indexType + "]与当前数据源类型给定的索引类型不匹配");
        }

    }


    /************************************ 适配方法。适配类包装方法（默认实现接口方法），子类可选择性实现 ************************************/

    @Override
    public List<String> getCreateUserCommands() {
        return null;
    }


    @Override
    public List<String> getCreateTablespace() {
        return null;
    }


    @Override
    public List<String> getGrantOptionToUser() {
        return null;
    }


    @Override
    public List<String> getCreateDatabaseCommands(RdbCreateDatabase database) {
        return null;
    }


    @Override
    public List<String> getDropDatabaseCommands(RdbDropDatabase database) {
        return null;
    }


    @Override
    public List<String> getCreateSequenceCommands(String seqName) {
        return null;
    }


    @Override
    public List<String> getDropSequenceCommands(String seqName) {
        return null;
    }


    @Override
    public String getSelectDbNameCommand(String DbName) {
        return null;
    }


    /************************************ 子类需实现方法 ************************************/

    /**
     * 获取新增列sql
     *
     * @param tableName
     * @param rc
     * @return
     * @throws Exception
     */
    protected abstract List<String> getAddColumnCommands(String tableName, RdbColumn rc);


    /**
     * 获取修改列sql
     *
     * @param tableName
     * @param rc
     * @return
     * @throws Exception
     */
    protected abstract List<String> getModifyColumnCommands(String tableName, RdbColumn rc);


    /**
     * 获取删除列sql
     *
     * @param tableName
     * @param columnName
     * @return
     * @throws Exception
     */
    protected abstract String getDropColumnCommand(String tableName, String columnName);


    /**
     * 获取重命名列sql
     *
     * @param tableName
     * @param oldColumnName
     * @param rc
     * @return
     * @throws Exception
     */
    protected abstract String getRenameColumnCommand(String tableName, String oldColumnName, RdbColumn rc);


    /**
     * 获取新建索引sql
     *
     * @param tableName
     * @param indices
     * @param columns
     * @param otherParams 其它参数 如，mysql 的存储引擎storageEngine -- 只对 mysql 有效，其它数据类型调用忽略
     * @return
     * @throws Exception
     */
    protected abstract List<String> getCreateIndexCommands(String tableName, ArrayList<RdbIndex> indices, List<RdbColumn> columns, Object... otherParams);


    /**
     * 获取删除索引sql
     *
     * @param tableName
     * @param indices
     * @return
     * @throws Exception
     */
    protected abstract List<String> getDropIndexCommands(String tableName, ArrayList<RdbIndex> indices);


    /**
     * 获取修改主键sql
     *
     * @param tableName
     * @param oldPrimaryKey
     * @param newPrimaryKey
     * @return
     * @throws Exception
     */
    protected abstract List<String> getAlterPKCommands(String tableName, RdbPrimaryKey oldPrimaryKey, RdbPrimaryKey newPrimaryKey);


    /**
     * 获取修改表注释的sql
     *
     * @return
     */
    protected abstract String getAlterTableCommentSql();


}
