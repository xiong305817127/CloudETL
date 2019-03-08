package com.ys.idatrix.cloudetl.dto.step.steps.statistics;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.groupby.GroupByMeta;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.ys.idatrix.cloudetl.dto.step.parts.GroupBysubjectFieldDto;
import com.ys.idatrix.cloudetl.dto.step.steps.StepParameter;
import com.ys.idatrix.cloudetl.recovery.trans.ResumeStepDataParser;
import com.ys.idatrix.cloudetl.recovery.trans.dto.StepLinesDto;
import com.ys.idatrix.cloudetl.toolkit.analyzer.trans.step.StepDataRelationship;
import com.ys.idatrix.cloudetl.toolkit.analyzer.trans.step.StepDataRelationshipParser;

import net.sf.json.JSONObject;

/**
 * Step - GroupBy. 转换 org.pentaho.di.trans.steps.groupby.GroupByMeta
 * 
 * @author XH
 * @since 2017-09-05
 */
@Component("SPGroupBy")
@Scope("prototype")
public class SPGroupBy implements StepParameter, StepDataRelationshipParser,ResumeStepDataParser  {

	boolean passAllRows;
	boolean aggregateIgnored;
	String aggregateIgnoredField;
	String directory;
	String prefix;
	boolean addingLineNrInGroup;
	String lineNrInGroupField;
	boolean alwaysGivingBackOneRow;

	List<String> groupFields;
	List<GroupBysubjectFieldDto> subjectField;

	/**
	 * @return the passAllRows
	 */
	public boolean isPassAllRows() {
		return passAllRows;
	}

	/**
	 * @param 设置
	 *            passAllRows
	 */
	public void setPassAllRows(boolean passAllRows) {
		this.passAllRows = passAllRows;
	}

	/**
	 * @return the aggregateIgnored
	 */
	public boolean isAggregateIgnored() {
		return aggregateIgnored;
	}

	/**
	 * @param 设置
	 *            aggregateIgnored
	 */
	public void setAggregateIgnored(boolean aggregateIgnored) {
		this.aggregateIgnored = aggregateIgnored;
	}

	/**
	 * @return the aggregateIgnoredField
	 */
	public String getAggregateIgnoredField() {
		return aggregateIgnoredField;
	}

	/**
	 * @param 设置
	 *            aggregateIgnoredField
	 */
	public void setAggregateIgnoredField(String aggregateIgnoredField) {
		this.aggregateIgnoredField = aggregateIgnoredField;
	}

	/**
	 * @return the directory
	 */
	public String getDirectory() {
		return directory;
	}

	/**
	 * @param 设置
	 *            directory
	 */
	public void setDirectory(String directory) {
		this.directory = directory;
	}

	/**
	 * @return the prefix
	 */
	public String getPrefix() {
		return prefix;
	}

	/**
	 * @param 设置
	 *            prefix
	 */
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	/**
	 * @return the addingLineNrInGroup
	 */
	public boolean isAddingLineNrInGroup() {
		return addingLineNrInGroup;
	}

	/**
	 * @param 设置
	 *            addingLineNrInGroup
	 */
	public void setAddingLineNrInGroup(boolean addingLineNrInGroup) {
		this.addingLineNrInGroup = addingLineNrInGroup;
	}

	/**
	 * @return the lineNrInGroupField
	 */
	public String getLineNrInGroupField() {
		return lineNrInGroupField;
	}

	/**
	 * @param 设置
	 *            lineNrInGroupField
	 */
	public void setLineNrInGroupField(String lineNrInGroupField) {
		this.lineNrInGroupField = lineNrInGroupField;
	}

	/**
	 * @return the alwaysGivingBackOneRow
	 */
	public boolean isAlwaysGivingBackOneRow() {
		return alwaysGivingBackOneRow;
	}

	/**
	 * @param 设置
	 *            alwaysGivingBackOneRow
	 */
	public void setAlwaysGivingBackOneRow(boolean alwaysGivingBackOneRow) {
		this.alwaysGivingBackOneRow = alwaysGivingBackOneRow;
	}

	/**
	 * @return the groupFields
	 */
	public List<String> getGroupFields() {
		return groupFields;
	}

	/**
	 * @param 设置
	 *            groupFields
	 */
	public void setGroupFields(List<String> groupFields) {
		this.groupFields = groupFields;
	}

	/**
	 * @return the subjectField
	 */
	public List<GroupBysubjectFieldDto> getSubjectField() {
		return subjectField;
	}

	/**
	 * @param 设置
	 *            subjectField
	 */
	public void setSubjectField(List<GroupBysubjectFieldDto> subjectField) {
		this.subjectField = subjectField;
	}

	@Override
	public Object getParameterObject(Object json) {
		JSONObject jsonObj = JSONObject.fromObject(json);
		Map<String, Class<?>> classMap = new HashMap<>();
		classMap.put("groupFields", String.class);
		classMap.put("subjectField", GroupBysubjectFieldDto.class);
		return (SPGroupBy) JSONObject.toBean(jsonObj, SPGroupBy.class, classMap);
	}

