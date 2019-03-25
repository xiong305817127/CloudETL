package com.idatrix.resource.report.controller;

import com.idatrix.resource.common.utils.Result;
import com.idatrix.resource.common.utils.UserUtils;
import com.idatrix.resource.report.service.IUploadReportService;
import com.idatrix.resource.report.vo.request.BaseSearchVO;
import com.idatrix.resource.report.vo.request.DataReportSearchVO;
import com.idatrix.resource.report.vo.response.CommonCountVO;
import com.idatrix.resource.report.vo.response.DataReportCountVO;
import com.idatrix.resource.report.vo.response.DataReportVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/report/data-report")
@Api(value = "/report/data-report", tags = "数据上报统计报表")
public class UploadReportController {

    @Autowired
    private IUploadReportService uploadReportService;

    @Autowired
    private UserUtils userUtils;

    @ApiOperation("统计上报任务数量")
    @RequestMapping(value = "countByNumberOfTasks", method = RequestMethod.GET)
    public Result<CommonCountVO<DataReportCountVO>> countByNumberOfTasks(BaseSearchVO searchVO) {
        searchVO.setStartTimeAndEndTime();
        searchVO.setRenterId(userUtils.getCurrentUserRentId());
        return Result.ok(uploadReportService.countByNumberOfTasks(searchVO));
    }

    @ApiOperation("统计上报数据量")
    @RequestMapping(value = "countByTheAmountOfData", method = RequestMethod.GET)
    public Result<CommonCountVO<DataReportCountVO>> countByTheAmountOfData(BaseSearchVO searchVO) {
        searchVO.setStartTimeAndEndTime();
        searchVO.setRenterId(userUtils.getCurrentUserRentId());
        return Result.ok(uploadReportService.countByTheAmountOfData(searchVO));
    }

    @ApiOperation("查询上报任务列表")
    @RequestMapping(method = RequestMethod.GET)
    public Result<List<DataReportVO>> list(DataReportSearchVO searchVO) {
        searchVO.setStartTimeAndEndTime();
        searchVO.setRenterId(userUtils.getCurrentUserRentId());
        return Result.ok(uploadReportService.list(searchVO));
    }
}
