/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.ext.executor;

import java.io.IOException;
import java.net.ConnectException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.di.ExecutionConfiguration;
import org.pentaho.di.base.AbstractMeta;
import org.pentaho.di.cluster.SlaveServer;
import org.pentaho.di.core.Result;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.SegmentingPartInfo;
import org.pentaho.di.core.util.IdatrixPropertyUtil;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.repository.RepositoryObjectType;

import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.ys.idatrix.cloudetl.dto.engine.ExecConfigurationDto;
import com.ys.idatrix.cloudetl.dto.exec4w.Exec4WExceptionDto;
import com.ys.idatrix.cloudetl.dto.history.ExecHistoryRecordDto;
import com.ys.idatrix.cloudetl.dto.history.ExecHistorySegmentingPartDto;
import com.ys.idatrix.cloudetl.ext.CloudApp;
import com.ys.idatrix.cloudetl.ext.CloudSession;
import com.ys.idatrix.cloudetl.ext.executor.exception.Exec4WExceptionHandler;
import com.ys.idatrix.cloudetl.ext.executor.logger.CloudExecHistory;
import com.ys.idatrix.cloudetl.ext.executor.logger.CloudExecLog;
import com.ys.idatrix.cloudetl.logger.CloudLogConst;
import com.ys.idatrix.cloudetl.logger.CloudLogType;
import com.ys.idatrix.cloudetl.logger.CloudLogger;
import com.ys.idatrix.cloudetl.monitor.CloudMetrics;
import com.ys.idatrix.cloudetl.repository.CloudRepository;
import com.ys.idatrix.cloudetl.repository.database.CloudDatabaseRepository;

/**
 * Common procedures for execution of transformation and jobs.
 * @author JW
 * @since 2017年7月12日
 *
 */
public abstract class BaseExecutor implements Runnable {
	
	public static final Log  logger = LogFactory.getLog("BaseExecutor");

	// The unique ID assigned to each execution
	protected final String executionId;
	protected final String taskName;
	protected final String renterId;
	protected final String owner;
	protected final String execUser;
	
	protected String carteObjectId = null;

	protected transient AbstractMeta metaClone = null;
	protected String taskCloneName ;
	
	protected Date beginDate;
	protected Date endDate;

	protected final ExecutionConfiguration executionConfiguration ;

	// History recorder
	protected CloudExecHistory execHistory;
	protected ExecHistoryRecordDto execRecord;
	protected Exec4WExceptionDto exec4WException;

	// Executor logger
	protected CloudExecLog execLog;
	protected Queue<String> extraLog;
	protected String lastExtraLogText;
	
	// Execution status for transformation in current executor
	// TODO. need to be improved with status controlling machine
	protected String status = CloudExecutorStatus.WAITING.getType();

	protected boolean finished = false;
	protected boolean active = false;
	protected boolean waiting = true;
	protected Result result ;
	
	
	protected BaseExecutor(AbstractMeta meta, ExecutionConfiguration configuration) {
		this(meta, configuration,CloudSession.getLoginUser(), CloudSession.getResourceUser() );
	}

