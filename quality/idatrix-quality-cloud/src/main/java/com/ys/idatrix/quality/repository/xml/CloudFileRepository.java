/**
 * 
 */
package com.ys.idatrix.quality.repository.xml;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.util.IdatrixPropertyUtil;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.RepositoryDirectory;
import org.pentaho.di.repository.RepositoryDirectoryInterface;
import org.pentaho.di.repository.RepositoryElementMetaInterface;
import org.pentaho.di.repository.RepositoryObject;
import org.pentaho.di.repository.RepositoryObjectInterface;
import org.pentaho.di.repository.RepositoryObjectType;
import org.pentaho.di.repository.RepositorySecurityProvider;
import org.pentaho.di.repository.StringObjectId;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ys.idatrix.quality.def.CloudMessage;
import com.ys.idatrix.quality.ext.CloudApp;
import com.ys.idatrix.quality.ext.CloudSession;

/**
 * Cloud repository implementation,
 *	- for transformation (ktr) and jobs (kjb) storage.
 *
 * @author JW
 * @since 05-12-2017
 * 
 */
public class CloudFileRepository {

	public final String DEFAULT_GROUP_NAME = "default";
	public final String ALL_GROUP_NAME = "all";
	
	
	private CloudFileRepository( ) {
	}
	
	private static CloudFileRepository resosi ;
	public static CloudFileRepository getInstance( ) {
		if (resosi == null) {
			synchronized (CloudFileRepository.class) {
				if (resosi == null) {
					resosi = new CloudFileRepository();
				}
			}
		}
		return resosi;
	}
	
	public ObjectId getRepositoryObjectId(String path) throws KettleException {

		RepositoryDirectoryInterface dirobj = getRootDirectory().findDirectory(path);
		if( dirobj != null ) {
			return dirobj.getObjectId();
		}
		return new StringObjectId(path);
	}
	
	public volatile RepositoryDirectoryInterface rootDirectory ;
	public RepositoryDirectoryInterface getRootDirectory() throws KettleException {
		 if(rootDirectory ==  null ) {
			  synchronized (CloudFileRepository.class) {
				  if(rootDirectory ==  null ) {
					  Repository repository = CloudApp.getInstance().getRepository();
					  rootDirectory = repository.loadRepositoryDirectoryTree();
				 }
			  }
		  }
		return rootDirectory ;
	}

	/**
	 * Create sub directory in repository.
	 * @param parent
	 * @param name
	 * @throws KettleException
	 * @throws IOException
	 */
	public RepositoryDirectoryInterface createDir(String parent, String name) throws KettleException {
		Repository repository = CloudApp.getInstance().getRepository();
		
		RepositoryDirectoryInterface newdir = getRootDirectory().findDirectory(parent + name);
		//a
		if (newdir == null) {
			RepositoryDirectoryInterface path = getRootDirectory().findDirectory(parent);
			if(path ==  null && parent.contains(CloudApp.defaut_userId)) {
				path = new RepositoryDirectory();
				path.setObjectId( new StringObjectId(parent ) );
			}
			newdir = repository.createRepositoryDirectory(path, name);
			//刷新缓存根目录  
			RepositoryDirectoryInterface rootDirectoryOld = rootDirectory;
			rootDirectory = null;
			rootDirectory =  getRootDirectory() ;
			//针对 rootDirectory.findDirectory(CloudApp.getInstance().transRepositoryPath(group)) 这种方式时 新建目录,上下文不会更新会造成使用旧的根目录信息造成错误
			rootDirectoryOld.setChildren(rootDirectory.getChildren());
			rootDirectoryOld = null ;
			
		}
		
		return newdir;
	}

	
	/**
	 * Get a list of group names of trans in repository.
	 * @return
	 * @throws KettleException
	 */
	public  List<String> getRenterUsers( String renterId ,RepositoryObjectType type) throws KettleException{
		Repository repository = CloudApp.getInstance().getRepository();
		String[] elements = repository.getDirectoryNames(getRepositoryObjectId(CloudApp.getInstance().getRenterIdRepositoryPath(renterId)));
		if(elements == null || elements.length == 0 ) {
			elements = new String[] {};
		}
		ArrayList<String> res = Lists.newArrayList(elements);
		res.remove(CloudApp.defaut_userId);
		return res ;
	}
	
	
	public  Map<String,List<String>> getRenterUsersMap( ) throws Exception{

		Map<String,List<String>> result = Maps.newHashMap() ;
		
		RepositoryDirectoryInterface root = getRootDirectory();
		if( root != null && root.getChildren() != null && !root.getChildren().isEmpty()) {
			
			boolean isRenterEnable = false ;
			if(!CloudApp.getInstance().getRenterIdRepositoryPath(null).equals(CloudApp.SEPARATOR)) {
				isRenterEnable = true ;
			}else {
				result.put("0", Lists.newArrayList() ) ;
			}
			
			for( RepositoryDirectoryInterface dir : root.getChildren()) {
				if(dir.getName().equalsIgnoreCase(CloudApp.defaut_userId)) {
					continue ;
				}
				if( isRenterEnable ) {
					String renterId = dir.getName() ;
					result.put(renterId , getRenterUsers(renterId,null) ) ;
				}else {
					result.get("0").add(dir.getName());
				}
			}
		}
		return result ;
	}
	
	
	//===================================================trans=====================================================================
	
