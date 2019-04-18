package com.ys.idatrix.cloudetl.ext.utils;

import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.di.core.util.IdatrixPropertyUtil;
import org.pentaho.di.core.util.OsgiBundleUtils;
import org.pentaho.di.core.util.Utils;
import org.pentaho.pms.util.Const;
import org.pentaho.di.core.database.Database;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleDatabaseException;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaBigNumber;
import org.pentaho.di.core.row.value.ValueMetaBinary;
import org.pentaho.di.core.row.value.ValueMetaBoolean;
import org.pentaho.di.core.row.value.ValueMetaDate;
import org.pentaho.di.core.row.value.ValueMetaInteger;
import org.pentaho.di.core.row.value.ValueMetaString;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.ys.idatrix.cloudetl.dto.common.PaginationDto;
import com.ys.idatrix.cloudetl.ext.CloudApp;

import javassist.Modifier;

/**
 * 操作内置数据库工具类 <br>
 * 
 * query* 为查询方法 : condition: BETWEEN,IS NULL,IS NOT NULL,=,.....
 *
 * @author XH
 * @since 2018年9月27日
 *
 */
public class DatabaseHelper {

	public static final Log logger = LogFactory.getLog("DatabaseHelper");

	private final static String GET_OBJECT_METHON_NAME = "objectToString";
	private final static String SET_OBJECT_METHON_NAME = "stringToObject";

	@Target(ElementType.TYPE) 
	@Retention(RetentionPolicy.RUNTIME)
	public @interface FieldUpperCase {}
	
	@Target(ElementType.FIELD) 
	@Retention(RetentionPolicy.RUNTIME)
	public @interface FieldLength {
		int length() default -1;
	}
	
	@Target(ElementType.FIELD) 
	@Retention(RetentionPolicy.RUNTIME)
	public @interface IgnoreField {
		/**
		 * 静态方法名,返回false ,不忽略,否则忽略
		 * @return
		 */
		String conditionMethod() default "";
	}
	

	private final static DatabaseMeta databaseMeta = CloudApp.getInstance().getCloudDatabaseMeta();

	private static ThreadLocal<Database> batchCommits = new ThreadLocal<Database>();

	public static void setCommitSize(Integer commit) {

		Database database = CloudApp.getInstance().getCloudDatabase();
		if (database == null) {
			return;
		}
		database.setCommit(commit);
		batchCommits.set(database);
	}

	public static void closeBatchCommit() {
		Database database = batchCommits.get();
		if (database != null) {
			if (!database.isAutoCommit()) {
				try {
					database.commit();
				} catch (KettleDatabaseException e) {
				}
			}
			database.disconnect();
		}
		batchCommits.remove();
	}

	
	public static void rollbackBatchCommit() {
		Database database = batchCommits.get();
		if (database != null) {
			try {
				database.rollback();
			} catch (KettleDatabaseException e) {
			}
			database.disconnect();
		}
		batchCommits.remove();
	}
	
	/**
	 * 查询 单条 数据,多条结果返回第一条
	 * 
	 * @param tableClass
	 * @param tableName
	 * @param sql
	 *            查询sql , 可以只写 WHERE开头的条件部分,字段和表名自动拼接
	 * @return
	 * @throws Exception
	 */
	public static <T> T queryFirst(Class<T> resultClass,  String sql) throws Exception {
		List<T> result = queryList(resultClass, sql, true);
		if (result != null && result.size() > 0) {
			return result.get(0);
		}
		return null;
	}

	/**
	 * 查询列表 <br>
	 * 
	 * @param tableClass
	 * @param tableName
	 * @param sql
	 *            查询sql , 可以只写 WHERE开头的条件部分,字段和表名自动拼接
	 * @return
	 * @throws Exception
	 */
	public static <T> List<T> queryList(Class<T> resultClass, String sql) throws Exception {
		return queryList(resultClass, sql, false);
	}

	/**
	 * 查询 单条 数据,多条结果返回第一条 <br>
	 * 只支持 AND(与条件)
	 * 
	 * @param tableClass
	 * @param tableName
	 * @param fields
	 *            查询的域字段,可为空
	 * @param whereFields
	 *            条件域字段 ,可为空
	 * @param condition
	 *            条件,默认 =,可省略,省略部分填充最后一个值,支持 (特殊条件符:) BETWEEN , IS NULL , IS NOT
	 *            NULL , (直接可用条件符:) = , < , ...
	 * @param whereValues
	 *            条件值字段
	 * @param orderby
	 *            排序字段 ,即order by 后面的部分
	 * @return
	 * @throws Exception
	 */
	public static <T> T queryFirst(Class<T> resultClass,Class<?> tableClass, String[] fields, String[] whereFields,
			String[] condition, Object[] whereValues, String orderby) throws Exception {
		List<T> result = queryList(resultClass,tableClass, fields, whereFields, condition, whereValues, orderby, null, null, true);
		if (result != null && result.size() > 0) {
			return result.get(0);
		}
		return null;
	}

