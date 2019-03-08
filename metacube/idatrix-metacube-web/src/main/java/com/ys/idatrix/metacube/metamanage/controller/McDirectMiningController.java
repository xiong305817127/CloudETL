package com.ys.idatrix.metacube.metamanage.controller;

import com.idatrix.unisecurity.sso.client.enums.ResultEnum;
import com.ys.idatrix.metacube.api.beans.ResultBean;
import com.ys.idatrix.metacube.common.exception.MetaDataException;
import com.ys.idatrix.metacube.metamanage.service.McDirectMiningService;
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

	    @ApiOperation(value = "元数据定义-获取可直采表信息",  httpMethod = "GET")
	    @GetMapping("/getTables")
	    public ResultBean< List<? extends TableVO>> getDirectMiningTables( @RequestParam(required = true, defaultValue = "1") Integer databaseType,
	    		@RequestParam(required = true ) Long schemaId ) {
	       List<? extends TableVO> tables = mcDirectMiningService.getTableAllInfo(databaseType, schemaId);
	        return ResultBean.ok(tables);
	    }

	    @ApiOperation(value = "元数据定义-获取可直采视图信息",  httpMethod = "GET")
	    @GetMapping("/getViews")
	    public ResultBean< List<? extends ViewVO>> getDirectMiningViews( @RequestParam(required = true, defaultValue = "1")Integer databaseType,
	    		@RequestParam(required = true ) Long schemaId ) {
	    	List<? extends ViewVO> views = mcDirectMiningService.getViewAllInfo(databaseType, schemaId);
	        return ResultBean.ok(views);
	    }
	   
	    @ApiOperation(value = "元数据定义-保存批量直采表信息",  httpMethod = "POST")
	    @PostMapping("/saveTables")
	    public ResultBean<List<? extends TableVO>> saveDirectMiningTables(@Validated @RequestBody DirectMiningSaveVO saveVo ,BindingResult bindingResult) {
	    	
	    	 if (bindingResult.hasErrors()) {
	             throw new MetaDataException(ResultEnum.PARAM_ERROR.getCode(), bindingResult.getFieldError().getDefaultMessage());
	         }
	    	List<? extends TableVO> res = mcDirectMiningService.directMiningTables(saveVo.getDatabaseType(), saveVo.getSchemaId(),saveVo.getMetadataBase() );
	        return ResultBean.ok(res);
	    }
	    
	    @ApiOperation(value = "元数据定义-保存批量直采视图信息",  httpMethod = "POST")
	    @PostMapping("/saveViews")
	    public ResultBean<List<? extends ViewVO>> saveDirectMiningViews(@Validated @RequestBody DirectMiningSaveVO saveVo ,BindingResult bindingResult) {
	    	
	    	 if (bindingResult.hasErrors()) {
	             throw new MetaDataException(ResultEnum.PARAM_ERROR.getCode(), bindingResult.getFieldError().getDefaultMessage());
	         }
	    	 List<? extends ViewVO> res = mcDirectMiningService.directMiningViews(saveVo.getDatabaseType(), saveVo.getSchemaId(),saveVo.getMetadataBase() );
	        return ResultBean.ok(res);
	    }
	   
}
