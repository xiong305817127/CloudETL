package com.idatrix.resource.portal.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Random;

/**
 * 目录录入填报情况统计
 */

@Data
@ApiModel("目录录入填报情况统计")
public class TypeInStatisticsVO {

    /*基础信息资源目录个数*/
    @ApiModelProperty("基础信息资源目录个数")
    private Long baseLibCount;

    /*主题信息资源目录个数*/
    @ApiModelProperty("主题信息资源目录个数")
    private Long topicLibCount;

    /*部门信息资源目录个数*/
    @ApiModelProperty("主题信息资源目录个数")
    private Long deptLibCount;

    /*当天接收目录提供个数*/
    @ApiModelProperty("当天接收目录提供个数")
    private Long resourceDailyCount;

    /*累计接收资源目录条数*/
    @ApiModelProperty("累计接收资源目录条数")
    private Long resourcePubTotal ;


    /*已经资源填报部门统计*/
    @ApiModelProperty("已经资源填报部门数量")
    private Long deptDailyCount;

    /*系统部门总数*/
    @ApiModelProperty("系统部门总数")
    private Long deptTotal;


//    /*基础信息资源目录个数*/
//    @ApiModelProperty("基础信息资源目录个数")
//    private Long baseLibCount;
//
//    /*累计上线资源目录条数*/
//    @ApiModelProperty("累计上线资源目录条数")
//    private Long resourcePubTotal;
//
//    /*地方部门每天接收资源条数*/
//    @ApiModelProperty("地方部门每天接收资源条数")
//    private Long deptLibDailyCount;
//
//    /*地方部门累计接收资源目录条数*/
//    @ApiModelProperty("地方部门累计接收资源目录条数")
//    private Long deptRegisterTotal;
//
//    /*地方部门累计上线资源目录条数*/
//    @ApiModelProperty("地方部门累计上线资源目录条数")
//    private Long deptPubTotal;

    public TypeInStatisticsVO(){
        super();
    }

//    public TypeInStatisticsVO(Long value){
//
//        Random ran1 = new Random();
//        int valueCount = value.intValue();
//        this.deptCount = new Long(ran1.nextInt(valueCount));
//        this.baseLibCount = new Long(ran1.nextInt(valueCount));
//        this.topicLibCount = new Long(ran1.nextInt(valueCount));
//        this.resourceDailyCount = new Long(ran1.nextInt(valueCount));
//        this.resourceRegisterTotal = new Long(ran1.nextInt(valueCount));
//        this.resourcePubTotal = new Long(ran1.nextInt(valueCount));
//        this.deptLibDailyCount = new Long(ran1.nextInt(valueCount));
//        this.deptRegisterTotal = new Long(ran1.nextInt(valueCount));
//        this.deptPubTotal = new Long(ran1.nextInt(valueCount));
//    }

    public TypeInStatisticsVO(Long value){

        Random ran1 = new Random();
        int valueCount = value.intValue();
        this.baseLibCount = new Long(ran1.nextInt(valueCount));
        this.topicLibCount = new Long(ran1.nextInt(valueCount));
        this.deptLibCount = new Long(ran1.nextInt(valueCount));
        this.resourceDailyCount = new Long(ran1.nextInt(valueCount));
        this.resourcePubTotal = new Long(ran1.nextInt(valueCount));
        this.deptDailyCount = new Long(ran1.nextInt(valueCount));
        this.deptTotal = new Long(ran1.nextInt(valueCount));
    }

}
