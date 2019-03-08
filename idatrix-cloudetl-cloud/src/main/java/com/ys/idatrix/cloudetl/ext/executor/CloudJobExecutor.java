/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.ext.executor;

import java.net.ConnectException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.pentaho.di.cluster.SlaveServer;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.Result;
import org.pentaho.di.core.ResultFile;
import org.pentaho.di.core.RowMetaAndData;
import org.pentaho.di.core.gui.JobTracker;
import org.pentaho.di.core.logging.KettleLogLayout;
import org.pentaho.di.core.logging.KettleLogStore;
import org.pentaho.di.core.logging.KettleLoggingEvent;
import org.pentaho.di.core.logging.LoggingRegistry;
import org.pentaho.di.core.parameters.DuplicateParamException;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.job.Job;
import org.pentaho.di.job.JobEntryListener;
import org.pentaho.di.job.JobEntryResult;
import org.pentaho.di.job.JobExecutionConfiguration;
import org.pentaho.di.job.JobListener;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.job.entry.JobEntryCopy;
import org.pentaho.di.repository.RepositoryObjectType;
import org.pentaho.di.www.SlaveServerJobStatus;
import org.pentaho.di.www.WebResult;

import com.ys.idatrix.cloudetl.dto.job.JobExecEntryMeasureDto;
import com.ys.idatrix.cloudetl.dto.job.JobExecEntryStatusDto;
import com.ys.idatrix.cloudetl.dto.job.JobExecLogDto;
import com.ys.idatrix.cloudetl.ext.CloudApp;
import com.ys.idatrix.cloudetl.ext.CloudSession;
import com.ys.idatrix.cloudetl.ext.utils.StringEscapeHelper;
import com.ys.idatrix.cloudetl.logger.CloudLogConst;
import com.ys.idatrix.cloudetl.logger.CloudLogger;
import com.ys.idatrix.cloudetl.repository.CloudRepository;
import com.ys.idatrix.cloudetl.subcribe.SubcribePushService;

/**
 * Job execution implementation, - Keeping runnable environment, - Implement
 * org.pentaho.di.ui.spoon.delegates.SpoonJobDelegate.executeJob()
 * org.pentaho.di.ui.spoon.job.JobGraph
 * 
 * @author JW
 * @since 2017年5月24日
 *
 */
public class CloudJobExecutor extends BaseExecutor  implements Runnable {

	public static synchronized CloudJobExecutor initExecutor(JobExecutionConfiguration jobExecutionConfiguration, JobMeta jobMeta, String execUser,String owner) throws Exception {
		CloudJobExecutor jobExecutor = new CloudJobExecutor(jobMeta, jobExecutionConfiguration,  execUser,owner);
		CloudExecution.getInstance().putExecutor(jobExecutor);
		return jobExecutor;
	}
	
	private Job job = null;

	@Override
	public RepositoryObjectType getType()   {
		return RepositoryObjectType.JOB;
	}
	

	private CloudJobExecutor(JobMeta jobMeta, JobExecutionConfiguration jobExecutionConfiguration, String execUser,String owner) {
		
		super(jobMeta, jobExecutionConfiguration, execUser, owner);
	}
	
	
	public JobExecutionConfiguration getExecutionConfiguration() {
		return (JobExecutionConfiguration)executionConfiguration;
	}

	@Override
	public void clear() throws Exception {
		waiting = false ;
		if (executionConfiguration.isExecutingLocally() && job != null) {
			KettleLogStore.discardLines(job.getLogChannelId(), true);
		} else if (executionConfiguration.isExecutingRemotely()) {
			try {
				SlaveServer remoteSlaveServer = executionConfiguration.getRemoteServer();
				remoteSlaveServer.removeJob(taskCloneName, carteObjectId);
			} catch (Exception e) {
				// 如果不存在 ,会报异常,忽略
			}
		}

		clearJobMeta();

		job = null;
		carteObjectId = null;
		result = null;

	}

	private void clearJobMeta() {

		if (metaClone != null) {
			metaClone.disposeEmbeddedMetastoreProvider();
			metaClone.clear();
		}
		metaClone = null;
	}

