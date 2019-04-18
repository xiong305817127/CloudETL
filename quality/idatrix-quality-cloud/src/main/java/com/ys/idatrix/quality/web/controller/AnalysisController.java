/**
 * 云化数据集成系统
 * iDatrix CloudETL
 */
package com.ys.idatrix.quality.web.controller;

import com.ys.idatrix.quality.analysis.dto.NodeDictDataDto;
import com.ys.idatrix.quality.analysis.dto.NodeDictDto;
import com.ys.idatrix.quality.analysis.dto.NodeRecordDto;
import com.ys.idatrix.quality.analysis.dto.NodeResultDto;
import com.ys.idatrix.quality.dto.common.PaginationDto;
import com.ys.idatrix.quality.dto.common.ReturnCodeDto;
import com.ys.idatrix.quality.ext.utils.FileReadUtil;
import com.ys.idatrix.quality.service.analysis.CloudAnalysisService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.metrics2.sink.relocated.google.common.collect.Maps;
import org.pentaho.di.core.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

/**
 * 主界面流程控制器 Cloud ETL main procedure controller
 *
 * @author JW
 * @since 05-12-2017
 */
@RestController
@RequestMapping(value = "/analysis")
public class AnalysisController extends BaseAction {

	@Autowired
	private CloudAnalysisService cloudAnalysisService;

	/**
	 * 请求方法 - 获取记录任务列表
	 *
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/getRecordList")
	@ApiResponses({ @ApiResponse(code = 200, response = NodeRecordDto[].class, message = "成功" ) })
	public @ResponseBody Object getRecordList(@RequestParam(required = false) String execId,
			@RequestParam(required = false, defaultValue = "-1") Integer page,
			@RequestParam(required = false, defaultValue = "10") Integer pageSize,
			@RequestParam(required = false) String search) throws Exception {

		return cloudAnalysisService.getAnalysisRecords(execId);
	}

	/**
	 * 请求方法 - 获取记录任务信息
	 *
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/getRecordInfo")
	@ApiResponses({ @ApiResponse(code = 200, response = NodeRecordDto.class, message = "成功" ) })
	public @ResponseBody Object getRecordInfo(@RequestParam(required = false) String uuid,
			@RequestParam(required = false) String execId, @RequestParam(required = false) String nodId)
			throws Exception {
		if (!Utils.isEmpty(uuid)) {
			return cloudAnalysisService.getAnalysisRecordsInfo(uuid);
		} else {
			return cloudAnalysisService.getAnalysisRecordsInfo(execId, nodId);
		}

	}

	/**
	 * 请求方法 - 获取记录任务结果列表
	 *
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/getResultInfo")
	@ApiResponses({ @ApiResponse(code = 200, response = NodeResultDto.class, message = "成功" ) })
	public @ResponseBody Object getResultInfo(@RequestParam String execId, @RequestParam(required = false) String nodId,
			@RequestParam(required = false) String referenceValue,
			@RequestParam(required = false, defaultValue = "false") boolean isList) throws Exception {

		if (!Utils.isEmpty(referenceValue) && !Utils.isEmpty(nodId)) {
			return cloudAnalysisService.getAnalysisResult(execId, nodId, referenceValue);
		} else if (!Utils.isEmpty(nodId)) {
			return cloudAnalysisService.getAnalysisResult(execId, nodId, isList);
		} else {
			return cloudAnalysisService.getAnalysisResult(execId, isList);
		}
	}
	
	/** 请求方法 - 获取冗余详情列表
	 *
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/getRedundanceDetail")
	@ApiResponses({ @ApiResponse(code = 200, response = Map.class, message = "成功" ) })
	public @ResponseBody Object getRedundanceDetail(@RequestParam String execId, @RequestParam(required = false) String nodId ) throws Exception {
		return cloudAnalysisService.getRedundanceDetail(execId, nodId) ;
	}

	/**
	 * 查询字典列表，带分页（字典页面使用）
	 *
	 * @param dictName
	 *            数据字典name
	 * @param page
	 *            当期显示第几页,-1 代表不进行分页
	 * @param size
	 *            一页显示多少数据
	 * @return
	 */
	@RequestMapping(value = "/dictList", method = RequestMethod.GET)
	@ApiResponses({ @ApiResponse(code = 200, response = NodeDictDto.class, message = "成功" ) })
	@ResponseBody
	public Object dictList(@RequestParam(required = false, defaultValue = "") String dictNameValue,
			@RequestParam(required = false, defaultValue = "-1") Integer page,
			@RequestParam(required = false, defaultValue = "10") Integer size) throws Exception {

		PaginationDto<NodeDictDto> pageDto = new PaginationDto<NodeDictDto>(page, size, dictNameValue);

		Long count = cloudAnalysisService.dictFindCount(dictNameValue);
		pageDto.setTotal(count.intValue());

		List<NodeDictDto> list = cloudAnalysisService.dictFindPage(dictNameValue, page, size);
		pageDto.setRows(list);

		return pageDto;
	}

