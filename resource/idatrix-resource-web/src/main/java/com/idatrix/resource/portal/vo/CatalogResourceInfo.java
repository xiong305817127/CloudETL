package com.idatrix.resource.portal.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by Administrator on 2018/12/17.
 */

@Data
@ApiModel("信息资源分类ID和分类个数")
public class CatalogResourceInfo {


    /*资源目录分类ID*/
    @ApiModelProperty("资源目录分类ID")
    private Long id;

    /*目录包含资源个数*/
    @ApiModelProperty("目录包含资源个数")
    private Long count;

    public CatalogResourceInfo(){
        super();
    }

    public CatalogResourceInfo(Long id, Long count){
        this.id = id;
        this.count = count;
    }
}
