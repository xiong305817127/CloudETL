package com.idatrix.unisecurity.provider.utils;

import com.idatrix.unisecurity.provider.entry.DBConnection;
import org.apache.log4j.Logger;

import java.sql.*;

/**
 * Created by james on 2017/7/14.
 */
public class DBTool {
	private static Connection conn = null;
	protected final static Logger logger = Logger.getLogger(DBTool.class);
    public static void closeRes(ResultSet rs,PreparedStatement ps){
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (ps != null) {
            try {
                ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
                conn = null;
            }
        }
    }

	public static Connection getConnection(String url, String userName, String password) {
		if(conn !=null){
			return conn;
		}
		try {
			conn = DriverManager.getConnection(DBConnection.getUrl(),DBConnection.getUserName(), DBConnection.getPassword());
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("getConnection error");
		}
		return null;
	}
}