	protected BaseExecutor(AbstractMeta meta, ExecutionConfiguration configuration,String execUser,String owner ) {
		
		this.executionId = Utils.isEmpty( meta.getVariable("idatrix.executionId") ) ?  UUID.randomUUID().toString().replaceAll("-", "") : meta.getVariable("idatrix.executionId");
		this.executionConfiguration = configuration;
		
		this.renterId =  CloudSession.getLoginRenterId();
		this.execUser =  Utils.isEmpty(execUser) ? CloudSession.getLoginUser() : execUser;
		this.owner =  Utils.isEmpty(owner) ? CloudSession.getResourceUser() : owner;
		this.taskName = meta.getName();
		this.taskCloneName =  taskName + Utils.getThreadNameesSuffixByUser(execUser, owner, false) ;
		
		//this.metaClone = (AbstractMeta) meta.clone();
		this.metaClone =  meta;
		metaClone.setName(taskCloneName);
		metaClone.setVariable("idatrix.taskName", taskName);
		metaClone.setVariable("executionId", executionId);
		metaClone.setVariable("idatrix.executionId", executionId);
		metaClone.setVariable("userId", execUser );
		metaClone.setVariable("idatrix.execUser", execUser );
		metaClone.setVariable("idatrix.owner", owner );

		this.extraLog = Queues.newConcurrentLinkedQueue();
		
		try {
			this.execLog = CloudExecLog.initExecLog( CloudApp.getInstance().getUserLogsRepositoryPath(owner) , taskName,  RepositoryObjectType.JOB.equals(getType())?CloudLogType.JOB_LOG:CloudLogType.TRANS_LOG );
	
			this.execHistory = CloudExecHistory.initExecHistory( owner, taskName, RepositoryObjectType.JOB.equals(getType())?CloudLogType.JOB_HISTORY:CloudLogType.TRANS_HISTORY);
			
			this.execRecord = this.execHistory.getExecRecord(executionId);
			if(this.execRecord ==  null ) {
				this.beginDate = new Date();
				
				this.execRecord = new ExecHistoryRecordDto(executionId);
				this.execRecord.setRenterId(renterId);
				this.execRecord.setOwner(owner);
				this.execRecord.setName(taskName);
				this.execRecord.setType(RepositoryObjectType.JOB.equals(getType())?CloudLogType.JOB_HISTORY.getType():CloudLogType.TRANS_HISTORY.getType());
				this.execRecord.setOperator(execUser);
				this.execRecord.setConfiguration(getConfigurationDto());
				this.execRecord.setStatus(this.status);
				this.execRecord.setBegin( this.beginDate );
				//this.execRecord.setEnd(DateFormatUtils.format(new Date(), CloudLogConst.EXEC_TIME_PATTERN));
				this.execHistory.addExecRecord(this.execRecord);
				//新的一次开始
				this.execLog.startExecLog(executionId);
				
			}else {
				//重启服务,使用旧的执行id
				this.beginDate = execRecord.getBegin() ;
				this.execLog.startExecLog("重启服务,重新运行...");
			}
			
			status= CloudExecutorStatus.PREPARING.getType();
			updateDatabaseFileInfo(status);
			
		} catch ( Exception e) {
			logger.error("初始化历史记录失败,",e);
		}
	}
	
	
	@Override
	public void run() {
		
		finished = false;
		if(beginDate== null) {
			beginDate = new Date();
		}
		
		try {
			updateDatabaseFileInfo(CloudExecutorStatus.RUNNING.getType());
			
			execTask();
		} catch (Exception e) {
			try {
				flushExecStatusAndLog(CloudExecutorStatus.FAILED, "执行异常:" + CloudLogger.getExceptionMessage(e, false));
				flushExtraLog("执行异常:" + CloudLogger.getExceptionMessage(e, false), false);
			} catch (Exception e1) {
			}
			logger.error("执行失败！", e);
		}finally {
			
			finished = true;
			endDate = new Date();
			this.execLog.endExecLog(executionId);
			
			updateDatabaseFileInfo(status);
			
			CloudMetrics.elkServerTaskReport(taskName, MapUtils.putAll(Maps.newHashMap(), new Object[] {"taskName",taskName,"userName",execUser,"taskType",getType().getTypeDescription(),"executionId",executionId,"startTime",DateFormatUtils.format(beginDate, CloudLogConst.EXEC_TIME_PATTERN),"endTime",DateFormatUtils.format(endDate, CloudLogConst.EXEC_TIME_PATTERN),"status",status}) );
			
		}
	}
	
	
	protected void saveCloudLog(String cloudLog, List<SegmentingPartInfo> parts) throws Exception {

		flushExecLog(cloudLog, false);
		if (parts != null && parts.size() > 0) {
			for (SegmentingPartInfo part : parts) {
				if( !Utils.isEmpty(part.getDataOutputSource()) ) {
					execRecord.setOutPutSource(part.getDataOutputSource());
				}
				if( !part.isEnable() ) {
					if( !Utils.isEmpty( part.getExceptionPosition()) &&  exec4WException == null ) {
						exec4WException = new Exec4WExceptionDto();
						exec4WException.setOwner(owner);
						exec4WException.setExecId(executionId);
						exec4WException.setExecSource(getType().getTypeDescription()+"["+taskName+"]");
						exec4WException.setInputSource(part.getDataInputSource());
						exec4WException.setOutputSource(part.getDataOutputSource());
						exec4WException.setType(part.getExceptionType());
						exec4WException.setName(part.getExceptionName());
						exec4WException.setPosition(part.getExceptionPosition());
						exec4WException.setExceptionDetail(part.getExceptionDetail());
						
						Exec4WExceptionHandler.getInstance().insertExecException(exec4WException);
					}

					continue ;
				}
				// 是否只包含有效运行,即运行输出行数大于0
				execLog.insertPartExecLog(part.getId().toString(), part.getLog());

				ExecHistorySegmentingPartDto segmentingPart = new ExecHistorySegmentingPartDto(part);
				segmentingPart.setOperator(execUser);
				segmentingPart.setRenterId(renterId);
				segmentingPart.setOwner(owner);
				segmentingPart.setExecId(executionId);
				execHistory.addSegmentingPart(segmentingPart);
			}
		}

		if (result != null) {
			execRecord.setReadLines(result.getNrLinesRead());
			execRecord.setWriteLines(result.getNrLinesWritten());
			execRecord.setInputLines(result.getNrLinesInput());
			execRecord.setOutputLines(result.getNrLinesOutput());
			execRecord.setUpdateLines(result.getNrLinesUpdated());
			execRecord.setErrorLines(result.getNrLinesRejected());
			
			execRecord.setSuccessFailTimes(result.getSuccessFailTimes());

			execHistory.updateExecRecord(execRecord);
		}

	}
	
