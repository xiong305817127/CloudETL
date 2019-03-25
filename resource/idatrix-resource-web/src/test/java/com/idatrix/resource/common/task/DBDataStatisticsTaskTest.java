package com.idatrix.resource.common.task;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by Administrator on 2019/1/7.
 */

@RunWith(SpringJUnit4ClassRunner.class)
/*	告诉junit spring配置文件 */
@ContextConfiguration({"classpath:META-INF/spring/catalog-root.xml"})
public class DBDataStatisticsTaskTest {

    @Autowired
    private DataStatisticsTask dbDataStatisticsTask;

    @Test
    public void dbDataTest(){
        dbDataStatisticsTask.startTask();
    }

}
