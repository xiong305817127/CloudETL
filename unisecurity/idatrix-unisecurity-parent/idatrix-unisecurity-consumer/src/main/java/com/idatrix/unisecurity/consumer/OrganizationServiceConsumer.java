package com.idatrix.unisecurity.consumer;

import com.idatrix.unisecurity.api.domain.Organization;
import com.idatrix.unisecurity.api.service.OrganizationService;
import com.idatrix.unisecurity.common.utils.GsonUtil;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @ClassName OrganizationServiceConsumer
 * @Description TODO
 * @Author ouyang
 * @Date 2018/9/6 14:26
 * @Version 1.0
 **/
public class OrganizationServiceConsumer {

    static ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:consumer-spring.xml");

    static OrganizationService organizationService = (OrganizationService) applicationContext.getBean("organizationService");

    public static void main(String[] args) {
        /*OrganizationService organizationService = (OrganizationService) applicationContext.getBean("userInfoSyncService");
        List<Long> ids = organizationService.findByName(null, "部门");
        System.out.println(ids.toString());*/
        findAscriptionDeptByUserId();
    }

    public static void findById() {
        Organization organization = organizationService.findById(4667l);
        System.out.println(organization);
    }

    public static void findAscriptionDeptCountByRenterId() {
        Integer count = organizationService.findAscriptionDeptCountByRenterId(456l);
        System.out.println(count);
    }

    public static void findByRenterId() {
        Organization organization = organizationService.findByRenterId(456l);
        System.out.println(GsonUtil.toJson(organization));
    }


    public static void findAscriptionDeptByUserId() {
        Organization organization = organizationService.findAscriptionDeptByUserId(1287l);
        System.out.println(GsonUtil.toJson(organization));
    }

}
