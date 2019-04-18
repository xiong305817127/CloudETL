/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.quality.service.job;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.commons.vfs2.FileObject;
import org.pentaho.di.cluster.SlaveServer;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.LogLevel;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.core.vfs.KettleVFS;
import org.pentaho.di.job.JobExecutionConfiguration;
import org.pentaho.di.job.JobHopMeta;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.job.entry.JobEntryCopy;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.RepositoryDirectoryInterface;
import org.pentaho.di.repository.RepositoryElementMetaInterface;
import org.pentaho.di.repository.RepositoryObjectType;
import org.pentaho.di.www.CarteSingleton;
import org.pentaho.pms.util.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.google.common.collect.Lists;
import com.ys.idatrix.quality.dto.JobDto;
import com.ys.idatrix.quality.dto.common.CheckResultDto;
import com.ys.idatrix.quality.dto.common.PaginationDto;
import com.ys.idatrix.quality.dto.common.ReturnCodeDto;
import com.ys.idatrix.quality.dto.engine.ExecConfigurationDto;
import com.ys.idatrix.quality.dto.entry.EntryGuiDto;
import com.ys.idatrix.quality.dto.entry.EntryInfoDto;
import com.ys.idatrix.quality.dto.history.ExecHistoryDto;
import com.ys.idatrix.quality.dto.history.ExecHistoryRecordDto;
import com.ys.idatrix.quality.dto.history.ExecLogsDto;
import com.ys.idatrix.quality.dto.hop.HopInfoDto;
import com.ys.idatrix.quality.dto.job.JobBatchExecRequestDto;
import com.ys.idatrix.quality.dto.job.JobBatchStopDto;
import com.ys.idatrix.quality.dto.job.JobExecEntryMeasureDto;
import com.ys.idatrix.quality.dto.job.JobExecEntryStatusDto;
import com.ys.idatrix.quality.dto.job.JobExecIdDto;
import com.ys.idatrix.quality.dto.job.JobExecLogDto;
import com.ys.idatrix.quality.dto.job.JobExecRequestDto;
import com.ys.idatrix.quality.dto.job.JobExecStatusDto;
import com.ys.idatrix.quality.dto.job.JobInfoDto;
import com.ys.idatrix.quality.dto.job.JobOverviewDto;
import com.ys.idatrix.quality.ext.CloudApp;
import com.ys.idatrix.quality.ext.CloudSession;
import com.ys.idatrix.quality.ext.executor.CloudExecutorStatus;
import com.ys.idatrix.quality.ext.executor.CloudExecution.ExecutionInfo;
import com.ys.idatrix.quality.ext.executor.CloudExecution;
import com.ys.idatrix.quality.ext.executor.CloudJobExecutor;
import com.ys.idatrix.quality.ext.executor.logger.CloudExecHistory;
import com.ys.idatrix.quality.ext.executor.logger.CloudExecLog;
import com.ys.idatrix.quality.ext.utils.StringEscapeHelper;
import com.ys.idatrix.quality.logger.CloudLogConst;
import com.ys.idatrix.quality.logger.CloudLogType;
import com.ys.idatrix.quality.logger.CloudLogger;
import com.ys.idatrix.quality.repository.CloudRepository;
import com.ys.idatrix.quality.repository.database.dto.FileRepositoryDto;
import com.ys.idatrix.quality.repository.xml.CloudFileRepository;
import com.ys.idatrix.quality.repository.xml.metastore.meta.DefaultEngineMeta;
import com.ys.idatrix.quality.service.CloudBaseService;
import com.ys.idatrix.quality.service.engine.CloudDefaultEngineService;
import com.ys.idatrix.quality.service.server.CloudServerService;

/**
 * Job repository implementation.
 *
 * @author JW
 * @since 05-12-2017
 *
 */
@Service
public class CloudJobServiceImpl extends CloudBaseService implements CloudJobService {
	
	@Autowired
	CloudServerService cloudServerService;
	
	@Autowired
	private CloudDefaultEngineService defaultEngineService;
	
	/**
	 * Get current status for job.
	 * @param jobName
	 * @return
	 * @throws Exception 
	 */
	private String jobCurrentStatus(String owner , String jabName) throws Exception {
		JobDto jd = new JobDto();
		jd.setOwner(owner);
		jd.setName(jabName);
		jobCurrentStatusAndTime(jd);	
		return jd.getStatus() ;
	}
	
