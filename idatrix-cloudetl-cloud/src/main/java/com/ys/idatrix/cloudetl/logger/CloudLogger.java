/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.logger;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.logging.log4j.Logger;
import org.pentaho.di.core.util.Utils;
import com.ys.idatrix.cloudetl.ext.CloudApp;
import com.ys.idatrix.cloudetl.ext.CloudSession;
import com.ys.idatrix.cloudetl.ext.utils.UnixPathUtil;

/**
 * Cloud logger implementation. <br/>
 * (Need to re-design and implement cloud ETL logger function in V01R02.)
 * 
 * @author JW
 * @since 05-12-2017
 *
 */
public class CloudLogger {
	
	public static final Log  logger = LogFactory.getLog("CloudLogger");
	
	private static final AtomicLong Num = new AtomicLong( 1 );
	private static final ThreadLocal<Long> logNumberThreadLocal = new ThreadLocal<Long>();

	private final static String LOG_DIR = "cloud/";
	private final static String LOG_NAME = "cloudetl";
	private final static String DEFAULT_LOGGER_NAME = "iDatrixCloudLog";
	private final static String LOGGER_PREFIX = "cloud_logger_for_";
	private final static String DEFAULT_LOGGER_TEXT_HEAD = "CloudETL ";
	
	private static HashMap<String, CloudLogger> userLoggers;

	private String userName;
	private Logger log;

	private CloudLogger() {
		log = CloudLoggerFactory.getLogger(DEFAULT_LOGGER_NAME);
		userName = CloudApp.defaut_userId ;
	}

	private CloudLogger(String userName , String name, String path, String filename, String extension) {
		log = CloudLoggerFactory.createLogger(name, path, filename, extension);
		this.userName = userName ;
	}
	
	private static String logPath(String username) throws Exception {
		return UnixPathUtil.unixPath(CloudApp.getInstance().getLocalRepositoryRootFolder() + CloudApp.getInstance().getUserLogsRepositoryPath(username) + LOG_DIR);
	}
	
	public synchronized static CloudLogger getInstance() {
			String username = CloudSession.getResourceUser();
			return getInstance(username);
	}

	public synchronized static CloudLogger getInstance(String username) {
		try {
			if (Utils.isEmpty(username)) {
				//return new CloudLogger();
				username = CloudApp.defaut_userId;
			}
			if (userLoggers != null && userLoggers.containsKey(username)) {
				return userLoggers.get(username);
			} else if (userLoggers == null) {
				userLoggers = new HashMap<>();
			}
			
			CloudLogger logger = new CloudLogger(username , LOGGER_PREFIX + username, logPath(username), LOG_NAME, CloudLogType.CLOUD_LOG.getExtension());
			userLoggers.put(username, logger);
			return logger;
			
		} catch (Exception e) {
			logger.error("初始化用户日志失败.",e);
			return new CloudLogger();
		}
	}

