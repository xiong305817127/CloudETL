package com.idatrix.resource.terminalmanage.service;

import com.idatrix.resource.common.utils.ResultPager;
import com.idatrix.resource.terminalmanage.vo.TerminalManageVO;

import java.util.Map;

public interface ITerminalManageService {
    void saveOrUpdateTerminalManageRecord(TerminalManageVO terminalManageVO, String userName) throws
            Exception;

    TerminalManageVO getTerminalManageRecordById(Long id);

    void deleteTerminalManageRecordById(Long id);

    ResultPager<TerminalManageVO> getTerminalManageRecordsByCondition(Map<String, String> conditionMap,
                                                                 Integer pageNum, Integer pageSize);

    String isExistedTerminalManageRecord(Long id, String deptId);
}
