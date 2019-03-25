package com.idatrix.resource.basedata.dao;

import com.idatrix.resource.basedata.po.SourceServicePO;
import com.idatrix.resource.common.utils.CommonConstants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.Date;

/**
 *	配置spring和junit整合，是为了启动时加载springIOC容器
 *	spring-test, junit
 */
@RunWith(SpringJUnit4ClassRunner.class)
/*	告诉junit spring配置文件 */
@ContextConfiguration({"classpath:META-INF/spring/catalog-root.xml"})
public class SourceServiceDAOTest {
	@Resource
	private SourceServiceDAO sourceServiceDAO;

	@Test
	public void insertSourceService() {
		try {
			SourceServicePO ssp = new SourceServicePO();

			ssp.setProviderId("1");
			ssp.setCreator("admin");
			ssp.setCreateTime(new Date());
			ssp.setModifier("admin");
			ssp.setModifyTime(new Date());
			ssp.setServiceName("测试服务001");
			ssp.setServiceCode("ISO001");
			ssp.setServiceType(CommonConstants.SERVICE_TYPE_SOAP);
			ssp.setRemark("测试服务备注");
			ssp.setWsdl("WSDL".getBytes());

			sourceServiceDAO.insert(ssp);
		} catch (Exception e) {
			System.out.println("Got u here!");
		}

	}

	@Test
	public void getSourceServiceByServiceCode() {
		String serviceCode = "ISO001";
		SourceServicePO sourceServicePO = sourceServiceDAO.getSourceServiceByServiceCode(serviceCode);

		if (sourceServicePO != null)
			System.out.println(sourceServicePO.getServiceName());
	}

	@Test
	public void updateById() {
		String serviceCode = "ISO001";
		SourceServicePO ssp = sourceServiceDAO.getSourceServiceByServiceCode(serviceCode);

		ssp.setServiceName("测试服务002");
		ssp.setServiceCode("ISO001");
		ssp.setRemark("测试服务备注2");
		ssp.setModifyTime(new Date());
		sourceServiceDAO.updateSourceService(ssp);
	}
}
