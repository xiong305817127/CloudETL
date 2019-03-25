package com.ys.idatrix.metacube.metamanage.service.impl;

import com.ys.idatrix.metacube.api.beans.ActionTypeEnum;
import com.ys.idatrix.metacube.api.beans.ModuleTypeEnum;
import com.ys.idatrix.metacube.metamanage.domain.ResourceAuth;
import com.ys.idatrix.metacube.metamanage.mapper.ResourceAuthMapper;
import com.ys.idatrix.metacube.authorize.service.ResourceAuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2019/1/15.
 */
@Transactional
@Slf4j
@Service
public class ResourceAuthServiceImpl implements ResourceAuthService {

    @Autowired
    private ResourceAuthMapper resourceAuthMapper;

    @Override
    public List<ResourceAuth> findAll() {
        return resourceAuthMapper.findAll();
    }

    @Override
    public List<ResourceAuth> findByModuleTypeAndActionType(ModuleTypeEnum moduleType, ActionTypeEnum actionType) {
        if (moduleType == null || actionType == null) {
            return null;
        }
        // 当前模块
        String moduleTypeCode = moduleType.getCode();
        // 操作类型
        List<Integer> authTypes = new ArrayList<>();
        int code = actionType.getCode();
        if (ActionTypeEnum.READ.getCode() == code) {
            authTypes.add(1);
        } else if (ActionTypeEnum.WRITE.getCode() == code) {
            authTypes.add(2);
        } else if (ActionTypeEnum.ALL.getCode() == code || ActionTypeEnum.READORWRITE.getCode() == code) {
            authTypes.add(1);
            authTypes.add(2);
        }
        if (authTypes.size() <= 0) {
            return null;
        }
        List<ResourceAuth> result = resourceAuthMapper.findByAuthNameAndAuthTypes(moduleTypeCode, authTypes);
        return result;
    }

}