	/**
	 * 获取 job的 status , execTime , lastExecTime 
	 * @param jd
	 * @throws Exception
	 */
	private void jobCurrentStatusAndTime(JobDto jd) throws Exception {
		
		String jobName = jd.getName() ;
		String owner = jd.getOwner() ;
		
		String currentStatus = null ;
		Date begin = null;
		Date end = null;
		
		ExecutionInfo executionInfo = CloudExecution.getInstance().getExecutionInfo(owner, jobName,true);
		if (executionInfo != null) {
			CloudJobExecutor jobExecutor = executionInfo.getJobExecutor();
			if (jobExecutor != null) {
				currentStatus = jobExecutor.getStatus();
				begin = jobExecutor.getBeginDate();
				end = jobExecutor.getEndDate();
			}
		}
		
		if ( Utils.isEmpty(currentStatus) || begin == null ) {
			CloudExecHistory execHistory = CloudExecHistory.initExecHistory( jd.getOwner() , jobName, CloudLogType.JOB_HISTORY);
			ExecHistoryRecordDto record = execHistory.getLastExecRecord();
			if( record != null) {
				currentStatus = record.getStatus();
				begin =  record.getBegin();
				end = record.getEnd();
					
				if (CloudExecutorStatus.assertRunning(currentStatus)) {
					// Exception - current status is running but executor is not existing!
					if( System.currentTimeMillis() > ( CarteSingleton.getSlaveServerConfig().getSlaveServer().getChangedDate().getTime() + 3000000 )) {
						//启动时间超过 5分钟 才进行异常纠正,否则可能是重启服务过慢造成任务无法重启
						currentStatus = CloudExecutorStatus.UNKNOWN.getType();
						
						record.setStatus(currentStatus);
						record.setEnd( new Date() );
						try {
							execHistory.updateExecRecord(record);
						}catch(Exception e) {
						}
					}
				}
			}
		}
		
		jd.setStatus(CloudExecutorStatus.correctStatusType(Const.NVL(currentStatus, CloudExecutorStatus.WAITING.getType())));
		jd.setExecTime(DurationFormatUtils.formatPeriod(((begin != null) ? begin : new Date()).getTime(), ((end != null) ? end : new Date()).getTime(), CloudLogConst.EXEC_DURATION_PATTERN) );
		if(begin != null ) {
			jd.setLastExecTime( DateFormatUtils.format(begin, CloudLogConst.EXEC_TIME_PATTERN));
		}
	}
	
	
	@Override
	public List<String> getCloudJobGroupList(String owner) throws Exception{
		if( Utils.isEmpty( owner )) {
    		owner = CloudSession.getResourceUser() ;
    	}
		return CloudRepository.getJobGroups(owner);
	}
	
	@Override
	public List<String> getCloudJobUserList( ) throws Exception{
		return CloudRepository.getCurrentRenterUsers(RepositoryObjectType.JOB);
	}
	
	@Override
	public String getCloudJobGroup(String owner ,String jobName ,String... priorityGroups) throws Exception{
		if( Utils.isEmpty( owner )) {
    		owner = CloudSession.getResourceUser() ;
    	}
		return CloudRepository.getJobGroup(owner ,jobName,priorityGroups);
	}
	
	@Override
	public Map<String,List<String>> getCloudJobNameList(String owner ,String group ) throws Exception{
		return CloudRepository.getJobNameMap(owner,group);
	}

