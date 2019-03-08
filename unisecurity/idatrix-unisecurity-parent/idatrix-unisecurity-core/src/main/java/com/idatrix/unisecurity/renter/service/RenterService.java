package com.idatrix.unisecurity.renter.service;

import com.idatrix.unisecurity.common.domain.URenter;
import com.idatrix.unisecurity.core.mybatis.page.Pagination;

import java.util.Map;

/**
 * Created by james on 2017/5/26.
 */
public interface RenterService {

    Pagination<URenter> findPage(Map<String, Object> resultMap, Integer pageNo, Integer pageSize);

    Integer addRenter(URenter uRenter) throws Exception;

    int updateByPrimaryKeySelective(URenter uRenter) throws Exception;

    Integer deleteRenterById(String ids) throws Exception;

	void updateRenterInfo(URenter uRenter);

	int isExist(URenter uRenter);

	URenter findRenterById(Long id);

	boolean isRentByUserName(String username);

    URenter findByAdminAccount(String username);

    // 重置租户密码为123456
    int restRenterPassword(Long renterId);

    // 修改租户状态，1：正常，2：禁用
    void updateStatus(String renterIds, Long status);
}
