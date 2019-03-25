package com.idatrix.resource.report.controller;

import com.idatrix.resource.common.utils.Result;
import com.idatrix.resource.common.utils.UserUtils;
import com.idatrix.resource.report.service.IExchangeReportService;
import com.idatrix.resource.report.vo.request.BaseSearchVO;
import com.idatrix.resource.report.vo.request.ExchangeSearchVO;
import com.idatrix.resource.report.vo.response.CommonCountVO;
import com.idatrix.resource.report.vo.response.ExchangeCountVO;
import com.idatrix.resource.report.vo.response.ExchangeVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/report/exchange")
@Api(value = "/report/exchange", tags = "交换任务统计报表")
public class ExchangeReportController {

    @Autowired
    private IExchangeReportService exchangeReportService;

    @Autowired
    private UserUtils userUtils;

    @ApiOperation("统计交换任务执行次数")
    @RequestMapping(value = "countByNumberOfTasks", method = RequestMethod.GET)
    public Result<CommonCountVO<ExchangeCountVO>> countByNumberOfTasks(BaseSearchVO searchVO) {
        searchVO.setStartTimeAndEndTime();
        searchVO.setRenterId(userUtils.getCurrentUserRentId());
        return Result.ok(exchangeReportService.countByNumberOfTasks(searchVO));
    }

    @ApiOperation("统计交换数据量")
    @RequestMapping(value = "countByTheAmountOfData", method = RequestMethod.GET)
    public Result<CommonCountVO<ExchangeCountVO>> countByTheAmountOfData(BaseSearchVO searchVO) {
        searchVO.setStartTimeAndEndTime();
        searchVO.setRenterId(userUtils.getCurrentUserRentId());
        return Result.ok(exchangeReportService.countByTheAmountOfData(searchVO));
    }

    @ApiOperation("查询交换任务列表")
    @RequestMapping(method = RequestMethod.GET)
    public Result<List<ExchangeVO>> list(ExchangeSearchVO searchVO) {
        searchVO.setStartTimeAndEndTime();
        searchVO.setRenterId(userUtils.getCurrentUserRentId());
        return Result.ok(exchangeReportService.list(searchVO));
    }
}
