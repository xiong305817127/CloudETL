package com.ys.idatrix.db.api.rdb.service;

import com.ys.idatrix.db.api.common.RespResult;
import com.ys.idatrix.db.api.rdb.dto.RdbLinkDto;
import com.ys.idatrix.db.api.sql.dto.SqlExecRespDto;
import com.ys.idatrix.db.api.sql.dto.SqlQueryRespDto;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName OracleService
 * @Description oracle 服务层
 * @Author ouyang
 * @Date
 */
public interface OracleService {

    /**批量执行sql**/
    RespResult<SqlExecRespDto> batchExecuteUpdate(ArrayList<String> commands, RdbLinkDto config);
     
    /**查询表名是否存在**/
    RespResult<Boolean> tableNameExists(String tableName, RdbLinkDto config);

    /**查询视图名是否存在**/
    RespResult<Boolean> viewNameExists(String viewName, RdbLinkDto config);

    /**查询约束名是否存在**/
    RespResult<Boolean> constraintsNameExists(String constraintsName, RdbLinkDto config);

    /**查询索引名是否存在**/
    RespResult<Boolean> indexNameExists(String indexName, RdbLinkDto config);

    /**查询当前模式下的序列**/
    RespResult<SqlQueryRespDto> selectSequenceList(RdbLinkDto config);

    /**查询表空间列表**/
    RespResult<SqlQueryRespDto> selectTableSpace(RdbLinkDto config);

    /**执行特殊语句使用**/
    RespResult<Boolean> execute(List<String> commands, RdbLinkDto config);
}