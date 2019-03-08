package com.ys.idatrix.cloudetl.webservice.impl;

import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.di.core.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;

import com.ys.idatrix.cloudetl.logger.CloudLogger;
import com.ys.idatrix.cloudetl.subscribe.api.dto.CreateJobDto;
import com.ys.idatrix.cloudetl.subscribe.api.dto.SubscribeResultDto;
import com.ys.idatrix.cloudetl.subscribe.api.dto.step.FileCopyDto;
import com.ys.idatrix.cloudetl.subscribe.api.dto.step.FileInputDto;
import com.ys.idatrix.cloudetl.subscribe.api.dto.step.FilterRowsDto;
import com.ys.idatrix.cloudetl.subscribe.api.dto.step.InsertUpdateDto;
import com.ys.idatrix.cloudetl.subscribe.api.dto.step.StepDto;
import com.ys.idatrix.cloudetl.subscribe.api.dto.step.TableInputDto;
import com.ys.idatrix.cloudetl.subscribe.api.dto.step.TimerDto;
import com.ys.idatrix.cloudetl.subscribe.api.service.SubscribeService;
import com.ys.idatrix.cloudetl.webservice.SubscribeWebService;

@WebService(targetNamespace="SubscribeWebServiceNamespace")
public class SubscribeWebServiceImpl implements SubscribeWebService {

	public static final Log  logger = LogFactory.getLog("subcribeWebService");
	
	@Autowired
	private SubscribeService subscribeService;
	
	@Override
	public SubscribeResultDto creteTableToTableJob(String userId,String name, TimerDto timer, TableInputDto dbInput,
			FilterRowsDto filter, InsertUpdateDto output) {
		logger.info("创建表到表的交换任务:"+name);
		SubscribeResultDto result = new SubscribeResultDto();
		try {
			if(Utils.isEmpty(name) ||dbInput == null || output == null) {
				result.setStatus(-1);
				result.setErrorMessage("name,dbInput,output 必须不为空");
				return result;
			}
			result = createJob(userId, name,  timer, dbInput, filter, output);
		} catch (Exception e) {
			logger.error("创建表到表的交换任务失败",e);
			result.setStatus(-1);
			result.setErrorMessage("创建表到表的交换任务失败"+CloudLogger.getExceptionMessage(e,false));
		}
		return result;
	}
	
	@Override
	public SubscribeResultDto creteFileToTableJob(String userId, String name, TimerDto timer,
			FileInputDto fileInput, FilterRowsDto filter, InsertUpdateDto output) {
		logger.info("创建文件到表的交换任务:"+name);
		SubscribeResultDto result = new SubscribeResultDto();
		try {
			if(Utils.isEmpty(name) ||fileInput == null || output == null) {
				result.setStatus(-1);
				result.setErrorMessage("name,fileInput,output 必须不为空");
				return result;
			}
			result = createJob(userId, name,  timer, fileInput, filter, output);
		} catch (Exception e) {
			logger.error("创建文件到表的交换任务失败",e);
			result.setStatus(-1);
			result.setErrorMessage("创建文件到表的交换任务失败"+CloudLogger.getExceptionMessage(e,false));
		}
		return result;
	}
	
	@Override
	public SubscribeResultDto creteFileCopyJob(String userId, String name, FileCopyDto fileCopy) {
		logger.info("创建文件复制的交换任务:"+name);
		SubscribeResultDto result = new SubscribeResultDto();
		try {
			if(Utils.isEmpty(name) ||fileCopy == null ) {
				result.setStatus(-1);
				result.setErrorMessage("name,fileCopy 必须不为空");
				return result;
			}
			result = createJob(userId, name,  null, null, null, fileCopy);
		} catch (Exception e) {
			logger.error("创建文件复制的交换任务失败",e);
			result.setStatus(-1);
			result.setErrorMessage("创建文件复制的交换任务失败"+CloudLogger.getExceptionMessage(e,false));
		}
		return result;
	}


	@SuppressWarnings("deprecation")
	private SubscribeResultDto createJob(String userId,String name,StepDto timer, StepDto input,
			StepDto filter, StepDto output) throws Exception {
		CreateJobDto createJobDto = new CreateJobDto(userId);
		createJobDto.setName(name);
		createJobDto.setTimer(timer);
		createJobDto.setDataInput(input);
		createJobDto.setFilterCondition(filter);
		createJobDto.setDataOutput(output);
		return subscribeService.createSubscribeJob(createJobDto );
	}




}
