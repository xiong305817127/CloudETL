package com.idatrix.resource.catalog.service;

import com.idatrix.resource.catalog.vo.CatalogNodeVO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.List;

/**
 *	配置spring和junit整合，是为了启动时加载springIOC容器
 *	spring-test, junit
 */
@RunWith(SpringJUnit4ClassRunner.class)
/*	告诉junit spring配置文件 */
@ContextConfiguration({"classpath:META-INF/spring/catalog-root.xml"})
public class CatalogNodeTest {

    @Resource
    private ICatalogClassifyService catalogClassifyService;

    @Test
    public void nodeSubtreeTest(){
        List<CatalogNodeVO> noList=catalogClassifyService.getCatalogNodeSubtree(463L, "etl63", 5167L);
        System.out.println(noList.toString());
    }
}
