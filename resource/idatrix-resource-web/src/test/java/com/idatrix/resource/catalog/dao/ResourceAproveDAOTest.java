package com.idatrix.resource.catalog.dao;

import static com.idatrix.resource.common.utils.ResourceTools.ResourceStatus.WAIT_PUB_APPROVE;

import com.idatrix.resource.catalog.po.ResourceApprovePO;
import com.idatrix.resource.common.utils.DateTools;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *	配置spring和junit整合，是为了启动时加载springIOC容器
 *	spring-test, junit
 */
@RunWith(SpringJUnit4ClassRunner.class)
/*	告诉junit spring配置文件 */
@ContextConfiguration({"classpath:META-INF/spring/catalog-root.xml"})
public class ResourceAproveDAOTest {

    @Resource
    private ResourceApproveDAO resourceApproveDAO;

    @Test
    public void getMonthlyTotalAmount() {

        Map<String, String> con = new HashMap<String, String>();
        con.put("status", "\"pub_success\",\"recall\"");
        con.put("currentStatus", WAIT_PUB_APPROVE.getStatusCode());
        try {
            List<ResourceApprovePO> resourceApprovePOList =  resourceApproveDAO.getMaintainResourceByCondition(con);

            for (ResourceApprovePO raP : resourceApprovePOList) {
                System.out.println(raP.getResourceId());
                System.out.println(raP.getApprover());
                System.out.println(raP.getApproverName());
                System.out.println(DateTools.formatDate(raP.getApproveTime()));
            }
        } catch (Exception e) {
            System.out.println("Unexpected Exception!" + e.getMessage());
        }
    }
}