package com.ys.idatrix.cloudetl.dubbo.service.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.metrics2.sink.relocated.google.common.collect.Lists;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.logging.CloudLogListener;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.repository.RepositoryElementMetaInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.dubbo.config.annotation.Service;
import com.google.common.collect.Maps;
import com.ys.idatrix.cloudetl.dto.common.ReturnCodeDto;
import com.ys.idatrix.cloudetl.dto.history.ExecHistoryRecordDto;
import com.ys.idatrix.cloudetl.dto.history.ExecHistorySegmentingPartDto;
import com.ys.idatrix.cloudetl.ext.CloudApp;
import com.ys.idatrix.cloudetl.ext.CloudSession;
import com.ys.idatrix.cloudetl.ext.executor.CloudExecution;
import com.ys.idatrix.cloudetl.ext.executor.CloudExecution.ExecutionInfo;
import com.ys.idatrix.cloudetl.ext.executor.CloudExecutorStatus;
import com.ys.idatrix.cloudetl.ext.executor.CloudJobExecutor;
import com.ys.idatrix.cloudetl.ext.executor.logger.CloudExecHistory;
import com.ys.idatrix.cloudetl.ext.executor.logger.CloudExecLog;
import com.ys.idatrix.cloudetl.ext.utils.StringEscapeHelper;
import com.ys.idatrix.cloudetl.logger.CloudLogType;
import com.ys.idatrix.cloudetl.logger.CloudLogger;
import com.ys.idatrix.cloudetl.repository.CloudRepository;
import com.ys.idatrix.cloudetl.repository.database.dto.FileRepositoryDto;
import com.ys.idatrix.cloudetl.service.CloudSubscribeCreateService;
import com.ys.idatrix.cloudetl.service.CloudSubscribeStepService;
import com.ys.idatrix.cloudetl.subcribe.SubcribePushService;
import com.ys.idatrix.cloudetl.subscribe.api.dto.CreateJobDto;
import com.ys.idatrix.cloudetl.subscribe.api.dto.QueryJobDto;
import com.ys.idatrix.cloudetl.subscribe.api.dto.SubscribeResultDto;
import com.ys.idatrix.cloudetl.common.api.dto.PaginationDto;
import com.ys.idatrix.cloudetl.subscribe.api.dto.parts.SubscribeMeasureDto;
import com.ys.idatrix.cloudetl.subscribe.api.service.SubscribeService;
import com.ys.idatrix.cloudetl.util.SubcribeUtils;

@Component
@Service(timeout=180000,interfaceClass=SubscribeService.class)
public class CloudSubscribeServiceImpl implements SubscribeService {

	public static final Log  logger = LogFactory.getLog("Dubbo订阅任务");
	
	@Autowired
	private CloudSubscribeCreateService cloudSubscribeCreateService;
	
	@Autowired
	private CloudSubscribeStepService cloudSubscribeStepService;
	
