package com.ys.idatrix.cloudetl.service;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.pentaho.di.core.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ys.idatrix.cloudetl.dto.common.ReturnCodeDto;
import com.ys.idatrix.cloudetl.dto.entry.entries.general.SPTrans;
import com.ys.idatrix.cloudetl.ext.CloudSession;
import com.ys.idatrix.cloudetl.logger.CloudLogger;
import com.ys.idatrix.cloudetl.service.step.StepServiceInterface;
import com.ys.idatrix.cloudetl.subscribe.api.dto.step.StepDto;
import com.ys.idatrix.cloudetl.subscribe.api.dto.step.TimerDto;
import com.ys.idatrix.cloudetl.util.SubcribeUtils;

@Component
public class CloudSubscribeCreateService {

	@Autowired
	private CloudSubscribeStepService cloudSubscribeStepService;
	
	public String createTransMeta(String name ,String group ,String description,Map<String,String> params, StepDto dataInput ,StepDto filterCondition ,List<StepDto> dataOutputs) throws Exception {
		if(dataInput == null){
			return null;
		}
		String userId = CloudSession.getLoginUser();
		String transName = SubcribeUtils.getTransName( userId ,name);
		
		ReturnCodeDto transResult = cloudSubscribeStepService.createTransMeta(transName,group, description);
		if(transResult.isSuccess()) {
			//transmeta 创建成功 ， 数据输入 -> 记录过滤 -> 数据输出/ES
			try{
				//增加trans输入步骤
				StepServiceInterface<StepDto> inputService = cloudSubscribeStepService.getStepService(dataInput);
				inputService.addStepToMeta(transName, group, params);
				List<String> preOutStepNames  = inputService.getOutStepNames() ;
				
				//增加条件过滤
				if(filterCondition != null ) {
					StepServiceInterface<StepDto> filterService = cloudSubscribeStepService.getStepService(filterCondition,dataInput);
					filterService.addStepToMeta(transName, group, params) ;
					filterService.addPreStepHop(transName, group, preOutStepNames);
					preOutStepNames =  filterService.getOutStepNames() ;
				}
				
				//增加trans输出步骤
				if(dataOutputs != null && dataOutputs.size() > 0 ) {
					for(StepDto dataOutput : dataOutputs ) {
						StepServiceInterface<StepDto> outputService = cloudSubscribeStepService.getStepService(dataOutput,dataInput);
						outputService.addStepToMeta(transName, group, params) ;
						outputService.addPreStepHop(transName, group, preOutStepNames);
					}
				}
				
			}catch(Exception e) {
				//新建步骤出错，删除该trans
				cloudSubscribeStepService.deleteTransMeta(userId ,transName,group);
				throw new Exception(" 创建获取数据转换失败,"+CloudLogger.getExceptionMessage(e,false));
			}
		}else if( transResult.getRetCode() ==9) {
			//已经存在
			return transName;
		}else{
			throw new Exception(" 创建获取数据转换失败！"+transResult.getMessage());
		}
		
		return transName ;
	}
	
	public String createJobMeta(String name ,String group ,String description, Map<String,String> params, StepDto timerDto,String transName,List<StepDto> jobOutSteps ) throws Exception {
		
		String userId = CloudSession.getLoginUser();
		String jobName = SubcribeUtils.getJobName(userId ,name);
		
		ReturnCodeDto jobResult = cloudSubscribeStepService.createJobMeta(jobName, group,description, params);
		if(jobResult.isSuccess()) {
			//jobMeta创建成功   定时start -> 获取数据转换/文件复制
			try {
					//增加Start定时调度步骤
				if(timerDto == null ) {
					TimerDto td = new TimerDto();
					td.setRepeat(false);
					td.setSchedulerType(0);
					timerDto = td;
				}
				StepServiceInterface<StepDto> timerService = cloudSubscribeStepService.getStepService(timerDto);
				timerService.addStepToMeta(jobName, group, params);
				List<String> timerOutStepNames  = timerService.getOutStepNames() ;
					
				List<String> transOutStepNames = null ;
				if(!Utils.isEmpty(transName)) {
					//增加获取数据转换调度步骤
					String dataEntryName = "dataTrans";
					SPTrans dataTrans = cloudSubscribeStepService.createTrans(transName,group,null);
					cloudSubscribeStepService.addAndUpdateEntryMeta(jobName, group , dataEntryName, "TRANS", dataTrans);
					transOutStepNames = Lists.newArrayList(dataEntryName);
					//增加定时器与trans节点的连线
					timerService.addNextStepHop(jobName, group, transOutStepNames);
				}
					
				//增加额外输出步骤
				if(jobOutSteps != null && jobOutSteps.size() > 0 ) {
					for( StepDto jobOutStep : jobOutSteps) {
						StepServiceInterface<StepDto> outputService = cloudSubscribeStepService.getStepService(jobOutStep,timerDto);
						outputService.addStepToMeta(jobName, group, params);
						if(jobOutStep.isAppendTrans() && transOutStepNames != null && !transOutStepNames.isEmpty()) {
							outputService.addPreStepHop(jobName, group, transOutStepNames);
						}else {
							outputService.addPreStepHop(jobName, group, timerOutStepNames);
						}
					}
				}
				
			}catch( Exception e) {
				//新建entry出错，删除该job
				cloudSubscribeStepService.deleteJobMeta(userId ,jobName , group );
				throw new Exception(" 创建定时调度失败,"+CloudLogger.getExceptionMessage(e,false));
			}
		}else if(jobResult.getRetCode() == 9){
			//已经存在
			return jobName;
		} else {
			throw new Exception(" 创建定时调度失败！"+jobResult.getMessage());
		}
		
		return jobName ;
	}
	
	
	public Map<String,String> updateMeta(String userId , String subcribeId ,String group , Map<String,Object> params) throws Exception{
		if( params == null || params.size() == 0) {
			return Maps.newHashMap() ;
		}
		Map<String, String> result = params.entrySet().stream().filter(en -> { return en.getValue() instanceof String; }).collect(Collectors.toMap(en -> { return en.getKey() ;}, en -> { return (String)en.getValue() ;}));
		if(Utils.isEmpty(subcribeId)) {
			return result;
		}
		
		Map<String, StepDto> pp = params.entrySet().stream().filter(en -> { return (en.getValue() instanceof StepDto); }).collect(Collectors.toMap(en -> { return en.getKey() ;}, en -> { return (StepDto)en.getValue() ;}));
		if(pp != null && pp.size() >0) {
			
			String name = SubcribeUtils.getNameFromId( userId, subcribeId);
			String jobName = SubcribeUtils.getJobName( userId, name);
			String transName =  SubcribeUtils.getTransName( userId , name);
			
			for(Entry<String, StepDto> p : pp.entrySet()) {
				
				StepDto step = p.getValue() ;
				StepServiceInterface<StepDto> service = cloudSubscribeStepService.getStepService(step);
				service.addStepToMeta(step.isJobStep()?jobName:transName, group, Maps.newHashMap());
			}
		}
		return result;
		
	}
}
