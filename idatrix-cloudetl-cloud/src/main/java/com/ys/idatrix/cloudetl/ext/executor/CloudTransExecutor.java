/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.ext.executor;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.pentaho.di.cluster.SlaveServer;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.exception.KettleValueException;
import org.pentaho.di.core.logging.KettleLogLayout;
import org.pentaho.di.core.logging.KettleLogStore;
import org.pentaho.di.core.logging.KettleLoggingEvent;
import org.pentaho.di.core.logging.LogMessage;
import org.pentaho.di.core.logging.LoggingRegistry;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.util.ExecutorUtil;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransAdapter;
import org.pentaho.di.trans.TransExecutionConfiguration;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.cluster.TransSplitter;
import org.pentaho.di.trans.debug.BreakPointListener;
import org.pentaho.di.trans.debug.StepDebugMeta;
import org.pentaho.di.trans.debug.TransDebugMeta;
import org.pentaho.di.trans.step.RowAdapter;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepStatus;
import org.pentaho.di.www.CarteSingleton;
import org.pentaho.di.www.SlaveServerTransStatus;
import org.pentaho.di.www.WebResult;
import org.springframework.util.StringUtils;

import com.ys.idatrix.cloudetl.dto.trans.TransDebugExecDto;
import com.ys.idatrix.cloudetl.dto.trans.TransExecLogDto;
import com.ys.idatrix.cloudetl.dto.trans.TransExecStepMeasureDto;
import com.ys.idatrix.cloudetl.dto.trans.TransExecStepStatusDto;
import com.ys.idatrix.cloudetl.ext.CloudApp;
import com.ys.idatrix.cloudetl.ext.utils.StringEscapeHelper;
import com.ys.idatrix.cloudetl.logger.CloudLogger;
import com.ys.idatrix.cloudetl.recovery.trans.ResumeTransParser;

/**
 * Transformation execution implementation, - Keeping runnable environment, -
 * Implement execution procedure.
 * 
 * org.pentaho.di.ui.spoon.delegates.SpoonTransformationDelegate.executeTransformation()
 * org.pentaho.di.ui.spoon.trans.TransGraph
 * 
 * @author JW
 * @since 05-12-2017
 *
 */
public class CloudTransExecutor extends BaseTransExecutor implements Runnable {
	
	public static synchronized CloudTransExecutor initExecutor(TransMeta transMeta, TransExecutionConfiguration configuration, List<TransDebugExecDto> debugExecDtos, String execUser,String owner) throws Exception {
		CloudTransExecutor transExecutor = new CloudTransExecutor(transMeta, configuration, debugExecDtos, execUser,owner);
		CloudExecution.getInstance().putExecutor( transExecutor);
		return transExecutor;
	}
	
	private Trans trans = null;
	private boolean initialized = false;

	// The splitter to split a transformation for cluster schema
	private TransSplitter transSplitter = null;

	private List<TransDebugExecDto> debugExecDtos;
	private TransDebugMeta transDebugMeta;

	private ResumeTransParser resumeTransParser;
	
	private CloudTransExecutor(TransMeta transMeta, TransExecutionConfiguration configuration, 	List<TransDebugExecDto> debugExecDtos, String execUser,String owner) {
		super(transMeta, configuration,execUser,owner);
		this.debugExecDtos = debugExecDtos;
	}
	
	@Override
	public void clear() throws Exception {
		waiting= false;
		if (executionConfiguration.isExecutingLocally() && trans != null) {
			
			 KettleLogStore.discardLines( trans.getLogChannelId(), true );
			 
		} else if (executionConfiguration.isExecutingRemotely() && active ) {
			try {
				SlaveServer remoteSlaveServer = executionConfiguration.getRemoteServer();
				remoteSlaveServer.removeTransformation(metaClone.getName(), carteObjectId);
			}catch(Exception e) {
				//如果不存在 ,会报异常,忽略
			}
		} else if (getExecutionConfiguration().isExecutingClustered() && transSplitter != null) {
			try {
				Trans.cleanupCluster(CarteSingleton.getInstance().getLog(), transSplitter);
			}catch(Exception e) {
				//如果不存在 ,会报异常,忽略
			}
		}
		if(metaClone != null) {
			metaClone.disposeEmbeddedMetastoreProvider();
			metaClone.clear();
		}
			
		transDebugMeta = null ;
		metaClone = null ;
		result = null;
		carteObjectId = null;
		trans = null ;
		resumeTransParser = null ;
	}
	
