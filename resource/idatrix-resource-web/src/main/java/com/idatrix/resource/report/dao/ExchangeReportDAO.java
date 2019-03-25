package com.idatrix.resource.report.dao;

import com.idatrix.resource.report.vo.request.BaseSearchVO;
import com.idatrix.resource.report.vo.request.DataReportSearchVO;
import com.idatrix.resource.report.vo.request.ExchangeSearchVO;
import com.idatrix.resource.report.vo.response.ExchangeCountVO;
import com.idatrix.resource.report.vo.response.ExchangeVO;
import java.util.List;

public interface ExchangeReportDAO {

    List<ExchangeCountVO> countByNumberOfTasks(BaseSearchVO searchVO);

    List<ExchangeCountVO> countByTheAmountOfData(BaseSearchVO searchVO);

    List<ExchangeVO> list(ExchangeSearchVO searchVO);
}
