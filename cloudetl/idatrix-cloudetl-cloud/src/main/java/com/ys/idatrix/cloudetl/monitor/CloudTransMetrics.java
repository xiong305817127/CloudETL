/**
 * 云化数据集成系统 
 * iDatrxi CloudETL
 */
package com.ys.idatrix.cloudetl.monitor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.di.repository.RepositoryObjectType;

import com.ys.idatrix.cloudetl.dto.statistics.ExecTaskTimesTotal;
import com.ys.idatrix.cloudetl.ext.executor.logger.CloudExecHistory;
import com.ys.idatrix.cloudetl.repository.CloudRepository;

/**
 * CloudTransMetrics <br/>
 * @author JW
 * @since 2017年9月26日
 * 
 */
public class CloudTransMetrics {
	
	public static final Log  logger = LogFactory.getLog(CloudTransMetrics.class);
	
	/**
	 * Get the count of all transformation (includes all users' repository)
	 * @return
	 */
	public static double transTotalCounter() {
		double count = 0;
		
		try {
			count = localTransCounter();
		} catch (Exception e) {
			logger.error("trans total counter error.", e);
		}
		
		return count;
	}
	
	/**
	 * Get the count of all transformation in error,success,Running (includes all users' repository)
	 * @return
	 */
	public static ExecTaskTimesTotal transCounter() {
		try {
			return CloudExecHistory.countLastExecRecordTimes(null, RepositoryObjectType.TRANSFORMATION);
		} catch (Exception e) {
			logger.error("转换Error计数失败",e);
			return null ;
		}
	}
	
	/**
	 * Get the count of all transformation in all users' local repository
	 * @return
	 * @throws Exception
	 */
	private static double localTransCounter() throws Exception {
		return CloudRepository.getTaskTotal(RepositoryObjectType.TRANSFORMATION, null);
	}

}
