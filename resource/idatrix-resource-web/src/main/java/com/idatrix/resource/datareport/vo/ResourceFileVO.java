package com.idatrix.resource.datareport.vo;

import lombok.Data;

/**
 * Created by Robin Wing on 2018-6-19.
 */
@Data
public class ResourceFileVO {

    /*主键*/
    private Long id;

    /*资源ID*/
    private Long resourceId;

    /*发布出来的文件名*/
    private String pubFileName;

    /*数据批次，格式为yyyy-MM-dd*/
    private String dataBatch;

    private String updateTime;

    /*TODO：文件大小从哪里获取？*/
    private String fileSize;

    private String fileType;

    private String fileDescription;

    private Boolean downFlag;

}
