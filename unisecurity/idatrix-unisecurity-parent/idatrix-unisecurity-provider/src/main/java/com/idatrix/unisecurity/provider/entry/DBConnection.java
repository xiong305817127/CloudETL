package com.idatrix.unisecurity.provider.entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Properties;

/**
 */
public class DBConnection {
    private final static Logger logger = LoggerFactory.getLogger(DBConnection.class);
    static private String url = null;
    static private String userName = null;
    static private String password = null;

    static {
        loads();
    }

    synchronized static public void loads() {
        if (url == null || userName == null || password == null) {
            String path = DBConnection.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            FileInputStream file = null;
            Properties dbProps = new Properties();
            try {
                path = path.substring(0, path.lastIndexOf("/") + 1);
                file = new FileInputStream(path + "jdbc.properties");
                dbProps.load(file);
                url = dbProps.getProperty("jdbc.url");
                userName = dbProps.getProperty("jdbc.username");
                password = dbProps.getProperty("jdbc.password");
            } catch (Exception e) {
                if (logger.isErrorEnabled())
                    logger.error(e.getMessage());
            } finally {
                if (file != null) {
                    try {
                        file.close();
                    } catch (IOException ioe) {
                        if (logger.isErrorEnabled())
                            logger.error("不能读取属性文件.请确保jdbc.properties和jar文件在同一个目录!");
                    }
                }
            }
        }
    }

    public static String getUrl() {
        if (url == null)
            loads();
        return url;
    }

    public static String getUserName() {
        if (userName == null)
            loads();
        return userName;
    }

    public static String getPassword() {
        if (password == null)
            loads();
        return password;
    }
}
