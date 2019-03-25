package com.idatrix.resource.portal.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by Administrator on 2018/12/27.
 */
@Data
@ApiModel("联系变量时间基础")
public class ContinuousInfoBase{

    @ApiModelProperty("发生时间，格式yyyy-MM-dd")
    private String markTime;

    public ContinuousInfoBase(){}


    public ContinuousInfoBase(String markTime){
        this.markTime = markTime;
    }
}
