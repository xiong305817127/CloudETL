/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.service.trans.stepdetail;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.vfs2.FileObject;
import org.pentaho.di.core.plugins.KettleLifecyclePluginType;
import org.pentaho.di.core.plugins.PluginInterface;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.core.plugins.StepPluginType;
import org.pentaho.di.core.util.OsgiBundleUtils;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.core.variables.Variables;
import org.pentaho.di.core.vfs.KettleVFS;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.springframework.stereotype.Service;

import com.ys.idatrix.cloudetl.dto.common.ReturnCodeDto;
import com.ys.idatrix.cloudetl.dto.step.parts.MappedColumnDto;
import com.ys.idatrix.cloudetl.dto.step.parts.MappingDto;
import com.ys.idatrix.cloudetl.ext.CloudSession;

import net.sf.json.JSONObject;

/**
 * org.pentaho.big.data.kettle.plugins.hbase.mapping.MappingEditor
 * 
 * @author XH
 * @since 2017年6月9日
 *
 */
@Service
public class HbaseDetailService implements StepDetailService {

	
	public final static String haddopPluginRootPathKey = "haddopPluginConfigPath";
	
	@Override
	public String getStepDetailType() {
		return "HBaseInput,HBaseOutput";
	}

	/**
	 * flag : getTable , getMapping ,createMapping
	 * 
	 * @throws Exception
	 */
	@Override
	public Object dealStepDetailByflag(String flag, Map<String, Object> param) throws Exception {
		if (StringUtils.isEmpty(flag)) {
			return null;
		}

		switch (flag) {
		case "getTables":
			return getHbaseTables(param);
		case "getMappings":
			return getHbaseMappings(param);
		case "getMappingInfo":
			return getHbaseMappingInfo(param);
		case "createMapping":
			return createHbaseMapping(param);
		case "deleteMapping":
			return deleteHbaseMapping(param);
			
		default:
			return null;

		}

	}

	@SuppressWarnings("unchecked")
	private List<String> getHbaseTables(Map<String, Object> params) throws Exception {

		checkDetailParam(params, "namedClusterName" );

		String ncName = params.get("namedClusterName").toString();
		String siteConfig =  (String)params.get("siteConfig") ;
		String defaultConfig = (String)params.get("defaultConfig");
		
		if( Utils.isEmpty(siteConfig) ) {
			siteConfig = getHaddopPluginRootPath()+"/hbase-site.xml" ;
		}else if( siteConfig.contains(haddopPluginRootPathKey)){
			siteConfig = siteConfig.replace("${"+haddopPluginRootPathKey+"}", getHaddopPluginRootPath()) ;
		}
		if( !Utils.isEmpty(defaultConfig) && defaultConfig.contains(haddopPluginRootPathKey)) {
			defaultConfig = defaultConfig.replace("${"+haddopPluginRootPathKey+"}", getHaddopPluginRootPath()) ;
		}

		PluginRegistry registry = PluginRegistry.getInstance();
		PluginInterface sp = registry.findPluginWithId(StepPluginType.class, "HBaseInput");
		StepMetaInterface hbaseInput = (StepMetaInterface) registry.loadClass(sp);
		//HBaseInputMeta hbaseInput = (HBaseInputMeta) registry.loadClass(sp);

		//NamedClusterService namedClusterService = hbaseInput.getNamedClusterService();
		Object namedClusterService = OsgiBundleUtils.invokeOsgiMethod(hbaseInput, "getNamedClusterService");
		//NamedCluster nc = namedClusterService.getNamedClusterByName(ncName, CloudSession.getMetaStore());
		Object nc = OsgiBundleUtils.invokeOsgiMethod(namedClusterService, "getNamedClusterByName",ncName, CloudSession.getMetaStore());
		//HBaseService hbaseService = hbaseInput.getNamedClusterServiceLocator().getService(nc, HBaseService.class);
		Object hbaseService =OsgiBundleUtils.invokeOsgiMethod( OsgiBundleUtils.invokeOsgiMethod(hbaseInput, "getNamedClusterServiceLocator"),"getService",nc, hbaseInput.getClass().getClassLoader().loadClass("org.pentaho.bigdata.api.hbase.HBaseService"));
		//MappingAdmin mappingAdmin = MappingUtils.getMappingAdmin(hbaseService, null, siteConfig, defaultConfig);
		ClassLoader classLoader = hbaseInput.getClass().getClassLoader();
		Object mappingAdmin = OsgiBundleUtils.invokeOsgiMethod(classLoader.loadClass("org.pentaho.big.data.kettle.plugins.hbase.mapping.MappingUtils"), "getMappingAdmin",
				new Object[] {hbaseService, new Variables(), siteConfig, defaultConfig},
				new Class[] {classLoader.loadClass("org.pentaho.bigdata.api.hbase.HBaseService"),VariableSpace.class,String.class,String.class});
		
		//List<String> tables = mappingAdmin.getConnection().listTableNames();
		List<String> tables =(List<String>) OsgiBundleUtils.invokeOsgiMethod( OsgiBundleUtils.invokeOsgiMethod(mappingAdmin, "getConnection"),"listTableNames");
		
		//mappingAdmin.close();
		OsgiBundleUtils.invokeOsgiMethod(mappingAdmin,"close");

		return tables;
	}

