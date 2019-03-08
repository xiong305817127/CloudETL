package com.ys.idatrix.metacube.metamanage.service;

import com.ys.idatrix.metacube.metamanage.domain.TableColumn;
import com.ys.idatrix.metacube.metamanage.domain.TableFkMysql;
import com.ys.idatrix.metacube.metamanage.domain.TableIdxMysql;
import com.ys.idatrix.metacube.metamanage.vo.request.MySqlTableVO;
import com.ys.idatrix.metacube.metamanage.vo.request.DBViewVO;

import java.util.Date;
import java.util.List;

/**
 * @ClassName MysqlValidatedService
 * @Description mysql校验服务层
 * @Author ouyang
 * @Date
 */
public interface MysqlValidatedService {

    // 校验表的基本信息
    void validatedTableBaseInfo(MySqlTableVO mysqlTable);

    // 校验表字段
    void validatedTableColumn(List<TableColumn> tableColumnList, Boolean hasFilter);

    // 校验索引
    void validatedTableIndex(List<TableIdxMysql> tableIndexList, List<TableColumn> tableColumnList, Boolean hasFilter);

    // 校验外键
    void validatedTableForeignKey(List<TableFkMysql> tableFkMysqlList, List<TableColumn> tableColumnList,
                             List<TableIdxMysql> tableIndexList, Long tableId, String creator, Date createTime, Boolean hasFilter);

    // 校验视图
    void validatedView(DBViewVO view);
}