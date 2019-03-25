package com.idatrix.resource.report.service;

import com.idatrix.resource.report.vo.request.BaseSearchVO;
import com.idatrix.resource.report.vo.request.DataReportSearchVO;
import com.idatrix.resource.report.vo.response.CommonCountVO;
import com.idatrix.resource.report.vo.response.DataReportCountVO;
import com.idatrix.resource.report.vo.response.DataReportVO;
import java.util.List;

/**
 * 数据上报报表服务
 *
 * @author wzl
 */
public interface IUploadReportService {

    /**
     * 统计上报任务数量
     */
    CommonCountVO<DataReportCountVO> countByNumberOfTasks(BaseSearchVO searchVO);

    /**
     * 统计上报数据量
     */
    CommonCountVO<DataReportCountVO> countByTheAmountOfData(BaseSearchVO searchVO);

    /**
     * 根据部门编码查询上报任务列表
     */
    List<DataReportVO> list(DataReportSearchVO searchVO);
}
