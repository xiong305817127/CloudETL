/**
 * 云化数据集成系统
 * iDatrix CloudETL
 */
package com.ys.idatrix.quality.web.controller;

import java.io.File;
import java.net.URI;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.apache.commons.vfs2.FileObject;
import org.pentaho.di.core.logging.LoggingObjectInterface;
import org.pentaho.di.core.logging.LoggingObjectType;
import org.pentaho.di.core.logging.SimpleLoggingObject;
import org.pentaho.di.core.util.OsgiBundleUtils;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.core.variables.Variables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.collect.Lists;
import com.ys.idatrix.quality.deploy.MetaCubeCategory;
import com.ys.idatrix.quality.deploy.MetaStoreCategory;
import com.ys.idatrix.quality.deploy.TransEngineCategory;
import com.ys.idatrix.quality.dto.DataStoreDto;
import com.ys.idatrix.quality.dto.DeployModeDto;
import com.ys.idatrix.quality.dto.JobDto;
import com.ys.idatrix.quality.dto.TransDto;
import com.ys.idatrix.quality.dto.cluster.ClusterDetailsDto;
import com.ys.idatrix.quality.dto.cluster.ClusterDto;
import com.ys.idatrix.quality.dto.common.CheckResultDto;
import com.ys.idatrix.quality.dto.common.FileListDto;
import com.ys.idatrix.quality.dto.common.FileListRequestDto;
import com.ys.idatrix.quality.dto.common.PaginationDto;
import com.ys.idatrix.quality.dto.common.RequestNameDto;
import com.ys.idatrix.quality.dto.common.ReturnCodeDto;
import com.ys.idatrix.quality.dto.common.TestResultDto;
import com.ys.idatrix.quality.dto.engine.EngineBriefDto;
import com.ys.idatrix.quality.dto.engine.EngineDetailsDto;
import com.ys.idatrix.quality.dto.engine.SparkBriefDto;
import com.ys.idatrix.quality.dto.engine.SparkDetailsDto;
import com.ys.idatrix.quality.dto.hadoop.HadoopBriefDto;
import com.ys.idatrix.quality.dto.hadoop.HadoopDetailsDto;
import com.ys.idatrix.quality.dto.server.ServerBriefDto;
import com.ys.idatrix.quality.dto.server.ServerDetailsDto;
import com.ys.idatrix.quality.ext.CloudSession;
import com.ys.idatrix.quality.ext.utils.FilePathUtil.FileType;
import com.ys.idatrix.quality.repository.database.dto.SystemSettingsDto;
import com.ys.idatrix.quality.service.cluster.CloudClusterService;
import com.ys.idatrix.quality.service.engine.CloudDefaultEngineService;
import com.ys.idatrix.quality.service.engine.CloudSparkEngineService;
import com.ys.idatrix.quality.service.file.FileService;
import com.ys.idatrix.quality.service.hadoop.CloudHadoopService;
import com.ys.idatrix.quality.service.job.CloudJobService;
import com.ys.idatrix.quality.service.server.CloudServerService;
import com.ys.idatrix.quality.service.trans.CloudTransService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * 主界面流程控制器
 * Cloud ETL main procedure controller
 * @author JW
 * @since 05-12-2017
 * 
 */
@Controller
@RequestMapping(value="/cloud")
@Api(value = "/cloud" , description="kettle资源操作api")
public class CloudController extends BaseAction {

	@Autowired
	private FileService fileService;
	@Autowired
	private CloudTransService cloudTransService;
	@Autowired
	private CloudJobService cloudJobService;
	@Autowired
	private CloudClusterService cloudClusterService;
	@Autowired
	private CloudHadoopService cloudHadoopService;
	@Autowired
	private CloudServerService cloudServerService;
	@Autowired
	private CloudSparkEngineService cloudSparkEngineService;
	@Autowired
	private CloudDefaultEngineService cloudDefaultEngineService;

	@Autowired
	private MetaStoreCategory metaStoreCategory;
	@Autowired
	private MetaCubeCategory metaCubeCategory;
	@Autowired
	private TransEngineCategory transEngineCategory;

