package com.ys.idatrix.cloudetl.recovery.restart;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.util.IdatrixPropertyUtil;
import org.pentaho.di.core.util.Utils;
import com.ys.idatrix.cloudetl.dto.common.ReturnCodeDto;
import com.ys.idatrix.cloudetl.dto.history.ExecHistoryRecordDto;
import com.ys.idatrix.cloudetl.dto.job.JobExecRequestDto;
import com.ys.idatrix.cloudetl.dto.trans.TransExecRequestNewDto;
import com.ys.idatrix.cloudetl.ext.CloudSession;
import com.ys.idatrix.cloudetl.ext.PluginFactory;
import com.ys.idatrix.cloudetl.ext.executor.CloudExecution;
import com.ys.idatrix.cloudetl.ext.executor.CloudExecution.ExecutionInfo;
import com.ys.idatrix.cloudetl.ext.executor.CloudExecutorStatus;
import com.ys.idatrix.cloudetl.ext.executor.logger.CloudExecHistory;
import com.ys.idatrix.cloudetl.ext.utils.RedisUtil;
import com.ys.idatrix.cloudetl.logger.CloudLogType;
import com.ys.idatrix.cloudetl.recovery.trans.ResumeTransParser;
import com.ys.idatrix.cloudetl.recovery.trans.dto.TransInfoDto;
import com.ys.idatrix.cloudetl.repository.CloudRepository;
import com.ys.idatrix.cloudetl.repository.database.CloudDatabaseRepository;
import com.ys.idatrix.cloudetl.service.job.CloudJobService;
import com.ys.idatrix.cloudetl.service.trans.CloudTransService;

public class UserTaskRestarter implements Runnable {

	public static final Log  logger = LogFactory.getLog(UserTaskRestarter.class);
	
	private UserLogReposiDto userLog ;
	private String renterId;
	private String owner;
	private CloudTransService cloudTransService;
	private CloudJobService cloudJobService;
	
	private boolean isMove =false ;
	
	public  UserTaskRestarter(UserLogReposiDto userLog ) {
		this.userLog = userLog ;
		owner = userLog.getUser() ;
		renterId = userLog.getRenterId();
		
		if (!Utils.isEmpty(IdatrixPropertyUtil.getProperty("idatrix.metadata.reposity.root.old")) || IdatrixPropertyUtil.getBooleanProperty("idatrix.metadata.reposity.database.reload",false) ) {
			this.isMove  = true;
		}else {
			this.isMove  = false;
		}
		
		cloudTransService = (CloudTransService) PluginFactory.getBean(CloudTransService.class);
		cloudJobService = (CloudJobService) PluginFactory.getBean(CloudJobService.class);
	}
	
