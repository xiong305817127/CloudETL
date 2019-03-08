package com.ys.idatrix.db.init;

import com.alibaba.fastjson.JSONObject;
import com.ys.idatrix.db.api.sql.dto.SqlExecReqDto;
import com.ys.idatrix.db.dto.ParseResultDto;
import com.ys.idatrix.db.enums.HBaseOperator;
import com.ys.idatrix.db.enums.HiveOperator;
import com.ys.idatrix.db.enums.RdbOperator;
import com.ys.idatrix.db.exception.DbProxyException;
import com.ys.idatrix.db.parser.HBaseSQLParser;
import com.ys.idatrix.db.parser.HiveSQLParser;
import com.ys.idatrix.db.parser.RdbSQLParser;
import com.ys.idatrix.db.service.consumer.MetadataConsumer;
import com.ys.idatrix.db.util.Constants;
import com.ys.idatrix.metacube.api.beans.ActionTypeEnum;
import com.ys.idatrix.metacube.api.beans.DatabaseTypeEnum;
import com.ys.idatrix.metacube.api.beans.ResultBean;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Mysql
 * ①MySql SQL语句关键字不区分大小写。但操作的表名解析出来区分大小写
 * ②MySql druid 解析时，表名与sql中的大小写保持一致；操作类型为首字母大写（如：Select）。
 * ③jdbc操作执行时db及table区分大小写
 * Hive
 * ①Hive SQL语句关键字不区分大小写。
 * ②Hive ParserDrive AST 解析时，表名与sql中的大小写保持一致；操作类型为大写（如：SELECT）。
 * ③Hive2 操作执行时db及table不区分大小写
 * HBase
 * ①HBase Phoenix SQL语句的关键字不区分大小写。
 * ②Phoenix SQLParser解析出来的db及table区分大小写。操作类型为大写（如：SELECT）。
 * ③Phoenix操作执行时的db及table名称区分大小写。默认大写。需要区分大小写必须带上双引号括住（如："zj"."tb_entity" 或 zj."tb_EntitY"）。（现在统一做忽略大小写处理）
 *
 * @ClassName: SqlParserService
 * @Description:
 * @Author: ZhouJian
 * @Date: 2019/3/4
 */
@Slf4j
@Component
public class SqlParserService {

    private final String ERROR_NO_SUPPORT_SYSTEM_MSG = "暂时不支持：{0} 存储系统操作";
    private final String ERROR_OP_MSG = "SQL查询不支持非查询：{0} 操作";
    private final String ERROR_NO_CONFIG_MSG = "暂时未提供存储系统：{0} 对SQL的可操作性";
    private final String ERROR_NO_SUPPORT_OP_LOG = "存储系统 {} 暂不支持 {} 此操作";
    private final String ERROR_NO_SUPPORT_OP_MSG = "暂不支持此操作";
    private final String ERROR_INVOKE_PERM_MSG = "获取操作权限异常";
    private final String ERROR_PERM_LOG = "用户：{} 没有操作表：{}.{} 的 {} 权限";
    private final String ERROR_PERM_MSG = "预执行SQL没有 {0} 的 {1} 权限";
    private final String ERROR_NO_PERM_MSG = "表：{0} 没有开放可操作权限";
    private final String ERROR_MATCH_TABLE_MSG = "SQL中包含不属于DB：{0} 的表：{1}";
    private final String ERROR_NO_TABLE_MSG = "语句不完整，欠缺操作表";
    private final String WARN_NO_LIMIT_LOG = "存储系统：{} 暂不支持查询限制类似：{} 的解析";


    @Autowired(required = false)
    private MetadataConsumer metadataConsumer;

    /**
     * 读取配置默认存储系统的可操作内容
     */
    @Value("${custom.sql.support-operator}")
    private String dbSqlOperator;

    /**
     * 按数据库类型保存操作类型
     */
    private Map<String, List<String>> actualSqlOperators;

    /**
     * 默认最大查询记录限制
     */
    private int rowLimit = 20;

    /**
     * 中间变量
     */
    private final String SPLIT_TBOP = "\\|";

    /**
     * MySql、Hive、HBase 均支持limit
     */
    private final String KEYWORD_LIMIT = "LIMIT";

