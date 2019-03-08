package com.ys.idatrix.metacube.metamanage.service;

import com.ys.idatrix.metacube.api.beans.PageResultBean;
import com.ys.idatrix.metacube.metamanage.domain.McServerDatabaseChangePO;
import com.ys.idatrix.metacube.metamanage.domain.McServerPO;
import com.ys.idatrix.metacube.metamanage.vo.request.ChangeSearchVO;
import com.ys.idatrix.metacube.metamanage.vo.request.ServerSearchVO;
import com.ys.idatrix.metacube.metamanage.vo.response.ServerVO;
import java.util.List;

public interface McServerService {

    /**
     * 注册服务器 鉴权、数据校验、业务处理
     */
    McServerPO register(McServerPO serverPO);

    /**
     * 新增服务器 业务表写入
     */
    McServerPO insert(McServerPO serverPO);

    /**
     * 更新服务器
     */
    McServerPO update(McServerPO serverPO);

    /**
     * 根据id获取服务器
     */
    McServerPO getServerById(Long id);

    /**
     * 根据ip获取服务器
     */
    McServerPO getServerByIp(String ip);

    /**
     * 注销服务器
     */
    void delete(Long id, String username);

    /**
     * 获取服务器列表
     */
    PageResultBean<List<ServerVO>> list(ServerSearchVO searchVO);

    /**
     * 根据ip和租户id查找服务器
     */
    McServerPO getServerByIpAndRenterId(String ip, Long renterId);

    boolean exists(String ip, Long renterId);

    /**
     * 变更记录列表
     */
    PageResultBean<List<McServerDatabaseChangePO>> listChangeLog(ChangeSearchVO searchVO);
}
