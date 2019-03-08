package com.ys.idatrix.metacube.metamanage.controller;

import com.ys.idatrix.metacube.api.beans.ResultBean;
import com.ys.idatrix.metacube.metamanage.service.AmbariRestApiAdapterService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ambari rest api适配接口
 *
 * @author wzl
 */
@RestController
@RequestMapping(value = "/ambari/api/v1")
@Api(value = "/ambari/api/v1", tags = "Ambari接口")
public class AmbariRestApiAdapterController {

    private static final String AUTHORIZATION = "Authorization";

    @Autowired
    private AmbariRestApiAdapterService ambariRestApiAdapterService;

    @ApiOperation(value = "服务配置版本")
    @ApiImplicitParam(name = "Authorization", value = "认证请求头", required = true, paramType =
            "header")
    @GetMapping(value = "service_config_versions")
    public ResultBean serviceConfigVersions(HttpServletRequest request) {
        return ResultBean.ok(ambariRestApiAdapterService
                .getServiceConfigVersions(request.getHeader(AUTHORIZATION)));
    }
}
