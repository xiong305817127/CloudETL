package com.ys.idatrix.db.test;

import com.alibaba.fastjson.JSON;
import com.ys.idatrix.db.api.rdb.dto.RdbEnum;
import com.ys.idatrix.db.api.rdb.dto.RdbLinkDto;
import com.ys.idatrix.db.api.sql.dto.SqlQueryRespDto;
import com.ys.idatrix.db.core.rdb.RdbExecService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.MessageFormat;

/**
 * @ClassName: RdbExecServiceTest
 * @Description:
 * @Author: ZhouJian
 * @Date: 2019/4/4
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RdbExecServiceTest {

    @Autowired
    private RdbExecService rdbExecService;

    @Test
    public void testQueryView(){
        //String sql = "SELECT  view_definition  FROM  information_schema.views where table_name='etl_view_test'";
        String sql = "select * from db_sql_result";
        RdbLinkDto linkDto = new RdbLinkDto();
        linkDto.setIp("10.0.0.88");
        linkDto.setPort("3306");
        linkDto.setType("MYSQL");
        linkDto.setUsername("root");
        linkDto.setPassword("qsde523@@#");
        linkDto.setDbName("test");
        linkDto.setDbName("dbproxy");
        linkDto.setUrl(MessageFormat.format(RdbEnum.RDBLink.MYSQL.getLinkUrl(), linkDto.getIp(), linkDto.getPort(), linkDto.getDbName()));
        linkDto.setDriverClassName(RdbEnum.RDBLink.MYSQL.getDriverName());

        try {
            SqlQueryRespDto resp = rdbExecService.executeQuery(linkDto,sql);
            System.out.println(JSON.toJSONString(resp,true));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
