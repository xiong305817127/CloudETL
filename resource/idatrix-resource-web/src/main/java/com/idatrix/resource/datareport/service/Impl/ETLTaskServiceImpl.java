package com.idatrix.resource.datareport.service.Impl;


import com.idatrix.resource.catalog.service.IResourceStatiscsService;
import com.idatrix.resource.common.utils.CommonConstants;
import com.idatrix.resource.common.utils.CommonUtils;
import com.idatrix.resource.common.utils.DateTools;
import com.idatrix.resource.datareport.dao.DataUploadDAO;
import com.idatrix.resource.datareport.dao.DataUploadDetailDAO;
import com.idatrix.resource.datareport.dao.ResourceFileDAO;
import com.idatrix.resource.datareport.dto.ETLTaskResultDto;
import com.idatrix.resource.datareport.dto.StatusFeedbackDto;
import com.idatrix.resource.datareport.po.DataUploadDetailPO;
import com.idatrix.resource.datareport.po.DataUploadPO;
import com.idatrix.resource.datareport.po.ResourceFilePO;
import com.idatrix.resource.datareport.service.IETLTaskService;
import com.idatrix.resource.subscribe.dao.SubscribeDAO;
import com.idatrix.resource.subscribe.po.SubscribePO;
import com.idatrix.resource.taskmanage.dao.SubTaskDAO;
import com.idatrix.resource.taskmanage.dao.SubTaskExecDAO;
import com.idatrix.resource.taskmanage.po.SubTaskExecPO;
import com.idatrix.resource.taskmanage.po.SubTaskPO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Transactional
@Service("ETLTaskService")
public class ETLTaskServiceImpl implements IETLTaskService {

	private static final Logger LOG = LoggerFactory.getLogger(ETLTaskServiceImpl.class);

	@Autowired
	private DataUploadDAO dataUploadDAO;

	@Autowired
	private DataUploadDetailDAO dataUploadDetailDAO;

	@Autowired
	private ResourceFileDAO resourceFileDAO;

	@Autowired
    private SubTaskExecDAO subTaskExecDAO;

	@Autowired
    private SubTaskDAO subTaskDAO;

	@Autowired
    private SubscribeDAO subscribeDAO;

	@Autowired
    private IResourceStatiscsService resourceStatiscsService;