	/**
	 * 查询列表 <br>
	 * 只支持 AND(与条件)
	 * 
	 * @param tableClass
	 * @param tableName
	 * @param fields
	 *            查询的域字段,可为空
	 * @param whereFields
	 *            条件域字段 ,可为空
	 * @param condition
	 *            条件,默认 =,可省略,省略部分填充最后一个值,支持 (特殊条件符:) BETWEEN , IS NULL , IS NOT
	 *            NULL , (直接可用条件符:) = , < , ...
	 * @param whereValues
	 *            条件值字段
	 * @param orderby
	 *            排序字段 ,即order by 后面的部分
	 * @return
	 * @throws Exception
	 */
	public static <T> List<T> queryList(Class<T> resultClass,Class<?> tableClass, String[] fields, String[] whereFields,
			String[] condition, Object[] whereValues, String orderby) throws Exception {
		return queryList(resultClass,tableClass, fields, whereFields, condition, whereValues, orderby, null, null,
				false);
	}
	
	
	@SuppressWarnings("unchecked")
	public static <T> PaginationDto<T> queryPageList(Class<T> resultClass,Class<?> tableClass, String[] selectFields,
			String whereSql, String orderby, Integer pageNo, Integer pageSize) throws Exception {
		//初始化处理参数
		
		if( tableClass == null ) {
			tableClass = resultClass ;
		}
		if( resultClass == null ) {
			resultClass = (Class<T>) tableClass ;
		}
		pageNo = pageNo == null ? 1 : pageNo;
		pageSize = pageSize == null ? 10 : pageSize;
		
		String tableName = getTableName(tableClass) ;
		StringBuffer selectSql = new StringBuffer();
		if ( whereSql.toUpperCase().trim().startsWith("WHERE") ) {
			if ( selectFields == null || selectFields.length == 0 ) {
				selectFields = getFields(tableClass);
			}
			selectSql.append("SELECT ");
			for (int i = 0; i < selectFields.length; i++) {
				if (i != 0) {
					selectSql.append(", ");
				}
				selectSql.append( databaseMeta.quoteField(selectFields[i]) );
			}
			selectSql.append(" FROM ").append( tableName ).append(" ") ;
			selectSql.append(whereSql).append(" ");
		}else if ( whereSql.toUpperCase().trim().startsWith("SELECT") && whereSql.toUpperCase().trim().contains("WHERE")){
			selectSql.append(whereSql) ;
			int whereIndex =  whereSql.indexOf("where") ;
			whereIndex =  whereIndex==-1 ?  whereSql.indexOf("WHERE") : whereIndex ;
			whereSql =  whereSql.substring(whereIndex );
		}
		// 分页 , 只适应 Mysql limit (pageNo-1)*pageSize,pageSize;
		if (Utils.isEmpty(orderby)) {
			Database database = CloudApp.getInstance().getCloudDatabase();
			String[] pks = database.getPrimaryKeyColumnNames(tableName);
			if (pks != null && pks.length > 0) {
				orderby = pks[0] + " DESC  ";
			}else {
				orderby = " ";
			}
		}
		if ( !Utils.isEmpty(orderby)) {
			selectSql.append(" ORDER BY ").append(orderby);
		}
		if (pageNo != null && pageNo > 0 && pageSize != null) {
			selectSql.append(" LIMIT ").append( ((pageNo - 1) * pageSize)).append(",").append(pageSize);
		}
		
		//返回结果对象
		PaginationDto<T> result = new PaginationDto<T>(pageNo, pageSize,"");
		//获取 rows
		List<T> list =  queryList(resultClass, selectSql.toString());
		result.setRows(list);
		
		//获取total
		StringBuffer countSql = new StringBuffer();
		countSql.append(  "SELECT COUNT(*) FROM " ).append( tableName ).append( " ").append(whereSql);
		if ( !Utils.isEmpty(orderby)) {
			countSql.append(" ORDER BY ").append(orderby);
		}
	    Long total = DatabaseHelper.queryFirst(Long.class,  countSql.toString());
		result.setTotal(total.intValue());
		
		return result;
	}

