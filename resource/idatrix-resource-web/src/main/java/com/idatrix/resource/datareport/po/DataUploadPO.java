package com.idatrix.resource.datareport.po;

import lombok.Data;

import java.util.Date;

/**
 * 数据上报信息表
 * @Description： 数据上报时,上传数据所生成的信息
 * @Date: 2018/06/11
 */
@Data
public class DataUploadPO {
	/*主键*/
	private Long id;

	/*资源ID*/
	private Long resourceId;

//	/*原始库中的文件名(UUID)*/
//	private String originFileName;
//
//	/*发布出来的文件名*/
//	private String pubFileName;

	/*当前状态:wait_import等待入库,importing入库中,import_complete已入库,import_error入库失败*/
	private String status;

	/*数据批次，格式为yyyy-MM-dd*/
	private String dataBatch;

	/*类型为: DB数据库,FILE文件*/
	private String dataType;

	/*入库时间*/
	private Date importTime;

	/*入库数据量*/
	private Long importCount;

	/* 实际新增入库数据量 */
	private Long insertCount;

	/* 实际更新入库数据量 */
	private Long updateCount;

	/* 错误数据数量 */
	private Long failCount;

	/*ETL的任务号:UP+部门编码+’-’+5位顺序号*/
	private String importTaskId;

	/*顺序号,全局累加 */
	private Long taskSeq;

	/*ETL任务生成的订阅ID */
	private String subscribeId;

//	/* 文件大小 */
//	private String fileSize;
//
//	/* 文件类型 */
//	private String fileType;
//
//	/*文件描述 */
//	private String fileDescription;

	/*ETL任务生成的订阅ID的单次执行ID*/
	private String execId;

	/*ETL任务出现异常时, 记录错误信息*/
	private String importErrmsg;

	/*租户ID，用于租户隔离*/
	private Long rentId;

	private String creator;

	private Date createTime;

	private String modifier;

	private Date modifyTime;

}
