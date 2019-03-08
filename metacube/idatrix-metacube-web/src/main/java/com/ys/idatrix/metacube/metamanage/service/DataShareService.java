package com.ys.idatrix.metacube.metamanage.service;

import com.ys.idatrix.metacube.metamanage.vo.response.DBConnectionVO;

import java.util.List;

/**
 * @ClassName DataShareService
 * @Description
 * @Author ouyang
 * @Date
 */
public interface DataShareService {


    // 根据用户名获取 表 或 视图
    List<DBConnectionVO> findTableOrView(String username);
}