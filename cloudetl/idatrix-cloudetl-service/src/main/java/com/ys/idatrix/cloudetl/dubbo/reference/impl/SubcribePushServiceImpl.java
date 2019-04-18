package com.ys.idatrix.cloudetl.dubbo.reference.impl;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.Result;
import org.pentaho.di.core.ResultFile;
import org.pentaho.di.core.logging.SegmentingPartInfo;
import org.pentaho.di.job.Job;
import org.pentaho.di.job.JobStatusChangeListener;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.google.common.collect.Maps;
import com.idatrix.resource.datareport.dto.ETLTaskResultDto;
import com.idatrix.resource.datareport.dto.StatusFeedbackDto;
import com.idatrix.resource.datareport.service.IETLTaskService;
import com.ys.idatrix.cloudetl.ext.CloudSession;
import com.ys.idatrix.cloudetl.ext.PluginFactory;
import com.ys.idatrix.cloudetl.ext.executor.CloudExecution;
import com.ys.idatrix.cloudetl.ext.executor.CloudExecution.ExecutionInfo;
import com.ys.idatrix.cloudetl.ext.executor.CloudExecutorStatus;
import com.ys.idatrix.cloudetl.ext.executor.CloudJobExecutor;
import com.ys.idatrix.cloudetl.ext.utils.RedisUtil;
import com.ys.idatrix.cloudetl.logger.CloudLogConst;
import com.ys.idatrix.cloudetl.logger.CloudLogger;
import com.ys.idatrix.cloudetl.subcribe.SubcribePushService;
import com.ys.idatrix.cloudetl.util.SubcribeUtils;

@Service
public class SubcribePushServiceImpl implements SubcribePushService {

	public static final Log logger = LogFactory.getLog("订阅任务状态推送");

	@Reference(check = false)
	private IETLTaskService taskService;

