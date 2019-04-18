/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.quality.service.trans;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.commons.vfs2.FileObject;
import org.pentaho.di.cluster.ClusterSchema;
import org.pentaho.di.cluster.SlaveServer;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.LogLevel;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.core.vfs.KettleVFS;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.RepositoryDirectoryInterface;
import org.pentaho.di.repository.RepositoryElementMetaInterface;
import org.pentaho.di.repository.RepositoryObjectType;
import org.pentaho.di.trans.TransExecutionConfiguration;
import org.pentaho.di.trans.TransHopMeta;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.www.CarteSingleton;
import org.pentaho.pms.util.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.google.common.collect.Lists;
import com.ys.idatrix.quality.dto.TransDto;
import com.ys.idatrix.quality.dto.common.CheckResultDto;
import com.ys.idatrix.quality.dto.common.PaginationDto;
import com.ys.idatrix.quality.dto.common.ReturnCodeDto;
import com.ys.idatrix.quality.dto.engine.ExecConfigurationDto;
import com.ys.idatrix.quality.dto.history.ExecHistoryDto;
import com.ys.idatrix.quality.dto.history.ExecHistoryRecordDto;
import com.ys.idatrix.quality.dto.history.ExecLogsDto;
import com.ys.idatrix.quality.dto.hop.HopInfoDto;
import com.ys.idatrix.quality.dto.step.StepGuiDto;
import com.ys.idatrix.quality.dto.step.StepInfoDto;
import com.ys.idatrix.quality.dto.trans.TransBatchExecRequestDto;
import com.ys.idatrix.quality.dto.trans.TransBatchStopDto;
import com.ys.idatrix.quality.dto.trans.TransExecIdDto;
import com.ys.idatrix.quality.dto.trans.TransExecLogDto;
import com.ys.idatrix.quality.dto.trans.TransExecRequestNewDto;
import com.ys.idatrix.quality.dto.trans.TransExecStatusDto;
import com.ys.idatrix.quality.dto.trans.TransExecStepMeasureDto;
import com.ys.idatrix.quality.dto.trans.TransExecStepStatusDto;
import com.ys.idatrix.quality.dto.trans.TransInfoDto;
import com.ys.idatrix.quality.dto.trans.TransOverviewDto;
import com.ys.idatrix.quality.ext.CloudApp;
import com.ys.idatrix.quality.ext.CloudSession;
import com.ys.idatrix.quality.ext.executor.BaseTransExecutor;
import com.ys.idatrix.quality.ext.executor.CloudExecution;
import com.ys.idatrix.quality.ext.executor.CloudExecutorStatus;
import com.ys.idatrix.quality.ext.executor.CloudExecution.ExecutionInfo;
import com.ys.idatrix.quality.ext.executor.CloudTransExecutor;
import com.ys.idatrix.quality.ext.executor.logger.CloudExecHistory;
import com.ys.idatrix.quality.ext.executor.logger.CloudExecLog;
import com.ys.idatrix.quality.ext.executor.spark.TransEngineExecutor;
import com.ys.idatrix.quality.ext.utils.StringEscapeHelper;
import com.ys.idatrix.quality.logger.CloudLogConst;
import com.ys.idatrix.quality.logger.CloudLogType;
import com.ys.idatrix.quality.logger.CloudLogger;
import com.ys.idatrix.quality.recovery.trans.ResumeTransParser;
import com.ys.idatrix.quality.reference.spark.SparkEngineCaller;
import com.ys.idatrix.quality.repository.CloudRepository;
import com.ys.idatrix.quality.repository.database.dto.FileRepositoryDto;
import com.ys.idatrix.quality.repository.xml.CloudFileRepository;
import com.ys.idatrix.quality.repository.xml.metastore.meta.DefaultEngineMeta;
import com.ys.idatrix.quality.repository.xml.metastore.meta.SparkEngineMeta;
import com.ys.idatrix.quality.service.CloudBaseService;
import com.ys.idatrix.quality.service.cluster.CloudClusterService;
import com.ys.idatrix.quality.service.engine.CloudDefaultEngineService;
import com.ys.idatrix.quality.service.engine.CloudSparkEngineService;
import com.ys.idatrix.quality.service.server.CloudServerService;
import mondrian.olap.Util;

/**
 * Transformation service implementation.
 * 
 * @author JW
 * @since 2017年5月24日
 *
 */
@Service
public class CloudTransServiceImpl extends CloudBaseService implements CloudTransService {

	@Autowired(required = false)
	private SparkEngineCaller sparkEngineCaller;

	@Autowired
	private CloudDefaultEngineService defaultEngineService;
	@Autowired
	private CloudSparkEngineService sparkEngineService;
	@Autowired
	private CloudServerService cloudServerService;
	@Autowired
	private CloudClusterService cloudClusterService;
	
	/**
	 * Get current status of the transformation.
	 * 
	 * @param transName
	 * @return
	 * @throws Exception 
	 */
	private String transCurrentStatus(String owner ,String transName) throws Exception {
		TransDto jtrans = new TransDto();
		jtrans.setName(transName);
		jtrans.setOwner(owner);
		transCurrentStatusAndTime(jtrans);

		return jtrans.getStatus();
	}

