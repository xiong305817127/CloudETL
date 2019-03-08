package com.ys.idatrix.db.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.sql.Timestamp;


/**
 * @ClassName: DbSqlResult
 * @Description:
 * @Author: ZhouJian
 * @Date: 2019/3/4
 */
@Data
@NoArgsConstructor
@ToString
public class DbSqlResult {
	/**
	 * 主键
	 */
	private Integer id;

	/**
	 * 执行id
	 */
	private Integer executionId;

	/**
	 * sql语句
	 */
	private String sql;

	/**
	 * 数据源类型：rdb,hive,hbase
	 */
	private String dbType;

	/**
	 * 数据源
	 */
	private String dbSource;

	/**
	 * 状态：wait，success, failed
	 */
	private String status;

	/**
	 * 执行结果
	 */
	private String result;

	/**
	 * 创建人
	 */
	private String creator;

	/**
	 * 创建时间
	 */
	private Timestamp createTime;

	/**
	 * 修改人
	 */
	private String modifier;

	/**
	 * 修改时间
	 */
	private Timestamp modifyTime;

}