	//
	// Cloud tasks (trans & jobs) in main page
	//

	
	/**
	 * 请求方法 - 保存缺省执行引擎配置信息
	 * @param engineDetails
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.POST, value="/saveSystemSetting")
	@ApiOperation(value = "保存系统设置,租户机制,配置管理员使用 key : 'SuperPrivilegeRoleId' , value : 选择的角色Id")
	@ApiResponses({ @ApiResponse(code = 200, response = ReturnCodeDto.class, message = "成功" ) })
	public @ResponseBody Object saveSystemSetting(@RequestBody SystemSettingsDto settings ) throws Exception {
		//只有管理员才能操作
		checkManagerPrivilege();
		return fileService.saveSystemSetting(settings);
	}
	
	
	/**
	 * 请求方法 - 保存缺省执行引擎配置信息
	 * @param engineDetails
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.GET, value="/getSystemSetting")
	@ApiOperation(value = "获取系统设置,配置管理员使用 key : 'SuperPrivilegeRoleId' ")
	@ApiResponses({ @ApiResponse(code = 200, response = SystemSettingsDto[].class, message = "成功" ) })
	public @ResponseBody Object getSystemSetting( ) throws Exception {
		checkManagerPrivilege();
		return fileService.getSystemSetting();
	}
	
	/**
	 * 请求方法 - 获取转换任务列表
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.GET, value="/getTransList")
	@ApiOperation(value = "获取转换任务列表", notes = "")
	@ApiImplicitParams( value = {
			@ApiImplicitParam(paramType = "query", name = "group", dataType = "String", required = false, value = "查询的转换租号,默认 default", defaultValue = "default"),
	        @ApiImplicitParam(paramType = "query", name = "page", dataType = "int", required = false, value = "分页参数,页号,-1代表不分页,获取全部,默认-1", defaultValue = "-1"),
	        @ApiImplicitParam(paramType = "query", name = "pageSize", dataType = "int", required = false, value = "分页参数,每页大小,默认10", defaultValue = "10"),
	        @ApiImplicitParam(paramType = "query", name = "search", dataType = "String", required = false, value = "搜索参数,根据名字过滤"),
	        @ApiImplicitParam(paramType = "query", name = "searchType", dataType = "String", required = false, value = "搜索参数,根据运行状态过滤"),
	        @ApiImplicitParam(paramType = "query", name = "isOnlyName", dataType = "Boolean", required = false, value = "是否只需要转换名,true则只获取转换的名字,默认为false", defaultValue = "false"),
	})
	@ApiResponses({ @ApiResponse(code = 200, response = TransDto[].class, message = "成功" ) })
	public @ResponseBody Object getTransList(@RequestParam(required=false)String owner,
			@RequestParam(required=false)String group,
			@RequestParam(required=false,defaultValue="-1")Integer page,
			@RequestParam(required=false,defaultValue="10")Integer pageSize,
			@RequestParam(required=false)String search,
			@RequestParam(required=false)String searchType,
			@RequestParam(required=false,defaultValue="false")boolean isOnlyName,
			@RequestParam(required=false,defaultValue="false")boolean isMap) throws Exception {
		
		saveResourceOwner(owner);
		
		if(isMap) {
			if(isOnlyName){
				return cloudTransService.getCloudTransNameList(owner,group);
			}
			return  cloudTransService.getCloudTransList(owner,group,true ,page,pageSize,Utils.isEmpty(searchType)?search:(searchType+"::"+search));
		}else {
			if(isOnlyName){
				Map<String, List<String>> names = cloudTransService.getCloudTransNameList(owner,group);
				return mergeMap(names , Lists.newArrayList(), (a,b) -> { a.addAll(b); return a ;} ) ;
			}
			Map<String, PaginationDto<TransDto>> temp = cloudTransService.getCloudTransList(owner,group,false ,page,pageSize,Utils.isEmpty(searchType)?search:(searchType+"::"+search));
			return mergeMap(temp , new PaginationDto<TransDto>(page,pageSize), (a,b) -> { a.mergePagination(b); return a ;} )  ;
		}
		
		
	}

	/**
	 * 请求方法 - 获取调度任务列表
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.GET, value="/getJobList")
	@ApiOperation(value = "获取调度任务列表", notes = "")
	@ApiImplicitParams( value = {
			@ApiImplicitParam(paramType = "query", name = "group", dataType = "String", required = false, value = "查询的转换租号,默认 default", defaultValue = "default"),
	        @ApiImplicitParam(paramType = "query", name = "page", dataType = "int", required = false, value = "分页参数,页号,-1代表不分页,获取全部,默认-1", defaultValue = "-1"),
	        @ApiImplicitParam(paramType = "query", name = "pageSize", dataType = "int", required = false, value = "分页参数,每页大小,默认10", defaultValue = "10"),
	        @ApiImplicitParam(paramType = "query", name = "search", dataType = "String", required = false, value = "搜索参数,根据名字过滤"),
	        @ApiImplicitParam(paramType = "query", name = "searchType", dataType = "String", required = false, value = "搜索参数,根据运行状态过滤"),
	        @ApiImplicitParam(paramType = "query", name = "isOnlyName", dataType = "Boolean", required = false, value = "是否只需要转换名,true则只获取转换的名字,默认为false", defaultValue = "false"),
	})
	@ApiResponses({ @ApiResponse(code = 200, response = JobDto[].class, message = "成功" ) })
	public @ResponseBody Object getJobList(@RequestParam(required=false)String owner,
			@RequestParam(required=false)String group,
			@RequestParam(required=false,defaultValue="-1")Integer page,
			@RequestParam(required=false,defaultValue="10")Integer pageSize,
			@RequestParam(required=false)String search,
			@RequestParam(required=false)String searchType,
			@RequestParam(required=false,defaultValue="false")boolean isOnlyName,
			@RequestParam(required=false,defaultValue="false")boolean isMap) throws Exception {
		
		saveResourceOwner(owner);
		
		if(isMap) {
			if(isOnlyName){
				return cloudJobService.getCloudJobNameList(owner,group);
			}
			return  cloudJobService.getCloudJobList(owner,group,true,page,pageSize,Utils.isEmpty(searchType)?search:(searchType+"::"+search));
		}else {
			if(isOnlyName){
				Map<String, List<String>> names = cloudJobService.getCloudJobNameList(owner,group);
				return mergeMap(names ,Lists.newArrayList(), (a,b) -> { a.addAll(b); return a ;} ) ;
			}
			Map<String,PaginationDto<JobDto>> temp = cloudJobService.getCloudJobList(owner,group,false ,page,pageSize,Utils.isEmpty(searchType)?search:(searchType+"::"+search));
			return  mergeMap(temp ,new PaginationDto<JobDto>(page,pageSize), (a,b) -> { a.mergePagination(b); return a ;} )  ;
		}
		

	}

	/**
	 * 请求方法 - 获取部署模式
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.GET, value="/getDeployMode")
	@ApiOperation(value = "获取部署模式")
	@ApiResponses({ @ApiResponse(code = 200, response = DeployModeDto.class, message = "成功" ) })
	public @ResponseBody Object getDeployMode() throws Exception {
		DeployModeDto deployMode = new DeployModeDto();
		deployMode.setMetaCube(metaCubeCategory.getCategory());
		deployMode.setMetaStore(metaStoreCategory.getCategory());
		deployMode.setTransEngine(transEngineCategory.getCategory());
		return deployMode;
	}

	/**
	 * 请求方法 - 获取服务器数据存储目录
	 * @param dataStore
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.POST, value="/getDataStore")
	@ApiOperation(value = "获取服务器数据存储目录")
	@ApiResponses({ @ApiResponse(code = 200, response = DataStoreDto.class, message = "成功" ) })
	public @ResponseBody Object getDataStore(@RequestBody DataStoreDto dataStore) throws Exception {
		dataStore.setPath(fileService.getDataStorePath(dataStore.getOwner() ,dataStore.getType()));
		return dataStore;
	}

	//
	// Slave server maintenance
	//

	/**
	 * 请求方法 - 获取采集服务器列表
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.GET, value="/getServerList")
	@ApiOperation(value = "获取采集服务器列表")
	@ApiImplicitParams( value = {
	        @ApiImplicitParam(paramType = "query", name = "page", dataType = "int", required = false, value = "分页参数,页号,-1代表不分页,获取全部,默认-1", defaultValue = "-1"),
	        @ApiImplicitParam(paramType = "query", name = "pageSize", dataType = "int", required = false, value = "分页参数,每页大小,默认10", defaultValue = "10"),
	        @ApiImplicitParam(paramType = "query", name = "search", dataType = "String", required = false, value = "搜索参数,根据名字过滤")
	})
	@ApiResponses({ @ApiResponse(code = 200, response = ServerBriefDto[].class, message = "成功" ) })
	public @ResponseBody Object getServerList(@RequestParam(required=false)String owner,
			@RequestParam(required=false,defaultValue="-1")Integer page,
			@RequestParam(required=false,defaultValue="10")Integer pageSize,
			@RequestParam(required=false)String search,
			@RequestParam(required=false,defaultValue="false")boolean isMap) throws Exception {
		
		saveResourceOwner(owner);
		
		if(isMap) {
			if(Integer.valueOf(page)<0){
				return   cloudServerService.getCloudServerList(owner);
			}
			return cloudServerService.getCloudServerList(owner,true,page,pageSize,search);
		}else {
			
			if(Integer.valueOf(page)<0){
				Map<String, List<ServerBriefDto>> temp = cloudServerService.getCloudServerList(owner);
				return mergeMap(temp , Lists.newArrayList(), (a,b) -> { a.addAll(b); return a ;} ) ;
			}
			Map<String, PaginationDto<ServerBriefDto>> temp = cloudServerService.getCloudServerList(owner,false,page,pageSize,search);
			return mergeMap(temp , new PaginationDto<ServerBriefDto>(page,pageSize), (a,b) -> { a.mergePagination(b); return a ;} )  ;
		}
	}

	/**
	 * 请求方法 - 测试服务器状态
	 * @param serverName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.POST, value="/testServer")
	@ApiOperation(value = "测试服务器状态")
	@ApiResponses({ @ApiResponse(code = 200, response = TestResultDto.class, message = "成功" ) })
	public @ResponseBody Object testServer(@RequestBody  RequestNameDto requestName) throws Exception {
		return cloudServerService.doServerTest(requestName.getOwner(),requestName.getName());
	}

	/**
	 * 请求方法 - 检查服务器名是否存在
	 * @param serverName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.POST, value="/checkServerName")
	@ApiOperation(value = "检查服务器名是否存在")
	@ApiResponses({ @ApiResponse(code = 200, response = CheckResultDto.class, message = "成功" ) })
	public @ResponseBody Object checkServerName(@RequestBody  RequestNameDto requestName) throws Exception {
		return cloudServerService.checkServerName(requestName.getOwner(),requestName.getName());
	}

	/**
	 * 请求方法 - 编辑服务器配置信息
	 * @param serverName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.POST, value="/editServer")
	@ApiOperation(value = "获取可编辑服务器配置信息")
	@ApiResponses({ @ApiResponse(code = 200, response = ServerDetailsDto.class, message = "成功" ) })
	public @ResponseBody Object editServer(@RequestBody  RequestNameDto requestName) throws Exception {
		return cloudServerService.editServer(requestName.getOwner(),requestName.getName());
	}

	/**
	 * 请求方法 - 保存服务器配置信息
	 * @param clusterDetails
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.POST, value="/saveServer")
	@ApiOperation(value = "保存服务器配置信息")
	@ApiResponses({ @ApiResponse(code = 200, response = ReturnCodeDto.class, message = "成功" ) })
	public @ResponseBody Object saveServer(@RequestBody ServerDetailsDto clusterDetails) throws Exception {
		checkPrivilege();
		return cloudServerService.saveServer(clusterDetails);
	}

	/**
	 * 请求方法 - 删除服务器配置
	 * @param serverName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.POST, value="/deleteServer")
	@ApiOperation(value = "删除服务器配置")
	@ApiResponses({ @ApiResponse(code = 200, response = ReturnCodeDto.class, message = "成功" ) })
	public @ResponseBody Object deleteServer(@RequestBody  RequestNameDto requestName) throws Exception {
		checkPrivilege();
		return cloudServerService.deleteServer(requestName.getOwner(),requestName.getName());
	}

	//
	// Cluster schema maintenance
	//

	/**
	 * 请求方法 - 获取服务器集群列表
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.GET, value="/getClusterList")
	@ApiOperation(value = "获取服务器集群列表")
	@ApiImplicitParams( value = {
	        @ApiImplicitParam(paramType = "query", name = "page", dataType = "int", required = false, value = "分页参数,页号,-1代表不分页,获取全部,默认-1", defaultValue = "-1"),
	        @ApiImplicitParam(paramType = "query", name = "pageSize", dataType = "int", required = false, value = "分页参数,每页大小,默认10", defaultValue = "10"),
	        @ApiImplicitParam(paramType = "query", name = "search", dataType = "String", required = false, value = "搜索参数,根据名字过滤")
	})
	@ApiResponses({ @ApiResponse(code = 200, response = ClusterDto.class, message = "成功" ) })
	public @ResponseBody Object getClusterList(@RequestParam(required=false)String owner,
			@RequestParam(required=false,defaultValue="-1")Integer page,
			@RequestParam(required=false,defaultValue="10")Integer pageSize,
			@RequestParam(required=false)String search,
			@RequestParam(required=false,defaultValue="false")boolean isMap) throws Exception {

		saveResourceOwner(owner);
		
		if(isMap) {
			if(Integer.valueOf(page)<0){
				return  cloudClusterService.getCloudClusterList(owner);
			}
			return cloudClusterService.getCloudClusterList(owner,true,page,pageSize,search);
		}else {
			if(Integer.valueOf(page)<0){
				 Map<String, List<ClusterDto>> temp = cloudClusterService.getCloudClusterList(owner);
				return mergeMap(temp , Lists.newArrayList(), (a,b) -> { a.addAll(b); return a ;} ) ;
			}
			Map<String, PaginationDto<ClusterDto>> temp = cloudClusterService.getCloudClusterList(owner,false,page,pageSize,search);
			return mergeMap(temp , new PaginationDto<ClusterDto>(page,pageSize), (a,b) -> { a.mergePagination(b); return a ;} )  ;
		}
	}

	/**
	 * 请求方法 - 检查集群名是否存在
	 * @param clusterName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.POST, value="/checkClusterName")
	@ApiOperation(value = "检查集群名是否存在")
	@ApiResponses({ @ApiResponse(code = 200, response = CheckResultDto.class, message = "成功" ) })
	public @ResponseBody Object checkClusterName(@RequestBody  RequestNameDto requestName) throws Exception {
		return cloudClusterService.checkClusterName(requestName.getOwner(),requestName.getName());
	}

	/**
	 * 请求方法 - 编辑集群配置信息
	 * @param clusterName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.POST, value="/editCluster")
	@ApiOperation(value = "获取可编辑集群配置信息")
	@ApiResponses({ @ApiResponse(code = 200, response = ClusterDetailsDto.class, message = "成功" ) })
	public @ResponseBody Object editCluster(@RequestBody  RequestNameDto requestName) throws Exception {
		return cloudClusterService.editCluster(requestName.getOwner(),requestName.getName());
	}

	/**
	 * 请求方法 - 保存集群配置信息
	 * @param clusterDetails
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.POST, value="/saveCluster")
	@ApiOperation(value = "保存集群配置信息")
	@ApiResponses({ @ApiResponse(code = 200, response = ReturnCodeDto.class, message = "成功" ) })
	public @ResponseBody Object saveCluster(@RequestBody ClusterDetailsDto clusterDetails) throws Exception {
		checkPrivilege();
		return cloudClusterService.saveCluster(clusterDetails);
	}

	/**
	 * 请求方法 - 删除集群配置
	 * @param clusterName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.POST, value="/deleteCluster")
	@ApiOperation(value = "删除集群配置")
	@ApiResponses({ @ApiResponse(code = 200, response = ReturnCodeDto.class, message = "成功" ) })
	public @ResponseBody Object deleteCluster(@RequestBody RequestNameDto requestName) throws Exception {
		checkPrivilege();
		return cloudClusterService.deleteCluster(requestName.getOwner(),requestName.getName());
	}

	//
	// Hadoop cluster maintenance
	//

	/**
	 * 请求方法 - 获取Hadoop集群列表
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.GET, value="/getHadoopList")
	@ApiOperation(value = "获取Hadoop集群列表")
	@ApiImplicitParams( value = {
	        @ApiImplicitParam(paramType = "query", name = "page", dataType = "int", required = false, value = "分页参数,页号,-1代表不分页,获取全部,默认-1", defaultValue = "-1"),
	        @ApiImplicitParam(paramType = "query", name = "pageSize", dataType = "int", required = false, value = "分页参数,每页大小,默认10", defaultValue = "10"),
	        @ApiImplicitParam(paramType = "query", name = "search", dataType = "String", required = false, value = "搜索参数,根据名字过滤")
	})
	@ApiResponses({ @ApiResponse(code = 200, response = HadoopBriefDto.class, message = "成功" ) })
	public @ResponseBody Object getHadoopList(@RequestParam(required=false)String owner,
			@RequestParam(required=false,defaultValue="-1")Integer page,
			@RequestParam(required=false,defaultValue="10")Integer pageSize,
			@RequestParam(required=false)String search,
			@RequestParam(required=false,defaultValue="false")boolean isMap) throws Exception {

		saveResourceOwner(owner);
		
		if(isMap) {
			if(Integer.valueOf(page)<0){
				return  cloudHadoopService.getCloudHadoopList(owner);
			}
			return cloudHadoopService.getCloudHadoopList(owner,true,page,pageSize,search);
		}else {
			if(Integer.valueOf(page)<0){
				Map<String, List<HadoopBriefDto>> temp = cloudHadoopService.getCloudHadoopList(owner);
				return mergeMap(temp , Lists.newArrayList(), (a,b) -> { a.addAll(b); return a ;} ) ;
			}
			Map<String, PaginationDto<HadoopBriefDto>> temp = cloudHadoopService.getCloudHadoopList(owner,false,page,pageSize,search);
			return mergeMap(temp , new PaginationDto<HadoopBriefDto>(page,pageSize), (a,b) -> { a.mergePagination(b); return a ;} )  ;
		}
	}
	
	@RequestMapping(method=RequestMethod.GET, value="/getHdfsRoots")
	@ApiOperation(value = "获取Hdfs根目录")
	@ApiResponses({ @ApiResponse(code = 200, response = String[].class, message = "成功" ) })
	public @ResponseBody Object getHdfsRoots(@RequestParam(required=false)String owner,
			@RequestParam(required=false )Boolean isRead ) throws Exception {

		saveResourceOwner(owner);
		return fileService.getHdfsRootPath(owner, isRead) ;
	}

	/**
	 * 请求方法 - 检查Hadoop集群名是否存在
	 * @param hadoopName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.POST, value="/checkHadoopName")
	@ApiOperation(value = "检查Hadoop集群名是否存在")
	@ApiResponses({ @ApiResponse(code = 200, response = CheckResultDto.class, message = "成功" ) })
	public @ResponseBody Object checkHadoopName(@RequestBody  RequestNameDto requestName) throws Exception {
		return cloudHadoopService.checkHadoopName(requestName.getOwner(),requestName.getName());
	}

	/**
	 * 请求方法 - 编辑Hadoop集群配置信息
	 * @param hadoopName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.POST, value="/editHadoop")
	@ApiOperation(value = "获取可编辑Hadoop集群配置信息")
	@ApiResponses({ @ApiResponse(code = 200, response = HadoopDetailsDto.class, message = "成功" ) })
	public @ResponseBody Object editHadoop(@RequestBody  RequestNameDto requestName) throws Exception {
		return cloudHadoopService.editHadoop(requestName.getOwner(),requestName.getName());
	}

	/**
	 * 请求方法 - 保存Hadoop集群配置信息
	 * @param hadoopDetails
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.POST, value="/saveHadoop")
	@ApiOperation(value = "保存Hadoop集群配置信息")
	@ApiResponses({ @ApiResponse(code = 200, response = ReturnCodeDto.class, message = "成功" ) })
	public @ResponseBody Object saveHadoop(@RequestBody HadoopDetailsDto hadoopDetails) throws Exception {
		checkPrivilege();
		return cloudHadoopService.saveHadoop(hadoopDetails);
	}

	/**
	 * 请求方法 - 删除Hadoop集群配置
	 * @param hadoopName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.POST, value="/deleteHadoop")
	@ApiOperation(value = "删除Hadoop集群配置")
	@ApiResponses({ @ApiResponse(code = 200, response = ReturnCodeDto.class, message = "成功" ) })
	public @ResponseBody Object deleteHadoop(@RequestBody  RequestNameDto requestName) throws Exception {
		checkPrivilege();
		return cloudHadoopService.deleteHadoop(requestName.getOwner(),requestName.getName());
	}

	//
	// Spark engine maintenance
	//

	/**
	 * 请求方法 - 获取Spark引擎列表
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.GET, value="/getSparkEngineList")
	@ApiOperation(value = "获取Spark引擎列表")
	@ApiImplicitParams( value = {
	        @ApiImplicitParam(paramType = "query", name = "page", dataType = "int", required = false, value = "分页参数,页号,-1代表不分页,获取全部,默认-1", defaultValue = "-1"),
	        @ApiImplicitParam(paramType = "query", name = "pageSize", dataType = "int", required = false, value = "分页参数,每页大小,默认10", defaultValue = "10"),
	        @ApiImplicitParam(paramType = "query", name = "search", dataType = "String", required = false, value = "搜索参数,根据名字过滤")
	})
	@ApiResponses({ @ApiResponse(code = 200, response = SparkBriefDto[].class, message = "成功" ) })
	public @ResponseBody Object getSparkEngineList(@RequestParam(required=false)String owner,
			@RequestParam(required=false,defaultValue="-1")Integer page,
			@RequestParam(required=false,defaultValue="10")Integer pageSize,
			@RequestParam(required=false)String search,
			@RequestParam(required=false,defaultValue="false")boolean isMap) throws Exception {

		saveResourceOwner(owner);
		
		if(isMap) {
			if(Integer.valueOf(page)<0){
				return  cloudSparkEngineService.getCloudSparkEngineList(owner);
			}
			return cloudSparkEngineService.getCloudSparkEngineList(owner,true,page,pageSize,search);
		}else {
			if(Integer.valueOf(page)<0){
				Map<String, List<SparkBriefDto>> temp = cloudSparkEngineService.getCloudSparkEngineList(owner);
				return mergeMap(temp , Lists.newArrayList(), (a,b) -> { a.addAll(b); return a ;} ) ;
			}
			Map<String, PaginationDto<SparkBriefDto>> temp = cloudSparkEngineService.getCloudSparkEngineList(owner,false,page,pageSize,search);
			return mergeMap(temp , new PaginationDto<SparkBriefDto>(page,pageSize), (a,b) -> { a.mergePagination(b); return a ;} )  ;
		}
	}

	/**
	 * 请求方法 - 检查Spark引擎名是否存在
	 * @param sparkName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.POST, value="/checkSparkName")
	@ApiOperation(value = "检查Spark引擎名是否存在")
	@ApiResponses({ @ApiResponse(code = 200, response = CheckResultDto.class, message = "成功" ) })
	public @ResponseBody Object checkSparkName(@RequestBody RequestNameDto requestName) throws Exception {
		return cloudSparkEngineService.checkSparkEngineName(requestName.getOwner(),requestName.getName());
	}

	/**
	 * 请求方法 - 编辑Spark引擎配置信息
	 * @param sparkName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.POST, value="/editSpark")
	@ApiOperation(value = "获取可编辑Spark引擎配置信息")
	@ApiResponses({ @ApiResponse(code = 200, response = SparkDetailsDto.class, message = "成功" ) })
	public @ResponseBody Object editSpark(@RequestBody RequestNameDto requestName) throws Exception {
		return cloudSparkEngineService.editSparkEngine(requestName.getOwner(),requestName.getName());
	}

	/**
	 * 请求方法 - 保存Spark引擎配置信息
	 * @param sparkDetails
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.POST, value="/saveSpark")
	@ApiOperation(value = "保存Spark引擎配置信息")
	@ApiResponses({ @ApiResponse(code = 200, response = ReturnCodeDto.class, message = "成功" ) })
	public @ResponseBody Object saveSpark(@RequestBody SparkDetailsDto sparkDetails) throws Exception {
		checkPrivilege();
		return cloudSparkEngineService.saveSparkEngine(sparkDetails);
	}

	/**
	 * 请求方法 - 删除Spark引擎配置
	 * @param sparkName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.POST, value="/deleteSpark")
	@ApiOperation(value = "删除Spark引擎配置")
	@ApiResponses({ @ApiResponse(code = 200, response = ReturnCodeDto.class, message = "成功" ) })
	public @ResponseBody Object deleteSpark(@RequestBody RequestNameDto requestName) throws Exception {
		checkPrivilege();
		return cloudSparkEngineService.deleteSparkEngine(requestName.getOwner(),requestName.getName());
	}

	//
	// Default engine maintenance
	//

	/**
	 * 请求方法 - 获取缺省执行引擎列表
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.GET, value="/getDefaultEngineList")
	@ApiOperation(value = "获取kettle执行引擎列表")
	@ApiImplicitParams( value = {
	        @ApiImplicitParam(paramType = "query", name = "page", dataType = "int", required = false, value = "分页参数,页号,-1代表不分页,获取全部,默认-1", defaultValue = "-1"),
	        @ApiImplicitParam(paramType = "query", name = "pageSize", dataType = "int", required = false, value = "分页参数,每页大小,默认10", defaultValue = "10"),
	        @ApiImplicitParam(paramType = "query", name = "search", dataType = "String", required = false, value = "搜索参数,根据名字过滤")
	})
	@ApiResponses({ @ApiResponse(code = 200, response = EngineBriefDto[].class, message = "成功" ) })
	public @ResponseBody Object getDefaultEngineList(@RequestParam(required=false)String owner,
			@RequestParam(required=false,defaultValue="-1")Integer page,
			@RequestParam(required=false,defaultValue="10")Integer pageSize,
			@RequestParam(required=false)String search,
			@RequestParam(required=false,defaultValue="false")boolean isMap) throws Exception {

		saveResourceOwner(owner);
		
		if(isMap) {
			if(Integer.valueOf(page)<0){
				return  cloudDefaultEngineService.getCloudDefaultEngineList(owner);
			}
			return cloudDefaultEngineService.getCloudDefaultEngineList(owner,true,page,pageSize,search);
		}else {
			if(Integer.valueOf(page)<0){
				Map<String, List<EngineBriefDto>> temp = cloudDefaultEngineService.getCloudDefaultEngineList(owner);
				return mergeMap(temp , Lists.newArrayList(), (a,b) -> { a.addAll(b); return a ;} ) ;
			}
			Map<String, PaginationDto<EngineBriefDto>> temp = cloudDefaultEngineService.getCloudDefaultEngineList(owner,false,page,pageSize,search);
			return mergeMap(temp , new PaginationDto<EngineBriefDto>(page,pageSize), (a,b) -> { a.mergePagination(b); return a ;} )  ;
		}
	}

	/**
	 * 请求方法 - 检查缺省执行引擎名是否存在
	 * @param engineName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.POST, value="/checkEngineName")
	@ApiOperation(value = "检查kettle执行引擎名是否存在")
	@ApiResponses({ @ApiResponse(code = 200, response = CheckResultDto.class, message = "成功" ) })
	public @ResponseBody Object checkEngineName(@RequestBody RequestNameDto requestName) throws Exception {
		return cloudDefaultEngineService.checkDefaultEngineName(requestName.getOwner(),requestName.getName());
	}

	/**
	 * 请求方法 - 编辑缺省执行引擎配置信息
	 * @param engineName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.POST, value="/editEngine")
	@ApiOperation(value = "获取可编辑kettle执行引擎配置信息")
	@ApiResponses({ @ApiResponse(code = 200, response = EngineDetailsDto.class, message = "成功" ) })
	public @ResponseBody Object editEngine(@RequestBody RequestNameDto requestName) throws Exception {
		return cloudDefaultEngineService.editDefaultEngine(requestName.getOwner(),requestName.getName());
	}

	/**
	 * 请求方法 - 保存缺省执行引擎配置信息
	 * @param engineDetails
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.POST, value="/saveEngine")
	@ApiOperation(value = "保存kettle执行引擎配置信息")
	@ApiResponses({ @ApiResponse(code = 200, response = ReturnCodeDto.class, message = "成功" ) })
	public @ResponseBody Object saveEngine(@RequestBody EngineDetailsDto engineDetails) throws Exception {
		checkPrivilege();
		return cloudDefaultEngineService.saveDefaultEngine(engineDetails);
	}

	/**
	 * 请求方法 - 删除缺省执行引擎配置
	 * @param engineName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.POST, value="/deleteEngine")
	@ApiOperation(value = "删除缺省执行引擎配置")
	@ApiResponses({ @ApiResponse(code = 200, response = ReturnCodeDto.class, message = "成功" ) })
	public @ResponseBody Object deleteEngine(@RequestBody RequestNameDto requestName) throws Exception {
		checkPrivilege();
		return cloudDefaultEngineService.deleteDefaultEngine(requestName.getOwner(),requestName.getName());
	}
	
	/**
	 * 请求方法 - 获取文件列表（如服务器、HDFS文件列表）
	 * @param fileListRequestDto
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.GET, value="/getFileList")
	@ApiOperation(value = "获取文件浏览列表（如服务器、HDFS文件列表）")
	@ApiImplicitParams( value = {
	        @ApiImplicitParam(paramType = "query", name = "page", dataType = "int", required = false, value = "分页参数,页号,-1代表不分页,获取全部,默认-1", defaultValue = "-1"),
	        @ApiImplicitParam(paramType = "query", name = "pageSize", dataType = "int", required = false, value = "分页参数,每页大小,默认10", defaultValue = "10"),
	        @ApiImplicitParam(paramType = "query", name = "search", dataType = "String", required = false, value = "搜索参数,根据名字过滤"),
	        @ApiImplicitParam(paramType = "query", name = "path", dataType = "String", required = false, value = "文件类型下的相对子目录"),
	        @ApiImplicitParam(paramType = "query", name = "depth", dataType = "int", required = false, value = "文件递归深度,默认为-1,不限制"),
	        @ApiImplicitParam(paramType = "query", name = "type", dataType = "String", required = true, value = "文件类型:data,excel,access,txt,csv,json,output,upload, input,hdfs,sftp,ktr,kjb"),
	        @ApiImplicitParam(paramType = "query", name = "filterType", dataType = "String", required = false, value = "过滤文件类型,eg. access:返回access类型(后缀为mdb,accdb)的文件")
	})
	@ApiResponses({ @ApiResponse(code = 200, response = FileListDto[].class, message = "成功" ) })
	public @ResponseBody Object getFileList( FileListRequestDto fileListRequestDto,
			@RequestParam(required=false)String owner,
			@RequestParam(required=false,defaultValue="-1")Integer page,
			@RequestParam(required=false,defaultValue="10")Integer pageSize,
			@RequestParam(required=false)String search,
			@RequestParam(required=false,defaultValue="false")boolean isMap) throws Exception{
		
		saveResourceOwner(owner);
		
		//文件类的 获取全部文件列表(文件浏览) 只返回当前自己的文件列表
		if(Integer.valueOf(page)<0){
			return  fileService.getFileList(fileListRequestDto);
		}
		if(isMap) {
			return fileService.getFileList(fileListRequestDto,owner,true,page,pageSize,search);
		}else {
			Map<String, PaginationDto<FileListDto>> temp = fileService.getFileList(fileListRequestDto,owner,false,page,pageSize,search);
			return mergeMap(temp , new PaginationDto<FileListDto>(page,pageSize), (a,b) -> { a.mergePagination(b); return a ;} )  ;
		}
	}

	/**
	 * 请求方法 - 获取指定目录的父目录路径（仅支持服务器本地路径）
	 * @param path
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.GET, value="/getParentPath")
	@ApiOperation(value = "获取指定目录的父目录路径（仅支持服务器本地路径）")
	@ApiResponses({ @ApiResponse(code = 200, response =String.class, message = "成功" ) })
	public @ResponseBody Object getParentPath(@RequestParam(required=false,defaultValue="/")String path) throws Exception {
		return fileService.getParentPath(path);
	}

	@RequestMapping(method=RequestMethod.POST, value="/uploadFile")
	@ApiOperation(value = "上传指定类型的文件")
	@ApiResponses({ @ApiResponse(code = 200, response =FileListDto.class, message = "成功" ) })
	public @ResponseBody Object uploadFile( MultipartFile file,
											@RequestParam(required=false)String owner,
											@ApiParam(name="type",value="文件类型:data,excel,access,txt,csv,json,output,upload, input,hdfs,sftp,ktr,kjb",required = true)String type,
											@ApiParam(name="isCover",value="文件存在时,是否强制覆盖,默认false",required = true,defaultValue="false")@RequestParam(required=false,defaultValue="false")boolean isCover,
											@ApiParam(name="filterType",value="过滤文件类型,eg. access:只接收access类型(后缀为mdb,accdb)的文件",required = true)@RequestParam(required=false) String filterType) throws Exception{
		if( FileType.kjb.toString().equalsIgnoreCase(type) || FileType.ktr.toString().equalsIgnoreCase(type)) {
			saveResourceOwner(CloudSession.getLoginUser());
		}else {
			saveResourceOwner(owner);
			checkPrivilege();
		}
		return fileService.uploadFile(file,owner,type,filterType,isCover);
	}

	@RequestMapping(method=RequestMethod.POST, value="/fileExist")
	@ApiOperation(value = "判断文件是否存在")
	@ApiImplicitParams( value = {
	        @ApiImplicitParam(paramType = "query", name = "path", dataType = "String", required = false, value = "文件类型下的相对子目录"),
	        @ApiImplicitParam(paramType = "query", name = "type", dataType = "String", required = true, value = "文件类型:data,excel,access,txt,csv,json,output,upload, input,hdfs,sftp,ktr,kjb")
	})
	@ApiResponses({ @ApiResponse(code = 200, response =ReturnCodeDto.class, message = "成功" ) })
	public @ResponseBody  Object isFileExist(@RequestBody FileListRequestDto fileListRequestDto) throws Exception{
		return fileService.fileisExist(fileListRequestDto);
	}

	@RequestMapping(method=RequestMethod.POST, value="/deleteFile")
	@ApiOperation(value = "删除文件")
	@ApiImplicitParams( value = {
	        @ApiImplicitParam(paramType = "query", name = "path", dataType = "String", required = false, value = "文件类型下的相对子目录"),
	        @ApiImplicitParam(paramType = "query", name = "type", dataType = "String", required = true, value = "文件类型:data,excel,access,txt,csv,json,output,upload, input,hdfs,sftp,ktr,kjb")
	})
	@ApiResponses({ @ApiResponse(code = 200, response =ReturnCodeDto.class, message = "成功" ) })
	public @ResponseBody  Object deleteFile(@RequestBody FileListRequestDto fileListRequestDto) throws Exception{
		checkPrivilege();
		return fileService.deleteFile(fileListRequestDto);
	}

	@RequestMapping(method=RequestMethod.GET, value="/downloadFile")
	@ApiOperation(value = "下载文件")
	@ApiImplicitParams( value = {
	        @ApiImplicitParam(paramType = "query", name = "path", dataType = "String", required = false, value = "文件类型下的相对子目录"),
	        @ApiImplicitParam(paramType = "query", name = "type", dataType = "String", required = true, value = "文件类型:data,excel,access,txt,csv,json,output,upload, input,hdfs,sftp,ktr,kjb"),
	})
	public ResponseEntity<byte[]> downloadFile( FileListRequestDto fileListRequestDto) throws Exception{
		checkPrivilege();
		FileObject file = fileService.downloadFile(fileListRequestDto);
		String fileName = new String( file.getName().getBaseName().getBytes("UTF-8"),"iso-8859-1");//为了解决中文名称乱码问题  
		URI fileUri = file.getURL().toURI();
		file.close();

		HttpHeaders headers = new HttpHeaders();    
		headers.setContentDispositionFormData("attachment", fileName);   
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);   
		return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(new File(fileUri)), headers, HttpStatus.CREATED);  

	}

	@RequestMapping(method=RequestMethod.GET, value="/version")
	@ApiOperation(value = "获取当前服务的版本信息")
	@ApiResponses({ @ApiResponse(code = 200, response =ReturnCodeDto.class, message = "成功" ) })
	public @ResponseBody Object getVersion() throws Exception{
		return   fileService.getVersion();
	}
	
	@RequestMapping(method=RequestMethod.GET, value="/getVariables")
	@ApiOperation(value = "获取当前服务的所有全局变量")
	@ApiResponses({ @ApiResponse(code = 200, response =Map.class, message = "成功" ) })
	public @ResponseBody Object getVariables() throws Exception{
		 VariableSpace v = Variables.getADefaultVariableSpace() ;
		return  OsgiBundleUtils.getOsgiField(v, "properties", true);
	}
	
	//
	// Logging object interface
	//
	public static final LoggingObjectInterface loggingObject = new SimpleLoggingObject("CloudController", LoggingObjectType.GENERAL, null );

}
