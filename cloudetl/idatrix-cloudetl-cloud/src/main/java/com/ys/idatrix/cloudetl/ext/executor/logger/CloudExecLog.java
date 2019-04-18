/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.ext.executor.logger;

import java.io.IOException;
import java.io.PrintStream;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs2.FileSystemException;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleFileException;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.core.vfs.KettleVFS;

import com.ys.idatrix.cloudetl.ext.CloudApp;
import com.ys.idatrix.cloudetl.ext.utils.UnixPathUtil;
import com.ys.idatrix.cloudetl.logger.CloudLogConst;
import com.ys.idatrix.cloudetl.logger.CloudLogType;
import com.ys.idatrix.cloudetl.logger.CloudLogUtils;

/**
 * CloudExecLog.java
 * @author JW
 * @since 2017年8月4日
 *
 */
public class CloudExecLog  {
	
	public static final Log  logger = LogFactory.getLog("CloudJobExecutor");
	
	private static String Start_Exec_Flag = "INFO [Start Exec] :";
	private static String End_Exec_Flag = "INFO [End Exec] :";
	
	private String rootPath;
	private String filePath;
	private CloudLogType filetype;

	public static synchronized CloudExecLog initExecLog(String path, String name, CloudLogType type) {
		CloudExecLog log = new CloudExecLog(path, name, type);
		return log;
	}

	public CloudExecLog(String path, String name, CloudLogType type) {
		this.rootPath = UnixPathUtil.unixPath(CloudApp.getInstance().getRepositoryRootFolder() + path + type.getType() + CloudLogConst.SEPARATOR);
		this.filePath = this.rootPath + name + CloudLogConst.SEPARATOR ;
		this.filetype = type;
	}
	
	/**
	 * 根据日期获取日志文件
	 * @param date
	 * @return
	 */
	private String getFileName(String date) {
		if(Utils.isEmpty(date)) {
			date =  DateFormatUtils.format(new Date(), CloudLogConst.LOG_DATE_PATTERN) ;
		}
		return this.filePath + date +  filetype.getExtension();
	}
	
	/**
	 * 根据分次记录文件获取分次记录文件路径
	 * @param partName
	 * @return
	 */
	private String getSegmentingPartFileName(String partName) {
		return this.filePath +  "SegmentingPart" +  CloudLogConst.SEPARATOR + partName;
	}

	/**
	 * 根据日期获取文件所有内容
	 * @param date
	 * @return
	 */
	public String getExecLog(String date) {
		return CloudLogUtils.getLog(getFileName(date));
	}
	
	/**
	 * 根据日期获取文件所有内容
	 * @param date
	 * @return
	 * @throws ParseException 
	 * @throws IOException 
	 */
	public String searchExecLog(String id , String startDate ,String endDate ) throws ParseException, IOException {
		StringBuilder sb = new StringBuilder();
		
		Date start = new Date();
		Date end = new Date();
		if( !Utils.isEmpty(startDate)) {
			start = DateUtils.parseDate(startDate, CloudLogConst.LOG_DATE_PATTERN,CloudLogConst.EXEC_TIME_PATTERN);
		}
		if( !Utils.isEmpty(endDate)) {
			end = DateUtils.parseDate(endDate, CloudLogConst.LOG_DATE_PATTERN,CloudLogConst.EXEC_TIME_PATTERN);
		}
		String  startFlag = Start_Exec_Flag+id;
		String  endFlag = End_Exec_Flag+id;
		
		do {
			 String date = DateFormatUtils.format(start, CloudLogConst.LOG_DATE_PATTERN) ;
			 sb.append( CloudLogUtils.searchLog( getFileName(date), startFlag, endFlag )) ;
			 start = DateUtils.addDays(start, 1);
			 startFlag = null;
		}while( DateUtils.truncatedCompareTo(start, end, Calendar.DATE) <= 0 ) ;
		
		if(sb.length() == 0 ) {
			sb.append("未找到日志.");
		}
		
		return sb.toString();
	}
	
	/**
	 * 更加文件名获取分次文件内容
	 * @param partFileName
	 * @return
	 */
	public String getPartExecLog(String partFileName) {
			return CloudLogUtils.getLog(getSegmentingPartFileName( partFileName) );
	}
	
	public String getFilePath() {
		return this.filePath;
	}
	
	public String getPartFileName(String partFileName) {
		return getSegmentingPartFileName( partFileName );
	}
	
	boolean isStartlog = false ;
	public void startExecLog(String id ) {
		try {
			if(!isStartlog) {
				CloudLogUtils.insertLog( getFileName(null), Const.CR+Start_Exec_Flag+id);
				isStartlog= true ;
			}
		} catch (KettleException | IOException e) {
			logger.error("增加日志开始标志失败.",e);
		}
	}
	
	public void endExecLog(String id ) {
		try {
			if(isStartlog) {
				CloudLogUtils.insertLog( getFileName(null), End_Exec_Flag+id);
				isStartlog = false ;
			}
			
		} catch (KettleException | IOException e) {
			logger.error("增加日志结束标志失败.",e);
		}
	}
	
	public void insertExecExceptionLog(Exception e) throws KettleException, IOException  {
		if(e == null) {
			return ;
		}
		try(PrintStream ps = new PrintStream( KettleVFS.getOutputStream(getFileName(null), true) ); ){
			e.printStackTrace(ps);
		}
	}

	public void insertExecLog(String logText,boolean addTime) throws KettleException, IOException  {
		if(Utils.isEmpty(logText) || Utils.isEmpty(logText.trim())) {
			return ;
		}
		StringBuilder sb = new StringBuilder();
		if(addTime) {
			sb.append(DateFormatUtils.format(new Date(), CloudLogConst.EXEC_TIME_PATTERN1));
			sb.append(Utils.isEmpty(logText) ? "  " : "  "+logText);
		}else {
			sb.append(logText);
		}
		CloudLogUtils.insertLog( getFileName(null), sb.toString().trim());
	}
	
	public void insertPartExecLog(String partFileName , String logText) throws KettleException, IOException {
		if(Utils.isEmpty(logText)) {
			return ;
		}
		CloudLogUtils.insertLog(getSegmentingPartFileName(partFileName), Utils.isEmpty(logText) ? "" : logText);
	}

	public void renameExecLog(String newname) throws KettleFileException, FileSystemException {
		CloudLogUtils.renameLog(this.filePath, this.rootPath + newname);
		this.filePath = this.rootPath + newname ;
	}

	public void deleteExecLog() throws KettleFileException, FileSystemException {
		CloudLogUtils.deleteLog(this.filePath);
	}

}
