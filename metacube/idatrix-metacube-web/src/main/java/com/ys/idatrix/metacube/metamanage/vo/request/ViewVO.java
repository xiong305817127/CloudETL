package com.ys.idatrix.metacube.metamanage.vo.request;

import com.ys.idatrix.metacube.common.group.Save;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @ClassName ViewVO
 * @Description 父类，将共同参数抽取
 * @Author ouyang
 * @Date
 */
@ApiModel(value = "ViewVO", description = "视图实体类")
@Data
public class ViewVO extends MetadataBaseVO {

    @NotBlank(message = "视图名不能为空", groups = Save.class)
    @ApiModelProperty("视图名")
    private String name;

    @NotBlank(message = "视图中文名不能为空", groups = Save.class)
    @ApiModelProperty("视图中文名")
    private String identification;

    @ApiModelProperty("不同数据库下分辨不同资源 如：db 1:表 2:视图")
    private Integer resourceType = 2;
}