package com.idatrix.resource.datareport.vo;

import lombok.Data;

@Data
public class SearchDataUploadVO {
	private Long id;
	private String importTaskId;
	private String subscribeId;
	private String dataType;
	private String code;
	private String name;
	private String pubFileName;
	private String dataBatch;
	private String createTime;
	private String importTime;
	private Long importCount;
	private String status;

}
