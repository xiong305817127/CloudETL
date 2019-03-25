package com.idatrix.resource.report.service;

import com.idatrix.resource.report.vo.request.BaseSearchVO;
import com.idatrix.resource.report.vo.request.DataReportSearchVO;
import com.idatrix.resource.report.vo.request.ExchangeSearchVO;
import com.idatrix.resource.report.vo.response.CommonCountVO;
import com.idatrix.resource.report.vo.response.ExchangeCountVO;
import com.idatrix.resource.report.vo.response.ExchangeVO;
import java.util.List;

/**
 * 数据交换报表服务
 *
 * @author wzl
 */
public interface IExchangeReportService {

    /**
     * 统计交换任务执行次数
     */
    CommonCountVO<ExchangeCountVO> countByNumberOfTasks(BaseSearchVO searchVO);

    /**
     * 统计交换数据量
     */
    CommonCountVO<ExchangeCountVO> countByTheAmountOfData(BaseSearchVO searchVO);

    /**
     * 根据部门id查询交换任务列表
     */
    List<ExchangeVO> list(ExchangeSearchVO searchVO);
}
