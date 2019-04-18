package com.ys.idatrix.quality.dto.statistics;

public class NodeTypeTaskTimesDto {

	private String userName;
	private String taskName;
	private Long countTotal;
	
	private Long succTotal;
	private Long errTotal;
	
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getTaskName() {
		return taskName;
	}
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	public Long getCountTotal() {
		if( countTotal == null ) {
			countTotal = 0L ;
		}
		return countTotal;
	}
	public void setCountTotal(Long countTotal) {
		this.countTotal = countTotal;
	}
	public Long getSuccTotal() {
		if( succTotal == null ) {
			succTotal = 0L ;
		}
		return succTotal;
	}
	public void setSuccTotal(Long succTotal) {
		this.succTotal = succTotal;
	}
	public Long getErrTotal() {
		if( errTotal == null ) {
			errTotal = 0L ;
		}
		return errTotal;
	}
	public void setErrTotal(Long errTotal) {
		this.errTotal = errTotal;
	}
	
	
}
