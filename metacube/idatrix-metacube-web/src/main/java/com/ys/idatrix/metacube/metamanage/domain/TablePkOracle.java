package com.ys.idatrix.metacube.metamanage.domain;

import com.ys.idatrix.metacube.common.group.Update;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Data
@ApiModel(value = "TablePkOracle", description = "oracle 表主键约束实体类")
public class TablePkOracle {

    @NotNull(message = "主键ID不能为空", groups = Update.class)
    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("主键名，如果有主键，则主键名不能为空")
    private String name;

    @ApiModelProperty("序列名，如果需要填充序列，则序列名不能为空")
    private String sequenceName;

    @NotNull(message = "序列状态不能为空")
    @ApiModelProperty("序列状态，1:无主键 2:未填充 3:从新序列填充 4:从已有序列填充")
    private Integer sequenceStatus = 1;

    @ApiModelProperty("表ID")
    private Long tableId;

    private Boolean isDeleted = false;

    private String creator;

    private Date createTime;

    private String modifier;

    private Date modifyTime;

    private List<String> cloumns;

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
        TablePkOracle other = (TablePkOracle) obj;

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

        if(sequenceName == null) {
            if (other.sequenceName != null) {
                return false;
            }
        } else if (!sequenceName.equals(other.sequenceName)) {
            return false;
        }

        if(sequenceStatus == null) {
            if (other.sequenceStatus != null) {
                return false;
            }
        } else if (!sequenceStatus.equals(other.sequenceStatus)) {
            return false;
        }
        return true;
    }
}