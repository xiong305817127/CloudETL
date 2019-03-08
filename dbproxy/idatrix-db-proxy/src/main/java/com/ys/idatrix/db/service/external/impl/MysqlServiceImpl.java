package com.ys.idatrix.db.service.external.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.google.common.base.Preconditions;
import com.ys.idatrix.db.api.common.RespResult;
import com.ys.idatrix.db.api.rdb.dto.RdbLinkDto;
import com.ys.idatrix.db.api.rdb.service.MysqlService;
import com.ys.idatrix.db.api.sql.dto.SqlExecRespDto;
import com.ys.idatrix.db.api.sql.dto.SqlQueryRespDto;
import com.ys.idatrix.db.core.rdb.RdbExecService;
import com.ys.idatrix.db.service.external.DbServiceAware;
import com.ys.idatrix.db.util.SqlExecuteUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName MysqlServiceImpl
 * @Description mysql service 实现类
 * @Author ouyang
 * @Date
 */
@Slf4j
@Service(protocol = "dubbo", timeout = 60000, interfaceClass = MysqlService.class)
@Component
public class MysqlServiceImpl extends DbServiceAware implements MysqlService {

    @Autowired(required = false)
    private RdbExecService rdbExecService;

    @Override
    public RespResult<SqlExecRespDto> createTable(String username, RdbLinkDto linkDto, ArrayList<String> commands) {

        if (StringUtils.isBlank(username)) {
            return RespResult.buildFailWithMsg("create table, username is null");
        }

        try {
            Preconditions.checkNotNull(linkDto, "create table, RdbLinkDto is null");

            // 重构RdbLinkDto
            SqlExecuteUtils.rebuildRdbLink(linkDto);

            // 执行sql
            List<SqlExecRespDto> results = rdbExecService.batchExecuteUpdate(commands, linkDto);

            return wrapExecuteResult(results, commands);
        } catch (Exception e) {
            log.error("createTable 执行异常:{}", e.getMessage());
            return wrapExecuteResultWithException(e);
        }
    }


    @Override
    public RespResult<SqlQueryRespDto> executeQuery(String selectSql, RdbLinkDto linkDto) {
        try {
            SqlQueryRespDto respDto = rdbExecService.executeQuery(selectSql, linkDto);
            return RespResult.buildSuccessWithData(respDto);
        } catch (Exception e) {
            log.error("executeQuery 执行异常:{}", e.getMessage());
            return RespResult.buildFailWithMsg(e.getMessage());
        }
    }

}