	@SuppressWarnings("unchecked")
	private List<String> getHbaseMappings(Map<String, Object> params) throws Exception {

		checkDetailParam(params, "namedClusterName",  "tableName");

		String ncName = params.get("namedClusterName").toString();
		String siteConfig =  (String)params.get("siteConfig") ;
		String defaultConfig = (String)params.get("defaultConfig");
		String tableName = params.get("tableName").toString();
		
		if( Utils.isEmpty(siteConfig) ) {
			siteConfig = getHaddopPluginRootPath()+"/hbase-site.xml" ;
		}else if( siteConfig.contains(haddopPluginRootPathKey)){
			siteConfig = siteConfig.replace("${"+haddopPluginRootPathKey+"}", getHaddopPluginRootPath()) ;
		}
		if( !Utils.isEmpty(defaultConfig) && defaultConfig.contains(haddopPluginRootPathKey)) {
			defaultConfig = defaultConfig.replace("${"+haddopPluginRootPathKey+"}", getHaddopPluginRootPath()) ;
		}

		PluginRegistry registry = PluginRegistry.getInstance();
		PluginInterface sp = registry.findPluginWithId(StepPluginType.class, "HBaseInput");
		StepMetaInterface hbaseInput = (StepMetaInterface) registry.loadClass(sp);
		//HBaseInputMeta hbaseInput = (HBaseInputMeta) registry.loadClass(sp);

		//NamedClusterService namedClusterService = hbaseInput.getNamedClusterService();
		Object namedClusterService = OsgiBundleUtils.invokeOsgiMethod(hbaseInput, "getNamedClusterService");
		//NamedCluster nc = namedClusterService.getNamedClusterByName(ncName, CloudSession.getMetaStore());
		Object nc = OsgiBundleUtils.invokeOsgiMethod(namedClusterService, "getNamedClusterByName",ncName, CloudSession.getMetaStore());
		//HBaseService hbaseService = hbaseInput.getNamedClusterServiceLocator().getService(nc, HBaseService.class);
		Object hbaseService =OsgiBundleUtils.invokeOsgiMethod( OsgiBundleUtils.invokeOsgiMethod(hbaseInput, "getNamedClusterServiceLocator"),"getService",nc, hbaseInput.getClass().getClassLoader().loadClass("org.pentaho.bigdata.api.hbase.HBaseService"));
		//MappingAdmin mappingAdmin = MappingUtils.getMappingAdmin(hbaseService, null, siteConfig, defaultConfig);
		ClassLoader classLoader = hbaseInput.getClass().getClassLoader();
		Object mappingAdmin = OsgiBundleUtils.invokeOsgiMethod(classLoader.loadClass("org.pentaho.big.data.kettle.plugins.hbase.mapping.MappingUtils"), "getMappingAdmin",
				new Object[] {hbaseService, new Variables(), siteConfig, defaultConfig},
				new Class[] {classLoader.loadClass("org.pentaho.bigdata.api.hbase.HBaseService"),VariableSpace.class,String.class,String.class});				
		
		//List<String> mappingNames = mappingAdmin.getMappingNames(tableName);
		List<String> mappingNames =(List<String>) OsgiBundleUtils.invokeOsgiMethod(mappingAdmin, "getMappingNames", tableName);

		//mappingAdmin.close();
		OsgiBundleUtils.invokeOsgiMethod(mappingAdmin,"close");

		return mappingNames;
	}

