package com.ys.idatrix.db.core.rdb;

import com.ys.idatrix.db.api.rdb.dto.RdbLinkDto;
import com.ys.idatrix.db.api.sql.dto.SqlExecRespDto;
import com.ys.idatrix.db.api.sql.dto.SqlQueryRespDto;
import com.ys.idatrix.db.core.base.BaseExecService;
import com.ys.idatrix.db.exception.DbProxyException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;

/**
 * @ClassName: RdbExecService
 * @Description: 关系型数据库Sql执行
 * @Author: ZhouJian
 * @Date: 2019/3/4
 */
@Slf4j
@Service
public class RdbExecService extends BaseExecService {


    /**
     * 查询并返回结果
     *
     * @param linkDto
     * @param select
     * @return
     */
    public SqlQueryRespDto executeQuery(RdbLinkDto linkDto, String select) throws Exception {
        return query(linkDto, select);
    }


    /**
     * 批量更新
     *
     * @param linkDto
     * @param commands
     * @return
     */
    public List<SqlExecRespDto> batchExecuteUpdate(RdbLinkDto linkDto, String... commands) throws Exception {
        return batchUpdate(linkDto, false, true, commands);
    }


    /**
     * 1.不带数据库测试支持：MYSQL、DM、POSTGRESQL（url中需最后位置带上斜杠“/”），ORACLE不支持
     * 2.带数据库测试执行：MYSQL、DM、ORACLE、POSTGRESQL
     *
     * @param linkDto
     * @return
     */
    public Boolean testDBLink(RdbLinkDto linkDto) throws Exception {

        try {
            Connection conn = getConnection(linkDto);
            if (null != conn) {
                return Boolean.TRUE;
            } else {
                throw new DbProxyException("不能获取连接");
            }
        } catch (Exception e) {
            log.error("execute rdb testDBLink fail exception is {}", e.getMessage());
            throw new DbProxyException(processException(e, "testDBLink", linkDto));
        }
    }


    @Override
    protected Connection getConnection(Object connParam) throws Exception {
        if (connParam instanceof RdbLinkDto) {
            RdbLinkDto config = (RdbLinkDto) connParam;
            // 加载驱动
            Class.forName(config.getDriverClassName());

            // 返回Connection链接
            return DriverManager.getConnection(config.getUrl(), config.getUsername(), config.getPassword());
        }
        return null;
    }

}
