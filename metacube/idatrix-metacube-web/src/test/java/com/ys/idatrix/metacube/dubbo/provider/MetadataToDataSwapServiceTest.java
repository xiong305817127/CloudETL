package com.ys.idatrix.metacube.dubbo.provider;

import com.ys.idatrix.metacube.api.beans.ResultBean;
import com.ys.idatrix.metacube.api.beans.dataswap.MetadataField;
import com.ys.idatrix.metacube.api.beans.dataswap.MetadataTable;
import com.ys.idatrix.metacube.api.beans.dataswap.SubscribeCrtTbResult;
import com.ys.idatrix.metacube.api.service.MetadataToDataSwapService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

/**
 * @Classname MetadataToDataSwapServiceTest
 * @Description 测试元数据提供给共享交换的接口
 * @Author robin
 * @Date 2019/3/13 10:00
 * @Version v1.0
 */

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class MetadataToDataSwapServiceTest {

    @Autowired
    private MetadataToDataSwapService swapService;

    @Test
    public void testDataSwap(){
        String userName = "robin";
        MetadataTable metadataTable = new MetadataTable();
        metadataTable.setMetaid(324);
        metadataTable.setSchemeId(40L);
        metadataTable.setPreviousMetaid(79);

        List< MetadataField > metadataFields = Arrays.asList(
                new MetadataField("asdasd", "float"),
                new MetadataField("ds_batch","varchar"),
                new MetadataField("ds_sync_time", "datetime"),
                new MetadataField("ds_sync_flag","varchar" ));
        ResultBean<SubscribeCrtTbResult> result = swapService.createTableBySubscribe(userName, metadataTable, metadataFields);
        if(result.isSuccess()){
            log.info("新建MetaId是{}", result.getData().getMetaId());
        }else{
            log.error("自动负责表格数据出错 {}", result.getMsg());
        }
    }

}
