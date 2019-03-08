package com.ys.idatrix.db.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.sql.Timestamp;


/**
 * @ClassName: DbSqlExecution
 * @Description:
 * @Author: ZhouJian
 * @Date: 2019/3/4
 */
@Data
@NoArgsConstructor
@ToString
public class DbSqlExecution {
	/**
	 * 主键
	 */
	private Integer id;

	/**
	 * 调用系统，如dataLab
	 */
	private String system;
	
	/**
	 * 执行类型：select update
	 */
	private String type;

	/**
	 * 执行中的sql，等于0时代表执行完成
	 */
	private int executingCount;

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
