/**
 * 
 */
package com.ys.idatrix.cloudetl.repository;

import java.util.List;
import java.util.Map;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.util.IdatrixPropertyUtil;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.RepositoryDirectoryInterface;
import org.pentaho.di.repository.RepositoryObjectType;
import org.pentaho.di.repository.StringObjectId;
import org.pentaho.di.trans.TransMeta;

import com.google.common.collect.Lists;
import com.ys.idatrix.cloudetl.dto.common.PaginationDto;
import com.ys.idatrix.cloudetl.dto.statistics.TaskMonthTotal;
import com.ys.idatrix.cloudetl.repository.database.CloudDatabaseRepository;
import com.ys.idatrix.cloudetl.repository.database.CloudDatabaseRepository.DatabaseRepositoryInstance;
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
public class CloudRepository {

	public static final String DEFAULT_GROUP_NAME = "default";
	public static final String ALL_GROUP_NAME = "all";
	

	private static DatabaseRepositoryInstance databaseRepository ;
	private static CloudFileRepository fileRepository ;
	
	private CloudRepository( RepositoryObjectType type) {
	}
	
	public static  boolean initDatebaseRepository( ) {
		if( databaseRepository == null && fileRepository == null ) {
			synchronized(CloudRepository.class) {
				if( databaseRepository == null && fileRepository == null ) {
					if(IdatrixPropertyUtil.getBooleanProperty("idatrix.database.enable", false)&&"database".equalsIgnoreCase(IdatrixPropertyUtil.getProperty("idatrix.file.info.store", "database"))) {
						//数据库存储方式
						databaseRepository =  CloudDatabaseRepository.getInstance();
					}else {
						//文件存储方式
						fileRepository=  CloudFileRepository.getInstance();
					}
				}
			}
		}
		if( databaseRepository != null ) {
			return true ;
		}else {
			return false ;
		}
	}
	
	//=================================================== common method =====================================================================

	
	public static RepositoryDirectoryInterface createDir(String parent, String name) throws KettleException {
		 return CloudFileRepository.getInstance().createDir(parent, name);
	}
	/**
	 * 获取当前租户下的所有用户 <br>
	 * type 为空 , 获取所有用户
	 * @return
	 * @throws Exception
	 */
	public  static List<String> getCurrentRenterUsers(RepositoryObjectType type ) throws Exception{
		if( initDatebaseRepository() ) {
			//数据库方式
			if(type == null ) {
				return CloudDatabaseRepository.getRenterUsers(null);
			}else if( RepositoryObjectType.JOB.equals(type) ){
				return databaseRepository.jobResosi.getUsers();
			}else if( RepositoryObjectType.TRANSFORMATION.equals(type) ) {
				return databaseRepository.transResosi.getUsers();
			}
			return fileRepository.getRenterUsers(null,type) ;
		}else {
			//查询文件目录方式
			return fileRepository.getRenterUsers(null,type) ;
		}
	}
	
	/**
	 * 获取当前系统所有租户下的所有用户
	 * @return
	 * @throws Exception
	 */
	public  static Map<String,List<String>> getSystemRenterUsers( ) throws Exception{
		if( initDatebaseRepository() ) {
			//数据库方式
			return CloudDatabaseRepository.getRenterUsersMap();
		}else {
			//查询文件目录方式
			return fileRepository.getRenterUsersMap() ;
		}
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
		if( initDatebaseRepository() ) {
			//数据库方式
			return CloudDatabaseRepository.getTaskTotal(type,flag);
		}else {
			//查询文件目录方式
			return 0L ;
		}
	}
	
	/**
	 * 按月份统计  当前租户下 每个月有多少 任务
	 * @param type ,任务类型,调度/转换 , 可为空,为空时返回两者总量
	 * @param flag year/month/day/yyyy-MM-dd , 为空:忽略日期 , 否则 当年/当月/当天/具体某天某月某年
	 * @return
	 * @throws Exception
	 */
	public static  List<TaskMonthTotal> getTaskTotalByMonth( RepositoryObjectType type,String flag ) throws Exception {
		if( initDatebaseRepository() ) {
			//数据库方式
			return CloudDatabaseRepository.getTaskTotalByMonth(type, flag);
		}else {
			//查询文件目录方式
			return Lists.newArrayList() ;
		}
	}

	
	//=================================================== method =====================================================================
	
	/**
	 * 获取转换的 id
	 * @param owner
	 * @param transName
	 * @param group
	 * @return
	 * @throws Exception
	 */
	public static ObjectId getTransObjectId( String owner ,String transName ,String group) throws Exception {
		
		if( initDatebaseRepository() ) {
			//数据库方式
			return new StringObjectId(databaseRepository.transResosi.getAttribute(owner, transName, "OBJECTID"));
		}else {
			//查询文件目录方式
			return fileRepository.getRepositoryTransObjectId(owner, transName, null, group) ;
		}
	}
	
