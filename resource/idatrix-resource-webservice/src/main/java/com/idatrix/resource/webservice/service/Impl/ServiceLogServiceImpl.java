package com.idatrix.resource.webservice.service.Impl;

import com.idatrix.resource.webservice.common.CommonConstants;
import com.idatrix.resource.webservice.common.CommonServiceException;
import com.idatrix.resource.webservice.dao.ResourceConfigDAO;
import com.idatrix.resource.webservice.dao.ServiceLogDAO;
import com.idatrix.resource.webservice.dao.ServiceLogDetailDAO;
import com.idatrix.resource.webservice.dao.SourceServiceDAO;
import com.idatrix.resource.webservice.po.ResourceConfigPO;
import com.idatrix.resource.webservice.po.ServiceLogDetailPO;
import com.idatrix.resource.webservice.po.ServiceLogPO;
import com.idatrix.resource.webservice.po.SourceServicePO;
import com.idatrix.resource.webservice.service.IServiceLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Transactional
@Service("serviceLogService")
@PropertySource("classpath:init.properties")
public class ServiceLogServiceImpl implements IServiceLogService {

    @Autowired
    private ResourceConfigDAO resourceConfigDAO;

    @Autowired
    private SourceServiceDAO sourceServiceDAO;

    @Autowired
    private ServiceLogDAO serviceLogDAO;

    @Autowired
    private ServiceLogDetailDAO serviceLogDetailDAO;

    @Value("${subscribe.webservice.name}")
    private String serviceName;

    @Value("${subscribe.webservice.code}")
    private String serviceCode;

    public ServiceLogServiceImpl(){
        super();
    }

    @Override
    public void insertServiceLogRecord(Long resourceId, Long callerDeptId, String callerDeptName, int isSuccess,
        int execTime, String input, String output, String errorMsg, String errorStack, String userName)
            throws CommonServiceException {
        ResourceConfigPO resourceConfigPO = resourceConfigDAO.getConfigById(resourceId);

        if (resourceConfigPO == null)
            throw new CommonServiceException(CommonConstants.EC_NOT_EXISTED_VALUE, new Date() + " 创建服务日志信息时对应资源信息" +
            resourceId + "不存在");

        SourceServicePO sourceServicePO = sourceServiceDAO.getSourceServiceById(resourceConfigPO.getBindServiceId());

//        if (sourceServicePO == null)
//            throw new CommonServiceException(CommonConstants.EC_NOT_EXISTED_VALUE, new Date() + " 创建服务日志信息时对应资源信息" +
//                    resourceId + "所绑定的共享服务" + resourceConfigPO.getBindServiceId() + "不存在");
        //资源没有绑定服务却进行服务开放时，该资源可能为数据库资源  Added By robin 2018/09/17
        if(sourceServicePO==null){
            sourceServicePO = new SourceServicePO();
            sourceServicePO.setServiceCode(serviceCode);
            sourceServicePO.setServiceName(serviceName);
            sourceServicePO.setServiceType("SOAP"); //RESTful
        }

        try {
            ServiceLogPO serviceLogPO = new ServiceLogPO();

            serviceLogPO.setServiceCode(sourceServicePO.getServiceCode());
            serviceLogPO.setServiceName(sourceServicePO.getServiceName());
            serviceLogPO.setServiceType(sourceServicePO.getServiceType());

            serviceLogPO.setCallerDeptId(callerDeptId);
            serviceLogPO.setCallerDeptName(callerDeptName);
            serviceLogPO.setExecTime(execTime);
            serviceLogPO.setIsSuccess(isSuccess);
            serviceLogPO.setCreateTime(new Date());
            serviceLogPO.setCreator(userName);
            serviceLogPO.setModifier(userName);
            serviceLogPO.setModifyTime(new Date());

            serviceLogDAO.insertServiceLogPO(serviceLogPO);

            ServiceLogDetailPO serviceLogDetailPO = new ServiceLogDetailPO();
            serviceLogDetailPO.setParentId(serviceLogPO.getId());
            serviceLogDetailPO.setInput(input.getBytes("utf-8"));
            serviceLogDetailPO.setOutput(output.getBytes("utf-8"));
            if(errorMsg!=null) {
                serviceLogDetailPO.setErrorMessage(errorMsg.length() < 500 ? errorMsg : errorMsg.substring(0, 500));
            }
            serviceLogDetailPO.setErrorStack(errorStack.getBytes("utf-8"));
            serviceLogDetailPO.setCreateTime(new Date());
            serviceLogDetailPO.setCreator(userName);
            serviceLogDetailPO.setModifier(userName);
            serviceLogDetailPO.setModifyTime(new Date());

            serviceLogDetailDAO.insertServiceLogDetailPO(serviceLogDetailPO);
        } catch (Exception e) {
            throw new CommonServiceException(CommonConstants.EC_UNEXPECTED, new Date() + " 创建服务日志信息时发生错误 " +
                    resourceId + " " + e.getMessage());
        }
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }
}