	/**
	 * Runner implementation.
	 */
	@Override
	public void execTask() {
		try {
			
			flushExecStatusAndLog(CloudExecutorStatus.INITIALIZING, "");

			active =  true;
			
			getTransMeta().sortHopsNatural();
			getTransMeta().sortStepsNatural();
			metaClone.setLogLevel(executionConfiguration.getLogLevel());
			
			if (executionConfiguration.isExecutingLocally()) {
				// Set the variables
				metaClone.injectVariables(executionConfiguration.getVariables());
				// Set the named parameters
				Map<String, String> paramMap = executionConfiguration.getParams();
				Set<String> keys = paramMap.keySet();
				for (String key : keys) {
					metaClone.setParameterValue(key, Const.NVL(paramMap.get(key), ""));
				}
				metaClone.activateParameters();

				// Set the arguments
				Map<String, String> arguments = executionConfiguration.getArguments();
				String[] argumentNames = arguments.keySet().toArray(new String[arguments.size()]);
				Arrays.sort(argumentNames);

				String[] args = new String[argumentNames.length];
				for (int i = 0; i < args.length; i++) {
					String argumentName = argumentNames[i];
					args[i] = arguments.get(argumentName);
				}

				
				trans = new Trans(getTransMeta());
				trans.setLogLevel(executionConfiguration.getLogLevel());
				
				resumeTransParser = new ResumeTransParser(execUser,taskName, trans, getTransMeta());
				try {
					// 从缓存中恢复时,在初始化步骤前 判断是否需要处理meta数据
					resumeTransParser.dealStepMeta();
					trans.prepareExecution(args);
					resumeTransParser.init();
					// capturePreviewData(trans, metaClone.getSteps());
					initialized = true;
				} catch (KettleException e) {
					saveCloudLog(trans.getCloudLog(),trans.getCloudPartLog()); 
					checkErrorVisuals();
				}

				// debug/preview setting
				if (debugExecDtos != null && debugExecDtos.size() > 0) {
					transDebugMeta = new TransDebugMeta(getTransMeta());

					transDebugMeta.getStepDebugMetaMap().clear();
					for (TransDebugExecDto debugDto : debugExecDtos) {

						StepMeta stepMeta = getTransMeta().findStep(debugDto.getStepName());
						StepDebugMeta stepDebugMeta = new StepDebugMeta(stepMeta);
						stepDebugMeta.setRowCount(debugDto.getRowCount());
						if (debugDto.getCondition() != null) {
							stepDebugMeta.setCondition(debugDto.getCondition().transToCodition());
						}
						stepDebugMeta.setPausingOnBreakPoint(debugDto.isPausingOnBreakPoint());
						stepDebugMeta.setReadingFirstRows(debugDto.isReadingFirstRows());
						transDebugMeta.getStepDebugMetaMap().put(stepMeta, stepDebugMeta);

					}

					transDebugMeta.addRowListenersToTransformation(trans);
					transDebugMeta.addBreakPointListers(new BreakPointListener() {
						@Override
						public void breakPointHit(TransDebugMeta transDebugMeta, StepDebugMeta stepDebugMeta,
								RowMetaInterface rowBufferMeta, List<Object[]> rowBuffer) {
							
							flushExecStatusAndLog(CloudExecutorStatus.PAUSED,"获取到预览数据,步骤["+stepDebugMeta.getStepMeta().getName()+"],数据量["+rowBuffer.size()+"]");
							
							showDebugPreview(transDebugMeta, stepDebugMeta, rowBufferMeta, rowBuffer);
						}
					});
					
					flushExecLog( "增加数据预览监听...",true);
				}

				if (trans.isReadyToStart() && initialized) {
					trans.addTransListener(new TransAdapter() {
						@Override
						public void transFinished(Trans trans) {

							checkErrorVisuals();
						}
					});

					// 先恢复转换数据 , 再 启动转换运行(直接进入暂停状态)
					resumeTransParser.preRunStepHandle();
					resumeTransParser.resumeCacheData();
					trans.startThreads();

					flushExecStatusAndLog(CloudExecutorStatus.RUNNING, "");

					do {
						// Here should not loop thread sleeping !
						Thread.sleep(1000);
						this.result = trans.getResult();
						//执行日志从监听器中自动实时获取
						saveCloudLog(trans.getCloudLog(),trans.getCloudPartLog()); 
					}while (!trans.isFinished() && waiting) ;
					waiting= false ;
					//flushExecLog(getExecLogTextGeneral()); //执行日志从监听器中自动实时获取
					//获取可能残存的剩余日志
					saveCloudLog(trans.getCloudLog(),trans.getCloudPartLog()); 
					this.result = trans.getResult();
					
					flushExecStatusAndLog(CloudExecutorStatus.correctStatusType(trans.getStatus()), "");
				} else {
					flushExecStatusAndLog(CloudExecutorStatus.FAILED, "Preparing transformation execution failed");
					checkErrorVisuals();
				}

				// flushExecStatusAndLog(CloudExecutorStatus.correctStatusType(trans.getStatus()),
				// "");
			} else if (executionConfiguration.isExecutingRemotely()) {
				executionConfiguration.setPassingExport(true);

				SlaveServer remoteSlaveServer = executionConfiguration.getRemoteServer();
				try {
					// 判断远程是否真正执行(重启本地服务后可以再次进入,但是远程可能还没有结束)
					SlaveServerTransStatus oldstatus = remoteSlaveServer.getTransStatus(metaClone.getName(), null, Integer.MAX_VALUE);
					if(oldstatus != null && !Utils.isEmpty(oldstatus.getStatusDescription()) && oldstatus.isRunning()) {
						//远程有当前任务正在运行
						carteObjectId = oldstatus.getId() ;
					}else {
						if(oldstatus != null && !Utils.isEmpty(oldstatus.getStatusDescription()) && !oldstatus.isRunning()) {
							//远程有当前任务 但是没有运行,删除任务
							remoteSlaveServer.removeTransformation(metaClone.getName(), oldstatus.getId());
						}
						carteObjectId = Trans.sendToSlaveServer(getTransMeta(), getExecutionConfiguration(), CloudApp.getInstance().getRepository(), CloudApp.getInstance().getMetaStore(owner));
						flushExecStatusAndLog(CloudExecutorStatus.RUNNING, "");
					}

				}catch( ConnectException e) {
					flushExtraLog("远程服务["+remoteSlaveServer.getName()+"]连接失败,尝试等待服务重启(重启转换)", true);
					//服务器停止,等待5分钟
					if(!waitForRemoteRestart()) {
						throw e ;
					}
				}
				
				SlaveServerTransStatus transStatus = null;
				 do{
					// Here should not loop thread sleeping.
					Thread.sleep(1000);
					try {
						transStatus = remoteSlaveServer.getTransStatus(metaClone.getName(), carteObjectId,  -1);
					}catch( ConnectException e) {
						flushExtraLog("远程服务["+remoteSlaveServer.getName()+"]连接失败,尝试等待服务重启(重启转换)", true);
						transStatus =  null; //需要置空,否则会造成日志重复保存
						//服务器停止,等待5分钟
						if(!waitForRemoteRestart()) {
							waiting= false ;
							break ;
						}
					}
						
					if( transStatus != null) {
						waiting = transStatus.isRunning();
						this.result = transStatus.getResult();
						//实时获取日志信息
						saveCloudLog(transStatus.getLoggingString(),transStatus.getPartLog()); 
					}
				}while (waiting);
				 
				try {
					//获取可能残存的剩余日志
					transStatus = remoteSlaveServer.getTransStatus(metaClone.getName(), carteObjectId, -1);
					saveCloudLog(transStatus.getLoggingString(),transStatus.getPartLog());
					this.result = transStatus.getResult();
					flushExecStatusAndLog(transStatus == null ? CloudExecutorStatus.UNKNOWN : CloudExecutorStatus.getStatusForType(transStatus.getStatusDescription()), "");
					
				 }catch( ConnectException e) {
					//服务器停止
					flushExecStatusAndLog(  CloudExecutorStatus.FAILED , "远程服务器["+remoteSlaveServer.getName()+"]连接失败,运行结束.");
				 }

				// Cleanup transformation in a server run have finished
				remoteSlaveServer.cleanupTransformation(metaClone.getName(), carteObjectId);
			} else if (getExecutionConfiguration().isExecutingClustered()) {
				executionConfiguration.setPassingExport(true);
				transSplitter = new TransSplitter(getTransMeta());
				transSplitter.splitOriginalTransformation();

				for (String var : Const.INTERNAL_TRANS_VARIABLES) {
					executionConfiguration.getVariables().put(var, metaClone.getVariable(var));
				}
				for (String var : Const.INTERNAL_JOB_VARIABLES) {
					executionConfiguration.getVariables().put(var, metaClone.getVariable(var));
				}

				// Parameters override the variables.
				// For the time being we're passing the parameters over the wire
				// as variables...
				//
				TransMeta ot = transSplitter.getOriginalTransformation();
				for (String param : ot.listParameters()) {
					String value = Const.NVL(ot.getParameterValue(param),
							Const.NVL(ot.getParameterDefault(param), ot.getVariable(param)));
					if (!Utils.isEmpty(value)) {
						executionConfiguration.getVariables().put(param, value);
					}
				}

				try {
					Trans.executeClustered(transSplitter, getExecutionConfiguration());
					flushExecStatusAndLog(CloudExecutorStatus.RUNNING, "");
				} catch (KettleException e) {
					// Something happened posting the transformation to the
					// cluster.
					// We need to make sure to de-allocate ports and so on for
					// the next try...
					// We don't want to suppress original exception here.
					try {
						Trans.cleanupCluster(CarteSingleton.getInstance().getLog(), transSplitter);
					} catch (Exception ee) {
						throw new Exception("Error executing transformation and error to clenaup cluster", ee);
					}
					// we still have execution error but cleanup ok here...
					throw e;
				}

				Trans.monitorClusteredTransformation(CarteSingleton.getInstance().getLog(), transSplitter, null);
				// Result result =
				// Trans.getClusteredTransformationResult(EtlApp.getInstance().getLog(),
				// transSplitter, null);

				// Flush executor log in log file
				flushExecLog(getExecLogTextGeneral(0),false);

				// Assume executor status through the whole cluster
				// 1. Assume executor status in master
				Map<TransMeta, String> carteMap = transSplitter.getCarteObjectMap();
				SlaveServer masterServer = transSplitter.getMasterServer();
				TransMeta masterTransMeta = transSplitter.getMaster();
				CloudExecutorStatus masterStatus = CloudExecutorStatus.UNDEFINED;

				if (masterTransMeta != null && masterTransMeta.nrSteps() > 0) {
					SlaveServerTransStatus transStatus = masterServer.getTransStatus(masterTransMeta.getName(), carteMap.get(masterTransMeta), 0);
					masterStatus = transStatus == null ? CloudExecutorStatus.UNKNOWN : CloudExecutorStatus.getStatusForType(transStatus.getStatusDescription());
					masterServer.cleanupTransformation(masterTransMeta.getName(), carteMap.get(masterTransMeta));
				}

				// 2. Assume executor status in slaves
				SlaveServer[] slaveServers = transSplitter.getSlaveTargets();
				TransMeta[] slavesTransMeta = transSplitter.getSlaves();
				CloudExecutorStatus[] slavesStatus = new CloudExecutorStatus[slaveServers.length];
				for (int s = 0; s < slaveServers.length; s++) {
					SlaveServerTransStatus transStatus = slaveServers[s].getTransStatus(slavesTransMeta[s].getName(), carteMap.get(slavesTransMeta[s]), 0);
					slavesStatus[s] = transStatus == null ? CloudExecutorStatus.UNKNOWN : CloudExecutorStatus.getStatusForType(transStatus.getStatusDescription());
					slaveServers[s].cleanupTransformation(slavesTransMeta[s].getName(), carteMap.get(slavesTransMeta[s]));
				}

				// 3. Assert executor status in master & slaves
				CloudExecutorStatus slavesStatusMerged = CloudExecutorStatus.mergeStatus(slavesStatus);
				flushExecStatusAndLog(
						CloudExecutorStatus.mergeStatus(new CloudExecutorStatus[] { masterStatus, slavesStatusMerged }),
						"");

				// Cleanup all clusters as part of a clustered transformation
				try {
					Trans.cleanupCluster(CarteSingleton.getInstance().getLog(), transSplitter);
				} catch (Exception ee) {
					throw new Exception("Error executing transformation and error to clenaup cluster", ee);
				}
			} else {
				// Not executed!
				flushExecStatusAndLog(CloudExecutorStatus.WAITING,
						"Error execution configuration, transformation has been not executed!");
			}
		} catch (Exception e) {
			flushExecStatusAndLog(CloudExecutorStatus.FAILED, CloudLogger.getExceptionMessage(e,false));
			logger.error("执行失败！", e);
		} finally {
			
			// Cleanup transformation have finished
			if (trans != null) {
				trans.cleanup();
			}

			// Trigger of analyzer
			startToolkitTrigger(getTransMeta(), taskName , owner , status);;
		}
	}

