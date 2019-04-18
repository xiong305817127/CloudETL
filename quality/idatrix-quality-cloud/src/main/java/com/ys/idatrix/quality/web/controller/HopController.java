/*
 * 云化数据集成系统
 * iDatrix CloudETL
 */
package com.ys.idatrix.quality.web.controller;

import org.pentaho.di.core.logging.LoggingObjectInterface;
import org.pentaho.di.core.logging.LoggingObjectType;
import org.pentaho.di.core.logging.SimpleLoggingObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ys.idatrix.quality.dto.common.ReturnCodeDto;
import com.ys.idatrix.quality.dto.hop.HopDto;
import com.ys.idatrix.quality.service.hop.CloudHopService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * 连接通道（步骤或节点间连线）控制器
 * Step hop procedure controller.
 * @author JW
 * @since 05-12-2017
 * 
 */
@Controller
@RequestMapping(value="/hop")
@Api(value = "/hop" , description="转换步骤 或调度节点的 连线 相关api")
public class HopController extends BaseAction {

	@Autowired
	private CloudHopService cloudHopService;

	/**
	 * 请求方法 - 新增连线
	 * @param transHop
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.POST, value="/addHop")
	@ApiOperation(value = "新增连线")
	@ApiResponses({ @ApiResponse(code = 200, response = ReturnCodeDto.class, message = "成功" ) })
	public @ResponseBody Object addHop(@RequestBody HopDto transHop) throws Exception {
		checkPrivilege();
		return cloudHopService.addHop(transHop);
	}

	/**
	 * 请求方法 - 编辑连线
	 * @param transHop
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.POST, value="/editHop")
	@ApiOperation(value = "获取可编辑连线的信息")
	@ApiResponses({ @ApiResponse(code = 200, response = ReturnCodeDto.class, message = "成功" ) })
	public @ResponseBody Object editHop(@RequestBody HopDto transHop) throws Exception {
		return cloudHopService.editHop(transHop);
	}

	/**
	 * 请求方法 - 反转连线方向
	 * @param transHop
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.POST, value="/invertHop")
	@ApiOperation(value = "反转连线方向")
	@ApiResponses({ @ApiResponse(code = 200, response = ReturnCodeDto.class, message = "成功" ) })
	public @ResponseBody Object invertHop(@RequestBody HopDto transHop) throws Exception {
		checkPrivilege();
		return cloudHopService.invertHop(transHop);
	}

	/**
	 * 请求方法 - 删除连线
	 * @param transHop
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.POST, value="/deleteHop")
	@ApiOperation(value = "删除连线")
	@ApiResponses({ @ApiResponse(code = 200, response = ReturnCodeDto.class, message = "成功" ) })
	public @ResponseBody Object deleteHop(@RequestBody HopDto transHop) throws Exception {
		checkPrivilege();
		return cloudHopService.deleteHop(transHop);
	}

	//
	// Logging object interface
	//
	public static final LoggingObjectInterface loggingObject = new SimpleLoggingObject("HopController", LoggingObjectType.STEP, null );

}
