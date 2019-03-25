package com.idatrix.resource.catalog.vo.request;

import com.idatrix.resource.common.vo.BaseRequestParamVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 资源目录搜索VO
 *
 * @author wzl
 */
@Data
@ApiModel("资源目录搜索请求")
public class ResourceCatalogSearchVO extends BaseRequestParamVO {

    /**
     * 资源分类id
     */
    @ApiModelProperty("资源分类id")
    private Long catalogId;
    /**
     * 资源分类编码
     */
    @ApiModelProperty(value = "资源分类编码",hidden = true)
    private String catalogCode;
    /**
     * 资源名称
     */
    @ApiModelProperty("资源名称")
    private String name;
    /**
     * 资源编码
     */
    @ApiModelProperty("资源编码")
    private String code;
    /**
     * 资源提供方名称
     */
    @ApiModelProperty("资源提供方名称")
    private String deptName;

    /**
     * 资源提供方代码
     */
    @ApiModelProperty("资源提供方代码")
    private String deptCode;
    /**
     * 创建者
     */
    @ApiModelProperty("创建者")
    private String creator;
    /**
     * 资源状态
     */
    @ApiModelProperty("资源状态")
    private String status;

    /**
     * 资源id列表
     */
    @ApiModelProperty(value = "资源id列表", hidden = true)
    private List<Long> resourceIds;

    /**
     * 全文搜索关键字
     */
    @ApiModelProperty("全文搜索关键字")
    private String keyword;

    /*根据租户ID*/
    @ApiModelProperty(value = "根据租户ID",hidden = true)
    private Long rentId;

}
