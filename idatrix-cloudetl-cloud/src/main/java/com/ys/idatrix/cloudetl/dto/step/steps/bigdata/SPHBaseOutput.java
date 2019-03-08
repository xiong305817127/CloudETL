/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.dto.step.steps.bigdata;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.util.OsgiBundleUtils;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ys.idatrix.cloudetl.dto.step.parts.MappedColumnDto;
import com.ys.idatrix.cloudetl.dto.step.parts.MappingDto;
import com.ys.idatrix.cloudetl.dto.step.steps.StepParameter;
import com.ys.idatrix.cloudetl.ext.CloudSession;
import com.ys.idatrix.cloudetl.service.server.CloudServerService;
import com.ys.idatrix.cloudetl.service.trans.stepdetail.HbaseDetailService;
import com.ys.idatrix.cloudetl.toolkit.analyzer.trans.step.StepDataRelationship;
import com.ys.idatrix.cloudetl.toolkit.analyzer.trans.step.StepDataRelationshipParser;

import net.sf.json.JSONObject;

/**
 * Step - HBaseOutput. 等效
 * org.pentaho.big.data.kettle.plugins.hbase.output.HBaseOutputMeta
 *
 * @author XH
 * @since 2017-08-07
 *
 */
@Component("SPHBaseOutput")
@Scope("prototype")
public class SPHBaseOutput implements StepParameter, StepDataRelationshipParser {

	@Autowired
	CloudServerService cloudServerService;
	@Autowired
	HbaseDetailService hbaseDetailService ;
	
	private  String namedClusterName;
	private  String coreConfigURL =  "${"+HbaseDetailService.haddopPluginRootPathKey+"}/hbase-site.xml";;
	private  String defaultConfigURL;
	private  String targetTableName;
	private  String targetMappingName;
	private boolean saveMappingToMeta = true;
	private  String writeBufferSize;
	private  boolean disableWriteToWAL;

	private MappingDto mapping;

	/**
	 * @return namedClusterName
	 */
	public String getNamedClusterName() {
		return namedClusterName;
	}

	/**
	 * @param  设置 namedClusterName
	 */
	public void setNamedClusterName(String namedClusterName) {
		this.namedClusterName = namedClusterName;
	}

	/**
	 * @return coreConfigURL
	 */
	public String getCoreConfigURL() {
		return coreConfigURL;
	}

	/**
	 * @param  设置 coreConfigURL
	 */
	public void setCoreConfigURL(String coreConfigURL) {
		this.coreConfigURL = coreConfigURL;
	}

	/**
	 * @return defaultConfigURL
	 */
	public String getDefaultConfigURL() {
		return defaultConfigURL;
	}

	/**
	 * @param  设置 defaultConfigURL
	 */
	public void setDefaultConfigURL(String defaultConfigURL) {
		this.defaultConfigURL = defaultConfigURL;
	}

	/**
	 * @return targetTableName
	 */
	public String getTargetTableName() {
		return targetTableName;
	}

	/**
	 * @param  设置 targetTableName
	 */
	public void setTargetTableName(String targetTableName) {
		this.targetTableName = targetTableName;
	}

	/**
	 * @return targetMappingName
	 */
	public String getTargetMappingName() {
		return targetMappingName;
	}

	/**
	 * @param  设置 targetMappingName
	 */
	public void setTargetMappingName(String targetMappingName) {
		this.targetMappingName = targetMappingName;
	}

	/**
	 * @return writeBufferSize
	 */
	public String getWriteBufferSize() {
		return writeBufferSize;
	}

	/**
	 * @param  设置 writeBufferSize
	 */
	public void setWriteBufferSize(String writeBufferSize) {
		this.writeBufferSize = writeBufferSize;
	}

	/**
	 * @return disableWriteToWAL
	 */
	public boolean isDisableWriteToWAL() {
		return disableWriteToWAL;
	}

	/**
	 * @param  设置 disableWriteToWAL
	 */
	public void setDisableWriteToWAL(boolean disableWriteToWAL) {
		this.disableWriteToWAL = disableWriteToWAL;
	}

	public boolean isSaveMappingToMeta() {
		return saveMappingToMeta;
	}

	public void setSaveMappingToMeta(boolean saveMappingToMeta) {
		this.saveMappingToMeta = saveMappingToMeta;
	}

	public MappingDto getMapping() {
		return mapping;
	}

	public void setMapping(MappingDto mapping) {
		this.mapping = mapping;
	}

	/*
	 * 覆盖方法：getParameterObject
	 */
	@Override
	public Object getParameterObject(Object json) {
		JSONObject jsonObj = JSONObject.fromObject(json);

		Map<String, Class<?>> classMap = new HashMap<>();
		classMap.put("mappedColumns", MappedColumnDto.class);

		return (SPHBaseOutput) JSONObject.toBean(jsonObj, SPHBaseOutput.class, classMap);
	}
	
