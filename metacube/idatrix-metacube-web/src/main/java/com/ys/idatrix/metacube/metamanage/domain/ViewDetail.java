package com.ys.idatrix.metacube.metamanage.domain;

import com.ys.idatrix.metacube.common.group.Save;
import com.ys.idatrix.metacube.common.group.Update;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@ApiModel(value = "ViewDetail", description = "视图详情实体类")
@Data
public class ViewDetail {

    @NotNull(message = "视图详细id不能为空", groups = Update.class)
    @ApiModelProperty("id")
    private Long id;

    @NotNull(message = "视图基本信息id不能为空", groups = Update.class)
    @ApiModelProperty("视图基本信息id")
    private Long viewId;

    @NotBlank(message = "视图sql不能为空", groups = Save.class)
    @ApiModelProperty("视图sql")
    private String viewSql;

    // == mysql高级设置部分 ==

    @ApiModelProperty("算法")
    private String arithmetic;

    @ApiModelProperty("定义者")
    private String definiens;

    @ApiModelProperty("安全性")
    private String security;

    @ApiModelProperty("检查选项")
    private String checkOption;

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
        ViewDetail other = (ViewDetail) obj;

        if (this.getId() == null) {
            if (other.getId() != null) {
                return false;
            }
        } else if (!this.getId().equals(other.getId())) {
            return false;
        }

        if (this.viewId == null) {
            if (other.viewId != null) {
                return false;
            }
        } else if (!this.viewId.equals(other.viewId)) {
            return false;
        }

        if (viewSql == null) {
            if (other.viewSql != null) {
                return false;
            }
        } else if (!this.viewSql.equals(other.viewSql)) {
            return false;
        }

        if (this.arithmetic == null) {
            if (other.arithmetic != null) {
                return false;
            }
        } else if (!this.arithmetic.equals(other.arithmetic)) {
            return false;
        }

        if (this.definiens == null) {
            if (other.definiens != null) {
                return false;
            }
        } else if (!this.definiens.equals(other.definiens)) {
            return false;
        }

        if (this.security == null) {
            if (other.security != null) {
                return false;
            }
        } else if (!this.security.equals(other.security)) {
            return false;
        }

        if (this.checkOption == null) {
            if (other.checkOption != null) {
                return false;
            }
        } else if (!this.checkOption.equals(other.checkOption)) {
            return false;
        }

        return true;
    }

}