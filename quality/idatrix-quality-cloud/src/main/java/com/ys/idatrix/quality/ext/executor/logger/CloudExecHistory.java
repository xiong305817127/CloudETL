/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.quality.ext.executor.logger;

import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.di.core.util.IdatrixPropertyUtil;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.repository.RepositoryObjectType;

import com.google.common.collect.Lists;
import com.ys.idatrix.quality.dto.common.PaginationDto;
import com.ys.idatrix.quality.dto.history.ExecHistoryRecordDto;
import com.ys.idatrix.quality.dto.history.ExecHistorySegmentingPartDto;
import com.ys.idatrix.quality.dto.statistics.ExecTaskNumberTotal;
import com.ys.idatrix.quality.dto.statistics.ExecTaskTimesTotal;
import com.ys.idatrix.quality.ext.CloudApp;
import com.ys.idatrix.quality.ext.CloudSession;
import com.ys.idatrix.quality.ext.executor.logger.database.DatabaseExecHistory;
import com.ys.idatrix.quality.ext.executor.logger.xml.FileExecHistory;
import com.ys.idatrix.quality.logger.CloudLogType;

/**
 * CloudExecHistory.java
 * @author JW
 * @since 2017年8月2日
 *
 */
public class CloudExecHistory {
	
	public static final Log  logger = LogFactory.getLog("CloudExecHistory");
	
	private DatabaseExecHistory databaseExecHistory ;
	private FileExecHistory fileExecHistory ;
	
	private CloudExecHistory(String owner , String name, CloudLogType type) {
		
		if(isDatabaseRecord()) {
			//数据库存储方式
			databaseExecHistory = new DatabaseExecHistory(owner, name, type );
		}else {
			//文件存储方式
			fileExecHistory= new FileExecHistory(CloudApp.getInstance().getUserLogsRepositoryPath(owner), name,type );
		}
	}
	
	public static synchronized CloudExecHistory initExecHistory( String owner ,String name, CloudLogType type) {
		if(Utils.isEmpty(owner)) {
			owner = CloudSession.getResourceUser();
		}
		return new CloudExecHistory(owner, name, type);
	}
	
	public void renameHistory(String newname) throws Exception {
		
		if(databaseExecHistory != null) {
			databaseExecHistory.renameSegmentingPart(newname);
			databaseExecHistory.renameExecHistory(newname);
		}else if( fileExecHistory != null) {
			fileExecHistory.renameSegmentingPart(newname);
			fileExecHistory.renameExecHistory(newname);
		}
	}

	public void deleteHistory() throws Exception {
		if(databaseExecHistory != null) {
			databaseExecHistory.clearSegmentingPart();
			databaseExecHistory.clearExecHistory();
		}else if( fileExecHistory != null) {
			fileExecHistory.clearSegmentingPart();
			fileExecHistory.clearExecHistory();
		}

	}
	
	/*============================================ExecHistoryRecordDto=================================================================*/

	public ExecHistoryRecordDto getExecRecord(String execId)  throws Exception{
		if(databaseExecHistory != null) {
			return databaseExecHistory.getExecRecord(execId);
		}else if( fileExecHistory != null) {
			return fileExecHistory.getExecRecord(execId);
		}
		return null;
	}
	
	public ExecHistoryRecordDto getLastExecRecord() throws Exception {
		if(databaseExecHistory != null) {
			return databaseExecHistory.getLastExecRecord();
		}else if( fileExecHistory != null) {
			return fileExecHistory.getLastExecRecord();
		}
		return null;
	}
	
	public ExecHistoryRecordDto getTotalExecRecord()  throws Exception{
		if(databaseExecHistory != null) {
			return databaseExecHistory.getTotalExecRecord();
		}else if( fileExecHistory != null) {
			return	fileExecHistory.getTotalExecRecord();
		}
		return null;
	}
	
	public List<ExecHistoryRecordDto> getExecRecords()  throws Exception{
		if(databaseExecHistory != null) {
			return databaseExecHistory.getExecRecords();
		}else if( fileExecHistory != null) {
			return fileExecHistory.getExecRecords( );
		}
		
		return null;
	}

	public void addExecRecord(ExecHistoryRecordDto record)  throws Exception{
		if(databaseExecHistory != null) {
			databaseExecHistory.insertExecRecord(record);
		}else if( fileExecHistory != null) {
			fileExecHistory.insertExecRecord(record );
		}
	}
	
