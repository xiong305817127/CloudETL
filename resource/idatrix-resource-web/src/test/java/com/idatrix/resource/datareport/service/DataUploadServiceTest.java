package com.idatrix.resource.datareport.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *	配置spring和junit整合，是为了启动时加载springIOC容器
 *	spring-test, junit
 */
@RunWith(SpringJUnit4ClassRunner.class)
/*	告诉junit spring配置文件 */
@ContextConfiguration({"classpath:META-INF/spring/catalog-root.xml"})
public class DataUploadServiceTest {

	@Autowired
	private IDataUploadService iDataUploadService;

	@Test
	public void uploadReportedDataProcessTest() {
//		String userName = "etl3";
//
//		Long resourceId = 2L;
//		String pubFileName = "沧州市-常住人口信息-2018年上半年普查";
//		String dataBatch = DateTools.getDate();
//		String dataType = CommonConstants.DATA_TYPE_FILE;
//		String deptCode = "82345623";
//		CommonsMultipartFile[] files = null;
//
//		Integer formatType = 4;
//
//		Long existedFileId = iDataUploadService.isExistedResourceFile(resourceId, pubFileName);
//
//		if (existedFileId != null)
//			System.out.println("已存在上传文件" + existedFileId);
//
//		try {
//			iDataUploadService.uploadReportedDataProcess(resourceId, dataBatch, formatType, dataType, deptCode, files,
//					userName);
//		} catch (CommonServiceException e) {
//			System.out.println("***************************************************************************");
//			System.out.println("错误代码: " + e.getErrorCode() + ", 错误原因: " + e.getMessage());
//			System.out.println("***************************************************************************");
//		}
	}
}
