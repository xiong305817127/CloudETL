package com.ys.idatrix.metacube.metamanage.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ys.idatrix.metacube.api.beans.PageResultBean;
import com.ys.idatrix.metacube.common.enums.ChangeTypeEnum;
import com.ys.idatrix.metacube.metamanage.domain.McServerDatabaseChangePO;
import com.ys.idatrix.metacube.metamanage.mapper.McServerDatabaseChangeMapper;
import com.ys.idatrix.metacube.metamanage.service.McServerDatabaseChangeService;
import com.ys.idatrix.metacube.metamanage.vo.request.ChangeSearchVO;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class McServerDatabaseChangeServiceImpl implements McServerDatabaseChangeService {

    @Autowired
    private McServerDatabaseChangeMapper changeMapper;

    @Override
    public int insert(McServerDatabaseChangePO changePO) {
        return changeMapper.insert(changePO);
    }

    /**
     * 生成变更记录实体
     *
     * @param type 变更类型 1 服务器 2 数据库 ...
     * @param fkId 逻辑外键 服务器id、数据库id ...
     * @param operator 操作人
     */
    @Override
    public McServerDatabaseChangePO generateChangePO(Integer type, Long fkId, String operator) {
        McServerDatabaseChangePO changePO = new McServerDatabaseChangePO();
        changePO.setType(ChangeTypeEnum.SERVER.getCode())
                .setFkId(fkId)
                .setCreateTime(new Date())
                .setOperator(operator);
        return changePO;
    }

    @Override
    public PageResultBean<List<McServerDatabaseChangePO>> list(ChangeSearchVO searchVO) {
        PageHelper.startPage(searchVO.getPageNum(), searchVO.getPageSize());
        List<McServerDatabaseChangePO> changePOList = changeMapper.search(searchVO);
        PageInfo<McServerDatabaseChangePO> info = new PageInfo<>(changePOList);
        return PageResultBean.of(searchVO.getPageNum(), info.getTotal(), changePOList);
    }
}
