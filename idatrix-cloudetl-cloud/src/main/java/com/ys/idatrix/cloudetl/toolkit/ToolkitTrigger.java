/**
 * 云化数据集成系统 
 * iDatrxi CloudETL
 */
package com.ys.idatrix.cloudetl.toolkit;

import java.util.Date;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.di.base.AbstractMeta;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.trans.TransMeta;

import com.ys.idatrix.cloudetl.ext.CloudSession;
import com.ys.idatrix.cloudetl.ext.executor.logger.CloudExecLog;
import com.ys.idatrix.cloudetl.logger.CloudLogType;
import com.ys.idatrix.cloudetl.logger.CloudLogger;
import com.ys.idatrix.cloudetl.reference.graph.GraphService;
import com.ys.idatrix.cloudetl.toolkit.analyzer.job.JobFlowParser;
import com.ys.idatrix.cloudetl.toolkit.analyzer.trans.TransFlowParser;
import com.ys.idatrix.cloudetl.toolkit.graph.GraphWriter;
import com.ys.idatrix.cloudetl.toolkit.record.AnalyzerHistory;
import com.ys.idatrix.cloudetl.toolkit.record.AnalyzerRecorder;
import com.ys.idatrix.cloudetl.toolkit.record.AnalyzerReporter;

/**
 * ToolkitTrigger <br/>
 * 
 * - ETL analyzer procedure: 1. Create a trigger with trans/job 2. Initialize
 * recorder and check if any current analyzer processing for the trans/job 3.
 * Initialize data decoder for the trans/job, find out all steps' input and
 * output data 4. Initialize analyzer by orders: custom -> job -> trans ->
 * script -> preview -> standard (calling next analyzer if rating < 100.0) 5.
 * Trigger combines all analyzer results (data node & relationship) per
 * type/catalog/level/flag/graph 6. Call graph writer to write data node &
 * relationship & properties into graph repository 7. Update analyzer records,
 * record analyzer logs
 * 
 * @author JW
 * @since 2018年1月8日
 * 
 */
public class ToolkitTrigger implements Runnable {

	public static final Log syslogger = LogFactory.getLog("ToolkitTrigger");

	private final String META_TYPE_TRANS = "trans";
	private final String META_TYPE_JOB = "job";

	private final String metaType;

	// The unique ID assigned to each trigger
	private final String triggerId;

	private final AbstractMeta meta;

	// Who owns this executor?
	private final String user;
	private final String name;

	private Date beginDate;
	private Date endDate;

	private boolean finished = false;

	private String status = "";

	private CloudExecLog logger;

	private AnalyzerHistory history;

	private AnalyzerRecorder recorder;

	private AnalyzerRecorder lastRecorder;

	private AnalyzerReporter reporter;

	/**
	 * Trigger constructor.
	 * 
	 * @param meta
	 *            - transMeta or jobMeta
	 * @param user
	 * @throws Exception
	 */
	public ToolkitTrigger(AbstractMeta meta, String name, String user) throws Exception {
		if (meta == null) {
			throw new Exception("The meta can not be null!");
		}

		this.triggerId = UUID.randomUUID().toString().replaceAll("-", "");
		this.meta = meta;
		this.user = user;
		this.name = Const.NVL(name, Utils.removeOwnerUserByThreadName(meta.getName()));

		if (meta instanceof TransMeta) {
			this.metaType = META_TYPE_TRANS;
		} else if (meta instanceof JobMeta) {
			this.metaType = META_TYPE_JOB;
		} else {
			this.metaType = "";
		}

		String filename = Utils.isEmpty(this.metaType) ? name : (this.metaType + "." + name);

		history = AnalyzerHistory.initAnalyzerHistory(CloudSession.getUserLogsRepositoryPath(), filename, CloudLogType.ANALYZER_HISTORY);
		lastRecorder = history.getLastAnalyzerRecord();

		if (!checkAnalyzerRecord()) {
			return ;
		}
		
		logger = CloudExecLog.initExecLog(CloudSession.getUserLogsRepositoryPath(), filename, CloudLogType.ANALYZER_LOG);
		logger.startExecLog(triggerId);

		recorder = new AnalyzerRecorder();
		recorder.setTriggerId(triggerId);
		recorder.setMetaName(name);
		recorder.setStatus(this.status);
		recorder.setUser(user);
		recorder.setBeginDate(new Date());
		recorder.setEndDate(new Date());
		recorder.setLogPath(this.logger.getFilePath());
		history.insertAnalyzerRecord(this.recorder);
		history.saveAnalyzerHistory();

		flushStatusAndLog("Initiating", "");
		flushLog("任务[" + name + "]分析器初始化完成.");

		this.reporter = new AnalyzerReporter();
	}

