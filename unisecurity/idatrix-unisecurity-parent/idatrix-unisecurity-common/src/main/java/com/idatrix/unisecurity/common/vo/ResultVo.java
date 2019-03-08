package com.idatrix.unisecurity.common.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;

/**
 * security常用的VO
 */
@Data
public class ResultVo<T> implements Serializable {

    private String code;

    private String msg;

    private T data;

    @JsonIgnore
    public String getMessage() {
        return msg;
    }
}