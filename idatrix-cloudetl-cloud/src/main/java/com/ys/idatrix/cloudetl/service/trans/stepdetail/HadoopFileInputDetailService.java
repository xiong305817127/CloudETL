/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.service.trans.stepdetail;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileType;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.core.vfs.KettleVFS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ys.idatrix.cloudetl.dto.hadoop.HadoopDetailsDto;
import com.ys.idatrix.cloudetl.ext.CloudSession;
import com.ys.idatrix.cloudetl.service.hadoop.CloudHadoopService;

/**
 * AccessInput related Detail Service
 * 
 * @author XH
 * @since 2017年6月9日
 *
 */
@Service
public class HadoopFileInputDetailService implements StepDetailService {

	@Autowired
	private CloudHadoopService cloudHadoopService;
	@Autowired
	private TextInputDetailService textInputDetailService;

	@Override
	public String getStepDetailType() {
		return "HadoopFileInputPlugin";
	}

	/**
	 * flag : getFile , getFields
	 * @throws Exception 
	 */
	@Override
	public List<Object> dealStepDetailByflag(String flag, Map<String, Object> param) throws Exception {
		if (StringUtils.isEmpty(flag)) {
			return null;
		}

		switch (flag) {
		case "getFile":
			return getFile(param);
		case "getFields":
			return getFields(param);
		default:
			return null;

		}

	}

	/**
	 * @param fileName
	 *            tableName
	 * @return Access Fields list
	 * @throws Exception 
	 */
	private List<Object> getFile(Map<String, Object> params) throws Exception {


		checkDetailParam(params, "sourceConfigurationName", "fileName");
		
		String owner =  params.get("owner") == null || Utils.isEmpty( (String)params.get("owner"))  ? CloudSession.getResourceUser() : (String)params.get("owner");
		String fileName = params.get("fileName").toString();
		String sourceConfigurationName = params.get("sourceConfigurationName").toString();
		
		FileObject rootFile = KettleVFS.getFileObject(getConnectPath(owner,sourceConfigurationName, fileName));
		if( rootFile ==null ){
			throw new KettleException("filename "+fileName+" not exist!");
		}
		List<Object> result = Lists.newArrayList();
		FileObject[] children;
		if(rootFile.getType().equals(FileType.FOLDER)){
			 children = rootFile.getChildren();
			 for( FileObject child :children){
				 HashMap<String, String> map = Maps.newHashMap();
				 map.put("Name", child.getName().getBaseName() );
				 map.put("Uri", child.getName().getPath() );
				 map.put("Type", child.getType().getName() );
				 result.add(map);
				 child.close();
			 }
		}else{
			 HashMap<String, String> map = Maps.newHashMap();
			 map.put("Name", rootFile.getName().getBaseName() );
			 map.put("Uri" ,  rootFile.getName().getPath() );
			 map.put("Type", rootFile.getType().getName() );
			 result.add(map);
		}

		rootFile.close();
		return result;
	}

	/**
	 * @param fileName
	 * @return Access Tables List
	 * @throws Exception 
	 */
	private List<Object> getFields(Map<String, Object> params) throws Exception {

		checkDetailParam(params, "fileName", "content");
		
		String owner =  params.get("owner") == null || Utils.isEmpty( (String)params.get("owner"))  ? CloudSession.getResourceUser() : (String)params.get("owner");
		String fileName = params.get("fileName").toString();
		String[] values = fileName.split("::");
		if(values ==null || values.length != 3){
			throw new KettleException("hadoop path is invalid!");
		}
		fileName= "hdfs::"+getConnectPath(owner ,values[1], values[2]);
		params.put("fileName", fileName);
		
		return textInputDetailService.dealStepDetailByflag("getFields", params);
	}

	public String getConnectPath(String owner ,String sourceConfigurationName, String rootpath) throws Exception {

		HadoopDetailsDto hadoopNamedCluster = cloudHadoopService.editHadoop(owner ,sourceConfigurationName);
		if (hadoopNamedCluster == null || Utils.isEmpty(hadoopNamedCluster.getName())) {
			throw new KettleException("NamedCluster " + sourceConfigurationName + " not exist!");
		}
		String schemeName = "wasb".equals( hadoopNamedCluster.getStorage() ) ? "wasb" : "hdfs";
		StringBuffer urlString = new StringBuffer(!Utils.isEmpty(schemeName) ? schemeName : "hdfs").append("://");
		if (!Utils.isEmpty(hadoopNamedCluster.getUsername())) {
			urlString.append(hadoopNamedCluster.getUsername()).append(":").append(hadoopNamedCluster.getPassword()).append("@");
		}

		urlString.append(hadoopNamedCluster.getHostname());
		if (!Utils.isEmpty(hadoopNamedCluster.getPort())) {
			urlString.append(":").append(hadoopNamedCluster.getPort());
		}
		if(!Utils.isEmpty(rootpath)){
			if(!rootpath.startsWith(urlString.toString())) {
				if(!rootpath.startsWith("/")){
					urlString.append("/");
				}
				urlString.append(rootpath);
			}else {
				return rootpath;
			}
		}else{
			urlString.append("/");
		}
		return urlString.toString();

	}

}
