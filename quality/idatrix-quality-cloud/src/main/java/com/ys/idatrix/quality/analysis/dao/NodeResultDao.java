package com.ys.idatrix.quality.analysis.dao;

import java.util.Date;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.di.core.util.UUIDUtil;
import org.pentaho.di.core.util.Utils;
import com.ys.idatrix.quality.analysis.dto.NodeResultDto;
import com.ys.idatrix.quality.dto.statistics.ReferenceNumberDto;
import com.ys.idatrix.quality.ext.CloudSession;
import com.ys.idatrix.quality.ext.utils.DatabaseHelper;


public class NodeResultDao {
	
public static final Log  logger = LogFactory.getLog("NodeResultDao");
	
	private static final Class<NodeResultDto> Table_Class = NodeResultDto.class;
	
	private static boolean isDatabaseinit = false;
	private static NodeResultDao instance ;
	
	public static void init() throws Exception {
		
		if(!isDatabaseinit) {
			DatabaseHelper.createOrUpdateTableifNonexist(Table_Class,  null);
			isDatabaseinit = true ;
		}
	}
	
	public NodeResultDao() {
		try {
			init();
		} catch (Exception e) {
		}
	}
	
	public static NodeResultDao getInstance() {
		if( instance == null ) {
			synchronized (Table_Class) {
				if( instance == null ) {
					instance = new NodeResultDao();
				}
			}
		}
		return instance;
	}
	
	private String getUserName() {
		return CloudSession.getResourceUser() ;
	}
	
	public NodeResultDto getResultById( String uuid)  throws Exception{
		return  DatabaseHelper.queryFirst(Table_Class, Table_Class , null, new String[] {"uuid"}, new String[] {"="}, new String[] {uuid}, null );
	}
	
	/**
	 * 获取分析任务下的所有数据
	 * @param analysisName
	 * @return
	 * @throws Exception
	 */
	public List<NodeResultDto> getResultByName( String analysisName)  throws Exception{
		return  DatabaseHelper.queryList(Table_Class, Table_Class , null, new String[] {"userName","analysisName"}, new String[] {"="}, new String[] {getUserName(),analysisName}, " updateTime DESC " );
	}
	
	/**
	 * 获取分析任务的某次执行的所有数据<br>
	 * @param execId
	 * @return
	 * @throws Exception
	 */
	public List<NodeResultDto> getResultByExecId( String execId)  throws Exception{
		return  DatabaseHelper.queryList(Table_Class, Table_Class , null, new String[] {"userName","execId"}, new String[] {"="}, new String[] {getUserName(),execId}, " updateTime DESC " );
	}
	
	/**
	 * 获取分析任务的某次执行的的某个节点所有数据<br>
	 * 一次运行的一个节点的所有域的参考值和数据对应list,理论条数为 域数量*参考值数量
	 * @param nodIdORType
	 * @param execId
	 * @return
	 * @throws Exception
	 */
	public List<NodeResultDto> getResultList( String execId , String nodIdORType )  throws Exception{
		return  getResultList(null, nodIdORType, execId,null, null);
	}
	
	/**
	 * 获取分析任务的某个节点的某次执行的某个参考值的所有数据<br>
	 * 一次运行的某个参考值的数据list,理论条数为 域数量
	 * @param nodIdORType
	 * @param execId
	 * @param ReferenceValue
	 * @return
	 * @throws Exception
	 */
	public List<NodeResultDto> getResultReferenceList( String execId , String nodIdORType ,String ReferenceValue )  throws Exception{
		return  getResultList(null, nodIdORType, execId,ReferenceValue,"");
	}
	
