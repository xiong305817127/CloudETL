package com.ys.idatrix.metacube.metamanage.service.impl;

import com.google.common.collect.ImmutableList;
import com.idatrix.unisecurity.api.domain.Organization;
import com.ys.idatrix.metacube.api.beans.ActionTypeEnum;
import com.ys.idatrix.metacube.api.beans.DatabaseTypeEnum;
import com.ys.idatrix.metacube.api.beans.ModuleTypeEnum;
import com.ys.idatrix.metacube.authorize.service.AuthorityService;
import com.ys.idatrix.metacube.dubbo.consumer.SecurityConsumer;
import com.ys.idatrix.metacube.metamanage.domain.McSchemaPO;
import com.ys.idatrix.metacube.metamanage.domain.Metadata;
import com.ys.idatrix.metacube.metamanage.mapper.McDatabaseMapper;
import com.ys.idatrix.metacube.metamanage.mapper.MetadataMapper;
import com.ys.idatrix.metacube.metamanage.service.DataShareService;
import com.ys.idatrix.metacube.metamanage.service.McSchemaService;
import com.ys.idatrix.metacube.metamanage.vo.request.ApprovalProcessVO;
import com.ys.idatrix.metacube.metamanage.vo.request.MetadataSearchVo;
import com.ys.idatrix.metacube.metamanage.vo.response.DBConnectionVO;
import com.ys.idatrix.metacube.metamanage.vo.response.DatasourceVO;
import com.ys.idatrix.metacube.metamanage.vo.response.TableVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName DataShareServiceImpl
 * @Description
 * @Author ouyang
 * @Date
 */
@Slf4j
@Service
public class DataShareServiceImpl implements DataShareService {

    private String ip = "183.15.183.3";
    private String mysqlPort = "60011";
    private String oraclePort = "60091";

    @Autowired
    private SecurityConsumer securityConsumer;

    @Autowired
    @Qualifier("mySqlSchemaService")
    private McSchemaService schemaService;

    @Autowired
    private McDatabaseMapper databaseMapper;

    @Autowired
    private AuthorityService authorityService;

    @Autowired
    private MetadataMapper metadataMapper;