	/**
	 * ExecutingLocally :
	 * org.pentaho.di.ui.spoon.job.JobGraph.startJob(JobExecutionConfiguration)
	 * ExecutingRemotely :
	 * org.pentaho.di.ui.spoon.delegates.SpoonJobDelegate.executeJob(JobMeta,
	 * boolean, boolean, Date, boolean, String, int)
	 */
	@Override
	public void execTask() {
		try {

			// 是否订阅推送任务状态
			sleepForPush();

			JobExecutionConfiguration jobExecutionConfiguration = (JobExecutionConfiguration)executionConfiguration ;
			
			flushExecStatusAndLog(CloudExecutorStatus.INITIALIZING, "程序开始运行...");

			metaClone.setLogLevel(jobExecutionConfiguration.getLogLevel());

			// Activate the parameters, turn them into variables...
			metaClone.activateParameters();

			active = true;
			
			if (jobExecutionConfiguration.isExecutingLocally()) {
				if (job == null || (job != null && !job.isActive())) {
					// Make sure we clear the log before executing again...
					//
					if (jobExecutionConfiguration.isClearingLog()) {
						// ignore
					}

					job = new Job(CloudApp.getInstance().getRepository(), (JobMeta)metaClone, null);
					job.setExecutingUser(CloudSession.getResourceUser());

					job.setLogLevel(jobExecutionConfiguration.getLogLevel());
					job.shareVariablesWith(metaClone);
					job.setInteractive(true);
					job.setGatheringMetrics(jobExecutionConfiguration.isGatheringMetrics());
					job.setArguments(jobExecutionConfiguration.getArgumentStrings());

					// Pass specific extension points...
					job.getExtensionDataMap().putAll(jobExecutionConfiguration.getExtensionOptions());

					// If there is an alternative start job entry, pass it to the job
					if (!Utils.isEmpty(jobExecutionConfiguration.getStartCopyName())) {
						JobEntryCopy startJobEntryCopy = ((JobMeta)metaClone).findJobEntry(
								jobExecutionConfiguration.getStartCopyName(), jobExecutionConfiguration.getStartCopyNr(),
								false);
						job.setStartJobEntryCopy(startJobEntryCopy);
					}

					// Set the named parameters
					Map<String, String> paramMap = jobExecutionConfiguration.getParams();
					Set<String> keys = paramMap.keySet();
					for (String key : keys) {
						try {
							job.getJobMeta().addParameterDefinition(key, Const.NVL(paramMap.get(key), ""), "");
						} catch (DuplicateParamException e) {
							job.getJobMeta().setParameterValue(key, Const.NVL(paramMap.get(key), ""));
						}
					}
					job.getJobMeta().activateParameters();

					job.start();

					flushExecStatusAndLog(CloudExecutorStatus.RUNNING, "程序正在运行...");

					do {
						// Here should not loop thread sleeping !
						Thread.sleep(1000);
						this.result = job.getResult();
						// 执行日志从监听器中自动实时获取
						saveCloudLog(job.getCloudLog(), job.getCloudPartLog());

					} while (( !job.isInitialized() || !job.isFinished() ) && waiting);
					waiting = false;
					// flushExecLog(getExecLogTextGeneral(fromNr)); //执行日志从监听器中自动实时获取
					// 获取可能残存的剩余日志
					saveCloudLog(job.getCloudLog(), job.getCloudPartLog());
					// 复制结果对象
					this.result = job.getResult();
					// flushExecStatusAndLog(CloudExecutorStatus.COMPLETED, "");
					flushExecStatusAndLog(CloudExecutorStatus.correctStatusType(job.getStatus()), "程序停止运行.");
				} else {
					flushExecStatusAndLog(CloudExecutorStatus.FAILED, "正在运行中,不要重复启动!！");
					logger.error("正在执行,不要重复启动!！");
				}
			} else if (jobExecutionConfiguration.isExecutingRemotely()) {

				SlaveServer remoteSlaveServer = jobExecutionConfiguration.getRemoteServer();
				if (remoteSlaveServer != null) {
					jobExecutionConfiguration.setPassingExport(true);
					try {
						SlaveServerJobStatus oldstatus = remoteSlaveServer.getJobStatus(taskCloneName, null,Integer.MAX_VALUE);
						if (oldstatus != null && !Utils.isEmpty(oldstatus.getStatusDescription())&& oldstatus.isRunning()) {
							// 远程有当前任务正在运行
							carteObjectId = oldstatus.getId();
						} else {
							if (oldstatus != null && !Utils.isEmpty(oldstatus.getStatusDescription())&& !oldstatus.isRunning()) {
								// 远程有当前任务 但是没有运行,删除任务
								remoteSlaveServer.removeJob(taskCloneName, oldstatus.getId());
							}
							carteObjectId = Job.sendToSlaveServer((JobMeta)metaClone, jobExecutionConfiguration,CloudApp.getInstance().getRepository(), CloudApp.getInstance().getMetaStore(owner));
						}
						
						flushExecStatusAndLog(CloudExecutorStatus.RUNNING, "程序正在运行...");
					} catch (ConnectException e) {
						flushExtraLog("远程服务[" + remoteSlaveServer.getName() + "]连接失败,尝试等待服务重启(重启调度)", true);
						// 服务器停止,等待5分钟
						if (!waitForRemoteRestart()) {
							throw e ;
						}
					}

					// 清除jobMeta对象,不再会使用,释放对象空间.
					clearJobMeta();

					SlaveServerJobStatus jobStatus = null;
					do {
						// Here should not loop thread sleeping.
						Thread.sleep(1000);
						try {
							jobStatus = remoteSlaveServer.getJobStatus(taskCloneName, carteObjectId, -1);
						} catch (ConnectException e) {
							flushExtraLog("远程服务[" + remoteSlaveServer.getName() + "]连接失败,尝试等待服务重启(重启调度)", true);
							jobStatus =  null; //需要置空,否则会造成日志重复保存
							// 服务器停止,等待5分钟
							if (!waitForRemoteRestart()) {
								waiting = false;
								break;
							}
							
						}
						if (jobStatus != null) {
							waiting = jobStatus.isRunning();
							this.result = jobStatus.getResult();
							// 执行日志从监听器中自动实时获取
							saveCloudLog(jobStatus.getCloudLog(), jobStatus.getPartLog());
						}

					} while (waiting );

					try {
						// 获取可能残存的剩余日志
						jobStatus = remoteSlaveServer.getJobStatus(taskCloneName, carteObjectId, -1);
						saveCloudLog(jobStatus.getCloudLog(), jobStatus.getPartLog());
						// 复制结果对象
						this.result = jobStatus.getResult();
						flushExecStatusAndLog(jobStatus == null ? CloudExecutorStatus.UNKNOWN: CloudExecutorStatus.getStatusForType(jobStatus.getStatusDescription()),"程序停止运行.");

					} catch (ConnectException e) {
						// 服务器停止
						flushExecStatusAndLog(CloudExecutorStatus.FAILED,"远程服务器[" + remoteSlaveServer.getName() + "]连接失败,运行结束.");
					}

				} else {
					flushExecStatusAndLog(CloudExecutorStatus.FAILED, "Remote Server not found!");
					logger.error("Remote Server not found!");
				}
			} else {
				// Not executed.
				flushExecStatusAndLog(CloudExecutorStatus.WAITING,
						"Error execution configuration, job has been not executed!");
			}

			flushExtraLog("执行结束.", false);

		} catch (Exception e) {
			try {
				flushExecStatusAndLog(CloudExecutorStatus.FAILED, "执行异常:" + CloudLogger.getExceptionMessage(e, false));
				flushExtraLog("执行异常:" + CloudLogger.getExceptionMessage(e, false), false);
			} catch (Exception e1) {
			}
			logger.error("执行失败！", e);
			job = null;
		} finally {
			// Cleanup job have finished
			if (job != null) {
				List<JobEntryListener> entryListeners = new ArrayList<JobEntryListener>(job.getJobEntryListeners());
				for (JobEntryListener entryListener : entryListeners) {
					job.removeJobEntryListener(entryListener);
				}

				List<JobListener> listeners = job.getJobListeners();
				for (JobListener listener : listeners) {
					job.removeJobListener(listener);
				}
			}
		}
	}

