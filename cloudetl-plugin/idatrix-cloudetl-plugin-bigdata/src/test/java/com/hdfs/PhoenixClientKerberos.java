package com.hdfs;

import java.io.IOException;
import java.security.PrivilegedAction;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.security.UserGroupInformation;


public class PhoenixClientKerberos {
    //url的格式：jdbc:phoenix:<zookeeper地址>， 如果zookeeper为多个，中间使用分号
	//jdbc:hive2://namenode23.example.com:2181,namenode20.example.com:2181,admin24.example.com:2181/;serviceDiscoveryMode=zooKeeper;zooKeeperNamespace=hiveserver2
    private String url = "jdbc:phoenix:namenode23.example.com:2181;namenode20.example.com:2181;admin24.example.com:2181";
    private String driver = "org.apache.phoenix.jdbc.PhoenixDriver";
    private Connection connection = null;  
    private static Configuration conf = null;   
    
    static {
        //krb5.conf配置文件，可以从kerberos配置
        System. setProperty("java.security.krb5.conf", "D:/keytab/krb5.conf" );
        conf = HBaseConfiguration.create();
    }

    /**
     * kerberos认证。使用keytab文件认证，keytab文件可以根据认证用户生成, *从环境中获取。
     *  keytab文件与princal都由管理员提供，用户可以咨询平台管理员
     */



    public PhoenixClientKerberos(String user) {
        try {
//            Class.forName(driver);
            connection = this.getConnection(user);
            System.out.println("Connect HBase success.." + user);
        }   catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    public void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (Exception e) {
            } finally {
                connection = null;
            }
        }
    }
    
    public static void initKerberos(){
        UserGroupInformation.setConfiguration(conf);
        try {     
        	UserGroupInformation.loginUserFromKeytab("hbase@EXAMPLE.COM", "D:/keytab/namenode24/hbase.keytab" );   
//        	UserGroupInformation.getCurrentUser().doAs(PrivilegedAction<T> action);
//        	UserGroupInformation.createProxyUser("user01", UserGroupInformation.getCurrentUser());
        } catch (IOException e) {
             e.printStackTrace();
       }
    }
    
//    public static Connection doas(UserGroupInformation userGroupInfo, String user) throws IOException {
//    	
//        return userGroupInfo.doas(user, new PrivilegedAction<Connection>() {
//            @Override
//            public Connection run() {
//
//                try {
//                    return ConnectionFactory.createConnection(hadoopConf.getConf());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//                return null;
//            }
//        });
//    }
    
//    public synchronized <T> T doas(String user, PrivilegedAction<T> action) throws IOException {
//        logger.info("proxy user:"+user+" to do tasks");
//        if (null == hdfsUgi || !hdfsPrincale.equals(hdfsUgi.getUserName())) {
//            init();
//        }
//        hdfsUgi.checkTGTAndReloginFromKeytab();
//        if (!hdfsPrincale.equals(user)) {
//            return UserGroupInformation.createProxyUser(user, hdfsUgi).doAs(action);
//        } else {
//            return hdfsUgi.doAs(action);
//        }
//    }


    public void selectRecord() {
        String sql = "select * from my_table";
        ResultSet resultSet = null;
        try {
            Statement statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            while (resultSet != null && resultSet.next()) {
                System.out.println("1: " + resultSet.getString(1) + "\t 2:" + resultSet.getString(2)              );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public Connection getConnection(String user) throws IOException {

        return this.doas(user);
    }
    
    private Connection doas(String user) throws IOException {
    	
        UserGroupInformation current = UserGroupInformation.getCurrentUser();
        System.out.println("current user = " + current.getShortUserName() + " login user = " + user);
//       	final Properties props = new Properties();
//        Iterator<Map.Entry<String, String>> iterator = conf.iterator();
//        while(iterator.hasNext()){
//            Map.Entry<String, String> entry = iterator.next();
//            props.put(entry.getKey(),entry.getValue());
//        }
        
        if(current.getShortUserName().equals(user)){
          try {
			Class.forName(driver);
			return DriverManager.getConnection(url);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
          return null;
        }else{
        	
        
        return current.doAs( new PrivilegedAction<Connection>(){
            @Override
            public Connection run() {
            	try {
					Class.forName(driver);
//					DriverManager.getConnection(user, info)
					return DriverManager.getConnection(url);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (SQLException e) {
					e.printStackTrace();
				}
                return null;
            }
        });}
    }

    
    public static void main(String[] args) {
    	PhoenixClientKerberos.initKerberos();
    	String user = "hbase";
        PhoenixClientKerberos client = new PhoenixClientKerberos(user);
        client.selectRecord();
        client.close();
    }
}