	@Override
	public Map<String,PaginationDto<JobDto>> getCloudJobList(String owner ,String group ,boolean isMap ,int page, int pageSize,String search) throws Exception {

		//直接数据库查询方式
		Map<String, PaginationDto<FileRepositoryDto>> databaseMap = CloudRepository.getJobElementsMap(owner, group, page, pageSize, search ,isMap);
		if( databaseMap != null ) {
			return databaseMap.entrySet().stream().collect(Collectors.toMap(entry -> { return entry.getKey(); } , entry -> { 
				PaginationDto<JobDto> res = new PaginationDto<JobDto>(page, pageSize,search);
				res.transformPagination(entry.getValue(), new PaginationDto.DealRowsInterface<JobDto>() {
					@Override
					public JobDto dealRow(Object obj, Object... params) throws Exception {
						FileRepositoryDto transEleMeta = (FileRepositoryDto) obj;
						JobDto jtrans = new JobDto();
						jtrans.setOwner(transEleMeta.getOwner());
						jtrans.setName(transEleMeta.getName());
						jtrans.setGroup(transEleMeta.getGroup());
						jtrans.setDescription(transEleMeta.getDescription());
						jtrans.setModifiedTime(DateFormatUtils.format(transEleMeta.getUpdateTime(), CloudLogConst.EXEC_TIME_PATTERN));

						jobCurrentStatusAndTime(jtrans);
						return jtrans;
					}
					
					@Override
					public boolean match(Object obj, String search, Object... params) throws Exception {
						return true ;
					}
				});
				return res;
				
			}));
		}
		//数据库文件适配方式
		Map<String, List<Object>> jobElesNames = CloudRepository.getJobElementsMap(owner , group);
		return getPaginationMaps(isMap, page, pageSize, search, jobElesNames, new PaginationDto.DealRowsInterface<JobDto>() {

			@Override
			public JobDto dealRow(Object obj, Object... params) throws Exception {
				String eleOwner = params != null && params.length > 0 ? (String) params[0]: CloudSession.getResourceUser();
				
				JobDto jd = new JobDto();
				if(obj instanceof RepositoryElementMetaInterface){
					RepositoryElementMetaInterface ele = (RepositoryElementMetaInterface) obj;
					String jobName = ele.getName();
					
					jd.setOwner(eleOwner);
					jd.setName(jobName);
					jd.setGroup(ele.getRepositoryDirectory().getName());
					jd.setDescription(ele.getDescription());
					jd.setClusters(0);
					jd.setModifiedTime(ele.getModifiedDate() != null? DateFormatUtils.format(ele.getModifiedDate(), CloudLogConst.EXEC_TIME_PATTERN): "");

					jobCurrentStatusAndTime(jd);
				}else if(obj instanceof FileRepositoryDto){
					FileRepositoryDto jobEleMeta = (FileRepositoryDto) obj;
					
					jd.setName(jobEleMeta.getName());
					jd.setGroup(jobEleMeta.getGroup());
					jd.setDescription(jobEleMeta.getDescription());
					jd.setClusters(0);
					jd.setModifiedTime( DateFormatUtils.format(jobEleMeta.getUpdateTime(), CloudLogConst.EXEC_TIME_PATTERN) );

					jobCurrentStatusAndTime(jd);
				}
				return jd;
			}

			@Override
			public boolean match(Object obj, String search, Object... params) throws Exception {
				String eleOwner = params != null && params.length > 0 ? (String) params[0] : CloudSession.getResourceUser();
				String jobName = "";
				if(obj instanceof RepositoryElementMetaInterface){
					RepositoryElementMetaInterface ele = (RepositoryElementMetaInterface) obj;
					jobName = ele.getName();
				}else if(obj instanceof FileRepositoryDto){
					FileRepositoryDto ele = (FileRepositoryDto) obj;
					jobName = ele.getName();
				}

				if (search != null && search.contains("::")) {
					String type = search.split("::", 2)[0];
					String value = search.split("::", 2)[1];
					boolean typeRet = true;
					if (!Utils.isEmpty(type)) {

						String status = CloudExecutorStatus.WAITING.getType();
						ExecutionInfo executionInfo = CloudExecution.getInstance().getExecutionInfo(eleOwner, jobName,true);
						if (executionInfo != null) {
							CloudJobExecutor jobExecutor = executionInfo.getJobExecutor();
							if (jobExecutor != null) {
								status = jobExecutor.getStatus();
							}
						}
						switch (type) {
						case "wait":
							typeRet = Lists.newArrayList("Waiting", "Undefined", "Finished").contains(status);
							break;
						case "run":
							typeRet = Lists.newArrayList("Running", "Preparing executing", "Initializing", "Paused").contains(status);
							break;
						case "warn":
							typeRet = Lists.newArrayList("Finished (with errors)", "Stopped", "Halting").contains(status);
							break;
						case "error":
							typeRet = Lists.newArrayList("TimeOut", "Failed", "Unknown").contains(status);
							break;
						default:
							typeRet = type.equalsIgnoreCase(status);
							break;
						}

					}
					boolean valueRet = true;
					if (!Utils.isEmpty(value)) {
						valueRet = defaultMatch(jobName, value);// jobName!=null&&jobName.toLowerCase().contains(value.toLowerCase());;
					}
					return typeRet && valueRet;
				}
				return defaultMatch(jobName, search);// jobName!=null&&jobName.toLowerCase().contains(search.toLowerCase());
			}

		});
	}

	/* 
	 * 
	 */
	@Override
	public JobOverviewDto loadCloudJob(String owner ,String name,String group ) throws Exception {
		if( Utils.isEmpty( owner )) {
    		owner = CloudSession.getResourceUser() ;
    	}
		JobOverviewDto overview = new JobOverviewDto();

		JobMeta jobMeta = CloudRepository.loadJobByName(owner ,name,group);
		JobInfoDto jobInfo = new JobInfoDto();
		jobInfo.setOwner(owner);
		jobInfo.setName(jobMeta.getName());
		jobInfo.setGroup(jobMeta.getRepositoryDirectory().getName());
		jobInfo.setDescription(jobMeta.getDescription());
		String[] paramsKeys = jobMeta.listParameters() ;
		if(paramsKeys != null && paramsKeys.length >0) {
			for(String key : paramsKeys) {
				String value = jobMeta.getParameterDefault(key) ;
				jobInfo.addParams(key, value) ;
			}
		}
		overview.setInfo(jobInfo);

		List<EntryInfoDto> entryList = new ArrayList<>();
		List<JobEntryCopy> entrys = jobMeta.getJobCopies();
		for (JobEntryCopy entry : entrys) {
			if (entry == null || entry.getEntry() == null) {
				continue;
			}
			EntryInfoDto entryInfo = new EntryInfoDto();
			entryInfo.setName(entry.getName());
			entryInfo.setType(entry.getEntry().getPluginId());
			entryInfo.setDescription(entry.getDescription());

			EntryGuiDto entryGui = new EntryGuiDto();
			entryGui.setDraw(entry.isDrawn() ? "Y" : "N");
			entryGui.setXloc(entry.getLocation().x);
			entryGui.setYloc(entry.getLocation().y);
			entryGui.setNr(entry.getNr());
			entryInfo.setGui(entryGui);
			entryList.add(entryInfo);
		}
		overview.setEntryList(entryList);

		List<HopInfoDto> hopList = new ArrayList<>();
		List<JobHopMeta> jobhops = jobMeta.getJobhops();
		for (JobHopMeta hopMeta : jobhops) {
			if (hopMeta == null) {
				continue;
			}
			HopInfoDto hopInfo = new HopInfoDto();
			hopInfo.setEnabled(hopMeta.isEnabled());
			hopInfo.setEvaluation(hopMeta.getEvaluation());
			hopInfo.setUnconditional(hopMeta.isUnconditional());
			hopInfo.setFrom(hopMeta.getFromEntry().getName());
			hopInfo.setTo(hopMeta.getToEntry().getName());
			hopInfo.setFromNr(hopMeta.getFromEntry().getNr());
			hopInfo.setToNr(hopMeta.getToEntry().getNr());

			hopList.add(hopInfo);
		}
		overview.setHopList(hopList);

		return overview;
	}

