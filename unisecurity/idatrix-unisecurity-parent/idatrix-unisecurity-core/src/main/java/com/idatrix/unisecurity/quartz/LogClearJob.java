package com.idatrix.unisecurity.quartz;

import com.idatrix.unisecurity.auditLog.service.AuditLogService;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @ClassName LogClearJob
 * @Description 日志清理
 * @Author ouyang
 * @Date 2018/10/25 14:15
 * @Version 1.0
 */
public class LogClearJob extends QuartzJobBean {

    private Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private AuditLogService auditLogService;

    // 具体要执行的任务体
    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        log.debug("{}，当前清理三个月前的日志信息。", format.format(date));
        auditLogService.clearLog();
    }

}