	public ObjectId getRepositoryTransObjectId( String owner , String transName ,String stepName,String group) throws KettleException {
		if(Utils.isEmpty(transName)) {
			return null ;
		}
		Repository repository = CloudApp.getInstance().getRepository();
		
		if(Utils.isEmpty(group)) {
			group = getTransGroup(owner , transName, DEFAULT_GROUP_NAME);
		}
		RepositoryDirectoryInterface path = getRootDirectory().findDirectory(CloudApp.getInstance().getUserTransRepositoryPath(owner , group));
		ObjectId id = repository.getTransformationID(transName, path);
		if( id ==null ) {
			throw new KettleException(CloudMessage.get("Repository.Trans.NotExists", transName));
		}
		if(Utils.isEmpty(stepName)) {
			return id ;
		}
			
		TransMeta transMeta = repository.loadTransformation(id, null);
		StepMeta step = transMeta.findStep(stepName);
		if(step == null ) {
			throw new KettleException(stepName+" 不存在.");
		}
		return  step.getObjectId() ;
	}
	
	/**
	 *  find trans info in repository. <br>
	 *  dir : RepositoryDirectoryInterface <br>
	 *  tans : RepositoryObject <br>
	 * @param transName
	 * @param group
	 * @return
	 * @throws KettleException
	 * @throws IOException
	 */
	public RepositoryObjectInterface findTransRepositoryInfo( String owner , String transName , String group) throws KettleException {
		Repository repository = CloudApp.getInstance().getRepository();
		
		if(Utils.isEmpty(transName) && !Utils.isEmpty(group)) {
			return getRootDirectory().findDirectory(CloudApp.getInstance().getUserTransRepositoryPath(owner ,group));
		}
		if(!Utils.isEmpty(transName ) ) {
			ObjectId objectId = getRepositoryTransObjectId(owner , transName, null, group);
			if(objectId != null) {
				return  repository.getObjectInformation(objectId, RepositoryObjectType.TRANSFORMATION);
			}
		}
		return null;
	}
	


