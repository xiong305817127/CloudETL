package com.idatrix.resource.datareport.service;

import com.idatrix.resource.common.Exception.CommonServiceException;
import com.idatrix.resource.common.utils.ResultPager;
import com.idatrix.resource.datareport.vo.DataUploadTotalVO;
import com.idatrix.resource.datareport.vo.ETLTaskDetailVO;
import com.idatrix.resource.datareport.vo.SearchDataUploadVO;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public interface IDataUploadService {
	Long saveOrUpdateUploadDataForDB(Long resourceId, String dataBatch, Integer formatType,
		CommonsMultipartFile files, String userName) throws RuntimeException, CommonServiceException;

	List<Map<String, Object>> saveOrUpdateUploadDataForFILE(Long resourceId, Integer formatType,
        CommonsMultipartFile[] files, String userName) throws RuntimeException, CommonServiceException;

	//根据资源ID + 文件展示名称判断当前上传的文件是否已存在
	String isExistedResourceFiles(Long resourceId, String[] pubFileName);

	void downLoadExcelTemplate(Long resourceId, HttpServletResponse response) throws CommonServiceException;

	ResultPager<SearchDataUploadVO> getDataUploadRecordByCondition(Map<String, String> conditionMap, String pubFileName,
																   Integer pageNum, Integer pageSize);

	void deleteDataUploadRecordById(Long id) throws CommonServiceException;

//	ResultPager<DataUploadDetailVO> getTempUploadFileListsByParentId(Long parentId, Integer pageNum, Integer pageSize);

	void updateUploadDataForFILE(DataUploadTotalVO dataUploadTotalVO, String userName) throws CommonServiceException;

	ResultPager<ETLTaskDetailVO> getETLTaskDetailInfoById(Long id, String userName) throws CommonServiceException;
}