	/* 
	 * 
	 */
	@Override
	public JobInfoDto editJobAttributes(String owner ,String name,String group ) throws Exception {
		if( Utils.isEmpty( owner )) {
    		owner = CloudSession.getResourceUser() ;
    	}
		JobInfoDto jobInfo = new JobInfoDto();

		JobMeta jobMeta = CloudRepository.loadJobByName(owner,name,group);

		jobInfo.setName(jobMeta.getName());
		jobInfo.setGroup(jobMeta.getRepositoryDirectory().getName());
		jobInfo.setNewName("");
		jobInfo.setDescription(jobMeta.getDescription());
		
		String[] paramsKeys = jobMeta.listParameters() ;
		if(paramsKeys != null && paramsKeys.length >0) {
			for(String key : paramsKeys) {
				String value = jobMeta.getParameterDefault(key) ;
				jobInfo.addParams(key, value) ;
			}
		}
		return jobInfo;
	}

	/* 
	 * 
	 */
	@Override
	public ReturnCodeDto saveJobAttributes(JobInfoDto jobInfo) throws Exception {
		ReturnCodeDto retCode = new ReturnCodeDto();

		if(!StringUtils.isEmpty(jobInfo.getNewName()) && checkJobName(jobInfo.getOwner() , jobInfo.getNewName()).getResult()){
			//已存在,不能更新
			retCode.setRetCode(1);
			retCode.setMessage("Failed, NewName "+jobInfo.getNewName()+" already exist, can't update!");
			return retCode;
		}
		
		JobMeta jobMeta = CloudRepository.loadJobByName(jobInfo.getOwner() ,jobInfo.getName(),jobInfo.getGroup());
		if (jobMeta != null ) {
			String newName =  Const.NVL(jobInfo.getNewName(), jobMeta.getName());//(jobInfo.getNewName()) ? jobMeta.getName() : jobInfo.getNewName();
			jobMeta.setName(newName);
			jobMeta.setDescription(jobInfo.getDescription());
			
			if(!Utils.isEmpty(jobInfo.getNewGroup()) && !jobInfo.getNewGroup().equalsIgnoreCase(jobInfo.getGroup())) {
				//组改变 则修改组
				jobMeta.setRepositoryDirectory((RepositoryDirectoryInterface)CloudFileRepository.getInstance().findJobRepositoryInfo(jobInfo.getOwner() , null,jobInfo.getNewGroup()));
			}
			
			jobMeta.eraseParameters();
			if(jobInfo.getParams() != null && jobInfo.getParams().size() >0) {
				for(String key : jobInfo.getParams().keySet()) {
					if( Utils.isEmpty(key) || Utils.isEmpty(jobInfo.getParams().get(key)) ) {
						continue ;
					}
					jobMeta.addParameterDefinition(key,  jobInfo.getParams().get(key), "");
				}
			}
			
			CloudRepository.saveJob(jobMeta,jobInfo.getOwner() ,jobInfo.getGroup());

			// Rename history file & log file
			if (!newName.equals(jobInfo.getName())) {
				CloudExecHistory execHistory = CloudExecHistory.initExecHistory( jobInfo.getOwner() , jobInfo.getName(), CloudLogType.JOB_HISTORY);
				execHistory.renameHistory(newName);

				CloudExecLog log = CloudExecLog.initExecLog( CloudSession.getUserLogsRepositoryPath(), jobInfo.getName(), CloudLogType.JOB_LOG);
				log.renameExecLog(newName);
			}

			retCode.setRetCode(0);
			retCode.setMessage("Succeeded");
		} else {
			retCode.setRetCode(1);
			retCode.setMessage("Failed,name is mismatching.");
		}

		return retCode;
	}

