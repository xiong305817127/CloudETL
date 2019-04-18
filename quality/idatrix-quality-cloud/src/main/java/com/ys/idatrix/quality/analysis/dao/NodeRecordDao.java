package com.ys.idatrix.quality.analysis.dao;

import java.util.Date;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.di.core.util.UUIDUtil;
import org.pentaho.di.core.util.Utils;

import com.ys.idatrix.quality.analysis.dto.NodeRecordDto;
import com.ys.idatrix.quality.dto.statistics.NodeTypeNumberDto;
import com.ys.idatrix.quality.dto.statistics.NodeTypeTaskTimesDto;
import com.ys.idatrix.quality.ext.CloudSession;
import com.ys.idatrix.quality.ext.utils.DatabaseHelper;


public class NodeRecordDao {
	
public static final Log  logger = LogFactory.getLog("NodeRecordDao");
	
	private static final Class<NodeRecordDto> Table_Class = NodeRecordDto.class;
	
	private static boolean isDatabaseinit = false;
	private static NodeRecordDao instance ;
	
	public static void init() throws Exception {
		
		if(!isDatabaseinit) {
			DatabaseHelper.createOrUpdateTableifNonexist(Table_Class,  null);
			isDatabaseinit = true ;
		}
	}
	
	public NodeRecordDao() {
		try {
			init();
		} catch (Exception e) {
		}
	}
	
	public static NodeRecordDao getInstance() {
		if( instance == null ) {
			synchronized (Table_Class) {
				if( instance == null ) {
					instance = new NodeRecordDao();
				}
			}
		}
		return instance;
	}
	
	private String getUserName() {
		return CloudSession.getResourceUser() ;
	}
	
	
	public NodeRecordDto getRecordById( String uuid)  throws Exception{
		return  DatabaseHelper.queryFirst(Table_Class, Table_Class , null, new String[] {"uuid"}, new String[] {"="}, new String[] {uuid}, null );
	}
	
	/**
	 * 获取某个分析的所有执行的所有节点
	 * @param analysisName
	 * @return
	 * @throws Exception
	 */
	public List<NodeRecordDto> getRecordListByName( String analysisName)  throws Exception{
		return  DatabaseHelper.queryList(Table_Class, Table_Class , null, new String[] {"userName","analysisName"}, new String[] {"="}, new String[] {getUserName() ,analysisName}, " updateTime DESC " );
	}
	
	/**
	 * 获取某个分析的  某次执行的所有节点信息
	 * @param execId
	 * @return
	 * @throws Exception
	 */
	public List<NodeRecordDto> getRecordListByExecId( String execId )  throws Exception{
		return  DatabaseHelper.queryList(Table_Class, Table_Class , null, new String[] {"userName","execId"}, new String[] {"="}, new String[] {getUserName() ,execId}, " updateTime DESC " );
	}
	
	/**
	 * 某次执行的某个节点的信息
	 * @param execId
	 * @param NodeId
	 * @return
	 * @throws Exception
	 */
	public NodeRecordDto getRecordInfo( String execId , String nodeId)  throws Exception{
		return  DatabaseHelper.queryFirst(Table_Class, Table_Class , null, new String[] {"userName","execId","nodId"}, new String[] {"="}, new String[] {getUserName() ,execId,nodeId}, " updateTime DESC " );
	}
	
	/**
	 * 获取所有记录
	 * @return
	 * @throws Exception
	 */
	public List<NodeRecordDto> getRecordList( )  throws Exception{
		return  DatabaseHelper.queryList(Table_Class, Table_Class , null, null, null, null , " updateTime DESC " );
	}
	
	/**
	 * 新增记录
	 * @param record
	 * @throws Exception
	 */
	public void insertRecord(NodeRecordDto record)  throws Exception{
		if( Utils.isEmpty(record.getUuid()) ){
			record.setUuid( UUIDUtil.getUUIDAsString() );
		}
		if( Utils.isEmpty(record.getUserName()) ){
			record.setUserName(getUserName());
		}
		if( record.getUpdateTime() == null  ){
			record.setUpdateTime(new Date());
		}
		record.setRenterId( CloudSession.getLoginRenterId() );
		DatabaseHelper.insert(Table_Class,  record );
	}
	
