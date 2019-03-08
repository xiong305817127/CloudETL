import com.idatrix.unisecurity.common.domain.AuditLog;
import com.idatrix.unisecurity.common.domain.UUser;
import com.idatrix.unisecurity.common.sso.StringUtil;
import com.idatrix.unisecurity.common.utils.GsonUtil;
import com.idatrix.unisecurity.common.utils.JsonUtils;
import com.idatrix.unisecurity.common.utils.MathUtil;
import com.idatrix.unisecurity.common.utils.ResultVoUtils;
import com.idatrix.unisecurity.common.vo.ResultVo;
import com.idatrix.unisecurity.sso.client.model.SSOUser;
import com.idatrix.unisecurity.sso.client.model.UserDeserailizerFactory;
import com.idatrix.unisecurity.sso.client.model.UserDeserializer;
import com.idatrix.unisecurity.user.Config;
import com.idatrix.unisecurity.user.service.UUserService;
import com.idatrix.unisecurity.user.service.UserSerializer;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Date;

/**
 * @ClassName JsonUtilsTest
 * @Description 测试json转换
 * @Author ouyang
 * @Date 2018/8/28 18:32
 * @Version 1.0
 **/
public class JsonUtilsTest {

    AuditLog auditLog = new AuditLog("server1", "/log/iidd", "get", "10.0.0.15", new Date(), 1l, "username1", 12l);

    @Test
    public void toJson(){
        String json = JsonUtils.toJson(auditLog);
        System.out.println(json);
    }

    @Test
    public void md5(){
        String md5 = MathUtil.getMD5(String.format("#%s", "123456"));
        System.out.println(md5);

        String random620 = MathUtil.getRandom620(6);
        System.out.println(random620);
        //7ee86ea7ca89728ef5f08aa0aef7a120
        //e2504757da409bf4d9f4e8e523144fd6
    }

    @Test
    public void gsonTest() throws Exception {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-*.xml");
        UUserService service = applicationContext.getBean(UUserService.class);
        // 获取序列化对象
        Config config = applicationContext.getBean(Config.class);
        UserSerializer serializer = config.getUserSerializer();

        UUser oyr = service.getUserByUsername("oyr");

        UserSerializer.UserData userData = serializer.serial(oyr, "aaa");
        /*String userDataJson = GsonUtil.toJson(userData);
        System.out.println(userDataJson);*/
        String resultJson = GsonUtil.toJson(ResultVoUtils.ok(GsonUtil.toJson(userData)));
        System.out.println(resultJson);

       /* ResultVo resultVo = GsonUtil.fromJson(resultJson, ResultVo.class);
        String userJson = (String) resultVo.getData();
        SSOUserImpl ssoUser = GsonUtil.fromJson(userJson, SSOUserImpl.class);
        System.out.println(ssoUser);*/
    }

    @Test
    public void test1() throws Exception {
        /*Map<String, Object> map = new HashMap<String, Object>();
        map.put("a", 10);
        map.put("b", "10");
        map.put("c", 10L);
        map.put("d", "fsaf");
        //Gson gson = new Gson();
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(
                        new TypeToken(){}.getType(),
                        new JsonDeserializer<Map<String, Object>>() {
                            @Override
                            public Map<String, Object> deserialize(
                                    JsonElement json, Type typeOfT,
                                    JsonDeserializationContext context) throws JsonParseException {

                                Map<String, Object> treeMap = new HashMap<String, Object>();
                                JsonObject jsonObject = json.getAsJsonObject();
                                Set<Map.Entry<String, JsonElement>> entrySet = jsonObject.entrySet();
                                for (Map.Entry<String, JsonElement> entry : entrySet) {
                                    treeMap.put(entry.getKey(), entry.getValue());
                                }
                                return treeMap;
                            }
                        }).create();

        String json = gson.toJson(map);
        System.out.println(json);*/

        /*Map map1 = gson.fromJson(json, Map.class);
        Map map2 = gson.fromJson(json, new TypeToken<Map<String, Object>>() {
        }.getType());*/

        /*Gson gson = new GsonBuilder()
                .registerTypeAdapter(
                        new TypeToken<SSOUserImpl>() {
                        }.getType(),

                        new JsonDeserializer<ImmutablePair>() {
                            @Override
                            public ImmutablePair deserialize(
                                    JsonElement json, Type typeOfT,
                                    JsonDeserializationContext context) throws JsonParseException {
                                *//*Map<String, Object> treeMap = new HashMap<String, Object>();
                                JsonObject jsonObject = json.getAsJsonObject();
                                Set<Map.Entry<String, JsonElement>> entrySet = jsonObject.entrySet();
                                for (Map.Entry<String, JsonElement> entry : entrySet) {
                                    treeMap.put(entry.getKey(), entry.getValue());
                                }*//*
                                JsonObject jsonObject = json.getAsJsonObject();
                                return ImmutablePair.of(jsonObject.get("key").getAsString(), jsonObject.get("value").getAsString());
                            }
                        }).create();*/

        //Gson gson = new GsonBuilder().create();
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-*.xml");
        UUserService service = applicationContext.getBean(UUserService.class);
        // 获取序列化对象
        Config config = applicationContext.getBean(Config.class);
        UserSerializer serializer = config.getUserSerializer();

        UUser oyr = service.getUserByUsername("oyr");

        UserSerializer.UserData userData = serializer.serial(oyr, "aaa");
        String resultJson = JsonUtils.toJson(ResultVoUtils.ok(JsonUtils.toJson(userData)));
        System.out.println(resultJson);

        ResultVo resultVo = JsonUtils.toJavaBean(resultJson, ResultVo.class);

        UserDeserializer userDeserializer = UserDeserailizerFactory.create();
        // 判断返回值是否为空，不为空则序列化
        String userResult = (String) resultVo.getData();
        SSOUser user = StringUtil.isEmpty(userResult) ? null : userDeserializer.deserail(userResult);

        /*SSOUserImpl uUserService = JsonUtils.toJavaBean((String) resultVo.getData(), SSOUserImpl.class);
*/
    }

    @Test
    public void dateTest() throws InterruptedException {
        Date date = new Date();
        Thread.sleep(1000);
        Date now = new Date();
        System.out.println(now.compareTo(date));
    }

}
