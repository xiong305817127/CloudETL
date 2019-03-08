package com.idatrix.resource.webservice.po;

import java.util.Date;

/**
 * Created by Administrator on 2018/7/16.
 */
public class SubscribeDbioPO {

    /*主键*/
    private Long id;

    /*订阅ID*/
    private Long subscribeId;

    /*参数类型 input,output*/
    private String paramType;

    /*信息项ID*/
    private Long columnId;

    /*信息项名称*/
    private String colName;

    /*信息项编码（即物理表名称）*/
    private String tableColCode;

    /*信息项类型*/
    private String tableColType;

    /*创建人*/
    private String creator;

    /*创建时间*/
    private Date createTime;

    /*修改人*/
    private String modifier;

    /*修改时间*/
    private Date modifyTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSubscribeId() {
        return subscribeId;
    }

    public void setSubscribeId(Long subscribeId) {
        this.subscribeId = subscribeId;
    }

    public String getParamType() {
        return paramType;
    }

    public void setParamType(String paramType) {
        this.paramType = paramType;
    }

    public Long getColumnId() {
        return columnId;
    }

    public void setColumnId(Long columnId) {
        this.columnId = columnId;
    }

    public String getColName() {
        return colName;
    }

    public void setColName(String colName) {
        this.colName = colName;
    }

    public String getTableColCode() {
        return tableColCode;
    }

    public void setTableColCode(String tableColCode) {
        this.tableColCode = tableColCode;
    }

    public String getTableColType() {
        return tableColType;
    }

    public void setTableColType(String tableColType) {
        this.tableColType = tableColType;
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
        return "SubscribeDbioPO{" +
                "id=" + id +
                ", subscribeId=" + subscribeId +
                ", paramType='" + paramType + '\'' +
                ", columnId=" + columnId +
                ", colName='" + colName + '\'' +
                ", tableColCode='" + tableColCode + '\'' +
                ", tableColType='" + tableColType + '\'' +
                ", creator='" + creator + '\'' +
                ", createTime=" + createTime +
                ", modifier='" + modifier + '\'' +
                ", modifyTime=" + modifyTime +
                '}';
    }
}