	public void updateExecRecord(ExecHistoryRecordDto record)  throws Exception{
		if(databaseExecHistory != null) {
			databaseExecHistory.updateExecRecord(record);
		}else if( fileExecHistory != null) {
			fileExecHistory.updateExecRecord(record );
		}
	}

	public void deleteExecRecord(String execId)  throws Exception{
		if(databaseExecHistory != null) {
			databaseExecHistory.deleteExecRecord(execId);
		}else if( fileExecHistory != null) {
			fileExecHistory.deleteExecRecord(execId );
		}
	}
	
	/*============================================ExecHistorySegmentingPartDto=================================================================*/
	
	public ExecHistorySegmentingPartDto getLastSegmentingPart() throws Exception {

		if(databaseExecHistory != null) {
			return databaseExecHistory.getLastSegmentingPart();
		}else if( fileExecHistory != null) {
			return fileExecHistory.getLastSegmentingPart();
		}
		return null;
	}
	
	public ExecHistorySegmentingPartDto getLastSegmentingPart( String execId ) throws Exception {

		if(databaseExecHistory != null) {
			return databaseExecHistory.getLastSegmentingPart(execId);
		}else if( fileExecHistory != null) {
			return fileExecHistory.getLastSegmentingPart(execId);
		}
		return null;
	}
	
	public ExecHistorySegmentingPartDto getSegmentingPart(String execId ,String runId)  throws Exception {
		if(databaseExecHistory != null) {
			return databaseExecHistory.getSegmentingPart( runId);
		}else if( fileExecHistory != null) {
			return fileExecHistory.getSegmentingPart(execId,runId);
		}

		return null;
	}
	
	public ExecHistorySegmentingPartDto getSegmentingPart( String runId)  throws Exception {
		
		if(databaseExecHistory != null) {
			return databaseExecHistory.getSegmentingPart( runId);
		}else if( fileExecHistory != null) {
			return fileExecHistory.getSegmentingPart(runId);
		}

		return null;
	}

	public List<ExecHistorySegmentingPartDto> getSegmentingParts()  throws Exception {
		
		if(databaseExecHistory != null) {
			return databaseExecHistory.getSegmentingParts();
		}else if( fileExecHistory != null) {
			return fileExecHistory.getSegmentingParts();
		}

		return null;
	}

	public List<ExecHistorySegmentingPartDto> getSegmentingParts(String execId)  throws Exception {
		
		if(databaseExecHistory != null) {
			return databaseExecHistory.getSegmentingPartsByExecId(execId);
		}else if( fileExecHistory != null) {
			return fileExecHistory.getSegmentingPartsByExecId(execId);
		}

		return null;
	}
	
	public void addSegmentingPart(ExecHistorySegmentingPartDto part)  throws Exception{
		if(databaseExecHistory != null) {
			databaseExecHistory.insertSegmentingPart(part);
		}else if( fileExecHistory != null) {
			fileExecHistory.insertSegmentingPart(part);
		}
	}
	
	public void updateSegmentingPart(ExecHistorySegmentingPartDto part)  throws Exception{
		if(databaseExecHistory != null) {
			databaseExecHistory.updateSegmentingPart(part);
		}else if( fileExecHistory != null) {
			fileExecHistory.updateSegmentingPart(part );
		}
	}

	public void deleteSegmentingPartByExecId(String execId)  throws Exception{
		if(databaseExecHistory != null) {
			databaseExecHistory.deleteSegmentingPartByExecId(execId);
		}else if( fileExecHistory != null) {
			fileExecHistory.deleteSegmentingPartByExecId(execId );
		}
	}
	
	public void deleteSegmentingPartById(String runId)  throws Exception{
		if(databaseExecHistory != null) {
			databaseExecHistory.deleteSegmentingPartById(runId);
		}else if( fileExecHistory != null) {
			fileExecHistory.deleteSegmentingPartById(runId);
		}
	}
	
	/*============================================ Common =================================================================*/
	
	public static boolean isDatabaseRecord() {
		return IdatrixPropertyUtil.getBooleanProperty("idatrix.database.enable", false)&&"database".equalsIgnoreCase(IdatrixPropertyUtil.getProperty("idatrix.exec.log.store", "database")) ;
	}
	
