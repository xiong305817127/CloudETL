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
import com.ys.idatrix.cloudetl.dto.trans.TransBatchExecRequestDto;
import com.ys.idatrix.cloudetl.dto.trans.TransBatchStopDto;
import com.ys.idatrix.cloudetl.dto.trans.TransExecIdDto;
import com.ys.idatrix.cloudetl.dto.trans.TransExecLogDto;
import com.ys.idatrix.cloudetl.dto.trans.TransExecRequestNewDto;
import com.ys.idatrix.cloudetl.dto.trans.TransExecStatusDto;
import com.ys.idatrix.cloudetl.dto.trans.TransExecStepMeasureDto;
import com.ys.idatrix.cloudetl.dto.trans.TransExecStepStatusDto;
import com.ys.idatrix.cloudetl.dto.trans.TransInfoDto;
import com.ys.idatrix.cloudetl.dto.trans.TransNameDto;
import com.ys.idatrix.cloudetl.dto.trans.TransOverviewDto;
import com.ys.idatrix.cloudetl.ext.CloudSession;
import com.ys.idatrix.cloudetl.service.trans.CloudTransService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * 转换流程控制器
 * Transformation procedure controller.
 * @author JW
 * @since 05-12-2017
 * 
 */
@Controller
@RequestMapping(value="/trans")
@Api(value = "/job" , description="转换 相关api")
public class TransController extends BaseAction {

	@Autowired
	private CloudTransService cloudTransService;
	
	/**
	 * 请求方法 - 获取组名列表
	 * @param jsonTransName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.GET, value="/getTransGroups")
	@ApiOperation(value = "获取转换组列表")
	@ApiImplicitParams( value = {
			@ApiImplicitParam(paramType = "query", name = "owner", dataType = "String", required = false, value = "拥有者名称,,为空时:返回用户(User)列表,不为空时:返回用户组(Group)列表"),
	        @ApiImplicitParam(paramType = "query", name = "isMap", dataType = "Boolean", required = false, value = "是否返回Map 为true返回{user1:['group1',group2',...],user2:[...]}, 为false返回['group1',group2',...]")
	})
	@ApiResponses({ @ApiResponse(code = 200, response = String[].class, message = "成功" ) })
	public @ResponseBody Object getTransGroups(@RequestParam(required=false)String owner , @RequestParam(required=false,defaultValue="false" )boolean isMap ) throws Exception {
		saveResourceOwner(owner);
		if(isMap ) {
			Map<String,List<String> > result = Maps.newHashMap() ;
			if( CloudSession.isRenterPrivilege()) {
				if(Utils.isEmpty(owner)) {
					//获取所有的用户
					for(String ownerUser : cloudTransService.getCloudTransUserList()) {
						List<String> list =  cloudTransService.getCloudTransGroupList( ownerUser);
						result.put(ownerUser, list == null?Lists.newArrayList() : list  );
					}
				}else {
					//获取owner 用户
					List<String> list = cloudTransService.getCloudTransGroupList(owner );
					result.put(owner, list == null?Lists.newArrayList() : list );
				}
			}else {
				//当前不是租户 或者 系统不开启租户超级权限,只能获取当前登录用户自己的
				List<String> list = cloudTransService.getCloudTransGroupList(CloudSession.getLoginUser()  );
				result.put(CloudSession.getLoginUser(),  list == null?Lists.newArrayList() : list );
			}
			
			return result ;
		}
		if( Utils.isEmpty(owner) ) {
			return cloudTransService.getCloudTransUserList() ;
		}
		return cloudTransService.getCloudTransGroupList(owner);
	}

	/**
	 * 请求方法 - 打开转换
	 * @param jsonTransName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.POST, value="/openTrans")
	@ApiOperation(value = "获取转换详细信息")
	@ApiResponses({ @ApiResponse(code = 200, response = TransOverviewDto.class, message = "成功" ) })
	public @ResponseBody Object openTrans(@RequestBody RequestNameDto jsonTransName) throws Exception {
		return cloudTransService.loadCloudTrans(jsonTransName.getOwner() , jsonTransName.getName(),jsonTransName.getGroup());
	}

	/**
	 * 请求方法 - 编辑转换属性
	 * @param jsonTransName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.POST, value="/editTransAttributes")
	@ApiOperation(value = "获取转换属性信息(名字,描述)")
	@ApiResponses({ @ApiResponse(code = 200, response = TransInfoDto.class, message = "成功" ) })
	public @ResponseBody Object editTransAttributes(@RequestBody RequestNameDto jsonTransName) throws Exception {
		return cloudTransService.editTransAttributes(jsonTransName.getOwner() ,jsonTransName.getName(),jsonTransName.getGroup());
	}

	/**
	 * 请求方法 - 保存转换属性
	 * @param transInfo
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.POST, value="/saveTransAttributes")
	@ApiOperation(value = "保存转换属性信息(名字,描述),用户重命名")
	@ApiResponses({ @ApiResponse(code = 200, response = ReturnCodeDto.class, message = "成功" ) })
	public @ResponseBody Object saveTransAttributes(@RequestBody TransInfoDto transInfo) throws Exception {
		checkPrivilege();
		return cloudTransService.saveTransAttributes(transInfo);
	}

	/**
	 * 请求方法 - 新建转换
	 * @param transInfo
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.POST, value="/newTrans")
	@ApiOperation(value = "新建转换")
	@ApiResponses({ @ApiResponse(code = 200, response = ReturnCodeDto.class, message = "成功" ) })
	public @ResponseBody Object newTrans(@RequestBody TransInfoDto transInfo) throws Exception {
		//只能新建 登录用户 自己的转换
		CloudSession.setThreadResourceUser(CloudSession.getLoginUser());
		return cloudTransService.newTrans(transInfo);
	}

	/**
	 * 请求方法 - 检查转换名是否存在
	 * @param jsonTransName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.POST, value="/checkTransName")
	@ApiOperation(value = "检查转换名是否存在")
	@ApiResponses({ @ApiResponse(code = 200, response = CheckResultDto.class, message = "成功" ) })
	public @ResponseBody Object checkTransName(@RequestBody RequestNameDto jsonTransName) throws Exception {
		//只检查 登录用户 自己的转换名称
		CloudSession.setThreadResourceUser(CloudSession.getLoginUser());
		return cloudTransService.checkTransName(CloudSession.getLoginUser() ,jsonTransName.getName());
	}

	/**
	 * 请求方法 - 删除转换
	 * @param jsonTransName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.POST, value="/deleteTrans")
	@ApiOperation(value = "删除转换")
	@ApiResponses({ @ApiResponse(code = 200, response = ReturnCodeDto.class, message = "成功" ) })
	public @ResponseBody Object deleteTrans(@RequestBody RequestNameDto jsonTransName) throws Exception {
		checkPrivilege();
		return cloudTransService.deleteTrans(jsonTransName.getOwner() ,jsonTransName.getName(),jsonTransName.getGroup());
	}

	//@RequestMapping(method=RequestMethod.POST, value="/execTrans")
	/*public @ResponseBody TransExecResultDto execTrans(@RequestBody TransExecRequestDto execRequest) throws Exception {
        return cloudTransService.execTrans(execRequest);
    }*/
	
