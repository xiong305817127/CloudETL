/**
 * 云化数据集成系统 iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.dto.step.steps.transfor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.row.value.ValueMetaBase;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.fieldsplitter.FieldSplitterMeta;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.ys.idatrix.cloudetl.dto.step.parts.FieldSplitterfieldNameDto;
import com.ys.idatrix.cloudetl.dto.step.steps.StepParameter;
import com.ys.idatrix.cloudetl.toolkit.analyzer.trans.step.StepDataRelationship;
import com.ys.idatrix.cloudetl.toolkit.analyzer.trans.step.StepDataRelationshipParser;
import com.ys.idatrix.cloudetl.toolkit.utils.RelationshipUtil;

import net.sf.json.JSONObject;

/**
 * Step -Field Splitter (拆分字段)
 * org.pentaho.di.trans.steps.fieldsplitter.FieldSplitterMeta
 * 
 * @author XH
 * @since 2017年6月13日
 *
 */
@Component("SPFieldSplitter")
@Scope("prototype")
public class SPFieldSplitter implements StepParameter, StepDataRelationshipParser {

	String splitField;
	String delimiter;
	String enclosure;
	List<FieldSplitterfieldNameDto> fieldName;

	/**
	 * @return splitField
	 */
	public String getSplitField() {
		return splitField;
	}

	/**
	 * @param splitField
	 *            要设置的 splitField
	 */
	public void setSplitField(String splitField) {
		this.splitField = splitField;
	}

	/**
	 * @return delimiter
	 */
	public String getDelimiter() {
		return delimiter;
	}

	/**
	 * @param delimiter
	 *            要设置的 delimiter
	 */
	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	/**
	 * @return enclosure
	 */
	public String getEnclosure() {
		return enclosure;
	}

	/**
	 * @param enclosure
	 *            要设置的 enclosure
	 */
	public void setEnclosure(String enclosure) {
		this.enclosure = enclosure;
	}

	/**
	 * @return fieldName
	 */
	public List<FieldSplitterfieldNameDto> getFieldName() {
		return fieldName;
	}

	/**
	 * @param fieldName
	 *            要设置的 fieldName
	 */
	public void setFieldName(List<FieldSplitterfieldNameDto> fieldName) {
		this.fieldName = fieldName;
	}

	/* 
	 * 
	 */
	@Override
	public Object getParameterObject(Object json) {
		JSONObject jsonObj = JSONObject.fromObject(json);
		Map<String, Class<?>> classMap = new HashMap<>();
		classMap.put("fieldName", FieldSplitterfieldNameDto.class);
		return (SPFieldSplitter) JSONObject.toBean(jsonObj, SPFieldSplitter.class, classMap);
	}

	/* 
	 * 
	 */
	@Override
	public Object encodeParameterObject(StepMeta stepMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPFieldSplitter spFieldSplitter = new SPFieldSplitter();
		FieldSplitterMeta fieldsplittermeta = (FieldSplitterMeta) stepMetaInterface;

		spFieldSplitter.setSplitField(fieldsplittermeta.getSplitField());
		spFieldSplitter.setDelimiter(fieldsplittermeta.getDelimiter());
		spFieldSplitter.setEnclosure(fieldsplittermeta.getEnclosure());

		List<FieldSplitterfieldNameDto> fieldNameList = Lists.newArrayList();
		String[] fieldNames = fieldsplittermeta.getFieldName();
		String[] fieldIDs = fieldsplittermeta.getFieldID();
		boolean[] fieldRemoveIDs = fieldsplittermeta.getFieldRemoveID();
		int[] fieldTypes = fieldsplittermeta.getFieldType();
		String[] fieldFormats = fieldsplittermeta.getFieldFormat();
		String[] fieldGroups = fieldsplittermeta.getFieldGroup();
		String[] fieldDecimals = fieldsplittermeta.getFieldDecimal();
		String[] fieldCurrencys = fieldsplittermeta.getFieldCurrency();
		int[] fieldLengths = fieldsplittermeta.getFieldLength();
		int[] fieldPrecisions = fieldsplittermeta.getFieldPrecision();
		String[] fieldNullIfs = fieldsplittermeta.getFieldNullIf();
		String[] fieldIfNulls = fieldsplittermeta.getFieldIfNull();
		int[] fieldTrimTypes = fieldsplittermeta.getFieldTrimType();
		for (int i = 0; fieldNames != null && i < fieldNames.length; i++) {
			FieldSplitterfieldNameDto fieldsplitterfieldnamedto = new FieldSplitterfieldNameDto();
			fieldsplitterfieldnamedto.setFieldName(fieldNames[i]);
			fieldsplitterfieldnamedto.setFieldID(fieldIDs[i]);
			fieldsplitterfieldnamedto.setFieldRemoveID(fieldRemoveIDs[i]);
			fieldsplitterfieldnamedto.setFieldType(fieldTypes[i]);
			fieldsplitterfieldnamedto.setFieldFormat(fieldFormats[i]);
			fieldsplitterfieldnamedto.setFieldGroup(fieldGroups[i]);
			fieldsplitterfieldnamedto.setFieldDecimal(fieldDecimals[i]);
			fieldsplitterfieldnamedto.setFieldCurrency(fieldCurrencys[i]);
			fieldsplitterfieldnamedto.setFieldLength(fieldLengths[i]);
			fieldsplitterfieldnamedto.setFieldPrecision(fieldPrecisions[i]);
			fieldsplitterfieldnamedto.setFieldNullIf(fieldNullIfs[i]);
			fieldsplitterfieldnamedto.setFieldIfNull(fieldIfNulls[i]);
			fieldsplitterfieldnamedto.setTrimType( ValueMetaBase.getTrimTypeCode(fieldTrimTypes[i]));
			fieldNameList.add(fieldsplitterfieldnamedto);
		}
		spFieldSplitter.setFieldName(fieldNameList);
		return spFieldSplitter;
	}

