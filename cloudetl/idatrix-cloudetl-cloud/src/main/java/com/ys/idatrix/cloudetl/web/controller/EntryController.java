/*
 * 云化数据集成系统
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.web.controller;

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

import com.ys.idatrix.cloudetl.dto.common.ReturnCodeDto;
import com.ys.idatrix.cloudetl.dto.entry.EntryCopyDto;
import com.ys.idatrix.cloudetl.dto.entry.EntryDetailsDto;
import com.ys.idatrix.cloudetl.dto.entry.EntryHeaderDto;
import com.ys.idatrix.cloudetl.dto.entry.EntryNameCheckResultDto;
import com.ys.idatrix.cloudetl.dto.entry.EntryPositionDto;
import com.ys.idatrix.cloudetl.dto.entry.JobEntryDto;
import com.ys.idatrix.cloudetl.service.job.CloudEntryService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * 调度节点流程处理控制器
 * Job entry procedure controller.
 * @author JW
 * @since 05-12-2017
 * 
 */
@Controller
@RequestMapping(value="/entry")
@Api(value = "/entry" , description="操作调度任务 节点  相关api")
public class EntryController extends BaseAction {

	@Autowired
	private CloudEntryService cloudEntryService;

	/**
	 * 请求方法 - 新增调度节点
	 * @param entryHeader
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.POST, value="/addEntry")
	@ApiOperation(value = "新增调度节点")
	@ApiResponses({ @ApiResponse(code = 200, response = ReturnCodeDto.class, message = "成功" ) })
	public @ResponseBody Object addEntry(@RequestBody EntryHeaderDto entryHeader) throws Exception {
		checkPrivilege();
		return cloudEntryService.addEntry(entryHeader);
	}

	@RequestMapping(method=RequestMethod.POST, value="/copyEntry")
	@ApiOperation(value = "复制调度节点")
	@ApiResponses({ @ApiResponse(code = 200, response = ReturnCodeDto.class, message = "成功" ) })
	public @ResponseBody Object copyEntry(@RequestBody EntryCopyDto entrycopy) throws Exception {
		saveResourceOwner(Const.NVL(entrycopy.getToOwner(),entrycopy.getOwner()) );
		checkPrivilege();
		return cloudEntryService.copyEntry(entrycopy) ;
	}
	
	/**
	 * 请求方法 - 检查调度节点名是否存在
	 * @param jobEntry
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.POST, value="/checkEntryName")
	@ApiOperation(value = "检查调度节点名是否存在")
	@ApiResponses({ @ApiResponse(code = 200, response = EntryNameCheckResultDto.class, message = "成功" ) })
	public @ResponseBody Object checkEntryName(@RequestBody JobEntryDto jobEntry) throws Exception {
		return cloudEntryService.checkEntryName(jobEntry);
	}

	/**
	 * 请求方法 - 编辑调度节点信息
	 * @param jobEntry
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.POST, value="/editEntry")
	@ApiOperation(value = "获取可编辑调度节点信息")
	@ApiResponses({ @ApiResponse(code = 200, response = EntryDetailsDto.class, message = "成功" ) })
	public @ResponseBody Object editEntry(@RequestBody JobEntryDto jobEntry) throws Exception {
		return cloudEntryService.editEntry(jobEntry);
	}

	/**
	 * 请求方法 - 保存调度节点信息
	 * @param entryDetails
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.POST, value="/saveEntry")
	@ApiOperation(value = "保存调度节点信息")
	@ApiResponses({ @ApiResponse(code = 200, response = ReturnCodeDto.class, message = "成功" ) })
	public @ResponseBody Object saveEntry(@RequestBody EntryDetailsDto entryDetails) throws Exception {
		checkPrivilege();
		return cloudEntryService.saveEntry(entryDetails);
	}

	/**
	 * 请求方法 - 删除调度节点
	 * @param jobEntry
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.POST, value="/deleteEntry")
	@ApiOperation(value = "删除调度节点")
	@ApiResponses({ @ApiResponse(code = 200, response = ReturnCodeDto.class, message = "成功" ) })
	public @ResponseBody Object deleteEntry(@RequestBody JobEntryDto jobEntry) throws Exception {
		checkPrivilege();
		return cloudEntryService.deleteEntry(jobEntry);
	}

	/**
	 * 请求方法 - 移动调度节点位置
	 * @param entryPosition
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.POST, value="/moveEntry")
	@ApiOperation(value = "移动调度节点位置")
	@ApiResponses({ @ApiResponse(code = 200, response = ReturnCodeDto.class, message = "成功" ) })
	public @ResponseBody Object moveEntry(@RequestBody EntryPositionDto entryPosition) throws Exception {
		checkPrivilege();
		return cloudEntryService.moveEntry(entryPosition);
	}

	/**
	 * 请求方法 - 查询调度节点详情
	 * @param jobEntry
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.POST, value="/getDetails")
	@ApiOperation(value = "查询调度节点详情")
	public @ResponseBody  Object getEntryDetails(@RequestBody JobEntryDto jobEntry) throws Exception{
		return cloudEntryService.getDetails(jobEntry);
	}

	//
	// Logging object interface
	//
	public static final LoggingObjectInterface loggingObject = new SimpleLoggingObject("EntryController", LoggingObjectType.STEP, null );

}
