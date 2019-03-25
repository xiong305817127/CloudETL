package com.ys.idatrix.metacube.dubbo.provider;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.alibaba.dubbo.config.annotation.Service;
import com.idatrix.unisecurity.api.domain.Organization;
import com.idatrix.unisecurity.api.domain.User;
import com.ys.idatrix.metacube.api.beans.ActionTypeEnum;
import com.ys.idatrix.metacube.api.beans.Database;
import com.ys.idatrix.metacube.api.beans.DatabaseTypeEnum;
import com.ys.idatrix.metacube.api.beans.ModuleTypeEnum;
import com.ys.idatrix.metacube.api.beans.ResultBean;
import com.ys.idatrix.metacube.api.beans.Schema;
import com.ys.idatrix.metacube.api.service.MetadataDatabaseService;
import com.ys.idatrix.metacube.authorize.service.AuthorityService;
import com.ys.idatrix.metacube.common.enums.DataWarehouseEnum;
import com.ys.idatrix.metacube.common.enums.ServerUseTypeEnum;
import com.ys.idatrix.metacube.common.exception.MetaDataException;
import com.ys.idatrix.metacube.common.utils.AmbariClusterInfoBeanUtils;
import com.ys.idatrix.metacube.dubbo.consumer.SecurityConsumer;
import com.ys.idatrix.metacube.metamanage.beans.AmbariCluesterInfoBean;
import com.ys.idatrix.metacube.metamanage.beans.AmbariHostInfoBean;
import com.ys.idatrix.metacube.metamanage.beans.Host;
import com.ys.idatrix.metacube.metamanage.domain.McDatabasePO;
import com.ys.idatrix.metacube.metamanage.domain.McSchemaPO;
import com.ys.idatrix.metacube.metamanage.domain.McServerPO;
import com.ys.idatrix.metacube.metamanage.service.AmbariRestApiAdapterService;
import com.ys.idatrix.metacube.metamanage.service.McDatabaseService;
import com.ys.idatrix.metacube.metamanage.service.McSchemaService;
import com.ys.idatrix.metacube.metamanage.service.McServerService;
import com.ys.idatrix.metacube.metamanage.vo.request.ApprovalProcessVO;
import com.ys.idatrix.metacube.metamanage.vo.request.DatabaseServerAggregationVO;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * 元数据数据库服务提供者实现
 *
 * @author wzl
 */
@Service(interfaceName = "com.ys.idatrix.metacube.api.service.MetadataDatabaseService")
@Component
public class MetadataDatabaseServiceImpl implements MetadataDatabaseService {

    private static final List<Integer> PLATFORM_DATABASE_TYPE_LIST = new ArrayList<>(4);

    static {
        PLATFORM_DATABASE_TYPE_LIST.add(DatabaseTypeEnum.HDFS.getCode());
        PLATFORM_DATABASE_TYPE_LIST.add(DatabaseTypeEnum.HBASE.getCode());
        PLATFORM_DATABASE_TYPE_LIST.add(DatabaseTypeEnum.HIVE.getCode());
        PLATFORM_DATABASE_TYPE_LIST.add(DatabaseTypeEnum.ELASTICSEARCH.getCode());
    }

    @Autowired
    private McServerService serverService;

    @Autowired
    private McDatabaseService databaseService;

    @Autowired
    private AmbariRestApiAdapterService ambariRestApiAdapterService;

    @Autowired
    @Qualifier("schemaServiceImpl")
    private McSchemaService schemaService;

    @Autowired
    private SecurityConsumer securityConsumer;

    @Autowired
    private AuthorityService authorityService;

