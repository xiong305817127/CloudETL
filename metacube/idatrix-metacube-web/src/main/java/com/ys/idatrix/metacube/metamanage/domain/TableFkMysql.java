package com.ys.idatrix.metacube.metamanage.domain;

import com.ys.idatrix.metacube.common.enums.DBEnum;
import com.ys.idatrix.metacube.common.group.Save;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@ApiModel(value = "TableFkMysql", description = "mysql外键约束实体类")
@Data
public class TableFkMysql {

    @ApiModelProperty("id")
    private Long id;

    @NotBlank(message = "外键名不能为空")
    @ApiModelProperty("外键名")
    private String name;

    @ApiModelProperty("当前表字段id，可能有多个,以,隔开，因为可能关联数据库中还不存在的字段，所以传列名，列id后台处理）")
    private String columnIds;

    @ApiModelProperty("参考的模式id，在mysql中这里表示是数据库，当用户没有选择模式时，自动选择当前模式")
    @NotNull(message = "参考模式不能为空")
    private Long referenceSchemaId;

    @ApiModelProperty("参考表id，参考表必须已经存在了")
    @NotNull(message = "参考表不能为空")
    private Long referenceTableId;

    @ApiModelProperty("参考列id，可能有多个，和当前表字段对应，必传")
    @NotNull(message = "参考列不能为空")
    private String referenceColumn;

    @ApiModelProperty("删除时触发的事件")
    private String deleteTrigger = DBEnum.MysqlFKTriggerAffairEnum.RESTRICT.getName();

    @ApiModelProperty("修改时触发的事件")
    private String updateTrigger = DBEnum.MysqlFKTriggerAffairEnum.RESTRICT.getName();

    @ApiModelProperty("位置（当一个位置标识，在每张表的索引位置都是自增，唯一的）")
    private Integer location;

    @ApiModelProperty("表id")
    private Long tableId;

    private String creator;

    private Date createTime;

    private String modifier;

    private Date modifyTime;

    @ApiModelProperty("是否删除，0:否 1:是")
    private Boolean isDeleted;

    @ApiModelProperty("0：默认值，什么也不做 1：新建 2：修改 3：删除")
    private Integer status = 0;

    @NotBlank(message = "外键关联字段不能为空", groups = Save.class)
    @ApiModelProperty("当前表字段name，可能有多个,以,隔开")
    private String columnNames;

    @ApiModelProperty("参考列names，可能有多个，和当前表字段对应，必传（与上个字段相对应）")
    @NotBlank(message = "参考列不能为空")
    private String referenceColumnNames;

    // 用于查询所用，并不是实体表中字段
    private Long schemaId;

    // 用于查询所用，并不是实体表中字段
    private String referenceSchemaName;
    
    // 用于查询所用，并不是实体表中字段
    private String referenceTableName;

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
        TableFkMysql other = (TableFkMysql) obj;

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

        if(updateTrigger == null) {
            if (other.updateTrigger != null) {
                return false;
            }
        } else if (!updateTrigger.equals(other.updateTrigger)) {
            return false;
        }

        if(tableId == null) {
            if (other.tableId != null) {
                return false;
            }
        } else if (!tableId.equals(other.tableId)) {
            return false;
        }

        return true;
    }

}