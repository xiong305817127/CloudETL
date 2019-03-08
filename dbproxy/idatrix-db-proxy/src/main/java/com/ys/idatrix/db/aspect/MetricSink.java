package com.ys.idatrix.db.aspect;

import com.ys.idatrix.metric.impl.JvmMetricsSource;
import com.ys.idatrix.metric.impl.MetricsConfiguration;
import com.ys.idatrix.metric.impl.ServiceMetricSinkImpl;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MetricSink {
	
	private ServiceMetricSinkImpl sink = null;  
    
    volatile private static MetricSink instance;
    
	private MetricSink() {
		 //执行完服务之后，上传监控信息
        JvmMetricsSource jvmMetricsSource = new JvmMetricsSource();
        MetricsConfiguration configuration = MetricsConfiguration.getMetricsConfiguration();
        //log.info("MetricsConfiguration:" + JSON.toJSONString(configuration,true));
        sink = new ServiceMetricSinkImpl();
        sink.init(configuration);
        jvmMetricsSource.init(configuration, sink);
        jvmMetricsSource.start();//上报jvm监控数据，到这里的代码都需要有
	}
	
    public static MetricSink getInstrance() {  
       if(instance != null){
       }
       else{  
           synchronized (MetricSink.class) {  
        	   instance = new MetricSink();
            }  
        }
        return instance;  
    }

	public ServiceMetricSinkImpl getSink() {
		return sink;
	}

	public void setSink(ServiceMetricSinkImpl sink) {
		this.sink = sink;
	}       
}