	public boolean execFinished() {
		
		if (job != null && job.isInitialized() && job.isFinished()) {
			return true;
		} else if (executionConfiguration.isExecutingRemotely() && active) {

			SlaveServer remoteSlaveServer = executionConfiguration.getRemoteServer();
			if (remoteSlaveServer != null) {
				try {
					SlaveServerJobStatus transStatus = remoteSlaveServer.getJobStatus(taskCloneName, carteObjectId,Integer.MAX_VALUE);
					return transStatus == null || Utils.isEmpty(transStatus.getStatusDescription())|| !transStatus.isRunning();
				} catch (Exception e) {
					// 服务器停止,会有一个等待恢复的过程
				}
			} else {
				logger.warn("获取job[" + taskCloneName + "]远程[" + executionConfiguration.getRunConfiguration()+ "]任务运行状态 ,但远程服务(SlaveServer)为空.");
			}
		}
		
		return finished;
	}

	public String execStatus() {
		String s = null;
		if (job != null) {
			s = job.getStatus();
		} else if (executionConfiguration.isExecutingRemotely() && active) {
			SlaveServer remoteSlaveServer = executionConfiguration.getRemoteServer();
			if (remoteSlaveServer != null) {
				try {
					SlaveServerJobStatus transStatus = remoteSlaveServer.getJobStatus(taskCloneName, carteObjectId,
							Integer.MAX_VALUE);
					s = (transStatus != null && !Utils.isEmpty(transStatus.getStatusDescription()))
							? transStatus.getStatusDescription()
							: status;
				} catch (Exception e) {
					// 服务器停止,会有一个等待恢复的过程
				}
			}
		}

		return Const.NVL(s, status);
	}

