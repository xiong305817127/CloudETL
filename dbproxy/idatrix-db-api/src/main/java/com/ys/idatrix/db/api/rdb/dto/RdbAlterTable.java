package com.ys.idatrix.db.api.rdb.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @ClassName: RdbAlterTable
 * @Description:
 * @Author: ZhouJian
 * @Date: 2019/3/4
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class RdbAlterTable extends RdbTable implements Serializable {

    private static final long serialVersionUID = -3079237970103736350L;

    /**
     * 新版本的主键
     */
    private RdbPrimaryKey newPrimaryKey;

    /**
     * 新版本的列元数据(必须)
     */
    private ArrayList<RdbColumn> newVersionColumns;

    /**
     * 新版本的索引元数据(必须)
     */
    private ArrayList<RdbIndex> newVersionIndices;

    public RdbAlterTable() {
        super();
    }

    public RdbAlterTable(String tableName, String schema, String comment, RdbPrimaryKey oldPrimaryKey, RdbPrimaryKey newPrimaryKey) {
        super(tableName, schema, comment, oldPrimaryKey);
        this.newPrimaryKey = newPrimaryKey;
    }

    public RdbAlterTable(String tableName, String schema, String comment, RdbPrimaryKey oldPrimaryKey, RdbPrimaryKey newPrimaryKey,
                         ArrayList<RdbColumn> oldVersionColumns, ArrayList<RdbColumn> newVersionColumns) {
        super(tableName, schema, comment, oldPrimaryKey, oldVersionColumns);
        this.newPrimaryKey = newPrimaryKey;
        this.newVersionColumns = newVersionColumns;
    }

    public RdbAlterTable(String tableName, String schema, String comment, RdbPrimaryKey oldPrimaryKey, RdbPrimaryKey newPrimaryKey,
                         ArrayList<RdbColumn> oldVersionColumns, ArrayList<RdbColumn> newVersionColumns,
                         ArrayList<RdbIndex> oldVersionIndices, ArrayList<RdbIndex> newVersionIndices) {
        super(tableName, schema, comment, oldPrimaryKey, oldVersionColumns, oldVersionIndices);
        this.newPrimaryKey = newPrimaryKey;
        this.newVersionColumns = newVersionColumns;
        this.newVersionIndices = newVersionIndices;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((newVersionColumns == null) ? 0 : newVersionColumns.hashCode());
        result = prime * result + ((getRdbColumns() == null) ? 0 : getRdbColumns().hashCode());
        result = prime * result + ((newVersionIndices == null) ? 0 : newVersionIndices.hashCode());
        result = prime * result + ((getIndices() == null) ? 0 : getIndices().hashCode());
        result = prime * result + ((newPrimaryKey == null) ? 0 : newPrimaryKey.hashCode());
        result = prime * result + ((getPrimaryKey() == null) ? 0 : getPrimaryKey().hashCode());
        result = prime * result + ((getTableName() == null) ? 0 : getTableName().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        RdbAlterTable other = (RdbAlterTable) obj;

        if (newVersionColumns == null) {
            if (other.newVersionColumns != null) {
                return false;
            }
        } else if (!newVersionColumns.equals(other.newVersionColumns)) {
            return false;
        }

        if (getRdbColumns() == null) {
            if (other.getRdbColumns() != null) {
                return false;
            }
        } else if (!getRdbColumns().equals(other.getRdbColumns())) {
            return false;
        }

        if (newVersionIndices == null) {
            if (other.newVersionIndices != null) {
                return false;
            }
        } else if (!newVersionIndices.equals(other.newVersionIndices)) {
            return false;
        }

        if (getIndices() == null) {
            if (other.getIndices() != null) {
                return false;
            }
        } else if (!getIndices().equals(other.getIndices())) {
            return false;
        }

        if (newPrimaryKey == null) {
            if (other.newPrimaryKey != null) {
                return false;
            }
        } else if (!newPrimaryKey.equals(other.newPrimaryKey)) {
            return false;
        }

        if (getPrimaryKey() == null) {
            if (other.getPrimaryKey() != null) {
                return false;
            }
        } else if (!getPrimaryKey().equals(other.getPrimaryKey())) {
            return false;
        }

        if (getTableName() == null) {
            if (other.getTableName() != null) {
                return false;
            }
        } else if (!getTableName().equals(other.getTableName())) {
            return false;
        }

        return true;
    }

}
