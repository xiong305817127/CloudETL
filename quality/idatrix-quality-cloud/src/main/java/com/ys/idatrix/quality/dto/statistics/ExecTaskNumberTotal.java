package com.ys.idatrix.quality.dto.statistics;

public class ExecTaskNumberTotal {
	
	//按月份时 有值
	String month ;
	
	//按任务时 有值
	String name ;
	String type ;
	String owner ;
	
	Long total ;
	
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	public Long getTotal() {
		if( total == null ) {
			total = 0L ;
		}
		return total;
	}
	public void setTotal(Long total) {
		this.total = total;
	}
	



}
