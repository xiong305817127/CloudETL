package com.ys.idatrix.db.test;

import com.alibaba.fastjson.JSON;
import com.ys.idatrix.db.api.common.RespResult;
import com.ys.idatrix.db.api.hbase.dto.DataType;
import com.ys.idatrix.db.api.hbase.dto.HBaseColumn;
import com.ys.idatrix.db.api.hbase.dto.HBaseTable;
import com.ys.idatrix.db.api.hbase.dto.PrimaryKey;
import com.ys.idatrix.db.api.hbase.service.HBaseService;
import com.ys.idatrix.db.api.sql.dto.SqlExecRespDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashSet;
import java.util.LinkedHashSet;

/**
 * @ClassName: HBaseServiceTest
 * @Description:
 * @Author: ZhouJian
 * @Date: 2019/3/8
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class HBaseServiceTest {

    @Autowired
    private HBaseService hBaseService;

    @Test
    public void testCreateNamespace() {
        RespResult<SqlExecRespDto> result = hBaseService.createNamespace(null, "zhj_np3");
        System.out.println(JSON.toJSONString(result, true));
    }


    @Test
    public void testCreateTable() {
        HBaseTable createTable = new HBaseTable();
        createTable.setTableName("zhj_np3_tb");
        createTable.setNamespace("zhj_np3");


        LinkedHashSet<HBaseColumn> columns = new LinkedHashSet<HBaseColumn>();
        columns.add( new HBaseColumn("cf_1", "col_1", DataType.VARCHAR));
        columns.add( new HBaseColumn("cf_2", "col_2", DataType.VARCHAR));
        columns.add( new HBaseColumn(null, "col_99", DataType.VARCHAR));

        createTable.setColumns(columns);
        createTable.setPrimaryKey(new PrimaryKey("np3_tb_pk_col_3",new HBaseColumn(null, "col_99", DataType.VARCHAR)));


        RespResult<SqlExecRespDto> result = hBaseService.createTable(null, createTable);
        System.out.println(JSON.toJSONString(result, true));
    }
}