	/**
	 * 获取 调度的 id
	 * @param owner
	 * @param jobName
	 * @param group
	 * @return
	 * @throws Exception
	 */
	public static ObjectId getJobObjectId( String owner ,String jobName ,String group) throws Exception {
		
		if( initDatebaseRepository() ) {
			//数据库方式
			return new StringObjectId(databaseRepository.jobResosi.getAttribute(owner, jobName, "OBJECTID"));
		}else {
			//查询文件目录方式
			return fileRepository.getRepositoryJobObjectId(owner, jobName, null, group);
		}
	}
	
	/**
	 * 查询 转换 相关信息
	 * @param owner
	 * @param transName
	 * @param group
	 * @return  数据库时 是   FileRepositoryDto  ,文件时 是  RepositoryObjectInterface
	 * @throws Exception
	 */
	public static Object getTransRepositoryInfo( String owner , String transName , String group) throws Exception {
		
		if( initDatebaseRepository() ) {
			//数据库方式
			return databaseRepository.transResosi.getRepositoryInfo(owner, transName );
		}else {
			//查询文件目录方式
			return fileRepository.findTransRepositoryInfo(owner, transName, group);
		}
	}
	
	/**
	 * 查询 调度 相关信息
	 * @param owner
	 * @param jobName
	 * @param group
	 * @return  数据库时 是   FileRepositoryDto  ,文件时 是  RepositoryObjectInterface
	 * @throws Exception
	 */
	public static Object getJobRepositoryInfo( String owner , String jobName , String group) throws Exception {
		
		if( initDatebaseRepository() ) {
			//数据库方式
			return databaseRepository.jobResosi.getRepositoryInfo(owner, jobName );
		}else {
			//查询文件目录方式
			return fileRepository.findJobRepositoryInfo(owner, jobName, group);
		}
	}
	
	/**
	 * 获取转换 用户下的组列表
	 * @param owner
	 * @return
	 * @throws Exception
	 */
	public  static List<String> getTransGroups( String owner ) throws Exception{
		if( initDatebaseRepository() ) {
			//数据库方式
			return databaseRepository.transResosi.getUsersGroup(owner);
		}else {
			//查询文件目录方式
			return fileRepository.getTransGroups(owner) ;
		}
	}
	
	
	/**
	 * 获取 调度 用户下的组列表
	 * @return
	 * @throws Exception
	 */
	public  static List<String> getJobGroups( String owner ) throws Exception{
		
		if( initDatebaseRepository() ) {
			//数据库方式
			return databaseRepository.jobResosi.getUsersGroup(owner);
		}else {
			//查询文件目录方式
			return fileRepository.getJobGroups(owner) ;
		}
	}

	
	/**
	 * 获取转换 用户下的组列表
	 * @param owner
	 * @return
	 * @throws Exception
	 */
	public  static String getTransGroup( String owner,String transName,String... priorityGroups ) throws Exception{
		if( initDatebaseRepository() ) {
			//数据库方式
			return databaseRepository.transResosi.getAttribute(owner, transName, "GROUP");
		}else {
			//查询文件目录方式
			return fileRepository.getTransGroup(owner, transName, priorityGroups);
		}
	}
	
	
	/**
	 * 获取 调度 用户下的组列表
	 * @return
	 * @throws Exception
	 */
	public  static String getJobGroup( String owner, String jobName,String... priorityGroups) throws Exception{
		
		if( initDatebaseRepository() ) {
			//数据库方式
			return databaseRepository.jobResosi.getAttribute(owner, jobName, "GROUP");
		}else {
			//查询文件目录方式
			return fileRepository.getJobGroup(owner, jobName, priorityGroups);
		}
	}
	
	/**
	 * 获取  用户下的 转换名称列表  <br>
	 * 有租户特权 : owner为空,获取租户下所有的转换名称列表  . owner不为空,获取当前越权用户转换名称列表  <br>
	 * 无租户特权:  owner为空或者owner为当前登录者,返回当前登陆者转换名称列表 
	 * @param owner
	 * @param group
	 * @return
	 * @throws Exception
	 */
	public static  Map<String,List<String>> getTransNameMap( String owner ,String group) throws Exception {

		if( initDatebaseRepository() ) {
			//数据库方式
			return databaseRepository.transResosi.getNamesMap(owner,group);
		}else {
			//查询文件目录方式
			return fileRepository.getTransNameMap(owner,group) ;
		}
	}
	