	@Override
	public StatusFeedbackDto updateETLTaskProcessResults(ETLTaskResultDto results) {
		StatusFeedbackDto statusFeedbackDto = new StatusFeedbackDto();

		LOG.info("时间-{}ETL处理结果：{}", DateTools.formatDate(new Date()), results.toString());
		String etlSubscribeId = results.getSubscribeId();
		if(StringUtils.isNotEmpty(etlSubscribeId) &&
                etlSubscribeId.startsWith(CommonConstants.PREFIX_ETL+CommonConstants.PREFIX_SUBSCRIBE)){
            StatusFeedbackDto feedbackDto = new StatusFeedbackDto();
            try{
                feedbackDto = exchangeETLTaskProcessResults(results);
            }catch (Exception e){
                e.printStackTrace();
                LOG.error("处理交换任务结果出错：{}", e.getMessage());
            }
            return feedbackDto;
        }else {

            if (results != null) {
                DataUploadPO dataUploadPO = dataUploadDAO.getDataUploadRecordByTaskExecId(results.getExecId());

                if (dataUploadPO != null) {
                    Long resourceId = dataUploadPO.getResourceId();

                    //根据熊汉那边反馈回来的执行结果
                    List<DataUploadDetailPO> dataUploadDetailPOList = dataUploadDetailDAO.getUploadDetailsByParentId(dataUploadPO.getId());

                    String result = CommonUtils.convertStatus(results.getResult());
                    if(CommonConstants.NONE_STATUS.equals(result)){   //不能识别的状态不做处理
                        statusFeedbackDto.setStatusCode(CommonConstants.SUCCESS_VALUE);
                        statusFeedbackDto.setErrMsg("ETL任务执行结果为空");
                        return statusFeedbackDto;
                    }
                    StringBuffer errInfo = new StringBuffer();

                    if (dataUploadPO.getDataType().equals(CommonConstants.DATA_TYPE_FILE)
                            && results.getResult().contains("Finished")) {
                        List<String> originFileNameList = results.getSuccessFileNameList();

                        if (originFileNameList != null && !originFileNameList.isEmpty()) {
                            StringBuffer failedCopiedFileNames = new StringBuffer();

                            for (DataUploadDetailPO model : dataUploadDetailPOList) {
                                if (originFileNameList.contains(model.getOriginFileName())) {
                                    //保存之前先判断文件记录是否已存在, 如果已存在则覆盖文件内容
                                    Map<String, Object> condition = new HashMap<String, Object>();
                                    condition.put("resourceId", resourceId);
                                    condition.put("pubFileName", model.getPubFileName());

                                    ResourceFilePO resourceFilePO
                                            = resourceFileDAO.isExistedResourceFile(condition);
                                    saveOrUpdateResourceFile(model, dataUploadPO, resourceFilePO);

                                } else {
                                    failedCopiedFileNames.append(model.getPubFileName()).append(" ");
                                }
                            }
                            if (!failedCopiedFileNames.toString().equals("")) {
                                errInfo.append("拷贝失败文件:").append(failedCopiedFileNames.toString()).append(" ");
                            }
                        }

                        Long importCount = originFileNameList == null ? 0 : Long.valueOf(originFileNameList.size());
                        dataUploadPO.setImportCount(importCount);

                        //文件类型增加每个文件个数为1
                        resourceStatiscsService.increaseDataCount(dataUploadPO.getResourceId(), 1L);
                    }

                    if (dataUploadPO.getDataType().equals(CommonConstants.DATA_TYPE_DB)
                            && results.getResult().equals("Finished")) {
                        dataUploadPO.setImportCount(results.getStockInCount() == null ? 0 : results.getStockInCount());
                        dataUploadPO.setInsertCount(results.getInsertCount() == null ? 0 : results.getInsertCount());
                        dataUploadPO.setUpdateCount(results.getUpdateCount() == null ? 0 : results.getUpdateCount());
                        dataUploadPO.setFailCount(results.getFailCount() == null ? 0 : results.getFailCount());
                        //数据库类型增加导入文件个数
                        resourceStatiscsService.increaseDataCount(dataUploadPO.getResourceId(), dataUploadPO.getImportCount());
                    }

                    if (result.equals(CommonConstants.IMPORT_ERROR)) {
                        errInfo.append(results.getResult() + ":" + results.getErrorMessage());
                        dataUploadPO.setImportErrmsg(errInfo.toString());
                    } else
                        dataUploadPO.setImportErrmsg(results.getResult());

                    dataUploadPO.setImportTime(results.getStockInTimeStamp());
                    dataUploadPO.setStatus(result);
                    dataUploadPO.setModifyTime(new Date());

                    dataUploadDAO.updateDataUploadRecordById(dataUploadPO);

                    LOG.info(new Date().toString() + ", ETL反馈任务" + results.getSubscribeSeqNum() + "更新状态完毕");
                    statusFeedbackDto.setStatusCode(CommonConstants.SUCCESS_VALUE);
                    return statusFeedbackDto;
                } else {
                    LOG.error(new Date().toString() + ", ETL反馈任务" + results.getSubscribeId() + "找不到对应的数据上报记录");
                    statusFeedbackDto.setStatusCode(CommonConstants.EC_NOT_EXISTED_VALUE);
                    statusFeedbackDto.setErrMsg("ETL任务" + results.getSubscribeSeqNum() + ", execId:" + results.getExecId()
                            + "找不到对应数据上报记录，当前更新状态为：" + results.getResult());
                    return statusFeedbackDto;
                }
            }

            statusFeedbackDto.setStatusCode(CommonConstants.EC_NULL_VALUE);
            statusFeedbackDto.setErrMsg("ETL任务执行结果为空");
        }
        return statusFeedbackDto;

	}


