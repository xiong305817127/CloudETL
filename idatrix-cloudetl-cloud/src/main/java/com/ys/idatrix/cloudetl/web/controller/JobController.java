/*
 * 云化数据集成系统
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.web.controller;

import java.util.List;
import java.util.Map;

import org.pentaho.di.core.logging.LoggingObjectInterface;
import org.pentaho.di.core.logging.LoggingObjectType;
import org.pentaho.di.core.logging.SimpleLoggingObject;
import org.pentaho.di.core.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ys.idatrix.cloudetl.dto.common.CheckResultDto;
import com.ys.idatrix.cloudetl.dto.common.RequestNameDto;
import com.ys.idatrix.cloudetl.dto.common.ReturnCodeDto;
import com.ys.idatrix.cloudetl.dto.history.ExecHistoryDto;
import com.ys.idatrix.cloudetl.dto.history.ExecLogsDto;
import com.ys.idatrix.cloudetl.dto.job.JobBatchExecRequestDto;
import com.ys.idatrix.cloudetl.dto.job.JobBatchStopDto;
import com.ys.idatrix.cloudetl.dto.job.JobExecEntryMeasureDto;
import com.ys.idatrix.cloudetl.dto.job.JobExecEntryStatusDto;
import com.ys.idatrix.cloudetl.dto.job.JobExecIdDto;
import com.ys.idatrix.cloudetl.dto.job.JobExecLogDto;
import com.ys.idatrix.cloudetl.dto.job.JobExecRequestDto;
import com.ys.idatrix.cloudetl.dto.job.JobExecStatusDto;
import com.ys.idatrix.cloudetl.dto.job.JobInfoDto;
import com.ys.idatrix.cloudetl.dto.job.JobOverviewDto;
import com.ys.idatrix.cloudetl.ext.CloudSession;
import com.ys.idatrix.cloudetl.service.job.CloudJobService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * 调度流程控制器
 * Job procedure controller.
 * @author JW
 * @since 05-12-2017
 * 
 */
@Controller
@RequestMapping(value="/job")
@Api(value = "/job" , description="调度任务 相关api")
public class JobController extends BaseAction {

	@Autowired
	private CloudJobService cloudJobService;
	
