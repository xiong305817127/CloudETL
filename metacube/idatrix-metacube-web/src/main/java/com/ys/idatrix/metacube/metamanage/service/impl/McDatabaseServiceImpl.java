package com.ys.idatrix.metacube.metamanage.service.impl;

import com.ys.idatrix.db.api.common.RespResult;
import com.ys.idatrix.db.api.rdb.dto.RdbLinkDto;
import com.ys.idatrix.db.api.rdb.service.RdbService;
import com.ys.idatrix.graph.service.api.dto.node.DatabaseNodeDto;
import com.ys.idatrix.metacube.api.beans.DatabaseTypeEnum;
import com.ys.idatrix.metacube.common.exception.MetaDataException;
import com.ys.idatrix.metacube.common.helper.GraphDatabaseTypeConvert;
import com.ys.idatrix.metacube.common.utils.UserUtils;
import com.ys.idatrix.metacube.dubbo.consumer.GraphConsumer;
import com.ys.idatrix.metacube.metamanage.domain.McDatabasePO;
import com.ys.idatrix.metacube.metamanage.domain.McServerPO;
import com.ys.idatrix.metacube.metamanage.mapper.McDatabaseMapper;
import com.ys.idatrix.metacube.metamanage.service.McDatabaseService;
import com.ys.idatrix.metacube.metamanage.service.McServerService;
import com.ys.idatrix.metacube.sysmanage.service.SystemSettingsService;
import com.ys.idatrix.metacube.metamanage.vo.request.DatabaseServerAggregationVO;
import java.sql.SQLException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 数据库服务实现类
 *
 * @author wzl
 */
@Service
public class McDatabaseServiceImpl implements McDatabaseService {

    @Autowired
    private McDatabaseMapper databaseMapper;

    @Autowired
    private McServerService serverService;

    @Autowired
    private SystemSettingsService systemSettingsService;

    @Autowired
    private GraphConsumer graphConsumer;

    @Autowired
    private RdbService rdbService;

    private McServerPO getServerById(Long serverId) {
        return serverService.getServerById(serverId);
    }

    /**
     * 校验权限 只有数据中心管理员和数据库管理员才有权限维护数据库
     */
    private void authentication() {
        if (!systemSettingsService.isDataCentreAdmin()
                && !systemSettingsService.isDatabaseAdmin()) {
            throw new MetaDataException("权限不足");
        }
    }

    @Override
    public boolean exists(String ip, int type, Long renterId) {
        return getDatabaseByIpAndType(ip, type, renterId) != null;
    }

    /**
     * 注册数据库 鉴权、数据校验、业务处理
     */
    @Override
    public McDatabasePO register(McDatabasePO databasePO) {
        authentication();
        return insert(databasePO);
    }

    @Transactional(rollbackFor = {RuntimeException.class, SQLException.class})
    @Override
    public McDatabasePO insert(McDatabasePO databasePO) {
        McServerPO serverPO = getServerById(databasePO.getServerId());
        checkUniqueness(databasePO.getType(), serverPO.getIp(), serverPO.getRenterId());
        databasePO.setRenterId(serverPO.getRenterId());
        databaseMapper.insert(databasePO);

        // 新增数据库节点
        if (databasePO.getType() != DatabaseTypeEnum.ELASTICSEARCH.getCode()) {
            DatabaseNodeDto nodeDto = new DatabaseNodeDto();
            nodeDto.setDatabaseId(databasePO.getId());
            nodeDto.setDatabaseType(
                    GraphDatabaseTypeConvert.getGraphDatabaseType(databasePO.getType()));
            nodeDto.setRenterId(serverPO.getRenterId());
            nodeDto.setServerId(serverPO.getId());
            graphConsumer.createDatabaseNode(nodeDto);
        }

        return databasePO;
    }

    /**
     * 检查唯一性
     *
     * @param type 数据库类型
     * @param ip 服务器ip
     */
    private void checkUniqueness(int type, String ip, Long renterId) {
        McDatabasePO databasePO = getDatabaseByIpAndType(ip, type, renterId);
        if (databasePO != null) {
            throw new MetaDataException(
                    "服务器" + ip + "已存在" + DatabaseTypeEnum.getName(type) + "数据库");
        }
    }

    @Override
    public List<McDatabasePO> getDatabaseByServerIds(List<Long> serverIds) {
        return databaseMapper.listDatabaseByServerIds(serverIds);
    }

    /**
     * 根据租户id获取系统自动注册的平台数据库
     */
    @Override
    public List<McDatabasePO> getPlatformDatabaseByRenterId(Long renterId,
            List<Integer> typeList) {
        return databaseMapper.listPlatformDatabase(renterId, typeList);
    }

    /**
     * 获取数据库详情
     *
     * @param id 数据库id
     */
    @Override
    public McDatabasePO getDatabaseById(Long id) {
        McDatabasePO databasePO = databaseMapper.getDatabaseById(id);
        if (databasePO == null) {
            throw new MetaDataException("数据库不存在");
        }
        return databasePO;
    }

    /**
     * 根据ip和类型查询数据库
     *
     * @param ip 服务器ip
     */
    private McDatabasePO getDatabaseByIpAndType(String ip, int type, Long renterId) {
        return databaseMapper.getDatabase(ip, type, renterId);
    }

    /**
     * 注销数据库
     *
     * @param id 数据库id
     * @param username 当前用户
     */
    @Override
    public void delete(Long id, String username) {
        McDatabasePO databasePO = getDatabaseById(id);
        if (databasePO == null) {
            return;
        }
//        if (databasePO.getType() != DatabaseTypeEnum.MYSQL.getCode()
//                && databasePO.getType() != DatabaseTypeEnum.ORACLE.getCode()) {
//            throw new MetaDataException("非法操作,不支持注销该类型的数据库");
//        }
        databasePO.setIsDeleted(1).fillModifyInfo(databasePO, username);
        databaseMapper.update(databasePO);

        // 删除数据库节点
        if (databasePO.getType() != DatabaseTypeEnum.ELASTICSEARCH.getCode()) {
            graphConsumer.deleteDatabaseNode(databasePO.getId());
        }
    }

    /**
     * 更新数据库
     */
    @Override
    public McDatabasePO update(McDatabasePO databasePO) {
        authentication();
        McDatabasePO oldDatabasePO = getDatabaseById(databasePO.getId());
        if (oldDatabasePO == null) {
            throw new MetaDataException("数据库不存在或已删除");
        }
        databasePO.setRenterId(UserUtils.getRenterId());
        databasePO.fillCreateInfo(databasePO, UserUtils.getUserName());
        databaseMapper.update(databasePO);
        return getDatabaseById(databasePO.getId());
    }


    /**
     * 获取数据库列表
     *
     * @param dbIds 数据库id列表
     */
    @Override
    public List<DatabaseServerAggregationVO> list(List<Long> dbIds, Long renterId,
            List<Integer> dbTypes) {
        return databaseMapper.listDatabaseByDbIds(dbIds, renterId, dbTypes);
    }

    /**
     * 测试连接
     */
    @Override
    public RespResult<Boolean> testDbLink(RdbLinkDto dto) {
        RespResult<Boolean> result = rdbService.testDBLink(dto);
        if (!result.isSuccess()) {
            throw new MetaDataException(result.getMsg());
        }
        return result;
    }
}
