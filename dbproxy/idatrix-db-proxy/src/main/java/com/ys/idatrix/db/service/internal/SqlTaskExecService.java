package com.ys.idatrix.db.service.internal;


import com.ys.idatrix.db.domain.DbSqlExecution;
import com.ys.idatrix.db.domain.DbSqlResult;

import java.util.List;

/**
 * @ClassName: SqlTaskExecService
 * @Description: sql 任务执行 服务（保存数据库）
 * @Author: ZhouJian
 * @Date: 2019/3/4
 */
public interface SqlTaskExecService {
	DbSqlExecution getExecution(int id);

	List<DbSqlResult> findSqlResults(int executionId);

	List<DbSqlExecution> findLatestExecutions(String username, String system, int rows);

	int createExecution(String username, DbSqlExecution execution, List<DbSqlResult> sqlResults);

	void writeExecuteResult(String username, DbSqlResult result);
}
