package com.ys.idatrix.metacube.metamanage.domain;

import com.ys.idatrix.metacube.common.group.Save;
import com.ys.idatrix.metacube.common.group.Update;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@ApiModel(value = "Theme", description = "主题实体类")
@Data
public class Theme {

    @ApiModelProperty("id")
    @NotNull(message = "id不能为空", groups = Update.class)
    private Long id;

    @ApiModelProperty("主题名")
    @NotBlank(message = "主题名不能为空", groups = Save.class)
    private String name;

    @ApiModelProperty("主题代码")
    @NotBlank(message = "主题代码不能为空", groups = Save.class)
    private String themeCode;

    @ApiModelProperty("当前主题使用次数")
    private Integer useCount;

    @ApiModelProperty("租户id")
    private Long renterId;

    @ApiModelProperty("创建人")
    private String creator;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("修改人")
    private String modifier;

    @ApiModelProperty("修改时间")
    private Date modifyTime;

    @ApiModelProperty("是否删除")
    private Boolean isDeleted;

}