/**
 * 云化数据集成系统 iDatrix CloudETL
 */
package com.ys.idatrix.quality.dto.step.steps.transfor;

import java.util.Arrays;
import java.util.List;

import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.fieldschangesequence.FieldsChangeSequenceMeta;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ys.idatrix.quality.dto.step.steps.StepParameter;
import com.ys.idatrix.quality.toolkit.analyzer.trans.step.StepDataRelationship;
import com.ys.idatrix.quality.toolkit.analyzer.trans.step.StepDataRelationshipParser;
import com.ys.idatrix.quality.toolkit.domain.Relationship;
import com.ys.idatrix.quality.toolkit.utils.RelationshipUtil;

import net.sf.json.JSONObject;

/**
 * Step - Fields Change Sequence(根据字段值来改变序列)
 * org.pentaho.di.trans.steps.fieldschangesequence.FieldsChangeSequenceMeta
 * 
 * @author XH
 * @since 2017年6月13日
 *
 */
@Component("SPFieldsChangeSequence")
@Scope("prototype")
public class SPFieldsChangeSequence implements StepParameter, StepDataRelationshipParser {

	String start;
	String increment;
	String resultfieldName;
	List<String> fieldName;

	/**
	 * @return start
	 */
	public String getStart() {
		return start;
	}

	/**
	 * @param start
	 *            要设置的 start
	 */
	public void setStart(String start) {
		this.start = start;
	}

	/**
	 * @return increment
	 */
	public String getIncrement() {
		return increment;
	}

	/**
	 * @param increment
	 *            要设置的 increment
	 */
	public void setIncrement(String increment) {
		this.increment = increment;
	}

	/**
	 * @return resultfieldName
	 */
	public String getResultfieldName() {
		return resultfieldName;
	}

	/**
	 * @param resultfieldName
	 *            要设置的 resultfieldName
	 */
	public void setResultfieldName(String resultfieldName) {
		this.resultfieldName = resultfieldName;
	}

	/**
	 * @return fieldName
	 */
	public List<String> getFieldName() {
		return fieldName;
	}

	/**
	 * @param fieldName
	 *            要设置的 fieldName
	 */
	public void setFieldName(List<String> fieldName) {
		this.fieldName = fieldName;
	}

	/* 
	 * 
	 */
	@Override
	public Object getParameterObject(Object json) {
		JSONObject jsonObj = JSONObject.fromObject(json);
		return (SPFieldsChangeSequence) JSONObject.toBean(jsonObj, SPFieldsChangeSequence.class);
	}

	/* 
	 * 
	 */
	@Override
	public Object encodeParameterObject(StepMeta stepMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPFieldsChangeSequence spFieldsChangeSequence = new SPFieldsChangeSequence();
		FieldsChangeSequenceMeta fieldschangesequencemeta = (FieldsChangeSequenceMeta) stepMetaInterface;

		spFieldsChangeSequence.setStart(fieldschangesequencemeta.getStart());
		spFieldsChangeSequence.setIncrement(fieldschangesequencemeta.getIncrement());
		spFieldsChangeSequence.setResultfieldName(fieldschangesequencemeta.getResultFieldName());
		if (fieldschangesequencemeta.getFieldName() != null) {
			spFieldsChangeSequence.setFieldName(Arrays.asList(fieldschangesequencemeta.getFieldName()));
		}

		return spFieldsChangeSequence;

	}

	/* 
	 * 
	 */
	@Override
	public void decodeParameterObject(StepMeta stepMeta, Object po, List<DatabaseMeta> databases,TransMeta transMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPFieldsChangeSequence spFieldsChangeSequence = (SPFieldsChangeSequence) po;
		FieldsChangeSequenceMeta fieldschangesequencemeta = (FieldsChangeSequenceMeta) stepMetaInterface;

		fieldschangesequencemeta.setStart(spFieldsChangeSequence.getStart());
		fieldschangesequencemeta.setIncrement(spFieldsChangeSequence.getIncrement());
		fieldschangesequencemeta.setResultFieldName(spFieldsChangeSequence.getResultfieldName());
		if (spFieldsChangeSequence.getFieldName() != null) {
			fieldschangesequencemeta.setFieldName(spFieldsChangeSequence.getFieldName()
					.toArray(new String[spFieldsChangeSequence.getFieldName().size()]));
		}

	}

	/*
	 * 覆盖方法：getStepDataAndRelationship
	 */
	@Override
	public void getStepDataAndRelationship(TransMeta transMeta, StepMeta stepMeta, StepDataRelationship sdr) throws Exception {
		
		String from= "转换:"+transMeta.getName()+",步骤:"+stepMeta.getName();
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		FieldsChangeSequenceMeta  fieldschangesequencemeta= (FieldsChangeSequenceMeta )stepMetaInterface;
		//输出
		String rfn = fieldschangesequencemeta.getResultFieldName();
		//输入
		String[] fields = fieldschangesequencemeta.getFieldName();
		if(fields != null && fields.length >0) {
			for(String f : fields) {
				sdr.addRelationship( RelationshipUtil.buildFieldRelationship(from, f, rfn) );
			}
		}else {
			//当没有字段时,会自动产生序列
			String dummyId = transMeta.getName()+"-"+stepMeta.getName()+"-"+rfn ;
			Relationship relationship = RelationshipUtil.buildDummyRelationship(from,dummyId , rfn);
			sdr.getDataRelationship().add(relationship);
			sdr.addInputDataNode(relationship.getStartNode());
		}
		
	}

}
