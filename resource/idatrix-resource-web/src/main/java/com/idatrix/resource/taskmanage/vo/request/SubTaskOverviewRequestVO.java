package com.idatrix.resource.taskmanage.vo.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 交换任务请求参数
 */
@Data
@ApiModel("交换任务请求参数")
public class SubTaskOverviewRequestVO{

    /*作业名称*/
    @ApiModelProperty("作业名称")
    private String taskName;

    /*资源代码*/
    @ApiModelProperty("资源代码")
    private String code;

    /*订阅方*/
    @ApiModelProperty("订阅方")
    private String subscribeDept;

    /*提供方*/
    @ApiModelProperty("提供方")
    private String provideDept;

    /*任务状态*/
    @ApiModelProperty("任务状态")
    private String taskStatus;

    /*当前页码*/
    @ApiModelProperty("分页起始页")
    private int page;

    /*每页显示数量 (必填)*/
    @ApiModelProperty("每页显示数量")
    private int pageSize;

    SubTaskOverviewRequestVO(){
        super();
        this.page=1;
        this.pageSize=10;
    }

}
