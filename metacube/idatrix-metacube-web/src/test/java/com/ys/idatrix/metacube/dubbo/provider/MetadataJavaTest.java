package com.ys.idatrix.metacube.dubbo.provider;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Classname MetadataJavaTest
 * @Description GC测试
 * @Author robin
 * @Date 2019/3/19 15:17
 * @Version v1.0
 */
@Slf4j
@RunWith(SpringRunner.class)
public class MetadataJavaTest {

    @Test
    public void testJavaLanguage(){
        String[] valueArrays = new String[1000];
        while(true){
            try {
                valueArrays = new String[100000000];
                Thread.sleep(1);
            }catch (Exception e){
                e.printStackTrace();
            }
            log.info("我就是打印怎么着。。。");
        }
    }


    /**
     * 去除字符串首尾出现的某个字符.
     * @param source 源字符串.
     * @param element 需要去除的字符.
     * @return String.
     */
    private String trimFirstAndLastChar(String source,char element){
        boolean beginIndexFlag = true;
        boolean endIndexFlag = true;
        do{
            int beginIndex = source.indexOf(element) == 0 ? 1 : 0;
            int endIndex = source.lastIndexOf(element) + 1 == source.length() ? source.lastIndexOf(element) : source.length();
            source = source.substring(beginIndex, endIndex);
            beginIndexFlag = (source.indexOf(element) == 0);
            endIndexFlag = (source.lastIndexOf(element) + 1 == source.length());
        } while (beginIndexFlag || endIndexFlag);
        return source;

    }

    @Test
    public void testDD(){
        String k = "'中国'";
        if(k.startsWith("'") && k.endsWith("'")) {
            System.out.println(trimFirstAndLastChar(k, '\''));
        }
    }
}
