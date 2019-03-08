/**
 * 云化数据集成系统 
 * iDatrxi CloudETL
 */
package com.ys.idatrix.cloudetl.toolkit.record;

import java.util.Date;

/**
 * AnalysisRecorder <br/>
 * @author JW
 * @since 2018年1月8日
 * 
 */
public class AnalyzerRecorder {
	
	private String user;
	
	private String metaName;
	
	private String triggerId;
	
	private String status;
	
	private Date beginDate;
	
	private Date endDate;
	
	private String logPath;
	
	private AnalyzerReporter reporter;

	/**
	 * @return user
	 */
	public String getUser() {
		return user;
	}

	/**
	 * @param user 要设置的 user
	 */
	public void setUser(String user) {
		this.user = user;
	}

	/**
	 * @return metaName
	 */
	public String getMetaName() {
		return metaName;
	}

	/**
	 * @param metaName 要设置的 metaName
	 */
	public void setMetaName(String metaName) {
		this.metaName = metaName;
	}

	/**
	 * @return triggerId
	 */
	public String getTriggerId() {
		return triggerId;
	}

	/**
	 * @param triggerId 要设置的 triggerId
	 */
	public void setTriggerId(String triggerId) {
		this.triggerId = triggerId;
	}

	/**
	 * @return status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status 要设置的 status
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return beginDate
	 */
	public Date getBeginDate() {
		return beginDate;
	}

	/**
	 * @param beginDate 要设置的 beginDate
	 */
	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}

	/**
	 * @return endDate
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * @param endDate 要设置的 endDate
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	/**
	 * @return logPath
	 */
	public String getLogPath() {
		return logPath;
	}

	/**
	 * @param logPath 要设置的 logPath
	 */
	public void setLogPath(String logPath) {
		this.logPath = logPath;
	}

	/**
	 * @return reporter
	 */
	public AnalyzerReporter getReporter() {
		return reporter;
	}

	/**
	 * @param reporter 要设置的 reporter
	 */
	public void setReporter(AnalyzerReporter reporter) {
		this.reporter = reporter;
	}

}
