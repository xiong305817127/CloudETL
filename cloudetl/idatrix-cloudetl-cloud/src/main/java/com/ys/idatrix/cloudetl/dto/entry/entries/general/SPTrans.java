/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.dto.entry.entries.general;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.pentaho.di.cluster.SlaveServer;
import org.pentaho.di.core.ObjectLocationSpecificationMethod;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.LogLevel;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.job.entries.trans.JobEntryTrans;
import org.pentaho.di.job.entry.JobEntryCopy;
import org.pentaho.di.job.entry.JobEntryInterface;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.RepositoryObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.google.common.collect.Lists;
import com.ys.idatrix.cloudetl.dto.entry.entries.EntryParameter;
import com.ys.idatrix.cloudetl.dto.entry.parts.ParametersDto;
import com.ys.idatrix.cloudetl.ext.CloudApp;
import com.ys.idatrix.cloudetl.logger.CloudLogger;
import com.ys.idatrix.cloudetl.repository.CloudRepository;
import com.ys.idatrix.cloudetl.repository.database.dto.FileRepositoryDto;
import com.ys.idatrix.cloudetl.repository.xml.metastore.meta.DefaultEngineMeta;
import com.ys.idatrix.cloudetl.service.engine.CloudDefaultEngineService;
import com.ys.idatrix.cloudetl.service.server.CloudServerService;

import net.sf.json.JSONObject;

/**
 * Entry - Trans. 转换 org.pentaho.di.job.entries.trans.JobEntryTrans
 * 
 * @author XH
 * @since 2017-06-29
 */
@Component("SPtrans")
@Scope("prototype")
public class SPTrans implements EntryParameter {
	
	@Autowired
	private CloudDefaultEngineService defaultEngineService;
	
	@Autowired
	CloudServerService cloudServerService;

	String specificationMethod=ObjectLocationSpecificationMethod.REPOSITORY_BY_REFERENCE.getCode();
	ObjectId transObjectId;
	String filename;
	String transname;
	String directory;
	
	String transGroup ;

	boolean argFromPrevious = false;
	boolean paramsFromPrevious = false;
	boolean execPerRow = false;
	boolean clearResultRows = false ;
	boolean clearResultFiles = false;
	boolean setLogfile = false;
	String logfile;
	String logext;
	boolean addDate = false ;
	boolean addTime = false ;

	String logFileLevel = LogLevel.BASIC.getCode();
	boolean clustering;
	String remoteSlaveServerName;
	boolean setAppendLogfile = false ;
	boolean waitingToFinish = true ;
	boolean followingAbortRemotely = false ;
	boolean createParentFolder = false;
	boolean loggingRemoteWork;
	String runConfiguration;

	List<String> argument;
	boolean passingAllParameters = true;
	List<ParametersDto> parameters;

	/**
	 * @return specificationMethod
	 */
	public String getSpecificationMethod() {
		return specificationMethod;
	}

	/**
	 * @param 设置
	 *            specificationMethod
	 */
	public void setSpecificationMethod(String specificationMethod) {
		this.specificationMethod = specificationMethod;
	}

	/**
	 * @return transObjectId
	 * @throws KettleException 
	 */
	public ObjectId getTransObjectId() throws Exception {
		if (transObjectId == null &&  !StringUtils.isEmpty( getTransname())) {
			try {
				transObjectId = CloudRepository.getTransObjectId(null,getTransname(), getTransGroup());
			} catch (Exception e) {
				throw new KettleException("transName " +getTransname()+" not exist!"+ CloudLogger.getExceptionMessage(e));
			}
			if( transObjectId == null ) {
				throw new KettleException("transName " +getTransname()+" not exist!");
			}
		}
		return transObjectId;
	}

