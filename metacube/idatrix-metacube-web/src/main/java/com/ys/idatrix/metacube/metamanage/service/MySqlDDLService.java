package com.ys.idatrix.metacube.metamanage.service;

import com.ys.idatrix.metacube.metamanage.domain.*;
import com.ys.idatrix.metacube.metamanage.vo.request.AlterSqlVO;
import com.ys.idatrix.metacube.metamanage.vo.request.DBViewVO;
import com.ys.idatrix.metacube.metamanage.vo.request.MySqlTableVO;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName MySqlDDLService
 * @Description mysql ddl 语句生成api
 * @Author ouyang
 * @Date
 */
public interface MySqlDDLService {

    // 获取创建表的sql
    ArrayList<String> getCreateTableSql(Metadata table, List<TableColumn> columnList, List<TableIdxMysql> tableIndexList, List<TableFkMysql> tableFkMysqlList);

    // 获取修改表的sql
    AlterSqlVO getAlterTableSql(MySqlTableVO newTable, MySqlTableVO snapshotTable);

    // 获取删除表的sql
    List<String> getDeleteTableSql(List<String> removeTableNames);

    // 获取创建视图的sql
    String getCreateOrUpdateViewSql(String name, ViewDetail viewDetail);

    // 获取修改视图的sql
    AlterSqlVO getAlterViewSql(DBViewVO snapshotMySqlView, DBViewVO newMySqlView);

    // 获取删除视图的sql
    List<String> getDropViewSql(List<String> viewNames);

    // 将操作生效到数据库中
    void goToDatabase(Metadata metadata, List<String> commands);
}
