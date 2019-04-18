/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.quality.dto.step.steps.bulkloading;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.hadoop.metrics2.sink.relocated.google.common.collect.Maps;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettlePluginException;
import org.pentaho.di.core.plugins.PluginInterface;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.core.plugins.StepPluginType;
import org.pentaho.di.core.util.IdatrixPropertyUtil;
import org.pentaho.di.core.util.OsgiBundleUtils;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ys.idatrix.quality.dto.step.StepFieldDto;
import com.ys.idatrix.quality.dto.step.parts.ElasticSearchBulkFieldDto;
import com.ys.idatrix.quality.dto.step.parts.ElasticSearchBulkServerDto;
import com.ys.idatrix.quality.dto.step.parts.ElasticSearchBulkSettingDto;
import com.ys.idatrix.quality.dto.step.steps.StepParameter;
import com.ys.idatrix.quality.recovery.trans.ResumeStepDataParser;
import com.ys.idatrix.quality.recovery.trans.ResumeTransParser;
import com.ys.idatrix.quality.recovery.trans.dto.StepLinesDto;
import com.ys.idatrix.quality.toolkit.analyzer.trans.step.StepDataRelationship;
import com.ys.idatrix.quality.toolkit.analyzer.trans.step.StepDataRelationshipParser;
import com.ys.idatrix.quality.toolkit.domain.DataNode;
import com.ys.idatrix.quality.toolkit.domain.Relationship;
import com.ys.idatrix.quality.toolkit.utils.DataNodeUtil;
import com.ys.idatrix.quality.toolkit.utils.RelationshipUtil;

import net.sf.json.JSONObject;

/**
 * step - ElasticSearch Bulk Insert 转换
 * org.pentaho.di.trans.steps.elasticsearchbulk.ElasticSearchBulkMeta
 * 
 * @author XH
 * @since 2017年6月23日
 *
 */
@Component("SPElasticSearchBulk")
@Scope("prototype")
public class SPElasticSearchBulk implements StepParameter, StepDataRelationshipParser, ResumeStepDataParser {

	String index;
	String type;
	String batchSize = "50000";
	String timeout;
	// TimeUnit.valueOf( timeoutStr );
	String timeoutUnit;
	boolean isJsonInsert = false;
	String jsonField;
	String idOutField;
	String idInField;
	boolean overWriteIfSameId = true;
	boolean useOutput;
	boolean stopOnError = true ;
	
	List<ElasticSearchBulkFieldDto> fields;
	List<ElasticSearchBulkServerDto> servers;
	List<ElasticSearchBulkSettingDto> settings;

	/**
	 * @return index
	 */
	public String getIndex() {
		return index;
	}

	/**
	 * @param 设置
	 *            index
	 */
	public void setIndex(String index) {
		this.index = index;
	}

	/**
	 * @return type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param 设置
	 *            type
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return batchSize
	 */
	public String getBatchSize() {
		return batchSize;
	}

	/**
	 * @param 设置
	 *            batchSize
	 */
	public void setBatchSize(String batchSize) {
		this.batchSize = batchSize;
	}

	/**
	 * @return timeout
	 */
	public String getTimeout() {
		return timeout;
	}

	/**
	 * @param 设置
	 *            timeout
	 */
	public void setTimeout(String timeout) {
		this.timeout = timeout;
	}

	/**
	 * @return timeoutUnit
	 */
	public String getTimeoutUnit() {
		return timeoutUnit;
	}

	public TimeUnit getTimeUnit() {
		if (timeoutUnit != null) {
			return TimeUnit.valueOf(timeoutUnit);
		}
		return TimeUnit.SECONDS;
	}

	/**
	 * @param 设置
	 *            timeoutUnit
	 */
	public void setTimeoutUnit(String timeoutUnit) {
		this.timeoutUnit = timeoutUnit;
	}

	public void setTimeUnit(TimeUnit timeUnit) {
		this.timeoutUnit = timeUnit.toString();
	}

	/**
	 * @return isJsonInsert
	 */
	public boolean isJsonInsert() {
		return isJsonInsert;
	}

