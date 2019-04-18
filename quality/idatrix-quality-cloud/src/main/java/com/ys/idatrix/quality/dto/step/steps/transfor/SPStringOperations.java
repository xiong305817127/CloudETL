/**
 * 云化数据集成系统 iDatrix CloudETL
 */
package com.ys.idatrix.quality.dto.step.steps.transfor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.row.value.ValueMetaBase;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.stringoperations.StringOperationsMeta;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.ys.idatrix.quality.dto.step.parts.StringOperationsfieldInStreamDto;
import com.ys.idatrix.quality.dto.step.steps.StepParameter;
import com.ys.idatrix.quality.toolkit.analyzer.trans.step.StepDataRelationship;
import com.ys.idatrix.quality.toolkit.analyzer.trans.step.StepDataRelationshipParser;
import com.ys.idatrix.quality.toolkit.utils.RelationshipUtil;

import net.sf.json.JSONObject;

/**
 * Step -String Operations(字符串操作)
 * org.pentaho.di.trans.steps.stringoperations.StringOperationsMeta
 * 
 * @author XH
 * @since 2017年6月13日
 *
 */
@Component("SPStringOperations")
@Scope("prototype")
public class SPStringOperations implements StepParameter, StepDataRelationshipParser {

	List<StringOperationsfieldInStreamDto> fieldInStream;

	/**
	 * @return fieldInStream
	 */
	public List<StringOperationsfieldInStreamDto> getFieldInStream() {
		return fieldInStream;
	}

	/**
	 * @param fieldInStream
	 *            要设置的 fieldInStream
	 */
	public void setFieldInStream(List<StringOperationsfieldInStreamDto> fieldInStream) {
		this.fieldInStream = fieldInStream;
	}

	/* 
	 * 
	 */
	@Override
	public Object getParameterObject(Object json) {
		JSONObject jsonObj = JSONObject.fromObject(json);
		Map<String, Class<?>> classMap = new HashMap<>();
		classMap.put("fieldInStream", StringOperationsfieldInStreamDto.class);
		return (SPStringOperations) JSONObject.toBean(jsonObj, SPStringOperations.class, classMap);
	}

	/* 
	 * 
	 */
	@Override
	public Object encodeParameterObject(StepMeta stepMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPStringOperations spStringOperations = new SPStringOperations();
		StringOperationsMeta stringoperationsmeta = (StringOperationsMeta) stepMetaInterface;

		List<StringOperationsfieldInStreamDto> fieldInStreamList = Lists.newArrayList();
		String[] fieldInStreams = stringoperationsmeta.getFieldInStream();
		String[] fieldOutStreams = stringoperationsmeta.getFieldOutStream();
		int[] trimTypes = stringoperationsmeta.getTrimType();
		int[] lowerUppers = stringoperationsmeta.getLowerUpper();
		int[] padding_types = stringoperationsmeta.getPaddingType();
		String[] padChars = stringoperationsmeta.getPadChar();
		String[] padLens = stringoperationsmeta.getPadLen();
		int[] initCaps = stringoperationsmeta.getInitCap();
		int[] maskXMLs = stringoperationsmeta.getMaskXML();
		int[] digitss = stringoperationsmeta.getDigits();
		int[] removeSpecialCharacterss = stringoperationsmeta.getRemoveSpecialCharacters();
		for (int i = 0; fieldInStreams != null && i < fieldInStreams.length; i++) {
			StringOperationsfieldInStreamDto stringoperationsfieldinstreamdto = new StringOperationsfieldInStreamDto();
			stringoperationsfieldinstreamdto.setFieldInStream(fieldInStreams[i]);
			stringoperationsfieldinstreamdto.setFieldOutStream(fieldOutStreams[i]);
			stringoperationsfieldinstreamdto.setTrimType(ValueMetaBase.getTrimTypeCode(trimTypes[i]));
			stringoperationsfieldinstreamdto.setLowerUpper(lowerUppers[i]);
			stringoperationsfieldinstreamdto.setPadding_type(padding_types[i]);
			stringoperationsfieldinstreamdto.setPadChar(padChars[i]);
			stringoperationsfieldinstreamdto.setPadLen(padLens[i]);
			stringoperationsfieldinstreamdto.setInitCap(initCaps[i]);
			stringoperationsfieldinstreamdto.setMaskXML(maskXMLs[i]);
			stringoperationsfieldinstreamdto.setDigits(digitss[i]);
			stringoperationsfieldinstreamdto.setRemoveSpecialCharacters(removeSpecialCharacterss[i]);
			fieldInStreamList.add(stringoperationsfieldinstreamdto);
		}
		spStringOperations.setFieldInStream(fieldInStreamList);
		return spStringOperations;
	}

