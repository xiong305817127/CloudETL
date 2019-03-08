package com.idatrix.unisecurity.consumer;

import com.idatrix.unisecurity.api.domain.LoginDateInfo;
import com.idatrix.unisecurity.api.domain.NowLoginResult;
import com.idatrix.unisecurity.api.domain.OrganizationUserLoginInfo;
import com.idatrix.unisecurity.api.service.UserService;
import com.idatrix.unisecurity.common.utils.GsonUtil;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2018/8/2.
 */
public class UserServiceConsumer {

    static ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:consumer-spring.xml");
    static UserService userService = (UserService) applicationContext.getBean("userService");

    public static void main(String[] args) throws Exception {
        findDeptUserLoginInfoByRentId();
        /*List<User> users = userService.findUserByRoleAndRenter(1, 444l);
        System.out.println(users);
        User user = userService.findByUserName("ooyr");
        System.out.println("user：" + user);
        System.out.println(user.getDeptId());
        System.out.println(user.getRenterId());*/
        /*List<Organization> organizations = userService.findOrganizations();
        System.out.println(organizations);*/
    }

    public static void findUserLoginInfoByRenterIdAndTimeSlot() throws Exception {
        String startDateStr = "2018-12-1 11:22:45";
        String endDateStr = "2019-1-21 00:00:00";
        SimpleDateFormat format = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
        Date startDate = format.parse(startDateStr);
        Date endDate = format.parse(endDateStr);
        List<LoginDateInfo> result = userService.findUserLoginInfoByRenterIdAndTimeSlot(456l, startDate, endDate);
        System.out.println(GsonUtil.toJson(result));
    }

    public static void findDeptUserLoginInfoByRentId() {
        List<OrganizationUserLoginInfo> result = userService.findDeptUserLoginInfoByRentId(456l);
        System.out.println(GsonUtil.toJson(result));
    }

    public static void findNowLoginInfoByRenterId() {
        NowLoginResult result = userService.findNowLoginInfoByRenterId(456l);
        System.out.println(GsonUtil.toJson(result));
    }

    public static void findParentIdsByUnifiedCreditCode() {
        List<Integer> list = userService.findParentIdsByUnifiedCreditCode("asdasd15561", 456l);
        for (Integer integer : list) {
            System.out.println(integer);
        }
    }

}
