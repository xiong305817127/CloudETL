package com.idatrix.unisecurity.common.dao;

import com.idatrix.unisecurity.auditLog.vo.LogSearchVO;
import com.idatrix.unisecurity.common.domain.AuditLog;

import java.util.List;

public interface AuditLogMapper {
    int deleteByPrimaryKey(Long id);

    int insert(AuditLog record);

    int insertSelective(AuditLog record);

    AuditLog selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(AuditLog record);

    int updateByPrimaryKey(AuditLog record);

    List<AuditLog> findPage(LogSearchVO search);

    void clearLog();
}