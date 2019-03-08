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
import org.pentaho.di.trans.steps.sort.SortRowsMeta;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.ys.idatrix.cloudetl.dto.step.parts.SortRowsfieldNameDto;
import com.ys.idatrix.cloudetl.dto.step.steps.StepParameter;
import com.ys.idatrix.cloudetl.recovery.trans.ResumeStepDataParser;
import com.ys.idatrix.cloudetl.toolkit.analyzer.trans.step.StepDataRelationship;
import com.ys.idatrix.cloudetl.toolkit.analyzer.trans.step.StepDataRelationshipParser;

import net.sf.json.JSONObject;

/**
 * Step -Sort Rows(排序记录) org.pentaho.di.trans.steps.sort.SortRowsMeta
 * 
 * @author XH
 * @since 2017年6月13日
 *
 */
@Component("SPSortRows")
@Scope("prototype")
public class SPSortRows implements StepParameter, StepDataRelationshipParser ,ResumeStepDataParser{

	String directory;
	String prefix;
	String sortSize;
	String freeMemoryLimit;
	boolean compressFiles;
	String compressFilesVariable;
	boolean onlyPassingUniqueRows;
	List<SortRowsfieldNameDto> fieldName;

	/**
	 * @return directory
	 */
	public String getDirectory() {
		return directory;
	}

	/**
	 * @param directory
	 *            要设置的 directory
	 */
	public void setDirectory(String directory) {
		this.directory = directory;
	}

	/**
	 * @return prefix
	 */
	public String getPrefix() {
		return prefix;
	}

	/**
	 * @param prefix
	 *            要设置的 prefix
	 */
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	/**
	 * @return sortSize
	 */
	public String getSortSize() {
		return sortSize;
	}

	/**
	 * @param sortSize
	 *            要设置的 sortSize
	 */
	public void setSortSize(String sortSize) {
		this.sortSize = sortSize;
	}

	/**
	 * @return freeMemoryLimit
	 */
	public String getFreeMemoryLimit() {
		return freeMemoryLimit;
	}

	/**
	 * @param freeMemoryLimit
	 *            要设置的 freeMemoryLimit
	 */
	public void setFreeMemoryLimit(String freeMemoryLimit) {
		this.freeMemoryLimit = freeMemoryLimit;
	}

	/**
	 * @return compressFiles
	 */
	public boolean isCompressFiles() {
		return compressFiles;
	}

	/**
	 * @param compressFiles
	 *            要设置的 compressFiles
	 */
	public void setCompressFiles(boolean compressFiles) {
		this.compressFiles = compressFiles;
	}

	/**
	 * @return compressFilesVariable
	 */
	public String getCompressFilesVariable() {
		return compressFilesVariable;
	}

	/**
	 * @param compressFilesVariable
	 *            要设置的 compressFilesVariable
	 */
	public void setCompressFilesVariable(String compressFilesVariable) {
		this.compressFilesVariable = compressFilesVariable;
	}

	/**
	 * @return onlyPassingUniqueRows
	 */
	public boolean isOnlyPassingUniqueRows() {
		return onlyPassingUniqueRows;
	}

	/**
	 * @param onlyPassingUniqueRows
	 *            要设置的 onlyPassingUniqueRows
	 */
	public void setOnlyPassingUniqueRows(boolean onlyPassingUniqueRows) {
		this.onlyPassingUniqueRows = onlyPassingUniqueRows;
	}

	/**
	 * @return fieldName
	 */
	public List<SortRowsfieldNameDto> getFieldName() {
		return fieldName;
	}

	/**
	 * @param fieldName
	 *            要设置的 fieldName
	 */
	public void setFieldName(List<SortRowsfieldNameDto> fieldName) {
		this.fieldName = fieldName;
	}

	/* 
	 * 
	 */
	@Override
	public Object getParameterObject(Object json) {
		JSONObject jsonObj = JSONObject.fromObject(json);
		Map<String, Class<?>> classMap = new HashMap<>();
		classMap.put("fieldName", SortRowsfieldNameDto.class);
		return (SPSortRows) JSONObject.toBean(jsonObj, SPSortRows.class, classMap);
	}

