package com.ys.idatrix.quality.recovery.restart;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs2.AllFileSelector;
import org.apache.commons.vfs2.FileObject;
import org.pentaho.di.core.util.IdatrixPropertyUtil;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.core.vfs.KettleVFS;
import org.pentaho.di.repository.filerep.KettleFileRepository;

import com.google.common.collect.Lists;
import com.idatrix.unisecurity.api.domain.User;
import com.ys.idatrix.quality.ext.CloudApp;
import com.ys.idatrix.quality.ext.CloudSession;
import com.ys.idatrix.quality.ext.PluginFactory;
import com.ys.idatrix.quality.ext.utils.UnixPathUtil;
import com.ys.idatrix.quality.reference.user.CloudUserService;
import com.ys.idatrix.quality.repository.CloudRepository;
import com.ys.idatrix.quality.repository.xml.CloudFileRepository;


public class RestartScanner implements Runnable {
	
	public static final Log  logger = LogFactory.getLog(RestartScanner.class);
	
	public static RestartScanner initScanner() {
		return new RestartScanner();
	}

	@Override
	public void run() {
		
		try {
			logger.info("检查是否有任务需要重新运行(重启恢复)...");
			//是否需要迁移数据
			triggerMetadataDirDataMove();
			// 扫描日志路径获取所有用户和相关执行记录
			List<UserLogReposiDto> userLogList = scanLocalLogRepository();
			if(userLogList != null && userLogList.size() >0 ) {
				
				for( UserLogReposiDto userLog : userLogList ) {
					UserTaskRestarter userTask =  new UserTaskRestarter(userLog);
					Thread tr = new Thread(userTask, "restart_"+userLog.getUser()+"_job_and_trans");
					tr.start();
				}
			}
		} catch (Exception e) {
			logger.error( "任务重启重新运行异常:" , e);
		}
		
	}
	
	/**
	 * Get the  all History Log in all users' local repository
	 * @return
	 * @throws Exception
	 */
	private  List<UserLogReposiDto> scanLocalLogRepository() throws Exception {
		
		Map<String, List<String>> renterUsersMap = CloudFileRepository.getInstance().getRenterUsersMap();
		if(renterUsersMap == null || renterUsersMap.isEmpty() ) {
			return null;
		}
		List<UserLogReposiDto>  result = Lists.newArrayList();
		for(Entry<String, List<String>> renterUsers : renterUsersMap.entrySet() ) {
			String renterId = renterUsers.getKey() ;
			for(String userName : renterUsers.getValue()) {
				
				CloudSession.setThreadLoginUser(userName);
				CloudSession.setThreadResourceUser(userName);
				CloudSession.setThreadInfo(CloudSession.ATTR_SESSION_RENTER_ID, renterId);
				
				UserLogReposiDto ur = getUserReposi(userName,renterId);
				result.add(ur);

				//清理线程用户信息
				CloudSession.clearThreadInfo();
			}
		}

		return result;
	}
	
	private static UserLogReposiDto getUserReposi(String userName,String renterId) throws Exception {
		UserLogReposiDto ulrd =  new UserLogReposiDto();
		Map<String, List<String>> transMap =  CloudFileRepository.getInstance().getTransNameMap(userName, CloudRepository.ALL_GROUP_NAME);
		List<String> transList = transMap.get(userName);
		
		Map<String, List<String>> jobMap =  CloudFileRepository.getInstance().getJobNameMap(userName, CloudRepository.ALL_GROUP_NAME);
		List<String> jobList = jobMap.get(userName);
		
		ulrd.setRenterId(renterId);
		ulrd.setUser(userName);
		ulrd.setJobList(jobList);
		ulrd.setTransList(transList);
		return ulrd ;
	}
	
	
	public boolean  triggerMetadataDirDataMove() throws  Exception {
		String oldDir = IdatrixPropertyUtil.getProperty("idatrix.metadata.reposity.root.old");
		if (!Utils.isEmpty(oldDir)) {
			logger.info("启动  metadata 数据目录迁移...");
			CloudUserService userService = PluginFactory.getBean(CloudUserService.class);
			String resposiDir = CloudApp.getInstance().getRepositoryRootFolder();
			FileObject oldReposi = KettleVFS.getFileObject(oldDir);
			for (FileObject userDir : oldReposi.getChildren()) {
				if (CloudApp.defaut_userId.equals(userDir.getName().getBaseName())) {
					userDir.close();
					continue;
				}
				String username = userDir.getName().getBaseName();
				User userinfo = userService.getUserInfo(username);
				if (userinfo != null && userinfo.getRenterId() != null) {
					String renterId = userinfo.getRenterId().toString();
					FileObject renterDir = KettleVFS.getFileObject(UnixPathUtil.unixPath(resposiDir, renterId,username));
					renterDir.copyFrom(userDir, new AllFileSelector());
					renterDir.close();
				}
				userDir.close();
			}
			oldReposi.close();
			
			 CloudFileRepository.getInstance().rootDirectory = null ;
			 KettleFileRepository.rootDirectory = null ;
			return  true ;
		}
		return false ;
	}
	
	
}


class UserLogReposiDto{
	
	private String renterId;
	private String user;
	private List<String> transList;
	private List<String> jobList;
	
	
	
	public String getRenterId() {
		return renterId;
	}
	public void setRenterId(String renterId) {
		this.renterId = renterId;
	}
	/**
	 * @return the user
	 */
	public String getUser() {
		return user;
	}
	/**
	 * @param  设置 user
	 */
	public void setUser(String user) {
		this.user = user;
	}
	/**
	 * @return the transList
	 */
	public List<String> getTransList() {
		return transList;
	}
	/**
	 * @param  设置 transList
	 */
	public void setTransList(List<String> transList) {
		this.transList = transList;
	}
	/**
	 * @return the jobList
	 */
	public List<String> getJobList() {
		return jobList;
	}
	/**
	 * @param  设置 jobList
	 */
	public void setJobList(List<String> jobList) {
		this.jobList = jobList;
	}
	
	public void addTrans(String transName) {
		if(transList == null) {
			transList = Lists.newArrayList();
		}
		transList.add(transName);
	}
	
	
	public void addJob(String transName) {
		if(jobList == null) {
			jobList = Lists.newArrayList();
		}
		jobList.add(transName);
	}
	
}