/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.reference.spark;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.trans.TransMeta;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.ys.idatrix.cloudetl.dto.trans.TransExecStepMeasureDto;
import com.ys.idatrix.cloudetl.ext.executor.CloudExecutorStatus;
import com.ys.idatrix.cloudetl.logger.CloudLogUtils;
import com.ys.idatrix.cloudetl.logger.CloudLogger;
import com.ys.idatrix.transengine.service.LogInfoService;
import com.ys.idatrix.transengine.service.StopSparkService;
import com.ys.idatrix.transengine.service.SubmitSparkCallService;
import com.ys.idatrix.transengine.vo.LogInfoVO;
import com.ys.idatrix.transengine.vo.LogQueryCallerReq;
import com.ys.idatrix.transengine.vo.LogQueryCallerResp;
import com.ys.idatrix.transengine.vo.SparkEngineCallerReq;
import com.ys.idatrix.transengine.vo.SparkEngineCallerResp;
import com.ys.idatrix.transengine.vo.StepInfo;
import com.ys.idatrix.transengine.vo.StepMeasureCallerReq;
import com.ys.idatrix.transengine.vo.StepMeasureCallerResp;
import com.ys.idatrix.transengine.vo.StopSparkRequest;
import com.ys.idatrix.transengine.vo.StopSparkResponse;

/**
 * iDatrix trans-engine caller.
 * The trans-engine will execute transformation on spark cluster.
 * @author JW
 * @since 2017年5月24日
 *
 */
@Service
public class SparkEngineCaller {

	@Reference(check=false)
	private LogInfoService logInfoService;
	@Reference(check=false)
	private SubmitSparkCallService submitSparkCallService;
	@Reference(check=false)
	private StopSparkService stopSparkService;

	private String userId = "cloudetl";
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUserId() {
		return this.userId;
	}

	/**
	 * Submit transformation execution on trans-engine.
	 * @param transMeta
	 * @return
	 */
	public SparkEngineCallerResp submitTrans(TransMeta transMeta) {
		SparkEngineCallerReq req = new SparkEngineCallerReq();
		req.setName(transMeta.getName());
		req.setUserId(userId);
		
		SparkEngineCallerResp callerResp = null;

		try {
			String content = transMeta.getXML();
			req.setContent(content);
			callerResp = submitSparkCallService.addListener(req);
			//submitSparkCallService.addListener(req, listener);
		} catch (KettleException e) {
			CloudLogger.getInstance(userId).error(" TransEngineCaller", "Submit trans exception!", e);
		}

		CloudLogger.getInstance(userId).debug("TransEngineCaller", "Submit resp: " + CloudLogUtils.jsonLog(callerResp));
		return callerResp;
	}
	
	/*public SubmitSparkCallbackListenerImpl submitTrans(TransMeta transMeta) {
		SubmitSparkCallbackListenerImpl listener = new SubmitSparkCallbackListenerImpl();

		SparkEngineCallerReq req = new SparkEngineCallerReq();
		req.setName(transMeta.getName());
		req.setUserId(userId);

		try {
			String content = transMeta.getXML();
			req.setContent(content);
			submitSparkCallService.addListener(req, listener);
		} catch (KettleException e) {
			e.printStackTrace();
		}

		return listener;
	}*/

	/**
	 * Get log for the transformation being executed on trans-engine.
	 * @param transName
	 * @param logLineNr
	 * @return
	 */
	public String getLog(String transName, String logLineNr) {
		StringBuilder sb = new StringBuilder();

		LogQueryCallerReq req = new LogQueryCallerReq();
		req.setUserId(userId);
		req.setName(transName);
		req.setLine(logLineNr);

		LogQueryCallerResp resp = logInfoService.getLogInfoByKettleName(req);
		if (resp != null) {
			List<LogInfoVO> logs = resp.getVo();
			if (logs != null) {
				for (LogInfoVO log : logs) {
					sb.append("[").append(log.getLogDate()).append("][").append(log.getLogStepName())
					.append("][").append(log.getLogName()).append("]").append(log.getLogMsg());
				}
			}
		}

		CloudLogger.getInstance(userId).debug("TransEngineCaller"," Log Resp: " + sb.toString());
		return sb.toString();
	}

	/**
	 * Get measure of all steps for the transformation being executed on trans-engine.
	 * @param transName
	 * @return
	 */
	public List<TransExecStepMeasureDto> getStepMeasure(String transName) {
		List<TransExecStepMeasureDto> measureList = new ArrayList<>();

		StepMeasureCallerReq req = new StepMeasureCallerReq();
		req.setUserId(userId);
		req.setName(transName);

		StepMeasureCallerResp resp = logInfoService.getMeasureStepLogInfo(req);
		if (resp != null) {
			List<StepInfo> logs = resp.getSteps();
			if (logs != null) {
				for (StepInfo log : logs) {
					CloudLogger.getInstance(userId).debug("TransEngineCaller","Step Measure Resp: " + CloudLogUtils.jsonLog(log));

					TransExecStepMeasureDto measure = new TransExecStepMeasureDto();
					measure.setCopy(log.getCopy());
					measure.setErrors(log.getErrors());
					measure.setLinesInput(log.getLinesInput());
					measure.setLinesOutput(log.getLinesOutput());
					measure.setLinesRead(log.getLinesRead());
					measure.setLinesRejected(log.getLinesRejected());
					measure.setLinesUpdated(log.getLinesUpdated());
					measure.setLinesWritten(log.getLinesWritten());
					measure.setPriority(log.getPriority());
					measure.setSeconds(log.getSeconds());
					measure.setSpeed(log.getSpeed());
					measure.setStatusDescription(log.getStatusDescription());
					measure.setStepName(log.getStepName());
					measureList.add(measure);
				}
			}
		}

		return measureList;
	}

