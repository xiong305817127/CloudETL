package com.idatrix.resource.servicelog.po;

import lombok.Data;

import java.util.Date;

@Data
public class ServiceLogDetailPO {
    /*主键*/
    private Long id;

    /*服务日志关联ID*/
    private Long parentId;

    /*输入参数JSON*/
    private byte input[];

    /*输出结果JSON*/
    private byte output[];

    /*错误信息:输入参数校验失败，无调用权限，调用失败*/
    private String errorMessage;

    /*错误堆栈*/
    private byte errorStack[];

    private String creator;

    private Date createTime;

    private String modifier;

    private Date modifyTime;

}
