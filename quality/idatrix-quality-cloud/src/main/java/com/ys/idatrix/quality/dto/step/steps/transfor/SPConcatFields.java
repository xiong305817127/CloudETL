/**
 * 云化数据集成系统 iDatrix CloudETL
 */
package com.ys.idatrix.quality.dto.step.steps.transfor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.concatfields.ConcatFieldsMeta;
import org.pentaho.di.trans.steps.textfileoutput.TextFileField;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ys.idatrix.quality.dto.step.parts.TextFileFieldDto;
import com.ys.idatrix.quality.dto.step.steps.output.SPTextFileOutput;
import com.ys.idatrix.quality.toolkit.analyzer.trans.step.StepDataRelationship;
import com.ys.idatrix.quality.toolkit.utils.RelationshipUtil;

import net.sf.json.JSONObject;

/**
 * Step - Concat Fields(连接字段) org.pentaho.di.trans.steps.concatfields.ConcatFieldsMeta
 * 
 * @author XH
 * @since 2017年6月9日
 *
 */
@Component("SPConcatFields")
@Scope("prototype")
public class SPConcatFields extends SPTextFileOutput {

	String targetFieldName;
	int targetFieldLength;
	boolean removeSelectedFields;

	/**
	 * @return targetFieldName
	 */
	public String getTargetFieldName() {
		return targetFieldName;
	}

	/**
	 * @param targetFieldName
	 *            要设置的 targetFieldName
	 */
	public void setTargetFieldName(String targetFieldName) {
		this.targetFieldName = targetFieldName;
	}

	/**
	 * @return targetFieldLength
	 */
	public int getTargetFieldLength() {
		return targetFieldLength;
	}

	/**
	 * @param targetFieldLength
	 *            要设置的 targetFieldLength
	 */
	public void setTargetFieldLength(int targetFieldLength) {
		this.targetFieldLength = targetFieldLength;
	}

	/**
	 * @return removeSelectedFields
	 */
	public boolean isRemoveSelectedFields() {
		return removeSelectedFields;
	}

	/**
	 * @param removeSelectedFields
	 *            要设置的 removeSelectedFields
	 */
	public void setRemoveSelectedFields(boolean removeSelectedFields) {
		this.removeSelectedFields = removeSelectedFields;
	}

	/* 
	 * 
	 */
	@Override
	public Object getParameterObject(Object json) {
		JSONObject jsonObj = JSONObject.fromObject(json);
		Map<String, Class<?>> classMap = new HashMap<>();
		classMap.put("fields", TextFileFieldDto.class);
		return (SPConcatFields) JSONObject.toBean(jsonObj, SPConcatFields.class, classMap);
	}

	/* 
	 * 
	 */
	@Override
	public Object encodeParameterObject(StepMeta stepMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		ConcatFieldsMeta concatfieldsmeta = (ConcatFieldsMeta) stepMetaInterface;
		SPTextFileOutput obj = (SPTextFileOutput) super.encodeParameterObject(stepMeta);

		ObjectMapper mapper = new ObjectMapper();
		SPConcatFields spConcatFields = null;
		spConcatFields = mapper.readValue(mapper.writeValueAsString(obj), SPConcatFields.class);
		spConcatFields.setTargetFieldName(concatfieldsmeta.getTargetFieldName());
		spConcatFields.setTargetFieldLength(concatfieldsmeta.getTargetFieldLength());
		spConcatFields.setRemoveSelectedFields(concatfieldsmeta.isRemoveSelectedFields());

		return spConcatFields;

	}

	/* 
	 * 
	 */
	@Override
	public void decodeParameterObject(StepMeta stepMeta, Object po, List<DatabaseMeta> databases,TransMeta transMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		super.decodeParameterObject(stepMeta, po, databases, transMeta);

		SPConcatFields spConcatFields = (SPConcatFields) po;
		ConcatFieldsMeta concatfieldsmeta = (ConcatFieldsMeta) stepMetaInterface;

		concatfieldsmeta.setTargetFieldName(spConcatFields.getTargetFieldName());
		concatfieldsmeta.setTargetFieldLength(spConcatFields.getTargetFieldLength());
		concatfieldsmeta.setRemoveSelectedFields(spConcatFields.isRemoveSelectedFields());
	}

	@Override
	public void getStepDataAndRelationship(TransMeta transMeta, StepMeta stepMeta, StepDataRelationship sdr) throws Exception {
		
		String from= "转换:"+transMeta.getName()+",步骤:"+stepMeta.getName();
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		ConcatFieldsMeta  concatfieldsmeta= (ConcatFieldsMeta )stepMetaInterface;
		//输出
		String targetField = concatfieldsmeta.getTargetFieldName();
		//输入
		TextFileField[] outputFields = concatfieldsmeta.getOutputFields();
		if (outputFields != null) {
			for (TextFileField field : outputFields) {
				String name = field.getName() ;
				if(!Utils.isEmpty(name)) {
					sdr.addRelationship( RelationshipUtil.buildFieldRelationship(from, name, targetField) );
				}
			}
		}
	}
	
	/**
	 * 需要覆盖父类中的saveCacheData
	 */
	@Override
	public boolean afterSaveHandle(Map<Object, Object> cacheData ,TransMeta transMeta, StepMeta stepMeta,
			StepMetaInterface stepMetaInterface, StepDataInterface stepDataInterface, StepInterface stepInterface)
			throws Exception {
		return true;
	}

	
	/**
	 * 需要覆盖父类中的dealStepMeta
	 */
	@Override
	public boolean dealStepMeta(TransMeta transMeta ,StepMeta stepMeta ,StepMetaInterface stepMetaInterface)  throws Exception  {
		return true;
	}
	/**
	 * 需要覆盖父类中的stepType
	 */
	@Override
	public int stepType() {
		return 4;
	}

}