	private MappingDto getHbaseMappingInfo(Map<String, Object> params) throws Exception {

		checkDetailParam(params, "namedClusterName",  "tableName", "mappingName");

		String ncName = params.get("namedClusterName").toString();
		String siteConfig =  (String)params.get("siteConfig") ;
		String defaultConfig = (String)params.get("defaultConfig");
		String tableName = params.get("tableName").toString();
		String mappingName = params.get("mappingName").toString();
		
		if( Utils.isEmpty(siteConfig) ) {
			siteConfig = getHaddopPluginRootPath()+"/hbase-site.xml" ;
		}else if( siteConfig.contains(haddopPluginRootPathKey)){
			siteConfig = siteConfig.replace("${"+haddopPluginRootPathKey+"}", getHaddopPluginRootPath()) ;
		}
		if( !Utils.isEmpty(defaultConfig) && defaultConfig.contains(haddopPluginRootPathKey)) {
			defaultConfig = defaultConfig.replace("${"+haddopPluginRootPathKey+"}", getHaddopPluginRootPath()) ;
		}

		return getHbaseMappingInfo(ncName, siteConfig, defaultConfig, tableName, mappingName);
	}
	
	private ReturnCodeDto deleteHbaseMapping(Map<String, Object> params) throws Exception {
		
		checkDetailParam(params, "namedClusterName",  "tableName", "mappingName");

		String ncName = params.get("namedClusterName").toString();
		String siteConfig =  (String)params.get("siteConfig") ;
		String defaultConfig = (String)params.get("defaultConfig");
		String tableName = params.get("tableName").toString();
		String mappingName = params.get("mappingName").toString();
		
		if( Utils.isEmpty(siteConfig) ) {
			siteConfig = getHaddopPluginRootPath()+"/hbase-site.xml" ;
		}else if( siteConfig.contains(haddopPluginRootPathKey)){
			siteConfig = siteConfig.replace("${"+haddopPluginRootPathKey+"}", getHaddopPluginRootPath()) ;
		}
		if( !Utils.isEmpty(defaultConfig) && defaultConfig.contains(haddopPluginRootPathKey)) {
			defaultConfig = defaultConfig.replace("${"+haddopPluginRootPathKey+"}", getHaddopPluginRootPath()) ;
		}

		PluginRegistry registry = PluginRegistry.getInstance();
		PluginInterface sp = registry.findPluginWithId(StepPluginType.class, "HBaseInput");
		StepMetaInterface hbaseInput = (StepMetaInterface) registry.loadClass(sp);
		//HBaseInputMeta hbaseInput = (HBaseInputMeta) registry.loadClass(sp);

		//NamedClusterService namedClusterService = hbaseInput.getNamedClusterService();
		Object namedClusterService = OsgiBundleUtils.invokeOsgiMethod(hbaseInput, "getNamedClusterService");
		//NamedCluster nc = namedClusterService.getNamedClusterByName(ncName, CloudSession.getMetaStore());
		Object nc = OsgiBundleUtils.invokeOsgiMethod(namedClusterService, "getNamedClusterByName",ncName, CloudSession.getMetaStore());
		//HBaseService hbaseService = hbaseInput.getNamedClusterServiceLocator().getService(nc, HBaseService.class);
		Object hbaseService =OsgiBundleUtils.invokeOsgiMethod( OsgiBundleUtils.invokeOsgiMethod(hbaseInput, "getNamedClusterServiceLocator"),"getService",nc, hbaseInput.getClass().getClassLoader().loadClass("org.pentaho.bigdata.api.hbase.HBaseService"));
		//MappingAdmin mappingAdmin = MappingUtils.getMappingAdmin(hbaseService, null, siteConfig, defaultConfig);
		ClassLoader classLoader = hbaseInput.getClass().getClassLoader();
		Object mappingAdmin = OsgiBundleUtils.invokeOsgiMethod(classLoader.loadClass("org.pentaho.big.data.kettle.plugins.hbase.mapping.MappingUtils"), "getMappingAdmin",
				new Object[] {hbaseService, new Variables(), siteConfig, defaultConfig},
				new Class[] {classLoader.loadClass("org.pentaho.bigdata.api.hbase.HBaseService"),VariableSpace.class,String.class,String.class});
		
		boolean result = (boolean) OsgiBundleUtils.invokeOsgiMethod(mappingAdmin, "deleteMapping", tableName, mappingName);
		
		//mappingAdmin.close();
		OsgiBundleUtils.invokeOsgiMethod(mappingAdmin,"close");
				
		return new ReturnCodeDto(result?0:-1,"");
	}
	
