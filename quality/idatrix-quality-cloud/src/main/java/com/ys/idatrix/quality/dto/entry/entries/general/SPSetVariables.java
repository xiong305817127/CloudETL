/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.quality.dto.entry.entries.general;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.pentaho.di.core.util.Utils;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.job.entries.setvariables.JobEntrySetVariables;
import org.pentaho.di.job.entry.JobEntryCopy;
import org.pentaho.di.job.entry.JobEntryInterface;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.ys.idatrix.quality.dto.entry.entries.EntryParameter;
import com.ys.idatrix.quality.dto.entry.parts.SetVariablesvariableNameDto;
import com.ys.idatrix.quality.ext.utils.FilePathUtil;
import com.ys.idatrix.quality.ext.utils.FilePathUtil.FileType;
import com.ys.idatrix.quality.toolkit.analyzer.trans.step.StepDataRelationship;
import com.ys.idatrix.quality.toolkit.analyzer.trans.step.StepDataRelationshipParser;

import net.sf.json.JSONObject;

/**
 * Entry - SetVariables. 转换
 * org.pentaho.di.job.entries.setvariables.JobEntrySetVariables
 * 
 * @author XH
 * @since 2017-06-29
 */
@Component("SPset_variables")
@Scope("prototype")
public class SPSetVariables implements EntryParameter, StepDataRelationshipParser {

	boolean replaceVars;
	String filename;
	int fileVariableType;

	List<SetVariablesvariableNameDto> variableName;

	/**
	 * @return replaceVars
	 */
	public boolean isReplaceVars() {
		return replaceVars;
	}

	/**
	 * @param 设置
	 *            replaceVars
	 */
	public void setReplaceVars(boolean replaceVars) {
		this.replaceVars = replaceVars;
	}

	/**
	 * @return filename
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * @param 设置
	 *            filename
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}

	/**
	 * @return fileVariableType
	 */
	public int getFileVariableType() {
		return fileVariableType;
	}

	/**
	 * @param 设置
	 *            fileVariableType
	 */
	public void setFileVariableType(int fileVariableType) {
		this.fileVariableType = fileVariableType;
	}

	/**
	 * @return variableName
	 */
	public List<SetVariablesvariableNameDto> getVariableName() {
		return variableName;
	}

	/**
	 * @param 设置
	 *            variableName
	 */
	public void setVariableName(List<SetVariablesvariableNameDto> variableName) {
		this.variableName = variableName;
	}

	/* 
	 * 
	 */
	@Override
	public Object getParameterObject(Object json) {

		JSONObject jsonObj = JSONObject.fromObject(json);
		Map<String, Class<?>> classMap = new HashMap<>();
		classMap.put("variableName", SetVariablesvariableNameDto.class);
		return (SPSetVariables) JSONObject.toBean(jsonObj, SPSetVariables.class, classMap);
	}

	/* 
	 * 
	 */
	@Override
	public Object encodeParameterObject(JobEntryCopy jobEntryCopy) throws Exception {
		JobEntryInterface entryMetaInterface = jobEntryCopy.getEntry() ;
		
		SPSetVariables spSetVariables = new SPSetVariables();
		JobEntrySetVariables jobentrysetvariables = (JobEntrySetVariables) entryMetaInterface;

		spSetVariables.setFilename( FilePathUtil.getRelativeFileName(null,jobentrysetvariables.getFilename(),FileType.input));
		spSetVariables.setFileVariableType(jobentrysetvariables.getFileVariableType());
		spSetVariables.setReplaceVars(jobentrysetvariables.isReplaceVars());
		if (jobentrysetvariables.variableName != null && jobentrysetvariables.variableName.length > 0) {
			List<SetVariablesvariableNameDto> variableList = Lists.newArrayList();
			String[] variableName = jobentrysetvariables.variableName;
			String[] variableValue = jobentrysetvariables.variableValue;
			int[] variableType = jobentrysetvariables.variableType;
			for (int i = 0; i < variableName.length; i++) {
				SetVariablesvariableNameDto VariablesvariableNameDto = new SetVariablesvariableNameDto();
				VariablesvariableNameDto.setVariableName(variableName[i]);
				VariablesvariableNameDto.setVariableValue(variableValue[i]);
				VariablesvariableNameDto.setVariableType(variableType[i]);
				variableList.add(VariablesvariableNameDto);
			}
			spSetVariables.setVariableName(variableList);
		}

		return spSetVariables;
	}

	/* 
	 * 
	 */
	@Override
	public void decodeParameterObject(JobEntryCopy jobEntryCopy , Object po, JobMeta jobMeta) throws Exception {
		JobEntryInterface entryMetaInterface = jobEntryCopy.getEntry() ;
		
		SPSetVariables spSetVariables = (SPSetVariables) po;
		JobEntrySetVariables jobentrysetvariables = (JobEntrySetVariables) entryMetaInterface;

		jobentrysetvariables.setFileVariableType(spSetVariables.getFileVariableType());
		jobentrysetvariables.setReplaceVars(spSetVariables.isReplaceVars());
		if (spSetVariables.getVariableName() != null) {
			String[] variableNames = new String[spSetVariables.getVariableName().size()];
			String[] variableValues = new String[spSetVariables.getVariableName().size()];
			int[] variableTypes = new int[spSetVariables.getVariableName().size()];
			for (int i = 0; i < spSetVariables.getVariableName().size(); i++) {
				SetVariablesvariableNameDto setvariablesvariablenamedto = spSetVariables.getVariableName().get(i);
				variableNames[i] = setvariablesvariablenamedto.getVariableName();
				variableValues[i] = setvariablesvariablenamedto.getVariableValue();
				variableTypes[i] = setvariablesvariablenamedto.getVariableType();
			}
			jobentrysetvariables.setVariableName(variableNames);
			jobentrysetvariables.variableValue = variableValues;
			jobentrysetvariables.setVariableType(variableTypes);
		}
		jobentrysetvariables.setFilename(spSetVariables.getFilename());
		if( !Utils.isEmpty(spSetVariables.getFilename())){
			jobentrysetvariables.setFilename( FilePathUtil.getRealFileName(null,spSetVariables.getFilename(),FileType.input) );
		}

	}
	
	/*
	 * 覆盖方法：getStepDataAndRelationship
	 */
	@Override
	public void getStepDataAndRelationship(TransMeta transMeta, StepMeta stepMeta, StepDataRelationship sdr) {
//		ToolkitUtil.checkStepDataRelationshipInit(sdr);
//
//		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
//		JobEntrySetVariables jobentrysetvariables = (JobEntrySetVariables) stepMetaInterface;
//
//		String[] fArray = jobentrysetvariables.variableName;
//		String[] rArray = jobentrysetvariables.getVariableValue();
//		if (fArray != null) {
//			for (int i = 0; i < fArray.length && i < rArray.length; i++) {
//				String dummyName = ToolkitUtil.outFieldUnique(sdr, rArray[i]);
//				DummyProperty dummyProperty = new DummyProperty();
//
//				dummyProperty.setFlag(PluginConstant.DUMMY_FLAG_VARIABLE);
//				// Generator - 变量生成表达式定义
//				dummyProperty.setGenerator("");
//				dummyProperty.setReason("设置变量");
//				dummyProperty.setReference(fArray[i]);
//
//				DataNode parentDataNode = null;
//
//				ToolkitUtil.DummyNodeParse(sdr.getOutputDataNodes(), dummyName, dummyProperty, parentDataNode);
//			}
//		}
	}

}
