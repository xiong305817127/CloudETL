package com.ys.idatrix.db.parser;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLLimit;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.db2.visitor.DB2SchemaStatVisitor;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectQueryBlock;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.sql.dialect.postgresql.visitor.PGSchemaStatVisitor;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.util.JdbcUtils;
import com.google.common.collect.Lists;
import com.ys.idatrix.db.dto.ParseResultDto;
import com.ys.idatrix.db.enums.RdbOperator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * RDBMS(MySql、Oracle、DB2) sql解析类
 * 目的：实现QL的语句解析，分析出SQL操作表。
 * 重点：
 * ① com.alibaba.druid 词法、语法解析。
 * ② 操作的db和table区分大小写。
 * ③ SchemaStatVisitor 解析操作表和操作类型。
 *
 * @ClassName: RdbSQLParser
 * @Description: RDBMS(MySql、Oracle、DB2) sql解析类
 * @Author: ZhouJian
 * @Date: 2017/7/20
 */
@Slf4j
public class RdbSQLParser {

    private final String ERROR_LIMIT_MSG = "预执行SQL非法，包含多条执行语句";
    private final String ERROR_LIMIT_LOG = "预执行SQL非法，包含{}条执行语句";
    private final String ERROR_SQL_MSG = "暂时不支持：{0} 存储系统操作";
    private final String INFO_LIMIT_LOG = "SQL：{0} RowLimit：{1}";

    /**
     * 中间变量
     */
    private RdbOperator currentOperator = null;

    /**
     * 结果
     */
    private List<ParseResultDto> resultList = Lists.newArrayList();

    /**
     * 所有的表名称及其操作类型。存储格式：key：table_name，value：insert|select
     */
    private Map<String, String> tabOperators = new HashMap<>();

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

    public RdbSQLParser() {
    }

    public RdbSQLParser(int sqlLimit) {
        this.sqlLimit = sqlLimit;
    }