	/**
	 * Get a list of group names of trans in repository.
	 * @return
	 * @throws KettleException
	 */
	public  List<String> getTransGroups( String owner ) throws KettleException{
		Repository repository = CloudApp.getInstance().getRepository();
		String[] elements = repository.getDirectoryNames(getRepositoryObjectId(CloudApp.getInstance().getUserTransRepositoryPath(owner, ALL_GROUP_NAME)));
		if(elements == null || elements.length == 0 ) {
			elements = new String[] {DEFAULT_GROUP_NAME};
		}
		return Lists.newArrayList(elements);
	}
	
	
	public  Map<String,List<String>> getTransNameMap( String owner ,String group) throws KettleException {
		
		 Map<String,List<String>> result =  Maps.newHashMap() ;
		
		Map<String, List<Object>> elementsMap = getTransElementsMap(owner, group,false);
		if(elementsMap != null && elementsMap.size() >0) {
			elementsMap.entrySet().forEach(entry -> {
				if( entry.getValue() != null && entry.getValue().size() >0) {
					result.put(entry.getKey(),	entry.getValue().stream().sorted(new Comparator<Object>(){
						@Override
						public int compare(Object o1, Object o2) {
							return ((RepositoryElementMetaInterface)o2 ).getModifiedDate().compareTo(((RepositoryElementMetaInterface)o1).getModifiedDate());
						}}).map(e ->{
							 return ((RepositoryElementMetaInterface)e).getName();
						}).collect(Collectors.toList()) ) ;
				}else {
					result.put(entry.getKey(),  new ArrayList<>() );
				}
			});
		}
		return result ;
	}

	/**
	 * 
	 * @param owner
	 * @param group
	 * @param isSort
	 * @return  List&lt?> 是  List&ltRepositoryElementMetaInterface> 
	 * @throws KettleException
	 */
	public Map<String,List<Object>> getTransElementsMap( String owner ,String group,boolean isSort) throws KettleException {
		 Map<String,List<Object>> result =  Maps.newHashMap() ;
		if( CloudSession.isPrivilegeEnable()) {
			//当前登录者是租户,获取 owner用户下的 group组 下的信息
			if(Utils.isEmpty(owner)) {
				//获取所有的用户
				for(String ownerUser : getRenterUsers( null,null)) {
					List<Object> list = getTransElementsList(ownerUser, group, isSort) ;
					result.put(ownerUser, list );
				}
			}else {
				//获取owner 用户
				result.put(owner,  getTransElementsList(owner, group, isSort) );
			}
		}else if( Utils.isEmpty(owner) || owner.equals(CloudSession.getLoginUser())){
			//当前不是租户 或者 系统不开启租户超级权限,只能获取当前登录用户自己的
			result.put(CloudSession.getLoginUser(),  getTransElementsList(CloudSession.getLoginUser(), group, isSort) );
		}
		
		return result ;
	}
	
	/**
	 * Get a list of Elements of transformations in repository.
	 * @return
	 * @throws KettleException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<Object> getTransElementsList( String owner ,String group,boolean isSort) throws KettleException {
		List  transElementsList = new ArrayList<>();
		Repository repository = CloudApp.getInstance().getRepository();
		if(Utils.isEmpty(group)) {
			group = IdatrixPropertyUtil.getProperty("idatrix.default.group.name", DEFAULT_GROUP_NAME);
		}
		if(ALL_GROUP_NAME.equalsIgnoreCase(group)) {
			//获取所有的数据
			 String[] elements = repository.getDirectoryNames(getRepositoryObjectId(CloudApp.getInstance().getUserTransRepositoryPath(owner, ALL_GROUP_NAME)));
			if(elements != null) {
				for(String e : elements) {
					transElementsList.addAll(getTransElementsList(owner, e,false));
				}
			}
		}else {
			transElementsList = repository.getTransformationObjects(getRepositoryObjectId(CloudApp.getInstance().getUserTransRepositoryPath(owner , group)), false);
		}
	
		if(isSort && transElementsList != null && transElementsList.size() >0) {
			return (List<Object>) transElementsList.stream().sorted(new Comparator<Object>(){
				@Override
				public int compare(Object o1, Object o2) {
					return ((RepositoryElementMetaInterface)o2).getModifiedDate().compareTo(((RepositoryElementMetaInterface)o1).getModifiedDate());
				}}).collect(Collectors.toList());
		}
		return transElementsList;
	}

	/**
	 * Load transformation meta from repository by given name.
	 * @param transName
	 * @return
	 * @throws Exception
	 */
	public TransMeta loadTransByName( String owner ,String transName,String group) throws Exception {
		Repository repository = CloudApp.getInstance().getRepository();
		if(Utils.isEmpty(group)) {
			group = getTransGroup(owner ,transName, DEFAULT_GROUP_NAME);
		}
		RepositoryDirectoryInterface path = getRootDirectory().findDirectory(CloudApp.getInstance().getUserTransRepositoryPath(owner, group));
		ObjectId id = repository.getTransformationID(transName, path);
		if( id ==null ) {
			throw new KettleException(CloudMessage.get("Repository.Trans.NotExists", transName));
		}
		TransMeta transMeta = repository.loadTransformation(id, null);
		RepositoryObject repositoryObject = repository.getObjectInformation(id, RepositoryObjectType.TRANSFORMATION);
		transMeta.setRepositoryDirectory(repositoryObject.getRepositoryDirectory());

		transMeta.setMaxUndo(Const.MAX_UNDO);
		return transMeta;
	}


