/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.service.trans.stepdetail;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.apache.commons.lang.StringUtils;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.PartitionInfo;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.plugins.PluginInterface;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.core.plugins.StepPluginType;
import org.pentaho.di.core.util.OsgiBundleUtils;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.springframework.stereotype.Service;

import com.google.common.collect.Sets;

/**
 * ExcelInput related Detail Service
 * 
 * @author XH
 * @since 2017年6月12日
 *
 */
@SuppressWarnings("unchecked")
@Service
public class KafkaDetailService implements StepDetailService {

	@Override
	public String getStepDetailType() {
		return "KafkaConsumerInput,KafkaProducerOutput";
	}

	/**
	 * flag: getSheets , getFields
	 * 
	 * @throws Exception
	 */
	@Override
	public Object dealStepDetailByflag(String flag, Map<String, Object> param) throws Exception {
		if (StringUtils.isEmpty(flag)) {
			return null;
		}

		switch (flag) {
		case "getTopic":
			return getTopic(param);
		default:
			return null;

		}
	}

	@SuppressWarnings("rawtypes")
	private Set<String> getTopic(Map<String, Object> params) throws Exception {

		Consumer kafkaConsumer = null;
		try {

			PluginRegistry registry = PluginRegistry.getInstance();
			PluginInterface sp = registry.findPluginWithId(StepPluginType.class, "KafkaConsumerInput");
			StepMetaInterface stepMetaInterface = (StepMetaInterface) registry.loadClass(sp);
			// KafkaConsumerInputMeta kafkaConsumerInputMeta = (KafkaConsumerInputMeta)
			// stepMetaInterface;

			ClassLoader classloader = stepMetaInterface.getClass().getClassLoader();

			// ConnectionType connectionType = ConnectionType.DIRECT;
			Object connectionType = OsgiBundleUtils.invokeOsgiMethod(	classloader.loadClass("org.pentaho.big.data.kettle.plugins.kafka.KafkaConsumerInputMeta$ConnectionType"),"valueOf", "DIRECT");
			String directBootstrapServers = (String) params.get("directBootstrapServers");
			String clusterName = (String) params.get("clusterName");
			if (Utils.isEmpty(directBootstrapServers) && Utils.isEmpty(clusterName)) {
				throw new KettleException("参数[directBootstrapServers]和[clusterName]必须至少一个不为空!");
			}
			if (Utils.isEmpty(directBootstrapServers)) {
				//connectionType = ConnectionType.CLUSTER;
				connectionType = OsgiBundleUtils.invokeOsgiMethod(	classloader.loadClass("org.pentaho.big.data.kettle.plugins.kafka.KafkaConsumerInputMeta$ConnectionType"),"valueOf", "CLUSTER");
			}

			Object configP = params.get("config");
			Map<String, String> config;
			if (configP == null) {
				config = new LinkedHashMap<>();
			} else {
				config = (Map<String, String>) configP;
			}

			//KafkaFactory kafkaFactory = KafkaFactory.defaultFactory();
			Object kafkaFactory = OsgiBundleUtils.invokeOsgiMethod(classloader.loadClass("org.pentaho.big.data.kettle.plugins.kafka.KafkaFactory"), "defaultFactory");
			
			//kafkaConsumerInputMeta.setConnectionType(connectionType);
			OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface,"setConnectionType",connectionType);
			//kafkaConsumerInputMeta.setClusterName(clusterName);
			OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface,"setClusterName",clusterName);
			//kafkaConsumerInputMeta.setDirectBootstrapServers(directBootstrapServers);
			OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface,"setDirectBootstrapServers",directBootstrapServers);
			//kafkaConsumerInputMeta.setConfig(config);
			OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface,"setConfig",config);
			//kafkaConsumer = kafkaFactory.consumer(kafkaConsumerInputMeta, Function.identity());
			kafkaConsumer = (Consumer) OsgiBundleUtils.invokeOsgiMethod(kafkaFactory, "consumer", stepMetaInterface,Function.identity());
			if(kafkaConsumer != null) {
				Map<String, List<PartitionInfo>> topicMap = kafkaConsumer.listTopics();
				if (topicMap != null) {
					return topicMap.keySet();
				}
			}
			return Sets.newHashSet();
		} finally {
			if (kafkaConsumer != null) {
				kafkaConsumer.close();
			}
		}
	}

}
