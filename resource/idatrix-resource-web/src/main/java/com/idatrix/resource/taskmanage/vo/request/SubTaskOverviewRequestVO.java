package com.idatrix.resource.taskmanage.vo.request;

/**
 * 交换任务请求参数
 */
public class SubTaskOverviewRequestVO{

    /*作业名称*/
    private String taskName;

    /*资源代码*/
    private String code;

    /*订阅方*/
    private String subscribeDept;

    /*提供方*/
    private String provideDept;

    /*任务状态*/
    private String taskStatus;

    /*当前页码 (必填)*/
    private int page;

    /*每页显示数量 (必填)*/
    private int pageSize;

    SubTaskOverviewRequestVO(){
        super();
        this.page=1;
        this.pageSize=10;
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

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSubscribeDept() {
        return subscribeDept;
    }

    public void setSubscribeDept(String subscribeDept) {
        this.subscribeDept = subscribeDept;
    }

    public String getProvideDept() {
        return provideDept;
    }

    public void setProvideDept(String provideDept) {
        this.provideDept = provideDept;
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }

    @Override
    public String toString() {
        return "SubTaskOverviewRequestVO{" +
                "taskName='" + taskName + '\'' +
                ", code='" + code + '\'' +
                ", subscribeDept='" + subscribeDept + '\'' +
                ", provideDept='" + provideDept + '\'' +
                ", taskStatus='" + taskStatus + '\'' +
                ", page=" + page +
                ", pageSize=" + pageSize +
                '}';
    }
}
