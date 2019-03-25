package com.idatrix.resource.report.service.impl;

import com.idatrix.resource.common.utils.CommonConstants;
import com.idatrix.resource.report.dao.ExchangeReportDAO;
import com.idatrix.resource.report.service.IExchangeReportService;
import com.idatrix.resource.report.vo.request.BaseSearchVO;
import com.idatrix.resource.report.vo.request.ExchangeSearchVO;
import com.idatrix.resource.report.vo.response.CommonCountVO;
import com.idatrix.resource.report.vo.response.ExchangeCountVO;
import com.idatrix.resource.report.vo.response.ExchangeVO;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExchageReportServiceImpl implements IExchangeReportService {

    @Autowired
    private ExchangeReportDAO exchangeReportDAO;

    /**
     * 统计交换任务执行次数
     */
    @Override
    public CommonCountVO<ExchangeCountVO> countByNumberOfTasks(BaseSearchVO searchVO) {
        List<ExchangeCountVO> list = exchangeReportDAO.countByNumberOfTasks(searchVO);
        Long total = list.stream().collect(Collectors.summingLong(ExchangeCountVO::getCount));
        return CommonCountVO.of(list, total);
    }

    /**
     * 统计交换数据量
     */
    @Override
    public CommonCountVO<ExchangeCountVO> countByTheAmountOfData(BaseSearchVO searchVO) {
        List<ExchangeCountVO> list = exchangeReportDAO.countByTheAmountOfData(searchVO);
        Long total = list.stream().collect(Collectors.summingLong(ExchangeCountVO::getCount));
        return CommonCountVO.of(list, total);
    }

    /**
     * 根据部门id查询交换任务列表
     */
    @Override
    public List<ExchangeVO> list(ExchangeSearchVO searchVO) {
        List<ExchangeVO> list = exchangeReportDAO.list(searchVO);

        return list.stream().map(exchangeVO -> {
            exchangeVO.setStatus(convertStatus(exchangeVO.getStatus()));
            return exchangeVO;
        }).collect(Collectors.toList());
    }

    private String convertStatus(String status) {
        if (CommonConstants.WAIT_IMPORT.equals(status)) {
            return "等待入库";
        }
        if (CommonConstants.IMPORTING.equals(status)) {
            return "入库中";
        }
        if (CommonConstants.IMPORT_COMPLETE.equals(status)) {
            return "已入库";
        }
        if (CommonConstants.STOP_IMPORT.equals(status)) {
            return "终止入库";
        }
        if (CommonConstants.IMPORT_ERROR.equals(status)) {
            return "入库失败";
        }
        if (CommonConstants.NONE_STATUS.equals(status)) {
            return "无状态";
        }
        return null;
    }

}