	public static String getAsUtf8(String logText) {
		try {
			return new String(logText.getBytes(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return logText;
		}
	}

	public static String logMessage2(String logHead, Object... logObjs) {
		if (logHead.contains("{}")) {
			return getAsUtf8(String.format(logHead.replaceAll("\\{\\}", "%s"), logObjs));
		}

		StringBuilder sb = new StringBuilder(logHead);
		if(logObjs != null ) {
			for (Object logObj : logObjs) {
				sb.append(" ");
				if (logObj == null) {
					sb.append("NULL");
				} if(logObj instanceof String ){
					sb.append(logObj);
				}else {
					sb.append(CloudLogUtils.jsonLog(logObj));
				}
			}
		}else {
			sb.append("<NULL>");
		}
		

		return getAsUtf8(sb.toString());
	}
	

	//################ INFO ##################
	
	public  void info(Object logHead , Object... logText ) {
		try {
			String head = getLogTextHead(logHead);
			log.info(logMessage2(head , logText ));
		} catch (Exception e) {
			logger.error("",e);
		}
	}

	//################ WARN ##################
	
	public  void warn(Object logHead ,Object... logText) {
		try {
			String head =  getLogTextHead(logHead);
			log.warn(logMessage2(head,logText));
		} catch (Exception e) {
			logger.error("",e);
		}
	}
	
	//################ ERROR ##################
	
	public  void error(Object logHead , String logText) {
		try {
			String head = getLogTextHead(logHead);
			log.error(logMessage2(head,logText));
		} catch (Exception e) {
			logger.error("",e);
		}
	}
	
	public  void error(Object logHead , String logText, Throwable t) {
		try {
			String head = getLogTextHead(logHead);
			log.error(logMessage2(head,logText), t);
		} catch (Exception e) {
			logger.error("",e);
		}
	}

	//################ DEBUG ##################
	
	public  void debug(Object logHead , Object... logText) {
		try {
			String head = getLogTextHead(logHead);
			log.debug(logMessage2(head, logText));
		} catch (Exception e) {
			logger.error("",e);
		}
	}
	
	//################ TRACE ##################
	
	public  void trace(Object logHead , Object... logText) {
		try {
			String head = getLogTextHead(logHead);
			log.trace(logMessage2(head, logText));
		} catch (Exception e) {
			logger.error("",e);
		}
	}

	public  void trace(Object logHead, String logText, Throwable t ) {
		try {
			String head = getLogTextHead(logHead);
			log.trace(logMessage2(head, logText), t);
		} catch (Exception e) {
			logger.error("",e);
		}
	}

	//################ COMMON ##################
	
	public CloudLogger addNumber() {
		logNumberThreadLocal.remove();
		long number = CloudLogger.Num.getAndIncrement();
		logNumberThreadLocal.set(number);
		
		return this ;
	}
	
	private  String getLogTextHead(Object logHead ) {
		
		StringBuffer head = new StringBuffer();
		head.append("[").append(userName).append(":") ;
		if(logHead != null) {
			if( logHead instanceof String ) {
				head.append(logHead.toString() );
			}else if( logHead instanceof Class ){
				head.append(((Class<?>)logHead).getSimpleName());
			}else {
				head.append(logHead.getClass().getSimpleName());
			}
		}else {
			head.append(DEFAULT_LOGGER_TEXT_HEAD) ;
		}
		if(logNumberThreadLocal.get() != null) {
			head.append(":").append(logNumberThreadLocal.get());
		}
		head.append("] ");
		return head.toString() ;
	}
	
	/**
	 * 生成界面上显示的异常提示消息 .    <br>
	 * 信息为空时打印堆栈信息
	 * @param e
	 * @return
	 */
	public static String getExceptionMessage(Throwable e){
		return getExceptionMessage(e, true);
	}
	

	/**
	 * 生成界面上显示的异常提示消息
	 * @param e
	 * @param isPrint  信息为空时 是否打印堆栈信息
	 * @return
	 */
	public static String getExceptionMessage(Throwable e,boolean isPrint){
		String errmsg = getExceptionMessage(e,true,isPrint);
		errmsg = Utils.isEmpty(errmsg) ? "发生错误, 请查看服务器详细日志["+e.getClass().getSimpleName()+"]!" : (errmsg.length() > 150 ? errmsg.substring(0, 150) : errmsg);
		return errmsg;
	}

	/**
	 * 生成界面上显示的异常提示消息
	 * @param e
	 * @param isRoot - 是否打印异常堆栈
	 * @return
	 */
	private static String getExceptionMessage(Throwable e,boolean isRoot,boolean isPrint){
		if(e == null || e.getClass() == null){
			return "";
		}

		//遗漏的空指针
		if ( isPrint && e instanceof NullPointerException) {
			logger.error("NullPointerException详细: ",e);
			isPrint = false ;
			return "";
		}
		
		StringBuilder sb = new StringBuilder(" ");

		if(e.getCause() != null){
			String resultCause = getExceptionMessage(e.getCause(),false,isPrint);
			if(!Utils.isEmpty(resultCause)){
				sb.append(resultCause);
			}
		} else {
			sb.append("异常 [").append(e.getClass().getSimpleName()).append("],");

			String result = e.getMessage() != null ? e.getMessage().trim() : "";;
			if(!Utils.isEmpty(result)) { //&& !result.trim().contains("\n")){
				sb.append(result); //.split("\\n")[0].trim());
			}else {
				if(isPrint) {
					logger.error("详细:",e);
					isPrint = false ;
				}
			}
		}
		return sb.toString();
	}

}
