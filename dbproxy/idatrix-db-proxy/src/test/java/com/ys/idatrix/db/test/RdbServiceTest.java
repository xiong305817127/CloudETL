package com.ys.idatrix.db.test;

import com.alibaba.fastjson.JSON;
import com.ys.idatrix.db.api.common.RespResult;
import com.ys.idatrix.db.api.rdb.dto.RdbDropDatabase;
import com.ys.idatrix.db.api.rdb.dto.RdbLinkDto;
import com.ys.idatrix.db.api.rdb.service.RdbService;
import com.ys.idatrix.db.api.sql.dto.SqlExecRespDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @ClassName: RdbServiceTest
 * @Description:
 * @Author: ZhouJian
 * @Date: 2019/3/19
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RdbServiceTest {

    @Autowired
    private RdbService rdbService;

    @Test
    public void testDbLink(){
        RdbLinkDto rdbLinkDto = new RdbLinkDto();
        rdbLinkDto.setIp("10.0.0.103")
                .setPort("3307")
                .setType("MYSQL")
                .setUsername("root")
                .setPassword("root");

        RespResult<Boolean> result = rdbService.testDBLink(rdbLinkDto);
        System.out.println(JSON.toJSONString(result));
    }


    @Test
    public void testDropDatabase(){
        RdbLinkDto rdbLinkDto = new RdbLinkDto();
        rdbLinkDto.setIp("10.0.0.108")
                .setPort("3306")
                .setType("MYSQL")
                .setUsername("root")
                .setPassword("root");

        RdbDropDatabase database = new RdbDropDatabase().setUserName("oyr").setDatabase("security");

        RespResult<SqlExecRespDto> result = rdbService.dropDatabase("wzl1",rdbLinkDto,database);
        System.out.println(JSON.toJSONString(result));
    }

}
