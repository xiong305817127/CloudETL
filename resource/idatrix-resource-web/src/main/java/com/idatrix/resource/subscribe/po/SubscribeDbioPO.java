package com.idatrix.resource.subscribe.po;

import lombok.Data;

import java.util.Date;

/**
 * 订阅数据库类输入输出定义表
 */
@Data
public class SubscribeDbioPO {

    /*主键*/
    private Long id;

    /*订阅ID*/
    private Long subscribeId;

    /*参数类型 input,output, input 表示订阅资源，output表示搜索条件*/
    private String paramType;

    /*信息项ID*/
    private Long columnId;

    /*创建人*/
    private String creator;

    /*创建时间*/
    private Date createTime;

    /*修改人*/
    private String modifier;

    /*修改时间*/
    private Date modifyTime;

    /*数据脱敏方式:(mask/cut)掩码/截取(唯一标识符不能够脱敏、
    只有字符串数据才能脱敏只有param_type是input类型才有效)*/
    private String dataMaskingType;

    /*数据开始位置,默认为0*/
    private int dataStartIndex;

    /*处理数据长度，默认为1*/
    private int dataLength;

    public SubscribeDbioPO(){
        super();
        dataStartIndex = 0;
        dataLength = 1;
    }

}
