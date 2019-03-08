package com.idatrix.resource.catalog.vo;

/**
 * Created by Robin Wing on 2018-5-23.
 */
public class ResourceColumnVO {

    /*主键*/
    private Long id;

    /*引用资源标识符id*/
    private Long resourceId;

    /*列名称*/
    private String colName;

    /*数据类型：字符型 C、数值型 N、货币型 Y、日期型 D、日期时间型 T、
    逻辑型 L、备注型 M、通用型 G、双精度型 B、整型 I、浮点型 F*/
    private String colType;

    /*细项顺序码：001-999*/
    private String colSeqNum;

    /*绑定数据库表的列名*/
    private String tableColCode;

    /*绑定数据库表的列类型*/
    private String tableColType;

    /*交换时是否必选：0否，1是*/
    private Boolean requiredFlag;

    /*是否主键: 0否，1是*/
    private Boolean uniqueFlag;

    /*日期类型格式*/
    private String dateFormat;

    public String getColSeqNum() {
        return colSeqNum;
    }

    public void setColSeqNum(String colSeqNum) {
        this.colSeqNum = colSeqNum;
    }

    public String getTableColType() {
        return tableColType;
    }

    public void setTableColType(String tableColType) {
        this.tableColType = tableColType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public String getColName() {
        return colName;
    }

    public void setColName(String colName) {
        this.colName = colName;
    }

    public String getColType() {
        return colType;
    }

    public void setColType(String colType) {
        this.colType = colType;
    }

    public String getTableColCode() {
        return tableColCode;
    }

    public void setTableColCode(String tableColCode) {
        this.tableColCode = tableColCode;
    }

    public Boolean getRequiredFlag() {
        return requiredFlag;
    }

    public void setRequiredFlag(Boolean requiredFlag) {
        this.requiredFlag = requiredFlag;
    }

    public Boolean getUniqueFlag() {
        return uniqueFlag;
    }

    public void setUniqueFlag(Boolean uniqueFlag) {
        this.uniqueFlag = uniqueFlag;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    @Override
    public String toString() {
        return "ResourceColumnVO{" +
                "id=" + id +
                ", resourceId=" + resourceId +
                ", colName='" + colName + '\'' +
                ", colType='" + colType + '\'' +
                ", colSeqNum='" + colSeqNum + '\'' +
                ", tableColCode='" + tableColCode + '\'' +
                ", tableColType='" + tableColType + '\'' +
                ", requiredFlag=" + requiredFlag +
                ", uniqueFlag=" + uniqueFlag +
                ", dateFormat='" + dateFormat + '\'' +
                '}';
    }
}
