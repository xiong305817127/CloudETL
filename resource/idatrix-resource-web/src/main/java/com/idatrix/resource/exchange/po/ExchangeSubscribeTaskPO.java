package com.idatrix.resource.exchange.po;

import com.idatrix.resource.common.utils.DateTools;
import com.idatrix.resource.exchange.vo.request.ExchangeSubscribeVO;

import java.util.Date;

/**
 * Created by Administrator on 2018/11/8.
 */
public class ExchangeSubscribeTaskPO {

    //主键ID
    private Long id;

    /*和rc_subscribe 里面获取的redis一致*/
    private Long seq;

    /*和rc_subscribe 里面生成规则一致*/
    private String subNo;

    /*rc_subscribe里面的id*/
    private Long subscribeId;

    /*资源信息编码*/
    private String resourceCode;

    /*部门信息 订阅部门ID*/
    private Long subscribeDeptId;

    /*部门信息 订阅部门名称*/
    private String subscribeDeptName;

    /*订阅截止日志 一锤子买卖的订阅，暂时无用*/
    private Date endTime;

    private String creator;

    private Date createTime;

    private String modifier;

    private Date modifyTime;



    public ExchangeSubscribeTaskPO(ExchangeSubscribeVO esVO, String user, Long detpId, String deptName){

        this.resourceCode = esVO.getResourceCode();
        this.endTime = DateTools.parseDate(esVO.getEndTime());

        this.subscribeDeptId = detpId;
        this.subscribeDeptName = deptName;

        this.creator = user;
        this.createTime = new Date();
        this.modifier = user;
        this.modifyTime = new Date();
    }




    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSeq() {
        return seq;
    }

    public void setSeq(Long seq) {
        this.seq = seq;
    }

    public String getSubNo() {
        return subNo;
    }

    public void setSubNo(String subNo) {
        this.subNo = subNo;
    }

    public Long getSubscribeId() {
        return subscribeId;
    }

    public void setSubscribeId(Long subscribeId) {
        this.subscribeId = subscribeId;
    }

    public String getResourceCode() {
        return resourceCode;
    }

    public void setResourceCode(String resourceCode) {
        this.resourceCode = resourceCode;
    }

    public Long getSubscribeDeptId() {
        return subscribeDeptId;
    }

    public void setSubscribeDeptId(Long subscribeDeptId) {
        this.subscribeDeptId = subscribeDeptId;
    }

    public String getSubscribeDeptName() {
        return subscribeDeptName;
    }

    public void setSubscribeDeptName(String subscribeDeptName) {
        this.subscribeDeptName = subscribeDeptName;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
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
        return "ExchangeSubscribeTaskPO{" +
                "id=" + id +
                ", seq=" + seq +
                ", subNo='" + subNo + '\'' +
                ", subscribeId=" + subscribeId +
                ", resourceCode='" + resourceCode + '\'' +
                ", subscribeDeptId='" + subscribeDeptId + '\'' +
                ", subscribeDeptName='" + subscribeDeptName + '\'' +
                ", endTime='" + endTime + '\'' +
                ", creator='" + creator + '\'' +
                ", createTime=" + createTime +
                ", modifier='" + modifier + '\'' +
                ", modifyTime=" + modifyTime +
                '}';
    }
}
