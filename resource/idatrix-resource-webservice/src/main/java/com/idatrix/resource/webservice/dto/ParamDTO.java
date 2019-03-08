package com.idatrix.resource.webservice.dto;

import java.io.Serializable;


public class ParamDTO implements Serializable {

    private static final long serialVersionUID = -2171911070654676423L;

    /*数据库字段名称*/
    private String paramCode;

    /*信息资源中描述名称*/
    private String paramName;

    /*字段数值*/
    private Object paramValue;

    public String getParamCode() {
        return paramCode;
    }

    public void setParamCode(String paramCode) {
        this.paramCode = paramCode;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public Object getParamValue() {
        return paramValue;
    }

    public void setParamValue(Object paramValue) {
        this.paramValue = paramValue;
    }

    @Override
    public String toString() {
        return "ParamDTO{" +
                "paramCode='" + paramCode + '\'' +
                ", paramName='" + paramName + '\'' +
                ", paramValue=" + paramValue +
                '}';
    }
}
