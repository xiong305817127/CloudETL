/**
 * 
 */
package com.ys.idatrix.cloudetl.metacube.api.dto;

import java.io.Serializable;
import java.util.List;

/**
 * 数据库表域列表list DTO
 * @author WGZ
 * @since 05-12-2017
 *
 */
public class DbTableFieldsListDto implements Serializable{
	private static final long serialVersionUID = 1L;
	private List<DbTableFieldsDto> list;
	private boolean success;
	private String mess;
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getMess() {
		return mess;
	}
	public void setMess(String mess) {
		this.mess = mess;
	}
	public List<DbTableFieldsDto> getList() {
		return list;
	}
	public void setList(List<DbTableFieldsDto> list) {
		this.list = list;
	}
}
