package com.ys.idatrix.db.parser;

import com.ys.idatrix.db.dto.ParseResultDto;
import com.ys.idatrix.db.enums.HiveOperator;
import lombok.extern.slf4j.Slf4j;
import org.antlr.runtime.tree.Tree;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.BaseSemanticAnalyzer;
import org.apache.hadoop.hive.ql.parse.HiveParser;
import org.apache.hadoop.hive.ql.parse.ParseDriver;

import java.text.MessageFormat;
import java.util.*;

/**
 * hive sql解析类
 * 目的：实现HQL的语句解析，分析出输入输出表。
 * 重点：
 *      ① 所有（select、insert overwrite、alter、drop、truncate、load、createtable）操作这判断到表级别。
 *      ② 操作的db和table不区分大小写。但是解析出来的表名区分大小写
 * 实现：ParseDriver解析HQL生成AST，深度优先遍历，遇到操作的token则判断当前的操作，遇到子句则压栈当前处理，处理子句。子句处理完，栈弹出。
 * 关键点：遇到TOK_NAME则判断出当前操作的表
 * 试用范围：
 * 1、支持标准SQL
 * 2、不支持transform using script
 *
 * @ClassName: HiveSQLParser
 * @Description:  hive sql解析类
 * @Author: ZhouJian
 * @Date: 2017/7/20
 */
@Slf4j
public class HiveSQLParser {

    private final String ERROR_SQL_MSG = "预执行SQL非法，包含多条执行语句";
    private final String ERROR_SQL_LOG = "预执行SQL：{}，包含多条执行语句";
    private final String INFO_AST_MSG = "\tHQL：{0} \n\t转换为语法抽象树：{1}";

    /**
     * 中间变量
     */
    private Stack<HiveOperator> operatorStack = new Stack<HiveOperator>();

    /**
     * 当前节点操作类型
     */
    private HiveOperator currOperator;

    /**
     * 结果
     */
    private List<ParseResultDto> resultList = new ArrayList<ParseResultDto>();

    /**
     * 所有的表名称及其操作类型。存储格式：key：table_name，value：insert|select
     */
    private Map<String,String> tabOperators = new HashMap<>();

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


    public HiveSQLParser() {
    }

    public HiveSQLParser(int sqlLimit) {
        this.sqlLimit = sqlLimit;
    }


    /**
     * hql解析入口
     *
     * @param hql
     * @return
     * @throws Exception
     */
    public List<ParseResultDto> parseSQL(String hql) throws Exception {
        if (StringUtils.isEmpty(hql.trim())) {
            return resultList;
        }
        if (ParseHandler.validateMultiSql(hql, sqlLimit)) {
            log.error(ERROR_SQL_LOG, hql);
            throw new Exception(ERROR_SQL_MSG);
        }
        clearParse();
        ParseDriver pd = new ParseDriver();
        /* String trimSql = hql.toLowerCase().trim();
        if (trimSql.startsWith("set") || trimSql.startsWith("add")) {
            return resultList;
        }*/
        //执行sql中包含“;”,移除后做sql解析。SQLParser 不支持带sql中带分句符“;”
        if (hql.trim().endsWith(";")) {
            hql = hql.trim().substring(0, hql.length() - 1);
        }
        ASTNode ast = pd.parse(hql.trim());
        log.info(MessageFormat.format(INFO_AST_MSG, hql, ast.dump()));
        startParse(ast);
        endParse();
        return resultList;
    }


    /**
     * 清空上次处理的结果
     */
    private void clearParse() {
        actualRowLimit = -1;
        mainOperator = null;
        operatorStack.clear();
        tabOperators.clear();
        resultList.clear();
    }


    /**
     * 开始解析AST
     *
     * @param ast
     * @throws Exception
     */
    private void startParse(ASTNode ast) throws Exception {
        parseIteral(ast);
    }

    /**
     * 所有解析完毕
     */
    private void endParse() {
        ParseResultDto result = new ParseResultDto();
        result.setActualRowLimit(actualRowLimit);
        result.setMainOperator(mainOperator);
        result.setTabOperators(ParseHandler.cloneMap(tabOperators));
        resultList.add(result);
    }


    /**
     * 解析AST
     *
     * @param ast
     * @return
     * @throws Exception
     */
    private void parseIteral(ASTNode ast) throws Exception {
        prepareToParseCurrentNodeAndChild(ast);
        parseChildNodes(ast);
        parseCurrentNode(ast);
        endParseCurrentNode(ast);
    }


