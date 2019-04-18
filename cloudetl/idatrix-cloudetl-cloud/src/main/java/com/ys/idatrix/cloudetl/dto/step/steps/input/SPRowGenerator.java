package com.ys.idatrix.cloudetl.dto.step.steps.input;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.rowgenerator.RowGeneratorData;
import org.pentaho.di.trans.steps.rowgenerator.RowGeneratorMeta;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.ys.idatrix.cloudetl.dto.step.parts.RowGeneratorfieldNameDto;
import com.ys.idatrix.cloudetl.dto.step.steps.StepParameter;
import com.ys.idatrix.cloudetl.recovery.trans.ResumeStepDataParser;
import com.ys.idatrix.cloudetl.recovery.trans.dto.StepLinesDto;
import com.ys.idatrix.cloudetl.toolkit.analyzer.trans.step.StepDataRelationship;
import com.ys.idatrix.cloudetl.toolkit.analyzer.trans.step.StepDataRelationshipParser;
import com.ys.idatrix.cloudetl.toolkit.domain.Relationship;
import com.ys.idatrix.cloudetl.toolkit.utils.RelationshipUtil;

import net.sf.json.JSONObject;

/**
 * Step - RowGenerator(生成记录). 转换
 * org.pentaho.di.trans.steps.rowgenerator.RowGeneratorMeta
 * 
 * @author XH
 * @since 2018-04-11
 */
@Component("SPRowGenerator")
@Scope("prototype")
public class SPRowGenerator implements StepParameter, StepDataRelationshipParser, ResumeStepDataParser {

	List<RowGeneratorfieldNameDto> fieldName;
	String rowLimit;
	boolean neverEnding;
	String intervalInMs;
	String rowTimeField;
	String lastTimeField;

	/**
	 * @return the fieldName
	 */
	public List<RowGeneratorfieldNameDto> getFieldName() {
		return fieldName;
	}

	/**
	 * @param 设置
	 *            fieldName
	 */
	public void setFieldName(List<RowGeneratorfieldNameDto> fieldName) {
		this.fieldName = fieldName;
	}

	/**
	 * @return the rowLimit
	 */
	public String getRowLimit() {
		return rowLimit;
	}

	/**
	 * @param 设置
	 *            rowLimit
	 */
	public void setRowLimit(String rowLimit) {
		this.rowLimit = rowLimit;
	}

	/**
	 * @return the neverEnding
	 */
	public boolean isNeverEnding() {
		return neverEnding;
	}

	/**
	 * @param 设置
	 *            neverEnding
	 */
	public void setNeverEnding(boolean neverEnding) {
		this.neverEnding = neverEnding;
	}

	/**
	 * @return the intervalInMs
	 */
	public String getIntervalInMs() {
		return intervalInMs;
	}

	/**
	 * @param 设置
	 *            intervalInMs
	 */
	public void setIntervalInMs(String intervalInMs) {
		this.intervalInMs = intervalInMs;
	}

	/**
	 * @return the rowTimeField
	 */
	public String getRowTimeField() {
		return rowTimeField;
	}

	/**
	 * @param 设置
	 *            rowTimeField
	 */
	public void setRowTimeField(String rowTimeField) {
		this.rowTimeField = rowTimeField;
	}

	/**
	 * @return the lastTimeField
	 */
	public String getLastTimeField() {
		return lastTimeField;
	}

	/**
	 * @param 设置
	 *            lastTimeField
	 */
	public void setLastTimeField(String lastTimeField) {
		this.lastTimeField = lastTimeField;
	}

	@Override
	public Object getParameterObject(Object json) {
		JSONObject jsonObj = JSONObject.fromObject(json);
		Map<String, Class<?>> classMap = new HashMap<>();
		classMap.put("fieldName", RowGeneratorfieldNameDto.class);
		return (SPRowGenerator) JSONObject.toBean(jsonObj, SPRowGenerator.class, classMap);
	}

	@Override
	public Object encodeParameterObject(StepMeta stepMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPRowGenerator spRowGenerator = new SPRowGenerator();
		RowGeneratorMeta rowgeneratormeta = (RowGeneratorMeta) stepMetaInterface;

		spRowGenerator.setIntervalInMs(rowgeneratormeta.getIntervalInMs());
		spRowGenerator.setRowTimeField(rowgeneratormeta.getRowTimeField());
		spRowGenerator.setLastTimeField(rowgeneratormeta.getLastTimeField());

		List<RowGeneratorfieldNameDto> fieldNameList = Lists.newArrayList();
		String[] fieldNames = rowgeneratormeta.getFieldName();
		String[] fieldTypes = rowgeneratormeta.getFieldType();
		String[] fieldFormats = rowgeneratormeta.getFieldFormat();
		String[] currencys = rowgeneratormeta.getCurrency();
		String[] decimals = rowgeneratormeta.getDecimal();
		String[] groups = rowgeneratormeta.getGroup();
		String[] values = rowgeneratormeta.getValue();
		int[] fieldLengths = rowgeneratormeta.getFieldLength();
		int[] fieldPrecisions = rowgeneratormeta.getFieldPrecision();
		boolean[] setEmptyStrings = rowgeneratormeta.getSetEmptyString();
		for (int i = 0; fieldNames != null && i < fieldNames.length; i++) {
			RowGeneratorfieldNameDto rowgeneratorfieldnamedto = new RowGeneratorfieldNameDto();
			rowgeneratorfieldnamedto.setFieldName(fieldNames[i]);
			rowgeneratorfieldnamedto.setFieldType(fieldTypes[i]);
			rowgeneratorfieldnamedto.setFieldFormat(fieldFormats[i]);
			rowgeneratorfieldnamedto.setCurrency(currencys[i]);
			rowgeneratorfieldnamedto.setDecimal(decimals[i]);
			rowgeneratorfieldnamedto.setGroup(groups[i]);
			rowgeneratorfieldnamedto.setValue(values[i]);
			rowgeneratorfieldnamedto.setFieldLength(fieldLengths[i]);
			rowgeneratorfieldnamedto.setFieldPrecision(fieldPrecisions[i]);
			rowgeneratorfieldnamedto.setSetEmptyString(setEmptyStrings[i]);
			fieldNameList.add(rowgeneratorfieldnamedto);
		}
		spRowGenerator.setFieldName(fieldNameList);
		spRowGenerator.setRowLimit(rowgeneratormeta.getRowLimit());
		spRowGenerator.setNeverEnding(rowgeneratormeta.isNeverEnding());
		return spRowGenerator;
	}

