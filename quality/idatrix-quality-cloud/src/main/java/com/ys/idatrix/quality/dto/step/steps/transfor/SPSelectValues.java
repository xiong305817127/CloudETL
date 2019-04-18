/**
 * 云化数据集成系统 iDatrix CloudETL
 */
package com.ys.idatrix.quality.dto.step.steps.transfor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.selectvalues.SelectMetadataChange;
import org.pentaho.di.trans.steps.selectvalues.SelectValuesMeta;
import org.pentaho.di.trans.steps.selectvalues.SelectValuesMeta.SelectField;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ys.idatrix.quality.dto.step.parts.SelectValuesSelectFieldDto;
import com.ys.idatrix.quality.dto.step.parts.SelectValuesSelectMetadataChangeDto;
import com.ys.idatrix.quality.dto.step.steps.StepParameter;
import com.ys.idatrix.quality.toolkit.analyzer.trans.step.StepDataRelationship;
import com.ys.idatrix.quality.toolkit.analyzer.trans.step.StepDataRelationshipParser;
import com.ys.idatrix.quality.toolkit.utils.RelationshipUtil;

import net.sf.json.JSONObject;

/**
 * Step - Select Values(字段选择)
 * org.pentaho.di.trans.steps.selectvalues.SelectValuesMeta
 * 
 * @author XH
 * @since 2017年6月13日
 *
 */
@Component("SPSelectValues")
@Scope("prototype")
public class SPSelectValues implements StepParameter, StepDataRelationshipParser {

	List<SelectValuesSelectFieldDto> selectFields;
	boolean selectingAndSortingUnspecifiedFields;
	List<String> deleteName;
	List<SelectValuesSelectMetadataChangeDto> selectMetadataChange;

	/**
	 * @return selectFields
	 */
	public List<SelectValuesSelectFieldDto> getSelectFields() {
		return selectFields;
	}

	/**
	 * @param selectFields
	 *            要设置的 selectFields
	 */
	public void setSelectFields(List<SelectValuesSelectFieldDto> selectFields) {
		this.selectFields = selectFields;
	}

	/**
	 * @return selectingAndSortingUnspecifiedFields
	 */
	public boolean isSelectingAndSortingUnspecifiedFields() {
		return selectingAndSortingUnspecifiedFields;
	}

	/**
	 * @param selectingAndSortingUnspecifiedFields
	 *            要设置的 selectingAndSortingUnspecifiedFields
	 */
	public void setSelectingAndSortingUnspecifiedFields(boolean selectingAndSortingUnspecifiedFields) {
		this.selectingAndSortingUnspecifiedFields = selectingAndSortingUnspecifiedFields;
	}

	/**
	 * @return deleteName
	 */
	public List<String> getDeleteName() {
		return deleteName;
	}

	/**
	 * @param deleteName
	 *            要设置的 deleteName
	 */
	public void setDeleteName(List<String> deleteName) {
		this.deleteName = deleteName;
	}

	/**
	 * @return selectMetadataChange
	 */
	public List<SelectValuesSelectMetadataChangeDto> getSelectMetadataChange() {
		return selectMetadataChange;
	}

	/**
	 * @param selectMetadataChange
	 *            要设置的 selectMetadataChange
	 */
	public void setSelectMetadataChange(List<SelectValuesSelectMetadataChangeDto> selectMetadataChange) {
		this.selectMetadataChange = selectMetadataChange;
	}

	/* 
	 * 
	 */
	@Override
	public Object getParameterObject(Object json) {
		JSONObject jsonObj = JSONObject.fromObject(json);
		Map<String, Class<?>> classMap = new HashMap<>();
		classMap.put("selectFields", SelectValuesSelectFieldDto.class);
		classMap.put("selectMetadataChange", SelectValuesSelectMetadataChangeDto.class);
		return (SPSelectValues) JSONObject.toBean(jsonObj, SPSelectValues.class, classMap);
	}

	/* 
	 * 
	 */
	@Override
	public Object encodeParameterObject(StepMeta stepMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();

		SPSelectValues spSelectValues = new SPSelectValues();
		SelectValuesMeta selectvaluesmeta = (SelectValuesMeta) stepMetaInterface;

		spSelectValues
				.setSelectingAndSortingUnspecifiedFields(selectvaluesmeta.isSelectingAndSortingUnspecifiedFields());
		if (selectvaluesmeta.getDeleteName() != null) {
			spSelectValues.setDeleteName(Arrays.asList(selectvaluesmeta.getDeleteName()));
		}

		SelectField[] selectFieldsArray = selectvaluesmeta.getSelectFields();
		List<SelectValuesSelectFieldDto> selectFieldsList = Arrays.asList(selectFieldsArray).stream().map(temp1 -> {
			SelectValuesSelectFieldDto temp2 = new SelectValuesSelectFieldDto();
			temp2.setLength(temp1.getLength() < 0 ? -1 : temp1.getLength());
			temp2.setName(temp1.getName());
			temp2.setPrecision(temp1.getPrecision() < 0 ? -1 : temp1.getPrecision());
			temp2.setRename(temp1.getRename());
			return temp2;
		}).collect(Collectors.toList());
		spSelectValues.setSelectFields(selectFieldsList);

		SelectMetadataChange[] selectmetaArray = selectvaluesmeta.getMeta();
		List<SelectValuesSelectMetadataChangeDto> selectmetaList = Arrays.asList(selectmetaArray).stream()
				.map(temp1 -> {
					SelectValuesSelectMetadataChangeDto temp2 = new SelectValuesSelectMetadataChangeDto();
					temp2.setConversionMask(temp1.getConversionMask());
					temp2.setCurrencySymbol(temp1.getCurrencySymbol());
					temp2.setDateFormatLenient(temp1.isDateFormatLenient());
					temp2.setDateFormatLocale(temp1.getDateFormatLocale());
					temp2.setDateFormatTimeZone(temp1.getDateFormatTimeZone());
					temp2.setDecimalSymbol(temp1.getDecimalSymbol());
					temp2.setEncoding(temp1.getEncoding());
					temp2.setGroupingSymbol(temp1.getGroupingSymbol());
					temp2.setLength(temp1.getLength() < 0 ? -1 : temp1.getLength());
					temp2.setLenientStringToNumber(temp1.isLenientStringToNumber());
					temp2.setName(temp1.getName());
					temp2.setPrecision(temp1.getPrecision() < 0 ? -1 : temp1.getPrecision());
					temp2.setRename(temp1.getRename());
					temp2.setStorageType(temp1.getStorageType());
					temp2.setType(temp1.getType());
					return temp2;
				}).collect(Collectors.toList());
		spSelectValues.setSelectMetadataChange(selectmetaList);

		return spSelectValues;

	}

