package com.ys.idatrix.db.api.sql.service;


import com.ys.idatrix.db.api.common.RespResult;
import com.ys.idatrix.db.api.sql.dto.SqlExecReqDto;
import com.ys.idatrix.db.api.sql.dto.SqlExecRespDto;
import com.ys.idatrix.db.api.sql.dto.SqlTaskExecDto;
import com.ys.idatrix.db.api.sql.dto.SqlQueryRespDto;

import java.util.List;

/**
 * 数据代理层，SQL执行服务
 * 
 * @author libin
 *
 */
public interface SqlExecService {

	/**
	 * 执行select查询语句</br>
	 * 支持的数据源类型：RDB 、Hive、Hbase
	 * 
	 * @param username
	 * @param query
	 * @return [</br>
	 * 
	 *         <PRE>
	 * {
	 *         </PRE>
	 * 
	 *         <PRE>
	 * column1:value1,
	 *         </PRE>
	 * 
	 *         <PRE>
	 * column2:value2
	 *         </PRE>
	 * 
	 *         <PRE>
	 * }
	 *         </PRE>
	 * 
	 *         ]
	 */
	RespResult<SqlQueryRespDto> executeQuery(String username, SqlExecReqDto query);


	/**
	 * 执行sql 更新语句，包括：</br>
	 * insert table</br>
	 * upsert table</br>
	 * update table</br>
	 * delete table</br>
	 * 支持的数据源类型：RDB 、Hive、Hbase
	 * 
	 * @param username
	 * @param update
	 * @return
	 */
	RespResult<SqlExecRespDto> executeUpdate(String username, SqlExecReqDto update);


	/**
	 * 批量执行sql 更新语句，包括：</br>
	 * insert table</br>
	 * upsert table</br>
	 * update table</br>
	 * delete table</br>
	 * 支持的数据源类型：RDB 、Hive、Hbase
	 * 
	 * @param username
	 * @param updates
	 * @return
	 */
	RespResult<List<SqlExecRespDto>> batchExecuteUpdate(String username, List<SqlExecReqDto> updates);


	/**
	 * 异步执行sql
	 * select insert,update upsert,delete DML操作
	 * @param username
	 * @param sqlExecReqDto
	 * @return
	 */
	RespResult<SqlExecRespDto> asyncExecute(String username, SqlExecReqDto sqlExecReqDto);

	/**
	 * 非阻塞方式批量执行sql 更新语句，立即返回本次执行id，适用于执行耗时较长的操作，包括：</br>
	 * create database [...]</br>
	 * create table [...]</br>
	 * drop table [...]</br>
	 * alter table [...]</br>
	 * create index [...]</br>
	 * drop index [...]</br>
	 * use [database]</br>
	 * insert table</br>
	 * upsert table</br>
	 * update table</br>
	 * delete table</br>
	 * 支持的数据源类型：RDB 、Hive、Hbase
	 * 
	 * @param username
	 * @param system
	 * @param updates
	 * @return
	 */
	RespResult<Integer> batchExecuteUpdateNoBlocking(String username, String system, List<SqlExecReqDto> updates);

	/**
	 * 获取最近执行的任务
	 * 
	 * @param username
	 * @param system
	 * @param rows
	 *            记录条数
	 * @return
	 */
	RespResult<List<SqlTaskExecDto>> getLatestSqlTasks(String username, String system, int rows);

	/**
	 * 根据id获取执行任务信息
	 * 
	 * @param executionId
	 * @return
	 */
	RespResult<SqlTaskExecDto> getSqlTaskDetail(int executionId);


	/**
	 * 查询执行任务是否完成
	 * 
	 * @param executionId
	 * @return
	 */
	RespResult<Boolean> querySqlTaskIsCompleted(int executionId);

}
