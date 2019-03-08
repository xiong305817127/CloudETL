package com.idatrix.resource.subscribe.service;

import com.ys.idatrix.metacube.api.bean.dataanalysis.DbStorageSystemInfo;
import com.ys.idatrix.metacube.api.service.dataanalysis.DataAnalysisService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by Administrator on 2018/8/3.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:META-INF/spring/catalog-root.xml"})
public class DataAnalysisServiceTest {

    @Autowired
    private DataAnalysisService dataAnalysisService;

    @Test
    public void testDataAnalysis(){

        DbStorageSystemInfo dbStorageSystemInfo = dataAnalysisService.getdbSystemType(null, "1213");
        System.out.println(dbStorageSystemInfo.getDbname());
        System.out.println(dbStorageSystemInfo.getIp());
        System.out.println(dbStorageSystemInfo.getPort());
        System.out.println(dbStorageSystemInfo.getType());
        System.out.println(dbStorageSystemInfo.getCreatetime());
        System.out.println(dbStorageSystemInfo.getDsname());
    }


    @Test
    public void testCopyDBTable(){

    }

    private void copyDBTable(){

    }



}
