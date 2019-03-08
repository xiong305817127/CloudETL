package com.ys.idatrix.metacube.metamanage.service;

import com.ys.idatrix.metacube.metamanage.domain.TableColumn;
import com.ys.idatrix.metacube.metamanage.domain.TableFkMysql;
import com.ys.idatrix.metacube.metamanage.vo.request.MySqlTableVO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName MysqlTableServiceTest
 * @Description MysqlTableServiceTest 单元测试类
 * @Author ouyang
 * @Date
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class MysqlTableServiceTest {

    @Autowired
    private MysqlTableService mysqlTableService;

    @Test
    public void add() {
        MySqlTableVO mysqlTable = new MySqlTableVO();
        mysqlTable.setName("table_50");
        mysqlTable.setIdentification("table_中文名50");
        mysqlTable.setThemeId(1l);
        mysqlTable.setPublicStatus(0);
        mysqlTable.setSchemaId(50l);
        mysqlTable.setDatabaseType(1);
        mysqlTable.setResourceType(1);

        List<TableColumn> columns = new ArrayList<>();
        TableColumn id = new TableColumn();
        id.setColumnName("id");
        id.setColumnType("bigint");
        id.setTypeLength("20");
        id.setIsPk(true);
        id.setIsNull(false);
        id.setIsAutoIncrement(true);
        id.setDescription("主键");
        id.setLocation(1);

        TableColumn name = new TableColumn();
        name.setColumnName("name");
        name.setColumnType("varchar");
        name.setTypeLength("255");
        name.setDescription("名字");
        name.setLocation(2);

        TableColumn age = new TableColumn();
        age.setColumnName("age");
        age.setColumnType("int");
        age.setTypeLength("10");
        age.setDescription("年龄");
        age.setLocation(3);

        TableColumn port = new TableColumn();
        port.setColumnName("port");
        port.setColumnType("varchar");
        port.setTypeLength("255");
        port.setDescription("端口");
        port.setLocation(4);

        TableColumn csId = new TableColumn();
        csId.setColumnName("cs_id");
        csId.setColumnType("varchar");
        csId.setTypeLength("255");
        csId.setDescription("外键");
        csId.setLocation(5);

        columns.add(id);
        columns.add(name);
        columns.add(age);
        columns.add(port);
        columns.add(csId);
/*
        List<TableIdxMysql> indexList = new ArrayList<>();
        TableIdxMysql indexCs1 = new TableIdxMysql();
        indexCs1.setIndexName("index_cs1");
        indexCs1.setColumnNames("name");
        indexCs1.setSubdivision(" ");
        indexCs1.setLocation(1);

        indexList.add(indexCs1);*/

        mysqlTable.setTableColumnList(columns);
        //mysqlTable.setTableIndexList(indexList);

        mysqlTableService.add(mysqlTable);
    }

    @Test
    public void update() {
        Long tableId = 174l;
        Long schemaId = 33l;

        MySqlTableVO mysqlTable = new MySqlTableVO();
        mysqlTable.setId(tableId);
        mysqlTable.setName("table_16");
        mysqlTable.setIdentification("table_中文名16_update");
        mysqlTable.setThemeId(1l);
        mysqlTable.setPublicStatus(0);
        mysqlTable.setSchemaId(schemaId);
        mysqlTable.setDatabaseType(1);
        mysqlTable.setResourceType(1);

        List<TableColumn> columns = new ArrayList<>();

        TableColumn age = new TableColumn();
        age.setId(444l);
        age.setTableId(tableId);
        age.setColumnName("cs_id_update");
        age.setColumnType("varchar");
        age.setTypeLength("20");
        age.setDescription("外键");
        age.setLocation(5);
        age.setStatus(2);

        columns.add(age);

        /*List<TableIdxMysql> indexList = new ArrayList<>();
        TableIdxMysql indexCs1 = new TableIdxMysql();
        indexCs1.setId(77l);
        indexCs1.setTableId(tableId);
        indexCs1.setIndexName("index_cs2");
        indexCs1.setColumnNames("age");
        indexCs1.setSubdivision(" ");
        indexCs1.setLocation(5);
        indexCs1.setStatus(2);

        indexList.add(indexCs1);*/

        List<TableFkMysql> fkList = new ArrayList<>();
        TableFkMysql fk1 = new TableFkMysql();
        fk1.setId(20l);
        fk1.setTableId(tableId);
        fk1.setName("fk_name_4");
        fk1.setColumnNames("cs_id_update");
        fk1.setReferenceSchemaId(schemaId);
        fk1.setReferenceTableId(173l);
        fk1.setReferenceColumn("437");
        fk1.setReferenceColumnNames("age");
        fk1.setLocation(1);
        fk1.setStatus(2);

        fkList.add(fk1);

        mysqlTable.setTableColumnList(columns);
        // mysqlTable.setTableIndexList(indexList);
        mysqlTable.setTableFkMysqlList(fkList);

        mysqlTableService.update(mysqlTable);
    }

}