package com.idatrix.resource.common.task;


import com.idatrix.resource.basedata.dao.SystemConfigDAO;
import com.idatrix.resource.basedata.po.SystemConfigPO;
import com.idatrix.resource.basedata.service.ISystemConfigService;
import com.idatrix.resource.catalog.dao.ResourceColumnDAO;
import com.idatrix.resource.catalog.dao.ResourceConfigDAO;
import com.idatrix.resource.catalog.po.ResourceColumnPO;
import com.idatrix.resource.catalog.po.ResourceConfigPO;
import com.idatrix.resource.common.Exception.CommonServiceException;
import com.idatrix.resource.common.utils.CommonConstants;
import com.idatrix.resource.common.utils.CommonUtils;
import com.idatrix.resource.common.utils.DateTools;
import com.idatrix.resource.common.utils.ResourceTools;
import com.idatrix.resource.datareport.dao.DataUploadDAO;
import com.idatrix.resource.datareport.dao.DataUploadDetailDAO;
import com.idatrix.resource.datareport.po.DataUploadDetailPO;
import com.idatrix.resource.datareport.po.DataUploadPO;
import com.ys.idatrix.cloudetl.subscribe.api.dto.CreateJobDto;
import com.ys.idatrix.cloudetl.subscribe.api.dto.QueryJobDto;
import com.ys.idatrix.cloudetl.subscribe.api.dto.SubscribeResultDto;
import com.ys.idatrix.cloudetl.subscribe.api.dto.parts.FileTransmitDto;
import com.ys.idatrix.cloudetl.subscribe.api.dto.parts.InputFieldsDto;
import com.ys.idatrix.cloudetl.subscribe.api.dto.parts.OutputFieldsDto;
import com.ys.idatrix.cloudetl.subscribe.api.dto.parts.SearchFieldsDto;
import com.ys.idatrix.cloudetl.subscribe.api.dto.step.*;
import com.ys.idatrix.cloudetl.subscribe.api.service.SubscribeService;
import com.ys.idatrix.metacube.api.beans.MetadataDTO;
import com.ys.idatrix.metacube.api.beans.ResultBean;
import com.ys.idatrix.metacube.api.beans.dataswap.MetadataField;
import com.ys.idatrix.metacube.api.beans.dataswap.QueryMetadataFieldsResult;
import com.ys.idatrix.metacube.api.service.MetadataToDataSwapService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.ys.idatrix.cloudetl.subscribe.api.dto.step.FileInputDto.ReadType;


@Component
@PropertySource("classpath:init.properties")
public class ExchangeETLTask{

	private final Logger LOG= org.slf4j.LoggerFactory.getLogger(this.getClass());

	@Autowired
	private DataUploadDAO dataUploadDAO;

	@Autowired
	private DataUploadDetailDAO dataUploadDetailDAO;

	@Autowired
	private SystemConfigDAO systemConfigDAO;

	@Autowired
	private ResourceColumnDAO resourceColumnDAO;

	@Autowired
	private ResourceConfigDAO resourceConfigDAO;

	@Autowired
	private SubscribeService subscribeService;

    @Autowired(required=false)
    private MetadataToDataSwapService metacubeCatalogService;

    @Autowired
    private ISystemConfigService systemConfigService;

    /*否使用全文搜索*/
    @Value("${is_use_full_text_search}")
    private Boolean fullTextSearchFlag;

