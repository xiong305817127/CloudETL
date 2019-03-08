package com.idatrix.resource.subscribe.po;

import java.util.Date;

/**
 * Created by Administrator on 2018/7/16.
 */
public class SubscribeDbioPO {

    /*主键*/
    private Long id;

    /*订阅ID*/
    private Long subscribeId;

    /*参数类型 input,output, input 表示订阅资源，output表示搜索条件*/
    private String paramType;

    /*信息项ID*/
    private Long columnId;

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
}
