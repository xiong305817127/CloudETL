package com.ys.idatrix.db.core.base;

import com.google.common.collect.Lists;
import com.mysql.cj.jdbc.exceptions.CommunicationsException;
import com.ys.idatrix.db.api.rdb.dto.RdbLinkDto;
import com.ys.idatrix.db.api.sql.dto.SqlExecRespDto;
import com.ys.idatrix.db.api.sql.dto.SqlQueryRespDto;
import com.ys.idatrix.db.exception.DbProxyException;
import com.ys.idatrix.db.exception.HadoopSecurityManagerException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.lang3.StringUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: BaseExecService
 * @Description:
 * @Author: ZhouJian
 * @Date: 2019/3/4
 */
@Slf4j
public abstract class BaseExecService {

    /**
     * 查询
     *
     * @param select
     * @param connParam
     * @return
     */
    protected SqlQueryRespDto query(String select, Object connParam) throws Exception {
        if (StringUtils.isBlank(select)) {
            throw new DbProxyException("query sql commands is null");
        }

        //try-with-resources try结束后自动关闭实现AutoCloseable接口的类的实例（不管是否异常）
        try (Connection conn = getConnection(connParam)) {
            // 创建QueryRunner执行器对象
            QueryRunner queryRunner = new QueryRunner(true);

            //先获取列名
            List<String> cols;
            try (PreparedStatement stmt = conn.prepareStatement(select);
                 ResultSet rs = stmt.executeQuery()) {
                ResultSetMetaData metaData = rs.getMetaData();
                int columns = metaData.getColumnCount();
                cols = new ArrayList<>();
                for (int i = 1; i <= columns; i++) {
                    cols.add(metaData.getColumnName(i));
                }
            } catch (SQLException e) {
                throw e;
            }

            //获取值
            List<Map<String, Object>> data = queryRunner.query(conn, select, new MapListHandler());
            log.info("execute sql:{} query success!", select);
            return new SqlQueryRespDto(select, data, cols);
        } catch (Exception e) {
            log.info("execute sql:{} query fail!", e.getMessage());
            throw new DbProxyException(processException(e, "query", connParam), select);
        }

    }


    /**
     * 批量修改
     *
     * @param commands
     * @param connParam
     * @param autoCommit
     * @param hasRollback
     * @return
     */
    protected List<SqlExecRespDto> batchUpdate(List<String> commands, Object connParam,
                                               boolean autoCommit, boolean hasRollback) throws Exception {

        if (CollectionUtils.isEmpty(commands)) {
            throw new DbProxyException("update sql commands is null");
        }

        List<SqlExecRespDto> results = Lists.newArrayList();

        String currentCommand = null;
        Connection conn = null;
        try {
            // 获取连接
            conn = getConnection(connParam);
            // hive 不能设置 autoCommit（不支持）。RDB及Phoenix支持
            if (!autoCommit) {
                conn.setAutoCommit(autoCommit);
            }

            // 创建QueryRunner执行器对象
            QueryRunner queryRunner = new QueryRunner(true);
            for (String command : commands) {
                currentCommand = command;
                int effectRow = queryRunner.update(conn, currentCommand);
                results.add(new SqlExecRespDto(currentCommand, effectRow));
            }

            // 默认 是自动事务提交，Phoenix 与 Rdb 此处需要手动提交，否则(phoenix)upsert操作不生效
            if (!autoCommit) {
                conn.commit();
            }
            log.info("execute commands:{} batchUpdate success!", commands.toString());
        } catch (Exception e) {
            if (hasRollback) {
                try {
                    DbUtils.rollback(conn);
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
            throw new DbProxyException(processException(e, "batchUpdate", connParam), currentCommand);
        } finally {
            DbUtils.closeQuietly(conn);
        }

        return results;
    }


    /**
     * 异常打印，及关注特殊异常信息
     *
     * @param e
     * @return
     */
    protected String processException(Exception e, String runnerType, Object connParam) {
        String exMsg = e.getMessage();
        if (e instanceof SQLException) {
            SQLException ex = (SQLException) e;
            exMsg = getRdbExceptionMsg(ex, connParam);
            if (ex instanceof CommunicationsException) {
                exMsg = "不能连接数据库，请检查[ IP ]或[ 端口 ]";
            }
            if (ex instanceof CommunicationsException) {
                exMsg = "不能连接数据库，请检查[ IP ]或[ 端口 ]";
            }
            log.error("SQLException error: ", ex.getMessage());
        } else if (e instanceof HadoopSecurityManagerException) {
            log.error("HadoopSecurityManagerException error: {}", e.getMessage());
        } else {
            log.error("execute {} error: {}", runnerType, e.getMessage());
            log.error("execute error", e);
        }
        log.error("SQL 执行异常:{}", exMsg);
        return exMsg;
    }


    /**
     * 获取错误信息。目前还不完整，可以添加
     *
     * @param ex
     * @param connParam
     * @return
     */
    private String getRdbExceptionMsg(SQLException ex, Object connParam) {
        String exMsg = ex.getMessage();
        if (connParam instanceof RdbLinkDto) {
            RdbLinkDto linkDto = (RdbLinkDto) connParam;
            switch (linkDto.getType().toUpperCase()) {
                case "MYSQL":
                    switch (ex.getErrorCode()) {
                        case 1045:
                            exMsg = "不能连接数据库：[ 用户名 ]或[ 密码 ]错误";
                            break;
                        case 1007:
                            exMsg = "数据库已经存在，创建数据库失败";
                            break;
                        case 1042:
                            exMsg = "无效的主机名";
                            break;
                        case 1044:
                            exMsg = "当前用户没有访问数据库的权限";
                            break;
                        case 1050:
                            exMsg = "数据表已存在";
                            break;
                        case 1051:
                        case 1146:
                            exMsg = "数据表不存在";
                            break;
                        case 1054:
                            exMsg = "字段不存在";
                            break;
                        case 1141:
                            exMsg = "当前用户无权访问数据库";
                            break;
                        case 1142:
                            exMsg = "当前用户无权访问数据表中的字段";
                            break;
                        case 1147:
                            exMsg = "未定义用户对数据表的访问权限";
                            break;
                        case 1149:
                            exMsg = "SQL语句语法错误";
                            break;
                        case 1227:
                            exMsg = "权限不足，您无权进行此操作";
                            break;
                        default:
                            break;
                    }
                    break;
                case "ORACLE":
                    switch (ex.getErrorCode()) {
                        case 1017:
                            exMsg = "用户名/口令无效; 登录被拒绝";
                            break;
                        default:
                            break;
                    }
                    break;
                case "DM7":
                    switch (ex.getErrorCode()) {
                        case -2501:
                            exMsg = "不能连接数据库：[ 用户名 ]或[ 密码 ]错误";
                            break;
                        case 6001:
                            exMsg = "网络通信异常：[ IP ] 或 [ 端口 ]错误";
                            break;
                        default:
                            break;
                    }
                    break;
                default:
                    break;
            }

        }
        return exMsg;
    }


    /**
     * 获取 connection
     *
     * @param connParam
     * @return
     * @throws Exception
     */
    protected abstract Connection getConnection(Object connParam) throws Exception;

}