    public void startTask(){

//        LOG.info("=========Exchange ETL Task==========");
		DataUploadPO dataUploadPO = dataUploadDAO.getWaitImportedDataUploadRecords(CommonConstants.WAIT_IMPORT);


		if (dataUploadPO != null) {

            //获取原始HDFS文件上传路径
    //		SystemConfigPO systemConfigPO = systemConfigDAO.getLastestSysConfig();
            SystemConfigPO systemConfigPO = systemConfigService.getSystemConfigByUser(dataUploadPO.getCreator());


            Map<String, Object> conditionMap = new HashMap<String, Object>();

			conditionMap.put("id", dataUploadPO.getId());
			conditionMap.put("resourceId", dataUploadPO.getResourceId());
			conditionMap.put("creator", dataUploadPO.getCreator());

			Long resourceId = dataUploadPO.getResourceId();

			DataUploadPO existedDataUploadPO = dataUploadDAO.getExistedDataUpLoadRecords(conditionMap);

			List<DataUploadDetailPO> dataUploadDetailPOList
					= dataUploadDetailDAO.getUploadDetailsByParentId(dataUploadPO.getId());

			if (CollectionUtils.isNotEmpty(dataUploadDetailPOList)) {
				if (existedDataUploadPO != null && !CommonUtils.isEmptyStr(existedDataUploadPO.getSubscribeId())) {

					//如果该资源已存在上报记录，则需要校验当前ETL任务类型与当前新上报数据是否相同
					try {
					QueryJobDto queryJobDto
							= new QueryJobDto(dataUploadPO.getCreator(), existedDataUploadPO.getSubscribeId());

						SubscribeResultDto subscribeResultDto = subscribeService.getSubscribeJobInfo(queryJobDto);
						String group = "SUBSCRIBE_" + dataUploadPO.getDataType();

						//如果该新增上报记录的任务类型与已存在的ETL任务类型不符，则需要重新创建新的任务
						if (!group.equals(subscribeResultDto.getGroup())) {
							createNewETLTask(resourceId, dataUploadPO, dataUploadDetailPOList, systemConfigPO.getOriginFileRoot(),
									systemConfigPO.getFileRoot());
						} else {
							startExistedETLTask(resourceId, dataUploadPO, existedDataUploadPO.getSubscribeId(), dataUploadDetailPOList,
									systemConfigPO.getOriginFileRoot(),	systemConfigPO.getFileRoot());
						}
					} catch (Exception e) {
					    e.printStackTrace();
						handleETLResult(null, existedDataUploadPO.getSubscribeId(),
								dataUploadPO, e.getMessage(), false);
						LOG.error(new Date().toString() + ", 数据上报记录" + dataUploadPO.getId() + "执行ETL任务失败"
						+ e.getMessage());
					}
				} else
					createNewETLTask(resourceId, dataUploadPO, dataUploadDetailPOList, systemConfigPO.getOriginFileRoot(),
							systemConfigPO.getFileRoot());
			} else {
				LOG.error(new Date().toString() + ", 数据上报记录" + dataUploadPO.getId() +
						"找不到关联的DataUploadDetails信息");
				dataUploadPO.setStatus(CommonConstants.IMPORT_ERROR);
				dataUploadPO.setModifyTime(new Date());
				dataUploadPO.setImportErrmsg(new Date().toString() + ", 数据上报记录找不到关联的DataUploadDetails信息");
				dataUploadDAO.updateDataUploadRecordById(dataUploadPO);
			}
		}
	}