	/**
	 * Check if transformation name exists.
	 * @param transName
	 * @return
	 * @throws KettleException
	 */
	public boolean checkTransName( String owner ,String transName) throws KettleException {
		Repository repository = CloudApp.getInstance().getRepository();
		//获取所有的数据
		String[] elements = repository.getDirectoryNames(getRepositoryObjectId(CloudApp.getInstance().getUserTransRepositoryPath(owner, ALL_GROUP_NAME)));
		if(elements != null) {
			for(String e : elements) {
				RepositoryDirectoryInterface path = getRootDirectory().findDirectory( CloudApp.getInstance().getUserTransRepositoryPath(owner, e) );
				if(repository.exists(transName, path, RepositoryObjectType.TRANSFORMATION)) {
					return true;
				}
			}
		}

		return false;
	}
	
	/**
	 * get transformation group.
	 * @param transName
	 * @param priorityGroups
	 * @return
	 * @throws KettleException
	 */
	public String getTransGroup( String owner ,String transName,String... priorityGroups) throws KettleException { 
		if(Utils.isEmpty(transName)) {
			throw new KettleException("transName 不能为空.");
		}
		
		Repository repository = CloudApp.getInstance().getRepository();
		List<String> pgList = null;
		if(priorityGroups  != null) {
			for(String gn : priorityGroups) {
				RepositoryDirectoryInterface path = getRootDirectory().findDirectory( CloudApp.getInstance().getUserTransRepositoryPath(owner , gn) );
				if(repository.exists(transName, path, RepositoryObjectType.TRANSFORMATION)) {
					return gn;
				}
			}
			pgList = Arrays.asList(priorityGroups);
		}
		//搜索所有的组
		String[] elements = repository.getDirectoryNames(getRepositoryObjectId(CloudApp.getInstance().getUserTransRepositoryPath(owner, ALL_GROUP_NAME)));
		if(elements != null) {
			for(String e : elements) {
				if(pgList != null && pgList.contains(e)) {
					//已经搜索过
					continue;
				}
				RepositoryDirectoryInterface path = getRootDirectory().findDirectory( CloudApp.getInstance().getUserTransRepositoryPath( owner , e) );
				if(repository.exists(transName, path, RepositoryObjectType.TRANSFORMATION)) {
					return e;
				}
			}
		}

		throw new KettleException(transName+" 不存在(组名为空).");
	}

