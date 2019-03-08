package com.ys.idatrix.db.service.internal.impl;

import com.ys.idatrix.db.dao.mapper.ESDicChangeMapper;
import com.ys.idatrix.db.service.internal.ESDicChangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ClassName: ESDicChangeServiceImpl
 * @Description:
 * @Author: ZhouJian
 * @Date: 2017/10/12
 */
@Service
public class ESDicChangeServiceImpl implements ESDicChangeService {

    @Autowired(required = false)
    private ESDicChangeMapper esDicChangeMapper;

    @Override
    public boolean existTable(String tenantId) {
        return esDicChangeMapper.existTable(tenantId) > 0;
    }

}