	public boolean execStop() throws Exception {

		flushExecStatusAndLog(CloudExecutorStatus.HALTING, "触发停止操作...");
		if (job != null && job.isInitialized()) {
			job.stopAll();
			job.waitUntilFinished(5000); // wait until everything is stopped, maximum 5 seconds...
			
			return job.isStopped();
		} else if (executionConfiguration.isExecutingRemotely() && active) {

			SlaveServer remoteSlaveServer = executionConfiguration.getRemoteServer();
			if (remoteSlaveServer != null) {
				try {
					WebResult webr = remoteSlaveServer.stopJob(taskCloneName, carteObjectId);
					return WebResult.STRING_OK.equals(webr.getResult());
				} catch (ConnectException e) {
					// 服务器停止,会有一个等待恢复的过程
					flushExtraLog("远程服务[" + remoteSlaveServer.getName() + "]连接失败,触发停止运行.", false);
					flushExecStatusAndLog(CloudExecutorStatus.STOPPED,"远程服务[" + remoteSlaveServer.getName() + "]连接失败,触发停止运行.");
				}
			}
		} else {
			flushExecStatusAndLog(getStatus(), "Can not stop job!");
		}

		return false;
	}

	/**
	 * ExecutingLocally :
	 * org.pentaho.di.ui.spoon.job.JobGridDelegate.refreshTreeTable()
	 * ExecutingRemotely :
	 * org.pentaho.di.ui.spoon.SpoonSlave.treeItemSelected(TreeItem)
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<JobExecEntryMeasureDto> getStepMeasure() throws Exception {
		List<JobExecEntryMeasureDto> measureList = new ArrayList<>();

		if (executionConfiguration.isExecutingLocally() && job != null) {
			JobTracker jobTracker = job.getJobTracker();
			if (jobTracker != null) {
				jobTracker.getTotalNumberOfItems();
				// Re-populate this...
				String taskName = jobTracker.getJobName();
				if (Utils.isEmpty(taskName)) {
					if (!Utils.isEmpty(jobTracker.getJobFilename())) {
						taskName = jobTracker.getJobFilename();
					} else {
						taskName = "";
					}
				}
				JobExecEntryMeasureDto jeemd = new JobExecEntryMeasureDto(taskName);
				measureList.add(jeemd);

				for (int i = 0; i < jobTracker.nrJobTrackers(); i++) {
					addTrackerToTree(jobTracker.getJobTracker(i), jeemd);
				}

			}
		} else if (executionConfiguration.isExecutingRemotely() && active) {

			SlaveServerJobStatus jobStatus = null;
			SlaveServer remoteSlaveServer = executionConfiguration.getRemoteServer();
			if (remoteSlaveServer != null) {
				try {
					jobStatus = remoteSlaveServer.getJobStatus(taskCloneName, carteObjectId, Integer.MAX_VALUE);
				} catch (ConnectException e) {
					// 服务器停止,会有一个等待恢复的过程
				}
			}
			if (jobStatus != null) {
				Result result = jobStatus.getResult();
				// 当定时任务已经启动,但是还没有到运行时间时获取result为空
				if (result != null) {
					JobExecEntryMeasureDto jeemd = new JobExecEntryMeasureDto(taskName);
					jeemd.setLinesRead(result.getNrLinesRead());
					jeemd.setLinesWritten(result.getNrLinesWritten());
					jeemd.setLinesInput(result.getNrLinesInput());
					jeemd.setLinesOutput(result.getNrLinesOutput());
					jeemd.setLinesUpdated(result.getNrLinesUpdated());
					jeemd.setLinesRejected(result.getNrLinesRejected());
					jeemd.setErrors(result.getNrErrors());
					jeemd.setCarteObjectId(carteObjectId);
					jeemd.setLogDate( new SimpleDateFormat(CloudLogConst.EXEC_TIME_PATTERN).format(jobStatus.getLogDate()));
					
					Long success = 0L;
					Long fail = 0L;
					if( !Utils.isEmpty( result.getSuccessFailTimes() ) ) {
						String[] sfSplit = result.getSuccessFailTimes().split("/");
						success = (sfSplit != null && sfSplit.length > 0 && !Utils.isEmpty( sfSplit[0] )) ? Long.valueOf(sfSplit[0]) : success ;
						fail = (sfSplit != null && sfSplit.length > 1 && !Utils.isEmpty( sfSplit[1] )) ? Long.valueOf(sfSplit[1]) : fail ;
					}
					
					jeemd.setSuccessTimes(success);
					jeemd.setSuccessTimes(fail);
					
					measureList.add(jeemd);
				}
			}
		}

		return measureList;
	}

	private void addTrackerToTree(JobTracker jobTracker, JobExecEntryMeasureDto parent) {
		if (jobTracker != null) {
			// This is a sub-job: display the name at the top of the list...
			JobExecEntryMeasureDto child = new JobExecEntryMeasureDto(jobTracker.getJobName());
			if (jobTracker.nrJobTrackers() > 0) {
				// then populate the sub-job entries ...
				for (int i = 0; i < jobTracker.nrJobTrackers(); i++) {
					addTrackerToTree(jobTracker.getJobTracker(i), child);
				}
			}
			JobEntryResult result = jobTracker.getJobEntryResult();
			if (result != null) {
				String jobEntryName = result.getJobEntryName();
				if (!Utils.isEmpty(jobEntryName)) {
					child.setEntryName(jobEntryName);
					child.setFilename(Const.NVL(result.getJobEntryFilename(), ""));
				}
				String comment = result.getComment();
				if (comment != null) {
					child.setComment(comment);
				}
				Result res = result.getResult();
				if (res != null) {
					child.setResult(res.getResult() ? "Success" : "Failure");
					child.setSuccessTimes(res.getResult() ? 1 : 0);
					child.setFailTimes(res.getResult() ? 0 : 1);
					child.setNr(Long.toString(res.getEntryNr()));

					child.setLinesInput(res.getNrLinesInput());
					child.setLinesOutput(res.getNrLinesOutput());
					child.setLinesRead(res.getNrLinesRead());
					child.setLinesWritten(res.getNrLinesWritten());
					child.setLinesRejected(res.getNrLinesRejected());
					child.setLinesUpdated(res.getNrLinesUpdated());
					child.setErrors(res.getNrErrors());
				}
				String reason = result.getReason();
				if (reason != null) {
					child.setReason(reason);
				}
				Date logDate = result.getLogDate();
				if (logDate != null) {
					child.setLogDate(new SimpleDateFormat(CloudLogConst.EXEC_TIME_PATTERN).format(logDate));
				}
			}

			parent.addChildEntryMeasure(child);
		}
	}

	/**
	 * ExecutingLocally : org.pentaho.di.ui.spoon.job.JobGraph.setToolTip(int, int,
	 * int, int) ExecutingRemotely : nothing
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<JobExecEntryStatusDto> getStepStatus() throws Exception {
		List<JobExecEntryStatusDto> statusList = new ArrayList<>();

		if (executionConfiguration.isExecutingLocally() && job != null) {
			job.getJobEntryResults().forEach((jobResult) -> {

				JobExecEntryStatusDto status = new JobExecEntryStatusDto();
				status.setEntryName(jobResult.getJobEntryName());
				Result result = jobResult.getResult();
				int errCount = (int) result.getNrErrors();
				status.setErrCount(errCount);
				status.setResult(result.getResult());

				StringBuilder logText = new StringBuilder();
				logText.append("'").append(jobResult.getJobEntryName()).append("' ");
				if (result.getResult()) {
					logText.append("finished successfully.");
				} else {
					logText.append("failed.");
				}
				logText.append(Const.CR).append("------------------------").append(Const.CR).append(Const.CR);
				logText.append("Result         : ").append(result.getResult()).append(Const.CR);
				logText.append("Errors         : ").append(result.getNrErrors()).append(Const.CR);

				if (result.getNrLinesRead() > 0) {
					logText.append("Lines read     : ").append(result.getNrLinesRead()).append(Const.CR);
				}
				if (result.getNrLinesWritten() > 0) {
					logText.append("Lines written  : ").append(result.getNrLinesWritten()).append(Const.CR);
				}
				if (result.getNrLinesInput() > 0) {
					logText.append("Lines input    : ").append(result.getNrLinesInput()).append(Const.CR);
				}
				if (result.getNrLinesOutput() > 0) {
					logText.append("Lines output   : ").append(result.getNrLinesOutput()).append(Const.CR);
				}
				if (result.getNrLinesUpdated() > 0) {
					logText.append("Lines updated  : ").append(result.getNrLinesUpdated()).append(Const.CR);
				}
				if (result.getNrLinesDeleted() > 0) {
					logText.append("Lines deleted  : ").append(result.getNrLinesDeleted()).append(Const.CR);
				}
				if (result.getNrLinesRejected() > 0) {
					logText.append("Lines rejected : ").append(result.getNrLinesRejected()).append(Const.CR);
				}
				if (result.getResultFiles() != null && !result.getResultFiles().isEmpty()) {
					logText.append(Const.CR).append("Result files:").append(Const.CR);
					if (result.getResultFiles().size() > 10) {
						logText.append(" (10 files of ").append(result.getResultFiles().size()).append(" shown");
					}
					List<ResultFile> files = new ArrayList<ResultFile>(result.getResultFiles().values());
					for (int i = 0; i < files.size(); i++) {
						ResultFile file = files.get(i);
						logText.append("  - ").append(file.toString()).append(Const.CR);
					}
				}
				if (result.getRows() != null && !result.getRows().isEmpty()) {
					logText.append(Const.CR).append("Result rows: ");
					if (result.getRows().size() > 10) {
						logText.append(" (10 rows of ").append(result.getRows().size()).append(" shown");
					}
					logText.append(Const.CR);
					for (int i = 0; i < result.getRows().size() && i < 10; i++) {
						RowMetaAndData row = result.getRows().get(i);
						logText.append("  - ").append(row.toString()).append(Const.CR);
					}
				}
				status.setLogText(logText.toString());

				if (statusList.contains(status)) {
					int index = statusList.indexOf(status);
					statusList.set(index, status);
				} else {
					statusList.add(status);
				}

			});
		} else if (executionConfiguration.isExecutingRemotely() && active) {
			// nothing
			return statusList;
		}

		return statusList;
	}

	/**
	 * ExecutingLocally :
	 * org.pentaho.di.ui.spoon.trans.LogBrowser.installLogSniffer()
	 * ExecutingRemotely :
	 * org.pentaho.di.ui.spoon.SpoonSlave.treeItemSelected(TreeItem);org.pentaho
	 * .di.ui.spoon.SpoonSlave.showLog()
	 * 
	 * @return
	 * @throws Exception
	 */
	public JobExecLogDto getExecLog() throws Exception {
		JobExecLogDto execLog = new JobExecLogDto();
		execLog.setName(taskName);
		execLog.setLog(StringEscapeHelper.encode(getExecLogTextTrim()));
		return execLog;
	}

