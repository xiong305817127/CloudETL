package com.ys.idatrix.metacube.metamanage.controller;

import com.ys.idatrix.metacube.api.beans.PageResultBean;
import com.ys.idatrix.metacube.api.beans.ResultBean;
import com.ys.idatrix.metacube.metamanage.service.AuthorityService;
import com.ys.idatrix.metacube.metamanage.vo.request.ApprovalProcessSearchVo;
import com.ys.idatrix.metacube.metamanage.vo.request.ApprovalProcessVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @ClassName AuthorityController
 * @Description
 * @Author ouyang
 * @Date
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/authority")
@Api(value = "/AuthorityController", tags = "元数据管理-权限管理-授权审批处理接口")
public class AuthorityController {

    @Autowired
    private AuthorityService authorityService;

    @ApiOperation(value = "权限管理-授权审批搜索", notes = "带分页", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "proposer", value = "申请人", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "applicationDate", value = "申请时间", dataType = "Date", paramType = "query"),
            @ApiImplicitParam(name = "status", value = "状态：1-申请中 2-通过 3-不通过 4-已回收 5-已撤回，多个以逗号隔开", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "页码", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "页大小", dataType = "int", paramType = "query"),
    })
    @GetMapping("/search")
    public ResultBean<PageResultBean<ApprovalProcessVO>> search(@RequestParam(value = "proposer", required = false) String proposer,
                                                                @RequestParam(value = "applicationDate", required = false) Date applicationDate,
                                                                @RequestParam(value = "status", defaultValue = "1", required = false) String status,
                                                                @RequestParam(value = "pageNum", defaultValue = "1", required = false) Integer pageNum,
                                                                @RequestParam(value = "pageSize", defaultValue = "10", required = false) Integer pageSize) {
        ApprovalProcessSearchVo search = new ApprovalProcessSearchVo();
        search.setPageNum(pageNum);
        search.setPageSize(pageSize);
        List<Integer> statusList = new ArrayList<>();
        String[] statusStr = status.split(",");
        for (String str : statusStr) {
            statusList.add(Integer.parseInt(str));
        }
        search.setStatus(statusList);
        search.setCreator(proposer);
        search.setCreateTime(applicationDate);
        PageResultBean<ApprovalProcessVO> result = authorityService.searchAuthorityApproveList(search);
        return ResultBean.ok(result);
    }

    @ApiOperation(value = "权限管理-批量通过审批资源", notes = "", httpMethod = "PUT")
    @ApiImplicitParam(name = "idList", value = "审批ID集合", dataType = "array", paramType = "body")
    @PutMapping("/batchToPass")
    public ResultBean batchToPass(@NotNull(message = "资源集合不能为空") @RequestBody List<Long> idList) {
        authorityService.batchToPass(idList);
        return ResultBean.ok();
    }

    @ApiOperation(value = "权限管理-批量不通过审批资源", notes = "", httpMethod = "PUT")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", value = "审批ID字符串，可能多个以，隔开", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "opinion", value = "审批意见", dataType = "String", paramType = "query")
    })
    @PutMapping("/batchToNoPass")
    public ResultBean batchToNoPass(@NotBlank(message = "资源不能为空") String ids,
                                    @NotBlank(message = "审批意见不能为空") String opinion) {
        authorityService.batchToNoPass(ids, opinion);
        return ResultBean.ok();
    }

    @ApiOperation(value = "权限管理-批量回收授权", notes = "", httpMethod = "PUT")
    @ApiImplicitParam(name = "idList", value = "审批ID集合", dataType = "array", paramType = "body")
    @PutMapping("/batchToRecycled")
    public ResultBean batchToRecycled(@NotNull(message = "资源集合不能为空") @RequestBody List<Long> idList) {
        authorityService.batchToRecycled(idList);
        return ResultBean.ok();
    }
}