	/* 
	 * 
	 */
	@Override
	public ReturnCodeDto newJob(JobInfoDto jobInfo) throws Exception {
		ReturnCodeDto retCode = new ReturnCodeDto();

		if( checkJobName(CloudSession.getLoginUser() ,jobInfo.getName()).getResult()){
			//已存在,不能更新
			retCode.setRetCode(9);
			retCode.setMessage("Failed, Name "+jobInfo.getName()+" already exist, can't create!");
			return retCode;
		}
		
		JobMeta jobMeta = null ;
		if(!Utils.isEmpty(jobInfo.getNewName())){
			//复制旧的转换
			try{
				JobMeta oldJobMeta = CloudRepository.loadJobByName(CloudSession.getLoginUser() ,jobInfo.getNewName(),jobInfo.getNewGroup()) ;
				if(oldJobMeta != null){
					jobMeta = (JobMeta) oldJobMeta.clone();
					jobMeta.setObjectId(null);
					jobMeta.setName(jobInfo.getName());
					jobMeta.setDescription(jobInfo.getDescription());
					
					if(Utils.isEmpty(jobInfo.getNewGroup())) {
						jobInfo.setNewGroup(oldJobMeta.getRepositoryDirectory().getName());
					}
					if(Utils.isEmpty(jobInfo.getGroup())) {
						jobInfo.setGroup(CloudRepository.DEFAULT_GROUP_NAME);
					}
					
					if( !jobInfo.getGroup().equalsIgnoreCase(jobInfo.getNewGroup())) {
						//组改变 则修改组
						jobMeta.setRepositoryDirectory((RepositoryDirectoryInterface)CloudFileRepository.getInstance().findJobRepositoryInfo(CloudSession.getLoginUser() , null,jobInfo.getGroup()));
					}else {
						jobMeta.setRepositoryDirectory(oldJobMeta.getRepositoryDirectory());
					}
					
					jobMeta.setRepository(CloudApp.getInstance().getRepository());
					jobMeta.setMetaStore(CloudApp.getInstance().getMetaStore(CloudSession.getLoginUser()));
					
					//原生JobHopMeta.clone()错误,需要更新
					jobMeta.getJobhops().clear();
					for(JobHopMeta hop : oldJobMeta.getJobhops()) {
						JobHopMeta hopMeta = new JobHopMeta();
						hopMeta.setFromEntry(jobMeta.findJobEntry(hop.getFromEntry().getName()));
						hopMeta.setToEntry(jobMeta.findJobEntry(hop.getToEntry().getName()));
						hopMeta.setEnabled(hop.isEnabled());
						hopMeta.setUnconditional(hop.isUnconditional());
						hopMeta.setEvaluation(hop.getEvaluation());
						jobMeta.addJobHop(hopMeta);
					}
				}
			}catch( KettleException e){
				//不存在,继续
			}			
		}
		if(jobMeta ==null ){
			jobMeta = CloudRepository.createJob(CloudSession.getLoginUser() ,jobInfo.getName(),jobInfo.getGroup());
			jobMeta.setDescription(jobInfo.getDescription());
		}
		
		if(jobInfo.getParams() != null && jobInfo.getParams().size() >0) {
			jobMeta.eraseParameters();
			for(String key : jobInfo.getParams().keySet()) {
				jobMeta.addParameterDefinition(key,  jobInfo.getParams().get(key), "");
			}
		}

		CloudRepository.saveJob(jobMeta,CloudSession.getLoginUser() ,jobInfo.getGroup());

		retCode.setRetCode(0);
		retCode.setMessage("Succeeded");

		return retCode;
	}

	@Override
	public ReturnCodeDto addDbJob(String filePath) throws Exception {
		ReturnCodeDto retCode = new ReturnCodeDto();

		if(CloudApp.getInstance().isDatabaseRepository() && !Utils.isEmpty(filePath)) {
			
			FileObject fo = KettleVFS.getFileObject(filePath);
			String group = fo.getParent().getName().getBaseName();
			fo.close();
			
			Repository repository = CloudApp.getInstance().getRepository();
			
			JobMeta jobMeta = new JobMeta(filePath, repository);
			jobMeta.setRepositoryDirectory((RepositoryDirectoryInterface)CloudFileRepository.getInstance().findJobRepositoryInfo(CloudSession.getLoginUser() , null, group));
			jobMeta.setRepository(repository);
			jobMeta.setMetaStore(CloudApp.getInstance().getMetaStore(CloudSession.getLoginUser()));
			CloudRepository.saveJob(jobMeta, null ,null);

			retCode.setRetCode(0);
			retCode.setMessage("Succeeded");

		}else {

			retCode.setRetCode(1);
			retCode.setMessage("Failed, Name "+filePath+" not exist, can't create!");

		}
		return retCode;
	}
	
	/* 
	 * 
	 */
	@Override
	public CheckResultDto checkJobName(String owner ,String name) throws Exception {
		if( Utils.isEmpty( owner )) {
    		owner = CloudSession.getResourceUser() ;
    	}
		CheckResultDto checkResult = new CheckResultDto();
		checkResult.setName(name);

		boolean res = false;
		res = CloudRepository.checkJobName(owner,name);

		checkResult.setResult(res);
		return checkResult;
	}

	/* 
	 * 
	 */
	@Override
	public ReturnCodeDto deleteJob(String owner ,String name,String group) throws Exception {
		if( Utils.isEmpty( owner )) {
    		owner = CloudSession.getResourceUser() ;
    	}
		// Delete history
		CloudExecHistory execHistory = CloudExecHistory.initExecHistory(owner, name, CloudLogType.JOB_HISTORY);
		execHistory.deleteHistory();

		// Delete Log
		CloudExecLog log = CloudExecLog.initExecLog(CloudSession.getUserLogsRepositoryPath(), name, CloudLogType.JOB_LOG);
		log.deleteExecLog();

		CloudRepository.dropJob(owner,name,group);
		return new ReturnCodeDto(0, "Succeeded");
	}
	