	/**
	 * Get status of all steps for the transformation being executed on trans-engine.<br/>
	 * Status Code:<br/>
	 * 0-Waiting<br/>
	 * 1-Failed<br/>
	 * 2-Completed<br/>
	 * 3-Completed with errors<br/>
	 * 4-Stopped<br/>
	 * 5-Paused<br/>
	 * 6-Running<br/>
	 * 7-Initializing<br/>
	 * 8-Timeout<br/>
	 * 9-Unknown<br/>
	 * @param transName
	 * @return
	 */
	public HashMap<String, String> getStepStatus(String transName) {
		HashMap<String, String> stepStatus = new HashMap<>();

		StepMeasureCallerReq req = new StepMeasureCallerReq();
		req.setUserId(userId);
		req.setName(transName);

		StepMeasureCallerResp resp = logInfoService.getMeasureStepLogInfo(req);
		if (resp != null) {
			List<StepInfo> logs = resp.getSteps();
			if (logs != null) {
				for (StepInfo log : logs) {
					CloudLogger.getInstance(userId).debug("TransEngineCaller", "Step Status Resp: " + log.getStepStatus());
					stepStatus.put(log.getStepName(), log.getStepStatus());
				}
			}
		}

		return stepStatus;
	}

	/**
	 * Get status the transformation being executed on trans-engine.<br/>
	 * Status Code:<br/>
	 * 0-Waiting<br/>
	 * 1-Failed<br/>
	 * 2-Completed<br/>
	 * 3-Completed with errors<br/>
	 * 4-Stopped<br/>
	 * 5-Paused<br/>
	 * 6-Running<br/>
	 * 7-Initializing<br/>
	 * 8-Timeout<br/>
	 * 9-Unknown<br/>
	 * @param transName
	 * @return
	 */
	public String getStatus(String transName) {
		StepMeasureCallerReq req = new StepMeasureCallerReq();
		req.setUserId(userId);
		req.setName(transName);

		String status = "";
		StepMeasureCallerResp resp = logInfoService.getMeasureStepLogInfo(req);
		if (resp != null) {
			status = resp.getStatus();
			CloudLogger.getInstance(userId).debug("TransEngineCaller", "Trans Status Resp: (" + status + ")" + getStatusType(status).getType());
		}

		return getStatusType(status).getType();
	}

	public CloudExecutorStatus getStatusType(String status) {
		if (status == null)
			return CloudExecutorStatus.UNKNOWN;
		
		switch (status) {
		case "0":
			return CloudExecutorStatus.WAITING;
		case "1":
			return CloudExecutorStatus.FAILED;
		case "2":
			return CloudExecutorStatus.COMPLETED;
		case "3":
			return CloudExecutorStatus.COMPLETE_WITH_ERRORS;
		case "4":
			return CloudExecutorStatus.STOPPED;
		case "5":
			return CloudExecutorStatus.PAUSED;
		case "6":
			return CloudExecutorStatus.RUNNING;
		case "7":
			return CloudExecutorStatus.INITIALIZING;
		case "8":
			return CloudExecutorStatus.TIMEOUT;
		case "9":
			return CloudExecutorStatus.UNKNOWN;
		default:
			return CloudExecutorStatus.UNKNOWN;
		}
	}
	
	public boolean assertStepStatusFailed(String status) {
		if (status == null)
			return false; // !!!
		
		switch (status) {
		case "1":
			//CloudExecutorStatus.FAILED;
		case "3":
			//CloudExecutorStatus.COMPLETE_WITH_ERRORS;
		//case "4":
			//CloudExecutorStatus.STOPPED;
		case "8":
			//CloudExecutorStatus.TIMEOUT;
		case "9":
			//CloudExecutorStatus.UNKNOWN;
			return true;
		}
		
		return false;
	}

	/**
	 * Stop the transformation execution on trans-engine.
	 * The stop action will just kill the execution on spark.
	 * @param transName
	 * @return
	 */
	public String stopSparkExecution(String transName) {
		StopSparkRequest req = new StopSparkRequest();
		req.setUserId(userId);
		req.setName(transName);

		StopSparkResponse resp = stopSparkService.killApplicationByName(req);
		if (resp != null) {
			CloudLogger.getInstance(userId).debug("TransEngineCaller"," Stopping Execution Resp: " + resp.getStatus());
			return resp.getStatus();
		}

		return "false";
	}

}
