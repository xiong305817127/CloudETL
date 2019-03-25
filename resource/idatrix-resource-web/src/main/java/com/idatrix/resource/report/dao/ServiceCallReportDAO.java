package com.idatrix.resource.report.dao;

import com.idatrix.resource.report.vo.request.BaseSearchVO;
import com.idatrix.resource.report.vo.request.ServiceSearchVO;
import com.idatrix.resource.report.vo.response.ServiceCountVO;
import com.idatrix.resource.report.vo.response.ServiceStatisticsVO;
import com.idatrix.resource.report.vo.response.ServiceVO;
import java.util.List;

public interface ServiceCallReportDAO {

    List<ServiceCountVO> countByNumberOfCalls(BaseSearchVO searchVO);

    List<ServiceCountVO> countByTheAmountOfData(BaseSearchVO searchVO);

    ServiceStatisticsVO countByNumberOfTimes(BaseSearchVO searchVO);

    List<ServiceVO> list(ServiceSearchVO searchVO);
}
