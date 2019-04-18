/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.quality.monitor;

import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.di.core.util.Utils;

import com.ys.idatrix.quality.ext.CloudApp;
import com.ys.idatrix.quality.ext.CloudSession;

/**
 * Metrics reporters.
 * @author JW
 * @since 2017年5月24日
 *
 */
public class CloudReporter implements Runnable {
	
	public static final Log  logger = LogFactory.getLog(CloudReporter.class);
	
	private boolean off = false;
	private int interval = 30000;

	private final String executionId;
	public String getExecutionId() {
		return executionId;
	}
	
	private CloudReporter() {
		off = false;
		executionId = UUID.randomUUID().toString().replaceAll("-", "");
	}
	
	public static synchronized CloudReporter initReporter() {
		return new CloudReporter();
	}
	
	public void initUserId() {
		String u = CloudSession.getLoginUser();
		if(Utils.isEmpty(u)) {
			CloudSession.setThreadLoginUser(CloudApp.defaut_userId);
		}
	}

	/*
	 * 覆盖方法：run
	 */
	@Override
	public void run() {
		logger.info("The metrics reporter is starting...");
		initUserId();
		
		while (!off) {
			try {
				// Reporting transformation metrics
				CloudMetrics.metricTransCounterTotal();
				CloudMetrics.metricTransCounter();
				
				// Reporting job metrics
				CloudMetrics.metricJobCounterTotal();
				CloudMetrics.metricJobCounter();
				
				// Reporting server metrics
				CloudMetrics.metricServerCounterTotal();
				CloudMetrics.metricServerCounterError();
				
				Thread.sleep(interval);
			} catch (InterruptedException e) {
				logger.error("metrics 上报失败.",e);
			}
		}
		//清理线程用户信息
		CloudSession.clearThreadInfo();
		
		logger.info("The metrics reporter has been stopped.");
	}

	/**
	 * @return off
	 */
	public boolean isOff() {
		return off;
	}

	/**
	 * @param off 要设置的 off
	 */
	public void setOff(boolean off) {
		this.off = off;
	}
	
	/**
	 * @return interval
	 */
	public int getInterval() {
		return interval;
	}

	/**
	 * @param interval 要设置的 interval
	 */
	public void setInterval(int interval) {
		this.interval = interval;
	}

}