	protected void flushExecLog(String logText,boolean addTime) {
		try {
			execLog.insertExecLog(logText,addTime);
		} catch (KettleException | IOException e) {
			logger.error("保存日志记录失败,",e);
		}
	}

	protected void flushExecStatusAndLog(String status, String logText) {
		try {
			// Flush executor log with current status
			StringBuilder sb = new StringBuilder("Executor status: ");
			sb.append(status);
			sb.append(Utils.isEmpty(logText) ? "" : ", " + logText);
			flushExecLog(sb.toString(),true);
			
			// Set executor status & history record
			setStatus(status);
		
		} catch ( Exception e) {
			logger.error("保存日志记录和状态失败,",e);
		}
	}

	protected void flushExecStatusAndLog(CloudExecutorStatus status, String logText) {
		flushExecStatusAndLog(status.getType(), logText);
	}

	
	/**
	 * 记录额外的日志,将会被发送到前端显示<br>
	 * 可以 增加 本地执行的异常信息,远程执行时增加说明信息等,连续的相同日志将被忽略
	 * 
	 * @param logText
	 *            日志内容
	 * @param isRecord
	 *            是否记录到日志文件
	 * @throws Exception
	 */
	public void flushExtraLog(String logText, boolean isRecord) throws Exception {
		if (Utils.isEmpty(logText) || (logText).equalsIgnoreCase(lastExtraLogText)) {
			// 为空 或者 连续两条相同的日志 ,忽略
			return;
		}
		lastExtraLogText = logText;
		this.extraLog.offer(DateFormatUtils.format(new Date(), CloudLogConst.EXEC_TIME_PATTERN1) + "  " + logText + "\n");
		if (isRecord) {
			flushExecLog(logText, true);
		}
	}
	
	public ExecutionConfiguration getExecutionConfiguration() {
		return executionConfiguration;
	}

	public String getExecutionId() {
		return executionId;
	}