	/**
	 * 请求方法 - 重启转换
	 * @param execRequest
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.POST, value="/rebootTrans")
	@ApiOperation(value = "重启转换")
	@ApiResponses({ @ApiResponse(code = 200, response = ReturnCodeDto.class, message = "成功" ) })
	public @ResponseBody Object rebootTrans(@RequestBody RequestNameDto jsonTransName)  throws Exception {
		checkPrivilege();
		return cloudTransService.rebootTrans(jsonTransName.getOwner() ,jsonTransName.getName(),jsonTransName.getGroup());
	}

	/**
	 * 请求方法 - 执行转换
	 * @param execRequest
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.POST, value="/execTrans")
	@ApiOperation(value = "执行转换")
	@ApiResponses({ @ApiResponse(code = 200, response = ReturnCodeDto.class, message = "成功" ) })
	public @ResponseBody Object execTrans(@RequestBody TransExecRequestNewDto execRequest)  throws Exception {
		checkPrivilege();
		return cloudTransService.execTransNew(execRequest);
	}
	
	
	/**
	 * 请求方法 - 批量执行转换
	 * @param execRequest
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.POST, value="/execBatchTrans")
	@ApiOperation(value = "批量执行转换")
	@ApiResponses({ @ApiResponse(code = 200, response = ReturnCodeDto.class, message = "成功" ) })
	public @ResponseBody Object execBatchTrans(@RequestBody TransBatchExecRequestDto execRequest)  throws Exception {
		checkPrivilege();
		return cloudTransService.execBatchTrans(execRequest);
	}
	
	

	/**
	 * 请求方法 - 暂停转换执行
	 * @param execId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.POST, value="/execPause")
	@ApiOperation(value = "暂停转换执行")
	@ApiResponses({ @ApiResponse(code = 200, response = ReturnCodeDto.class, message = "成功" ) })
	public @ResponseBody Object execPause(@RequestBody TransExecIdDto execId) throws Exception {
		checkExecPrivilege(execId.getExecutionId());
		return cloudTransService.execPause(execId.getExecutionId());
	}

	/**
	 * 请求方法 - 恢复转换执行（暂停后调用）
	 * @param execId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.POST, value="/execResume")
	@ApiOperation(value = "恢复转换执行（暂停后调用）")
	@ApiResponses({ @ApiResponse(code = 200, response = ReturnCodeDto.class, message = "成功" ) })
	public @ResponseBody Object execResume(@RequestBody TransExecIdDto execId) throws Exception {
		checkExecPrivilege(execId.getExecutionId());
		return cloudTransService.execResume(execId.getExecutionId());
	}
	
	/**
	 * 请求方法 - 恢复转换执行（暂停后调用）
	 * @param execId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.POST, value="/execMorePreview")
	@ApiOperation(value = "预览获取更多数据")
	@ApiResponses({ @ApiResponse(code = 200, response = ReturnCodeDto.class, message = "成功" ) })
	public @ResponseBody Object execMorePreview(@RequestBody TransExecIdDto execId) throws Exception {
		checkExecPrivilege(execId.getExecutionId());
		return cloudTransService.execMorePreview(execId.getExecutionId());
	}

	/**
	 * 请求方法 - 停止（终止）转换执行
	 * @param execId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.POST, value="/execStop")
	@ApiOperation(value = "停止（终止）转换执行")
	@ApiResponses({ @ApiResponse(code = 200, response = ReturnCodeDto.class, message = "成功" ) })
	public @ResponseBody Object execTransStop(@RequestBody TransExecIdDto execId) throws Exception {
		checkExecPrivilege(execId.getExecutionId());
		return cloudTransService.execStop(execId.getExecutionId());
	}

	/**
	 * 请求方法 - 批量执行调度作业
	 * @param execRequest
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.POST, value="/execBatchStop")
	@ApiOperation(value = "批量停止转换")
	@ApiResponses({ @ApiResponse(code = 200, response = ReturnCodeDto.class, message = "成功" ) })
	public @ResponseBody Object execTransBatchStop(@RequestBody TransBatchStopDto transNames) throws Exception {
		checkPrivilege();
		return cloudTransService.execBatchStop(transNames);
	}
	
	
	/**
	 * 请求方法 - 查询转换执行度量信息
	 * @param execId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.POST, value="/getStepMeasure")
	@ApiOperation(value = "根据执行ID查询转换执行度量信息")
	@ApiResponses({ @ApiResponse(code = 200, response = TransExecStepMeasureDto[].class, message = "成功" ) })
	public @ResponseBody Object getStepMeasure(@RequestBody TransExecIdDto execId) throws Exception {
		return cloudTransService.getStepMeasure(execId.getExecutionId());
	}
	
	/**
	 * 请求方法 - 查询转换Debug执行的预览数据
	 * @param execId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.POST, value="/getDebugPreviewData")
	@ApiOperation(value = "根据执行ID查询转换执行度量信息")
	@ApiResponses({ @ApiResponse(code = 200, response = Map.class, message = "成功" ) })
	public @ResponseBody Object getDebugPreviewData(@RequestBody TransExecIdDto execId) throws Exception {
		return cloudTransService.getDebugPreviewData(execId.getExecutionId());
	}

	/**
	 * 请求方法 - 查询转换步骤执行状态
	 * @param execId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.POST, value="/getStepStatus")
	@ApiOperation(value = "根据执行ID查询转换步骤执行状态")
	@ApiResponses({ @ApiResponse(code = 200, response = TransExecStepStatusDto[].class, message = "成功" ) })
	public @ResponseBody Object getStepStatus(@RequestBody TransExecIdDto execId) throws Exception {
		return cloudTransService.getStepStatus(execId.getExecutionId());
	}

	/**
	 * 请求方法 - 查询转换执行日志
	 * @param execId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.POST, value="/getExecLog")
	@ApiOperation(value = "根据执行ID查询转换执行日志")
	@ApiResponses({ @ApiResponse(code = 200, response = TransExecLogDto.class, message = "成功" ) })
	public @ResponseBody Object getTransExecLog(@RequestBody TransExecIdDto execId) throws Exception {
		return cloudTransService.getExecLog(execId.getExecutionId());
	}

	/**
	 * 请求方法 - 查询转换执行ID
	 * @param jsonTransName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.POST, value="/getExecId")
	@ApiOperation(value = "根据转换名查询转换执行ID")
	@ApiResponses({ @ApiResponse(code = 200, response = TransExecIdDto.class, message = "成功" ) })
	public @ResponseBody Object getTransExecId(@RequestBody RequestNameDto jsonTransName) throws Exception {
		return cloudTransService.getExecId(jsonTransName.getOwner(),jsonTransName.getName());
	}

	/**
	 * 请求方法 - 查询转换执行状态
	 * @param execId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.POST, value="/getExecStatus")
	@ApiOperation(value = "根据执行ID查询转换执行状态")
	@ApiResponses({ @ApiResponse(code = 200, response = TransExecStatusDto.class, message = "成功" ) })
	public @ResponseBody Object getTransExecStatus(@RequestBody TransExecIdDto execId) throws Exception {
		return cloudTransService.getExecStatus(execId.getExecutionId());
	}

	/**
	 * 请求方法 - 查询转换状态（如果正在执行，则返回执行状态）
	 * @param jsonTransName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.POST, value="/getTransStatus")
	@ApiOperation(value = "根据转换名查询转换状态（如果正在执行，则返回执行状态）")
	@ApiResponses({ @ApiResponse(code = 200, response = TransExecStatusDto.class, message = "成功" ) })
	public @ResponseBody Object getTransStatus(@RequestBody RequestNameDto jsonTransName) throws Exception {
		return cloudTransService.getTransStatus(jsonTransName.getOwner(),jsonTransName.getName());
	}

	
	/**
	 * 请求方法 - 查询转换执行执行后的汇总信息
	 * @param execId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.POST, value="/getExecInfo")
	@ApiOperation(value = "查询转换执行执行后的汇总信息")
	@ApiResponses({ @ApiResponse(code = 200, response = Map.class, message = "成功" ) })
	public @ResponseBody Object getTransExecInfo(@RequestBody TransExecIdDto execId) throws Exception {
		Map<String ,Object> res= Maps.newHashMap() ;
		res.put("StepMeasure", cloudTransService.getStepMeasure(execId.getExecutionId()) );
		res.put("StepStatus", cloudTransService.getStepStatus(execId.getExecutionId()) );
		res.put("ExecLog", cloudTransService.getExecLog(execId.getExecutionId()) );
		res.put("DebugPreviewData", cloudTransService.getDebugPreviewData(execId.getExecutionId()) );
		return res;
	}
	
	
	/**
	 * 请求方法 - 查询转换执行历史记录
	 * @param jsonTransName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.POST, value="/getTransRecords")
	@ApiOperation(value = "查询转换执行历史记录")
	@ApiResponses({ @ApiResponse(code = 200, response = ExecHistoryDto.class, message = "成功" ) })
	public @ResponseBody Object getTransHistory(@RequestBody RequestNameDto jsonTransName) throws Exception {
		return cloudTransService.getTransHistory(jsonTransName.getOwner(),jsonTransName.getName());
	}

	/**
	 * 请求方法 - 查询转换历史日志（最后一次执行的日志）
	 * @param jsonTransName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.POST, value="/getTransLogs")
	@ApiOperation(value = "查询转换历史日志")
	@ApiResponses({ @ApiResponse(code = 200, response = ExecLogsDto.class, message = "成功" ) })
	public @ResponseBody Object getTransLogs(@ApiParam(name="jsonTransName",value="调度任务名 或者 日志文件全路径名",required = true)@RequestBody TransNameDto jsonTransName) throws Exception {
		return cloudTransService.getTransLogs(jsonTransName.getOwner(),jsonTransName.getName(), null, null, null);
	}
	
	/**
	 * 请求方法 - 查询调度作业执行历史日志
	 * @param jsonTransName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.GET, value="/getTransLogs")
	@ApiOperation(value = "查询转换执行历史日志")
	@ApiImplicitParams( value = {
			@ApiImplicitParam(paramType = "query", name = "name", dataType = "String", required = true, value = "查询的转换名"),
	        @ApiImplicitParam(paramType = "query", name = "group", dataType = "String", required = false, value = "转换所属组名"),
	        @ApiImplicitParam(paramType = "query", name = "id", dataType = "String", required = false, value = "转换执行id"),
	        @ApiImplicitParam(paramType = "query", name = "date", dataType = "String", required = true, value = "查询的开始时间"),
	        @ApiImplicitParam(paramType = "query", name = "endDate", dataType = "String", required = false, value = "查询的结束时间")
	})
	@ApiResponses({ @ApiResponse(code = 200, response = ExecLogsDto.class, message = "成功" ) })
	public @ResponseBody Object getTransLogs1(@RequestParam(required=false)String owner,
			@RequestParam(required=true)String name,
			@RequestParam(required=false)String group,
			@RequestParam(required=false)String id,
			@RequestParam(required=true)String date,
			@RequestParam(required=false)String endDate) throws Exception {
		saveResourceOwner(owner);
		return cloudTransService.getTransLogs(owner ,name, id, date, endDate);
	}
	

	//
	// Logging object interface
	//
	public static final LoggingObjectInterface loggingObject = new SimpleLoggingObject("TransController", LoggingObjectType.TRANS, null );

}
