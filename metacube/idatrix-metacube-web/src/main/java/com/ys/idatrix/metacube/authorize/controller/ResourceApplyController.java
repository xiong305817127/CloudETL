package com.ys.idatrix.metacube.authorize.controller;

import com.idatrix.unisecurity.sso.client.enums.ResultEnum;
import com.ys.idatrix.metacube.api.beans.PageResultBean;
import com.ys.idatrix.metacube.api.beans.ResultBean;
import com.ys.idatrix.metacube.common.exception.MetaDataException;
import com.ys.idatrix.metacube.common.group.Save;
import com.ys.idatrix.metacube.metamanage.domain.ApprovalProcess;
import com.ys.idatrix.metacube.metamanage.domain.ResourceAuth;
import com.ys.idatrix.metacube.authorize.service.AuthorityService;
import com.ys.idatrix.metacube.metamanage.vo.request.ApprovalProcessSearchVo;
import com.ys.idatrix.metacube.metamanage.vo.request.ApprovalProcessVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @ClassName ResourceApplyController
 * @Description
 * @Author ouyang
 * @Date
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/resource/apply")
@Api(value = "/ResourceApplyController", tags = "元数据管理-权限管理-用户授权申请处理接口")
public class ResourceApplyController {

    @Autowired
    private AuthorityService authorityService;

    @ApiOperation(value = "权限管理-授权申请列表搜索", notes = "带分页", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "resourceOrganisationCodes", value = "资源所属组织，可以传递多个，以,隔开", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "databaseType", value = "资源数据库类型 1:MYSQL 2:ORACLE 3:DM 4:POSTGRESQL 5:HIVE 6:HBASE 7:HDFS 8:ELASTICSEARCH", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "status", value = "授权状态：1-申请中 2-通过 3-不通过 4-已回收 5-已撤回，多个以逗号隔开", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "页码", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "页大小", dataType = "int", paramType = "query"),
    })
    @GetMapping("/search")
    public ResultBean<PageResultBean<ApprovalProcessVO>> search(@RequestParam(value = "resourceOrganisationCodes", required = false) String resourceOrganisationCodes,
                                                                @RequestParam(value = "databaseType", required = false) Integer databaseType,
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
        search.setDatabaseType(databaseType);
        search.setResourceOrganisationCodes(resourceOrganisationCodes == null ? null : Arrays.asList(resourceOrganisationCodes.split(",")));
        PageResultBean<ApprovalProcessVO> result = authorityService.searchResourceApplyList(search);
        return ResultBean.ok(result);
    }

    @ApiOperation(value = "权限管理-判断当前用户所属组织是否已经有当前元数据的权限 或 已经申请过（正在申请，已通过）", notes = "", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "metadataId", value = "元数据ID", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "resourceType", value = "资源数据库类型 1:MYSQL 2:ORACLE 3:DM 4:POSTGRESQL 5:HIVE 6:HBASE 7:HDFS 8:ELASTICSEARCH", dataType = "Int", paramType = "query"),
    })
    @RequestMapping("/userIfAuthority")
    public ResultBean<Boolean> userIfAuthority(@NotNull(message = "元数据ID不能为空") Long metadataId,
                                               @NotNull(message = "资源数据库类型不能为空") Integer resourceType) {
        Boolean result = authorityService.userIfAuthority(metadataId, resourceType);
        return ResultBean.ok(result);
    }

    @ApiOperation(value = "权限管理-根据元数据ID获取可授权列表", notes = "", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "metadataId", value = "元数据ID", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "resourceType", value = "资源数据库类型 1:MYSQL 2:ORACLE 3:DM 4:POSTGRESQL 5:HIVE 6:HBASE 7:HDFS 8:ELASTICSEARCH", dataType = "Int", paramType = "query"),
    })
    @RequestMapping("/authority/list")
    public ResultBean<List<ResourceAuth>> authorityList(@NotNull(message = "元数据ID不能为空") Long metadataId,
                                                        @NotNull(message = "资源数据库类型不能为空") Integer resourceType) {
        List<ResourceAuth> result = authorityService.getAuthorityListByMetadataId(metadataId, resourceType);
        return ResultBean.ok(result);
    }

    @ApiOperation(value = "权限管理-用户对某条元数据申请权限", notes = "权限归属于当前用户的归属组织", httpMethod = "POST")
    @ApiImplicitParam(name = "approvalProcess", value = "权限申请实体类", dataType = "ApprovalProcess", paramType = "body")
    @PostMapping("/user/apply/authority")
    public ResultBean userApplyAuthority(@Validated(Save.class) @RequestBody ApprovalProcess approvalProcess, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new MetaDataException(ResultEnum.PARAM_ERROR.getCode(), bindingResult.getFieldError().getDefaultMessage());
        }
        authorityService.userApplyAuthority(approvalProcess);
        return ResultBean.ok();
    }

}