package com.ys.idatrix.metacube.metamanage.vo.request;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class DirectMiningSaveVO {
	
	  @ApiModelProperty("模式id")
	  @NotNull(message = "模式不能为空")
	  private Long schemaId;
	
	  @ApiModelProperty("批量直采的表或者视图列表,包含名称,备注等信息")
	  private MetadataBaseVO[] metadataBase ;
	  
	  @ApiModelProperty("数据库类型不能为空，1:mysql 2:oracle 3:...")
	  @NotNull(message = "数据库类型不能为空")
	  private Integer databaseType;
	  
}