	/**
	 * 获取  用户下的 调度名称列表  <br>
	 * 有租户特权 : owner为空,获取租户下所有的调度名称列表  . owner不为空,获取当前越权用户调度名称列表  <br>
	 * 无租户特权:  owner为空或者owner为当前登录者,返回当前登陆者调度名称列表 
	 * @param owner
	 * @param group
	 * @return
	 * @throws Exception
	 */
	public static  Map<String,List<String>> getJobNameMap( String owner ,String group) throws Exception {

		if( initDatebaseRepository() ) {
			//数据库方式
			return databaseRepository.jobResosi.getNamesMap(owner, group);
		}else {
			//查询文件目录方式
			return fileRepository.getJobNameMap(owner,group) ;
		}
	}

	/**
	 * 获取 用户 下所有的转换列表信息 <br>
	 * 有租户特权 : owner为空,获取租户下所有的转换列表  . owner不为空,获取当前越权转换列表  <br>
	 * 无租户特权:  owner为空或者owner为当前登录者,返回当前转换列表  <br>
	 * @param owner
	 * @param group
	 * @return   List&lt?> 数据库时 是  List&ltFileRepositoryDto> ,文件时 是  List&ltRepositoryElementMetaInterface> 
	 * @throws Exception
	 */
	public static  Map<String,List<Object>> getTransElementsMap( String owner ,String group) throws Exception {
		
		if( initDatebaseRepository() ) {
			//数据库方式
			return databaseRepository.transResosi.getRepositoryInfoMap(owner, group);
		}else {
			//查询文件目录方式
			return fileRepository.getTransElementsMap(owner, group, true) ;
		}
	}
	
	
	/**
	 * 获取 用户 下所有的调度列表信息 <br>
	 * 有租户特权 : owner为空,获取租户下所有的调度列表  . owner不为空,获取当前越权调度列表  <br>
	 * 无租户特权:  owner为空或者owner为当前登录者,返回当前调度列表  <br>
	 * @param owner
	 * @param group
	 * @return   List&lt?> 数据库时 是  List&ltFileRepositoryDto> ,文件时 是  List&ltRepositoryElementMetaInterface> 
	 * @throws Exception
	 */
	public static  Map<String,List<Object>> getJobElementsMap( String owner ,String group) throws Exception {
		
		if( initDatebaseRepository() ) {
			//数据库方式
			return databaseRepository.jobResosi.getRepositoryInfoMap(owner, group);
		}else {
			//查询文件目录方式
			return fileRepository.getJobElementsMap(owner, group, true) ;
		}
	}
	

	/**
	 * 获取 用户 下分页的转换列表信息 ,数据库信息方式有数据,否则返回空<br>
	 * 有租户特权 : owner为空,获取租户下所有的转换列表  . owner不为空,获取当前越权转换列表  <br>
	 * 无租户特权:  owner为空或者owner为当前登录者,返回当前转换列表  <br>
	 * @param owner
	 * @param group
	 * @param pageNo
	 * @param pageSize
	 * @param isMap
	 * @return
	 * @throws Exception
	 */
	public static Map<String,PaginationDto<FileRepositoryDto>> getTransElementsMap( String owner ,String group,Integer pageNo ,Integer pageSize,String search ,boolean isMap) throws Exception {
		
		if( initDatebaseRepository() ) {
			//数据库方式
			return  databaseRepository.transResosi.getPaginationInfoMap(owner,group,  pageNo, pageSize,search, isMap);
		}else {
			//查询文件目录方式
			return null ;
		}
	}
	
	/**
	 * 获取 用户 下分页的调度列表信息 ,数据库信息方式有数据,否则返回空<br>
	 * 有租户特权 : owner为空,获取租户下所有的调度列表  . owner不为空,获取当前越权调度列表  <br>
	 * 无租户特权:  owner为空或者owner为当前登录者,返回当前调度列表  <br>
	 * @param owner
	 * @param group
	 * @param pageNo
	 * @param pageSize
	 * @param isMap
	 * @return
	 * @throws Exception
	 */
	public static Map<String,PaginationDto<FileRepositoryDto>> getJobElementsMap( String owner ,String group,Integer pageNo ,Integer pageSize, String search, boolean isMap) throws Exception {
		
		if( initDatebaseRepository() ) {
			//数据库方式
			return  databaseRepository.jobResosi.getPaginationInfoMap(owner,group,  pageNo, pageSize, search, isMap);
		}else {
			//查询文件目录方式
			return null ;
		}
	}
	
