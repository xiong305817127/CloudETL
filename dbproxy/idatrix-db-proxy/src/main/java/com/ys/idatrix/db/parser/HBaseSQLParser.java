package com.ys.idatrix.db.parser;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ys.idatrix.db.dto.ParseResultDto;
import com.ys.idatrix.db.enums.HBaseOperator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.phoenix.parse.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * HBase sql解析类
 * 目的：实现HBase SQL的语句解析，分析出操作表。
 * 重点： ①（select、upsert、delete）DML操作这判断到表级别。
 *       ② 操作的db和table区分大小写。默认大写。需要区分大小写必须带上双引号
 *       ③（create table、drop table、alter table、drop index、create index、alter index、update statistics、explain）DDL 也获取到操作表级
 *       ④ (use schema、create schema、drop schema、create function 、drop function、create sequence、drop sequence) DDL 操作获取操作对象（schema、schema.function|function、sequence）
 * 实现：SQLParser 解析HBase SQL,判断BindableStatement实例类型。
 * 关键点：包含from、where、子查询的递归查询，获取所有表
 * 适用范围：Phoenix HBase 语法
 *
 * @ClassName: HBaseSQLParser
 * @Description: HBase sql解析类
 * @Author: ZhouJian
 * @Date: 2017/7/20
 */
@Slf4j
public class HBaseSQLParser {

    private final String ERROR_SQL_MSG = "预执行SQL非法，包含多条执行语句";
    private final String ERROR_SQL_LOG = "预执行SQL：{}，包含多条执行语句";

    /**
     * 中间变量
     */
    private final String SPLIT_DOT = ".";


    /**
     * 结果
     */
    private List<ParseResultDto> resultList = Lists.newArrayList();

    /**
     * 所有的表名称及其操作类型。存储格式：key：table_name，value：insert|select
     */
    private Map<String,String> tabOperators = Maps.newHashMap();

    /**
     * 非表操作。存储格式：key：schema，value：create|use
     */
    private Map<String, String> unTabOperators = Maps.newHashMap();

    /**
     * 主SQL操作类型
     */
    private String mainOperator = null;

    /**
     * 实际sql语句中的limit
     */
    private int actualRowLimit = -1;

    /**
     * 执行sql的限定条数。默认 1 条。
     */
    private int sqlLimit = 1;

    public HBaseSQLParser() {
    }

    public HBaseSQLParser(int sqlLimit) {
        this.sqlLimit = sqlLimit;
    }

    /**
     * HBase sql 解析入口
     *
     * @param sql
     * @throws SQLException
     */
    public List<ParseResultDto> parseSQL(String sql) throws Exception {
        if (StringUtils.isEmpty(sql.trim())) {
            return resultList;
        }

        //是否多条执行语句
        if (ParseHandler.validateMultiSql(sql,sqlLimit)) {
            log.error(ERROR_SQL_LOG,sql);
            throw new Exception(ERROR_SQL_MSG);
        }
        //执行sql中包含“;”,移除后做sql解析。SQLParser 不支持带sql中带分句符“;”
        if (sql.trim().endsWith(";")) {
            sql = sql.trim().substring(0,sql.length() - 1);
        }
        clearParse();
        SQLParser sqlParser = new SQLParser(sql.trim());
        BindableStatement statement = sqlParser.parseStatement();
        log.info("当前操作类型：" + statement.getOperation().name());
        parseSelectStatement(statement);
        parseUnSelectStatement(statement);
        endParse();
        return resultList;
    }

    /**
     * 清空上次处理的结果
     */
    private void clearParse() {
        actualRowLimit = -1;
        mainOperator = null;
        tabOperators.clear();
        resultList.clear();
    }


    /**
     * 所有解析完毕
     */
    private void endParse() {
        ParseResultDto result = new ParseResultDto();
        result.setActualRowLimit(actualRowLimit);
        result.setMainOperator(mainOperator);
        result.setTabOperators(ParseHandler.cloneMap(tabOperators));
        result.setUnTabOperators(ParseHandler.cloneMap(unTabOperators));
        resultList.add(result);
    }


