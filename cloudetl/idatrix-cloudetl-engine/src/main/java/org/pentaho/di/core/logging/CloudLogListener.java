/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package org.pentaho.di.core.logging;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Queue;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.Result;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.logging.KettleLogLayout;
import org.pentaho.di.core.logging.KettleLoggingEvent;
import org.pentaho.di.core.logging.KettleLoggingEventListener;
import org.pentaho.di.core.logging.LogMessage;
import org.pentaho.di.core.logging.LoggingRegistry;
import org.pentaho.di.core.util.IdatrixPropertyUtil;
import org.pentaho.di.core.util.OsgiBundleUtils;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.job.DelegationListener;
import org.pentaho.di.job.Job;
import org.pentaho.di.job.JobEntryListener;
import org.pentaho.di.job.JobExecutionConfiguration;
import org.pentaho.di.job.JobStatusChangeListener;
import org.pentaho.di.job.SingleListener;
import org.pentaho.di.job.entries.trans.JobEntryTrans;
import org.pentaho.di.job.entry.JobEntryCopy;
import org.pentaho.di.job.entry.JobEntryInterface;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransExecutionConfiguration;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.steps.insertupdate.InsertUpdateMeta;
import org.pentaho.di.trans.steps.tableinput.TableInputMeta;
import org.pentaho.di.trans.steps.tableoutput.TableOutputMeta;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;

/**
 * CloudExecLog.java
 * 
 * @author JW
 * @since 2017年8月4日
 *
 */
public class CloudLogListener implements KettleLoggingEventListener {

	public static final Log logger = LogFactory.getLog("CloudLogListener");
	
	private static final int MAX_CACHE_NUMBER = Integer.valueOf(IdatrixPropertyUtil.getProperty("idatrix.exec.log.cache.max.record.number", "10000"));

	public static final String LOG_SEGMENTINGPART_ENABLE = "segmentingPartEnable";
	public static final String LOG_SEGMENTINGPART_WRITEGREATER0_ENABLE = "segmentingPartWriteGreater0Enable";
	public static final String LOG_OUTSTEPNAME = "logOutStepName";
	public static final String LOG_INSTEPNAME = "logInStepName";
	public static final String LOG_MAINTRANSNAME = "logMainTransName";

	private static String START_PART_FLAG = "INFO [Start Part Exec] :";

	private static String LOG_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
	private static String LOG_TIME_PATTERN1 = "yyyy/MM/dd HH:mm:ss";

	private Queue<SegmentingPartInfo> partCache;
	private Queue<String> logCache;
	private SegmentingPartInfo curPart;
	private int failCount = 0 ;
	
	private String jobName;
	private Job job;
	private Trans trans;
	private VariableSpace variableSpace;

	private String logChannelId;
	private KettleLogLayout layout;
	
	private SegmentingPartInfo exceptionPart ; 

	public CloudLogListener(String logChannelId) {
		this.logChannelId = logChannelId;

		this.layout = new KettleLogLayout(true);
		this.partCache = Queues.newConcurrentLinkedQueue();
		this.logCache = Queues.newConcurrentLinkedQueue();
		
		exceptionPart = new SegmentingPartInfo();
		exceptionPart.setEnable(false);
	}

	public void initJob(Job job) {
		this.job = job;
		this.variableSpace = job;
		this.jobName = job.getJobname().split("@u-")[0];
		
		job.addJobEntryListener(createJobEntryListener());
		job.addDelegationListener(createDelegationListener());
		job.addSingleListener(createSingleListener());
		
		try {
			addSubcribePushListener();
		} catch (Exception e) {
			logger.error("增加订阅状态推送监听器失败.", e);
		}
	}
	
	public void initTrans(Trans trans) {
		this.trans = trans;
		this.variableSpace = trans;
		//this.transName = trans.getName().split("@u-")[0];
		if(trans != null ) {
			addLinesRecord(trans.getTransMeta());
		}
		
	}

