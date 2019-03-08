package com.ys.idatrix.metacube.metamanage.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName: EsMetadataPO
 * @Description:
 * @Author: ZhouJian
 * @Date: 2019/1/23
 */
@ApiModel(value = "EsMetadata", description = "ES元数据实体类，也是主表")
@Data
public class EsMetadataPO extends BasePO {

    @ApiModelProperty("类型名称")
    private String name = "default_type";

    @ApiModelProperty("索引描述")
    private String identification;

    @ApiModelProperty("公开状态：0:不公开 1:授权访问")
    private Integer publicStatus;

    @ApiModelProperty("主题id")
    private Long themeId;

    @ApiModelProperty("标签，可能多个，以，隔开")
    private String tags;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("当前版本号，递增")
    private Integer version;

    @ApiModelProperty("当前状态：0草稿 1生效 2删除 3停止")
    private Integer status;

    @ApiModelProperty("是否开启 0:否 1:是")
    private Boolean isOpen = true;

    @ApiModelProperty("模式id")
    private Long schemaId;

    @ApiModelProperty("索引名称")
    private String schemaName;

    @ApiModelProperty("组织code，多个，以，隔开")
    private String deptCodes;

    @ApiModelProperty("状态:是否禁用 0正常 1禁用")
    private Integer disabled;

    @JsonIgnore
    @ApiModelProperty(value = "最大版本号。冗余", hidden = true)
    private Integer maxVersion;

    @JsonIgnore
    @ApiModelProperty(value = "最大字段位置值。冗余", hidden = true)
    private Integer maxLocation;

    @ApiModelProperty("是否可以切换版本")
    private boolean canSwitch;


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        EsMetadataPO other = (EsMetadataPO) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (themeId == null) {
            if (other.themeId != null) {
                return false;
            }
        } else if (!themeId.equals(other.themeId)) {
            return false;
        }
        if (tags == null) {
            if (other.tags != null) {
                return false;
            }
        } else if (!tags.equals(other.tags)) {
            return false;
        }
        if (remark == null) {
            if (other.remark != null) {
                return false;
            }
        } else if (!remark.equals(other.remark)) {
            return false;
        }
        if (publicStatus == null) {
            if (other.publicStatus != null) {
                return false;
            }
        } else if (!publicStatus.equals(other.publicStatus)) {
            return false;
        }
        return true;
    }

}