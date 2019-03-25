package com.idatrix.unisecurity.log;

import org.apache.log4j.Logger;
import org.apache.log4j.net.SyslogAppender;

/**
 * @ClassName SecurityLog
 * @Description
 * @Author ouyang
 * @Date 2018/9/17 13:57
 * @Version 1.0
 **/
public class SecurityLog {

    private static Logger logger = Logger.getLogger(SecurityLog.class);

    private static final LogLevel Security_LOG_LEVEL = new LogLevel(20050, "SECURITY",
            SyslogAppender.LOG_LOCAL0);

    public static void log(Object pm_objLogInfo) {
        logger.log(Security_LOG_LEVEL, pm_objLogInfo);
    }
}
