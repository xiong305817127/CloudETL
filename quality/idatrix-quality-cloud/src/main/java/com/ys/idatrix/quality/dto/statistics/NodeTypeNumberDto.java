package com.ys.idatrix.quality.dto.statistics;

public class NodeTypeNumberDto {

	private String nodType;
	private Long succTotal;
	private Long errTotal;
	
	public String getNodType() {
		return nodType;
	}
	public void setNodType(String nodType) {
		this.nodType = nodType;
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
