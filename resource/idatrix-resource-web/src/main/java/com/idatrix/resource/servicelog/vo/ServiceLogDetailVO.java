package com.idatrix.resource.servicelog.vo;

import lombok.Data;

@Data
public class ServiceLogDetailVO {
    /*服务日志关联ID*/
    private Long parentId;

    /*输入参数JSON*/
    private String input;

    /*输出结果JSON*/
    private String output;

    /*错误信息:输入参数校验失败，无调用权限，调用失败*/
    private String errorMessage;

    /*错误堆栈*/
    private String errorStack;

    /*执行时长*/
    private Integer execTime;

    /*调用时间*/
    private String callTime;


}