    /**
     * DB2可使用 fetch first 20 rows only 查询记录限制
     */
    private final String KEYWORD_FETCH = "FETCH FIRST";
    private final String KEYWORD_SPACE = " ";


    /**
     * 解析配置文件获取具体的db类型及sql操作
     *
     * @return
     */
    @PostConstruct
    private void getActualSqlOperator() {
        if (StringUtils.isBlank(this.dbSqlOperator)) {
            actualSqlOperators = null;
        } else {
            actualSqlOperators = new HashMap<>();
            //db类型
            String[] dbOperators = this.dbSqlOperator.split(";");
            for (int i = 0; i < dbOperators.length; i++) {
                String dbOperator = dbOperators[i];
                //db类型-操作类型
                String[] subDbOperators = dbOperator.split("::");
                if (null != subDbOperators && subDbOperators.length == 2) {
                    String dbType = subDbOperators[0].toLowerCase();
                    List<String> sqlOperators = Arrays.asList(subDbOperators[1].toLowerCase().split(SPLIT_TBOP));
                    actualSqlOperators.put(dbType, sqlOperators);
                }
            }
        }
    }


    /**
     * 解析sql
     *
     * @param command
     * @param dbType
     * @return
     * @throws Exception
     */
    public List<ParseResultDto> parseSQL(String command, String dbType) throws Exception {
        log.info("SQL解析存储库类型：{}，SQL语句：{}", dbType, command);
        List<ParseResultDto> results;
        switch (DatabaseTypeEnum.valueOf(dbType)) {
            case MYSQL:
            case ORACLE:
                //case DB2:
            case DM:
            case POSTGRESQL:
                RdbSQLParser rsp = new RdbSQLParser();
                results = rsp.parseSQL(command, dbType.toLowerCase());
                break;
            case HIVE:
                HiveSQLParser hsp = new HiveSQLParser();
                results = hsp.parseSQL(command);
                break;
            case HBASE:
                HBaseSQLParser hbsp = new HBaseSQLParser();
                results = hbsp.parseSQL(command);
                break;
            default:
                log.error(MessageFormat.format(ERROR_NO_SUPPORT_SYSTEM_MSG, dbType));
                throw new Exception(MessageFormat.format(ERROR_NO_SUPPORT_SYSTEM_MSG, dbType));
        }
        return results;
    }


    /**
     * ①校验解析sql的结果
     * ②重构sql
     *
     * @param results
     * @param executeDto
     * @param username
     * @param specialOperator
     * @throws Exception
     */
    public void validateAndRebuildParseResult(List<ParseResultDto> results, SqlExecReqDto executeDto, String username, String specialOperator) throws Exception {
        if (CollectionUtils.isNotEmpty(results)) {
            for (ParseResultDto result : results) {
                int actualRowLimit = result.getActualRowLimit();
                String mainOperator = result.getMainOperator();
                Map<String, String> tabOperators = result.getTabOperators();
                Map<String, String> unTabOperators = result.getUnTabOperators();

                if (RdbOperator.SHOW.getOperator().equalsIgnoreCase(mainOperator)) {
                    //忽略 show 语句
                    continue;
                }
                //特定模式。rop提供的sql查询操作
                if (null != specialOperator && !specialOperator.equalsIgnoreCase(mainOperator)) {
                    throw new Exception(MessageFormat.format(ERROR_OP_MSG, mainOperator));
                }

                //校验操作
                validateOperator(mainOperator, executeDto.getType());

                //校验权限
                validateTableAndPermission(username, executeDto, tabOperators);

                //重构sql语句
                rebuildSql(executeDto, tabOperators);

                //重构提取行数值
                rebuildRowLimit(executeDto, mainOperator, actualRowLimit);
            }
        }
    }


