/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.quality.dto.entry.entries.general;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.pentaho.di.core.ObjectLocationSpecificationMethod;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.LogLevel;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.job.entries.job.JobEntryJob;
import org.pentaho.di.job.entry.JobEntryCopy;
import org.pentaho.di.job.entry.JobEntryInterface;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.RepositoryObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.google.common.collect.Lists;
import com.ys.idatrix.quality.dto.entry.entries.EntryParameter;
import com.ys.idatrix.quality.dto.entry.parts.ParametersDto;
import com.ys.idatrix.quality.ext.CloudApp;
import com.ys.idatrix.quality.logger.CloudLogger;
import com.ys.idatrix.quality.repository.CloudRepository;
import com.ys.idatrix.quality.repository.database.dto.FileRepositoryDto;
import com.ys.idatrix.quality.service.server.CloudServerService;

import net.sf.json.JSONObject;

/**
 * Entry - Job. 转换 org.pentaho.di.job.entries.job.JobEntryJob
 * 
 * @author XH
 * @since 2017-06-29
 */
@Component("SPjob")
@Scope("prototype")
public class SPJob implements EntryParameter {
	
	@Autowired
	CloudServerService cloudServerService;
	

	String specificationMethod=ObjectLocationSpecificationMethod.REPOSITORY_BY_REFERENCE.getCode();
	ObjectId jobObjectId;
	String filename;
	String jobname;
	String directory;
	
	String jobGroup ;

	boolean argFromPrevious;
	boolean paramsFromPrevious;
	boolean execPerRow;
	boolean setLogfile;
	String logfile;
	String logext;
	boolean addDate;
	boolean addTime;
	String logFileLevel = LogLevel.NOTHING.getCode();
	String remoteSlaveServerName;
	boolean waitingToFinish;
	boolean followingAbortRemotely;
	boolean expandingRemoteJob;
	boolean createParentFolder;
	boolean passingExport;

	List<String> argument;
	boolean passingAllParameters;
	List<ParametersDto> parameters;
	boolean setAppendLogfile;

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
	 * @return the jobGroup
	 */
	public String getJobGroup() {
		return jobGroup;
	}

	/**
	 * @param  设置 jobGroup
	 */
	public void setJobGroup(String jobGroup) {
		this.jobGroup = jobGroup;
	}

	/**
	 * @return jobObjectId
	 * @throws KettleException 
	 */
	public ObjectId getJobObjectId() throws Exception {

		if (jobObjectId == null && ! StringUtils.isEmpty( getJobname())) {
			try {
				jobObjectId = CloudRepository.getJobObjectId(null, getJobname(), getJobGroup());
			} catch ( Exception e) {
				throw new KettleException("JobName " +getJobname()+" not exist!"+ CloudLogger.getExceptionMessage(e));
			}
		}

		return jobObjectId;
	}

