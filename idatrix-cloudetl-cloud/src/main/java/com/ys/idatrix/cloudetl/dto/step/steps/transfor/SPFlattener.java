/**
 * 云化数据集成系统 iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.dto.step.steps.transfor;

import java.util.Arrays;
import java.util.List;

import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.flattener.FlattenerMeta;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ys.idatrix.cloudetl.dto.step.steps.StepParameter;
import com.ys.idatrix.cloudetl.recovery.trans.ResumeStepDataParser;
import com.ys.idatrix.cloudetl.toolkit.analyzer.trans.step.StepDataRelationship;
import com.ys.idatrix.cloudetl.toolkit.analyzer.trans.step.StepDataRelationshipParser;
import com.ys.idatrix.cloudetl.toolkit.utils.RelationshipUtil;

import net.sf.json.JSONObject;

/**
 * Step - Flattener(行扁平化) org.pentaho.di.trans.steps.flattener.FlattenerMeta
 * 
 * @author XH
 * @since 2017年6月13日
 *
 */
@Component("SPFlattener")
@Scope("prototype")
public class SPFlattener implements StepParameter, StepDataRelationshipParser, ResumeStepDataParser {
	String fieldName;
	List<String> targetField;

	/**
	 * @return fieldName
	 */
	public String getFieldName() {
		return fieldName;
	}

	/**
	 * @param fieldName
	 *            要设置的 fieldName
	 */
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	/**
	 * @return targetField
	 */
	public List<String> getTargetField() {
		return targetField;
	}

	/**
	 * @param targetField
	 *            要设置的 targetField
	 */
	public void setTargetField(List<String> targetField) {
		this.targetField = targetField;
	}

	/* 
	 * 
	 */
	@Override
	public Object getParameterObject(Object json) {
		JSONObject jsonObj = JSONObject.fromObject(json);
		return (SPFlattener) JSONObject.toBean(jsonObj, SPFlattener.class);
	}

	/* 
	 * 
	 */
	@Override
	public Object encodeParameterObject(StepMeta stepMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPFlattener spFlattener = new SPFlattener();
		FlattenerMeta flattenermeta = (FlattenerMeta) stepMetaInterface;

		spFlattener.setFieldName(flattenermeta.getFieldName());
		if (flattenermeta.getTargetField() != null) {
			spFlattener.setTargetField(Arrays.asList(flattenermeta.getTargetField()));
		}

		return spFlattener;
	}

	/* 
	 * 
	 */
	@Override
	public void decodeParameterObject(StepMeta stepMeta, Object po, List<DatabaseMeta> databases,TransMeta transMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPFlattener spFlattener = (SPFlattener) po;
		FlattenerMeta flattenermeta = (FlattenerMeta) stepMetaInterface;

		flattenermeta.setFieldName(spFlattener.getFieldName());
		if (spFlattener.getTargetField() != null) {
			flattenermeta.setTargetField(
					spFlattener.getTargetField().toArray(new String[spFlattener.getTargetField().size()]));
		}

	}

	/*
	 * 覆盖方法：getStepDataAndRelationship
	 */
	@Override
	public void getStepDataAndRelationship(TransMeta transMeta, StepMeta stepMeta, StepDataRelationship sdr) throws Exception {
		
		String from= "转换:"+transMeta.getName()+",步骤:"+stepMeta.getName();
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		FlattenerMeta  flattenermeta= (FlattenerMeta )stepMetaInterface;
		//输入
		String fn = flattenermeta.getFieldName();
		//输出
		if(flattenermeta.getTargetField() != null) {
			for(String tf : flattenermeta.getTargetField()) {
				sdr.addRelationship( RelationshipUtil.buildFieldRelationship(from, fn, tf) );
			}
		}
		
	}

	@Override
	public int stepType() {
		return 12;
	}

}
