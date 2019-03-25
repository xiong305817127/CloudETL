package com.idatrix.resource.datareport.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.idatrix.resource.basedata.dao.SystemConfigDAO;
import com.idatrix.resource.basedata.po.SystemConfigPO;
import com.idatrix.resource.basedata.service.ISystemConfigService;
import com.idatrix.resource.catalog.dao.ResourceColumnDAO;
import com.idatrix.resource.catalog.dao.ResourceConfigDAO;
import com.idatrix.resource.catalog.po.ResourceColumnPO;
import com.idatrix.resource.catalog.po.ResourceConfigPO;
import com.idatrix.resource.common.Exception.CommonServiceException;
import com.idatrix.resource.common.cache.SequenceNumberManager;
import com.idatrix.resource.common.utils.*;
import com.idatrix.resource.common.vo.ExcelUtilsInfo;
import com.idatrix.resource.datareport.dao.DataUploadDAO;
import com.idatrix.resource.datareport.dao.DataUploadDetailDAO;
import com.idatrix.resource.datareport.dao.ResourceFileDAO;
import com.idatrix.resource.datareport.po.DataUploadDetailPO;
import com.idatrix.resource.datareport.po.DataUploadPO;
import com.idatrix.resource.datareport.po.ResourceFilePO;
import com.idatrix.resource.datareport.po.SearchDataUploadPO;
import com.idatrix.resource.datareport.service.IDataUploadService;
import com.idatrix.resource.datareport.vo.*;
import com.ys.idatrix.cloudetl.subscribe.api.dto.QueryJobDto;
import com.ys.idatrix.cloudetl.subscribe.api.dto.SubscribeResultDto;
import com.ys.idatrix.cloudetl.subscribe.api.dto.parts.SubscribeMeasureDto;
import com.ys.idatrix.cloudetl.subscribe.api.service.SubscribeService;
import com.ys.idatrix.db.api.common.RespResult;
import com.ys.idatrix.db.api.hdfs.service.HdfsUnrestrictedService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

@Slf4j
@Transactional
@Service("dataUploadService")
public class DataUploadServiceImpl implements IDataUploadService {
	private static final Logger LOG = LoggerFactory.getLogger(IDataUploadService.class);

	@Autowired
	public DataUploadServiceImpl(DataUploadDAO dataUploadDAO,
								 DataUploadDetailDAO dataUploadDetailDAO,
								 ResourceFileDAO resourceFileDAO,
								 ResourceColumnDAO resourceColumnDAO,
								 HdfsUnrestrictedService hdfsUnrestrictedDao,
								 SystemConfigDAO systemConfigDAO,
								 SequenceNumberManager sequenceNumberManager,
								 SubscribeService subscribeService,
                                 ResourceConfigDAO resourceConfigDAO,
                                 ISystemConfigService systemConfigService) {
		this.dataUploadDAO = dataUploadDAO;
		this.dataUploadDetailDAO = dataUploadDetailDAO;
		this.resourceFileDAO = resourceFileDAO;
		this.resourceColumnDAO = resourceColumnDAO;
		this.hdfsUnrestrictedDao = hdfsUnrestrictedDao;
		this.systemConfigDAO = systemConfigDAO;
		this.sequenceNumberManager = sequenceNumberManager;
		this.subscribeService = subscribeService;
		this.resourceConfigDAO= resourceConfigDAO;
		this.systemConfigService = systemConfigService;
	}

	private DataUploadDAO dataUploadDAO;
	private DataUploadDetailDAO dataUploadDetailDAO;
	private ResourceFileDAO resourceFileDAO;
	private ResourceColumnDAO resourceColumnDAO;
	private HdfsUnrestrictedService hdfsUnrestrictedDao;
	private SystemConfigDAO systemConfigDAO;
	private SequenceNumberManager sequenceNumberManager;
	private SubscribeService subscribeService;
	private ISystemConfigService systemConfigService;
	private ResourceConfigDAO resourceConfigDAO;



