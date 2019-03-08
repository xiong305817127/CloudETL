package com.idatrix.unisecurity.core.shiro.session.dao;

import org.apache.shiro.session.Session;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Collection;

/**
 * @ClassName CustomShiroSessionDAOTest
 * @Description TODO
 * @Author ouyang
 * @Date 2018/11/20 11:48
 * @Version 1.0
 */
public class CustomShiroSessionDAOTest {

    ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-*.xml");

    @Test
    public void setShiroSessionRepository() throws Exception {

    }

    @Test
    public void doReadSession() throws Exception {

    }

    @Test
    public void doCreate() throws Exception {

    }

    @Test
    public void update() throws Exception {

    }

    @Test
    public void delete() throws Exception {

    }

    @Test
    public void getActiveSessions() throws Exception {
        CustomShiroSessionDAO sessionDAO = applicationContext.getBean(CustomShiroSessionDAO.class);
        Collection<Session> sessionAll = sessionDAO.getActiveSessions();
        System.out.println(sessionAll);
    }

}