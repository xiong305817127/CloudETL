/**
 * 云化数据集成系统 
 * iDatrxi CloudETL
 */
package com.ys.idatrix.cloudetl.monitor;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.Executors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.di.core.util.IdatrixPropertyUtil;
import org.pentaho.di.core.util.Utils;

import com.alibaba.fastjson.JSONObject;
import com.ys.idatrix.cloudetl.dto.statistics.ExecTaskTimesTotal;

/**
 * CloudMetrics <br/>
 * 
 * @author JW
 * @since 2017年9月26日
 * 
 */
public class CloudMetrics {
	
	public static final Log  logger = LogFactory.getLog("CloudMetrics");

	// Trans Counter Metrics

	public static void metricTransCounterTotal() {
		MetricSinkCaller.publishSingleMetric("trans_current_counter_total", CloudTransMetrics.transTotalCounter());
	}

	public static void metricTransCounter() {
		ExecTaskTimesTotal counter = CloudTransMetrics.transCounter() ;
		if( counter != null ) {
			MetricSinkCaller.publishSingleMetric("trans_current_counter_error", counter.getFailTotal());
			MetricSinkCaller.publishSingleMetric("trans_current_counter_running", counter.getRunningTotal());
			MetricSinkCaller.publishSingleMetric("trans_current_counter_success",counter.getSuccessTotal());
		}
	}

	// Jobs Counter Metrics

	public static void metricJobCounterTotal() {
		MetricSinkCaller.publishSingleMetric("job_current_counter_total", CloudJobMetrics.jobTotalCounter());
	}

	public static void metricJobCounter() {
		
		ExecTaskTimesTotal counter = CloudJobMetrics.jobCounter() ;
		if( counter != null ) {
			MetricSinkCaller.publishSingleMetric("job_current_counter_error", counter.getFailTotal());
			MetricSinkCaller.publishSingleMetric("job_current_counter_running", counter.getRunningTotal());
			MetricSinkCaller.publishSingleMetric("job_current_counter_success",counter.getSuccessTotal());
		}
	}

	// Server Counter Metrics

	public static void metricServerCounterTotal() {
		MetricSinkCaller.publishSingleMetric("server_current_counter_total", CloudServerMetrics.serverTotalCounter());
	}

	public static void metricServerCounterError() {
		MetricSinkCaller.publishSingleMetric("server_current_counter_error", CloudServerMetrics.serverErrorCounter());
	}

	
	public static void elkServerTaskReport(String taskName,Map<String,Object> infos) {
		if(Utils.isEmpty(taskName) || infos == null || infos.isEmpty()) {
			//没有数据
			return ;
		}
		
		boolean elkServerEnable = IdatrixPropertyUtil.getBooleanProperty("idatrix.elk.server.enable",true);
		String elkServerIp = IdatrixPropertyUtil.getProperty("idatrix.elk.server.ip");
		String elkServerPort = IdatrixPropertyUtil.getProperty("idatrix.elk.server.port");
		if( !elkServerEnable || Utils.isEmpty(elkServerIp) || Utils.isEmpty(elkServerPort) ) {
			//没有配置
			return ;
		}
		
		Executors.newSingleThreadExecutor().execute(new Runnable() {
			
			@Override
			public void run() {
				Socket socket = null ;
				PrintWriter os = null ;
				try {
					socket = new Socket(elkServerIp, Integer.valueOf(elkServerPort));
					os = new PrintWriter(socket.getOutputStream());
					os.println(JSONObject.toJSON(infos));
					os.flush();

				} catch (Exception e) {
					logger.error("任务["+taskName+"]运行信息推送到ELK失败.",e);
				}finally {
					if(os != null ) {
						os.close(); // 关闭Socket输出流
					}
					if(socket != null) {
						try {
							socket.close();
						} catch (IOException e) {
							logger.error("任务["+taskName+"]运行信息推送socket关闭失败.", e);
						} // 关闭Socket
					}
				}
			}
		});
		
	}

}
