package com.ys.idatrix.db.service.external.provider.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.google.common.base.Preconditions;
import com.ys.idatrix.db.api.common.RespResult;
import com.ys.idatrix.db.api.rdb.dto.RdbEnum;
import com.ys.idatrix.db.api.rdb.dto.RdbLinkDto;
import com.ys.idatrix.db.api.rdb.service.OracleService;
import com.ys.idatrix.db.api.sql.dto.SqlExecRespDto;
import com.ys.idatrix.db.api.sql.dto.SqlQueryRespDto;
import com.ys.idatrix.db.core.rdb.RdbExecService;
import com.ys.idatrix.db.service.external.provider.base.DbServiceAware;
import com.ys.idatrix.db.util.SqlExecuteUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName OracleServiceImpl
 * @Description oracle service 实现类
 * @Author ouyang
 * @Date
 */
@Slf4j
@Service(protocol = "dubbo", timeout = 60000, interfaceClass = OracleService.class)
@Component
public class OracleServiceImpl extends DbServiceAware implements OracleService {

    // ============ 以下查询都是基于模式

    /**
     * 查询表空间
     **/
    private static final String SELECT_TABLESPACE = "select tablespace_name from user_tablespaces";

    /**
     * 查询当前的序列
     **/
    private static final String SELECT_SEQUENCES = "select sequence_name from user_sequences";

    /**
     * 判断表名是否存在
     **/
    private static final String SELECT_TABLE = "select count(*) as count from user_tables where table_name = '%s'";

    /**
     * 判断视图名是否存在
     **/
    private static final String SELECT_VIEW = "select count(*) as count from user_views where view_name = '%s'";

    /**
     * 查询约束（主键，外键，唯一，检查）
     **/
    private static final String SELECT_CONSTRAINT = "select count(*) as count from user_constraints where constraint_name = '%s'";

    /**
     * 查询索引
     **/
    private static final String SELECT_INDEX = "select count(*) as count from user_indexes where index_name ='%s'";

    @Autowired(required = false)
    private RdbExecService rdbExecService;


    @Override
    public RespResult<SqlExecRespDto> batchExecuteUpdate(ArrayList<String> commands, RdbLinkDto linkDto) {
        try {
            Preconditions.checkNotNull(linkDto, "create table, RdbLinkDto is null");
            // 重构RDBConfiguration
            SqlExecuteUtils.rebuildRdbLink(linkDto);
            // 执行sql
            List<SqlExecRespDto> results = rdbExecService.batchExecuteUpdate(linkDto, commands.toArray(new String[0]));
            return wrapExecuteResult(results, commands);
        } catch (Exception e) {
            log.error("batchExecuteUpdate 执行异常:{}", e.getMessage());
            return wrapExecuteResultWithException(e);
        }
    }


    @Override
    public RespResult<Boolean> tableNameExists(String tableName, RdbLinkDto linkDto) {
        if (StringUtils.isBlank(tableName)) {
            log.error("table name is null");
            return RespResult.buildFailWithMsg("table name is null");
        }

        Preconditions.checkNotNull(linkDto, "check table exists, RdbLinkDto is null");

        if (!RdbEnum.DBType.ORACLE.name().equals(linkDto.getType().toUpperCase())) {
            RespResult.buildFailWithMsg("Qualify ORACLE call");
        }

        String sql = String.format(SELECT_TABLE, tableName);
        return getResult(sql, linkDto);
    }

    @Override
    public RespResult<Boolean> viewNameExists(String viewName, RdbLinkDto linkDto) {
        if (StringUtils.isBlank(viewName)) {
            log.error("view name is null");
            return RespResult.buildFailWithMsg("view name is null");
        }

        Preconditions.checkNotNull(linkDto, "check view exists, RdbLinkDto is null");

        if (!RdbEnum.DBType.ORACLE.name().equals(linkDto.getType().toUpperCase())) {
            return RespResult.buildFailWithMsg("Qualify ORACLE call");
        }

        String sql = String.format(SELECT_VIEW, viewName);
        return getResult(sql, linkDto);
    }


    @Override
    public RespResult<Boolean> constraintsNameExists(String constraintsName, RdbLinkDto linkDto) {
        if (StringUtils.isBlank(constraintsName)) {
            log.error("constraints name is null");
            return RespResult.buildFailWithMsg("constraints name is null");
        }

        Preconditions.checkNotNull(linkDto, "check constraints exists, RdbLinkDto is null");

        if (!RdbEnum.DBType.ORACLE.name().equals(linkDto.getType().toUpperCase())) {
            return RespResult.buildFailWithMsg("Qualify ORACLE call");
        }

        String sql = String.format(SELECT_CONSTRAINT, constraintsName);

        return getResult(sql, linkDto);
    }


