package com.ys.idatrix.metacube.dubbo.provider;

import com.alibaba.fastjson.JSON;
import com.ys.idatrix.metacube.api.beans.*;
import com.ys.idatrix.metacube.api.service.MetadataToDataAnalysisService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.List;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class MetadataToDataAnalysisServiceTest {

    @Autowired
    private MetadataToDataAnalysisService dataAnalysisService;

    @Test
    public void registerOrUpdatePlatformDatabaseInfo() {
    }

    @Test
    public void testGetDatabaseResource() {
        ResultBean<List<MetaDbResourceDTO>> result = dataAnalysisService.getDatabaseResource("new88");
        if (result.isSuccess()) {
            List<MetaDbResourceDTO> dbResourceList = result.getData();
            System.out.println(JSON.toJSONString(dbResourceList, true));
        } else {
            System.out.println(result.getMsg());
        }
    }


    @Test
    public void testGetTablesAndFields() {
        ResultBean<HashMap<String, List<MetaFieldDTO>>> result = dataAnalysisService.getTablesAndFields("wzl1", 6L);
        if (result.isSuccess()) {
            HashMap<String, List<MetaFieldDTO>> tbAndFieldList = result.getData();
            System.out.println(JSON.toJSONString(tbAndFieldList, true));
        } else {
            System.out.println(result.getMsg());
        }
    }

    @Test
    public void testGetEsIndices() {
        ResultBean<List<MetaEsDTO>> result = dataAnalysisService.getEsIndices("wzl1");
        if (result.isSuccess()) {
            List<MetaEsDTO> esList = result.getData();
            System.out.println(JSON.toJSONString(esList, true));
        } else {
            System.out.println(result.getMsg());
        }
    }


    @Test
    public void testGetHdfsPaths() {
        ResultBean<List<MetaHdfsDTO>> result = dataAnalysisService.getHdfsPaths("new88");
        if (result.isSuccess()) {
            List<MetaHdfsDTO> hdfsList = result.getData();
            System.out.println(JSON.toJSONString(hdfsList, true));
        } else {
            System.out.println(result.getMsg());
        }
    }


    @Test
    public void testGetDatabaseInfo() {
        ResultBean<MetaDatabaseDTO> result = dataAnalysisService.getDatabaseInfo("wzl1", 6L);
        if (result.isSuccess()) {
            MetaDatabaseDTO data = result.getData();
            System.out.println(JSON.toJSONString(data, true));
        } else {
            System.out.println(result.getMsg());
        }
    }


    @Test
    public void testGetTbPermiss() {
        ResultBean<ActionTypeEnum> result = dataAnalysisService.getTbPermiss("wzl1", 6L, "cs1");
        if (result.isSuccess()) {
            ActionTypeEnum data = result.getData();
            System.out.println(JSON.toJSONString(data, true));
        } else {
            System.out.println(result.getMsg());
        }
    }


}