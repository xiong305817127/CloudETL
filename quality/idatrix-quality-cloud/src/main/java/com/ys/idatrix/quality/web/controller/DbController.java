/**
 * 云化数据集成系统
 * iDatrix CloudETL
 */
package com.ys.idatrix.quality.web.controller;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Lists;
import com.ys.idatrix.quality.dto.common.CheckResultDto;
import com.ys.idatrix.quality.dto.common.PaginationDto;
import com.ys.idatrix.quality.dto.common.RequestNameDto;
import com.ys.idatrix.quality.dto.common.ReturnCodeDto;
import com.ys.idatrix.quality.dto.common.TestResultDto;
import com.ys.idatrix.quality.dto.db.DBIdDto;
import com.ys.idatrix.quality.dto.db.DbBriefDto;
import com.ys.idatrix.quality.dto.db.DbConnectionDto;
import com.ys.idatrix.quality.dto.db.DbSchemaDto;
import com.ys.idatrix.quality.dto.db.DbTableNameDto;
import com.ys.idatrix.quality.reference.metacube.dto.MetaCubeDbTableFieldDto;
import com.ys.idatrix.quality.reference.metacube.dto.MetaCubeDbTableViews;
import com.ys.idatrix.quality.service.db.CloudLocalDbService;
import com.ys.idatrix.quality.service.db.CloudMetaCubeDbService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * 数据库流程控制器
 * DB connection procedure controller.
 * @author JW
 * @since 05-12-2017
 * 
 */
@Controller
@RequestMapping(value="/db")
@Api(value = "/db" , description="数据库相关操作api")
public class DbController extends BaseAction {

	@Autowired
	private CloudMetaCubeDbService cloudDbService;
	
	@Autowired
	private CloudLocalDbService localDbService;
	

