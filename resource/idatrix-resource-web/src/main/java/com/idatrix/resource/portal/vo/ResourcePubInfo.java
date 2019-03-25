package com.idatrix.resource.portal.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * Created by Administrator on 2018/12/17.
 */
@Data
@ApiModel("资源目录发布信息")
public class ResourcePubInfo {

    /*部门信息*/
    @ApiModelProperty(value="部门信息")
    private String deptName;

    /*发布时间*/
    @ApiModelProperty(value="发布时间,如：2018年12月15日17时20分10秒")
    @DateTimeFormat(pattern="yyyy年MM月dd日HH时mm分ss秒")
    @JsonFormat(pattern="yyyy年MM月dd日HH时mm分ss秒",timezone = "GMT+8")
    private Date pubTime;

    /*资源类型: 文件、库表、接口*/
    @ApiModelProperty(value="资源类型: 文件、库表、接口")
    private String resourceType;


    /*具体数据格式，其中 3 表示数据库，7 表示接口，1,2,4,5,6 表示文件*/
    @ApiModelProperty(hidden = true)
    private int formatType;


    public ResourcePubInfo(){
        super();
    }

    public ResourcePubInfo(String deptName, Date pubTime, String resourceType){
        this.deptName = deptName;
        this.pubTime = pubTime;
        this.resourceType = resourceType;
    }
}
