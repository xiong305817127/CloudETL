package com.ys.idatrix.cloudetl.security;

public class HadoopSecurityManagerException extends Exception {

	private static final long serialVersionUID = 1L;

	public HadoopSecurityManagerException(final String message) {
		super(message);
	}

	public HadoopSecurityManagerException(final String message, final Throwable cause) {
		super(message, cause);
	}
}