	private void createNewETLTask(Long resourceId, DataUploadPO dataUploadPO, List<DataUploadDetailPO> dataUploadDetailPOList,
								  String sourceFileDirectory, String destFileDirectory) {
		String userId = dataUploadPO.getCreator();

		try {
			//采用数据上报创建者作为ETL任务的userId, ETL的任务号作为ETL的名字
			String name = dataUploadPO.getImportTaskId();
			String group = "SUBSCRIBE_" + dataUploadPO.getDataType();
 			CreateJobDto createJobDto = new CreateJobDto(userId, name, group);

			//数据上报----数据库类型
			if (dataUploadPO.getDataType().equals(CommonConstants.DATA_TYPE_DB)) {
				//根据资源ID获取所对应的资源细项的列名称
				List<ResourceColumnPO> columnPOList
						= resourceColumnDAO.getColumnByResourceId(dataUploadPO.getResourceId());

//				String remoteFilePath = sourceFileDirectory + dataUploadDetailPOList.get(0).getOriginFileName();

				String remoteFilePath = dataUploadDetailPOList.get(0).getOriginFileName();
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("remoteFilePath", remoteFilePath);

				//根据黄义元数据接口获取资源相对应的表信息, 用于生成ETL任务
				ResourceConfigPO resourceConfigPO = resourceConfigDAO.getConfigById(dataUploadPO.getResourceId());
				Long bindTableId = resourceConfigPO.getBindTableId();

                MetadataDTO metadataDTO = new MetadataDTO();
                ResultBean<MetadataDTO> respon = metacubeCatalogService.findTableInfoByID(bindTableId);
                if(!respon.isSuccess()){
                    LOG.error("元数据表metaId "+bindTableId+",获取元数据信息失败："+respon.getMsg());
                    throw new Exception("元数据表metaId "+bindTableId+",获取元数据信息失败："+respon.getMsg());
                }
                LOG.info("元数据查询返回信息：{}",respon.toString());
                metadataDTO = respon.getData();


				try {
					/************************dataInput FileInputDto参数拼装****************************/
					FileInputDto fileInputDto = assembleFileInputDtoParams(sourceFileDirectory, columnPOList, metadataDTO.getMetaName());
					createJobDto.setDataInput(fileInputDto);

					/************************dataOutput InsertUpdateDto参数拼装************************/
					InsertUpdateDto insertUpdateDto = assembleInsertUpdateDto(bindTableId, columnPOList, metadataDTO);
					createJobDto.setDataOutput(insertUpdateDto);

					//2018-08-08 根据需求，将数据批次传递至ETL
					map.put("ds_batch", dataUploadPO.getDataBatch());
					createJobDto.setParams(map);
				} catch (NullPointerException e) {
				    e.printStackTrace();
					throw new CommonServiceException(CommonConstants.EC_NULL_VALUE, "当前资源" + resourceConfigPO.getId()
							+ "|" + resourceConfigPO.getName() + " bind_table_id为空, 入库失败");
				}
			} else {

                SftpPutDto sftpPutDto = new SftpPutDto();
                List<FileTransmitDto> targetList = new ArrayList<FileTransmitDto>();

				for (DataUploadDetailPO model : dataUploadDetailPOList) {
					String fullFilePath = sourceFileDirectory + model.getOriginFileName();
					FileTransmitDto fileTransmitDto = new FileTransmitDto(fullFilePath, destFileDirectory);
					targetList.add(fileTransmitDto);
				}

				FileCopyDto fileCopyDto = new FileCopyDto();
				fileCopyDto.setOverwriteFiles(true);
				fileCopyDto.setDestinationIsAfile(false);
				fileCopyDto.setFiles(targetList);
                //老接口已经被放弃
				//createJobDto.setDataOutput(fileCopyDto);
                List<StepDto> jobDataOutputs = new ArrayList<StepDto>();
                jobDataOutputs.add(fileCopyDto);
                createJobDto.setJobDataOutputs(jobDataOutputs);

                List<StepDto> transDataOutputs = new ArrayList<StepDto>();
//                transDataOutputs.add(fileCopyDto);

                //需要全文搜索的时候
                if(fullTextSearchFlag){
                    ElasticSearchDto esDto = new ElasticSearchDto();
                    transDataOutputs.add(esDto);

                    //增加
                    ResourceConfigPO rcPO = resourceConfigDAO.getConfigById(resourceId);
                    //ES只需要处理文件，表格，其它不予处理
                    if (rcPO.getFormatType()==ResourceTools.FormatType.IMAGE.getTypeValue() ||
                            rcPO.getFormatType()==ResourceTools.FormatType.STEAM_MEDIA.getTypeValue() ||
                            rcPO.getFormatType()==ResourceTools.FormatType.SELF_FORMAT.getTypeValue() ||
                            rcPO.getFormatType()==ResourceTools.FormatType.SERVICE_INTERFACE.getTypeValue() ||
                            rcPO.getFormatType()==ResourceTools.FormatType.DB.getTypeValue() ||
                            rcPO.getFormatType()==ResourceTools.FormatType.NOT_SURE.getTypeValue()){

                    }else {

                        FileInputDto fileInputDto = new FileInputDto();
                        fileInputDto.setType(ReadType);
                        List<String> fileDir = Arrays.asList(sourceFileDirectory);
                        fileInputDto.setFiles(fileDir);
                        //通过${}传递 正则表达式
                        fileInputDto.setFileMask("${fileMask}");
                        createJobDto.setDataInput(fileInputDto);

                        //第一次运行传递实际参数
                        Map<String, Object> params = new HashMap<String, Object>();
                        String fileMaskParam = null;
                        if(dataUploadDetailPOList!=null && dataUploadDetailPOList.size()>0) {
                            StringBuilder fileMarks = new StringBuilder();
                            for (DataUploadDetailPO model : dataUploadDetailPOList) {
                                fileMarks.append(model.getOriginFileName() + "|");
                                String key = "_id_" + model.getOriginFileName();
                                params.put(key, resourceId.toString() + "_" + model.getOriginFileName());
                            }
                            fileMaskParam = fileMarks.substring(0, fileMarks.length() - 1).toString();
                        }
                        params.put("fileMask", fileMaskParam);
                        createJobDto.setParams(params);
                    }
                }
                createJobDto.setTransDataOutputs(transDataOutputs);

			}

			SubscribeResultDto subscribeResultDto;
            LOG.info(DateTools.formatDate(new Date())+"-数据上报创建任务参数:{}", createJobDto.toString());
			subscribeResultDto = subscribeService.createSubscribeJob(createJobDto);
            LOG.info(DateTools.formatDate(new Date())+"-数据上报创建任务结果:{}", subscribeResultDto.toString());

			if (subscribeResultDto.getStatus() == 0) {
				handleETLResult(subscribeResultDto, subscribeResultDto.getSubscribeId(), dataUploadPO, "", true);

				LOG.info(new Date().toString() + ", 数据上报记录" + dataUploadPO.getId() + "创建ETL任务");
			} else {
				handleETLResult(null, "", dataUploadPO, subscribeResultDto.getErrorMessage(),
						false);
			}
		} catch (Exception e) {
            e.printStackTrace();
			handleETLResult(null, "", dataUploadPO, e.getMessage(), false);
			LOG.error(new Date().toString() + ", 数据上报记录" + dataUploadPO.getId() + "创建ETL任务失败");
		}
	}

