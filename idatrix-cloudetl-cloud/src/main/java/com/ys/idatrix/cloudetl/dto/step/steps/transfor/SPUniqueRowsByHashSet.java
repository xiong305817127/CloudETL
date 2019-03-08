/**
 * 云化数据集成系统 iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.dto.step.steps.transfor;

import java.util.Arrays;
import java.util.List;

import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.uniquerowsbyhashset.UniqueRowsByHashSetMeta;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ys.idatrix.cloudetl.dto.step.steps.StepParameter;
import com.ys.idatrix.cloudetl.recovery.trans.ResumeStepDataParser;
import com.ys.idatrix.cloudetl.toolkit.analyzer.trans.step.StepDataRelationship;
import com.ys.idatrix.cloudetl.toolkit.analyzer.trans.step.StepDataRelationshipParser;

import net.sf.json.JSONObject;

/**
 * Step - Unique Rows By HashSet(唯一行 (哈希值))
 * org.pentaho.di.trans.steps.uniquerowsbyhashset.UniqueRowsByHashSetMeta
 * 
 * @author XH
 * @since 2017年6月12日
 *
 */
@Component("SPUniqueRowsByHashSet")
@Scope("prototype")
public class SPUniqueRowsByHashSet implements StepParameter, StepDataRelationshipParser,ResumeStepDataParser {

	boolean storeValues;
	boolean rejectDuplicateRow;
	String errorDescription;
	List<String> compareFields;

	/**
	 * @return storeValues
	 */
	public boolean isStoreValues() {
		return storeValues;
	}

	/**
	 * @param storeValues
	 *            要设置的 storeValues
	 */
	public void setStoreValues(boolean storeValues) {
		this.storeValues = storeValues;
	}

	/**
	 * @return rejectDuplicateRow
	 */
	public boolean isRejectDuplicateRow() {
		return rejectDuplicateRow;
	}

	/**
	 * @param rejectDuplicateRow
	 *            要设置的 rejectDuplicateRow
	 */
	public void setRejectDuplicateRow(boolean rejectDuplicateRow) {
		this.rejectDuplicateRow = rejectDuplicateRow;
	}

	/**
	 * @return errorDescription
	 */
	public String getErrorDescription() {
		return errorDescription;
	}

	/**
	 * @param errorDescription
	 *            要设置的 errorDescription
	 */
	public void setErrorDescription(String errorDescription) {
		this.errorDescription = errorDescription;
	}

	/**
	 * @return compareFields
	 */
	public List<String> getCompareFields() {
		return compareFields;
	}

	/**
	 * @param compareFields
	 *            要设置的 compareFields
	 */
	public void setCompareFields(List<String> compareFields) {
		this.compareFields = compareFields;
	}

	/* 
	 * 
	 */
	@Override
	public Object getParameterObject(Object json) {
		JSONObject jsonObj = JSONObject.fromObject(json);
		return (SPUniqueRowsByHashSet) JSONObject.toBean(jsonObj, SPUniqueRowsByHashSet.class);
	}

	/* 
	 * 
	 */
	@Override
	public Object encodeParameterObject(StepMeta stepMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPUniqueRowsByHashSet spUniqueRowsByHashSet = new SPUniqueRowsByHashSet();
		UniqueRowsByHashSetMeta uniquerowsbyhashsetmeta = (UniqueRowsByHashSetMeta) stepMetaInterface;

		spUniqueRowsByHashSet.setErrorDescription(uniquerowsbyhashsetmeta.getErrorDescription());
		spUniqueRowsByHashSet.setStoreValues(uniquerowsbyhashsetmeta.getStoreValues());
		spUniqueRowsByHashSet.setRejectDuplicateRow(uniquerowsbyhashsetmeta.isRejectDuplicateRow());
		spUniqueRowsByHashSet.setCompareFields(Arrays.asList(uniquerowsbyhashsetmeta.getCompareFields()));

		return spUniqueRowsByHashSet;
	}

	/* 
	 * 
	 */
	@Override
	public void decodeParameterObject(StepMeta stepMeta, Object po, List<DatabaseMeta> databases,TransMeta transMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPUniqueRowsByHashSet spUniqueRowsByHashSet = (SPUniqueRowsByHashSet) po;
		UniqueRowsByHashSetMeta uniquerowsbyhashsetmeta = (UniqueRowsByHashSetMeta) stepMetaInterface;

		uniquerowsbyhashsetmeta.setRejectDuplicateRow(spUniqueRowsByHashSet.isRejectDuplicateRow());
		uniquerowsbyhashsetmeta.setErrorDescription(spUniqueRowsByHashSet.getErrorDescription());
		uniquerowsbyhashsetmeta.setStoreValues(spUniqueRowsByHashSet.isStoreValues());
		uniquerowsbyhashsetmeta.setCompareFields(spUniqueRowsByHashSet.getCompareFields()
				.toArray(new String[spUniqueRowsByHashSet.getCompareFields().size()]));

	}

	/*
	 * 覆盖方法：getStepDataAndRelationship
	 */
	@Override
	public void getStepDataAndRelationship(TransMeta transMeta, StepMeta stepMeta, StepDataRelationship sdr) {
		//字段没有变化
		
	}

	@Override
	public int stepType() {
		return 0;
	}

}
