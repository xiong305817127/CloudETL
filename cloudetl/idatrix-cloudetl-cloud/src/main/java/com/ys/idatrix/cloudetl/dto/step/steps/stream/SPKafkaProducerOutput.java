package com.ys.idatrix.cloudetl.dto.step.steps.stream;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.metrics2.sink.relocated.google.common.collect.Maps;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.util.OsgiBundleUtils;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ys.idatrix.cloudetl.toolkit.analyzer.trans.step.StepDataRelationship;
import com.ys.idatrix.cloudetl.toolkit.analyzer.trans.step.StepDataRelationshipParser;
import com.ys.idatrix.cloudetl.toolkit.domain.DataNode;
import com.ys.idatrix.cloudetl.toolkit.domain.Relationship;
import com.ys.idatrix.cloudetl.toolkit.utils.DataNodeUtil;
import com.ys.idatrix.cloudetl.toolkit.utils.RelationshipUtil;
import com.ys.idatrix.cloudetl.dto.step.StepFieldDto;
import com.ys.idatrix.cloudetl.dto.step.steps.StepParameter;
import com.ys.idatrix.cloudetl.recovery.trans.ResumeStepDataParser;
import net.sf.json.JSONObject;

/**
 * Step - KafkaProducerOutput. 转换
 * org.pentaho.big.data.kettle.plugins.kafka.KafkaProducerOutputMeta
 * 
 * @author XH
 * @since 2018-10-31
 */
@Component("SPKafkaProducerOutput")
@Scope("prototype")
public class SPKafkaProducerOutput implements StepParameter, StepDataRelationshipParser, ResumeStepDataParser {

	String connectionType;
	String directBootstrapServers;
	String clusterName;
	String topic;
	String clientId;
	String keyField;
	String messageField;
	Map<String, String> config;

	public String getConnectionType() {
		return connectionType;
	}

	public void setConnectionType(String connectionType) {
		this.connectionType = connectionType;
	}

	public String getDirectBootstrapServers() {
		return directBootstrapServers;
	}

	public void setDirectBootstrapServers(String directBootstrapServers) {
		this.directBootstrapServers = directBootstrapServers;
	}

	public String getClusterName() {
		return clusterName;
	}