	/**
	 * @param 设置
	 *            jobObjectId
	 */
	public void setJobObjectId(ObjectId jobObjectId) {
		this.jobObjectId = jobObjectId;
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
	 * @return jobname
	 * @throws KettleException 
	 */
	public String getJobname() throws KettleException {

		return jobname;
	}

	/**
	 * @param 设置
	 *            jobname
	 */
	public void setJobname(String jobname) {
		this.jobname = jobname;
	}

	/**
	 * @return directory
	 * @throws KettleException 
	 * @throws  
	 */
	public String getDirectory() throws KettleException {
		if (directory == null && !StringUtils.isEmpty( getJobname())) {
			try {
				
				Object obj = CloudRepository.getJobRepositoryInfo(null, getJobname(), getJobGroup());
				if( obj != null ) {
					if( obj instanceof FileRepositoryDto ) {
						directory =((FileRepositoryDto)obj).getDirectory();
					}else {
						RepositoryObject objectInformation = ((RepositoryObject)obj);
						if (objectInformation != null) {
							directory = objectInformation.getRepositoryDirectory().getPath();
						}
					}
				}
				
			} catch (Exception e) {
				throw new KettleException("JobName " +getJobname()+" not exist!"+CloudLogger.getExceptionMessage(e));
				
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
	 * @return expandingRemoteJob
	 */
	public boolean isExpandingRemoteJob() {
		return expandingRemoteJob;
	}

	/**
	 * @param 设置
	 *            expandingRemoteJob
	 */
	public void setExpandingRemoteJob(boolean expandingRemoteJob) {
		this.expandingRemoteJob = expandingRemoteJob;
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
	 * @return passingExport
	 */
	public boolean isPassingExport() {
		return passingExport;
	}

	/**
	 * @param 设置
	 *            passingExport
	 */
	public void setPassingExport(boolean passingExport) {
		this.passingExport = passingExport;
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

	/* 
	 * 
	 */
	@Override
	public Object getParameterObject(Object json) {
		JSONObject jsonObj = JSONObject.fromObject(json);
		Map<String, Class<?>> classMap = new HashMap<>();
		classMap.put("parameters", ParametersDto.class);
		return (SPJob) JSONObject.toBean(jsonObj, SPJob.class, classMap);
	}

	/* 
	 * 
	 */
	@Override
	public Object encodeParameterObject(JobEntryCopy jobEntryCopy) throws Exception {
		JobEntryInterface entryMetaInterface = jobEntryCopy.getEntry() ;
		
		SPJob spJob = new SPJob();
		JobEntryJob jobentryjob = (JobEntryJob) entryMetaInterface;

		// filename 是相对路径,还原从jobName还原
		spJob.setFilename(StringUtils.isEmpty(jobentryjob.getJobName())? null : jobentryjob.getJobName()  );
		spJob.setJobname(jobentryjob.getJobName());
		spJob.setJobObjectId(jobentryjob.getJobObjectId());
		spJob.setDirectory(jobentryjob.getDirectory());
		
		if(!Utils.isEmpty(jobentryjob.getVariable("IDATRIX_JOB_GROUP_NAME"))) {
			spJob.setJobGroup(jobentryjob.getVariable("IDATRIX_JOB_GROUP_NAME"));
		}

		spJob.setSpecificationMethod(
				jobentryjob.getSpecificationMethod() == null ? null : jobentryjob.getSpecificationMethod().getCode());
		spJob.setArgFromPrevious(jobentryjob.argFromPrevious);
		spJob.setParamsFromPrevious(jobentryjob.paramsFromPrevious);
		spJob.setExecPerRow(jobentryjob.isExecPerRow());
		spJob.setSetLogfile(jobentryjob.setLogfile);
		spJob.setLogfile(jobentryjob.logfile);
		spJob.setLogext(jobentryjob.logext);
		spJob.setAddDate(jobentryjob.addDate);
		spJob.setAddTime(jobentryjob.addTime);
		spJob.setLogFileLevel(
				jobentryjob.logFileLevel != null ? jobentryjob.logFileLevel.getCode() : LogLevel.NOTHING.getCode());

		spJob.setRemoteSlaveServerName(jobentryjob.getRemoteSlaveServerName());
		spJob.setExpandingRemoteJob(jobentryjob.isExpandingRemoteJob());
		spJob.setPassingExport(jobentryjob.isPassingExport());
		spJob.setWaitingToFinish(jobentryjob.isWaitingToFinish());
		spJob.setFollowingAbortRemotely(jobentryjob.isFollowingAbortRemotely());
		spJob.setCreateParentFolder(jobentryjob.createParentFolder);

		spJob.setArgument(Arrays.asList(jobentryjob.arguments));

		if (jobentryjob.parameters != null) {
			spJob.setPassingAllParameters(jobentryjob.isPassingAllParameters());

			List<ParametersDto> parametersList = Lists.newArrayList();
			String[] name = jobentryjob.parameters;
			String[] streamName = jobentryjob.parameterFieldNames;
			String[] value = jobentryjob.parameterValues;
			for (int i = 0; i < name.length; i++) {
				ParametersDto ParametersDto = new ParametersDto();
				ParametersDto.setParameters(name[i]);
				ParametersDto.setParameterFieldNames(streamName[i]);
				ParametersDto.setParameterValues(value[i]);
				parametersList.add(ParametersDto);
			}
			spJob.setParameters(parametersList);
		}

		spJob.setSetAppendLogfile(jobentryjob.setAppendLogfile);

		return spJob;
	}

	/* 
	 * 
	 */
	@Override
	public void decodeParameterObject(JobEntryCopy jobEntryCopy , Object po, JobMeta jobMeta) throws Exception {
		JobEntryInterface entryMetaInterface = jobEntryCopy.getEntry() ;
		
		SPJob spJob= (SPJob)po;
		JobEntryJob  jobentryjob= (JobEntryJob )entryMetaInterface;
		
		
		//filename 是相对路径,设置为空,还原从jobName还原
		jobentryjob.setFileName(null);
		jobentryjob.setJobName(spJob.getJobname());
		jobentryjob.setRepository(CloudApp.getInstance().getRepository());
		jobentryjob.setJobObjectId(spJob.getJobObjectId());
		jobentryjob.setDirectory(spJob.getDirectory());
		jobentryjob.setVariable("IDATRIX_JOB_GROUP_NAME", spJob.getJobGroup());

		jobentryjob.setSpecificationMethod(ObjectLocationSpecificationMethod.getSpecificationMethodByCode(spJob.getSpecificationMethod()));
		jobentryjob.argFromPrevious=spJob.isArgFromPrevious() ;
		jobentryjob.paramsFromPrevious=spJob.isParamsFromPrevious() ;
		jobentryjob.setExecPerRow(spJob.isExecPerRow());
		jobentryjob.setLogfile = spJob.isSetLogfile() ;
		jobentryjob.logfile = spJob.getLogfile()  ;
		jobentryjob.logext = spJob.getLogext()  ;
		jobentryjob.addDate = spJob.isAddDate()  ;
		jobentryjob.addTime = spJob.isAddTime()  ;
		jobentryjob.logFileLevel =  LogLevel.getLogLevelForCode(spJob.getLogFileLevel() );
		
		jobentryjob.setRemoteSlaveServerName(spJob.getRemoteSlaveServerName());
		if( !StringUtils.isEmpty(spJob.getRemoteSlaveServerName())){
			jobMeta.addOrReplaceSlaveServer(cloudServerService.findSlaveServer(null,spJob.getRemoteSlaveServerName()));
		}

		jobentryjob.setFollowingAbortRemotely(spJob.isFollowingAbortRemotely());
		jobentryjob.createParentFolder = spJob.isCreateParentFolder()  ;
		jobentryjob.setPassingExport(spJob.isPassingExport());
		jobentryjob.setExpandingRemoteJob(spJob.isExpandingRemoteJob());
		jobentryjob.setWaitingToFinish(spJob.isWaitingToFinish());

		if( spJob.getArgument() != null && spJob.getArgument().size()>0){
			jobentryjob.arguments = spJob.getArgument().toArray(new String[spJob.getArgument().size()]) ;
		}
		
		if (spJob.getParameters() != null) {
			
			jobentryjob.setPassingAllParameters(spJob.isPassingAllParameters());
			
			if (spJob.getParameters() !=null && spJob.getParameters().size()>0){
				String name[] = new String[spJob.getParameters().size()];
				String streamName[] = new String[spJob.getParameters().size()];
				String value[] = new String[spJob.getParameters().size()];
				for (int i = 0; i < spJob.getParameters().size(); i++) {
					ParametersDto jpd = spJob.getParameters().get(i);
					name[i] = jpd.getParameters();
					streamName[i] = jpd.getParameterFieldNames();
					value[i] = jpd.getParameterValues();
				}
				jobentryjob.parameters = name;
				jobentryjob.parameterFieldNames = streamName;
				jobentryjob.parameterValues = value;
			}
			
		}

		jobentryjob.setAppendLogfile = spJob.isSetAppendLogfile() ;

	}

}
