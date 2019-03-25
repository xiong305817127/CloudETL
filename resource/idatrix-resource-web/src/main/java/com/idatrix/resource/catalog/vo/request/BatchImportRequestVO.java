package com.idatrix.resource.catalog.vo.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by Administrator on 2018/8/1.
 */
@Data
@ApiModel("批量导入请求参数")
public class BatchImportRequestVO {

    @ApiModelProperty("文件名称")
    private String fileName;
}
