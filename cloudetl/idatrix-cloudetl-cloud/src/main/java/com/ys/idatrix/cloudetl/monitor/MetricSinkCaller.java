/**
 * 云化数据集成系统 
 * iDatrxi CloudETL
 */
package com.ys.idatrix.cloudetl.monitor;

import javax.inject.Singleton;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ys.idatrix.metric.impl.JvmMetricsSource;
import com.ys.idatrix.metric.impl.MetricsConfiguration;
import com.ys.idatrix.metric.impl.ServiceMetricSinkImpl;

/**
 * SinkCaller <br/>
 * @author JW
 * @since 2017年9月25日
 * 
 */
@Singleton
public class MetricSinkCaller {
	
	public static final Log  logger = LogFactory.getLog(MetricSinkCaller.class);
	
	private ServiceMetricSinkImpl sink;
	
	private static MetricSinkCaller caller;
	
	private MetricSinkCaller() {
		JvmMetricsSource jvmMetricsSource = new JvmMetricsSource();
	    MetricsConfiguration configuration = MetricsConfiguration.getMetricsConfiguration();
	    sink = new ServiceMetricSinkImpl();
	    jvmMetricsSource.init(configuration, sink);
	    sink.init(configuration);
	    jvmMetricsSource.start(); //上报jvm监控数据，到这里的代码都需要有
	}
	
	private static MetricSinkCaller getCaller() {
		if (caller == null) {
			caller = new MetricSinkCaller();
		}
		return caller;
	}
	
	public static void publishSingleMetric(String metricName, double value) {
		logger.debug("Metric: " + metricName + ", Value: " + value);
		getCaller().sink.publishSingleMetric(metricName, value);
	}

}