	private int lastLogLineFromNr = 0;

	private String getExecLogTextTrim() throws Exception {
		StringBuilder sb = new StringBuilder();

		if (executionConfiguration.isExecutingLocally() && job != null) {
			KettleLogLayout logLayout = new KettleLogLayout(true);
			List<String> childIds = LoggingRegistry.getInstance().getLogChannelChildren(job.getLogChannelId());
			int toNr = KettleLogStore.getLastBufferLineNr();
			List<KettleLoggingEvent> logLines = KettleLogStore.getLogBufferFromTo(childIds, false, lastLogLineFromNr,
					toNr);
			lastLogLineFromNr = toNr;
			for (int i = 0; i < logLines.size(); i++) {
				KettleLoggingEvent event = logLines.get(i);
				String line = logLayout.format(event).trim();
				sb.append(line).append("\n");
			}
		} else if (executionConfiguration.isExecutingRemotely() && active) {
			SlaveServer remoteSlaveServer = executionConfiguration.getRemoteServer();
			if (remoteSlaveServer != null) {
				try {
					SlaveServerJobStatus jobStatus = remoteSlaveServer.getJobStatus(taskCloneName, carteObjectId,
							lastLogLineFromNr);
					if (jobStatus != null) {
						sb.append(jobStatus.getLoggingString());
						lastLogLineFromNr = jobStatus.getLastLoggingLineNr();
					}
				} catch (ConnectException e) {
					// 服务器停止,会有一个等待恢复的过程
				}
			}
		}

		int len = this.extraLog.size();
		for (int i = 0; i < len; i++) {
			sb.append(this.extraLog.poll());
		}

		return sb.toString().trim();
	}