    @Override
    public List<DBConnectionVO> findTableOrView(String username) {
        /*
        // TODO 先使用数据拼接方式返回数据，后面再来实现
        if (username.equals("oyr")) {
            List<DBConnectionVO> dbConnections = new ArrayList<>();

            DBConnectionVO db1 = new DBConnectionVO();
            db1.setType("MYSQL");
            db1.setIp(ip);
            db1.setPort(mysqlPort);
            db1.setDataBaseName("security");
            db1.setUsername("root");
            db1.setPassword("root");

            List<TableVO> tableList1 = new ArrayList<>();

            TableVO user = new TableVO();
            user.setTable("user");
            user.setTableName("用户表");

            TableVO role = new TableVO();
            role.setTable("role");
            role.setTableName("角色表");

            tableList1.add(user);
            tableList1.add(role);

            List<TableVO> viewList = new ArrayList<>();

            TableVO user_view = new TableVO();
            user_view.setTable("view_user");
            user_view.setTableName("用户视图");
            viewList.add(user_view);

            db1.setTableList(tableList1);
            db1.setViewList(viewList);

            DBConnectionVO db2 = new DBConnectionVO();
            db2.setType("ORACLE");
            db2.setIp(ip);
            db2.setPort(oraclePort);
            db2.setDataBaseName("ORCL");
            db2.setUsername("oyr");
            db2.setPassword("a123456");

            List<TableVO> tableList2 = new ArrayList<>();

            TableVO metadata = new TableVO();
            metadata.setTable("metadata");
            metadata.setTableName("元数据表");

            TableVO schema = new TableVO();
            schema.setTable("schema");
            schema.setTableName("模式表");

            tableList2.add(metadata);
            tableList2.add(schema);

            db2.setTableList(tableList2);

            dbConnections.add(db1);
            dbConnections.add(db2);

            return dbConnections;
        }
        return null;*/
        // 当前需要查询出 mysql 和 oracle 的数据，如果数据库类型不为mysql与oracle的ResourceType都是为null的
        Set<TableVO> tableResult = new HashSet<>();
        Set<TableVO> viewResult = new HashSet<>();

        // 所属部门
        Organization ascriptionDept = securityConsumer.getAscriptionDeptByUserName(username);

        // ========= 用户所属组织下元数据信息
        MetadataSearchVo vo = new MetadataSearchVo();
        vo.setRegCode(ascriptionDept.getDeptCode()); // 所属组织代码
        vo.setResourceType(1); // 表
        List<Metadata> ascriptionDeptTableList = metadataMapper.list(vo);
        metadataPropertyCopy(ascriptionDeptTableList, tableResult, 1);

        vo.setResourceType(2); // 视图
        List<Metadata> ascriptionDeptViewList = metadataMapper.list(vo);
        metadataPropertyCopy(ascriptionDeptViewList, viewResult, 2);

        // ========= 用户授权数据

        // 授权表资源
        List<ApprovalProcessVO> tableResourceList =
                authorityService.getAuthorizedResource(username, ModuleTypeEnum.ETL, ActionTypeEnum.READORWRITE, ImmutableList.of(1, 2), ImmutableList.of(1));
        addTableOrView(tableResourceList, tableResult, 1);
        // 授权视图资源
        List<ApprovalProcessVO> viewResourceList =
                authorityService.getAuthorizedResource(username, ModuleTypeEnum.ETL, ActionTypeEnum.READORWRITE, ImmutableList.of(1, 2), ImmutableList.of(2));
        addTableOrView(viewResourceList, viewResult, 2);


        ArrayList<TableVO> all = new ArrayList();
        all.addAll(tableResult);
        all.addAll(viewResult);

        // 返回的结果
        List<DBConnectionVO> result = new ArrayList<>();

        Map<Long, List<TableVO>> allListMap = all.stream().collect(Collectors.groupingBy(TableVO::getSchemaId));
        allListMap.forEach((key, value) -> {
            // 数据库连接数据
            McSchemaPO schema = schemaService.findById(key);
            DatasourceVO datasource = databaseMapper.getDatasourceInfoById(schema.getDbId());
            String dbName = Integer.parseInt(datasource.getType()) == DatabaseTypeEnum.MYSQL.getCode() ? schema.getName() : schema.getServiceName();
            String dataType = Integer.parseInt(datasource.getType()) == DatabaseTypeEnum.MYSQL.getCode() ? "MYSQL" : "ORACLE";
            DBConnectionVO connectionVO = new DBConnectionVO(datasource.getIp(), datasource.getPort(),
                    dbName, schema.getUsername(), schema.getPassword(), dataType);
            // 表 和 视图
            List<TableVO> tableList = new ArrayList<>();
            List<TableVO> viewList = new ArrayList<>();
            for (TableVO val : value) {
                if (val.getResourceType() == 1) {
                    tableList.add(val);
                } else {
                    viewList.add(val);
                }
            }
            connectionVO.setTableList(tableList);
            connectionVO.setViewList(viewList);
            result.add(connectionVO);
        });
        return result;
    }

    public void addTableOrView(List<ApprovalProcessVO> resourceList, Set<TableVO> target, Integer resourceType) {
        if (CollectionUtils.isEmpty(resourceList)) {
            return;
        }
        List<Long> resourceId = new ArrayList<>();
        for (ApprovalProcessVO vo : resourceList) {
            resourceId.add(vo.getId());
        }
        List<Metadata> list = metadataMapper.findByIdList(resourceId);
        metadataPropertyCopy(list, target, resourceType);
    }

    public void metadataPropertyCopy(List<Metadata> source, Set<TableVO> target, Integer resourceType) {
        for (Metadata metadata : source) {
            TableVO vo = new TableVO();
            vo.setTable(metadata.getName());
            vo.setTableName(metadata.getIdentification());
            vo.setSchemaId(metadata.getSchemaId());
            vo.setResourceType(resourceType);
            target.add(vo);
        }
    }

}