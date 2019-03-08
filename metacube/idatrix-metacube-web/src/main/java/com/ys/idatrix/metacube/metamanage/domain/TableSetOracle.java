package com.ys.idatrix.metacube.metamanage.domain;

import com.ys.idatrix.metacube.common.group.Update;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;

@ApiModel(value = "TableSetOracle", description = "oracle 表设置实体类")
@Data
public class TableSetOracle {

    @NotNull(message = "主键ID不能为空", groups = Update.class)
    @ApiModelProperty("id")
    private Long id;

    @NotNull(message = "表空间不能为空", groups = Update.class)
    @ApiModelProperty("表空间")
    private String tablespace;

    @ApiModelProperty("表id")
    private Long tableId;

    private String creator;

    private Date createTime;

    private String modifier;

    private Date modifyTime;
}