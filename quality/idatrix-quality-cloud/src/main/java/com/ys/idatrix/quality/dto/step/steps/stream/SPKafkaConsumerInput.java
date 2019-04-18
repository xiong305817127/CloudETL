package com.ys.idatrix.quality.dto.step.steps.stream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.pentaho.di.core.ObjectLocationSpecificationMethod;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.util.OsgiBundleUtils;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import com.ys.idatrix.quality.toolkit.analyzer.trans.step.StepDataRelationship;
import com.ys.idatrix.quality.toolkit.analyzer.trans.step.StepDataRelationshipParser;
import com.ys.idatrix.quality.dto.step.parts.KafkaConsumerFieldDto;
import com.ys.idatrix.quality.dto.step.steps.StepParameter;
import com.ys.idatrix.quality.recovery.trans.ResumeStepDataParser;
import com.ys.idatrix.quality.repository.CloudRepository;

import net.sf.json.JSONObject;

/**
 * Step - KafkaConsumerInput. 转换
 * org.pentaho.big.data.kettle.plugins.kafka.KafkaConsumerInputMeta
 * 
 * @author XH
 * @since 2018-10-31
 */
@Component("SPKafkaConsumerInput")
@Scope("prototype")
public class SPKafkaConsumerInput implements StepParameter, StepDataRelationshipParser, ResumeStepDataParser {

	String clusterName;
	ArrayList<String> topics;

	String transformationPath;
	String group ;
	
	String consumerGroup;
	String batchSize;
	String batchDuration;
	String connectionType;
	String directBootstrapServers;

	KafkaConsumerFieldDto keyField;
	KafkaConsumerFieldDto messageField;
	KafkaConsumerFieldDto topicField;
	KafkaConsumerFieldDto offsetField;
	KafkaConsumerFieldDto partitionField;
	KafkaConsumerFieldDto timestampField;

	Map<String, String> config;

	public String getClusterName() {
		return clusterName;
	}

	public void setClusterName(String clusterName) {
		this.clusterName = clusterName;
	}

	public ArrayList<String> getTopics() {
		return topics;
	}

	public void setTopics(List<String> topics) {
		this.topics =new  ArrayList<String>(topics);
	}

	public String getConsumerGroup() {
		return consumerGroup;
	}

	public void setConsumerGroup(String consumerGroup) {
		this.consumerGroup = consumerGroup;
	}

	public String getTransformationPath() {
		return transformationPath;
	}

