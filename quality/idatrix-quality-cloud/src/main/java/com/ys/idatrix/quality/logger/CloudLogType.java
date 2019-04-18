/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.quality.logger;

/**
 * CloudLogType.java
 * @author JW
 * @since 2017年8月3日
 *
 */
public enum CloudLogType {

	ANALYZER_LOG("analyzer", ".log"),
	ANALYZER_HISTORY("analyzer_history", ".rds"),
	ERROR_LOG("error", ".log"), CLOUD_LOG("cloud", ".log"),
	TRANS_LOG("trans", ".log"), JOB_LOG("job", ".log"),
	TRANS_HISTORY("trans_history", ".rds"), JOB_HISTORY("job_history", ".rds");

	// non-standard, Kettle database repository only!
	//
	// USER("user", ".usr"),

	private String type;
	private String extension;

	private CloudLogType(String type, String extension) {
		this.type = type;
		this.extension = extension;
	}

	@Override
	public String toString() {
		return type;
	}

	public String getType() {
		return type;
	}

	public String getExtension() {
		return extension;
	}

}
