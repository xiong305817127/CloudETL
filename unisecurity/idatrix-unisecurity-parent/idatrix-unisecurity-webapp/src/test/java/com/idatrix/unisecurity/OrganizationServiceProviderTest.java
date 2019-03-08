package com.idatrix.unisecurity;

import com.idatrix.unisecurity.api.service.OrganizationService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;

/**
 * @ClassName OrganizationServiceProviderTest
 * @Description TODO
 * @Author ouyang
 * @Date 2018/9/6 14:21
 * @Version 1.0
 **/
public class OrganizationServiceProviderTest {

    public void findByName(){
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring.xml");
        OrganizationService service =
                (OrganizationService) applicationContext.getBean("organizationServiceProvider");
        List<Long> ids = service.findByName(null, "a");
        System.out.println(ids.toString());
    }

}