	@Override
	public ReturnCodeDto execBatchJob(JobBatchExecRequestDto execRequest) throws Exception {
		if(execRequest != null && execRequest.getConfiguration() != null && execRequest.getNames() != null &&  execRequest.getNames().size() >0 ) {
			String owner = execRequest.getOwner() ;
			for( int i=0;i<execRequest.getNames().size();i++ ) {
				String name = execRequest.getNames().get(i);
				String group = ( execRequest.getGroups()!= null && execRequest.getGroups().size() >i )? execRequest.getGroups().get(i) : null ;
				JobExecRequestDto teqnd=  new JobExecRequestDto();
				teqnd.setOwner(owner);
				teqnd.setName(name);
				teqnd.setGroup(group);
				teqnd.setConfiguration(execRequest.getConfiguration().clone());
				ReturnCodeDto res = execJob(teqnd);
				if(!res.isSuccess()) {
					CloudLogger.getInstance().warn("批量执行任务["+name+"]失败,"+res.getMessage());
				}
			}
		}
		return new ReturnCodeDto(0,"Succeeded");
	}


	public ReturnCodeDto execJob(JobExecRequestDto execRequest) throws Exception{
		return execJob(execRequest,CloudSession.getLoginUser());
	}
	
	/* 
	 * 
	 */
	@Override
	public ReturnCodeDto execJob(JobExecRequestDto execRequest,String execUser) throws Exception {
		ReturnCodeDto execResult = new ReturnCodeDto();

		ExecutionInfo executionInfo = CloudExecution.getInstance().getExecutionInfo(execRequest.getOwner(),execRequest.getName(),true);
		if (executionInfo != null && executionInfo.getJobExecutor() != null) {
			CloudJobExecutor jobExecutor = executionInfo.getJobExecutor() ;
			if ( jobExecutor.isFinished() ) {
				CloudExecution.getInstance().clearExecution(execRequest.getOwner(),execRequest.getName(),true);
			} else if( !CloudExecutorStatus.assertRunning(jobExecutor.getStatus())){
				execResult.setRetCode(3);
				execResult.setMessage("执行已经结束,但未停止完成,正在进行停止处理,请稍后再启动.");
				return execResult;
			}else {
				execResult.setRetCode(2);
				execResult.setMessage("正在执行中,请勿重复启动.");
				return execResult;
			}
		}
		
		ExecConfigurationDto jobExecConfigs = execRequest.getConfiguration();
		if (jobExecConfigs == null) {
			execResult.setRetCode(1);
			execResult.setMessage("Missing of execution configuration.");
			return execResult;
		}
		String engineType = jobExecConfigs.getEngineType();
		String engineName = jobExecConfigs.getEngineName();
		
		DefaultEngineMeta defaultEngine = defaultEngineService.findDefaultEngine(execRequest.getOwner(),engineName);
		if(defaultEngine ==  null) {
			execResult.setRetCode(1);
			execResult.setMessage("没有找到执行配置:("+engineType+") "+engineName);
			return execResult;
		}
		
		JobMeta jobMeta = CloudRepository.loadJobByName(execRequest.getOwner() ,execRequest.getName(),execRequest.getGroup());
		
		jobExecConfigs.addParam("engineType",engineType);
		jobExecConfigs.addParam("engineName",engineName);
		jobExecConfigs.addParam("rebootAutoRun",String.valueOf(jobExecConfigs.isRebootAutoRun()));
		jobExecConfigs.putParamsFromMeta(jobMeta);
		
		if(!Utils.isEmpty(execRequest.getExecId())) {
			//自动重启时保存旧的执行ID
			jobMeta.setVariable("idatrix.executionId", execRequest.getExecId());
		}

		JobExecutionConfiguration executionConfiguration = new JobExecutionConfiguration();
		executionConfiguration.setClearingLog(jobExecConfigs.isClearingLog());
		executionConfiguration.setSafeModeEnabled(jobExecConfigs.isSafeMode());
		executionConfiguration.setGatheringMetrics(jobExecConfigs.isGatherMetrics());
		executionConfiguration.setLogLevel(LogLevel.getLogLevelForCode(jobExecConfigs.getLogLevel()));
		
		executionConfiguration.setExecutingLocally(defaultEngine.isLocal());
		executionConfiguration.setExecutingRemotely(defaultEngine.isRemote());
		String remoteServer = defaultEngine.getServer();
		if(StringUtils.hasText(remoteServer) && defaultEngine.isRemote() ) {
			SlaveServer slaveServer = cloudServerService.findSlaveServer(execRequest.getOwner(),remoteServer);
			if(slaveServer == null  ) {
				//未找到执行服务器
				execResult.setRetCode(2);
				execResult.setMessage("没有找到执行引擎["+engineName+"]的远程服务器["+remoteServer+"] ");
				return execResult;
			}
			if(Utils.isEmpty( execRequest.getExecId()) ){
				//任务重启时执行ID不为空,只有普通执行时才检测状态
				try {
					slaveServer.getStatus();
				}catch( ConnectException ee) {
					//未找到执行服务器
					execResult.setRetCode(2);
					execResult.setMessage("执行引擎["+engineName+"]的远程服务器["+remoteServer+"]连接失败. ");
					return execResult;
				}
			}
			
			
			executionConfiguration.setRemoteServer(slaveServer);
		}
		//远程时发送资源
		executionConfiguration.setPassingExport(true);
		executionConfiguration.setStartCopyName(jobExecConfigs.getStartCopyName());
		executionConfiguration.setParams(jobExecConfigs.getParams());
		executionConfiguration.setVariables(jobExecConfigs.getVariables());
		executionConfiguration.setExpandingRemoteJob(false);
		
		CloudJobExecutor jobExecutor = CloudJobExecutor.initExecutor(executionConfiguration, jobMeta,execUser,execRequest.getOwner() );
		Thread tr = new Thread(jobExecutor, "JobExecutor_" + jobExecutor.getExecutionId()+ Utils.getThreadNameesSuffixByUser(execUser, execRequest.getOwner(), true));

		execResult.addMapData("executionId",jobExecutor.getExecutionId());
		execResult.setRetCode(0);
		execResult.setMessage("Started");
		//jobExecutor.setStatus(CloudExecutorStatus.RUNNING);

		tr.start();
		return execResult;
	}
	
