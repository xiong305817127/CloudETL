package com.ys.idatrix.quality.dto.step.steps.Joins;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.pentaho.di.core.Condition;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.joinrows.JoinRowsMeta;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ys.idatrix.quality.dto.step.parts.ConditionDto;
import com.ys.idatrix.quality.dto.step.steps.StepParameter;
import com.ys.idatrix.quality.recovery.trans.ResumeStepDataParser;
import com.ys.idatrix.quality.toolkit.analyzer.trans.step.StepDataRelationship;
import com.ys.idatrix.quality.toolkit.analyzer.trans.step.StepDataRelationshipParser;

import net.sf.json.JSONObject;

/**
 * Step - JoinRows(记录关联 (笛卡尔输出)).
 * 转换  org.pentaho.di.trans.steps.joinrows.JoinRowsMeta
 * 
 * @author XH
 * @since 2018-04-11
 */
@Component("SPJoinRows")
@Scope("prototype")
public class SPJoinRows implements StepParameter, StepDataRelationshipParser, ResumeStepDataParser {

	String directory = "${java.io.tmpdir}";
	String prefix;
	int cacheSize;
	String mainStep ;
	
	ConditionDto condition ;
	
	/**
	 * @return the directory
	 */
	public String getDirectory() {
		return directory;
	}

	/**
	 * @param  设置 directory
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
	 * @param  设置 prefix
	 */
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	/**
	 * @return the cacheSize
	 */
	public int getCacheSize() {
		return cacheSize;
	}

	/**
	 * @param  设置 cacheSize
	 */
	public void setCacheSize(int cacheSize) {
		this.cacheSize = cacheSize;
	}

	/**
	 * @return the mainStep
	 */
	public String getMainStep() {
		return mainStep;
	}

	/**
	 * @param  设置 mainStep
	 */
	public void setMainStep(String mainStep) {
		this.mainStep = mainStep;
	}

	/**
	 * @return the condition
	 */
	public ConditionDto getCondition() {
		return condition;
	}

	/**
	 * @param  设置 condition
	 */
	public void setCondition(ConditionDto condition) {
		this.condition = condition;
	}

	@Override
	public Object getParameterObject(Object json) {
		JSONObject jsonObj = JSONObject.fromObject(json);
		Map<String, Class<?>> classMap = new HashMap<>();
		classMap.put("condition", ConditionDto.class);
		classMap.put("conditions", ConditionDto.class);
		return (SPJoinRows) JSONObject.toBean(jsonObj, SPJoinRows.class,classMap);
	}

	@Override
	public Object encodeParameterObject(StepMeta stepMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPJoinRows spJoinRows= new SPJoinRows();
		JoinRowsMeta joinrowsmeta= (JoinRowsMeta )stepMetaInterface;

		spJoinRows.setMainStep(joinrowsmeta.getLookupStepname());
		spJoinRows.setCacheSize(joinrowsmeta.getCacheSize());
		spJoinRows.setDirectory(joinrowsmeta.getDirectory());
		spJoinRows.setPrefix(joinrowsmeta.getPrefix());
		spJoinRows.setCondition(new ConditionDto(joinrowsmeta.getCondition()));
		
		return spJoinRows;
	}

	@Override
	public void decodeParameterObject(StepMeta stepMeta, Object po, List<DatabaseMeta> databases,TransMeta transMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPJoinRows spJoinRows= (SPJoinRows)po;
		JoinRowsMeta  joinrowsmeta= (JoinRowsMeta )stepMetaInterface;

		joinrowsmeta.setMainStepname(spJoinRows.getMainStep());
		joinrowsmeta.setCacheSize(spJoinRows.getCacheSize());
		joinrowsmeta.setDirectory(spJoinRows.getDirectory());
		joinrowsmeta.setPrefix(spJoinRows.getPrefix());
		if(spJoinRows.getCondition() != null ) {
			joinrowsmeta.setCondition(spJoinRows.getCondition().transToCodition());
		}else {
			joinrowsmeta.setCondition(new Condition());
		}
	}

	@Override
	public void getStepDataAndRelationship(TransMeta transMeta, StepMeta stepMeta, StepDataRelationship sdr)
			throws Exception {
		
	}

	@Override
	public int stepType() {
		return 0;
	}
	
}
