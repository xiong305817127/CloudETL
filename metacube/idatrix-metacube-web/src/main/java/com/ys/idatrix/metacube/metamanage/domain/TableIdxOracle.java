package com.ys.idatrix.metacube.metamanage.domain;

import com.ys.idatrix.metacube.common.group.Save;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Date;

@Data
@ApiModel(value = "TableIdxOracle", description = "oracle 表索引实体类")
public class TableIdxOracle {

    @ApiModelProperty("ID")
    private Long id;

    @NotBlank(message = "索引名不能为空", groups = Save.class)
    @ApiModelProperty("索引名")
    private String indexName;

    @ApiModelProperty("索引对应字段ids")
    private String columnIds;

    @NotBlank(message = "索引对应字段names不能为空", groups = Save.class)
    @ApiModelProperty("索引对应字段names")
    private String columnNames;

    @NotBlank(message = "字段对应的排序方式不能为空", groups = Save.class)
    @ApiModelProperty("字段对应的排序方式")
    private String columnSort;

    @NotBlank(message = "索引类型不能为空", groups = Save.class)
    @ApiModelProperty("字段对应的排序方式")
    private String indexType;

    @ApiModelProperty("表空间")
    private String tablespace;

    @ApiModelProperty("模式名")
    private String schemaName;

    @ApiModelProperty("索引位置")
    private Integer location;

    private Long tableId;

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
        TableIdxOracle other = (TableIdxOracle) obj;

        if(id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }

        if(indexName == null) {
            if (other.indexName != null) {
                return false;
            }
        } else if (!indexName.equals(other.indexName)) {
            return false;
        }

        if(indexName == null) {
            if (other.indexName != null) {
                return false;
            }
        } else if (!indexName.equals(other.indexName)) {
            return false;
        }

        if(columnIds == null) {
            if (other.columnIds != null) {
                return false;
            }
        } else if (!columnIds.equals(other.columnIds)) {
            return false;
        }

        if(columnSort == null) {
            if (other.columnSort != null) {
                return false;
            }
        } else if (!columnSort.equals(other.columnSort)) {
            return false;
        }

        if(indexType == null) {
            if (other.indexType != null) {
                return false;
            }
        } else if (!indexType.equals(other.indexType)) {
            return false;
        }

        if(tablespace == null) {
            if (other.tablespace != null) {
                return false;
            }
        } else if (!tablespace.equals(other.tablespace)) {
            return false;
        }

        if(schemaName == null) {
            if (other.schemaName != null) {
                return false;
            }
        } else if (!schemaName.equals(other.schemaName)) {
            return false;
        }

        return true;
    }

}