	public String getLog() {
		StringBuilder sb = new StringBuilder();
		if (logCache != null && logCache.size() > 0) {
			int len = logCache.size();
			for (int i = 0; i < len; i++) {
				sb.append(logCache.poll());
			}
		}
		return sb.toString();
	}

	public List<SegmentingPartInfo> getPartLog() {
		List<SegmentingPartInfo> res = Lists.newArrayList();
		if (partCache != null && partCache.size() > 0) {
			int len = partCache.size();
			for (int i = 0; i < len; i++) {
				res.add(partCache.poll());
			}
			return res;
		}
		return null;
	}

	private void insertLog(String logText, boolean addTime) {
		if (Utils.isEmpty(logText) || Utils.isEmpty(logText.trim())) {
			return;
		}
		StringBuilder sb = new StringBuilder();
		if (addTime) {
			sb.append(new SimpleDateFormat(LOG_TIME_PATTERN1).format(new Date()));
			sb.append(Utils.isEmpty(logText) ? "  " : "  " + logText);
		} else {
			sb.append(logText);
		}
		if (!sb.toString().endsWith(Const.CR)) {
			sb.append( Const.CR );
		}
		logCache.offer(sb.toString());
		
		//缓存一直没有被抽取,自动删除第一个
		if(logCache.size() > MAX_CACHE_NUMBER) {
			logCache.poll();
		}
	}

	private void addPartDto(SegmentingPartInfo curPart) {
		if (curPart == null) {
			return;
		}
		if (!partCache.contains(curPart)) {
			partCache.offer(curPart);
		}
		
		//缓存一直没有被抽取,自动删除第一个
		if(partCache.size() > MAX_CACHE_NUMBER) {
			partCache.poll();
		}
		
	}

	@Override
	public void eventAdded(KettleLoggingEvent event) {
		try {

			if (logChannelId == null) {
				return;
			}
			
			if(trans != null && !IdatrixPropertyUtil.getBooleanProperty("Trans.Cloud.Log.Record.Enable", true)) {
				return ;
			}
			
			if(job != null && !IdatrixPropertyUtil.getBooleanProperty("Job.Cloud.Log.Record.Enable", true)) {
				return ;
			}

			Object messageObject = event.getMessage();
			if (messageObject instanceof LogMessage) {
				
				boolean logToFile = false;
				LogMessage message = (LogMessage) messageObject;
				// This should be fast enough cause cached.
				List<String> logChannelChildren = LoggingRegistry.getInstance().getLogChannelChildren(logChannelId);
				// This could be non-optimal, consider keeping the list sorted in the logging
				// registry
				logToFile = Const.indexOfString(message.getLogChannelId(), logChannelChildren) >= 0;

				if (logToFile) {
					String logText = layout.format(event);
					insertLog(logText, false);

					if (isSegmentingPartEable() && curPart != null) {
						curPart.appendLog(logText);
					}
				}
			}
		} catch (Exception e) {
			logger.error("日志事件处理失败.", e);
		}
	}

