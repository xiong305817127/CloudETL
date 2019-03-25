package com.idatrix.resource.datareport.vo;

import lombok.Data;

/**
 * ETL任务相关执行信息
 *
 */
@Data
public class ETLTaskDetailVO {
	/* 任务名称 */
	private String taskName;

	/* 上报记录顺序号 */
	private String dataUploadSeqNum;

	/* 任务当前状态 */
	private String curStatus;

	/* 日志信息 */
	private String log;

	private String operator;

	private String startTime;

	private String endTime;

}