	/**
	 * 分页查询 列表
	 * 
	 * @param tableClass
	 * @param tableName
	 * @param fields
	 *            查询的域字段,可为空
	 * @param whereFields
	 *            条件域字段 ,可为空
	 * @param condition
	 *            条件,默认 =,可省略,省略部分填充最后一个值,支持 (特殊条件符:) BETWEEN , IS NULL , IS NOT
	 *            NULL , (直接可用条件符:) = , < , ...
	 * @param whereValues
	 *            条件值字段
	 * @param orderby
	 *            排序字段 ,即order by 后面的部分,不能为空,默认使用 主键
	 * @param pageNo
	 * @param pageSize
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static <T> PaginationDto<T> queryPageList(Class<T> resultClass,Class<?> tableClass, String[] fields,
			String[] whereFields, String[] condition, Object[] whereValues, String orderby, Integer pageNo,
			Integer pageSize) throws Exception {
		
		//初始化处理参数
		if( tableClass == null ) {
			tableClass = resultClass ;
		}
		if( resultClass == null ) {
			resultClass = (Class<T>) tableClass ;
		}
		pageNo = pageNo == null ? 1 : pageNo;
		pageSize = pageSize == null ? 10 : pageSize;
		whereFields = whereFields == null ? new String[] {} : whereFields;
		if (whereFields.length > condition.length && whereFields.length > 0) {
			// 比较符填充
			int oldLen = condition.length;
			String defaultCondition = oldLen > 0 ? condition[oldLen - 1] : "=";
			condition = Arrays.copyOf(condition, whereFields.length);
			Arrays.fill(condition, oldLen, condition.length, defaultCondition);
		}
		//返回结果对象
		PaginationDto<T> result = new PaginationDto<T>(pageNo, pageSize,"");
		//获取 rows
		List<T> list = queryList(resultClass ,tableClass,  fields, whereFields, condition, whereValues, orderby, pageNo, pageSize,false);
		result.setRows(list);
		
		//获取total
		StringBuffer sql = new StringBuffer();
		sql.append(  "SELECT COUNT(*) FROM " ).append( getTableName(tableClass) ).append( " WHERE 1 = 1 ");
		int j = 0 ;
	    for ( int i = 0; i < whereFields.length; i++ ) {
	        sql.append(" AND ");
	        sql.append( databaseMeta.quoteField( whereFields[ i ] ) );
	        if ( "BETWEEN".equalsIgnoreCase( condition[ i ] ) ) {
	          sql.append( " BETWEEN '").append(whereValues[j++]).append("' ").append(" AND  '" ).append(whereValues[j++]).append("' ");
	        } else if ( "IS NULL".equalsIgnoreCase( condition[ i ] ) || "IS NOT NULL".equalsIgnoreCase( condition[ i ] ) ) {
	          sql .append( condition[ i ]  ).append(" ");
	        } else {
	          sql.append( condition[ i ] ).append(" '").append(whereValues[j++]).append("' ");
	        }
	     }
	     if ( orderby != null && orderby.length() != 0 ) {
	        sql.append(" ORDER BY ").append( orderby );
	     }
	     Long total = DatabaseHelper.queryFirst(Long.class,  sql.toString());
		result.setTotal(total.intValue());
		
		return result;
	}

	/**
	 * 查询列表 <br>
	 * 只支持 AND(与条件)
	 * 
	 * @param tableClass
	 * @param tableName
	 * @param fields
	 * @param whereFields
	 * @param condition
	 *            条件,默认 =,可省略,省略部分填充最后一个值,支持 (特殊条件符:) BETWEEN , IS NULL , IS NOT
	 *            NULL , (直接可用条件符:) = , < , ...
	 * @param whereValues
	 * @param orderby
	 * @param isOne
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private static <T> List<T> queryList(Class<T> resultClass,Class<?> tableClass, String[] fields,
			String[] whereFields,String[] condition, Object[] whereValues, 
			String orderby, Integer pageNo, Integer pageSize, boolean isOne)
			throws Exception {

		Database database = CloudApp.getInstance().getCloudDatabase();
		if (database == null) {
			return null;
		}
		if( tableClass == null ) {
			tableClass = resultClass ;
		}
		if( resultClass == null ) {
			resultClass = (Class<T>) tableClass ;
		}
		
		List<T> result = Lists.newArrayList();
		try {
			
			String tableName = getTableName(tableClass) ;

			fields = fields == null ? getFields(tableClass) : fields;
			whereFields = whereFields == null ? new String[] {} : whereFields;
			condition = condition == null ? new String[] {} : condition;
			if (whereFields.length > condition.length && whereFields.length > 0) {
				// 比较符填充
				int oldLen = condition.length;
				String defaultCondition = oldLen > 0 ? condition[oldLen - 1] : "=";
				condition = Arrays.copyOf(condition, whereFields.length);
				Arrays.fill(condition, oldLen, condition.length, defaultCondition);
			}

			if (pageNo != null && pageNo > 0 && pageSize != null) {
				// 分页 , 只适应 Mysql limit (pageNo-1)*pageSize,pageSize;
				if (Utils.isEmpty(orderby)) {
					String[] pks = database.getPrimaryKeyColumnNames(tableName);
					if (pks != null && pks.length > 0) {
						orderby = pks[0] + " DESC  ";
					}
				}
				orderby += " LIMIT " + ((pageNo - 1) * pageSize) + "," + pageSize;
			}

			database.setLookup(tableName, whereFields, condition, fields, null, orderby, !isOne);
			RowMeta parameterRowMeta = new RowMeta();
			if (whereFields != null && whereFields.length > 0) {
				for (String name : whereFields) {
					Field f = OsgiBundleUtils.seekOsgiField(tableClass, name, true);
					ValueMetaInterface meta = grenentValurMeta(f, isFieldUpperCase(tableClass));
					if (meta != null) {
						parameterRowMeta.addValueMeta(meta);
					}
				}
			}
			if (whereValues != null) {
				database.setValuesLookup(parameterRowMeta, whereValues);
			}

			PreparedStatement prepStatement = database.getPrepStatementLookup();
			ResultSet rs = prepStatement.executeQuery();

			result = getResultList(resultClass, database, rs, isOne);

		} finally {
			database.disconnect();
		}
		return result;

	}

	/**
	 * 查询列表 <br>
	 * 
	 * @param tableClass
	 * @param tableName
	 * @param sql
	 * @param isOne
	 * @return
	 * @throws Exception
	 */
	private static <T> List<T> queryList(Class<T> resultClass, String sql, boolean isOne)
			throws Exception {

		Database database = CloudApp.getInstance().getCloudDatabase();
		if (database == null) {
			return null;
		}
		List<T> result = null;
		PreparedStatement prepStatement = null ;
		try {
			
			if (sql.startsWith("where") || sql.startsWith("WHERE")) {
				String[] fields = getFields(resultClass);

				String selectSql = "SELECT ";

				for (int i = 0; i < fields.length; i++) {
					if (i != 0) {
						selectSql += ", ";
					}
					selectSql += databaseMeta.quoteField(fields[i]);
				}

				String tableName = getTableName(resultClass) ;
				sql = selectSql + " FROM " + tableName + " " + sql;
			}

			prepStatement = database.getConnection().prepareStatement(databaseMeta.stripCR(sql));
			ResultSet rs = prepStatement.executeQuery();

			result = getResultList(resultClass, database, rs, isOne);
		} finally {
			database.closePreparedStatement(prepStatement);
			database.disconnect();
		}
		return result;

	}

