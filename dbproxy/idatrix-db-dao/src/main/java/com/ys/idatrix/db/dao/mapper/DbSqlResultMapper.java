package com.ys.idatrix.db.dao.mapper;

import com.ys.idatrix.db.annotation.TargetDataSource;
import com.ys.idatrix.db.domain.DbSqlResult;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * @ClassName: DbSqlResult
 * @Description:
 * @Author: ZhouJian
 * @Date: 2019/3/4
 */
@TargetDataSource(name = "dbproxy")
public interface DbSqlResultMapper {

	/**
	 * 根据执行id查找执行结果
	 * @param id
	 * @return
	 */
	List<DbSqlResult> findByExecutionId(@Param("id") int id);

	/**
	 * 新增记录
	 * @param record
	 * @return
	 */
	int insert(DbSqlResult record);

	/**
	 * 记录执行结果
	 * @param record
	 * @return
	 */
	int writeResult(DbSqlResult record);

}