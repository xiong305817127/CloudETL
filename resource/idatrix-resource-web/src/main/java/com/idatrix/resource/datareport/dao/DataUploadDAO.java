package com.idatrix.resource.datareport.dao;

import com.idatrix.resource.datareport.po.DataUploadPO;
import com.idatrix.resource.datareport.po.SearchDataUploadPO;
import com.idatrix.resource.taskmanage.po.UploadTaskOverviewPO;
import com.idatrix.resource.taskmanage.vo.DescribeInfoVO;

import java.util.List;
import java.util.Map;

public interface DataUploadDAO {
	void insertDataUploadRecord(DataUploadPO dataUploadPO);

	List<SearchDataUploadPO> getDataUploadRecordByCondition(Map<String, String> conditionMap);

	DataUploadPO getDataUploadRecordById(Long id);

	void deleteDataUploadRecordById(Long id);

	DataUploadPO getWaitImportedDataUploadRecords(String status);

	DataUploadPO getExistedDataUpLoadRecords(Map<String, Object> conditionMap);

	void updateDataUploadRecordById(DataUploadPO dataUploadPO);

	DataUploadPO getDataUploadRecordByTaskExecId(String execId);

	Long getMaxTaskSeq();

	Long getTaskCount();

	List<UploadTaskOverviewPO> queryOverview(Map<String, String>con);

    List<DescribeInfoVO> getTaskInfoByMonth(Long num);
}