	/**
	 * Create a new transformation by given name.
	 * @param transName
	 * @return
	 * @throws KettleException
	 * @throws IOException
	 */
	public TransMeta createTrans( String owner ,String transName,String group) throws Exception {
		Repository repository = CloudApp.getInstance().getRepository();
		RepositoryDirectoryInterface path = getRootDirectory().findDirectory( CloudApp.getInstance().getUserTransRepositoryPath(owner, group) );

		if(repository.exists(transName, path, RepositoryObjectType.TRANSFORMATION)) {
			throw new KettleException(CloudMessage.get("Repository.Trans.Existed", transName));
		}

		TransMeta transMeta = new TransMeta();
		transMeta.setRepository(CloudApp.getInstance().getRepository());
		transMeta.setMetaStore(CloudApp.getInstance().getMetaStore(Const.NVL(owner, CloudSession.getResourceUser())));
		transMeta.setName(transName);
		transMeta.setRepositoryDirectory(path);

		repository.save(transMeta, "add: " + new Date(), null);

		return transMeta;
	}

	/**
	 * Drop a transformation from repository.
	 * @param transName
	 * @throws KettleException
	 * @throws IOException
	 */
	public void dropTrans( String owner ,String transName,String group) throws KettleException {
		Repository repository = CloudApp.getInstance().getRepository();
		if(Utils.isEmpty(group)) {
			group = getTransGroup(owner ,transName, DEFAULT_GROUP_NAME);
		}
		RepositoryDirectoryInterface path = getRootDirectory().findDirectory( CloudApp.getInstance().getUserTransRepositoryPath(owner, group) );
		ObjectId id = repository.getTransformationID(transName, path);
		if( id ==null ) {
			throw new KettleException(CloudMessage.get("Repository.Trans.NotExists", transName));
		}
		repository.deleteTransformation(id);
	}

	/**
	 * Save transformation into repository.
	 * @param transMeta
	 * @throws KettleException
	 */
	public void saveTrans( TransMeta transMeta) throws KettleException {
		Repository repository = CloudApp.getInstance().getRepository();
		ObjectId existingId = repository.getTransformationID( transMeta.getName(), transMeta.getRepositoryDirectory() );
		if(transMeta.getCreatedDate() == null)
			transMeta.setCreatedDate(new Date());
		if(transMeta.getObjectId() == null)
			transMeta.setObjectId(existingId);
		transMeta.setModifiedDate(new Date());

		boolean versioningEnabled = true;
		boolean versionCommentsEnabled = true;
		String fullPath = transMeta.getRepositoryDirectory() + "/" + transMeta.getName() + transMeta.getRepositoryElementType().getExtension(); 
		RepositorySecurityProvider repositorySecurityProvider = repository.getSecurityProvider() != null ? repository.getSecurityProvider() : null;
		if ( repositorySecurityProvider != null ) {
			versioningEnabled = repositorySecurityProvider.isVersioningEnabled( fullPath );
			versionCommentsEnabled = repositorySecurityProvider.allowsVersionComments( fullPath );
		}
		String versionComment = null;
		if (!versioningEnabled || !versionCommentsEnabled) {
			versionComment = "";
		} else {
			versionComment = "no comment";
		}

		repository.save( transMeta, versionComment, null);
	}