	/**
	 * 获取分析任务的某个节点的某次执行的某个参考值的统计数据,累加所有的域<br>
	 * 参数全部不为空,则为一次运行的某个参考值的统计数据
	 * @param execId
	 * @param nodIdORType
	 * @param ReferenceValue
	 * @return
	 * @throws Exception
	 */
	public NodeResultDto getResultReferenceTotal( String execId , String nodIdORType , String ReferenceValue )  throws Exception{
		List<NodeResultDto> resList = getResultReferenceList(execId, nodIdORType, ReferenceValue);
		if( resList != null && resList.size() > 0 ) { 
			if( resList.size() == 1 ) {
				return resList.get(0) ;
			}
			NodeResultDto res = resList.get(0);
			for( int i =1; i < resList.size() ; i++ ) {
				NodeResultDto resi = resList.get(i);
				if( resi == null ) {
					continue ;
				}
				res.addNumber(resi.getNumber());
			}
			return res ;
		}
		return null ;
	}
	
	
	/**
	 * 获取分析任务的某个节点的某次执行的某个域的所有数据<br>
	 * 一次运行的某个域的数据list,理论条数为 参考值数量
	 * @param execId
	 * @param nodIdORType
	 * @param fieldName
	 * @return
	 * @throws Exception
	 */
	public List<NodeResultDto> getResultFieldList(  String execId , String nodIdORType ,String fieldName )  throws Exception{
		return  getResultList(null, nodIdORType, execId,null,fieldName);
	}
	
	/**
	 * 获取分析任务的某个节点的某次执行的某个域的某个参考值的数据<br>
	 * 参数都可以为空 <br>
	 * 参数全部不为空时,理论上只有一条数据<br>
	 * @param analysisName
	 * @param nodIdORType
	 * @param execId
	 * @param referenceValue
	 * @param fieldName
	 * @return
	 * @throws Exception
	 */
	public List<NodeResultDto> getResultList( String analysisName, String nodIdORType , String execId,String ReferenceValue , String fieldName)  throws Exception{

		String sql = "WHERE userName = '"+getUserName()+"' ";
		if(!Utils.isEmpty(analysisName)) {
			sql += " AND analysisName = '"+analysisName+"'" ;
		}
		if(!Utils.isEmpty(nodIdORType)) {
			sql += " AND  ( nodeType = '"+nodIdORType+"' OR  nodId = '"+nodIdORType+"' )" ;
		}
		if(!Utils.isEmpty(execId)) {
			sql += " AND   execId = '"+execId+"'" ;
		}
		if(!Utils.isEmpty(fieldName)) {
			sql += " AND   fieldName = '"+fieldName+"'" ;
		}
		if(!Utils.isEmpty(ReferenceValue)) {
			sql += " AND   referenceValue = '"+ReferenceValue+"'" ;
		}
		
		sql += " order by updateTime DESC " ;
		
		return  DatabaseHelper.queryList(Table_Class,  sql);
	}
	
	/**
	 * 获取所有数据
	 * @return
	 * @throws Exception
	 */
	public List<NodeResultDto> getResultList( )  throws Exception{
		return  DatabaseHelper.queryList(Table_Class, Table_Class , null, null, null, null , " updateTime DESC " );
	}
	
	/**
	 * 新增结果数据<br>
	 * 新增某个分析的某次运行的某个节点的某个域的某个参考值的数量
	 * @param result
	 * @throws Exception
	 */
	public void insertResult(NodeResultDto result)  throws Exception{
		if( Utils.isEmpty(result.getUuid()) ){
			result.setUuid( UUIDUtil.getUUIDAsString() );
		}
		if( Utils.isEmpty(result.getUserName()) ){
			result.setUserName(getUserName());
		}
		if( result.getUpdateTime() == null  ){
			result.setUpdateTime(new Date());
		}
		result.setRenterId( CloudSession.getLoginRenterId() );
		DatabaseHelper.insert(Table_Class,  result );
	}
	
