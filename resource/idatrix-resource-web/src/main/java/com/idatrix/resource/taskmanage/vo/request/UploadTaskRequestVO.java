package com.idatrix.resource.taskmanage.vo.request;

import lombok.Data;

/**
 * 上传任务概览查询信息
 */
@Data
public class UploadTaskRequestVO {

    /*作业名称*/
    private String taskName;

    /*部门*/
    private String deptName;

    /*类型*/
    private String taskType;

    /*任务当前状态*/
    private String status;

    /*当前页码 (必填)*/
    private int page;

    /*每页显示数量 (必填)*/
    private int pageSize;

    UploadTaskRequestVO(){
        super();
        this.page=1;
        this.pageSize=10;
    }


}
