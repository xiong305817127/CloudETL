package com.idatrix.resource.basedata.service;

import com.idatrix.resource.basedata.vo.SourceServiceVO;
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
public class SourceServiceServiceTest {
	@Resource ISourceServiceService iSourceServiceService;

	@Test
	public void saveOrUpdateSourceService() {
		String user = "admin2";

		SourceServiceVO ssp = new SourceServiceVO();

		for (int i = 0; i < 10; i++) {
			ssp.setProviderId("2");
			ssp.setProviderName("公安局");
			ssp.setServiceName("测试服务106" + i);
			ssp.setServiceCode("ISO006" + i);
			ssp.setServiceType(CommonConstants.SERVICE_TYPE_SOAP);
			ssp.setRemark("测试服务备注2");
			ssp.setUrl("http://www.webxml.com.cn/WebServices/WeatherWebService.asmx?WSDL");
			ssp.setWsdl("WSDL");

			String msg = iSourceServiceService.saveOrUpdateSourceService(642L, user, ssp);

			System.out.println("******************************************************");
			System.out.println(msg);
			System.out.println("******************************************************");
		}

	}

	@Test
	public void getSourceServiceById() {
		Long id = 10L;

		SourceServiceVO svo = iSourceServiceService.getSourceServiceById(id);

		if (svo != null) {
			System.out.println("******************************************************");
			System.out.println(svo.toString());
			System.out.println("******************************************************");
		} else {
			System.out.println("******************************************************");
			System.out.println("当前源服务资源不存在");
			System.out.println("******************************************************");
		}
	}

	@Test
	public void getAllSourceService() {
		List<SourceServiceVO> servicesList = iSourceServiceService.getAllSourceService(642L);

		System.out.println("**********************************************************");
		if (servicesList != null) {
			for (SourceServiceVO model : servicesList) {
				System.out.println(model.toString());
			}
		}
		System.out.println("**********************************************************");
	}
}
