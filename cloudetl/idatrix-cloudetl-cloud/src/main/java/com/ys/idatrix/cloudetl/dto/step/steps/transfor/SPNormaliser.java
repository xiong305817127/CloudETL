/**
 * 云化数据集成系统 iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.dto.step.steps.transfor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.normaliser.NormaliserMeta;
import org.pentaho.di.trans.steps.normaliser.NormaliserMeta.NormaliserField;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ys.idatrix.cloudetl.dto.step.parts.NormaliserNormaliserFieldDto;
import com.ys.idatrix.cloudetl.dto.step.steps.StepParameter;
import com.ys.idatrix.cloudetl.recovery.trans.ResumeStepDataParser;
import com.ys.idatrix.cloudetl.toolkit.analyzer.trans.step.StepDataRelationship;
import com.ys.idatrix.cloudetl.toolkit.analyzer.trans.step.StepDataRelationshipParser;
import com.ys.idatrix.cloudetl.toolkit.utils.RelationshipUtil;

import net.sf.json.JSONObject;

/**
 * Step - Normaliser(行转列) org.pentaho.di.trans.steps.normaliser.NormaliserMeta
 * 
 * @author XH
 * @since 2017年6月13日
 *
 */
@Component("SPNormaliser")
@Scope("prototype")
public class SPNormaliser implements StepParameter, StepDataRelationshipParser,ResumeStepDataParser {

	String typeField;
	List<NormaliserNormaliserFieldDto> normaliserFields;

	/**
	 * @return typeField
	 */
	public String getTypeField() {
		return typeField;
	}

	/**
	 * @param typeField
	 *            要设置的 typeField
	 */
	public void setTypeField(String typeField) {
		this.typeField = typeField;
	}

	/**
	 * @return normaliserFields
	 */
	public List<NormaliserNormaliserFieldDto> getNormaliserFields() {
		return normaliserFields;
	}

	/**
	 * @param normaliserFields
	 *            要设置的 normaliserFields
	 */
	public void setNormaliserFields(List<NormaliserNormaliserFieldDto> normaliserFields) {
		this.normaliserFields = normaliserFields;
	}

	/* 
	 * 
	 */
	@Override
	public Object getParameterObject(Object json) {
		JSONObject jsonObj = JSONObject.fromObject(json);
		Map<String, Class<?>> classMap = new HashMap<>();
		classMap.put("normaliserFields", NormaliserNormaliserFieldDto.class);
		return (SPNormaliser) JSONObject.toBean(jsonObj, SPNormaliser.class, classMap);
	}

	/* 
	 * 
	 */
	@Override
	public Object encodeParameterObject(StepMeta stepMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPNormaliser spNormaliser = new SPNormaliser();
		NormaliserMeta normalisermeta = (NormaliserMeta) stepMetaInterface;

		spNormaliser.setTypeField(normalisermeta.getTypeField());
		if (normalisermeta.getNormaliserFields() != null) {
			NormaliserField[] normaliserFieldsArray = normalisermeta.getNormaliserFields();
			List<NormaliserNormaliserFieldDto> normaliserFieldsList = Arrays.asList(normaliserFieldsArray).stream()
					.map(temp1 -> {
						NormaliserNormaliserFieldDto temp2 = new NormaliserNormaliserFieldDto();
						temp2.setNorm(temp1.getNorm());
						temp2.setName(temp1.getName());
						temp2.setValue(temp1.getValue());
						return temp2;
					}).collect(Collectors.toList());
			spNormaliser.setNormaliserFields(normaliserFieldsList);
		}

		return spNormaliser;

	}

	/* 
	 * 
	 */
	@Override
	public void decodeParameterObject(StepMeta stepMeta, Object po, List<DatabaseMeta> databases,TransMeta transMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPNormaliser spNormaliser = (SPNormaliser) po;
		NormaliserMeta normalisermeta = (NormaliserMeta) stepMetaInterface;

		normalisermeta.setTypeField(spNormaliser.getTypeField());
		if (spNormaliser.getNormaliserFields() != null) {
			List<NormaliserField> normaliserFieldsList = spNormaliser.getNormaliserFields().stream().map(temp1 -> {
				NormaliserField temp2 = new NormaliserField();
				temp2.setNorm(temp1.getNorm());
				temp2.setName(temp1.getName());
				temp2.setValue(temp1.getValue());
				return temp2;
			}).collect(Collectors.toList());
			normalisermeta.setNormaliserFields(
					normaliserFieldsList.toArray(new NormaliserField[spNormaliser.getNormaliserFields().size()]));
		}

	}

	/*
	 * 覆盖方法：getStepDataAndRelationship
	 */
	@Override
	public void getStepDataAndRelationship(TransMeta transMeta, StepMeta stepMeta, StepDataRelationship sdr) {
		
		String from= "转换:"+transMeta.getName()+",步骤:"+stepMeta.getName();
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		NormaliserMeta  normalisermeta= (NormaliserMeta )stepMetaInterface;
		
		//输出
		String tf = normalisermeta.getTypeField();
		if( normalisermeta.getNormaliserFields() !=null ){
			NormaliserField[] normaliserFieldsArray = normalisermeta.getNormaliserFields();
			Arrays.asList(normaliserFieldsArray).stream().forEach(temp1 -> {
				try {
					//输入
					String name= temp1.getName() ;
					//输出
					String norm =  temp1.getNorm();
					
					sdr.addRelationship( RelationshipUtil.buildFieldRelationship(from, name, tf) );
					sdr.addRelationship( RelationshipUtil.buildFieldRelationship(from, name, norm) );
				} catch (Exception e) {
					relationshiplogger.error("",e);
				}
						
			});
		}
		

	}

	@Override
	public int stepType() {
		return 12;
	}

}
