import com.alibaba.fastjson.JSONObject;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
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

    @Test
    public void test4() {
        RestTemplate restTemplate = applicationContext.getBean(RestTemplate.class);
        JSONObject json = new JSONObject();
        json.put("firstname", "韩立");
        json.put("lastname", "韩立");
        json.put("username", "hanli");
        json.put("password", "a123456");
        json.put("active", 1);
        json.put("email", "123456@qq.com");
        JSONObject responseJson = restTemplate.postForEntity("http://192.168.19.61:8099/bi/abUser/save", json, JSONObject.class).getBody();
        Integer code = (Integer) responseJson.get("code");
        System.out.println(code);
    }

    @Test
    public void test5() {
        int[] arr = new int[]{7,10,13,16,19,29,32,33,37,41,43};
        int i = binarySearch(arr, 10);
        System.out.println(i);
    }

    public int binarySearch(int arr[], int value) {
        int left = 0;
        int right = arr.length-1;
        while (left <= right) {
            // 中间下标
            int mid = (left + right) / 2;
            if(arr[mid] == value) {
                // 需要查找的值等于中间值
                return mid;
            } else if(arr[mid] > value) {
                // 需要查找的值小于中间值
                right = mid - 1;
            } else {
                // 需要查找的值大于中间值
                 left = mid + 1;
            }
        }
        return -1;
    }

    public void sum() {
        int arr[][] = new int[100][10];
        for (int i = 0; i < arr.length; i++) {
            int sum = 0;
            for (int l = 0; l < arr[i].length; l++) {
                sum += arr[i][l];
            }
            System.out.println("第" + i +"张图片的总得分为：" + sum);
        }
    }

    public void dd() {
        // 平均分
        List avgList = new ArrayList();
        int arr[][] = new int[100][10];
        for (int i = 0; i < arr.length; i++) {
            int sum = 0;
            for (int l = 0; l < arr[i].length; l++) {
                sum += arr[i][l];
            }
            avgList.add(sum / 10);
        }

        // 将数据排序并返回
        for (int i = 0; i < arr.length; i++) {

        }

    }

    /*public static void InsertSort(int[] arr) {
        int value;
        // 当前第一个元素认为是有序表，其他元素都认为是无序表，所以从下标1开始遍历
        for (int i = 1; i < arr.length; i++) {
            int index = i;
            value = arr[i];
            // 如果当前下标大于0，并且值当前值小于下标index-1的话则替换。
            while (index>0 && value<arr[index-1]){
                arr[index] = arr[index-1];
                index--;
            }
            arr[index] = k;
        }
    }*/

    @Test
    public void test6(){
        int[] arr = new int[]{20, 40, 30, 10, 60, 50};
        quickSort(arr, 0, arr.length - 1);
        for (int i : arr) {
            System.out.print(i + "\t");
        }
    }

    public void bubbleSort(int arr[]) {
        int length = arr.length ;
        int flag; // 标记
        for (int i = length - 1; i > 0 ; i--) {
            flag = 0;
            for (int l = 0; l < i; l++) {
                if(arr[l] > arr[l + 1]) {
                    arr[l] = arr[l] + arr[l+1]; // 总和
                    arr[l + 1] = arr[l] - arr[l + 1]; // 获取arr[l]的值
                    arr[l] = arr[l] - arr[l + 1]; // 获取arr[l + 1]的值
                    flag = 1; // 有修改
                }
            }
            if(flag == 0) { // 没有修改
                break;
            }
        }
    }

    // 30,40,60,10,20,50
    // 从小到大
    public static void quickSort(int[] arr, int l, int r) {
        if (l < r) {
            int start = l, end = r, value = arr[start];
            while (start < end) {
                while(start < end && arr[end] > value) {
                    end--; // 从右向左找第一个小于x的数
                }
                // 找到小于x的数了
                if(start < end){
                    arr[start++] = arr[end]; // 将小于x的数放到左边
                }
                while(start < end && arr[start] < value){
                    start++; // 从左向右找第一个大于x的数
                }
                if(start < end){
                    arr[end--] = arr[start]; // 将大于x的数放到右边
                }
            }
            arr[start] = value;
            quickSort(arr, l, start-1); /* 递归调用，左边排序 */
            quickSort(arr, start+1, r); /* 递归调用，右边排序 */
        }
    }

}

