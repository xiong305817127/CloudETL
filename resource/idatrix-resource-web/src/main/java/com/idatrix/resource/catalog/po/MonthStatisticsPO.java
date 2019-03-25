package com.idatrix.resource.catalog.po;

import lombok.Data;

import java.util.Date;

/**
 * 月度统计表
 * @Author: Wangbin
 * @Date: 2018/5/23
 */

@Data
public class MonthStatisticsPO {

    /*主键*/
    private Long id;

    /*月度名称yyyymm*/
    private String month;

    /*订阅总次数*/
    private int subCount;

    /*发布资源总量*/
    private int pubCount;

    /*注册资源总量*/
    private int regCount;

    /*组合ID，租户隔离使用*/
    private Long rentId;

    private String creator;

    private Date createTime;

    private String modifier;

    private Date modifyTime;

}
