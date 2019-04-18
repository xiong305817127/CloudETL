/**
 * 云化数据集成系统 iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.dto.step.steps.transfor;

import java.util.List;

import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.splitfieldtorows.SplitFieldToRowsMeta;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ys.idatrix.cloudetl.dto.step.steps.StepParameter;
import com.ys.idatrix.cloudetl.recovery.trans.ResumeStepDataParser;
import com.ys.idatrix.cloudetl.toolkit.analyzer.trans.step.StepDataRelationship;
import com.ys.idatrix.cloudetl.toolkit.analyzer.trans.step.StepDataRelationshipParser;
import com.ys.idatrix.cloudetl.toolkit.utils.RelationshipUtil;

import net.sf.json.JSONObject;

/**
 * Step - SplitFieldToRows(列拆分为多行)
 * org.pentaho.di.trans.steps.splitfieldtorows.SplitFieldToRowsMeta
 * 
 * @author XH
 * @since 2017年6月9日
 *
 */
@Component("SPSplitFieldToRows3")
@Scope("prototype")
public class SPSplitFieldToRows implements StepParameter, StepDataRelationshipParser,ResumeStepDataParser {

	String splitField;
	String delimiter;
	String newFieldname;
	boolean includeRowNumber;
	String rowNumberField;
	boolean resetRowNumber;
	boolean isDelimiterRegex;

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
	 * @return newFieldname
	 */
	public String getNewFieldname() {
		return newFieldname;
	}

	/**
	 * @param newFieldname
	 *            要设置的 newFieldname
	 */
	public void setNewFieldname(String newFieldname) {
		this.newFieldname = newFieldname;
	}

	/**
	 * @return includeRowNumber
	 */
	public boolean isIncludeRowNumber() {
		return includeRowNumber;
	}

	/**
	 * @param includeRowNumber
	 *            要设置的 includeRowNumber
	 */
	public void setIncludeRowNumber(boolean includeRowNumber) {
		this.includeRowNumber = includeRowNumber;
	}

	/**
	 * @return rowNumberField
	 */
	public String getRowNumberField() {
		return rowNumberField;
	}

	/**
	 * @param rowNumberField
	 *            要设置的 rowNumberField
	 */
	public void setRowNumberField(String rowNumberField) {
		this.rowNumberField = rowNumberField;
	}

	/**
	 * @return resetRowNumber
	 */
	public boolean isResetRowNumber() {
		return resetRowNumber;
	}

	/**
	 * @param resetRowNumber
	 *            要设置的 resetRowNumber
	 */
	public void setResetRowNumber(boolean resetRowNumber) {
		this.resetRowNumber = resetRowNumber;
	}

	/**
	 * @return isDelimiterRegex
	 */
	public boolean isDelimiterRegex() {
		return isDelimiterRegex;
	}

	/**
	 * @param isDelimiterRegex
	 *            要设置的 isDelimiterRegex
	 */
	public void setDelimiterRegex(boolean isDelimiterRegex) {
		this.isDelimiterRegex = isDelimiterRegex;
	}

	/* 
	 * 
	 */
	@Override
	public Object getParameterObject(Object json) {
		JSONObject jsonObj = JSONObject.fromObject(json);
		return (SPSplitFieldToRows) JSONObject.toBean(jsonObj, SPSplitFieldToRows.class);
	}

	/* 
	 * 
	 */
	@Override
	public Object encodeParameterObject(StepMeta stepMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPSplitFieldToRows spSplitFieldToRows = new SPSplitFieldToRows();
		SplitFieldToRowsMeta splitfieldtorowsmeta = (SplitFieldToRowsMeta) stepMetaInterface;

		spSplitFieldToRows.setNewFieldname(splitfieldtorowsmeta.getNewFieldname());
		spSplitFieldToRows.setRowNumberField(splitfieldtorowsmeta.getRowNumberField());
		spSplitFieldToRows.setDelimiter(splitfieldtorowsmeta.getDelimiter());
		spSplitFieldToRows.setSplitField(splitfieldtorowsmeta.getSplitField());
		spSplitFieldToRows.setIncludeRowNumber(splitfieldtorowsmeta.includeRowNumber());
		spSplitFieldToRows.setResetRowNumber(splitfieldtorowsmeta.resetRowNumber());
		spSplitFieldToRows.setDelimiterRegex(splitfieldtorowsmeta.isDelimiterRegex());
		return spSplitFieldToRows;
	}

	/* 
	 * 
	 */
	@Override
	public void decodeParameterObject(StepMeta stepMeta, Object po, List<DatabaseMeta> databases,TransMeta transMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPSplitFieldToRows spSplitFieldToRows = (SPSplitFieldToRows) po;
		SplitFieldToRowsMeta splitfieldtorowsmeta = (SplitFieldToRowsMeta) stepMetaInterface;

		splitfieldtorowsmeta.setNewFieldname(spSplitFieldToRows.getNewFieldname());
		splitfieldtorowsmeta.setDelimiterRegex(spSplitFieldToRows.isDelimiterRegex());
		splitfieldtorowsmeta.setRowNumberField(spSplitFieldToRows.getRowNumberField());
		splitfieldtorowsmeta.setResetRowNumber(spSplitFieldToRows.isResetRowNumber());
		splitfieldtorowsmeta.setIncludeRowNumber(spSplitFieldToRows.isIncludeRowNumber());
		splitfieldtorowsmeta.setDelimiter(spSplitFieldToRows.getDelimiter());
		splitfieldtorowsmeta.setSplitField(spSplitFieldToRows.getSplitField());

	}

	/*
	 * 覆盖方法：getStepDataAndRelationship
	 */
	@Override
	public void getStepDataAndRelationship(TransMeta transMeta, StepMeta stepMeta, StepDataRelationship sdr) throws Exception {
		
		String from= "转换:"+transMeta.getName()+",步骤:"+stepMeta.getName();
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SplitFieldToRowsMeta splitfieldtorowsmeta = (SplitFieldToRowsMeta) stepMetaInterface;
		//输出
		String out = splitfieldtorowsmeta.getNewFieldname();
		//输入
		String in = splitfieldtorowsmeta.getSplitField();
		
		sdr.addRelationship( RelationshipUtil.buildFieldRelationship(from, in, out) );
	}

	@Override
	public int stepType() {
		return 12;
	}

}
