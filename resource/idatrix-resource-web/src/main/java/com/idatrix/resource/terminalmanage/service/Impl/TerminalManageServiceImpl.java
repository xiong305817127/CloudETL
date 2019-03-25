package com.idatrix.resource.terminalmanage.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.idatrix.resource.common.utils.ResultPager;
import com.idatrix.resource.subscribe.dao.SubscribeDAO;
import com.idatrix.resource.subscribe.po.SubscribePO;
import com.idatrix.resource.terminalmanage.dao.TerminalManageDAO;
import com.idatrix.resource.terminalmanage.po.TerminalManagePO;
import com.idatrix.resource.terminalmanage.service.ITerminalManageService;
import com.idatrix.resource.terminalmanage.vo.TerminalManageVO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Transactional
@Service("terminalManageService")
public class TerminalManageServiceImpl implements ITerminalManageService {

    private TerminalManageDAO terminalManageDAO;

    private SubscribeDAO subscribeDAO;

    @Autowired
    public TerminalManageServiceImpl(TerminalManageDAO terminalManageDAO,
                                     SubscribeDAO subscribeDAO) {
        this.terminalManageDAO = terminalManageDAO;
        this.subscribeDAO = subscribeDAO;
    }

    @Override
    public TerminalManageVO getTerminalManageRecordById(Long id) throws RuntimeException {
        TerminalManagePO terminalManagePO = terminalManageDAO.getTerminalManageRecordById(id);

        TerminalManageVO terminalManageVO = new TerminalManageVO();

        if (terminalManagePO != null) {
            terminalManageVO.setId(terminalManagePO.getId());

            terminalManageVO.setTmDBId(terminalManagePO.getTmDBId());
            terminalManageVO.setDeptFinalId(terminalManagePO.getDeptFinalId());

            terminalManageVO.setDeptCode(terminalManagePO.getDeptCode());
            terminalManageVO.setDeptName(terminalManagePO.getDeptName());
            terminalManageVO.setDeptId(terminalManagePO.getDeptId());

            terminalManageVO.setTmName(terminalManagePO.getTmName());
            terminalManageVO.setTmIP(terminalManagePO.getTmIP());

            terminalManageVO.setTmDBName(terminalManagePO.getTmDBName());
            terminalManageVO.setTmDBPort(terminalManagePO.getTmDBPort());
            terminalManageVO.setTmDBType(terminalManagePO.getTmDBType());

            terminalManageVO.setSftpSwitchRoot(terminalManagePO.getSftpSwitchRoot());
            terminalManageVO.setHdfsSwitchRoot(terminalManagePO.getHdfsSwitchRoot());
            terminalManageVO.setSftpPort(terminalManagePO.getSftpPort());
            terminalManageVO.setSftpUsername(terminalManagePO.getSftpUsername());

            terminalManageVO.setSchemaId(terminalManagePO.getSchemaId());
            terminalManageVO.setSchemaName(terminalManagePO.getSchemaName());
        }

        return terminalManageVO;
    }

    @Override
    public void deleteTerminalManageRecordById(Long id) throws RuntimeException {
        //KEITH 删除前需要判断当前前置机是否有交换任务, 如果没有才允许进行删除
        TerminalManagePO tPO = terminalManageDAO.getTerminalManageRecordById(id);
        String deptId = tPO.getDeptFinalId();
        if(StringUtils.isNotEmpty(deptId)) {
            List<SubscribePO> subscribePOList = subscribeDAO.getByDeptId(Long.valueOf(deptId));
            if (subscribePOList != null && subscribePOList.size() > 0) {
                throw new RuntimeException("有交换任务正在使用前置机，删除前置机失败。");
            }
        }
        terminalManageDAO.deleteTerminalManageRecordById(id);
    }

