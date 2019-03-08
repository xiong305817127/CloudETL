package com.ys.idatrix.db.dto;

/**
 * 用于存放拷贝 列id和表名
 *
 * @author lijie@gdbigdata.com
 * @version 1.0
 * @date 创建时间：2017年4月28日 上午9:15:13
 * @parameter
 * @return
 * @since
 */
public class RdbColumnCopy {

    /**
     * 列columnId
     */
    private String columnId;

    /**
     * 列名称
     */
    private String columnName;

    public String getColumnId() {
        return columnId;
    }

    public void setColumnId(String columnId) {
        this.columnId = columnId;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public RdbColumnCopy(String columnId, String columnName) {
        super();
        this.columnId = columnId;
        this.columnName = columnName;
    }

    public RdbColumnCopy() {
        super();
    }

    @Override
    public String toString() {
        return "RDBColumnCopyLess [columnId=" + columnId + ", columnName=" + columnName + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((columnId == null) ? 0 : columnId.hashCode());
        result = prime * result + ((columnName == null) ? 0 : columnName.hashCode());
        return result;
    }

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
        RdbColumnCopy other = (RdbColumnCopy) obj;
        if (columnId == null) {
            if (other.columnId != null) {
                return false;
            }
        } else if (!columnId.equals(other.columnId)) {
            return false;
        }
        if (columnName == null) {
            if (other.columnName != null) {
                return false;
            }
        } else if (!columnName.equals(other.columnName)) {
            return false;
        }
        return true;
    }

}
