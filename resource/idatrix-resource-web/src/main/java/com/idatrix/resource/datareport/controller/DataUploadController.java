package com.idatrix.resource.datareport.controller;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.idatrix.resource.common.Exception.CommonServiceException;
import com.idatrix.resource.common.controller.BaseController;
import com.idatrix.resource.common.utils.*;
import com.idatrix.resource.datareport.po.ResourceFilePO;
import com.idatrix.resource.datareport.service.IDataUploadService;
import com.idatrix.resource.datareport.service.IResourceFileService;
import com.idatrix.resource.datareport.vo.DataUploadTotalVO;
import com.idatrix.resource.datareport.vo.PubFileNameVO;
import com.ys.idatrix.db.proxy.api.hdfs.HdfsUnrestrictedDao;
import org.apache.commons.lang3.StringUtils;
import org.apache.zookeeper.common.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据上报流程
 */

@Controller
@RequestMapping("/dataUpload")
public class DataUploadController extends BaseController {
//
//    @Autowired
//	public DataUploadController(IDataUploadService iDataUploadService, IResourceFileService resourceFileService) {
//		this.iDataUploadService = iDataUploadService;
//		this.resourceFileService = resourceFileService;
//	}
//    private final IDataUploadService iDataUploadService;
//
//    private final IResourceFileService resourceFileService;

	@Autowired
    private IDataUploadService iDataUploadService;

    @Autowired
    private IResourceFileService resourceFileService;

    @Autowired
    private HdfsUnrestrictedDao hdfsUnDaoHessian;


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
	@RequestMapping(value="/saveOrUpdateUploadDataForDB", method= RequestMethod.POST)
	@ResponseBody
	public Result saveOrUpdateUploadDataForDB(@RequestParam("resourceId") Long resourceId,
											  @RequestParam("dataBatch") String dataBatch,
											  @RequestParam("formatType") Integer formatType,
											  @RequestParam(value = "file") CommonsMultipartFile file) {

		//根据登录用户来过滤数据上传记录
		String user = getUserName();
		try {
			Long result = iDataUploadService.saveOrUpdateUploadDataForDB(resourceId, dataBatch, formatType, file, user);
			return Result.ok(result);
		} catch (RuntimeException e) {
			LOG.error(DateTools.getDateTime() + ", 进行数据上报操作时出现异常错误");
			LOG.error(e.getMessage());
			return Result.error(CommonConstants.EC_INCORRECT_VALUE, "出现异常错误, 保存上传文件失败");
		} catch (CommonServiceException e) {
			LOG.error(DateTools.getDateTime() + ", 进行数据上报操作时出现异常错误");
			LOG.error(e.getMessage());

			return Result.error(e.getErrorCode(), e.getMessage());
		}
	}

	/*新增数据上报*/
	/**
	 * @param formatType		数据格式类型 0,1,2,3,4,5,6,7,8
	 * @param files				上传的文件
	 */
	@RequestMapping(value="/saveOrUpdateUploadDataForFILE", method= RequestMethod.POST)
	@ResponseBody
	public Result saveOrUpdateUploadDataForFILE(@RequestParam("resourceId") Long resourceId,
												@RequestParam("formatType") Integer formatType,
											  @RequestParam(value = "files") CommonsMultipartFile[] files) {
		try {
			//根据登录用户来过滤数据上传记录
			String user = getUserName();

			List<Map<String, Object>> result
					= iDataUploadService.saveOrUpdateUploadDataForFILE(resourceId, formatType, files, user);
			return Result.ok(result);
		} catch (RuntimeException e) {
			LOG.error(DateTools.getDateTime() + ", 进行数据上报操作时出现异常错误");
			LOG.error(e.getMessage());
			return Result.error(CommonConstants.EC_INCORRECT_VALUE, e.getMessage());
		} catch (CommonServiceException e) {
			LOG.error(DateTools.getDateTime() + ", 进行数据上报操作时出现异常错误");
			LOG.error(e.getMessage());

			return Result.error(e.getErrorCode(), e.getMessage());
		}
	}

