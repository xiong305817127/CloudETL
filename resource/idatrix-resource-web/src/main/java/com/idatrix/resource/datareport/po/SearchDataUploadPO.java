package com.idatrix.resource.datareport.po;

import lombok.Data;

import java.util.Date;

@Data
public class SearchDataUploadPO {
	private Long id;

	private String importTaskId;

	private String subscribeId;

	private String dataType;

	private String code;

	private String name;

	private String fileNames;

	private String dataBatch;

	private Date createTime;

	private Date importTime;

	private Long importCount;

	private String status;

}
