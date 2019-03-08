package com.idatrix.unisecurity.ranger.common.vo;

public class RangerBaseVO {
	private Integer statusCode;
	private String msgDesc;
	private String msgName;

	public String getMsgName() {
		return msgName;
	}

	public void setMsgName(String msgName) {
		this.msgName = msgName;
	}

	public Integer getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(Integer statusCode) {
		this.statusCode = statusCode;
	}

	public String getMsgDesc() {
		return msgDesc;
	}

	public void setMsgDesc(String msgDesc) {
		this.msgDesc = msgDesc;
	}

	@Override
	public String toString() {
		return "RangerBaseVO [statusCode=" + statusCode + ", msgDesc="
				+ msgDesc + ", msgName=" + msgName + "]";
	}

}