	/* 
	 * 
	 */
	@Override
	public Object encodeParameterObject(StepMeta stepMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPSortRows spSortRows = new SPSortRows();
		SortRowsMeta sortrowsmeta = (SortRowsMeta) stepMetaInterface;

		spSortRows.setDirectory(sortrowsmeta.getDirectory());

		List<SortRowsfieldNameDto> fieldNameList = Lists.newArrayList();
		String[] fieldNames = sortrowsmeta.getFieldName();
		boolean[] ascendings = sortrowsmeta.getAscending();
		boolean[] caseSensitives = sortrowsmeta.getCaseSensitive();
		boolean[] collatorEnableds = sortrowsmeta.getCollatorEnabled();
		int[] collatorStrengths = sortrowsmeta.getCollatorStrength();
		boolean[] preSortedFields = sortrowsmeta.getPreSortedField();
		for (int i = 0; fieldNames != null && i < fieldNames.length; i++) {
			SortRowsfieldNameDto sortrowsfieldnamedto = new SortRowsfieldNameDto();
			sortrowsfieldnamedto.setFieldName(fieldNames[i]);
			sortrowsfieldnamedto.setAscending(ascendings[i]);
			sortrowsfieldnamedto.setCaseSensitive(caseSensitives[i]);
			sortrowsfieldnamedto.setCollatorEnabled(collatorEnableds[i]);
			sortrowsfieldnamedto.setCollatorStrength(collatorStrengths[i]);
			sortrowsfieldnamedto.setPreSortedField(preSortedFields[i]);
			fieldNameList.add(sortrowsfieldnamedto);
		}
		spSortRows.setFieldName(fieldNameList);

		spSortRows.setPrefix(sortrowsmeta.getPrefix());
		spSortRows.setSortSize(sortrowsmeta.getSortSize());
		spSortRows.setCompressFiles(sortrowsmeta.getCompressFiles());
		spSortRows.setCompressFilesVariable(sortrowsmeta.getCompressFilesVariable());
		spSortRows.setFreeMemoryLimit(sortrowsmeta.getFreeMemoryLimit());
		spSortRows.setOnlyPassingUniqueRows(sortrowsmeta.isOnlyPassingUniqueRows());
		return spSortRows;
	}

	/* 
	 * 
	 */
	@Override
	public void decodeParameterObject(StepMeta stepMeta, Object po, List<DatabaseMeta> databases,TransMeta transMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPSortRows spSortRows = (SPSortRows) po;
		SortRowsMeta sortrowsmeta = (SortRowsMeta) stepMetaInterface;

		sortrowsmeta.setDirectory(spSortRows.getDirectory());
		if (spSortRows.getFieldName() != null) {
			String[] fieldNames = new String[spSortRows.getFieldName().size()];
			boolean[] ascendings = new boolean[spSortRows.getFieldName().size()];
			boolean[] caseSensitives = new boolean[spSortRows.getFieldName().size()];
			boolean[] collatorEnableds = new boolean[spSortRows.getFieldName().size()];
			int[] collatorStrengths = new int[spSortRows.getFieldName().size()];
			boolean[] preSortedFields = new boolean[spSortRows.getFieldName().size()];
			for (int i = 0; i < spSortRows.getFieldName().size(); i++) {
				SortRowsfieldNameDto sortrowsfieldnamedto = spSortRows.getFieldName().get(i);
				fieldNames[i] = sortrowsfieldnamedto.getFieldName();
				ascendings[i] = sortrowsfieldnamedto.isAscending();
				caseSensitives[i] = sortrowsfieldnamedto.isCaseSensitive();
				collatorEnableds[i] = sortrowsfieldnamedto.isCollatorEnabled();
				collatorStrengths[i] = sortrowsfieldnamedto.getCollatorStrength();
				preSortedFields[i] = sortrowsfieldnamedto.isPreSortedField();
			}
			sortrowsmeta.setFieldName(fieldNames);
			sortrowsmeta.setAscending(ascendings);
			sortrowsmeta.setCaseSensitive(caseSensitives);
			sortrowsmeta.setCollatorEnabled(collatorEnableds);
			sortrowsmeta.setCollatorStrength(collatorStrengths);
			sortrowsmeta.setPreSortedField(preSortedFields);
		}
		sortrowsmeta.setPrefix(spSortRows.getPrefix());
		sortrowsmeta.setSortSize(spSortRows.getSortSize());
		sortrowsmeta.setCompressFiles(spSortRows.isCompressFiles());
		sortrowsmeta.setCompressFilesVariable(spSortRows.getCompressFilesVariable());
		sortrowsmeta.setOnlyPassingUniqueRows(spSortRows.isOnlyPassingUniqueRows());
		sortrowsmeta.setFreeMemoryLimit(spSortRows.getFreeMemoryLimit());

	}

	/*
	 * 覆盖方法：getStepDataAndRelationship
	 */
	@Override
	public void getStepDataAndRelationship(TransMeta transMeta, StepMeta stepMeta, StepDataRelationship sdr) {
		// 没有变化
	}

	@Override
	public int stepType() {
		return 0;
	}

}