	@Override
	@Transactional(rollbackFor = RuntimeException.class)
	public Long saveOrUpdateUploadDataForDB(Long rentId, Long resourceId, String dataBatch, Integer formatType,
		CommonsMultipartFile file, String userName) throws RuntimeException, CommonServiceException {
		Long rowNum = 0L;

		String suffix = "";

		//如果文件不为空, 则获取文件后缀名, 重新将文件命名为UUID.后缀名, 组成新的文件名称
        SystemConfigPO systemConfigPO = systemConfigService.getSystemConfig();
		if (systemConfigPO == null)
			throw new CommonServiceException(CommonConstants.EC_NULL_VALUE, "系统参数还未配置，请在先配置再使用");

		//获取上传文件的后缀名
		String originFileName = file.getOriginalFilename();
		String[] nameSplit = originFileName.split("\\.");
		suffix += nameSplit[nameSplit.length - 1];

		//校验文件大小以及类型
		validateUploadFile(file, systemConfigPO, formatType, CommonConstants.DATA_TYPE_DB, suffix);
		String hdfsPath = systemConfigPO.getOriginFileRoot();
		String uuidName = CommonUtils.generateUUID() + "." + suffix;

		//上传至HDFS
		rowNum = uploadFileToHDFS(file, CommonConstants.DATA_TYPE_DB, hdfsPath, uuidName);

		DataUploadDetailPO dataUploadDetailPO
				= generateDataUploadDetailPO(uuidName, file.getOriginalFilename(), suffix, userName, file.getSize());

		Long parentId = saveDataUploadRecord(rentId, resourceId, dataBatch, CommonConstants.DATA_TYPE_DB, userName);
		dataUploadDetailPO.setParentId(parentId);
		//保存数据上报记录
		dataUploadDetailDAO.insertDataUploadDetail(dataUploadDetailPO);

		//如果是数据库类上报, 则待处理Excel文件行数作为返回值传递给前端
		return rowNum;
	}