	/**
	 * 请求方法 - 获取组名列表
	 * @param jsonTransName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.GET, value="/getJobGroups")
	@ApiOperation(value = "获取调度组列表")
	@ApiImplicitParams( value = {
			@ApiImplicitParam(paramType = "query", name = "owner", dataType = "String", required = false, value = "拥有者名称,为空时:返回用户(User)列表,不为空时:返回用户组(Group)列表"),
	        @ApiImplicitParam(paramType = "query", name = "isMap", dataType = "Boolean", required = false, value = "是否返回Map 为true返回{user1:['group1',group2',...],user2:[...]}, 为false返回['group1',group2',...]")
	})
	@ApiResponses({ @ApiResponse(code = 200, response = String[].class, message = "成功" ) })
	public @ResponseBody Object getJobGroups(@RequestParam(required=false)String owner,@RequestParam(required=false,defaultValue="false" )boolean isMap) throws Exception {
		saveResourceOwner(owner);
		
		if(isMap ) {
			Map<String,List<String> > result = Maps.newHashMap() ;
			if( CloudSession.isRenterPrivilege()) {
				if(Utils.isEmpty(owner)) {
					//获取所有的用户
					for(String ownerUser : cloudJobService.getCloudJobUserList()) {
						List<String> list =  cloudJobService.getCloudJobGroupList( ownerUser);
						result.put(ownerUser, list == null?Lists.newArrayList() : list  );
					}
				}else {
					//获取owner 用户
					List<String> list = cloudJobService.getCloudJobGroupList(owner );
					result.put(owner, list == null?Lists.newArrayList() : list );
				}
			}else {
				//当前不是租户 或者 系统不开启租户超级权限,只能获取当前登录用户自己的
				List<String> list = cloudJobService.getCloudJobGroupList(CloudSession.getLoginUser() );
				result.put(CloudSession.getLoginUser(),  list == null?Lists.newArrayList() : list );
			}
			
			return result ;
		}
		if(Utils.isEmpty(owner) ) {
			return cloudJobService.getCloudJobUserList() ;
		}
		return cloudJobService.getCloudJobGroupList(owner);
	}

	/**
	 * 请求方法 - 打开调度
	 * @param jsonJobName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.POST, value="/openJob")
	@ApiOperation(value = "获取调度任务详细信息")
	@ApiResponses({ @ApiResponse(code = 200, response = JobOverviewDto.class, message = "成功" ) })
	public @ResponseBody Object openJob(@RequestBody RequestNameDto jsonJobName) throws Exception {
		return cloudJobService.loadCloudJob(jsonJobName.getOwner() , jsonJobName.getName(),jsonJobName.getGroup());
	}

	/**
	 * 请求方法 - 编辑调度属性
	 * @param jsonJobName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.POST, value="/editJobAttributes")
	@ApiOperation(value = "获取调度任务属性信息(名字,描述)")
	@ApiResponses({ @ApiResponse(code = 200, response = JobInfoDto.class, message = "成功" ) })
	public @ResponseBody Object editJobAttributes(@RequestBody RequestNameDto jsonJobName) throws Exception {
		return cloudJobService.editJobAttributes(jsonJobName.getOwner() ,jsonJobName.getName(),jsonJobName.getGroup());
	}

	/**
	 * 请求方法 - 保存调度属性
	 * @param jobInfo
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.POST, value="/saveJobAttributes")
	@ApiOperation(value = "保存调度任务属性信息(名字,描述),用于重命名")
	@ApiResponses({ @ApiResponse(code = 200, response = ReturnCodeDto.class, message = "成功" ) })
	public @ResponseBody Object saveJobAttributes(@RequestBody JobInfoDto jobInfo) throws Exception {
		checkPrivilege();
		return cloudJobService.saveJobAttributes(jobInfo);
	}

	/**
	 * 请求方法 - 新建调度作业
	 * @param jobInfo
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.POST, value="/newJob")
	@ApiOperation(value = " 新建调度任务")
	@ApiResponses({ @ApiResponse(code = 200, response = ReturnCodeDto.class, message = "成功" ) })
	public @ResponseBody Object newJob(@RequestBody JobInfoDto jobInfo) throws Exception {
		//只能新建 登录用户 自己的调度
		CloudSession.setThreadResourceUser(CloudSession.getLoginUser());
		return cloudJobService.newJob(jobInfo);
	}

	/**
	 * 请求方法 - 检查调度作业名是否存在
	 * @param jsonJobName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.POST, value="/checkJobName")
	@ApiOperation(value = "检查调度任务名是否存在")
	@ApiResponses({ @ApiResponse(code = 200, response = CheckResultDto.class, message = "成功" ) })
	public @ResponseBody Object checkJobName(@RequestBody RequestNameDto jsonJobName) throws Exception {
		//只检查 登录用户 自己的调度
		CloudSession.setThreadResourceUser(CloudSession.getLoginUser());
		return cloudJobService.checkJobName(CloudSession.getLoginUser() ,jsonJobName.getName());
	}

	/**
	 * 请求方法 - 删除调度作业
	 * @param jsonJobName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.POST, value="/deleteJob")
	@ApiOperation(value = "删除调度任务")
	@ApiResponses({ @ApiResponse(code = 200, response = ReturnCodeDto.class, message = "成功" ) })
	public @ResponseBody Object deleteJob(@RequestBody RequestNameDto jsonJobName) throws Exception {
		checkPrivilege();
		return cloudJobService.deleteJob(jsonJobName.getOwner() ,jsonJobName.getName(),jsonJobName.getGroup());
	}

	/**
	 * 请求方法 - 重启调度作业
	 * @param execRequest
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.POST, value="/rebootJob")
	@ApiOperation(value = "重启调度作业")
	@ApiResponses({ @ApiResponse(code = 200, response = ReturnCodeDto.class, message = "成功" ) })
	public @ResponseBody Object rebootJob(@RequestBody RequestNameDto jsonJobName) throws Exception {
		checkPrivilege();
		return cloudJobService.rebootJob(jsonJobName.getOwner() ,jsonJobName.getName(),jsonJobName.getGroup());
	}
	
	/**
	 * 请求方法 - 执行调度作业
	 * @param execRequest
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.POST, value="/execJob")
	@ApiOperation(value = "执行调度任务")
	@ApiResponses({ @ApiResponse(code = 200, response = ReturnCodeDto.class, message = "成功" ) })
	public @ResponseBody Object execJob(@RequestBody JobExecRequestDto execRequest) throws Exception {
		checkPrivilege();
		return cloudJobService.execJob(execRequest);
	}
	
	/**
	 * 请求方法 - 批量执行调度作业
	 * @param execRequest
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.POST, value="/execBatchJob")
	@ApiOperation(value = "批量执行调度任务")
	@ApiResponses({ @ApiResponse(code = 200, response = ReturnCodeDto.class, message = "成功" ) })
	public @ResponseBody Object execBatchJob(@RequestBody JobBatchExecRequestDto execRequest) throws Exception {
		checkPrivilege();
		return cloudJobService.execBatchJob(execRequest);
	}

	/**
	 * 请求方法 - 停止（终止）调度作业执行
	 * @param execId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.POST, value="/execStop")
	@ApiOperation(value = "停止（终止）调度任务")
	@ApiResponses({ @ApiResponse(code = 200, response = ReturnCodeDto.class, message = "成功" ) })
	public @ResponseBody Object execJobStop(@RequestBody JobExecIdDto execId) throws Exception {
		checkExecPrivilege(execId.getExecutionId());
		return cloudJobService.execStop(execId.getExecutionId());
	}
	
	/**
	 * 请求方法 - 批量执行调度作业
	 * @param execRequest
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.POST, value="/execBatchStop")
	@ApiOperation(value = "批量停止调度任务")
	@ApiResponses({ @ApiResponse(code = 200, response = ReturnCodeDto.class, message = "成功" ) })
	public @ResponseBody Object execJobBatchStop(@RequestBody JobBatchStopDto stopNames) throws Exception {
		checkPrivilege();
		return cloudJobService.execBatchStop(stopNames);
	}

	/**
	 * 请求方法 - 查询调度作业执行度量信息
	 * @param execId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.POST, value="/getEntryMeasure")
	@ApiOperation(value = "根据执行ID查询调度任务执行度量信息")
	@ApiResponses({ @ApiResponse(code = 200, response = JobExecEntryMeasureDto[].class, message = "成功" ) })
	public @ResponseBody Object getEntryMeasure(@RequestBody JobExecIdDto execId) throws Exception {
		return cloudJobService.getEntryMeasure(execId.getExecutionId());
	}

	/**
	 * 请求方法 - 查询调度作业执行节点状态信息
	 * @param execId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.POST, value="/getEntryStatus")
	@ApiOperation(value = "根据执行ID查询调度任务执行节点状态信息")
	@ApiResponses({ @ApiResponse(code = 200, response = JobExecEntryStatusDto.class, message = "成功" ) })
	public @ResponseBody Object getEntryStatus(@RequestBody JobExecIdDto execId) throws Exception {
		return cloudJobService.getEntryStatus(execId.getExecutionId());
	}

	/**
	 * 请求方法 - 查询调度作业执行日志信息
	 * @param execId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.POST, value="/getExecLog")
	@ApiOperation(value = "根据执行ID查询调度任务执行日志信息")
	@ApiResponses({ @ApiResponse(code = 200, response = JobExecLogDto.class, message = "成功" ) })
	public @ResponseBody Object getJobExecLog(@RequestBody JobExecIdDto execId) throws Exception {
		return cloudJobService.getExecLog(execId.getExecutionId());
	}
	
	/**
	 * 请求方法 - 查询调度作业执行后的汇总信息
	 * @param execId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.POST, value="/getExecInfo")
	@ApiOperation(value = "查询调度作业执行后的汇总信息")
	@ApiResponses({ @ApiResponse(code = 200, response = Map.class, message = "成功" ) })
	public @ResponseBody Object getJobExecInfo(@RequestBody JobExecIdDto execId) throws Exception {
		Map<String ,Object> res= Maps.newHashMap() ;
		res.put("EntryMeasure", cloudJobService.getEntryMeasure(execId.getExecutionId()) );
		res.put("EntryStatus", cloudJobService.getEntryStatus(execId.getExecutionId()) );
		res.put("ExecLog", cloudJobService.getExecLog(execId.getExecutionId()) );
		return res;
	}
	

	/**
	 * 请求方法 - 查询调度作业执行ID
	 * @param jsonJobName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.POST, value="/getExecId")
	@ApiOperation(value = "根据调度名查询调度任务执行ID")
	@ApiResponses({ @ApiResponse(code = 200, response = JobExecIdDto.class, message = "成功" ) })
	public @ResponseBody Object getJobExecId(@RequestBody RequestNameDto jsonJobName) throws Exception {
		return cloudJobService.getExecId(jsonJobName.getOwner(),jsonJobName.getName());
	}

	/**
	 * 请求方法 - 查询调度作业执行状态
	 * @param execId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.POST, value="/getExecStatus")
	@ApiOperation(value = "根据执行ID查询调度任务执行状态")
	@ApiResponses({ @ApiResponse(code = 200, response = JobExecStatusDto.class, message = "成功" ) })
	public @ResponseBody Object getJobExecStatus(@RequestBody JobExecIdDto execId) throws Exception {
		return cloudJobService.getExecStatus(execId.getExecutionId());
	}

	/**
	 * 请求方法 - 查询调度作业状态（如果正在执行，则返回执行状态）
	 * @param jsonJobName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.POST, value="/getJobStatus")
	@ApiOperation(value = "根据调度名查询调度任务状态（如果正在执行，则返回执行状态）")
	@ApiResponses({ @ApiResponse(code = 200, response = JobExecStatusDto.class, message = "成功" ) })
	public @ResponseBody Object getJobStatus(@RequestBody RequestNameDto jsonJobName) throws Exception {
		return cloudJobService.getJobStatus(jsonJobName.getOwner(),jsonJobName.getName());
	}

	/**
	 * 请求方法 - 查询调度作业执行历史
	 * @param jsonTransName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.POST, value="/getJobRecords")
	@ApiOperation(value = "根据调度名查询调度任务执行历史")
	@ApiResponses({ @ApiResponse(code = 200, response = ExecHistoryDto.class, message = "成功" ) })
	public @ResponseBody Object getJobHistory(@RequestBody RequestNameDto jsonTransName) throws Exception {
		return cloudJobService.getJobHistory(jsonTransName.getOwner(),jsonTransName.getName());
	}

	/**
	 * 请求方法 - 查询调度作业执行历史日志（最后一次执行的日志）
	 * @param jsonTransName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.POST, value="/getJobLogs")
	@ApiOperation(value = "查询调度任务执行历史日志")
	@ApiResponses({ @ApiResponse(code = 200, response = ExecLogsDto.class, message = "成功" ) })
	public @ResponseBody Object getJobLogs(@ApiParam(name="jsonTransName",value="调度任务名 或者 日志文件全路径名",required = true)@RequestBody RequestNameDto jsonTransName) throws Exception {
		return cloudJobService.getJobLogs(jsonTransName.getOwner(),jsonTransName.getName(),null,null,null);
	}

	/**
	 * 请求方法 - 查询调度作业执行历史日志
	 * @param jsonTransName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.GET, value="/getJobLogs")
	@ApiOperation(value = "查询调度任务执行历史日志")
	@ApiImplicitParams( value = {
			@ApiImplicitParam(paramType = "query", name = "name", dataType = "String", required = true, value = "查询的调度名"),
	        @ApiImplicitParam(paramType = "query", name = "group", dataType = "String", required = false, value = "调度所属组名"),
	        @ApiImplicitParam(paramType = "query", name = "id", dataType = "String", required = false, value = "调度执行id"),
	        @ApiImplicitParam(paramType = "query", name = "date", dataType = "String", required = true, value = "查询的开始时间"),
	        @ApiImplicitParam(paramType = "query", name = "endDate", dataType = "String", required = false, value = "查询的结束时间")
	})
	@ApiResponses({ @ApiResponse(code = 200, response = ExecLogsDto.class, message = "成功" ) })
	public @ResponseBody Object getJobLogs1(
			@RequestParam(required=false)String owner,
			@RequestParam(required=true)String name,
			@RequestParam(required=false)String group,
			@RequestParam(required=false)String id,
			@RequestParam(required=true)String date,
			@RequestParam(required=false)String endDate) throws Exception {
		saveResourceOwner(owner);
		return cloudJobService.getJobLogs(owner,name, id, date, endDate);
	}
	
	//
	// Logging object interface
	//
	public static final LoggingObjectInterface loggingObject = new SimpleLoggingObject("JobController", LoggingObjectType.JOB, null );

}
