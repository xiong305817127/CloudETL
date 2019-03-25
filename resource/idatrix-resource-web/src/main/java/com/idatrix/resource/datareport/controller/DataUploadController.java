package com.idatrix.resource.datareport.controller;

import com.idatrix.resource.catalog.dao.ResourceConfigDAO;
import com.idatrix.resource.catalog.po.ResourceConfigPO;
import com.idatrix.resource.common.Exception.CommonServiceException;
import com.idatrix.resource.common.controller.BaseController;
import com.idatrix.resource.common.utils.*;
import com.idatrix.resource.datareport.po.ResourceFilePO;
import com.idatrix.resource.datareport.service.IDataUploadService;
import com.idatrix.resource.datareport.service.IResourceFileService;
import com.idatrix.resource.datareport.vo.BrowseDataVO;
import com.idatrix.resource.datareport.vo.DataUploadTotalVO;
import com.idatrix.resource.datareport.vo.PubFileNameVO;
import com.idatrix.resource.portal.service.IStatisticsDeptService;
import com.idatrix.unisecurity.api.domain.Organization;
import com.idatrix.unisecurity.api.domain.User;
import com.idatrix.unisecurity.api.service.UserService;
import com.ys.idatrix.db.api.hdfs.service.HdfsUnrestrictedService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.zookeeper.common.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据上报流程
 */

@Controller
@RequestMapping("/dataUpload")
@Api(value = "/dataUpload" , tags="数据上报-数据上报接口")
public class DataUploadController extends BaseController {

	@Autowired
    private IDataUploadService iDataUploadService;

    @Autowired
    private IResourceFileService resourceFileService;

    @Autowired
    private HdfsUnrestrictedService hdfsUnDaoHessian;

    @Autowired
    private UserUtils userUtils;

    @Autowired
    private UserService userService;

    @Autowired
    private ResourceConfigDAO resourceConfigDAO;

    @Autowired
    private IStatisticsDeptService statisticsDeptService;


	private static final Logger LOG = LoggerFactory.getLogger(DataUploadController.class);

	/*新增数据上报*/

