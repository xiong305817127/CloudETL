package com.idatrix.resource.report.controller;

import com.idatrix.resource.common.utils.Result;
import com.idatrix.resource.common.utils.UserUtils;
import com.idatrix.resource.report.service.IServiceCallReportService;
import com.idatrix.resource.report.vo.request.BaseSearchVO;
import com.idatrix.resource.report.vo.request.ServiceSearchVO;
import com.idatrix.resource.report.vo.response.CommonCountVO;
import com.idatrix.resource.report.vo.response.ServiceCountVO;
import com.idatrix.resource.report.vo.response.ServiceStatisticsVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/report/services")
@Api(value = "/report/services", tags = "服务调用统计报表")
public class ServiceCallReportController {

    @Autowired
    private IServiceCallReportService serviceCallReportService;

    @Autowired
    private UserUtils userUtils;

    @ApiOperation("服务调用总次数、调用次数最多的前N个接口")
    @RequestMapping(value = "countByNumberOfCalls", method = RequestMethod.GET)
    public Result<CommonCountVO<ServiceCountVO>> countByNumberOfCalls(BaseSearchVO searchVO) {
        searchVO.setStartTimeAndEndTime();
        searchVO.setRenterId(userUtils.getCurrentUserRentId());
        return Result.ok(serviceCallReportService.countByNumberOfCalls(searchVO));
    }

    @ApiOperation("服务调用总次数、成功次数、失败次数")
    @RequestMapping(value = "countByNumberOfTimes", method = RequestMethod.GET)
    public Result<ServiceStatisticsVO> countByNumberOfTimes(BaseSearchVO searchVO) {
        searchVO.setStartTimeAndEndTime();
        searchVO.setRenterId(userUtils.getCurrentUserRentId());
        return Result.ok(serviceCallReportService.countByNumberOfTimes(searchVO));
    }

    @ApiOperation("服务调用数据量总数、调用数据量最多的前N个接口")
    @RequestMapping(value = "countByTheAmountOfData", method = RequestMethod.GET)
    public Result<CommonCountVO<ServiceCountVO>> countByTheAmountOfData(BaseSearchVO searchVO) {
        searchVO.setStartTimeAndEndTime();
        searchVO.setRenterId(userUtils.getCurrentUserRentId());
        return Result.ok(serviceCallReportService.countByTheAmountOfData(searchVO));
    }

    @ApiOperation("查看服务调用列表")
    @RequestMapping(method = RequestMethod.GET)
    public Result<List<ServiceCountVO>> list(ServiceSearchVO searchVO) {
        searchVO.setStartTimeAndEndTime();
        searchVO.setRenterId(userUtils.getCurrentUserRentId());
        return Result.ok(serviceCallReportService.list(searchVO));
    }
}
