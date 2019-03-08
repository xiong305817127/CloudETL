package com.ys.idatrix.db.exception;


/**
 * @ClassName: HadoopSecurityManagerException
 * @Description:
 * @Author: ZhouJian
 * @Date: 2019/3/4
 */
public class HadoopSecurityManagerException extends Exception {

    private static final long serialVersionUID = 1L;

    public HadoopSecurityManagerException(final String message) {
        super(message);
    }

    public HadoopSecurityManagerException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
