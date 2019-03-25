package com.idatrix.resource.report.controller;

import com.idatrix.resource.common.utils.Result;
import com.idatrix.resource.common.utils.UserUtils;
import com.idatrix.resource.report.service.IResourceReportService;
import com.idatrix.resource.report.vo.request.BaseSearchVO;
import com.idatrix.resource.report.vo.request.ResourceSearchVO;
import com.idatrix.resource.report.vo.response.ResourceCountVO;
import com.idatrix.resource.report.vo.response.ResourceVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/report/resources")
@Api(value = "/report/resources", tags = "资源目录统计报表")
public class ResourceReportController {

    @Autowired
    private IResourceReportService resourceReportService;

    @Autowired
    private UserUtils userUtils;

    @ApiOperation("统计注册量、订阅量、发布量")
    @RequestMapping(value = "count", method = RequestMethod.GET)
    public Result<ResourceCountVO> count(BaseSearchVO searchVO) {
        searchVO.setStartTimeAndEndTime();
        searchVO.setRenterId(userUtils.getCurrentUserRentId());
        return Result.ok(resourceReportService.count(searchVO));
    }

    @ApiOperation("查看资源目录列表")
    @RequestMapping(method = RequestMethod.GET)
    public Result<List<ResourceVO>> list(ResourceSearchVO searchVO) {
        searchVO.setRenterId(userUtils.getCurrentUserRentId());
        searchVO.setStartTimeAndEndTime();
        return Result.ok(resourceReportService.list(searchVO));
    }
}
