package com.idatrix.resource.common.task;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/**
 *	配置spring和junit整合，是为了启动时加载springIOC容器
 *	spring-test, junit
 */
@RunWith(SpringJUnit4ClassRunner.class)
/*	告诉junit spring配置文件 */
@ContextConfiguration({"classpath:META-INF/spring/catalog-root.xml"})
public class ExchangeTaskText {

    @Resource
    private ExchangeTask exchangeTask;

    @Test
    public void startTaskTest() {
//        exchangeTask.startTask();
    }
}