	@SuppressWarnings("rawtypes")
	@Override
	public SubscribeResultDto createSubscribeJob(CreateJobDto createJobDto)  {
		//构建结果对象
		SubscribeResultDto result =  new SubscribeResultDto();
		result.setName(createJobDto.getName());
		String userId = createJobDto.getUserId() ;
		//保存用户信息
		if(!init(createJobDto.getUserId())) {
			result.setStatus(-1);
			result.setErrorMessage("初始化用户失败!");
			return result ;
		}
		CloudLogger.getInstance().addNumber().info(this,"创建订阅任务："+createJobDto.toString());
		
		String name =  createJobDto.getName() ;
		String group = Const.NVL(createJobDto.getGroup(), SubcribeUtils.DEFAULT_GROUP_NAME);
		result.setGroup(group);
		String jobName = null ;
		//创建转换文件
		try {
			//该任务相关的参数属性设置对象
			Map<String,String> params = Maps.newHashMap();
			params.put(CloudLogListener.LOG_SEGMENTINGPART_ENABLE, "true");//job日志分次
			params.put(SubcribePushService.SUBCRIBE_PUSH_KEY,  "true");//推送运行状态到订阅服务
			
			//创建转换文件
			String dataDescription= "获取数据并写入目的库，"+Const.NVL(createJobDto.getDescription(),"") ;
			String transName = cloudSubscribeCreateService.createTransMeta( name , group, dataDescription , params , createJobDto.getDataInput(), createJobDto.getFilterCondition(), createJobDto.getTransDataOutputs());
			if( !Utils.isEmpty(transName) ) {
				//有Trans已经被创建
				params.put(CloudLogListener.LOG_MAINTRANSNAME, transName);//主trans
			}
			
			//创建调度文件
			String jobDescription=  "定时执行转换操作数据，"+Const.NVL(createJobDto.getDescription(),"");
			jobName  = cloudSubscribeCreateService.createJobMeta(name, group, jobDescription , params , createJobDto.getTimer(), transName,createJobDto.getJobDataOutputs());
			if( !Utils.isEmpty(jobName)) {
				//有调度job已经被创建
				result.setSubscribeId(jobName);
				CloudLogger.getInstance().info(this,"成功创建，任务名:"+jobName);
			}
			
		} catch (Exception e) {
			CloudLogger.getInstance().error(this,"创建失败:",e);
			result.setStatus(-1);
			result.setErrorMessage("创建失败:"+CloudLogger.getExceptionMessage(e,false));
			
			//清理线程用户信息
			CloudSession.clearThreadInfo();
			
			return result ;
		}
		
		if( !Utils.isEmpty(jobName)) {
			//启动执行job
			try {
				result.setCurStatus(CloudExecutorStatus.STOPPED.getType());	
				if( createJobDto.isImmediatelyRun() ) {
					//立即执行
					String execId = null ;
					ReturnCodeDto jer = cloudSubscribeStepService.startJob(jobName, group, cloudSubscribeCreateService.updateMeta(userId,jobName,  group, createJobDto.getParams()),createJobDto.isRemoteRun());
					if(jer != null && jer.isSuccess() ) {
						if( jer.getData() != null && ( jer.getData() instanceof Map ) ) {
							execId = (String) ( (Map)jer.getData()).get("executionId");
							result.setCurExecId(execId);
							result.setCurStatus(CloudExecutorStatus.RUNNING.getType());
						}
						CloudLogger.getInstance().info(this,"成功启动任务，任务名:"+jobName);
					}else {
						result.setStatus(-2);
						result.setErrorMessage( "启动任务失败,"+jer.getMessage() );
						if( jer.getRetCode() == 2 ) {
							//已经在运行,重复启动
							result.setStatus(2);
						}
						CloudLogger.getInstance().info(this,"启动任务失败，任务名:"+jobName+",message:"+jer.getMessage());
						return result;
					}
					
				}
			} catch (Exception e) {
				CloudLogger.getInstance().error(this,"启动job("+jobName+")失败:",e);
				result.setStatus(-2);
				result.setErrorMessage("启动job("+jobName+")失败:"+CloudLogger.getExceptionMessage(e,false));
				//清理线程用户信息
				CloudSession.clearThreadInfo();
				
				return result ;
			}
		}else {
			CloudLogger.getInstance().info(this,"任务名为空,创建失败.");
			result.setStatus(-1);
			result.setErrorMessage("任务名为空,创建失败.");
			return result ;
		}
		//清理线程用户信息
		CloudSession.clearThreadInfo();
		
		return result;
	}