	/* 
	 * 
	 */
	@Override
	public void decodeParameterObject(StepMeta stepMeta, Object po, List<DatabaseMeta> databases,TransMeta transMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPSelectValues spSelectValues = (SPSelectValues) po;
		SelectValuesMeta selectvaluesmeta = (SelectValuesMeta) stepMetaInterface;

		selectvaluesmeta
				.setSelectingAndSortingUnspecifiedFields(spSelectValues.isSelectingAndSortingUnspecifiedFields());
		if (spSelectValues.getDeleteName() != null) {
			selectvaluesmeta.setDeleteName(
					spSelectValues.getDeleteName().toArray(new String[spSelectValues.getDeleteName().size()]));
		}

		List<SelectField> selectFieldList = spSelectValues.getSelectFields().stream().map(temp1 -> {
			SelectField temp2 = new SelectField();
			temp2.setLength(temp1.getLength());
			temp2.setName(temp1.getName());
			temp2.setPrecision(temp1.getPrecision());
			temp2.setRename(temp1.getRename());
			return temp2;
		}).collect(Collectors.toList());
		selectvaluesmeta
				.setSelectFields(selectFieldList.toArray(new SelectField[spSelectValues.getSelectFields().size()]));

		List<SelectMetadataChange> selectmetaList = spSelectValues.getSelectMetadataChange().stream().map(temp1 -> {
			SelectMetadataChange temp2 = new SelectMetadataChange(selectvaluesmeta);
			temp2.setConversionMask(temp1.getConversionMask());
			temp2.setCurrencySymbol(temp1.getCurrencySymbol());
			temp2.setDateFormatLenient(temp1.isDateFormatLenient());
			temp2.setDateFormatLocale(temp1.getDateFormatLocale());
			temp2.setDateFormatTimeZone(temp1.getDateFormatTimeZone());
			temp2.setDecimalSymbol(temp1.getDecimalSymbol());
			temp2.setEncoding(temp1.getEncoding());
			temp2.setGroupingSymbol(temp1.getGroupingSymbol());
			temp2.setLength(temp1.getLength());
			temp2.setLenientStringToNumber(temp1.isLenientStringToNumber());
			temp2.setName(temp1.getName());
			temp2.setPrecision(temp1.getPrecision());
			temp2.setRename(temp1.getRename());
			temp2.setStorageType(temp1.getStorageType());
			temp2.setType(temp1.getType());
			return temp2;
		}).collect(Collectors.toList());
		selectvaluesmeta.setMeta(
				selectmetaList.toArray(new SelectMetadataChange[spSelectValues.getSelectMetadataChange().size()]));

	}

	/*
	 * 覆盖方法：getStepDataAndRelationship
	 */
	@Override
	public void getStepDataAndRelationship(TransMeta transMeta, StepMeta stepMeta, StepDataRelationship sdr) {
		
		String from= "转换:"+transMeta.getName()+",步骤:"+stepMeta.getName();
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SelectValuesMeta  selectvaluesmeta= (SelectValuesMeta )stepMetaInterface;

		SelectField[] selectFieldsArray = selectvaluesmeta.getSelectFields();
		if(selectFieldsArray != null ) {
			Arrays.asList(selectFieldsArray).stream().forEach(temp1 -> {
				try {
					String out = temp1.getRename();
					String in = temp1.getName();
					if(!Utils.isEmpty(out)) {
						sdr.addRelationship( RelationshipUtil.buildFieldRelationship(from, in, out) );
					}
				} catch (Exception e) {
					relationshiplogger.error("",e);
				}
			});
		}
		
		SelectMetadataChange[] selectmetaArray = selectvaluesmeta.getMeta();
		if(selectmetaArray != null ) {
			Arrays.asList(selectmetaArray).stream().forEach(temp1 -> {
				try {
					String out = temp1.getRename();
					String in = temp1.getName();
					if(!Utils.isEmpty(out)) {
						sdr.addRelationship( RelationshipUtil.buildFieldRelationship(from, in, out) );
					}
				} catch (Exception e) {
					relationshiplogger.error("",e);
				}
			});
		}
		
		
	}

}
