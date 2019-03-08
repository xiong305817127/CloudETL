package com.idatrix.unisecurity.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Created by Administrator on 2018/8/2.
 */
public class JsonUtils {

    private final static ObjectMapper objectMapper = new ObjectMapper();

    //javaBean to json
    public static String toJson(Object object){
        if(object==null){
            return null;
        }
        try {
            String json = objectMapper.writeValueAsString(object);
            return json;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    //json to javaBean
    public static <T> T toJavaBean(String json, Class<T> classType){
        if(json==null){
            return null;
        }
        try {
            T t = objectMapper.readValue(json, classType);
            return t;
        }  catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*public static void main(String[] args) {
        Person person = new Person();
        person.setName("sarah");
        person.setAge(10);
        person.setSex("male");
        person.setTelephone("12345678");
        List<String> friends = new ArrayList<String>();
        friends.add("mattew");
        friends.add("phoenix");
//		String[] friends = new String[2];
//		friends[0] = "mattew";
//		friends[1] = "phoenix";
        person.setFriends(friends);
        System.out.println(toJson(person));
        String json = "{\"name\":\"sarah\",\"age\":10,\"sex\":\"male\",\"telephone\":\"12345678\",\"friends\":[\"mattew\",\"phoenix\"]}";
        System.out.println(toJavaBean(json, Person.class));

        String json1 = "{\n" +
                "\t\"table\": [{\n" +
                "\t\t\"name\": \"product\",\n" +
                "\t\t\"keyColumn\": \"product_key_id\",\n" +
                "\t\t\"foreignKey\": \"keyColumn\",\n" +
                "\t\t\"sourceId\": \"tableId1\",\n" +
                "\t\t\"targetId\": \"tableid2\"\n" +
                "\t}, {\n" +
                "\t\t\"name\": \"product1\",\n" +
                "\t\t\"keyColumn\": \"product_key_id1\",\n" +
                "\t\t\"foreignKey\": \"keyColumn1\",\n" +
                "\t\t\"sourceId\": \"tableId1\",\n" +
                "\t\t\"targetId\": \"tableid3\"\n" +
                "\t}]\n" +
                "}";
        System.out.println(toJavaBean(json1, TableRelation.class));
    }*/
}