    /**
     * 校验配置的存储系统的操作与主SQL操作是否匹配
     *
     * @param mainOperator
     * @param dbType
     * @throws Exception
     */
    private void validateOperator(String mainOperator, String dbType) throws Exception {

        List<String> permits;
        if (MapUtils.isNotEmpty(actualSqlOperators) && CollectionUtils.isNotEmpty(actualSqlOperators.get(dbType.toLowerCase()))) {
            permits = actualSqlOperators.get(dbType.toLowerCase());
        } else {
            log.error(MessageFormat.format(ERROR_NO_CONFIG_MSG, dbType));
            throw new Exception(MessageFormat.format(ERROR_NO_CONFIG_MSG, dbType));
        }
        switch (DatabaseTypeEnum.valueOf(dbType)) {
            case MYSQL:
            case ORACLE:
            case DM:
            case POSTGRESQL:
                //case DB2:
            case HIVE:
            case HBASE:
                if (CollectionUtils.isNotEmpty(permits) && !permits.contains(mainOperator.toLowerCase())) {
                    log.error(ERROR_NO_SUPPORT_OP_LOG, dbType, mainOperator);
                    throw new Exception(ERROR_NO_SUPPORT_OP_MSG);
                }
                break;
            //非表操作 -- HBase
                /*if (MapUtils.isNotEmpty(unTabOperators)) {
                    for (Object key : unTabOperators.keySet()) {
                        ParseEnum.HBaseOperator hbaseOperator = (ParseEnum.HBaseOperator) key;
                        if (CollectionUtils.isNotEmpty(permits) && !permits.contains(hbaseOperator.getOperator().toLowerCase())) {
                            log.error(ERROR_NO_SUPPORT_OP_LOG, dbType, hbaseOperator.getOperator());
                            throw new Exception(ERROR_NO_SUPPORT_OP_MSG);
                        }
                    }
                }*/
            default:
                log.error(MessageFormat.format(ERROR_NO_SUPPORT_SYSTEM_MSG, dbType));
                throw new Exception(MessageFormat.format(ERROR_NO_SUPPORT_SYSTEM_MSG, dbType));
        }
    }


    /**
     * ①验证查询语句中是否有表输入
     * ②验证输入的表是否在当前db中
     * ③验证表对象的操作权限
     *
     * @param username
     * @param executeDto
     * @param tabOperators
     * @throws Exception
     */
    private void validateTableAndPermission(String username, SqlExecReqDto executeDto, Map<String, String> tabOperators) throws Exception {
        if (MapUtils.isNotEmpty(tabOperators)) {
            for (String tableName : tabOperators.keySet()) {
                //数据库操作。e.g:SELECT|UPDATE|UPSERT|DELETE
                String tbOperator = tabOperators.get(tableName);
                //表操作
                List<String> tbOperators = Arrays.asList(tbOperator.split(SPLIT_TBOP));
                for (String operator : tbOperators) {
                    String opPermission = null;
                    switch (DatabaseTypeEnum.valueOf(executeDto.getType().toUpperCase())) {
                        case MYSQL:
                        case ORACLE:
                        case DM:
                        case POSTGRESQL:
                            //case DB2:
                            RdbOperator rdbOperator = RdbOperator.valueOf(operator);
                            opPermission = rdbOperator.getPermission();
                            break;
                        case HIVE:
                            HiveOperator hiveOperator = HiveOperator.valueOf(operator);
                            opPermission = hiveOperator.getPermission();
                            break;
                        case HBASE:
                            HBaseOperator hbaseOperator = HBaseOperator.valueOf(operator);
                            opPermission = hbaseOperator.getPermission();
                            break;
                        default:
                            break;
                    }

                    if (StringUtils.isNotEmpty(opPermission)) {
                        if (executeDto.isNeedPermission()) {
                            ActionTypeEnum actionTypeEnum;
                            try {
                                ResultBean<ActionTypeEnum> result = metadataConsumer.getTbPermiss(username, Long.valueOf(executeDto.getSchemaId()), tableName.toLowerCase());
                                log.info("调用元数据接口：{}，输入参数：user={},schemaId={},tableName={}，返回结果：{}", "getTbPermiss", username, executeDto.getSchemaId(), tableName, JSONObject.toJSONString(result, true));
                                if (result.isSuccess()) {
                                    actionTypeEnum = result.getData();
                                } else {
                                    log.error("查询元数据表操作权限失败:{}", result.getMsg());
                                    throw new DbProxyException(result.getMsg());
                                }
                            } catch (Exception e) {
                                log.error(ERROR_INVOKE_PERM_MSG);
                                throw new Exception(ERROR_INVOKE_PERM_MSG);
                            }

                            switch (opPermission) {
                                case "read":
                                    if (actionTypeEnum != ActionTypeEnum.ALL && actionTypeEnum != ActionTypeEnum.READ) {
                                        log.error(ERROR_PERM_LOG, username, executeDto.getSchemaId(), tableName, operator);
                                        throw new Exception(MessageFormat.format(ERROR_PERM_MSG, tableName, operator));
                                    }
                                    break;
                                case "delete":
                                case "write":
                                    if (actionTypeEnum != ActionTypeEnum.ALL && actionTypeEnum != ActionTypeEnum.WRITE) {
                                        log.error(ERROR_PERM_LOG, username, executeDto.getSchemaId(), tableName, operator);
                                        throw new Exception(MessageFormat.format(ERROR_PERM_MSG, tableName, operator));
                                    }
                                    break;
                                case "create":
                                case "alter":
                                case "drop":
                                default:
                                    throw new Exception(MessageFormat.format(ERROR_PERM_MSG, tableName, operator));
                            }
                        }
                    } else {
                        log.error(MessageFormat.format(ERROR_NO_PERM_MSG, tableName));
                        throw new Exception(MessageFormat.format(ERROR_NO_PERM_MSG, tableName));
                    }
                }
            }
        } else {
            log.error(ERROR_NO_TABLE_MSG);
            throw new Exception(ERROR_NO_TABLE_MSG);
        }
    }