	/* 
	 * 
	 */
	@Override
	public void decodeParameterObject(StepMeta stepMeta, Object po, List<DatabaseMeta> databases,TransMeta transMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPStringOperations spStringOperations = (SPStringOperations) po;
		StringOperationsMeta stringoperationsmeta = (StringOperationsMeta) stepMetaInterface;

		if (spStringOperations.getFieldInStream() != null) {
			String[] fieldInStreams = new String[spStringOperations.getFieldInStream().size()];
			String[] fieldOutStreams = new String[spStringOperations.getFieldInStream().size()];
			int[] trimTypes = new int[spStringOperations.getFieldInStream().size()];
			int[] lowerUppers = new int[spStringOperations.getFieldInStream().size()];
			int[] padding_types = new int[spStringOperations.getFieldInStream().size()];
			String[] padChars = new String[spStringOperations.getFieldInStream().size()];
			String[] padLens = new String[spStringOperations.getFieldInStream().size()];
			int[] initCaps = new int[spStringOperations.getFieldInStream().size()];
			int[] maskXMLs = new int[spStringOperations.getFieldInStream().size()];
			int[] digitss = new int[spStringOperations.getFieldInStream().size()];
			int[] removeSpecialCharacterss = new int[spStringOperations.getFieldInStream().size()];
			for (int i = 0; i < spStringOperations.getFieldInStream().size(); i++) {
				StringOperationsfieldInStreamDto stringoperationsfieldinstreamdto = spStringOperations
						.getFieldInStream().get(i);
				fieldInStreams[i] = stringoperationsfieldinstreamdto.getFieldInStream();
				fieldOutStreams[i] = stringoperationsfieldinstreamdto.getFieldOutStream();
				trimTypes[i] = ValueMetaBase.getTrimTypeByCode(stringoperationsfieldinstreamdto.getTrimType());
				lowerUppers[i] = stringoperationsfieldinstreamdto.getLowerUpper();
				padding_types[i] = stringoperationsfieldinstreamdto.getPadding_type();
				padChars[i] = stringoperationsfieldinstreamdto.getPadChar();
				padLens[i] = stringoperationsfieldinstreamdto.getPadLen();
				initCaps[i] = stringoperationsfieldinstreamdto.getInitCap();
				maskXMLs[i] = stringoperationsfieldinstreamdto.getMaskXML();
				digitss[i] = stringoperationsfieldinstreamdto.getDigits();
				removeSpecialCharacterss[i] = stringoperationsfieldinstreamdto.getRemoveSpecialCharacters();
			}
			stringoperationsmeta.setFieldInStream(fieldInStreams);
			stringoperationsmeta.setFieldOutStream(fieldOutStreams);
			stringoperationsmeta.setTrimType(trimTypes);
			stringoperationsmeta.setLowerUpper(lowerUppers);
			stringoperationsmeta.setPaddingType(padding_types);
			stringoperationsmeta.setPadChar(padChars);
			stringoperationsmeta.setPadLen(padLens);
			stringoperationsmeta.setInitCap(initCaps);
			stringoperationsmeta.setMaskXML(maskXMLs);
			stringoperationsmeta.setDigits(digitss);
			stringoperationsmeta.setRemoveSpecialCharacters(removeSpecialCharacterss);
		}

	}

	/*
	 * 覆盖方法：getStepDataAndRelationship
	 */
	@Override
	public void getStepDataAndRelationship(TransMeta transMeta, StepMeta stepMeta, StepDataRelationship sdr) throws Exception {

		String from = "转换:" + transMeta.getName() + ",步骤:" + stepMeta.getName();
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		StringOperationsMeta stringoperationsmeta = (StringOperationsMeta) stepMetaInterface;
		// 输入
		String[] fieldInStreams = stringoperationsmeta.getFieldInStream();
		// 输出
		String[] fieldOutStreams = stringoperationsmeta.getFieldOutStream();
		for (int i = 0; i < fieldOutStreams.length; i++) {
			if (fieldOutStreams[i] != null) {
				sdr.addRelationship( RelationshipUtil.buildFieldRelationship(from, fieldInStreams[i], fieldOutStreams[i]));
			}
		}
	}

}
