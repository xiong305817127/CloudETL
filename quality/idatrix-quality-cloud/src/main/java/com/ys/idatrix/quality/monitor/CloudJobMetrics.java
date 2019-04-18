/**
 * 云化数据集成系统 
 * iDatrxi CloudETL
 */
package com.ys.idatrix.quality.monitor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.di.repository.RepositoryObjectType;

import com.ys.idatrix.quality.dto.statistics.ExecTaskTimesTotal;
import com.ys.idatrix.quality.ext.executor.logger.CloudExecHistory;
import com.ys.idatrix.quality.repository.CloudRepository;

/**
 * CloudJobMetrics <br/>
 * @author JW
 * @since 2017年9月26日
 * 
 */
public class CloudJobMetrics {
	
	public static final Log  logger = LogFactory.getLog("CloudJobMetrics");
	
	/**
	 * Get the count of all jobs (includes all users' repository)
	 * @return
	 */
	public static double jobTotalCounter() {
		double count = 0;
		
		try {
			count = localJobsCounter();
		} catch (Exception e) {
			logger.error("调度任务计数失败",e);
		}

		return count;
	}
	
	/**
	 * Get the count of all jobs in error , success , running (includes all users' repository)
	 * @return
	 * @throws Exception 
	 */
	public static ExecTaskTimesTotal jobCounter() {
		try {
			return CloudExecHistory.countLastExecRecordTimes(null, RepositoryObjectType.JOB);
		} catch (Exception e) {
			logger.error("调度任务Error计数失败",e);
			return null ;
		}
	}
	
	/**
	 * Get the count of all jobs in all users' local repository
	 * @return
	 * @throws Exception
	 */
	private static double localJobsCounter() throws Exception {
		return CloudRepository.getTaskTotal(RepositoryObjectType.JOB,null);
	}

}