	protected Map<String, List<String[]>> previewData = new HashMap<>();

	private void showDebugPreview(TransDebugMeta transDebugMeta, StepDebugMeta stepDebugMeta, RowMetaInterface rowBufferMeta, List<Object[]> rowBuffer) {

		String stepName = stepDebugMeta.getStepMeta().getName();
		
		List<String[]> result = previewData.get(stepName);
		if(result == null || result.size() == 0 ) {
			result = new ArrayList<String[]>();
			result.add(rowBufferMeta.getFieldNames());
		}
		
		for( Object[] row : rowBuffer) {
			String[]  rowData = new String[rowBufferMeta.size()];
			for (int c = 0; c < rowBufferMeta.size(); c++) {
				try {
					rowData[c] = rowBufferMeta.getString(row, c);
				} catch (KettleValueException e) {
					rowData[c] = "Error";
				}
			}
			result.add(rowData);
		}
		previewData.put(stepName, result);
	}

	public Map<String, List<String[]>> getDebugPreviewData() {
		Map<String, List<String[]>> old = previewData;
		previewData =  new HashMap<>();
		return old ;
	}
	
	
	public boolean execMorePreview() throws Exception {
		
		if( transDebugMeta != null && transDebugMeta.getNrOfUsedSteps() > 0) {
			transDebugMeta.getStepDebugMetaMap().values().stream().forEach(  stepDebugMeta -> {
				int rowCount = stepDebugMeta.getRowCount();
				int bufferSize = stepDebugMeta.getRowBuffer().size();
				if( rowCount > 0 && bufferSize >= rowCount ) {
					//缓存已满
					stepDebugMeta.getRowBuffer().clear();
				}
			});
			
		}
		
		return execResume(true);
	}