	/**
	 * 获取  trans 的 status , execTime , lastExecTime 
	 * 
	 * @param transName
	 * @return
	 * @throws Exception 
	 */
	private void transCurrentStatusAndTime(TransDto jtrans) throws Exception {
		
		String transName = jtrans.getName() ;
		
		Date begin = null;
		Date end = null;
		String currentStatus = null ;
				
		// Calculate execution time based on executor
		 ExecutionInfo executionInfo = CloudExecution.getInstance().getExecutionInfo(jtrans.getOwner(), transName, false);
		if (executionInfo != null) {
			BaseTransExecutor transExecutor = executionInfo.getTransExecutor();
			if (transExecutor != null) {
				currentStatus = transExecutor.getStatus();
				begin = transExecutor.getBeginDate();
				end = transExecutor.getEndDate();
			}
		}

		// Calculate execution time based on history
		if ( Utils.isEmpty(currentStatus) || begin == null ) {
			CloudExecHistory execHistory = CloudExecHistory.initExecHistory(jtrans.getOwner(), transName, CloudLogType.TRANS_HISTORY);
			ExecHistoryRecordDto record = execHistory.getLastExecRecord();
			if (record != null ) {
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
						} catch (Exception e) {
						}
					}
				}
			}
		}

		jtrans.setStatus(CloudExecutorStatus.correctStatusType(Const.NVL(currentStatus, CloudExecutorStatus.WAITING.getType())));
		jtrans.setExecTime(DurationFormatUtils.formatPeriod(((begin != null) ? begin : new Date()).getTime(), ((end != null) ? end : new Date()).getTime(), CloudLogConst.EXEC_DURATION_PATTERN) );
		if(begin != null ) {
			jtrans.setLastExecTime( DateFormatUtils.format(begin, CloudLogConst.EXEC_TIME_PATTERN));
		}
	}
	
	@Override
	public List<String> getCloudTransGroupList(String owner ) throws Exception {
		if( Utils.isEmpty( owner )) {
    		owner = CloudSession.getResourceUser() ;
    	}
		return CloudRepository.getTransGroups(owner);
	}
	
	@Override
	public List<String> getCloudTransUserList( ) throws Exception {
		return CloudRepository.getCurrentRenterUsers(RepositoryObjectType.TRANSFORMATION);
	}
	
	@Override
	public String getCloudTransGroup(String owner ,String transName,String... priorityGroups) throws Exception {
		if( Utils.isEmpty( owner )) {
    		owner = CloudSession.getResourceUser() ;
    	}
		return CloudRepository.getTransGroup(owner,transName,priorityGroups);
	}

	@Override
	public Map<String,PaginationDto<TransDto>> getCloudTransList(String owner , String group,boolean isMap, int page, int pageSize, String search)
			throws Exception {
		//直接数据库查询方式
		Map<String, PaginationDto<FileRepositoryDto>> databaseMap = CloudRepository.getTransElementsMap(owner, group, page, pageSize, search,isMap);
		if( databaseMap != null ) {
			return databaseMap.entrySet().stream().collect(Collectors.toMap(entry -> { return entry.getKey(); } , entry -> { 
				PaginationDto<TransDto> res = new PaginationDto<TransDto>(page, pageSize,search);
				res.transformPagination(entry.getValue(), new PaginationDto.DealRowsInterface<TransDto>() {
					@Override
					public TransDto dealRow(Object obj, Object... params) throws Exception {
						FileRepositoryDto transEleMeta = (FileRepositoryDto) obj;
						TransDto jtrans = new TransDto();
						jtrans.setOwner(transEleMeta.getOwner());
						jtrans.setName(transEleMeta.getName());
						jtrans.setGroup(transEleMeta.getGroup());
						jtrans.setDescription(transEleMeta.getDescription());
						jtrans.setModifiedTime(DateFormatUtils.format(transEleMeta.getUpdateTime(), CloudLogConst.EXEC_TIME_PATTERN));

						transCurrentStatusAndTime(jtrans);
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
		Map<String, List<Object>> transMetas = CloudRepository.getTransElementsMap(owner ,group);
		return getPaginationMaps(isMap, page, pageSize, search, transMetas, new PaginationDto.DealRowsInterface<TransDto>() {

			@Override
			public TransDto dealRow(Object obj, Object... params) throws Exception {
				String eleOwner = params != null && params.length > 0 ? (String) params[0]: CloudSession.getResourceUser();
				
				TransDto jtrans = new TransDto();
				if(obj instanceof RepositoryElementMetaInterface){
					RepositoryElementMetaInterface transEleMeta = (RepositoryElementMetaInterface) obj;
					String transName = transEleMeta.getName();
					
					jtrans.setOwner(eleOwner);
					jtrans.setName(transName);
					jtrans.setGroup(transEleMeta.getRepositoryDirectory().getName());
					jtrans.setDescription(transEleMeta.getDescription());
					jtrans.setModifiedTime(transEleMeta.getModifiedDate() != null? DateFormatUtils.format(transEleMeta.getModifiedDate(), CloudLogConst.EXEC_TIME_PATTERN): "");

					transCurrentStatusAndTime(jtrans);
				}else if(obj instanceof FileRepositoryDto){
					FileRepositoryDto transEleMeta = (FileRepositoryDto) obj;
					
					jtrans.setOwner(eleOwner);
					jtrans.setName(transEleMeta.getName());
					jtrans.setGroup(transEleMeta.getGroup());
					jtrans.setDescription(transEleMeta.getDescription());
					jtrans.setModifiedTime(DateFormatUtils.format(transEleMeta.getUpdateTime(), CloudLogConst.EXEC_TIME_PATTERN));

					transCurrentStatusAndTime(jtrans);
				}
				return jtrans;
			}

			@Override
			public boolean match(Object obj, String search, Object... params) throws Exception {
				String eleOwner = params != null && params.length > 0 ? (String) params[0] : CloudSession.getResourceUser();
				String transName = "";
				if(obj instanceof RepositoryElementMetaInterface){
					RepositoryElementMetaInterface transEleMeta = (RepositoryElementMetaInterface) obj;
					transName = transEleMeta.getName();
				}else if(obj instanceof FileRepositoryDto){
					FileRepositoryDto transEleMeta = (FileRepositoryDto) obj;
					transName = transEleMeta.getName();
				}

				if (search != null && search.contains("::")) {
					String type = search.split("::", 2)[0];
					String value = search.split("::", 2)[1];
					boolean typeRet = true;
					if (!Utils.isEmpty(type)) {

						String status = CloudExecutorStatus.WAITING.getType();
						ExecutionInfo executionInfo = CloudExecution.getInstance().getExecutionInfo(eleOwner, transName,false);
						if (executionInfo != null) {
							BaseTransExecutor transExecutor = executionInfo.getTransExecutor();
							if (transExecutor != null) {
								status = transExecutor.getStatus();
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
						valueRet = defaultMatch(transName, value); // transName!=null&&transName.toLowerCase().contains(value.toLowerCase());
					}
					return typeRet && valueRet;
				}

				return defaultMatch(transName, search);
			}

		});
		
	}

	/*
	 * Get transformation list. (TODO: by user's repository)
	 */
	@Override
	public Map<String,List<String>> getCloudTransNameList(String owner ,String group) throws Exception {
		return CloudRepository.getTransNameMap(owner,group);
	}

	/*
	 * Load transformation data by name.
	 */
	@Override
	public TransOverviewDto loadCloudTrans(String owner ,String transName, String group) throws Exception {
		if( Utils.isEmpty( owner )) {
    		owner = CloudSession.getResourceUser() ;
    	}
		TransOverviewDto overview = new TransOverviewDto();

		TransMeta transMeta = CloudRepository.loadTransByName(owner,transName, group);
		TransInfoDto transInfo = new TransInfoDto();
		transInfo.setOwner(owner);
		transInfo.setName(transMeta.getName());
		transInfo.setGroup(transMeta.getRepositoryDirectory().getName());
		transInfo.setDescription(transMeta.getDescription());
		String[] paramsKeys = transMeta.listParameters();
		if (paramsKeys != null && paramsKeys.length > 0) {
			for (String key : paramsKeys) {
				String value = transMeta.getParameterDefault(key);
				transInfo.addParams(key, value);
			}
		}
		overview.setInfo(transInfo);

		List<StepInfoDto> stepList = new ArrayList<>();
		List<StepMeta> steps = transMeta.getSteps();
		for (StepMeta step : steps) {
			StepInfoDto stepInfo = new StepInfoDto();
			stepInfo.setName(step.getName());
			//TODO 硬编码替换
			stepInfo.setType("ElasticSearchBulk5".equals(step.getTypeId()) ? "ElasticSearchBulk": step.getTypeId()  );
			stepInfo.setDescription(step.getDescription());
			stepInfo.setSupportsErrorHandling(step.supportsErrorHandling());
			stepInfo.setDistributes(step.isDistributes());

			if (step.getClusterSchema() != null) {
				stepInfo.setClusterSchema(step.getClusterSchema().getName());
			} else {
				stepInfo.setClusterSchema("");
			}

			StepGuiDto stepGui = new StepGuiDto();
			stepGui.setDraw(step.isDrawn() ? "Y" : "N");
			stepGui.setXloc(step.getLocation().x);
			stepGui.setYloc(step.getLocation().y);
			stepInfo.setGui(stepGui);
			stepList.add(stepInfo);
		}
		overview.setStepList(stepList);

		List<HopInfoDto> hopList = new ArrayList<>();
		int hopsNr = transMeta.nrTransHops();
		for (int i = 0; i < hopsNr; i++) {
			TransHopMeta hopMeta = transMeta.getTransHop(i);
			HopInfoDto hopInfo = new HopInfoDto();
			hopInfo.setEnabled(hopMeta.isEnabled());
			hopInfo.setFrom(hopMeta.getFromStep().getName());
			hopInfo.setTo(hopMeta.getToStep().getName());

			// 增加错误处理步骤
			if (hopMeta.getFromStep().supportsErrorHandling() && hopMeta.getFromStep().getStepErrorMeta() != null
					&& hopMeta.getFromStep().getStepErrorMeta().isEnabled()
					&& hopMeta.getFromStep().getStepErrorMeta().getTargetStep() != null && hopMeta.getToStep().getName()
							.equals(hopMeta.getFromStep().getStepErrorMeta().getTargetStep().getName())) {
				// 此hop 是错误处理hop
				hopInfo.setUnconditional(false);
			}

			hopList.add(hopInfo);
		}
		overview.setHopList(hopList);
		return overview;
	}

	/*
	 * Edit transformation attributes.
	 */
	@Override
	public TransInfoDto editTransAttributes(String owner ,String transName, String group) throws Exception {
		if( Utils.isEmpty( owner )) {
    		owner = CloudSession.getResourceUser() ;
    	}
		TransInfoDto transInfo = new TransInfoDto();

		TransMeta transMeta = CloudRepository.loadTransByName(owner,transName, group);
		transInfo.setOwner(owner);
		transInfo.setName(transMeta.getName());
		transInfo.setGroup(transMeta.getRepositoryDirectory().getName());
		transInfo.setNewName("");
		transInfo.setDescription(transMeta.getDescription());

		String[] paramsKeys = transMeta.listParameters();
		if (paramsKeys != null && paramsKeys.length > 0) {
			for (String key : paramsKeys) {
				String value = transMeta.getParameterDefault(key);
				transInfo.addParams(key, value);
			}
		}

		return transInfo;
	}

	/*
	 * Save transformation attributes.
	 */
	@Override
	public ReturnCodeDto saveTransAttributes(TransInfoDto transInfo) throws Exception {
		TransMeta transMeta = null;
		String owner = transInfo.getOwner() ;
		String transName = transInfo.getName();
		String newName = transInfo.getNewName();

		String group = transInfo.getGroup();
		String newGroup = transInfo.getNewGroup();

		ReturnCodeDto retCode = new ReturnCodeDto();
		if (!StringUtils.isEmpty(newName) && checkTransName(owner, newName).getResult()) {
			// 已存在,不能更新
			retCode.setRetCode(1);
			retCode.setMessage("Failed, NewName " + newName + " already exist, can't update!");
			return retCode;
		}

		transMeta = CloudRepository.loadTransByName(owner, transName, group);
		if (transMeta != null && transName.equals(transMeta.getName())) {
			newName = Const.NVL(newName, transName); // StringUtils.isEmpty(newName) ? transName : newName;
			transMeta.setName(newName);
			transMeta.setDescription(transInfo.getDescription());

			if (!Utils.isEmpty(newGroup) && !newGroup.equalsIgnoreCase(group)) {
				// 组改变 则修改组
				transMeta.setRepositoryDirectory( (RepositoryDirectoryInterface) CloudFileRepository.getInstance().findTransRepositoryInfo(owner, null, newGroup));
			}

			transMeta.eraseParameters();
			if (transInfo.getParams() != null && transInfo.getParams().size() > 0) {
				for (String key : transInfo.getParams().keySet()) {
					if( Utils.isEmpty(key) || Utils.isEmpty(transInfo.getParams().get(key)) ) {
						continue ;
					}
					transMeta.addParameterDefinition(key, transInfo.getParams().get(key), "");
				}
			}

			CloudRepository.saveTrans(transMeta,owner,group);

			// Rename history file & log file
			if (!newName.equals(transName)) {
				CloudExecHistory execHistory = CloudExecHistory.initExecHistory(owner, transName, CloudLogType.TRANS_HISTORY);
				execHistory.renameHistory(newName);

				CloudExecLog log = CloudExecLog.initExecLog(CloudApp.getInstance().getUserLogsRepositoryPath(owner), transName, CloudLogType.TRANS_LOG);
				log.renameExecLog(newName);
				
			}

			retCode.setRetCode(0);
			retCode.setMessage("Succeeded");
		} else {
			retCode.setRetCode(1);
			retCode.setMessage("Failed,找不到 transformation["+transName+"]");
		}

		return retCode;
	}

	/*
	 * Create a new transformation.
	 */
	@Override
	public ReturnCodeDto newTrans(TransInfoDto transInfo) throws Exception {
		ReturnCodeDto retCode = new ReturnCodeDto();

		if (checkTransName(CloudSession.getLoginUser(),transInfo.getName()).getResult()) {
			// 已存在,不能更新
			retCode.setRetCode(9);
			retCode.setMessage("Failed, Name " + transInfo.getName() + " already exist, can't create!");
			return retCode;
		}

		TransMeta transMeta = null;
		if (!Utils.isEmpty(transInfo.getNewName())) {
			// 复制旧的转换
			try {
				TransMeta oldTransMeta = CloudRepository.loadTransByName(CloudSession.getLoginUser(),transInfo.getNewName(), transInfo.getNewGroup());
				if (oldTransMeta != null) {
					transMeta = (TransMeta) oldTransMeta.clone();
					transMeta.setName(transInfo.getName());
					transMeta.setDescription(transInfo.getDescription());
					
					if(Utils.isEmpty(transInfo.getNewGroup())) {
						transInfo.setNewGroup(oldTransMeta.getRepositoryDirectory().getName());
					}
					if(Utils.isEmpty(transInfo.getGroup())) {
						transInfo.setGroup(CloudRepository.DEFAULT_GROUP_NAME);
					}

					if (!transInfo.getNewGroup().equalsIgnoreCase(transInfo.getGroup())) {
						// 组改变 则修改组
						transMeta.setRepositoryDirectory((RepositoryDirectoryInterface) CloudFileRepository.getInstance().findTransRepositoryInfo(CloudSession.getLoginUser(),null, transInfo.getGroup()));
					} else {
						transMeta.setRepositoryDirectory(oldTransMeta.getRepositoryDirectory());
					}

					transMeta.setRepository(CloudApp.getInstance().getRepository());
					transMeta.setMetaStore(CloudApp.getInstance().getMetaStore(CloudSession.getLoginUser()));

					// 原生TransHopMeta.clone()错误,需要更新
					int size = transMeta.nrTransHops();
					for (int i = 0; i < size; i++) {
						// transMeta.removeTransHop(i);
						TransHopMeta hop = oldTransMeta.getTransHop(i);

						TransHopMeta hopMeta = new TransHopMeta();
						hopMeta.setFromStep(transMeta.findStep(hop.getFromStep().getName()));
						hopMeta.setToStep(transMeta.findStep(hop.getToStep().getName()));
						hopMeta.setEnabled(hop.isEnabled());
						transMeta.setTransHop(i, hopMeta);
					}

				}
			} catch (KettleException e) {
				// 不存在,继续
			}
		}
		if (transMeta == null) {
			// 新建一个新的转换
			transMeta = CloudRepository.createTrans(CloudSession.getLoginUser(),transInfo.getName(), transInfo.getGroup());
			transMeta.setDescription(transInfo.getDescription());
		}

		if (transInfo.getParams() != null && transInfo.getParams().size() > 0) {
			transMeta.eraseParameters();
			for (String key : transInfo.getParams().keySet()) {
				transMeta.addParameterDefinition(key, transInfo.getParams().get(key), "");
			}
		}

		//保存
		CloudRepository.saveTrans(transMeta,CloudSession.getLoginUser(),transInfo.getGroup());
		
		retCode.setRetCode(0);
		retCode.setMessage("Succeeded");

		return retCode;
	}

	@Override
	public ReturnCodeDto addDBTrans(String filePath) throws Exception {
		ReturnCodeDto retCode = new ReturnCodeDto();

		if (CloudApp.getInstance().isDatabaseRepository() && !Utils.isEmpty(filePath)) {

			FileObject fo = KettleVFS.getFileObject(filePath);
			String group = fo.getParent().getName().getBaseName();
			fo.close();

			Repository repository = CloudApp.getInstance().getRepository();

			TransMeta transMeta = new TransMeta(filePath, repository);
			transMeta.setRepositoryDirectory(
					(RepositoryDirectoryInterface) CloudFileRepository.getInstance().findTransRepositoryInfo(CloudSession.getLoginUser(),null, group));
			transMeta.setRepository(repository);
			transMeta.setMetaStore(CloudApp.getInstance().getMetaStore(CloudSession.getLoginUser()));

			CloudRepository.saveTrans(transMeta, null ,null);

			retCode.setRetCode(0);
			retCode.setMessage("Succeeded");
		} else {
			retCode.setRetCode(1);
			retCode.setMessage("Failed, Name " + filePath + " not exist, can't create!");
		}

		return retCode;
	}

	/* 
	 * 
	 */
	@Override
	public CheckResultDto checkTransName(String owner ,String transName) throws Exception {
		if( Utils.isEmpty( owner )) {
    		owner = CloudSession.getResourceUser() ;
    	}
		CheckResultDto checkResult = new CheckResultDto();
		checkResult.setName(transName);

		boolean res = false;
		res = CloudRepository.checkTransName(owner,transName);

		checkResult.setResult(res);
		return checkResult;
	}

	/* 
	 * 
	 */
	@Override
	public ReturnCodeDto deleteTrans(String owner ,String transName, String group) throws Exception {
		if( Utils.isEmpty( owner )) {
    		owner = CloudSession.getResourceUser() ;
    	}
		// Delete history
		CloudExecHistory execHistory = CloudExecHistory.initExecHistory(owner, transName,CloudLogType.TRANS_HISTORY);
		execHistory.deleteHistory();

		// Delete Log
		CloudExecLog log = CloudExecLog.initExecLog(CloudApp.getInstance().getUserLogsRepositoryPath(owner), transName, CloudLogType.TRANS_LOG);
		log.deleteExecLog();

		CloudRepository.dropTrans(owner,transName, group);
		return new ReturnCodeDto(0, "Succeeded");

	}


	@Override
	public ReturnCodeDto execBatchTrans(TransBatchExecRequestDto execRequest) throws Exception {
		if (execRequest != null && execRequest.getConfiguration() != null && execRequest.getNames() != null
				&& execRequest.getNames().size() > 0) {
			String owner =  execRequest.getOwner() ;
			for( int i=0;i<execRequest.getNames().size();i++ ) {
				String name = execRequest.getNames().get(i);
				String group = ( execRequest.getGroups()!= null && execRequest.getGroups().size() >i )? execRequest.getGroups().get(i) : null ;
				TransExecRequestNewDto teqnd = new TransExecRequestNewDto();
				teqnd.setOwner(owner);
				teqnd.setName(name);
				teqnd.setGroup(group);
				teqnd.setConfiguration(execRequest.getConfiguration().clone());
				ReturnCodeDto res = execTransNew(teqnd);
				if(!res.isSuccess()) {
					CloudLogger.getInstance().warn("批量执行转换["+name+"]失败,"+res.getMessage());
				}
			}
		}
		return new ReturnCodeDto(0, "Succeeded");
	}

	@Override
	public ReturnCodeDto execTransNew(TransExecRequestNewDto execRequest) throws Exception {
		return execTransNew(execRequest, CloudSession.getLoginUser());
	}

	@Override
	public ReturnCodeDto execTransNew(TransExecRequestNewDto execRequest, String execUser) throws Exception {
		ReturnCodeDto execResult = new ReturnCodeDto();

		 ExecutionInfo executionInfo = CloudExecution.getInstance().getExecutionInfo(execRequest.getOwner(), execRequest.getName(), false);
		if ( executionInfo != null && executionInfo.getTransExecutor() != null) {
			BaseTransExecutor transExecutor = executionInfo.getTransExecutor();
			if ( transExecutor.isFinished() ) {
				CloudExecution.getInstance().clearExecution(execRequest.getOwner(), execRequest.getName(), false);
			}else if( !CloudExecutorStatus.assertRunning(transExecutor.getStatus()) ){
				execResult.setRetCode(3);
				execResult.setMessage("执行已经结束,但未停止完成,正在进行停止处理,请稍后再启动.");
				return execResult;
			}else {
				execResult.setRetCode(2);
				execResult.setMessage("正在执行中,请勿重复启动.");
				return execResult;
			}
		}

		ExecConfigurationDto configuration = execRequest.getConfiguration();
		if (configuration == null) {
			execResult.setRetCode(1);
			execResult.setMessage("Missing of execution configuration.");
			return execResult;
		}

		String engineType = configuration.getEngineType();
		String engineName = configuration.getEngineName();

		TransMeta transMeta = CloudRepository.loadTransByName(execRequest.getOwner(),execRequest.getName(), execRequest.getGroup());
		
		configuration.addParam("engineType", engineType);
		configuration.addParam("engineName", engineName);
		configuration.addParam("rebootAutoRun", String.valueOf(configuration.isRebootAutoRun()));
		configuration.addParam(ResumeTransParser.BREAKPOINT_CONTINUE_ENABLE, String.valueOf(configuration.isBreakpointsContinue()));
		configuration.addParam(ResumeTransParser.BREAKPOINTS_REMOTE, String.valueOf(configuration.isBreakpointsRemote()));
		configuration.addParam(ResumeTransParser.BREAKPOINTS_FORCELOCAL, String.valueOf(configuration.isForceLocal()));
		configuration.putParamsFromMeta(transMeta);
		
		if(!Utils.isEmpty(execRequest.getExecId())) {
			//自动重启时保存旧的执行ID
			transMeta.setVariable("idatrix.executionId", execRequest.getExecId());
		}

		TransExecutionConfiguration executionConfiguration = new TransExecutionConfiguration();
		executionConfiguration.setClearingLog(configuration.isClearingLog());
		executionConfiguration.setGatheringMetrics(configuration.isGatherMetrics());
		executionConfiguration.setSafeModeEnabled(configuration.isSafeMode());
		executionConfiguration.setLogLevel(LogLevel.getLogLevelForCode(configuration.getLogLevel()));
		executionConfiguration.setParams(configuration.getParams());
		executionConfiguration.setVariables(configuration.getVariables());

		BaseTransExecutor transExecutor = null;
		Thread tr = null;

		switch (engineType) {
		case "default":
			if (Util.isEmpty(engineType) || Util.isEmpty(engineName)) {
				execResult.setRetCode(1);
				execResult.setMessage("执行配置信息错误.engineType:" + engineType + " engineName:" + engineName);
				return execResult;
			}
			// executing on default kettle engine
			DefaultEngineMeta defaultEngine = defaultEngineService.findDefaultEngine(execRequest.getOwner(),engineName);
			if (defaultEngine == null) {
				execResult.setRetCode(1);
				execResult.setMessage("没有找到执行配置:(" + engineType + ") " + engineName);
				return execResult;
			}

			executionConfiguration.setExecutingLocally(defaultEngine.isLocal());

			executionConfiguration.setExecutingRemotely(defaultEngine.isRemote());
			String remoteServer = defaultEngine.getServer();
			if (StringUtils.hasText(remoteServer)  && defaultEngine.isRemote() ) {
				SlaveServer slaveServer = cloudServerService.findSlaveServer(execRequest.getOwner(),remoteServer);
				if(slaveServer == null ) {
					//未找到执行服务器
					execResult.setRetCode(2);
					execResult.setMessage("没有找到执行引擎["+engineName+"]的远程服务器["+remoteServer+"] ");
					return execResult;
				}
				if( Utils.isEmpty(execRequest.getExecId())) {
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

			executionConfiguration.setExecutingClustered(defaultEngine.isClustered());
			executionConfiguration.setClusterShowingTransformation(defaultEngine.isShowTransformations());
			if (defaultEngine.isClustered()) {
				// 更新集群服务信息
				String[] clustersNames = transMeta.getClusterSchemaNames();
				if (clustersNames == null || clustersNames.length == 0) {
					execResult.setRetCode(1);
					execResult.setMessage("转换没有设置集群信息.");
					return execResult;
				}
				for (String clustername : clustersNames) {
					ClusterSchema schema = cloudClusterService.findClusterSchema(execRequest.getOwner(),clustername);
					if (schema != null) {
						transMeta.addOrReplaceClusterSchema(schema);
					}
					// Add or replace the slave server in transMeta
					List<SlaveServer> servers = schema.getSlaveServers();
					if (servers != null) {
						for (SlaveServer server : servers) {
							transMeta.addOrReplaceSlaveServer(server);
						}
					}
				}

			}

			executionConfiguration.setLogRemoteExecutionLocally(defaultEngine.isLogRemoteExecutionLocally());
			executionConfiguration.setRunConfiguration(defaultEngine.getName()); // Set run configuration

			transExecutor = CloudTransExecutor.initExecutor(transMeta, executionConfiguration, execRequest.getDebugExecDtos(), execUser,execRequest.getOwner());
			tr = new Thread((CloudTransExecutor) transExecutor, "TransExecutor_" + transExecutor.getExecutionId() + Utils.getThreadNameesSuffixByUser(execUser, execRequest.getOwner(), true));
			tr.start();
			break;
		case "spark":
			// Executing on spark engine
			SparkEngineMeta sparkEngine = sparkEngineService.findSparkEngine(execRequest.getOwner(),engineName);
			executionConfiguration.setRunConfiguration(sparkEngine.getName()); // Set run configuration

			transExecutor = CloudTransExecutor.initExecutor(transMeta, executionConfiguration,  null, execUser,execRequest.getOwner());
			tr = new Thread((CloudTransExecutor) transExecutor, "TransExecutor_" + transExecutor.getExecutionId() + Utils.getThreadNameesSuffixByUser(execUser, execRequest.getOwner(), true));
			tr.start();

			break;
		case "idatrix":
			// Executing on iDatrix trans-engine
			executionConfiguration.setRunConfiguration(CloudLogConst.DEFAULT_RUN_CONFIGURATION); // Set run
																									// configuration
			transExecutor = TransEngineExecutor.initExecutor(transMeta, executionConfiguration, sparkEngineCaller, execUser);
			tr = new Thread((TransEngineExecutor) transExecutor, "TransExecutor_" + transExecutor.getExecutionId() + Utils.getThreadNameesSuffixByUser(execUser, execRequest.getOwner(), true));
			tr.start();

			break;
		}

		execResult.addMapData("executionId",transExecutor.getExecutionId());
		execResult.setRetCode(0);
		execResult.setMessage("Started");
		// transExecutor.setStatus(CloudExecutorStatus.RUNNING);

		return execResult;
	}
	
	@Override
	public ReturnCodeDto rebootTrans(String owner ,String transName, String group) throws Exception {
		if( Utils.isEmpty( owner )) {
    		owner = CloudSession.getResourceUser() ;
    	}
		ReturnCodeDto result = new ReturnCodeDto() ;
		result.setRetCode(0);
		
		if( !Utils.isEmpty(transName) && transName.contains("/")) {
			group = transName.split("/", 2 )[0];
			transName = transName.split("/", 2 )[1];
		}
		 ExecutionInfo executionInfo = CloudExecution.getInstance().getExecutionInfo(owner, transName, false);
		if(executionInfo != null ) {
			BaseTransExecutor transExecutor = executionInfo.getTransExecutor();
			if( transExecutor != null ) {
				//先停止trans
				transExecutor.execStop();
				int i = 10;
				while ( !transExecutor.isFinished() && i != 0) {
					//等待5秒
					Thread.sleep(1000);
					i--;
				}
				if( i != 0 ) {
					//停止成功
					TransExecRequestNewDto execRequest = new TransExecRequestNewDto();
					execRequest.setOwner(owner);
					execRequest.setName(transName);
					execRequest.setGroup(group);
					execRequest.setConfiguration(transExecutor.getConfigurationDto());
					return execTransNew(execRequest ) ;
				}else {
					result.setMessage("停止转换失败,请重试!");
					result.setRetCode(1);
					return result;
				}
			}
		}
		
		result.setMessage("转换没有启动,请先启动!");
		result.setRetCode(1);
		return result;
	}

	@Override
	public ReturnCodeDto execPause(String executionId) throws Exception {
		BaseTransExecutor transExecutor = CloudExecution.getInstance().getTransExecutor(executionId);

		/*
		 * if(transExecutor.isFinished()) { clearExecution(executionId); return new
		 * ReturnCodeDto(1, "Failed"); // Already finished! }
		 */

		if (transExecutor != null) {
			if (transExecutor.execPause()) {
				return new ReturnCodeDto(0, "Succeeded");
			}else {
				return new ReturnCodeDto(1, "已触发暂停,但暂停失败.");
			}
		}

		return new ReturnCodeDto(1, "执行ID["+executionId+"]对应的执行器未找到");
	}

	@Override
	public ReturnCodeDto execBatchStop(TransBatchStopDto transNames)throws Exception{
		if(transNames != null && transNames.getTransNames()!= null && transNames.getTransNames().size() >0 ) {
			String owner =  Const.NVL(transNames.getOwner(), CloudSession.getResourceUser() ) ;
			for( int i=0; i< transNames.getTransNames().size();i++ ) {
				String name = transNames.getTransNames().get(i);
				
				String executionId;
				if( !Utils.isEmpty(name) && name.contains("/")) {
					//group = name.split("/", 2 )[0];
					name = name.split("/", 2 )[1];
					ExecutionInfo executionInfo = CloudExecution.getInstance().getExecutionInfo(owner, name, false) ;
					if( executionInfo == null ) {
						//没有启动
						continue ;
					}
					 executionId = executionInfo.executionId ;
				}else {
					
					ExecutionInfo executionInfo = CloudExecution.getInstance().getExecutionInfo(owner, name, false);
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
		BaseTransExecutor transExecutor = CloudExecution.getInstance().getTransExecutor(executionId);

		/*
		 * if(transExecutor.isFinished()) { clearExecution(executionId); return new
		 * ReturnCodeDto(1, "Failed"); // Already finished! }
		 */

		if (transExecutor != null) {
			if (transExecutor.execStop()) {
				return new ReturnCodeDto(0, "Succeeded");
			}else {
				return new ReturnCodeDto(1, "已触发停止,请等待...");
			}
		}

		return new ReturnCodeDto(1, "执行ID["+executionId+"]对应的执行器未找到");
	}

	@Override
	public ReturnCodeDto execResume(String executionId) throws Exception {
		BaseTransExecutor transExecutor = CloudExecution.getInstance().getTransExecutor(executionId);

		/*
		 * if(transExecutor.isFinished()) { clearExecution(executionId); return new
		 * ReturnCodeDto(1, "Failed"); // Already finished! }
		 */

		if (transExecutor != null) {
			if (transExecutor.execResume()) {
				return new ReturnCodeDto(0, "Succeeded");
			}else {
				return new ReturnCodeDto(1, "已触发恢复,但恢复失败.");
			}
		}

		return new ReturnCodeDto(1, "执行ID["+executionId+"]对应的执行器未找到");
	}
	
	
	public ReturnCodeDto execMorePreview(String executionId) throws Exception{
		
		CloudTransExecutor transExecutor = (CloudTransExecutor)CloudExecution.getInstance().getTransExecutor(executionId);
		if (transExecutor != null) {
			if (transExecutor.execMorePreview()) {
				return new ReturnCodeDto(0, "Succeeded");
			}else {
				return new ReturnCodeDto(1, "已触发恢复,但恢复失败.");
			}
		}

		return new ReturnCodeDto(1, "执行ID["+executionId+"]对应的执行器未找到");
		
	}

	@Override
	public List<TransExecStepMeasureDto> getStepMeasure(String executionId) throws Exception {
		BaseTransExecutor transExecutor = CloudExecution.getInstance().getTransExecutor(executionId);

		if (transExecutor != null) {
			return transExecutor.getStepMeasure();
		}

		return new ArrayList<>();
	}

	@Override
	public List<TransExecStepStatusDto> getStepStatus(String executionId) throws Exception {
		BaseTransExecutor transExecutor = CloudExecution.getInstance().getTransExecutor(executionId);

		//?? If execution doesn't exist, always return the latest step status while  execution in live.
		if (transExecutor != null) {
			return transExecutor.getStepStatus();
		}
		
		return new ArrayList<>();
	}

	@Override
	public TransExecLogDto getExecLog(String executionId) throws Exception {
		BaseTransExecutor transExecutor = CloudExecution.getInstance().getTransExecutor(executionId);

		if (transExecutor != null) {
			return transExecutor.getExecLog();
		}

		throw new KettleException(" executionId " + executionId + " 不存在!");
	}

	@Override
	public Map<String, List<String[]>> getDebugPreviewData(String executionId) throws Exception {
		CloudTransExecutor transExecutor = (CloudTransExecutor) CloudExecution.getInstance()
				.getTransExecutor(executionId);

		if (transExecutor != null) {
			return transExecutor.getDebugPreviewData();
		}

		throw new KettleException(" executionId " + executionId + " 不存在!");
	}

	@Override
	public TransExecIdDto getExecId(String owner ,String transName) throws Exception {
		if( Utils.isEmpty( owner )) {
    		owner = CloudSession.getResourceUser() ;
    	}
		if (!Utils.isEmpty(transName) && transName.contains("/")) {
			// group = transName.split("/", 2 )[0];
			transName = transName.split("/", 2)[1];
		}
		String executionId = null;
		ExecutionInfo executionInfo = CloudExecution.getInstance().getExecutionInfo(owner, transName, false) ;
		if ( executionInfo == null ){
			if( CloudExecutorStatus.assertRunning(transCurrentStatus(owner,transName)) &&  System.currentTimeMillis() <= ( CarteSingleton.getSlaveServerConfig().getSlaveServer().getChangedDate().getTime() + 3000000 )) {
				//任务正在重启
				throw new Exception("转换["+transName+"]正在重启中,请稍后再查看.");
			}
		}else {
			executionId = executionInfo.executionId ;
		}
		
		TransExecIdDto execId = new TransExecIdDto();
		execId.setExecutionId(Const.NVL(executionId, ""));
		return execId;
	}

	@Override
	public TransExecStatusDto getExecStatus(String executionId) throws Exception {
		TransExecStatusDto status = new TransExecStatusDto();
		status.setStatus(CloudExecutorStatus.WAITING.getType());

		BaseTransExecutor transExecutor = CloudExecution.getInstance().getTransExecutor(executionId);
		if (transExecutor != null) {
			status.setStatus(transExecutor.execStatus());
		}

		return status;
	}

	// TODO.
	// Need to improve the status controlling procedure !
	@Override
	public TransExecStatusDto getTransStatus(String owner ,String transName) throws Exception {
		if( Utils.isEmpty( owner )) {
    		owner = CloudSession.getResourceUser() ;
    	}
		if (!Utils.isEmpty(transName) && transName.contains("/")) {
			// group = transName.split("/", 2 )[0];
			transName = transName.split("/", 2)[1];
		}
		TransExecStatusDto status = new TransExecStatusDto();
		
		String s = null ;
		ExecutionInfo executionInfo = CloudExecution.getInstance().getExecutionInfo(owner, transName, false) ;
		if ( executionInfo != null ) {
			BaseTransExecutor transExecutor = executionInfo.getTransExecutor();
			if (transExecutor != null) {
				s = transExecutor.getStatus();
			}
		}
		if ( Utils.isEmpty(s)) {
			s = transCurrentStatus(owner , transName);
		}
		status.setStatus(s);
		return status;
	}

	/*
	 * @see
	 * com.ys.idatrix.cloudetl.service.trans.CloudTransService#getTransHistory(java.
	 * lang.String)
	 */
	@Override
	public ExecHistoryDto getTransHistory(String owner ,String transName) throws Exception {
		if( Utils.isEmpty( owner )) {
    		owner = CloudSession.getResourceUser() ;
    	}
		if (!Utils.isEmpty(transName) && transName.contains("/")) {
			// group = transName.split("/", 2 )[0];
			transName = transName.split("/", 2)[1];
		}

		ExecHistoryDto history = new ExecHistoryDto();
		history.setRetCode("0");
		history.setMessage("");

		CloudExecHistory execHistory = CloudExecHistory.initExecHistory(owner, transName, CloudLogType.TRANS_HISTORY);
		history.setRecords(execHistory.getExecRecords());

		return history;
	}

	@Override
	public ExecLogsDto getTransLogs(String owner ,String name,String id ,String startDate ,String endDate) throws Exception {
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

		CloudExecLog log = CloudExecLog.initExecLog(CloudApp.getInstance().getUserLogsRepositoryPath(owner), name, CloudLogType.TRANS_LOG);
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
