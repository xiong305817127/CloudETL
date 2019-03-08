package com.ys.idatrix.db.service.external;

import com.ys.idatrix.db.api.common.RespResult;
import com.ys.idatrix.db.api.sql.dto.SqlExecRespDto;
import com.ys.idatrix.db.exception.DbProxyException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.List;

/**
 * @Interface: DbServiceAware
 * @Description:
 * @Author: ZhouJian
 * @Date: 2019/3/5
 */
@Slf4j
public class DbServiceAware {

    /**
     * 返回执行结果
     *
     * @param results
     * @return
     */
    protected RespResult<SqlExecRespDto> wrapExecuteResult(List<SqlExecRespDto> results) {
        if (CollectionUtils.isNotEmpty(results)) {
            log.info("List<SqlExecuteResult> size:{}", results.size());
            return RespResult.buildSuccessWithData(results.get(results.size() - 1));
        } else {
            return RespResult.buildFailWithMsg("result is null");
        }
    }

    /**
     * 返回执行结果
     *
     * @param results
     * @return
     */
    protected RespResult<SqlExecRespDto> wrapExecuteResult(List<SqlExecRespDto> results, List<String> processCommands) {
        if (CollectionUtils.isNotEmpty(results)) {
            log.info("List<SqlExecuteResult> size:{}", results.size());
            return RespResult.buildSuccessWithData(results.get(results.size() - 1));
        } else {
            String sql = CollectionUtils.isEmpty(processCommands) ? "" : StringUtils.join(processCommands,",");
            SqlExecRespDto sqlExecRespDto = new SqlExecRespDto(sql);
            return RespResult.buildFailWithDataAndMsg(sqlExecRespDto,"result is null");
        }
    }


    /**
     * 异常处理
     *
     * @param e
     * @return
     */
    protected RespResult<SqlExecRespDto> wrapExecuteResultWithException(Exception e) {
        if (e instanceof DbProxyException) {
            String sqlCommand = ((DbProxyException) e).getSqlCommand();
            if (StringUtils.isNotBlank(sqlCommand)) {
                log.error(sqlCommand + " 执行失败：" + e.getMessage());
            }
        }
        return RespResult.buildFailWithMsg(e.getMessage());
    }

}
