package com.idatrix.resource.basedata.service;

import com.idatrix.resource.basedata.vo.ServiceVO;
import com.idatrix.resource.common.utils.CommonConstants;
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
public class ServiceServiceTest {
	@Resource IServiceService iServiceService;

	@Test
	public void saveOrUpdateSourceService() {
		String user = "admin2";

		ServiceVO ssp = new ServiceVO();

		ssp.setProviderId("2");
		ssp.setProviderName("公安局");
		ssp.setServiceName("测试服务110");
		ssp.setServiceCode("ISO010");
		ssp.setServiceType(CommonConstants.SERVICE_TYPE_SOAP);
		ssp.setRemark("测试服务备注2");
		ssp.setUrl("http://www.webxml.com.cn/WebServices/WeatherWebService.asmx?WSDL");

		String msg = iServiceService.saveOrUpdateService(642L, user, ssp);

		System.out.println("******************************************************");
		System.out.println(msg);
		System.out.println("******************************************************");
	}

	@Test
	public void getAllService() {
		List<ServiceVO> servicesList = iServiceService.getAllService(642L);

		System.out.println("**********************************************************");
		if (servicesList != null) {
			for (ServiceVO model : servicesList) {
				System.out.println(model.toString());
			}
		}
		System.out.println("**********************************************************");
	}

}