	@Override
	@Transactional(rollbackFor = RuntimeException.class)
	public List<Map<String, Object>> saveOrUpdateUploadDataForFILE(Long resourceId, Integer formatType,
		CommonsMultipartFile[] files, String userName) throws RuntimeException, CommonServiceException {

		if (files != null) {
			if (files.length > 10)
				throw new CommonServiceException(CommonConstants.EC_INCORRECT_VALUE, "上传文件数量不允许超过十个");

			//如果文件不为空, 则获取文件后缀名, 重新将文件命名为UUID.后缀名, 组成新的文件名称
            SystemConfigPO systemConfigPO = systemConfigService.getSystemConfig();
			if (systemConfigPO == null)
				throw new CommonServiceException(CommonConstants.EC_NULL_VALUE, "系统参数还未配置，请在先配置再使用");

			List<DataUploadDetailPO> dataUploadDetailPOList = new ArrayList<DataUploadDetailPO>();
			for (CommonsMultipartFile file : files) {
				String suffix = "";

				//获取上传文件的后缀名
				String originFileName = file.getOriginalFilename();
				String[] nameSplit = originFileName.split("\\.");
				suffix += nameSplit[nameSplit.length - 1];

				//校验文件大小以及类型
				validateUploadFile(file, systemConfigPO, formatType, CommonConstants.DATA_TYPE_FILE, suffix);
				String hdfsPath = systemConfigPO.getOriginFileRoot();
				String uuidName = CommonUtils.generateUUID() + "." + suffix;

				//上传至HDFS
				uploadFileToHDFS(file, CommonConstants.DATA_TYPE_FILE, hdfsPath, uuidName);
				DataUploadDetailPO dataUploadDetailPO
						= generateDataUploadDetailPO(uuidName, file.getOriginalFilename(), suffix, userName,
						file.getSize());

				if (dataUploadDetailPO.getId() == null)
					dataUploadDetailDAO.insertDataUploadDetail(dataUploadDetailPO);
				else
					dataUploadDetailDAO.updateDataUploadDetailById(dataUploadDetailPO);

				dataUploadDetailPOList.add(dataUploadDetailPO);
			}

			//如果是文件类上报, 则上报记录主键作为返回值传递给前端, 以备用于下一步查找已上传文件列表
			List<Map<String, Object>> tempUploadFileList = new ArrayList<Map<String, Object>>();
			for (DataUploadDetailPO model : dataUploadDetailPOList) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("id", model.getId());
				map.put("pubFileName", model.getPubFileName());

				Map<String, Object> condition = new HashMap<String, Object>();
				condition.put("resourceId", resourceId);
				condition.put("pubFileName", model.getPubFileName());
				ResourceFilePO resourceFilePO = resourceFileDAO.isExistedResourceFile(condition);

				map.put("isExisted", resourceFilePO != null ? true : false);
				tempUploadFileList.add(map);
			}
			return tempUploadFileList;
		}
		return null;
	}

	private DataUploadDetailPO generateDataUploadDetailPO(String originFileName, String fileName, String suffix,
														  String userName, long fileSize) {
		Map<String, String> condition = new HashMap<String, String>();
		condition.put("pubFileName", fileName);
		condition.put("creator", userName);

		DataUploadDetailPO dataUploadDetailPO
				= (dataUploadDetailDAO.getDataUploadDetailByCondition(condition) == null ? new DataUploadDetailPO() :
				dataUploadDetailDAO.getDataUploadDetailByCondition(condition));

		dataUploadDetailPO.setOriginFileName(originFileName);

		//dataUploadDetailPO.setFileSize(CommonUtils.getFileSizeStr(fileSize));
        dataUploadDetailPO.setFileSize(Long.valueOf(fileSize).toString());
		dataUploadDetailPO.setFileType(suffix);

		dataUploadDetailPO.setPubFileName(fileName);
		dataUploadDetailPO.setCreator(userName);
		dataUploadDetailPO.setCreateTime(new Date());
		dataUploadDetailPO.setModifier(userName);
		dataUploadDetailPO.setModifyTime(new Date());

		return dataUploadDetailPO;
	}

	//根据资源ID + 文件展示名称判断当前上传的文件是否已存在
	@Override
	public String isExistedResourceFiles(Long resourceId, String[] pubFileName) {

		StringBuffer existedFiles = new StringBuffer();

		if (pubFileName != null && pubFileName.length > 0) {
			for (int i = 0; i < pubFileName.length; i++) {
				Map<String, Object> condition = new HashMap<String, Object>();
				condition.put("resourceId", resourceId);
				condition.put("pubFileName", pubFileName[i]);

				ResourceFilePO resourceFilePO = resourceFileDAO.isExistedResourceFile(condition);
				if (resourceFilePO != null) {
					existedFiles.append(pubFileName[i]).append(" ");
				}
			}
		}
		return existedFiles.toString();
	}

	//根据上传类型以及文件后缀名进行校验
	private boolean isValidateDataFormat(Integer formatType, String fileName, String suffix) throws
			CommonServiceException {
		boolean result = false;

		if (suffix.equals("") || suffix.length() > 4)
			throw new CommonServiceException(CommonConstants.EC_INCORRECT_VALUE, "上报文件" + fileName +
					"文件后缀名" + suffix + "为空或不合法");

		String tempSuffix = suffix.toLowerCase();
		if (formatType.equals(ResourceTools.FormatType.DB.getTypeValue())
			&& (tempSuffix.equals(CommonConstants.SUFFIX_XLS) || tempSuffix.equals(CommonConstants.SUFFIX_XLSX)))
			result = true;

		if (formatType.equals(ResourceTools.FormatType.FORM.getTypeValue())
				&& CommonConstants.SPREADSHEET.contains(tempSuffix))
			result = true;

		if (formatType.equals(ResourceTools.FormatType.FILE.getTypeValue())
				&& CommonConstants.DOCUMENT.contains(tempSuffix))
			result = true;

		if (formatType.equals(ResourceTools.FormatType.IMAGE.getTypeValue())
				&& CommonConstants.IMAGE.contains(tempSuffix))
			result = true;

		if (formatType.equals(ResourceTools.FormatType.STEAM_MEDIA.getTypeValue())
				&& CommonConstants.STREAM.contains(tempSuffix))
			result = true;

		//如果是自定义格式, 则不需要进行格式校验
		if (formatType.equals(ResourceTools.FormatType.SELF_FORMAT.getTypeValue()))
			result = true;

		return result;
	}

	private void validateUploadFile(CommonsMultipartFile file, SystemConfigPO systemConfigPO, Integer formatType,
								String dataType, String suffix) throws CommonServiceException {
		if(!isValidateDataFormat(formatType, file.getOriginalFilename(), suffix))
			throw new CommonServiceException(CommonConstants.EC_INCORRECT_VALUE, "上传文件" + file.getOriginalFilename()
					+ "与资源所规定的类型不符");

		int maxSize; //Default max size
		if (dataType.equals(CommonConstants.DATA_TYPE_DB)) {
			maxSize = systemConfigPO.getDbUploadSize(); //获取配置限制大小的单位为:MB
		} else
			maxSize = systemConfigPO.getFileUploadSize(); //获取配置限制大小的单位为:MB

		//校验文件大小
		int actualFileSize = CommonUtils.calculateFileSizeByMB(file.getSize());
		if (actualFileSize > maxSize)
			throw new CommonServiceException(CommonConstants.EC_INCORRECT_VALUE, "上传文件" + file.getOriginalFilename()
					+ "大小超出限制" + maxSize + "MB");
	}

	private Long uploadFileToHDFS(CommonsMultipartFile file, String dataType, String hdfsPath, String uniqueFileName)
			throws RuntimeException, CommonServiceException {
		try {
			InputStream inputStream;
			Long rowNum = 1L;

			//如果是数据库类型文件, 则需要返回具体需要处理的行数
			if (dataType.equals(CommonConstants.DATA_TYPE_DB)) {
				//先将上传文件保存至本地
				String localTempPath = saveToLocalTempSys(uniqueFileName, file.getInputStream());

				//读取EXCEL文件中的记录行数
				rowNum = getExcelRowNum(localTempPath);

				//从本地临时文件生成新的流, 用于传给HDFS
				inputStream = new FileInputStream(localTempPath);
			} else
				inputStream = file.getInputStream();

            RespResult<Boolean> hdfsExecuteResult =
					hdfsUnrestrictedDao.uploadFileByStream("hdfs:" + hdfsPath + uniqueFileName, inputStream);

			if (!hdfsExecuteResult.isSuccess()) {
				String errMsg = DateTools.getDateTime() + ", " + file.getOriginalFilename() + "上传HDFS系统时发生错误.";
				LOG.error(errMsg + " " + hdfsExecuteResult.getMsg());
				throw new CommonServiceException(CommonConstants.EC_INCORRECT_VALUE, ", 上传HDFS系统时发生错误，"+hdfsExecuteResult.getMsg());
			}

			return rowNum;
		} catch (Exception e) {
		    e.printStackTrace();
			String errMsg = DateTools.getDateTime() + ", " + file.getOriginalFilename() +  "上传HDFS系统时发生错误";
			LOG.error(errMsg+":"+e.getMessage());
			String messge = e.getMessage().length()<300?e.getMessage():e.getMessage().substring(0, 300);
			throw new CommonServiceException(CommonConstants.EC_INCORRECT_VALUE, messge);
		}
	}


    private void uploadFileToHDFS(File tmpFile, String hdfsName)throws Exception {
        try {

            //从本地临时文件生成新的流, 用于传给HDFS
            InputStream inputStream = new FileInputStream(tmpFile);
            RespResult<Boolean> hdfsExecuteResult =   hdfsUnrestrictedDao.uploadFileByStream("hdfs:" + hdfsName, inputStream);

            if (!hdfsExecuteResult.isSuccess()) {
                String errMsg = DateTools.getDateTime() + ", " + tmpFile.getName() + "上传HDFS系统时发生错误.";
                LOG.error(errMsg + " " + hdfsExecuteResult.getMsg());
                throw new CommonServiceException(CommonConstants.EC_INCORRECT_VALUE, ", 上传HDFS系统时发生错误");
            }

        } catch (Exception e) {
            e.printStackTrace();
            String errMsg = DateTools.getDateTime() + ", " + tmpFile.getName() +  "上传HDFS系统时发生错误";
            LOG.error(errMsg+":"+e.getMessage());
            String messge = e.getMessage().length()<300?e.getMessage():e.getMessage().substring(0, 300);
            throw new CommonServiceException(CommonConstants.EC_INCORRECT_VALUE, messge);
        }
    }

	private Long saveDataUploadRecord(Long rentId, Long resourceId, String dataBatch, String dataType, String userName)
			throws RuntimeException, CommonServiceException {
		Long taskSeq;
		try {
			taskSeq = sequenceNumberManager.getSeqNum();
		} catch (Exception e) {
			LOG.error(new Date() + "资源" + resourceId + "在上报数据时, 获取全局顺序号失败" + e.getMessage());
			throw new CommonServiceException(CommonConstants.EC_INCORRECT_VALUE, "获取全局顺序号失败");
		}

		DataUploadPO dataUploadPO = new DataUploadPO();

		dataUploadPO.setResourceId(resourceId);
		dataUploadPO.setStatus(CommonConstants.WAIT_IMPORT);
		dataUploadPO.setDataBatch(dataBatch);
		dataUploadPO.setDataType(dataType);
		dataUploadPO.setImportCount(0L);
		dataUploadPO.setTaskSeq(taskSeq);

		//生成ETL的任务号
		String importTaskId = CommonUtils.generateETLTaskNum(taskSeq);

		dataUploadPO.setImportTaskId(importTaskId);
		dataUploadPO.setRentId(rentId);

		dataUploadPO.setCreator(userName);
		dataUploadPO.setCreateTime(new Date());
		dataUploadPO.setModifier(userName);
		dataUploadPO.setModifyTime(new Date());

		dataUploadDAO.insertDataUploadRecord(dataUploadPO);

		return dataUploadPO.getId();
	}

	@Override
	public void downLoadExcelTemplate(Long resourceId, HttpServletResponse response)
			throws CommonServiceException {

		BufferedInputStream bis;
		BufferedOutputStream bos;

		try {
			//根据资源ID获取所对应的资源细项的列名称, 并根据资源细项信息生成Excel模板列
			List<ResourceColumnPO> columnPOList = resourceColumnDAO.getColumnByResourceId(resourceId);
			if (CollectionUtils.isEmpty(columnPOList)) {
				throw new CommonServiceException(CommonConstants.EC_NOT_EXISTED_VALUE, "资源ID所对应细项不存在");
			}

			List<String> colNamesList = new ArrayList<String>();
			for (ResourceColumnPO model : columnPOList) {
				colNamesList.add(model.getColName());
			}

			String fileName
					= "资源信息细项模板-" + DateTools.getYear() + DateTools.getMonth() + DateTools.getDay() + ".xlsx";
			ByteArrayOutputStream os = new ByteArrayOutputStream();

			//通过获取的细项列名, 生成Excel模板, 并写进流
			ExcelUtils.createWorkBookTemplate(colNamesList).write(os);
			byte[] content = os.toByteArray();
			InputStream is = new ByteArrayInputStream(content);
			// 设置response参数，可以打开下载页面
			response.reset();
			//response.setContentType("application/vnd.ms-excel"); "application/application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            response.setContentType("application/application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment;filename="+
					new String((fileName).getBytes(), "ISO-8859-1"));

			ServletOutputStream out = response.getOutputStream();
			bis = null;
			bos = null;
			try {
				bis = new BufferedInputStream(is);
				bos = new BufferedOutputStream(out);
				byte[] buff = new byte[2048];
				int bytesRead;
				// Simple read/write loop.
				while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
					bos.write(buff, 0, bytesRead);
				}
			} finally {
				if (bis != null)
					bis.close();
				if (bos != null)
					bos.close();
			}
		} catch (IOException e) {
			throw new CommonServiceException(CommonConstants.EC_INCORRECT_VALUE, "生成资源细项模板时, 发生IO错误");
		}
	}

	private SearchDataUploadVO convertToSearchDataUploadVO(SearchDataUploadPO searchDataUploadPO) {
		SearchDataUploadVO svo = new SearchDataUploadVO();

		svo.setId(searchDataUploadPO.getId());
		svo.setImportTaskId(searchDataUploadPO.getImportTaskId());
		svo.setSubscribeId(searchDataUploadPO.getSubscribeId());
		svo.setDataType(searchDataUploadPO.getDataType());
		svo.setCode(searchDataUploadPO.getCode());
		svo.setName(searchDataUploadPO.getName());
		svo.setDataBatch(searchDataUploadPO.getDataBatch());
		svo.setPubFileName(searchDataUploadPO.getFileNames());

		if (searchDataUploadPO.getCreateTime() != null)
			svo.setCreateTime(DateTools.getDateTime(searchDataUploadPO.getCreateTime()));
		else
			svo.setCreateTime("");

		if (searchDataUploadPO.getImportTime() != null)
			svo.setImportTime(DateTools.getDateTime(searchDataUploadPO.getImportTime()));
		else
			svo.setImportTime("");

		svo.setImportCount(searchDataUploadPO.getImportCount());
		svo.setStatus(searchDataUploadPO.getStatus());
		return svo;
	}

	public ResultPager<SearchDataUploadVO> getDataUploadRecordByCondition(Map<String, String> conditionMap,
															String pubFileName, Integer pageNum, Integer pageSize) {
		pageNum = null == pageNum ? 1 : pageNum;
		pageSize = null == pageSize ? 10 : pageSize;
		PageHelper.startPage(pageNum, pageSize);

		List<SearchDataUploadVO> voList = new ArrayList<SearchDataUploadVO>();
		List<SearchDataUploadPO> poList = dataUploadDAO.getDataUploadRecordByCondition(conditionMap);

		if (CollectionUtils.isNotEmpty(poList)) {
			for (SearchDataUploadPO model : poList) {
				SearchDataUploadVO searchDataUploadVO = convertToSearchDataUploadVO(model);
				voList.add(searchDataUploadVO);
			}

			//用PageInfo对结果进行包装
			PageInfo<SearchDataUploadPO> pi = new PageInfo<SearchDataUploadPO>(poList);
			Long totalNum = pi.getTotal();
			return new ResultPager<SearchDataUploadVO>(pi.getPageNum(),totalNum, voList);
		}
		return null;
	}

	@Override
	public void deleteDataUploadRecordById(Long id) throws CommonServiceException {
		DataUploadPO dataUploadPO = dataUploadDAO.getDataUploadRecordById(id);

		if (dataUploadPO == null)
			throw new CommonServiceException(CommonConstants.EC_NULL_VALUE, "要删除的数据上报记录不存在");

		//之前是上报5分钟之内可以删除，超过5分钟则不可以删除，现在修改成已经5分钟内入库就不可以删除，没有没入库可以删除
		if (isOutOfDate(dataUploadPO.getCreateTime())) {
			throw new CommonServiceException(CommonConstants.EC_OVER_TIME_ERROR, "当前数据上报已超过5分钟, 不允许删除");
		}

		if(StringUtils.isNotEmpty(dataUploadPO.getSubscribeId())){
            throw new CommonServiceException(CommonConstants.EC_OVER_TIME_ERROR, "当前上报作业已经运行，不允许删除");
        }

		dataUploadDAO.deleteDataUploadRecordById(id);
		dataUploadDetailDAO.deleteUploadDetailsByParentId(id);
	}

	@Override
	public void updateUploadDataForFILE(Long rentId, DataUploadTotalVO dataUploadTotalVO, String userName)
			throws CommonServiceException {
		//如果上传成功, 生成数据上报rc_data_upload记录
		Long parentId = saveDataUploadRecord(rentId, dataUploadTotalVO.getResourceId(), dataUploadTotalVO.getDataBatch(),
				dataUploadTotalVO.getDataType(), userName);

		if (CollectionUtils.isNotEmpty(dataUploadTotalVO.getDataUploadDetailVOList())){
			for (DataUploadDetailVO model : dataUploadTotalVO.getDataUploadDetailVOList()) {
				DataUploadDetailPO dataUploadDetailPO = new DataUploadDetailPO();
				dataUploadDetailPO.setId(model.getId());
				dataUploadDetailPO.setPubFileName(model.getPubFileName());
				dataUploadDetailPO.setFileDescription(model.getFileDescription());
				dataUploadDetailPO.setModifier(userName);
				dataUploadDetailPO.setModifyTime(new Date());
				dataUploadDetailPO.setParentId(parentId);

				dataUploadDetailDAO.updateDataUploadDetailById(dataUploadDetailPO);
			}
		}
	}

	@Override
	public ResultPager<ETLTaskDetailVO> getETLTaskDetailInfoById(Long id, String userName) throws CommonServiceException {
		ETLTaskDetailVO etlTaskDetailVO = new ETLTaskDetailVO();

		try {
			DataUploadPO dataUploadPO = dataUploadDAO.getDataUploadRecordById(id);
			QueryJobDto queryJobDto = new QueryJobDto(userName, dataUploadPO.getSubscribeId());
			queryJobDto.setIncloudLog(true);

			if (!CommonUtils.isEmptyStr(dataUploadPO.getExecId()))
				queryJobDto.setExecId(dataUploadPO.getExecId());


            LOG.info("调用ETL查询日志参数：{}", queryJobDto.toString());
			SubscribeResultDto subscribeResultDto = subscribeService.getSubscribeJobInfo(queryJobDto);
			LOG.info("调用ETL返回数据：{}", subscribeResultDto.toString());
			SubscribeMeasureDto subscribeMeasureDto = subscribeResultDto.getMeasure();

			etlTaskDetailVO.setTaskName(dataUploadPO.getImportTaskId());
			etlTaskDetailVO.setDataUploadSeqNum(dataUploadPO.getImportTaskId());

            //String status = subscribeResultDto.getMeasure().getStatus();  //Changed By robin 2018/08/11
            String status = subscribeResultDto.getCurStatus();
            if(subscribeMeasureDto!=null){
                status = subscribeMeasureDto.getStatus();
            }

			etlTaskDetailVO.setCurStatus(status);
			etlTaskDetailVO.setLog(subscribeResultDto.getLog());
			if (subscribeMeasureDto != null) {
				etlTaskDetailVO.setStartTime(subscribeMeasureDto.getStartTime());
				etlTaskDetailVO.setEndTime(subscribeMeasureDto.getEndTime());
				etlTaskDetailVO.setOperator(dataUploadPO.getCreator());
			}
		} catch (Exception e) {
			LOG.error(new Date() + "上报记录" + id + "获取ETL任务失败" + e.getMessage());
			throw new CommonServiceException(CommonConstants.EC_INCORRECT_VALUE, "获取ETL任务失败");
		}

		//应前方要求, 将该返回结果改造成分页类型, 以便前方接收
		Integer pageNum = 1;
		Integer pageSize = 10;
		PageHelper.startPage(pageNum, pageSize);

		List<ETLTaskDetailVO> voList = new ArrayList<ETLTaskDetailVO>();
		voList.add(etlTaskDetailVO);
		ResultPager<ETLTaskDetailVO> rp = new ResultPager<ETLTaskDetailVO>(1,1, voList);
		return rp;
	}

    @Override
    public Long updateBrowseData(Long rentId, String user, BrowseDataVO data) throws Exception {

        Long resourceId = data.getResourceId();
        if(resourceId==null||resourceId==0L){
            throw new RuntimeException("没有传递资源ID,请前端配置好资源ID");
        }
        String suffix = ".xlsx";
        ResourceConfigPO rc =resourceConfigDAO.getConfigById(resourceId);
	    String filePrefix=rc.getCatalogCode()+"-"+rc.getSeqNum()+suffix;
	    File updateFile = FileUtils.createUpdateDirByInfo();

	    File excelFile = new File(updateFile, filePrefix);
	    Long dataLineSize = ExcelUtils.createBrowseFormExcel(excelFile,data);
	    if(dataLineSize.equals(0L)){
	        return 0L;
        }
        LOG.info("存储文件名称为 {},文件大小为{},数据行数{}" , updateFile.getPath(), excelFile.length(),
                dataLineSize);


        //如果文件不为空, 则获取文件后缀名, 重新将文件命名为UUID.后缀名, 组成新的文件名称
        SystemConfigPO systemConfigPO = systemConfigService.getSystemConfig();
        if (systemConfigPO == null)
            throw new CommonServiceException(CommonConstants.EC_NULL_VALUE, "系统参数还未配置，请在先配置再使用");

        //校验文件大小以及类型
        String hdfsPath = systemConfigPO.getOriginFileRoot();
        String uuidName = CommonUtils.generateUUID() + suffix;

        //上传至HDFS
        uploadFileToHDFS(excelFile, hdfsPath+uuidName);
        LOG.info("HDFS文件名称为 {}" ,hdfsPath+uuidName);
        DataUploadDetailPO dataUploadDetailPO =
                generateDataUploadDetailPO(uuidName, excelFile.getName(), suffix, user, excelFile.length());

        Long parentId = saveDataUploadRecord(rentId, resourceId, data.getDataBatch(), CommonConstants.DATA_TYPE_DB, user);
        dataUploadDetailPO.setParentId(parentId);
        //保存数据上报记录
        dataUploadDetailDAO.insertDataUploadDetail(dataUploadDetailPO);
        return dataLineSize;
    }

    /**
     * 获取网页填报字段标题内容
     *
     * @param resourceId
     * @return
     */
    @Override
    public List<String> getBrowseFormDataTitle(Long resourceId)throws Exception{

        List<ResourceColumnPO> columnPOList = resourceColumnDAO.getColumnByResourceId(resourceId);
        if (CollectionUtils.isEmpty(columnPOList)) {
            throw new Exception("资源ID所对应细项不存在");
        }

        List<String> colNamesList = new ArrayList<String>();
        for (ResourceColumnPO model : columnPOList) {
            colNamesList.add(model.getColName());
        }
        return colNamesList;
    }

    /**
     * 用户直接导入表格到网页编辑
     *
     * @param titleFlag
     * @param file
     * @return
     * @throws Exception
     */
    @Override
    public BrowseDataVO importFormDataIntoBrowse(Long titleFlag, CommonsMultipartFile file) throws Exception {

        File importForm= FileUtils.createFileByType("userFormData", file);
        log.info("上传表格存储文件路径为： " + importForm.getPath());
        //读取EXCEL文件中的记录行数
        Long rowNum = getExcelRowNum(importForm.getPath());
        if(rowNum>500){
            throw new Exception("上传文件行数超过500行("+rowNum+")，建议使用Excel上报数据库方式上传");
        }

        BrowseDataVO dataVO = new BrowseDataVO();
        if(ExcelUtils.verifyExcel2003(importForm.getName())){
            List<String[]> dataList = ExcelUtils.getExcelFormData(importForm.getPath());
            dataVO.setBrowseData(dataList);
        }else if(ExcelUtils.verifyExcel2007(importForm.getName())){
            ExcelUtilsInfo info = XLSXCovertCSVReader.getXlsxFormData(importForm.getPath());
            dataVO.setBrowseData(info.getData());
        }else{
            throw new Exception("上传文件并非Excel表格数据，请重新上传Excel表格数据");
        }
        if (titleFlag > 0) {
            if (CollectionUtils.isNotEmpty(dataVO.getBrowseData()) && dataVO.getBrowseData().size() > 0) {
                dataVO.getBrowseData().remove(0);
            }
        }

        return dataVO;
    }

    private boolean isOutOfDate(Date uploadDate) {
		long currentTime = (new Date()).getTime();
		long updateTime = uploadDate.getTime();
		long pastMinutes = (currentTime - updateTime)/1000/60;

		return pastMinutes >= 5;
	}

	// @描述：是否是2003的excel，返回true是2003
	private boolean isExcel2003(String filePath)  {
		return filePath.matches("^.+\\.(?i)(xls)$");
	}

	private Long getExcelRowNum(String localTmpPath) throws CommonServiceException {

        //原有Excel poi 在数据量有5万行以上时，操作会导致内存溢出 robin 2018/08/24
        int rowNums = 0;
        try{
            rowNums = ExcelUtils.getExcelCountInfo(new File(localTmpPath)).getLineCount();
        }catch (Exception e){
            e.printStackTrace();
            throw new CommonServiceException(CommonConstants.EC_UNEXPECTED, e.getMessage());
        }
        return Long.valueOf(rowNums);
	}

	private String saveToLocalTempSys(String fileName, InputStream inputStream) throws CommonServiceException {

        String localTempPath =System.getProperty("java.io.tmpdir")+File.separator;
		File localFilePath = new File(localTempPath + fileName);
		localTempPath = localTempPath + fileName;

		try {
			FileOutputStream out = new FileOutputStream(localFilePath);
			//创建一个缓冲区
			byte buffer[] = new byte[1024];
			//判断输入流中的数据是否已经读完的标识
			int len;
			//循环将输入流读入到缓冲区当中，(len=in.read(buffer))>0就表示in里面还有数据
			while((len = inputStream.read(buffer)) > 0){
				//使用FileOutputStream输出流将缓冲区的数据写入到指定的目录(savePath + "\\" + filename)当中
				out.write(buffer, 0, len);
			}
			//关闭输入流
			inputStream.close();
			//关闭输出流
			out.close();

			return localTempPath;
		} catch (IOException e) {
			LOG.error(new Date() + "保存文件" + fileName + "至本地路径时出现IO异常!");
			throw new CommonServiceException(CommonConstants.EC_UNEXPECTED, "保存文件至本地路径时出现IO异常!");
		}
	}
}
