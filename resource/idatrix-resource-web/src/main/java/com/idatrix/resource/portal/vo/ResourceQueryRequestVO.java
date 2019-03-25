package com.idatrix.resource.portal.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 门户资源目录请求条件
 */

@Data
@ApiModel("门户资源目录请求条件")
public class ResourceQueryRequestVO {

    /*查询关键字*/
    @ApiModelProperty("查询关键字，为空表示所有")
    private String queryKeyWord;

    /* 资源分类id */
    @ApiModelProperty("资源分类id")
    private Long catalogId;

    /* 资源分类id */
    @ApiModelProperty("资源分类名称，该配置主要用于门户首页几个配置点击")
    private String catalogName;

    /* 资源分类编码 */
    @ApiModelProperty(value = "资源分类编码",hidden =true)
    private String catalogCode;

    /*根据资源类型 db/file/interface*/
    @ApiModelProperty("搜索所有数据库类型资源，值设置成1")
    private String dbTypeFlag;

    @ApiModelProperty("搜索所有文件类型资源，值设置成1")
    private String fileTypeFlag;

    @ApiModelProperty("搜索所有接口类型资源，值设置成1")
    private String interfaceTypeFlag;

    @ApiModelProperty("按照更新时间排序，0表示不采用，1表示降序，2表示升序")
    private String timeFlag;

    @ApiModelProperty("按照浏览量排序，0表示不采用，1表示降序，2表示升序")
    private String visitCountFlag;

    @ApiModelProperty("按照订阅量排序，0表示不采用，1表示降序，2表示升序")
    private String subCountFlag;

    /*租户ID*/
    @ApiModelProperty(value = "租户ID",hidden=true)
    private Long rentId;

    /*租户ID*/
    @ApiModelProperty(value = "资源类型，用于查询如（1,2,3）",hidden=true)
    private String resourceType;

    @ApiModelProperty("用户名称，如果门户已经登录则传值，否则不传为空")
    private String userName;

    /*第几页*/
    private Integer page;

    /*页面大小*/
    private Integer pageSize;

}
