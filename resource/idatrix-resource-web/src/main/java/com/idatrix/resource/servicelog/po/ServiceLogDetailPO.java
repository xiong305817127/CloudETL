package com.idatrix.resource.servicelog.po;

import java.util.Arrays;
import java.util.Date;

public class ServiceLogDetailPO {
    /*主键*/
    private Long id;

    /*服务日志关联ID*/
    private Long parentId;

    /*输入参数JSON*/
    private byte input[];

    /*输出结果JSON*/
    private byte output[];

    /*错误信息:输入参数校验失败，无调用权限，调用失败*/
    private String errorMessage;

    /*错误堆栈*/
    private byte errorStack[];

    private String creator;

    private Date createTime;

    private String modifier;

    private Date modifyTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public byte[] getInput() {
        return input;
    }

    public void setInput(byte[] input) {
        this.input = input;
    }

    public byte[] getOutput() {
        return output;
    }

    public void setOutput(byte[] output) {
        this.output = output;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public byte[] getErrorStack() {
        return errorStack;
    }

    public void setErrorStack(byte[] errorStack) {
        this.errorStack = errorStack;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    @Override
    public String toString() {
        return "ServiceLogDetailPO{" +
                "id=" + id +
                ", parentId=" + parentId +
                ", input=" + Arrays.toString(input) +
                ", output=" + Arrays.toString(output) +
                ", errorMessage='" + errorMessage + '\'' +
                ", errorStack=" + Arrays.toString(errorStack) +
                ", creator='" + creator + '\'' +
                ", createTime=" + createTime +
                ", modifier='" + modifier + '\'' +
                ", modifyTime=" + modifyTime +
                '}';
    }
}
