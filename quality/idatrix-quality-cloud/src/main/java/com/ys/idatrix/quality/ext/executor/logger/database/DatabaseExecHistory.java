/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.quality.ext.executor.logger.database;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.repository.RepositoryObjectType;

import com.ys.idatrix.quality.dto.common.PaginationDto;
import com.ys.idatrix.quality.dto.history.ExecHistoryRecordDto;
import com.ys.idatrix.quality.dto.history.ExecHistorySegmentingPartDto;
import com.ys.idatrix.quality.dto.statistics.ExecTaskNumberTotal;
import com.ys.idatrix.quality.dto.statistics.ExecTaskTimesTotal;
import com.ys.idatrix.quality.ext.CloudSession;
import com.ys.idatrix.quality.ext.utils.DatabaseHelper;
import com.ys.idatrix.quality.logger.CloudLogType;

/**
 * CloudExecHistory.java
 * @author JW
 * @since 2017年8月2日
 *
 */
public class DatabaseExecHistory {
	
	public static final Log  logger = LogFactory.getLog("CloudExecHistory");
	
	private static final Class<ExecHistoryRecordDto> TABLE_RECORD_CLASS=ExecHistoryRecordDto.class;
	private static final Class<ExecHistorySegmentingPartDto> TABLE_RECORD_PART_CLASS=ExecHistorySegmentingPartDto.class;
	
	private static boolean isDatabaseinit = false;
	
	public static void init() throws Exception {
		
		if(!isDatabaseinit) {
			DatabaseHelper.createOrUpdateTableifNonexist(TABLE_RECORD_CLASS,  null);
			DatabaseHelper.createOrUpdateTableifNonexist(TABLE_RECORD_PART_CLASS, null);
			
			isDatabaseinit = true ;
		}
		
	}
	
	private String owner;
	private String name;
	private CloudLogType type ;
	
	private String[] columnsConstant  ;
	private String[] conditionsConstant  ;
	private String[] valuesConstant ;
	