	private void startExistedETLTask(Long resourceId, DataUploadPO dataUploadPO, String subscribeId, List<DataUploadDetailPO>
				dataUploadDetailPOList, String sourceFileDirectory, String destFileDirectory) {
		Map<String, Object> params = new HashMap<String, Object>();

		//如果是文件类, 则需要添加额外参数
		if (dataUploadPO.getDataType().equals(CommonConstants.DATA_TYPE_FILE)) {
			List<FileTransmitDto> targetList = new ArrayList<FileTransmitDto>();


			for (DataUploadDetailPO model : dataUploadDetailPOList) {
				String fullFilePath = sourceFileDirectory + model.getOriginFileName();
				FileTransmitDto fileTransmitDto = new FileTransmitDto(fullFilePath, destFileDirectory);
				targetList.add(fileTransmitDto);
			}

			FileCopyDto fileCopyDto = new FileCopyDto();
			fileCopyDto.setOverwriteFiles(true);
			fileCopyDto.setDestinationIsAfile(false);
			fileCopyDto.setFiles(targetList);

			params.put(fileCopyDto.getType(), fileCopyDto);

			//如果进行全文搜索
			if(fullTextSearchFlag){

                ResourceConfigPO rcPO = resourceConfigDAO.getConfigById(resourceId);
                //ES只需要处理文件，表格，其它不予处理
                if (rcPO.getFormatType()==ResourceTools.FormatType.IMAGE.getTypeValue() ||
                        rcPO.getFormatType()==ResourceTools.FormatType.STEAM_MEDIA.getTypeValue() ||
                        rcPO.getFormatType()==ResourceTools.FormatType.SELF_FORMAT.getTypeValue() ||
                        rcPO.getFormatType()==ResourceTools.FormatType.SERVICE_INTERFACE.getTypeValue() ||
                        rcPO.getFormatType()==ResourceTools.FormatType.DB.getTypeValue() ||
                        rcPO.getFormatType()==ResourceTools.FormatType.NOT_SURE.getTypeValue()){

                }else{
                    if(dataUploadDetailPOList!=null && dataUploadDetailPOList.size()>0) {
                        StringBuilder fileMarks = new StringBuilder();
                        for (DataUploadDetailPO model : dataUploadDetailPOList) {
                            String fullFilePath = sourceFileDirectory + model.getOriginFileName();
                            fileMarks.append(model.getOriginFileName() + "|");

                            //key 为fieldName_fileName, value 为需要传递数值
                            String key = "_id_" + model.getOriginFileName();
                            params.put(key, resourceId.toString() + "_" + model.getOriginFileName());
                        }
                        String fileMaskParam = fileMarks.substring(0, fileMarks.length() - 1).toString();
                        params.put("fileMask", fileMaskParam);
                    }
                }
            }
		} else {
//			String remoteFilePath = sourceFileDirectory + dataUploadDetailPOList.get(0).getOriginFileName();
			String remoteFilePath = dataUploadDetailPOList.get(0).getOriginFileName();
			params.put("remoteFilePath", remoteFilePath);
			//2018-08-08 根据需求，将数据批次传递至ETL
			params.put("ds_batch", dataUploadPO.getDataBatch());
		}

		SubscribeResultDto subscribeResultDto;

		try {
			QueryJobDto queryJobDto = new QueryJobDto(dataUploadPO.getCreator(), subscribeId);
			queryJobDto.setParams(params);

            LOG.info(DateTools.formatDate(new Date())+"-数据上报开始任务参数：{}", queryJobDto.toString());
            subscribeResultDto = subscribeService.startSubscribeJob(queryJobDto);
            LOG.info(DateTools.formatDate(new Date())+"-数据上报开始任务结果：{}", subscribeResultDto.toString());


            if (subscribeResultDto.getStatus() == 0) {
				handleETLResult(subscribeResultDto, subscribeId, dataUploadPO, "", true);

				LOG.info(new Date().toString() + ", 数据上报记录" + dataUploadPO.getId() + " SubscribeId:"
								+ subscribeResultDto.getSubscribeId() + " ExcId:" + subscribeResultDto.getCurExecId()
						+ "重新开始ETL任务");
		    } else {
				handleETLResult(null, subscribeId, dataUploadPO, subscribeResultDto.getErrorMessage(), false);
			}
		} catch (Exception e) {
            e.printStackTrace();
			handleETLResult(null, subscribeId,  dataUploadPO, e.getMessage(), false);
			LOG.error(new Date().toString() + ", 数据上报记录" + dataUploadPO.getId() + "执行ETL任务失败原因-" +e.getMessage() );
		}
	}

