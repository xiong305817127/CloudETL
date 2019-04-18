package com.ys.idatrix.quality.dto.statistics;

public class ReferenceNumberDto {

	private String referenceValue;
	private Long dataTotal;
	private Long taskTotal;
	
	public String getReferenceValue() {
		return referenceValue;
	}
	public void setReferenceValue(String referenceValue) {
		this.referenceValue = referenceValue;
	}
	public Long getDataTotal() {
		if( dataTotal == null ) {
			dataTotal = 0L ;
		}
		return dataTotal;
	}
	public void setDataTotal(Long dataTotal) {
		this.dataTotal = dataTotal;
	}
	public Long getTaskTotal() {
		if( taskTotal == null ) {
			taskTotal = 0L ;
		}
		return taskTotal;
	}
	public void setTaskTotal(Long taskTotal) {
		this.taskTotal = taskTotal;
	}
	
	
}
