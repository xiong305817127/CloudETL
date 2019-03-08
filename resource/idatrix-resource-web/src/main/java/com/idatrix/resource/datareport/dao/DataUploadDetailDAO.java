package com.idatrix.resource.datareport.dao;

import com.idatrix.resource.datareport.po.DataUploadDetailPO;

import java.util.List;
import java.util.Map;

public interface DataUploadDetailDAO {
	void insertDataUploadDetail(DataUploadDetailPO dataUploadDetailPO);

	void insertDataUploadDetailBatchList(List<DataUploadDetailPO> list);

	void updateDataUploadDetailById(DataUploadDetailPO dataUploadDetailPO);

	List<DataUploadDetailPO> getUploadDetailsByParentId(Long parentId);

	List<DataUploadDetailPO> getUploadDetailsByResourceId(Long resourceId);

	void deleteUploadDetailById(Long id);

	DataUploadDetailPO getDataUploadDetailByCondition(Map<String, String> condition);

	void deleteUploadDetailsByParentId(Long parentId);
}