	@Override
	public Object encodeParameterObject(StepMeta stepMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPGroupBy spGroupBy = new SPGroupBy();
		GroupByMeta groupbymeta = (GroupByMeta) stepMetaInterface;

		spGroupBy.setAggregateIgnoredField(groupbymeta.getAggregateIgnoredField());
		spGroupBy.setLineNrInGroupField(groupbymeta.getLineNrInGroupField());

		List<GroupBysubjectFieldDto> subjectFieldList = Lists.newArrayList();
		String[] aggregateFields = groupbymeta.getAggregateField();
		String[] subjectFields = groupbymeta.getSubjectField();
		int[] aggregateTypes = groupbymeta.getAggregateType();
		String[] valueFields = groupbymeta.getValueField();
		for (int i = 0; subjectFields != null && i < subjectFields.length; i++) {
			GroupBysubjectFieldDto groupbysubjectfielddto = new GroupBysubjectFieldDto();
			groupbysubjectfielddto.setAggregateField(aggregateFields[i]);
			groupbysubjectfielddto.setSubjectField(subjectFields[i]);
			groupbysubjectfielddto.setAggregateType(aggregateTypes[i]);
			groupbysubjectfielddto.setValueField(valueFields[i]);
			subjectFieldList.add(groupbysubjectfielddto);
		}
		spGroupBy.setSubjectField(subjectFieldList);
		spGroupBy.setDirectory(groupbymeta.getDirectory());
		spGroupBy.setPrefix(groupbymeta.getPrefix());
		spGroupBy.setAggregateIgnored(groupbymeta.isAggregateIgnored());
		spGroupBy.setAddingLineNrInGroup(groupbymeta.isAddingLineNrInGroup());
		spGroupBy.setAlwaysGivingBackOneRow(groupbymeta.isAlwaysGivingBackOneRow());

		spGroupBy.setPassAllRows(groupbymeta.passAllRows());
		spGroupBy.setGroupFields(Arrays.asList(groupbymeta.getGroupField()));

		return spGroupBy;
	}

	@Override
	public void decodeParameterObject(StepMeta stepMeta, Object po, List<DatabaseMeta> databases,TransMeta transMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPGroupBy spGroupBy = (SPGroupBy) po;
		GroupByMeta groupbymeta = (GroupByMeta) stepMetaInterface;

		groupbymeta.setAggregateIgnored(spGroupBy.isAggregateIgnored());
		groupbymeta.setAggregateIgnoredField(spGroupBy.getAggregateIgnoredField());
		groupbymeta.setAddingLineNrInGroup(spGroupBy.isAddingLineNrInGroup());
		groupbymeta.setLineNrInGroupField(spGroupBy.getLineNrInGroupField());
		groupbymeta.setAlwaysGivingBackOneRow(spGroupBy.isAlwaysGivingBackOneRow());
		groupbymeta.setPassAllRows(spGroupBy.isPassAllRows());
		if (spGroupBy.getSubjectField() != null) {
			String[] aggregateFields = new String[spGroupBy.getSubjectField().size()];
			String[] subjectFields = new String[spGroupBy.getSubjectField().size()];
			int[] aggregateTypes = new int[spGroupBy.getSubjectField().size()];
			String[] valueFields = new String[spGroupBy.getSubjectField().size()];
			for (int i = 0; i < spGroupBy.getSubjectField().size(); i++) {
				GroupBysubjectFieldDto groupbysubjectfielddto = spGroupBy.getSubjectField().get(i);
				aggregateFields[i] = groupbysubjectfielddto.getAggregateField();
				subjectFields[i] = groupbysubjectfielddto.getSubjectField();
				aggregateTypes[i] = groupbysubjectfielddto.getAggregateType();
				valueFields[i] = groupbysubjectfielddto.getValueField();
			}
			groupbymeta.setAggregateField(aggregateFields);
			groupbymeta.setSubjectField(subjectFields);
			groupbymeta.setAggregateType(aggregateTypes);
			groupbymeta.setValueField(valueFields);
		}
		groupbymeta.setDirectory(spGroupBy.getDirectory());
		groupbymeta.setPrefix(spGroupBy.getPrefix());

		if (spGroupBy.getGroupFields() != null) {
			groupbymeta
					.setGroupField(spGroupBy.getGroupFields().toArray(new String[spGroupBy.getGroupFields().size()]));
		}

	}

	/*
	 * 覆盖方法：getStepDataAndRelationship
	 */
	@Override
	public void getStepDataAndRelationship(TransMeta transMeta, StepMeta stepMeta, StepDataRelationship sdr) {
		//流字段没有改变

	}

	@Override
	public void setLinesFromCacheLines(StepLinesDto lines , StepLinesDto cacheLines) {
			//该组件会预先读取一行,比较后将有效数据输入写出,所以写出的行号对应的读入需要减1
			Map<String, Long> preMap = cacheLines.getPreEffectiveInputLines();
			if(  preMap != null && preMap.size() > 0) {
				for(String key :preMap.keySet()) {
					preMap.put(key, preMap.get(key)-1);
				}
			}
			lines.setPreEffectiveInputLines(preMap);
			lines.setLinesInput(cacheLines.getLinesInput());
			lines.setLinesOutput(cacheLines.getLinesOutput());
			lines.setLinesRead(cacheLines.getLinesRead());
			lines.setLinesWritten(cacheLines.getLinesWritten());
			lines.setLinesRejected(cacheLines.getLinesRejected());
			lines.setLinesUpdated(cacheLines.getLinesUpdated());
			lines.setLinesErrors(cacheLines.getLinesErrors());
	}
	
	@Override
	public int stepType() {
		return 12;
	}

}