    /**
     * 注册平台数据库信息
     *
     * @param renterId 租户id
     */
    @Override
    public ResultBean<Boolean> registerOrUpdatePlatformDatabaseInfo(Long renterId) {
        if (renterId == null) {
            throw new MetaDataException("非法参数renterId");
        }
        List<McDatabasePO> databasePOList =
                databaseService
                        .getPlatformDatabaseByRenterId(renterId, PLATFORM_DATABASE_TYPE_LIST);

        if (databasePOList != null && databasePOList.size() >= PLATFORM_DATABASE_TYPE_LIST.size()) {
            return ResultBean.ok("租户id" + renterId + "已注册全部平台数据库，无需更新", true);
        }

        List<Integer> registered = databasePOList.stream().map(databasePO -> databasePO.getType())
                .collect(Collectors.toList());

        List<Integer> unRegisterTypeList = extractUnRegisterType(registered);

        AmbariCluesterInfoBean bean = ambariRestApiAdapterService.getServiceConfigVersions();
        String hdfs = AmbariClusterInfoBeanUtils.getHDFS(bean);
        String hbase = AmbariClusterInfoBeanUtils.getHBASE(bean);
        String hive = AmbariClusterInfoBeanUtils.getHIVE(bean);
        String es = AmbariClusterInfoBeanUtils.getELASTICSEARCH(bean);

        for (Integer type : unRegisterTypeList) {
            Host host = null;
            if (type == DatabaseTypeEnum.HDFS.getCode()) {
                host = ambariRestApiAdapterService.resolveHDFSHost(hdfs);
            }
            if (type == DatabaseTypeEnum.HBASE.getCode()) {
                host = ambariRestApiAdapterService.resolveHBASEHost(hbase);
            }
            if (type == DatabaseTypeEnum.HIVE.getCode()) {
                host = ambariRestApiAdapterService.resolveHIVEHost(hive);
            }
            if (type == DatabaseTypeEnum.ELASTICSEARCH.getCode()) {
                host = ambariRestApiAdapterService.resolveESHost(es);
            }

            // 调用服务根据主机名获取ip地址
            AmbariHostInfoBean hostInfoBean =
                    ambariRestApiAdapterService.getHostInfoByHostname(host.getHostname());
            if (hostInfoBean == null) {
                continue;
            }
            host.setIp(hostInfoBean.getHosts().getIp());

            // 调用安全服务获取租户信息
            User user = securityConsumer.findRenterInfoByRenterId(renterId);
            Organization org = securityConsumer.findTopOrgByRenterId(renterId);

            // TODO 自动注册数据库 需回写安全的所属组织使用计数器

            McServerPO serverPO = serverService.getServerByIpAndRenterId(host.getIp(), renterId);
            if (serverPO == null) {
                serverPO = registerServer(host.getIp(), host.getHostname(), renterId, type,
                        user.getUsername(), org.getDeptCode());
            }
            registerDatabase(serverPO.getId(), host.getPort(), renterId, type, user.getUsername());
        }

        return null;
    }

    /**
     * 提取尚未注册的平台数据库类型
     */
    private List<Integer> extractUnRegisterType(List<Integer> registered) {
        List<Integer> unRegisterTypeList = new ArrayList<>();
        for (Integer anyType : PLATFORM_DATABASE_TYPE_LIST) {
            if (!registered.contains(anyType)) {
                unRegisterTypeList.add(anyType);
            }
        }
        return unRegisterTypeList;
    }

    /**
     * 返回用户所属组织的数据库列表
     *
     * @param username 用户名
     */
    @Override
    public ResultBean<List<Database>> listDatabase(String username) {
        List<McSchemaPO> schemaPOList = listSchemaByUsername(username);
        List<Schema> schemaList = convertSchema(schemaPOList);

        User user = securityConsumer.findByUserName(username);
        List<DatabaseServerAggregationVO> list = listDatabaseBySchemaList(schemaPOList,
                user.getRenterId(), getDbTypeList());
        List<Database> databaseList = convertDatabase(list);
        fillSchemaIntoDatabase(databaseList, schemaList);

        return ResultBean.ok(databaseList);
    }

    /**
     * 根据用户所属组织获取模式列表
     */
    private List<McSchemaPO> listSchemaByUsername(String username) {
        Organization org = securityConsumer.getAscriptionDeptByUserName(username);
        String orgCode = org.getDeptCode();
        User user = securityConsumer.findByUserName(username);
        return schemaService.listSchema(orgCode, user.getRenterId(), getDbTypeList(), null);
    }

    /**
     * 根据模式列表返回数据库列表
     */
    private List<DatabaseServerAggregationVO> listDatabaseBySchemaList(
            List<McSchemaPO> schemaPOList, Long renterId, List<Integer> dbTypes) {

        List<Long> dbIds = schemaPOList.stream().map(e -> e.getDbId())
                .collect(Collectors.toList());

        return databaseService.list(dbIds, renterId, dbTypes);
    }

