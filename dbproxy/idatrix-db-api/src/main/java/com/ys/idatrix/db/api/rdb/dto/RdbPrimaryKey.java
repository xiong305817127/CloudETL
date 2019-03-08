package com.ys.idatrix.db.api.rdb.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName: RdbPrimaryKey
 * @Description: 建表时候指定主键集合
 * @Author: ZhouJian
 * @Date: 2019/3/4
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class RdbPrimaryKey implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键名字 当建表有主键且数据库为Oracle、DM7的时候设置,其他数据库建立时候忽略(Oracle、DM7 才设置)
     */
    private String primaryKeyName;

    /**
     * 主键列集合(必须 1~N)
     */
    private List<RdbColumn> primaryKeys;

    public RdbPrimaryKey() {
        super();
    }

    public RdbPrimaryKey(String primaryKeyName, List<RdbColumn> primaryKeys) {
        super();
        this.primaryKeyName = primaryKeyName;
        this.primaryKeys = primaryKeys;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        RdbPrimaryKey rdbIndex = (RdbPrimaryKey) o;

        if (!primaryKeyName.equals(rdbIndex.primaryKeyName)) {
            return false;
        }
        if (primaryKeys == null) {
            if (rdbIndex.primaryKeys != null) {
                return false;
            }
        } else if (!primaryKeys.equals(rdbIndex.primaryKeys)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = primaryKeyName.hashCode();
        result = 31 * result + primaryKeyName.hashCode();
        return result;
    }


}