    /**
     * 解析查询语句
     * 联合查询、子查询、where从句子查询递归调用
     *
     * @param statement
     * @throws Exception
     */
    private void parseSelectStatement(BindableStatement statement) throws SQLException {
        //SELECT -> QUERY
        if (null != statement && statement instanceof SelectStatement) {

            SelectStatement stmt = (SelectStatement) statement;

            //优先获取limit,只获取最外层的limit
            getRowLimit(stmt.getLimit());

            //from 后的子查询
            SelectStatement innerStmt = stmt.getInnerSelectStatement();
            parseSelectStatement(innerStmt);

            //from 子查询
            TableNode fromNode = stmt.getFrom();
            if (fromNode instanceof DerivedTableNode) {
                DerivedTableNode derivedNode = (DerivedTableNode) fromNode;
                SelectStatement derivedStmt = derivedNode.getSelect();
                parseSelectStatement(derivedStmt);
            }

            //where 子查询
            ParseNode whereNode = stmt.getWhere();
            parseChildNode(whereNode);


            //联合查询UNION
            List<SelectStatement> selects = stmt.getSelects();
            if (CollectionUtils.isNotEmpty(selects)) {
                log.info("SQL:{} 包含 {} 条查询语句", stmt.toString(), selects.size());
                for (SelectStatement select : selects) {
                    parseSelectStatement(select);
                }
            }

            parseTableNode(stmt.getFrom());

            mainOperator = HBaseOperator.SELECT.getOperator();
        }
    }


