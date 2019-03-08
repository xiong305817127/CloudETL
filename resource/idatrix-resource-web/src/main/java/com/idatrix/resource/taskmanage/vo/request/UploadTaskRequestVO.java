package com.idatrix.resource.taskmanage.vo.request;

/**
 * 上传任务概览查询信息
 */
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

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