	public void setTransformationPath(String transformationPath) {
		this.transformationPath = transformationPath;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public void setTopics(ArrayList<String> topics) {
		this.topics = topics;
	}

	public String getBatchSize() {
		return batchSize;
	}

	public void setBatchSize(String batchSize) {
		this.batchSize = batchSize;
	}

	public String getBatchDuration() {
		return batchDuration;
	}

	public void setBatchDuration(String batchDuration) {
		this.batchDuration = batchDuration;
	}

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

	public KafkaConsumerFieldDto getKeyField() {
		return keyField;
	}

	public void setKeyField(KafkaConsumerFieldDto keyField) {
		this.keyField = keyField;
	}

	public KafkaConsumerFieldDto getMessageField() {
		return messageField;
	}

	public void setMessageField(KafkaConsumerFieldDto messageField) {
		this.messageField = messageField;
	}

	public KafkaConsumerFieldDto getTopicField() {
		return topicField;
	}

	public void setTopicField(KafkaConsumerFieldDto topicField) {
		this.topicField = topicField;
	}

	public KafkaConsumerFieldDto getOffsetField() {
		return offsetField;
	}

	public void setOffsetField(KafkaConsumerFieldDto offsetField) {
		this.offsetField = offsetField;
	}

	public KafkaConsumerFieldDto getPartitionField() {
		return partitionField;
	}

	public void setPartitionField(KafkaConsumerFieldDto partitionField) {
		this.partitionField = partitionField;
	}

	public KafkaConsumerFieldDto getTimestampField() {
		return timestampField;
	}

	public void setTimestampField(KafkaConsumerFieldDto timestampField) {
		this.timestampField = timestampField;
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
		classMap.put("keyField", KafkaConsumerFieldDto.class);
		classMap.put("messageField", KafkaConsumerFieldDto.class);
		classMap.put("topicField", KafkaConsumerFieldDto.class);
		classMap.put("offsetField", KafkaConsumerFieldDto.class);
		classMap.put("partitionField", KafkaConsumerFieldDto.class);
		classMap.put("timestampField", KafkaConsumerFieldDto.class);
		classMap.put("config", HashMap.class);
		return (SPKafkaConsumerInput) JSONObject.toBean(jsonObj, SPKafkaConsumerInput.class, classMap);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object encodeParameterObject(StepMeta stepMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPKafkaConsumerInput spkafkaconsumerinput = new SPKafkaConsumerInput();
		//KafkaConsumerInputMeta kafkaconsumerinputmeta = (KafkaConsumerInputMeta) stepMetaInterface;

		//spkafkaconsumerinput.setClusterName(kafkaconsumerinputmeta.getClusterName());
		spkafkaconsumerinput.setClusterName((String)OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getClusterName"));
		//spkafkaconsumerinput.setTopics(kafkaconsumerinputmeta.getTopics());
		spkafkaconsumerinput.setTopics((List<String>)OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getTopics"));

		//spkafkaconsumerinput.setConsumerGroup(kafkaconsumerinputmeta.getConsumerGroup());
		spkafkaconsumerinput.setConsumerGroup((String)OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getConsumerGroup"));
		
		//spkafkaconsumerinput.setTransformationPath(kafkaconsumerinputmeta.getTransformationPath());
		String transPath = (String)OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getTransformationPath");
		if(!Utils.isEmpty(transPath) ) {
			spkafkaconsumerinput.setTransformationPath( getToAttribute(stepMeta, "IDATRIX_TRANS_NAME"));
			spkafkaconsumerinput.setGroup( getToAttribute(stepMeta, "IDATRIX_TRANS_GROUP_NAME"));
		}
		
		
		//spkafkaconsumerinput.setBatchSize(kafkaconsumerinputmeta.getBatchSize());
		spkafkaconsumerinput.setBatchSize((String)OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getBatchSize"));
		//spkafkaconsumerinput.setBatchDuration(kafkaconsumerinputmeta.getBatchDuration());
		spkafkaconsumerinput.setBatchDuration((String)OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getBatchDuration"));
		//spkafkaconsumerinput.setConnectionType(kafkaconsumerinputmeta.getConnectionType().name());
		spkafkaconsumerinput.setConnectionType((String)OsgiBundleUtils.invokeOsgiMethod(OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getConnectionType"),"name"));
		//spkafkaconsumerinput.setDirectBootstrapServers(kafkaconsumerinputmeta.getDirectBootstrapServers());
		spkafkaconsumerinput.setDirectBootstrapServers((String)OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getDirectBootstrapServers"));

		KafkaConsumerFieldDto keyFieldDto = new KafkaConsumerFieldDto();
		//KafkaConsumerField keyFieldMetaDto = kafkaconsumerinputmeta.getKeyField();
		Object keyFieldMetaDto = OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getKeyField");
		if (keyFieldMetaDto != null) {
			//keyFieldDto.setKafkaName(keyFieldMetaDto.getKafkaName().toString());
			keyFieldDto.setKafkaName((String)OsgiBundleUtils.invokeOsgiMethod(OsgiBundleUtils.invokeOsgiMethod(keyFieldMetaDto, "getKafkaName"),"toString"));
			//keyFieldDto.setOutputName(keyFieldMetaDto.getOutputName());
			keyFieldDto.setOutputName((String)OsgiBundleUtils.invokeOsgiMethod(keyFieldMetaDto, "getOutputName"));
			//keyFieldDto.setOutputType(keyFieldMetaDto.getOutputType().toString());
			keyFieldDto.setOutputType((String)OsgiBundleUtils.invokeOsgiMethod(OsgiBundleUtils.invokeOsgiMethod(keyFieldMetaDto, "getOutputType"),"toString"));

		}
		spkafkaconsumerinput.setKeyField(keyFieldDto);
		KafkaConsumerFieldDto messageFieldDto = new KafkaConsumerFieldDto();
		//KafkaConsumerField messageFieldMetaDto = kafkaconsumerinputmeta.getMessageField();
		Object messageFieldMetaDto = OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getMessageField");
		if (messageFieldMetaDto != null) {
			//messageFieldDto.setKafkaName(messageFieldMetaDto.getKafkaName().toString());
			messageFieldDto.setKafkaName((String)OsgiBundleUtils.invokeOsgiMethod(OsgiBundleUtils.invokeOsgiMethod(messageFieldMetaDto, "getKafkaName"),"toString"));
			//messageFieldDto.setOutputName(messageFieldMetaDto.getOutputName());
			messageFieldDto.setOutputName((String)OsgiBundleUtils.invokeOsgiMethod(messageFieldMetaDto, "getOutputName"));
			//messageFieldDto.setOutputType(messageFieldMetaDto.getOutputType().toString());
			messageFieldDto.setOutputType((String)OsgiBundleUtils.invokeOsgiMethod(OsgiBundleUtils.invokeOsgiMethod(messageFieldMetaDto, "getOutputType"),"toString"));
		}
		spkafkaconsumerinput.setMessageField(messageFieldDto);
		KafkaConsumerFieldDto topicFieldDto = new KafkaConsumerFieldDto();
		//KafkaConsumerField topicFieldMetaDto = kafkaconsumerinputmeta.getTopicField();
		Object topicFieldMetaDto = OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getTopicField");
		if (topicFieldMetaDto != null) {
			//topicFieldDto.setKafkaName(topicFieldMetaDto.getKafkaName().toString());
			topicFieldDto.setKafkaName((String)OsgiBundleUtils.invokeOsgiMethod(OsgiBundleUtils.invokeOsgiMethod(topicFieldMetaDto, "getKafkaName"),"toString"));
			//topicFieldDto.setOutputName(topicFieldMetaDto.getOutputName());
			topicFieldDto.setOutputName((String)OsgiBundleUtils.invokeOsgiMethod(topicFieldMetaDto, "getOutputName"));
			//topicFieldDto.setOutputType(topicFieldMetaDto.getOutputType().toString());
			topicFieldDto.setOutputType((String)OsgiBundleUtils.invokeOsgiMethod(OsgiBundleUtils.invokeOsgiMethod(topicFieldMetaDto, "getOutputType"),"toString"));
		}
		spkafkaconsumerinput.setTopicField(topicFieldDto);
		KafkaConsumerFieldDto offsetFieldDto = new KafkaConsumerFieldDto();
		//KafkaConsumerField offsetFieldMetaDto = kafkaconsumerinputmeta.getOffsetField();
		Object offsetFieldMetaDto = OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getOffsetField");
		if (offsetFieldMetaDto != null) {
			//offsetFieldDto.setKafkaName(offsetFieldMetaDto.getKafkaName().toString());
			offsetFieldDto.setKafkaName((String)OsgiBundleUtils.invokeOsgiMethod(OsgiBundleUtils.invokeOsgiMethod(offsetFieldMetaDto, "getKafkaName"),"toString"));
			//offsetFieldDto.setOutputName(offsetFieldMetaDto.getOutputName());
			offsetFieldDto.setOutputName((String)OsgiBundleUtils.invokeOsgiMethod(offsetFieldMetaDto, "getOutputName"));
			//offsetFieldDto.setOutputType(offsetFieldMetaDto.getOutputType().toString());
			offsetFieldDto.setOutputType((String)OsgiBundleUtils.invokeOsgiMethod(OsgiBundleUtils.invokeOsgiMethod(offsetFieldMetaDto, "getOutputType"),"toString"));
		}
		spkafkaconsumerinput.setOffsetField(offsetFieldDto);
		KafkaConsumerFieldDto partitionFieldDto = new KafkaConsumerFieldDto();
		//KafkaConsumerField partitionFieldMetaDto = kafkaconsumerinputmeta.getPartitionField();
		Object partitionFieldMetaDto = OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getPartitionField");
		if (partitionFieldMetaDto != null) {
			//partitionFieldDto.setKafkaName(partitionFieldMetaDto.getKafkaName().toString());
			partitionFieldDto.setKafkaName((String)OsgiBundleUtils.invokeOsgiMethod(OsgiBundleUtils.invokeOsgiMethod(partitionFieldMetaDto, "getKafkaName"),"toString"));
			//partitionFieldDto.setOutputName(partitionFieldMetaDto.getOutputName());
			partitionFieldDto.setOutputName((String)OsgiBundleUtils.invokeOsgiMethod(partitionFieldMetaDto, "getOutputName"));
			//partitionFieldDto.setOutputType(partitionFieldMetaDto.getOutputType().toString());
			partitionFieldDto.setOutputType((String)OsgiBundleUtils.invokeOsgiMethod(OsgiBundleUtils.invokeOsgiMethod(partitionFieldMetaDto, "getOutputType"),"toString"));
		}
		spkafkaconsumerinput.setPartitionField(partitionFieldDto);
		KafkaConsumerFieldDto timestampFieldDto = new KafkaConsumerFieldDto();
		//KafkaConsumerField timestampFieldMetaDto = kafkaconsumerinputmeta.getTimestampField();
		Object timestampFieldMetaDto = OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getTimestampField");
		if (timestampFieldMetaDto != null) {
			//timestampFieldDto.setKafkaName(timestampFieldMetaDto.getKafkaName().toString());
			timestampFieldDto.setKafkaName((String)OsgiBundleUtils.invokeOsgiMethod(OsgiBundleUtils.invokeOsgiMethod(timestampFieldMetaDto, "getKafkaName"),"toString"));
			//timestampFieldDto.setOutputName(timestampFieldMetaDto.getOutputName());
			timestampFieldDto.setOutputName((String)OsgiBundleUtils.invokeOsgiMethod(timestampFieldMetaDto, "getOutputName"));
			//timestampFieldDto.setOutputType(timestampFieldMetaDto.getOutputType().toString());
			timestampFieldDto.setOutputType((String)OsgiBundleUtils.invokeOsgiMethod(OsgiBundleUtils.invokeOsgiMethod(timestampFieldMetaDto, "getOutputType"),"toString"));
		}
		spkafkaconsumerinput.setTimestampField(timestampFieldDto);
		
		//Map<String, String> configMetaDto = kafkaconsumerinputmeta.getConfig();
		Map<String, String> configMetaDto = (Map<String, String>) OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getConfig");
		if (configMetaDto != null) {
			spkafkaconsumerinput.setConfig(configMetaDto);
		}else {
			spkafkaconsumerinput.setConfig(new HashMap<String, String>());
		}
		
		return spkafkaconsumerinput;
	}

	@Override
	public void decodeParameterObject(StepMeta stepMeta, Object po, List<DatabaseMeta> databases, TransMeta transMeta)
			throws Exception {
		
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPKafkaConsumerInput spkafkaconsumerinput= (SPKafkaConsumerInput)po;
		//KafkaConsumerInputMeta  kafkaconsumerinputmeta= (KafkaConsumerInputMeta )stepMetaInterface;
		
		
		//kafkaconsumerinputmeta.setClusterName(spkafkaconsumerinput.getClusterName());
		OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "setClusterName", spkafkaconsumerinput.getClusterName());
		//kafkaconsumerinputmeta.setTopics(spkafkaconsumerinput.getTopics());
		OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "setTopics", spkafkaconsumerinput.getTopics());
		
		//kafkaconsumerinputmeta.setConsumerGroup(spkafkaconsumerinput.getConsumerGroup());
		OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "setConsumerGroup", spkafkaconsumerinput.getConsumerGroup());
		//kafkaconsumerinputmeta.setBatchSize(spkafkaconsumerinput.getBatchSize());
		OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "setBatchSize", spkafkaconsumerinput.getBatchSize());
		//kafkaconsumerinputmeta.setBatchDuration(spkafkaconsumerinput.getBatchDuration());
		OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "setBatchDuration", spkafkaconsumerinput.getBatchDuration());
		//kafkaconsumerinputmeta.setTransformationPath(spkafkaconsumerinput.getTransformationPath());
		
		String transName = spkafkaconsumerinput.getTransformationPath() ;
		if( !Utils.isEmpty(transName) ) {
			
			setToAttribute(stepMeta, "IDATRIX_TRANS_NAME",transName );
			setToAttribute(stepMeta, "IDATRIX_TRANS_GROUP_NAME", getGroup() );
			
			Object transObjectId = CloudRepository.getTransObjectId(null, transName, null);
			if (transObjectId != null) {
				OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "setSpecificationMethod",  ObjectLocationSpecificationMethod.REPOSITORY_BY_REFERENCE);
				OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "setTransformationPath", transObjectId.toString() );
			}else {
				OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "setTransformationPath", transName );
			}
		}

		//kafkaconsumerinputmeta.setDirectBootstrapServers(spkafkaconsumerinput.getDirectBootstrapServers());
		OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "setDirectBootstrapServers", spkafkaconsumerinput.getDirectBootstrapServers());
		//kafkaconsumerinputmeta.setConnectionType(ConnectionType.valueOf(spkafkaconsumerinput.getConnectionType()));
		ClassLoader classLoader = stepMetaInterface.getClass().getClassLoader();
		Object connectType = OsgiBundleUtils.invokeOsgiMethod(classLoader.loadClass("org.pentaho.big.data.kettle.plugins.kafka.KafkaConsumerInputMeta$ConnectionType"), "valueOf", spkafkaconsumerinput.getConnectionType());
		OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "setConnectionType", connectType);

		
		KafkaConsumerFieldDto timestampFieldDto = spkafkaconsumerinput.getTimestampField();
		//KafkaConsumerField timestampFieldMetaDto = new KafkaConsumerField();
		Object timestampFieldMetaDto = OsgiBundleUtils.newOsgiInstance(stepMetaInterface, "org.pentaho.big.data.kettle.plugins.kafka.KafkaConsumerField");
		if (timestampFieldDto != null) {
			//timestampFieldMetaDto.setKafkaName(Name.valueOf(timestampFieldDto.getKafkaName()));
			OsgiBundleUtils.invokeOsgiMethod(timestampFieldMetaDto, "setKafkaName", OsgiBundleUtils.invokeOsgiMethod(classLoader.loadClass("org.pentaho.big.data.kettle.plugins.kafka.KafkaConsumerField$Name"), "valueOf", timestampFieldDto.getKafkaName().toUpperCase()));
			//timestampFieldMetaDto.setOutputName(timestampFieldDto.getOutputName());
			OsgiBundleUtils.invokeOsgiMethod(timestampFieldMetaDto, "setOutputName", timestampFieldDto.getOutputName());
			//timestampFieldMetaDto.setOutputType(Type.valueOf(timestampFieldDto.getOutputType()));
			OsgiBundleUtils.invokeOsgiMethod(timestampFieldMetaDto, "setOutputType", OsgiBundleUtils.invokeOsgiMethod(classLoader.loadClass("org.pentaho.big.data.kettle.plugins.kafka.KafkaConsumerField$Type"), "valueOf", timestampFieldDto.getOutputType()));

		}
		//kafkaconsumerinputmeta.setTimestampField(timestampFieldMetaDto);
		OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "setTimestampField", timestampFieldMetaDto);
		
