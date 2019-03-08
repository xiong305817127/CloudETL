package com.idatrix.resource.webservice.common;

public class CommonServiceException extends Exception {

	private Integer errorCode;

	private String message;

	public CommonServiceException(Integer errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
		this.message = message;
	}

	public Integer getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(Integer errorCode) {
		this.errorCode = errorCode;
	}

	@Override
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
