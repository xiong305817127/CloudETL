/**
 * 云化数据集成系统
 * iDatrix CloudETL
 */
package com.ys.idatrix.quality.web.controller;

import org.pentaho.di.core.util.Utils;
import org.pentaho.di.repository.RepositoryObjectType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.ys.idatrix.cloudetl.exec4w.api.dto.ExecExceptionDto;
import com.ys.idatrix.quality.ext.CloudSession;
import com.ys.idatrix.quality.reference.etl.CloudETLService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * 主界面流程控制器
 * Cloud ETL main procedure controller
 * @author JW
 * @since 05-12-2017
 * 
 */
@Controller
@RequestMapping(value="/etl4w")
@Api(value = "/etl4w" , description="获取ETL执行 4W集合报告")
public class Cloud4WController extends BaseAction {

	@Autowired
	private CloudETLService cloudETLService;

	@RequestMapping(method=RequestMethod.GET, value="/get4WByExecId")
	@ApiOperation(value = "获取当前登录租户的 所有执行 异常报告")
	@ApiResponses({ @ApiResponse(code = 200, response = ExecExceptionDto.class, message = "成功" ) })
	public @ResponseBody Object get4WByExecId( @RequestParam(required=true )String  execId ) throws Exception {
		//TODO 是否需要检查权限
		//checkPrivilege();
		return cloudETLService.getExecException(execId);
	}
	
	
	/**
	 * 请求方法 - 获取当前登录租户的 所有执行 异常报告
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.GET, value="/get4WListByRenter")
	@ApiOperation(value = "获取当前登录租户的 所有执行 异常报告")
	@ApiImplicitParams( value = {
	        @ApiImplicitParam(paramType = "query", name = "page", dataType = "int", required = false, value = "分页参数,页号,,默认1", defaultValue = "1"),
	        @ApiImplicitParam(paramType = "query", name = "pageSize", dataType = "int", required = false, value = "分页参数,每页大小,默认10", defaultValue = "10")
	})
	@ApiResponses({ @ApiResponse(code = 200, response = ExecExceptionDto[].class, message = "成功" ) })
	public @ResponseBody Object get4WListByRenter(
			@RequestParam(required=false,defaultValue="1")Integer page,
			@RequestParam(required=false,defaultValue="10")Integer pageSize ) throws Exception {
		//TODO 是否需要检查权限
		//checkPrivilege();
		
		return cloudETLService.getExecExceptions(CloudSession.getLoginRenterId(), page, pageSize) ;
	}
	
	@RequestMapping(method=RequestMethod.GET, value="/find4WByRenter")
	@ApiOperation(value = "根据条件(用户,任务名称,任务类型)过滤租户下的  异常报告列表")
	@ApiImplicitParams( value = {
	        @ApiImplicitParam(paramType = "query", name = "page", dataType = "int", required = false, value = "分页参数,页号,,默认1", defaultValue = "1"),
	        @ApiImplicitParam(paramType = "query", name = "pageSize", dataType = "int", required = false, value = "分页参数,每页大小,默认10", defaultValue = "10"),
	        @ApiImplicitParam(paramType = "query", name = "owner", dataType = "String", required = false, value = "拥有者,默认当前登录用户"),
	        @ApiImplicitParam(paramType = "query", name = "name", dataType = "String", required = false, value = "用户下的转换名/调度名"),
			@ApiImplicitParam(paramType = "query", name = "type", dataType = "String", required = false, value = " job/trans 调度 或者 转换 ,为空 不区分" ),
	})
	@ApiResponses({ @ApiResponse(code = 200, response = ExecExceptionDto[].class, message = "成功" ) })
	public @ResponseBody Object get4WByUser(
			@RequestParam(required=false,defaultValue="1")Integer page,
			@RequestParam(required=false,defaultValue="10")Integer pageSize,
			@RequestParam(required=false ) String owner ,
			@RequestParam(required=false )String name ,
			@RequestParam(required=false )String  type ) throws Exception {
		//TODO 是否需要检查权限
		//checkPrivilege();
		
		return cloudETLService.getExecExceptions(owner, name, getRepositoryType(type), page, pageSize);
	}


	private RepositoryObjectType getRepositoryType(String type) {
		RepositoryObjectType jobType = null ;
		if( !Utils.isEmpty(type) ) {
			if( "trans".equalsIgnoreCase(type)) {
				jobType = RepositoryObjectType.TRANSFORMATION  ;
			}else {
				jobType = RepositoryObjectType.JOB  ;
			}
		}
		return jobType ;
	}

}
