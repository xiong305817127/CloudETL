/**
 * 云化数据集成系统 iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.dto.step.steps.transfor;

import java.util.List;
import java.util.Map;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.addsequence.AddSequenceData;
import org.pentaho.di.trans.steps.addsequence.AddSequenceMeta;
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
 * Step - Add Sequence(增加序列) 转换
 * org.pentaho.di.trans.steps.addsequence.AddSequenceMeta
 * 
 * @author XH
 * @since 2017年6月12日
 *
 */
@Component("SPSequence")
@Scope("prototype")
public class SPAddSequence implements StepParameter, StepDataRelationshipParser,ResumeStepDataParser {

	String valuename;
	boolean useDatabase;
	String connection;
	String schemaName;
	String sequenceName;
	boolean useCounter;
	String counterName;
	String startAt;
	String incrementBy;
	String maxValue;

	/**
	 * @return valuename
	 */
	public String getValuename() {
		return valuename;
	}

	/**
	 * @param valuename
	 *            要设置的 valuename
	 */
	public void setValuename(String valuename) {
		this.valuename = valuename;
	}

	/**
	 * @return useDatabase
	 */
	public boolean isUseDatabase() {
		return useDatabase;
	}

	/**
	 * @param useDatabase
	 *            要设置的 useDatabase
	 */
	public void setUseDatabase(boolean useDatabase) {
		this.useDatabase = useDatabase;
	}

	/**
	 * @return connection
	 */
	public String getConnection() {
		return connection;
	}

	/**
	 * @param connection
	 *            要设置的 connection
	 */
	public void setConnection(String connection) {
		this.connection = connection;
	}

	/**
	 * @return schemaName
	 */
	public String getSchemaName() {
		return schemaName;
	}

