package com.idatrix.resource.common.task;

/**
 * Created by Administrator on 2018/8/17.
 */

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 *	配置spring和junit整合，是为了启动时加载springIOC容器
 *	spring-test, junit
 */
@RunWith(SpringJUnit4ClassRunner.class)
/*	告诉junit spring配置文件 */
@ContextConfiguration({"classpath:META-INF/spring/catalog-root.xml"})
public class StatisticsTaskTest {

    @Resource
    private StatisticsTask satisticsTask;

    /*获取最近几个月的yyyyMM 月份数*/
    private List<String> getRecentMonthStr(int months){
        List<String> monthStr = new ArrayList<String>();
        if(months<=0){
            return monthStr;
        }
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
        Date date = new Date();
        String dateString = sdf.format(cal.getTime());

        for (int index = 0; index<months; index++) {

            dateString = sdf.format(cal.getTime());
            System.out.println(dateString);
            monthStr.add(dateString);
            cal.add(Calendar.MONTH, -1);
        }
        Collections.reverse(monthStr);
        return monthStr;
    }

    @Test
    public void testMonth(){
        System.out.println(getRecentMonthStr(6).toString());
    }

    @Test
    public void startTaskTest() {

        satisticsTask.startTask();
    }
}
