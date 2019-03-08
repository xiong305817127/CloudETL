package com.idatrix.resource.catalog.dao;

import com.idatrix.resource.catalog.po.MonthStatisticsPO;
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
public class MonthStatisticsDAOTest {

	@Resource
	private MonthStatisticsDAO monthStatisticsDAO;

	@Test
	public void getMonthlyTotalAmount() {
		try {
			List<MonthStatisticsPO> monthlyAmountList =  monthStatisticsDAO.getMonthlyTotalAmount(2);

			for (MonthStatisticsPO model : monthlyAmountList) {
				System.out.println(model.getMonth());
				System.out.println(model.getPubCount());
				System.out.println(model.getSubCount());
				System.out.println(model.getRegCount());
			}
		} catch (Exception e) {
			System.out.println("Unexpected Exception!" + e.getMessage());
		}
	}
}
