package com.ys.idatrix.metacube.metamanage.domain;

import com.ys.idatrix.metacube.common.group.Save;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Date;

@ApiModel(value = "TableChOracle", description = "oracle 表检查约束实体类")
@Data
public class TableChOracle {

    @ApiModelProperty("ID")
    private Long id;

    @NotBlank(message = "检查约束名不能为空", groups = Save.class)
    @ApiModelProperty("检查约束名")
    private String name;

    @NotBlank(message = "检查sql不能为空", groups = Save.class)
    @ApiModelProperty("检查sql")
    private String checkSql;

    @ApiModelProperty("是否启用")
    private Boolean isEnabled = true;

    @ApiModelProperty("表id")
    private Long tableId;

    @ApiModelProperty("位置")
    private Integer location;

    private Boolean isDeleted = false;

    private String creator;

    private Date createTime;

    private String modifier;

    private Date modifyTime;

    @ApiModelProperty("0：默认值什么也不做 1：新建 2：修改 3：删除")
    private Integer status = 0;

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

        TableChOracle other = (TableChOracle) obj;

        if(id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }

        if(name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }

        if(checkSql == null) {
            if (other.checkSql != null) {
                return false;
            }
        } else if (!checkSql.equals(other.checkSql)) {
            return false;
        }

        if(isEnabled == null) {
            if (other.isEnabled != null) {
                return false;
            }
        } else if (!isEnabled.equals(other.isEnabled)) {
            return false;
        }

        return true;
    }
}