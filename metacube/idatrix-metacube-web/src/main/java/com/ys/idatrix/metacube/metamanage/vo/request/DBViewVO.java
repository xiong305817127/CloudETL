package com.ys.idatrix.metacube.metamanage.vo.request;

import com.ys.idatrix.metacube.api.beans.DatabaseTypeEnum;
import com.ys.idatrix.metacube.metamanage.domain.Metadata;
import com.ys.idatrix.metacube.metamanage.domain.ViewDetail;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import javax.validation.Valid;

/**
 * @ClassName DBViewVO
 * @Description 视图 vo
 * @Author ouyang
 * @Date
 */
@ApiModel(value = "DBViewVO", description = "mysql 视图实体类")
@Data
public class DBViewVO extends ViewVO {

    @Valid
    private ViewDetail viewDetail;

    public DBViewVO() {
        this.setDatabaseType(DatabaseTypeEnum.MYSQL.getCode()); // 数据库类型
    }

    public DBViewVO(Metadata view, ViewDetail viewDetail) {
        BeanUtils.copyProperties(view, this);
        this.setViewDetail(viewDetail);
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
        DBViewVO other = (DBViewVO) obj;

        if (this.getId() == null) {
            if (other.getId() != null) {
                return false;
            }
        } else if (!this.getId().equals(other.getId())) {
            return false;
        }

        if (getName() == null) {
            if (other.getName() != null) {
                return false;
            }
        } else if (!getName().equals(other.getName())) {
            return false;
        }

        if (getIdentification() == null) {
            if (other.getIdentification() != null) {
                return false;
            }
        } else if (!getIdentification().equals(other.getIdentification())) {
            return false;
        }

        if (this.getThemeId() == null) {
            if (other.getThemeId() != null) {
                return false;
            }
        } else if (!this.getThemeId().equals(other.getThemeId())) {
            return false;
        }

        if (this.getTags() == null) {
            if (other.getTags() != null) {
                return false;
            }
        } else if (!this.getTags().equals(other.getTags())) {
            return false;
        }

        if (this.getRemark() == null) {
            if (other.getRemark() != null) {
                return false;
            }
        } else if (!getRemark().equals(other.getRemark())) {
            return false;
        }

        if (getPublicStatus() == null) {
            if (other.getPublicStatus() != null) {
                return false;
            }
        } else if (!getPublicStatus().equals(other.getPublicStatus())) {
            return false;
        }
        return true;
    }

}