	/**
	 * @param 设置
	 *            isJsonInsert
	 */
	public void setJsonInsert(boolean isJsonInsert) {
		this.isJsonInsert = isJsonInsert;
	}

	/**
	 * @return jsonField
	 */
	public String getJsonField() {
		return jsonField;
	}

	/**
	 * @param 设置
	 *            jsonField
	 */
	public void setJsonField(String jsonField) {
		this.jsonField = jsonField;
	}

	/**
	 * @return idOutField
	 */
	public String getIdOutField() {
		return idOutField;
	}

	/**
	 * @param 设置
	 *            idOutField
	 */
	public void setIdOutField(String idOutField) {
		this.idOutField = idOutField;
	}

	/**
	 * @return idInField
	 */
	public String getIdInField() {
		return idInField;
	}

	/**
	 * @param 设置
	 *            idInField
	 */
	public void setIdInField(String idInField) {
		this.idInField = idInField;
	}

	/**
	 * @return overWriteIfSameId
	 */
	public boolean isOverWriteIfSameId() {
		return overWriteIfSameId;
	}

	/**
	 * @param 设置
	 *            overWriteIfSameId
	 */
	public void setOverWriteIfSameId(boolean overWriteIfSameId) {
		this.overWriteIfSameId = overWriteIfSameId;
	}

	/**
	 * @return useOutput
	 */
	public boolean isUseOutput() {
		return useOutput;
	}

	/**
	 * @param 设置
	 *            useOutput
	 */
	public void setUseOutput(boolean useOutput) {
		this.useOutput = useOutput;
	}

	/**
	 * @return stopOnError
	 */
	public boolean isStopOnError() {
		return stopOnError;
	}

	/**
	 * @param 设置
	 *            stopOnError
	 */
	public void setStopOnError(boolean stopOnError) {
		this.stopOnError = stopOnError;
	}

	/**
	 * @return fields
	 */
	public List<ElasticSearchBulkFieldDto> getFields() {
		return fields;
	}

	/**
	 * @param 设置
	 *            fields
	 */
	public void setFields(List<ElasticSearchBulkFieldDto> fields) {
		this.fields = fields;
	}

	/**
	 * @return servers
	 */
	public List<ElasticSearchBulkServerDto> getServers() {
		return servers;
	}

	/**
	 * @param 设置
	 *            servers
	 */
	public void setServers(List<ElasticSearchBulkServerDto> servers) {
		this.servers = servers;
	}

	/**
	 * @return settings
	 */
	public List<ElasticSearchBulkSettingDto> getSettings() {
		return settings;
	}

	/**
	 * @param 设置
	 *            settings
	 */
	public void setSettings(List<ElasticSearchBulkSettingDto> settings) {
		this.settings = settings;
	}
	
	public void checkVersionTo5( StepMeta stepMeta ) {
		
		String plugins = PluginRegistry.getInstance().getPluginId( StepPluginType.class, stepMeta.getStepMetaInterface() );
		if("ElasticSearchBulk5".equals(plugins)) {
			return ;
		}
		
		String version = "v2" ;
		
		List<ElasticSearchBulkSettingDto> st = getSettings();
		if( st != null && !st.isEmpty() ) {
			String  settingKey= "idatrix.elasticsearch.version";
			ElasticSearchBulkSettingDto es = st.stream().filter(esbs -> { return settingKey.equals(esbs.getSetting()); }).findAny().orElse(null);
			if(es != null  ) {
				if( es.getValue().startsWith("v5")) {
					version = "v5" ;
				}
				st.remove(es);
			}
		}
		if( !version.startsWith("v5") ) {
			version = IdatrixPropertyUtil.getProperty("idatrix.elasticsearch.version", "v2");
		}
	
		if( version.startsWith("v5") ) {
			PluginRegistry registry = PluginRegistry.getInstance();
			PluginInterface stepPlugin = registry.findPluginWithId(StepPluginType.class, "ElasticSearchBulk5");
			if (stepPlugin != null) {
				StepMetaInterface info;
				try {
					info = (StepMetaInterface) registry.loadClass(stepPlugin);
					info.setDefault();
					stepMeta.setStepMetaInterface(info);
					stepMeta.setStepID("ElasticSearchBulk5");
				} catch (KettlePluginException e) {
				}
			}
		}
	}

