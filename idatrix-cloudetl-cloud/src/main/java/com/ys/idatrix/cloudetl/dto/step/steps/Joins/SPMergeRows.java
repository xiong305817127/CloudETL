package com.ys.idatrix.cloudetl.dto.step.steps.Joins;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.step.errorhandling.StreamInterface;
import org.pentaho.di.trans.steps.mergerows.MergeRowsMeta;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ys.idatrix.cloudetl.dto.step.steps.StepParameter;
import com.ys.idatrix.cloudetl.recovery.trans.ResumeStepDataParser;
import com.ys.idatrix.cloudetl.recovery.trans.dto.StepLinesDto;
import com.ys.idatrix.cloudetl.toolkit.analyzer.trans.step.StepDataRelationship;
import com.ys.idatrix.cloudetl.toolkit.analyzer.trans.step.StepDataRelationshipParser;
import com.ys.idatrix.cloudetl.toolkit.domain.Relationship;
import com.ys.idatrix.cloudetl.toolkit.utils.RelationshipUtil;

import net.sf.json.JSONObject;

/**
 * Step - MergeRows(合并记录).
 * 转换  org.pentaho.di.trans.steps.mergerows.MergeRowsMeta
 * 
 * @author XH
 * @since 2018-04-10
 */
@Component("SPMergeRows")
@Scope("prototype")
public class SPMergeRows implements StepParameter, StepDataRelationshipParser, ResumeStepDataParser {
	

	private List<String> keyFields;
	private List<String> valueFields;
	private String flagField;
	private String oldStep ; //reference;
	private String newStep ; //compare;
	

	/**
	 * @return the flagField
	 */
	public String getFlagField() {
		return flagField;
	}

	/**
	 * @param  设置 flagField
	 */
	public void setFlagField(String flagField) {
		this.flagField = flagField;
	}

	/**
	 * @return the oldStep
	 */
	public String getOldStep() {
		return oldStep;
	}

	/**
	 * @param  设置 oldStep
	 */
	public void setOldStep(String oldStep) {
		this.oldStep = oldStep;
	}

	/**
	 * @return the newStep
	 */
	public String getNewStep() {
		return newStep;
	}

	/**
	 * @param  设置 newStep
	 */
	public void setNewStep(String newStep) {
		this.newStep = newStep;
	}
	
	/**
	 * @return the keyFields
	 */
	public List<String> getKeyFields() {
		return keyFields;
	}

	/**
	 * @param  设置 keyFields
	 */
	public void setKeyFields(List<String> keyFields) {
		this.keyFields = keyFields;
	}

	/**
	 * @return the valueFields
	 */
	public List<String> getValueFields() {
		return valueFields;
	}

	/**
	 * @param  设置 valueFields
	 */
	public void setValueFields(List<String> valueFields) {
		this.valueFields = valueFields;
	}

	@Override
	public Object getParameterObject(Object json) {
		JSONObject jsonObj = JSONObject.fromObject(json);
		return (SPMergeRows) JSONObject.toBean(jsonObj, SPMergeRows.class);
	}

	@Override
	public Object encodeParameterObject(StepMeta stepMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPMergeRows spMergeRows= new SPMergeRows();
		MergeRowsMeta mergerowsmeta= (MergeRowsMeta )stepMetaInterface;
		spMergeRows.setFlagField(mergerowsmeta.getFlagField());
		
		List<StreamInterface> infoStreams = mergerowsmeta.getStepIOMeta().getInfoStreams();
		if(infoStreams.size() > 0 &&  infoStreams.get( 0 ) != null) {
			 spMergeRows.setOldStep( infoStreams.get( 0 ).getStepname() );
		}
		if(infoStreams.size() > 1 &&  infoStreams.get( 1 ) != null) {
			 spMergeRows.setNewStep( infoStreams.get( 1 ).getStepname() );
		}
		if(mergerowsmeta.getKeyFields() != null && mergerowsmeta.getKeyFields().length>0 ) {
			spMergeRows.setKeyFields(Arrays.asList(mergerowsmeta.getKeyFields()));
		}
		
		if(mergerowsmeta.getValueFields() != null && mergerowsmeta.getValueFields().length>0 ) {
			spMergeRows.setValueFields( Arrays.asList(mergerowsmeta.getValueFields()));
		}
		return spMergeRows;
	}

	@Override
	public void decodeParameterObject(StepMeta stepMeta, Object po, List<DatabaseMeta> databases,TransMeta transMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPMergeRows spMergeRows= (SPMergeRows)po;
		MergeRowsMeta  mergerowsmeta= (MergeRowsMeta )stepMetaInterface;
		
		mergerowsmeta.setFlagField(spMergeRows.getFlagField());
		
		List<StreamInterface> infoStreams = mergerowsmeta.getStepIOMeta().getInfoStreams();
		if(  infoStreams.size() > 0 &&  infoStreams.get( 0 ) != null) {
			 StreamInterface referenceStream = infoStreams.get( 0 );
			 if(!Utils.isEmpty( spMergeRows.getOldStep() ) ) {
				 referenceStream.setSubject( spMergeRows.getOldStep() );
				 referenceStream.setStepMeta(transMeta.findStep(spMergeRows.getOldStep()));
			 }else {
				 referenceStream.setSubject( null );
				 referenceStream.setStepMeta(null);
			 }
			 
		}
		if(infoStreams.size() > 1 &&  infoStreams.get( 1 ) != null) {
			 StreamInterface compareStream = infoStreams.get( 1 );
			 if( !Utils.isEmpty( spMergeRows.getNewStep() ) ) {
				  compareStream.setSubject(spMergeRows.getNewStep() );
				  compareStream.setStepMeta(transMeta.findStep(spMergeRows.getNewStep()));
			 }else {
				  compareStream.setSubject(null );
				  compareStream.setStepMeta(null);
			 }
		   
		}
		if(spMergeRows.getKeyFields()!= null && spMergeRows.getKeyFields().size() >0) {
			mergerowsmeta.setKeyFields(spMergeRows.getKeyFields().toArray(new String[] {}));
		}else {
			mergerowsmeta.setKeyFields(new String[] {});
		}
		if(spMergeRows.getValueFields() != null && spMergeRows.getValueFields().size()>0) {
			mergerowsmeta.setValueFields(spMergeRows.getValueFields().toArray(new String[] {}));
		}else {
			mergerowsmeta.setValueFields(new String[] {});
		}
	}

	@Override
	public void getStepDataAndRelationship(TransMeta transMeta, StepMeta stepMeta, StepDataRelationship sdr)
			throws Exception {
		
		String from= "转换:"+transMeta.getName()+",步骤:"+stepMeta.getName();
		
		MergeRowsMeta  mergerowsmeta= (MergeRowsMeta )stepMeta.getStepMetaInterface();
		String flag = mergerowsmeta.getFlagField();
		String dummyId = transMeta.getName()+"-"+stepMeta.getName()+"-"+flag ;
		
		// 增加 系统节点 和 流节点的关系
		Relationship relationship = RelationshipUtil.buildDummyRelationship(from, dummyId, flag);
		sdr.addRelationship(relationship);
		sdr.addInputDataNode(relationship.getStartNode());
		
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
		return 12;
	}

}