	@SuppressWarnings("unchecked")
	private ReturnCodeDto createHbaseMapping(Map<String, Object> params) throws Exception {

		checkDetailParam(params, "namedClusterName", "mapping");

		String ncName = params.get("namedClusterName").toString();
		String siteConfig =  (String)params.get("siteConfig") ;
		String defaultConfig = (String)params.get("defaultConfig");
		
		if( Utils.isEmpty(siteConfig) ) {
			siteConfig = getHaddopPluginRootPath()+"/hbase-site.xml" ;
		}else if( siteConfig.contains(haddopPluginRootPathKey)){
			siteConfig = siteConfig.replace("${"+haddopPluginRootPathKey+"}", getHaddopPluginRootPath()) ;
		}
		if( !Utils.isEmpty(defaultConfig) && defaultConfig.contains(haddopPluginRootPathKey)) {
			defaultConfig = defaultConfig.replace("${"+haddopPluginRootPathKey+"}", getHaddopPluginRootPath()) ;
		}

		boolean allowTableCreate = true;
		if (params.containsKey("allowTableCreate")
				&& "false".equalsIgnoreCase((String) params.get("allowTableCreate"))) {
			allowTableCreate = false;
		}
		
		boolean overwrite = true;
		if (params.containsKey("overwrite")
				&& "false".equalsIgnoreCase((String) params.get("overwrite"))) {
			overwrite = false;
		}

		Object mappingMap = params.get("mapping");
		JSONObject jsonObj = JSONObject.fromObject(mappingMap);
		Map<String, Class<?>> classMap = new HashMap<>();
		classMap.put("mappedColumns", MappedColumnDto.class);
		MappingDto mappingDto = (MappingDto) JSONObject.toBean(jsonObj, MappingDto.class, classMap);

		PluginRegistry registry = PluginRegistry.getInstance();
		PluginInterface sp = registry.findPluginWithId(StepPluginType.class, "HBaseInput");
		StepMetaInterface hbaseInput = (StepMetaInterface) registry.loadClass(sp);
		//HBaseInputMeta hbaseInput = (HBaseInputMeta) registry.loadClass(sp);

		//NamedClusterService namedClusterService = hbaseInput.getNamedClusterService();
		Object namedClusterService = OsgiBundleUtils.invokeOsgiMethod(hbaseInput, "getNamedClusterService");
		//NamedCluster nc = namedClusterService.getNamedClusterByName(ncName, CloudSession.getMetaStore());
		Object nc = OsgiBundleUtils.invokeOsgiMethod(namedClusterService, "getNamedClusterByName",ncName, CloudSession.getMetaStore());
		//HBaseService hbaseService = hbaseInput.getNamedClusterServiceLocator().getService(nc, HBaseService.class);
		Object hbaseService =OsgiBundleUtils.invokeOsgiMethod( OsgiBundleUtils.invokeOsgiMethod(hbaseInput, "getNamedClusterServiceLocator"),"getService",nc, hbaseInput.getClass().getClassLoader().loadClass("org.pentaho.bigdata.api.hbase.HBaseService"));
		//MappingAdmin mappingAdmin = MappingUtils.getMappingAdmin(hbaseService, null, siteConfig, defaultConfig);
		ClassLoader classLoader = hbaseInput.getClass().getClassLoader();
		Object mappingAdmin = OsgiBundleUtils.invokeOsgiMethod(classLoader.loadClass("org.pentaho.big.data.kettle.plugins.hbase.mapping.MappingUtils"), "getMappingAdmin",
				new Object[] {hbaseService, new Variables(), siteConfig, defaultConfig},
				new Class[] {classLoader.loadClass("org.pentaho.bigdata.api.hbase.HBaseService"),VariableSpace.class,String.class,String.class});
		
		Object theMapping = mappingDto.transToHbaseMapping(hbaseService);
		if (theMapping == null || (int)OsgiBundleUtils.invokeOsgiMethod( OsgiBundleUtils.invokeOsgiMethod(theMapping,"getMappedColumns"),"size" ) == 0) { //theMapping.getMappedColumns().size()
			// some problem with the mapping (user will have been informed via dialog)
			return new ReturnCodeDto(-1, "mapping 为空(转换错误)或者mapping 中列信息为空");
		}
		//HBaseConnection hbAdmin = mappingAdmin.getConnection();
		Object hbAdmin = OsgiBundleUtils.invokeOsgiMethod(mappingAdmin, "getConnection" );
		//String tableName = theMapping.getTableName();
		String tableName = (String) OsgiBundleUtils.invokeOsgiMethod(theMapping, "getTableName" );
		//String mappingName = theMapping.getMappingName();
		String mappingName  = (String) OsgiBundleUtils.invokeOsgiMethod(theMapping, "getMappingName" );

		if (allowTableCreate) {
			// check for existence of the table. If table doesn't exist
			// prompt for creation

			if ( !(boolean)OsgiBundleUtils.invokeOsgiMethod( OsgiBundleUtils.invokeOsgiMethod(hbAdmin, "getTable", tableName),"exists")) { //(!hbAdmin.getTable(tableName).exists()) {
				// collect up all the column families so that we can create the table
				//Set<String> cols = theMapping.getMappedColumns().keySet();
				Set<String> cols = (Set<String>) OsgiBundleUtils.invokeOsgiMethod( OsgiBundleUtils.invokeOsgiMethod(theMapping, "getMappedColumns"),"keySet");
				Set<String> families = new TreeSet<String>();
				for (String col : cols) {
					//String family = theMapping.getMappedColumns().get(col).getColumnFamily();
					String family =(String)OsgiBundleUtils.invokeOsgiMethod( OsgiBundleUtils.invokeOsgiMethod( OsgiBundleUtils.invokeOsgiMethod(theMapping, "getMappedColumns"),"get",col),"getColumnFamily");
					families.add(family);
				}

				// do we have additional parameters supplied in the table name field
				// String compression = Compression.Algorithm.NONE.getName();
				Properties creationProps = new Properties();
//				if (compression != null) {
//					creationProps.setProperty("col.descriptor.compression", compression);
//				}
//				if (bloomFilter != null) {
//					creationProps.setProperty("col.descriptor.bloomFilter", bloomFilter);
//				}
				List<String> familyList = new ArrayList<String>();
				for (String fam : families) {
					familyList.add(fam);
				}
				// create the table
				//hbAdmin.getTable(tableName).create(familyList, creationProps);
				OsgiBundleUtils.invokeOsgiMethod( OsgiBundleUtils.invokeOsgiMethod(hbAdmin, "getTable", tableName),"create",familyList, creationProps) ;
			}
		}

		// now check to see if the mapping exists
		if ( (boolean)OsgiBundleUtils.invokeOsgiMethod(mappingAdmin, "mappingExists", tableName, mappingName) && !overwrite) { //(mappingAdmin.mappingExists(tableName, mappingName)&& !overwrite) {
			// prompt for overwrite
			return  new ReturnCodeDto(-1, "tableName["+tableName+"],mapping["+mappingName+"]映射已经存在!");
		}
		// finally add the mapping.
		//mappingAdmin.putMapping(theMapping, true);
		OsgiBundleUtils.invokeOsgiMethod(mappingAdmin, "putMapping", theMapping, true) ;

		//mappingAdmin.close();
		OsgiBundleUtils.invokeOsgiMethod(mappingAdmin,"close");
		
		return new ReturnCodeDto(0,"");
	}
	