    /**
     * sql 解析入口
     *
     * @param sql
     * @param dbType 小写
     * @return
     * @throws Exception
     */
    public List<ParseResultDto> parseSQL(String sql, String dbType) throws Exception {
        if (StringUtils.isEmpty(sql.trim())) {
            return resultList;
        }
        clearParse();
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql.trim(), dbType);
        parseLegal(stmtList);
        for (SQLStatement stmt : stmtList) {
            parseOperator(stmt);
            parseTable(stmt, dbType);
            parseLimit(stmt, dbType);
            endParse();
        }
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
        result.setMainOperator(mainOperator);
        result.setActualRowLimit(actualRowLimit);
        result.setTabOperators(ParseHandler.cloneMap(tabOperators));
        resultList.add(result);
    }


    /**
     * 校验输入的sql是否合法。暂时不允许执行多条sql.
     *
     * @param stmtList
     * @throws Exception
     */
    public void parseLegal(List<SQLStatement> stmtList) throws Exception {
        log.info("input-sql split sub-sql records:{}", stmtList.size());
        if (CollectionUtils.isEmpty(stmtList) || stmtList.size() > sqlLimit) {
            log.error(ERROR_LIMIT_LOG, stmtList.size());
            throw new Exception(ERROR_LIMIT_MSG);
        }
    }


    /**
     * 解析sql的操作类型
     *
     * @param stmt
     * @return
     * @throws Exception
     */
    public void parseOperator(SQLStatement stmt) throws Exception {
        log.info("current sql:{}", stmt.toString());
        if (stmt instanceof SQLSelectStatement) {
            currentOperator = RdbOperator.SELECT;
        } else if (stmt instanceof SQLUpdateStatement) {
            currentOperator = RdbOperator.UPDATE;
        } else if (stmt instanceof SQLInsertStatement) {
            currentOperator = RdbOperator.INSERT;
        } else if (stmt instanceof SQLDeleteStatement) {
            currentOperator = RdbOperator.DELETE;
        } else if (stmt instanceof SQLAlterTableStatement) {
            currentOperator = RdbOperator.ALTER;
        } else if (stmt instanceof SQLCreateTableStatement) {
            currentOperator = RdbOperator.CREATETABLE;
        } else if (stmt instanceof SQLDropTableStatement) {
            currentOperator = RdbOperator.DROPTABLE;
        } else if (stmt instanceof SQLDropDatabaseStatement) {
            currentOperator = RdbOperator.DROPDATABASE;
        } else if (stmt instanceof SQLCreateDatabaseStatement) {
            currentOperator = RdbOperator.CREATEDATABASE;
        } else if(stmt instanceof MySqlShowStatement || stmt instanceof SQLExplainStatement){
        	 currentOperator = RdbOperator.SHOW;
        }else {
            currentOperator = RdbOperator.UNKNOWN;
        }
        mainOperator = currentOperator.getOperator();
        log.info("RDB当前数据查询SQL操作类型:{}", currentOperator.getOperator());
    }


    /**
     * 获取sql语句中的表名和操作名
     *
     * @param stmt
     * @throws Exception
     */
    private void parseTable(SQLStatement stmt, String dbType) throws Exception {
        /**
         * 根据不同的存储系统对应不同类型的visitor（访问者）
         */
        SchemaStatVisitor visitor;
        switch (dbType) {
            case JdbcUtils.ORACLE:
            case "dm7":
                visitor = new OracleSchemaStatVisitor();
                break;
            case JdbcUtils.MYSQL:
                visitor = new MySqlSchemaStatVisitor();
                break;
            case JdbcUtils.DB2:
                visitor = new DB2SchemaStatVisitor();
                break;
            case JdbcUtils.POSTGRESQL:
                visitor = new PGSchemaStatVisitor();
                break;
            default:
                log.error(MessageFormat.format(ERROR_SQL_MSG, dbType));
                throw new Exception(MessageFormat.format(ERROR_SQL_MSG, dbType));
        }
        //接受指定类型的访问者访问sql
        stmt.accept(visitor);
        Map<TableStat.Name, TableStat> tableStatMap = visitor.getTables();
        log.info("execute sql correlation tables:{}", tableStatMap.keySet());
        for (TableStat.Name name : tableStatMap.keySet()) {
            String tableName = name.getName();
            String tableOperator = tableStatMap.get(name).toString();
            //如果表名数据库或模式+“.”+表名
            if (tableName.split("\\.").length > 1) {
                tableName = tableName.split("\\.")[1];
            }

            //tableOperator 值为首字母大写。e.g:Select、Insert等等
            ParseHandler.pushTbOperatorToMap(tabOperators, tableName, tableOperator.toUpperCase());
        }

    }


    /**
     * 解析limit
     *
     * @param statement
     * @param dbType
     * @throws Exception
     */
    private void parseLimit(SQLStatement statement, String dbType) throws Exception {
        switch (dbType) {
            case JdbcUtils.MYSQL:
            case JdbcUtils.ORACLE:
            case "dm7":
            case JdbcUtils.DB2:
            case JdbcUtils.POSTGRESQL:
                switch (currentOperator) {
                    case SELECT:
                        SQLSelectStatement selectStatement = (SQLSelectStatement) statement;
                        SQLSelectQuery query = selectStatement.getSelect().getQuery();
                        if (query instanceof SQLSelectQueryBlock) {
                            SQLSelectQueryBlock subQuery = (SQLSelectQueryBlock) query;
                            if (JdbcUtils.ORACLE.equalsIgnoreCase(dbType)) {
                                getRowNum(subQuery);
                            } else {
                                getRowLimit(selectStatement, subQuery.getLimit());
                            }
                        }
                        break;
                }
                break;
        }
    }


    /**
     * 获取row limit
     * mysl、db2
     *
     * @param statement
     * @param sqlLimit
     */
    private void getRowLimit(SQLStatement statement, SQLLimit sqlLimit) {
        if (null != sqlLimit) {
            actualRowLimit = Integer.valueOf(sqlLimit.getRowCount().toString()).intValue();
            log.info(MessageFormat.format(INFO_LIMIT_LOG, statement.toString(), actualRowLimit));
        }
    }

    /**
     * 获取 rownum
     * oracle
     *
     * @param queryBlock
     */
    private void getRowNum(SQLSelectQueryBlock queryBlock) {
        OracleSelectQueryBlock query = (OracleSelectQueryBlock) queryBlock;
        SQLExpr expr = query.getWhere();
        if (null != expr ) {
            String exprStr = expr.toString();
            Pattern pattern = Pattern.compile("(\\s*(rownum)\\s*([<=>]){1,}\\s*[0-9]{1,}\\s*)", Pattern.CASE_INSENSITIVE);
            boolean bContains = pattern.matcher(exprStr).matches();
            if (bContains) {
                //给一个大于默认-1 的值表示sql语句中包含rownum
                actualRowLimit = 1;
            } else {
                queryBlock.limit(20, 0);
            }
        }
    }

}