		KafkaConsumerFieldDto keyFieldDto = spkafkaconsumerinput.getKeyField();
		//KafkaConsumerField keyFieldMetaDto = new KafkaConsumerField();
		Object keyFieldMetaDto = OsgiBundleUtils.newOsgiInstance(stepMetaInterface, "org.pentaho.big.data.kettle.plugins.kafka.KafkaConsumerField");
		if (keyFieldDto != null) {
			//keyFieldMetaDto.setKafkaName(Name.valueOf(keyFieldDto.getKafkaName()));
			OsgiBundleUtils.invokeOsgiMethod(keyFieldMetaDto, "setKafkaName", OsgiBundleUtils.invokeOsgiMethod(classLoader.loadClass("org.pentaho.big.data.kettle.plugins.kafka.KafkaConsumerField$Name"), "valueOf", keyFieldDto.getKafkaName().toUpperCase()));
			//keyFieldMetaDto.setOutputName(keyFieldDto.getOutputName());
			OsgiBundleUtils.invokeOsgiMethod(keyFieldMetaDto, "setOutputName", keyFieldDto.getOutputName());
			//keyFieldMetaDto.setOutputType(Type.valueOf(keyFieldDto.getOutputType()));
			OsgiBundleUtils.invokeOsgiMethod(keyFieldMetaDto, "setOutputType", OsgiBundleUtils.invokeOsgiMethod(classLoader.loadClass("org.pentaho.big.data.kettle.plugins.kafka.KafkaConsumerField$Type"), "valueOf", keyFieldDto.getOutputType()));

		}
		//kafkaconsumerinputmeta.setKeyField(keyFieldMetaDto);
		OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "setKeyField", keyFieldMetaDto);
		
		KafkaConsumerFieldDto messageFieldDto = spkafkaconsumerinput.getMessageField();
		//KafkaConsumerField messageFieldMetaDto = new KafkaConsumerField();
		Object messageFieldMetaDto = OsgiBundleUtils.newOsgiInstance(stepMetaInterface, "org.pentaho.big.data.kettle.plugins.kafka.KafkaConsumerField");
		if (messageFieldDto != null) {
			//messageFieldMetaDto.setKafkaName(Name.valueOf(messageFieldDto.getKafkaName()));
			OsgiBundleUtils.invokeOsgiMethod(messageFieldMetaDto, "setKafkaName", OsgiBundleUtils.invokeOsgiMethod(classLoader.loadClass("org.pentaho.big.data.kettle.plugins.kafka.KafkaConsumerField$Name"), "valueOf", messageFieldDto.getKafkaName().toUpperCase()));
			//messageFieldMetaDto.setOutputName(messageFieldDto.getOutputName());
			OsgiBundleUtils.invokeOsgiMethod(messageFieldMetaDto, "setOutputName", messageFieldDto.getOutputName());
			//messageFieldMetaDto.setOutputType(Type.valueOf(messageFieldDto.getOutputType()));
			OsgiBundleUtils.invokeOsgiMethod(messageFieldMetaDto, "setOutputType", OsgiBundleUtils.invokeOsgiMethod(classLoader.loadClass("org.pentaho.big.data.kettle.plugins.kafka.KafkaConsumerField$Type"), "valueOf", messageFieldDto.getOutputType()));

		}
		//kafkaconsumerinputmeta.setMessageField(messageFieldMetaDto);
		OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "setMessageField", messageFieldMetaDto);
		
		KafkaConsumerFieldDto topicFieldDto = spkafkaconsumerinput.getTopicField();
		//KafkaConsumerField topicFieldMetaDto =  new KafkaConsumerField();
		Object topicFieldMetaDto = OsgiBundleUtils.newOsgiInstance(stepMetaInterface, "org.pentaho.big.data.kettle.plugins.kafka.KafkaConsumerField");
		if (topicFieldDto != null) {
			//topicFieldMetaDto.setKafkaName(Name.valueOf(topicFieldDto.getKafkaName()));
			OsgiBundleUtils.invokeOsgiMethod(topicFieldMetaDto, "setKafkaName", OsgiBundleUtils.invokeOsgiMethod(classLoader.loadClass("org.pentaho.big.data.kettle.plugins.kafka.KafkaConsumerField$Name"), "valueOf", topicFieldDto.getKafkaName().toUpperCase()));
			//topicFieldMetaDto.setOutputName(topicFieldDto.getOutputName());
			OsgiBundleUtils.invokeOsgiMethod(topicFieldMetaDto, "setOutputName", topicFieldDto.getOutputName());
			//topicFieldMetaDto.setOutputType(Type.valueOf(topicFieldDto.getOutputType()));
			OsgiBundleUtils.invokeOsgiMethod(topicFieldMetaDto, "setOutputType", OsgiBundleUtils.invokeOsgiMethod(classLoader.loadClass("org.pentaho.big.data.kettle.plugins.kafka.KafkaConsumerField$Type"), "valueOf", topicFieldDto.getOutputType()));

		}
		//kafkaconsumerinputmeta.setTopicField(topicFieldMetaDto);
		OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "setTopicField", topicFieldMetaDto);
		
		KafkaConsumerFieldDto offsetFieldDto =spkafkaconsumerinput.getOffsetField();
		//KafkaConsumerField offsetFieldMetaDto =  new KafkaConsumerField();
		Object offsetFieldMetaDto = OsgiBundleUtils.newOsgiInstance(stepMetaInterface, "org.pentaho.big.data.kettle.plugins.kafka.KafkaConsumerField");
		if (offsetFieldDto != null) {
			//offsetFieldMetaDto.setKafkaName(Name.valueOf(offsetFieldDto.getKafkaName()));
			OsgiBundleUtils.invokeOsgiMethod(offsetFieldMetaDto, "setKafkaName", OsgiBundleUtils.invokeOsgiMethod(classLoader.loadClass("org.pentaho.big.data.kettle.plugins.kafka.KafkaConsumerField$Name"), "valueOf", offsetFieldDto.getKafkaName().toUpperCase()));
			//offsetFieldMetaDto.setOutputName(offsetFieldDto.getOutputName());
			OsgiBundleUtils.invokeOsgiMethod(offsetFieldMetaDto, "setOutputName", offsetFieldDto.getOutputName());
			//offsetFieldMetaDto.setOutputType(Type.valueOf(offsetFieldDto.getOutputType()));
			OsgiBundleUtils.invokeOsgiMethod(offsetFieldMetaDto, "setOutputType", OsgiBundleUtils.invokeOsgiMethod(classLoader.loadClass("org.pentaho.big.data.kettle.plugins.kafka.KafkaConsumerField$Type"), "valueOf", offsetFieldDto.getOutputType()));

		}
		//kafkaconsumerinputmeta.setOffsetField(offsetFieldMetaDto);
		OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "setOffsetField", offsetFieldMetaDto);
		
		KafkaConsumerFieldDto partitionFieldDto = spkafkaconsumerinput.getPartitionField();
		//KafkaConsumerField partitionFieldMetaDto =  new KafkaConsumerField();
		Object partitionFieldMetaDto = OsgiBundleUtils.newOsgiInstance(stepMetaInterface, "org.pentaho.big.data.kettle.plugins.kafka.KafkaConsumerField");
		if (partitionFieldDto != null) {
			//partitionFieldMetaDto.setKafkaName(Name.valueOf(partitionFieldDto.getKafkaName()));
			OsgiBundleUtils.invokeOsgiMethod(partitionFieldMetaDto, "setKafkaName", OsgiBundleUtils.invokeOsgiMethod(classLoader.loadClass("org.pentaho.big.data.kettle.plugins.kafka.KafkaConsumerField$Name"), "valueOf", partitionFieldDto.getKafkaName().toUpperCase()));
			//partitionFieldMetaDto.setOutputName(partitionFieldDto.getOutputName());
			OsgiBundleUtils.invokeOsgiMethod(partitionFieldMetaDto, "setOutputName", partitionFieldDto.getOutputName());
			//partitionFieldMetaDto.setOutputType(Type.valueOf(partitionFieldDto.getOutputType()));
			OsgiBundleUtils.invokeOsgiMethod(partitionFieldMetaDto, "setOutputType", OsgiBundleUtils.invokeOsgiMethod(classLoader.loadClass("org.pentaho.big.data.kettle.plugins.kafka.KafkaConsumerField$Type"), "valueOf", partitionFieldDto.getOutputType()));

		}
		//kafkaconsumerinputmeta.setPartitionField(partitionFieldMetaDto);
		OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "setPartitionField", partitionFieldMetaDto);

		//kafkaconsumerinputmeta.setConfig(spkafkaconsumerinput.getConfig());
		if(spkafkaconsumerinput.getConfig() != null) {
			OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "setConfig",  new Object[] {spkafkaconsumerinput.getConfig() },new Class[] {Map.class} );
		}else {
			OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "setConfig", new Object[] {new LinkedHashMap<>()},new Class[] {Map.class} );
		}
	

	}

	@Override
	public int stepType() {
		return 1;
	}

	@Override
	public void getStepDataAndRelationship(TransMeta transMeta, StepMeta stepMeta, StepDataRelationship sdr)
			throws Exception {
		//kafka消费者,监听到消息调用转换进行处理,组件不会产生关系
	}

}
