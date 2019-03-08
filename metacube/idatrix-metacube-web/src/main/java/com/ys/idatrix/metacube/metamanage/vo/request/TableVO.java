package com.ys.idatrix.metacube.metamanage.vo.request;

import com.ys.idatrix.metacube.common.group.Save;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @ClassName TableVO
 * @Description Table 主类
 * @Author ouyang
 * @Date
 */
@ApiModel(value = "SearchVO", description = "搜索Vo")
@Data
public class TableVO extends MetadataBaseVO {

    @ApiModelProperty("实体表名")
    @NotBlank(message = "实体表名不能为空", groups = Save.class)
    private String name;

    @ApiModelProperty("中文表名")
    @NotBlank(message = "中文表名不能为空", groups = Save.class)
    private String identification;

    // @NotNull(message = "资源类型不能为空")
    @ApiModelProperty("不同数据库下分辨不同资源 如：db 1:表 2:视图")
    private Integer resourceType;
}
