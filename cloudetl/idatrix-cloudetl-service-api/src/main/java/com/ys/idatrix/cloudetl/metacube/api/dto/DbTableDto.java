/**
 * 
 */
package com.ys.idatrix.cloudetl.metacube.api.dto;

import java.io.Serializable;

/**
 * 数据库表名 DTo,
 * @author WGZ
 * @since 05-12-2017
 *
 */
public class DbTableDto implements Serializable{
	
	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = 1L;
	private String table;
	private String remark;
	private boolean isSuccess;
	private String mess;
	
	
    public boolean isSuccess() {
		return isSuccess;
	}
	public void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}
	public String getMess() {
		return mess;
	}
	public void setMess(String mess) {
		this.mess = mess;
	}
	public void setTable(String table) {
        this.table = table;
    }
    public String getTable() {
        return table;
    }
    
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	@Override
	public String toString() {
		return "DbTableDto [table=" + table + ", remark=" + remark + ", isSuccess=" + isSuccess + ", mess=" + mess
				+ "]";
	}

}
