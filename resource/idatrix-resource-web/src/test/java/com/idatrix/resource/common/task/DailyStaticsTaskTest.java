package com.idatrix.resource.common.task;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/**
 * @Classname DailyStaticsTaskTest
 * @Description 测试日统计任务
 * @Author robin
 * @Date 2019/2/28 10:13
 * @Version v1.0
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:META-INF/spring/catalog-root.xml"})
public class DailyStaticsTaskTest {

    @Resource
    private DailyStaticsTask dailyStaticsTask;

    @Test
    public void dailyStaticsTask(){
        dailyStaticsTask.startTask();
    }
}
