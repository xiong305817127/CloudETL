package com.idatrix.resource.datareport.po;

import lombok.Data;

import java.util.Date;

/**
 * 政务信息资源文件表
 * @Description： 数据上报时,上传文件类时生成的政务信息资源文件表
 * @Date: 2018/06/11
 */
@Data
public class ResourceFilePO {
	/*主键*/
	private Long id;

	/*资源ID*/
	private Long resourceId;

	/*原始库中的文件名(UUID)*/
	private String originFileName;

	/*发布出来的文件名*/
	private String pubFileName;

	/*文件描述*/
	private String fileDescription;

	/*文件版本号，每次覆盖+1*/
	private Integer fileVersion;

	/*数据批次，格式为yyyy-MM-dd*/
	private String dataBatch;

	private String fileSize;

	private String fileType;

	private String creator;

	private Date createTime;

	private String modifier;

	private Date modifyTime;

}