	public MappingDto getHbaseMappingInfo(String ncName,String siteConfig,String defaultConfig,String tableName,String mappingName) throws Exception {
		
		if( Utils.isEmpty(siteConfig) ) {
			siteConfig = getHaddopPluginRootPath()+"/hbase-site.xml" ;
		}else if( siteConfig.contains(haddopPluginRootPathKey)){
			siteConfig = siteConfig.replace("${"+haddopPluginRootPathKey+"}", getHaddopPluginRootPath()) ;
		}
		if( !Utils.isEmpty(defaultConfig) && defaultConfig.contains(haddopPluginRootPathKey)) {
			defaultConfig = defaultConfig.replace("${"+haddopPluginRootPathKey+"}", getHaddopPluginRootPath()) ;
		}
		
		PluginRegistry registry = PluginRegistry.getInstance();
		PluginInterface sp = registry.findPluginWithId(StepPluginType.class, "HBaseInput");
		 StepMetaInterface hbaseInput = (StepMetaInterface) registry.loadClass(sp);
		//HBaseInputMeta hbaseInput = (HBaseInputMeta) registry.loadClass(sp);

		 //NamedClusterService namedClusterService = hbaseInput.getNamedClusterService();
		 Object namedClusterService = OsgiBundleUtils.invokeOsgiMethod(hbaseInput, "getNamedClusterService");
		 //NamedCluster nc = namedClusterService.getNamedClusterByName(ncName, CloudSession.getMetaStore());
		 Object nc = OsgiBundleUtils.invokeOsgiMethod(namedClusterService, "getNamedClusterByName",ncName, CloudSession.getMetaStore());
		 //HBaseService hbaseService = hbaseInput.getNamedClusterServiceLocator().getService(nc, HBaseService.class);
		 Object hbaseService =OsgiBundleUtils.invokeOsgiMethod( OsgiBundleUtils.invokeOsgiMethod(hbaseInput, "getNamedClusterServiceLocator"),"getService",nc, hbaseInput.getClass().getClassLoader().loadClass("org.pentaho.bigdata.api.hbase.HBaseService"));
		 //MappingAdmin mappingAdmin = MappingUtils.getMappingAdmin(hbaseService, null, siteConfig, defaultConfig);
			ClassLoader classLoader = hbaseInput.getClass().getClassLoader();
			Object mappingAdmin = OsgiBundleUtils.invokeOsgiMethod(classLoader.loadClass("org.pentaho.big.data.kettle.plugins.hbase.mapping.MappingUtils"), "getMappingAdmin",
					new Object[] {hbaseService, new Variables(), siteConfig, defaultConfig},
					new Class[] {classLoader.loadClass("org.pentaho.bigdata.api.hbase.HBaseService"),VariableSpace.class,String.class,String.class});
			
		//Mapping mapping = mappingAdmin.getMapping(tableName, mappingName);
		Object mapping = OsgiBundleUtils.invokeOsgiMethod(mappingAdmin, "getMapping", tableName, mappingName);
		MappingDto mappingDto = new MappingDto();
		mappingDto.transFromHbaseMapping(mapping);

		//mappingAdmin.close();
		OsgiBundleUtils.invokeOsgiMethod(mappingAdmin,"close");
		
		return mappingDto;
	}
	
