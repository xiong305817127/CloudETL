package com.ys.idatrix.db.exception;

import lombok.Getter;
import lombok.Setter;

/**
 * @ClassName: DbProxyException
 * @Description:
 * @Author: ZhouJian
 * @Date: 2019/1/2
 */
@Getter
@Setter
public class DbProxyException extends RuntimeException {

    private String sqlCommand;

    public DbProxyException(String message) {
        super(message);
    }

    public DbProxyException(String message, Throwable cause) {
        super(message, cause);
    }

    public DbProxyException(String message, String sqlCommand) {
        super(message);
        this.sqlCommand = sqlCommand;
    }

}
