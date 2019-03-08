/*
 * 云化数据集成系统
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ys.idatrix.cloudetl.dto.step.parts.WebServiceOpDto;
import com.ys.idatrix.cloudetl.ext.utils.WebServiceUtils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Webservice 服务控制器
 * 
 * @author FBZ
 * @since 12-14-2017
 * 
 */
@Controller
@RequestMapping(value = "/ws")
@Api(value = "/ws" , description="webservice 相关api")
public class WsController extends BaseAction {

	/**
	 * 请求方法 - 获取wsdl 的方法描述
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method = {RequestMethod.POST, RequestMethod.GET}, value = "/getOperations")
	@ApiOperation(value = "获取wsdl 的方法描述")
	@ApiImplicitParams( value = {
	        @ApiImplicitParam(paramType = "query", name = "transName", dataType = "String", required = true, value = "转换名"),
	        @ApiImplicitParam(paramType = "query", name = "url", dataType = "String", required = true, value = "wsdl url"),
	        @ApiImplicitParam(paramType = "query", name = "user", dataType = "String", required = false, value = "用户名"),
	        @ApiImplicitParam(paramType = "query", name = "password", dataType = "String", required = false, value = "密码")
	})
	@ApiResponses({ @ApiResponse(code = 200, response = WebServiceOpDto.class, message = "成功" ) })
	public @ResponseBody Object getOperations(@RequestParam(required = false, name = "owner") String owner,
			@RequestParam(required = true, name = "transName") String transName,
			@RequestParam(required = false, name = "group") String group,
			@RequestParam(required = true, name = "url") String url,
			@RequestParam(required = false, name = "user") String user,
			@RequestParam(required = false, name = "password") String password) throws Exception {
		saveResourceOwner(owner);
		group = getGroupName(group, transName);
		return WebServiceUtils.getOperations(owner,transName,group, url, user, password);
	}
}