	/**
	 * @param 设置
	 *            transObjectId
	 */
	public void setTransObjectId(ObjectId transObjectId) {
		this.transObjectId = transObjectId;
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
	 * @return transname
	 * @throws KettleException 
	 */
	public String getTransname() throws KettleException {

		return transname;
	}

	/**
	 * @param 设置
	 *            transname
	 */
	public void setTransname(String transname) {
		this.transname = transname;
	}

	/**
	 * @return directory
	 * @throws KettleException 
	 */
	public String getDirectory() throws Exception {
		if (directory == null &&  !StringUtils.isEmpty( getTransname())) {
			try {
				Object obj = CloudRepository.getJobRepositoryInfo(null,getTransname(), getTransGroup()) ;
				if( obj!= null ) {
					if( obj instanceof FileRepositoryDto ) {
						directory =((FileRepositoryDto)obj).getDirectory();
					}else {
						RepositoryObject objectInformation = (RepositoryObject)obj ;
						if (objectInformation != null) {
							directory = objectInformation.getRepositoryDirectory().getPath();
						}
					}
				}
				
			} catch ( Exception e) {
				throw new KettleException("transName " +getTransname()+" not exist!"+ CloudLogger.getExceptionMessage(e));
			}
		}
		return directory;
	}

	/**
	 * @param 设置
	 *            directory
	 */
	public void setDirectory(String directory) {
		this.directory = directory;
	}

	/**
	 * @return argFromPrevious
	 */
	public boolean isArgFromPrevious() {
		return argFromPrevious;
	}

	/**
	 * @param 设置
	 *            argFromPrevious
	 */
	public void setArgFromPrevious(boolean argFromPrevious) {
		this.argFromPrevious = argFromPrevious;
	}

	/**
	 * @return paramsFromPrevious
	 */
	public boolean isParamsFromPrevious() {
		return paramsFromPrevious;
	}

	/**
	 * @param 设置
	 *            paramsFromPrevious
	 */
	public void setParamsFromPrevious(boolean paramsFromPrevious) {
		this.paramsFromPrevious = paramsFromPrevious;
	}

	/**
	 * @return execPerRow
	 */
	public boolean isExecPerRow() {
		return execPerRow;
	}

	/**
	 * @param 设置
	 *            execPerRow
	 */
	public void setExecPerRow(boolean execPerRow) {
		this.execPerRow = execPerRow;
	}

	/**
	 * @return clearResultRows
	 */
	public boolean isClearResultRows() {
		return clearResultRows;
	}

	/**
	 * @param 设置
	 *            clearResultRows
	 */
	public void setClearResultRows(boolean clearResultRows) {
		this.clearResultRows = clearResultRows;
	}

	/**
	 * @return clearResultFiles
	 */
	public boolean isClearResultFiles() {
		return clearResultFiles;
	}

	/**
	 * @param 设置
	 *            clearResultFiles
	 */
	public void setClearResultFiles(boolean clearResultFiles) {
		this.clearResultFiles = clearResultFiles;
	}

	/**
	 * @return setLogfile
	 */
	public boolean isSetLogfile() {
		return setLogfile;
	}

	/**
	 * @param 设置
	 *            setLogfile
	 */
	public void setSetLogfile(boolean setLogfile) {
		this.setLogfile = setLogfile;
	}

	/**
	 * @return logfile
	 */
	public String getLogfile() {
		return logfile;
	}

	/**
	 * @param 设置
	 *            logfile
	 */
	public void setLogfile(String logfile) {
		this.logfile = logfile;
	}

	/**
	 * @return logext
	 */
	public String getLogext() {
		return logext;
	}

	/**
	 * @param 设置
	 *            logext
	 */
	public void setLogext(String logext) {
		this.logext = logext;
	}

	/**
	 * @return addDate
	 */
	public boolean isAddDate() {
		return addDate;
	}

	/**
	 * @param 设置
	 *            addDate
	 */
	public void setAddDate(boolean addDate) {
		this.addDate = addDate;
	}

	/**
	 * @return addTime
	 */
	public boolean isAddTime() {
		return addTime;
	}

	/**
	 * @param 设置
	 *            addTime
	 */
	public void setAddTime(boolean addTime) {
		this.addTime = addTime;
	}

	/**
	 * @return logFileLevel
	 */
	public String getLogFileLevel() {
		return logFileLevel;
	}

	/**
	 * @param 设置
	 *            logFileLevel
	 */
	public void setLogFileLevel(String logFileLevel) {
		this.logFileLevel = logFileLevel;
	}

	/**
	 * @return clustering
	 */
	public boolean isClustering() {
		return clustering;
	}

	/**
	 * @param 设置
	 *            clustering
	 */
	public void setClustering(boolean clustering) {
		this.clustering = clustering;
	}

	/**
	 * @return remoteSlaveServerName
	 */
	public String getRemoteSlaveServerName() {
		return remoteSlaveServerName;
	}

	/**
	 * @param 设置
	 *            remoteSlaveServerName
	 */
	public void setRemoteSlaveServerName(String remoteSlaveServerName) {
		this.remoteSlaveServerName = remoteSlaveServerName;
	}

	/**
	 * @return setAppendLogfile
	 */
	public boolean isSetAppendLogfile() {
		return setAppendLogfile;
	}

	/**
	 * @param 设置
	 *            setAppendLogfile
	 */
	public void setSetAppendLogfile(boolean setAppendLogfile) {
		this.setAppendLogfile = setAppendLogfile;
	}

	/**
	 * @return waitingToFinish
	 */
	public boolean isWaitingToFinish() {
		return waitingToFinish;
	}

	/**
	 * @param 设置
	 *            waitingToFinish
	 */
	public void setWaitingToFinish(boolean waitingToFinish) {
		this.waitingToFinish = waitingToFinish;
	}

	/**
	 * @return followingAbortRemotely
	 */
	public boolean isFollowingAbortRemotely() {
		return followingAbortRemotely;
	}

	/**
	 * @param 设置
	 *            followingAbortRemotely
	 */
	public void setFollowingAbortRemotely(boolean followingAbortRemotely) {
		this.followingAbortRemotely = followingAbortRemotely;
	}

	/**
	 * @return createParentFolder
	 */
	public boolean isCreateParentFolder() {
		return createParentFolder;
	}

	/**
	 * @param 设置
	 *            createParentFolder
	 */
	public void setCreateParentFolder(boolean createParentFolder) {
		this.createParentFolder = createParentFolder;
	}

	/**
	 * @return loggingRemoteWork
	 */
	public boolean isLoggingRemoteWork() {
		return loggingRemoteWork;
	}

	/**
	 * @param 设置
	 *            loggingRemoteWork
	 */
	public void setLoggingRemoteWork(boolean loggingRemoteWork) {
		this.loggingRemoteWork = loggingRemoteWork;
	}

	/**
	 * @return runConfiguration
	 */
	public String getRunConfiguration() {
		return runConfiguration;
	}

	/**
	 * @param 设置
	 *            runConfiguration
	 */
	public void setRunConfiguration(String runConfiguration) {
		this.runConfiguration = runConfiguration;
	}

	/**
	 * @return arguments
	 */
	public List<String> getArgument() {
		return argument;
	}

	/**
	 * @param 设置
	 *            arguments
	 */
	public void setArgument(List<String> arguments) {
		this.argument = arguments;
	}

	/**
	 * @return passingAllParameters
	 */
	public boolean isPassingAllParameters() {
		return passingAllParameters;
	}

	/**
	 * @param 设置
	 *            passingAllParameters
	 */
	public void setPassingAllParameters(boolean passingAllParameters) {
		this.passingAllParameters = passingAllParameters;
	}

	/**
	 * @return parameters
	 */
	public List<ParametersDto> getParameters() {
		return parameters;
	}

	/**
	 * @param 设置
	 *            parameters
	 */
	public void setParameters(List<ParametersDto> parameters) {
		this.parameters = parameters;
	}

	
	
	/**
	 * @return the transGroup
	 */
	public String getTransGroup() {
		return transGroup;
	}

	/**
	 * @param  设置 transGroup
	 */
	public void setTransGroup(String transGroup) {
		this.transGroup = transGroup;
	}

	/* 
	 * 
	 */
	@Override
	public Object getParameterObject(Object json) {
		JSONObject jsonObj = JSONObject.fromObject(json);
		Map<String, Class<?>> classMap = new HashMap<>();
		classMap.put("parameters", ParametersDto.class);
		return (SPTrans) JSONObject.toBean(jsonObj, SPTrans.class, classMap);
	}

	/* 
	 * 
	 */
	@Override
	public Object encodeParameterObject(JobEntryCopy jobEntryCopy) throws Exception {
		JobEntryInterface entryMetaInterface = jobEntryCopy.getEntry() ;
		
		SPTrans spTrans = new SPTrans();
		JobEntryTrans jobentrytrans = (JobEntryTrans) entryMetaInterface;

		// filename 是相对路径,还原从Transname还原
		spTrans.setFilename( StringUtils.isEmpty( jobentrytrans.getTransname()) ? null :  jobentrytrans.getTransname() );
		spTrans.setTransname(jobentrytrans.getTransname());
		spTrans.setTransObjectId(jobentrytrans.getTransObjectId());
		spTrans.setDirectory(jobentrytrans.getDirectory());
		
		if(!Utils.isEmpty(jobentrytrans.getVariable("IDATRIX_TRANS_GROUP_NAME"))) {
			spTrans.setTransGroup(jobentrytrans.getVariable("IDATRIX_TRANS_GROUP_NAME"));
		}


		spTrans.setSpecificationMethod(jobentrytrans.getSpecificationMethod() == null ? null
				: jobentrytrans.getSpecificationMethod().getCode());

		spTrans.setArgFromPrevious(jobentrytrans.argFromPrevious);
		spTrans.setParamsFromPrevious(jobentrytrans.paramsFromPrevious);
		spTrans.setExecPerRow(jobentrytrans.execPerRow);
		spTrans.setClearResultRows(jobentrytrans.clearResultRows);
		spTrans.setClearResultFiles(jobentrytrans.clearResultFiles);
		spTrans.setSetLogfile(jobentrytrans.setLogfile);
		spTrans.setLogfile(jobentrytrans.logfile);
		spTrans.setLogext(jobentrytrans.logext);
		spTrans.setAddDate(jobentrytrans.addDate);
		spTrans.setAddTime(jobentrytrans.addTime);
		spTrans.setLogFileLevel(
				jobentrytrans.logFileLevel != null ? jobentrytrans.logFileLevel.getCode() : LogLevel.NOTHING.getCode());

		spTrans.setRemoteSlaveServerName(jobentrytrans.getRemoteSlaveServerName());
		spTrans.setSetAppendLogfile(jobentrytrans.setAppendLogfile);
		spTrans.setRunConfiguration(jobentrytrans.getRunConfiguration());
		spTrans.setClustering(jobentrytrans.isClustering());
		spTrans.setWaitingToFinish(jobentrytrans.isWaitingToFinish());
		spTrans.setFollowingAbortRemotely(jobentrytrans.isFollowingAbortRemotely());
		spTrans.setLoggingRemoteWork(jobentrytrans.isLoggingRemoteWork());
		spTrans.setCreateParentFolder(jobentrytrans.createParentFolder);

		spTrans.setArgument(Arrays.asList(jobentrytrans.arguments));

		if (jobentrytrans.parameters != null) {
			spTrans.setPassingAllParameters(jobentrytrans.isPassingAllParameters());

			List<ParametersDto> parametersList = Lists.newArrayList();
			String[] name = jobentrytrans.parameters;
			String[] streamName = jobentrytrans.parameterFieldNames;
			String[] value = jobentrytrans.parameterValues;
			for (int i = 0; i < name.length; i++) {
				ParametersDto jobparametersDto = new ParametersDto();
				jobparametersDto.setParameters(name[i]);
				jobparametersDto.setParameterFieldNames(streamName[i]);
				jobparametersDto.setParameterValues(value[i]);
				parametersList.add(jobparametersDto);
			}
			spTrans.setParameters(parametersList);
		}

		return spTrans;
	}

	/* 
	 * 
	 */
	@Override
	public void decodeParameterObject(JobEntryCopy jobEntryCopy , Object po, JobMeta jobMeta) throws Exception {
		JobEntryInterface entryMetaInterface = jobEntryCopy.getEntry() ;
		
		SPTrans spTrans= (SPTrans)po;
		JobEntryTrans  jobentrytrans= (JobEntryTrans )entryMetaInterface;
		
		//filename 是相对路径,设置为空,还原从Transname还原
		jobentrytrans.setFileName(null);
		jobentrytrans.setTransname(spTrans.getTransname());
		jobentrytrans.setRepository(CloudApp.getInstance().getRepository());
		jobentrytrans.setTransObjectId(spTrans.getTransObjectId());
		jobentrytrans.setDirectory(spTrans.getDirectory());
		jobentrytrans.setVariable("IDATRIX_TRANS_GROUP_NAME", spTrans.getTransGroup());

		jobentrytrans.setSpecificationMethod(ObjectLocationSpecificationMethod.getSpecificationMethodByCode(spTrans.getSpecificationMethod()));
		jobentrytrans.setWaitingToFinish(spTrans.isWaitingToFinish());
		jobentrytrans.setFollowingAbortRemotely(spTrans.isFollowingAbortRemotely());
		jobentrytrans.setLoggingRemoteWork(spTrans.isLoggingRemoteWork());
		
		jobentrytrans.setRunConfiguration(spTrans.getRunConfiguration());
		if(!StringUtils.isEmpty(spTrans.getRunConfiguration())){
			DefaultEngineMeta defaultEngine = defaultEngineService.findDefaultEngine(null,spTrans.getRunConfiguration());
		    jobentrytrans.setClustering( defaultEngine.isClustered() );
			String remoteServer = defaultEngine.getServer();
			if(StringUtils.hasText(remoteServer)) {
				SlaveServer slaveServer = cloudServerService.findSlaveServer(null,remoteServer);
				jobMeta.addOrReplaceSlaveServer(slaveServer);
		        jobentrytrans.setRemoteSlaveServerName( remoteServer );
		    }
		    jobentrytrans.setLoggingRemoteWork( defaultEngine.isLogRemoteExecutionLocally());
		}

		jobentrytrans.argFromPrevious = spTrans.isArgFromPrevious() ;
		jobentrytrans.paramsFromPrevious = spTrans.isParamsFromPrevious() ;
		jobentrytrans.execPerRow = spTrans.isExecPerRow() ;
		jobentrytrans.clearResultRows = spTrans.isClearResultRows() ;
		jobentrytrans.clearResultFiles = spTrans.isClearResultFiles() ;
		jobentrytrans.setLogfile = spTrans.isSetLogfile() ;
		jobentrytrans.logfile = spTrans.getLogfile() ;
		jobentrytrans.logext = spTrans.getLogext() ;
		jobentrytrans.addDate = spTrans.isAddDate() ;
		jobentrytrans.addTime = spTrans.isAddTime() ;
		jobentrytrans.logFileLevel =  LogLevel.getLogLevelForCode(spTrans.getLogFileLevel() );
		jobentrytrans.setAppendLogfile = spTrans.isSetAppendLogfile() ;
		jobentrytrans.createParentFolder = spTrans.isCreateParentFolder() ;
		
		if(spTrans.getArgument() !=null && spTrans.getArgument().size() >0){
			jobentrytrans.arguments = spTrans.getArgument().toArray(new String[spTrans.getArgument().size()]) ;
		}else {
			jobentrytrans.arguments =new String[] {};
		}
		
		jobentrytrans.setPassingAllParameters(spTrans.isPassingAllParameters());
		if (spTrans.getParameters() !=null && spTrans.getParameters().size()>0){
				String name[] = new String[spTrans.getParameters().size()];
				String streamName[] = new String[spTrans.getParameters().size()];
				String value[] = new String[spTrans.getParameters().size()];
				for (int i = 0; i < spTrans.getParameters().size(); i++) {
					ParametersDto jpd = spTrans.getParameters().get(i);
					name[i] = jpd.getParameters();
					streamName[i] = jpd.getParameterFieldNames();
					value[i] = jpd.getParameterValues();
				}
				jobentrytrans.parameters = name;
				jobentrytrans.parameterFieldNames = streamName;
				jobentrytrans.parameterValues = value;
		}else {
			jobentrytrans.parameters = new String[] {};;
			jobentrytrans.parameterFieldNames = new String[] {};;
			jobentrytrans.parameterValues = new String[] {};;
		}
		
	}

}