	public void capturePreviewData(Trans trans, List<StepMeta> stepMetas) {
		final StringBuffer loggingText = new StringBuffer();

		try {
			final TransMeta transMeta1 = trans.getTransMeta();

			for (final StepMeta stepMeta : stepMetas) {
				final RowMetaInterface rowMeta = transMeta1.getStepFields(stepMeta).clone();
				previewMetaMap.put(stepMeta, rowMeta);
				final List<Object[]> rowsData = new LinkedList<>();

				previewDataMap.put(stepMeta, rowsData);
				previewLogMap.put(stepMeta, loggingText);

				StepInterface step = trans.findRunThread(stepMeta.getName());

				if (step != null) {
					step.addRowListener(new RowAdapter() {
						@Override
						public void rowWrittenEvent(RowMetaInterface rowMeta, Object[] row) throws KettleStepException {
							try {
								rowsData.add(rowMeta.cloneRow(row));
								if (rowsData.size() > 100) {
									rowsData.remove(0);
								}
							} catch (KettleValueException e) {
								throw new KettleStepException("Unable to clone row for metadata : " + rowMeta, e);
							}
						}
					});
				}
			}
		} catch (KettleStepException e) {
			loggingText.append(Const.getStackTracker(e));
		}

		trans.addTransListener(new TransAdapter() {
			@Override
			public void transFinished(Trans trans) throws KettleException {
				if (trans.getErrors() != 0) {
					trans.getSteps().stream().filter((combi) -> (combi.copy == 0)).forEachOrdered((combi) -> {
						StringBuffer logBuffer = KettleLogStore.getAppender()
								.getBuffer(combi.step.getLogChannel().getLogChannelId(), false);
						previewLogMap.put(combi.stepMeta, logBuffer);
					});
				}
			}
		});
	}

	protected Map<StepMeta, RowMetaInterface> previewMetaMap = new HashMap<>();
	protected Map<StepMeta, List<Object[]>> previewDataMap = new HashMap<>();
	protected Map<StepMeta, StringBuffer> previewLogMap = new HashMap<>();

	private void checkErrorVisuals() {
		if (trans.getErrors() > 0) {
			if( resumeTransParser != null) {
				resumeTransParser.saveCacheData();
			}
			

			trans.getSteps().stream().filter((combi) -> (combi.step.getErrors() > 0)).forEachOrdered((combi) -> {
				String channelId = combi.step.getLogChannel().getLogChannelId();
				List<KettleLoggingEvent> eventList = KettleLogStore.getLogBufferFromTo(channelId, false, 0,
						KettleLogStore.getLastBufferLineNr());
				StringBuilder logText = new StringBuilder();
				eventList.stream().map((event) -> event.getMessage())
						.filter((message) -> (message instanceof LogMessage)).map((message) -> (LogMessage) message)
						.filter((logMessage) -> (logMessage.isError())).forEachOrdered((logMessage) -> {
							logText.append(logMessage.getMessage()).append(Const.CR);
						});
			});
		} else {
			if(  resumeTransParser != null &&  initialized) {
				resumeTransParser.removeCacheData();
			}
		}
	}

	@Override
	public String execStatus() {
		String s = null;
		if (executionConfiguration.isExecutingLocally() && trans != null) {
			s  = trans.getStatus();
		} else if (executionConfiguration.isExecutingRemotely() && active && carteObjectId != null ) {
			SlaveServer remoteSlaveServer = executionConfiguration.getRemoteServer();
			if(remoteSlaveServer != null ) {
				SlaveServerTransStatus transStatus;
				try {
					transStatus = remoteSlaveServer.getTransStatus(metaClone.getName(), carteObjectId,Integer.MAX_VALUE);
					s = (transStatus!= null && !Utils.isEmpty( transStatus.getStatusDescription() )) ? transStatus.getStatusDescription():s;
				} catch (Exception e) {
				}
				
			}
		}
		
		return  Const.NVL(s, super.getStatus()) ;
	}

	@Override
	public boolean execPause() throws Exception {
		return execPause(true);
	}

	public boolean execPause(boolean isSaveData) throws Exception {
		boolean paused = false;

		flushExecStatusAndLog(CloudExecutorStatus.PAUSED, "触发暂停操作...");
		if (executionConfiguration.isExecutingLocally()) {
			if (trans != null) {
				trans.pauseRunning();
				paused = trans.isPaused();
			}
		} else if (executionConfiguration.isExecutingRemotely() && active) {
			if (carteObjectId != null) {
				SlaveServer remoteSlaveServer = executionConfiguration.getRemoteServer();
				if( remoteSlaveServer != null ) {
					try {
						remoteSlaveServer.pauseResumeTransformation(metaClone.getName(), carteObjectId);
						paused = true;
					}catch( ConnectException e) {
						//服务器停止,会有一个等待恢复的过程
						flushExtraLog("远程服务["+remoteSlaveServer.getName()+"]连接失败,请等待重新连接后再试.",true);
						paused = false;
					}
				}else {
					paused = true;
				}
			}
		} else if (getExecutionConfiguration().isExecutingClustered() && active) {
			if (transSplitter != null) {
				// Pause transformation in master
				Map<TransMeta, String> carteMap = transSplitter.getCarteObjectMap();
				SlaveServer masterServer = transSplitter.getMasterServer();
				TransMeta masterTransMeta = transSplitter.getMaster();
				masterServer.pauseResumeTransformation(metaClone.getName(), carteMap.get(masterTransMeta));

				// Pause transformation in slaves
				SlaveServer[] slaveServers = transSplitter.getSlaveTargets();
				TransMeta[] slavesTransMeta = transSplitter.getSlaves();
				for (int s = 0; s < slaveServers.length; s++) {
					slaveServers[s].pauseResumeTransformation(metaClone.getName(), carteMap.get(slavesTransMeta[s]));
				}

				paused = true;
			}
		}

		if (isSaveData && resumeTransParser != null ) {
			// 暂停后保存数据到缓存
			resumeTransParser.saveCacheData();
		}

		if (paused) {
			flushExecStatusAndLog(CloudExecutorStatus.PAUSED, "");
		} else {
			flushExecStatusAndLog(getStatus(), "Can not pause transformation!");
		}

		return paused;
	}
	
