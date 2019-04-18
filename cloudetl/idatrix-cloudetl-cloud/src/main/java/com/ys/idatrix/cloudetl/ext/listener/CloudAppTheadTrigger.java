package com.ys.idatrix.cloudetl.ext.listener;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.di.core.util.IdatrixPropertyUtil;
import org.pentaho.di.core.util.Utils;
import com.ys.idatrix.cloudetl.ext.executor.CloudExecution;
import com.ys.idatrix.cloudetl.logger.CloudLogger;
import com.ys.idatrix.cloudetl.monitor.CloudReporter;
import com.ys.idatrix.cloudetl.recovery.restart.RestartScanner;
import com.ys.idatrix.cloudetl.recovery.trans.RemoteResumeListener;
import com.ys.idatrix.cloudetl.recovery.trans.ResumeTransParser;
import com.ys.idatrix.cloudetl.subcribe.SubcribeFailRetryListener;

public class CloudAppTheadTrigger {

	public static final Log logger = LogFactory.getLog("CloudAppTheadTrigger");
	private static CloudAppTheadTrigger appThread;
	
	public static CloudAppTheadTrigger getInstance() {
		if (appThread == null) {
			synchronized (CloudAppTheadTrigger.class) {
				if (appThread == null) {
					appThread = new CloudAppTheadTrigger();
				}
			}
		}
		return appThread;
	}
	
	public static void startTrigger() {
		
		boolean isMaster = IdatrixPropertyUtil.getBooleanProperty( "idatrix.web.deployment", false) ;
		
		// Trigger monitor metrics reporter
		if (isMaster && "true".equalsIgnoreCase(IdatrixPropertyUtil.getProperty("idatrix.metrics.reporter"))) {
			getInstance().triggerMetricsReporter();
		}
		
		//Reboot the job and trans in the pre restart run after the service restarts
		if(isMaster && "true".equalsIgnoreCase(IdatrixPropertyUtil.getProperty("idatrix.restart.transjob"))) {
			getInstance().triggerRestartJobAndTrans();
		}
		//当是远程服务器并且缓存可用时,启动断点恢复监听进程
		if( !isMaster && ResumeTransParser.isResumeEnable() ) {
			getInstance().triggerRemoteResumeListener();
		}
		
		//当是主服务(因为使用redis,从服务不用启动,否则会重复)并且订阅推送失败重推可用并且dubbo可用
		if( isMaster  && IdatrixPropertyUtil.getBooleanProperty( "dubbo.deployment", false) 
				&& IdatrixPropertyUtil.getBooleanProperty( "idatrix.subcribe.push.fail.retry.enable", true)) {
			getInstance().triggerSubcribePushRetryListener();
		}
		
		//当是远程服务器并且缓存可用时,启动断点恢复监听进程
		if( IdatrixPropertyUtil.getBooleanProperty("idatrix.executions.clear.enable" , true) ) {
			getInstance().triggerClearExecutorListener();
		}
		
	}
	
	
	// Trigger metrics reporter
	private CloudReporter reporter;

	public CloudReporter getReporter() {
		return this.reporter;
	}

	public void closeMetricsReporter() {
		if (reporter != null) {
			reporter.setOff(true);
			reporter = null;
		}
	}

	public void openMetricsReporter() {
		if (reporter == null) {
			triggerMetricsReporter();
		}
	}

	public void triggerMetricsReporter() {
		logger.info("启动运维报告器...");
		reporter = CloudReporter.initReporter();
		reporter.setOff(false);

		// 分钟
		int interval = 60;
		try {
			interval = Integer.parseInt(IdatrixPropertyUtil.getProperty("idatrix.metrics.reporter.interval", "60"));
		} catch (NumberFormatException ex) {
			// by default.
		}
		reporter.setInterval(interval * 60 * 1000);

		Thread tr = new Thread(reporter, "MetricsReporter_" + reporter.getExecutionId());
		tr.start();
	}

	public void triggerRestartJobAndTrans() {
		logger.info("启动转换服务重启自动运行监听...");
		RestartScanner scanner = RestartScanner.initScanner();
		Thread tr = new Thread(scanner, "restart_job_and_trans");
		tr.start();
	}

	public void triggerRemoteResumeListener() {
		logger.info("启动转换远程恢复监听...");
		RemoteResumeListener listener = RemoteResumeListener.getInstance();
		Thread tr = new Thread(listener, "listener_trans_breakpoient");
		tr.start();
	}

	public void triggerSubcribePushRetryListener() {
		logger.info("启动订阅推送失败重新推送监听...");
		SubcribeFailRetryListener listener = SubcribeFailRetryListener.getInstance();
		Thread tr = new Thread(listener, "listener_subcribe_push_retry");
		tr.start();
	}

	public void triggerClearExecutorListener() {
		logger.info("启动已停止执行器定时清理监听...");
		long intervalInSeconds = Long.valueOf(IdatrixPropertyUtil.getProperty("idatrix.executions.clear.interval", "600"));
		final ScheduledExecutorService clearListener = Executors.newSingleThreadScheduledExecutor();
		clearListener.scheduleAtFixedRate(new Runnable() {
			public void run() {
				try {
					String clearStr = CloudExecution.getInstance().ClearExecutorListener();
					if (!Utils.isEmpty(clearStr)) {
						logger.info("清理执行器成功:" + clearStr);
					}
				} catch (Exception e) {
					logger.info("清理已停止执行器失败:" + CloudLogger.getExceptionMessage(e));
				}
			}
		}, intervalInSeconds /* initial delay */, intervalInSeconds /* interval delay */, TimeUnit.SECONDS);
	}
	
}
