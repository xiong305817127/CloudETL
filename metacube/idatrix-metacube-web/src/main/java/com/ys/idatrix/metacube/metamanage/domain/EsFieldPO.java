package com.ys.idatrix.metacube.metamanage.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ys.idatrix.metacube.common.group.Save;
import com.ys.idatrix.metacube.common.group.Update;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @ClassName: EsIndexFieldPO
 * @Description:
 * @Author: ZhouJian
 * @Date: 2019/1/23
 */
@Data
@ApiModel(value = "EsFieldPO", description = "索引字段实体")
public class EsFieldPO extends BasePO {

    @ApiModelProperty("索引主表Id")
    private Long indexId;

    @NotBlank(message = "字段类型不能为空", groups = {Save.class, Update.class})
    @ApiModelProperty("字段名称")
    private String fieldName;

    @NotBlank(message = "字段类型不能为空", groups = {Save.class, Update.class})
    @ApiModelProperty("字段类型")
    private String fieldType;

    @ApiModelProperty("分析器名称")
    private String analyzer;

    @ApiModelProperty("是否索引")
    private Boolean canIndex = true;

    @ApiModelProperty("是否存储")
    private Boolean canStore = false;

    @ApiModelProperty("是否包含在_all中")
    private Boolean canAll = true;

    @ApiModelProperty("是否包含在_source中")
    private Boolean canSource = false;

    @JsonIgnore
    @ApiModelProperty(value = "字段存放位置",hidden = true)
    private Integer location;

    @ApiModelProperty("修改操作,标识字段 0=不变,1-新增,2修改,3-删除")
    private Integer status = 0;

}
