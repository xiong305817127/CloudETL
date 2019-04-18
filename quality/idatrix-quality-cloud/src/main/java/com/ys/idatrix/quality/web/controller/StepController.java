/*
 * 云化数据集成系统
 * iDatrix CloudETL
 */
package com.ys.idatrix.quality.web.controller;

import org.pentaho.di.core.logging.LoggingObjectInterface;
import org.pentaho.di.core.logging.LoggingObjectType;
import org.pentaho.di.core.logging.SimpleLoggingObject;
import org.pentaho.pms.util.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ys.idatrix.quality.dto.common.ReturnCodeDto;
import com.ys.idatrix.quality.dto.step.StepConfigsDto;
import com.ys.idatrix.quality.dto.step.StepCopyDto;
import com.ys.idatrix.quality.dto.step.StepDetailsDto;
import com.ys.idatrix.quality.dto.step.StepFieldDto;
import com.ys.idatrix.quality.dto.step.StepHeaderDto;
import com.ys.idatrix.quality.dto.step.StepNameCheckResultDto;
import com.ys.idatrix.quality.dto.step.StepPositionDto;
import com.ys.idatrix.quality.dto.step.TransStepDto;
import com.ys.idatrix.quality.service.trans.CloudStepService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * 转换步骤流程控制器
 * Transformation step procedure controller.
 * @author JW
 * @since 05-12-2017
 * 
 */
@Controller
@RequestMapping(value="/step")
@Api(value = "/step" , description="转换步骤 相关api")
public class StepController extends BaseAction {

	@Autowired
	private CloudStepService cloudStepService;

	/**
	 * 请求方法 - 新增转换步骤
	 * @param stepHeader
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.POST, value="/addStep")
	@ApiOperation(value = "新增转换步骤")
	@ApiResponses({ @ApiResponse(code = 200, response = ReturnCodeDto.class, message = "成功" ) })
	public @ResponseBody Object addStep(@RequestBody StepHeaderDto stepHeader) throws Exception {
		checkPrivilege();
		return cloudStepService.addStep(stepHeader);
	}
	
	/**
	 * 请求方法 - 新增转换步骤
	 * @param stepHeader
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.POST, value="/copyStep")
	@ApiOperation(value = "复制转换步骤")
	@ApiResponses({ @ApiResponse(code = 200, response = ReturnCodeDto.class, message = "成功" ) })
	public @ResponseBody Object copyStep(@RequestBody StepCopyDto stepcopy) throws Exception {
		saveResourceOwner(Const.NVL(stepcopy.getToOwner(),stepcopy.getOwner()) );
		checkPrivilege();
		return cloudStepService.copyStep(stepcopy);
	}

	/**
	 * 请求方法 - 检查步骤名是否存在
	 * @param transStep
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.POST, value="/checkStepName")
	@ApiOperation(value = "检查步骤名是否存在")
	@ApiResponses({ @ApiResponse(code = 200, response = StepNameCheckResultDto.class, message = "成功" ) })
	public @ResponseBody Object checkStepName(@RequestBody TransStepDto transStep) throws Exception {
		return cloudStepService.checkStepName(transStep);
	}

	/**
	 * 请求方法 - 编辑步骤
	 * @param transStep
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.POST, value="/editStep")
	@ApiOperation(value = "获取可编辑步骤信息")
	@ApiResponses({ @ApiResponse(code = 200, response = StepDetailsDto.class, message = "成功" ) })
	public @ResponseBody Object editStep(@RequestBody TransStepDto transStep) throws Exception {
		return cloudStepService.editStep(transStep);
	}

	/**
	 * 请求方法 - 保存步骤
	 * @param stepDetails
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.POST, value="/saveStep")
	@ApiOperation(value = "保存步骤信息")
	@ApiResponses({ @ApiResponse(code = 200, response = ReturnCodeDto.class, message = "成功" ) })
	public @ResponseBody Object saveStep(@RequestBody StepDetailsDto stepDetails) throws Exception {
		checkPrivilege();
		return cloudStepService.saveStep(stepDetails);
	}

	/**
	 * 请求方法 - 编辑步骤属性参数
	 * @param transStep
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.POST, value="/editStepConfigs")
	@ApiOperation(value = "获取可编辑步骤属性参数(集群相关设置)")
	@ApiResponses({ @ApiResponse(code = 200, response = StepConfigsDto.class, message = "成功" ) })
	public @ResponseBody Object editStepConfigs(@RequestBody TransStepDto transStep) throws Exception {
		return cloudStepService.editStepConfigs(transStep);
	}

	/**
	 * 请求方法 - 保存步骤属性参数
	 * @param stepConfigs
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.POST, value="/saveStepConfigs")
	@ApiOperation(value = "保存步骤属性参数(集群相关设置)")
	@ApiResponses({ @ApiResponse(code = 200, response = ReturnCodeDto.class, message = "成功" ) })
	public @ResponseBody Object saveStepConfigs(@RequestBody StepConfigsDto stepConfigs) throws Exception {
		checkPrivilege();
		return cloudStepService.saveStepConfigs(stepConfigs);
	}

	/**
	 * 请求方法 - 删除步骤
	 * @param transStep
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.POST, value="/deleteStep")
	@ApiOperation(value = "删除步骤")
	@ApiResponses({ @ApiResponse(code = 200, response = ReturnCodeDto.class, message = "成功" ) })
	public @ResponseBody Object deleteStep(@RequestBody TransStepDto transStep) throws Exception {
		checkPrivilege();
		return cloudStepService.deleteStep(transStep);
	}

	/**
	 * 请求方法 - 移动步骤位置
	 * @param stepPosition
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.POST, value="/moveStep")
	@ApiOperation(value = "移动步骤位置")
	@ApiResponses({ @ApiResponse(code = 200, response = ReturnCodeDto.class, message = "成功" ) })
	public @ResponseBody Object moveStep(@RequestBody StepPositionDto stepPosition) throws Exception {
		checkPrivilege();
		return cloudStepService.moveStep(stepPosition);
	}

	/**
	 * 请求方法 - 获取步骤输入流字段
	 * @param transStep
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.POST, value="/getInputFields")
	@ApiOperation(value = "获取步骤输入流字段")
	@ApiResponses({ @ApiResponse(code = 200, response = StepFieldDto[].class, message = "成功" ) })
	public @ResponseBody Object getInputFields(@RequestBody TransStepDto transStep) throws Exception {
		return cloudStepService.getInputFields(transStep);
	}

	/**
	 * 请求方法 - 获取步骤输出流字段
	 * @param transStep
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.POST, value="/getOutputFields")
	@ApiOperation(value = "获取步骤输出流字段")
	@ApiResponses({ @ApiResponse(code = 200, response = StepFieldDto[].class, message = "成功" ) })
	public @ResponseBody Object getOutputFields(@RequestBody TransStepDto transStep) throws Exception {
		return cloudStepService.getOutputFields(transStep);
	}

	/**
	 * 请求方法 - 查询步骤详情
	 * @param transStep
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.POST, value="/getDetails")
	@ApiOperation(value = "查询步骤额外服务详情信息( 文件文件输入 获取文件域,execl输入获取 Sheets列表等)")
	public @ResponseBody Object getStepDetails(@RequestBody TransStepDto transStep) throws Exception {
		return cloudStepService.getDetails(transStep);
	}

	//
	// Logging object interface
	//
	public static final LoggingObjectInterface loggingObject = new SimpleLoggingObject("StepController", LoggingObjectType.STEP, null );

}