	/**
	 * @param schemaName
	 *            要设置的 schemaName
	 */
	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}

	/**
	 * @return sequenceName
	 */
	public String getSequenceName() {
		return sequenceName;
	}

	/**
	 * @param sequenceName
	 *            要设置的 sequenceName
	 */
	public void setSequenceName(String sequenceName) {
		this.sequenceName = sequenceName;
	}

	/**
	 * @return useCounter
	 */
	public boolean isUseCounter() {
		return useCounter;
	}

	/**
	 * @param useCounter
	 *            要设置的 useCounter
	 */
	public void setUseCounter(boolean useCounter) {
		this.useCounter = useCounter;
	}

	/**
	 * @return counterName
	 */
	public String getCounterName() {
		return counterName;
	}

	/**
	 * @param counterName
	 *            要设置的 counterName
	 */
	public void setCounterName(String counterName) {
		this.counterName = counterName;
	}

	/**
	 * @return startAt
	 */
	public String getStartAt() {
		return startAt;
	}

	/**
	 * @param startAt
	 *            要设置的 startAt
	 */
	public void setStartAt(String startAt) {
		this.startAt = startAt;
	}

	/**
	 * @return incrementBy
	 */
	public String getIncrementBy() {
		return incrementBy;
	}

	/**
	 * @param incrementBy
	 *            要设置的 incrementBy
	 */
	public void setIncrementBy(String incrementBy) {
		this.incrementBy = incrementBy;
	}

	/**
	 * @return maxValue
	 */
	public String getMaxValue() {
		return maxValue;
	}

	/**
	 * @param maxValue
	 *            要设置的 maxValue
	 */
	public void setMaxValue(String maxValue) {
		this.maxValue = maxValue;
	}

	/* 
	 * 
	 */
	@Override
	public Object getParameterObject(Object json) {
		JSONObject jsonObj = JSONObject.fromObject(json);
		return (SPAddSequence) JSONObject.toBean(jsonObj, SPAddSequence.class);
	}

	/* 
	 * 
	 */
	@Override
	public Object encodeParameterObject(StepMeta stepMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPAddSequence spAddSequence = new SPAddSequence();
		AddSequenceMeta addsequencemeta = (AddSequenceMeta) stepMetaInterface;

		spAddSequence.setValuename(addsequencemeta.getValuename());
		spAddSequence.setCounterName(addsequencemeta.getCounterName());
		spAddSequence.setSchemaName(addsequencemeta.getSchemaName());
		spAddSequence
				.setConnection(addsequencemeta.getDatabase() == null ? "" : addsequencemeta.getDatabase().getName());
		spAddSequence.setIncrementBy(addsequencemeta.getIncrementBy());
		spAddSequence.setMaxValue(addsequencemeta.getMaxValue());
		spAddSequence.setSequenceName(addsequencemeta.getSequenceName());
		spAddSequence.setStartAt(addsequencemeta.getStartAt());
		spAddSequence.setUseDatabase(addsequencemeta.isDatabaseUsed());
		spAddSequence.setUseCounter(addsequencemeta.isCounterUsed());
		return spAddSequence;
	}

	/* 
	 * 
	 */
	@Override
	public void decodeParameterObject(StepMeta stepMeta, Object po, List<DatabaseMeta> databases,TransMeta transMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPAddSequence spAddSequence = (SPAddSequence) po;
		AddSequenceMeta addsequencemeta = (AddSequenceMeta) stepMetaInterface;

		addsequencemeta.setUseDatabase(spAddSequence.isUseDatabase());
		addsequencemeta.setValuename(spAddSequence.getValuename());
		addsequencemeta.setCounterName(spAddSequence.getCounterName());
		
		DatabaseMeta d = DatabaseMeta.findDatabase(databases, spAddSequence.getConnection());
		if( d != null) {
			transMeta.addOrReplaceDatabase(d);
			addsequencemeta.setDatabase(d);
		}
		
		addsequencemeta.setIncrementBy(spAddSequence.getIncrementBy());
		addsequencemeta.setMaxValue(spAddSequence.getMaxValue());
		addsequencemeta.setSequenceName(spAddSequence.getSequenceName());
		addsequencemeta.setStartAt(spAddSequence.getStartAt());
		addsequencemeta.setUseCounter(spAddSequence.isUseCounter());
		addsequencemeta.setSchemaName(spAddSequence.getSchemaName());

	}

	/*
	 * 覆盖方法：getStepDataAndRelationship
	 */
	@Override
	public void getStepDataAndRelationship(TransMeta transMeta, StepMeta stepMeta, StepDataRelationship sdr) throws Exception {
		
		String from= "转换:"+transMeta.getName()+",步骤:"+stepMeta.getName();
		
		AddSequenceMeta  addsequencemeta= (AddSequenceMeta )stepMeta.getStepMetaInterface();
		String valueName = addsequencemeta.getValuename();
		String dummyId = transMeta.getName()+"-"+stepMeta.getName()+"-"+valueName ;
		Relationship relationship = RelationshipUtil.buildDummyRelationship(from, dummyId, valueName);
		
		sdr.getDataRelationship().add(relationship);
		sdr.addInputDataNode( relationship.getStartNode());
		

	}

	@Override
	public boolean resumeCacheData(Map<Object,Object> cacheData,StepLinesDto linesDto ,TransMeta transMeta ,StepMeta stepMeta ,StepMetaInterface stepMetaInterface,StepDataInterface stepDataInterface , StepInterface stepInterface)  throws Exception {
		AddSequenceMeta meta = (AddSequenceMeta)stepMetaInterface;
		AddSequenceData data = (AddSequenceData)stepDataInterface;
		
		if ( meta.isCounterUsed() ) {
			
			 long nval = linesDto.getLinesWritten()+data.start ;
			if ( data.increment > 0 && data.maximum > data.start && nval > data.maximum ) {
		          nval = data.start;
		    }
		    if ( data.increment < 0 && data.maximum < data.start && nval < data.maximum ) {
		         nval = data.start;
		    }
		    data.counter.setCounter( nval );
		}
		
		return true;
		
	}

	@Override
	public int stepType() {
		return 4;
	}

}