	/*
	 * 覆盖方法：encodeParameterObject
	 */
	@Override
	public Object encodeParameterObject(StepMeta stepMeta) {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPHBaseOutput spHBaseOutput= new SPHBaseOutput();
		//HBaseOutputMeta hbaseoutputmeta= (HBaseOutputMeta )stepMetaInterface;

		Object namedCluster = OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getNamedCluster");
		spHBaseOutput.setNamedClusterName( namedCluster == null ? "" : (String) OsgiBundleUtils.invokeOsgiMethod(namedCluster, "getName") ) ;//hbaseoutputmeta.getNamedCluster().getName()
		spHBaseOutput.setDefaultConfigURL((String)OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getDefaultConfigURL"));//hbaseoutputmeta.getDefaultConfigURL());
		spHBaseOutput.setTargetTableName((String)OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getTargetTableName"));//hbaseoutputmeta.getTargetTableName());
		spHBaseOutput.setTargetMappingName((String)OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getTargetMappingName"));//hbaseoutputmeta.getTargetMappingName());
		spHBaseOutput.setDisableWriteToWAL((boolean)OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getDisableWriteToWAL"));//hbaseoutputmeta.getDisableWriteToWAL());
		spHBaseOutput.setWriteBufferSize((String)OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getWriteBufferSize"));//hbaseoutputmeta.getWriteBufferSize());
		spHBaseOutput.setCoreConfigURL((String)OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getCoreConfigURL"));//hbaseoutputmeta.getCoreConfigURL());

		Object mapping = OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getMapping") ;//hbaseoutputmeta.getMapping();
		if (mapping != null) {
			spHBaseOutput.setSaveMappingToMeta(true);
			MappingDto mappingDto = new MappingDto();
			mappingDto.transFromHbaseMapping(mapping);
			spHBaseOutput.setMapping(mappingDto);
		}else {
			spHBaseOutput.setSaveMappingToMeta(false);
		}

		return spHBaseOutput;
	}

	/*
	 * 覆盖方法：decodeParameterObject
	 */
	@Override
	public void decodeParameterObject(StepMeta stepMeta, Object po, List<DatabaseMeta> databases,TransMeta transMeta) throws Exception {
		//设置hadddop插件默认路径
		transMeta.setVariable(HbaseDetailService.haddopPluginRootPathKey, hbaseDetailService.getHaddopPluginRootPath());
				
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPHBaseOutput spHBaseOutput= (SPHBaseOutput)po;
		//HBaseOutputMeta  hbaseoutputmeta= (HBaseOutputMeta )stepMetaInterface;

		OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "setTargetTableName", spHBaseOutput.getTargetTableName());//hbaseoutputmeta.setTargetTableName(spHBaseOutput.getTargetTableName());
		OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "setTargetMappingName", spHBaseOutput.getTargetMappingName());//hbaseoutputmeta.setTargetMappingName(spHBaseOutput.getTargetMappingName());
		OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "setDisableWriteToWAL", spHBaseOutput.isDisableWriteToWAL());//hbaseoutputmeta.setDisableWriteToWAL(spHBaseOutput.isDisableWriteToWAL());
		OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "setWriteBufferSize", spHBaseOutput.getWriteBufferSize());//hbaseoutputmeta.setWriteBufferSize(spHBaseOutput.getWriteBufferSize());
		OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "setCoreConfigURL", spHBaseOutput.getCoreConfigURL());//hbaseoutputmeta.setCoreConfigURL(spHBaseOutput.getCoreConfigURL());
		OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "setDefaulConfigURL", spHBaseOutput.getDefaultConfigURL());//hbaseoutputmeta.setDefaulConfigURL(spHBaseOutput.getDefaultConfigURL() ) ;
		if( ! StringUtils.isEmpty(spHBaseOutput.getNamedClusterName())){
			//hbaseoutputmeta.setNamedCluster( hbaseoutputmeta.getNamedClusterService().getNamedClusterByName(spHBaseOutput.getNamedClusterName(), CloudSession.getMetaStore()) ) ;
			Object namedClusterService = OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getNamedClusterService");
			Object namedCluster = OsgiBundleUtils.invokeOsgiMethod(namedClusterService, "getNamedClusterByName", spHBaseOutput.getNamedClusterName(),  CloudSession.getMetaStore());
			OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "setNamedCluster", namedCluster);
		}

		MappingDto mappingDto = null;
		if (spHBaseOutput.getMapping() != null) {
			mappingDto = spHBaseOutput.getMapping() ;
		}else if( spHBaseOutput.isSaveMappingToMeta() &&  !Utils.isEmpty( spHBaseOutput.getNamedClusterName()) && !Utils.isEmpty( spHBaseOutput.getTargetTableName()) && !Utils.isEmpty( spHBaseOutput.getTargetMappingName())) {
			mappingDto = hbaseDetailService.getHbaseMappingInfo(spHBaseOutput.getNamedClusterName(),spHBaseOutput.getCoreConfigURL(), spHBaseOutput.getDefaultConfigURL(), spHBaseOutput.getTargetTableName(), spHBaseOutput.getTargetMappingName());
		}
		if( mappingDto != null ) {
			Object hbaseService = OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getService");
			Object tempMapping = mappingDto.transToHbaseMapping(hbaseService);
			OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "setMapping", tempMapping);//hbaseoutputmeta.setMapping(tempMapping);
		}
	}

	/*
	 * 覆盖方法：getStepDataAndRelationship
	 */
	@Override
	public void getStepDataAndRelationship(TransMeta transMeta, StepMeta stepMeta, StepDataRelationship sdr) {
		// TODO 自动生成的方法存根
		
	}

}