	/**
	 * 更新结果数据
	 * @param result
	 * @throws Exception
	 */
	public void updateResult(NodeResultDto result)  throws Exception{
		String[] queryKeys = null ;
		if( !Utils.isEmpty( result.getUuid() )) {
			queryKeys = new String[] {"uuid"} ;
		}else if( !Utils.isEmpty( result.getAnalysisName() ) && !Utils.isEmpty( result.getExecId() ) && !Utils.isEmpty( result.getNodId() )){
			queryKeys = new String[] {"analysisName","execId","nodId"} ;
		}else {
			throw new Exception("匹配条件为空,无法更新!");
		}
		result.setUpdateTime(new Date());
		DatabaseHelper.update(Table_Class, result, queryKeys );
	}
	
	/**
	 * 更新 数量
	 * @param result
	 * @throws Exception
	 */
	public void updateNumber(NodeResultDto result)  throws Exception{
		String[] queryKeys = null ;
		Object[] values = null ;
		if( !Utils.isEmpty( result.getUuid() )) {
			queryKeys = new String[] {"uuid"} ;
			values = new Object[] {result.getNumber() , new Date() ,result.getUuid()} ;
		}else if( !Utils.isEmpty( result.getAnalysisName() ) && !Utils.isEmpty( result.getExecId() ) && !Utils.isEmpty( result.getNodId() )){
			queryKeys = new String[] {"analysisName","execId","nodId"} ;
			values = new Object[] {result.getNumber() ,new Date(), result.getAnalysisName(), result.getExecId(), result.getNodId()} ;
		}else {
			throw new Exception("匹配条件为空,无法更新!");
		}
		DatabaseHelper.update( Table_Class, new String[] {"number","updateTime"},  queryKeys, new String[] {"="}, values);
	}
	
	/**
	 * 更新 数量
	 * @param result
	 * @throws Exception
	 */
	public void updateNumber(String uuid , long number)  throws Exception{
		DatabaseHelper.update( Table_Class,  new String[] {"number","updateTime"},  new String[] {"uuid"}, new String[] {"="},  new Object[] { number , new Date() ,uuid });
	}
	
	/**
	 * 删除结果数据
	 * @param uuid
	 * @throws Exception
	 */
	public void deleteResult(String uuid)  throws Exception{
		DatabaseHelper.delete(Table_Class, new String[] {"uuid"}, new String[] {"="}, new String[] {uuid});
	}
	
	

	/*============================================ Common =================================================================*/
	
	/**
	 * 按照参考值分类 , 统计 某个节点  对应的数据总数 和 任务总数  <br>
	 * @param renterId
	 * @param nodeType
	 * @param flag year/month/day
	 * @return
	 * @throws Exception
	 */
	public static List<ReferenceNumberDto> countNumberByReference(String renterId , String nodeType ,String flag )  throws Exception{
		init() ;
		//如果是标准值组件 , 按照字典名称分组
		String groupName = "CHARACTER".equalsIgnoreCase(nodeType) ?  "optional1" : "referenceValue";
		
		StringBuffer sql= new StringBuffer() ;
		sql.append(" SELECT ").append(groupName).append(" referenceValue, SUM(number) dataTotal , count( DISTINCT CONCAT(userName,analysisName) ) taskTotal from  ")
		.append(DatabaseHelper.getTableName(Table_Class)).append(" where 1 = 1 ") ;
		if( !Utils.isEmpty(renterId) ) {
			sql.append(" AND renterId= '").append(renterId).append("' ");
		}
		if( !Utils.isEmpty(flag) ) {
			String formatDate = DatabaseHelper.getDateFormatByFlag(flag) ;
			sql.append(" AND date_format(updateTime,'").append(formatDate).append("') = ").append(DatabaseHelper.getDateValueByFlag(flag, formatDate)) ;
		}
		if( !Utils.isEmpty(nodeType) ) {
			sql.append(" AND nodeType = '").append(nodeType).append("' ");
		}
		sql.append(" GROUP BY ").append(groupName) ;
		return  DatabaseHelper.queryList(ReferenceNumberDto.class,  sql.toString());
		
	}
	
	
}
