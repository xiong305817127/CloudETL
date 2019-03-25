package com.idatrix.resource.datareport.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("文件同名检测")
public class PubFileNameVO {

    @ApiModelProperty("资源ID")
	Long resourceId;

    @ApiModelProperty("文件名称")
	String[] pubFileName;

}