	/*文件类数据上报时, 判断该资源ID下的同发布名文件是否已存在*/
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

            return result.error(CommonConstants.EC_EXISTED_VALUE, existedFiles);
        }

	}

	@RequestMapping(value="/downloadTemplate") //,produces = {"application/vnd.ms-excel;charset=UTF-8"})
	public Result download(@RequestParam("resourceId") Long resourceId, HttpServletResponse response){
		if (CommonUtils.isEmptyLongValue(resourceId)) {
			return Result.error(CommonConstants.EC_NULL_VALUE, "资源ID为空, 无法生成相应模板");
		}

		try {
			iDataUploadService.downLoadExcelTemplate(resourceId, response);

			return Result.ok("模板下载成功");
		} catch (CommonServiceException e) {
			return Result.error(e.getErrorCode(), e.getMessage());
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
			return Result.error(6001000, e.getMessage()); //调试Ajax屏蔽掉
		}
	}

	/*上报文件查看*/
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
		con.put("id", Long.toString(resourceId));
		if(StringUtils.isNoneEmpty(name)){
			con.put("fileName", name);
		}
		//后面只判断是否非空，不做取值判断
		if(StringUtils.equals(order, "up")){
			con.put("ascOrder", order);
		}else{
			con.put("descOrder", "down");
		}

		try{
			tasks = resourceFileService.queryResourceFile(con, pageNum, pageSize);
		}catch(Exception e){
			e.printStackTrace();
			return Result.error(6001000, e.getMessage());
		}
		return Result.ok(tasks);
	}

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
            return Result.error(600100, e.getMessage());
        }

        InputStream inputStream = null;
        try {
            inputStream = hdfsUnDaoHessian.downloadFileByStream(hdfsFilePath);
            if (null == inputStream) {
                return Result.error(600100, "文件下载失败，请确定文件是否不存在");
            }
        }catch (Exception e){
            e.printStackTrace();
            return Result.error(600100, "文件下载出现异常："+e.getMessage());
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
            return Result.error(600100, e.getMessage());
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
	@RequestMapping(value="/deleteDLRecordById", method= RequestMethod.GET)
	@ResponseBody
	public Result deleteDLRecordById (@RequestParam("id") Long id) {

		try {
			iDataUploadService.deleteDataUploadRecordById(id);

			return Result.ok("删除上报记录成功");
		} catch (CommonServiceException e) {
			return Result.error(e.getErrorCode(), e.getMessage());
		}
	}


	/*更新数据上报细节信息*/
	@RequestMapping(value="/updateBatchDataUploadDetails", method= RequestMethod.POST)
	@ResponseBody
	public Result updateBatchDataUploadDetails (@RequestBody DataUploadTotalVO dataUploadTotalVO) {

		//根据登录用户来过滤数据上传记录
		String userName = getUserName();

		try {
			iDataUploadService.updateUploadDataForFILE(dataUploadTotalVO, userName);
			return Result.ok("更新上报记录成功");
		} catch (Exception e) {
			return Result.error(CommonConstants.EC_INCORRECT_VALUE, e.getMessage());
		}
	}

	/*根据数据上报ID查询对应的ETL任务状态*/
	@RequestMapping(value="/getETLTaskDetailInfoById", method= RequestMethod.GET)
	@ResponseBody
	public Result getETLTaskDetailInfoById(@RequestParam("id") Long id) {

		//根据登录用户来过滤数据上传记录
		String userName = getUserName();
		try {
			ResultPager tasks = iDataUploadService.getETLTaskDetailInfoById(id, userName);
			return Result.ok(tasks);
		} catch (CommonServiceException e) {
			return Result.error(CommonConstants.EC_INCORRECT_VALUE, e.getMessage());
		}
	}
}
