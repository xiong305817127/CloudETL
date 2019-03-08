package com.idatrix;

import org.junit.Test;

import java.sql.*;

/**
 * @ClassName MysqlConnectionTest
 * @Description
 * @Author ouyang
 * @Date
 */
public class MysqlConnectionTest {

    @Test
    public void test1() {
        Connection con = null;
        Statement statement = null;
        String driver = "com.mysql.jdbc.Driver";
        String username = "root";
        String password = "admin";
        String url = "jdbc:mysql://10.0.0.85:3306/metadata2";
        try {
            Class.forName(driver);
            con = DriverManager.getConnection(url, username, password);
            System.out.println("获取mysql连接");
            String sql = "create table oyr_test(id int(10), name varchar(25))charset=utf8;";
            statement = con.createStatement();
            long l = statement.executeLargeUpdate(sql);
            System.out.println("创建表：" + l);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (!con.isClosed()) {
                    con.close();
                }
                if (!statement.isClosed()) {
                    statement.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
