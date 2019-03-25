package com.idatrix.resource.portal.po;

import lombok.Data;

import java.util.Date;

/**
 * Created by Administrator on 2018/12/22.
 */

@Data
public class ResourceQueryPO {

    /*资源ID*/
    private Long resourceId;

    /*资源名称*/
    private String resourceName;

    /*资源类型 type: db/file/interface*/
    private Long formatType;

    /*资源提供方名称*/
    private String provideDeptName;

    /*资源摘要*/
    private String resourceRemark;

    /*资源所属分类*/
    private String catalogFullName;

    /*最近更新时间 YYYY-MM-DD HH:MM:SS*/
    private Date updateTime;

    /*共享属性：共享类型（无条件共享、有条件共享、不予共享三类。值域范围对应共享类型排序分别为1、2、3。）*/
    private int shareType;

    /*开放属性：是否开放*/
    private int openType;

    /*访问量*/
    private Long visitCount;

    /*申请量*/
    private Long subCount;
}
