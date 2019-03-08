package com.ys.idatrix.db.api.hive.service;


import com.ys.idatrix.db.api.common.RespResult;
import com.ys.idatrix.db.api.hive.dto.HiveTable;
import com.ys.idatrix.db.api.sql.dto.SqlExecRespDto;

/**
 * @ClassName: HiveService
 * @Description:
 * @Author: ZhouJian
 * @Date: 2019/3/4
 */
public interface HiveService {

	RespResult<SqlExecRespDto> createDatabase(String username, String database);

	RespResult<SqlExecRespDto> dropDatabase(String username, String database);

	RespResult<SqlExecRespDto> createTable(String username, HiveTable createTable);

	RespResult<SqlExecRespDto> dropTable(String username, String database, String tableName, boolean bForced);
}
