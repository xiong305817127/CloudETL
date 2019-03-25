package com.idatrix.resource.catalog.po;

import lombok.Data;

import java.util.Date;

/**
 * Created by Robin Wing on 2018-5-23.
 * @Author: Wangbin
 * @Date: 2017/6/5
 */
@Data
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

}
