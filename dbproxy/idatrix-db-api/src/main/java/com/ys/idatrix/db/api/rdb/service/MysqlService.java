package com.ys.idatrix.db.api.rdb.service;

import com.ys.idatrix.db.api.common.RespResult;
import com.ys.idatrix.db.api.rdb.dto.RdbLinkDto;
import com.ys.idatrix.db.api.sql.dto.SqlExecRespDto;
import com.ys.idatrix.db.api.sql.dto.SqlQueryRespDto;

import java.util.ArrayList;

/**
 * @ClassName MysqlService
 * @Description TODO
 * @Author ouyang
 * @Date
 */
public interface MysqlService {

    RespResult<SqlExecRespDto> createTable(String username, RdbLinkDto rdbLinkDto, ArrayList<String> commands);

    RespResult<SqlQueryRespDto> executeQuery(String selectSql, RdbLinkDto rdbLinkDto);

}