	@Override
	public JobStatusChangeListener createSubcribePushListener(String jobName) {

		JobStatusChangeListener statusListener = new JobStatusChangeListener() {
			
			@Override
			public void statusChange(Job job,String status,Result result, Object params)  {
				
				String owner = Const.NVL(CloudSession.getResourceUser() , job.getJobMeta().getVariable("idatrix.owner"));
				String execId = job.getJobMeta().getVariable("idatrix.executionId");
				
				try {
					ETLTaskResultDto oldDto = getPushFailData( getKey(execId, params) );
					if(oldDto != null) {
						//已经保存了一个 推送失败的旧状态,现有有了新的状态,旧状态进行删除,不需要再推送
						CloudLogger.getInstance(owner).info("SubcribePushService", "job["+job.getName()+"]的执行id["+execId+"]有新状态["+status+"],删除推送失败旧数据 ["+oldDto.getResult()+"]...");
						removePushFailData(  getKey(execId, params) );
					}

					ETLTaskResultDto res = new ETLTaskResultDto(SubcribeUtils.getNameFromId(owner, jobName), jobName, execId, status);
					res.setStockInTimeStamp(new Date());
					res.setUserId(owner);
					
					String outLines ="";
					if( params != null && params instanceof SegmentingPartInfo) {
						SegmentingPartInfo part = (SegmentingPartInfo)params;
						if(part.getEnd() != null) {
							res.setEndTime( DateUtils.parseDate(part.getEnd(), CloudLogConst.EXEC_TIME_PATTERN) );
						}
						if(part.getBegin() != null) {
							res.setStartTime(DateUtils.parseDate(part.getBegin(), CloudLogConst.EXEC_TIME_PATTERN) );
						}
						
						res.setRunningId( part.getId().toString() );
						
						res.setStockInCount(part.getWriteLines());
						res.setFailCount(part.getErrorLines());
						res.setInsertCount(part.getOutputLines());
						res.setUpdateCount(part.getUpdateLines());
						
						outLines = part.getInputLines()+":"+part.getOutputLines()+":"+part.getReadLines()+":"+part.getWriteLines()+":"+part.getUpdateLines()+":"+part.getErrorLines()+":0" ;
						
					}else if (result != null) {
						res.setStockInCount(result.getNrLinesWritten());
						res.setFailCount(result.getNrLinesRejected());
						res.setInsertCount(result.getNrLinesOutput());
						res.setUpdateCount(result.getNrLinesUpdated());
						
						outLines = result.getNrLinesInput()+":"+result.getNrLinesOutput()+":"+result.getNrLinesRead()+":"+result.getNrLinesWritten()+":"+result.getNrLinesUpdated()+":"+result.getNrLinesRejected()+":"+result.getNrErrors();
						res.setErrorMessage(outLines);
					}

					if ( result != null && result.getResultFilesList().size() > 0) {
						// 运行结束并且有文件信息
						List<ResultFile> fileObjs = result.getResultFilesList();
						List<String> successFileNameList = fileObjs.stream().map(fo -> {
							return fo.getFile().getName().getBaseName();
						}).distinct().collect(Collectors.toList());
						res.setSuccessFileNameList(successFileNameList);
					}
					if(!pushDataToRemote(res)) {
						savePushFailData( getKey(execId, params), res);
					}
				} catch ( Exception e) {
					CloudLogger.getInstance(owner).error("SubcribePushService","推送任务状态失败:",e);
				}
			}
		};

		return statusListener;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public  void retryPush()  {
		
		Map<Object, Object> cache ;
		boolean isRedis =  RedisUtil.isCacheEnable();
		if ( !isRedis ) {
			//缓存不可用
			CacheManager cacheManager = (CacheManager) PluginFactory.getBean("cacheManager");
			cache = cacheManager.getCache("sampleCache").get(PUSH_FAIL_KEY,Map.class);
		}else {
			cache= RedisUtil.hmget(PUSH_FAIL_KEY);
		}
		if( cache != null && cache.size() >0 ) {
			logger.info("准备重新推送 订阅推送失败的信息,数量:"+cache.size());
			Iterator<Entry<Object, Object>> cacheit = cache.entrySet().iterator();
			while( cacheit.hasNext() ) {
				Entry<Object, Object> entry = cacheit.next();
				String key = (String) entry.getKey() ;
				ETLTaskResultDto res = (ETLTaskResultDto) entry.getValue();
				
				logger.info("重新推送订阅任务["+res.getUserId()+":"+ res.getSubscribeId() + "],执行id["+ key +"],状态[" + res.getResult() + "],输出条数[" + res.getErrorMessage() + "],文件数[" + (res.getSuccessFileNameList()!= null ? res.getSuccessFileNameList().size():"0") + "]");
				if(pushDataToRemote(res)) {
					removePushFailData(key); //从缓存中删除
					logger.info("重新推送成功,订阅任务[" + res.getSubscribeId() + "],执行id["+ key +"]");
				}else {
					logger.info("重新推送失败,订阅任务[" + res.getSubscribeId() + "],执行id["+ key +"]");
				}
			}
		}
	}
	
	
	private boolean pushDataToRemote(ETLTaskResultDto res)  {
		
		String owner = Const.NVL( CloudSession.getResourceUser(), res.getUserId());
		String jobName =  res.getSubscribeId();
		String execId =  res.getExecId();
		
		CloudLogger.getInstance(owner).addNumber().info(this, "订阅推送任务[" +owner+":"+ jobName + "],执行id["+ execId +"],状态[" + res.getResult() + "],输出条数[" + res.getErrorMessage() + "],文件数[" + (res.getSuccessFileNameList()!= null ? res.getSuccessFileNameList().size():"0") + "]");
		
		StatusFeedbackDto sfdto =null ;
		if (taskService != null) {
			try {
				sfdto = taskService.updateETLTaskProcessResults(res);
			}catch(Exception e) {
				CloudLogger.getInstance(owner).error(this, "订阅推送失败,任务["+owner+":"+ jobName + "],执行id["+ execId +"],异常["+CloudLogger.getExceptionMessage(e)+"]");
				return false;
			}
			
			if(sfdto != null && sfdto.getStatusCode() != null && sfdto.getStatusCode() == 200) {
				CloudLogger.getInstance(owner).info(this,  "推送成功[200]",res);
				return true;
			}else if( sfdto != null && sfdto.getStatusCode() != null && sfdto.getStatusCode() == 309 ) {
				//远程删除,本地停止
				CloudLogger.getInstance(owner).info(this,  "推送成功[309]",res);
				 ExecutionInfo executionInfo = CloudExecution.getInstance().getExecutionInfo(owner, jobName, true);
				if (executionInfo != null &&  execId.equals(executionInfo.executionId)) {
					//当前的数据是最新的
					CloudJobExecutor jobExecutor = CloudExecution.getInstance().getJobExecutor(executionInfo.executionId);
					try {
						if ( !jobExecutor.isFinished() || CloudExecutorStatus.assertRunning(jobExecutor.getStatus())) {
							//还在运行,停止它
							jobExecutor.flushExtraLog("[ERROR]订阅任务推送状态返回309(任务已经不存在),停止执行任务...",true);
							jobExecutor.execStop();
						}
					} catch (Exception e) {
						//停止异常,忽略
					}
				}
				return true;
			}
		}
		if(sfdto != null ) {
			CloudLogger.getInstance(owner).error(this,  CloudLogger.logMessage2("订阅推送失败,任务["+owner+":" + jobName + "],执行id["+ execId+"],返回结果["+sfdto.getStatusCode()+","+sfdto.getErrMsg()+"]",sfdto));
		}else {
			CloudLogger.getInstance(owner).error(this,  "订阅推送失败,任务["+owner+":" + jobName + "],执行id["+ execId+"],推送服务未找到,请检查dubbo服务是否开启提供者.");
		}
		return false;
	}
	
	
	private String getKey(String execId ,  Object params) {
		if( params == null ) {
			return execId ;
		}else if( params != null && params instanceof SegmentingPartInfo) {
				SegmentingPartInfo part = (SegmentingPartInfo)params;
				return execId+":"+part.getId() ;
		}else if( params != null && params instanceof String) {
			return execId+":"+params ;
		}
		return "Unknow";
	}
	
	
	@SuppressWarnings("unchecked")
	public void savePushFailData(String key , ETLTaskResultDto res) {
		
		boolean isRedis =  RedisUtil.isCacheEnable();
		if ( !isRedis ) {
			//缓存不可用
			CacheManager cacheManager = (CacheManager) PluginFactory.getBean("cacheManager");
			Map<Object, Object> map = cacheManager.getCache("sampleCache").get(PUSH_FAIL_KEY,Map.class);
			if( map == null ) {
				map = Maps.newHashMap();
			}
			map.put(key, res);
			cacheManager.getCache("sampleCache").put(PUSH_FAIL_KEY, map);
		}else {
			RedisUtil.hset(PUSH_FAIL_KEY, key, res);
		}
	}
	
	
	@SuppressWarnings("unchecked")
	public ETLTaskResultDto getPushFailData(String  key ) {
		
		boolean isRedis =  RedisUtil.isCacheEnable();
		if ( !isRedis ) {
			//缓存不可用
			CacheManager cacheManager = (CacheManager) PluginFactory.getBean("cacheManager");
			Map<Object, Object> cache = cacheManager.getCache("sampleCache").get(PUSH_FAIL_KEY,Map.class);
			if( cache != null && cache.containsKey(key)) {
				return (ETLTaskResultDto) cache.get(key);
			}
		}else {
			return  (ETLTaskResultDto) RedisUtil.hget(PUSH_FAIL_KEY,key);
		}
		
		return  null;
	}
	
	@SuppressWarnings("unchecked")
	public void removePushFailData( String  key ) {
		
		boolean isRedis =  RedisUtil.isCacheEnable();
		if ( !isRedis ) {
			//缓存不可用
			CacheManager cacheManager = (CacheManager) PluginFactory.getBean("cacheManager");
			Map<Object, Object> cache = cacheManager.getCache("sampleCache").get(PUSH_FAIL_KEY,Map.class);
			if( cache != null && cache.containsKey(key)) {
				cache.remove(key);
				cacheManager.getCache("sampleCache").put(PUSH_FAIL_KEY,cache);
			}
		}else {
			RedisUtil.hdel(PUSH_FAIL_KEY,key);
		}
	}

}
