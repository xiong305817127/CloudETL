/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.quality.dto.entry.entries.bigdata;

import java.util.HashMap;
import java.util.Map;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ys.idatrix.quality.dto.entry.entries.filemanagement.SPCopyFiles;
import com.ys.idatrix.quality.dto.entry.parts.CopyFilessourceFilefolderDto;
import com.ys.idatrix.quality.dto.hadoop.HadoopDetailsDto;
import com.ys.idatrix.quality.ext.utils.StringEscapeHelper;
import com.ys.idatrix.quality.service.hadoop.CloudHadoopService;
import com.ys.idatrix.quality.service.trans.stepdetail.HadoopFileInputDetailService;

import net.sf.json.JSONObject;

/**
 *  Entry - HadoopCopyFilesPlugin. 转换b org.pentaho.big.data.kettle.plugins.hdfs.job.JobEntryHadoopCopyFiles
 * @author XH
 * @since 2017年6月29日
 *
 */
@Component("SPhadoopcopyfilesplugin")
@Scope("prototype")
public class SPHadoopCopyFilesPlugin extends SPCopyFiles{
	
	@Autowired
	HadoopFileInputDetailService hadoopFileInputDetailService;
	@Autowired
	CloudHadoopService cloudHadoopService;
	
	@Override
	public Object getParameterObject(Object json) {
		JSONObject jsonObj = JSONObject.fromObject(json);
		Map<String, Class<?>> classMap = new HashMap<>();
		classMap.put("filefolder", CopyFilessourceFilefolderDto.class);

		return (SPHadoopCopyFilesPlugin) JSONObject.toBean(jsonObj, SPHadoopCopyFilesPlugin.class, classMap);
	}

	/* 
	 * 
	 */
	@Override
	public String SaveDataMap(String configName, String path, HashMap<String, String> mappings, int i) throws Exception  {
		
		String hadoopFileUrl;
		try {
			hadoopFileUrl = hadoopFileInputDetailService.getConnectPath(null,configName, path);
		} catch (KettleException e) {
			return super.SaveDataMap(configName, path, mappings, i);
		}
		mappings.put(hadoopFileUrl, configName);
		return hadoopFileUrl;
	}

	/* 
	 * 
	 */
	@Override
	public void loadDataMap(CopyFilessourceFilefolderDto parametersDto, String configName, String path, boolean isSource) throws Exception {
		
		HadoopDetailsDto hadoopNamedCluster = cloudHadoopService.editHadoop(null,configName);
		if (hadoopNamedCluster == null || Utils.isEmpty(hadoopNamedCluster.getName())) {
			super.loadDataMap(parametersDto, configName, path, isSource);
			return ;
		}
		
		path=StringEscapeHelper.getUrlPath(path);
		if(isSource){
			parametersDto.setSourceConfigurationName(configName);
			parametersDto.setSourceFilefolder(path);
		}else{
			parametersDto.setDestinationFilefolder(path);
			parametersDto.setDestinationConfigurationName(configName);
		}
	}


	
	
	
	
}
