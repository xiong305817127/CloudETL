package com.idatrix.resource.catalog.po;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 政务信息资源统计表
 * @Author: Wangbin
 * @Date: 2018/5/23
 */
@Getter
@Setter
public class ResourceStatisticsPO {

    /*主键*/
    private Long id;

    /*订阅次数*/
    private int subCount;

    /*浏览次数*/
    private int visitCount;

    /*数据总量，文件类型计算计算而文件个数*/
    private Long dataCount;

    /*交换数据是总量*/
    private Long shareDataCount;

    /*数据更新时间*/
    private Date dataUpdateTime;

    private String creator;

    private Date createTime;

    private String modifier;

    private Date modifyTime;

    public ResourceStatisticsPO(){
        this.subCount = 0;
        this.visitCount = 0;
        this.shareDataCount = 0L;
        this.dataCount = 0L;
    }
}
