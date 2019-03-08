package com.idatrix.resource.catalog.service;

import com.idatrix.resource.catalog.vo.MonthStatisticsVO;
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
public class OverviewServiceTest {
	@Resource
	private IOverviewService iOverviewService;

	@Test
	public void getMonthlyTotalAmount() {
		int months = 6;

		List<MonthStatisticsVO> list = iOverviewService.getMonthlyTotalAmount(months);

		for (MonthStatisticsVO model : list) {
			System.out.print(model.getMonthName() + ": ");
			System.out.print(model.getRegCount() + ", ");
			System.out.print(model.getPubCount() + ", ");
			System.out.println(model.getSubCount());
		}
	}

//	@Test
//	public void getPublishedResourcesByCondition() {
//		Map<String, String> condition = new HashMap<String, String>();
//
//		condition.put("name", "四级证");
//		condition.put("status", "");
//
//		ResultPager<ResourceConfigVO> pager
//				= iOverviewService.getPublishedResourcesByCondition("7", condition, 1, 10);
//
//		List<ResourceConfigVO> list = pager.getResults();
//
//		for (ResourceConfigVO model : list) {
//			System.out.println(model.getCatalogName());
//		}
//	}
}
