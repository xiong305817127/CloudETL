package com.idatrix.resource.catalog.po;

import java.util.Date;

/**
 * 政务信息资源表
 * @Author: Wangbin
 * @Date: 2018/5/23
 */
public class ResourceConfigPO
{
    /*资源ID（自动增加）*/
    private Long id;

    /*记录目录树里面，最后一个目录节点ID，方便目录树删除节点处理*/
/*    private Long lastCatalogId;*/
    private String catalogFullName;

    /*信息资源分类(来自信息资源分类表)*/
    private String catalogCode;

    /*资源顺序号*/
    private String seqNum;

    /*资源编码，由catalog_code +”/”+ seq_num，自动生成*/
    private String code;

    /*资源名称*/
    private String name;

    /*资源摘要*/
    private String remark;

    /*信息资源提供方名称*/
    private String deptName;

    /*信息资源提供方代码*/
    private String deptCode;

    /*资源提供方ID 字符串*/
    private String deptNameIds;

    /*资源摘要*/
    private String resourceAbstract;

    /*关键字,将来扩展功能*/
    private String keyword;

    /*资源格式大类:1电子文件，2电子表格，3数据库，4图形图像，5流媒体，6自描述格式，7服务接口*/
    private int formatType;

    /*资源格式信息（mysql,oracle,db2,sqlserver，文件，pdf,xls等）*/
    private String formatInfo;

    /*格式补充说明*/
    private String formatInfoExtend;

    /*共享类型（无条件共享、有条件共享、不予共享三类。值域范围对应共享类型排序分别为1、2、3。）*/
    private int shareType;

    /*共享条件，当有条件共享时填写*/
    private String shareCondition;

    /*共享方式：1数据库，2文件下载，3webservice服务*/
    private int shareMethod;

    /*是否向社会开放，1是，0否*/
    private int openType;

    /*对向社会开放资源的条件描述。当“是否向社会开放”取值为 1 时，描述开放条件*/
    private String openCondition;

    /*更新周期（信息资源更新的频度。分为实时1、每日2、每周3、每月4、每季度5、每半年6，每年7等。）*/
    private int refreshCycle;

    /*政务信息资源提供方发布共享、开放政务信息资源的日期CCYY-MM-DD*/
    private Date pubDate;

    /*关联资源代码*/
    private String relationCode;

    /*绑定table的唯一标识id，仅当资源类型为数据库时填写*/
    private Long bindTableId;

    /*绑定服务id，rc_service表主键，仅当资源类型为服务时填写*/
    private Long bindServiceId;

    /*涉及数据类型为数据库时候，需要保存库名ID和表名ID*/
    private String libTableId;

    /*状态(0.草稿 1.退回修改2.注册审核拒绝10.待注册审核19.已注册20.待发布审核22.发布审核拒绝23.取消发布29.发布) */
    private String status;

    private Date createTime;

    private String creator;

    private Date updateTime;

    private String updater;

    public String getLibTableId() {
        return libTableId;
    }

    public void setLibTableId(String libTableId) {
        this.libTableId = libTableId;
    }

    public String getDeptNameIds() {
        return deptNameIds;
    }

    public void setDeptNameIds(String deptNameIds) {
        this.deptNameIds = deptNameIds;
    }

    public String getCatalogFullName() {
        return catalogFullName;
    }

    public void setCatalogFullName(String catalogFullName) {
        this.catalogFullName = catalogFullName;
    }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCatalogCode() {
        return catalogCode;
    }

    public void setCatalogCode(String catalogCode) {
        this.catalogCode = catalogCode;
    }

    public String getSeqNum() {
        return seqNum;
    }

    public void setSeqNum(String seqNum) {
        this.seqNum = seqNum;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getDeptCode() {
        return deptCode;
    }

    public void setDeptCode(String deptCode) {
        this.deptCode = deptCode;
    }

    public String getResourceAbstract() {
        return resourceAbstract;
    }

    public void setResourceAbstract(String resourceAbstract) {
        this.resourceAbstract = resourceAbstract;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public int getFormatType() {
        return formatType;
    }

    public void setFormatType(int formatType) {
        this.formatType = formatType;
    }

    public String getFormatInfo() {
        return formatInfo;
    }

    public void setFormatInfo(String formatInfo) {
        this.formatInfo = formatInfo;
    }

    public String getFormatInfoExtend() {
        return formatInfoExtend;
    }

    public void setFormatInfoExtend(String formatInfoExtend) {
        this.formatInfoExtend = formatInfoExtend;
    }

    public int getShareType() {
        return shareType;
    }

    public void setShareType(int shareType) {
        this.shareType = shareType;
    }

    public String getShareCondition() {
        return shareCondition;
    }

    public void setShareCondition(String shareCondition) {
        this.shareCondition = shareCondition;
    }

    public int getShareMethod() {
        return shareMethod;
    }

    public void setShareMethod(int shareMethod) {
        this.shareMethod = shareMethod;
    }

    public int getOpenType() {
        return openType;
    }

    public void setOpenType(int openType) {
        this.openType = openType;
    }

    public String getOpenCondition() {
        return openCondition;
    }

    public void setOpenCondition(String openCondition) {
        this.openCondition = openCondition;
    }

    public int getRefreshCycle() {
        return refreshCycle;
    }

    public void setRefreshCycle(int refreshCycle) {
        this.refreshCycle = refreshCycle;
    }

    public Date getPubDate() {
        return pubDate;
    }

    public void setPubDate(Date pubDate) {
        this.pubDate = pubDate;
    }

    public String getRelationCode() {
        return relationCode;
    }

    public void setRelationCode(String relationCode) {
        this.relationCode = relationCode;
    }

    public Long getBindTableId() {
        return bindTableId;
    }

    public void setBindTableId(Long bindTableId) {
        this.bindTableId = bindTableId;
    }

    public Long getBindServiceId() {
        return bindServiceId;
    }

    public void setBindServiceId(Long bindServiceId) {
        this.bindServiceId = bindServiceId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getUpdater() {
        return updater;
    }

    public void setUpdater(String updater) {
        this.updater = updater;
    }

    @Override
    public String toString() {
        return "ResourceConfigPO{" +
                "id=" + id +
                ", catalogFullName='" + catalogFullName + '\'' +
                ", catalogCode='" + catalogCode + '\'' +
                ", seqNum='" + seqNum + '\'' +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", remark='" + remark + '\'' +
                ", deptName='" + deptName + '\'' +
                ", deptCode='" + deptCode + '\'' +
                ", deptNameIds='" + deptNameIds + '\'' +
                ", resourceAbstract='" + resourceAbstract + '\'' +
                ", keyword='" + keyword + '\'' +
                ", formatType=" + formatType +
                ", formatInfo='" + formatInfo + '\'' +
                ", formatInfoExtend='" + formatInfoExtend + '\'' +
                ", shareType=" + shareType +
                ", shareCondition='" + shareCondition + '\'' +
                ", shareMethod=" + shareMethod +
                ", openType=" + openType +
                ", openCondition='" + openCondition + '\'' +
                ", refreshCycle=" + refreshCycle +
                ", pubDate=" + pubDate +
                ", relationCode='" + relationCode + '\'' +
                ", bindTableId=" + bindTableId +
                ", bindServiceId=" + bindServiceId +
                ", libTableId='" + libTableId + '\'' +
                ", status='" + status + '\'' +
                ", createTime=" + createTime +
                ", creator='" + creator + '\'' +
                ", updateTime=" + updateTime +
                ", updater='" + updater + '\'' +
                '}';
    }
}
