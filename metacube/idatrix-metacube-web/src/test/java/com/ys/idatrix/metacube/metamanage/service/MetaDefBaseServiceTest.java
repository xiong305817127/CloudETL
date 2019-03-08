package com.ys.idatrix.metacube.metamanage.service;

import com.ys.idatrix.graph.service.api.def.DatabaseType;
import com.ys.idatrix.metacube.metamanage.domain.Metadata;
import com.ys.idatrix.metacube.metamanage.mapper.MetadataMapper;
import com.ys.idatrix.metacube.metamanage.vo.request.MetaDefHDFSVO;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Classname MetaDefBaseServiceTest
 * @Description 元数据定义共用服务测试
 * @Author robin
 * @Date 2019/2/19 14:31
 * @Version v1.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class MetaDefBaseServiceTest {

    @Autowired
    private IMetaDefBaseService metaDefBaseService;

    @Autowired
    private MetadataMapper metadataMapper;

    @Autowired
    private IMetaDefHDFSService metaDefHDFSService;

    @Test
    public void testUpdateChangeToGraph(){
        Metadata data = metadataMapper.selectByPrimaryKey(100L);
        Assert.assertNotNull(data);
        metaDefBaseService.updateMetadataChangeInfoToGraph(DatabaseType.HDFS, data);
    }


    @Test
    public void testUpdateDeleteToGraph(){
        metaDefBaseService.updateMetadataDeleteInfoToGraph(DatabaseType.HDFS, 100L);
    }

    @Test
    public void testhdfsService(){
        MetaDefHDFSVO dfsVO = new MetaDefHDFSVO();
        dfsVO.setName("robin_test_service");
        dfsVO.setSchemaId(34L);
        dfsVO.setIdentification("/ki/ki");
        dfsVO.setVersion(1);
        metaDefHDFSService.saveExec(463L, "robin", dfsVO);
    }
}
