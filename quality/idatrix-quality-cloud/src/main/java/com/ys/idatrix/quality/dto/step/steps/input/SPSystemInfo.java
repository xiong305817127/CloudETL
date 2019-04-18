package com.ys.idatrix.quality.dto.step.steps.input;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.systemdata.SystemDataMeta;
import org.pentaho.di.trans.steps.systemdata.SystemDataTypes;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ys.idatrix.quality.dto.step.steps.StepParameter;
import com.ys.idatrix.quality.toolkit.analyzer.trans.step.StepDataRelationship;
import com.ys.idatrix.quality.toolkit.analyzer.trans.step.StepDataRelationshipParser;
import com.ys.idatrix.quality.toolkit.domain.DataNode;
import com.ys.idatrix.quality.toolkit.domain.Relationship;
import com.ys.idatrix.quality.toolkit.utils.DataNodeUtil;
import com.ys.idatrix.quality.toolkit.utils.RelationshipUtil;

import net.sf.json.JSONObject;

/**
 * Step - SystemInfo(获取系统信息).
 * 转换  org.pentaho.di.trans.steps.systemdata.SystemDataMeta
 * 
 * @author XH
 * @since 2018-04-10
 */
@Component("SPSystemInfo")
@Scope("prototype")
public class SPSystemInfo implements StepParameter, StepDataRelationshipParser {
	
	private List<String> fieldNames ;
	private List<String> types ;

	/**
	 * @return the fieldNames
	 */
	public List<String> getFieldNames() {
		return fieldNames;
	}

	/**
	 * @param  设置 fieldNames
	 */
	public void setFieldNames(List<String> fieldNames) {
		this.fieldNames = fieldNames;
	}

	/**
	 * @return the types
	 */
	public List<String> getTypes() {
		return types;
	}

	/**
	 * @param  设置 types
	 */
	public void setTypes(List<String> types) {
		this.types = types;
	}

	@Override
	public Object getParameterObject(Object json) {
		JSONObject jsonObj = JSONObject.fromObject(json);
		return (SPSystemInfo) JSONObject.toBean(jsonObj, SPSystemInfo.class);
	}

	@Override
	public Object encodeParameterObject(StepMeta stepMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPSystemInfo spSystemInfo= new SPSystemInfo();
		SystemDataMeta systemdatameta= (SystemDataMeta )stepMetaInterface;
		
		spSystemInfo.setFieldNames( Arrays.asList(systemdatameta.getFieldName())) ;
		spSystemInfo.setTypes( transArrayToList(systemdatameta.getFieldType(), new DtoTransData<String>() {
			@Override
			public String dealData(Object obj, int index) {
				SystemDataTypes type = (SystemDataTypes)obj ;
				return type.getCode();
			}
		})) ;
		
		return spSystemInfo;
	}

	@Override
	public void decodeParameterObject(StepMeta stepMeta, Object po, List<DatabaseMeta> databases,TransMeta transMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();

		SPSystemInfo spSystemInfo= (SPSystemInfo)po;
		SystemDataMeta  systemdatameta= (SystemDataMeta )stepMetaInterface;
		
		systemdatameta.setFieldName(spSystemInfo.getFieldNames().toArray(new String[] {}));
		SystemDataTypes[] sdtypes= new SystemDataTypes[spSystemInfo.getTypes().size()];
		transListToArray(spSystemInfo.getTypes(), new DtoTransData<SystemDataTypes>() {
			@Override
			public SystemDataTypes dealData(Object obj, int index) {
				sdtypes[index]=SystemDataMeta.getType((String)obj);
				return null;
			}} );
		systemdatameta.setFieldType(sdtypes);
		
	}

	@Override
	public void getStepDataAndRelationship(TransMeta transMeta, StepMeta stepMeta, StepDataRelationship sdr)
			throws Exception {

		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SystemDataMeta systemdatameta = (SystemDataMeta) stepMetaInterface;

		if (systemdatameta.getFieldName() == null || systemdatameta.getFieldName().length ==0) {
			return;
		}

		Map<String, DataNode> itemDataNodes =  DataNodeUtil.interfaceNodeParse("Http", "dataInterface-getSystemInfo", "json",stepMeta.getName(), sdr.getOutputStream().values()) ;
		sdr.getInputDataNodes().addAll(itemDataNodes.values());
		
		// 增加 系统节点 和 流节点的关系
		String from = "转换:" + transMeta.getName() + ",步骤:" + stepMeta.getName();
		List<Relationship> relationships = RelationshipUtil.inputStepRelationship(itemDataNodes, null, sdr.getOutputStream(), stepMeta.getName(), from);
		sdr.getDataRelationship().addAll(relationships);
		
	}

}