	@Override
	public void run() {
		
		try {
			
			//设置默认用户到线程对象中
			CloudSession.setThreadLoginUser(owner);
			CloudSession.setThreadResourceUser(owner);
			if( !Utils.isEmpty(renterId)) {
				CloudSession.setThreadInfo(CloudSession.ATTR_SESSION_RENTER_ID,renterId);
			}
			//检查和启动trans
			List<String> transList = userLog.getTransList() ;
			if(transList !=  null && transList.size() >0) {
				//循环trans列表获取每个trans状态
				int restartNum= 0; 
				 for(String transName: transList) {
					 //获取trans 执行历史
					 CloudExecHistory execHistory = CloudExecHistory.initExecHistory( owner, transName, CloudLogType.TRANS_HISTORY);
					 ExecHistoryRecordDto lastRecord = execHistory.getLastExecRecord();
					 if(lastRecord != null && lastRecord.getConfiguration().isRebootAutoRun() &&  CloudExecutorStatus.assertRunning(lastRecord.getStatus()) ) {
						//如果是 初始化状态 或者 运行状态, 判断当前运行池中是否存在,不存在则重启
						 
						//最后执行者可能是 租户,不是当前拥有者
						String execUser = Const.NVL( lastRecord.getOperator(),owner) ;
						CloudSession.setThreadLoginUser(execUser);
						CloudSession.setThreadResourceUser(owner);
						
						 ExecutionInfo executionInfo = CloudExecution.getInstance().getExecutionInfo(owner, transName, false);
						if (executionInfo != null ) {
							//服务已经存在,不需要重启,继续判断下一个
							continue ;
						}
						if( ResumeTransParser.isResumeEnable() ) {
							//如果缓存可用,判断是否已经在远程运行了
							TransInfoDto transInfo = new TransInfoDto(execUser, lastRecord.getName());
							String serviceId = (String) RedisUtil.hget(ResumeTransParser.getTransKey(transInfo), ResumeTransParser.RemoteResumeServerKey);
							if(!Utils.isEmpty(serviceId) ) {
								Long exceptTmie = Long.valueOf(IdatrixPropertyUtil.getProperty("idatrix.breakpoint.except.time", "300"));
								long lastUpdateTime = (long) RedisUtil.hget(ResumeTransParser.getTransKey(transInfo), ResumeTransParser.TransUpdateKey);
								if ( (lastUpdateTime + (exceptTmie * 1000)) >= System.currentTimeMillis()) {
									logger.info(" transName:"+lastRecord.getName()+" [user:"+execUser+"] 正在远程["+serviceId+"]上运行...");
									continue ;
								}
							}
									 
						}
						
						//服务不存在 并且日志记录正在运行,重启
						logger.info("transName:"+lastRecord.getName()+" [user:"+execUser+"] [engineName:"+lastRecord.getConfiguration().getEngineName()+"] 重新启动中... ");
						TransExecRequestNewDto execRequest =  new TransExecRequestNewDto();
						execRequest.setConfiguration(lastRecord.getConfiguration());
						execRequest.setName(lastRecord.getName());
						execRequest.setExecId(lastRecord.getExecId());
						
						ReturnCodeDto result = cloudTransService.execTransNew(execRequest,execUser);
						if(result != null && !result.isSuccess()) {
							logger.info("JobName:"+lastRecord.getName()+" [user:"+execUser+"] [engineName:"+lastRecord.getConfiguration().getEngineName()+"] 重新启动失败:"+result.getMsg());
						}
						restartNum++;
						
						//清理线程用户信息
						CloudSession.clearThreadInfo();
					 }else if( isMove &&  CloudRepository.initDatebaseRepository()  ) {
						 CloudDatabaseRepository.getInstance().transResosi.loadByName(owner, transName, null);
						 if( lastRecord != null ) {
							 CloudDatabaseRepository.getInstance().transResosi.updateExecInfo(owner, transName, lastRecord.getBegin(), lastRecord.getStatus());
						 }
					 }
				 }
				 if(restartNum >0) {
					 logger.info("拥有者["+owner+"]的[转换]重新运行(重启恢复)完成.数量["+restartNum+"]");
				 }
				 
			}
			//检查和启动job
			 List<String> jobList = userLog.getJobList() ;
				if(jobList !=  null && jobList.size() >0) {
					//循环trans列表获取每个trans状态
					int restartNum= 0; 
					 for(String jobName: jobList) {
						 //获取trans 执行历史
						 CloudExecHistory execHistory = CloudExecHistory.initExecHistory(owner, jobName, CloudLogType.JOB_HISTORY );
						 ExecHistoryRecordDto lastRecord = execHistory.getLastExecRecord();
						 if(lastRecord != null && lastRecord.getConfiguration().isRebootAutoRun()  &&  CloudExecutorStatus.assertRunning(lastRecord.getStatus()) ) {
							//如果是 初始化状态 或者 运行状态, 判断当前运行池中是否存在,不存在则重启
							//最后执行者可能是 租户,不是当前拥有者
							String execUser = Const.NVL( lastRecord.getOperator(),owner) ;
							CloudSession.setThreadLoginUser(execUser);
							CloudSession.setThreadResourceUser(owner);
								
							 ExecutionInfo executionInfo = CloudExecution.getInstance().getExecutionInfo( owner , jobName, true);
							if (executionInfo != null) {
								//服务已经存在,不需要重启,继续判断下一个
								continue ;
							}else {
								//服务不存在 并且日志记录正在运行,重启
								logger.info("JobName:"+lastRecord.getName()+" [user:"+execUser+"] [engineName:"+lastRecord.getConfiguration().getEngineName()+"] 重新启动中... ");
								JobExecRequestDto execRequest = new JobExecRequestDto();
								execRequest.setName(lastRecord.getName());
								execRequest.setExecId(lastRecord.getExecId());
								execRequest.setConfiguration(lastRecord.getConfiguration());
								
								ReturnCodeDto result = cloudJobService.execJob(execRequest ,execUser);
								if(result != null && !result.isSuccess()) {
									logger.info("JobName:"+lastRecord.getName()+" [user:"+execUser+"] [engineName:"+lastRecord.getConfiguration().getEngineName()+"] 重新启动失败:"+result.getMessage());
								}
								restartNum++ ;
							}
							
							//清理线程用户信息
							CloudSession.clearThreadInfo();
						 }else if( isMove && CloudRepository.initDatebaseRepository() ) {
							 CloudDatabaseRepository.getInstance().jobResosi.loadByName(owner, jobName, null);
							 if( lastRecord != null ) {
								 CloudDatabaseRepository.getInstance().jobResosi.updateExecInfo(owner, jobName, lastRecord.getBegin(), lastRecord.getStatus());
							 }
						 }
					 }
					 if(restartNum > 0) {
						 logger.info("拥有者["+owner+"]的[调度]重新运行(重启恢复)完成.数量["+restartNum+"]");
					 }
				}
			
		} catch (Exception e) {
			logger.error("重新运行(重启恢复)任务失败.", e);
		}finally {
			//清理线程用户信息
			CloudSession.clearThreadInfo();
		}
	}

}
