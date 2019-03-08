package com.ys.idatrix.db.dto;

import com.ys.idatrix.db.api.rdb.dto.RdbColumn;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author lijie@gdbigdata.com
 * @version 1.0
 * @date 创建时间：2017年4月27日 下午7:13:21
 * @parameter
 * @return
 * @since
 */
@Getter
@Setter
@ToString
public class RdbColumnChanges {

    /**
     * 旧版本表的列名
     */
    private String oldColumnName;

    /**
     * 新版本的列的pojo对象
     */
    private RdbColumn newColumnBean;

    /**
     * 0为只修改列名,1为只修改属性,2为既修改列名也修改属性
     */
    private int flag;

    public RdbColumnChanges() {
        super();
    }

    public RdbColumnChanges(String oldColumnName, RdbColumn newColumnBean, int flag) {
        super();
        this.oldColumnName = oldColumnName;
        this.newColumnBean = newColumnBean;
        this.flag = flag;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + flag;
        result = prime * result + ((newColumnBean == null) ? 0 : newColumnBean.hashCode());
        result = prime * result + ((oldColumnName == null) ? 0 : oldColumnName.hashCode());
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
        RdbColumnChanges other = (RdbColumnChanges) obj;
        if (flag != other.flag) {
            return false;
        }
        if (newColumnBean == null) {
            if (other.newColumnBean != null) {
                return false;
            }
        } else if (!newColumnBean.equals(other.newColumnBean)) {
            return false;
        }
        if (oldColumnName == null) {
            if (other.oldColumnName != null) {
                return false;
            }
        } else if (!oldColumnName.equals(other.oldColumnName)) {
            return false;
        }
        return true;
    }

}
