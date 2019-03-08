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
     * @param select
     * @param linkDto
     * @return
     */
    public SqlQueryRespDto executeQuery(String select, RdbLinkDto linkDto) throws Exception {
        return query(select, linkDto);
    }


    /**
     * 批量更新
     *
     * @param commands
     * @param linkDto
     * @return
     */
    public List<SqlExecRespDto> batchExecuteUpdate(List<String> commands, RdbLinkDto linkDto) throws Exception {
        return batchUpdate(commands, linkDto, false, true);
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