	@Override
	public PaginationDto<SubscribeResultDto> getSubscribeJobList(QueryJobDto queryJobDto) {
		//定义分页对象		
		PaginationDto<SubscribeResultDto> result = new PaginationDto<SubscribeResultDto>(queryJobDto.getPage(), queryJobDto.getPageSize(),queryJobDto.getSearch());
		result.setCommons(new SubscribeResultDto());
		//保存用户信息
		if(!init(queryJobDto.getUserId())) {
			result.getCommons().setStatus(-1);
			result.getCommons().setErrorMessage("初始化用户失败!");
			return result ;
		}
		
		CloudLogger.getInstance().addNumber().info(this,"查询任务列表："+queryJobDto.toString());
		try {
			if(Utils.isEmpty(queryJobDto.getSubscribeId())) {
				//任务列表
				Map<String, List<Object>>  allMap = CloudRepository.getJobElementsMap(queryJobDto.getUserId(),CloudRepository.ALL_GROUP_NAME);
				List<Object> userList = allMap.get(queryJobDto.getUserId());
				List<Object> jobNames = userList.stream().filter(remi -> { 
					String jobName ="";
					if(remi instanceof RepositoryElementMetaInterface){
						RepositoryElementMetaInterface ele = (RepositoryElementMetaInterface) remi;
						jobName  = ele.getName();
					}else if(remi instanceof FileRepositoryDto){
						FileRepositoryDto ele = (FileRepositoryDto) remi;
						jobName = ele.getName();
					}
					return SubcribeUtils.isSubcribeJob(queryJobDto.getUserId(), jobName);
				}).collect(Collectors.toList());
				result.processingDataPaging(jobNames, new PaginationDto.DealRowsInterface<SubscribeResultDto>() {
					@Override
					public SubscribeResultDto dealRow(Object obj,Object... params) throws Exception {

						String subscribeId = "";
						String group = "";
						if(obj instanceof RepositoryElementMetaInterface){
							RepositoryElementMetaInterface ele = (RepositoryElementMetaInterface) obj;
							subscribeId  = ele.getName();
							group = ele.getRepositoryDirectory().getName();
						}else if(obj instanceof FileRepositoryDto){
							FileRepositoryDto ele = (FileRepositoryDto) obj;
							subscribeId = ele.getName();
							group = ele.getGroup() ;
						}
						
						if(Utils.isEmpty(subscribeId)) {
							return null;
						}
						SubscribeResultDto sr =  getInitResult(queryJobDto.getUserId(), subscribeId,group);
						if(queryJobDto.isIncloudDetail()){
							//获取详细信息(汇总信息)
							CloudExecHistory execHistory = CloudExecHistory.initExecHistory(queryJobDto.getUserId(), subscribeId, CloudLogType.JOB_HISTORY);
							ExecHistoryRecordDto history = execHistory.getTotalExecRecord();
							SubscribeMeasureDto sm = new SubscribeMeasureDto();
							sm.setEndTime(history.getEndStr());
							sm.setStartTime(history.getBeginStr());
							sm.setInputLines(history.getInputLines());
							sm.setOutputLines(history.getOutputLines());
							sm.setUpdateLines(history.getUpdateLines());
							sm.setError(history.getErrorLines());
							sr.setMeasure(sm);
						}
						return sr;
					}
	
					@Override
					public boolean match(Object obj, String search,Object... params) {
						String subscribeId = "" ;
						if(obj instanceof RepositoryElementMetaInterface){
							RepositoryElementMetaInterface ele = (RepositoryElementMetaInterface) obj;
							subscribeId  = ele.getName();
						}else if(obj instanceof FileRepositoryDto){
							FileRepositoryDto ele = (FileRepositoryDto) obj;
							subscribeId = ele.getName();
						}
						
						return defaultMatch(subscribeId, search);
					}
					
				});
			}else {
				//任务下的 分次执行信息
				//公共信息
				String subscribeId = queryJobDto.getSubscribeId();
				SubscribeResultDto res = getInitResult(queryJobDto.getUserId(),subscribeId,null);
				
				//获取单个信息
				ExecHistoryRecordDto history = null;
				CloudExecHistory execHistory = CloudExecHistory.initExecHistory(queryJobDto.getUserId(), subscribeId, CloudLogType.JOB_HISTORY);
				if(Utils.isEmpty(queryJobDto.getExecId())) {
					history =execHistory.getTotalExecRecord();
				}else {
					history = execHistory.getExecRecord(queryJobDto.getExecId());
				}
				
				//汇总信息
				SubscribeMeasureDto sm = new SubscribeMeasureDto();
				sm.setEndTime(history.getEndStr());
				sm.setStartTime(history.getBeginStr());
				sm.setInputLines(history.getInputLines());
				sm.setOutputLines(history.getOutputLines());
				sm.setUpdateLines(history.getUpdateLines());
				sm.setError(history.getErrorLines());
				result.getCommons().setMeasure(sm);
				
				result.processingDataPaging(execHistory.getSegmentingParts(), new PaginationDto.DealRowsInterface<SubscribeResultDto>() {
					@Override
					public SubscribeResultDto dealRow(Object obj,Object... params) throws Exception {
						ExecHistorySegmentingPartDto ecPart = (ExecHistorySegmentingPartDto)obj;
						SubscribeResultDto sr = res.clone();
						
						SubscribeMeasureDto sm = new SubscribeMeasureDto();
						sm.setRunId(ecPart.getId().toString());
						sm.setExecId(ecPart.getExecId());
						sm.setStatus(ecPart.getStatus());
						sm.setEndTime(ecPart.getEndStr());
						sm.setStartTime(ecPart.getBeginStr());
						sm.setInputLines(ecPart.getInputLines());
						sm.setOutputLines(ecPart.getOutputLines());
						sm.setUpdateLines(ecPart.getUpdateLines());
						sm.setError(ecPart.getErrorLines());
						sr.setMeasure(sm);
						return sr;
					}
	
					@Override
					public boolean match(Object obj, String search,Object... params) {
						ExecHistorySegmentingPartDto ecPart = (ExecHistorySegmentingPartDto)obj;
						return defaultMatch(ecPart.getId().toString(), search);
					}
					
				});
			}
		}catch( Exception e) {
			CloudLogger.getInstance().error(this,"查询任务列表失败:",e);
			result.getCommons().setStatus(-1);
			result.getCommons().setErrorMessage("查询任务列表失败,"+CloudLogger.getExceptionMessage(e,false));
			//清理线程用户信息
			CloudSession.clearThreadInfo();
			
			return result;
		}
		CloudLogger.getInstance().info(this,"查询任务列表结束，"+result.toString());
		//清理线程用户信息
		CloudSession.clearThreadInfo();
		
		return result;
	}