	public Date getBeginDate() {
		return beginDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public String getStatus() {
		return this.status;
	}

	public String getTaskName() {
		return taskName;
	}

	public Result getResult() {
		return this.result;
	}
	
	public String getUserString() {
		if( !Utils.isEmpty(owner)&& !execUser.equalsIgnoreCase(owner) ) {
			return execUser+"["+owner+"]" ;
		}
		return execUser;
	}
	
	public String getExecUser() {
		return execUser;
	}

	public String getOwner() {
		return owner;
	}

	public void setStatus(String status) throws  Exception {
		this.status = status;
		// Upgrade executor history record
		this.execRecord.setStatus(status);
		if (!CloudExecutorStatus.assertRunning(status)) {
			this.execRecord.setEnd( new Date() );
			
			if (result != null) {
				execRecord.setReadLines(result.getNrLinesRead());
				execRecord.setWriteLines(result.getNrLinesWritten());
				execRecord.setInputLines(result.getNrLinesInput());
				execRecord.setOutputLines(result.getNrLinesOutput());
				execRecord.setUpdateLines(result.getNrLinesUpdated());
				execRecord.setErrorLines(result.getNrLinesRejected());
				
				execRecord.setSuccessFailTimes(result.getSuccessFailTimes());
			}
		}
		
		this.execHistory.updateExecRecord(this.execRecord);
	}

	public boolean isFinished() {
		return finished;
	}

	public void setFinished(boolean finished) {
		this.finished = finished;
		this.endDate = new Date();
	}

	private void updateDatabaseFileInfo(String status) {
		try {
			if (CloudRepository.initDatebaseRepository()) {
				if (getType().equals(RepositoryObjectType.JOB)) {
					CloudDatabaseRepository.getInstance().jobResosi.updateExecInfo(owner, taskName, beginDate, status);
				} else {
					CloudDatabaseRepository.getInstance().transResosi.updateExecInfo(owner, taskName, beginDate,status);
				}
			}
		} catch (Exception e) {
		}
	}
	
	
	public ExecConfigurationDto getConfigurationDto() {
		
		Map<String, String> execParams = executionConfiguration.getParams() ;
		
		ExecConfigurationDto configurationDto = new ExecConfigurationDto();
		configurationDto.setEngineName( execParams.get("engineName"));
		configurationDto.setEngineType( execParams.get("engineType"));
		configurationDto.setClearingLog(executionConfiguration.isClearingLog());
		configurationDto.setGatherMetrics(executionConfiguration.isGatheringMetrics());
		configurationDto.setLogLevel(executionConfiguration.getLogLevel().getCode());
		configurationDto.setSafeMode(executionConfiguration.isSafeModeEnabled());

		configurationDto.setRebootAutoRun(Boolean.valueOf((String)execParams.get("rebootAutoRun")));
		configurationDto.setParams(execParams);
		configurationDto.setVariables(executionConfiguration.getVariables());

		return configurationDto;
	}
	
	/**
	 * 远程服务连接失败,重试5分钟,等待远程服务恢复
	 * 
	 * @return
	 * @throws Exception
	 */
	protected boolean waitForRemoteRestart() throws Exception {

		if (executionConfiguration.isExecutingRemotely()) {

			SlaveServer remoteSlaveServer = executionConfiguration.getRemoteServer();
			if (remoteSlaveServer != null) {
				Integer waitTime = Integer.valueOf( IdatrixPropertyUtil.getProperty("idatrix.exec.remote.server.wait.retry.minute", "5")) * 60;
				for (int usedTime = 0; usedTime < waitTime; usedTime = usedTime + 30) {

					if (!CloudExecutorStatus.assertRunning(getStatus())) {
						// 触发了停止操作 ,不再重试等待 ,任务停止
						return false;
					}

					try {
						// 获取远程服务状态,不抛异常 则连接成功
						remoteSlaveServer.getStatus();
					} catch (ConnectException e) {
						if (usedTime % 60 == 0) {
							flushExtraLog("远程服务[" + remoteSlaveServer.getName() + "]第[" + (usedTime / 60 + 1) + "]次尝试连接失败...",true);
						}
						// 连接失败,休眠30秒
						Thread.sleep(30 * 1000);
						// 再次尝试
						continue;
					} catch (Exception e) {
						// 成功
					}
					// 成功
					flushExtraLog("远程服务[" + remoteSlaveServer.getName() + "]第[" + ((usedTime + 30) / 60 + 1)+ "]次尝试连接成功,重新启动...", true);
					//重新启动任务
					restartServer();
					
					return true;
				}
			}
		}

		return false;
	}

	
	public abstract void execTask() ;

	public abstract void clear() throws Exception;

	public abstract RepositoryObjectType getType()   ;
	
	public abstract void restartServer() throws Exception;

}
