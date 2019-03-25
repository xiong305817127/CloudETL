package com.idatrix.resource.portal.service;

import com.idatrix.resource.subscribe.utils.DataMaskingTypeEnum;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by Administrator on 2018/12/29.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:META-INF/spring/catalog-root.xml"})
public class StatisticsDailyServiceTest {

    @Autowired
    private IStatisticsDailyService statisticsDailyService;

    @Autowired
    private IStatisticsDeptService statisticsDeptService;

    @Test
    public void testSetValue(){
        try {
            statisticsDailyService.saveStatisticsDaily(8L, 10L );
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void testVerifyType(){
        DataMaskingTypeEnum.verifyDataMaskingType("MYSQL", "VARCHAR(12)");
        DataMaskingTypeEnum.verifyDataMaskingType("MYSQL", "VARCHAR");
    }


    @Test
    public void testDeptSetValue(){
        try {
            statisticsDeptService.saveDeptShareInfo(12L, "公安局", 8L , 1L);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