	/* 
	 * 
	 */
	@Override
	public void decodeParameterObject(StepMeta stepMeta, Object po, List<DatabaseMeta> databases,TransMeta transMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPFieldSplitter spFieldSplitter = (SPFieldSplitter) po;
		FieldSplitterMeta fieldsplittermeta = (FieldSplitterMeta) stepMetaInterface;

		fieldsplittermeta.setSplitField(spFieldSplitter.getSplitField());
		fieldsplittermeta.setDelimiter(spFieldSplitter.getDelimiter());
		fieldsplittermeta.setEnclosure(spFieldSplitter.getEnclosure());
		if (spFieldSplitter.getFieldName() != null) {
			String[] fieldNames = new String[spFieldSplitter.getFieldName().size()];
			String[] fieldIDs = new String[spFieldSplitter.getFieldName().size()];
			boolean[] fieldRemoveIDs = new boolean[spFieldSplitter.getFieldName().size()];
			int[] fieldTypes = new int[spFieldSplitter.getFieldName().size()];
			String[] fieldFormats = new String[spFieldSplitter.getFieldName().size()];
			String[] fieldGroups = new String[spFieldSplitter.getFieldName().size()];
			String[] fieldDecimals = new String[spFieldSplitter.getFieldName().size()];
			String[] fieldCurrencys = new String[spFieldSplitter.getFieldName().size()];
			int[] fieldLengths = new int[spFieldSplitter.getFieldName().size()];
			int[] fieldPrecisions = new int[spFieldSplitter.getFieldName().size()];
			String[] fieldNullIfs = new String[spFieldSplitter.getFieldName().size()];
			String[] fieldIfNulls = new String[spFieldSplitter.getFieldName().size()];
			int[] fieldTrimTypes = new int[spFieldSplitter.getFieldName().size()];
			for (int i = 0; i < spFieldSplitter.getFieldName().size(); i++) {
				FieldSplitterfieldNameDto fieldsplitterfieldnamedto = spFieldSplitter.getFieldName().get(i);
				fieldNames[i] = fieldsplitterfieldnamedto.getFieldName();
				fieldIDs[i] = fieldsplitterfieldnamedto.getFieldID();
				fieldRemoveIDs[i] = fieldsplitterfieldnamedto.isFieldRemoveID();
				fieldTypes[i] = fieldsplitterfieldnamedto.getFieldType();
				fieldFormats[i] = fieldsplitterfieldnamedto.getFieldFormat();
				fieldGroups[i] = fieldsplitterfieldnamedto.getFieldGroup();
				fieldDecimals[i] = fieldsplitterfieldnamedto.getFieldDecimal();
				fieldCurrencys[i] = fieldsplitterfieldnamedto.getFieldCurrency();
				fieldLengths[i] = fieldsplitterfieldnamedto.getFieldLength();
				fieldPrecisions[i] = fieldsplitterfieldnamedto.getFieldPrecision();
				fieldNullIfs[i] = fieldsplitterfieldnamedto.getFieldNullIf();
				fieldIfNulls[i] = fieldsplitterfieldnamedto.getFieldIfNull();
				fieldTrimTypes[i] = ValueMetaBase.getTrimTypeByCode(fieldsplitterfieldnamedto.getTrimType());
			}
			fieldsplittermeta.setFieldName(fieldNames);
			fieldsplittermeta.setFieldID(fieldIDs);
			fieldsplittermeta.setFieldRemoveID(fieldRemoveIDs);
			fieldsplittermeta.setFieldType(fieldTypes);
			fieldsplittermeta.setFieldFormat(fieldFormats);
			fieldsplittermeta.setFieldGroup(fieldGroups);
			fieldsplittermeta.setFieldDecimal(fieldDecimals);
			fieldsplittermeta.setFieldCurrency(fieldCurrencys);
			fieldsplittermeta.setFieldLength(fieldLengths);
			fieldsplittermeta.setFieldPrecision(fieldPrecisions);
			fieldsplittermeta.setFieldNullIf(fieldNullIfs);
			fieldsplittermeta.setFieldIfNull(fieldIfNulls);
			fieldsplittermeta.setFieldTrimType(fieldTrimTypes);
		}

	}

	/*
	 * 覆盖方法：getStepDataAndRelationship
	 */
	@Override
	public void getStepDataAndRelationship(TransMeta transMeta, StepMeta stepMeta, StepDataRelationship sdr) throws Exception {
		
		String from= "转换:"+transMeta.getName()+",步骤:"+stepMeta.getName();
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		FieldSplitterMeta fieldsplittermeta = (FieldSplitterMeta) stepMetaInterface;
		//输入
		String sf = fieldsplittermeta.getSplitField() ;
		//输出
		if (fieldsplittermeta.getFieldName() != null) {
			for(String fn : fieldsplittermeta.getFieldName()) {
				sdr.addRelationship( RelationshipUtil.buildFieldRelationship(from, sf, fn) );

			}
		}
	}

}
