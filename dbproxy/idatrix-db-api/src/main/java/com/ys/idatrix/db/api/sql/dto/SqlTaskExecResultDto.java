package com.ys.idatrix.db.api.sql.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @ClassName: SqlTaskExecResultDto
 * @Description: Sql任务执行结果
 * @Author: ZhouJian
 * @Date: 2019/3/4
 */
@Data
@Accessors(chain = true)
public class SqlTaskExecResultDto implements Serializable{

	private static final long serialVersionUID = 7857028359785197018L;
	/**
	 * sql语句
	 */
	private String sql;

	/**
	 * 状态：wait，success, failed
	 */
	private String status;

	/**
	 * 执行结果
	 */
	private String result;

}