	/**
	 * 更新记录
	 * @param record
	 * @throws Exception
	 */
	public void updateRecord(NodeRecordDto record)  throws Exception{
		
		String[] queryKeys = null ;
		if( !Utils.isEmpty( record.getUuid() )) {
			queryKeys = new String[] {"uuid"} ;
		}else if( !Utils.isEmpty( record.getAnalysisName() ) && !Utils.isEmpty( record.getExecId() ) && !Utils.isEmpty( record.getNodId() )){
			queryKeys = new String[] {"analysisName","execId","nodId"} ;
		}else {
			throw new Exception("匹配条件为空,无法更新!");
		}
		record.setUpdateTime(new Date());
		DatabaseHelper.update(Table_Class,  record, queryKeys );
	}
	
	/**
	 * 更新 正确数量
	 * @param result
	 * @throws Exception
	 */
	public void updateCorrectNumber( NodeRecordDto record )  throws Exception{
		String[] queryKeys = null ;
		Object[] values = null ;
		if( !Utils.isEmpty( record.getUuid() )) {
			queryKeys = new String[] {"uuid"} ;
			values = new Object[] { record.getSuccNum() , new Date() ,record.getUuid() } ;
		}else if( !Utils.isEmpty( record.getAnalysisName() ) && !Utils.isEmpty( record.getExecId() ) && !Utils.isEmpty( record.getNodId() )){
			queryKeys = new String[] {"analysisName","execId","nodId"} ;
			values = new Object[] {record.getSuccNum() , new Date(), record.getAnalysisName(), record.getExecId(), record.getNodId()} ;
		}else {
			throw new Exception("匹配条件为空,无法更新!");
		}
		DatabaseHelper.update( Table_Class,  new String[] {"succNum","updateTime"},  queryKeys, new String[] {"="}, values);
	}
	
	/**
	 * 更新正确 数量
	 * @param result
	 * @throws Exception
	 */
	public void updateCorrectNumber(String uuid , long curNodNum )  throws Exception{
		DatabaseHelper.update( Table_Class, new String[] {"succNum","updateTime"},  new String[] {"uuid"}, new String[] {"="},  new Object[] { curNodNum , new Date() ,uuid });
	}
	
	
	/**
	 * 更新 错误数量
	 * @param result
	 * @throws Exception
	 */
	public void updateErrorNumber( NodeRecordDto record )  throws Exception{
		String[] queryKeys = null ;
		Object[] values = null ;
		if( !Utils.isEmpty( record.getUuid() )) {
			queryKeys = new String[] {"uuid"} ;
			values = new Object[] { record.getErrNum() , new Date() ,record.getUuid() } ;
		}else if( !Utils.isEmpty( record.getAnalysisName() ) && !Utils.isEmpty( record.getExecId() ) && !Utils.isEmpty( record.getNodId() )){
			queryKeys = new String[] {"analysisName","execId","nodId"} ;
			values = new Object[] {record.getErrNum() ,new Date(), record.getAnalysisName(), record.getExecId(), record.getNodId()} ;
		}else {
			throw new Exception("匹配条件为空,无法更新!");
		}
		DatabaseHelper.update( Table_Class,  new String[] {"errNum", "updateTime"},  queryKeys, new String[] {"="}, values);
	}
	
	/**
	 * 更新错误数量
	 * @param result
	 * @throws Exception
	 */
	public void updateErrorNumber(String uuid , long errNodNum )  throws Exception{
		DatabaseHelper.update( Table_Class,  new String[] {"errNum","updateTime"},  new String[] {"uuid"}, new String[] {"="},  new Object[] { errNodNum , new Date() ,uuid });
	}
	
	/**
	 * 删除结果数据
	 * @param uuid
	 * @throws Exception
	 */
	public void deleteRecord(String uuid)  throws Exception{
		DatabaseHelper.delete(Table_Class, new String[] {"uuid"}, new String[] {"="}, new String[] {uuid});
	}
	
