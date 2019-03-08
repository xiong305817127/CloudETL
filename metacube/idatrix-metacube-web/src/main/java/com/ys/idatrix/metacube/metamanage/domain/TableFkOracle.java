package com.ys.idatrix.metacube.metamanage.domain;

import com.ys.idatrix.metacube.common.group.Save;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@ApiModel(value = "TableFkOracle", description = "oracle 表外键约束实体类")
@Data
public class TableFkOracle {

    @ApiModelProperty("ID")
    private Long id;

    @NotBlank(message = "外键名不能为空", groups = Save.class)
    @ApiModelProperty("外键名")
    private String name;

    @ApiModelProperty("关联字段")
    private String columnIds;

    @NotBlank(message = "关联字段不能为空", groups = Save.class)
    @ApiModelProperty("关联字段names")
    private String columnNames;

    @NotNull(message = "参考模式id不能为空", groups = Save.class)
    @ApiModelProperty("参考模式id")
    private Long referenceSchemaId;

    @NotNull(message = "参考表id不能为空", groups = Save.class)
    @ApiModelProperty("参考表id")
    private Long referenceTableId;

    @NotNull(message = "参考约束id不能为空", groups = Save.class)
    @ApiModelProperty("参考约束id")
    private Long referenceRestrain;

    @NotNull(message = "参考约束类型不能为空", groups = Save.class)
    @ApiModelProperty("参考约束类型")
    private Integer referenceRestrainType;

    @ApiModelProperty("参考约束字段id")
    private String referenceColumn;

    @ApiModelProperty("删除时触发事件")
    private String deleteTrigger;

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

    @ApiModelProperty("参考的模式名")
    private String referenceSchemaName;

    @ApiModelProperty("参考的表名")
    private String referenceTableName;

    @ApiModelProperty("参考的约束名")
    private String referenceRestrainName;

    @NotBlank(message = "参考约束字段不能为空", groups = Save.class)
    @ApiModelProperty("参考的字段名，可能有多个")
    private String referenceColumnNames;

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

        TableFkOracle other = (TableFkOracle) obj;

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

        if(columnIds == null) {
            if (other.columnIds != null) {
                return false;
            }
        } else if (!columnIds.equals(other.columnIds)) {
            return false;
        }

        if(referenceSchemaId == null) {
            if (other.referenceSchemaId != null) {
                return false;
            }
        } else if (!referenceSchemaId.equals(other.referenceSchemaId)) {
            return false;
        }

        if(referenceTableId == null) {
            if (other.referenceTableId != null) {
                return false;
            }
        } else if (!referenceTableId.equals(other.referenceTableId)) {
            return false;
        }

        if(referenceRestrain == null) {
            if (other.referenceRestrain != null) {
                return false;
            }
        } else if (!referenceRestrain.equals(other.referenceRestrain)) {
            return false;
        }

        if(referenceRestrainType == null) {
            if (other.referenceRestrainType != null) {
                return false;
            }
        } else if (!referenceRestrainType.equals(other.referenceRestrainType)) {
            return false;
        }

        if(referenceColumn == null) {
            if (other.referenceColumn != null) {
                return false;
            }
        } else if (!referenceColumn.equals(other.referenceColumn)) {
            return false;
        }

        if(deleteTrigger == null) {
            if (other.deleteTrigger != null) {
                return false;
            }
        } else if (!deleteTrigger.equals(other.deleteTrigger)) {
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