	/**
	 * 获取所有的已生效状态的字典列表，不带分页（标准值组件使用）
	 *
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/dictAllList", method = RequestMethod.GET)
	@ApiResponses({ @ApiResponse(code = 200, response = NodeDictDto[].class, message = "成功" ) })
	@ResponseBody
	public Object dictNameList(@RequestParam(value = "status", defaultValue = "1", required = false) Integer status)
			throws Exception {
		return cloudAnalysisService.findDictListByStatus(status);
	}

	/**
	 * @throws @title
	 *             dict
	 * @description 根据id获取字典对象
	 * @param: id
	 * @author oyr
	 * @updateTime 2018/10/17 14:23
	 * @return: java.lang.Object
	 */
	@RequestMapping(value = "/dict/{id}", method = RequestMethod.GET)
	@ApiResponses({ @ApiResponse(code = 200, response = NodeDictDto.class, message = "成功" ) })
	@ResponseBody
	public Object dict(@PathVariable("id") String id) throws Exception {
		return cloudAnalysisService.findDictById(id);
	}

	/**
	 * @title add @description 新增字典 @param: dataDict @author oyr @updateTime
	 * 2018/10/18 15:22 @return: java.lang.Object @throws
	 */
	@RequestMapping(value = "/dict", method = RequestMethod.POST)
	@ResponseBody
	public Object add(@RequestBody NodeDictDto dataDict) throws Exception {
		if (dataDict == null || StringUtils.isEmpty(dataDict.getDictName())) {
			throw new Exception("参数有误(字典名称不能为空).");
		}
		
		cloudAnalysisService.addDataDict(dataDict);
		return new ReturnCodeDto(0);
	}

	/**
	 * @throws @title
	 *             updateActiveStatus
	 * @description 修改字典的生效状态
	 * @param: dataDict
	 * @author oyr
	 * @updateTime 2018/10/15 11:03
	 * @return: java.lang.Object
	 */
	@RequestMapping(value = "/dict/status", method = RequestMethod.POST)
	@ResponseBody
	public Object updateDictStatus(@RequestBody NodeDictDto dict) throws Exception {
		if (dict == null || StringUtils.isEmpty(dict.getId()) || dict.getStatus() == null) {
			throw new Exception("参数有误(ID或者状态为空).");
		}
		cloudAnalysisService.updateDictStatus(dict);
		return new ReturnCodeDto(0);
	}

	/**
	 * @title deleteDict
	 * @description 删除字典
	 * @param: id
	 * @author oyr
	 * @updateTime 2018/10/17 10:45
	 * @return: java.lang.Object
	 * @throws
	 * 
	 * 			@RequestMapping(value
	 *             = "/dict/{id}", method = RequestMethod.DELETE)
	 * @ResponseBody public Object deleteDict(@PathVariable String id){
	 *               cloudAnalysisService.deleteDict(id); Map<String, Object>
	 *               resultMap = Maps.newHashMap(); resultMap.put("code", 200);
	 *               return resultMap; }
	 */

	/**
	 * @title dataDict @description 修改数据字典 @param: dataDict @author oyr @updateTime
	 * 2018/10/18 14:29 @return: java.lang.Object @throws
	 */
	@RequestMapping(value = "/dict/update", method = RequestMethod.POST)
	public Object dataDict(@RequestBody NodeDictDto dataDict) throws Exception {
		if (dataDict == null || StringUtils.isEmpty(dataDict.getId()) || StringUtils.isEmpty(dataDict.getDictName())) {
			throw new Exception("参数有误(ID,字典名称 为空).");
		}
		cloudAnalysisService.updateDataDict(dataDict);
		return new ReturnCodeDto(0);
	}

	/**
	 * @throws @title
	 *             dictDataList
	 * @description 根据字典ID，查询字典下的数据，带分页（字典数据页面使用）
	 * @author oyr
	 * @updateTime 2018/10/15 15:09
	 * @return: java.util.List<com.ys.idatrix.quality.analysis.dto.NodeDictDataDto>
	 */
	@RequestMapping(value = "/dictDataList/{dictId}", method = RequestMethod.GET)
	@ApiResponses({ @ApiResponse(code = 200, response = NodeDictDataDto.class, message = "成功" ) })
	@ResponseBody
	public Object dictDataList(@PathVariable String dictId,
			@RequestParam(required = false, defaultValue = "") String value,
			@RequestParam(required = false, defaultValue = "-1") Integer page,
			@RequestParam(required = false, defaultValue = "10") Integer size) throws Exception {

		PaginationDto<NodeDictDataDto> pageDto = new PaginationDto<NodeDictDataDto>(page, size, value);

		Long count = cloudAnalysisService.dictDataFindCount(dictId, value);
		pageDto.setTotal(count.intValue());

		List<NodeDictDataDto> list = cloudAnalysisService.dictDataFindPage(dictId, value, page, size);
		pageDto.setRows(list);
		
		cloudAnalysisService.findDictById(dictId);
		
		//额外增加 字典ID
		pageDto.addOther("dictId", dictId);
		pageDto.addOther("dictdata", cloudAnalysisService.findDictById(dictId));

		return pageDto;
	}