	@Override
	public boolean execStop() throws Exception {
		return execStop(true);
	}
	
	
	public boolean execStop(boolean isRemveData) throws Exception {
		boolean stopped = false;
		
		flushExecStatusAndLog(CloudExecutorStatus.HALTING, "触发停止操作...");
		if(isRemveData && resumeTransParser != null) {
			resumeTransParser.removeCacheData();
		}

		if (executionConfiguration.isExecutingLocally()) {
			if (trans != null) {

				// 当有数据库组件时,数据库无法连接时,退出处理需要等待超时(时间很久)
				ExecutorUtil.getExecutor().execute(new Runnable() {
					@Override
					public void run() {
						trans.stopAll();
					}
				});

				// Wait until everything is stopped
				// trans.waitUntilFinished();
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}

				stopped = trans.isStopped();
			}
		} else if (executionConfiguration.isExecutingRemotely() && active) {
			if (carteObjectId != null) {
				SlaveServer remoteSlaveServer = executionConfiguration.getRemoteServer();
				if( remoteSlaveServer != null ) {
					try {
						WebResult webr = remoteSlaveServer.stopTransformation(metaClone.getName(), carteObjectId);
						if (webr!= null && WebResult.STRING_OK.equals(webr.getResult())) {
							remoteSlaveServer.cleanupTransformation(metaClone.getName(), carteObjectId);
							stopped = true;
						}
						// Wait until everything is stopped , remoteSlaveServer.wait(3000);
						Thread.sleep(2000);
					}catch( ConnectException e) {
						//服务器停止,会有一个等待恢复的过程
						flushExtraLog("远程服务["+remoteSlaveServer.getName()+"]连接失败,触发停止运行.", false);
						flushExecStatusAndLog(CloudExecutorStatus.STOPPED, "远程服务["+remoteSlaveServer.getName()+"]连接失败,触发停止运行.");
						stopped = true;
					}
				}
					
			}
		} else if (getExecutionConfiguration().isExecutingClustered() && active) {
			if (transSplitter != null) {
					// Stop transformation in master
					Map<TransMeta, String> carteMap = transSplitter.getCarteObjectMap();
					SlaveServer masterServer = transSplitter.getMasterServer();
					TransMeta masterTransMeta = transSplitter.getMaster();

					stopped = true;
					if (masterTransMeta != null && masterTransMeta.nrSteps() > 0) {
						WebResult webr = masterServer.stopTransformation(masterTransMeta.getName(), carteMap.get(masterTransMeta));
						if (webr!= null && WebResult.STRING_OK.equals(webr.getResult())) {
							/*
							 * int counter = 5; SlaveServerTransStatus transStatus =
							 * masterServer.getTransStatus(masterTransMeta.getName(),
							 * carteMap.get(masterTransMeta), 0); while (counter-- > 0 &&
							 * CloudExecutorStatus.STOPPED !=
							 * CloudExecutorStatus.getStatusForType(transStatus.getStatusDescription())) {
							 * Thread.sleep(1000); transStatus =
							 * masterServer.getTransStatus(masterTransMeta.getName(),
							 * carteMap.get(masterTransMeta), 0); }
							 */

							masterServer.cleanupTransformation(masterTransMeta.getName(), carteMap.get(masterTransMeta));
							stopped &= true;
						} else {
							stopped &= false;
						}
					}

					// Stop transformation in slaves
					SlaveServer[] slaveServers = transSplitter.getSlaveTargets();
					TransMeta[] slavesTransMeta = transSplitter.getSlaves();
					for (int s = 0; s < slaveServers.length; s++) {
						WebResult webr = slaveServers[s].stopTransformation(slavesTransMeta[s].getName(), carteMap.get(slavesTransMeta[s]));
						if (webr!= null && WebResult.STRING_OK.equals(webr.getResult())) {
							slaveServers[s].cleanupTransformation(slavesTransMeta[s].getName(), carteMap.get(slavesTransMeta[s]));
							stopped &= true;
						} else {
							stopped &= false;
						}
					}

					/*
					 * for (int s = 0; s < slaveServers.length; s++) { int counter = 60;
					 * SlaveServerTransStatus transStatus =
					 * slaveServers[s].getTransStatus(slavesTransMeta[s].getName(),
					 * carteMap.get(slavesTransMeta[s]), 0); while (counter-- > 0 &&
					 * CloudExecutorStatus.STOPPED !=
					 * CloudExecutorStatus.getStatusForType(transStatus.getStatusDescription())) {
					 * Thread.sleep(1000); transStatus =
					 * slaveServers[s].getTransStatus(slavesTransMeta[s].getName(),
					 * carteMap.get(slavesTransMeta[s]), 0); }
					 * 
					 * slaveServers[s].cleanupTransformation(slavesTransMeta[s].getName(),
					 * carteMap.get(slavesTransMeta[s])); }
					 */

					// Wait until everything is stopped
					// masterServer.wait(3000);
					Thread.sleep(3000);
			}
		}

		if (stopped) {
			finished = true;
			endDate = new Date();
			flushExecStatusAndLog(CloudExecutorStatus.STOPPED, "");

			// Cleanup transformation have finished
			/*
			 * if (trans != null) { trans.cleanup(); }
			 */
		} else {
			flushExecStatusAndLog(getStatus(), "Can not stop transformation!");
		}

