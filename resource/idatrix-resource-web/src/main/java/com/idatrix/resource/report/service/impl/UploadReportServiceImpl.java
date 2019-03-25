package com.idatrix.resource.report.service.impl;

import com.idatrix.resource.report.dao.UploadReportDAO;
import com.idatrix.resource.report.service.IUploadReportService;
import com.idatrix.resource.report.vo.request.BaseSearchVO;
import com.idatrix.resource.report.vo.request.DataReportSearchVO;
import com.idatrix.resource.report.vo.response.CommonCountVO;
import com.idatrix.resource.report.vo.response.DataReportCountVO;
import com.idatrix.resource.report.vo.response.DataReportVO;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UploadReportServiceImpl implements IUploadReportService {

    @Autowired
    private UploadReportDAO uploadReportDAO;

    /**
     * 统计上报任务数量
     */
    @Override
    public CommonCountVO<DataReportCountVO> countByNumberOfTasks(BaseSearchVO searchVO) {
        List<DataReportCountVO> list = uploadReportDAO.countByNumberOfTasks(searchVO);
        Long total = list.stream().collect(Collectors.summingLong(DataReportCountVO::getCount));
        return CommonCountVO.of(list, total);
    }

    /**
     * 统计上报数据量
     */
    @Override
    public CommonCountVO<DataReportCountVO> countByTheAmountOfData(BaseSearchVO searchVO) {
        List<DataReportCountVO> list = uploadReportDAO.countByTheAmountOfData(searchVO);
        Long total = list.stream().collect(Collectors.summingLong(DataReportCountVO::getCount));
        return CommonCountVO.of(list, total);
    }

    /**
     * 根据部门编码查询上报任务列表
     */
    @Override
    public List<DataReportVO> list(DataReportSearchVO searchVO) {
        return uploadReportDAO.list(searchVO);
    }
}
