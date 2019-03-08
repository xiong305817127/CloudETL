package com.idatrix.resource.webservice.vo;

import com.idatrix.resource.webservice.dto.ParamDTO;

import java.util.List;

/**
 * 输入参数对象
 */
public class InputParamVO {

    private List<ParamDTO> inputParams;

    private String subscribeKey;


    private Integer pageNum;


    private Integer pageSize;

    public InputParamVO(List<ParamDTO> inputParams, String subscribeKey, Integer pageNum, Integer pageSize) {
        this.inputParams = inputParams;
        this.subscribeKey = subscribeKey;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }

    public List<ParamDTO> getInputParams() {
        return inputParams;
    }

    public void setInputParams(List<ParamDTO> inputParams) {
        this.inputParams = inputParams;
    }

    public String getSubscribeKey() {
        return subscribeKey;
    }

    public void setSubscribeKey(String subscribeKey) {
        this.subscribeKey = subscribeKey;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    @Override
    public String toString() {
        return "InputParamVO{" +
                "inputParams=" + inputParams +
                ", subscribeKey='" + subscribeKey + '\'' +
                ", pageNum=" + pageNum +
                ", pageSize=" + pageSize +
                '}';
    }
}
