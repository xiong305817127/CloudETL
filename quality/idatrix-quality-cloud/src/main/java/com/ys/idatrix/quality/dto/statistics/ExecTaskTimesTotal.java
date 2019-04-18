package com.ys.idatrix.quality.dto.statistics;

public class ExecTaskTimesTotal {
	
	String month ;
	Long successTotal ;
	Long failTotal ;
	Long runningTotal ;
	
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public Long getSuccessTotal() {
		if( successTotal == null ) {
			successTotal = 0L ;
		}
		return successTotal;
	}
	public void setSuccessTotal(Long successTotal) {
		this.successTotal = successTotal;
	}
	public Long getFailTotal() {
		if( failTotal == null ) {
			failTotal = 0L ;
		}
		return failTotal;
	}
	public void setFailTotal(Long failTotal) {
		this.failTotal = failTotal;
	}
	public Long getRunningTotal() {
		if( runningTotal == null ) {
			runningTotal = 0L ;
		}
		return runningTotal;
	}
	public void setRunningTotal(Long runningTotal) {
		this.runningTotal = runningTotal;
	}
	

}
