package com.ys.idatrix.metacube.metamanage.domain;

import com.ys.idatrix.metacube.common.group.Save;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Date;

@ApiModel(value = "TableColumn", description = "表字段实体类")
@Data
public class TableColumn {

    @ApiModelProperty("id")
    private Long id;

    @NotBlank(message = "字段名不能为空", groups = Save.class)
    @ApiModelProperty("字段名")
    private String columnName;

    @NotBlank(message = "字段类型不能为空", groups = Save.class)
    @ApiModelProperty("字段类型")
    private String columnType;

    @ApiModelProperty("类型长度")
    private String typeLength;

    @ApiModelProperty("类型精度")
    private String typePrecision;

    @ApiModelProperty("是否为主键 0:否,1:是")
    private Boolean isPk = false;

    @ApiModelProperty("是否自增")
    private Boolean isAutoIncrement = false;

    @ApiModelProperty("是否允许为空 0:否 1:是")
    private Boolean isNull = true;

    @ApiModelProperty("默认值")
    private String defaultValue;

    @ApiModelProperty("是否为无符号")
    private Boolean isUnsigned = false;

    @ApiModelProperty("字段描述")
    private String description;

    @ApiModelProperty("字段位置（当前字段的一个位置标识，每张表的字段位置都是自增，唯一的）")
    private Integer location;

    @ApiModelProperty("表id")
    private Long tableId;

    @ApiModelProperty("创建人")
    private String creator;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("修改人")
    private String modifier;

    @ApiModelProperty("修改时间")
    private Date modifyTime;

    @ApiModelProperty("是否删除，0:否 1:是")
    private Boolean isDeleted = false;

    @ApiModelProperty("0：默认值什么也不做 1：新建 2：修改 3：删除")
    private Integer status = 0;

    @ApiModelProperty("是否是分区字段,分区字段和表字段不能相同-HIVE使用")
    private Boolean isPartition;

    @ApiModelProperty("分区序列,多个分区字段时候,需要记录顺序-HIVE使用")
    private Integer indexPartition;

    @ApiModelProperty("是否是同字段-HIVE使用")
    private Boolean isBucket;

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
        TableColumn other = (TableColumn) obj;

        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }

        if (columnName == null) {
            if (other.columnName != null) {
                return false;
            }
        } else if (!columnName.equals(other.columnName)) {
            return false;
        }

        if (columnType == null) {
            if (other.columnType != null) {
                return false;
            }
        } else if (!columnType.equals(other.columnType)) {
            return false;
        }

        if (typeLength == null) {
            if (other.typeLength != null) {
                return false;
            }
        } else if (!typeLength.equals(other.typeLength)) {
            return false;
        }

        if (typePrecision == null) {
            if (other.typePrecision != null) {
                return false;
            }
        } else if (!typePrecision.equals(other.typePrecision)) {
            return false;
        }

        if (isAutoIncrement == null) {
            if (other.isAutoIncrement != null) {
                return false;
            }
        } else if (!isAutoIncrement.equals(other.isAutoIncrement)) {
            return false;
        }

        if (isNull == null) {
            if (other.isNull != null) {
                return false;
            }
        } else if (!isNull.equals(other.isNull)) {
            return false;
        }

        if (defaultValue == null) {
            if (other.defaultValue != null) {
                return false;
            }
        } else if (!defaultValue.equals(other.defaultValue)) {
            return false;
        }

        if (isUnsigned == null) {
            if (other.isUnsigned != null) {
                return false;
            }
        } else if (!isUnsigned.equals(other.isUnsigned)) {
            return false;
        }

        if (description == null) {
            if (other.description != null) {
                return false;
            }
        } else if (!description.equals(other.description)) {
            return false;
        }

        if (tableId == null) {
            if (other.tableId != null) {
                return false;
            }
        } else if (!tableId.equals(other.tableId)) {
            return false;
        }

        return true;
    }

}