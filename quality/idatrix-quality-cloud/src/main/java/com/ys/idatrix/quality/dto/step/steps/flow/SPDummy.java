package com.ys.idatrix.quality.dto.step.steps.flow;

import java.util.List;

import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ys.idatrix.quality.dto.step.steps.StepParameter;
import com.ys.idatrix.quality.toolkit.analyzer.trans.step.StepDataRelationship;
import com.ys.idatrix.quality.toolkit.analyzer.trans.step.StepDataRelationshipParser;

import net.sf.json.JSONObject;

/**
 * Step - Dummy(空操作). 转换 org.pentaho.di.trans.steps.dummytrans.DummyTransMeta
 * 
 * @author XH
 * @since 2018-04-11
 */
@Component("SPDummy")
@Scope("prototype")
public class SPDummy implements StepParameter , StepDataRelationshipParser{

	@Override
	public Object getParameterObject(Object json) {

		JSONObject jsonObj = JSONObject.fromObject(json);
		return (SPDummy) JSONObject.toBean(jsonObj, SPDummy.class);
	}

	@Override
	public Object encodeParameterObject(StepMeta stepMeta) {
//		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPDummy spDummy= new SPDummy();
//		DummyTransMeta dummytransmeta= (DummyTransMeta )stepMetaInterface;

		return spDummy;
	}

	@Override
	public void decodeParameterObject(StepMeta stepMeta, Object po, List<DatabaseMeta> databases,TransMeta transMeta) throws Exception {
//		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
//		SPDummy spDummy= (SPDummy)po;
//		DummyTransMeta  dummytransmeta= (DummyTransMeta )stepMetaInterface;

	}

	@Override
	public void getStepDataAndRelationship(TransMeta transMeta, StepMeta stepMeta, StepDataRelationship sdr) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
