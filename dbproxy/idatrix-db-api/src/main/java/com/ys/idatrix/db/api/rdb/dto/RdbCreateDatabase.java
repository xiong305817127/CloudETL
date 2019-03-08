package com.ys.idatrix.db.api.rdb.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @ClassName: RdbCreateDatabase
 * @Description:
 * @Author: ZhouJian
 * @Date: 2019/3/4
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class RdbCreateDatabase implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 数据库标识串(必须)
	 */
	private String database;

	/**
	 * 用户名
	 */
	private String userName;

	/**
	 * 密码
	 */
	private String password;

	/**
	 * 用户存在是否重用
	 */
	private boolean userReusing;

	public RdbCreateDatabase() {
	}

	public RdbCreateDatabase(String database, String userName, String password, boolean userReusing) {
		super();
		this.database = database;
		this.userName = userName;
		this.password = password;
		this.userReusing = userReusing;
	}

}
