package com.idatrix.resource.datareport.dao;

import com.idatrix.resource.common.utils.CommonConstants;
import com.idatrix.resource.common.utils.CommonUtils;
import com.idatrix.resource.common.utils.DateTools;
import com.idatrix.resource.datareport.po.DataUploadPO;
import com.idatrix.resource.datareport.po.SearchDataUploadPO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *	配置spring和junit整合，是为了启动时加载springIOC容器
 *	spring-test, junit
 */
@RunWith(SpringJUnit4ClassRunner.class)
/*	告诉junit spring配置文件 */
@ContextConfiguration({"classpath:META-INF/spring/catalog-root.xml"})
public class DataUploadDAOTest {
	@Resource
	private DataUploadDAO dataUploadDAO;

	@Test
	public void insertDataUploadRecordTest() {
/*		String user = "admin";

		DataUploadPO dataUploadPO = new DataUploadPO();

		dataUploadPO.setResourceId(1L);
		dataUploadPO.setStatus(CommonConstants.WAIT_IMPORT);
		dataUploadPO.setDataBatch(DateTools.getDate());
		dataUploadPO.setDataType(CommonConstants.DATA_TYPE_DB);

		//初始状态下如果是数据库类, 则实际入库数量由ETL接口返回; 文件类则固定为1
		if (dataUploadPO.getDataType().equals(CommonConstants.DATA_TYPE_DB))
			dataUploadPO.setImportCount(0L);
		else
			dataUploadPO.setImportCount(1L);

		String deptCode = "82345623";

		Long taskSeq = 1L;

		dataUploadPO.setTaskSeq(taskSeq);
		dataUploadPO.setImportTaskId(CommonUtils.generateETLTaskNum(taskSeq));

		dataUploadPO.setCreator(user);
		dataUploadPO.setCreateTime(new Date());
		dataUploadPO.setModifier(user);
		dataUploadPO.setModifyTime(new Date());

		dataUploadDAO.insertDataUploadRecord(dataUploadPO);*/
	}

	@Test
	public void getDataUploadRecordByConditionTest() {
/*		Map<String, String> condition = new HashMap<String, String>();

		condition.put("name", "身份证信息");

		List<SearchDataUploadPO> result = dataUploadDAO.getDataUploadRecordByCondition(condition);

		if (result != null && !result.isEmpty()) {

			for (SearchDataUploadPO model : result) {
				System.out.println();
				System.out.println("*************************************************************");
				System.out.println(model.toString());
				System.out.println("*************************************************************");
				System.out.println();
			}

		}*/
	}
}
