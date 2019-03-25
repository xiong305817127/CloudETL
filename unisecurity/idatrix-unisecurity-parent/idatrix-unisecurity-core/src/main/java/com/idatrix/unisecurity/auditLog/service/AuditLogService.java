package com.idatrix.unisecurity.auditLog.service;

import com.github.pagehelper.PageInfo;
import com.idatrix.unisecurity.auditLog.vo.LogSearchVO;
import com.idatrix.unisecurity.common.domain.AuditLog;

/**
 * @ClassName AuditLogService
 * @Description
 * @Author ouyang
 * @Date 2018/8/28 13:37
 * @Version 1.0
 **/
public interface AuditLogService {

    // 新增日志信息
    Integer insert(AuditLog auditLog);

    // 查询日志，带分页
    PageInfo<AuditLog> findPage(LogSearchVO search);

    // 清理三个月前的日志
    void clearLog();
}
