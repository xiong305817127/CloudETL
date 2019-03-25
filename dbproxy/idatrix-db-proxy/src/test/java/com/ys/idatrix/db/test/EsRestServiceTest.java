package com.ys.idatrix.db.test;

import com.alibaba.fastjson.JSON;
import com.ys.idatrix.db.api.common.RespResult;
import com.ys.idatrix.db.api.es.service.EsRestService;
import com.ys.idatrix.db.api.sql.dto.SqlExecRespDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @ClassName: HBaseServiceTest
 * @Description:
 * @Author: ZhouJian
 * @Date: 2019/3/8
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class EsRestServiceTest {

    @Autowired
    private EsRestService esRestService;

    @Test
    public void testQueryIndexMetadata(){
        RespResult<String> result =  esRestService.queryIndexMetadata("zhoujian","idx_metadata_data_445");
        System.out.println(JSON.toJSONString(result,true));
    }

    String dsl = "{\\n  \\\"query\\\": {\\n    \\\"query_string\\\": {\\n      \\\"query\\\": \\\"\\\"\\n    }\\n  }\\n}\"";
    @Test
    public void testQueryDocsWithFull(){
        RespResult<String> result =  esRestService.queryDocsWithFull("wzl1","zhj_idx","");
        System.out.println(JSON.toJSONString(result,true));
    }
}
