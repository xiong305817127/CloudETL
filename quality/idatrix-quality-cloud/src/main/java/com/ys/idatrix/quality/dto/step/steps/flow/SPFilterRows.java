package com.ys.idatrix.quality.dto.step.steps.flow;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.filterrows.FilterRowsData;
import org.pentaho.di.trans.steps.filterrows.FilterRowsMeta;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ys.idatrix.quality.dto.step.parts.ConditionDto;
import com.ys.idatrix.quality.dto.step.steps.StepParameter;
import com.ys.idatrix.quality.recovery.trans.ResumeStepDataParser;
import com.ys.idatrix.quality.toolkit.analyzer.trans.step.StepDataRelationship;
import com.ys.idatrix.quality.toolkit.analyzer.trans.step.StepDataRelationshipParser;

import net.sf.json.JSONObject;

/**
 * Step - FilterRows(过滤记录).
 * 转换  org.pentaho.di.trans.steps.filterrows.FilterRowsMeta
 * 
 * @author XH
 * @since 2017-09-04
 */
@Component("SPFilterRows")
@Scope("prototype")
public class SPFilterRows implements StepParameter, StepDataRelationshipParser ,ResumeStepDataParser{
	
	String sendTrueTo;
	String sendFalseTo;
	
	ConditionDto condition;

	/**
	 * @return the sendTrueTo
	 */
	public String getSendTrueTo() {
		return sendTrueTo;
	}

	/**
	 * @param  设置 sendTrueTo
	 */
	public void setSendTrueTo(String sendTrueTo) {
		this.sendTrueTo = sendTrueTo;
	}

	/**
	 * @return the sendFalseTo
	 */
	public String getSendFalseTo() {
		return sendFalseTo;
	}

	/**
	 * @param  设置 sendFalseTo
	 */
	public void setSendFalseTo(String sendFalseTo) {
		this.sendFalseTo = sendFalseTo;
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
		return (SPFilterRows) JSONObject.toBean(jsonObj, SPFilterRows.class,classMap);
	}

	@Override
	public Object encodeParameterObject(StepMeta stepMeta) {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPFilterRows spFilterRows= new SPFilterRows();
		FilterRowsMeta filterrowsmeta= (FilterRowsMeta )stepMetaInterface;

		spFilterRows.setSendTrueTo( filterrowsmeta.getTrueStepname()) ;
		spFilterRows.setSendFalseTo( filterrowsmeta.getFalseStepname()) ;
		 if ( filterrowsmeta.getCondition() != null ) {
			 spFilterRows.setCondition(new ConditionDto(filterrowsmeta.getCondition()));
		 }
		return spFilterRows;
	}

	@Override
	public void decodeParameterObject(StepMeta stepMeta, Object po, List<DatabaseMeta> databases,TransMeta transMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		
		SPFilterRows spFilterRows= (SPFilterRows)po;
		FilterRowsMeta  filterrowsmeta= (FilterRowsMeta )stepMetaInterface;
		
		filterrowsmeta.setTrueStepname(spFilterRows.getSendTrueTo() ) ;
		filterrowsmeta.setFalseStepname(spFilterRows.getSendFalseTo() ) ;
		if(spFilterRows.getCondition() != null){
			filterrowsmeta.setCondition(spFilterRows.getCondition().transToCodition());
		}
	
		filterrowsmeta.searchInfoAndTargetSteps(transMeta.getSteps());
		
		
	}

	/*
	 * 覆盖方法：getStepDataAndRelationship
	 */
	@Override
	public void getStepDataAndRelationship(TransMeta transMeta, StepMeta stepMeta, StepDataRelationship sdr) {
		// TODO 自动生成的方法存根
		
	}

	@Override
	public boolean preRunHandle(TransMeta transMeta ,StepMeta stepMeta ,StepMetaInterface stepMetaInterface,StepDataInterface stepDataInterface , StepInterface stepInterface)  throws Exception {
		
		
		FilterRowsData  data = ( FilterRowsData )stepDataInterface;
	    
	    if ( data.chosesTargetSteps ) {
	    	//该组件只有分发,没有复制模式
			stepMeta.setDistributes(true);
	     }
		return true ;
	}
	
	@Override
	public int stepType() {
		return 12;
		
	}


	

	
}
