/**
 * 云化数据集成系统 iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.dto.step.steps.transfor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.replacestring.ReplaceStringMeta;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.ys.idatrix.cloudetl.dto.step.parts.ReplaceStringfieldInStreamDto;
import com.ys.idatrix.cloudetl.dto.step.steps.StepParameter;
import com.ys.idatrix.cloudetl.toolkit.analyzer.trans.step.StepDataRelationship;
import com.ys.idatrix.cloudetl.toolkit.analyzer.trans.step.StepDataRelationshipParser;
import com.ys.idatrix.cloudetl.toolkit.utils.RelationshipUtil;

import net.sf.json.JSONObject;

/**
 * Step - Replace String(字符串替换)
 * org.pentaho.di.trans.steps.replacestring.ReplaceStringMeta
 * 
 * @author XH
 * @since 2017年6月13日
 *
 */
@Component("SPReplaceString")
@Scope("prototype")
public class SPReplaceString implements StepParameter, StepDataRelationshipParser {

	List<ReplaceStringfieldInStreamDto> fieldInStream;

	/**
	 * @return fieldInStream
	 */
	public List<ReplaceStringfieldInStreamDto> getFieldInStream() {
		return fieldInStream;
	}

	/**
	 * @param fieldInStream
	 *            要设置的 fieldInStream
	 */
	public void setFieldInStream(List<ReplaceStringfieldInStreamDto> fieldInStream) {
		this.fieldInStream = fieldInStream;
	}

	/* 
	 * 
	 */
	@Override
	public Object getParameterObject(Object json) {
		JSONObject jsonObj = JSONObject.fromObject(json);
		Map<String, Class<?>> classMap = new HashMap<>();
		classMap.put("fieldInStream", ReplaceStringfieldInStreamDto.class);
		return (SPReplaceString) JSONObject.toBean(jsonObj, SPReplaceString.class, classMap);
	}

	/* 
	 * 
	 */
	@Override
	public Object encodeParameterObject(StepMeta stepMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();

		SPReplaceString spReplaceString = new SPReplaceString();
		ReplaceStringMeta replacestringmeta = (ReplaceStringMeta) stepMetaInterface;

		List<ReplaceStringfieldInStreamDto> fieldInStreamList = Lists.newArrayList();
		String[] fieldInStreams = replacestringmeta.getFieldInStream();
		String[] fieldOutStreams = replacestringmeta.getFieldOutStream();
		int[] useRegExs = replacestringmeta.getUseRegEx();
		String[] replaceStrings = replacestringmeta.getReplaceString();
		String[] replaceByStrings = replacestringmeta.getReplaceByString();
		boolean[] setEmptyStrings = replacestringmeta.isSetEmptyString();
		String[] replaceFieldByStrings = replacestringmeta.getFieldReplaceByString();
		int[] wholeWords = replacestringmeta.getWholeWord();
		int[] caseSensitives = replacestringmeta.getCaseSensitive();
		for (int i = 0; fieldInStreams != null && i < fieldInStreams.length; i++) {
			ReplaceStringfieldInStreamDto replacestringfieldinstreamdto = new ReplaceStringfieldInStreamDto();
			replacestringfieldinstreamdto.setFieldInStream(fieldInStreams[i]);
			replacestringfieldinstreamdto.setFieldOutStream(fieldOutStreams[i]);
			replacestringfieldinstreamdto.setUseRegEx(useRegExs[i]);
			replacestringfieldinstreamdto.setReplaceString(replaceStrings[i]);
			replacestringfieldinstreamdto.setReplaceByString(replaceByStrings[i]);
			replacestringfieldinstreamdto.setSetEmptyString(setEmptyStrings[i]);
			replacestringfieldinstreamdto.setReplaceFieldByString(replaceFieldByStrings[i]);
			replacestringfieldinstreamdto.setWholeWord(wholeWords[i]);
			replacestringfieldinstreamdto.setCaseSensitive(caseSensitives[i]);
			fieldInStreamList.add(replacestringfieldinstreamdto);
		}
		spReplaceString.setFieldInStream(fieldInStreamList);
		return spReplaceString;
	}

	/* 
	 * 
	 */
	@Override
	public void decodeParameterObject(StepMeta stepMeta, Object po, List<DatabaseMeta> databases,TransMeta transMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPReplaceString spReplaceString = (SPReplaceString) po;
		ReplaceStringMeta replacestringmeta = (ReplaceStringMeta) stepMetaInterface;

		if (spReplaceString.getFieldInStream() != null) {
			String[] fieldInStreams = new String[spReplaceString.getFieldInStream().size()];
			String[] fieldOutStreams = new String[spReplaceString.getFieldInStream().size()];
			int[] useRegExs = new int[spReplaceString.getFieldInStream().size()];
			String[] replaceStrings = new String[spReplaceString.getFieldInStream().size()];
			String[] replaceByStrings = new String[spReplaceString.getFieldInStream().size()];
			boolean[] setEmptyStrings = new boolean[spReplaceString.getFieldInStream().size()];
			String[] replaceFieldByStrings = new String[spReplaceString.getFieldInStream().size()];
			int[] wholeWords = new int[spReplaceString.getFieldInStream().size()];
			int[] caseSensitives = new int[spReplaceString.getFieldInStream().size()];
			for (int i = 0; i < spReplaceString.getFieldInStream().size(); i++) {
				ReplaceStringfieldInStreamDto replacestringfieldinstreamdto = spReplaceString.getFieldInStream().get(i);
				fieldInStreams[i] = replacestringfieldinstreamdto.getFieldInStream();
				fieldOutStreams[i] = replacestringfieldinstreamdto.getFieldOutStream();
				useRegExs[i] = replacestringfieldinstreamdto.getUseRegEx();
				replaceStrings[i] = replacestringfieldinstreamdto.getReplaceString();
				replaceByStrings[i] = replacestringfieldinstreamdto.getReplaceByString();
				setEmptyStrings[i] = replacestringfieldinstreamdto.isSetEmptyString();
				replaceFieldByStrings[i] = replacestringfieldinstreamdto.getReplaceFieldByString();
				wholeWords[i] = replacestringfieldinstreamdto.getWholeWord();
				caseSensitives[i] = replacestringfieldinstreamdto.getCaseSensitive();
			}
			replacestringmeta.setFieldInStream(fieldInStreams);
			replacestringmeta.setFieldOutStream(fieldOutStreams);
			replacestringmeta.setUseRegEx(useRegExs);
			replacestringmeta.setReplaceString(replaceStrings);
			replacestringmeta.setReplaceByString(replaceByStrings);
			replacestringmeta.setEmptyString(setEmptyStrings);
			replacestringmeta.setFieldReplaceByString(replaceFieldByStrings);
			replacestringmeta.setWholeWord(wholeWords);
			replacestringmeta.setCaseSensitive(caseSensitives);
		}

	}

	/*
	 * 覆盖方法：getStepDataAndRelationship
	 */
	@Override
	public void getStepDataAndRelationship(TransMeta transMeta, StepMeta stepMeta, StepDataRelationship sdr) throws Exception {
		
		String from= "转换:"+transMeta.getName()+",步骤:"+stepMeta.getName();
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		ReplaceStringMeta replacestringmeta = (ReplaceStringMeta) stepMetaInterface;
		//输入
		String[] in = replacestringmeta.getFieldInStream();
		String []  outs = replacestringmeta.getFieldOutStream();
		for(int i=0;i< outs.length ;i++) {
			if(!Utils.isEmpty(outs[i])) {
				sdr.addRelationship( RelationshipUtil.buildFieldRelationship(from, in[i], outs[i]) );
			}
		}
	}

}
