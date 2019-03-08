package com.ys.idatrix.metacube.metamanage.controller;

import com.idatrix.unisecurity.api.domain.Organization;
import com.ys.idatrix.metacube.api.beans.ResultBean;
import com.ys.idatrix.metacube.common.utils.UserUtils;
import com.ys.idatrix.metacube.dubbo.consumer.SecurityConsumer;
import com.ys.idatrix.metacube.metamanage.vo.response.MetaDataOrganization;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/organizations")
@Api(value = "api/v1/organizations", tags = "部门组织接口")
public class OrganizationController {

    @Autowired
    private SecurityConsumer securityConsumer;

    @ApiOperation("获取所属组织列表")
    @GetMapping
    public ResultBean<List<MetaDataOrganization>> listAscriptionDept() {
        List<Organization> organizationList = securityConsumer
                .listAscriptionDept(UserUtils.getRenterId());
        List<MetaDataOrganization> orgList = organizationList.stream().map(e -> {
            MetaDataOrganization organization = new MetaDataOrganization();
            BeanUtils.copyProperties(e, organization);
            return organization;
        }).collect(Collectors.toList());

        return ResultBean.ok(orgList);
    }
}