	/*============================================ Common =================================================================*/
	
	/**
	 * 统计 所有执行的任务(调度)的 输出行数  <br>
	 * 一个任务重复统计所有的执行
	 * @param renterId
	 * @param type
	 * @param flag year/month/day/yyyy-MM-dd , 为空:null , 否则 当年/当月/当天/具体某天某月某年
	 * @return
	 * @throws Exception
	 */
	public static NodeTypeNumberDto countALLRecordLines(String renterId , String flag )  throws Exception{
		init() ;
		StringBuffer sql= new StringBuffer() ;
		sql.append(" select SUM(succNum) succTotal , SUM(errNum) errTotal ") ;
		sql.append(" from ").append(DatabaseHelper.getTableName(Table_Class)).append("  where 1 = 1 ") ;
		if( !Utils.isEmpty(flag) ) {
			String formatDate = DatabaseHelper.getDateFormatByFlag(flag) ;
			sql.append(" AND date_format(updateTime,'").append(formatDate).append("') = ").append(DatabaseHelper.getDateValueByFlag(flag, formatDate));
		}
		if( !Utils.isEmpty(renterId) ) {
			sql.append(" AND renterId = '").append(renterId).append("' ");
		}
		return DatabaseHelper.queryFirst(NodeTypeNumberDto.class,  sql.toString());
		
	}
	

	/**
	 * 按照 节点类型 分类 统计节点处理数量总数
	 * @param renterId 租户Id ,为空 统计整个系统数据
	 * @param flag year/month/day
	 * @return
	 * @throws Exception
	 */
	public static List<NodeTypeNumberDto>  countNumberByNodeType( String renterId ,String flag) throws Exception {
		init() ;
		StringBuffer sql= new StringBuffer() ;
		sql.append(" SELECT nodType,SUM(succNum) succTotal,sum(errNum) errTotal from  ").append(DatabaseHelper.getTableName(Table_Class)).append(" where 1 = 1 ") ;
		if( !Utils.isEmpty(renterId) ) {
			sql.append(" AND renterId= '").append(renterId).append("' ");
		}
		if( !Utils.isEmpty(flag) ) {
			String formatDate = DatabaseHelper.getDateFormatByFlag(flag) ;
			sql.append(" AND date_format(updateTime,'").append(formatDate).append("') = ").append(DatabaseHelper.getDateValueByFlag(flag, formatDate)) ;
		}
		sql.append(" GROUP BY nodType ") ;
		return  DatabaseHelper.queryList(NodeTypeNumberDto.class,  sql.toString());
	}
	
	/**
	 * 按照任务名分类获取 某个节点的运行次数 (包括节点处理数据总数)
	 * @param renterId 租户Id ,为空 统计整个系统数据
	 * @param nodeType  节点类型
	 * @param flag  year/month/day
	 * @return
	 * @throws Exception
	 */
	public static List<NodeTypeTaskTimesDto>  countTimesByTaskName( String renterId ,String nodeType , String flag) throws Exception {
		init() ;
		StringBuffer sql= new StringBuffer() ;
		sql.append(" SELECT userName,analysisName taskName ,COUNT(*) countTotal ,SUM(succNum) succTotal,sum(errNum) errTotal  from ")
		.append(DatabaseHelper.getTableName(Table_Class)).append(" where 1 = 1 ") ;
		if( !Utils.isEmpty(renterId) ) {
			sql.append(" AND renterId= '").append(renterId).append("' ");
		}
		if( !Utils.isEmpty(flag) ) {
			String formatDate = DatabaseHelper.getDateFormatByFlag(flag) ;
			sql.append(" AND date_format(updateTime,'").append(formatDate).append("') = ").append(DatabaseHelper.getDateValueByFlag(flag, formatDate)) ;
		}
		if( !Utils.isEmpty(nodeType) ) {
			sql.append(" AND nodType = '").append(nodeType).append("' ");
		}
		sql.append(" GROUP BY analysisName, userName ") ;
		return  DatabaseHelper.queryList(NodeTypeTaskTimesDto.class,  sql.toString());
	}
	
	
	
}