	private void sleepForPush() {

		Map<String, String> execParams = executionConfiguration.getParams();
		if (execParams.containsKey(SubcribePushService.SUBCRIBE_PUSH_KEY)
				&& Boolean.valueOf(execParams.get(SubcribePushService.SUBCRIBE_PUSH_KEY).toString())) {
			try {
				flushExecLog("任务启动,需要推送状态,休眠5秒,确保资源服务准备完毕.", true);
				// 需要推送状态,进行休眠5秒,确保推送端的数据准备完毕(资源服务启动任务结果可能还没有接收完毕,所以等待5秒)
				Thread.sleep(5000);
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 远程服务连接失败,重试5分钟,等待远程服务恢复
	 * 
	 * @return
	 * @throws Exception
	 */
	public void restartServer() throws Exception {

		if (executionConfiguration.isExecutingRemotely()) {

			SlaveServer remoteSlaveServer = executionConfiguration.getRemoteServer();
			if (remoteSlaveServer != null) {
				
				SlaveServerJobStatus oldstatus = remoteSlaveServer.getJobStatus(taskCloneName, null,Integer.MAX_VALUE);
				if (oldstatus != null && !Utils.isEmpty(oldstatus.getStatusDescription())&& oldstatus.isRunning()) {
					// 远程有当前任务正在运行
					carteObjectId = oldstatus.getId();
				} else {
					if (oldstatus != null && !Utils.isEmpty(oldstatus.getStatusDescription())&& !oldstatus.isRunning()) {
						// 远程有当前任务 但是没有运行,删除任务
						remoteSlaveServer.removeJob(taskCloneName, oldstatus.getId());
					}
					// jobMeta已经被释放了,需要重新获取
					if( metaClone == null ) {
						metaClone = CloudRepository.loadJobByName(owner,taskName, null);
						getConfigurationDto().putParamsFromMeta(metaClone);
						metaClone.setName(taskCloneName);
						metaClone.setVariable("executionId", executionId);
						metaClone.setVariable("idatrix.executionId", executionId);
						metaClone.setVariable("userId", execUser );
						metaClone.setVariable("idatrix.execUser", execUser );
						metaClone.setVariable("idatrix.owner", owner );
						
						metaClone.setLogLevel(executionConfiguration.getLogLevel());
						metaClone.activateParameters();
					}
					carteObjectId = Job.sendToSlaveServer((JobMeta)metaClone, (JobExecutionConfiguration)executionConfiguration,CloudApp.getInstance().getRepository(), CloudApp.getInstance().getMetaStore(owner));

					// 清除jobMeta对象,不再会使用,释放对象空间.
					clearJobMeta();
				}
				
			}
			
		}
	}

}
