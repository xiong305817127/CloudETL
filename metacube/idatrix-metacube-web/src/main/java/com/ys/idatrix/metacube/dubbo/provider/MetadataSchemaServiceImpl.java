package com.ys.idatrix.metacube.dubbo.provider;

import com.alibaba.dubbo.config.annotation.Service;
import com.idatrix.unisecurity.api.domain.Organization;
import com.idatrix.unisecurity.api.domain.User;
import com.ys.idatrix.metacube.api.beans.ActionTypeEnum;
import com.ys.idatrix.metacube.api.beans.DatabaseTypeEnum;
import com.ys.idatrix.metacube.api.beans.ModuleTypeEnum;
import com.ys.idatrix.metacube.api.beans.ResultBean;
import com.ys.idatrix.metacube.api.beans.Schema;
import com.ys.idatrix.metacube.api.beans.SchemaDetails;
import com.ys.idatrix.metacube.api.service.MetadataSchemaService;
import com.ys.idatrix.metacube.dubbo.consumer.SecurityConsumer;
import com.ys.idatrix.metacube.metamanage.domain.McDatabasePO;
import com.ys.idatrix.metacube.metamanage.domain.McSchemaPO;
import com.ys.idatrix.metacube.metamanage.domain.McServerPO;
import com.ys.idatrix.metacube.metamanage.service.AuthorityService;
import com.ys.idatrix.metacube.metamanage.service.McDatabaseService;
import com.ys.idatrix.metacube.metamanage.service.McSchemaService;
import com.ys.idatrix.metacube.metamanage.service.McServerService;
import com.ys.idatrix.metacube.metamanage.vo.request.ApprovalProcessVO;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * 元数据模式服务提供者实现
 *
 * @author wzl
 */
@Service
@Component
public class MetadataSchemaServiceImpl implements MetadataSchemaService {

    @Autowired
    private AuthorityService authorityService;

    @Autowired
    @Qualifier("schemaServiceImpl")
    private McSchemaService schemaService;

    @Autowired
    private McServerService serverService;

    @Autowired
    private McDatabaseService databaseService;

    @Autowired
    private SecurityConsumer securityConsumer;

    /**
     * 根据ip和数据库类型返回用户有权限访问的模式列表
     *
     * @param username 用户名
     * @param ip 服务器ip
     * @param databaseType 数据库类型
     * @param module 模块
     * @param actionType 权限类型
     * @return ResultBean <List<Schema>>
     */
    @Override
    public ResultBean<List<Schema>> listSchemaByIpAndDatabaseType(String username, String ip,
            DatabaseTypeEnum databaseType, ModuleTypeEnum module, ActionTypeEnum actionType) {

        List<Integer> databaseTypes = new ArrayList<>();
        databaseTypes.add(databaseType.getCode());

        List<ApprovalProcessVO> processVOList = null;
        // 根据操作权限类型返回数据
        if (module.equals(ModuleTypeEnum.ETL)) {
            processVOList = authorityService
                    .getAuthorizedResource(username, module, actionType, databaseTypes, null);
        }

        List<McSchemaPO> authSchemaList =
                schemaService.listSchemaBySchemaIds(
                        processVOList.stream().map(e -> e.getSchemaId())
                                .collect(Collectors.toList()));

        List<McSchemaPO> orgSchemaList = listSchemaByUsername(username, databaseTypes);

        List<McSchemaPO> schemaList = merge(orgSchemaList, authSchemaList);
        List<Schema> schemas = convertSchema(schemaList);
        return ResultBean.ok(schemas);
    }

    /**
     * 根据模式id返回模式详情
     *
     * @param username 用户名
     * @param schemaId 模式id
     * @return ResultBean<SchemaDetails>
     */
    @Override
    public ResultBean<SchemaDetails> getSchemaById(String username, Long schemaId) {
        McSchemaPO schemaPO = schemaService.getSchemaById(schemaId);
        if (schemaPO == null) {
            return ResultBean.error("模式不存在");
        }
        McDatabasePO databasePO = databaseService.getDatabaseById(schemaPO.getDbId());
        McServerPO serverPO = serverService.getServerById(databasePO.getServerId());

        SchemaDetails details = new SchemaDetails();
        details.setDatabaseId(databasePO.getId())
                .setDatabaseType(DatabaseTypeEnum.getInstance(databasePO.getType()))
                .setIp(serverPO.getIp()).setPort(databasePO.getPort()).setSchemaId(schemaPO.getId())
                .setSchemaName(schemaPO.getName()).setUsername(schemaPO.getUsername())
                .setPassword(schemaPO.getPassword()).setServiceName(schemaPO.getServiceName());
        return ResultBean.ok(details);
    }

    /**
     * 根据用户所属组织获取模式列表
     */
    private List<McSchemaPO> listSchemaByUsername(String username, List<Integer> databaseTypes) {
        Organization org = securityConsumer.getAscriptionDeptByUserName(username);
        String orgCode = org.getDeptCode();
        User user = securityConsumer.findByUserName(username);
        return schemaService.listSchema(orgCode, user.getRenterId(), databaseTypes);
    }

    /**
     * 去重合并两个集合 类型T 需实现hashCode和equals方法
     */
    private <T> List<T> merge(List<T> t1, List<T> t2) {
        return Stream.of(t1, t2).flatMap(Collection::stream).distinct()
                .collect(Collectors.toList());
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
}
