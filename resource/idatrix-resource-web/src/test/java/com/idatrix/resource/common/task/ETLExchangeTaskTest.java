package com.idatrix.resource.common.task;

import com.idatrix.resource.basedata.dao.SystemConfigDAO;
import com.idatrix.resource.datareport.dao.DataUploadDAO;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/**
 *	配置spring和junit整合，是为了启动时加载springIOC容器
 *	spring-test, junit
 */
@RunWith(SpringJUnit4ClassRunner.class)
/*	告诉junit spring配置文件 */
@ContextConfiguration({"classpath:META-INF/spring/catalog-root.xml"})
public class ETLExchangeTaskTest {


	@Resource
	private SystemConfigDAO systemConfigDAO;

	@Resource
	private ExchangeETLTask exchangeETLTask;

	@Autowired
	private DataUploadDAO dataUploadDAO;

	@Test
	public void getDbInfoByMetaId() {
//		long bindTableId = 6372;
//		DataBaseInfo dataBaseInfo = cloudETLService.getDbInfoByMetaId(bindTableId);
//
//		if (dataBaseInfo != null) {
//			System.out.println("***********************************************************************");
//			System.out.println(dataBaseInfo.toString());
//			System.out.println("***********************************************************************");
//		}
	}

	@Test
	public void startETLTaskTest() {

//	    exchangeETLTask.startTask();
	}
}