	/*处理ETL交换任务时，任务状态出现变化返回数据以及处理*/
    private StatusFeedbackDto exchangeETLTaskProcessResults(ETLTaskResultDto results) throws Exception{

        String etlSubscribeId = results.getSubscribeId();
        SubTaskPO taskPO = subTaskDAO.getBySubscribe(etlSubscribeId);
        Long importCount = 0L;

        //subTask删除的时候，需要返回对ETL不存或者删除，让ETL停止定时操作。
        String exchangeResult = results.getResult();
        String result = CommonUtils.convertLocalStatus(exchangeResult);
        if(StringUtils.equals(result, CommonConstants.NONE_STATUS) ){  //原则不识别状态不做处理
            return getFeedBackDto(CommonConstants.SUCCESS_VALUE, "ETL任务执行结果为空");
        }else if(taskPO==null){ ///|| taskPO.getEndTime().compareTo(new Date())>0){  //taskPO不存在订阅信息时候
            return getFeedBackDto(CommonConstants.EC_NOT_EXISTED_VALUE,
                    "ETL任务" + results.getSubscribeSeqNum() + ", execId:" + results.getExecId()
                    + "找不到对应数据上报记录，当前更新状态为：" + results.getResult());
        }

        if(StringUtils.equals(exchangeResult, "SingleStart")){
            //会有新的 execId 和 runningId
            SubTaskExecPO execPO = new SubTaskExecPO();
            execPO.setId(taskPO.getId());
            execPO.setTaskType(taskPO.getTaskType());
            execPO.setEtlSubscribeId(etlSubscribeId);
            execPO.setEtlExecId(results.getExecId());
            execPO.setEtlRunningId(results.getRunningId());
            execPO.setSubTaskId(taskPO.getSubTaskId());

            execPO.setStatus(CommonUtils.convertLocalStatus(results.getResult()));
            Date startTime = results.getStartTime();
            if(startTime!=null){
                execPO.setStartTime(startTime);
            }

            execPO.setCreator(taskPO.getCreator());
            execPO.setModifier(taskPO.getCreator());
            execPO.setModifyTime(new Date());
            execPO.setCreateTime(new Date());
            subTaskExecDAO.insert(execPO);

        }else if(StringUtils.equals(exchangeResult, "SingleEnd") ||
                    StringUtils.equals(exchangeResult, "SingleEndError") ||
                        StringUtils.equals(exchangeResult, "Finished") ||
                            StringUtils.equals(exchangeResult, "Finished (with errors)") ||
                                StringUtils.equals(exchangeResult, "Stopped")){
            String runningId = results.getRunningId();
            String subscribeId = results.getSubscribeId();
            SubTaskExecPO execHistoryPo = subTaskExecDAO.getByEtlSubscribeAndRunningId(subscribeId, runningId);
            if(execHistoryPo==null){
                LOG.error("ETL 交换任务返回任务runningId没有记录, {}-{}-{}, Rusults:{}", results.getSubscribeId(),
                        results.getExecId(),results.getRunningId(), results.getResult());
                return getFeedBackDto(CommonConstants.EC_NOT_EXISTED_VALUE, "ETL SubscribeId或者RunningId不存在");
            }

            execHistoryPo.setStatus(result);
            Date endTime = results.getEndTime();
            if(endTime!=null){
                execHistoryPo.setEndTime(endTime);
            }
            execHistoryPo.setImportCount(results.getStockInCount()==null ? 0 : results.getStockInCount());
            execHistoryPo.setModifyTime(new Date());
            if(execHistoryPo.getImportCount()==0){  //交换数据为空，说明没有交换不需要存储记录。
                subTaskExecDAO.deleteById(execHistoryPo.getId());
            }else {
                importCount = execHistoryPo.getImportCount();
                subTaskExecDAO.updateById(execHistoryPo);
            }
        }

        if(!StringUtils.equals(taskPO.getStatus(), result)) {
            taskPO.setStatus(result);
            if (StringUtils.equals(result, CommonConstants.IMPORTING)) {
                Date startTime = results.getStartTime();
                if (startTime != null) {
                    taskPO.setLastRunTime(startTime);
                }
            } else if (importCount!=0 && StringUtils.equals(result, CommonConstants.IMPORT_COMPLETE)) {
                //导入完成时候，subTask需要把 subTaskExec 里面import数据总和计算出来
                Long toltalCounts = subTaskExecDAO.getTotalImport(etlSubscribeId);
                taskPO.setImportCount(toltalCounts);

                //增加对交换数据的统计
                SubscribePO sPO = subscribeDAO.getBySubNo(taskPO.getSubTaskId());
                if(sPO!=null) {
                    resourceStatiscsService.increaseShareDataCount(sPO.getResourceId(), toltalCounts);
                }
            } else if(StringUtils.equals(result, CommonConstants.STOP_IMPORT)){
                if(importCount!=0) {
                    Long toltalCounts = subTaskExecDAO.getTotalImport(etlSubscribeId);
                    taskPO.setImportCount(toltalCounts);
                }
                LOG.info("ETL交换任务暂停：{}-{}-{}, Rusults:{}", results.getSubscribeId(),
                        results.getExecId(),results.getRunningId(), results.getResult());
            }else if(StringUtils.equals(result, CommonConstants.IMPORT_ERROR)){
                LOG.error("ETL交换任务出错：{}-{}-{}, Rusults:{}", results.getSubscribeId(),
                        results.getExecId(),results.getRunningId(), results.getResult());
            }
            taskPO.setModifyTime(new Date());
            subTaskDAO.updateById(taskPO);
        }
        return getFeedBackDto(CommonConstants.SUCCESS_VALUE, "ETL任务执行结果为空");
    }

