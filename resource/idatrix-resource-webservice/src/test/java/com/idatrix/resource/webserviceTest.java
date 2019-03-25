package com.idatrix.resource;

import com.idatrix.resource.webservice.webservice.ISubscribeSearchService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @Classname webserviceTest
 * @Description 订阅查询服务测试接口
 * @Author robin
 * @Date 2019/2/19 16:14
 * @Version v1.0
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:applicationContext.xml"})
public class webserviceTest {

    @Autowired
    private ISubscribeSearchService subscribeSearchService;

    @Test
    public void searchServiceTest(){
        subscribeSearchService.databaseSearchByCondition(null, "a87823090sdi234", 0, 10);
    }
}
