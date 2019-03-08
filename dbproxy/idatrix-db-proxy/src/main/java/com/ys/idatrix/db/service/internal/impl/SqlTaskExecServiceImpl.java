package com.ys.idatrix.db.service.internal.impl;

import com.ys.idatrix.db.dao.mapper.DbSqlExecutionMapper;
import com.ys.idatrix.db.dao.mapper.DbSqlResultMapper;
import com.ys.idatrix.db.domain.DbSqlExecution;
import com.ys.idatrix.db.domain.DbSqlResult;
import com.ys.idatrix.db.service.internal.SqlTaskExecService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

/**
 * @ClassName: SqlTaskServiceImpl
 * @Description: sql执行服务处理类
 * @Author: ZhouJian
 * @Date: 2018/10/9
 */
@Service
public class SqlTaskExecServiceImpl implements SqlTaskExecService {

	@Autowired(required = false)
	private DbSqlExecutionMapper dbSqlExecutionMapper;

	@Autowired(required = false)
	private DbSqlResultMapper dbSqlResultMapper;

	@Override
	public DbSqlExecution getExecution(int id) {
		return dbSqlExecutionMapper.getById(id);
	}


	@Override
	public List<DbSqlResult> findSqlResults(int executionId) {
		return dbSqlResultMapper.findByExecutionId(executionId);
	}


	@Override
	public List<DbSqlExecution> findLatestExecutions(String username, String system, int rows) {
		return dbSqlExecutionMapper.findLatestExecutions(username, system, rows);
	}


	@Transactional(rollbackFor = {RuntimeException.class, SQLException.class})
	@Override
	public int createExecution(String username, DbSqlExecution execution, List<DbSqlResult> sqlResults) {
		Timestamp now = new Timestamp(System.currentTimeMillis());
		execution.setCreator(username);
		execution.setCreateTime(now);
		dbSqlExecutionMapper.insert(execution);
		for (DbSqlResult dbSqlResult : sqlResults) {
			dbSqlResult.setExecutionId(execution.getId());
			dbSqlResult.setCreator(username);
			dbSqlResult.setCreateTime(now);
			dbSqlResultMapper.insert(dbSqlResult);
		}

		return execution.getId();
	}


	@Transactional(rollbackFor = {RuntimeException.class, SQLException.class})
	@Override
	public void writeExecuteResult(String username, DbSqlResult result) {
		Timestamp now = new Timestamp(System.currentTimeMillis());
		// 保存执行结果
		result.setModifier(username);
		result.setModifyTime(now);
		dbSqlResultMapper.writeResult(result);
		// execution任务数减一
		DbSqlExecution execution = dbSqlExecutionMapper.getById(result.getExecutionId());
		execution.setExecutingCount(execution.getExecutingCount()-1);
		execution.setModifier(username);
		execution.setModifyTime(now);
		dbSqlExecutionMapper.updateExecutingCount(execution);
	}

}