	private SingleListener createSingleListener() {
		return new SingleListener() {

			@Override
			public void singleStart(Job job, JobEntryCopy startpoint) {
				curPart = new SegmentingPartInfo();
				curPart.setBegin(new SimpleDateFormat(LOG_TIME_PATTERN).format(new Date()));
				curPart.setStatus(Trans.STRING_RUNNING);
				
				if (isSegmentingPartEable()) {
					// 定时完毕，开始执行
					insertLog(START_PART_FLAG + curPart.getId() ,true);
					curPart.setEnable(true);
				}else {
					insertLog(START_PART_FLAG, true);
					curPart.setEnable(false);
				}
				
				statusChange("SingleStart", null, curPart);
				
				job.setVariable("SegmentingPartRinnigId", curPart.getId().toString());
			}

			@Override
			public void singleEnd(Job job, Result result) {
				// 定时完毕，开始执行
				if(curPart != null) {
					curPart.setEnd(new SimpleDateFormat(LOG_TIME_PATTERN).format(new Date()));
					String status;
					if( result.getNrErrors() >0) {
						curPart.setStatus(Trans.STRING_FINISHED_WITH_ERRORS);
						status = "SingleEndError" ;
						failCount++ ;
					}else {
						curPart.setStatus(Trans.STRING_FINISHED);
						status = "SingleEnd";
					}
					if (!job.isActive() || job.isStopped()) {
						curPart.setStatus(job.getStatus());
					}
					curPart.setInputLines(result.getNrLinesInput());
					curPart.setOutputLines(result.getNrLinesOutput());
					curPart.setReadLines(result.getNrLinesRead());
					curPart.setWriteLines(result.getNrLinesWritten());
					curPart.setUpdateLines(result.getNrLinesUpdated());
					curPart.setErrorLines(result.getNrLinesRejected());
					
					if (isSegmentingPartEable()) {
						//启用单次执行记录
						if( failCount == 1) {
							//第一次执行失败,不管数量进行记录
							curPart.setEnable(true);
							addPartDto(curPart);
						}else if ( !isSegmentingPartWriteGreater0() || (isSegmentingPartWriteGreater0() && result.getNrLinesWritten() > 0) )  {
							//非第一次执行失败(执行成功或者连续的第二次以上的执行失败),且 数据量控制启用
							addPartDto(curPart);
						}
					}
					statusChange(status, result , curPart);
					curPart = null;
				}
				
			}
		};
	}

	private JobEntryListener createJobEntryListener() {
		return new JobEntryListener() {

			@Override
			public void beforeExecution(Job job, JobEntryCopy jobEntryCopy, JobEntryInterface jobEntryInterface) {

			}

			@Override
			public void afterExecution(Job job, JobEntryCopy jobEntryCopy, JobEntryInterface jobEntryInterface,
					Result result) {
				if (isSegmentingPartEable() && curPart != null && jobEntryInterface.isStart()) {
					// 定时完毕，开始执行,更新开始时间
					curPart.setBegin(new SimpleDateFormat(LOG_TIME_PATTERN).format(new Date()));
				}
			}

		};
	}

	private DelegationListener createDelegationListener() {
		return new DelegationListener() {

			@Override
			public void jobDelegationStarted(Job delegatedJob, JobExecutionConfiguration jobExecutionConfiguration) {

			}

			@Override
			public void transformationDelegationStarted(Trans delegatedTrans,
					TransExecutionConfiguration transExecutionConfiguration) {
				addLinesRecord(delegatedTrans.getTransMeta());
			}
		};
	}
	
