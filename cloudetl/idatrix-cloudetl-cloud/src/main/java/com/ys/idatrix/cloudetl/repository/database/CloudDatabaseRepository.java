/**
 * 
 */
package com.ys.idatrix.cloudetl.repository.database;

import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.di.base.AbstractMeta;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.RepositoryDirectoryInterface;
import org.pentaho.di.repository.RepositoryObject;
import org.pentaho.di.repository.RepositoryObjectType;
import org.pentaho.di.repository.StringObjectId;
import org.pentaho.di.trans.TransMeta;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ys.idatrix.cloudetl.def.CloudMessage;
import com.ys.idatrix.cloudetl.dto.common.PaginationDto;
import com.ys.idatrix.cloudetl.dto.statistics.TaskMonthTotal;
import com.ys.idatrix.cloudetl.ext.CloudApp;
import com.ys.idatrix.cloudetl.ext.CloudSession;
import com.ys.idatrix.cloudetl.ext.utils.DatabaseHelper;
import com.ys.idatrix.cloudetl.ext.utils.StringEscapeHelper;
import com.ys.idatrix.cloudetl.repository.CloudRepository;
import com.ys.idatrix.cloudetl.repository.database.dto.FileRepositoryDto;
import com.ys.idatrix.cloudetl.repository.xml.CloudFileRepository;

/**
 * Cloud repository implementation,
 *	- for transformation (ktr) and jobs (kjb) storage.
 *
 * @author JW
 * @since 05-12-2017
 * 
 */
public class CloudDatabaseRepository {


	public static final Log  logger = LogFactory.getLog("CloudDatabaseRepository");
	
	private static final Class<FileRepositoryDto> TABLE_CLASS=FileRepositoryDto.class;
	
	private static boolean isDatabaseinit = false;
	
	public static void init() throws Exception {
		
		if(!isDatabaseinit) {
			DatabaseHelper.createOrUpdateTableifNonexist(TABLE_CLASS,  null);
			isDatabaseinit = true ;
		}
	}

	private static final String orderbyConstant = " UPDATETIME DESC " ;
	
	private RepositoryObjectType  type ;
	
