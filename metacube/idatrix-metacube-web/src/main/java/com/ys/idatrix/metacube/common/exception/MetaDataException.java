package com.ys.idatrix.metacube.common.exception;

import lombok.Data;

/**
 * Created by Administrator on 2019/1/15.
 */
@Data
public class MetaDataException extends RuntimeException {

    private String code;

    public MetaDataException() {

    }

    public MetaDataException(String code, String message) {
        super(message);
        this.code = code;
    }

    public MetaDataException(Integer code, String message) {
        super(message);
        this.code = code + "";
    }

    public MetaDataException(String message) {
        super(message);
        this.code = "500";
    }
    
    public MetaDataException(String message, Throwable caus) {
        super(message,caus);
        this.code = "500";
    }
    
    public MetaDataException(Throwable caus) {
        super(caus);
        this.code = "500";
    }

}
