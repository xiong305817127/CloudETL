import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * @ClassName RestTemplateTest
 * @Description 使用 restTemplate 的一个demo
 * @Author ouyang
 * @Date 2018/8/21 10:53
 * @Version 1.0
 **/
public class RestTemplateTest {

    Logger logger = LoggerFactory.getLogger(getClass());

    ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-restTemplate.xml");

    @Test
    public void test1(){
        RestTemplate restTemplate = applicationContext.getBean(RestTemplate.class);
        ResponseEntity<String> response = restTemplate.getForEntity("http://14.215.177.38", String.class);
        HttpStatus code = response.getStatusCode();
        System.out.println(code.toString());
    }

    @Test
    public void get(){
        RestTemplate restTemplate = applicationContext.getBean(RestTemplate.class);
        /*
        ResponseEntity<Map> response = restTemplate.getForEntity("http://202.106.10.250:40080/dmp-oppm/users/sso/addUser?username={1}&appkey={3}", Map.class, "ssz_test1", "ssz_test1");
        Map map = response.getBody();
        if(map.get("code").equals(0)){
            logger.info("delete user synchronized ssz，success！！！");
        } else {
            logger.error("delete user synchronized ssz，error！！！message：" + map.get("msg"));
        }*/
        String strUserNames = "ssz_test1,神算子_test1";
        logger.info("delete user synchronized ssz，userNames：" + strUserNames);
        String[] userNameArr = strUserNames.split(",");
        for (String username : userNameArr) {
            try {
                ResponseEntity<Map> response = restTemplate.getForEntity("http://202.106.10.250:40080/dmp-oppm/users/sso/delUser?username={1}&appkey={2}", Map.class, username, username);
                Map map = response.getBody();
                if(map.get("code").equals(0)){
                    logger.info("delete user synchronized ssz，success！！！");
                } else {
                    logger.error("delete user synchronized ssz，error！！！username：" + username + "，message：" + map.get("msg"));
                }
            } catch (Exception e){
                e.printStackTrace();
                logger.error("delete user synchronized ssz，error！！！username：" + username);
            }
        }
    }

    @Test
    public void test2(){
        RestTemplate restTemplate = applicationContext.getBean(RestTemplate.class);
        ResponseEntity<Map> response = restTemplate.getForEntity("http://10.0.0.117:8080/security/member/user/all", Map.class);
        Map result = response.getBody();
        if(result.get("code").equals(200)) {
            Object jsonString = result.get("data");
            List userList = (List) jsonString;
            for (Object obj : userList) {
                Map map = (Map) obj;
                System.out.println(map.get("username"));
            }
        }
    }

    /*@Test
    public void test3() {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-*.xml");
        UserInfoSyncService userInfoSyncService = (UserInfoSyncService) applicationContext.getBean("userInfoSyncService");
        System.out.println("aaaaaaaaaaaaaa");
    }*/

}
