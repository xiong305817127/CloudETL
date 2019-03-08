package com.idatrix.unisecurity.log;

import org.apache.log4j.Level;

/**
 * 自定义log等级
 * @ClassName LogLevel
 * @Description TODO
 * @Author ouyang
 * @Date 2018/9/17 13:55
 * @Version 1.0
 **/
public class LogLevel extends Level {

    protected LogLevel(int level, String levelStr, int syslogEquivalent) {
        super(level, levelStr, syslogEquivalent);
    }

}
