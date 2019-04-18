/*
 * 云化数据集成系统
 * iDatrix quality
 */
package com.ys.idatrix.quality.web.controller;

import org.pentaho.di.core.encryption.Encr;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ys.idatrix.quality.dto.common.ReturnCodeDto;
import com.ys.idatrix.quality.repository.database.SystemSettingDao;

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
@Api(value = "/" , description="免认证 ,系统相关api")
public class SystemController extends BaseAction {

	/**
	 * 请求方法 - 内部密码加密  的方法描述
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method = { RequestMethod.GET}, value = "password/encode")
	@ApiOperation(value = "内部密码加密 的方法描述")
	@ApiImplicitParams( value = {
	        @ApiImplicitParam(paramType = "query", name = "password", dataType = "String", required = true, value = "需要加密的密码"),
	        @ApiImplicitParam(paramType = "query", name = "encrypted", dataType = "String", required = false, value = "是否使用 Encrypted 方式")
	})
	@ApiResponses({ @ApiResponse(code = 200, response = String.class, message = "成功" ) })
	public @ResponseBody Object encodePassword(@RequestParam(required = true, name = "password") String password,
			@RequestParam(required = false, name = "encrypted",defaultValue="true") boolean encrypted) throws Exception {
		if(encrypted) {
			return Encr.encryptPasswordIfNotUsingVariables(password);
		}else {
			return Encr.encryptPassword(password);
		}
	}
	
	/**
	 * 请求方法 - 清空 数据库系统设置缓存,再次使用会进行刷新
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method = { RequestMethod.GET}, value = "setting/refresh")
	@ApiOperation(value = "刷新数据库系统设置")
	@ApiResponses({ @ApiResponse(code = 200, response = ReturnCodeDto.class, message = "成功" ) })
	public @ResponseBody ReturnCodeDto refreshSettings()  throws Exception {
		SystemSettingDao.getInstance().clearCache();
		return new ReturnCodeDto(0);
	}
	
}
