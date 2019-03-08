package com.ys.idatrix.metacube.metamanage.vo.request;

import com.ys.idatrix.metacube.api.beans.DatabaseTypeEnum;
import com.ys.idatrix.metacube.metamanage.domain.Metadata;
import com.ys.idatrix.metacube.metamanage.domain.TableColumn;
import com.ys.idatrix.metacube.metamanage.domain.TableFkMysql;
import com.ys.idatrix.metacube.metamanage.domain.TableIdxMysql;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import javax.validation.Valid;
import java.util.List;

/**
 * @ClassName MySqlTableVO
 * @Description mysql table 实体类
 * @Author ouyang
 * @Date
 */
@ApiModel(value = "MySqlTableVO", description = "mysql table 实体类")
@Data
public class MySqlTableVO extends TableVO {

    // 字段
    @Valid
    private List<TableColumn> tableColumnList;

    // 索引
    @Valid
    private List<TableIdxMysql> tableIndexList;

    // 外键
    @Valid
    private List<TableFkMysql> tableFkMysqlList;

    public MySqlTableVO() {
        this.setDatabaseType(DatabaseTypeEnum.MYSQL.getCode());
        this.setResourceType(1);// 1为表
    }

    public MySqlTableVO(Metadata table, List<TableColumn> tableColumnList, List<TableIdxMysql> tableIndexList, List<TableFkMysql> tableFkMysqlList) {
        BeanUtils.copyProperties(table, this);
        this.setTableColumnList(tableColumnList);
        this.setTableIndexList(tableIndexList);
        this.setTableFkMysqlList(tableFkMysqlList);
    }
}
