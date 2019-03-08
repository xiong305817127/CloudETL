package com.ys.idatrix.cloudetl.dto.entry.entries.bigdata;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.pentaho.di.core.Const;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.util.OsgiBundleUtils;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.job.entry.JobEntryCopy;
import org.pentaho.di.job.entry.JobEntryInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ys.idatrix.cloudetl.dto.entry.entries.EntryParameter;
import com.ys.idatrix.cloudetl.dto.entry.parts.SqoopConfig;
import com.ys.idatrix.cloudetl.dto.hadoop.HadoopDetailsDto;
import com.ys.idatrix.cloudetl.service.db.CloudMetaCubeDbService;
import com.ys.idatrix.cloudetl.service.hadoop.CloudHadoopService;

import net.sf.json.JSONObject;

/**
 * Configuration for a Sqoop Export,等效  org.pentaho.big.data.kettle.plugins.sqoop.SqoopExportJobEntry
 */
@Component("SPsqoopexport")
@Scope("prototype")
public class SPSqoopExport extends SqoopConfig implements EntryParameter {
	
	
	@Autowired
	private CloudHadoopService cloudHadoopService;
	@Autowired
	private CloudMetaCubeDbService cloudDbService;
	
	public static final String EXPORT_DIR = "exportDir";
	public static final String UPDATE_KEY = "updateKey";
	public static final String UPDATE_MODE = "updateMode";
	public static final String DIRECT = "direct";
	public static final String STAGING_TABLE = "stagingTable";
	public static final String CLEAR_STAGING_TABLE = "clearStagingTable";
	public static final String BATCH = "batch";

	public static final String CALL = "call";
	public static final String COLUMNS = "columns";

	// @CommandLineArgument( name = "export-dir" )
	private String exportDir;
	// @CommandLineArgument( name = "update-key" )
	private String updateKey;
	// @CommandLineArgument( name = "update-mode" )
	private String updateMode;
	// @CommandLineArgument( name = DIRECT, flag = true )
	private String direct;
	// @CommandLineArgument( name = "staging-table" )
	private String stagingTable;
	// @CommandLineArgument( name = "clear-staging-table", flag = true )
	private String clearStagingTable;
	// @CommandLineArgument( name = BATCH, flag = true )
	private String batch;

	// @CommandLineArgument( name = "call" )
	private String call;
	// @CommandLineArgument( name = "columns" )
	private String columns;

	public String getExportDir() {
		return exportDir;
	}

	public void setExportDir(String exportDir) {
		String old = this.exportDir;
		this.exportDir = exportDir;
		propertyChange(EXPORT_DIR, old, this.exportDir);
	}

	public String getUpdateKey() {
		return updateKey;
	}

	public void setUpdateKey(String updateKey) {
		String old = this.updateKey;
		this.updateKey = updateKey;
		propertyChange(UPDATE_KEY, old, this.updateKey);
	}

	public String getUpdateMode() {
		return updateMode;
	}

	public void setUpdateMode(String updateMode) {
		String old = this.updateMode;
		this.updateMode = updateMode;
		propertyChange(UPDATE_MODE, old, this.updateMode);
	}

	public String getDirect() {
		return direct;
	}

	public void setDirect(String direct) {
		String old = this.direct;
		this.direct = direct;
		propertyChange(DIRECT, old, this.direct);
	}

	public String getStagingTable() {
		return stagingTable;
	}

	public void setStagingTable(String stagingTable) {
		String old = this.stagingTable;
		this.stagingTable = stagingTable;
		propertyChange(STAGING_TABLE, old, this.stagingTable);
	}

	public String getClearStagingTable() {
		return clearStagingTable;
	}

	public void setClearStagingTable(String clearStagingTable) {
		String old = this.clearStagingTable;
		this.clearStagingTable = clearStagingTable;
		propertyChange(CLEAR_STAGING_TABLE, old, this.clearStagingTable);
	}

	public String getBatch() {
		return batch;
	}

	public void setBatch(String batch) {
		String old = this.batch;
		this.batch = batch;
		propertyChange(BATCH, old, this.batch);
	}

	public String getCall() {
		return call;
	}

	public void setCall(String call) {
		String old = this.call;
		this.call = call;
		propertyChange(CALL, old, this.call);
	}

	public String getColumns() {
		return columns;
	}

	public void setColumns(String columns) {
		String old = this.columns;
		this.columns = columns;
		propertyChange(COLUMNS, old, this.columns);
	}

	
	@Override
	public Object getParameterObject(Object json) {
		JSONObject jsonObj = JSONObject.fromObject(json);
		return (SPSqoopExport) JSONObject.toBean(jsonObj, SPSqoopExport.class);
	}

