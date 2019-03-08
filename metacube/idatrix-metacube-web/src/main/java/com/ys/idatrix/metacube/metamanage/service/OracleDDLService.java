package com.ys.idatrix.metacube.metamanage.service;

import com.ys.idatrix.db.api.rdb.dto.RdbLinkDto;
import com.ys.idatrix.metacube.metamanage.domain.Metadata;
import com.ys.idatrix.metacube.metamanage.domain.ViewDetail;
import com.ys.idatrix.metacube.metamanage.vo.request.AlterSqlVO;
import com.ys.idatrix.metacube.metamanage.vo.request.DBViewVO;
import com.ys.idatrix.metacube.metamanage.vo.request.OracleTableVO;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName OracleDDLService
 * @Description oracle ddl 语句生成api
 * @Author ouyang
 * @Date
 */
public interface OracleDDLService {

    // 获取创建表的sql
    AlterSqlVO getCreateTableSql(OracleTableVO table);

    // 获取修改表的sql
    AlterSqlVO getAlterTableSql(OracleTableVO newTable, OracleTableVO oldTable);

    // 获取删除表的sql
    List<String> getDeleteTableSql(List<String> tableNames);

    // 获取删除视图的sql
    List<String> getDropViewSql(List<String> viewNames);

    // 获取创建或修改视图的sql
    String getCreateOrUpdateViewSql(String name, ViewDetail viewDetail);

    // 获取修改视图的对象
    AlterSqlVO getAlterViewSql(DBViewVO snapshotMySqlView, DBViewVO newMySqlView);

    // 根据元数据获取数据库连接配置
    RdbLinkDto getConnectionConfig(Metadata metadata);

    // 将sql到数据库中执行
    void goToDatabase(Metadata metadata, ArrayList<String> commands);

    // 特殊的sql到数据库中执行
    void specialSqlGoToDatabase(Metadata metadata, List<String> specialSql);
}