	private void addLinesRecord(TransMeta transMeta) {
		StepMeta targetOutStep = null;
		StepMeta targetInStep = null;

		String outStepName = getOutStepName();
		if (!Utils.isEmpty(outStepName)) {
			targetOutStep = transMeta.findStep(outStepName);
		}
		String inStepName = getInStepName();
		if (!Utils.isEmpty(inStepName)) {
			targetInStep = transMeta.findStep(inStepName);
		}
		if (targetOutStep == null || targetInStep == null) {
			String mainTrans = getMainTrans();
			if (!Utils.isEmpty(mainTrans) && transMeta.getName().contains(mainTrans)) {
				for (StepMeta step : transMeta.getTransHopSteps(false)) {
					
					String excludePattern = IdatrixPropertyUtil.getProperty("idatrix.log.lines.step.exclude");
					if( !Utils.isEmpty(excludePattern) && Pattern.matches(excludePattern, step.getName())) {
						//排除该步骤
						continue ;
					}
					if (Utils.isEmpty(outStepName) && transMeta.findNextSteps(step).size() == 0
							&& transMeta.findPreviousSteps(step).size() > 0) {
						targetOutStep = step;
					}
					if (Utils.isEmpty(inStepName) && transMeta.findNextSteps(step).size() > 0
							&& transMeta.findPreviousSteps(step).size() == 0) {
						targetInStep = step;
					}
				}
			}
		}
		
		
		String dataInputSource = null ;
		if (targetInStep != null) {
			transMeta.getTransLogTable().findField(TransLogTable.ID.LINES_INPUT).setSubject(targetInStep.getName());
			
			//查询表输入信息
			if(  targetInStep.getStepMetaInterface() instanceof TableInputMeta) {
				TableInputMeta tim = (TableInputMeta) targetInStep.getStepMetaInterface() ;
				if(  tim != null && tim.getDatabaseMeta() != null ) {
					dataInputSource = tim.getDatabaseMeta().getDisplayName()+"["+Utils.encode(tim.getSQL()) +"]" ;
				}
			}
			
		}
		
		String dataOututSource = null ;
		if (targetOutStep != null) {
			transMeta.getTransLogTable().findField(TransLogTable.ID.LINES_OUTPUT).setSubject(targetOutStep.getName());
			transMeta.getTransLogTable().findField(TransLogTable.ID.LINES_READ).setSubject(targetOutStep.getName());
			transMeta.getTransLogTable().findField(TransLogTable.ID.LINES_WRITTEN).setSubject(targetOutStep.getName());
			transMeta.getTransLogTable().findField(TransLogTable.ID.LINES_UPDATED).setSubject(targetOutStep.getName());
			transMeta.getTransLogTable().findField(TransLogTable.ID.LINES_REJECTED).setSubject(targetOutStep.getName());
			
			//记录 数据库输出源
			if( !Utils.isEmpty(targetOutStep.getStepMetaInterface().getUsedDatabaseConnections()) ) {
				if( targetOutStep.getStepMetaInterface() instanceof TableOutputMeta ) {
					TableOutputMeta tom = (TableOutputMeta) targetOutStep.getStepMetaInterface() ;
					if(  tom != null && tom.getDatabaseMeta() != null ) {
						DatabaseMeta dm = tom.getDatabaseMeta() ;
						dataOututSource  = dm.getPluginId()+"."+dm.getName()+"."+tom.getSchemaName()+"."+tom.getTableName() ;
					}
				}else if( targetOutStep.getStepMetaInterface() instanceof InsertUpdateMeta ) {
					InsertUpdateMeta ium = (InsertUpdateMeta) targetOutStep.getStepMetaInterface() ;
					if(  ium != null && ium.getDatabaseMeta() != null ) {
						DatabaseMeta dm = ium.getDatabaseMeta() ;
						dataOututSource = dm.getPluginId()+"."+dm.getName()+"."+ium.getSchemaName()+"."+ium.getTableName() ;
					}
				}
			}
			
		}
		
		if( !Utils.isEmpty(dataOututSource) ||  !Utils.isEmpty(dataInputSource) ) {
			exceptionPart.setDataOutputSource(dataOututSource);
			exceptionPart.setDataInputSource(dataInputSource);
			addPartDto(exceptionPart);
		}
		
	}
	
	public void  save4WException(String exceptionType ,String exceptionName ,String exceptionPosition ,String exceptionDetail) {
		if(  exceptionPart != null && Utils.isEmpty( exceptionPart.getExceptionPosition()) ) {
			//只保存第一个
			exceptionPart.setExceptionName(exceptionName);
			exceptionPart.setExceptionPosition(exceptionPosition);
			exceptionPart.setExceptionType(exceptionType);
			exceptionPart.setExceptionDetail(exceptionDetail);
			addPartDto(exceptionPart);
		}
	}
	

	private String getMainTrans() {
		if (variableSpace == null) {
			return null;
		}
		String mainTrans = (String) variableSpace.getVariable(LOG_MAINTRANSNAME);
		if (Utils.isEmpty(mainTrans)) {
			mainTrans = (String) variableSpace.getVariable("MainTransName");
		}
		if (Utils.isEmpty(mainTrans)) {
			if( trans != null ) {
				return trans.getTransMeta().getName();
			}else if( job != null) {
				//配置的排除正则表达式
				String excludePattern = IdatrixPropertyUtil.getProperty("idatrix.log.lines.main.trans.exclude");
				for (JobEntryCopy entry : job.getJobMeta().getJobCopies()) {
					if (entry.getEntry() instanceof JobEntryTrans) {
						String transName = ((JobEntryTrans) entry.getEntry()).getTransname();
						if( !Utils.isEmpty(excludePattern) && Pattern.matches(excludePattern, transName) ) {
							//符合排除正则表达式
							continue ;
						}
						mainTrans = transName ;
					}
				}
			}
		}
		return mainTrans;
	}

