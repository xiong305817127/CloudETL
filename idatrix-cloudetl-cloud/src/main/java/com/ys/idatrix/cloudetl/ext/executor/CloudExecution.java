/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.ext.executor;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.pentaho.di.core.util.IdatrixPropertyUtil;
import org.pentaho.di.core.util.Utils;
import org.pentaho.pms.util.Const;

import com.ys.idatrix.cloudetl.ext.CloudSession;
import com.ys.idatrix.cloudetl.logger.CloudLogger;

/**
 * Job execution context.
 * Keeps all job executors in application scope.
 * @author JW
 * @since 2017年5月24日
 *
 */
public class CloudExecution implements Serializable {

	private static final long serialVersionUID = 8731238537782930893L;

	private static CloudExecution execution;

	// (TODO.) Execution information should be stored in ETL database !!!
	private final  Map<String, ExecutionInfo> cloudExecutions;

	private CloudExecution() {
		cloudExecutions = new ConcurrentHashMap<>(); 
	}

	public static CloudExecution getInstance() {
		if (execution == null) {
			synchronized (CloudExecution.class) {
				if (execution == null) {
					execution = new CloudExecution();
				}
			}
		}
		return execution;
	}
	
	private String getKey(String owner , String taskName, boolean isJob) {
		owner = Const.NVL( owner,CloudSession.getResourceUser()) ;
		return owner+"_"+taskName+"_"+(isJob?"job":"trans");
	}

	public boolean clearExecution(String owner , String taskName, boolean isJob) throws Exception {
		String key = getKey(owner, taskName, isJob);
		ExecutionInfo executorInfo = cloudExecutions.get(key);
		if( executorInfo == null ) {
			return true ;
		}
		if(executorInfo.executor.isFinished()) {
			CloudLogger.getInstance().info(this ,"Clear execution : "+(executorInfo.isJob?"job":"trans")+","+executorInfo.execUser+"["+executorInfo.owner+"],"+executorInfo.taskName+","+executorInfo.executionId);
			executorInfo.executor.clear();
			//清空缓存
			cloudExecutions.remove(key);
			executorInfo.executor = null;
			executorInfo = null ;
			return true ;
		}
		return false ;
	}
	
	public ExecutionInfo getExecutionInfo(String owner , String taskName, boolean isJob) {
		String key = getKey(owner, taskName, isJob);
		return cloudExecutions.get(key) ;
	}
	
	public ExecutionInfo getExecutionInfo(String executionId) {
		return cloudExecutions.values().stream().filter( info -> { return info.executionId.equals(executionId); }).findAny().orElse(null) ;
	}

	public CloudJobExecutor getJobExecutor(String executionId) {
		ExecutionInfo exectuorInfo = getExecutionInfo(executionId);
		if( exectuorInfo != null ) {
			return exectuorInfo.getJobExecutor();
		}
		return null ;
	}
	
	public CloudTransExecutor getTransExecutor(String executionId) {
		ExecutionInfo exectuorInfo = getExecutionInfo(executionId);
		if( exectuorInfo != null ) {
			return exectuorInfo.getTransExecutor();
		}
		return null ;
	}
	
	public boolean putExecutor( BaseExecutor executor ) throws Exception {
		boolean isJob = false ;
		if(executor instanceof CloudJobExecutor ) {
			isJob = true ;
		}
		return putExecutor(new ExecutionInfo(executor.getExecUser(), executor.getOwner(), executor.getTaskName(), executor.getExecutionId(), executor, isJob));
	}

	public boolean putExecutor(ExecutionInfo executionInfo) throws Exception {
		String key = getKey(executionInfo.owner, executionInfo.taskName, executionInfo.isJob);
		ExecutionInfo old = cloudExecutions.get(key) ;
		if( old != null && !clearExecution(executionInfo.owner, executionInfo.taskName, executionInfo.isJob)) {
			//旧的执行器还在执行
			return false;
		}
		synchronized(cloudExecutions) {
			cloudExecutions.put(key, executionInfo);
		}
		return true;
	}

	

	public int remoteCounter(String serverName,boolean isJob) {
		int count = 0;
		for (ExecutionInfo executionInfo : cloudExecutions.values()) {
			if ( !(executionInfo.isJob^isJob) && executionInfo.executor.getExecutionConfiguration().isExecutingRemotely() ) {
				if(Utils.isEmpty(serverName) || serverName.equals(executionInfo.executor.getExecutionConfiguration().getRemoteServer().getName() ) ){
					count++;
				}
			}
		}
		return count;
	}
	
	public int localCounter(boolean isJob ) {
		int count = 0;
		for (ExecutionInfo executionInfo : cloudExecutions.values()) {
			if (  !(executionInfo.isJob^isJob) && executionInfo.executor.getExecutionConfiguration().isExecutingLocally() ) {
				count++;
			}
		}
		return count;
	}
	
	public  String  ClearExecutorListener() throws Exception {
		
		StringBuffer jobSb = new StringBuffer();
		StringBuffer transSb = new StringBuffer();
		
		long intervalMillis = Long.valueOf( IdatrixPropertyUtil.getProperty("idatrix.exec.clear.delay.interval", "2") )*60*1000;
		synchronized(cloudExecutions) {
			for (ExecutionInfo executionInfo : cloudExecutions.values()) {
				if(executionInfo.executor.isFinished()) {
					if( executionInfo.executor.getEndDate() != null ) {
						Calendar now = Calendar.getInstance();
						long curTime = now.getTimeInMillis();
						now.setTime(executionInfo.executor.getEndDate());
						long stopTime = now.getTimeInMillis();
						if( (curTime -stopTime) > intervalMillis ) {
							if(executionInfo.isJob) {
								jobSb.append(executionInfo.executor.getUserString()).append(":").append(executionInfo.executor.getTaskName()).append(":").append(executionInfo.executor.getExecutionId()).append(",");
							}else {
								transSb.append(executionInfo.executor.getUserString()).append(":").append(executionInfo.executor.getTaskName()).append(":").append(executionInfo.executor.getExecutionId()).append(",");
							}
							clearExecution(executionInfo.owner, executionInfo.taskName, executionInfo.isJob);
						}
					}
				}
			}
		}
		String res = "";
		if(jobSb.length() != 0) {
			res += "\njobs: "+jobSb.toString() ;
		}
		if(transSb.length() != 0) {
			res += "\ntrans: "+transSb.toString() ;
		}
		
		return res;
	}
	
	
	public class ExecutionInfo{
		
		public String execUser ;
		public String owner ;
		public String taskName ;
		public String executionId;
		public BaseExecutor executor;
		public boolean isJob ;
		/**
		 * @param execUser
		 * @param owner
		 * @param taskName
		 * @param executionId
		 * @param executor
		 * @param isJob
		 */
		public ExecutionInfo(String execUser, String owner, String taskName, String executionId, BaseExecutor executor, boolean isJob) {

			this.execUser = execUser;
			this.owner = owner;
			this.taskName = taskName;
			this.executionId = executionId;
			this.executor = executor;
			this.isJob = isJob;
		}
		
		public CloudJobExecutor getJobExecutor() {
			if(isJob) {
				return (CloudJobExecutor)executor ;
			}
			return null ;
		}
		
		public CloudTransExecutor getTransExecutor() {
			if(!isJob) {
				return (CloudTransExecutor)executor ;
			}
			return null ;
		}
		
		
		
	}

}
