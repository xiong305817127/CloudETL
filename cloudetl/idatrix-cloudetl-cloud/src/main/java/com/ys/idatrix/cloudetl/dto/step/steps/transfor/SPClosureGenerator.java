/**
 * 云化数据集成系统 iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.dto.step.steps.transfor;

import java.util.List;

import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.closure.ClosureGeneratorMeta;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ys.idatrix.cloudetl.dto.step.steps.StepParameter;
import com.ys.idatrix.cloudetl.recovery.trans.ResumeStepDataParser;
import com.ys.idatrix.cloudetl.toolkit.analyzer.trans.step.StepDataRelationship;
import com.ys.idatrix.cloudetl.toolkit.analyzer.trans.step.StepDataRelationshipParser;
import com.ys.idatrix.cloudetl.toolkit.utils.RelationshipUtil;

import net.sf.json.JSONObject;

/**
 *  Step - Closure Generator(闭包生成器)
 *  org.pentaho.di.trans.steps.closure.ClosureGeneratorMeta
 * @author XH
 * @since 2017年6月9日
 *
 */
@Component("SPClosureGenerator")
@Scope("prototype")
public class SPClosureGenerator implements StepParameter, StepDataRelationshipParser,ResumeStepDataParser {

	String parentIdFieldName;
	String childIdFieldName;
	String distanceFieldName;
	boolean rootIdZero;

	/**
	 * @return parentIdFieldName
	 */
	public String getParentIdFieldName() {
		return parentIdFieldName;
	}

	/**
	 * @param parentIdFieldName
	 *            要设置的 parentIdFieldName
	 */
	public void setParentIdFieldName(String parentIdFieldName) {
		this.parentIdFieldName = parentIdFieldName;
	}

	/**
	 * @return childIdFieldName
	 */
	public String getChildIdFieldName() {
		return childIdFieldName;
	}

	/**
	 * @param childIdFieldName
	 *            要设置的 childIdFieldName
	 */
	public void setChildIdFieldName(String childIdFieldName) {
		this.childIdFieldName = childIdFieldName;
	}

	/**
	 * @return distanceFieldName
	 */
	public String getDistanceFieldName() {
		return distanceFieldName;
	}

	/**
	 * @param distanceFieldName
	 *            要设置的 distanceFieldName
	 */
	public void setDistanceFieldName(String distanceFieldName) {
		this.distanceFieldName = distanceFieldName;
	}

	/**
	 * @return rootIdZero
	 */
	public boolean isRootIdZero() {
		return rootIdZero;
	}

	/**
	 * @param rootIdZero
	 *            要设置的 rootIdZero
	 */
	public void setRootIdZero(boolean rootIdZero) {
		this.rootIdZero = rootIdZero;
	}

	/* 
	 * 
	 */
	@Override
	public Object getParameterObject(Object json) {
		JSONObject jsonObj = JSONObject.fromObject(json);
		return (SPClosureGenerator) JSONObject.toBean(jsonObj, SPClosureGenerator.class);
	}

	/* 
	 * 
	 */
	@Override
	public Object encodeParameterObject(StepMeta stepMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();

		SPClosureGenerator spClosureGenerator = new SPClosureGenerator();
		ClosureGeneratorMeta closuregeneratormeta = (ClosureGeneratorMeta) stepMetaInterface;

		spClosureGenerator.setParentIdFieldName(closuregeneratormeta.getParentIdFieldName());
		spClosureGenerator.setChildIdFieldName(closuregeneratormeta.getChildIdFieldName());
		spClosureGenerator.setDistanceFieldName(closuregeneratormeta.getDistanceFieldName());
		spClosureGenerator.setRootIdZero(closuregeneratormeta.isRootIdZero());
		return spClosureGenerator;
	}

	/* 
	 * 
	 */
	@Override
	public void decodeParameterObject(StepMeta stepMeta, Object po, List<DatabaseMeta> databases,TransMeta transMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPClosureGenerator spClosureGenerator = (SPClosureGenerator) po;
		ClosureGeneratorMeta closuregeneratormeta = (ClosureGeneratorMeta) stepMetaInterface;

		closuregeneratormeta.setParentIdFieldName(spClosureGenerator.getParentIdFieldName());
		closuregeneratormeta.setChildIdFieldName(spClosureGenerator.getChildIdFieldName());
		closuregeneratormeta.setDistanceFieldName(spClosureGenerator.getDistanceFieldName());
		closuregeneratormeta.setRootIdZero(spClosureGenerator.isRootIdZero());

	}

	/*
	 * 覆盖方法：getStepDataAndRelationship
	 */
	@Override
	public void getStepDataAndRelationship(TransMeta transMeta, StepMeta stepMeta, StepDataRelationship sdr) throws Exception {
		
		String from= "转换:"+transMeta.getName()+",步骤:"+stepMeta.getName();
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		ClosureGeneratorMeta closuregeneratormeta = (ClosureGeneratorMeta) stepMetaInterface;
		//输出
		distanceFieldName = closuregeneratormeta.getDistanceFieldName();
		//输入
		parentIdFieldName=closuregeneratormeta.getParentIdFieldName();
		childIdFieldName = closuregeneratormeta.getChildIdFieldName();
		sdr.addRelationship( RelationshipUtil.buildFieldRelationship(from, parentIdFieldName, distanceFieldName) );
		sdr.addRelationship( RelationshipUtil.buildFieldRelationship(from, childIdFieldName, distanceFieldName) );
		
	}

	@Override
	public int stepType() {
		return 0;
	}

}
