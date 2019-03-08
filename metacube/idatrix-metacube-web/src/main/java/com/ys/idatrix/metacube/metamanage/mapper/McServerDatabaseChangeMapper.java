package com.ys.idatrix.metacube.metamanage.mapper;

import com.ys.idatrix.metacube.metamanage.domain.McServerDatabaseChangePO;
import com.ys.idatrix.metacube.metamanage.vo.request.ChangeSearchVO;
import java.util.List;

/**
 * 变更记录数据访问接口
 *
 * @author wzl
 */
public interface McServerDatabaseChangeMapper {

    int insert(McServerDatabaseChangePO changePO);

    List<McServerDatabaseChangePO> search(ChangeSearchVO searchVO);
}
