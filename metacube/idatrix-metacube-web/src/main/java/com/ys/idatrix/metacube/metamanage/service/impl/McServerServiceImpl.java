package com.ys.idatrix.metacube.metamanage.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.idatrix.unisecurity.api.domain.Organization;
import com.idatrix.unisecurity.api.service.OrganizationService;
import com.ys.idatrix.graph.service.api.dto.node.ServerNodeDto;
import com.ys.idatrix.metacube.api.beans.DatabaseTypeEnum;
import com.ys.idatrix.metacube.api.beans.PageResultBean;
import com.ys.idatrix.metacube.common.enums.ChangeTypeEnum;
import com.ys.idatrix.metacube.common.exception.MetaDataException;
import com.ys.idatrix.metacube.common.utils.UserUtils;
import com.ys.idatrix.metacube.dubbo.consumer.GraphConsumer;
import com.ys.idatrix.metacube.metamanage.domain.McDatabasePO;
import com.ys.idatrix.metacube.metamanage.domain.McSchemaPO;
import com.ys.idatrix.metacube.metamanage.domain.McServerDatabaseChangePO;
import com.ys.idatrix.metacube.metamanage.domain.McServerPO;
import com.ys.idatrix.metacube.metamanage.mapper.McServerMapper;
import com.ys.idatrix.metacube.metamanage.service.McDatabaseService;
import com.ys.idatrix.metacube.metamanage.service.McSchemaService;
import com.ys.idatrix.metacube.metamanage.service.McServerDatabaseChangeService;
import com.ys.idatrix.metacube.metamanage.service.McServerService;
import com.ys.idatrix.metacube.metamanage.vo.request.ChangeSearchVO;
import com.ys.idatrix.metacube.metamanage.vo.request.ServerSearchVO;
import com.ys.idatrix.metacube.metamanage.vo.response.DatabaseVO;
import com.ys.idatrix.metacube.metamanage.vo.response.SchemaListVO;
import com.ys.idatrix.metacube.metamanage.vo.response.ServerVO;
import com.ys.idatrix.metacube.sysmanage.service.SystemSettingsService;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class McServerServiceImpl implements McServerService {

    private static final String REGISTER = "注册";
    private static final String DESTROY = "注销";

    @Autowired
    private McServerMapper serverMapper;

    @Autowired
    private McDatabaseService databaseService;

    @Autowired
    @Qualifier("schemaServiceImpl")
    private McSchemaService schemaService;

    @Autowired
    private McServerDatabaseChangeService changeService;

    @Autowired
    private SystemSettingsService systemSettingsService;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private GraphConsumer graphConsumer;

    /**
     * 校验权限 只有数据中心管理员和数据库管理员有权限维护服务器
     */
    private void authentication() {
        if (!systemSettingsService.isDataCentreAdmin()
                && !systemSettingsService.isDatabaseAdmin()) {
            throw new MetaDataException("权限不足");
        }
    }

    /**
     * 注册服务器 鉴权、数据校验、业务处理
     */
    @Override
    public McServerPO register(McServerPO serverPO) {
        authentication();
        serverPO.setRenterId(UserUtils.getRenterId());
        serverPO.fillCreateInfo(serverPO, UserUtils.getUserName());
        return insert(serverPO);
    }

    @Transactional(rollbackFor = {RuntimeException.class, SQLException.class})
    @Override
    public McServerPO insert(McServerPO serverPO) {
        if (exists(serverPO.getIp(), serverPO.getRenterId())) {
            throw new MetaDataException(duplicateKeyMessage(serverPO.getIp()));
        }
        serverMapper.insert(serverPO);
        changeService.insert(generateChangePO(serverPO.getId(),
                serverPO.getCreator()).setContent(generateContentForRegisterServer()));

        // TODO 需回写安全的所属组织使用计数器

        // 保存服务器节点
        ServerNodeDto serverNodeDto = new ServerNodeDto();
        serverNodeDto.setRenterId(serverPO.getRenterId());
        serverNodeDto.setServerId(serverPO.getId());
        graphConsumer.createServerNode(serverNodeDto);
        return serverPO;
    }

    @Transactional(rollbackFor = {RuntimeException.class, SQLException.class})
    @Override
    public McServerPO update(McServerPO serverPO) {
        authentication();
        McServerPO oldServerPO = getServerById(serverPO.getId());
        if (oldServerPO == null || oldServerPO.getId() == null) {
            throw new MetaDataException("服务器不存在或已删除");
        }
        serverPO.setRenterId(UserUtils.getRenterId());
        serverPO.fillModifyInfo(serverPO, UserUtils.getUserName());

        McServerPO server = getServerByIpAndRenterId(serverPO.getIp(), serverPO.getRenterId());
        if (server != null && !serverPO.getId().equals(server.getId())) {
            throw new MetaDataException(duplicateKeyMessage(serverPO.getIp()));
        }
        serverMapper.update(serverPO);

        // TODO 需回写安全的所属组织使用计数器

        // 记录变更 当前版本只记录ip，若ip未变更，则直接返回
        if (serverPO.getIp().equals(oldServerPO.getIp())) {
            return getServerById(serverPO.getId());
        }

        changeService.insert(generateChangePO(serverPO.getId(), serverPO.getModifier())
                .setContent(generateContentForChangeIp(oldServerPO.getIp(),
                        serverPO.getIp())));

        return getServerById(serverPO.getId());
    }

    private String duplicateKeyMessage(String ip) {
        StringBuilder builder = new StringBuilder();
        builder.append("服务器").append(ip).append("已存在");
        return builder.toString();
    }

    @Transactional(rollbackFor = {RuntimeException.class, SQLException.class})
    @Override
    public void delete(Long id, String username) {
        authentication();
        McServerPO serverPO = getServerById(id);
        if (serverPO == null) {
            return;
        }
        serverPO.setIsDeleted(1).fillModifyInfo(serverPO, username);
        serverMapper.update(serverPO);
        changeService.insert(generateChangePO(serverPO.getId(), username)
                .setContent(generateContentForDestroyServer()));

        // TODO 需回写安全的所属组织使用计数器

        // 删除服务器节点
        graphConsumer.deleteServerNode(serverPO.getId());
    }

    @Override
    public McServerPO getServerById(Long id) {
        McServerPO serverPO = serverMapper.getServerPOById(id);
        if (serverPO == null) {
            throw new MetaDataException("服务器不存在");
        }
        return serverPO;
    }

    @Override
    public ServerVO getServerVOById(Long id) {
        McServerPO serverPO = getServerById(id);
        ServerVO serverVO = new ServerVO();
        BeanUtils.copyProperties(serverPO, serverVO);
        // 填充组织名称
        Organization org = organizationService.findByCode(serverVO.getOrgCode());
        serverVO.setOrgName(org.getDeptName());
        return serverVO;
    }

    /**
     * 根据ip获取服务器
     */
    @Override
    public McServerPO getServerByIp(String ip) {
        return serverMapper.getServerPOByIp(ip);
    }

    @Override
    public PageResultBean<ServerVO> list(ServerSearchVO searchVO) {

        // 普通用户返回空
        if (!systemSettingsService.isDataCentreAdmin() && !systemSettingsService
                .isDatabaseAdmin()) {
            return PageResultBean.empty();
        }

        PageHelper.startPage(searchVO.getPageNum(), searchVO.getPageSize());
        List<McServerPO> serverPOList = serverMapper.list(searchVO);
        PageInfo<McServerPO> info = new PageInfo<>(serverPOList);

        List<ServerVO> serverVOList = transferServerPOToServerVO(serverPOList);

        // 填充组织名称
        List<String> orgCodeList =
                serverVOList.stream().map(e -> e.getOrgCode()).collect(Collectors.toList());
        String orgCodes = String.join(",", orgCodeList);
        List<Organization> orgList = organizationService.findByCodes(orgCodes);

        // 查询已删除列表
        if (searchVO.getDeleted()) {
            fillEmptyList(serverVOList);
            return PageResultBean.of(searchVO.getPageNum(), info.getTotal(),
                    fillOrgNameIntoServerVO(serverVOList, orgList));
        }

        // 填充服务器下的数据库
        List<Long> serverIds =
                serverPOList.stream().map(e -> e.getId()).collect(Collectors.toList());
        List<McDatabasePO> databasePOList = databaseService.getDatabaseByServerIds(serverIds);
        List<DatabaseVO> databaseVOList = transferDatabasePOToDatabaseVO(databasePOList, true);
        fillDatabaseList(serverVOList, databaseVOList);
        return PageResultBean.of(searchVO.getPageNum(), info.getTotal(),
                fillOrgNameIntoServerVO(serverVOList, orgList));
    }

    private List<ServerVO> transferServerPOToServerVO(List<McServerPO> serverPOList) {
        return serverPOList.stream().map(e -> {
            ServerVO serverVO = new ServerVO();
            BeanUtils.copyProperties(e, serverVO);
            return serverVO;
        }).collect(Collectors.toList());
    }

    /**
     * 转换DatabasePO为DatabaseVO
     *
     * @param databasePOList 数据库PO列表
     * @param fillSchema 是否填充模式 默认false
     */
    private List<DatabaseVO> transferDatabasePOToDatabaseVO(List<McDatabasePO> databasePOList,
            boolean fillSchema) {
        List<SchemaListVO> schemaVOList = null;
        if (fillSchema) {
            // 填充模式
            List<Long> dbIds = databasePOList.stream().map(e -> e.getId())
                    .collect(Collectors.toList());
            List<McSchemaPO> schemaPOList = schemaService.listSchemaByDatabaseIds(dbIds);
            schemaVOList = schemaService.convertSchemaListVO(schemaPOList);

            List<String> orgCodeList =
                    schemaVOList.stream().map(e -> e.getOrgCode()).collect(Collectors.toList());
            String orgCodes = String.join(",", orgCodeList);
            List<Organization> orgList = organizationService.findByCodes(orgCodes);
            schemaVOList = schemaService.fillOrgNameIntoSchemaVO(schemaVOList,
                    orgList);
        }

        final List<SchemaListVO> result = schemaVOList;

        List<DatabaseVO> databaseVOList =
                databasePOList.stream().map(e -> {
                    DatabaseVO databaseVO = new DatabaseVO();
                    BeanUtils.copyProperties(e, databaseVO);
                    databaseVO.setName(DatabaseTypeEnum.getName(e.getType()));
                    if (fillSchema) {
                        databaseVO.setSchemaList(filterSchemaByDbId(result, e.getId()));
                    }

                    return databaseVO;
                }).collect(Collectors.toList());

        return databaseVOList;
    }

    private List<SchemaListVO> filterSchemaByDbId(List<SchemaListVO> schemaVOList, Long dbId) {
        return schemaVOList.stream().filter(e -> e.getDbId().equals(dbId))
                .collect(Collectors.toList());
    }

    /**
     * 填充数据库列表
     */
    private void fillDatabaseList(List<ServerVO> serverVOList, List<DatabaseVO> databaseVOList) {
        serverVOList.stream().map(serverVO -> {
                    serverVO.setDatabaseList(databaseVOList.stream()
                            .filter(databaseVO -> serverVO.getId().equals(databaseVO.getServerId()))
                            .map(databaseVO -> {
                                databaseVO.setIp(serverVO.getIp());
                                return databaseVO;
                            })
                            .collect(Collectors.toList()));
                    return serverVO;
                }
        ).collect(Collectors.toList());
    }

    /**
     * 填充空列表
     */
    private void fillEmptyList(List<ServerVO> serverVOList) {
        serverVOList.stream().map(serverVO -> {
            serverVO.setDatabaseList(new ArrayList<>());
            return serverVO;
        }).collect(Collectors.toList());
    }

    @Override
    public McServerPO getServerByIpAndRenterId(String ip, Long renterId) {
        return serverMapper.getServerPOByIpAndRenterId(ip, renterId);
    }

    @Override
    public boolean exists(String ip, Long renterId) {
        return getServerByIpAndRenterId(ip, renterId) != null;
    }

    @Override
    public PageResultBean<McServerDatabaseChangePO> listChangeLog(ChangeSearchVO searchVO) {
        authentication();
        return changeService.list(searchVO);
    }

    /**
     * 生成变更记录实体
     *
     * @param id 服务器id
     * @param username 操作人（当前用户）
     */
    private McServerDatabaseChangePO generateChangePO(Long id, String username) {
        return changeService
                .generateChangePO(ChangeTypeEnum.SERVER.getCode(), id, username);
    }

    /**
     * 生成注册服务器的变更内容
     */
    private String generateContentForRegisterServer() {
        return REGISTER;
    }

    /**
     * 生成注销服务器的变更内容
     */
    private String generateContentForDestroyServer() {
        return DESTROY;
    }

    /**
     * 生成变更IP的内容
     */
    private String generateContentForChangeIp(String oldIp, String newIp) {
        StringBuilder builder = new StringBuilder();
        builder.append("变更IP（");
        builder.append(oldIp);
        builder.append("->");
        builder.append(newIp);
        builder.append("）");
        return builder.toString();
    }

    /**
     * 填充组织名称
     */
    private List<ServerVO> fillOrgNameIntoServerVO(List<ServerVO> serverVOList,
            List<Organization> orgList) {
        return serverVOList.stream().map(serverVO -> {
            serverVO.setOrgName(orgList.stream()
                    .filter(org -> serverVO.getOrgCode().equals(org.getDeptCode()))
                    .map(Organization::getDeptName).findAny().orElse("")
            );
            return serverVO;
        }).collect(Collectors.toList());
    }

}