	private void handleETLResult(SubscribeResultDto subscribeResultDto, String subscribedId, DataUploadPO dataUploadPO,
								 String errMsg, boolean isCommunicatedSuccess) {
		dataUploadPO.setImportTime(new Date());
		dataUploadPO.setModifyTime(new Date());

		if (isCommunicatedSuccess) {
		    dataUploadPO.setSubscribeId(subscribedId);
			dataUploadPO.setStatus(CommonUtils.convertStatus(subscribeResultDto.getCurStatus()));
			dataUploadPO.setExecId(subscribeResultDto.getCurExecId());
			dataUploadPO.setImportErrmsg(subscribeResultDto.getErrorMessage());
		} else {
			dataUploadPO.setStatus(CommonConstants.IMPORT_ERROR);
			dataUploadPO.setImportErrmsg(new Date()+" "+(errMsg.length()>450?errMsg.substring(0,450):errMsg));
		}

		dataUploadDAO.updateDataUploadRecordById(dataUploadPO);
	}

	/*dataInput FileInputDto参数拼装*/
	private FileInputDto assembleFileInputDtoParams(String dirPrefix, List<ResourceColumnPO> columnPOList, String tableNameVal) {
		FileInputDto fileInputDto = new FileInputDto();
		fileInputDto.addFile(dirPrefix+"${remoteFilePath}");

		String tableName = tableNameVal;
		fileInputDto.setAccessTable(tableName);

		List<InputFieldsDto> fieldsList = new ArrayList<InputFieldsDto>();

		//根据资源文件
		for (ResourceColumnPO model : columnPOList) {
			String colType = CommonUtils.convertToETLColType(model.getTableColType().toLowerCase());
            InputFieldsDto inputFieldsDto  = new InputFieldsDto(model.getTableColCode(), colType);
			if(StringUtils.equals(colType, "Date")){
			    String format = "yyyy-MM-dd HH:mm:ss";
                if(StringUtils.isNotEmpty(model.getDateFormat())){
			        format=model.getDateFormat();
                }
                inputFieldsDto.setFormat(format);
            }

			fieldsList.add(inputFieldsDto);
		}

		fileInputDto.setFields(fieldsList);

		return fileInputDto;
	}