	private CloudDatabaseRepository( RepositoryObjectType type) {
		this.type = type ;
		
		try {
			init();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static DatabaseRepositoryInstance resosi ;
	public static DatabaseRepositoryInstance getInstance( ) {
		if (resosi == null) {
			synchronized (CloudDatabaseRepository.class) {
				if (resosi == null) {
					resosi = new CloudDatabaseRepository.DatabaseRepositoryInstance();
				}
			}
		}
		return resosi;
	}
	

	public static class DatabaseRepositoryInstance {
		
		public  CloudDatabaseRepository transResosi ;
		public  CloudDatabaseRepository jobResosi ;
		
		public DatabaseRepositoryInstance( ) {
			if (jobResosi == null) {
				synchronized (CloudDatabaseRepository.class) {
					if (jobResosi == null) {
						jobResosi = new CloudDatabaseRepository(RepositoryObjectType.JOB);
					}
				}
			}
			if (transResosi == null) {
				synchronized (CloudDatabaseRepository.class) {
					if (transResosi == null) {
						transResosi = new CloudDatabaseRepository(RepositoryObjectType.TRANSFORMATION);
					}
				}
			}
		}
		
	}
	
	
	
	//=================================================== common method =====================================================================
	
	/**
	 * 按月份统计  当前租户下 每个月有多少 任务
	 * @param type ,任务类型,调度/转换 , 可为空,为空时返回两者总量
	 * @param flag year/month/day/yyyy-MM-dd , 为空:忽略日期 , 否则 当年/当月/当天/具体某天某月某年
	 * @return
	 * @throws Exception
	 */
	public static List<TaskMonthTotal> getTaskTotalByMonth( RepositoryObjectType type ,String flag) throws Exception {
		
		//SELECT  DATE_FORMAT(CREATETIME, '%Y-%m') AS month , COUNT(*) AS total FROM ETL_FILE_REPOSITORY    
		//where RENTERID = 443 and CREATETIME between date_sub(now(),interval 12 month) and now() 
		//		and date_format(`CREATETIME`,'%Y-%m-%d') = '2019-01-22' 
		//		GROUP BY month
		
		init() ;
		
		String renterId = CloudSession.getLoginRenterId() ;
		
		StringBuffer totalSql = new StringBuffer();
		totalSql.append("SELECT  DATE_FORMAT(CREATETIME, '%Y-%m') AS month , COUNT(*) AS total FROM ").append(DatabaseHelper.getTableName(TABLE_CLASS)).append(" ")
		.append( " WHERE RENTERID = '"  ).append( renterId ).append( "' ") 
		.append(" AND CREATETIME  between date_sub(now(),interval 12 month) and now() ") ;
		if( type != null ) {
			totalSql.append( " AND TYPE = '"  ).append( type.getTypeDescription() ).append("' ") ; ;
		}
		if( !Utils.isEmpty(flag) ) {
			String formatDate = DatabaseHelper.getDateFormatByFlag(flag) ;
			totalSql.append( " AND date_format(`CREATETIME`,'"  ).append( formatDate ).append("' ) =  ").append( DatabaseHelper.getDateValueByFlag(flag, formatDate)) ;
		}
		totalSql.append(" GROUP BY month ");
		
		return  DatabaseHelper.queryList(TaskMonthTotal.class, totalSql.toString());
	}
	
	/**
	 * 统计当前租户下有多少任务
	 * 
	 * @param type,任务类型,调度/转换 , 可为空,为空时返回两者总量
	 * @param flag year/month/day/yyyy-MM-dd , 为空:忽略日期 , 否则 当年/当月/当天/具体某天某月某年
	 * @return
	 * @throws Exception
	 */
	public static Long getTaskTotal( RepositoryObjectType type , String flag) throws Exception {
		
		//SELECT * FROM  ETL_FILE_REPOSITORY  where RENTERID = 443 and date_format(`CREATETIME`,'%Y-%m-%d') = '2019-01-22' 
		
		init() ;
		
		String renterId = CloudSession.getLoginRenterId() ;
		StringBuffer totalSql = new StringBuffer();
		totalSql.append("SELECT COUNT(*) FROM ").append(DatabaseHelper.getTableName(TABLE_CLASS)).append(" ")
		.append( " WHERE RENTERID = '"  ).append( renterId ).append( "' ") ;
		if( type != null ) {
			totalSql.append( " AND TYPE = '"  ).append( type.getTypeDescription() ).append("' ") ;
		}
		if( !Utils.isEmpty(flag) ) {
			String formatDate = DatabaseHelper.getDateFormatByFlag(flag) ;
			totalSql.append( " AND date_format(`CREATETIME`,'"  ).append( formatDate ).append("' ) =  ").append( DatabaseHelper.getDateValueByFlag(flag, formatDate)) ;
		}
		return  DatabaseHelper.queryFirst(Long.class, totalSql.toString());
	}
	
	/**
	 * 获取当前所有的租户
	 * @return
	 * @throws KettleException
	 */
	public  static List<String> getRenters( ) throws Exception{
		
		init() ;
		
		StringBuffer sql = new StringBuffer();
		sql.append( " SELECT DISTINCT RENTERID FROM " ).append( DatabaseHelper.getTableName(TABLE_CLASS)  );
		
		List<String> renters = DatabaseHelper.queryList(String.class,  sql.toString());
		if(renters == null  ) {
			return Lists.newArrayList();
		}
		return renters;
	}
	
	/**
	 * 获取当前租户下所有的用户名
	 * @return
	 * @throws KettleException
	 */
	public  static List<String> getRenterUsers( String renterId ) throws Exception{
		
		init() ;
		
		if(Utils.isEmpty(renterId)) {
			renterId = CloudSession.getLoginRenterId() ;
		}
		
		StringBuffer sql = new StringBuffer();
		sql.append( " SELECT DISTINCT OWNER FROM " ).append( DatabaseHelper.getTableName(TABLE_CLASS)  ).append( " WHERE RENTERID = '"  ).append(renterId ).append( "' ");
		
		List<String> users = DatabaseHelper.queryList(String.class,  sql.toString());
		if(users == null  ) {
			return Lists.newArrayList();
		}
		return users;
	}
	
	public static Map<String,List<String>> getRenterUsersMap( ) throws Exception{
		
		init() ;
		
		Map<String,List<String>> result = Maps.newHashMap() ;
		for(String renterId : getRenters() ) {
			result.put(renterId,getRenterUsers(renterId) );
		}
		return result ;
	}
	
	
	
	//=================================================== method =====================================================================

	/**
	 * 获取当前租户下的所有当前类型的用户列表
	 * @return
	 * @throws Exception
	 */
	public  List<String> getUsers() throws Exception {

		String renterId = CloudSession.getLoginRenterId() ;
		
		StringBuffer sql = new StringBuffer();
		sql.append( " SELECT DISTINCT OWNER FROM " ).append( DatabaseHelper.getTableName(TABLE_CLASS)  )
			.append( " WHERE RENTERID = '"  ).append( renterId ).append( "' ")
			.append( " AND TYPE = '"  ).append( type.getTypeDescription() ).append( "' ");
		
		List<String> names = DatabaseHelper.queryList(String.class,  sql.toString());
		if( names == null ) {
			return Lists.newArrayList();
		}
		return  names;
		
	}
	
	/**
	 * 获取用户下所有的组名
	 * @return
	 * @throws KettleException
	 */
	public  List<String> getUsersGroup( String owner ) throws Exception{
		return getAttributes(owner, "GROUP" );
	}
	
	/**
	 *  获取 对象属性
	 * @param owner
	 * @param name
	 * @param fieldName
	 * @return
	 * @throws Exception
	*/
	public  String getAttribute(String owner, String name , String fieldName) throws Exception {
		if( Utils.isEmpty( name )) {
    		return null;
    	}
		if(Utils.isEmpty(fieldName)) {
			fieldName = "OBJECTID" ;
		}
		if( Utils.isEmpty( owner )) {
    		owner = CloudSession.getResourceUser() ;
    	}
		String objectId = DatabaseHelper.queryFirst( String.class, TABLE_CLASS , new String[]{ fieldName }, 
				new String[] {"OWNER","TYPE","NAME"} ,  new String[] {"=","=","="} , new String[] {owner, type.getTypeDescription(), name},  
				orderbyConstant) ;
		return  objectId;
		
	}
	
	/**
	 *  获取 对象属性
	 * @param owner
	 * @param name
	 * @param fieldName
	 * @return
	 * @throws Exception
	*/
	public  List<String> getAttributes(String owner, String fieldName,String... conditionSql) throws Exception {
		if(Utils.isEmpty(fieldName)) {
			fieldName = "NAME" ;
		}
		if( Utils.isEmpty( owner )) {
    		owner = CloudSession.getResourceUser() ;
    	}
		String renterId = CloudSession.getLoginRenterId() ;
		
		StringBuffer sql = new StringBuffer();
		sql.append( " SELECT DISTINCT ").append(DatabaseHelper.quoteField(fieldName)).append(" FROM " ).append( DatabaseHelper.getTableName(TABLE_CLASS)  )
			.append( " WHERE RENTERID = '"  ).append( renterId ).append( "' ")
			.append( " AND OWNER = '"  ).append( owner ).append( "' ")
			.append( " AND TYPE = '"  ).append( type.getTypeDescription() ).append( "' ");
		if( !Utils.isEmpty(conditionSql)) {
			for(String condition : conditionSql) {
				if( Utils.isEmpty(condition )) {
					continue ;
				}
				sql.append(" AND ").append(condition);
			}
		}
		sql.append( " ORDER BY ").append(orderbyConstant);
		
		List<String> names = DatabaseHelper.queryList(String.class,  sql.toString());
		if( names == null ) {
			return Lists.newArrayList();
		}
		return  names;
		
	}

	/**
	 * 获取 对象信息
	 * @param owner
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public  FileRepositoryDto getRepositoryInfo(String owner,  String name ) throws Exception {
		if( Utils.isEmpty( name )) {
    		return null;
    	}
		if( Utils.isEmpty( owner )) {
    		owner = CloudSession.getResourceUser() ;
    	}
		
		return DatabaseHelper.queryFirst( TABLE_CLASS ,  null , null , 
				new String[] {"OWNER","TYPE","NAME"} ,  new String[] {"=","=","="} , new String[] {owner, type.getTypeDescription(), name},  
				orderbyConstant) ;
	}
	
	
	/**
	 * 获取 对象信息 分页列表 <br>
	 * @param renterId 租户id ,可为空
	 * @param owner,拥有者 , 可为空
	 * @param pageNo
	 * @param pageSize
	 * @param search
	 * @return
	 * @throws Exception
	 */
	public  PaginationDto<FileRepositoryDto> getRepositoryPagination( String renterId , String owner, String group, Integer pageNo ,Integer pageSize ,String search) throws Exception {
		
		String searchStatus = null ;
		String searchName = null ;
		if( !Utils.isEmpty(search)) {
			search = StringEscapeHelper.decode(search);
			//解析过滤
			if( search.contains("::")) {
				String status = search.split("::", 2)[0];
				searchName = search.split("::", 2)[1];
				if (!Utils.isEmpty(status)) {
					switch (status) {
					case "wait":
						searchStatus = " ( 'Waiting', 'Undefined', 'Finished' ) ";
						break;
					case "run":
						searchStatus = " ( 'Running', 'Preparing executing', 'Initializing', 'Paused' ) ";
						break;
					case "warn":
						searchStatus = " ( 'Finished (with errors)', 'Stopped', 'Halting' ) ";
						break;
					case "error":
						searchStatus = " ( 'TimeOut', 'Failed', 'Unknown' ) ";
						break;
					default:
						searchStatus = null;
						break;
					}
				}
			}else {
				searchName = search ;
			}
		}
		//返回结果对象
		PaginationDto<FileRepositoryDto> result = new PaginationDto<FileRepositoryDto>(pageNo, pageSize, search);
		// 获取 rows
		StringBuffer whereSql = new StringBuffer();
		whereSql.append("WHERE TYPE = '") .append(type.getTypeDescription()).append("' ");
		if(!Utils.isEmpty(renterId)) {
			whereSql.append("AND RENTERID = '").append(renterId).append("' ");
		}
		if(!Utils.isEmpty(owner)) {
			whereSql.append("AND OWNER = '").append(owner).append("' ");
		}
		if(!Utils.isEmpty(group)) {
			whereSql.append(" AND ").append(DatabaseHelper.quoteField("GROUP")).append(" = '").append(group).append("' ");
		}
		if (!Utils.isEmpty(searchStatus)) {
			whereSql.append(" AND LASTSTATUS IN ").append(searchStatus);
		}
		if (!Utils.isEmpty(searchName)) {
			whereSql.append(" AND NAME LIKE '%").append(searchName).append("%' ");
		}
		whereSql.append(" ORDER BY ").append(orderbyConstant);

		StringBuffer querySql = new StringBuffer(whereSql.toString());
		if (pageNo != null && pageNo > 0 && pageSize != null) {
			// 分页 , 只适应 Mysql limit (pageNo-1)*pageSize,pageSize;
			querySql.append(" LIMIT ").append(((pageNo - 1) * pageSize)).append(",").append(pageSize);
		}
		List<FileRepositoryDto> list = DatabaseHelper.queryList(TABLE_CLASS, querySql.toString());
		result.setRows(list);

		// 获取total
		StringBuffer totalSql = new StringBuffer();
		totalSql.append("SELECT COUNT(*) FROM ").append(DatabaseHelper.getTableName(TABLE_CLASS)).append(" ").append(whereSql.toString());
		Long total = DatabaseHelper.queryFirst(Long.class, totalSql.toString());
		result.setTotal(total.intValue());

		return result;
		
	}
		
	
	public Map<String,PaginationDto<FileRepositoryDto>> getPaginationInfoMap(String owner , String group, Integer pageNo ,Integer pageSize,String search ,boolean isMap) throws Exception{
		
		Map<String,PaginationDto<FileRepositoryDto>> result =  Maps.newHashMap() ;
		if( CloudSession.isPrivilegeEnable()) {
			//当前登录者是租户,获取 owner用户下的 group组 下的信息
			if(isMap) {
				if(Utils.isEmpty(owner)  && CloudSession.isSuperPrivilege() ) {
					//获取所有的用户
					for(String ownerUser : getRenterUsers( null)) {
						PaginationDto<FileRepositoryDto> pagedto = getRepositoryPagination(null,ownerUser,group,  pageNo, pageSize,search);
						result.put(ownerUser, pagedto );
					}
				}else {
					//获取owner 用户
					 if( Utils.isEmpty( owner )) {
				    	owner = CloudSession.getResourceUser() ;
				     }
					PaginationDto<FileRepositoryDto> pagedto = getRepositoryPagination(null,owner, group, pageNo, pageSize,search);
					result.put(owner, pagedto);
				}
			}else {
				//通过租户Id 获取列表
				result.put(CloudSession.getLoginUser(), getRepositoryPagination(CloudSession.getLoginRenterId(),null,group, pageNo, pageSize,search));
			}
		}else if( Utils.isEmpty(owner) || owner.equals(CloudSession.getLoginUser())){
			//当前不是租户 或者 系统不开启租户超级权限,只能获取当前登录用户自己的
			PaginationDto<FileRepositoryDto> pagedto = getRepositoryPagination(null,CloudSession.getLoginUser(),group,  pageNo, pageSize,search);
			result.put(CloudSession.getLoginUser(), pagedto );
		}
		
		return result ;
	}

	/**
	 * 获取 用户的所有对象信息 <br>
	 *
	 * @param owner
	 * @return  List&lt?> 是  List&ltFileRepositoryDto>
	 * @throws Exception
	 */
	public  List<?> getRepositoryList(String owner,String group ) throws Exception {
		if( Utils.isEmpty( owner )) {
    		owner = CloudSession.getResourceUser() ;
    	}
		
		String[] whereFields;
		String[] condition ;
		Object[] whereValues ;
		if( !Utils.isEmpty(group) && !CloudRepository.ALL_GROUP_NAME.equalsIgnoreCase(group) ) {
			 whereFields = new String[] { "OWNER", "TYPE" ,"GROUP"};
			 condition = new String[] {"=","=","="} ;
			whereValues= new String[] {owner, type.getTypeDescription(),group} ;
		}else{
			 whereFields = new String[] { "OWNER", "TYPE" };
			 condition = new String[] {"=","="} ;
			whereValues= new String[] {owner, type.getTypeDescription()} ;
		}
	
		return DatabaseHelper.queryList(TABLE_CLASS , null, null , 	whereFields , condition, whereValues ,  orderbyConstant) ;
	}
	
	/**
	 * 获取 用户 下所有的 对象列表信息 <br>
	 * 有租户特权 : owner为空,获取租户下所有的信息列表  . owner不为空,获取当前越权信息列表  <br>
	 * 无租户特权:  owner为空或者owner为当前登录者,返回当前信息列表  <br>
	 * 
	 * @param owner 
	 * @return  List&lt?> 是  List&ltFileRepositoryDto>
	 * @throws Exception 
	 */
	public Map<String,List<Object>> getRepositoryInfoMap( String owner,String group ) throws Exception {
		Map<String,List<Object>> result =  Maps.newHashMap() ;
		if( CloudSession.isPrivilegeEnable()) {
			//当前登录者是租户,获取 owner用户下的 group组 下的信息
			if(Utils.isEmpty(owner) && CloudSession.isSuperPrivilege() ) {
				//获取所有的用户
				for(String ownerUser : getRenterUsers( null)) {
					@SuppressWarnings("unchecked")
					List<Object> list = (List<Object>) getRepositoryList(ownerUser, group );
					if(  list != null && !list.isEmpty() ) {
						result.put(ownerUser, list );
					}
				}
			}else {
				//获取owner 用户
				 if( Utils.isEmpty( owner )) {
			    		owner = CloudSession.getResourceUser() ;
			     }
				@SuppressWarnings("unchecked")
				List<Object> list = (List<Object>) getRepositoryList(owner, group );
				result.put(owner, list == null?Lists.newArrayList() : list );
			}
		}else if( Utils.isEmpty(owner) || owner.equals(CloudSession.getLoginUser())){
			//当前不是租户 或者 系统不开启租户超级权限,只能获取当前登录用户自己的
			@SuppressWarnings("unchecked")
			List<Object> list =  (List<Object>) getRepositoryList(CloudSession.getLoginUser() , group );
			result.put(CloudSession.getLoginUser(),  list == null?Lists.newArrayList() : list );
		}
		
		return result ;
	}
	
	
	public  Map<String,List<String>> getNamesMap( String owner, String  group  ) throws Exception {
		
		 Map<String,List<String>> result =  Maps.newHashMap() ;
		 if( CloudSession.isPrivilegeEnable()) {
			 //当前登录者是租户,获取 owner用户下的 group组 下的信息
			 if(Utils.isEmpty(owner) && CloudSession.isSuperPrivilege()) {
				 //获取所有的用户
				 for(String ownerUser : getRenterUsers(null )) {
					 List<String> list = getAttributes(ownerUser, "NAME" , !Utils.isEmpty(group)? DatabaseHelper.quoteField("GROUP")+" = '+group+'" : null );
					 if(  list != null && !list.isEmpty() ) {
						 result.put(ownerUser, list );
					 }
				 }
			 }else {
				 //获取owner 用户
				 if( Utils.isEmpty( owner )) {
			    		owner = CloudSession.getResourceUser() ;
			     }
				 result.put( owner,  getAttributes(owner,  "NAME" , !Utils.isEmpty(group)?DatabaseHelper.quoteField("GROUP")+" = '+group+'" : null ) );
			 }
		 }else if( Utils.isEmpty(owner) || owner.equals(CloudSession.getLoginUser())){
			 //当前不是租户 或者 系统不开启租户超级权限,只能获取当前登录用户自己的
			 result.put(CloudSession.getLoginUser(),  getAttributes(CloudSession.getLoginUser(),  "NAME" , !Utils.isEmpty(group)?DatabaseHelper.quoteField("GROUP")+" = '+group+'" : null ));
		 }
		 return result ;
	}

	/**
	 * 通过名字加载meta对象
	 * @param owner
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public  AbstractMeta loadByName( String owner ,String name,String group ) throws Exception {
		
		String objectId = getAttribute(owner, name, "OBJECTID");
		
		boolean updateInfo =  false ;
		AbstractMeta meta = null ;
		Repository repository = CloudApp.getInstance().getRepository();
		ObjectId id;
		if( RepositoryObjectType.JOB.equals(type)) {
			//调度
			if(Utils.isEmpty(objectId)) {
				if(Utils.isEmpty(group)) {
					group = CloudFileRepository.getInstance().getJobGroup(owner ,name, CloudRepository.DEFAULT_GROUP_NAME);
				}
				RepositoryDirectoryInterface path = CloudFileRepository.getInstance().getRootDirectory().findDirectory( CloudApp.getInstance().getUserJobsRepositoryPath(owner, group));
				id = repository.getJobId(name, path);
				if( id == null ) {
					throw new KettleException(CloudMessage.get("Repository.Job.NotExists", name));
				}
				updateInfo = true;
			}else {
				id = new StringObjectId(objectId) ;
			}
			
			meta = repository.loadJob(id, null);
			RepositoryObject repositoryObject = repository.getObjectInformation(id, RepositoryObjectType.JOB);
			meta.setRepositoryDirectory(repositoryObject.getRepositoryDirectory());
			
		} else if( RepositoryObjectType.TRANSFORMATION.equals(type)) {
			//转换
			if(Utils.isEmpty(objectId)) {
				if(Utils.isEmpty(group)) {
					group = CloudFileRepository.getInstance().getTransGroup(owner ,name, CloudRepository.DEFAULT_GROUP_NAME);
				}
				RepositoryDirectoryInterface path = CloudFileRepository.getInstance().getRootDirectory().findDirectory(CloudApp.getInstance().getUserTransRepositoryPath(owner, group));
				id = repository.getTransformationID(name, path);
				if( id ==null ) {
					throw new KettleException(CloudMessage.get("Repository.Trans.NotExists", name));
				}
				updateInfo = true;
			}else {
				id = new StringObjectId(objectId) ;
			}
			
			meta = repository.loadTransformation(id, null);
			RepositoryObject repositoryObject = repository.getObjectInformation(id, RepositoryObjectType.TRANSFORMATION);
			meta.setRepositoryDirectory(repositoryObject.getRepositoryDirectory());
			meta.setMaxUndo(Const.MAX_UNDO);
		}
		
		if(updateInfo) {
			saveInfo(meta, owner, group);
		}
		
		
		return meta;
		
	}


	/**
	 * 检查是否名称已经存在
	 * @param owner
	 * @param transName
	 * @return
	 * @throws KettleException
	 */
	public  boolean checkName( String owner ,String name) throws Exception {
		
		String objectId = getAttribute(owner, name, "OBJECTID");
		if(Utils.isEmpty(objectId)) {
			return false ;
		}
		return true;
	}
	
	/**
	 * 获取所在组
	 * @param owner
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public String getGroup( String owner, String name) throws Exception { 
		if(Utils.isEmpty(name)) {
			throw new KettleException(type.getTypeDescription()+"名称不能为空!");
		}
		String group = getAttribute(owner, name, "GROUP");
		if(Utils.isEmpty(group)) {
			throw new KettleException(type.getTypeDescription()+" ["+name+"(拥有者:"+owner+")] 未找到Group!");
		}
		return group ;
	}

	/**
	 * 创建 meta
	 * @param owner
	 * @param name
	 * @param group
	 * @return
	 * @throws Exception
	 */
	public AbstractMeta create( String owner ,String name, String group) throws Exception {
		if(Utils.isEmpty(name)) {
			throw new KettleException(type.getTypeDescription()+"名称不能为空!");
		}
		if( Utils.isEmpty( owner )) {
    		owner = CloudSession.getLoginUser() ;
    	}
		if( Utils.isEmpty( group )) {
			group = CloudRepository.DEFAULT_GROUP_NAME ;
    	}
		
		AbstractMeta meta = null ;
		//在文件系统创建
		if( RepositoryObjectType.JOB.equals(type)) {
			//调度
			meta = CloudFileRepository.getInstance().createJob(owner, name, group);
		} else if( RepositoryObjectType.TRANSFORMATION.equals(type)) {
			//转换
			meta = CloudFileRepository.getInstance().createTrans(owner, name, group) ;
		}
		if( meta == null ) {
			throw new KettleException(type.getTypeDescription()+" ["+name+"(拥有者:"+owner+")] 创建失败!");
		}
		//保存信息到数据库
		saveInfo(meta, owner, group);

		return meta;
	}

	/**
	 * 删除meta
	 * @param owner
	 * @param name
	 * @throws KettleException
	 */
	public void drop( String owner ,String name,String group ) throws Exception {
		if(Utils.isEmpty(name)) {
			throw new KettleException(type.getTypeDescription()+"名称不能为空!");
		}
		if( Utils.isEmpty( owner )) {
    		owner = CloudSession.getResourceUser() ;
    	}
		
		//在文件系统删除
		if( RepositoryObjectType.JOB.equals(type)) {
			//调度
			CloudFileRepository.getInstance().dropJob(owner, name, group);
		} else if( RepositoryObjectType.TRANSFORMATION.equals(type)) {
			//转换
			CloudFileRepository.getInstance().dropTrans(owner, name, group);
		}
		
		DatabaseHelper.delete(TABLE_CLASS , new String[] {"OWNER","TYPE","NAME"} ,  new String[] {"=","=","="} , new String[] {owner, type.getTypeDescription(), name} );
	}

	/**
	 * Save transformation into repository.
	 * @param transMeta
	 * @throws Exception 
	 */
	public void save( AbstractMeta meta ,String owner,String group) throws Exception {
		if( meta == null ) {
			throw new KettleException(type.getTypeDescription()+"meta对象不能为空!");
		}
		if( Utils.isEmpty( owner )) {
    		owner = CloudSession.getResourceUser() ;
    	}
		if( Utils.isEmpty( group )) {
			group = meta.getRepositoryDirectory().getName() ;
    	}
		FileRepositoryDto info = null;
		//获取数据库信息对象,名称可能在重命名时改变,不能使用名称获取,objectId在保存前还是旧的id
		if( meta.getObjectId() != null ) {
			info = DatabaseHelper.queryFirst(TABLE_CLASS , null, null, new String[] {"OBJECTID"} ,  new String[] {"="} , new String[] {meta.getObjectId().getId()}, orderbyConstant);
		}
		if( info == null ) {
			//保存信息到数据库
			info = saveInfo(meta, owner, group);;
		}
		//在文件系统创建,保存后 objectId属性 可能会改变
		if( RepositoryObjectType.JOB.equals(type)) {
			//调度
			CloudFileRepository.getInstance().saveJob((JobMeta)meta);
		} else if( RepositoryObjectType.TRANSFORMATION.equals(type)) {
			//转换
			CloudFileRepository.getInstance().saveTrans((TransMeta)meta);
		}
		
		updateInfo(meta, info);
	}
	
	
	public FileRepositoryDto saveInfo( AbstractMeta meta ,String owner,String group) throws Exception {
		
		FileRepositoryDto info = null;
		if( meta.getObjectId() != null ) {
			info = DatabaseHelper.queryFirst(TABLE_CLASS , null, null, new String[] {"OBJECTID"} ,  new String[] {"="} , new String[] {meta.getObjectId().getId()}, orderbyConstant);
		}
		if( info == null ) {
			//新增
			if( Utils.isEmpty( owner )) {
	    		owner = CloudSession.getResourceUser() ;
	    	}
			if( Utils.isEmpty( group )) {
				group = meta.getRepositoryDirectory().getName() ;
	    	}
			String objectId = "";
			if(  meta.getObjectId() != null  ) {
				objectId  =  meta.getObjectId().getId();
			}
			
			info = new FileRepositoryDto(objectId, CloudSession.getLoginRenterId(), owner, group, type.getTypeDescription(), meta.getName(), meta.getRepositoryDirectory().getPath());
			info.setDescription(meta.getDescription());
			Date curTime = new Date() ;
			info.setCreateTime( curTime );
			info.setUpdateTime( curTime );
			
			DatabaseHelper.insert(TABLE_CLASS , info);
		}else {
			//更新
			updateInfo(meta, info);
		}
		
		return info;
	}
	
	
	public void updateInfo( AbstractMeta meta,FileRepositoryDto info ) throws Exception {
		
		if( info == null ) {
			info = DatabaseHelper.queryFirst(TABLE_CLASS , null, null, new String[] {"OBJECTID"} ,  new String[] {"="} , new String[] {meta.getObjectId().getId()}, orderbyConstant);
		}
		if( info != null ) {
			//更新
			info.setObjectId(meta.getObjectId().getId());
			info.setName(meta.getName());
			info.setUpdateTime(meta.getModifiedDate());
			info.setDescription(meta.getDescription());
			info.setDirectory(meta.getRepositoryDirectory().getPath());
			info.setGroup(meta.getRepositoryDirectory().getName());
			
			DatabaseHelper.update(TABLE_CLASS ,  info, "ID");
		}
		
	}
	
	public void updateExecInfo( String owner ,String name, Date lastExecTime ,String lastStatus) throws Exception {
		DatabaseHelper.update(TABLE_CLASS, new String[] {"LASTEXECTIME","LASTSTATUS"},new String[] {"OWNER","TYPE","NAME"} ,  new String[] {"=","=","="} , new Object[] {lastExecTime,lastStatus,owner, type.getTypeDescription(), name});
	}

}