	/**
	 * 请求方法 - 获取数据库连接列表
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.GET, value="/getDbList")
	@ApiOperation(value = "获取数据库连接列表(测试数据库状态,需要等待)")
	@ApiResponses({ @ApiResponse(code = 200, response = DbBriefDto[].class, message = "成功" ) })
	@Deprecated
	public @ResponseBody Object getDbList( @RequestParam(required=false) String owner ,
										   @RequestParam(required=false) Boolean isRead ,
										   @RequestParam(required=false,defaultValue="false")boolean isMap) throws Exception {
		
		saveResourceOwner(owner);
		if(isMap) {
			return  cloudDbService.getDbConnectionList(owner, isRead);
		}else {
			Map<String, List<DbBriefDto>> temp = cloudDbService.getDbConnectionList(owner,isRead);
			return mergeMap(temp ,Lists.newArrayList(), (a,b) -> { a.addAll(b); return a ;} ) ;
		}
	}

	/**
	 * 请求方法 - 获取数据库连接列表（不测试状态）
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.GET, value="/getDbList2")
	@ApiOperation(value = "获取数据库连接列表（不测试状态）")
	@ApiImplicitParams( value = {
	        @ApiImplicitParam(paramType = "query", name = "page", dataType = "int", required = false, value = "分页参数,页号,-1代表不分页,获取全部,默认-1", defaultValue = "-1"),
	        @ApiImplicitParam(paramType = "query", name = "pageSize", dataType = "int", required = false, value = "分页参数,每页大小,默认10", defaultValue = "10"),
	        @ApiImplicitParam(paramType = "query", name = "search", dataType = "String", required = false, value = "搜索参数,根据名字过滤")
	})
	@ApiResponses({ @ApiResponse(code = 200, response = DbBriefDto[].class, message = "成功" ) })
	public @ResponseBody Object getDbList2(@RequestParam(required=false)String owner,
											@RequestParam(required=false) Boolean isRead ,
										   @RequestParam(required=false,defaultValue="-1")Integer page,
			 							   @RequestParam(required=false,defaultValue="10")Integer pageSize,
			 							   @RequestParam(required=false)String search,
			 							   @RequestParam(required=false,defaultValue="false")boolean isMap) throws Exception {
		saveResourceOwner(owner);
		
		if(isMap) {
			if(Integer.valueOf(page)<0){
				return   cloudDbService.getDbConnectionList(owner,isRead);
			}
			return cloudDbService.getDbConnectionList(owner,isRead, isMap, page, pageSize, search);
		}else {
			if(Integer.valueOf(page)<0){
				Map<String, List<DbBriefDto>> temp = cloudDbService.getDbConnectionList(owner,isRead);
				return mergeMap(temp ,Lists.newArrayList(), (a,b) -> { a.addAll(b); return a ;} ) ;
			}
			Map<String, PaginationDto<DbBriefDto>> temp = cloudDbService.getDbConnectionList(owner,isRead, isMap, page, pageSize, search);
			return  mergeMap(temp ,new PaginationDto<DbBriefDto>(page,pageSize), (a,b) -> { a.mergePagination(b); return a ;} )  ;
		}
	}

	/**
	 * 请求方法 - 测试数据库连接状态
	 * @param dbcName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.POST, value="/testDbConnection")
	@ApiOperation(value = "测试数据库连接状态(By owner and schemaId)")
	@ApiResponses({ @ApiResponse(code = 200, response = TestResultDto.class, message = "成功" ) })
	public @ResponseBody Object testDbConnection(@RequestBody DBIdDto dbcId) throws Exception {
		return cloudDbService.doDbConnectionTest(dbcId.getOwner(), dbcId.getId());
	}

	/**
	 * 请求方法 - 检查数据库连接名是否存在
	 * @param dbcName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.POST, value="/checkDbConnectionName")
	@ApiOperation(value = "检查数据库连接名是否存在")
	@ApiResponses({ @ApiResponse(code = 200, response = CheckResultDto.class, message = "成功" ) })
	public @ResponseBody Object checkDbConnectionName(@RequestBody RequestNameDto dbcName) throws Exception {
		return localDbService.checkDbConnectionName(dbcName.getOwner(),dbcName.getName());
	}

	/**
	 * 请求方法 - 创建新的数据库连接
	 * @param dbConnection
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.POST, value="/createDbConnection")
	@ApiOperation(value = "创建新的数据库连接")
	@ApiResponses({ @ApiResponse(code = 200, response = ReturnCodeDto.class, message = "成功" ) })
	public @ResponseBody Object createDbConnection(@RequestBody DbConnectionDto dbConnection) throws Exception {
		checkPrivilege();
		return localDbService.addDbConnection(dbConnection);
	}

	/**
	 * 请求方法 - 编辑数据库连接配置信息
	 * @param dbcName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.GET, value="/editDbConnection")
	@ApiOperation(value = "获取可编辑数据库连接配置信息(By owner and schemaId)")
	@ApiResponses({ @ApiResponse(code = 200, response = DbConnectionDto.class, message = "成功" ) })
	public @ResponseBody Object editDbConnection( DBIdDto dbcId ) throws Exception {
		return cloudDbService.getDbConnection(dbcId.getOwner(),dbcId.getId());
	}

	/**
	 * 请求方法 - 保存数据库连接配置信息
	 * @param dbConnection
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.POST, value="/saveDbConnection")
	@ApiOperation(value = "保存数据库连接配置信息")
	@ApiResponses({ @ApiResponse(code = 200, response = ReturnCodeDto.class, message = "成功" ) })
	public @ResponseBody Object saveDbConnection(@RequestBody DbConnectionDto dbConnection) throws Exception {
		checkPrivilege();
		return localDbService.updateDbConnection(dbConnection);
	}

	/**
	 * 请求方法 - 删除数据库连接配置
	 * @param dbcName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.POST, value="/deleteDbConnection")
	@ApiOperation(value = "删除数据库连接配置")
	@ApiResponses({ @ApiResponse(code = 200, response = ReturnCodeDto.class, message = "成功" ) })
	public @ResponseBody Object deleteDbConnection(@RequestBody RequestNameDto dbcName) throws Exception {
		checkPrivilege();
		return localDbService.deleteDbConnection(dbcName.getOwner(),dbcName.getName());
	}

	/**
	 * 请求方法 - 查询数据库连接中的模式
	 * @param dbcName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.GET, value="/getDbSchema")
	@ApiOperation(value = "查询数据库连接的模式( By owner and databaseId and DbName and isRead)")
	@ApiResponses({ @ApiResponse(code = 200, response = DbSchemaDto[].class, message = "成功" ) })
	public @ResponseBody Object getDbSchema( DBIdDto dbcId ) throws Exception {
		return cloudDbService.getDbSchema(dbcId.getOwner(),dbcId.getId(),dbcId.getName(), dbcId.getIsRead());
	}

	/**
	 * 请求方法 - 查询数据库连接中指定模式下的表
	 * @param schemaName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.GET, value="/getDbTables")
	@ApiOperation(value = "查询数据库连接中指定模式下的表( By owner and SchemaID and isRead)")
	@ApiResponses({ @ApiResponse(code = 200, response = MetaCubeDbTableViews.class, message = "成功" ) })
	public @ResponseBody Object getDbTables( DBIdDto dbcId ) throws Exception {
		return cloudDbService.getDbTables(dbcId.getOwner(), dbcId.getId(), dbcId.getIsRead());
	}

	/**
	 * 请求方法 - 查询数据库连接中指定模式和表名的字段
	 * @param dbTableName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.GET, value="/getDbTableFields")
	@ApiOperation(value = "查询数据库连接中指定模式和表名的字段( by owner and TableID)")
	@ApiResponses({ @ApiResponse(code = 200, response = MetaCubeDbTableFieldDto[].class, message = "成功" ) })
	public @ResponseBody Object getDbTableFields( DBIdDto dbcId ) throws Exception {
		return cloudDbService.getDbTableFields(dbcId.getOwner(), dbcId.getId() );
	}

	/**
	 * 请求方法 - 查询数据库连接中指定模式和表名的主键
	 * @param dbTableName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.GET, value="/getDbTablePrimaryKey")
	@ApiOperation(value = "查询数据库连接中指定模式和表名的主键 ( By owner and schemaID and tableName)")
	@ApiResponses({ @ApiResponse(code = 200, response = String[].class, message = "成功" ) })
	public @ResponseBody Object getDbTablePrimaryKey( DbTableNameDto dbTableName) throws Exception {
		return cloudDbService.getDbTablePrimaryKey(dbTableName.getOwner(), dbTableName.getSchemaId(), dbTableName.getTable());
	}

	
	/**
	 * 请求方法 - 查询数据库连接中的存储过程
	 * @param transName
	 * @param connection
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method = {RequestMethod.GET}, value = "/getProc")
	@ApiOperation(value = "查询数据库连接中的存储过程")
	@ApiImplicitParams( value = {
			@ApiImplicitParam(paramType = "query", name = "connection", dataType = "String", required = true, value = "数据库连接名")
	})
	@ApiResponses({ @ApiResponse(code = 200, response = String[].class, message = "成功" ) })
	public @ResponseBody Object getProc(@RequestParam(required = true, name = "schemaId") Long schemaId ,
										@RequestParam(required=false)String owner ) throws Exception {
		saveResourceOwner(owner);
		return cloudDbService.getProc(owner, schemaId) ;
	}
	
	/**
	 * 请求方法 - 创建新的JNDI连接
	 * @param dbConnection
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.POST, value="/createJndi")
	@ApiOperation(value = "创建新的jndi连接")
	@ApiResponses({ @ApiResponse(code = 200, response = ReturnCodeDto.class, message = "成功" ) })
	public @ResponseBody Object createjndi(@RequestBody DbConnectionDto dbConnection) throws Exception {
		checkPrivilege();
		return localDbService.createjndi(dbConnection);
	}
	
	/**
	 * 请求方法 - 获取JNdi连接列表
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.GET, value="/getJndiList")
	@ApiOperation(value = "获取JNdi连接列表")
	@ApiResponses({ @ApiResponse(code = 200, response = DbConnectionDto[].class, message = "成功" ) })
	public @ResponseBody Object getJndiList(String type,
			@RequestParam(required=false)String owner) throws Exception {
		saveResourceOwner(owner);
		return localDbService.getjndiList(owner , type);
	}
	
	/**
	 * 请求方法 - 获取JNdi连接信息
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.GET, value="/editJndi")
	@ApiOperation(value = "获取JNdi连接信息")
	@ApiResponses({ @ApiResponse(code = 200, response = DbConnectionDto.class, message = "成功" ) })
	public @ResponseBody Object editJndi(String type,String name,
			@RequestParam(required=false)String owner) throws Exception {
		saveResourceOwner(owner);
		return localDbService.getjndiByName(owner , type, name);
	}
	
	/**
	 * 请求方法 - 删除JNDI连接配置
	 * @param dbcName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.GET, value="/deleteJndi")
	@ApiOperation(value = "删除JNDI配置")
	@ApiResponses({ @ApiResponse(code = 200, response = ReturnCodeDto.class, message = "成功" ) })
	public @ResponseBody Object deleteJndi(String type,String name,
			@RequestParam(required=false)String owner) throws Exception {
		checkPrivilege();
		saveResourceOwner(owner);
		return localDbService.deletejndi(owner , type, name) ;
	}
	
	/**
	 * 请求方法 - 检查JNDI连接配置名是否存在
	 * @param dbcName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.GET, value="/checkJndiName")
	@ApiOperation(value = "检查JNDI连接配置名是否存在")
	@ApiResponses({ @ApiResponse(code = 200, response = CheckResultDto.class, message = "成功" ) })
	public @ResponseBody Object checkJndiName(String type,String name,
			@RequestParam(required=false)String owner) throws Exception {
		saveResourceOwner(owner);
		return localDbService.checkJndiName(owner , type, name) ;
	}

}