	//===================================================jobs=====================================================================
	
	
	public ObjectId getRepositoryJobObjectId( String owner ,String jobName ,String entryName,String group) throws KettleException {
		if(Utils.isEmpty(jobName)) {
			return null ;
		}
		Repository repository = CloudApp.getInstance().getRepository();
		if(Utils.isEmpty(group)) {
			group = getJobGroup(owner ,jobName, DEFAULT_GROUP_NAME);
		}
		
		RepositoryDirectoryInterface path = getRootDirectory().findDirectory( CloudApp.getInstance().getUserJobsRepositoryPath(owner, group) );
		ObjectId id = repository.getJobId(jobName, path);
		if( id ==null ) {
			throw new KettleException(CloudMessage.get("Repository.Job.NotExists", jobName));
		}
		if(Utils.isEmpty(entryName)) {
			return id ;
		}
		JobMeta jobMeta = repository.loadJob(id, null);
		if(jobMeta == null ) {
			throw new KettleException(entryName+" 不存在.");
		}
		return  jobMeta.getObjectId() ;
	}
	
	
	/**
	 *  find job info in repository. <br>
	 *  dir : RepositoryDirectoryInterface <br>
	 *  tans : RepositoryObject <br>
	 * @param jobName
	 * @param group
	 * @return
	 * @throws KettleException
	 * @throws IOException
	 */
	public RepositoryObjectInterface findJobRepositoryInfo( String owner , String jobName , String group) throws KettleException {
		Repository repository = CloudApp.getInstance().getRepository();
		if(Utils.isEmpty(jobName) && !Utils.isEmpty(group)) {
			return getRootDirectory().findDirectory( CloudApp.getInstance().getUserJobsRepositoryPath(owner, group));
		}
		if(!Utils.isEmpty(jobName ) ) {
			ObjectId objectId = getRepositoryJobObjectId(owner ,jobName, null, group);
			if(objectId != null) {
				return  repository.getObjectInformation(objectId, RepositoryObjectType.JOB);
			}
		}
		return null;
		
	}
	
	
	/**
	 * Get a list of group names of jobs in repository.
	 * @return
	 * @throws KettleException
	 */
	public  List<String> getJobGroups( String owner ) throws KettleException{
		
		Repository repository = CloudApp.getInstance().getRepository();
		String[] elements = repository.getDirectoryNames(getRepositoryObjectId(CloudApp.getInstance().getUserJobsRepositoryPath(owner, ALL_GROUP_NAME)));
		
		if(elements == null || elements.length == 0 ) {
			elements = new String[] {DEFAULT_GROUP_NAME};
		}
		return Lists.newArrayList(elements);
	}

	
	public  Map<String,List<String>> getJobNameMap( String owner ,String group) throws KettleException {
		
		Map<String,List<String>> result =  Maps.newHashMap() ;
		
		Map<String, List<Object>> elementsMap = getJobElementsMap(owner, group,false);
		if(elementsMap != null && elementsMap.size() >0) {
			elementsMap.entrySet().forEach(entry -> {
				if( entry.getValue() != null && entry.getValue().size() >0) {
					result.put(entry.getKey(),	entry.getValue().stream().sorted(new Comparator<Object>(){
						@Override
						public int compare(Object o1, Object o2) {
							return ((RepositoryElementMetaInterface)o2).getModifiedDate().compareTo(((RepositoryElementMetaInterface)o1).getModifiedDate());
						}}).map(e ->{
							 return ((RepositoryElementMetaInterface)e).getName();
						}).collect(Collectors.toList()) ) ;
				}else {
					result.put(entry.getKey(),  new ArrayList<>() );
				}
			});
		}
		return result ;
	}
	
	/**
	 * 
	 * @param owner
	 * @param group
	 * @param isSort
	 * @return  List<?> 是  List&ltRepositoryElementMetaInterface> 
	 * @throws KettleException
	 */
	public Map<String,List<Object>> getJobElementsMap( String owner ,String group,boolean isSort) throws KettleException {
		 Map<String,List<Object>> result =  Maps.newHashMap() ;
		if( CloudSession.isPrivilegeEnable()) {
			//当前登录者是租户,获取 owner用户下的 group组 下的信息
			if(Utils.isEmpty(owner)) {
				//获取所有的用户
				for(String ownerUser : getRenterUsers(null,null )) {
					result.put(ownerUser,  getJobElementList(ownerUser, group, isSort) );
				}
			}else {
				//获取owner 用户
				result.put(owner,  getJobElementList(owner, group, isSort) );
			}
		}else if( Utils.isEmpty(owner) || owner.equals(CloudSession.getLoginUser())){
			//当前不是租户 或者 系统不开启租户超级权限,只能获取当前登录用户自己的
			result.put(CloudSession.getLoginUser(),  getJobElementList(CloudSession.getLoginUser(), group, isSort) );
		}
		
		return result ;
	}
	