    /**
     * 准备解析当前节点
     *
     * @param ast
     */
    private void prepareToParseCurrentNodeAndChild(ASTNode ast) throws Exception {
        if (null != ast.getToken()) {
            log.info("当前节点：{} ", ast.getToken());
            switch (ast.getToken().getType()) {
                case HiveParser.TOK_QUERY:
                case HiveParser.TOK_SELECT:
                    operatorStack.push(currOperator);
                    currOperator = HiveOperator.SELECT;
                    break;
                case HiveParser.TOK_INSERT:
                    operatorStack.push(currOperator);
                    currOperator = HiveOperator.INSERT;
                    break;
                case HiveParser.TOK_LOAD:
                    operatorStack.push(currOperator);
                    currOperator = HiveOperator.LOAD;
                    mainOperator = HiveOperator.LOAD.getOperator();
                    break;
                case HiveParser.TOK_DROPTABLE:
                    operatorStack.push(currOperator);
                    currOperator = HiveOperator.DROP;
                    mainOperator = HiveOperator.DROP.getOperator();
                    break;
                case HiveParser.TOK_TRUNCATETABLE:
                    operatorStack.push(currOperator);
                    currOperator = HiveOperator.TRUNCATE;
                    mainOperator = HiveOperator.TRUNCATE.getOperator();
                    break;
                case HiveParser.TOK_CREATETABLE:
                case HiveParser.TOK_CREATEVIEW:
                    operatorStack.push(currOperator);
                    currOperator = HiveOperator.CREATETABLE;
                    mainOperator = HiveOperator.CREATETABLE.getOperator();
                    break;
                case HiveParser.TOK_TRANSFORM:
                    throw new Exception("no support transform using clause");
                default:
                    if (ast.getToken().getType() >= HiveParser.TOK_ALTERDATABASE_PROPERTIES
                            && ast.getToken().getType() <= HiveParser.TOK_ALTERVIEW_RENAME) { //alter操作范围值
                        operatorStack.push(currOperator);
                        currOperator = HiveOperator.ALTER;
                        mainOperator = HiveOperator.ALTER.getOperator();
                    }
                    break;
            }
        }
    }


    /**
     * 解析所有子节点
     *
     * @param ast
     * @return
     */
    private void parseChildNodes(ASTNode ast) throws Exception {
        //获取子节点个数
        int numCh = ast.getChildCount();
        log.info("当前节点：{} 的包含 {} 个子节点", ast.getText(), numCh);
        if (numCh > 0) {
            for (int num = 0; num < numCh; num++) {
                ASTNode child = (ASTNode) ast.getChild(num);
                parseIteral(child);
            }
        }
    }


    /**
     * 解析当前节点。获取表名称
     *
     * @param ast
     * @return
     */
    private void parseCurrentNode(ASTNode ast) throws Exception {
        if (ast.getToken() != null) {
            switch (ast.getToken().getType()) {
                //所有表名。解析的内容可能是：①TOK_TABNAME table_name ②TOK_TABNAME db_name table_name
                case HiveParser.TOK_TABNAME:
                    String tbName = (ast.getChildCount() == 1)
                            ? BaseSemanticAnalyzer.getUnescapedName((ASTNode) ast.getChild(0))
                            : ast.getChild(1).getText();
                    ParseHandler.pushTbOperatorToMap(tabOperators,tbName,currOperator.getOperator());
                    break;
                //insert 操作
                case HiveParser.TOK_TAB:
                    // insert overwrite into table insert_table select * from from_table AST 类似 select * from from_table 结构
                    operatorStack.push(HiveOperator.INSERT);
                    break;
                //select 操作
                case HiveParser.TOK_TABREF:
                    if (currOperator == HiveOperator.SELECT) {
                        operatorStack.push(HiveOperator.SELECT);
                    }
                    break;
                //limit
                case HiveParser.TOK_LIMIT:
                    getActualRowLimit(ast);
                    break;
            }
        }
    }


    /**
     * 结束解析语句
     *
     * @param ast
     */
    private void endParseCurrentNode(ASTNode ast) {
        if (null != ast.getToken()) {
            switch (ast.getToken().getType()) {
                case HiveParser.TOK_QUERY:
                    break;
                case HiveParser.TOK_INSERT:
                case HiveParser.TOK_SELECT:
                    currOperator = operatorStack.pop();
                    mainOperator = currOperator.getOperator();
                    break;
            }
        }
    }

    /**
     * 获取主sql的limit值
     * 注：判断当前limit是否在主sql上
     *
     * @param ast
     */
    private void getActualRowLimit(Tree ast) {
        boolean bMainSql = true;
        Tree _tree = ast;
        while (!(_tree = _tree.getParent()).isNil()) {
            if (_tree.getType() == HiveParser.TOK_SUBQUERY) {
                bMainSql = false;
            }
        }
        if (bMainSql) {
            ASTNode subNode = (ASTNode) ast.getChild(0);
            actualRowLimit = Integer.parseInt(BaseSemanticAnalyzer.getUnescapedName(subNode));
        }
    }

}
