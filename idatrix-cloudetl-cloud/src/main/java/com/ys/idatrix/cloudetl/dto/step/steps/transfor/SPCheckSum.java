/**
 * 云化数据集成系统 iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.dto.step.steps.transfor;

import java.util.Arrays;
import java.util.List;

import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.checksum.CheckSumMeta;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ys.idatrix.cloudetl.dto.step.steps.StepParameter;
import com.ys.idatrix.cloudetl.toolkit.analyzer.trans.step.StepDataRelationship;
import com.ys.idatrix.cloudetl.toolkit.analyzer.trans.step.StepDataRelationshipParser;
import com.ys.idatrix.cloudetl.toolkit.utils.RelationshipUtil;

import net.sf.json.JSONObject;

/**
 * Step - CheckSum(增加校验列) 转换 org.pentaho.di.trans.steps.checksum.CheckSumMeta
 * 
 * @author XH
 * @since 2017年6月13日
 *
 */
@Component("SPCheckSum")
@Scope("prototype")
public class SPCheckSum implements StepParameter, StepDataRelationshipParser {

	String checksumtype;
	String resultfieldName;
	int resultType;
	boolean compatibilityMode;
	List<String> fieldName;

	/**
	 * @return checksumtype
	 */
	public String getChecksumtype() {
		return checksumtype;
	}

	/**
	 * @param checksumtype
	 *            要设置的 checksumtype
	 */
	public void setChecksumtype(String checksumtype) {
		this.checksumtype = checksumtype;
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
	 * @return resultType
	 */
	public int getResultType() {
		return resultType;
	}

	/**
	 * @param resultType
	 *            要设置的 resultType
	 */
	public void setResultType(int resultType) {
		this.resultType = resultType;
	}

	/**
	 * @return compatibilityMode
	 */
	public boolean isCompatibilityMode() {
		return compatibilityMode;
	}

	/**
	 * @param compatibilityMode
	 *            要设置的 compatibilityMode
	 */
	public void setCompatibilityMode(boolean compatibilityMode) {
		this.compatibilityMode = compatibilityMode;
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
		return (SPCheckSum) JSONObject.toBean(jsonObj, SPCheckSum.class);
	}

	/* 
	 * 
	 */
	@SuppressWarnings("deprecation")
	@Override
	public Object encodeParameterObject(StepMeta stepMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPCheckSum spCheckSum = new SPCheckSum();
		CheckSumMeta checksummeta = (CheckSumMeta) stepMetaInterface;

		spCheckSum.setChecksumtype(checksummeta.getCheckSumType());
		spCheckSum.setCompatibilityMode(checksummeta.isCompatibilityMode());
		spCheckSum.setResultfieldName(checksummeta.getResultFieldName());
		spCheckSum.setResultType(checksummeta.getResultType());
		spCheckSum.setFieldName(Arrays.asList(checksummeta.getFieldName()));

		return spCheckSum;
	}

	/* 
	 * 
	 */
	@SuppressWarnings("deprecation")
	@Override
	public void decodeParameterObject(StepMeta stepMeta, Object po, List<DatabaseMeta> databases,TransMeta transMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPCheckSum spCheckSum = (SPCheckSum) po;
		CheckSumMeta checksummeta = (CheckSumMeta) stepMetaInterface;

		checksummeta.setCheckSumType(spCheckSum.getChecksumtype());
		checksummeta.setCompatibilityMode(spCheckSum.isCompatibilityMode());
		checksummeta.setResultType(spCheckSum.getResultType());
		checksummeta.setFieldName(spCheckSum.getFieldName().toArray(new String[spCheckSum.getFieldName().size()]));
		checksummeta.setResultFieldName(spCheckSum.getResultfieldName());

	}

	/*
	 * 覆盖方法：getStepDataAndRelationship
	 */
	@Override
	public void getStepDataAndRelationship(TransMeta transMeta, StepMeta stepMeta, StepDataRelationship sdr) {
		
		String from= "转换:"+transMeta.getName()+",步骤:"+stepMeta.getName();
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		CheckSumMeta  checksummeta= (CheckSumMeta )stepMetaInterface;
		//输出
		String resultFieldName = checksummeta.getResultFieldName();
		//输入
		String[] fields = checksummeta.getFieldName();
		if (fields != null) {
			 Arrays.asList(fields).stream().forEach(fieldName -> {
				if(!Utils.isEmpty(fieldName)) {
					try {
						sdr.addRelationship( RelationshipUtil.buildFieldRelationship(from, fieldName, resultFieldName) );
					} catch (Exception e) {
						relationshiplogger.error("",e);
					}
				}
			});

		}
	}

}
