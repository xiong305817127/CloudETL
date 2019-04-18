package com.ys.idatrix.quality.dto.step.steps.Joins;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.step.errorhandling.StreamInterface;
import org.pentaho.di.trans.steps.mergejoin.MergeJoinMeta;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ys.idatrix.quality.dto.step.steps.StepParameter;
import com.ys.idatrix.quality.recovery.trans.ResumeStepDataParser;
import com.ys.idatrix.quality.recovery.trans.dto.StepLinesDto;
import com.ys.idatrix.quality.toolkit.analyzer.trans.step.StepDataRelationship;
import com.ys.idatrix.quality.toolkit.analyzer.trans.step.StepDataRelationshipParser;

import net.sf.json.JSONObject;

/**
 * Step - MergeJoin(记录集连接).
 * 转换  org.pentaho.di.trans.steps.mergejoin.MergeJoinMeta
 * 
 * @author XH
 * @since 2018-04-11
 */
@Component("SPMergeJoin")
@Scope("prototype")
public class SPMergeJoin implements StepParameter, StepDataRelationshipParser, ResumeStepDataParser {

	String joinType;
	String step1;
	String step2;
	List<String> keyFields1;
	List<String> keyFields2;
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
	/**
	 * @return the step1
	 */
	public String getStep1() {
		return step1;
	}
	/**
	 * @param  设置 step1
	 */
	public void setStep1(String step1) {
		this.step1 = step1;
	}
	/**
	 * @return the step2
	 */
	public String getStep2() {
		return step2;
	}
	/**
	 * @param  设置 step2
	 */
	public void setStep2(String step2) {
		this.step2 = step2;
	}
	/**
	 * @return the keyFields1
	 */
	public List<String> getKeyFields1() {
		return keyFields1;
	}
	/**
	 * @param  设置 keyFields1
	 */
	public void setKeyFields1(List<String> keyFields1) {
		this.keyFields1 = keyFields1;
	}
	/**
	 * @return the keyFields2
	 */
	public List<String> getKeyFields2() {
		return keyFields2;
	}
	/**
	 * @param  设置 keyFields2
	 */
	public void setKeyFields2(List<String> keyFields2) {
		this.keyFields2 = keyFields2;
	}
	@Override
	public Object getParameterObject(Object json) {
		JSONObject jsonObj = JSONObject.fromObject(json);
		return (SPMergeJoin) JSONObject.toBean(jsonObj, SPMergeJoin.class);
	}
	@Override
	public Object encodeParameterObject(StepMeta stepMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		
		SPMergeJoin spMergeJoin= new SPMergeJoin();
		MergeJoinMeta mergejoinmeta= (MergeJoinMeta )stepMetaInterface;

		spMergeJoin.setJoinType(mergejoinmeta.getJoinType());
		List<StreamInterface> infoStreams = mergejoinmeta.getStepIOMeta().getInfoStreams();
		if(infoStreams.size() > 0 &&  infoStreams.get( 0 ) != null) {
			spMergeJoin.setStep1( infoStreams.get( 0 ).getStepname() );
		}
		if(infoStreams.size() > 1 &&  infoStreams.get( 1 ) != null) {
			spMergeJoin.setStep2( infoStreams.get( 1 ).getStepname() );
		}
		if(mergejoinmeta.getKeyFields1() != null && mergejoinmeta.getKeyFields1().length>0 ) {
			spMergeJoin.setKeyFields1(Arrays.asList(mergejoinmeta.getKeyFields1()));
		}
		
		if(mergejoinmeta.getKeyFields2() != null && mergejoinmeta.getKeyFields2().length>0 ) {
			spMergeJoin.setKeyFields2(Arrays.asList(mergejoinmeta.getKeyFields2()));
		}
		
		return spMergeJoin;
	}
	@Override
	public void decodeParameterObject(StepMeta stepMeta, Object po, List<DatabaseMeta> databases,TransMeta transMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPMergeJoin spMergeJoin= (SPMergeJoin)po;
		MergeJoinMeta  mergejoinmeta= (MergeJoinMeta )stepMetaInterface;

		mergejoinmeta.setJoinType(spMergeJoin.getJoinType());
		
		List<StreamInterface> infoStreams = mergejoinmeta.getStepIOMeta().getInfoStreams();
		if( infoStreams.size() > 0 &&  infoStreams.get( 0 ) != null) {
			 StreamInterface step_1 = infoStreams.get( 0 );
			 if( !Utils.isEmpty( spMergeJoin.getStep1() ) ) {
				 step_1.setSubject( spMergeJoin.getStep1() );
				 step_1.setStepMeta(transMeta.findStep(spMergeJoin.getStep1()));
			 }else {
				 step_1.setSubject(null);
				 step_1.setStepMeta(null);
			 }
			
		}
		if(infoStreams.size() > 1 &&  infoStreams.get( 1 ) != null) {
			 StreamInterface step_2 = infoStreams.get( 1 );
			 if(!Utils.isEmpty( spMergeJoin.getStep2() ) ) {
				 step_2.setSubject(spMergeJoin.getStep2() );
				 step_2.setStepMeta(transMeta.findStep(spMergeJoin.getStep2()));
			 }else {
				 step_2.setSubject(null);
				 step_2.setStepMeta(null);
			 }
		}
		if(spMergeJoin.getKeyFields1()!= null && spMergeJoin.getKeyFields1().size() >0) {
			mergejoinmeta.setKeyFields1(spMergeJoin.getKeyFields1().toArray(new String[] {}));
		}else {
			mergejoinmeta.setKeyFields1(new String[] {});
		}
		if(spMergeJoin.getKeyFields2() != null && spMergeJoin.getKeyFields2().size()>0) {
			mergejoinmeta.setKeyFields2(spMergeJoin.getKeyFields2().toArray(new String[] {}));
		}else {
			mergejoinmeta.setKeyFields2(new String[] {});
		}
		
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
		return 12;
	}
}
