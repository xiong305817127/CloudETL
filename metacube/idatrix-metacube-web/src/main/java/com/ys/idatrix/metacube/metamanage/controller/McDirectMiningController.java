package com.ys.idatrix.metacube.metamanage.controller;

import com.idatrix.unisecurity.sso.client.enums.ResultEnum;
import com.ys.idatrix.metacube.api.beans.ResultBean;
import com.ys.idatrix.metacube.common.exception.MetaDataException;
import com.ys.idatrix.metacube.common.utils.UserUtils;
import com.ys.idatrix.metacube.metamanage.service.McDirectMiningService;
import com.ys.idatrix.metacube.metamanage.service.impl.McDirectMiningServiceImpl.MiningTaskDto;
import com.ys.idatrix.metacube.metamanage.vo.request.DirectMiningSaveVO;
import com.ys.idatrix.metacube.metamanage.vo.request.TableVO;
import com.ys.idatrix.metacube.metamanage.vo.request.ViewVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
//@Slf4j
@RestController
@RequestMapping("/direct")
@Api(value = "/direct", tags = "元数据管理-元数据定义-数据直采")
public class McDirectMiningController {
	
	   @Autowired
	   McDirectMiningService mcDirectMiningService ;

	   @ApiOperation(value = "元数据定义-获取正在执行直采的任务信息,为空:未执行过, status(大于10为上次已完成): 0: 初始化 , 1: 进行中 , 11:成功完成 ,  12: 部分完成, 13 :运行异常 ",  httpMethod = "GET")
	    @GetMapping("/getMiningStatus")
	    public ResultBean<MiningTaskDto> getDirectMiningTaskStatus( @RequestParam(required = true ) Long schemaId ,@RequestParam(required = true ) int resourceType) {
	      MiningTaskDto runnings = mcDirectMiningService.getMiningTask(schemaId, resourceType);
	      return ResultBean.ok(runnings);
	    }
	   
	    @ApiOperation(value = "元数据定义-获取可直采表信息",  httpMethod = "GET")
	    @GetMapping("/getTables")
	    public ResultBean< List<? extends TableVO>> getDirectMiningTables( @RequestParam(required = true ) Long schemaId ) {
	       List<? extends TableVO> tables = mcDirectMiningService.getTableAllInfo( schemaId);
	        return ResultBean.ok(tables);
	    }

	    @ApiOperation(value = "元数据定义-获取可直采视图信息",  httpMethod = "GET")
	    @GetMapping("/getViews")
	    public ResultBean< List<? extends ViewVO>> getDirectMiningViews( @RequestParam(required = true ) Long schemaId ) {
	    	List<? extends ViewVO> views = mcDirectMiningService.getViewAllInfo(schemaId);
	        return ResultBean.ok(views);
	    }
	   
	    @ApiOperation(value = "元数据定义-保存批量直采表信息",  httpMethod = "POST")
	    @PostMapping("/saveTables")
	    public ResultBean<String> saveDirectMiningTables(@Validated @RequestBody DirectMiningSaveVO saveVo ,BindingResult bindingResult) {
	    	
	    	 if (bindingResult.hasErrors()) {
	             throw new MetaDataException(ResultEnum.PARAM_ERROR.getCode(), bindingResult.getFieldError().getDefaultMessage());
	         }
	    	 MiningTaskDto cachedto = mcDirectMiningService.getMiningTask(saveVo.getSchemaId(),1);
	 		 if(cachedto != null &&  !cachedto.isEnd() ) {
	 			//正在运作中...
	 			throw new MetaDataException(cachedto.getMessage());
	 		 }
	    	 //保存当前用户对象
	 		UserUtils.addCacheMap("DirectMining-"+saveVo.getSchemaId());
	 		 
	    	mcDirectMiningService.directMiningTables(saveVo.getSchemaId(),saveVo.getMetadataBase() );
	        return ResultBean.ok();
	    }
	    
	    @ApiOperation(value = "元数据定义-保存批量直采视图信息",  httpMethod = "POST")
	    @PostMapping("/saveViews")
	    public ResultBean<String> saveDirectMiningViews(@Validated @RequestBody DirectMiningSaveVO saveVo ,BindingResult bindingResult) {
	    	
	    	 if (bindingResult.hasErrors()) {
	             throw new MetaDataException(ResultEnum.PARAM_ERROR.getCode(), bindingResult.getFieldError().getDefaultMessage());
	         }
	    	 MiningTaskDto cachedto = mcDirectMiningService.getMiningTask(saveVo.getSchemaId(),2);
	 		 if(cachedto != null &&  !cachedto.isEnd() ) {
	 			//正在运作中...
	 			throw new MetaDataException(cachedto.getMessage());
	 		 }
	 		 //保存当前用户对象
		 	 UserUtils.addCacheMap("DirectMining-"+saveVo.getSchemaId());
		 		
	    	 mcDirectMiningService.directMiningViews( saveVo.getSchemaId(),saveVo.getMetadataBase() );
	         return ResultBean.ok();
	    }
	   
}
