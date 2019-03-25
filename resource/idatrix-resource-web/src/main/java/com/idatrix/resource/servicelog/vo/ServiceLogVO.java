package com.idatrix.resource.servicelog.vo;


import com.idatrix.resource.common.utils.DateTools;
import com.idatrix.resource.servicelog.po.ServiceLogPO;
import lombok.Data;

@Data
public class ServiceLogVO {

    /*主键*/
    private Long id;

    /*服务名称*/
    private String serviceName;

    /*服务类型: HTTP/WEBSERVICE*/
    private String serviceType;

    /*服务代码*/
    private String serviceCode;

    /*调用方部门名称*/
    private String callerDeptName;

    /*调用时间*/
    private String callTime;

    /*执行时长*/
    private String execTime;

    /*是否成功：0失败，1成功*/
    private Integer isSuccess;

    public ServiceLogVO(){super();}

    public ServiceLogVO(ServiceLogPO slPO){
        this.id = slPO.getId();
        this.serviceName = slPO.getServiceName();
        this.serviceCode = slPO.getServiceCode();
        this.callerDeptName = slPO.getCallerDeptName();
        this.callTime = DateTools.formatDate(slPO.getCreateTime(), "yyyy年MM月dd日HH时mm分ss秒");
        this.serviceType = slPO.getServiceType();
        this.execTime = slPO.getExecTime().toString();
        this.isSuccess = slPO.getIsSuccess();
    }


}
