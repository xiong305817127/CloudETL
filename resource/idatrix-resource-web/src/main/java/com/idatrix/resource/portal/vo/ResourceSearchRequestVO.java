package com.idatrix.resource.portal.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName: ResourceSearchRequestVO
 * @Description:
 * @Author: ZhouJian
 * @Date: 2019/1/7
 */
@Data
@ApiModel(value = "SearchRequestVO", description = "资源目录搜索返回数据")
public class ResourceSearchRequestVO {

    @ApiModelProperty(value = "搜索关键字")
    private String keyword;

    @ApiModelProperty("用户名称，如果门户已经登录则传值，否则不传为空")
    private String userName;

    @ApiModelProperty(value = "高亮标签前缀", example = "<em style='color:red'>")
    private String highlightPreTag = "<em style='color:red'>";

    @ApiModelProperty(value = "高亮标签后缀", example = "</em>")
    private String highlightPostTag = "</em>";

    @ApiModelProperty(value = "页码数", example = "1")
    private Integer page = 1;

    @ApiModelProperty(value = "每页大写", example = "10")
    private Integer pageSize = 10;

}