	public DatabaseExecHistory( String owner, String name, CloudLogType type) {
		this.owner = owner ;
		this.name = name ;
		this.type = type ;
		
		if( ExecHistoryRecordDto.isNoRenterPrivilege() ) {
			//不是租户超级权限
			columnsConstant =  new String[] {"NAME","TYPE","OPERATOR"} ;
			conditionsConstant =  new String[] {"=","=","="} ;
			valuesConstant =  new String[] {name,type.getType(),owner} ;
		}else {
			columnsConstant =  new String[] {"NAME","TYPE","OWNER"} ;
			conditionsConstant =  new String[] {"=","=","="} ;
			valuesConstant =  new String[] {name,type.getType(),owner} ;
		}
		
		try {
			init();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*============================================ExecHistoryRecordDto=================================================================*/
	
	public ExecHistoryRecordDto getExecRecord( String execId)  throws Exception{
		return  DatabaseHelper.queryFirst(TABLE_RECORD_CLASS, null, null, new String[] {"EXECID"}, new String[] {"="}, new String[] {execId}, " BEGIN DESC ");
	}
	
	public ExecHistoryRecordDto getLastExecRecord(  ) throws Exception {
		return  DatabaseHelper.queryFirst(TABLE_RECORD_CLASS, null, null, columnsConstant, conditionsConstant, valuesConstant, " BEGIN DESC ");
	}
	
	public ExecHistoryRecordDto getTotalExecRecord( )  throws Exception{
		 List<ExecHistoryRecordDto> results = DatabaseHelper.queryList(TABLE_RECORD_CLASS, null, null, columnsConstant, conditionsConstant, valuesConstant, " BEGIN DESC ");
		if(results != null && results.size() >1) {
			ExecHistoryRecordDto lastResult = results.get(0);
			ExecHistoryRecordDto oldestResult = results.get(results.size()-1);
			ExecHistoryRecordDto result = new ExecHistoryRecordDto(lastResult.getExecId(),lastResult.getRenterId(),lastResult.getOwner(),  lastResult.getName(), lastResult.getType(), lastResult.getStatus(), lastResult.getOperator(),  oldestResult.getBegin(), lastResult.getEnd());
			result.setConfiguration(lastResult.getConfiguration());
			results.stream().forEach(h -> {
				result.addIncreaseLines(h.getInputLines(), h.getOutputLines(),h.getReadLines(),h.getWriteLines(), h.getErrorLines(), h.getUpdateLines());
			});
		}else if( results != null && results.size() == 1){
			return results.get(0);
		}
		 return null;
	}
	
	public List<ExecHistoryRecordDto> getExecRecords( )  throws Exception{
		return  DatabaseHelper.queryList(TABLE_RECORD_CLASS, null, null, columnsConstant, conditionsConstant, valuesConstant, " BEGIN DESC ");
	}
	
	public void insertExecRecord(ExecHistoryRecordDto record)  throws Exception{
		record.setName(name);
		record.setOwner(owner);
		record.setType(type.getType());
		if(Utils.isEmpty(record.getOperator())) {
			record.setOperator( Const.NVL( CloudSession.getLoginUser(),owner) );
		}
		DatabaseHelper.insert( TABLE_RECORD_CLASS, record );
	}
	
	public void updateExecRecord(ExecHistoryRecordDto record)  throws Exception{
		String[] p = Arrays.copyOf(columnsConstant, 4) ;
		p[3] = "EXECID" ;
		DatabaseHelper.update(TABLE_RECORD_CLASS,  record, p );
	}
	

	public void renameExecHistory( String newname) throws Exception {
		String[] v =  new String[4] ;
		System.arraycopy(valuesConstant, 0, v, 1,  3);
		v[0] = newname ;
		DatabaseHelper.update(TABLE_RECORD_CLASS, new String[] {"NAME"},columnsConstant, conditionsConstant, v);
	}
	
	public void deleteExecRecord(String execId)  throws Exception{
		DatabaseHelper.delete(TABLE_RECORD_CLASS,  new String[] {"EXECID"}, new String[] {"="}, new String[] { execId});
	}
	
	public void clearExecHistory() throws Exception {
		DatabaseHelper.delete(TABLE_RECORD_CLASS,  columnsConstant, conditionsConstant, valuesConstant);
	}
	
	/*============================================ExecHistorySegmentingPartDto=================================================================*/
	
	public ExecHistorySegmentingPartDto getLastSegmentingPart() throws Exception {
		return DatabaseHelper.queryFirst(TABLE_RECORD_PART_CLASS, null , null, columnsConstant, conditionsConstant, valuesConstant, " BEGIN DESC " ) ;
	}
	
	public ExecHistorySegmentingPartDto getLastSegmentingPart(String execId )  throws Exception {
		return DatabaseHelper.queryFirst(TABLE_RECORD_PART_CLASS, null, null, new String[] {"EXECID"}, new String[] {"="}, new Object[] {execId}, " BEGIN DESC " ) ;
	}
	
	public ExecHistorySegmentingPartDto getSegmentingPart( String runId)  throws Exception {
		return DatabaseHelper.queryFirst(TABLE_RECORD_PART_CLASS, null, null, new String[] {"ID"}, new String[] {"="}, new Object[] {runId}, " BEGIN DESC " ) ;

	}

	public List<ExecHistorySegmentingPartDto> getSegmentingParts( )  throws Exception{
		return  DatabaseHelper.queryList(TABLE_RECORD_PART_CLASS, null, null, columnsConstant, conditionsConstant, valuesConstant, " BEGIN DESC ");
	}
	
	public List<ExecHistorySegmentingPartDto> getSegmentingPartsByExecId( String execId)  throws Exception{
		return  DatabaseHelper.queryList(TABLE_RECORD_PART_CLASS, null, null, new String[] {"EXECID"}, new String[] {"="}, new String[] {execId}, " BEGIN DESC ");
	}

	public void insertSegmentingPart(ExecHistorySegmentingPartDto part)  throws Exception{
		part.setName(name);
		part.setOwner(owner);
		part.setType(type.getType() );
		if(Utils.isEmpty(part.getOperator())) {
			part.setOperator( Const.NVL( CloudSession.getLoginUser(),owner) );
		}
		DatabaseHelper.insert( TABLE_RECORD_PART_CLASS,  part );
	}
	
	public void updateSegmentingPart( ExecHistorySegmentingPartDto part )  throws Exception{
		String[] p = Arrays.copyOf(columnsConstant, 5) ;
		p[3] = "EXECID" ;
		p[4] = "ID" ;
		DatabaseHelper.update(TABLE_RECORD_PART_CLASS,  part, p );
	}
	

	public void renameSegmentingPart( String newname) throws Exception {
		String[] v =  new String[4] ;
		System.arraycopy(valuesConstant, 0, v, 1,  3);
		v[0] = newname ;
		DatabaseHelper.update(TABLE_RECORD_PART_CLASS,  new String[] {"NAME"},columnsConstant, conditionsConstant, v);
	}
	
	public void deleteSegmentingPartByExecId(String execId)  throws Exception{
		DatabaseHelper.delete(TABLE_RECORD_PART_CLASS,  new String[] {"EXECID"}, new String[] {"="}, new String[] {execId});
	}
	
	public void deleteSegmentingPartById( String runId)  throws Exception{
		DatabaseHelper.delete(TABLE_RECORD_PART_CLASS,  new String[] {"ID"}, new String[] {"="}, new Object[] { runId});
	}
	
	
	public void clearSegmentingPart() throws Exception {
		DatabaseHelper.delete(TABLE_RECORD_PART_CLASS,  columnsConstant, conditionsConstant, valuesConstant );
	}
	
	/*============================================ Common =================================================================*/
	
	/**
	 * 统计当前(最新一次)任务(调度)的 状态总次数 <br>
	 * 一个任务只统计最新的一次
	 * @param renterId
	 * @param type
	 * @return
	 * @throws Exception
	 */
	public static ExecTaskTimesTotal countLastExecRecordTimes( String renterId , RepositoryObjectType type  )  throws Exception{
		
//		select COUNT(`STATUS`='Finished' or null ) successTotal ,  COUNT( (`STATUS`!='Finished' and `STATUS`!='Running' ) or null )  failTotal ,COUNT(`STATUS`='Running'  or null ) runningTotal 
//		from  ( 
//		select STATUS,TYPE,RENTERID,BEGIN from ETL_EXEC_RECORD  where 1 = 1  GROUP BY NAME,TYPE,OWNER HAVING `BEGIN` = MAX(`BEGIN`) 
//		) t 
		
		init() ;
		
		String[] groupStr;
		if( ExecHistoryRecordDto.isNoRenterPrivilege() ) {
			//不是租户超级权限
			groupStr =  new String[] {"NAME","TYPE","OPERATOR"} ;
		}else {
			groupStr =  new String[] {"NAME","TYPE","OWNER"} ;
		}
		StringBuffer sql= new StringBuffer() ;
		sql.append("select  COUNT(`STATUS`='Finished' or null ) successTotal ,  COUNT( (`STATUS`!='Finished' and `STATUS`!='Running' ) or null )  failTotal ,COUNT(`STATUS`='Running'  or null ) runningTotal from ")
				.append(" ( select STATUS,TYPE,RENTERID,BEGIN from ").append(DatabaseHelper.getTableName(TABLE_RECORD_CLASS))
				.append("  where 1 = 1 ") ;
				if( type != null ) {
					CloudLogType logType = RepositoryObjectType.JOB.equals(type)?CloudLogType.JOB_HISTORY:CloudLogType.TRANS_HISTORY ;
					sql.append(" and TYPE = '").append(logType.getType()).append("' ");
				}
				if( !Utils.isEmpty(renterId)) {
					sql.append(" and RENTERID = '").append(renterId).append("' ");
				}
		sql.append(" GROUP BY ").append(StringUtils.join(groupStr, ",")).append(" HAVING `BEGIN` = MAX(`BEGIN`) ) t ") ;
		return  DatabaseHelper.queryFirst(ExecTaskTimesTotal.class,  sql.toString());
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
	public static ExecTaskTimesTotal countALLExecRecordTimes(String renterId , RepositoryObjectType type ,String flag )  throws Exception{
		
		 //SELECT  DATE_FORMAT(`BEGIN`, '%Y-%m') AS month ,  COUNT(`STATUS`='Finished' or null ) successTotal ,  COUNT( (`STATUS`!='Finished' and `STATUS`!='Running' ) or null )  failTotal ,COUNT(`STATUS`='Running'  or null ) runningTotal 
		  //from ETL_EXEC_RECORD where
		 //date_format(`BEGIN`,'%Y-%m') = date_format(NOW(),'%Y-%m') 
		
		init() ;
		
		String formatDate = DatabaseHelper.getDateFormatByFlag(flag);
	
		StringBuffer sql= new StringBuffer() ;
		sql.append("select ").append( Utils.isEmpty( formatDate)? "''":"DATE_FORMAT(`BEGIN`, '"+formatDate+"')" ).append(" AS month ,  COUNT(`STATUS`='Finished' or null ) successTotal , COUNT( (`STATUS`!='Finished' and `STATUS`!='Running' ) or null )  failTotal ,COUNT(`STATUS`='Running'  or null ) runningTotal  from ").append(DatabaseHelper.getTableName(TABLE_RECORD_CLASS))
		.append("  where 1 = 1 ") ;
		if( type != null ) {
			CloudLogType logType = RepositoryObjectType.JOB.equals(type)?CloudLogType.JOB_HISTORY:CloudLogType.TRANS_HISTORY ;
			sql.append(" and TYPE = '").append(logType.getType()).append("' ");
		}
		if( !Utils.isEmpty(renterId)) {
			sql.append(" and RENTERID = '").append(renterId).append("' ");
		}
		if( !Utils.isEmpty(formatDate) ) {
			sql.append(" AND date_format(`BEGIN`,'").append(formatDate).append("') = ").append(DatabaseHelper.getDateValueByFlag(flag, formatDate));
		}
		return DatabaseHelper.queryFirst(ExecTaskTimesTotal.class,  sql.toString());
	}
	

	/**
	 * 按月份 统计 过去一年的每月的状态执行次数
	 * @param renterId
	 * @param type
	 * @param flag year/month/day/yyyy-MM-dd , 为空:忽略日期 , 否则 当年/当月/当天/具体某天某月某年
	 * @return
	 * @throws Exception
	 */
	public static List<ExecTaskTimesTotal> countALLExecRecordTimesByMonth(String renterId , RepositoryObjectType type , String flag)  throws Exception{

		//select  DATE_FORMAT(`BEGIN`, '%Y-%m') AS month ,COUNT(`STATUS`='Finished' or `STATUS`='Running' or null ) successTotal  ,  COUNT( (`STATUS`!='Finished' and `STATUS`!='Running' ) or null )  failTotal   from ETL_EXEC_RECORD where 
		// `BEGIN`  between date_sub(now(),interval 12 month) and now()  
		// and date_format(`BEGIN`,'%Y-%m-%d') = '2019-01-22' 
		//GROUP BY month;
		
		init() ;
		
		StringBuffer sql= new StringBuffer() ;
		sql.append("select  DATE_FORMAT(`BEGIN`, '%Y-%m') AS month , COUNT(`STATUS`='Finished' or `STATUS`='Running' or null ) successTotal ,  COUNT( (`STATUS`!='Finished' and `STATUS`!='Running' ) or null )  failTotal   from ").append(DatabaseHelper.getTableName(TABLE_RECORD_CLASS))
		.append("  where  `BEGIN`  between date_sub(now(),interval 12 month) and now()  ") ;
		if( type != null ) {
			CloudLogType logType = RepositoryObjectType.JOB.equals(type)?CloudLogType.JOB_HISTORY:CloudLogType.TRANS_HISTORY ;
			sql.append(" and TYPE = '").append(logType.getType()).append("' ");
		}
		if( !Utils.isEmpty(renterId)) {
			sql.append(" and RENTERID = '").append(renterId).append("' ");
		}
		if( !Utils.isEmpty(flag)) {
			String formatDate = DatabaseHelper.getDateFormatByFlag(flag);
			sql.append(" and date_format(`BEGIN`,'").append(formatDate).append("') = ").append(DatabaseHelper.getDateValueByFlag(flag, formatDate));
		}
		sql.append( " GROUP BY month ");
		return  DatabaseHelper.queryList(ExecTaskTimesTotal.class,  sql.toString());
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
	public static Long countALLExecRecordLines(String renterId , RepositoryObjectType type,String flag )  throws Exception{
		
		// select SUM(WRITELINES) from ETL_EXEC_RECORD  where date_format(`BEGIN`,'%Y-%m') = date_format(NOW(),'%Y-%m') 
		
		init() ;
		
		StringBuffer sql= new StringBuffer() ;
		sql.append(" select SUM(WRITELINES) from ").append(DatabaseHelper.getTableName(TABLE_RECORD_CLASS))
			.append("  where 1 = 1 ") ;
		if( type != null ) {
			CloudLogType logType = RepositoryObjectType.JOB.equals(type)?CloudLogType.JOB_HISTORY:CloudLogType.TRANS_HISTORY ;
			sql.append(" and TYPE = '").append(logType.getType()).append("' ");
		}
		if( !Utils.isEmpty(renterId)) {
			sql.append(" and RENTERID = '").append(renterId).append("' ");
		}
		if( !Utils.isEmpty(flag) ) {
			String formatDate = DatabaseHelper.getDateFormatByFlag(flag);
			sql.append(" AND date_format(`BEGIN`,'").append(formatDate).append("') = ").append(DatabaseHelper.getDateValueByFlag(flag, formatDate));
		}
		Long res = DatabaseHelper.queryFirst(Long.class,  sql.toString());
		return res == null ? 0 : res ;
	}
	
	/**
	 * 按月份 统计 过去一年的每月的任务处理数量
	 * @param renterId
	 * @param type
	 * @param flag year/month/day/yyyy-MM-dd , 为空:忽略日期 , 否则 当年/当月/当天/具体某天某月某年
	 * @return
	 * @throws Exception
	 */
	public static List<ExecTaskNumberTotal> countALLExecRecordLinesByMonth(String renterId , RepositoryObjectType type ,String flag)  throws Exception{
		// select DATE_FORMAT(`BEGIN`, '%Y-%m') AS month ,SUM(WRITELINES) from ETL_EXEC_RECORD  where
		//  `BEGIN`  between date_sub(now(),interval 12 month) and now()  
		// and date_format(`BEGIN`,'%Y-%m-%d') = '2019-01-22' 
		//  GROUP BY month; 
		
		init() ;
		
		StringBuffer sql= new StringBuffer() ;
		sql.append("select DATE_FORMAT(`BEGIN`, '%Y-%m') AS month ,SUM(WRITELINES) total from ").append(DatabaseHelper.getTableName(TABLE_RECORD_CLASS))
		.append("  where  `BEGIN`  between date_sub(now(),interval 12 month) and now()  ") ;
		if( type != null ) {
			CloudLogType logType = RepositoryObjectType.JOB.equals(type)?CloudLogType.JOB_HISTORY:CloudLogType.TRANS_HISTORY ;
			sql.append(" and TYPE = '").append(logType.getType()).append("' ");
		}
		if( !Utils.isEmpty(renterId)) {
			sql.append(" and RENTERID = '").append(renterId).append("' ");
		}
		if( !Utils.isEmpty(flag) ) {
			String formatDate = DatabaseHelper.getDateFormatByFlag(flag);
			sql.append(" AND date_format(`BEGIN`,'").append(formatDate).append("') = ").append(DatabaseHelper.getDateValueByFlag(flag, formatDate));
		}
		sql.append( " GROUP BY month ");
		return  DatabaseHelper.queryList(ExecTaskNumberTotal.class,  sql.toString());
		
	}
	
	/**
	 * 按任务 统计 每个任务的任务处理数量
	 * @param renterId
	 * @param type
	 * @param flag year/month/day/yyyy-MM-dd , 为空:忽略日期 , 否则 当年/当月/当天/具体某天某月某年
	 * @return
	 * @throws Exception
	 */
	public static PaginationDto<ExecTaskNumberTotal> countALLExecRecordLinesByTask(String renterId , RepositoryObjectType type ,String flag, Integer pageNo, Integer pageSize )  throws Exception{

		// select NAME,TYPE,OWNER ,SUM(WRITELINES) from ETL_EXEC_RECORD 
		// where date_format(`BEGIN`,'%Y-%m-%d') = '2019-01-22' 
		// GROUP BY NAME,TYPE,OWNER; 
		
		init() ;
		
		StringBuffer sql= new StringBuffer() ;
		sql.append("select NAME, TYPE, OWNER ,SUM(WRITELINES) total from  ").append(DatabaseHelper.getTableName(TABLE_RECORD_CLASS)) 
			.append("  where 1 = 1 ") ;
		if( type != null ) {
			CloudLogType logType = RepositoryObjectType.JOB.equals(type)?CloudLogType.JOB_HISTORY:CloudLogType.TRANS_HISTORY ;
			sql.append(" and TYPE = '").append(logType.getType()).append("' ");
		}
		if( !Utils.isEmpty(renterId)) {
			sql.append(" and RENTERID = '").append(renterId).append("' ");
		}
		if( !Utils.isEmpty(flag) ) {
			String formatDate = DatabaseHelper.getDateFormatByFlag(flag);
			sql.append(" AND date_format(`BEGIN`,'").append(formatDate).append("') = ").append(DatabaseHelper.getDateValueByFlag(flag, formatDate));
		}
		sql.append( " GROUP BY NAME, TYPE, OWNER ");
		return  DatabaseHelper.queryPageList(ExecTaskNumberTotal.class, TABLE_RECORD_CLASS, null, sql.toString(), " NAME ", pageNo, pageSize) ;
		
	}
	
	/**
	 * 获取租户下的 所有成功的执行记录列表
	 * @param renterId
	 * @param type
	 * @param flag year/month/day/yyyy-MM-dd , 为空:忽略日期 , 否则 当年/当月/当天/具体某天某月某年
	 * @return
	 * @throws Exception
	 */
	public static PaginationDto<ExecHistoryRecordDto> getRenterExecRecords( String renterId , RepositoryObjectType type ,String flag, Integer pageNo, Integer pageSize )  throws Exception{
		//select * from ETL_EXEC_RECORD  
		//where RENTERID = 443 and ( `STATUS`='Finished' or `STATUS`='Running' ) 
		//		and date_format(`BEGIN`,'%Y-%m-%d') = '2019-01-22' 
		
		init() ;
		
		StringBuffer whereSql= new StringBuffer() ;
		whereSql.append(" where ( `STATUS`='Finished' or `STATUS`='Running' )  ") ;
		if( type != null ) {
			CloudLogType logType = RepositoryObjectType.JOB.equals(type)?CloudLogType.JOB_HISTORY:CloudLogType.TRANS_HISTORY ;
			whereSql.append(" and TYPE = '").append(logType.getType()).append("' ");
		}
		if( !Utils.isEmpty(renterId)) {
			whereSql.append(" and RENTERID = '").append(renterId).append("' ");
		}
		if( !Utils.isEmpty(flag) ) {
			String formatDate = DatabaseHelper.getDateFormatByFlag(flag);
			whereSql.append(" AND date_format(`BEGIN`,'").append(formatDate).append("') = ").append(DatabaseHelper.getDateValueByFlag(flag, formatDate));
		}
		return  DatabaseHelper.queryPageList(TABLE_RECORD_CLASS, TABLE_RECORD_CLASS, new String[] {"EXECID" , "NAME" , "TYPE" , "STATUS" ,"BEGIN" ,"WRITELINES" },
				whereSql.toString(), " BEGIN DESC  ", pageNo, pageSize) ;
				
	}
	
	
}