	public String getHaddopPluginRootPath() {
		
		String haddopPluginPath = "";
		
		PluginInterface pi = PluginRegistry.getInstance().findPluginWithId(KettleLifecyclePluginType.class, "idatrixhdfsprovider");
		if (pi != null && !Utils.isEmpty(pi.getPluginDirectory().getPath())) {
			String hpp = pi.getPluginDirectory().getPath(); 
			try {
				FileObject f = KettleVFS.getFileObject(hpp);
				haddopPluginPath = f.getURL().toString();
				f.close();
			} catch ( Exception e) {
				haddopPluginPath = hpp;
			}
		} else {
			
			URL hbaseResourceUrl = getClass().getClassLoader().getResource("file:./plugins/idatrix-cloudetl-plugin-bigdata-0.0.1-SNAPSHOT");
			if(hbaseResourceUrl == null) {
				hbaseResourceUrl = getClass().getClassLoader().getResource("plugins/idatrix-cloudetl-plugin-bigdata-0.0.1-SNAPSHOT");
			}
			if(hbaseResourceUrl == null) {
				hbaseResourceUrl =  getClass().getClassLoader().getResource("");
			}
			if(hbaseResourceUrl != null) {
				haddopPluginPath =  hbaseResourceUrl.toString();
			}
		}
		return haddopPluginPath;
	}
	

}