    /**
     * 非select解析
     *
     * @param statement
     * @throws Exception
     */
    private void parseUnSelectStatement(BindableStatement statement) throws SQLException {
        if (null != statement && !(statement instanceof SelectStatement)) {
            //UPSERT VALUES 或 UPSERT SELECT -> UPSERT
            if (statement instanceof UpsertStatement) {

                UpsertStatement stmt = (UpsertStatement) statement;

                NamedTableNode ntn = stmt.getTable();
                String tableName = ntn.getName().getTableName();
                ParseHandler.pushTbOperatorToMap(tabOperators,tableName, HBaseOperator.UPSERT.getOperator());


                SelectStatement subStmt = stmt.getSelect();
                parseSelectStatement(subStmt);

                mainOperator = HBaseOperator.UPSERT.getOperator();

            } else if (statement instanceof DeleteStatement) {//DELETE -> DELETE

                DeleteStatement stmt = (DeleteStatement) statement;
                //优先获取limit,只获取最外层的limit
                //getRowLimit(stmt.getLimit());

                NamedTableNode ntn = stmt.getTable();
                String tableName = ntn.getName().getTableName();
                ParseHandler.pushTbOperatorToMap(tabOperators,tableName, HBaseOperator.DELETE.getOperator());
                ParseNode whereNode = stmt.getWhere();
                parseChildNode(whereNode);
                mainOperator = HBaseOperator.DELETE.getOperator();

            } else if (statement instanceof CreateTableStatement) {//CREATE TABLE 或 CREATE VIEW -> UPSERT

                DropTableStatement stmt = (DropTableStatement) statement;
                String tableName = stmt.getTableName().getTableName();
                ParseHandler.pushTbOperatorToMap(tabOperators,tableName, HBaseOperator.CREATE_TABLE.getOperator());
                mainOperator = HBaseOperator.CREATE_TABLE.getOperator();

            } else if (statement instanceof DropTableStatement) {//DROP TABLE 或 DROP VIEW -> DELETE

                DropTableStatement stmt = (DropTableStatement) statement;
                String tableName = stmt.getTableName().getTableName();
                ParseHandler.pushTbOperatorToMap(tabOperators,tableName, HBaseOperator.DROP_TABLE.getOperator());
                mainOperator = HBaseOperator.DROP_TABLE.getOperator();

            } else if (statement instanceof CreateFunctionStatement) {//CREATE FUNCTION -> UPSERT

                CreateFunctionStatement stmt = (CreateFunctionStatement) statement;
                PFunction function = stmt.getFunctionInfo();
                String functionName = function.getFunctionName();
                unTabOperators.put(functionName, HBaseOperator.CREATE_FUNCTION.getOperator());
                mainOperator = HBaseOperator.CREATE_FUNCTION.getOperator();

            } else if (statement instanceof DropFunctionStatement) {//DROP FUNCTION -> UPSERT

                DropFunctionStatement stmt = (DropFunctionStatement) statement;
                String functionName = stmt.getFunctionName();
                unTabOperators.put(functionName, HBaseOperator.DROP_FUNCTION.getOperator());
                mainOperator = HBaseOperator.DROP_FUNCTION.getOperator();

            } else if (statement instanceof CreateSequenceStatement) {//CREATE SEQUENCE -> UPSERT

                CreateSequenceStatement stmt = (CreateSequenceStatement) statement;
                TableName tn = stmt.getSequenceName();
                String schema = tn.getSchemaName();
                String sequence = tn.getTableName();
                if (StringUtils.isNotEmpty(schema)) {
                    unTabOperators.put(schema + SPLIT_DOT + sequence, HBaseOperator.CREATE_SEQUENCE.getOperator());
                } else {
                    unTabOperators.put(sequence, HBaseOperator.CREATE_SEQUENCE.getOperator());
                }
                mainOperator = HBaseOperator.CREATE_SEQUENCE.getOperator();

            } else if (statement instanceof DropSequenceStatement) {//DROP SEQUENCE -> DELETE

                DropSequenceStatement stmt = (DropSequenceStatement) statement;
                TableName tn = stmt.getSequenceName();
                String schema = tn.getSchemaName();
                String sequence = tn.getTableName();
                if (StringUtils.isNotEmpty(schema)) {
                    unTabOperators.put(schema + SPLIT_DOT + sequence, HBaseOperator.DROP_SEQUENCE.getOperator());
                } else {
                    unTabOperators.put(sequence, HBaseOperator.DROP_SEQUENCE.getOperator());
                }
                mainOperator = HBaseOperator.DROP_SEQUENCE.getOperator();

            } else if (statement instanceof AlterTableStatement) {//ALTER TABLE 或 ALTER VIEW -> UPSERT

                AlterTableStatement stmt = (AlterTableStatement) statement;
                NamedTableNode ntn = stmt.getTable();
                String tableName = ntn.getName().getTableName();
                ParseHandler.pushTbOperatorToMap(tabOperators,tableName, HBaseOperator.ALTER_TABLE.getOperator());
                mainOperator = HBaseOperator.ALTER_TABLE.getOperator();

            } else if (statement instanceof CreateIndexStatement) {//CREATE INDEX -> UPSERT

                CreateIndexStatement stmt = (CreateIndexStatement) statement;
                NamedTableNode ntn = stmt.getTable();
                String tableName = ntn.getName().getTableName();
                ParseHandler.pushTbOperatorToMap(tabOperators,tableName, HBaseOperator.CREATE_INDEX.getOperator());
                mainOperator = HBaseOperator.CREATE_INDEX.getOperator();

            } else if (statement instanceof DropIndexStatement) {//DROP INDEX -> DELETE

                DropIndexStatement stmt = (DropIndexStatement) statement;
                TableName tn = stmt.getTableName();
                String tableName = tn.getTableName();
                ParseHandler.pushTbOperatorToMap(tabOperators,tableName, HBaseOperator.DROP_INDEX.getOperator());
                mainOperator = HBaseOperator.DROP_INDEX.getOperator();

            } else if (statement instanceof AlterIndexStatement) {//ALTER INDEX -> UPSERT

                AlterIndexStatement stmt = (AlterIndexStatement) statement;
                NamedTableNode ntn = stmt.getTable();
                String tableName = ntn.getName().getTableName();
                ParseHandler.pushTbOperatorToMap(tabOperators,tableName, HBaseOperator.ALTER_INDEX.getOperator());
                mainOperator = HBaseOperator.ALTER_INDEX.getOperator();

            } else if (statement instanceof ExplainStatement) {//EXPLAIN -> QUERY

                ExplainStatement stmt = (ExplainStatement) statement;
                BindableStatement exe_stmt = stmt.getStatement();
                parseSelectStatement(exe_stmt);
                parseUnSelectStatement(exe_stmt);
                unTabOperators.put(HBaseOperator.EXPLAIN.getOperator(), HBaseOperator.EXPLAIN.getOperator());
                mainOperator = HBaseOperator.EXPLAIN.getOperator();

            } else if (statement instanceof UpdateStatisticsStatement) {//UPDATE STATISTICS -> UPSERT

                UpdateStatisticsStatement stmt = (UpdateStatisticsStatement) statement;
                NamedTableNode ntn = stmt.getTable();
                String tableName = ntn.getName().getTableName();
                ParseHandler.pushTbOperatorToMap(tabOperators,tableName, HBaseOperator.UPDATE.getOperator());
                mainOperator = HBaseOperator.UPDATE.getOperator();

            } else if (statement instanceof CreateSchemaStatement) {//CREATE SCHEMA -> UPSERT

                CreateSchemaStatement stmt = (CreateSchemaStatement) statement;
                String schema = stmt.getSchemaName();
                unTabOperators.put(schema, HBaseOperator.CREATE_SCHEMA.getOperator());
                mainOperator = HBaseOperator.CREATE_SCHEMA.getOperator();

            } else if (statement instanceof UseSchemaStatement) {//USE SCHEMA -> UPSERT

                UseSchemaStatement stmt = (UseSchemaStatement) statement;
                String schema = stmt.getSchemaName();
                unTabOperators.put(schema, HBaseOperator.USE.getOperator());
                mainOperator = HBaseOperator.USE.getOperator();

            } else if (statement instanceof DropSchemaStatement) {//DROP SCHEMA -> DELETE

                DropSchemaStatement stmt = (DropSchemaStatement) statement;
                String schema = stmt.getSchemaName();
                unTabOperators.put(schema, HBaseOperator.DROP_SCHEMA.getOperator());
                mainOperator = HBaseOperator.DROP_SCHEMA.getOperator();

            } else {
                unTabOperators.put(HBaseOperator.UNKNOWN.getOperator(), HBaseOperator.UNKNOWN.getOperator());
            }
        }
    }