	@Override
	public SubscribeResultDto deleteSubscribeJob(QueryJobDto queryJobDto) {
		//删除订阅任务
		SubscribeResultDto res = new SubscribeResultDto();
		//保存用户信息
		if(!init(queryJobDto.getUserId())) {
			res.setStatus(-1);
			res.setErrorMessage("初始化用户失败!");
			return res ;
		}
		
		CloudLogger.getInstance().addNumber().info(this,"删除任务："+queryJobDto.toString());
		try {
			if((queryJobDto.getSubscribeIds() == null || queryJobDto.getSubscribeIds().size() == 0) && !Utils.isEmpty(queryJobDto.getSubscribeId())) {
				//删除一个
				queryJobDto.setSubscribeIds(Lists.newArrayList(queryJobDto.getSubscribeId()));
			}
			
			for(String jobName : queryJobDto.getSubscribeIds()){
				String group = getGroupName(queryJobDto.getUserId() ,jobName, null);
				
				String name = SubcribeUtils.getNameFromId(queryJobDto.getUserId(), jobName);
				if(!Utils.isEmpty(name)) {
					 ExecutionInfo executionInfo = CloudExecution.getInstance().getExecutionInfo(queryJobDto.getUserId(), jobName, true);
					if( executionInfo!= null  ) {
						ReturnCodeDto stopRes = cloudSubscribeStepService.StopJob(executionInfo.executionId);
						if(!stopRes.isSuccess()) {
							res.setStatus(-1);
							res.setErrorMessage("停止任务["+jobName+"]失败:"+stopRes.getMessage());
							return res;
						}
					}
					String transName =  SubcribeUtils.getTransName(queryJobDto.getUserId(), name);
					ReturnCodeDto transRes = cloudSubscribeStepService.deleteTransMeta(queryJobDto.getUserId() ,transName,group);
					if(!transRes.isSuccess()) {
						res.setStatus(-1);
						res.setErrorMessage("删除trans["+transName+"]失败:"+transRes.getMessage());
						return res;
					}
					ReturnCodeDto jobRes = cloudSubscribeStepService.deleteJobMeta(queryJobDto.getUserId() ,jobName,group);
					if(!jobRes.isSuccess()) {
						res.setStatus(-1);
						res.setErrorMessage("删除job["+jobName+"]失败:"+jobRes.getMessage());
						return res;
					}
				}
			}
		}catch( Exception e) {
			CloudLogger.getInstance().error(this,"删除任务失败:",e);
			res.setStatus(-1);
			res.setErrorMessage("删除任务失败,"+CloudLogger.getExceptionMessage(e,false));
			//清理线程用户信息
			CloudSession.clearThreadInfo();
			
			return res;
		}
		CloudLogger.getInstance().info(this,"删除任务成功，"+res.toString());
		//清理线程用户信息
		CloudSession.clearThreadInfo();
		
		return res;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public SubscribeResultDto startSubscribeJob(QueryJobDto queryJobDto) {
		//启动订阅任务
		//保存用户信息
		String subscribeId = queryJobDto.getSubscribeId();
		String userId = queryJobDto.getUserId();
		SubscribeResultDto res = getInitResult(queryJobDto.getUserId(), subscribeId, queryJobDto.getGroup());
		if(res.getStatus() != 0) {
			return res ;
		}
		String group = res.getGroup();
		
		CloudLogger.getInstance().addNumber().info(this,"启动任务："+queryJobDto.toString());
		try {
			String execId = null ;
			ReturnCodeDto jer = cloudSubscribeStepService.startJob(subscribeId,group,cloudSubscribeCreateService.updateMeta(userId,subscribeId,group, queryJobDto.getParams()),queryJobDto.isRemoteRun());
			if(jer != null && jer.isSuccess() ) {
				if( jer.getData() != null && ( jer.getData() instanceof Map ) ) {
					execId = (String) ( (Map)jer.getData()).get("executionId");
					res.setCurExecId(execId);
					res.setCurStatus(CloudExecutorStatus.RUNNING.getType());
				}
			}else {
				res.setStatus(jer.getRetCode());
				res.setErrorMessage( "启动任务失败,"+jer.getMessage() );
				if( jer.getRetCode() == 2 ) {
					//已经在运行,重复启动
					res.setStatus(2);
				}
				CloudLogger.getInstance().info(this,"启动任务失败，任务名:"+queryJobDto.getSubscribeId()+",message:"+jer.getMessage());
				return res;
			}
				
		}catch(Exception e) {
			CloudLogger.getInstance().error(this,"启动任务失败:",e);
			res.setStatus(-1);
			res.setErrorMessage("启动任务失败,"+CloudLogger.getExceptionMessage(e,false));
			//清理线程用户信息
			CloudSession.clearThreadInfo();
			
			return res;
		}
		CloudLogger.getInstance().info(this,"启动任务成功，"+res.toString());
		//清理线程用户信息
		CloudSession.clearThreadInfo();
		
		return res;
	}

	@Override
	public SubscribeResultDto stopSubscribeJob(QueryJobDto queryJobDto) {
		//启动订阅任务
		//保存用户信息
		String subscribeId = queryJobDto.getSubscribeId();
		SubscribeResultDto res = getInitResult(queryJobDto.getUserId(), subscribeId, queryJobDto.getGroup());
		if(res.getStatus() != 0) {
			return res ;
		}
		
		CloudLogger.getInstance().addNumber().info(this,"停止任务："+queryJobDto.toString());
		//停止订阅任务
		try {
			 ExecutionInfo executionInfo = CloudExecution.getInstance().getExecutionInfo(queryJobDto.getUserId(), subscribeId, true);
			if( executionInfo != null ) {
				ReturnCodeDto stopRes = cloudSubscribeStepService.StopJob(executionInfo.executionId);
				if(stopRes != null && stopRes.isSuccess()) {
					res.setCurStatus(CloudExecutorStatus.HALTING.getType());
					CloudLogger.getInstance().info(this,"任务正在停止...,任务名:"+queryJobDto.getSubscribeId());
				}else {
					CloudLogger.getInstance().info(this,"停止任务失败，任务名:"+queryJobDto.getSubscribeId()+",message:"+stopRes.getMessage());
					res.setStatus(-2);
					res.setErrorMessage(stopRes.getMessage());
					return res ;
				}
			}else {
				CloudLogger.getInstance().info(this,"任务本已经停止，任务名:"+queryJobDto.getSubscribeId());
				res.setCurStatus(CloudExecutorStatus.STOPPED.getType());
			}
		}catch(Exception e) {
			CloudLogger.getInstance().error(this,"停止任务失败:",e);
			res.setStatus(-1);
			res.setErrorMessage("停止任务失败,"+CloudLogger.getExceptionMessage(e,false));
			//清理线程用户信息
			CloudSession.clearThreadInfo();
			
			return res;
		}
		CloudLogger.getInstance().info(this,"任务停止成功，"+res.toString());
		//清理线程用户信息
		CloudSession.clearThreadInfo();
		
		return res;
	}

	@Override
	public SubscribeResultDto getSubscribeJobInfo(QueryJobDto queryJobDto) {
		//获取订阅任务信息
		String subscribeId = queryJobDto.getSubscribeId();
		String execId = queryJobDto.getExecId();
		String runId =  queryJobDto.getRunId() ;
		//保存用户信息
		SubscribeResultDto res = getInitResult(queryJobDto.getUserId(), subscribeId,queryJobDto.getGroup());
		if(res.getStatus() != 0) {
			return res ;
		}
				
		CloudLogger.getInstance().addNumber().info(this,"获取任务信息："+queryJobDto.toString());
		try {
			//获取单个信息
			CloudExecHistory execHistory = CloudExecHistory.initExecHistory(queryJobDto.getUserId(), subscribeId , CloudLogType.JOB_HISTORY);
			ExecHistoryRecordDto history = null;
			ExecHistorySegmentingPartDto part = null;
			if(Utils.isEmpty(runId)) {
				if(Utils.isEmpty(execId)) {
					history  =execHistory.getLastExecRecord();
					part = execHistory.getLastSegmentingPart();
				}else{
					history = execHistory.getExecRecord(execId);
					part = execHistory.getLastSegmentingPart(execId);
				}
			}else {
				if(Utils.isEmpty(execId)) {
					history  =execHistory.getExecRecord(execId);
					part = execHistory.getSegmentingPart(execId, runId);
				}else{
					part = execHistory.getSegmentingPart(runId);
					if(part != null) {
						history  =execHistory.getExecRecord(part.getExecId());
					}
				}
			}
			if(part == null && history == null) {
				CloudLogger.getInstance().info(this,"未找到相应的订阅信息!");
				res.setStatus(-1);
				res.setErrorMessage("未找到相应的订阅信息!");
				return res ;
			}
			
			if(queryJobDto.isIncloudLog()) {
				CloudExecLog log = CloudExecLog.initExecLog(CloudApp.getInstance().getUserLogsRepositoryPath(queryJobDto.getUserId()), subscribeId, CloudLogType.JOB_LOG);
				String logText = null ;
				if( part != null ) {
					logText = log.getPartExecLog(part.getId().toString()) ;
				}
				if(Utils.isEmpty(logText)) {
					logText = log.searchExecLog(execId, history.getBeginStr(), history.getEndStr());
				}
				res.setLog(StringEscapeHelper.encode(logText));
			}
			
			SubscribeMeasureDto sm = new SubscribeMeasureDto();
			sm.setRunId(  part != null ? part.getId().toString() : null);
			sm.setExecId( part != null ? part.getExecId() : execId);
			sm.setStatus( part != null ? part.getStatus() : history.getStatus() );
			sm.setEndTime( part != null ? part.getEndStr() : history.getEndStr());
			sm.setStartTime( part != null ? part.getBeginStr() : history.getBeginStr());
			sm.setInputLines( part != null ? part.getInputLines() : history.getInputLines());
			sm.setOutputLines( part != null ? part.getOutputLines() : history.getOutputLines());
			sm.setUpdateLines( part != null ? part.getUpdateLines() : history.getUpdateLines());
			sm.setError( part != null ? part.getErrorLines() : history.getErrorLines());
			res.setMeasure(sm);
		}catch(Exception e) {
			CloudLogger.getInstance().error(this,"获取任务信息失败:",e);
			res.setStatus(-1);
			res.setErrorMessage("获取任务信息失败,"+CloudLogger.getExceptionMessage(e,false));
			//清理线程用户信息
			CloudSession.clearThreadInfo();
			
			return res;
		}
		
		CloudLogger.getInstance().info(this,"获取任务信息成功，"+res.toString());
		//清理线程用户信息
		CloudSession.clearThreadInfo();
		
		return res;
	}

	/**
	 * 保存用户信息
	 * @param dto
	 * @throws Exception 
	 */
	private boolean init(String user) {
		if( Utils.isEmpty(user)) {
			logger.error("userId 为空！");
			return false;
		}
		//保存用户信息
		CloudSession.setThreadLoginUser(user);
		return true;
	}
	/**
	 * 获取初始化过的返还结果对象，赋值name,当前状态，当前执行id等信息	
	 * @param userId
	 * @param subscribeId
	 * @return
	 * @throws Exception
	 */
	private SubscribeResultDto getInitResult(String userId,String subscribeId,String group ) { 
		SubscribeResultDto res = new SubscribeResultDto();
		//保存用户信息
		if(!init(userId)) {
			res.setStatus(-1);
			res.setErrorMessage("初始化用户失败!");
			return res ;
		}
		
		if(Utils.isEmpty(subscribeId) ) {
			res.setStatus(-1);
			res.setErrorMessage("SubscribeId 为空!");
			return res ;
		}
		
		String name = SubcribeUtils.getNameFromId( userId , subscribeId);
		group = getGroupName(userId ,subscribeId, group);
		String runStatus = CloudExecutorStatus.STOPPED.getType();
		
		//获取job状态
		String executionId ="";
		 ExecutionInfo executionInfo = CloudExecution.getInstance().getExecutionInfo(userId, subscribeId, true);
		if (executionInfo != null) {
			executionId = executionInfo.executionId ;
			CloudJobExecutor jobExecutor = executionInfo.getJobExecutor();
			if (jobExecutor != null) {
				runStatus = jobExecutor.getStatus() ;
			}
		}
		
		res.setSubscribeId(subscribeId);
		res.setName(name);
		res.setGroup(group);
		res.setCurExecId(executionId);
		res.setCurStatus(runStatus);
		
		return res;
	}

	
	private String getGroupName(String userId ,String jobName,String group) {
		if(Utils.isEmpty(group)) {
			try {
				group = cloudSubscribeStepService.getJobGroupName(userId ,jobName) ;
			} catch (Exception e) {
				group = SubcribeUtils.DEFAULT_GROUP_NAME;
			}
		}
		
		return group ;
	}
	
}
