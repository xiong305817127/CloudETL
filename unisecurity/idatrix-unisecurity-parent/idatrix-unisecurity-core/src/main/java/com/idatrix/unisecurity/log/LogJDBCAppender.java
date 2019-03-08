package com.idatrix.unisecurity.log;

import org.apache.log4j.jdbc.JDBCAppender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * 现在使用的是 Connection 直接连接，后面需要改成连接池方式（本来想用spring配置的连接池，但是log4j是在前面加载，所以待定实现）
 * @ClassName LogJDBCAppender
 * @Description 自定义实现Log4j日志组件,将日志记录到数据库，
 *              解决问题: 原生组件在系统运行过程中可能会出现数据库连接断开,导致无法正常记录日志信息到数据库.
 * @Author ouyang
 * @Date 2018/11/6 16:01
 * @Version 1.0
 */
public class LogJDBCAppender extends JDBCAppender {

    private Logger log = LoggerFactory.getLogger(getClass());

    public LogJDBCAppender() {
        super();
    }

    @Override
    protected Connection getConnection() throws SQLException {
        Connection connection = super.getConnection();
        if(connection == null || connection.isClosed()) {
            // 当前获取到的连接是空的或关闭的
            log.warn(String.format("reconnect log jdbc appender connection"));
            connection = reconnect();
        }
        return connection;
    }

    /**
     * 重新创建连接
     * @return
     * @throws SQLException
     */
    private Connection reconnect() throws SQLException {
        Connection connection = DriverManager.getConnection(databaseURL, databaseUser,databasePassword);
        return connection;
    }

    /**
 　　* 重载父类方法,打印错误信息到日志文件 <br />
 　　* 同时,处理数据库重连并在出错时重试记录日志信息.
 　　*/
    @Override
    protected void execute(String sql) throws SQLException {
        try {
            super.execute(sql);
        } catch (Exception e) {
            log.error(String.format("log jdbc appender execute sql eror: %s", getSql()), e);
            closeConnectionInterval();
            super.execute(sql);
        }
    }

    /**
     * 关闭连接
     */
    private void closeConnectionInterval() {
        try {
            if(connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (Exception e) {
            log.error("log jdbc appender cole error message：{}", e.getMessage());
        } finally {
            connection = null;
        }
    }
}