	/**
	 * Get a list of Element of jobs in repository.
	 * @return
	 * @throws KettleException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<Object> getJobElementList( String owner , String group,boolean isSort ) throws KettleException {
		List jobElementList = new ArrayList<>();
		Repository repository = CloudApp.getInstance().getRepository();
		if(Utils.isEmpty(group)) {
			group = IdatrixPropertyUtil.getProperty("idatrix.default.group.name", DEFAULT_GROUP_NAME);
		}
		if(ALL_GROUP_NAME.equalsIgnoreCase(group)) {
			//获取所有的数据
			String[] elements = repository.getDirectoryNames(getRepositoryObjectId(CloudApp.getInstance().getUserJobsRepositoryPath(owner, ALL_GROUP_NAME)));
			if(elements != null) {
				for(String e : elements) {
					jobElementList.addAll(getJobElementList(owner ,e,false));
				}
			}
		}else {
			jobElementList = repository.getJobObjects(getRepositoryObjectId(CloudApp.getInstance().getUserJobsRepositoryPath(owner, group)), false);
		}
		
		if(isSort && jobElementList != null && jobElementList.size() >0) {
			return (List<Object>) jobElementList.stream().sorted(new Comparator<Object>(){
				@Override
				public int compare(Object o1, Object o2) {
					return ((RepositoryElementMetaInterface)o2).getModifiedDate().compareTo(((RepositoryElementMetaInterface)o1).getModifiedDate());
				}}).collect(Collectors.toList());
		}
		
		return jobElementList;
	}

	/**
	 * Load job meta from repository by given name.
	 * @param jobName
	 * @return
	 * @throws Exception
	 */
	public JobMeta loadJobByName( String owner ,String jobName, String group ) throws Exception {
		Repository repository = CloudApp.getInstance().getRepository();
		if(Utils.isEmpty(group)) {
			group = getJobGroup(owner ,jobName, DEFAULT_GROUP_NAME);
		}
		
		RepositoryDirectoryInterface path = getRootDirectory().findDirectory( CloudApp.getInstance().getUserJobsRepositoryPath(owner, group));
		ObjectId id = repository.getJobId(jobName, path);
		if( id ==null ) {
			throw new KettleException(CloudMessage.get("Repository.Job.NotExists", jobName));
		}
		JobMeta jobMeta = repository.loadJob(id, null);
		RepositoryObject repositoryObject = repository.getObjectInformation(id, RepositoryObjectType.JOB);
		jobMeta.setRepositoryDirectory(repositoryObject.getRepositoryDirectory());

		return jobMeta;
	}


