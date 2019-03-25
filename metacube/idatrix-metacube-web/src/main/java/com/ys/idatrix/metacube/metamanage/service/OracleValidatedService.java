package com.ys.idatrix.metacube.metamanage.service;

import com.ys.idatrix.metacube.metamanage.domain.*;
import com.ys.idatrix.metacube.metamanage.vo.request.DBViewVO;
import com.ys.idatrix.metacube.metamanage.vo.request.OracleTableVO;

import java.util.List;

/**
 * @ClassName OracleValidatedService
 * @Description oracle 校验service
 * @Author ouyang
 * @Date
 */
public interface OracleValidatedService {

    // 校验表的基本信息
    void validatedTableBaseInfo(OracleTableVO oracleTable);

    // 校验表主键
    void validatedTablePrimaryKey(Metadata table, TablePkOracle primaryKey, List<TableColumn> columnList);

    // 校验表字段
    void validatedTableColumn(List<TableColumn> tableColumnList, Integer versions);

    // 校验表索引
    void validatedTableIndex(OracleTableVO table, List<TableIdxOracle> indexList, List<TableColumn> columnList, Integer versions);

    // 校验表的唯一约束
    void validatedTableUnique(OracleTableVO oracleTable, List<TableUnOracle> uniqueList, TablePkOracle primaryKey, List<TableColumn> columnList, Integer versions);

    // 校验表的检查约束
    void validatedTableCheck(OracleTableVO oracleTable, List<TableChOracle> checkList, List<TableUnOracle> uniqueList, TablePkOracle primaryKey, Integer versions);

    // 校验表外键
    void validatedTableForeignKey(OracleTableVO table, List<TableFkOracle> foreignKeyList, List<TableColumn> columnList,
                                  List<TableUnOracle> uniqueList, List<TableChOracle> checkList, TablePkOracle primaryKey, Integer versions);

    // 校验表设置
    void validatedTableSetting(OracleTableVO oracleTable, TableSetOracle tableSetting);

    // 校验视图
    void validatedView(DBViewVO view, Integer versions);
}