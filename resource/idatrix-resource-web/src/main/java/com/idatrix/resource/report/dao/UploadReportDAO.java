package com.idatrix.resource.report.dao;

import com.idatrix.resource.report.vo.request.BaseSearchVO;
import com.idatrix.resource.report.vo.request.DataReportSearchVO;
import com.idatrix.resource.report.vo.response.DataReportCountVO;
import com.idatrix.resource.report.vo.response.DataReportVO;
import java.util.List;

public interface UploadReportDAO {

    List<DataReportCountVO> countByNumberOfTasks(BaseSearchVO searchVO);

    List<DataReportCountVO> countByTheAmountOfData(BaseSearchVO searchVO);

    List<DataReportVO> list(DataReportSearchVO searchVO);
}