	public ReturnCodeDto rebootJob(String owner ,String jobName, String group) throws Exception {
		if( Utils.isEmpty( owner )) {
    		owner = CloudSession.getResourceUser() ;
    	}
		ReturnCodeDto result = new ReturnCodeDto() ;
		result.setRetCode(0);
		
		if( !Utils.isEmpty(jobName) && jobName.contains("/")) {
			group = jobName.split("/", 2 )[0];
			jobName = jobName.split("/", 2 )[1];
		}
		ExecutionInfo executionInfo = CloudExecution.getInstance().getExecutionInfo(owner, jobName, true);
		if(executionInfo != null) {
			CloudJobExecutor jobExecutor = executionInfo.getJobExecutor();
			if( jobExecutor != null ) {
				//先停止trans
				jobExecutor.execStop();
				int i = 10;
				while (!jobExecutor.isFinished() && i != 0) {
					//等待5秒
					Thread.sleep(1000);
					i--;
				}
				if( i != 0 ) {
					//停止成功
					JobExecRequestDto execRequest = new JobExecRequestDto();
					execRequest.setName(jobName);
					execRequest.setGroup(group);
					execRequest.setConfiguration(jobExecutor.getConfigurationDto());
					return execJob(execRequest ) ;
				}else {
					result.setMessage("停止调度任务失败,请重试!");
					result.setRetCode(1);
					return result;
				}
			}
		}
		
		result.setMessage("调度任务没有启动,请先启动!");
		result.setRetCode(1);
		return result;
	}

	@Override
	public ReturnCodeDto execBatchStop(JobBatchStopDto stopNames) throws Exception {
		if(stopNames != null && stopNames.getJobNames()!= null && stopNames.getJobNames().size() >0 ) {
			String owner =  Const.NVL(stopNames.getOwner(),   CloudSession.getResourceUser() ) ;
			for( int i=0; i< stopNames.getJobNames().size();i++ ) {
				String name = stopNames.getJobNames().get(i);
				
				String executionId;
				if( !Utils.isEmpty(name) && name.contains("/")) {
					//group = name.split("/", 2 )[0];
					name = name.split("/", 2 )[1];
					ExecutionInfo executionInfo = CloudExecution.getInstance().getExecutionInfo(owner, name, true);
					if( executionInfo == null ) {
						//没有启动
						continue ;
					}
					 executionId = executionInfo.executionId ;
				}else {
					ExecutionInfo executionInfo = CloudExecution.getInstance().getExecutionInfo(owner, name, true);
					if( executionInfo != null ) {
						 executionId = executionInfo.executionId ;
					}else {
						executionId = name ;
					}
				}
				execStop(executionId);
			}
		}
		return new ReturnCodeDto(0,"Succeeded");
	}
	
	@Override
	public ReturnCodeDto execStop(String executionId) throws Exception {
		if(Utils.isEmpty(executionId)) {
			return new ReturnCodeDto(1, "executionId 不能为空!");
		}
		CloudJobExecutor jobExecutor = CloudExecution.getInstance().getJobExecutor(executionId);
		if (jobExecutor != null) {
			if (jobExecutor.execStop()) {
				jobExecutor.setStatus(CloudExecutorStatus.STOPPED.getType());
				return new ReturnCodeDto(0, "Succeeded");
			}else {
				return new ReturnCodeDto(1, "已触发停止,请等待...");
			}
		}
		return new ReturnCodeDto(1, "执行ID["+executionId+"]对应的执行器未找到");
	}

	/* 
	 * 
	 */
	@Override
	public List<JobExecEntryMeasureDto> getEntryMeasure(String executionId) throws Exception {
		CloudJobExecutor jobExecutor = CloudExecution.getInstance().getJobExecutor(executionId);

		if (jobExecutor != null) {
			return jobExecutor.getStepMeasure();
		}

		return new ArrayList<>();
	}

	/* 
	 * 
	 */
	@Override
	public List<JobExecEntryStatusDto> getEntryStatus(String executionId) throws Exception {
		CloudJobExecutor jobExecutor = CloudExecution.getInstance().getJobExecutor(executionId);

		if (jobExecutor != null) {
			return jobExecutor.getStepStatus();
		}else{
			return new ArrayList<>();
		}

	}

	/* 
	 * 
	 */
	@Override
	public JobExecLogDto getExecLog(String executionId) throws Exception {
		CloudJobExecutor jobExecutor = CloudExecution.getInstance().getJobExecutor(executionId);
		if (jobExecutor != null) {
			return jobExecutor.getExecLog();
		}

		return null;
	}

