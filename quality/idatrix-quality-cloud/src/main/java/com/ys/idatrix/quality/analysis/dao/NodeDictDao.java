package com.ys.idatrix.quality.analysis.dao;

import com.google.common.collect.Lists;
import com.ys.idatrix.quality.analysis.dto.NodeDictDto;
import com.ys.idatrix.quality.ext.CloudSession;
import com.ys.idatrix.quality.ext.utils.DatabaseHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.di.core.util.UUIDUtil;
import org.pentaho.di.core.util.Utils;

import java.util.Date;
import java.util.List;


public class NodeDictDao {
	
	public static final Log  logger = LogFactory.getLog("NodeDictDao");

	private static final Class<NodeDictDto> Table_Class = NodeDictDto.class;

	private static boolean isDatabaseinit = false;
	private static NodeDictDao instance ;

	public static void init() throws Exception {
		if(!isDatabaseinit) {
			DatabaseHelper.createOrUpdateTableifNonexist(Table_Class, null);
			isDatabaseinit = true ;
		}
	}

	public NodeDictDao() {
		try {
			init();
		} catch (Exception e) {
		}
	}

	public static NodeDictDao getInstance() {
		if( instance == null ) {
			synchronized (Table_Class) {
				if( instance == null ) {
					instance = new NodeDictDao();
				}
			}
		}
		return instance;
	}

	public String getCurrnetRenterId() {
		return CloudSession.getLoginRenterId() ;
	}
	
	public NodeDictDto getDictById(String id)  throws Exception{
		return DatabaseHelper.queryFirst(Table_Class, Table_Class , null, new String[] {"id"}, new String[] {"="}, new String[] {id }, null );
	}

	public NodeDictDto getDictByName(String dictName)  throws Exception{
		return  DatabaseHelper.queryFirst(Table_Class, Table_Class , null, new String[] {"dictName"}, new String[] {"="}, new String[] {dictName}, null );
	}

	public List<NodeDictDto> getDictList()  throws Exception{
		StringBuffer sql = new StringBuffer();
		sql.append( "select * from " ).append( DatabaseHelper.getTableName(Table_Class) ).append( " where ").append(getRenterIdSql()).append(" order by updateTime DESC ");
		return DatabaseHelper.queryList(Table_Class, sql.toString());
	}

	public List<NodeDictDto> getDictListByStatus(Integer status)  throws Exception{
		if(status == null){
			throw new Exception("查询参数不能为空");
		}
		StringBuffer sql = new StringBuffer();
		sql.append("select * from ").append(DatabaseHelper.getTableName(Table_Class)).append(" where status = " ).append( status).append( " and ").append(getRenterIdSql()).append(" order by updateTime DESC ");
		return DatabaseHelper.queryList(Table_Class, sql.toString());
	}


	public List<NodeDictDto> getDictListBySearch(String name , String stdVal)  throws Exception{
		StringBuffer sql = new StringBuffer();
		sql.append(" select * from  ").append(DatabaseHelper.getTableName(Table_Class)).append(" where ").append( getRenterIdSql() );
		if( !Utils.isEmpty(stdVal)){
			sql.append(" and stdVal1 = '").append(stdVal).append("'");
		}
		if( !Utils.isEmpty(name)){
			sql.append(" and dictName = '").append(name).append("'");
		}
		sql.append(" order by updateTime DESC");
		
		return DatabaseHelper.queryList(Table_Class, sql.toString());
	}


	public void insertDict(NodeDictDto dict)  throws Exception{
		if (Utils.isEmpty(dict.getId())) {
			dict.setId(UUIDUtil.getUUIDAsString());
		}
		String user = CloudSession.getLoginUser();
		dict.setCreator(user);
		dict.setRenterId(CloudSession.getLoginRenterId());

		if(dict.getStatus() == null ) {
			dict.setStatus(0L);
		}
		if(dict.isShare() == null ) {
			dict.setShare(false);
		}
		
		dict.setAddTime(new Date());
		dict.setUpdateTime(new Date());
		DatabaseHelper.insert(Table_Class,  dict);
	}

