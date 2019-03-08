package com.idatrix.resource.catalog.po;

import java.util.Date;

/**
 * Created by Robin Wing on 2018-5-23.
 * @Author: Wangbin
 * @Date: 2017/6/5
 */
public class DeptLimitedPO {

    /*ID(自动增加)*/
    private Long id;

    /*引用资源标识符id */
    private Long resourceId;

    /*引用部门id*/
    private Long deptId;

    /*状态(软删除用n)*/
    private String status;

    /*创建时间 */
    private Date createTime;

    /*创建人 */
    private String creator;

    /*更新时间*/
    private Date updateTime;

    /*更新人 */
    private String updater;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getUpdater() {
        return updater;
    }

    public void setUpdater(String updater) {
        this.updater = updater;
    }
}
