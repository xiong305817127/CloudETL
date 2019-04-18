package com.ys.idatrix.quality.dto.statistics;

public class TaskMonthTotal {
	
	String month ;
	Long total ;
	
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
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