	public void updateDict(NodeDictDto dict)  throws Exception{
		//不能更新 创建者和新增时间
		List<String> setFields = Lists.newArrayList();
		List<String> whereFields = Lists.newArrayList();
		List<Object> values = Lists.newArrayList();
		if(dict.getDictName() != null) {
			setFields.add("dictName");
			values.add(dict.getDictName());
		}
		if(dict.getDictDesc() != null) {
			setFields.add("dictDesc");
			values.add(dict.getDictDesc());
		}
		if(dict.getStatus() != null ) {
			setFields.add("status");
			values.add(dict.getStatus());
		}
		
		if(dict.isShare() != null ) {
			setFields.add("share");
			values.add(dict.isShare());
		}
		
		setFields.add("updateTime");
		values.add(new Date());
		
		setFields.add("modifier");
		values.add(CloudSession.getLoginUser());
		
		whereFields.add("id");
		values.add(dict.getId());
		 
		DatabaseHelper.update(Table_Class, setFields.toArray(new String[0]), whereFields.toArray(new String[0]), new String[] {"="}, values.toArray(new Object[0]));
	}

	public void updateDictActiveStatus(NodeDictDto dataDict) throws Exception {
		String[] queryKeys = null;
		Object[] values = null;
		if(!StringUtils.isEmpty(dataDict.getId())){
			queryKeys = new String[]{"id"};
			values = new Object[] { dataDict.getUpdateTime(), dataDict.getStatus(), dataDict.getActiveTime(), dataDict.getModifier(), dataDict.getId()};
		} else {
			throw new Exception("匹配条件为空,无法更新!");
		}
		DatabaseHelper.update(Table_Class, new String[] {"updateTime", "status", "activeTime", "modifier"},  queryKeys, new String[] {"="}, values);
	}

	public void deleteDict(String uuid)  throws Exception{
		DatabaseHelper.delete(Table_Class, new String[] {"id"}, new String[] {"="}, new String[] {uuid});
	}

	public List<NodeDictDto> getDictListBySearch(String dictName , Integer page, Integer size)  throws Exception{
		StringBuffer sql = new StringBuffer();
		sql.append( "select * from ").append( DatabaseHelper.getTableName(Table_Class) ).append(whereSql(dictName)).append(" order by updateTime DESC ");
		if( page > 0 ) {
			sql.append( " limit " ).append( (page-1)*size ).append(", " ).append( size ).append( ";" );
		}
		List<NodeDictDto> list = DatabaseHelper.queryList(Table_Class, sql.toString());
		return list;
	}

	public Long getDictCount(String dictName) throws Exception {
		StringBuffer sql = new StringBuffer();
		sql.append(  "select count(*) from " ).append( DatabaseHelper.getTableName(Table_Class) ).append(whereSql(dictName));
		Long conut = DatabaseHelper.queryFirst(Long.class, sql.toString());
		return conut == null ? 0L: conut;
	}

	public String whereSql(String dictName) {
		String whereSql = " where "+getRenterIdSql();
		if(StringUtils.isNotEmpty(dictName)){
			whereSql += " and dictName like '%" + dictName + "%'";
		}
		return whereSql;
	}

//	public List<String> getDictGroupName() throws Exception {
//		String sql = "select dictName from " + Table_NAME + " where id NOT IN ('1', '2', '3') group by dictName";
//		List<String> result = DatabaseHelper.pageList(String.class, sql);
//		return result;
//	}
//
//	public List<NodeDictDto> findDictInfoByDictName(String dictName) throws Exception {
//		String sql = "select * from "+ Table_NAME + " where id NOT IN ('1', '2', '3') and dictName = '" + dictName +"'";
//		List<NodeDictDto> result = DatabaseHelper.pageList(NodeDictDto.class, sql);
//		return result;
//	}

	public Boolean isExistDictName(String dictName) throws Exception {
		if( Utils.isEmpty(dictName)) {
			return false ;
		}
		StringBuffer sql = new StringBuffer();
		sql.append(  "select count(*) from " ).append( DatabaseHelper.getTableName(Table_Class) ).append( " where ").append(  "  dictName = '" ).append( dictName ).append("'" );
		Long count = DatabaseHelper.queryFirst(Long.class, sql.toString());
		return ( count!= null&&count > 0 ) ? true:false;
	}

	
	private String getRenterIdSql() {
		StringBuffer sql = new StringBuffer();
		sql.append(" ( share = 'Y' ");
		if( !Utils.isEmpty( getCurrnetRenterId() )) {
			sql.append(" or renterId = '"+ getCurrnetRenterId() ).append("' ") ;
		}
		sql.append( " )");
		return sql.toString() ;
	}
	
}