    @Override
    public ResultPager<TerminalManageVO> getTerminalManageRecordsByCondition(Map<String, String> conditionMap,
                                                                             Integer pageNum, Integer pageSize) throws RuntimeException {
        pageNum = null == pageNum ? 1 : pageNum;
        pageSize = null == pageSize ? 10 : pageSize;
        PageHelper.startPage(pageNum, pageSize);

        List<TerminalManagePO> terminalManagePOList
                = terminalManageDAO.getTerminalManageRecordByCondition(conditionMap);

        if(CollectionUtils.isEmpty(terminalManagePOList)){
            return null;
        }

        List<TerminalManageVO> terminalManageVOList = new ArrayList<TerminalManageVO>();

        for (TerminalManagePO model : terminalManagePOList) {
            TerminalManageVO terminalManageVO = new TerminalManageVO();

            terminalManageVO.setTmDBId(model.getTmDBId());
            terminalManageVO.setDeptFinalId(model.getDeptFinalId());
            terminalManageVO.setTmDBType(model.getTmDBType());
            terminalManageVO.setId(model.getId());
            terminalManageVO.setDeptCode(model.getDeptCode());
            terminalManageVO.setDeptName(model.getDeptName());
            terminalManageVO.setTmName(model.getTmName());
            terminalManageVO.setTmIP(model.getTmIP());
            terminalManageVO.setTmDBName(model.getTmDBName());

            terminalManageVO.setSchemaId(model.getSchemaId());
            terminalManageVO.setSchemaName(model.getSchemaName());

            terminalManageVOList.add(terminalManageVO);
        }

        //用PageInfo对结果进行包装
        PageInfo<TerminalManagePO> pi = new PageInfo<TerminalManagePO>(terminalManagePOList);
        Long totalNum = pi.getTotal();
        return new ResultPager<TerminalManageVO>(pi.getPageNum(),
                totalNum, terminalManageVOList);
    }

    @Override
    public String isExistedTerminalManageRecord(Long id, String deptId) {
        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("deptId", deptId);
        conditionMap.put("id", id);

        String result;
        TerminalManagePO terminalManagePO = terminalManageDAO.isExistedTerminalManageRecord(conditionMap);

        if(terminalManagePO == null)
            result = "";
        else
            result = terminalManagePO.getDeptName();

        return result;
    }

    @Override
    public void saveOrUpdateTerminalManageRecord (Long rentId, TerminalManageVO terminalManageVO, String userName)
            throws Exception {

        TerminalManagePO terminalManagePO = convertToTerminalManagePO(terminalManageVO);
        terminalManagePO.setModifier(userName);
        terminalManagePO.setModifyTime(new Date());
        terminalManagePO.setRentId(rentId);

        if (terminalManagePO.getId() == null) {
            terminalManagePO.setCreator(userName);
            terminalManagePO.setCreateTime(new Date());
            terminalManageDAO.insertTerminalManageRecord(terminalManagePO);
        } else
            terminalManageDAO.updateTerminalManageRecord(terminalManagePO);
    }

    private TerminalManagePO convertToTerminalManagePO (TerminalManageVO terminalManageVO) {
        TerminalManagePO terminalManagePO = new TerminalManagePO();

        terminalManagePO.setId(terminalManageVO.getId());

        terminalManagePO.setDeptFinalId(terminalManageVO.getDeptFinalId());
        terminalManagePO.setTmDBId(terminalManageVO.getTmDBId());
        terminalManagePO.setDeptCode(terminalManageVO.getDeptCode());
        terminalManagePO.setDeptName(terminalManageVO.getDeptName());
        terminalManagePO.setDeptId(terminalManageVO.getDeptId());
        terminalManagePO.setTmName(terminalManageVO.getTmName());
        terminalManagePO.setTmIP(terminalManageVO.getTmIP());

        terminalManagePO.setTmDBName(terminalManageVO.getTmDBName());
        terminalManagePO.setTmDBPort(terminalManageVO.getTmDBPort());
        terminalManagePO.setTmDBType(terminalManageVO.getTmDBType());

        terminalManagePO.setSftpSwitchRoot(terminalManageVO.getSftpSwitchRoot());
        terminalManagePO.setHdfsSwitchRoot(terminalManageVO.getHdfsSwitchRoot());
        terminalManagePO.setSftpPort(terminalManageVO.getSftpPort());
        terminalManagePO.setSftpUsername(terminalManageVO.getSftpUsername());

        terminalManagePO.setSchemaId(terminalManageVO.getSchemaId());
        terminalManagePO.setSchemaName(terminalManageVO.getSchemaName());

        return terminalManagePO;
    }
}
