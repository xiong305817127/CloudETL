package com.ys.idatrix.metacube.metamanage.vo.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ys.idatrix.metacube.common.group.Save;
import com.ys.idatrix.metacube.metamanage.domain.EsFieldPO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @ClassName: EsIndexVO
 * @Description:
 * @Author: ZhouJian
 * @Date: 2019/1/24
 */
@Data
@ApiModel(value = "EsIndexVO", description = "ES索引对象")
public class EsIndexVO extends MetadataBaseVO {

    @ApiModelProperty(value = "索引类型", example = "default_type")
    private String name = "default_type";

    @ApiModelProperty("索引描述")
    @NotBlank(message = "索引描述不能为空", groups = Save.class)
    private String identification;

    @ApiModelProperty(value = "索引字段")
    private List<EsFieldPO> fieldPOList;

    //========================= 页面不做显示 字段 =========================//

    @JsonIgnore
    @ApiModelProperty(value = "最大版本", hidden = true)
    private Integer maxVersion;

    @JsonIgnore
    @ApiModelProperty(value = "字段最大位置", hidden = true)
    private Integer maxLocation;


    //========================= 非持久化，页面不做显示，用户业务处理 字段=========================//

    @JsonIgnore
    @ApiModelProperty(value = "是否变更基本信息", hidden = true)
    private Boolean hasChangeBase;

    @JsonIgnore
    @ApiModelProperty(value = "新增-字段数量", hidden = true)
    private Integer addCnt;

    @JsonIgnore
    @ApiModelProperty(value = "修改-字段数量", hidden = true)
    private Integer updateCnt;

    @JsonIgnore
    @ApiModelProperty(value = "删除-字段数量", hidden = true)
    private Integer deleteCnt;

}
