/**
 * 云化数据集成系统
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.web.controller;

import java.util.Map;

import org.pentaho.di.core.util.Utils;
import org.pentaho.di.repository.RepositoryObjectType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.google.common.collect.Maps;
import com.ys.idatrix.cloudetl.dto.history.ExecHistoryRecordDto;
import com.ys.idatrix.cloudetl.dto.statistics.ExecTaskNumberTotal;
import com.ys.idatrix.cloudetl.dto.statistics.ExecTaskTimesTotal;
import com.ys.idatrix.cloudetl.dto.statistics.TaskMonthTotal;
import com.ys.idatrix.cloudetl.service.statistics.StatisticsService;
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
@RequestMapping(value="/statistics")
@Api(value = "/statistics" , description="kettle统计操作api")
public class StatisticsController extends BaseAction {

	@Autowired
	private StatisticsService statisticsService;

	
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
	
	/**
	 * 请求方法 - 获取当前登录租户的汇总 统计数据
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.GET, value="/getSummaryInfo")
	@ApiOperation(value = "获取当前登录租户的汇总数据,任务总数:taskTotal, 执行成功任务总次数:successTask ,执行失败任务总次数:failTask ,运行中的任务数: runningTask , 处理的任务数据总量 : dataTotal ")
	@ApiImplicitParams( value = {
			@ApiImplicitParam(paramType = "query", name = "type", dataType = "String", required = false, value = " job/trans 调度 或者 转换, 可为空, 为空时获取两者之和"),
			@ApiImplicitParam(paramType = "query", name = "flag", dataType = "String", required = false, value = "  year/month/day/2001-01-02 , 为空:忽略日期 , 否则 当年/当月/当天/具体某天某月某年" )
	})
	@ApiResponses({ @ApiResponse(code = 200, response = Map.class, message = "成功" ) })
	public @ResponseBody Object getSummaryInfo(   @RequestParam(required=false )String type ,
												  @RequestParam(required=false )String flag ) throws Exception {
		//TODO 是否需要检查权限
		//checkPrivilege();
		
		Long taskTotal = statisticsService.getTaskTotal(getRepositoryType(type),flag) ;
		ExecTaskTimesTotal execTimes = statisticsService.getTaskExecTimes(getRepositoryType(type), flag) ;
		long dataTotal = statisticsService.getTaskExecLines(getRepositoryType(type), flag);
		
		Map<String,Long> result = Maps.newHashMap() ;
		//任务总数
		result.put("taskTotal",taskTotal);
		//成功任务数
		result.put("successTask",execTimes.getSuccessTotal());
		//失败任务数
		result.put("failTask",execTimes.getFailTotal());
		//失败任务数
		result.put("runningTask",execTimes.getRunningTotal());
		//数据总量 : dataTotal
		result.put("dataTotal",dataTotal );
		
		return result ;
	}
	
	@RequestMapping(method=RequestMethod.GET, value="/getTaskTotalByMonth")
	@ApiOperation(value = "按月份 统计 过去一年的每月(或固定某个月)的新增任务数 (月份:本月新增任务数)")
	@ApiImplicitParams( value = {
			@ApiImplicitParam(paramType = "query", name = "type", dataType = "String", required = false, value = " job/trans 调度 或者 转换, 可为空, 为空时获取两者之和"),
			@ApiImplicitParam(paramType = "query", name = "flag", dataType = "String", required = false, value = "  year/month/day/2001-01-02 , 为空:忽略日期 , 否则 当年/当月/当天/具体某天某月某年" )
	})
	@ApiResponses({ @ApiResponse(code = 200, response = TaskMonthTotal[].class, message = "成功" ) })
	public @ResponseBody Object getTaskTotalByMonth(  @RequestParam(required=false )String type,
													  @RequestParam(required=false )String flag  ) throws Exception {
		return statisticsService.getTaskTotalByMonth(getRepositoryType(type),flag);
	}

	
	@RequestMapping(method=RequestMethod.GET, value="/getTaskExecTimesByMonth")
	@ApiOperation(value = "按月份 统计 过去一年的每月(或固定某个月)的执行次数(月份:成功执行次数,失败执行次数)")
	@ApiImplicitParams( value = {
			@ApiImplicitParam(paramType = "query", name = "type", dataType = "String", required = false, value = " job/trans 调度 或者 转换, 可为空, 为空时获取两者之和"),
			@ApiImplicitParam(paramType = "query", name = "flag", dataType = "String", required = false, value = "  year/month/day/2001-01-02 , 为空:忽略日期 , 否则 当年/当月/当天/具体某天某月某年" )
	})
	@ApiResponses({ @ApiResponse(code = 200, response = ExecTaskTimesTotal[].class, message = "成功" ) })
	public @ResponseBody Object getTaskExecTimesByMonth(  @RequestParam(required=false )String type ,
			  												@RequestParam(required=false )String flag  ) throws Exception {
		return statisticsService.getTaskExecTimesByMonth(getRepositoryType(type),flag);
	}

	@RequestMapping(method=RequestMethod.GET, value="/getTaskExecLinesByMonth")
	@ApiOperation(value = "按月份 统计 过去一年的每月(或固定某个月)的数据总量(月份:当月处理数据总量)")
	@ApiImplicitParams( value = {
			@ApiImplicitParam(paramType = "query", name = "type", dataType = "String", required = false, value = " job/trans 调度 或者 转换, 可为空, 为空时获取两者之和"),
			@ApiImplicitParam(paramType = "query", name = "flag", dataType = "String", required = false, value = "  year/month/day/2001-01-02 , 为空:忽略日期 , 否则 当年/当月/当天/具体某天某月某年" )
	})
	@ApiResponses({ @ApiResponse(code = 200, response = ExecTaskNumberTotal[].class, message = "成功" ) })
	public @ResponseBody Object getTaskExecLinesByMonth(  @RequestParam(required=false )String type,
														  @RequestParam(required=false )String flag   ) throws Exception {
		return statisticsService.getTaskExecLinesByMonth(getRepositoryType(type),flag);
	}

	@RequestMapping(method=RequestMethod.GET, value="/getTaskExecLinesByTask")
	@ApiOperation(value = "按任务 统计 每个任务[在某个固定月]的数据总量(任务名:任务类型:拥有者:该任务处理的数据总量)")
	@ApiImplicitParams( value = {
			@ApiImplicitParam(paramType = "query", name = "type", dataType = "String", required = false, value = " job/trans 调度 或者 转换, 可为空, 为空时获取两者之和"),
			@ApiImplicitParam(paramType = "query", name = "flag", dataType = "String", required = false, value = "  year/month/day/2001-01-02 , 为空:忽略日期 , 否则 当年/当月/当天/具体某天某月某年" ),
			@ApiImplicitParam(paramType = "query", name = "page", dataType = "int", required = false, value = "分页参数,页号,-1代表不分页,获取全部,默认-1", defaultValue = "-1"),
			@ApiImplicitParam(paramType = "query", name = "pageSize", dataType = "int", required = false, value = "分页参数,每页大小,默认10", defaultValue = "10"),
	})
	@ApiResponses({ @ApiResponse(code = 200, response = ExecTaskNumberTotal[].class, message = "成功" ) })
	public @ResponseBody Object getTaskExecLinesByTask(  @RequestParam(required=false )String type ,
														 @RequestParam(required=false )String flag ,
														 @RequestParam(required=false,defaultValue="-1")Integer page,
														@RequestParam(required=false,defaultValue="10")Integer pageSize) throws Exception {
		return statisticsService.getTaskExecLinesByTask(getRepositoryType(type), flag, page, pageSize);
	}

	@RequestMapping(method=RequestMethod.GET, value="/getRenterSuccessTasks")
	@ApiOperation(value = "获取租户下[在某个固定月]的成功执行的任务执行列表(任务名:任务类型:执行ID:执行时间:处理数据量)")
	@ApiImplicitParams( value = {
			@ApiImplicitParam(paramType = "query", name = "type", dataType = "String", required = false, value = " job/trans 调度 或者 转换, 可为空, 为空时获取两者之和"),
			@ApiImplicitParam(paramType = "query", name = "flag", dataType = "String", required = false, value = "  year/month/day/2001-01-02 , 为空:忽略日期 , 否则 当年/当月/当天/具体某天某月某年" ),
			@ApiImplicitParam(paramType = "query", name = "page", dataType = "int", required = false, value = "分页参数,页号,-1代表不分页,获取全部,默认-1", defaultValue = "-1"),
			@ApiImplicitParam(paramType = "query", name = "pageSize", dataType = "int", required = false, value = "分页参数,每页大小,默认10", defaultValue = "10"),
	})
	@ApiResponses({ @ApiResponse(code = 200, response = ExecHistoryRecordDto[].class, message = "成功" ) })
	public @ResponseBody Object getRenterSuccessTasks(  @RequestParam(required=false )String type ,
														 @RequestParam(required=false )String flag	,
														 @RequestParam(required=false,defaultValue="-1")Integer page,
														@RequestParam(required=false,defaultValue="10")Integer pageSize ) throws Exception {
		return statisticsService.getRenterTaskList(getRepositoryType(type), flag, page, pageSize);
	}
	
}
