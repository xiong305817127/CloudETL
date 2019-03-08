package com.idatrix.resource.catalog.es.exception;

/**
 * ES查询异常
 *
 * @author wzl
 */
public class EsSearchException extends RuntimeException {

    public EsSearchException() {
        super();
    }

    public EsSearchException(String msg) {
        super(msg);
    }
}
