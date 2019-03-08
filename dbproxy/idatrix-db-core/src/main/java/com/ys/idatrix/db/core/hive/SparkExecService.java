package com.ys.idatrix.db.core.hive;

import com.ys.idatrix.db.api.sql.dto.SqlExecRespDto;
import com.ys.idatrix.db.api.sql.dto.SqlQueryRespDto;
import com.ys.idatrix.db.core.base.BaseExecService;
import com.ys.idatrix.db.core.security.HadoopSecurityManager;
import com.ys.idatrix.db.exception.HadoopSecurityManagerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.util.List;

/**
 * @ClassName: SparkExecService
 * @Description: Hive Sql 执行
 * @Author: ZhouJian
 * @Date: 2019/3/4
 */
@Service
public class SparkExecService extends BaseExecService {

	@Autowired(required = false)
	private HadoopSecurityManager hadoopSecurityManager;


	/**
	 * 执行select查询语句
	 *
	 * @param select
	 * @return
	 * @throws Exception
	 */
	public SqlQueryRespDto executeQuery(String database, String select) throws Exception  {
		return query(select,database);
	}


	/**
	 * 批量执行更新语句
	 *
	 * @param commands ddl语句列表
	 * @return
	 */
	public List<SqlExecRespDto> batchExecuteUpdate(List<String> commands) throws Exception  {
		return batchUpdate(commands,"",true,false);
	}

	@Override
	protected Connection getConnection(Object connParam) throws HadoopSecurityManagerException {
		if(connParam instanceof String ){
			return hadoopSecurityManager.getSparkJdbcConnection(String.valueOf(connParam));
		}
		return null;
	}

}
