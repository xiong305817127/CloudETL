/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.quality.dto.entry.entries.conditions;

import org.pentaho.di.job.JobMeta;
import org.pentaho.di.job.entries.simpleeval.JobEntrySimpleEval;
import org.pentaho.di.job.entry.JobEntryCopy;
import org.pentaho.di.job.entry.JobEntryInterface;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ys.idatrix.quality.dto.entry.entries.EntryParameter;

import net.sf.json.JSONObject;

/**
 * Entry - SimpleEval. 转换
 * org.pentaho.di.job.entries.simpleeval.JobEntrySimpleEval
 * 
 * @author XH
 * @since 2017-06-29
 */
@Component("SPsimple_eval")
@Scope("prototype")
public class SPSimpleEval implements EntryParameter {

	int valuetype;
	String fieldname;
	String variablename;
	int fieldtype;
	String mask;
	String comparevalue;
	String minvalue;
	String maxvalue;
	int successcondition;
	int successnumbercondition;
	int successbooleancondition;
	boolean successwhenvarset;

	/**
	 * @return valuetype
	 */
	public int getValuetype() {
		return valuetype;
	}

	/**
	 * @param 设置
	 *            valuetype
	 */
	public void setValuetype(int valuetype) {
		this.valuetype = valuetype;
	}

	/**
	 * @return fieldname
	 */
	public String getFieldname() {
		return fieldname;
	}

	/**
	 * @param 设置
	 *            fieldname
	 */
	public void setFieldname(String fieldname) {
		this.fieldname = fieldname;
	}

	/**
	 * @return variablename
	 */
	public String getVariablename() {
		return variablename;
	}

	/**
	 * @param 设置
	 *            variablename
	 */
	public void setVariablename(String variablename) {
		this.variablename = variablename;
	}

	/**
	 * @return fieldtype
	 */
	public int getFieldtype() {
		return fieldtype;
	}

	/**
	 * @param 设置
	 *            fieldtype
	 */
	public void setFieldtype(int fieldtype) {
		this.fieldtype = fieldtype;
	}

	/**
	 * @return mask
	 */
	public String getMask() {
		return mask;
	}

	/**
	 * @param 设置
	 *            mask
	 */
	public void setMask(String mask) {
		this.mask = mask;
	}

	/**
	 * @return comparevalue
	 */
	public String getComparevalue() {
		return comparevalue;
	}

	/**
	 * @param 设置
	 *            comparevalue
	 */
	public void setComparevalue(String comparevalue) {
		this.comparevalue = comparevalue;
	}

	/**
	 * @return minvalue
	 */
	public String getMinvalue() {
		return minvalue;
	}

	/**
	 * @param 设置
	 *            minvalue
	 */
	public void setMinvalue(String minvalue) {
		this.minvalue = minvalue;
	}

	/**
	 * @return maxvalue
	 */
	public String getMaxvalue() {
		return maxvalue;
	}

	/**
	 * @param 设置
	 *            maxvalue
	 */
	public void setMaxvalue(String maxvalue) {
		this.maxvalue = maxvalue;
	}

	/**
	 * @return successcondition
	 */
	public int getSuccesscondition() {
		return successcondition;
	}

	/**
	 * @param 设置
	 *            successcondition
	 */
	public void setSuccesscondition(int successcondition) {
		this.successcondition = successcondition;
	}

	/**
	 * @return successnumbercondition
	 */
	public int getSuccessnumbercondition() {
		return successnumbercondition;
	}

	/**
	 * @param 设置
	 *            successnumbercondition
	 */
	public void setSuccessnumbercondition(int successnumbercondition) {
		this.successnumbercondition = successnumbercondition;
	}

	/**
	 * @return successbooleancondition
	 */
	public int getSuccessbooleancondition() {
		return successbooleancondition;
	}

	/**
	 * @param 设置
	 *            successbooleancondition
	 */
	public void setSuccessbooleancondition(int successbooleancondition) {
		this.successbooleancondition = successbooleancondition;
	}

	/**
	 * @return successwhenvarset
	 */
	public boolean isSuccesswhenvarset() {
		return successwhenvarset;
	}

	/**
	 * @param 设置
	 *            successwhenvarset
	 */
	public void setSuccesswhenvarset(boolean successwhenvarset) {
		this.successwhenvarset = successwhenvarset;
	}

	/* 
	 * 
	 */
	@Override
	public Object getParameterObject(Object json) {
		JSONObject jsonObj = JSONObject.fromObject(json);
		return (SPSimpleEval) JSONObject.toBean(jsonObj, SPSimpleEval.class);
	}

	/* 
	 * 
	 */
	@Override
	public Object encodeParameterObject(JobEntryCopy jobEntryCopy) throws Exception {
		JobEntryInterface entryMetaInterface = jobEntryCopy.getEntry() ;
		
		SPSimpleEval spSimpleEval = new SPSimpleEval();
		JobEntrySimpleEval jobentrysimpleeval = (JobEntrySimpleEval) entryMetaInterface;

		spSimpleEval.setMask(jobentrysimpleeval.getMask());
		spSimpleEval.setVariablename(jobentrysimpleeval.getVariableName());
		spSimpleEval.setMinvalue(jobentrysimpleeval.getMinValue());
		spSimpleEval.setFieldname(jobentrysimpleeval.getFieldName());
		spSimpleEval.setComparevalue(jobentrysimpleeval.getCompareValue());
		spSimpleEval.setMaxvalue(jobentrysimpleeval.getMaxValue());
		spSimpleEval.setSuccesswhenvarset(jobentrysimpleeval.isSuccessWhenVarSet());

		spSimpleEval.setValuetype(jobentrysimpleeval.valuetype);
		spSimpleEval.setFieldtype(jobentrysimpleeval.fieldtype);
		spSimpleEval.setSuccesscondition(jobentrysimpleeval.successcondition);
		spSimpleEval.setSuccessnumbercondition(jobentrysimpleeval.successnumbercondition);
		spSimpleEval.setSuccessbooleancondition(jobentrysimpleeval.successbooleancondition);
		return spSimpleEval;
	}

	/* 
	 * 
	 */
	@Override
	public void decodeParameterObject(JobEntryCopy jobEntryCopy , Object po, JobMeta jobMeta) throws Exception {
		JobEntryInterface entryMetaInterface = jobEntryCopy.getEntry() ;
		
		SPSimpleEval spSimpleEval= (SPSimpleEval)po;
		JobEntrySimpleEval  jobentrysimpleeval= (JobEntrySimpleEval )entryMetaInterface;

		jobentrysimpleeval.setSuccessWhenVarSet(spSimpleEval.isSuccesswhenvarset());
		jobentrysimpleeval.setMinValue(spSimpleEval.getMinvalue());
		jobentrysimpleeval.setCompareValue(spSimpleEval.getComparevalue());
		jobentrysimpleeval.setMask(spSimpleEval.getMask());
		jobentrysimpleeval.setFieldName(spSimpleEval.getFieldname());
		jobentrysimpleeval.setVariableName(spSimpleEval.getVariablename());
		jobentrysimpleeval.setMaxValue(spSimpleEval.getMaxvalue());

		jobentrysimpleeval.valuetype = spSimpleEval.getValuetype() ;
		jobentrysimpleeval.fieldtype = spSimpleEval.getFieldtype() ;
		jobentrysimpleeval.successcondition = spSimpleEval.getSuccesscondition() ;
		jobentrysimpleeval.successnumbercondition = spSimpleEval.getSuccessnumbercondition() ;
		jobentrysimpleeval.successbooleancondition = spSimpleEval.getSuccessbooleancondition() ;

	}

}