    /**
     * HBase sql重新构建:
     * ①schema.table：db和table全部大写的则不用带双引号否则则带双引号处理（现在统一做忽略大小写处理）
     * ②去除“;”（phoenix 执行包含“;”的语句报错）
     * <p>
     * Hive sql重构：
     * ①去除“;”（hive2 执行包含“;”的语句报错）
     * <p>
     * DM、PostgreSql sql重新构建：
     * ①schema.table
     *
     * @param executeDto
     * @param tabOperators
     */
    public void rebuildSql(SqlExecReqDto executeDto, Map<String, String> tabOperators) {
        if (MapUtils.isNotEmpty(tabOperators)) {
            String trimSql = executeDto.getCommand().trim();
            log.info("{} sql rebuild before:{}", executeDto.getType(), trimSql);
            switch (DatabaseTypeEnum.valueOf(executeDto.getType().toUpperCase())) {
                case DM:
                case POSTGRESQL:
                    for (String tableName : tabOperators.keySet()) {
                        String regex = "(\\b\\s+" + tableName + "\\b)";
                        trimSql = replaceAllIgnoreCase(trimSql, regex, KEYWORD_SPACE + executeDto.getSchemaName() + "." + tableName);
                    }
                    break;
                //HBase 既要用dbName+"."+tableName,又要去除尾部的分号
                case HBASE:
                    for (String tableName : tabOperators.keySet()) {
                        String regex = "(\\b\\s+" + tableName + "\\b)";
                        trimSql = replaceAllIgnoreCase(trimSql, regex, KEYWORD_SPACE + executeDto.getSchemaName() + "." + tableName);
                    }
                    //Hive 要去除尾部的分号
                case HIVE:
                    if (trimSql.endsWith(";")) {
                        StringBuilder sb = new StringBuilder(trimSql);
                        trimSql = sb.deleteCharAt(sb.length() - 1).toString();
                    }
                    break;
                default:
                    break;
            }
            log.info("{} sql rebuild after:{}", executeDto.getType(), trimSql);
            executeDto.setCommand(trimSql);
        }
    }


