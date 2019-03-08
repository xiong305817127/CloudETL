/**
 * 云化数据集成系统 
 * iDatrxi CloudETL
 */
package com.ys.idatrix.cloudetl.database.hbase;

import java.sql.Connection;

import org.apache.commons.lang.ArrayUtils;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.database.GenericDatabaseMeta;
import org.pentaho.di.core.exception.KettleDatabaseException;
import org.pentaho.di.core.exception.KettleValueException;
import org.pentaho.di.core.plugins.DatabaseMetaPlugin;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.util.Utils;

import com.ys.idatrix.cloudetl.security.HadoopSecurityManagerException;
import com.ys.idatrix.cloudetl.security.IdatrixSecurityManager;
import com.ys.idatrix.cloudetl.util.EnvUtils;

/**
 * HBase Database With Phoenix JDBC.
 * HbaseTableDatabaseMeta <br/>
 * @author XH
 * @since 2017年10月26日
 *
 */
@DatabaseMetaPlugin( type = "HBASETABLE", typeDescription = "Phoenix Hbase Table" )
public class HbaseTableDatabaseMeta extends GenericDatabaseMeta{

	public HbaseTableDatabaseMeta(){
		super();
		getAttributes().put("EXTRA_OPTION_HBASETABLE.phoenix.schema.isNamespaceMappingEnabled", "true");
	}

	@Override
	public String getDriverClass() {
		return "org.apache.phoenix.jdbc.PhoenixDriver";
	}

	@Override
	public boolean supportsOptionsInURL() {
		// JW: set false for generic database connecting to HBase with phoenix driver.
		return false;
	}

	public Connection getConnection(String database) throws HadoopSecurityManagerException{
		return IdatrixSecurityManager.getInstance().getHbaseJdbcConnection(EnvUtils.getUserId(),this);
	}
	
	public String getPhoenixUrl() throws HadoopSecurityManagerException {
		return Const.NVL(getURL(null, null, null),IdatrixSecurityManager.getInstance().getPhoenixUrl());
	}
	
	/**
	 *  根据参数获取更新 prepareStatement sql ( ? 代替值)
	 * @param schemaTable 表名 , schema.tableName 形式
	 * @param codes where条件的域名
	 * @param condition where条件的操作符,BETWEEN/IS NULL... 等
	 * @param sets set部分的域名
	 * @return
	 * @throws KettleDatabaseException 
	 */
	public String getUpdateSql(String schemaTable, String[] codes, String[] condition, String[] sets ) throws KettleDatabaseException{
		if(Utils.isEmpty(schemaTable) || sets == null || sets.length ==0 || codes == null || codes.length ==0 ) {
			return null;
		}
		String[] fieldNames=(String[]) ArrayUtils.addAll(sets, codes);
		String[] fieldValues=null;
		return getSQLOutput(schemaTable, fieldNames, fieldValues);
	}
	
	/**
	 *  根据参数获取 insert sql语句
	 * @param schemaTable 表名 , schema.tableName 形式
	 * @param fields  需要插入的域的 RowMetaInterface
	 * @param values 域值数组,为空或者长度为0是 使用prepareStatement方式 ( ? 代替值)
	 * @return
	 * @throws KettleDatabaseException
	 * @throws KettleValueException
	 */
	public String  getInsertSql(String schemaTable, RowMetaInterface fields, Object[] values  ) throws KettleDatabaseException, KettleValueException{
		if(Utils.isEmpty(schemaTable) || fields == null || fields.size() ==0) {
			return null;
		}
		
		String[] fieldNames=new String[fields.size()];
		String[] fieldValues=null;
		if(values != null && values.length>0) {
			fieldValues=new String[fields.size()];
		}
		// now add the names in the row:
		for ( int i = 0; i < fields.size(); i++ ) {
			ValueMetaInterface valueMeta = fields.getValueMeta( i );
			fieldNames[i]= valueMeta.getName() ;
			
			if(values != null && values.length>0) {
				if( i >= values.length) {
					fieldValues[i] =  null ;
				}else {
					Object valueData = values[i];
					if ( valueMeta.isNull( valueData ) ) {
						fieldValues[i] = null ;
					} else {
						switch ( valueMeta.getType() ) {
						case ValueMetaInterface.TYPE_BOOLEAN:
						case ValueMetaInterface.TYPE_STRING:
							String string = valueMeta.getString( valueData );
							fieldValues[i] = string ;
							break;
						case ValueMetaInterface.TYPE_DATE:
//							Date date = fields.getDate(values, i );
							fieldValues[i] = "'" + fields.getString(values, i ) + "'" ;
							break;
						default:
							fieldValues[i] = fields.getString(values, i ) ;
							break;
						}
					}
				}
			}
		}

		return getSQLOutput(schemaTable, fieldNames, fieldValues);
		
	}
	
	private  String getSQLOutput( String schemaTable, String[] fields, String[] values ) throws KettleDatabaseException {
		StringBuilder ins = new StringBuilder( 128 );

		try {
			ins.append( "UPSERT INTO " ).append( schemaTable ).append( '(' );

			// now add the names in the row:
			for ( int i = 0; i < fields.length; i++ ) {
				if ( i > 0 ) {
					ins.append( ", " );
				}
				String name = fields[i];
				ins.append( name );

			}
			ins.append( ") VALUES (" );
			
			for ( int i = 0; i < fields.length; i++ ) {
				if ( i > 0 ) {
					ins.append( ", " );
				}
				
				if( values == null || values.length == 0) {
					ins.append( " ?" );
				}else {
					String val ="null" ;
					if(i<values.length) {
						val = values[i];
					}
					ins.append( val );
				}
			}
			ins.append( ')' );
			
		} catch ( Exception e ) {
			throw new KettleDatabaseException( e );
		}
		return ins.toString();
	}

}