    /**
     * 返回用户有权限访问的数据库列表（所属组织 + 模块授权）
     *
     * @param username 用户名
     * @param module 模块名称
     * @param actionType 操作权限类型
     */
    @Override
    public ResultBean<List<Database>> listDatabaseWithModuleAuth(String username,
            ModuleTypeEnum module, ActionTypeEnum actionType) {
        List<Integer> databaseTypes = new ArrayList<>();
        databaseTypes.add(DatabaseTypeEnum.ORACLE.getCode());
        databaseTypes.add(DatabaseTypeEnum.MYSQL.getCode());
        databaseTypes.add(DatabaseTypeEnum.HIVE.getCode());
        databaseTypes.add(DatabaseTypeEnum.HBASE.getCode());

        User user = securityConsumer.findByUserName(username);

        try {
            // 根据操作权限类型返回数据
            List<ApprovalProcessVO> processVOList = authorityService
                    .getAuthorizedResource(username, module, actionType, databaseTypes, null);

            List<DatabaseServerAggregationVO> authDatabaseList = new ArrayList<>();
            List<McSchemaPO> authSchemaList = new ArrayList<>();
            // 授权数据如果不为空
            if (CollectionUtils.isNotEmpty(processVOList)) {
                List<Long> schemaIds = processVOList.stream().map(e -> e.getSchemaId())
                        .collect(Collectors.toList());
                authSchemaList = schemaService.listSchemaBySchemaIds(schemaIds, user.getRenterId()
                        , null, null);
                authDatabaseList = listDatabaseBySchemaList(authSchemaList, user.getRenterId(),
                        getDbTypeList());
            }

            List<McSchemaPO> orgSchemaList = listSchemaByUsername(username);
            List<DatabaseServerAggregationVO> orgDatabaseList = listDatabaseBySchemaList(
                    orgSchemaList, user.getRenterId(), getDbTypeList());

            List<McSchemaPO> schemaList = merge(orgSchemaList, authSchemaList);
            List<DatabaseServerAggregationVO> databaseList = merge(orgDatabaseList,
                    authDatabaseList);

            List<Schema> schemas = convertSchema(schemaList);
            List<Database> databases = convertDatabase(databaseList);
            fillSchemaIntoDatabase(databases, schemas);
            return ResultBean.ok(databases);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResultBean.error("系统异常");
    }

    /**
     * 去重合并两个集合 类型T 需实现hashCode和equals方法
     */
    private <T> List<T> merge(List<T> t1, List<T> t2) {
        return Stream.of(t1, t2).flatMap(Collection::stream).distinct()
                .collect(Collectors.toList());
    }

    /**
     * 根据数据库id获取数据库信息
     *
     * @param databaseId 数据库id
     * @return ResultBean<Database>
     */
    @Override
    public ResultBean<Database> getDatabaseById(Long databaseId) {
        McDatabasePO databasePO = databaseService.getDatabaseById(databaseId);
        McServerPO serverPO = serverService.getServerById(databasePO.getServerId());
        Database database = new Database();
        database.setDatabaseId(databasePO.getId()).setPort(databasePO.getPort())
                .setDatabaseType(DatabaseTypeEnum.getInstance(databasePO.getType()))
                .setServerId(databasePO.getServerId()).setIp(serverPO.getIp())
                .setSchemaList(new ArrayList<>());
        return ResultBean.ok(database);
    }

    /**
     * 注册服务器
     */
    private McServerPO registerServer(String ip, String hostname, Long renterId, int type,
            String username, String orgCode) {
        McServerPO serverPO = new McServerPO();
        serverPO.setName(hostname);
        if (type == DatabaseTypeEnum.ELASTICSEARCH.getCode()) {
            serverPO.setUse(ServerUseTypeEnum.PLATFORM.getCode());
        } else {
            serverPO.setUse(ServerUseTypeEnum.PLATFORM_HADOOP.getCode());
        }
        serverPO.setIp(ip);
        serverPO.setHostname(hostname);
        serverPO.setOrgCode(orgCode);
        serverPO.setCreator(username);
        serverPO.setCreateTime(new Date());
        serverPO.setModifier(username);
        serverPO.setModifyTime(new Date());
        serverPO.setRenterId(renterId);
        serverService.insert(serverPO);
        return serverPO;
    }

    /**
     * 注册数据库
     */
    private void registerDatabase(long serverId, String port, Long renterId, int type,
            String username) {
        McDatabasePO databasePO = new McDatabasePO();
        databasePO.setServerId(serverId);
        databasePO.setType(type);
        databasePO.setBelong(DataWarehouseEnum.DW.getCode());
        databasePO.setPort(Integer.parseInt(port));
        databasePO.setCreator(username);
        databasePO.setCreateTime(new Date());
        databasePO.setModifier(username);
        databasePO.setModifyTime(new Date());
        databasePO.setRenterId(renterId);
        databaseService.insert(databasePO);
    }

    /**
     * 返回支持sql查询的数据库类型
     */
    private List<Integer> getDbTypeList() {
        return Arrays.asList(
                DatabaseTypeEnum.MYSQL.getCode(),
                DatabaseTypeEnum.ORACLE.getCode(),
                DatabaseTypeEnum.HBASE.getCode(),
                DatabaseTypeEnum.HIVE.getCode());
    }

    /**
     * 转换Database
     */
    private List<Database> convertDatabase(List<DatabaseServerAggregationVO> list) {
        return list.stream().map(e -> {
            Database database = new Database();
            BeanUtils.copyProperties(e, database);
            database.setDatabaseType(DatabaseTypeEnum.getInstance(e.getDatabaseType()));
            return database;
        }).collect(Collectors.toList());
    }

    /**
     * 转换Schema
     */
    private List<Schema> convertSchema(List<McSchemaPO> schemaPOList) {
        return schemaPOList.stream().map(e -> {
            Schema schema = new Schema();
            BeanUtils.copyProperties(e, schema);
            schema.setSchemaId(e.getId()).setSchemaName(e.getName()).setDatabaseId(e.getDbId());
            return schema;
        }).collect(Collectors.toList());
    }

    /**
     * 将schema填充进database
     */
    private void fillSchemaIntoDatabase(List<Database> databaseList, List<Schema> schemaList) {
        databaseList.stream().map(database -> {
            database.setSchemaList(schemaList.stream()
                    .filter(schema -> database.getDatabaseId().equals(schema.getDatabaseId()))
                    .collect(Collectors.toList()));
            return database;
        }).collect(Collectors.toList());
    }
}