	/* 
	 * 
	 */
	@Override
	public JobExecIdDto getExecId(String owner ,String name) throws Exception {
		if( Utils.isEmpty( owner )) {
    		owner = CloudSession.getResourceUser() ;
    	}
		if( !Utils.isEmpty(name) && name.contains("/")) {
			//group = name.split("/", 2 )[0];
			name = name.split("/", 2 )[1];
		}
		
		String executionId = null ;
		ExecutionInfo executionInfo = CloudExecution.getInstance().getExecutionInfo(owner, name, true);
		if ( executionInfo ==  null){
			if( CloudExecutorStatus.assertRunning(jobCurrentStatus(owner , name)) &&  System.currentTimeMillis() <= ( CarteSingleton.getSlaveServerConfig().getSlaveServer().getChangedDate().getTime() + 3000000 )) {
				//任务正在重启
				throw new Exception("调度["+name+"]正在重启中,请稍后再查看.");
			}
		}else {
			executionId = executionInfo.executionId ;
		}
		JobExecIdDto execId = new JobExecIdDto();
		execId.setExecutionId(Const.NVL(executionId, ""));
		return execId;
	}

	/* 
	 * 
	 */
	@Override
	public JobExecStatusDto getExecStatus(String executionId) throws Exception {
		JobExecStatusDto status = new JobExecStatusDto();
		status.setStatus(CloudExecutorStatus.WAITING.getType());

		CloudJobExecutor jobExecutor = CloudExecution.getInstance().getJobExecutor(executionId);
		if (jobExecutor != null) {
			status.setStatus(jobExecutor.getStatus());
		}

		return status;
	}

	/* 
	 * 
	 */
	@Override
	public JobExecStatusDto getJobStatus(String owner , String jobName) throws Exception {
		if( Utils.isEmpty( owner )) {
    		owner = CloudSession.getResourceUser() ;
    	}
		if( !Utils.isEmpty(jobName) && jobName.contains("/")) {
			//group = name.split("/", 2 )[0];
			jobName = jobName.split("/", 2 )[1];
		}
		JobExecStatusDto status = new JobExecStatusDto();
		status.setStatus(jobCurrentStatus(owner , jobName));
		return status;
	}

	/*
	 * @see com.ys.idatrix.cloudetl.service.job.CloudJobService#getJobHistory(java.lang.String)
	 */
	@Override
	public ExecHistoryDto getJobHistorSegmenting(String owner ,String group,String name,String execId) throws Exception {
		if( Utils.isEmpty( owner )) {
    		owner = CloudSession.getResourceUser() ;
    	}
		if( !Utils.isEmpty(name) && name.contains("/")) {
			//group = name.split("/", 2 )[0];
			name = name.split("/", 2 )[1];
		}
		
		ExecHistoryDto history = new ExecHistoryDto();
		history.setRetCode("0");
		history.setMessage("");
		
		CloudExecHistory execHistory = CloudExecHistory.initExecHistory(owner, name, CloudLogType.JOB_HISTORY);
		history.setRecords(execHistory.getSegmentingParts(execId));
		
		return history;
	}
	
	/*
	 * @see com.ys.idatrix.cloudetl.service.job.CloudJobService#getJobHistory(java.lang.String)
	 */
	@Override
	public ExecHistoryDto getJobHistory(String owner ,String name) throws Exception {
		if( Utils.isEmpty( owner )) {
    		owner = CloudSession.getResourceUser() ;
    	}
		if( !Utils.isEmpty(name) && name.contains("/")) {
			//group = name.split("/", 2 )[0];
			name = name.split("/", 2 )[1];
		}
		
		ExecHistoryDto history = new ExecHistoryDto();
		history.setRetCode("0");
		history.setMessage("");
		
		CloudExecHistory execHistory = CloudExecHistory.initExecHistory(owner, name, CloudLogType.JOB_HISTORY);
		history.setRecords(execHistory.getExecRecords());
		
		return history;
	}

	/*
	 * @see com.ys.idatrix.cloudetl.service.job.CloudJobService#getJobLogs(java.lang.String)
	 */
	@Override
	public ExecLogsDto getJobLogs(String owner ,String name,String id ,String startDate ,String endDate) throws Exception {
		if( Utils.isEmpty( owner )) {
    		owner = CloudSession.getResourceUser() ;
    	}
		if( !Utils.isEmpty(name) && name.contains("/")) {
			//group = name.split("/", 2 )[0];
			name = name.split("/", 2 )[1];
		}
		
		ExecLogsDto logs = new ExecLogsDto();
		logs.setRetCode("0");
		logs.setMessage("");

		CloudExecLog log = CloudExecLog.initExecLog(CloudApp.getInstance().getUserLogsRepositoryPath(owner), name, CloudLogType.JOB_LOG);
		String logStr ="";
		if( Utils.isEmpty(id)) {
			logStr  = log.getExecLog(startDate) ;
		}else {
			logStr  = log.searchExecLog(id, startDate, endDate);
		}
		logs.setLogs(StringEscapeHelper.encode(logStr));

		return logs;
	}

}
