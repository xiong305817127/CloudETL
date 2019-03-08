package com.idatrix.resource.webservice.dto;

import java.io.Serializable;
import java.util.List;

public class ResultDTO implements Serializable {

    private static final long serialVersionUID = -1744814498306631537L;

    /*200为正确状态， 其余状态均为错误状态*/
    private Integer statusCode;

    /*正确状态时为空， 错误状态时包含错误信息*/
    private String errorMsg;


    /*返回数据的总记录条数*/
    private Long totalSize;

    /*页面序号*/
    private Long pageNum;

    /*执行的sql语句*/
//    private String sql;

    /*执行返回结果*/
//    @XmlJavaTypeAdapter(MapXmlAdapter.class)
//    private List<Map<String, Object>> data;
//    private List<AdapterMap> data;
//    private List<List<Object>> data;
    private String data;

    /*返回数据的所有字段*/
    private List<String> columns;




    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public List<String> getColumns() {
        return columns;
    }

    public void setColumns(List<String> columns) {
        this.columns = columns;
    }

    public Long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(Long totalSize) {
        this.totalSize = totalSize;
    }

    public Long getPageNum() {
        return pageNum;
    }

    public void setPageNum(Long pageNum) {
        this.pageNum = pageNum;
    }

    @Override
    public String toString() {
        return "ResultDTO{" +
                "totalSize=" + totalSize +
                ", pageNum=" + pageNum +
                ", statusCode=" + statusCode +
                ", errorMsg='" + errorMsg + '\'' +
                ", data=" + data +
                ", columns=" + columns +
                '}';
    }
}
