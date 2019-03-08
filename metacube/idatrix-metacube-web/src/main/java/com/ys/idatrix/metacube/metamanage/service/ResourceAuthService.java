package com.ys.idatrix.metacube.metamanage.service;

import com.ys.idatrix.metacube.api.beans.ActionTypeEnum;
import com.ys.idatrix.metacube.api.beans.ModuleTypeEnum;
import com.ys.idatrix.metacube.metamanage.domain.ResourceAuth;

import java.util.List;

/**
 * Created by Administrator on 2019/1/15.
 */
public interface ResourceAuthService {

    List<ResourceAuth> findAll();

    List<ResourceAuth> findByModuleTypeAndActionType(ModuleTypeEnum moduleType, ActionTypeEnum actionType);
}
