package com.ys.idatrix.metacube.metamanage.domain;

import com.ys.idatrix.metacube.common.group.Save;
import com.ys.idatrix.metacube.common.group.Update;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @ClassName: EsSnapshotFieldPO
 * @Description:
 * @Author: ZhouJian
 * @Date: 2019/1/23
 */
@Data
@ApiModel(value = "EsSnapshotFieldPO", description = "索引字段快照实体")
public class EsSnapshotFieldPO extends BasePO {

    @ApiModelProperty("索引主表Id")
    private Long indexId;

    @ApiModelProperty("版本号")
    private Integer version;

    @NotBlank(message = "字段类型不能为空", groups = {Save.class, Update.class})
    @ApiModelProperty("字段名称")
    private String fieldName;

    @NotBlank(message = "字段类型不能为空", groups = {Save.class, Update.class})
    @ApiModelProperty("字段类型")
    private String fieldType;

    @ApiModelProperty("分词器名称")
    private String analyzer;

    @ApiModelProperty("是否分词")
    private Boolean canIndex;

    @ApiModelProperty("是否存储")
    private Boolean canStore;

    @ApiModelProperty("是否包含在_all中")
    private Boolean canAll;

    @ApiModelProperty("是否包含在_source中")
    private Boolean canSource;

    @ApiModelProperty("字段存放位置")
    private Integer location;

}