	@SuppressWarnings("unchecked")
	private static <T> List<T> getResultList(Class<T> resultClass, Database database, ResultSet rs, boolean isOne)
			throws Exception {

		List<T> result = Lists.newArrayList();
		
		Object[] data = database.getRow(rs);
		String[] returnFields = database.getReturnRowMeta() != null ? database.getReturnRowMeta().getFieldNames(): null;
		RowMetaInterface returnRowMeta = database.getReturnRowMeta();
		while (data != null) {
			
			T t;
			switch (resultClass.getSimpleName()) {
			case "Boolean":
			case "boolean":
				if( returnRowMeta != null ) {
					t = (T) returnRowMeta.getBoolean(data,0);
				}else {
					 Object d = data[0] ;
					 if( d instanceof Integer ) {
						 if( (Integer)d != 0 ) {
							 t = (T) Boolean.TRUE ;
						 }else {
							 t = (T) Boolean.FALSE ;
						 }
					 }else if ( d instanceof String ){
						 if( "Y".equalsIgnoreCase((String)d) || "true".equalsIgnoreCase((String)d)  ||  "0".equalsIgnoreCase((String)d))  {
							 t = (T) Boolean.TRUE ;
						 }else {
							 t = (T) Boolean.FALSE ;
						 }
					 }else {
						 t= (T) data[0] ;
					 }
				}
				break ;
			case "Integer":
			case "int": 
				t = returnRowMeta!= null ? (T) (Integer)returnRowMeta.getInteger(data, 0).intValue() : (T) data[0] ;
				break ;
			case "Long":
			case "long":
				t = returnRowMeta!= null ? (T) returnRowMeta.getInteger(data, 0) : (T) data[0] ;
				break ;
			case "double":
			case "Double":
				t = returnRowMeta!= null ? (T) returnRowMeta.getNumber(data, 0) : (T) data[0] ;
				break ;
			case "float":
			case "Float":
				t = returnRowMeta!= null ? (T) (Float)returnRowMeta.getNumber(data, 0).floatValue() : (T) data[0] ;
				break ;
			case "byte[]":
			case "Byte[]":
				t = returnRowMeta!= null ? (T) returnRowMeta.getBinary(data, 0) : (T) data[0] ;
				break ;
			case "Date":
				t = returnRowMeta!= null ? (T) returnRowMeta.getDate(data, 0) : (T) data[0] ;
				break ;
			case "String":
				t = returnRowMeta!= null ? (T) returnRowMeta.getString(data, 0) : (T) data[0] ;
				break ;
			case "StringBuffer":
				String tt = returnRowMeta!= null ? returnRowMeta.getString(data, 0) : (String) data[0] ;
				t = (T) new  StringBuffer(tt != null ? tt : "" ) ;
				break ;
			default:
				t = resultClass.newInstance();
				List<Field> fields = OsgiBundleUtils.seekOsgiFields(resultClass);
				for (int i = 0; i < fields.size(); i++) {
					Field f = fields.get(i);
					if ( ( f.getModifiers() < 8 || Modifier.isVolatile( f.getModifiers())) ) {
						// 只需要 private protected,public 三种修饰符的域才获取
						int j = 0;
						for (; j < returnFields.length; j++) {
							if (f.getName().equalsIgnoreCase(returnFields[j])) {
								break;
							}
						}
						if (j < data.length) {
							setValueToObject(t, f, returnRowMeta!= null?returnRowMeta.getValueMeta(j):null ,data[j]);
						}
					}
				}
			}
			result.add(t);

			if (isOne) {
				break;
			}
			data = database.getRow(rs);
		}

		database.closeQuery(rs);

		return result;
	}

	/**
	 * 删除数据
	 * 
	 * @param tableClass
	 * @param tableName
	 * @param whereFields
	 *            条件域字段 ,可为空
	 * @param condition
	 *            条件,默认 =,可省略,省略部分填充最后一个值,支持 (特殊条件符:) BETWEEN , IS NULL , IS NOT
	 *            NULL , (直接可用条件符:) = , < , ...
	 * @param whereValues
	 *            条件值字段
	 * @throws Exception
	 */
	public static <T> void delete(Class<T> tableClass, String[] whereFields, String[] condition,
			Object[] whereValues) throws Exception {

		Database database = CloudApp.getInstance().getCloudDatabase();
		if (database == null) {
			return;
		}

		try {
			String tableName = getTableName(tableClass);
			
			whereFields = whereFields == null ? new String[] {} : whereFields;
			condition = condition == null ? new String[] {} : condition;
			if (whereFields.length > condition.length && whereFields.length > 0) {
				// 比较符填充
				int oldLen = condition.length;
				String defaultCondition = oldLen > 0 ? condition[oldLen - 1] : "=";
				condition = Arrays.copyOf(condition, whereFields.length);
				Arrays.fill(condition, oldLen, condition.length, defaultCondition);
			}

			database.prepareDelete(tableName, whereFields, condition);
			RowMeta parameterRowMeta = new RowMeta();
			if (whereFields != null && whereFields.length > 0) {
				for (String name : whereFields) {
					ValueMetaInterface meta = grenentValurMeta(OsgiBundleUtils.seekOsgiField(tableClass, name, true),
							isFieldUpperCase(tableClass));
					if (meta != null) {
						parameterRowMeta.addValueMeta(meta);
					}
				}
			}
			if (whereValues != null) {
				database.setValuesUpdate(parameterRowMeta, whereValues);
			}
			database.updateRow();
		} finally {
			database.disconnect();
		}
	}

