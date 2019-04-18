/**
 * 云化数据集成系统 
 * iDatrxi CloudETL
 */
package com.ys.idatrix.cloudetl.web.controller;

import java.util.Map;
import java.util.function.BinaryOperator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.pentaho.di.core.util.Utils;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.ys.idatrix.cloudetl.ext.CloudSession;
import com.ys.idatrix.cloudetl.ext.executor.CloudExecution;
import com.ys.idatrix.cloudetl.ext.executor.CloudExecution.ExecutionInfo;

/**
 * BaseAction <br/>
 * @author JW
 * @since 2017年9月12日
 * 
 */
public class BaseAction {
	
	/**
	 * 检查权限,检查是否是管理员,只有管理员有权限
	 * @throws Exception
	 */
	public void checkManagerPrivilege() throws Exception {
		CloudSession.clearThreadInfo();
		if( !CloudSession.isSuperPrivilege() ) {
			throw new Exception("您没有管理员权限,无法操作!");
		}
	}
	
	/**
	 * 检查权限 , 管理员有全部权限, 非管理员对自己的资源有权限
	 * @throws Exception
	 */
	public void checkPrivilege() throws Exception {
		if( !CloudSession.isSuperPrivilege() ) {
			//throw new Exception("您没有权限,无法操作!");
			//没有权限时 只能操作自己的资源
			if( Utils.isEmpty(CloudSession.getCurrentResourceUser())) {
				CloudSession.setThreadResourceUser(CloudSession.getLoginUser());
			}else {
				throw new Exception("您没有权限,无法操作!");
			}
		}
	}
	
	/**
	 * 检查执行id对应的执行任务 是否有权限操作, 管理员有权限,非管理员对自己的任务有权限
	 * @param executionId
	 * @throws Exception
	 */
	public void checkExecPrivilege(String executionId) throws Exception {
		if(Utils.isEmpty(executionId)) {
			return ;
		}
		if( CloudSession.isSuperPrivilege() ) {
			//是超级管理员 
			return ;
		}
		//不是超级管理员 , 只能处理自己的 
		ExecutionInfo executorInfo = CloudExecution.getInstance().getExecutionInfo(executionId);
		if( executorInfo == null || CloudSession.getLoginUser().equals(executorInfo.owner) ) {
			//没有执行信息 或者  是本人的任务
			return ;
		}
		throw new Exception("您没有管理员权限,无法操作(任务拥有者:"+executorInfo.owner+")!");
	}
	
	
	
	
	@ModelAttribute
	public void setReqAndRes(HttpServletRequest request, HttpServletResponse response){
		// Query user information from SSO
//		SSOUser user = UserHolder.getUser();
//		String username = user != null ? (String) user.getProperty("username") : "";
//		if(!Utils.isEmpty(username)) {
//			// Store user information in current thread
//			if(!Thread.currentThread().getName().contains(username)){
//				Thread.currentThread().setName(Thread.currentThread().getName()+StringUtil.getThreadNameesSuffixByUser(username));
//			}
//		}
	}
	
	
	protected String getGroupName(String group , String transOrJobName) {
		
		if(Utils.isEmpty(group) && !Utils.isEmpty(transOrJobName) && transOrJobName.contains("/")) {
			group = transOrJobName.split("/", 2 )[0];
		}
		return group;
	}
	
	/**
	 * 只有一个参数且不是基础类型,不是String类型 的方法会自动保存资源用户(如果有),剩下的方法需要手动保存
	 * @param owner
	 */
	protected void saveResourceOwner(String owner ) {
		if(!Utils.isEmpty(owner) ) {
			CloudSession.setThreadResourceUser(owner);
		}
	}
	
	
	protected <T> T mergeMap( Map<String, T> temp ,T first , BinaryOperator<T> oper){
		 return temp.values().stream().reduce(first, oper )  ;
	}
	

}
