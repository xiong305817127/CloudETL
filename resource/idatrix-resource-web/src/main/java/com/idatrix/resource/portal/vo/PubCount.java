package com.idatrix.resource.portal.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 表示资源发布计数
 */
@Data
@ApiModel("信息资源发布计数")
public class PubCount {

    /*所有类型资源总数*/
    @ApiModelProperty("所有类型资源总数")
    private Long total;

    /*文件类型资源数量*/
    @ApiModelProperty("文件类型资源数量")
    private Long fileCount;

    /*共享库类型资源数量*/
    @ApiModelProperty("共享库类型资源数量")
    private Long dbCount;

    /*接口类型资源*/
    @ApiModelProperty("接口类型资源数量")
    private Long interfaceCount;

    public PubCount(){
        super();
    }

    public PubCount(Long total, Long file, Long db, Long interfaceCount){
        this.total = total;
        this.fileCount = file;
        this.dbCount = db;
        this.interfaceCount = interfaceCount;
    }
}