	/**
	 * 新增插入数据
	 * 
	 * @param tableClass
	 * @param tableName
	 * @param t
	 *            需要插入的DTO对象
	 * @throws Exception
	 */
	public static <T> void insert(Class<T> tableClass,  T t) throws Exception {

		Database database = null;
		if (batchCommits.get() != null) {
			database = batchCommits.get();
		} else {
			database = CloudApp.getInstance().getCloudDatabase();
		}
		if (database == null) {
			return;
		}
		try {
			String tableName = getTableName(tableClass);
			
			Field returnField = null ;
			
			RowMetaInterface rowMeta = new RowMeta();
			List<Object> data = Lists.newArrayList();
			List<Field> fields = OsgiBundleUtils.seekOsgiFields(tableClass);
			for (Field f : fields) {
				if ( ( f.getModifiers() < 8 || Modifier.isVolatile( f.getModifiers())) ) {
					// 公有 , 保护,私有
						//主键
					if(  f.getAnnotation(GeneratedValue.class) != null ) {
						//自增 数字
						returnField = f ;
					}
					ValueMetaInterface meta = grenentValurMeta(f, isFieldUpperCase(tableClass));
					if (meta != null) {
						rowMeta.addValueMeta(meta);
						data.add(getValueFromObject(t, f));
					}
				}
			}
			if(returnField == null) {
				database.prepareInsert(rowMeta, tableName);
				database.setValuesInsert(rowMeta, data.toArray());
				database.insertRow();
			}else {
				String sql = database.getInsertStatement( tableName, rowMeta);
				PreparedStatement prepStatementInsert = database.prepareSQL(sql,true);// 956
				database.setValues(rowMeta, data.toArray(),prepStatementInsert);
			
				database.insertRow( prepStatementInsert );
				ResultSet generatedKeys = prepStatementInsert.getGeneratedKeys();
				if (generatedKeys!= null && generatedKeys.next()) {
				    long id = generatedKeys.getLong(1);
				    setValueToObject(t, returnField, new ValueMetaInteger(), id);
				}
			}
			
		} finally {
			if (batchCommits.get() == null) {
				database.disconnect();
			}
		}
	}

	/**
	 * 更新数据
	 * 
	 * @param tableClass
	 * @param tableName
	 * @param setFields
	 *            需要更新的域名列表
	 * @param whereFields
	 *            匹配的查询域名列表
	 * @param condition
	 *            条件,默认 =,可省略,省略部分填充最后一个值,支持 (特殊条件符:) BETWEEN , IS NULL , IS NOT
	 *            NULL , (直接可用条件符:) = , < , ...
	 * @param values
	 *            更新域值和查询域值, 顺序为 : 更新域值,查询域值
	 * @throws Exception
	 */
	public static <T> void update(Class<T> tableClass,  String[] setFields, String[] whereFields,
			String[] condition, Object[] values) throws Exception {

		Database database = null;
		if (batchCommits.get() != null) {
			database = batchCommits.get();
		} else {
			database = CloudApp.getInstance().getCloudDatabase();
		}
		if (database == null) {
			return;
		}

		try {
			String tableName = getTableName(tableClass);
			
			if (setFields == null || setFields.length == 0) {
				return;
			}
			whereFields = whereFields == null ? new String[] {} : whereFields;
			condition = condition == null ? new String[] {} : condition;
			if (whereFields.length > condition.length && whereFields.length > 0) {
				// 比较符填充
				int oldLen = condition.length;
				String defaultCondition = oldLen > 0 ? condition[oldLen - 1] : "=";
				condition = Arrays.copyOf(condition, whereFields.length);
				Arrays.fill(condition, oldLen, condition.length, defaultCondition);
			}

			database.prepareUpdate(tableName, whereFields, condition, setFields);

			RowMeta parameterRowMeta = new RowMeta();
			for (String setName : setFields) {
				ValueMetaInterface meta = grenentValurMeta(OsgiBundleUtils.seekOsgiField(tableClass, setName, true),
						isFieldUpperCase(tableClass));
				if (meta != null) {
					parameterRowMeta.addValueMeta(meta);
				}
			}
			if (whereFields != null && whereFields.length > 0) {
				for (String name : whereFields) {
					ValueMetaInterface meta = grenentValurMeta(OsgiBundleUtils.seekOsgiField(tableClass, name, true),
							isFieldUpperCase(tableClass));
					if (meta != null) {
						parameterRowMeta.addValueMeta(meta);
					}
				}
			}
			if (values != null) {
				database.setValuesUpdate(parameterRowMeta, values);
			}
			database.updateRow();
		} finally {
			if (batchCommits.get() == null) {
				database.disconnect();
			}
		}
	}