	/*dataInput InsertUpdateDto参数拼装*/
	private InsertUpdateDto assembleInsertUpdateDto(Long metaId, List<ResourceColumnPO> columnPOList, MetadataDTO metadataDTO) {

	    InsertUpdateDto insertUpdateDto = new InsertUpdateDto();

        insertUpdateDto.setSchemaId(new Long(metadataDTO.getSchemaId()));
        insertUpdateDto.setTableId(new Long(metadataDTO.getMetaId()));
        insertUpdateDto.setTable(metadataDTO.getMetaName());

		SearchFieldsDto primaryKey = new SearchFieldsDto();

		List<OutputFieldsDto> outputFields = new ArrayList<OutputFieldsDto>();

		for (ResourceColumnPO model : columnPOList) {
			OutputFieldsDto outputFieldsDto = new OutputFieldsDto(model.getTableColCode(), model.getTableColCode());
			outputFieldsDto.setUpdate(true);

			if (model.getUniqueFlag()!=null && model.getUniqueFlag().equals(true)) {
				primaryKey = new SearchFieldsDto(model.getTableColCode(), "=", model.getTableColCode());
				outputFieldsDto.setUpdate(false);
			}

			outputFields.add(outputFieldsDto);
		}

		//配置好交换需要用到的字段 robin 2018/08/28
        //获取元数据字段
        List<MetadataField> metadataOriginFields = new ArrayList<MetadataField>();

        ResultBean<QueryMetadataFieldsResult> metaResult = metacubeCatalogService.getMetadataFieldsByMetaId(metaId.intValue());
        if(metaResult.isSuccess()){
            metadataOriginFields = metaResult.getData().getMetadataField();
        }else{
            LOG.error("获取资源绑定元数据结构异常：metaId {},错误原因：{}", metaId, metaResult.getMsg());
        }
        List<MetadataField> metaFinalFields = new ArrayList<MetadataField>();
        for(MetadataField field:metadataOriginFields){

            if(StringUtils.equalsIgnoreCase("ds_batch", field.getColName())){
                insertUpdateDto.setBatchFieldName(field.getColName());
            }else if(StringUtils.equalsIgnoreCase("ds_sync_time", field.getColName())){
                insertUpdateDto.setTimeFieldName(field.getColName());
            }else if(StringUtils.equalsIgnoreCase("ds_sync_flag", field.getColName())){
                insertUpdateDto.setFlagFieldName(field.getColName());
            }
        }

		List<SearchFieldsDto> searchField = new ArrayList<SearchFieldsDto>();
		searchField.add(primaryKey);
		insertUpdateDto.setSearchFields(searchField);
		insertUpdateDto.setUpdateFields(outputFields);

		return insertUpdateDto;
	}

}
