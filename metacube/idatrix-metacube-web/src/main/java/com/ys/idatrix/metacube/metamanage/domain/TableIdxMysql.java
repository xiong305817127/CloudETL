package com.ys.idatrix.metacube.metamanage.domain;

import com.ys.idatrix.metacube.common.enums.DBEnum;
import com.ys.idatrix.metacube.common.group.Save;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Date;

@ApiModel(value = "TableIdxMysql", description = "mysql索引实体类")
@Data
public class TableIdxMysql {

    @ApiModelProperty("id")
    private Long id;

    @NotBlank(message = "索引名不能为空", groups = Save.class)
    @ApiModelProperty("索引名")
    private String indexName;

    @ApiModelProperty("当前索引关联的字段，可能多个，以，隔开（因为可能关联数据库中还不存在的字段，所以传列名，列id后台处理）")
    private String columnIds;

    // @NotBlank(message = "索引关联字段不能为空", groups = Save.class)
    @ApiModelProperty("当前索引关联的字段name，可能多个，以，隔开")
    private String columnNames;

    // @NotNull(message = "字段对应的子部分不能为空")
    @ApiModelProperty("子部分，对应字段，可以有多个")
    private String subdivision;

    @ApiModelProperty("索引类型")
    private String indexType = DBEnum.MysqlIndexTypeEnum.NORMAL.name();

    @ApiModelProperty("索引方法")
    private String indexMethod = DBEnum.MysqlIndexMethodEnum.BTREE.getName();

    @ApiModelProperty("索引位置（当前字段的一个位置标识，每张表的索引位置都是自增，唯一的）")
    private Integer location;

    @ApiModelProperty("表id")
    private Long tableId;

    private String creator;

    private Date createTime;

    private String modifier;

    private Date modifyTime;

    @ApiModelProperty("是否删除，0:否 1:是")
    private Boolean isDeleted;

    @ApiModelProperty("1：新建 2：修改 3：删除")
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

        TableIdxMysql other = (TableIdxMysql) obj;

        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }

        if (indexName == null) {
            if (other.indexName != null) {
                return false;
            }
        } else if (!indexName.equals(other.indexName)) {
            return false;
        }

        if (columnIds == null) {
            if (other.columnIds != null) {
                return false;
            }
        } else if (!columnIds.equals(other.columnIds)) {
            return false;
        }

        /*if (subdivision == null) {
            if (other.subdivision != null) {
                return false;
            }
        } else if (!subdivision.equals(other.subdivision)) {
            return false;
        }*/

        if (indexType == null) {
            if (other.indexType != null) {
                return false;
            }
        } else if (!indexType.equals(other.indexType)) {
            return false;
        }

        if (indexMethod == null) {
            if (other.indexMethod != null) {
                return false;
            }
        } else if (!indexMethod.equals(other.indexMethod)) {
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