/**
 * 云化数据集成系统 iDatrix CloudETL
 */
package com.ys.idatrix.quality.dto.step.steps.transfor;

import java.util.List;
import java.util.Map;

import org.pentaho.di.cluster.SlaveServer;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.getslavesequence.GetSlaveSequenceMeta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.ys.idatrix.quality.dto.step.StepFieldDto;
import com.ys.idatrix.quality.dto.step.steps.StepParameter;
import com.ys.idatrix.quality.service.server.CloudServerService;
import com.ys.idatrix.quality.toolkit.analyzer.trans.step.StepDataRelationship;
import com.ys.idatrix.quality.toolkit.analyzer.trans.step.StepDataRelationshipParser;
import com.ys.idatrix.quality.toolkit.domain.DataNode;
import com.ys.idatrix.quality.toolkit.domain.Relationship;
import com.ys.idatrix.quality.toolkit.utils.DataNodeUtil;
import com.ys.idatrix.quality.toolkit.utils.RelationshipUtil;

import net.sf.json.JSONObject;

/**
 * Step - Get Slave Sequence
 * org.pentaho.di.trans.steps.getslavesequence.GetSlaveSequenceMeta
 * 
 * @author XH
 * @since 2017年6月9日
 *
 */
@Component("SPGetSlaveSequence")
@Scope("prototype")
public class SPGetSlaveSequence implements StepParameter, StepDataRelationshipParser {

	@Autowired
	CloudServerService cloudServerService;

	String valuename;
	String slaveServerName;
	String sequenceName;
	String increment;

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
	 * @return slaveServerName
	 */
	public String getSlaveServerName() {
		return slaveServerName;
	}

	/**
	 * @param slaveServerName
	 *            要设置的 slaveServerName
	 */
	public void setSlaveServerName(String slaveServerName) {
		this.slaveServerName = slaveServerName;
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
	 * @return increment
	 */
	public String getIncrement() {
		return increment;
	}

	/**
	 * @param increment
	 *            要设置的 increment
	 */
	public void setIncrement(String increment) {
		this.increment = increment;
	}

	/* 
	 * 
	 */
	@Override
	public Object getParameterObject(Object json) {
		JSONObject jsonObj = JSONObject.fromObject(json);
		return (SPGetSlaveSequence) JSONObject.toBean(jsonObj, SPGetSlaveSequence.class);
	}

	/* 
	 * 
	 */
	@Override
	public Object encodeParameterObject(StepMeta stepMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPGetSlaveSequence spGetSlaveSequence = new SPGetSlaveSequence();
		GetSlaveSequenceMeta getslavesequencemeta = (GetSlaveSequenceMeta) stepMetaInterface;

		spGetSlaveSequence.setValuename(getslavesequencemeta.getValuename());
		spGetSlaveSequence.setSequenceName(getslavesequencemeta.getSequenceName());
		spGetSlaveSequence.setIncrement(getslavesequencemeta.getIncrement());
		spGetSlaveSequence.setSlaveServerName(getslavesequencemeta.getSlaveServerName());
		return spGetSlaveSequence;
	}

	/* 
	 * 
	 */
	@Override
	public void decodeParameterObject(StepMeta stepMeta, Object po, List<DatabaseMeta> databases,TransMeta transMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();

		SPGetSlaveSequence spGetSlaveSequence = (SPGetSlaveSequence) po;
		GetSlaveSequenceMeta getslavesequencemeta = (GetSlaveSequenceMeta) stepMetaInterface;

		getslavesequencemeta.setValuename(spGetSlaveSequence.getValuename());
		getslavesequencemeta.setSequenceName(spGetSlaveSequence.getSequenceName());
		getslavesequencemeta.setIncrement(spGetSlaveSequence.getIncrement());
		getslavesequencemeta.setSlaveServerName(spGetSlaveSequence.getSlaveServerName());

		if (!Utils.isEmpty(spGetSlaveSequence.getSlaveServerName())) {
			transMeta.addOrReplaceSlaveServer(
					cloudServerService.findSlaveServer(null,spGetSlaveSequence.getSlaveServerName()));
		}
	}

	/*
	 * 覆盖方法：getStepDataAndRelationship
	 */
	@Override
	public void getStepDataAndRelationship(TransMeta transMeta, StepMeta stepMeta, StepDataRelationship sdr) throws Exception {
		
		String from= "转换:"+transMeta.getName()+",步骤:"+stepMeta.getName();
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		GetSlaveSequenceMeta getslavesequencemeta = (GetSlaveSequenceMeta) stepMetaInterface;
		//输出
		String vn = getslavesequencemeta.getValuename();
		if( !Utils.isEmpty(vn)) {
			SlaveServer slave = cloudServerService.findSlaveServer(null,getslavesequencemeta.getSlaveServerName());
			
			StepFieldDto stepFieldDto = new StepFieldDto();
			stepFieldDto.setName(vn);
			stepFieldDto.setOrigin(stepMeta.getName());
			
			Map<String, DataNode> itemNodes = DataNodeUtil.interfaceNodeParse("Http", "slave-"+slave.getHostname()+"/"+slave.getName(), "GetIdFromSlaveSequence", stepMeta.getName(), Lists.newArrayList( stepFieldDto) );
			DataNode itemNode = itemNodes.get(vn);
			if( itemNode != null ) {
				sdr.getOutputDataNodes().addAll(itemNodes.values());
				Relationship relationship = RelationshipUtil.buildFieldRelationship(itemNode, null, from, null, vn) ;
				sdr.addRelationship(relationship);
			}
		
		}
		
	}

}