	/*
	 * 覆盖方法：run
	 */
	@Override
	public void run() {
		
		if(!GraphService.isAnalyzerEnable()) {
			return ;
		}
		
		if (!checkAnalyzerRecord()) {
			// flushLog("Tookit trigger step#2: No need running since no any updating  found!");
			//flushLog("任务没有变化,不需要运行分析器.停止运行");
			CloudLogger.getInstance(user).debug("任务没有变化,不需要运行分析器.结束运行");
			return;
		}

		// flushLog("Tookit trigger step#2: Be ready of running!");

		try {
			finished = false;
			beginDate = new Date();
			endDate = new Date();

			// Flushing log
			flushStatusAndLog("Running", "开始执行分析器...");

			if (META_TYPE_TRANS.equals(metaType)) {
				// Analyzer processing on transMeta
				flushLog("开始 分析转换 : " + name);
				TransFlowParser tp = new TransFlowParser((TransMeta) meta, this);
				tp.parsing(reporter);
			} else if (META_TYPE_JOB.equals(metaType)) {
				// Analyzer processing on jobMeta (TODO.)
				flushLog("开始 分析调度 : " + name);
				JobFlowParser jp = new JobFlowParser((JobMeta) meta, this);
				jp.parsing(reporter);
			} else {
				// Do nothing.
			}

			// Write analyzer results into graph database
			flushLog("准备发送数据到数据地图...");
			// NodeWriter nw = new NodeWriter(logger);
			// nw.write(reporter);
			//
			// RelationshipWriter rw = new RelationshipWriter(logger);
			// rw.write(reporter);

			GraphWriter gw = new GraphWriter(this);
			gw.writeNodeAndRelationship(reporter);

			flushStatusAndLog("Completed", "Tookit trigger has been completed.");
		} catch (Exception e) {
			// Flushing log
			flushStatusAndLog("Failed", "Tookit trigger has been failed : " + CloudLogger.getExceptionMessage(e));
			syslogger.error("处理数据地图异常:",e);

		} finally {
			finished = true;
			endDate = new Date();
			this.logger.endExecLog(triggerId);
		}
	}

	public void flushLog(String logText) {
		try {
			this.logger.insertExecLog(logText, true);
			CloudLogger.getInstance(user).debug(logText);
		} catch (Exception e) {
			syslogger.error("",e);
		}
	}

	public void flushStatusAndLog(String status, String logText) {

		// Flush analyzer log with current status
		StringBuilder sb = new StringBuilder("Toolkit status: ");
		sb.append(status);
		sb.append(Utils.isEmpty(logText) ? "" : ", " + logText);
		flushLog(sb.toString());

		// Set analyzer status & history record
		setStatus(status);
	}

	private boolean checkAnalyzerRecord() {
		if (lastRecorder != null && lastRecorder.getEndDate() != null) {
			if (lastRecorder.getEndDate().after(meta.getModifiedDate())) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @return triggerId
	 */
	public String getTriggerId() {
		return triggerId;
	}

	/**
	 * @return user
	 */
	public String getUser() {
		return user;
	}
	
	/**
	 * @return user
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return beginDate
	 */
	public Date getBeginDate() {
		return beginDate;
	}

	/**
	 * @return endDate
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * @return finished
	 */
	public boolean isFinished() {
		return finished;
	}

	/**
	 * @param finished
	 *            要设置的 finished
	 */
	public void setFinished(boolean finished) {
		this.finished = finished;
		this.endDate = new Date();
	}

	/**
	 * @return logger
	 */
	public CloudExecLog getLogger() {
		return logger;
	}

	/**
	 * @param logger
	 *            要设置的 logger
	 */
	public void setLogger(CloudExecLog logger) {
		this.logger = logger;
	}

	/**
	 * @return recorder
	 */
	public AnalyzerRecorder getRecorder() {
		return recorder;
	}

	/**
	 * @param recorder
	 *            要设置的 recorder
	 */
	public void setRecorder(AnalyzerRecorder recorder) {
		this.recorder = recorder;
	}

	/**
	 * @return reporter
	 */
	public AnalyzerReporter getReporter() {
		return reporter;
	}

	/**
	 * @param reporter
	 *            要设置的 reporter
	 */
	public void setReporter(AnalyzerReporter reporter) {
		this.reporter = reporter;
	}

	/**
	 * @return status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            要设置的 status
	 */
	public void setStatus(String status) {
		this.status = status;
		try {
			// Upgrade executor history record
			this.recorder.setStatus(status);
			this.recorder.setLogPath(this.logger.getFilePath());
			// if (!"Running".equalsIgnoreCase(status)) {
			// this.recorder.setEndDate(new Date());
			// }
			if ("Completed".equalsIgnoreCase(status) || "Failed".equalsIgnoreCase(status)) {
				this.recorder.setEndDate(new Date());
			}

			this.history.saveAnalyzerHistory();
			;
		} catch (Exception e) {
			syslogger.error("",e);
		}
	}

}
