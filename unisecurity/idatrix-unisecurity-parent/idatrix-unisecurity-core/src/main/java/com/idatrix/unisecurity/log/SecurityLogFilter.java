package com.idatrix.unisecurity.log;

import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;

/**
 * 自定义log拦截器
 * @ClassName SecurityLogFilter
 * @Description
 * @Author ouyang
 * @Date 2018/9/17 13:59
 * @Version 1.0
 **/
public class SecurityLogFilter extends Filter {

    boolean acceptOnMatch = false;

    int levelMin;

    int levelMax;

    public int getLevelMin() {
        return levelMin;
    }

    public void setLevelMin(int levelMin) {
        this.levelMin = levelMin;
    }

    public int getLevelMax() {
        return levelMax;
    }

    public void setLevelMax(int levelMax) {
        this.levelMax = levelMax;
    }

    @Override
    public int decide(LoggingEvent lgEvent) {
        int inputLevel = lgEvent.getLevel().toInt();
        if (inputLevel >= levelMin && inputLevel <= levelMax) {
            return 0;
        }
        return -1;
    }

}
