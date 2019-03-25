package com.idatrix.unisecurity.auditLog.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.idatrix.unisecurity.auditLog.service.AuditLogService;
import com.idatrix.unisecurity.auditLog.vo.LogSearchVO;
import com.idatrix.unisecurity.common.dao.AuditLogMapper;
import com.idatrix.unisecurity.common.domain.AuditLog;
import com.idatrix.unisecurity.common.domain.UUser;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @ClassName AuditLogServiceImpl
 * @Description TODO
 * @Author ouyang
 * @Date 2018/8/28 13:41
 * @Version 1.0
 **/
@Service
public class AuditLogServiceImpl implements AuditLogService {

    @Autowired(required = false)
    private AuditLogMapper auditLogMapper;

    @Override
    public Integer insert(AuditLog auditLog) {
        return auditLogMapper.insert(auditLog);
    }

    @Override
    public PageInfo<AuditLog> findPage(LogSearchVO search) {
        UUser user = (UUser) SecurityUtils.getSubject().getPrincipal();// 获取当前用户信息
        search.setRenterId(user.getRenterId());
        PageHelper.startPage(search.getPage(), search.getSize());// 分页
        List<AuditLog> list = auditLogMapper.findPage(search);
        PageInfo<AuditLog> pageInfo = new PageInfo<>(list);
        return pageInfo;
    }

    @Override
    @Transactional
    public void clearLog() {
        auditLogMapper.clearLog();
    }
}
