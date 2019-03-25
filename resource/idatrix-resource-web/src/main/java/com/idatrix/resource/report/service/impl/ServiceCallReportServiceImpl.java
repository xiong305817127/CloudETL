package com.idatrix.resource.report.service.impl;

import com.idatrix.resource.report.dao.ServiceCallReportDAO;
import com.idatrix.resource.report.service.IServiceCallReportService;
import com.idatrix.resource.report.vo.request.BaseSearchVO;
import com.idatrix.resource.report.vo.request.ServiceSearchVO;
import com.idatrix.resource.report.vo.response.CommonCountVO;
import com.idatrix.resource.report.vo.response.ServiceCountVO;
import com.idatrix.resource.report.vo.response.ServiceStatisticsVO;
import com.idatrix.resource.report.vo.response.ServiceVO;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 服务调用报表服务实现
 *
 * @author wzl
 */
@Service
public class ServiceCallReportServiceImpl implements IServiceCallReportService {

    @Autowired
    private ServiceCallReportDAO serviceCallReportDAO;

    /**
     * 统计服务调用次数
     */
    @Override
    public CommonCountVO<ServiceCountVO> countByNumberOfCalls(BaseSearchVO searchVO) {
        List<ServiceCountVO> list = serviceCallReportDAO.countByNumberOfCalls(searchVO);
        Long total = list.stream().collect(Collectors.summingLong(ServiceCountVO::getCount));
        return CommonCountVO.of(list, total);
    }

    /**
     * 统计服务调用返回数据量
     */
    @Override
    public CommonCountVO<ServiceCountVO> countByTheAmountOfData(BaseSearchVO searchVO) {
        List<ServiceCountVO> list = serviceCallReportDAO.countByTheAmountOfData(searchVO);
        Long total = list.stream().collect(Collectors.summingLong(ServiceCountVO::getCount));
        return CommonCountVO.of(list, total);
    }

    /**
     * 统计服务调用次数、失败次数、成功次数
     */
    @Override
    public ServiceStatisticsVO countByNumberOfTimes(BaseSearchVO searchVO) {
        return serviceCallReportDAO.countByNumberOfTimes(searchVO);
    }

    /**
     * 根据服务编码查询服务列表
     */
    @Override
    public List<ServiceVO> list(ServiceSearchVO searchVO) {
        return serviceCallReportDAO.list(searchVO);
    }
}