	/**
	 *
	 * @param resourceId		所选资源ID
	 * @param dataBatch			数据批次
	 * @param formatType		数据格式类型 0,1,2,3,4,5,6,7,8
	 * @param file				上传文件
	 * @return
	 */
    @ApiOperation(value = "数据库类资源上报", notes="数据库类资源上报", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "resourceId", value = "资源ID", required = false,dataType="Long"),
            @ApiImplicitParam(name = "dataBatch", value = "数据批次", required = false,dataType="String"),
            @ApiImplicitParam(name = "formatType", value = "资源格式类型", required = false,dataType="Integer"),
            @ApiImplicitParam(name = "file", value = "文件内容", required = false,dataType="CommonsMultipartFile")
    })
	@RequestMapping(value="/saveOrUpdateUploadDataForDB", method= RequestMethod.POST)
	@ResponseBody
	public Result saveOrUpdateUploadDataForDB(@RequestParam("resourceId") Long resourceId,
											  @RequestParam("dataBatch") String dataBatch,
											  @RequestParam("formatType") Integer formatType,
											  @RequestParam(value = "file") CommonsMultipartFile file) {

		//根据登录用户来过滤数据上传记录
		String user = getUserName();
		Long rentId = userUtils.getCurrentUserRentId();
		try {
			Long result = iDataUploadService.saveOrUpdateUploadDataForDB(rentId, resourceId, dataBatch, formatType, file, user);
			return Result.ok(result);
		} catch (RuntimeException e) {
			LOG.error(DateTools.getDateTime() + ", 进行数据上报操作时出现异常错误");
			LOG.error(e.getMessage());
			return Result.error("出现异常错误, 保存上传文件失败, "+e.getMessage());
		} catch (CommonServiceException e) {
			LOG.error(DateTools.getDateTime() + ", 进行数据上报操作时出现异常错误");
			LOG.error(e.getMessage());
			return Result.error(e.getMessage());
		}
	}

	/*新增数据上报*/
	/**
	 * @param formatType		数据格式类型 0,1,2,3,4,5,6,7,8
	 * @param files				上传的文件
	 */
    @ApiOperation(value = "文件类资源上报", notes="文件类资源上报", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "resourceId", value = "资源ID", required = false,dataType="Long"),
            @ApiImplicitParam(name = "formatType", value = "资源格式类型", required = false,dataType="Integer"),
            @ApiImplicitParam(name = "files", value = "文件内容", required = false,dataType="CommonsMultipartFile")
    })
	@RequestMapping(value="/saveOrUpdateUploadDataForFILE", method= RequestMethod.POST)
	@ResponseBody
	public Result saveOrUpdateUploadDataForFILE(@RequestParam("resourceId") Long resourceId,
												@RequestParam("formatType") Integer formatType,
											  @RequestParam(value = "files") CommonsMultipartFile[] files) {
        //根据登录用户来过滤数据上传记录
        String user = getUserName();
	    Long rentId = userUtils.getCurrentUserRentId();
	    try {
		    List<Map<String, Object>> result
					= iDataUploadService.saveOrUpdateUploadDataForFILE(resourceId, formatType, files, user);
			return Result.ok(result);
		} catch (RuntimeException e) {
			LOG.error(DateTools.getDateTime() + ", 进行数据上报操作时出现异常错误");
			LOG.error(e.getMessage());
			return Result.error(e.getMessage());
		} catch (CommonServiceException e) {
			LOG.error(DateTools.getDateTime() + ", 进行数据上报操作时出现异常错误");
			LOG.error(e.getMessage());

			return Result.error(e.getMessage());
		}
	}

	/*文件类数据上报时, 判断该资源ID下的同发布名文件是否已存在*/
    @ApiOperation(value = "文件上报同文件名称检测", notes="件类数据上报时, 判断该资源ID下的同发布名文件是否已存在", httpMethod = "POST")
	@RequestMapping(value="/isExistedResourceFile", method= RequestMethod.POST)
	@ResponseBody
	public Result isExistedResourceFile(@RequestBody PubFileNameVO pubFileNameVO) {
		String existedFiles = iDataUploadService.isExistedResourceFiles(pubFileNameVO.getResourceId(),
				pubFileNameVO.getPubFileName());

		Result result = new Result();

		if (CommonUtils.isEmptyStr(existedFiles))
			return result.ok("");
		else {
		    if (pubFileNameVO.getPubFileName().length == 1) {
				Map<String, Object> resultMap = new HashMap<String, Object>();
				resultMap.put("isExisted", true);
                result.setData(resultMap);
            }
            return result.error(existedFiles);
        }

	}

    @ApiOperation(value = "数据库类型资源模板下载", notes="文件下载", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "resourceId", value = "资源ID", required = false,dataType="Long"),
    })
	@RequestMapping(value="/downloadTemplate") //,produces = {"application/vnd.ms-excel;charset=UTF-8"})
	@ResponseBody
    public Result download(@RequestParam("resourceId") Long resourceId, HttpServletResponse response){
		if (CommonUtils.isEmptyLongValue(resourceId)) {
			return Result.error("资源ID为空, 无法生成相应模板");
		}

		try {
			iDataUploadService.downLoadExcelTemplate(resourceId, response);

			return Result.ok("模板下载成功");
		} catch (CommonServiceException e) {
			return Result.error(e.getMessage());
		}
	}

	/**
	 *
	 * @param name				资源名称
	 * @param code				资源代码
	 * @param pubFileName		"数据库类上传"时的文件名; "文件类上传"时的文件展示名称
	 * @param status			资源发布状态
	 * @param pageNum			当前页数
	 * @param pageSize			每页显示数据量
	 */
	/*根据查询条件查找数据上报记录*/
    @ApiOperation(value = "上报记录查询", notes="根据查询条件查找数据上报记录", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "资源名称", required = false, dataType="String",paramType = "query"),
            @ApiImplicitParam(name = "code", value = "资源代码", required = false, dataType="String",paramType = "query"),
            @ApiImplicitParam(name = "pubFileName", value = "文件名称", required = false, dataType="String",paramType = "query"),
            @ApiImplicitParam(name = "status", value = "资源发布状态", required = false, dataType="String",paramType = "query"),
            @ApiImplicitParam(name = "page", value = "分页当前页数", required = false, dataType="Integer",paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页每页显示数据量", required = false, dataType="Integer",paramType = "query")
    })
	@RequestMapping(value="/getAllDateUploadRecords", method= RequestMethod.GET)
	@ResponseBody
	public Result getAllDateUploadRecords (
			@RequestParam(value = "name", required = false) String  name,
			@RequestParam(value = "code", required = false) String  code,
			@RequestParam(value = "pubFileName", required = false) String  pubFileName,
			@RequestParam(value = "status", required = false) String status,
			@RequestParam(value = "page", required = false) Integer pageNum,
			@RequestParam(value = "pageSize", required = false) Integer pageSize) {

		Map<String, String> queryCondition = new HashMap<String, String>();

		if(StringUtils.isNotEmpty(name)
				&& !CommonUtils.isOverLimitedLength(name, 100)){
			queryCondition.put("name", name);
		}
		if(StringUtils.isNotEmpty(code)
				&& !CommonUtils.isOverLimitedLength(code, 50)){
			queryCondition.put("code", code);
		}
		if(StringUtils.isNotEmpty(status)){
			queryCondition.put("status", status);
		}
		if(StringUtils.isNotEmpty(pubFileName)){
			queryCondition.put("pubFileName", pubFileName);
		}

		//根据登录用户来过滤数据上传记录
		String user = getUserName(); //"admin";
		queryCondition.put("creator", user);

		try {
			ResultPager tasks
					= iDataUploadService.getDataUploadRecordByCondition(queryCondition, pubFileName, pageNum, pageSize);
//  Add: 王斌修改，查询数据不存在，不用按照错误返回，返回空即可 20180821
//			if (tasks != null)
				return Result.ok(tasks);
//			else
//				return Result.error(CommonConstants.EC_NOT_EXISTED_VALUE, "所查询的数据上报信息不存在");
		}catch(Exception e){
			e.printStackTrace();
			return Result.error(e.getMessage()); //调试Ajax屏蔽掉
		}
	}

	/*上报文件查看*/
    @ApiOperation(value = "上报文件查询", notes="上报文件查询", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "资源ID", required = false, dataType="Long",paramType = "query"),
            @ApiImplicitParam(name = "order", value = "按照升序或者降序，传送数值 down/up", required = false, dataType="String",paramType = "query"),
            @ApiImplicitParam(name = "name", value = "文件名称", required = false, dataType="String",paramType = "query"),
            @ApiImplicitParam(name = "page", value = "分页起始页", required = false, dataType="Integer",paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页页面大小", required = false, dataType="Integer",paramType = "query")
    })
	@RequestMapping(value="/getResourceFile", method= RequestMethod.GET)
	@ResponseBody
	public Result getResourceFile(
			    @RequestParam(value = "id") Long resourceId,
				@RequestParam(value = "order", required = false) String  order,
				@RequestParam(value = "name", required = false) String  name,
				@RequestParam(value = "page", required = false) Integer pageNum,
				@RequestParam(value = "pageSize", required = false) Integer pageSize){
		ResultPager tasks;
		Map<String, String> con = new HashMap<String, String>();
		con.put("id",resourceId.toString());
		if(StringUtils.isNoneEmpty(name)){
			con.put("fileName", name);
		}
		//后面只判断是否非空，不做取值判断
		if(StringUtils.equals(order, "up")){
			con.put("ascOrder", order);
		}else{
			con.put("descOrder", "down");
		}

		String user = userUtils.getCurrentUserName();
		try{
			tasks = resourceFileService.queryResourceFile(user, con, pageNum, pageSize);
		}catch(Exception e){
			e.printStackTrace();
			return Result.error(e.getMessage());
		}
		return Result.ok(tasks);
	}

    @ApiOperation(value = "文件下载", notes="文件下载", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fileId", value = "文件ID", required = true, dataType="Long",paramType = "query")
    })
    @RequestMapping(value = "/download", method = RequestMethod.GET)
    @ResponseBody
    public Result downloadFile(@RequestParam(value = "fileId", required = true) Long fileId,
                               HttpServletResponse response) {
//    public Result downloadFile(HttpServletResponse response){
        String username = getUserName(); //"admin";
//        Long fileId = 71L;
        String hdfsFilePath = null;
        ResourceFilePO fileInfo = null;
        try{
            hdfsFilePath = resourceFileService.getFileHdfsPath(fileId);
            fileInfo = resourceFileService.getFileInfo(fileId);
        }catch (Exception e){
            e.printStackTrace();
            return Result.error(e.getMessage());
        }

        //统计文件下载情况，只有用户部门和当前用户为不同部门时候，就认为是订阅下载
        Long userId = Long.valueOf(userUtils.getCurrentUserId());
        Organization organization = userService.getUserOrganizationByUserId(userId);
        Long currentDeptId = organization.getId();
        String deptName = organization.getDeptName();
        ResourceConfigPO rcPO = resourceConfigDAO.getConfigById(fileInfo.getResourceId());
        User creatUser = userService.findByUserName(rcPO.getCreator());
        if(creatUser.getDeptId() != currentDeptId){
            statisticsDeptService.saveDeptShareInfo(currentDeptId, deptName, rcPO.getId(), 1L);
        }

        InputStream inputStream = null;
        try {
            inputStream = hdfsUnDaoHessian.downloadFileByStream(hdfsFilePath);
            if (null == inputStream) {
                return Result.error("文件下载失败，请确定文件是否不存在");
            }
        }catch (Exception e){
            e.printStackTrace();
            return Result.error("文件下载出现异常："+e.getMessage());
        }


        Long totalBytes = 0L;
        BufferedOutputStream bos = null;
        try {
            response.setContentType("application/octet-stream");
            //解决中文乱码问题，不能用utf-8
            response.addHeader("Content-Disposition", "attachment; filename=" + new String(fileInfo.getPubFileName().getBytes(), "iso-8859-1"));
            response.addHeader("Content-Length", String.valueOf(fileInfo.getFileSize()));
            bos = new BufferedOutputStream(response.getOutputStream());
            byte[] buff = new byte[4096];
            int bytesRead;

            while (-1 != (bytesRead = inputStream.read(buff, 0, buff.length))) {
                bos.write(buff, 0, bytesRead);
                totalBytes += bytesRead;
            }
            return Result.ok(true);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        } finally {
//            try {
//                bos.flush();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            IOUtils.closeStream(bos);
            IOUtils.closeStream(inputStream);
        }
    }


    /*根据数据上报ID删除记录*/
    @ApiOperation(value = "根据数据上报ID删除记录", notes="根据数据上报ID删除记录", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "任务上报ID", required = true,dataType="Long",paramType = "query")
    })
	@RequestMapping(value="/deleteDLRecordById", method= RequestMethod.GET)
	@ResponseBody
	public Result deleteDLRecordById (@RequestParam(value = "id",required = true) Long id) {

		try {
			iDataUploadService.deleteDataUploadRecordById(id);

			return Result.ok("删除上报记录成功");
		} catch (CommonServiceException e) {
			return Result.error(e.getMessage());
		}
	}


	/*更新数据上报细节信息*/
    @ApiOperation(value = "更新数据上报细节信息", notes="更新数据上报细节信息", httpMethod = "POST")
	@RequestMapping(value="/updateBatchDataUploadDetails", method= RequestMethod.POST)
	@ResponseBody
	public Result updateBatchDataUploadDetails (@RequestBody DataUploadTotalVO dataUploadTotalVO) {

		//根据登录用户来过滤数据上传记录
		String userName = getUserName();
        Long rentId = userUtils.getCurrentUserRentId();
		try {
			iDataUploadService.updateUploadDataForFILE(rentId, dataUploadTotalVO, userName);
			return Result.ok("更新上报记录成功");
		} catch (Exception e) {
			return Result.error(e.getMessage());
		}
	}

	/*根据数据上报ID查询对应的ETL任务状态*/
    @ApiOperation(value = "根据数据上报ID查询对应的ETL任务状态", notes="根据数据上报ID查询对应的ETL任务状态", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "任务上报ID", required = true,dataType="Long",paramType = "query")
    })
	@RequestMapping(value="/getETLTaskDetailInfoById", method= RequestMethod.GET)
    @ResponseBody
    public Result getETLTaskDetailInfoById(@RequestParam("id") Long id) {

        //根据登录用户来过滤数据上传记录
        String userName = getUserName();
        try {
            ResultPager tasks = iDataUploadService.getETLTaskDetailInfoById(id, userName);
            return Result.ok(tasks);
        } catch (CommonServiceException e) {
            return Result.error(e.getMessage());
        }
    }


    /**
     * 页面填写数据上报
     * @param data
     * @return
     */
    @ApiOperation(value = "页面填写数据上报", notes="通过web页面生成上报数据", httpMethod = "POST")
    @RequestMapping(value="/updateDateByBrowse", method= RequestMethod.POST)
    @ResponseBody
    public Result updateDateByBrowse(@RequestBody BrowseDataVO data) {
        int dateSize = data.getBrowseData().size();
        int rowSize = data.getBrowseData().get(0).length;
        LOG.info("总共条数 "+ dateSize + " ,每行数据大小 " + rowSize);
        Long rentId = userUtils.getCurrentUserRentId();
        String user = userUtils.getCurrentUserName();

        Long lineCount=null;
        try{
            lineCount=iDataUploadService.updateBrowseData(rentId, user, data);
        }catch (Exception e){
            e.printStackTrace();
            Result.error(e.getMessage());
        }
        return Result.ok(lineCount);
    }


    /**
     * 获取网页填报字段标题内容
     * @param resourceId 资源ID
     * @return
     */
    @ApiOperation(value = "获取网页填报字段标题内容", notes="获取网页填报字段标题内容", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "resourceId", value = "资源ID", required = true,dataType="Long",paramType = "query")
    })
    @RequestMapping(value="/getBrowseFormDataTitle", method= RequestMethod.GET)
    @ResponseBody
    public Result getBrowseFormDataTitle(@RequestParam("resourceId") Long resourceId) {

        List<String> titleList = new ArrayList<>();
        try{
            titleList = iDataUploadService.getBrowseFormDataTitle(resourceId);
        }catch (Exception e){
            e.printStackTrace();
            Result.error(e.getMessage());
        }
        return Result.ok(titleList);
    }



    /**
     * 用户直接导入表格到网页编辑
     * @param titleFlag		数据格式类型 0,1,2,3,4,5,6,7,8
     * @param file				上传的文件
     */
    @ApiOperation(value = "用户直接导入表格到网页编辑", notes="用户直接导入表格到网页编辑", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "titleFlag", value = "表格中是否包含标题", required = true,dataType="Long"),
            @ApiImplicitParam(name = "file", value = "文件内容", required = true,dataType="CommonsMultipartFile")
    })
    @RequestMapping(value="/importFormDataIntoBrowse", method= RequestMethod.POST)
    @ResponseBody
    public Result<BrowseDataVO> importFormDataIntoBrowse(@RequestParam(value = "titleFlag",defaultValue = "0") Long titleFlag,
                                                @RequestParam(value = "file") CommonsMultipartFile file) {
        //根据登录用户来过滤数据上传记录
        String user = getUserName();
        Long rentId = userUtils.getCurrentUserRentId();
        BrowseDataVO browseDataVO = null;
        try{
            browseDataVO = iDataUploadService.importFormDataIntoBrowse(titleFlag, file);
        }catch (Exception e){
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
        return Result.ok(browseDataVO);

    }



}