	/**
	 * 加载 转换 meta
	 * @param owner
	 * @param transName
	 * @param group
	 * @return
	 * @throws Exception
	 */
	public static TransMeta loadTransByName( String owner ,String transName,String group) throws Exception {
		
		if( initDatebaseRepository() ) {
			//数据库方式
			return (TransMeta) databaseRepository.transResosi.loadByName(owner, transName, group);
		}else {
			//查询文件目录方式
			return fileRepository.loadTransByName(owner, transName, group);
		}
	}

	/**
	 * 加载  调度meta
	 * @param owner
	 * @param jobName
	 * @param group
	 * @return
	 * @throws Exception
	 */
	public static JobMeta loadJobByName( String owner ,String jobName,String group) throws Exception {
		
		if( initDatebaseRepository() ) {
			//数据库方式
			return (JobMeta) databaseRepository.jobResosi.loadByName(owner, jobName, group);
		}else {
			//查询文件目录方式
			return fileRepository.loadJobByName(owner, jobName, group);
		}
	}

	/**
	 * 检查 转换名称是否存在
	 * @param owner
	 * @param transName
	 * @return
	 * @throws Exception
	 */
	public static boolean checkTransName( String owner ,String transName) throws Exception {
		
		if( initDatebaseRepository() ) {
			//数据库方式
			return  databaseRepository.transResosi.checkName(owner, transName);
		}else {
			//查询文件目录方式
			return fileRepository.checkTransName(owner, transName);
		}
	}
	
	/**
	 *  检查 调度名称是否存在
	 * @param owner
	 * @param jobName
	 * @return
	 * @throws Exception
	 */
	public static boolean checkJobName( String owner ,String jobName ) throws Exception {
		
		if( initDatebaseRepository() ) {
			//数据库方式
			return databaseRepository.jobResosi.checkName(owner, jobName);
		}else {
			//查询文件目录方式
			return fileRepository.checkJobName(owner, jobName);
		}
	}
	

	/**
	 * 创建 转换
	 * @param owner
	 * @param transName
	 * @param group
	 * @return
	 * @throws Exception
	 */
	public static TransMeta createTrans( String owner ,String transName,String group) throws Exception {

		if( initDatebaseRepository() ) {
			//数据库方式
			return (TransMeta) databaseRepository.transResosi.create(owner, transName,group);
		}else {
			//查询文件目录方式
			return fileRepository.createTrans(owner, transName, group);
		}
		
	}
	
	/**
	 * 创建 调度
	 * @param owner
	 * @param jobName
	 * @param group
	 * @return
	 * @throws Exception
	 */
	public static JobMeta createJob( String owner ,String jobName,String group) throws Exception {

		if( initDatebaseRepository() ) {
			//数据库方式
			return (JobMeta) databaseRepository.jobResosi.create(owner, jobName,group);
		}else {
			//查询文件目录方式
			return fileRepository.createJob(owner, jobName, group);
		}
		
	}

	/**
	 * 删除转换
	 * @param owner
	 * @param transName
	 * @param group
	 * @throws Exception
	 */
	public static void dropTrans( String owner ,String transName,String group) throws Exception {
		
		if( initDatebaseRepository() ) {
			//数据库方式
			databaseRepository.transResosi.drop(owner, transName, group);
		}else {
			//查询文件目录方式
			fileRepository.dropTrans(owner, transName, group);
		}
	}
	
	/**
	 * 删除 调度
	 * @param owner
	 * @param jobName
	 * @param group
	 * @throws Exception
	 */
	public static void dropJob( String owner ,String jobName,String group) throws Exception {
		
		if( initDatebaseRepository() ) {
			//数据库方式
			databaseRepository.jobResosi.drop(owner, jobName, group);
		}else {
			//查询文件目录方式
			fileRepository.dropJob(owner, jobName, group);
		}
	}

	/**
	 * 保存 转换
	 * @param transMeta
	 * @throws Exception
	 */
	public static void saveTrans( TransMeta transMeta,String owner,String group) throws Exception {
		
		if( initDatebaseRepository() ) {
			//数据库方式
			databaseRepository.transResosi.save(transMeta,owner,group);
		}else {
			//查询文件目录方式
			fileRepository.saveTrans(transMeta);
		}
	}
	
	/**
	 * 保存 调度
	 * @param jobMeta
	 * @throws Exception
	 */
	public static void saveJob( JobMeta jobMeta,String owner,String group) throws Exception {
		
		if( initDatebaseRepository() ) {
			//数据库方式
			databaseRepository.jobResosi.save(jobMeta,owner,group);
		}else {
			//查询文件目录方式
			fileRepository.saveJob(jobMeta);
		}
	}


}
