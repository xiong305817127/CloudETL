/**
 * 云化数据集成系统 iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.dto.step.steps.transfor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.util.OsgiBundleUtils;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.ys.idatrix.cloudetl.dto.step.parts.DesensitizationFieldDto;
import com.ys.idatrix.cloudetl.dto.step.steps.StepParameter;
import com.ys.idatrix.cloudetl.toolkit.analyzer.trans.step.StepDataRelationship;
import com.ys.idatrix.cloudetl.toolkit.analyzer.trans.step.StepDataRelationshipParser;
import com.ys.idatrix.cloudetl.toolkit.utils.RelationshipUtil;

import net.sf.json.JSONObject;

/**
 * Step - Desensitization(数据脱敏)
 * com.ys.idatrix.cloudetl.steps.DesensitizationMeta
 * 
 * @author XH
 * @since 2019年1月8日
 *
 */
@Component("SPDesensitization")
@Scope("prototype")
public class SPDesensitization implements StepParameter, StepDataRelationshipParser {

	List<DesensitizationFieldDto> desensitizations;

	public List<DesensitizationFieldDto> getDesensitizations() {
		return desensitizations;
	}

	public void setDesensitizations(List<DesensitizationFieldDto> desensitizations) {
		this.desensitizations = desensitizations;
	}

	/* 
	 * 
	 */
	@Override
	public Object getParameterObject(Object json) {
		JSONObject jsonObj = JSONObject.fromObject(json);
		Map<String, Class<?>> classMap = new HashMap<>();
		classMap.put("desensitizations", DesensitizationFieldDto.class);
		return (SPDesensitization) JSONObject.toBean(jsonObj, SPDesensitization.class, classMap);
	}

	/* 
	 * 
	 */
	@Override
	public Object encodeParameterObject(StepMeta stepMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPDesensitization spDesensitization = new SPDesensitization();

		List<DesensitizationFieldDto> fieldsList = Lists.newArrayList();
		String[] fieldInStreams = (String[]) OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getFieldInStream");
		if(fieldInStreams != null && fieldInStreams.length >0) {
			String[] fieldOutStreams = (String[]) OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getFieldOutStream");
			String[] ruleTypes = (String[]) OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getRuleTypes");
			int[] startPositons = (int[]) OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getStartPositons");
			int[] lengths = (int[]) OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getLengths");
			String[] replacements = (String[]) OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getReplacements");
			Boolean[] ignoreSpaces = (Boolean[]) OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getIgnoreSpaces");
			
			for ( int i =0 ; i < fieldInStreams.length ; i++ ) {
				DesensitizationFieldDto dfd = new DesensitizationFieldDto();
				dfd.setFieldInStream(fieldInStreams[i]);
				dfd.setFieldOutStream(fieldOutStreams[i]);
				dfd.setRuleType(ruleTypes[i]);
				dfd.setStartPositon(startPositons[i]);
				dfd.setLength(lengths[i]);
				dfd.setReplacement(replacements[i]);
				dfd.setIgnoreSpace(ignoreSpaces[i]);
				fieldsList.add(dfd);
			}
		}
		spDesensitization.setDesensitizations(fieldsList);
		return spDesensitization;
	}

	/* 
	 * 
	 */
	@Override
	public void decodeParameterObject(StepMeta stepMeta, Object po, List<DatabaseMeta> databases,TransMeta transMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPDesensitization spDesensitization = (SPDesensitization) po;

		List<DesensitizationFieldDto> fields = spDesensitization.getDesensitizations() ;
		if( fields != null && fields.size() > 0 ) {
			
			OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "allocate", fields.size() ) ;
			
			String[] fieldInStreams = (String[]) OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getFieldInStream") ;
			String[] fieldOutStreams = (String[]) OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getFieldOutStream") ;
			String[] ruleTypes = (String[]) OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getRuleTypes") ;
			int[] startPositons = (int[]) OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getStartPositons") ;
			int[] lengths = (int[]) OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getLengths") ;
			String[] replacements = (String[]) OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getReplacements" );
			Boolean[] ignoreSpaces =(Boolean[]) OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getIgnoreSpaces" ) ;
			for ( int i =0 ; i < fields.size() ; i++ ) {
				DesensitizationFieldDto dfd = fields.get(i);

				fieldInStreams[i] = dfd.getFieldInStream();
				fieldOutStreams[i] = dfd.getFieldOutStream() ;
				ruleTypes[i] = dfd.getRuleType() ;
				startPositons[i] = dfd.getStartPositon() ;
				lengths[i] = dfd.getLength() ;
				replacements[i] = dfd.getReplacement() ;
				ignoreSpaces[i] = dfd.getIgnoreSpace() ;
			}
			
			//OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "allocate", fields.size() ) ;
			//OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "setFieldInStream",new Object[] { fieldInStreams }) ;
			//OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "setFieldOutStream",new Object[] { fieldOutStreams }) ;
			//OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "setRuleTypes",new Object[] { ruleTypes }) ;
			//OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "setStartPositons", new Object[] {startPositons}) ;
			//OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "setLengths", new Object[] {lengths}) ;
			//OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "setReplacements", new Object[] {replacements} ) ;
			//OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "setIgnoreSpaces", new Object[] { ignoreSpaces}) ;
			
			OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "ruleTransformation" ) ;
		}
	

	}

	/*
	 * 覆盖方法：getStepDataAndRelationship
	 */
	@Override
	public void getStepDataAndRelationship(TransMeta transMeta, StepMeta stepMeta, StepDataRelationship sdr) throws Exception {
		
		String from= "转换:"+transMeta.getName()+",步骤:"+stepMeta.getName();
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		//输入
		String[] in = (String[]) OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getFieldInStream");
		String[] outs = (String[]) OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getFieldOutStream");
		
		for(int i=0;i< outs.length ;i++) {
			if(!Utils.isEmpty(outs[i])) {
				sdr.addRelationship( RelationshipUtil.buildFieldRelationship(from, in[i], outs[i]) );
			}
		}
	}

}
