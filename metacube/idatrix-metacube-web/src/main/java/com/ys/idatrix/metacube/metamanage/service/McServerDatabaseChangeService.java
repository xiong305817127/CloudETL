package com.ys.idatrix.metacube.metamanage.service;

import com.ys.idatrix.metacube.api.beans.PageResultBean;
import com.ys.idatrix.metacube.metamanage.domain.McServerDatabaseChangePO;
import com.ys.idatrix.metacube.metamanage.vo.request.ChangeSearchVO;
import java.util.List;

public interface McServerDatabaseChangeService {

    int insert(McServerDatabaseChangePO changePO);

    /**
     * 生成变更记录实体
     *
     * @param type 变更类型 1 服务器 2 数据库 ...
     * @param fkId 逻辑外键 服务器id、数据库id ...
     * @param operator 操作人
     */
    McServerDatabaseChangePO generateChangePO(Integer type, Long fkId, String operator);

    PageResultBean<McServerDatabaseChangePO> list(ChangeSearchVO searchVO);
}
