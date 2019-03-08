package com.idatrix.unisecurity;

import com.idatrix.unisecurity.log.SecurityLog;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @ClassName LogTest
 * @Description TODO
 * @Author ouyang
 * @Date 2018/9/17 14:04
 * @Version 1.0
 **/
public class LogTest {

    private Logger log = LoggerFactory.getLogger(getClass());

    @Test
    public void test1(){
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring.xml");
        //MDC.put("userId", 111 + "");
        //MDC.put("userName", "name_test1");
        //log.debug("log debug");
        //log.info("log info");
        //log.error("log error");


        MDC.put("userId",  "111");
        MDC.put("userName", "name111");
        MDC.put("renterId", "222");
        MDC.put("server", "server_test");
        MDC.put("resource", "resource");
        MDC.put("methodType", "methodType");
        MDC.put("clientIp", "clientIp");
        MDC.put("result", "success");
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        MDC.put("visitTime", format.format(new Date()));
        SecurityLog.log("这是一个测试数据............");

    }

    public static void main(String[] args) {
        MDC.put("userId",  "111");
        MDC.put("userName", "name111");
        MDC.put("renterId", "222");
        MDC.put("server", "server_test");
        MDC.put("resource", "resource");
        MDC.put("methodType", "methodType");
        MDC.put("clientIp", "clientIp");
        MDC.put("result", "success");
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        MDC.put("visitTime", format.format(new Date()));
        SecurityLog.log("这是一个测试数据............");
    }

}
