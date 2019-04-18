/**
 * 云化数据集成系统 iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.dto.step.steps.transfor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.stringcut.StringCutMeta;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.ys.idatrix.cloudetl.dto.step.parts.StringCutfieldInStreamDto;
import com.ys.idatrix.cloudetl.dto.step.steps.StepParameter;
import com.ys.idatrix.cloudetl.toolkit.analyzer.trans.step.StepDataRelationship;
import com.ys.idatrix.cloudetl.toolkit.analyzer.trans.step.StepDataRelationshipParser;
import com.ys.idatrix.cloudetl.toolkit.utils.RelationshipUtil;

import net.sf.json.JSONObject;

/**
 * Step - String Cut(剪切字符串) org.pentaho.di.trans.steps.stringcut.StringCutMeta
 * 
 * @author XH
 * @since 2017年6月12日
 *
 */
@Component("SPStringCut")
@Scope("prototype")
public class SPStringCut implements StepParameter, StepDataRelationshipParser {

	List<StringCutfieldInStreamDto> fieldInStream;

	/**
	 * @return fieldInStream
	 */
	public List<StringCutfieldInStreamDto> getFieldInStream() {
		return fieldInStream;
	}

	/**
	 * @param fieldInStream
	 *            要设置的 fieldInStream
	 */
	public void setFieldInStream(List<StringCutfieldInStreamDto> fieldInStream) {
		this.fieldInStream = fieldInStream;
	}

	/* 
	 * 
	 */
	@Override
	public Object getParameterObject(Object json) {
		JSONObject jsonObj = JSONObject.fromObject(json);
		Map<String, Class<?>> classMap = new HashMap<>();
		classMap.put("fieldInStream", StringCutfieldInStreamDto.class);
		return (SPStringCut) JSONObject.toBean(jsonObj, SPStringCut.class, classMap);
	}

	/* 
	 * 
	 */
	@Override
	public Object encodeParameterObject(StepMeta stepMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPStringCut spStringCut = new SPStringCut();
		StringCutMeta stringcutmeta = (StringCutMeta) stepMetaInterface;

		List<StringCutfieldInStreamDto> fieldInStreamList = Lists.newArrayList();
		String[] fieldInStreams = stringcutmeta.getFieldInStream();
		String[] fieldOutStreams = stringcutmeta.getFieldOutStream();
		String[] cutFroms = stringcutmeta.getCutFrom();
		String[] cutTos = stringcutmeta.getCutTo();
		for (int i = 0; i < fieldInStreams.length; i++) {
			StringCutfieldInStreamDto stringcutfieldinstreamdto = new StringCutfieldInStreamDto();
			stringcutfieldinstreamdto.setFieldInStream(fieldInStreams[i]);
			stringcutfieldinstreamdto.setFieldOutStream(fieldOutStreams[i]);
			stringcutfieldinstreamdto.setCutFrom(cutFroms[i]);
			stringcutfieldinstreamdto.setCutTo(cutTos[i]);
			fieldInStreamList.add(stringcutfieldinstreamdto);
		}
		spStringCut.setFieldInStream(fieldInStreamList);
		return spStringCut;
	}

	/* 
	 * 
	 */
	@Override
	public void decodeParameterObject(StepMeta stepMeta, Object po, List<DatabaseMeta> databases,TransMeta transMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();

		SPStringCut spStringCut = (SPStringCut) po;
		StringCutMeta stringcutmeta = (StringCutMeta) stepMetaInterface;

		String[] fieldInStreams = new String[spStringCut.getFieldInStream().size()];
		String[] fieldOutStreams = new String[spStringCut.getFieldInStream().size()];
		String[] cutFroms = new String[spStringCut.getFieldInStream().size()];
		String[] cutTos = new String[spStringCut.getFieldInStream().size()];
		for (int i = 0; i < spStringCut.getFieldInStream().size(); i++) {
			StringCutfieldInStreamDto stringcutfieldinstreamdto = spStringCut.getFieldInStream().get(i);
			fieldInStreams[i] = stringcutfieldinstreamdto.getFieldInStream();
			fieldOutStreams[i] = stringcutfieldinstreamdto.getFieldOutStream();
			cutFroms[i] = stringcutfieldinstreamdto.getCutFrom();
			cutTos[i] = stringcutfieldinstreamdto.getCutTo();
		}
		stringcutmeta.setFieldInStream(fieldInStreams);
		stringcutmeta.setFieldOutStream(fieldOutStreams);
		stringcutmeta.setCutFrom(cutFroms);
		stringcutmeta.setCutTo(cutTos);

	}

	/*
	 * 覆盖方法：getStepDataAndRelationship
	 */
	@Override
	public void getStepDataAndRelationship(TransMeta transMeta, StepMeta stepMeta, StepDataRelationship sdr) throws Exception {
		
		String from= "转换:"+transMeta.getName()+",步骤:"+stepMeta.getName();
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		StringCutMeta stringcutmeta = (StringCutMeta) stepMetaInterface;
		//输入
		String[] fieldInStreams = stringcutmeta.getFieldInStream();
		//输出
		String[] fieldOutStreams = stringcutmeta.getFieldOutStream();
		for(int i=0 ;i< fieldOutStreams.length ;i++) {
			if(fieldOutStreams[i] != null) {
				sdr.addRelationship( RelationshipUtil.buildFieldRelationship(from, fieldInStreams[i], fieldOutStreams[i]) );
			}
		}
	}

}
