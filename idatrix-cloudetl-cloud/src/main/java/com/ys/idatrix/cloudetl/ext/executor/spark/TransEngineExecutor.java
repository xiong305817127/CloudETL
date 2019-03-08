/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.ext.executor.spark;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.pentaho.di.trans.TransExecutionConfiguration;
import org.pentaho.di.trans.TransMeta;

import com.ys.idatrix.cloudetl.dto.trans.TransExecLogDto;
import com.ys.idatrix.cloudetl.dto.trans.TransExecStepMeasureDto;
import com.ys.idatrix.cloudetl.dto.trans.TransExecStepStatusDto;
import com.ys.idatrix.cloudetl.ext.executor.BaseTransExecutor;
import com.ys.idatrix.cloudetl.ext.executor.CloudExecution;
import com.ys.idatrix.cloudetl.ext.executor.CloudExecutorStatus;
import com.ys.idatrix.cloudetl.logger.CloudLogUtils;
import com.ys.idatrix.cloudetl.logger.CloudLogger;
import com.ys.idatrix.cloudetl.reference.spark.SparkEngineCaller;
import com.ys.idatrix.transengine.vo.SparkEngineCallerResp;

/**
 * Transformation execution on iDatrix trans-engine implementation,
 *   - Keeping runnable environment,
 *   - Implement execution procedure.
 * @author JW
 * @since 2017年7月7日
 *
 */
public class TransEngineExecutor extends BaseTransExecutor implements Runnable {

	private SparkEngineCaller engineCaller;

	private final int defaultLogLineNr = 0;
	
	//private final int callbackWaitingCounter = 30;
	
	private String currentStatusForLog;

	private TransEngineExecutor(TransMeta transMeta, TransExecutionConfiguration configuration, SparkEngineCaller caller, String username) {
		super(transMeta, configuration);
		
		this.engineCaller = caller;
		this.engineCaller.setUserId(username);
		
		this.currentStatusForLog = "";
	}

	public static synchronized TransEngineExecutor initExecutor(TransMeta transMeta, TransExecutionConfiguration configuration, SparkEngineCaller caller, String username) throws Exception {
		TransEngineExecutor transExecutor = new TransEngineExecutor(transMeta, configuration, caller, username);
		CloudExecution.getInstance().putExecutor( transExecutor);
		return transExecutor;
	}

	/**
	 * Runner implementation.
	 */
	@Override
	public void execTask() {
		try {
			finished = false;
			beginDate = new Date();
			endDate = new Date();
			flushExecStatusAndLog(CloudExecutorStatus.INITIALIZING, "");
			
			//SubmitSparkCallbackListenerImpl listener = engineCaller.submitTrans(transMeta);
			SparkEngineCallerResp callerResp = engineCaller.submitTrans(getTransMeta());
			logger.debug(CloudLogger.logMessage2("TransEngine.submitTrans , Caller response: ", CloudLogUtils.jsonLog(callerResp)));

			/*int counter = 0;
			while (!listener.isCallbackOK()) {
				if (counter++ > callbackWaitingCounter) {
					throw new Exception("[TransEngine] Spark引擎执行调用超时！");
				}
				
				CloudLogger.debug("[===TransEngine===] Waiting callback..." + counter);
				Thread.sleep(1000);
			}*/
			
			flushExecStatusAndLog(CloudExecutorStatus.RUNNING, "");

			if (callerResp != null && callerResp.getStatus()) {
				String curStatus = "";
				while (!finished) {
					curStatus = engineCaller.getStatus(metaClone.getName());
					setStatus(curStatus); // Set current status in executor
					logger.debug("TransEngine.getStatus,Trans execution status: " + curStatus);
					
					if (!CloudExecutorStatus.assertRunning(curStatus)) {
						finished = true;
					}
					
					Thread.sleep(1000);
				}
				
				//setStatus(CloudExecutorStatus.FINISHED);
				flushExecStatusAndLog(CloudExecutorStatus.getStatusForType(curStatus), "");
			} else {
				// Fail to call the spark engine!
				flushExecStatusAndLog(CloudExecutorStatus.COMPLETE_WITH_ERRORS, "");
			}
		} catch (Exception e) {
			flushExecStatusAndLog(CloudExecutorStatus.FAILED, CloudLogger.getExceptionMessage(e,false));
			logger.error("TransEngine执行异常！", e);
		} finally {
			finished = true;
			endDate = new Date();
		}
	}

	/* 
	 * Get transformation execution status.
	 */
	@Override
	public String execStatus() {
		return getStatus();
	}

	/* 
	 * Pause transformation execution.
	 */
	@Override
	public boolean execPause() {
		// Not supported.
		return false;
	}

	/* 
	 * Stop transformation execution.
	 */
	@Override
	public boolean execStop() {
		logger.debug("TransEngine, Stopping execution...");
		return Boolean.parseBoolean(engineCaller.stopSparkExecution(metaClone.getName()));
	}

	/* 
	 * Resume transformation execution which is paused.
	 */
	@Override
	public boolean execResume() {
		// TODO
		return false;
	}

	/* 
	 * Get step measure on transformation execution.
	 */
	@Override
	public List<TransExecStepMeasureDto> getStepMeasure() throws Exception {
		List<TransExecStepMeasureDto> dtos = engineCaller.getStepMeasure(metaClone.getName());
		logger.debug("TransEngine, Step measure: " + CloudLogUtils.jsonLog(dtos));
		return dtos;
	}

	/* 
	 * Get step status on transformation execution.
	 */
	@Override
	public List<TransExecStepStatusDto> getStepStatus() throws Exception {
		List<TransExecStepStatusDto> statusList = new ArrayList<>();

		HashMap<String, String> stepStatus = engineCaller.getStepStatus(metaClone.getName());
		stepStatus.entrySet().forEach((entry) -> {
			TransExecStepStatusDto statusDto = new TransExecStepStatusDto();
			statusDto.setStepName(entry.getKey());
			statusDto.setLogText(entry.getValue());
			statusDto.setErrCount(engineCaller.assertStepStatusFailed(entry.getValue()) ? 1 : 0);
			statusList.add(statusDto);
			logger.debug("TransEngine, Step status: " + CloudLogUtils.jsonLog(entry));
		});

		return statusList;
	}

	/* 
	 * Get transformation execution log.
	 */
	@Override
	public TransExecLogDto getExecLog() throws Exception {
		TransExecLogDto execLog = new TransExecLogDto();
		execLog.setName(taskName);

		StringBuilder sb = new StringBuilder(engineCaller.getLog(metaClone.getName(), Integer.toString(defaultLogLineNr)));
		
		if (sb.toString().isEmpty()) {
			if (!currentStatusForLog.equals(getStatus())) {
				sb.append("[TransEngine] Current trans status: ");
				sb.append(getStatus());
				currentStatusForLog = getStatus();
			}
		}
		
		execLog.setLog(sb.toString());
		logger.debug("TransEngine,Log: \n" + execLog.getLog() + "\n");

		return execLog;
	}

	@Override
	public void clear() throws Exception {
		
	}

	@Override
	public void restartServer() throws Exception {
		
	}

}
