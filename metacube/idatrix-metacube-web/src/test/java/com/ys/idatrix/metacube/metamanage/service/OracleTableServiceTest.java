package com.ys.idatrix.metacube.metamanage.service;

import com.ys.idatrix.metacube.common.enums.DBEnum;
import com.ys.idatrix.metacube.metamanage.domain.*;
import com.ys.idatrix.metacube.metamanage.vo.request.OracleTableVO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName OracleTableServiceTest
 * @Description oracle table 单元测试类
 * @Author ouyang
 * @Date
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class OracleTableServiceTest {

    @Autowired
    private OracleTableService tableService;

    @Test
    public void add() {
        OracleTableVO table = new OracleTableVO();
        table.setName("table_54");
        table.setIdentification("table_中文名54");
        table.setThemeId(1l);
        table.setPublicStatus(0);
        table.setSchemaId(43l);
        table.setDatabaseType(2);
        table.setResourceType(1);

        List<TableColumn> columns = new ArrayList<>();

        TableColumn id = new TableColumn();
        id.setColumnName("id");
        id.setColumnType("number");
        id.setTypeLength("20");
        id.setIsNull(false);
        id.setIsPk(true);
        id.setDescription("主键");

        TableColumn name = new TableColumn();
        name.setColumnName("name");
        name.setColumnType("varchar2");
        name.setTypeLength("255");
        name.setDescription("名字");

        TableColumn age = new TableColumn();
        age.setColumnName("age");
        age.setColumnType("number");
        age.setTypeLength("20");
        age.setDescription("年龄");

        TableColumn sex = new TableColumn();
        sex.setColumnName("sex");
        sex.setColumnType("varchar2");
        sex.setTypeLength("2");
        sex.setDescription("性别");

        TableColumn cs = new TableColumn();
        cs.setColumnName("cs");
        cs.setColumnType("number");
        cs.setTypeLength("10");
        cs.setDescription("cs字段");

        columns.add(id);
        columns.add(name);
        columns.add(age);
        columns.add(sex);
        columns.add(cs);
        table.setColumnList(columns);

        TableSetOracle setting = new TableSetOracle();
        table.setTableSetting(setting);

        TablePkOracle primaryKey = new TablePkOracle();
        primaryKey.setName("table_54_id");
        primaryKey.setSequenceStatus(2);
        table.setPrimaryKey(primaryKey);

        /*List<TableIdxOracle> indexList = new ArrayList<>();
        TableIdxOracle cs1 = new TableIdxOracle();
        cs1.setIndexName("cs9");
        cs1.setIndexType("NON_UNIQUE");
        cs1.setColumnNames("name");
        cs1.setColumnSort("ASC");
        indexList.add(cs1);
        table.setIndexList(indexList);*/

        List<TableUnOracle> uniqueList = new ArrayList<>();
        TableUnOracle un_cs1 = new TableUnOracle();
        un_cs1.setName("un_cs8");
        un_cs1.setColumnNames("name");
        un_cs1.setIsEnabled(false);
        uniqueList.add(un_cs1);
        table.setUniqueList(uniqueList);

        /*List<TableChOracle> checkList = new ArrayList<>();
        TableChOracle ch_cs1 = new TableChOracle();
        ch_cs1.setName("ch_cs_3");
        ch_cs1.setCheckSql("\"sex\"='男' or \"sex\"='女' ");
        checkList.add(ch_cs1);
        table.setCheckList(checkList);*/

        /*List<TableFkOracle> foreignKeyList = new ArrayList<>();
        TableFkOracle fk = new TableFkOracle();
        fk.setName("fk_cs_3");
        fk.setColumnNames("name");
        fk.setReferenceSchemaId(43l);
        fk.setReferenceTableId(190l);
        fk.setReferenceRestrain(8l);
        fk.setReferenceColumn("475");
        fk.setDeleteTrigger("SET_NULL");
        fk.setReferenceRestrainType(DBEnum.ConstraintTypeEnum.UNIQUE.getCode());
        foreignKeyList.add(fk);
        table.setForeignKeyList(foreignKeyList);*/

        tableService.add(table);
    }

    @Test
    public void update() {
        Long tableId = 193l;
        Long schemaId = 43l;

        OracleTableVO table = new OracleTableVO();
        table.setId(tableId);
        table.setName("table_53_update");
        table.setIdentification("table_中文名52_update");
        table.setThemeId(1l);
        table.setPublicStatus(0);
        table.setSchemaId(schemaId);
        table.setDatabaseType(2);
        table.setResourceType(1);

        List<TableColumn> columns = new ArrayList<>();

        TableColumn cs = new TableColumn();
        cs.setId(490l);
        cs.setColumnName("cs_update");
        cs.setColumnType("DATE");
        // cs.setTypeLength("25");
        cs.setDescription("cs字段_update");
        cs.setLocation(5);
        cs.setStatus(2);

        TableColumn cs2 = new TableColumn();
        cs2.setId(515l);
        cs2.setColumnName("cs_2");
        cs2.setColumnType("varchar2");
        cs2.setTypeLength("25");
        cs2.setDescription("cs2字段");
        cs2.setStatus(2);

        columns.add(cs);
        columns.add(cs2);
        table.setColumnList(columns);

        // 表设置
        TableSetOracle setting = new TableSetOracle();
        setting.setId(46l);
        setting.setTablespace("USERS");
        setting.setTableId(tableId);
        table.setTableSetting(setting);

        // 表主键设置
        TablePkOracle primaryKey = new TablePkOracle();
        primaryKey.setId(57l);
        primaryKey.setName("table_53_id");
        primaryKey.setSequenceStatus(2);
        primaryKey.setTableId(tableId);
        table.setPrimaryKey(primaryKey);

        // 索引
        List<TableIdxOracle> indexList = new ArrayList<>();
        TableIdxOracle cs1 = new TableIdxOracle();
        cs1.setId(14l);
        cs1.setTableId(tableId);
        cs1.setIndexName("cs10");
        cs1.setIndexType("NON_UNIQUE");
        cs1.setColumnNames("age");
        cs1.setColumnSort("ASC");
        cs1.setTablespace("USERS");
        cs1.setSchemaName("OYR");
        cs1.setStatus(2);

        /*TableIdxOracle indexCs2 = new TableIdxOracle();
        indexCs2.setIndexName("cs11");
        indexCs2.setIndexType("NON_UNIQUE");
        indexCs2.setColumnNames("name");
        indexCs2.setColumnSort("ASC");
        indexCs2.setStatus(1);*/

        indexList.add(cs1);
        // indexList.add(indexCs2);
        table.setIndexList(indexList);

        // 唯一约束
        List<TableUnOracle> uniqueList = new ArrayList<>();
        TableUnOracle un_cs1 = new TableUnOracle();
        un_cs1.setId(9l);
        un_cs1.setTableId(tableId);
        un_cs1.setName("un_cs7");
        un_cs1.setColumnNames("age");
        un_cs1.setIsEnabled(false);
        un_cs1.setStatus(2);

        uniqueList.add(un_cs1);
        table.setUniqueList(uniqueList);

        // 检查约束
        List<TableChOracle> checkList = new ArrayList<>();
        TableChOracle ch_cs1 = new TableChOracle();
        ch_cs1.setId(7l);
        ch_cs1.setTableId(tableId);
        ch_cs1.setName("ch_cs_4");
        ch_cs1.setCheckSql("\"sex\"='a1' or \"sex\"='a2' ");
        un_cs1.setIsEnabled(true);
        ch_cs1.setStatus(2);

        checkList.add(ch_cs1);
        table.setCheckList(checkList);

        // 外键
        List<TableFkOracle> foreignKeyList = new ArrayList<>();
        TableFkOracle fk = new TableFkOracle();
        fk.setId(6l);
        fk.setTableId(tableId);
        fk.setName("fk_cs_6");
        fk.setColumnNames("name");
        fk.setReferenceSchemaId(schemaId);
        fk.setReferenceTableId(234l); // 参考表
        fk.setReferenceRestrain(10l); // 参考约束
        fk.setReferenceColumn("527"); // 参考列
        fk.setDeleteTrigger("SET_NULL");
        fk.setReferenceRestrainType(DBEnum.ConstraintTypeEnum.UNIQUE.getCode());
        fk.setStatus(2);

        foreignKeyList.add(fk);
        table.setForeignKeyList(foreignKeyList);

        tableService.update(table);
    }

}