	/**
	 * Check if job name exists.
	 * @param jobName
	 * @return
	 * @throws KettleException
	 */
	public boolean checkJobName( String owner ,String jobName ) throws KettleException {
		Repository repository = CloudApp.getInstance().getRepository();
		//获取所有的数据
		String[] elements = repository.getDirectoryNames(getRepositoryObjectId(CloudApp.getInstance().getUserJobsRepositoryPath(owner, ALL_GROUP_NAME)));
		if(elements != null) {
			for(String e : elements) {
				RepositoryDirectoryInterface path = getRootDirectory().findDirectory( CloudApp.getInstance().getUserJobsRepositoryPath(owner, e) );
				if(repository.exists(jobName, path, RepositoryObjectType.JOB)) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * get job group.
	 * @param jobName
	 * @return
	 * @throws KettleException
	 */
	public String getJobGroup( String owner , String jobName,String... priorityGroups) throws KettleException { //
		if(Utils.isEmpty(jobName)) {
			throw new KettleException("jobName 不能为空.");
		}
		Repository repository = CloudApp.getInstance().getRepository();
		List<String> pgList = null;
		if(priorityGroups != null) {
			for(String gn : priorityGroups) {
				RepositoryDirectoryInterface path = getRootDirectory().findDirectory( CloudApp.getInstance().getUserJobsRepositoryPath(owner, gn) );
				if(repository.exists(jobName, path, RepositoryObjectType.JOB)) {
					return gn;
				}
			}
			pgList = Arrays.asList(priorityGroups);
		}
		//搜索所有的组
		String[] elements = repository.getDirectoryNames(getRepositoryObjectId(CloudApp.getInstance().getUserJobsRepositoryPath(owner, ALL_GROUP_NAME)));
		if(elements != null) {
			for(String e : elements) {
				if(pgList!= null && pgList.contains(e)) {
					//已经搜索过
					continue ;
				}
				RepositoryDirectoryInterface path = getRootDirectory().findDirectory( CloudApp.getInstance().getUserJobsRepositoryPath(owner, e) );
				if(repository.exists(jobName, path, RepositoryObjectType.JOB)) {
					return e;
				}
			}
		}
		throw new KettleException(jobName+" 不存在(组名为空).");
	}

	/**
	 * Create a new job by given name.
	 * @param jobName
	 * @return
	 * @throws KettleException
	 * @throws IOException
	 */
	public JobMeta createJob( String owner ,String jobName, String group) throws Exception {
		Repository repository = CloudApp.getInstance().getRepository();
		RepositoryDirectoryInterface path = getRootDirectory().findDirectory( CloudApp.getInstance().getUserJobsRepositoryPath(owner, group) );

		if(repository.exists(jobName, path, RepositoryObjectType.JOB)) {
			throw new KettleException(CloudMessage.get("Repository.Job.Existed", jobName));
		}

		JobMeta jobMeta = new JobMeta();
		jobMeta.setRepository(CloudApp.getInstance().getRepository());
		jobMeta.setMetaStore(CloudApp.getInstance().getMetaStore(Const.NVL(owner, CloudSession.getResourceUser())));
		jobMeta.setName(jobName);
		jobMeta.setRepositoryDirectory(path);

		repository.save(jobMeta, "add: " + new Date(), null);
		return jobMeta;
	}

	/**
	 * Drop a job from repository.
	 * @param jobName
	 * @return
	 * @throws KettleException
	 * @throws IOException
	 */
	public void dropJob( String owner ,String jobName, String group) throws KettleException, IOException {
		Repository repository = CloudApp.getInstance().getRepository();
		if(Utils.isEmpty(group)) {
			group = getJobGroup(owner ,jobName, DEFAULT_GROUP_NAME);
		}
		
		RepositoryDirectoryInterface path = getRootDirectory().findDirectory( CloudApp.getInstance().getUserJobsRepositoryPath(owner, group ));
		ObjectId id = repository.getJobId(jobName, path);
		if( id ==null ) {
			throw new KettleException(CloudMessage.get("Repository.Job.NotExists", jobName));
		}
		repository.deleteJob(id);
	}

	/**
	 * Save job into repository.
	 * @param jobMeta
	 * @throws KettleException
	 */
	public void saveJob( JobMeta jobMeta) throws KettleException {
		Repository repository = CloudApp.getInstance().getRepository();
		ObjectId existingId = repository.getJobId( jobMeta.getName(), jobMeta.getRepositoryDirectory() );
		if(jobMeta.getCreatedDate() == null)
			jobMeta.setCreatedDate(new Date());
		if(jobMeta.getObjectId() == null)
			jobMeta.setObjectId(existingId);
		jobMeta.setModifiedDate(new Date());

		boolean versioningEnabled = true;
		boolean versionCommentsEnabled = true;
		String fullPath = jobMeta.getRepositoryDirectory() + "/" + jobMeta.getName() + jobMeta.getRepositoryElementType().getExtension(); 
		RepositorySecurityProvider repositorySecurityProvider = repository.getSecurityProvider() != null ? repository.getSecurityProvider() : null;
		if ( repositorySecurityProvider != null ) {
			versioningEnabled = repositorySecurityProvider.isVersioningEnabled( fullPath );
			versionCommentsEnabled = repositorySecurityProvider.allowsVersionComments( fullPath );
		}
		String versionComment = null;
		if (!versioningEnabled || !versionCommentsEnabled) {
			versionComment = "";
		} else {
			versionComment = "no comment";
		}

		repository.save( jobMeta, versionComment, null);
	}

}