    /**
     * 递归调用解析子节点
     *
     * @param node
     * @throws SQLException
     */
    private void parseChildNode(ParseNode node) throws SQLException {
        if (null != node) {
            List<ParseNode> parseNodes = node.getChildren();
            for (ParseNode parseNode : parseNodes) {
                parseChildNode(parseNode);
            }
            log.info("nodeClass:" + node.getClass().getName());
            if (node instanceof SubqueryParseNode) {
                SubqueryParseNode subNode = (SubqueryParseNode) node;
                SelectStatement subStmt = subNode.getSelectNode();
                if (null != subStmt) {
                    parseSelectStatement(subStmt);
                }
            }
        }
    }


    /**
     * 递归调用获取表名称
     *
     * @param node
     * @throws SQLException
     */
    private void parseTableNode(TableNode node) throws SQLException {
        if (null != node) {
            if (node instanceof ConcreteTableNode) {
                ConcreteTableNode ctn = (ConcreteTableNode) node;
                ParseHandler.pushTbOperatorToMap(tabOperators,ctn.getName().getTableName(), HBaseOperator.SELECT.getOperator());
            }
            if (node instanceof JoinTableNode) {
                JoinTableNode jtn = (JoinTableNode) node;
                parseTableNode(jtn.getLHS());
                parseTableNode(jtn.getRHS());
            }
        }
    }


    /**
     * 获取主节点的limit
     * 只获取最外层的limit
     *
     * @param node
     */
    private void getRowLimit(LimitNode node) {
        if (null != node && -1 == actualRowLimit) {
            ParseNode pn = node.getLimitParseNode();
            if (pn instanceof LiteralParseNode) {
                LiteralParseNode lpn = (LiteralParseNode) pn;
                actualRowLimit = Integer.valueOf(lpn.getValue() + "").intValue();
            }
        }
    }

}