    /**
     * 查询操作限制 20 条
     * MySql、Hive、HBase、DM、POSTGRESQL 应用 limit做查询记录限制。可通过解析获取到 limit 关键词
     * Oracle 可通过 where rownum < 10 来获取*行记录。不能通过解析获取到 limit 关键词
     * DB2  1.可以用fetch first 10 rows only 获取记录的前*行记录。可通过解析获取到 limit 关键词
     * 2.可用类似 Oracle rownum操作。不能通过解析获取到 limit 关键词
     * 此处只处理可以解析到 limit 关键词的做处理。
     * 1.没有 limit 关键词，只对 MySql、Hive、HBase、DM、POSTGRESQL 做 添加“limit 20”的限制；
     * 2.有 limit 关键词，且 >20 的情况，对 DB2(处理 fetch first)、MySql、Hive、HBase 做修改处理设置为“limit 20”。
     *
     * @param executeDto
     * @param operator
     * @param actualRowLimit
     */
    public void rebuildRowLimit(SqlExecReqDto executeDto, String operator, int actualRowLimit) {
        if (Constants.SQL_EXEC_TYPE_Q.equalsIgnoreCase(operator)) {
            String trimSql = executeDto.getCommand().trim();
            StringBuilder rebuildSql = null;
            //SQL语句中（主SQL）不包含 limit 从句。添加limit
            if (actualRowLimit == -1) {
                switch (DatabaseTypeEnum.valueOf(executeDto.getType().toUpperCase())) {
                    case MYSQL:
                    case HIVE:
                    case HBASE:
                    case DM:
                    case POSTGRESQL:
                        rebuildSql = new StringBuilder(trimSql);
                        if (trimSql.endsWith(";")) {
                            rebuildSql.deleteCharAt(rebuildSql.length() - 1)
                                    .append(KEYWORD_SPACE)
                                    .append(KEYWORD_LIMIT)
                                    .append(KEYWORD_SPACE)
                                    .append(rowLimit)
                                    .append(";");
                        } else {
                            rebuildSql.append(KEYWORD_SPACE)
                                    .append(KEYWORD_LIMIT)
                                    .append(KEYWORD_SPACE)
                                    .append(rowLimit);
                        }
                        break;
                    default:
                        log.warn(WARN_NO_LIMIT_LOG, executeDto.getType(), KEYWORD_LIMIT);
                        break;
                }
            }
            //SQL语句中（主SQL）包含 limit，且 limit 值大于20。修改limit。
            if (actualRowLimit > rowLimit) {
                switch (DatabaseTypeEnum.valueOf(executeDto.getType().toUpperCase())) {
                    //Mysql、Hive、HBase 相同方式处理 limit 关键词。
                    case MYSQL:
                    case HIVE:
                    case HBASE:
                    case DM:
                    case POSTGRESQL:
                        //查找最后一个，因为子查询中可能包含。
                        int limitIdx = trimSql.trim().toUpperCase().lastIndexOf(KEYWORD_LIMIT);
                        if (limitIdx > -1) {
                            rebuildSql = new StringBuilder();

                            //不包含limit前半部
                            String prefixSql = trimSql.substring(0, limitIdx);

                            //包含limit后半部
                            String suffixSql = trimSql.substring(limitIdx);

                            //“limit”正则表达式
                            String regEx = "(\\s*(LIMIT)\\s+[0-9]{1,})";

                            suffixSql = replaceAllIgnoreCase(suffixSql, regEx, KEYWORD_SPACE + KEYWORD_LIMIT + KEYWORD_SPACE + rowLimit);

                            rebuildSql.append(prefixSql.trim()).append(KEYWORD_SPACE).append(suffixSql);
                        }
                        break;
                    //db2中的“FETCH FIRST 10 rows only” druid解析后也记录limit值，需要
                    /*case DB2:
                        int fetchIdx = trimSql.trim().toUpperCase().lastIndexOf(KEYWORD_FETCH);
                        if (fetchIdx > -1) {
                            rebuildSql = new StringBuilder();

                            //不包含limit前半部
                            String prefixSql = trimSql.substring(0, fetchIdx);

                            //包含limit后半部
                            String suffixSql = trimSql.substring(fetchIdx);

                            //“fetch first”正则表达式
                            String regEx = "(\\s*(FETCH\\s+FIRST)\\s+[0-9]{1,})";

                            suffixSql = replaceAllIgnoreCase(suffixSql, regEx, KEYWORD_SPACE + KEYWORD_FETCH + KEYWORD_SPACE + rowLimit);

                            rebuildSql.append(prefixSql.trim()).append(KEYWORD_SPACE).append(suffixSql);
                        }
                        break;*/
                    default:
                        log.warn(WARN_NO_LIMIT_LOG, executeDto.getType().toUpperCase(), KEYWORD_LIMIT);
                        break;
                }
            }
            if (null != rebuildSql) {
                log.info("sql rebuild limit before:{}", trimSql);
                executeDto.setCommand(rebuildSql.toString());
                log.info("sql rebuild limit after:{}", executeDto.getCommand());
            }
        }
    }


    /***
     * replaceAll,忽略大小写
     *
     * @param input
     * @param regex
     * @param replacement
     * @return
     */
    private String replaceAllIgnoreCase(String input, String regex, String replacement) {
        Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(input);
        String result = m.replaceAll(replacement);
        return result;
    }

}
