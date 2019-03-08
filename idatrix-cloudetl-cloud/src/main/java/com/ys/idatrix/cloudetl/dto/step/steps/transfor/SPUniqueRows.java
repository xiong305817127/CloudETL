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
import org.pentaho.di.trans.steps.uniquerows.UniqueRowsMeta;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.ys.idatrix.cloudetl.dto.step.parts.UniqueRowscompareFieldsDto;
import com.ys.idatrix.cloudetl.dto.step.steps.StepParameter;
import com.ys.idatrix.cloudetl.recovery.trans.ResumeStepDataParser;
import com.ys.idatrix.cloudetl.toolkit.analyzer.trans.step.StepDataRelationship;
import com.ys.idatrix.cloudetl.toolkit.analyzer.trans.step.StepDataRelationshipParser;
import com.ys.idatrix.cloudetl.toolkit.domain.Relationship;
import com.ys.idatrix.cloudetl.toolkit.utils.RelationshipUtil;

import net.sf.json.JSONObject;

/**
 * Step - Unique Rows(去除重复记录)
 * org.pentaho.di.trans.steps.uniquerows.UniqueRowsMeta
 * 
 * @author XH
 * @since 2017年6月12日
 *
 */
@Component("SPUnique")
@Scope("prototype")
public class SPUniqueRows implements StepParameter, StepDataRelationshipParser,ResumeStepDataParser {

	boolean countRows;
	String countField;
	boolean rejectDuplicateRow;
	String errorDescription;
	List<UniqueRowscompareFieldsDto> compareFields;

	/**
	 * @return countRows
	 */
	public boolean isCountRows() {
		return countRows;
	}

	/**
	 * @param countRows
	 *            要设置的 countRows
	 */
	public void setCountRows(boolean countRows) {
		this.countRows = countRows;
	}

	/**
	 * @return countField
	 */
	public String getCountField() {
		return countField;
	}

	/**
	 * @param countField
	 *            要设置的 countField
	 */
	public void setCountField(String countField) {
		this.countField = countField;
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
	public List<UniqueRowscompareFieldsDto> getCompareFields() {
		return compareFields;
	}

	/**
	 * @param compareFields
	 *            要设置的 compareFields
	 */
	public void setCompareFields(List<UniqueRowscompareFieldsDto> compareFields) {
		this.compareFields = compareFields;
	}

	/* 
	 * 
	 */
	@Override
	public Object getParameterObject(Object json) {
		JSONObject jsonObj = JSONObject.fromObject(json);
		Map<String, Class<?>> classMap = new HashMap<>();
		classMap.put("compareFields", UniqueRowscompareFieldsDto.class);
		return (SPUniqueRows) JSONObject.toBean(jsonObj, SPUniqueRows.class, classMap);
	}

	/* 
	 * 
	 */
	@Override
	public Object encodeParameterObject(StepMeta stepMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPUniqueRows spUniqueRows = new SPUniqueRows();
		UniqueRowsMeta uniquerowsmeta = (UniqueRowsMeta) stepMetaInterface;

		spUniqueRows.setErrorDescription(uniquerowsmeta.getErrorDescription());
		spUniqueRows.setCountField(uniquerowsmeta.getCountField());

		List<UniqueRowscompareFieldsDto> compareFieldsList = Lists.newArrayList();
		String[] compareFieldss = uniquerowsmeta.getCompareFields();
		boolean[] caseInsensitives = uniquerowsmeta.getCaseInsensitive();
		for (int i = 0; i < compareFieldss.length; i++) {
			UniqueRowscompareFieldsDto uniquerowscomparefieldsdto = new UniqueRowscompareFieldsDto();
			uniquerowscomparefieldsdto.setCompareField(compareFieldss[i]);
			uniquerowscomparefieldsdto.setCaseInsensitive(caseInsensitives[i]);
			compareFieldsList.add(uniquerowscomparefieldsdto);
		}
		spUniqueRows.setCompareFields(compareFieldsList);
		spUniqueRows.setRejectDuplicateRow(uniquerowsmeta.isRejectDuplicateRow());
		spUniqueRows.setCountRows(uniquerowsmeta.isCountRows());
		return spUniqueRows;
	}

	/* 
	 * 
	 */
	@Override
	public void decodeParameterObject(StepMeta stepMeta, Object po, List<DatabaseMeta> databases,TransMeta transMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPUniqueRows spUniqueRows = (SPUniqueRows) po;
		UniqueRowsMeta uniquerowsmeta = (UniqueRowsMeta) stepMetaInterface;

		uniquerowsmeta.setRejectDuplicateRow(spUniqueRows.isRejectDuplicateRow());
		uniquerowsmeta.setErrorDescription(spUniqueRows.getErrorDescription());
		uniquerowsmeta.setCountRows(spUniqueRows.isCountRows());
		uniquerowsmeta.setCountField(spUniqueRows.getCountField());
		String[] compareFieldss = new String[spUniqueRows.getCompareFields().size()];
		boolean[] caseInsensitives = new boolean[spUniqueRows.getCompareFields().size()];
		for (int i = 0; i < spUniqueRows.getCompareFields().size(); i++) {
			UniqueRowscompareFieldsDto uniquerowscomparefieldsdto = spUniqueRows.getCompareFields().get(i);
			compareFieldss[i] = uniquerowscomparefieldsdto.getCompareField();
			caseInsensitives[i] = uniquerowscomparefieldsdto.isCaseInsensitive();
		}
		uniquerowsmeta.setCompareFields(compareFieldss);
		uniquerowsmeta.setCaseInsensitive(caseInsensitives);
	}

	/*
	 * 覆盖方法：getStepDataAndRelationship
	 */
	@Override
	public void getStepDataAndRelationship(TransMeta transMeta, StepMeta stepMeta, StepDataRelationship sdr) throws Exception {

		String from = "转换:" + transMeta.getName() + ",步骤:" + stepMeta.getName();
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		UniqueRowsMeta uniquerowsmeta = (UniqueRowsMeta) stepMetaInterface;
		//输出
		String countField = uniquerowsmeta.getCountField() ;
		if(!Utils.isEmpty( countField ) ) {
			String dummyId = transMeta.getName()+"-"+stepMeta.getName()+"-"+countField ;
			Relationship relationship = RelationshipUtil.buildDummyRelationship(from, dummyId, countField);
			sdr.addRelationship(relationship);
			sdr.addInputDataNode( relationship.getStartNode());
		}
	}

	@Override
	public int stepType() {
		return 12;
	}

}