	@Override
	public Object encodeParameterObject(JobEntryCopy jobEntryCopy) throws Exception {
		JobEntryInterface entryMetaInterface = jobEntryCopy.getEntry() ;
		
		SPSqoopExport spSqoopExport = new SPSqoopExport();
//		SqoopExportJobEntry sqoopExportJobEntry = (SqoopExportJobEntry) entryMetaInterface;
		Object config = OsgiBundleUtils.invokeOsgiMethod(entryMetaInterface,"getJobConfig"); //sqoopExportJobEntry.getJobConfig()
		objectToObject(config, spSqoopExport);
		//TODO 处理 database 和  clusterName,customArguments
		//database 和  clusterName 不用恢复
		@SuppressWarnings("unchecked")
		List<? extends Entry<String,String>> customArguments =  (List<? extends Entry<String, String>>) OsgiBundleUtils.invokeOsgiMethod(config,"getCustomArguments");  //AbstractModelList<PropertyEntry> customArguments = sqoopExportJobEntry.getJobConfig().getCustomArguments();
		if(customArguments != null && customArguments.size() >0) {
			Map<String, String> map = spSqoopExport.getCustomArgumentsMap();
			for(Entry<String,String> pe : customArguments) {
				map.put(pe.getKey(), pe.getValue());
			}
		}
		
		//获取SchemaId
		spSqoopExport.setSchemaId(Long.valueOf(Const.NVL(getToAttribute(jobEntryCopy, "schemaId"), "-1") ) );
		
		return spSqoopExport;
		
	}

	@Override
	public void decodeParameterObject(JobEntryCopy jobEntryCopy , Object po, JobMeta jobMeta) throws Exception {
		JobEntryInterface entryMetaInterface = jobEntryCopy.getEntry() ;
		
		SPSqoopExport spSqoopExport = (SPSqoopExport) po;
//		SqoopExportJobEntry sqoopExportJobEntry = (SqoopExportJobEntry) entryMetaInterface;
		Object config = OsgiBundleUtils.invokeOsgiMethod(entryMetaInterface,"getJobConfig"); //sqoopExportJobEntry.getJobConfig()
		if(config == null) {
			OsgiBundleUtils.invokeOsgiMethod(entryMetaInterface,"createJobConfig"); //sqoopExportJobEntry.createJobConfig();
			config = OsgiBundleUtils.invokeOsgiMethod(entryMetaInterface,"getJobConfig"); //sqoopExportJobEntry.getJobConfig()
		}
		
		objectToObject(spSqoopExport, config);
		//TODO 处理 database 和 clusterName , customArguments
		String clusterName= spSqoopExport.getClusterName();
		HadoopDetailsDto cluster = cloudHadoopService.editHadoop(null,clusterName);
		if(cluster != null ) {
			OsgiBundleUtils.invokeOsgiMethod(config,"setNamenodeHost",cluster.getHostname()); //sqoopExportJobEntry.getJobConfig().setNamenodeHost(cluster.getHostname());
			OsgiBundleUtils.invokeOsgiMethod(config,"setNamenodePort",cluster.getPort()); //sqoopExportJobEntry.getJobConfig().setNamenodePort(cluster.getPort());
			OsgiBundleUtils.invokeOsgiMethod(config,"setJobtrackerHost",cluster.getJobTracker().getHostname()); //sqoopExportJobEntry.getJobConfig().setJobtrackerHost(cluster.getJobTracker().getHostname());
			OsgiBundleUtils.invokeOsgiMethod(config,"setJobtrackerPort",cluster.getJobTracker().getPort()); //sqoopExportJobEntry.getJobConfig().setJobtrackerPort(cluster.getJobTracker().getPort());
		}
		DatabaseMeta dbMeta = cloudDbService.getDatabaseMeta(null,spSqoopExport.getSchemaId());
		if( dbMeta !=null) {
			//保存SchemaId
			setToAttribute(jobEntryCopy, "schemaId", spSqoopExport.getSchemaId());
			
			//sqoopExportJobEntry.getJobConfig().setConnectionInfo(dbMeta.getName(), dbMeta.getUrl(), dbMeta.getUsername(), dbMeta.getPassword());
			OsgiBundleUtils.invokeOsgiMethod(config,"setConnectionInfo",dbMeta.getName(), dbMeta.getURL(), dbMeta.getUsername(), dbMeta.getPassword());
			//sqoopExportJobEntry.setDatabaseMeta(DatabaseMeta.findDatabase(cloudDbService.getAllDbConnection(), databaseName));
			OsgiBundleUtils.invokeOsgiMethod(entryMetaInterface,"setDatabaseMeta",DatabaseMeta.findDatabase(cloudDbService.getAllDbConnection(null), spSqoopExport.getDatabase()));
		}
		
		Map<String, String> map = spSqoopExport.getCustomArgumentsMap();
		if(map != null && map.size() >0) {
			//AbstractModelList<PropertyEntry> customArguments = sqoopExportJobEntry.getJobConfig().getCustomArguments();
			@SuppressWarnings("unchecked")
			List<? extends Entry<String,String>> customArguments =  (List<? extends Entry<String, String>>) OsgiBundleUtils.invokeOsgiMethod(config,"getCustomArguments"); 
			Set<Entry<String, String>> es = map.entrySet();
			for(Entry<String, String> e : es) {
				Object pe = OsgiBundleUtils.newOsgiInstance(entryMetaInterface, "org.pentaho.big.data.kettle.plugins.job.PropertyEntry"); //PropertyEntry pe = new PropertyEntry();
				OsgiBundleUtils.invokeOsgiMethod(pe, "setKey", e.getKey());//pe.setKey(e.getKey());
				OsgiBundleUtils.invokeOsgiMethod(pe, "setValue", e.getValue());//pe.setValue(e.getValue());
				OsgiBundleUtils.invokeOsgiMethod(customArguments, "add", pe);//customArguments.add(pe);
			}
		}
		
		
	}

}