    private StatusFeedbackDto getFeedBackDto(Integer statusCode, String errMsg){

        StatusFeedbackDto statusFeedbackDto = new StatusFeedbackDto();
        statusFeedbackDto.setStatusCode(statusCode);
        statusFeedbackDto.setErrMsg(errMsg);
        if(statusCode==CommonConstants.SUCCESS_VALUE){
            LOG.info("ETL调用返回：{}", statusFeedbackDto.toString());
        }else{
            LOG.error("ETL调用异常返回: {}", statusFeedbackDto.toString());
        }
        return statusFeedbackDto;
    }

	private void saveOrUpdateResourceFile(DataUploadDetailPO dataUploadDetailPO, DataUploadPO dataUploadPO,
										  ResourceFilePO resourceFilePO) throws RuntimeException {
		//如果上传文件已存在, 则进行更新操作
		if (resourceFilePO != null) {
			int fileVersion = resourceFilePO.getFileVersion();
			fileVersion++;
			resourceFilePO.setFileVersion(fileVersion);

			resourceFilePO.setResourceId(dataUploadPO.getResourceId());
			resourceFilePO.setOriginFileName(dataUploadDetailPO.getOriginFileName());
			resourceFilePO.setPubFileName(dataUploadDetailPO.getPubFileName());
			resourceFilePO.setDataBatch(dataUploadPO.getDataBatch());
			resourceFilePO.setFileSize(dataUploadDetailPO.getFileSize());
			resourceFilePO.setFileType(dataUploadDetailPO.getFileType());
			resourceFilePO.setFileDescription(dataUploadDetailPO.getFileDescription());

			resourceFilePO.setModifier(dataUploadPO.getCreator());
			resourceFilePO.setModifyTime(dataUploadPO.getImportTime());
			resourceFileDAO.updateResourceFile(resourceFilePO);
		} else {
			resourceFilePO = new ResourceFilePO();

			resourceFilePO.setResourceId(dataUploadPO.getResourceId());
			resourceFilePO.setOriginFileName(dataUploadDetailPO.getOriginFileName());
			resourceFilePO.setPubFileName(dataUploadDetailPO.getPubFileName());
			resourceFilePO.setDataBatch(dataUploadPO.getDataBatch());
			resourceFilePO.setFileSize(dataUploadDetailPO.getFileSize());
			resourceFilePO.setFileType(dataUploadDetailPO.getFileType());
			resourceFilePO.setFileDescription(dataUploadDetailPO.getFileDescription());

			resourceFilePO.setModifier(dataUploadPO.getModifier());
			resourceFilePO.setModifyTime(dataUploadPO.getImportTime());
			resourceFilePO.setFileVersion(1);
			resourceFilePO.setCreator(dataUploadPO.getCreator());
			resourceFilePO.setCreateTime(new Date());
			resourceFileDAO.insertResourceFile(resourceFilePO);
		}
	}
}
