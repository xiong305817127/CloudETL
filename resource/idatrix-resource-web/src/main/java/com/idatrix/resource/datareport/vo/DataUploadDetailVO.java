package com.idatrix.resource.datareport.vo;

import lombok.Data;

import java.util.Date;

/**
 * 数据上报详细信息表
 * @Description： 数据上报时,上传数据所生成的信息
 * @Date: 2018/06/11
 */
@Data
public class DataUploadDetailVO {
	/*主键*/
	private Long id;

	/*Data Upload表主键 */
	private Long parentId;

	/*原始库中的文件名(UUID)*/
	private String originFileName;

	/*发布出来的文件名*/
	private String pubFileName;

	/* 文件大小 */
	private String fileSize;

	/* 文件类型 */
	private String fileType;

	/*文件描述 */
	private String fileDescription;

	private String creator;

	private Date createTime;

	private String modifier;

	private Date modifyTime;

}
