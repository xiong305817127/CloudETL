/**
 * 云化数据集成系统 
 * iDatrxi CloudETL
 */
package test.kbr;

import java.io.IOException;
import java.sql.SQLException;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import com.ys.idatrix.cloudetl.security.HadoopSecurityManagerException;
import com.ys.idatrix.cloudetl.security.IdatrixSecurityManager;

/**
 * 安全插件测试主类 <br/>
 * PhoenixMutiThreadTest <br/>
 * @author JW
 * @since 2017年10月31日
 *
 */
public class PhoenixMutiThreadTest {

	public static void main(String[] args) throws HadoopSecurityManagerException, IllegalArgumentException, IOException, SQLException {

		System.setProperty("metaCube.category","iDatrix") ;
		System.setProperty("idatrix.kerberos.deployment","true") ;
		System.setProperty("security.kerberos.timeout","10");

		System.setProperty("hadoop.home.dir", "D:\\tool\\hadoop-2.6.5");
		IdatrixSecurityManager hsm =  IdatrixSecurityManager.getInstance();
		
		// HDFS Testing
		FileSystem fs = hsm.getFs("user01", null, null);
		System.out.println("\n==================================================================");
		System.out.println(fs.getHomeDirectory());
		System.out.println(fs.getFileStatus(new Path("/")));
		System.out.println(fs.listStatus(new Path("/")));
		System.out.println("==================================================================\n");

		
		// HBase Testing
		/*Properties props = new Properties();
		props.setProperty("phoenix.schema.isNamespaceMappingEnabled", "true");
		
		System.setProperty("HADOOP_USER_NAME", "hdfs");
		Connection conn = null;
		try {
			Class.forName("org.apache.phoenix.jdbc.PhoenixDriver");
			//DriverManager.registerDriver((Driver) (Class.forName("org.apache.phoenix.jdbc.PhoenixDriver").newInstance()));
			conn = DriverManager.getConnection("jdbc:phoenix:sitfch01.gdbd.com,sitfch02.gdbd.com,sitfch03.gdbd.com:2181:/hbase-unsecure",props);
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}*/
		
		//PhoenixExecutor e1 = new PhoenixExecutor(conn, sql11, 900);
		
//		Statement stmt = null;
        //ResultSet rset = null;
        
//        Connection conn = hsm.getHbaseJdbcConnection("user01");
//		stmt = conn.createStatement();
//		//stmt.executeUpdate("create table HBASE22345646.ttt4 (id varchar(20) not null primary key,vv varchar(20))");
//		stmt.executeUpdate("upsert into HBASE22345646.ttt4 (id,vv) values ('10011','11111')");
//		conn.commit();
        
//        PreparedStatement stmt1 = conn.prepareStatement("upsert into HBASE22345646.ttt4 (id) values (?) ");
//        stmt1.setString(1, "10012");
//        stmt1.execute();
//        conn.commit();
//        
//        ResultSet allkeys = conn.getMetaData().getPrimaryKeys(null, null, "HBASE22345646.TTT4");
//        while ( allkeys.next() ) {
//			String keyname = allkeys.getString( "PK_NAME" );
//			String col_name = allkeys.getString( "COLUMN_NAME" );
//
//			System.out.println(keyname);
//			System.out.println(col_name);
//		}
//		System.out.println("---------------");
//		
//		PreparedStatement statement = conn.prepareStatement("select * from HBASE22345646.ttt4");
//        rset = statement.executeQuery();
//        while (rset.next()) {
//            System.out.println(rset.getString("id"));
//        }
//        
//        PreparedStatement statement1 = conn.prepareStatement("select  TENANT_ID TABLE_CAT,TABLE_SCHEM,TABLE_NAME ,COLUMN_NAME,KEY_SEQ,PK_NAME,CASE WHEN SORT_ORDER = 1 THEN 'D' ELSE 'A' END ASC_OR_DESC,ExternalSqlTypeId(DATA_TYPE) AS DATA_TYPE,SqlTypeName(DATA_TYPE) AS TYPE_NAME,COLUMN_SIZE,DATA_TYPE TYPE_ID,VIEW_CONSTANT from SYSTEM.\"CATALOG\" \"SYSTEM.TABLE\" ");
//        rset = statement1.executeQuery();
//        
//        while (rset.next()) {
//        	System.out.println("---------------===============");
//            System.out.println(rset.getString("TABLE_CAT"));
//            System.out.println(rset.getString("TABLE_SCHEM"));
//            System.out.println(rset.getString("TABLE_NAME"));
//            System.out.println(rset.getString("COLUMN_NAME"));
//            System.out.println(rset.getString("KEY_SEQ"));
//            System.out.println(rset.getString("PK_NAME"));
//        }
//        
//        
//        
//        statement.close();
//        conn.close();

        
//		// HIVE Testing
//        conn = hsm.getHiveJdbcConnection("test", "user01");
//        stmt = conn.createStatement();
//        //stmt.executeUpdate("create table test.etltest5(id varchar(20))");
//        stmt.executeUpdate("insert into test.etltest5 (id) values (30000)");
//        //conn.commit();
//        
//        statement = conn.prepareStatement("select * from test.etltest5");
//        rset = statement.executeQuery();
//        while (rset.next()) {
//            System.out.println(rset.getString("id"));
//        }
//        statement.close();
//        conn.close();
	}

}