		return stopped;
	}

	@Override
	public boolean execResume() throws Exception {
		
		if( transDebugMeta != null && transDebugMeta.getNrOfUsedSteps() > 0) {
			//当进行debug预览数据直接恢复执行时,将清空预览行数(不再预览数据已满的数据)
			transDebugMeta.getStepDebugMetaMap().values().stream().forEach(  stepDebugMeta -> {
				int rowCount = stepDebugMeta.getRowCount();
				int bufferSize = stepDebugMeta.getRowBuffer().size();
				if( rowCount > 0 && bufferSize >= rowCount ) {
					//预览行数为0
					stepDebugMeta.setReadingFirstRows(false);
					stepDebugMeta.setPausingOnBreakPoint(false);
					stepDebugMeta.getRowBuffer().clear();
					stepDebugMeta.setRowCount(0);
					
					flushExecLog( "停止步骤["+stepDebugMeta.getStepMeta().getName()+"]数据预览.",true);
				}
			});
			
		}
		
		return execResume(true);
	}

	public boolean execResume(boolean isResumeData) throws Exception {
		boolean resumed = false;
		
		flushExecStatusAndLog(CloudExecutorStatus.RUNNING, "触发恢复操作...");
		if (isResumeData && resumeTransParser != null) {
			resumeTransParser.resumeCacheData();
		}

		if (executionConfiguration.isExecutingLocally()) {
			if (trans != null) {
				trans.resumeRunning();
				resumed = !trans.isPaused();
				// resumed = trans.isRunning();
			}
		} else if (executionConfiguration.isExecutingRemotely() && active) {
			if (carteObjectId != null) {
				SlaveServer remoteSlaveServer = executionConfiguration.getRemoteServer();
				if( remoteSlaveServer != null ) {
					try {
						remoteSlaveServer.pauseResumeTransformation(metaClone.getName(), carteObjectId);
						resumed = true;
					}catch( ConnectException e) {
						//服务器停止,会有一个等待恢复的过程
						flushExtraLog("远程服务["+remoteSlaveServer.getName()+"]连接失败,请等待重新连接后再试.",true);
						resumed = true;
					}
				}
			}
		} else if (getExecutionConfiguration().isExecutingClustered() && active) {
			if (transSplitter != null) {
					// Pause transformation in master
					Map<TransMeta, String> carteMap = transSplitter.getCarteObjectMap();
					SlaveServer masterServer = transSplitter.getMasterServer();
					TransMeta masterTransMeta = transSplitter.getMaster();
					masterServer.pauseResumeTransformation(metaClone.getName(), carteMap.get(masterTransMeta));

					// Pause transformation in slaves
					SlaveServer[] slaveServers = transSplitter.getSlaveTargets();
					TransMeta[] slavesTransMeta = transSplitter.getSlaves();
					for (int s = 0; s < slaveServers.length; s++) {
						slaveServers[s].pauseResumeTransformation(metaClone.getName(),
								carteMap.get(slavesTransMeta[s]));
					}

					resumed = true;
			}
		}

		if (resumed) {
			flushExecStatusAndLog(CloudExecutorStatus.RUNNING, "");
		} else {
			flushExecStatusAndLog(getStatus(), "Can not resume transformation!");
		}

		return resumed;
	}

	@Override
	public List<TransExecStepMeasureDto> getStepMeasure() throws Exception {
		List<TransExecStepMeasureDto> measureList = new ArrayList<>();

		if (executionConfiguration.isExecutingLocally() && trans != null) {
			for (int i = 0; i < trans.nrSteps(); i++) {
				StepInterface baseStep = trans.getRunThread(i);
				StepStatus stepStatus = new StepStatus(baseStep);
				String[] fields = stepStatus.getTransLogFields();

				TransExecStepMeasureDto measure = new TransExecStepMeasureDto(fields);
				measureList.add(measure);
			}
		} else if (executionConfiguration.isExecutingRemotely() && active ) {
			SlaveServerTransStatus transStatus =  null ;
			SlaveServer remoteSlaveServer = executionConfiguration.getRemoteServer();
			if( remoteSlaveServer != null ) {
				try {
					transStatus = remoteSlaveServer.getTransStatus(metaClone.getName(), carteObjectId, 0);
				}catch( ConnectException e) {
					//服务器停止,会有一个等待恢复的过程
				}
			}
			if( transStatus != null ) {
				List<StepStatus> stepStatusList = transStatus.getStepStatusList();
				for (int i = 0; i < stepStatusList.size(); i++) {
					StepStatus stepStatus = stepStatusList.get(i);
					String[] fields = stepStatus.getTransLogFields();

					TransExecStepMeasureDto measure = new TransExecStepMeasureDto(fields);
					measureList.add(measure);
				}
				
			}
				
		} else if (getExecutionConfiguration().isExecutingClustered() && transSplitter != null && active ) {
			SlaveServer masterServer = transSplitter.getMasterServer();
			if(masterServer != null ) {
				SlaveServer[] slaves = transSplitter.getSlaveTargets();
				Map<TransMeta, String> carteMap = transSplitter.getCarteObjectMap();

				SlaveServerTransStatus transStatus = masterServer.getTransStatus(metaClone.getName(), carteMap.get(transSplitter.getMaster()), 0);
				List<StepStatus> stepStatusList = transStatus.getStepStatusList();
				for (int i = 0; i < stepStatusList.size(); i++) {
					StepStatus stepStatus = stepStatusList.get(i);
					String[] fields = stepStatus.getTransLogFields();

					TransExecStepMeasureDto measure = new TransExecStepMeasureDto(fields);
					measureList.add(measure);
				}

				for (SlaveServer slaveServer : slaves) {
					transStatus = slaveServer.getTransStatus(metaClone.getName(),
							carteMap.get(transSplitter.getSlaveTransMap().get(slaveServer)), 0);
					stepStatusList = transStatus.getStepStatusList();
					for (int i = 0; i < stepStatusList.size(); i++) {
						StepStatus stepStatus = stepStatusList.get(i);
						String[] fields = stepStatus.getTransLogFields();

						TransExecStepMeasureDto measure = new TransExecStepMeasureDto(fields);
						measureList.add(measure);
					}
				}
			}
		}
		return measureList;
	}

	@Override
	public List<TransExecStepStatusDto> getStepStatus() throws Exception {
		List<TransExecStepStatusDto> statusList = new ArrayList<>();

		HashMap<String, Integer> stepIndex = new HashMap<>();
		if (executionConfiguration.isExecutingLocally() && trans != null) {
			if(trans.getSteps() == null) {
				return statusList;
			}
			trans.getSteps().forEach((combi) -> {
				Integer index = stepIndex.get(combi.stepMeta.getName());
				if (index == null) {
					TransExecStepStatusDto status = new TransExecStepStatusDto();
					status.setStepName(combi.stepMeta.getName());

					int errCount = (int) combi.step.getErrors();
					status.setErrCount(errCount);

					if (errCount > 0) {
						StringBuilder logText = new StringBuilder();
						String channelId = combi.step.getLogChannel().getLogChannelId();
						List<KettleLoggingEvent> eventList = KettleLogStore.getLogBufferFromTo(channelId, false, -1,
								KettleLogStore.getLastBufferLineNr());
						eventList.stream().map((event) -> event.getMessage())
								.filter((message) -> (message instanceof LogMessage))
								.map((message) -> (LogMessage) message).filter((logMessage) -> (logMessage.isError()))
								.forEachOrdered((logMessage) -> {
									logText.append(logMessage.getMessage()).append(Const.CR);
								});

						status.setLogText(logText.toString());
					}

					stepIndex.put(combi.stepMeta.getName(), statusList.size());
					statusList.add(status);
				} else {
					TransExecStepStatusDto status = statusList.get(index);
					int errCount = (int) (combi.step.getErrors() + status.getErrCount());
					status.setErrCount(errCount);
				}
			});
		} else if (executionConfiguration.isExecutingRemotely() &&  executionConfiguration.getRemoteServer() != null && active ) {
			SlaveServerTransStatus transStatus = null ;
			SlaveServer remoteSlaveServer = executionConfiguration.getRemoteServer();
			if( remoteSlaveServer != null ) {
				try {
					transStatus = remoteSlaveServer.getTransStatus(metaClone.getName(), carteObjectId, 0);
				}catch( ConnectException e) {
					//服务器停止,会有一个等待恢复的过程
				}
			}
			if(transStatus != null) {
				List<StepStatus> stepStatusList = transStatus.getStepStatusList();
				for (int i = 0; i < stepStatusList.size(); i++) {
					StepStatus stepStatus = stepStatusList.get(i);
					Integer index = stepIndex.get(stepStatus.getStepname());
					if (index == null) {
						TransExecStepStatusDto status = new TransExecStepStatusDto();
						status.setStepName(stepStatus.getStepname());
						status.setErrCount((int) stepStatus.getErrors());

						stepIndex.put(stepStatus.getStepname(), statusList.size());
						statusList.add(status);
					} else {
						TransExecStepStatusDto status = statusList.get(index);
						int errCount = (int) (stepStatus.getErrors() + status.getErrCount());
						status.setErrCount(errCount);
					}
				}
			}
		} else if (getExecutionConfiguration().isExecutingClustered()  && transSplitter != null &&  active ) {
			SlaveServer masterServer = transSplitter.getMasterServer();
			SlaveServer[] slaves = transSplitter.getSlaveTargets();
			Map<TransMeta, String> carteMap = transSplitter.getCarteObjectMap();

			SlaveServerTransStatus transStatus = masterServer.getTransStatus(metaClone.getName(),
					carteMap.get(transSplitter.getMaster()), 0);
			List<StepStatus> stepStatusList = transStatus.getStepStatusList();
			for (int i = 0; i < stepStatusList.size(); i++) {
				StepStatus stepStatus = stepStatusList.get(i);
				Integer index = stepIndex.get(stepStatus.getStepname());
				if (index == null) {
					TransExecStepStatusDto status = new TransExecStepStatusDto();
					status.setStepName(stepStatus.getStepname());
					status.setErrCount((int) stepStatus.getErrors());

					stepIndex.put(stepStatus.getStepname(), statusList.size());
					statusList.add(status);
				} else {
					TransExecStepStatusDto status = statusList.get(index);
					int errCount = (int) (stepStatus.getErrors() + status.getErrCount());
					status.setErrCount(errCount);
				}
			}

			for (SlaveServer slaveServer : slaves) {
				transStatus = slaveServer.getTransStatus(metaClone.getName(),
						carteMap.get(transSplitter.getSlaveTransMap().get(slaveServer)), 0);
				stepStatusList = transStatus.getStepStatusList();
				for (int i = 0; i < stepStatusList.size(); i++) {
					StepStatus stepStatus = stepStatusList.get(i);
					Integer index = stepIndex.get(stepStatus.getStepname());
					if (index == null) {
						TransExecStepStatusDto status = new TransExecStepStatusDto();
						status.setStepName(stepStatus.getStepname());
						status.setErrCount((int) stepStatus.getErrors());

						stepIndex.put(stepStatus.getStepname(), statusList.size());
						statusList.add(status);
					} else {
						TransExecStepStatusDto status = statusList.get(index);
						int errCount = (int) (stepStatus.getErrors() + status.getErrCount());
						status.setErrCount(errCount);
					}
				}
			}
		}

		return statusList;
	}

	@Override
	public TransExecLogDto getExecLog() throws Exception {
		TransExecLogDto execLog = new TransExecLogDto();
		execLog.setName(taskName);
		execLog.setLog(StringEscapeHelper.encode(getExecLogTextTrim()));
		return execLog;
	}

	private int lastLogLineFromNr = 0;
	private HashMap<String, Integer> slaveLastLogLineFromNr = new HashMap<>();

	private String getExecLogTextTrim() throws Exception {
		StringBuilder sb = new StringBuilder();

		if (executionConfiguration.isExecutingLocally() && trans != null) {
			KettleLogLayout logLayout = new KettleLogLayout(true);
			List<String> childIds = LoggingRegistry.getInstance().getLogChannelChildren(trans.getLogChannelId());

			int toNr = KettleLogStore.getLastBufferLineNr();
			List<KettleLoggingEvent> logLines = KettleLogStore.getLogBufferFromTo(childIds, false, lastLogLineFromNr,
					toNr);
			lastLogLineFromNr = toNr;

			for (int i = 0; i < logLines.size(); i++) {
				KettleLoggingEvent event = logLines.get(i);
				String line = logLayout.format(event).trim();
				sb.append(line).append("\n");
			}
		} else if (executionConfiguration.isExecutingRemotely() &&  active  ) {
			SlaveServer remoteSlaveServer = executionConfiguration.getRemoteServer();
			if( remoteSlaveServer != null ) {
				try {
					SlaveServerTransStatus transStatus = remoteSlaveServer.getTransStatus(metaClone.getName(),carteObjectId, lastLogLineFromNr);
					sb.append(transStatus.getLoggingString());
					lastLogLineFromNr = transStatus.getLastLoggingLineNr();
				}catch( ConnectException e) {
					//服务器停止,会有一个等待恢复的过程
				}
			}
		
		} else if (getExecutionConfiguration().isExecutingClustered() && transSplitter != null  &&  active  ) {
			SlaveServer masterServer = transSplitter.getMasterServer();
			SlaveServer[] slaves = transSplitter.getSlaveTargets();
			Map<TransMeta, String> carteMap = transSplitter.getCarteObjectMap();

			SlaveServerTransStatus transStatus = masterServer.getTransStatus(metaClone.getName(),
					carteMap.get(transSplitter.getMaster()), lastLogLineFromNr);
			String log = transStatus.getLoggingString();
			lastLogLineFromNr = transStatus.getLastLoggingLineNr();

			for (SlaveServer slaveServer : slaves) {
				Integer startNr = slaveLastLogLineFromNr.get(slaveServer.getName());
				transStatus = slaveServer.getTransStatus(metaClone.getName(),
						carteMap.get(transSplitter.getSlaveTransMap().get(slaveServer)),
						startNr != null ? startNr.intValue() : 0);
				if (StringUtils.hasText(transStatus.getLoggingString())) {
					log += transStatus.getLoggingString();
				}

				slaveLastLogLineFromNr.put(slaveServer.getName(), transStatus.getLastLoggingLineNr());
			}
			sb.append(log);
		}

		int len = this.extraLog.size();
		for(int i =0 ; i<len ;i++) {
			sb.append(this.extraLog.poll());
		}
		return sb.toString();
	}

	private String getExecLogTextGeneral(Integer fromNr) throws Exception {
		StringBuilder sb = new StringBuilder("\n");
		if(fromNr == null) {
			 fromNr = 0;
		}

		if (executionConfiguration.isExecutingLocally() && trans != null) {
			KettleLogLayout logLayout = new KettleLogLayout(true);
			List<String> childIds = LoggingRegistry.getInstance().getLogChannelChildren(trans.getLogChannelId());

			int toNr = KettleLogStore.getLastBufferLineNr();
			List<KettleLoggingEvent> logLines = KettleLogStore.getLogBufferFromTo(childIds, false, fromNr, toNr);

			for (int i = 0; i < logLines.size(); i++) {
				KettleLoggingEvent event = logLines.get(i);
				String line = logLayout.format(event).trim();
				sb.append(line).append("\n");
			}

		} else if (executionConfiguration.isExecutingRemotely() &&  active ) {

			SlaveServer remoteSlaveServer = executionConfiguration.getRemoteServer();
			if( remoteSlaveServer != null ) {
				try {
					SlaveServerTransStatus transStatus = remoteSlaveServer.getTransStatus(metaClone.getName(), carteObjectId, fromNr);
					if(transStatus != null ) {
						sb.append(transStatus.getLoggingString());
					}
				}catch( ConnectException e) {
					//服务器停止,会有一个等待恢复的过程
				}
			}
		} else if (getExecutionConfiguration().isExecutingClustered() && transSplitter != null  &&  active ) {
			SlaveServer masterServer = transSplitter.getMasterServer();
			SlaveServer[] slaves = transSplitter.getSlaveTargets();
			Map<TransMeta, String> carteMap = transSplitter.getCarteObjectMap();

			SlaveServerTransStatus transStatus = masterServer.getTransStatus(metaClone.getName(), carteMap.get(transSplitter.getMaster()), fromNr);
			if(transStatus != null ) {
				String log = transStatus.getLoggingString();
				for (SlaveServer slaveServer : slaves) {
					transStatus = slaveServer.getTransStatus(metaClone.getName(), carteMap.get(transSplitter.getSlaveTransMap().get(slaveServer)), fromNr);
					if (transStatus!= null && StringUtils.hasText(transStatus.getLoggingString())) {
						log += transStatus.getLoggingString();
					}
				}
				sb.append(log);
			}
		}

		return sb.toString();
	}
	
	
	/**
	 * 远程服务连接失败,重试5分钟,等待远程服务恢复
	 * @return
	 * @throws Exception 
	 */
	public void restartServer() throws Exception {

		if(executionConfiguration.isExecutingRemotely() ) {
			
			SlaveServer remoteSlaveServer = executionConfiguration.getRemoteServer();
			if( remoteSlaveServer != null ) {
				
				SlaveServerTransStatus oldstatus = remoteSlaveServer.getTransStatus(metaClone.getName(), null, Integer.MAX_VALUE);
				if(oldstatus != null && !Utils.isEmpty(oldstatus.getStatusDescription()) && oldstatus.isRunning()) {
					//远程有当前任务正在运行
					carteObjectId = oldstatus.getId() ;
				}else {
					if(oldstatus != null && !Utils.isEmpty(oldstatus.getStatusDescription()) && !oldstatus.isRunning()) {
						//远程有当前任务 但是没有运行,删除任务
						remoteSlaveServer.removeTransformation(metaClone.getName(), oldstatus.getId());
					}
					carteObjectId = Trans.sendToSlaveServer(getTransMeta(), getExecutionConfiguration(), CloudApp.getInstance().getRepository(), CloudApp.getInstance().getMetaStore(owner));
					flushExecStatusAndLog(CloudExecutorStatus.RUNNING, "");
				}
			}
		}
	}

	

}
