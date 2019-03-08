package com.idatrix.resource.terminalmanage.dao;

import com.idatrix.resource.terminalmanage.po.TerminalManagePO;

import java.util.List;
import java.util.Map;

public interface TerminalManageDAO {
    void insertTerminalManageRecord(TerminalManagePO terminalManagePO);

    void updateTerminalManageRecord(TerminalManagePO terminalManagePO);

    TerminalManagePO getTerminalManageRecordById (Long id);

    TerminalManagePO getTerminalManageRecordByDeptId(Long deptId);

    void deleteTerminalManageRecordById(Long id);

    List<TerminalManagePO> getTerminalManageRecordByCondition(Map<String, String> conditionMap);

    TerminalManagePO isExistedTerminalManageRecord(Map<String, Object> conditionMap);
}
