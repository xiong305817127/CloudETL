package com.idatrix.resource.catalog.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by Robin Wing on 2018-6-13.
 */
@Data
@ApiModel("查询请求")
public class QueryRequestVO {

    @ApiModelProperty("资源名称")
    private String name;

    @ApiModelProperty("资源代码")
    private String code;

    @ApiModelProperty("部门名称")
    private String deptName;

    @ApiModelProperty("部门代码")
    private String deptCode;

    @ApiModelProperty("处理状态")
    private String status;

    @ApiModelProperty("分页起始页")
    private Integer page;

    @ApiModelProperty("分页每页面大小")
    private Integer pageSize;


}