	@Override
	public void decodeParameterObject(StepMeta stepMeta, Object po, List<DatabaseMeta> databases,TransMeta transMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPRowGenerator spRowGenerator = (SPRowGenerator) po;
		RowGeneratorMeta rowgeneratormeta = (RowGeneratorMeta) stepMetaInterface;

		rowgeneratormeta.setNeverEnding(spRowGenerator.isNeverEnding());
		rowgeneratormeta.setIntervalInMs(spRowGenerator.getIntervalInMs());
		rowgeneratormeta.setRowTimeField(spRowGenerator.getRowTimeField());
		rowgeneratormeta.setLastTimeField(spRowGenerator.getLastTimeField());
		if (spRowGenerator.getFieldName() != null) {
			String[] fieldNames = new String[spRowGenerator.getFieldName().size()];
			String[] fieldTypes = new String[spRowGenerator.getFieldName().size()];
			String[] fieldFormats = new String[spRowGenerator.getFieldName().size()];
			String[] currencys = new String[spRowGenerator.getFieldName().size()];
			String[] decimals = new String[spRowGenerator.getFieldName().size()];
			String[] groups = new String[spRowGenerator.getFieldName().size()];
			String[] values = new String[spRowGenerator.getFieldName().size()];
			int[] fieldLengths = new int[spRowGenerator.getFieldName().size()];
			int[] fieldPrecisions = new int[spRowGenerator.getFieldName().size()];
			boolean[] setEmptyStrings = new boolean[spRowGenerator.getFieldName().size()];
			for (int i = 0; i < spRowGenerator.getFieldName().size(); i++) {
				RowGeneratorfieldNameDto rowgeneratorfieldnamedto = spRowGenerator.getFieldName().get(i);
				fieldNames[i] = rowgeneratorfieldnamedto.getFieldName();
				fieldTypes[i] = rowgeneratorfieldnamedto.getFieldType();
				fieldFormats[i] = rowgeneratorfieldnamedto.getFieldFormat();
				currencys[i] = rowgeneratorfieldnamedto.getCurrency();
				decimals[i] = rowgeneratorfieldnamedto.getDecimal();
				groups[i] = rowgeneratorfieldnamedto.getGroup();
				values[i] = rowgeneratorfieldnamedto.getValue();
				fieldLengths[i] = rowgeneratorfieldnamedto.getFieldLength();
				fieldPrecisions[i] = rowgeneratorfieldnamedto.getFieldPrecision();
				setEmptyStrings[i] = rowgeneratorfieldnamedto.isSetEmptyString();
			}
			rowgeneratormeta.setFieldName(fieldNames);
			rowgeneratormeta.setFieldType(fieldTypes);
			rowgeneratormeta.setFieldFormat(fieldFormats);
			rowgeneratormeta.setCurrency(currencys);
			rowgeneratormeta.setDecimal(decimals);
			rowgeneratormeta.setGroup(groups);
			rowgeneratormeta.setValue(values);
			rowgeneratormeta.setFieldLength(fieldLengths);
			rowgeneratormeta.setFieldPrecision(fieldPrecisions);
			rowgeneratormeta.setSetEmptyString(setEmptyStrings);
		}
		rowgeneratormeta.setRowLimit(spRowGenerator.getRowLimit());
	}

	@Override
	public void getStepDataAndRelationship(TransMeta transMeta, StepMeta stepMeta, StepDataRelationship sdr)
			throws Exception {

		String from = "转换:" + transMeta.getName() + ",步骤:" + stepMeta.getName();

		RowGeneratorMeta rowgeneratormeta = (RowGeneratorMeta) stepMeta.getStepMetaInterface();
		String[] fieldNames = rowgeneratormeta.getFieldName();
		for (int i = 0; fieldNames != null && i < fieldNames.length; i++) {
			String fieldName = fieldNames[i];
			String dummyId = transMeta.getName() + "-" + stepMeta.getName() ;
			
			Relationship relationship = RelationshipUtil.buildDummyRelationship(from, dummyId, fieldName);
			sdr.addRelationship(relationship);
			sdr.addInputDataNode(relationship.getStartNode());
		}
	}

	@Override
	 public boolean resumeCacheData(Map<Object,Object> cacheData,StepLinesDto linesDto ,TransMeta transMeta ,StepMeta stepMeta ,StepMetaInterface stepMetaInterface,StepDataInterface stepDataInterface , StepInterface stepInterface)  throws Exception {

		RowGeneratorData data = (RowGeneratorData )stepDataInterface ;
		data.rowsWritten = linesDto.getRowLine(); 
		return true;
	}
	
	@Override
	public int stepType() {
		return 1;
	}

}
