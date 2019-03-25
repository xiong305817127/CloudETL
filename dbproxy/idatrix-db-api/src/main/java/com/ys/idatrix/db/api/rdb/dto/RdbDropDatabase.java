package com.ys.idatrix.db.api.rdb.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @ClassName: RdbDropDatabase
 * @Description:
 * @Author: ZhouJian
 * @Date: 2019/3/4
 */
@Data
@Accessors(chain = true)
public class RdbDropDatabase implements Serializable {

	private static final long serialVersionUID = 5633881228232356265L;

	/**
	 * 数据库名称（模式）
	 */
	private String database;

	/**
	 * 用户名
	 */
	private String userName;

}
