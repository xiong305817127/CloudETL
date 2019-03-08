package com.ys.idatrix.db.api.rdb.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @ClassName: RdbColumn
 * @Description:
 * @Author: ZhouJian
 * @Date: 2019/3/4
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class RdbColumn implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 列名唯一标识id(必须)
     */
    private String columnId;

    /**
     * 列名(必须)
     */
    private String columnName;

    /**
     * 列的数据类型(必须)
     */
    private String dataType;

    /**
     * 类型的范围(可不设置)
     */
    private String columnChamp;

    /**
     * 是否允许为空 ,true为不允许为空,false为允许为空,默认为允许为空(可不设置)
     */
    private boolean hasNotNull = false;

    /**
     * 是否自增,为true是自增,默认不是自增 (oracle没有该属性)(可不设置)
     */
    private boolean hasAutoIncrement = false;

    /**
     * 是否是无符号数,当该列为字符型的时候才有用,true为无符号,false为有符号,默认为有符号数(oracle没有该属性)(可不设置)
     */
    private boolean hasUnsigned = false;

    /**
     * 默认值(可不设置)
     */
    private String defaultValue;

    /**
     * 注释(可不设置)
     */
    private String comment;


    public RdbColumn() {
        super();
    }


    /**
     * 最小构造方法
     *
     * @param columnName
     * @param dataType
     */
    public RdbColumn(String columnName, String dataType) {
        super();
        this.columnName = columnName;
        this.dataType = dataType;
    }

    /**
     * 最大构造方法
     *
     * @param columnName
     * @param dataType
     * @param columnChamp
     * @param hasNotNull
     * @param hasAutoIncrement
     * @param hasUnsigned
     * @param defaultValue
     * @param comment
     */
    public RdbColumn(String columnName, String dataType, String columnChamp, boolean hasNotNull,
                     boolean hasAutoIncrement, boolean hasUnsigned, String defaultValue, String comment) {
        super();
        this.columnName = columnName;
        this.dataType = dataType;
        this.columnChamp = columnChamp;
        this.hasNotNull = hasNotNull;
        this.hasAutoIncrement = hasAutoIncrement;
        this.hasUnsigned = hasUnsigned;
        this.defaultValue = defaultValue;
        this.comment = comment;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((columnChamp == null) ? 0 : columnChamp.hashCode());
        result = prime * result + ((columnId == null) ? 0 : columnId.hashCode());
        result = prime * result + ((columnName == null) ? 0 : columnName.hashCode());
        result = prime * result + ((comment == null) ? 0 : comment.hashCode());
        result = prime * result + ((dataType == null) ? 0 : dataType.hashCode());
        result = prime * result + ((defaultValue == null) ? 0 : defaultValue.hashCode());
        result = prime * result + (hasAutoIncrement ? 1231 : 1237);
        result = prime * result + (hasNotNull ? 1231 : 1237);
        result = prime * result + (hasUnsigned ? 1231 : 1237);
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
        RdbColumn other = (RdbColumn) obj;
        if (columnChamp == null) {
            if (other.columnChamp != null) {
                return false;
            }
        } else if (!columnChamp.equals(other.columnChamp)) {
            return false;
        }
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
        if (comment == null) {
            if (other.comment != null) {
                return false;
            }
        } else if (!comment.equals(other.comment)) {
            return false;
        }
        if (dataType != other.dataType) {
            return false;
        }
        if (defaultValue == null) {
            if (other.defaultValue != null) {
                return false;
            }
        } else if (!defaultValue.equals(other.defaultValue)) {
            return false;
        }
        if (hasAutoIncrement != other.hasAutoIncrement) {
            return false;
        }
        if (hasNotNull != other.hasNotNull) {
            return false;
        }
        if (hasUnsigned != other.hasUnsigned) {
            return false;
        }
        return true;
    }

}