	@Override
	public void initParamObject(StepMeta stepMeta) {
		checkVersionTo5(stepMeta);
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		
		String serverIp = IdatrixPropertyUtil.getProperty("idatrix.elastic.search.default.ip");
		if( !Utils.isEmpty(serverIp) ) {
			int serverPort = Integer.valueOf( IdatrixPropertyUtil.getProperty("idatrix.elastic.search.default.port", "9300") );
			OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "clearServers");
			OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "addServer", serverIp , serverPort );
		}
		
		String clusterName = IdatrixPropertyUtil.getProperty("idatrix.elastic.search.default.clustername");
		if( !Utils.isEmpty(clusterName) ) {
			OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "clearSettings");
			OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "addSetting", "cluster.name", clusterName);
		}

	}
	
	/* 
	 * 
	 */
	@Override
	public Object getParameterObject(Object json) {
		JSONObject jsonObj = JSONObject.fromObject(json);
		Map<String, Class<?>> classMap = new HashMap<>();
		classMap.put("fields", ElasticSearchBulkFieldDto.class);
		classMap.put("servers", ElasticSearchBulkServerDto.class);
		classMap.put("settings", ElasticSearchBulkSettingDto.class);
		return (SPElasticSearchBulk) JSONObject.toBean(jsonObj, SPElasticSearchBulk.class, classMap);
	}

	/* 
	 * 
	 */
	@Override
	public Object encodeParameterObject(StepMeta stepMeta) {
		stepMeta.setStepID("ElasticSearchBulk");
		
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPElasticSearchBulk spElasticSearchBulk = new SPElasticSearchBulk();
		// ElasticSearchBulkMeta elasticsearchbulkmeta= (ElasticSearchBulkMeta
		// )stepMetaInterface;

		@SuppressWarnings("unchecked")
		List<Object> fieldList = (List<Object>) OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getFields");// elasticsearchbulkmeta.getFields()
		spElasticSearchBulk.setFields(fieldList.stream().map(field -> {
			ElasticSearchBulkFieldDto esbfd = new ElasticSearchBulkFieldDto();
			esbfd.setName((String) OsgiBundleUtils.getOsgiField(field, "name", false)); // field.name);
			esbfd.setTargetName((String) OsgiBundleUtils.getOsgiField(field, "targetName", false)); // field.targetName);
			return esbfd;

		}).collect(Collectors.toList()));
		@SuppressWarnings("unchecked")
		List<Object> serverList = (List<Object>) OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getServers");// elasticsearchbulkmeta.getServers()
		spElasticSearchBulk.setServers(serverList.stream().map(server -> {
			ElasticSearchBulkServerDto esbsd = new ElasticSearchBulkServerDto();
			esbsd.setAddress((String) OsgiBundleUtils.getOsgiField(server, "address", false)); // server.address
			esbsd.setPort((int) OsgiBundleUtils.getOsgiField(server, "port", false)); // server.port);
			return esbsd;
		}).collect(Collectors.toList()));
		@SuppressWarnings("unchecked")
		Map<String, String> settingsMap = (Map<String, String>) OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface,
				"getSettingsMap"); // elasticsearchbulkmeta.getSettingsMap();
		spElasticSearchBulk.setSettings(settingsMap.keySet().stream().map(setting -> {
			ElasticSearchBulkSettingDto esbsd = new ElasticSearchBulkSettingDto();
			esbsd.setSetting(setting);
			esbsd.setValue(settingsMap.get(setting));
			return esbsd;
		}).collect(Collectors.toList()));
		spElasticSearchBulk.setType((String) OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getType")); // elasticsearchbulkmeta.getType());
		spElasticSearchBulk.setIndex((String) OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getIndex")); // elasticsearchbulkmeta.getIndex());
		spElasticSearchBulk.setJsonField((String) OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getJsonField")); // elasticsearchbulkmeta.getJsonField());
		spElasticSearchBulk
				.setIdOutField((String) OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getIdOutField")); // elasticsearchbulkmeta.getIdOutField());
		spElasticSearchBulk.setIdInField((String) OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getIdInField")); // elasticsearchbulkmeta.getIdInField());
		spElasticSearchBulk.setBatchSize((String) OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getBatchSize")); // elasticsearchbulkmeta.getBatchSize());
		spElasticSearchBulk.setTimeout((String) OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getTimeOut")); // elasticsearchbulkmeta.getTimeOut());
		spElasticSearchBulk.setTimeoutUnit(
				(String) OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getTimeoutUnit").toString()); // elasticsearchbulkmeta.getTimeoutUnit());
		spElasticSearchBulk.setUseOutput((boolean) OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "isUseOutput")); // elasticsearchbulkmeta.isUseOutput());
		spElasticSearchBulk
				.setStopOnError((boolean) OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "isStopOnError")); // elasticsearchbulkmeta.isStopOnError());
		spElasticSearchBulk.setOverWriteIfSameId(
				(boolean) OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "isOverWriteIfSameId")); // elasticsearchbulkmeta.isOverWriteIfSameId());
		spElasticSearchBulk
				.setJsonInsert((boolean) OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "isJsonInsert")); // elasticsearchbulkmeta.isJsonInsert())
																												// ;
		return spElasticSearchBulk;
	}

	/* 
	 * 
	 */
	@Override
	public void decodeParameterObject(StepMeta stepMeta, Object po, List<DatabaseMeta> databases,TransMeta transMeta) throws Exception {
		SPElasticSearchBulk spElasticSearchBulk = (SPElasticSearchBulk) po;
		spElasticSearchBulk.checkVersionTo5(stepMeta);
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		
		// ElasticSearchBulkMeta elasticsearchbulkmeta= (ElasticSearchBulkMeta
		// )stepMetaInterface;

		OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "clearFields");
		spElasticSearchBulk.getFields().stream().forEach(field -> {
			// elasticsearchbulkmeta.addField(field.getName(),
			// field.getTargetName());
			OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "addField", field.getName(), field.getTargetName());
		});
		OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "clearServers");
		spElasticSearchBulk.getServers().stream().forEach(server -> {
			// elasticsearchbulkmeta.addServer(server.getAddress(),
			// server.getPort());
			OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "addServer", server.getAddress(), server.getPort());
		});
		OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "clearSettings");
		spElasticSearchBulk.getSettings().stream().forEach(setting -> {
			// elasticsearchbulkmeta.addSetting(setting.getSetting(),
			// setting.getValue());
			OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "addSetting", setting.getSetting(), setting.getValue());
		});
		OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "setJsonField", spElasticSearchBulk.getJsonField());// elasticsearchbulkmeta.setJsonField(spElasticSearchBulk.getJsonField());
		OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "setIdOutField", spElasticSearchBulk.getIdOutField());// elasticsearchbulkmeta.setIdOutField(spElasticSearchBulk.getIdOutField());
		OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "setIndex", spElasticSearchBulk.getIndex());// elasticsearchbulkmeta.setIndex(spElasticSearchBulk.getIndex());
		OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "setType", spElasticSearchBulk.getType());// elasticsearchbulkmeta.setType(spElasticSearchBulk.getType());
		OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "setIdInField", spElasticSearchBulk.getIdInField());// elasticsearchbulkmeta.setIdInField(spElasticSearchBulk.getIdInField());
		OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "setUseOutput", spElasticSearchBulk.isUseOutput());// elasticsearchbulkmeta.setUseOutput(spElasticSearchBulk.isUseOutput());
		OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "setStopOnError", spElasticSearchBulk.isStopOnError());// elasticsearchbulkmeta.setStopOnError(spElasticSearchBulk.isStopOnError());
		OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "setBatchSize", spElasticSearchBulk.getBatchSize());// elasticsearchbulkmeta.setBatchSize(spElasticSearchBulk.getBatchSize());
		OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "setTimeOut", spElasticSearchBulk.getTimeout());// elasticsearchbulkmeta.setTimeOut(spElasticSearchBulk.getTimeout());
		OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "setTimeoutUnit", spElasticSearchBulk.getTimeUnit());// elasticsearchbulkmeta.setTimeoutUnit(spElasticSearchBulk.getTimeUnit());
		OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "setOverWriteIfSameId",
				spElasticSearchBulk.isOverWriteIfSameId());// elasticsearchbulkmeta.setOverWriteIfSameId(spElasticSearchBulk.isOverWriteIfSameId());
		OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "setJsonInsert", spElasticSearchBulk.isJsonInsert());// elasticsearchbulkmeta.setJsonInsert(spElasticSearchBulk.isJsonInsert()
																													// )
																													// ;

	}

	/*
	 * 覆盖方法：getStepDataAndRelationship
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void getStepDataAndRelationship(TransMeta transMeta, StepMeta stepMeta, StepDataRelationship sdr) throws Exception {

		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();

		// 获取第一个服务器地址
		String firstServerName = "";
		List<Object> serverList = (List<Object>) OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getServers");// elasticsearchbulkmeta.getServers()
		if (serverList != null && serverList.size() > 0) {
			firstServerName = OsgiBundleUtils.getOsgiField(serverList.get(0), "address", false) + ":"
					+ OsgiBundleUtils.getOsgiField(serverList.get(0), "port", false);
		}
		// 获取所有和类型
		String type = (String) OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getType"); // elasticsearchbulkmeta.getType());
		String index = (String) OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getIndex"); // elasticsearchbulkmeta.getIndex());

		// 获取操作的字段
		List<Object> outputFields = (List<Object>) OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getFields");// elasticsearchbulkmeta.getFields()
		Map<String, StepFieldDto> fields = null;
		String[] outFieldNames = null;
		String[] inFieldNames = null;
		if (outputFields != null && outputFields.size() > 0) {
			outFieldNames = new String[outputFields.size()];
			inFieldNames = new String[outputFields.size()];
			fields = Maps.newHashMap();
			for (int i = 0; i < outputFields.size(); i++) {
				Object outfield = outputFields.get(i);
				String inF = (String) OsgiBundleUtils.getOsgiField(outfield, "name", false); // field.name);
				String outF = (String) OsgiBundleUtils.getOsgiField(outfield, "targetName", false); // field.targetName);

				outFieldNames[i] = outF;
				inFieldNames[i] = inF;

				StepFieldDto stepFieldDto = new StepFieldDto();
				stepFieldDto.setName(outF);
				stepFieldDto.setOrigin(stepMeta.getName());
				fields.put(outF, stepFieldDto);
			}
		}
		
		Map<String, DataNode> itemNodes = DataNodeUtil.interfaceNodeParse("ElasticSearch", firstServerName, type + "-" + index, stepMeta.getName(), fields.values());
		sdr.getOutputDataNodes().addAll(itemNodes.values());

		// 增加 流节点 和 输出系统节点 的关系
		String from = "转换:" + transMeta.getName() + ",步骤:" + stepMeta.getName();
		List<Relationship> relationships = RelationshipUtil.outputStepRelationship(itemNodes, null, stepMeta.getName(), from, outFieldNames, outFieldNames) ;
		sdr.getDataRelationship().addAll(relationships);

	}
	
	@Override
	public int stepType() {
		if(useOutput) {
			return 6;
		}else {
			return 2;
		}
		
	}
	
	@Override
	public boolean isListenerLineKey(String rowKey ,StepLinesDto result) {
		
		if(useOutput) {
			return ResumeTransParser.outputKey.equals(rowKey);
		}else {
			return ResumeTransParser.writeKey .equals(rowKey);
		}
	}
	
	@Override
	public Long getLinekeyIndex(StepLinesDto result) {
		Long rowLine = result.getRowLine();
		if(rowLine ==  null) {
			if(useOutput) {
				rowLine = result.getLinesOutput();
			}else {
				rowLine = result.getLinesWritten();
			}
		}
		return rowLine;
	}
}
