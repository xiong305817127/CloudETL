package com.idatrix.resource.report.service;

import com.idatrix.resource.report.vo.request.BaseSearchVO;
import com.idatrix.resource.report.vo.request.ServiceSearchVO;
import com.idatrix.resource.report.vo.response.CommonCountVO;
import com.idatrix.resource.report.vo.response.ServiceCountVO;
import com.idatrix.resource.report.vo.response.ServiceStatisticsVO;
import com.idatrix.resource.report.vo.response.ServiceVO;
import java.util.List;

/**
 * 服务调用报表服务
 *
 * @author wzl
 */
public interface IServiceCallReportService {

    /**
     * 统计服务调用次数
     */
    CommonCountVO<ServiceCountVO> countByNumberOfCalls(BaseSearchVO searchVO);

    /**
     * 统计服务调用返回数据量
     */
    CommonCountVO<ServiceCountVO> countByTheAmountOfData(BaseSearchVO searchVO);

    /**
     * 统计服务调用次数、失败次数、成功次数
     */
    ServiceStatisticsVO countByNumberOfTimes(BaseSearchVO searchVO);

    /**
     * 根据服务编码查询服务列表
     */
    List<ServiceVO> list(ServiceSearchVO searchVO);
}