    @Override
    public RespResult<Boolean> indexNameExists(String indexName, RdbLinkDto linkDto) {
        if (StringUtils.isBlank(indexName)) {
            log.error("index name is null");
            return RespResult.buildFailWithMsg("index name is null");
        }

        Preconditions.checkNotNull(linkDto, "check index exists, RdbLinkDto is null");

        if (!RdbEnum.DBType.ORACLE.name().equals(linkDto.getType().toUpperCase())) {
            return RespResult.buildFailWithMsg("Qualify ORACLE call");
        }

        String sql = String.format(SELECT_INDEX, indexName);

        return getResult(sql, linkDto);
    }


    @Override
    public RespResult<SqlQueryRespDto> selectSequenceList(RdbLinkDto linkDto) {

        Preconditions.checkNotNull(linkDto, "RdbLinkDto is null");

        if (!RdbEnum.DBType.ORACLE.name().equals(linkDto.getType().toUpperCase())) {
            return RespResult.buildFailWithMsg("Qualify ORACLE call");
        }

        String sql = SELECT_SEQUENCES;

        try {

            // 重构RDBConfiguration
            SqlExecuteUtils.rebuildRdbLink(linkDto);

            // 执行查询
            SqlQueryRespDto respDto = rdbExecService.executeQuery(linkDto, sql);
            return RespResult.buildSuccessWithData(respDto);
        } catch (Exception e) {
            log.error("selectSequenceList 执行异常:{}", e.getMessage());
            return RespResult.buildFailWithMsg(e.getMessage());
        }

    }


    @Override
    public RespResult<SqlQueryRespDto> selectTableSpace(RdbLinkDto linkDto) {

        Preconditions.checkNotNull(linkDto, "RdbLinkDto is null");

        if (!RdbEnum.DBType.ORACLE.name().equals(linkDto.getType().toUpperCase())) {
            return RespResult.buildFailWithMsg("Qualify ORACLE call");
        }

        String sql = SELECT_TABLESPACE;


        try {
            // 重构RDBConfiguration
            SqlExecuteUtils.rebuildRdbLink(linkDto);

            // 执行查询
            SqlQueryRespDto respDto = rdbExecService.executeQuery(linkDto, sql);
            return RespResult.buildSuccessWithData(respDto);
        } catch (Exception e) {
            log.error("selectSequenceList 执行异常:{}", e.getMessage());
            return RespResult.buildFailWithMsg(e.getMessage());
        }

    }


    @Override
    public RespResult<Boolean> execute(List<String> sqls, RdbLinkDto linkDto) {
        // 创建一个数据库连接
        Connection con = null;
        Statement statement = null;
        String errorSql = null;
        try {
            // 加载Oracle驱动程序
            Class.forName("oracle.jdbc.driver.OracleDriver");
            String url = "jdbc:oracle:" + "thin:@" + linkDto.getIp() + ":" + linkDto.getPort() + ":" + linkDto.getDbName();
            // 用户名,系统默认的账户名
            String user = linkDto.getUsername();
            // 你安装时选设置的密码
            String password = linkDto.getPassword();
            // 获取连接
            con = DriverManager.getConnection(url, user, password);
            statement = con.createStatement();
            for (String sql : sqls) {
                errorSql = sql;
                statement.execute(sql);
            }
            return RespResult.buildSuccessWithData(Boolean.TRUE);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("sql:{} execute fail:{}", errorSql, e.getMessage());
            return RespResult.buildFailWithMsg(e.getMessage());
        } finally {
            try {
                // 逐一将上面的几个对象关闭，因为不关闭的话会影响性能、并且占用资源
                // 注意关闭的顺序，最后使用的最先关闭
                if (statement != null) {
                    statement.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private RespResult<Boolean> getResult(String sql, RdbLinkDto linkDto) {

        try {

            // RdbLinkDto
            SqlExecuteUtils.rebuildRdbLink(linkDto);

            //执行结果
            SqlQueryRespDto rt = rdbExecService.executeQuery(linkDto, sql);
            if (CollectionUtils.isEmpty(rt.getData())) {
                return RespResult.buildSuccessWithData(Boolean.FALSE);
            } else {
                if (Integer.valueOf(rt.getData().get(0).get("count") + "") > 0) {
                    return RespResult.buildSuccessWithData(Boolean.TRUE);
                } else {
                    return RespResult.buildSuccessWithData(Boolean.FALSE);
                }
            }
        } catch (Exception e) {
            log.error("sql:{} 执行异常:{}", sql, e.getMessage());
            return RespResult.buildFailWithMsg(e.getMessage());
        }

    }

}