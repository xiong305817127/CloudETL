package com.idatrix.unisecurity.core.config;

import com.idatrix.unisecurity.common.utils.LoggerUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class IConfig {

    /**
     * 同步锁
     */
    private static final Object obj = new Object();

    /**
     * 配置文件
     */
    private static Properties prop = null;

    /**
     * 配置对象单例模式
     */
    private static IConfig config = null;

    /**
     * 配置文件名称
     */
    private final static String FILE_NAME = "/permit_config.properties";

    static {
        prop = new Properties();
        InputStream is = null;
        try {
            is = IConfig.class.getResourceAsStream(FILE_NAME);
            prop.load(is);
        } catch (IOException e) {
            LoggerUtils.fmtError(IConfig.class, e, "加载文件异常，文件路径：%s", FILE_NAME);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ioe) {
                    LoggerUtils.fmtError(IConfig.class, ioe, "关闭流异常");
                }
            }
        }
    }

    /**
     * 获取单例模式对象实例
     * @return 唯一对象实例
     */
    public static IConfig getInstance() {
        if (null == config) {
            synchronized (obj) {
                config = new IConfig();
            }
        }
        return config;
    }

    /**
     */
    public static String get(String key) {
        return prop.getProperty(key);
    }

}
