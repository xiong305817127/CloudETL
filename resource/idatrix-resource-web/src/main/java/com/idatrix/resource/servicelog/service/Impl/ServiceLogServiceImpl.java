package com.idatrix.resource.servicelog.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.idatrix.resource.common.utils.DateTools;
import com.idatrix.resource.common.utils.ResultPager;
import com.idatrix.resource.servicelog.dao.ServiceLogDAO;
import com.idatrix.resource.servicelog.dao.ServiceLogDetailDAO;
import com.idatrix.resource.servicelog.po.ServiceLogDetailPO;
import com.idatrix.resource.servicelog.po.ServiceLogPO;
import com.idatrix.resource.servicelog.service.IServiceLogService;
import com.idatrix.resource.servicelog.vo.ServiceLogDetailVO;
import com.idatrix.resource.servicelog.vo.ServiceLogVO;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Transactional
@Service("serviceLogService")
public class ServiceLogServiceImpl implements IServiceLogService {

    @Autowired
    public ServiceLogServiceImpl(ServiceLogDAO serviceLogDAO, ServiceLogDetailDAO serviceLogDetailDAO) {
        this.serviceLogDAO = serviceLogDAO;
        this.serviceLogDetailDAO = serviceLogDetailDAO;
    }

    private final ServiceLogDAO serviceLogDAO;

    private final ServiceLogDetailDAO serviceLogDetailDAO;

    @Override
    public ResultPager<ServiceLogVO> getServiceLogInfoByCondition(Map<String, Object> condition, Integer pageNum,
                                                                  Integer pageSize) {
        pageNum = null == pageNum ? 1 : pageNum;
        pageSize = null == pageSize ? 10 : pageSize;
        PageHelper.startPage(pageNum, pageSize);

        List<ServiceLogVO> servicesVOList = new ArrayList<ServiceLogVO>();
        List<ServiceLogPO> serviceLogPOList = serviceLogDAO.getServiceLogInfoByCondition(condition);

        if(CollectionUtils.isNotEmpty(serviceLogPOList)){

            for (ServiceLogPO model : serviceLogPOList) {

                ServiceLogVO serviceLogVO = convertFromPOToVO(model);
                servicesVOList.add(serviceLogVO);
            }

            //用PageInfo对结果进行包装
            PageInfo<ServiceLogPO> pi = new PageInfo<ServiceLogPO>(serviceLogPOList);
            Long totalNum = pi.getTotal();
            ResultPager<ServiceLogVO> rp = new ResultPager<ServiceLogVO>(pi.getPageNum(),
                    totalNum, servicesVOList);
            return rp;
        }

        return null;
    }

    private ServiceLogVO convertFromPOToVO (ServiceLogPO serviceLogPO) {
        ServiceLogVO svo = new ServiceLogVO();
        svo.setId(serviceLogPO.getId());
        svo.setServiceCode(serviceLogPO.getServiceCode());
        svo.setServiceName(serviceLogPO.getServiceName());
        svo.setServiceType(serviceLogPO.getServiceType());
        svo.setCallerDeptName(serviceLogPO.getCallerDeptName());
        svo.setCallTime(DateTools.getDateTime(serviceLogPO.getCreateTime()));
        BigDecimal execTime = new BigDecimal(serviceLogPO.getExecTime());
        svo.setExecTime(execTime.divide(new BigDecimal(1000)).toString()+"s");
        svo.setIsSuccess(serviceLogPO.getIsSuccess());

        return svo;
    }

    @Override
    public ServiceLogDetailVO getServiceLogDetailById(Long id) {
        ServiceLogPO serviceLogPO = serviceLogDAO.getServiceLogById(id);
        ServiceLogDetailPO serviceLogDetailPO = serviceLogDetailDAO.getServiceLogDetailByParentId(id);

        if (serviceLogPO == null) {
            return null;
        }
        ServiceLogDetailVO serviceLogDetailVO = new ServiceLogDetailVO();

        if (serviceLogPO != null) {
            serviceLogDetailVO.setParentId(id);
            serviceLogDetailVO.setCallTime(DateTools.getDateTime(serviceLogPO.getCreateTime()));
            serviceLogDetailVO.setExecTime(serviceLogPO.getExecTime());
        }
        if (serviceLogDetailPO != null) {
            serviceLogDetailVO.setInput(new String(serviceLogDetailPO.getInput()));
            serviceLogDetailVO.setOutput(new String(serviceLogDetailPO.getOutput()));
            serviceLogDetailVO.setErrorMessage(serviceLogDetailPO.getErrorMessage());
            serviceLogDetailVO.setErrorStack(new String(serviceLogDetailPO.getErrorStack()));
        }
        return serviceLogDetailVO;
    }

    /**
     * 获取最新几个被调用的服务信息
     *
     * @param num
     * @return
     */
    @Override
    public List<ServiceLogPO> getLastestCalledService(Long num) {

        return serviceLogDAO.getLastestServiceLog(num);
    }
}
