package com.idatrix.resource.datareport.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 数据上报详细信息表
 * @Description： 数据上报时,上传数据所生成的信息
 * @Date: 2018/06/11
 */
@Data
@ApiModel("数据上报详细信息表")
public class DataUploadTotalVO {
	/* 资源ID */
	@ApiModelProperty("资源ID")
	private Long resourceId;

	/* 数据批次 */
	@ApiModelProperty("数据批次")
	private String dataBatch;

	/* 数据类别 DB/FILE */
    @ApiModelProperty("数据类别 DB/FILE")
    private String dataType;

	/* 数据上报细节信息 */
    @ApiModelProperty("数据上报细节信息 ")
    private List<DataUploadDetailVO> dataUploadDetailVOList;

}