	/**
	 * 更新数据
	 * 
	 * @param tableClass
	 * @param tableName
	 * @param t
	 *            需要更新的dto
	 * @param queryKeys
	 *            用来匹配的域字段名,可为空,为空时匹配字段为主键
	 * @throws Exception
	 */
	public static <T> void update(Class<T> tableClass,  T t, String... queryKeys) throws Exception {
		Database database = null;
		if (batchCommits.get() != null) {
			database = batchCommits.get();
		} else {
			database = CloudApp.getInstance().getCloudDatabase();
		}
		if (database == null) {
			return;
		}

		try {
			String tableName = getTableName(tableClass);
			
			String[] pks = queryKeys;
			if (queryKeys == null || queryKeys.length == 0) {
				pks = database.getPrimaryKeyColumnNames(tableName);
				if (pks == null || pks.length == 0) {
					return;
				}
			}
			List<String> primarykeys = Lists.newArrayList(pks).stream().map(String::toUpperCase).collect(Collectors.toList());

			RowMetaInterface rowMeta = new RowMeta();
			List<Object> data = Lists.newArrayList();

			List<String> setFields = Lists.newArrayList();
			List<String> whereFields = Lists.newArrayList();
			List<String> condition = Lists.newArrayList();

			List<Field> fields = OsgiBundleUtils.seekOsgiFields(tableClass);// 当前类的属性类型
			for (Field f : fields) {
				if ( ( f.getModifiers() < 8 || Modifier.isVolatile( f.getModifiers())) ) {
					// 公有 , 保护,私有
					ValueMetaInterface meta = grenentValurMeta(f, isFieldUpperCase(tableClass));
					if (meta != null) {
						String name = f.getName();
						// if (primarykeys.contains(name.toUpperCase())) {
						if (primarykeys.contains(name.toUpperCase())) {
							// 主键 where 域
							whereFields.add(name);
							condition.add(" = ");
							rowMeta.addValueMeta(meta);
							data.add(getValueFromObject(t, f));
						} else {
							// set 域
							setFields.add(0, name);
							rowMeta.addValueMeta(0, meta);
							data.add(0, getValueFromObject(t, f));
						}
					}
				}
			}

			database.prepareUpdate(tableName, whereFields.toArray(new String[] {}), condition.toArray(new String[] {}),
					setFields.toArray(new String[] {}));
			database.setValuesUpdate(rowMeta, data.toArray());
			database.updateRow();
		} finally {
			if (batchCommits.get() == null) {
				database.disconnect();
			}
		}

	}

	/**
	 * 创建或者更新表 <br>
	 * 当不存在时 创建表 <br>
	 * 当 表存在并且idatrix.database.update.table=true时 更新表
	 * 
	 * @param tableClass
	 * @param tableName
	 * @param primaryKeyName
	 * @param initData
	 * @throws Exception
	 */
	public static <T> void createOrUpdateTableifNonexist(Class<T> tableClass,  List<T> initData)
			throws Exception {
		Database database = CloudApp.getInstance().getCloudDatabase();
		if (database == null) {
			return;
		}
		
		String tableName = getTableName(tableClass);
		
		if (!database.checkTableExists(tableName)) {
			boolean result = createOrUpdateTable(tableClass, tableName);
			if( result && initData != null && initData.size() > 0 ) {
				for( T t : initData) {
					insert(tableClass, t);
				}
			}
		} else {
			if (IdatrixPropertyUtil.getBooleanProperty("idatrix.database.update.table", true)) {
				createOrUpdateTable(tableClass, tableName);
			}
		}
	}

	/**
	 * 不管是否存在或者是否需要更新,直接创建或者更新表
	 * 
	 * @param tableClass
	 * @param tableName
	 * @param primaryKeyName
	 * @throws Exception
	 */
	public static synchronized <T> boolean createOrUpdateTable(Class<T> tableClass, String tableName) throws Exception {

		Database database = CloudApp.getInstance().getCloudDatabase();
		if (database == null) {
			return false;
		}
		try {
			
			tableName = Const.NVL(getTableName(tableClass), tableName);
			
			RowMetaInterface tableRowMeta = new RowMeta();
			boolean use_autoinc = false;
			String primaryKeyName = "ID" ;
			
			List<Field> fields = OsgiBundleUtils.seekOsgiFields(tableClass);
			for (Field f : fields) {
				if ( ( f.getModifiers() < 8 || Modifier.isVolatile( f.getModifiers())) ) {
					// 公有 , 保护,私有
					ValueMetaInterface meta = grenentValurMeta(f, isFieldUpperCase(tableClass));
					if (meta != null) {
						if( f.getAnnotation(Id.class) != null ) {
							//主键
							tableRowMeta.addValueMeta(0, meta);
							if(  f.getAnnotation(GeneratedValue.class) != null && meta.isNumeric() ) {
								//自增 数字
								use_autoinc = true ;
							}
							primaryKeyName = meta.getName() ;
						}else {
							tableRowMeta.addValueMeta(meta);
						}
					}
				}
			}
			String schemaTable = databaseMeta.getQuotedSchemaTableCombination(null, tableName);
			String sql = database.getDDL(schemaTable, tableRowMeta, primaryKeyName, use_autoinc, primaryKeyName, false);

			if (!Utils.isEmpty(sql)) {
				database.execStatements(sql);
				return true;
			}
			
			return false;
		} finally {
			database.disconnect();
		}

	}