	/**
	 * @throws @title
	 *             dictDataList
	 * @description 根据字典ID，查询字典下的所有数据，不带分页（标准值页面使用）
	 * @author oyr
	 * @updateTime 2018/10/15 15:09
	 * @return: java.util.List<com.ys.idatrix.quality.analysis.dto.NodeDictDataDto>
	 */
	@RequestMapping(value = "/dictDataAllList/{dictId}", method = RequestMethod.GET)
	@ApiResponses({ @ApiResponse(code = 200, response = NodeDictDataDto[].class, message = "成功" ) })
	@ResponseBody
	public Object dictDataAllList(@PathVariable String dictId) throws Exception {
		return cloudAnalysisService.findDictDataListByDictId(dictId);
	}

	/**
	 * @throws @title
	 *             dictData
	 * @description 根据id获取字典数据的某条（标准值页面使用）
	 * @author oyr
	 * @updateTime 2018/10/16 10:59
	 * @return: java.lang.Object
	 */
	@RequestMapping(value = "/dictData/{id}", method = RequestMethod.GET)
	@ApiResponses({ @ApiResponse(code = 200, response = NodeDictDataDto.class, message = "成功" ) })
	@ResponseBody
	public Object dictData(@PathVariable Long id) throws Exception {
		return cloudAnalysisService.findDictDataById(id);
	}

	/**
	 * @throws @title
	 *             updateDictData
	 * @description 根据 字典数据id 修改字典数据（字典数据使用）
	 * @author oyr
	 * @updateTime 2018/10/15 15:19
	 * @return: java.lang.Object
	 */
	@RequestMapping(value = "/dictData/update", method = RequestMethod.POST)
	@ResponseBody
	public Object updateDictData(@RequestBody NodeDictDataDto dictData) throws Exception {
		if (dictData == null || StringUtils.isEmpty(dictData.getDictId()) || dictData.getId() == null
				|| StringUtils.isEmpty(dictData.getStdVal1())) {
			throw new Exception("必要的参数(字典ID,字典数据ID,数据标准值)不能为空.");
		}
		
		cloudAnalysisService.updateDictData(dictData);
		return new ReturnCodeDto(0);
	}

	/**
	 * POI：操作excel文件 CSVReader：操作csv文件
	 * 
	 * @throws @title
	 *             analysisCsvFile
	 * @description 新增字典数据（通过上传文件的方式新增，支持 excel 文件 和 CSV格式的文件）（字典数据使用）
	 * @param: dictId
	 * @param: file
	 * @param: request
	 * @author oyr
	 * @updateTime 2018/10/16 19:25
	 * @return: java.lang.Object
	 */
	@RequestMapping(value = "/dictData", method = RequestMethod.POST)
	@ResponseBody
	public Object uploadDictData(@RequestParam("dictId") String dictId, @RequestParam("file") MultipartFile file ) throws Exception {

		if (file == null) {
			throw new Exception("文件为空，新增数据失败。");
		}

		String fileName = file.getOriginalFilename();
		String fileExtName = fileName.substring(fileName.lastIndexOf(".") + 1);
		if (!"csv".equalsIgnoreCase(fileExtName) && !"txt".equalsIgnoreCase(fileExtName)
				&& !"xls".equalsIgnoreCase(fileExtName) && !"xlsx".equalsIgnoreCase(fileExtName)) {
			throw new Exception("不支持的文件类型，当前系统只支持 excel 和 csv 文件。");
		}
		// 当前的字典是有效的
		NodeDictDto dict = cloudAnalysisService.findDictById(dictId);
		if (dict == null) {
			throw new Exception("未找到关联的字典信息.");
		}

		List<Object[]> data = FileReadUtil.readDataFromFile(null, fileName, file.getInputStream(), null, null);
		if (data != null && data.size() > 1) {
			return  cloudAnalysisService.insertBatchDictData(dictId, data);
		}else {
			Map<String, Object> result = Maps.newHashMap();
			result.put("successIds", new String[] {});
			result.put("successMessage", "");
			result.put("errorMessage",  "文件列表为空.");
			return result;
		}
	}

	/**
	 * 请求方法 - 删除转换
	 * @param jsonTransName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.POST, value="/deletedictData")
	@ApiOperation(value = "删除字典数据")
	@ApiResponses({ @ApiResponse(code = 200, response = ReturnCodeDto.class, message = "成功" ) })
	public @ResponseBody Object deletedictData(@RequestBody Map<String,Object> body) throws Exception {
		
		@SuppressWarnings("unchecked")
		List<Integer> ids =  (List<Integer>) body.get("ids");
		String dictId = (String)body.get("dictId") ;
		
		return cloudAnalysisService.deleteBatchDictData(dictId, ids.toArray(new Integer[] {}));
	}

	
}