	/**
	 * 获取租户下的 所有成功的执行记录列表
	 * @param renterId
	 * @param type
	 * @param flag year/month/day/yyyy-MM-dd , 为空:忽略日期 , 否则 当年/当月/当天/具体某天某月某年
	 * @return
	 * @throws Exception
	 */
	public static  PaginationDto<ExecHistoryRecordDto> getRenterExecRecords(String renterId , RepositoryObjectType type ,String flag, Integer pageNo, Integer pageSize)  throws Exception{
		if(isDatabaseRecord()) {
			return DatabaseExecHistory.getRenterExecRecords(renterId, type, flag, pageNo, pageSize);
		}
		return null;
		
	}
	
	/**
	 * 统计当前(最新一次)任务(调度)的 状态总次数 <br>
	 * 一个任务只统计最新的一次
	 * @param renterId
	 * @param type
	 * @return
	 * @throws Exception
	 */
	public static ExecTaskTimesTotal countLastExecRecordTimes(String renterId , RepositoryObjectType type )  throws Exception{
		if(isDatabaseRecord()) {
			return DatabaseExecHistory.countLastExecRecordTimes(renterId, type);
		}
		return null ;
		
	}
	
	/**
	 * 统计 所有执行的任务(调度)的 状态总次数 <br>
	 * 一个任务重复统计所有的执行
	 * 
	 * @param renterId
	 * @param type
	 * @param flag year/month/day/yyyy-MM-dd , 为空:忽略日期 , 否则 当年/当月/当天/具体某天某月某年
	 * @return
	 * @throws Exception
	 */
	public static ExecTaskTimesTotal countALLExecRecordTimes(String renterId , RepositoryObjectType type ,String flag)  throws Exception{
		if(isDatabaseRecord()) {
			return DatabaseExecHistory.countALLExecRecordTimes(renterId, type, flag);
		}
		return null;
		
	}
	
	/**
	 * 按月份 统计 过去一年的每月的状态执行次数
	 * @param renterId
	 * @param type
	 * @param flag year/month/day/yyyy-MM-dd , 为空:忽略日期 , 否则 当年/当月/当天/具体某天某月某年
	 * @return
	 * @throws Exception
	 */
	public static List<ExecTaskTimesTotal>  countALLExecRecordTimesByMonth(String renterId , RepositoryObjectType type, String flag)  throws Exception{
		if(isDatabaseRecord()) {
			return DatabaseExecHistory.countALLExecRecordTimesByMonth(renterId, type, flag);
		}
		return Lists.newArrayList();
		
	}
	
	/**
	 * 统计 所有执行的任务(调度)的 任务处理数量  <br>
	 * 一个任务重复统计所有的执行
	 * @param renterId
	 * @param type
	 * @param flag year/month/day/yyyy-MM-dd , 为空:忽略日期 , 否则 当年/当月/当天/具体某天某月某年
	 * @return
	 * @throws Exception
	 */
	public static long countALLExecRecordLines(String renterId , RepositoryObjectType type, String flag )  throws Exception{
		if(isDatabaseRecord()) {
			return DatabaseExecHistory.countALLExecRecordLines(renterId, type, flag);
		}
		return 0L ;
		
	}
	
	/**
	 * 按月份 统计 过去一年的每月的任务处理数量
	 * @param renterId
	 * @param type
	 * @param flag year/month/day/yyyy-MM-dd , 为空:忽略日期 , 否则 当年/当月/当天/具体某天某月某年
	 * @return
	 * @throws Exception
	 */
	public static List<ExecTaskNumberTotal> countALLExecRecordLinesByMonth(String renterId , RepositoryObjectType type, String flag )  throws Exception{
		if(isDatabaseRecord()) {
			return DatabaseExecHistory.countALLExecRecordLinesByMonth(renterId, type, flag);
		}
		return Lists.newArrayList() ;
	}
	
	/**
	 * 按任务 统计 每个任务的任务处理数量
	 * @param renterId
	 * @param type
	 * @param flag year/month/day/yyyy-MM-dd , 为空:忽略日期 , 否则 当年/当月/当天/具体某天某月某年
	 * @return
	 * @throws Exception
	 */
	public static PaginationDto<ExecTaskNumberTotal> countALLExecRecordLinesByTask(String renterId , RepositoryObjectType type , String flag, Integer pageNo, Integer pageSize )  throws Exception{
		if(isDatabaseRecord()) {
			return DatabaseExecHistory.countALLExecRecordLinesByTask(renterId, type ,flag, pageNo ,pageSize );
		}
		return new PaginationDto<ExecTaskNumberTotal>(pageNo, pageSize,"") ;
	}
	
}