	private static Object getValueFromObject(Object t, Field field) {
		if(isIgnoreField(field)) {
			return null ;
		}
		String name = field.getName();
		Class<?> type = field.getType();
		if (type.isArray() || (type.isAssignableFrom(Collection.class))) {
			// 集合类型 ,忽略
			return null;
		}
		switch (type.getSimpleName()) {
		case "Integer":
		case "int":
		case "Long":
		case "long":
		case "double":
		case "Double":
		case "float":
		case "Float":
		case "Boolean":
		case "boolean":
		case "byte":
		case "Byte":
		case "Date":
		case "String":
			return OsgiBundleUtils.getOsgiField(t, name, true);
		case "StringBuffer":
			Object vv = OsgiBundleUtils.getOsgiField(t, name, true);
			if( vv != null) {
				return vv.toString() ;
			}
			return vv;
		default:
			Object val = OsgiBundleUtils.getOsgiField(t, name, true);
			if (val != null) {
				Object valObj = OsgiBundleUtils.invokeOsgiMethod(val, GET_OBJECT_METHON_NAME);
				if (valObj == null || ((valObj instanceof String) && ((String) valObj).length() == 0)) {
					return objectToString(val);
				}
				return valObj;
			}
			return val;
		}
	}

	private static void setValueToObject(Object t, Field field, ValueMetaInterface valueMeta , Object value) throws Exception {

		if(value == null || isIgnoreField(field)) {
			return ;
		}
		if (field.getModifiers() >= 8 && !Modifier.isVolatile( field.getModifiers())) {
			// 不是 公有 , 保护,私有 权限
			return;
		}
		String name = field.getName();
		Class<?> type = field.getType();

		if (type.isArray() || (type.isAssignableFrom(Collection.class))) {
			// 集合类型 ,忽略
			return;
		}
		switch (type.getSimpleName()) {
		case "Boolean":
		case "boolean":
			Boolean tb ;
			if( valueMeta != null ) {
				tb = valueMeta.getBoolean(value);
			}else {
				 if( value instanceof Integer ) {
					 if( (Integer)value != 0 ) {
						 tb =  Boolean.TRUE ;
					 }else {
						 tb = Boolean.FALSE ;
					 }
				 }else if ( value instanceof String ){
					 if( "Y".equalsIgnoreCase((String)value) || "true".equalsIgnoreCase((String)value) ||  !"0".equalsIgnoreCase((String)value))  {
						 tb =   Boolean.TRUE ;
					 }else {
						 tb =   Boolean.FALSE ;
					 }
				 }else {
					 tb= (Boolean) value ;
				 }
			}
			OsgiBundleUtils.setOsgiField(t, name, tb , true);
			break ;
		case "Integer":
		case "int":
			Integer ti = valueMeta != null? valueMeta.getInteger(value).intValue() : (Integer) value;
			OsgiBundleUtils.setOsgiField(t, name, ti  , true);
			break ;
		case "Long":
		case "long":
			Long tl =  valueMeta != null? valueMeta.getInteger(value)  : (Long) value;
			OsgiBundleUtils.setOsgiField(t, name,tl , true);
			break ;
		case "double":
		case "Double":
			Double td =  valueMeta != null? valueMeta.getNumber(value)  : (Double) value;
			OsgiBundleUtils.setOsgiField(t, name,td , true);
			break ;
		case "float":
		case "Float":
			Double tf =  valueMeta != null? valueMeta.getNumber(value)  : (Double) value;
			OsgiBundleUtils.setOsgiField(t, name, tf.floatValue() , true);
			break ;
		case "byte[]":
		case "Byte[]":
			byte[] tba =  valueMeta != null? valueMeta.getBinary(value)  : (byte[]) value;
			OsgiBundleUtils.setOsgiField(t, name, tba , true);
			break ;
		case "Date":
			Date tdd =  valueMeta != null? valueMeta.getDate(value)  : (Date) value;
			OsgiBundleUtils.setOsgiField(t, name, tdd , true);
			break ;
		case "String":
			String ts =  valueMeta != null? valueMeta.getString(value)  : (String) value;
			OsgiBundleUtils.setOsgiField(t, name, ts , true);
			break ;
		case "StringBuffer":
			String tsb =  valueMeta != null? valueMeta.getString(value)  : (String) value;
			OsgiBundleUtils.setOsgiField(t, name, new StringBuffer(tsb==null?"":tsb), true);
			break;
		default:
			Object v = null;
			Method method = OsgiBundleUtils.seekOsgiMethod(type, SET_OBJECT_METHON_NAME, value==null?null:value.getClass());
			if (method != null) {
				v = OsgiBundleUtils.newOsgiInstance(type, null);
				Object dto = OsgiBundleUtils.invokeOsgiMethod(v, SET_OBJECT_METHON_NAME, value);
				if (dto != null) {
					v = dto;
				}
			} else {
				v = stringToObject(value, type);
			}
			if (v != null) {
				OsgiBundleUtils.setOsgiField(t, name, v, true);
			}
			break;
		}
	}