	public void setClusterName(String clusterName) {
		this.clusterName = clusterName;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getKeyField() {
		return keyField;
	}

	public void setKeyField(String keyField) {
		this.keyField = keyField;
	}

	public String getMessageField() {
		return messageField;
	}

	public void setMessageField(String messageField) {
		this.messageField = messageField;
	}

	public Map<String, String> getConfig() {
		return config;
	}

	public void setConfig(Map<String, String> config) {
		this.config = config;
	}

	@Override
	public Object getParameterObject(Object json) {
		JSONObject jsonObj = JSONObject.fromObject(json);
		Map<String, Class<?>> classMap = new HashMap<>();
		classMap.put("config", LinkedHashMap.class);
		return (SPKafkaProducerOutput) JSONObject.toBean(jsonObj, SPKafkaProducerOutput.class, classMap);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object encodeParameterObject(StepMeta stepMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPKafkaProducerOutput spkafkaproduceroutput = new SPKafkaProducerOutput();
		//KafkaProducerOutputMeta kafkaproduceroutputmeta = (KafkaProducerOutputMeta) stepMetaInterface;

		//spkafkaproduceroutput.setConnectionType(kafkaproduceroutputmeta.getConnectionType().name());
		spkafkaproduceroutput.setConnectionType((String)OsgiBundleUtils.invokeOsgiMethod(OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getConnectionType"),"name"));
		//spkafkaproduceroutput.setDirectBootstrapServers(kafkaproduceroutputmeta.getDirectBootstrapServers());
		spkafkaproduceroutput.setDirectBootstrapServers((String)OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getDirectBootstrapServers"));
		//spkafkaproduceroutput.setClusterName(kafkaproduceroutputmeta.getClusterName());
		spkafkaproduceroutput.setClusterName((String)OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getClusterName"));
		//spkafkaproduceroutput.setTopic(kafkaproduceroutputmeta.getTopic());
		spkafkaproduceroutput.setTopic((String)OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getTopic"));
		//spkafkaproduceroutput.setClientId(kafkaproduceroutputmeta.getClientId());
		spkafkaproduceroutput.setClientId((String)OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getClientId"));
		//spkafkaproduceroutput.setKeyField(kafkaproduceroutputmeta.getKeyField());
		spkafkaproduceroutput.setKeyField((String)OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getKeyField"));
		//spkafkaproduceroutput.setMessageField(kafkaproduceroutputmeta.getMessageField());
		spkafkaproduceroutput.setMessageField((String)OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getMessageField"));
		//Map<String, String> configMetaDto = kafkaproduceroutputmeta.getConfig();
		Map<String, String> configMetaDto = (Map<String, String>) OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getConfig");
		if (configMetaDto != null) {
			spkafkaproduceroutput.setConfig(configMetaDto);
		}else {
			spkafkaproduceroutput.setConfig(new HashMap<>());
		}

		return spkafkaproduceroutput;
	}

	@Override
	public void decodeParameterObject(StepMeta stepMeta, Object po, List<DatabaseMeta> databases, TransMeta transMeta)
			throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPKafkaProducerOutput spkafkaproduceroutput = (SPKafkaProducerOutput) po;
		//KafkaProducerOutputMeta kafkaproduceroutputmeta = (KafkaProducerOutputMeta) stepMetaInterface;

		//kafkaproduceroutputmeta.setClientId(spkafkaproduceroutput.getClientId());
		OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "setClientId", spkafkaproduceroutput.getClientId());
		//kafkaproduceroutputmeta.setTopic(spkafkaproduceroutput.getTopic());
		OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "setTopic", spkafkaproduceroutput.getTopic());
		//kafkaproduceroutputmeta.setKeyField(spkafkaproduceroutput.getKeyField());
		OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "setKeyField", spkafkaproduceroutput.getKeyField());
		//kafkaproduceroutputmeta.setMessageField(spkafkaproduceroutput.getMessageField());
		OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "setMessageField", spkafkaproduceroutput.getMessageField());
		//kafkaproduceroutputmeta.setClusterName(spkafkaproduceroutput.getClusterName());
		OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "setClusterName", spkafkaproduceroutput.getClusterName());
		//kafkaproduceroutputmeta.setConfig(spkafkaproduceroutput.getConfig());
		if(spkafkaproduceroutput.getConfig() != null) {
			OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "setConfig",  new Object[] {spkafkaproduceroutput.getConfig() },new Class[] {Map.class} );
		}else {
			OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "setConfig", new Object[] {new LinkedHashMap<>()},new Class[] {Map.class} );
		}
		
		//kafkaproduceroutputmeta.setConnectionType(ConnectionType.valueOf(spkafkaproduceroutput.getConnectionType()));
		OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "setConnectionType", OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface.getClass().getClassLoader().loadClass("org.pentaho.big.data.kettle.plugins.kafka.KafkaProducerOutputMeta$ConnectionType"), "valueOf", spkafkaproduceroutput.getConnectionType()));
		
		//kafkaproduceroutputmeta.setDirectBootstrapServers(spkafkaproduceroutput.getDirectBootstrapServers());
		OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "setDirectBootstrapServers", spkafkaproduceroutput.getDirectBootstrapServers());

	}

	@Override
	public int stepType() {
		return 6;
	}

	@Override
	public void getStepDataAndRelationship(TransMeta transMeta, StepMeta stepMeta, StepDataRelationship sdr)
			throws Exception {
		// kafka生产者 输出消息

		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();

		String bootstrapServer = (String)OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getDirectBootstrapServers");
		String cluster = (String)OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getClusterName");
		
		String kafkaServer = Const.NVL(bootstrapServer, cluster);
		if ( Utils.isEmpty(kafkaServer)) {
			return;
		}
		
		String topic = (String)OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getTopic");
		String clientId = (String)OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getClientId");
		String keyMessage = (String)OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getMessageField");

		if( !Utils.isEmpty(keyMessage) ) {
			Map<String, StepFieldDto> fields = Maps.newHashMap();;
			StepFieldDto stepFieldDto = new StepFieldDto();
			stepFieldDto.setName(keyMessage);
			stepFieldDto.setOrigin(stepMeta.getName());
			fields.put(keyMessage, stepFieldDto);
			
			Map<String, DataNode> itemNodes = DataNodeUtil.interfaceNodeParse("KafKa", "dataInterface-" + kafkaServer +"-"+ Const.NVL(topic , clientId) , "Producer", stepMeta.getName(), fields.values()) ;
			sdr.getOutputDataNodes().addAll(itemNodes.values());
			
			// 增加 系统节点 和 流节点的关系
			String from = "转换:" + transMeta.getName() + ",步骤:" + stepMeta.getName();
			List<Relationship> rs = RelationshipUtil.outputStepRelationship(itemNodes, null,  stepMeta.getName(), from, new String[] {keyMessage}, new String[] {keyMessage});
			sdr.getDataRelationship().addAll(rs);
		}
		

	}

}
