package com.ys.idatrix.quality.dto.step.steps.Joins;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.multimerge.MultiMergeJoinMeta;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ys.idatrix.quality.dto.step.steps.StepParameter;
import com.ys.idatrix.quality.recovery.trans.ResumeStepDataParser;
import com.ys.idatrix.quality.recovery.trans.dto.StepLinesDto;
import com.ys.idatrix.quality.toolkit.analyzer.trans.step.StepDataRelationship;
import com.ys.idatrix.quality.toolkit.analyzer.trans.step.StepDataRelationshipParser;

import net.sf.json.JSONObject;

/**
 * Step - MultiwayMergeJoin.
 * 转换  org.pentaho.di.trans.steps.multimerge.MultiMergeJoinMeta
 * 
 * @author XH
 * @since 2018-04-11
 */
@Component("SPMultiwayMergeJoin")
@Scope("prototype")
public class SPMultiwayMergeJoin implements StepParameter, StepDataRelationshipParser, ResumeStepDataParser  {

	List<String> inputSteps;
	List<String> keys;
	String joinType;
	
	/**
	 * @return the inputSteps
	 */
	public List<String> getInputSteps() {
		return inputSteps;
	}

	/**
	 * @param  设置 inputSteps
	 */
	public void setInputSteps(List<String> inputSteps) {
		this.inputSteps = inputSteps;
	}

	/**
	 * @return the keys
	 */
	public List<String> getKeys() {
		return keys;
	}

	/**
	 * @param  设置 keys
	 */
	public void setKeys(List<String> keys) {
		this.keys = keys;
	}

	/**
	 * @return the joinType
	 */
	public String getJoinType() {
		return joinType;
	}

	/**
	 * @param  设置 joinType
	 */
	public void setJoinType(String joinType) {
		this.joinType = joinType;
	}

	@Override
	public Object getParameterObject(Object json) {
		JSONObject jsonObj = JSONObject.fromObject(json);
		return (SPMultiwayMergeJoin) JSONObject.toBean(jsonObj, SPMultiwayMergeJoin.class);
	}

	@Override
	public Object encodeParameterObject(StepMeta stepMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPMultiwayMergeJoin spMultiwayMergeJoin= new SPMultiwayMergeJoin();
		MultiMergeJoinMeta multimergejoinmeta= (MultiMergeJoinMeta )stepMetaInterface;

		spMultiwayMergeJoin.setJoinType(multimergejoinmeta.getJoinType());
		
		String[] inputStepsNames  = multimergejoinmeta.getInputSteps() != null ? multimergejoinmeta.getInputSteps() : ArrayUtils.EMPTY_STRING_ARRAY;
		spMultiwayMergeJoin.setInputSteps(Arrays.asList(inputStepsNames));
		
		String[] keys  = multimergejoinmeta.getKeyFields() != null ? multimergejoinmeta.getKeyFields() : ArrayUtils.EMPTY_STRING_ARRAY;
		spMultiwayMergeJoin.setKeys(Arrays.asList(keys));
		
		return spMultiwayMergeJoin;
	}

	@Override
	public void decodeParameterObject(StepMeta stepMeta, Object po, List<DatabaseMeta> databases,TransMeta transMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPMultiwayMergeJoin spMultiwayMergeJoin= (SPMultiwayMergeJoin)po;
		MultiMergeJoinMeta  multimergejoinmeta= (MultiMergeJoinMeta )stepMetaInterface;
		
		multimergejoinmeta.setJoinType(spMultiwayMergeJoin.getJoinType());
		multimergejoinmeta.setInputSteps(spMultiwayMergeJoin.getInputSteps().toArray(new String[] {}));
		multimergejoinmeta.setKeyFields(spMultiwayMergeJoin.getKeys().toArray(new String[] {}));
	}

	@Override
	public void getStepDataAndRelationship(TransMeta transMeta, StepMeta stepMeta, StepDataRelationship sdr)
			throws Exception {
	}
	
	@Override
	public void setLinesFromCacheLines(StepLinesDto lines , StepLinesDto cacheLines) {
			//该组件会预先读取一行,比较后将有效数据输入写出,所以写出的行号对应的读入需要减1
			Map<String, Long> preMap = cacheLines.getPreEffectiveInputLines();
			if(  preMap != null && preMap.size() > 0) {
				for(String key :preMap.keySet()) {
					if(preMap.get(key) >1) {
						preMap.put(key, preMap.get(key)-1);
					}
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
		// TODO Auto-generated method stub
		return 12;
	}

}