	private static ValueMetaInterface grenentValurMeta(Field field, boolean isUpperCase) {
		if(isIgnoreField(field)) {
			return null ;
		}
		String name = field.getName();
		Class<?> type = field.getType();
		if (type.isArray() || (type.isAssignableFrom(Collection.class))) {
			// 集合类型 ,忽略
			return null;
		}
		switch (type.getSimpleName()) {
		case "Integer":
		case "int":
			return new ValueMetaInteger(isUpperCase ? name.toUpperCase() : name, getFieldLength(field,9), 0);
		case "Long":
		case "long":
			return new ValueMetaInteger(isUpperCase ? name.toUpperCase() : name, getFieldLength(field,18), 0);
		case "double":
		case "Double":
		case "float":
		case "Float":
			return new ValueMetaBigNumber(isUpperCase ? name.toUpperCase() : name);
		case "Boolean":
		case "boolean":
			return new ValueMetaBoolean(isUpperCase ? name.toUpperCase() : name);
		case "byte":
		case "Byte":
			return new ValueMetaBinary(isUpperCase ? name.toUpperCase() : name);
		case "Date":
			return new ValueMetaDate(isUpperCase ? name.toUpperCase() : name);
		case "String":
			return new ValueMetaString(isUpperCase ? name.toUpperCase() : name, getFieldLength(field,255), 0);
		case "StringBuffer":
			return new ValueMetaString(isUpperCase ? name.toUpperCase() : name, getFieldLength(field,20000), 0);
		default:
			return new ValueMetaString(isUpperCase ? name.toUpperCase() : name, getFieldLength(field,4000), 0); // 普通对象
		}
	}

	private static String[] getFields(Class<?> clazz) {
		List<String> result = Lists.newArrayList();

		List<Field> fields = OsgiBundleUtils.seekOsgiFields(clazz);
		for (Field f : fields) {
			if ( ( f.getModifiers() < 8 || Modifier.isVolatile( f.getModifiers())) && !isIgnoreField(f)) {
				String name = f.getName();
				Class<?> type = f.getType();
				if (type.isArray() || (type.isAssignableFrom(Collection.class))) {
					// 集合类型 ,忽略
					continue;
				}
				result.add( isFieldUpperCase(clazz) ?name.toUpperCase():name );
			}
		}

		return result.toArray(new String[] {});

	}

	public static String objectToString(Object obj) {

		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			return null;
		}
	}

	public static Object stringToObject(Object obj, Class<?> clazz) {
		if (obj == null) {
			return null;
		}
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.readValue(obj.toString(), clazz);
		} catch (IOException e) {
			return null;
		}
	}

	private static boolean isFieldUpperCase(Class<?> tableClass) {

		if(tableClass.getAnnotation(FieldUpperCase.class) != null ) {
			return true ;
		}
		return false;
	}
	
	
	public static String quoteField(String fieldName ) {
		return databaseMeta.quoteField(fieldName);
	}
	
	public static String getTableName(Class<?> tableClass) {
		Table table = tableClass.getAnnotation(Table.class) ;
		if( table != null ) {
			if(Utils.isEmpty(table.catalog())) {
				return table.name();
			}else {
				return IdatrixPropertyUtil.getProperty(table.catalog() , table.name());
			}
		}
		return null;
	}
	
	private static boolean isIgnoreField(Field field) {
		IgnoreField ignoreField = field.getAnnotation(IgnoreField.class) ;
		if( ignoreField != null ) {
			String conditionMethod = ignoreField.conditionMethod();
			if( !Utils.isEmpty(conditionMethod) ) {
				Object isOk = OsgiBundleUtils.invokeOsgiMethod(field.getDeclaringClass(), conditionMethod);
				if( isOk != null && !Boolean.valueOf(isOk.toString())) {
					return false ;
				}
			}
			return true;
		}
		return false;
	}
	
	private static Integer getFieldLength(Field field,int defaultLength) {
		FieldLength fieldLength = field.getAnnotation(FieldLength.class) ;
		if( fieldLength != null ) {
			int len = fieldLength.length();
			if( len >= 0 ) {
				return len ;
			}
		}
		return defaultLength;
	}
	
	/**
	 * 根据flag 获取数据库日期值 字符串
	 * @param flag year/month/day/yyyy-MM-dd , 为空:null , 否则 当年/当月/当天/具体某天某月某年
	 * @param formatDate
	 * @return
	 */
	public static String  getDateValueByFlag(String flag,String formatDate) {
		if( Utils.isEmpty(flag) ) {
			return null ;
		}
		return  ( "year".equalsIgnoreCase(flag)|| "month".equalsIgnoreCase(flag)  || "day".equalsIgnoreCase(flag) ) ? " date_format( NOW(),'"+formatDate+"' ) " : "'"+flag+"'" ;
	}
	
	/**
	 * 根据flag 获取数据库日期格式化字符串
	 * @param flag year/month/day/yyyy-MM-dd , 为空:null , 否则 当年/当月/当天/具体某天某月某年
	 * @return
	 */
	public static String  getDateFormatByFlag( String flag) {
		if( Utils.isEmpty(flag)) {
			return null ;
		}
		
		String formatDate = "year".equalsIgnoreCase(flag) ? "%Y" : ( "month".equalsIgnoreCase(flag) ? "%Y-%m" : ( "day".equalsIgnoreCase(flag) ? "%Y-%m-%d" : null ) ) ;
		if(Utils.isEmpty(formatDate)  ) {
			String[] fs = flag.split("-") ;
			formatDate = fs.length == 1 ? "%Y" : (  fs.length == 2 ? "%Y-%m" : (  fs.length == 3 ? "%Y-%m-%d" : null ) ) ;
		}
		return formatDate ;
		
	}
	

}




