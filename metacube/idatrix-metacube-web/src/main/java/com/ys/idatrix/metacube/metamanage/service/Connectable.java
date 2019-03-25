package com.ys.idatrix.metacube.metamanage.service;

import com.ys.idatrix.db.api.common.RespResult;
import com.ys.idatrix.db.api.rdb.dto.RdbLinkDto;

/**
 * 可连接的
 */
public interface Connectable {

    /**
     * 测试连接
     */
    default RespResult<Boolean> testDbLink(RdbLinkDto dto) {
        return null;
    }
}