	private String getOutStepName() {
		if (variableSpace == null) {
			return null;
		}
		String outName = (String) variableSpace.getVariable(LOG_OUTSTEPNAME);
		if (Utils.isEmpty(outName)) {
			outName = (String) variableSpace.getVariable("OutStepName");
		}
		return outName;
	}

	private String getInStepName() {
		if (variableSpace == null) {
			return null;
		}
		String inName = (String) variableSpace.getVariable(LOG_INSTEPNAME);
		if (Utils.isEmpty(inName)) {
			inName = (String) variableSpace.getVariable("InStepName");
		}
		return inName;
	}

	private boolean isSegmentingPartEable() {
		boolean result= ( job != null && job.getParentJob() == null ) ;
		if(result) {
			
			String varEnable = job.getVariable(LOG_SEGMENTINGPART_ENABLE);
			if( Utils.isEmpty( varEnable )) {
				varEnable = IdatrixPropertyUtil.getProperty("idatrix.job.log.segmenting.part");
			}
			result =  result &&  Boolean.valueOf(varEnable) ;
		}
		return result;
	}
	
	private boolean isSegmentingPartWriteGreater0() {
		if(job != null && job.getParentJob() == null) {
			return !Utils.isEmpty(variableSpace.getVariable(LOG_SEGMENTINGPART_WRITEGREATER0_ENABLE)) ? Boolean.valueOf(variableSpace.getVariable(LOG_SEGMENTINGPART_WRITEGREATER0_ENABLE)) : IdatrixPropertyUtil.getBooleanProperty("idatrix.exec.log.part.only.effective.running", true);
		}
		return false;
	}
	
	
	private void addSubcribePushListener() throws Exception  {
		
		String SubcribePushServicePackageName= Utils.getPackageName("com.ys.idatrix.cloudetl.subcribe.SubcribePushService");
		String subcribePushKey = Const.NVL( (String) OsgiBundleUtils.getOsgiField(SubcribePushServicePackageName, "SUBCRIBE_PUSH_KEY", false) ,"SubcribePush");
		Boolean isPush = Boolean.valueOf(variableSpace.getVariable(subcribePushKey,"false"));
		if(isPush) {
			 String pluginFactoryPackageName= Utils.getPackageName("com.ys.idatrix.cloudetl.ext.PluginFactory");
			 Object subcribePushService = OsgiBundleUtils.invokeOsgiMethod(pluginFactoryPackageName, "getBean", Class.forName(SubcribePushServicePackageName));
			 if(subcribePushService != null ) {
				//subcribePushService.createSubcribePushListener(jobName)
				JobStatusChangeListener listener = (JobStatusChangeListener)OsgiBundleUtils.invokeOsgiMethod(subcribePushService, "createSubcribePushListener", jobName);
				job.addStatusListener( listener );
			 }
		}
		
	}
	
	  public void statusChange(String status , Result result,SegmentingPartInfo curPart) {
		  
		  String SubcribePushServicePackageName= Utils.getPackageName("com.ys.idatrix.cloudetl.subcribe.SubcribePushService");
		  String subcribePushKey = Const.NVL( (String) OsgiBundleUtils.getOsgiField(SubcribePushServicePackageName, "SUBCRIBE_PUSH_KEY", false) ,"SubcribePush");
		  Boolean isPush = Boolean.valueOf(variableSpace.getVariable(subcribePushKey,"false"));
		  if(isPush && job != null ) {
			  if(job.getStatusListeners() != null && job.getStatusListeners().size() > 0 ) {
					for(JobStatusChangeListener listener : job.getStatusListeners() ) {
						listener.statusChange( job , status , result , curPart);
					}
				}
		  }
		  
	  }
	
}
