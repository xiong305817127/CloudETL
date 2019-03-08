package com.idatrix.resource.catalog.service.Impl;

import com.idatrix.resource.basedata.dao.SystemConfigDAO;
import com.idatrix.resource.basedata.po.SystemConfigPO;
import com.idatrix.resource.basedata.service.ISystemConfigService;
import com.idatrix.resource.catalog.dao.CatalogNodeDAO;
import com.idatrix.resource.catalog.dao.CatalogResourceDAO;
import com.idatrix.resource.catalog.dao.ResourceConfigDAO;
import com.idatrix.resource.catalog.po.CatalogNodePO;
import com.idatrix.resource.catalog.po.CatalogResourcePO;
import com.idatrix.resource.catalog.po.ResourceConfigPO;
import com.idatrix.resource.catalog.service.ICatalogClassifyService;
import com.idatrix.resource.catalog.vo.CatalogNodeVO;
import com.idatrix.resource.common.utils.BatchTools;
import com.idatrix.resource.common.utils.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.File;
import java.util.*;


@Transactional
@Service("catalogClassifyService")
public class CatalogClassifyServiceImpl implements ICatalogClassifyService {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CatalogNodeDAO catalogNodeDAO;

    @Autowired
    private CatalogResourceDAO catalogResourceDAO;

    @Autowired
    private ResourceConfigDAO resourceConfigDAO;

    @Autowired
    private BatchTools batchTools;

    @Autowired
    private SystemConfigDAO systemConfigDAO;

    @Autowired
    private ISystemConfigService systemConfigService;

    @Override
    public Long saveCatalogNode(String user, CatalogNodeVO node) throws Exception {

        Long id = node.getId();
        //考虑查重，原则是 统一节点下方不能有名字或者编码相同数据项
        if (ownSameNameOrCode(node)) {
            throw new RuntimeException("在同一资源目录下面配置了相同的节点名称或者编码");
        }
        //页面配置的时候 catalogFullName 里面没有内容，自行处理往里面添加内容
        Long parentId = node.getParentId();
        String parentFullCode = "0";
        if (parentId != 0L) {
            CatalogNodePO cnPO = catalogNodeDAO.getCatalogNodeById(parentId);
            if (StringUtils.equals(cnPO.getParentFullCode(), "0")) {
                parentFullCode = cnPO.getResourceEncode();
            } else {
                parentFullCode = cnPO.getParentFullCode() + cnPO.getResourceEncode();
            }
        }
        node.setParentFullCode(parentFullCode);

        if (id == null || id == 0L) {
            id = addCatalogNode(user, node);
        } else {
            id = updateCatalogNode(user, node);
        }
        return id;
    }

    @Override
    public int deleteCatalogNode(String user, Long id) throws Exception {
        List<CatalogResourcePO> crPOList = new ArrayList<CatalogResourcePO>();
        crPOList = catalogResourceDAO.getByCatalogId(id);

        CatalogNodePO cnPO = catalogNodeDAO.getCatalogNodeById(id);
        if (!StringUtils.equals(user, cnPO.getCreator())) {
            throw new RuntimeException("该用户没有权限删除。");
        }

        if (crPOList != null && crPOList.size() > 0) {
            throw new RuntimeException("该信息资源分类下存在节点，请先删除信息资源细目后再处理。");
        } else {
            catalogNodeDAO.deleteByNodeId(id);
        }
        return 0;
    }

    @Override
    public List<CatalogNodeVO> getAllCatalogNode() {

        List<CatalogNodePO> catalogPOList = catalogNodeDAO.getAllCatalogNodes();
        List<CatalogNodeVO> catalogVOList = transferPOToVO(catalogPOList);
        if (catalogPOList != null) {
//            catalogVOList = bulidTreeNetwork(catalogVOList);
            catalogVOList = buildByRecursive(catalogVOList);
        }
        return catalogVOList;
    }

    //为方便判断需要查看节点是否继续有子节点，方便前段做处理
    @Override
    public List<CatalogNodeVO> getCatalogNodeSubtree(Long id) {

        List<CatalogNodePO> catalogPOList = catalogNodeDAO.getCatalogByParentId(id);
        List<CatalogNodeVO> catalogVOList = transferPOToVO(catalogPOList);
        List<Long> idList = catalogNodeDAO.getCatalogParentList();

        List<CatalogNodeVO> catalogNodeChildList = new ArrayList<CatalogNodeVO>();
        for (CatalogNodeVO catalog : catalogVOList) {
            CatalogNodeVO catalogNode = catalog;
            if (idList.contains(catalog.getId())) {
                catalogNode.setHasChildFlag(true);
            }
            catalogNodeChildList.add(catalog);
        }
//        CatalogNodePO parentNodePO =  catalogNodeDAO.getCatalogNodeById(id);
//        CatalogNodeVO parentNodeVO = transferPOToVO(parentNodePO);//
//        parentNodeVO.setChildren(catalogVOList);

        return catalogNodeChildList;
    }


    /**
     * 查找某个节点详细信息
     */
    @Override
    public CatalogNodeVO getCatalogNode(Long id) {

        CatalogNodePO catalogNodePO = catalogNodeDAO.getCatalogNodeById(id);
        CatalogNodeVO catalogNodeVO = null;
        if (catalogNodePO != null) {
            catalogNodeVO = transferPOToVO(catalogNodePO);
        }
        return catalogNodeVO;
    }

    /*
     *  处理批量导入的excel文件内容
     */
    @Override
    public String saveBatchImport(String user, CommonsMultipartFile multiPartFile)
            throws Exception {

        if (multiPartFile == null) {
            throw new Exception("上传信息没有包含文件");
        }

        String fileOriginName = multiPartFile.getOriginalFilename();
        if (!batchTools.verifyExcel(fileOriginName)) {
            throw new Exception("上传文件格式不符合要求");
        }
        //文件大小过滤
        SystemConfigPO systemConfigPO = systemConfigService.getSystemConfig(); //systemConfigDAO.getLastestSysConfig();
        if (systemConfigPO == null || systemConfigPO.getFileUploadSize() == 0) {
            throw new Exception("系统参数没有配置，请先配置再上传");
        }
        int fileLimitSizeMB = systemConfigPO.getFileUploadSize();
        if (!FileUtils.validFileSize(fileLimitSizeMB, multiPartFile)) {
            throw new Exception("文件超过系统配置大小，文件限制大小为 " + fileLimitSizeMB + " MB");
        }

        String fileName = FileUtils.createFile("excel", multiPartFile);
//        List<CatalogNodeVO> cnList = batchTools.readExcelCatalogValue(new File(filePath));
//        batchTools.saveExcelCatalogNode(user, cnList);
//        batchTools.preProcessExcelCatalog(cnList);
//        FileUtils.deletefile(filePath);
        return fileName;

    }

    @Override
    public void processExcel(String user, String fileName) throws Exception {
        if (StringUtils.isEmpty(fileName)) {
            throw new Exception("文件名为空");
        }
        String filePath = FileUtils.getFileDirByType("excel") + File.separator + fileName;
        List<CatalogNodeVO> cnList = batchTools.readExcelCatalogValue(new File(filePath));
        batchTools.saveExcelCatalogNode(user, cnList);
        FileUtils.deletefile(filePath);
    }


    @Transactional(rollbackFor = Exception.class)
    public void saveExcelCatalogNode(String user, List<CatalogNodeVO> cnVOList) throws Exception {

        //Code 和 Id 的映射关系
        Map<String, Long> codeIdMap = new HashMap<String, Long>();
        codeIdMap.put("0", 0L); //三大库父节点Id为0
        List<CatalogNodePO> cnPOList = new ArrayList<CatalogNodePO>();

        for (CatalogNodeVO cnVO : cnVOList) {
            CatalogNodePO cnPO = new CatalogNodePO();
            cnPO.setParentId(cnVO.getParentId());
            cnPO.setDept(cnVO.getDept());
            cnPO.setResourceName(cnVO.getResourceName());
            cnPO.setResourceEncode(cnVO.getResourceEncode());
            cnPO.setParentFullCode(cnVO.getParentFullCode());
//            String user = (String) UserHolder.getUser().getProperty("username");
            cnPO.setModifier(user);
            cnPO.setModifyTime(new Date());

            //去重
            String catalogCode = cnVO.getResourceEncode();
            List<CatalogNodePO> libNodeList = catalogNodeDAO
                    .getCatalogByParentId(cnVO.getParentId());
            CatalogNodePO sameNode = null;
            for (CatalogNodePO libNode : libNodeList) {
                if (StringUtils.equals(libNode.getResourceEncode(), catalogCode)) {
                    sameNode = libNode;
                }
            }
            if (sameNode != null) {   //更新
                cnPO.setId(sameNode.getId());
                cnPO.setCreator(sameNode.getCreator());
                cnPO.setCreateTime(sameNode.getCreateTime());
                LOG.info("updateById： {}", cnPO);
                catalogNodeDAO.updateById(cnPO);
            } else {  //插入
                cnPO.setCreator(user);
                cnPO.setCreateTime(new Date());
                LOG.info("insert： {}", cnPO);
                catalogNodeDAO.insert(cnPO);
            }
        }
    }

    /*判断同一节点下面是否有 相同名字或资源编码的节点*/
    private Boolean ownSameNameOrCode(CatalogNodeVO node) {

        Long nodeId = node.getId();
        if (nodeId != null && nodeId != 0) {
            return false;
        }
        Long parentId = node.getParentId();
        List<CatalogNodePO> catalogPOList = catalogNodeDAO.getCatalogByParentId(parentId);

        if (catalogPOList != null) {
            for (CatalogNodePO nodePo : catalogPOList) {
                if (StringUtils.equals(nodePo.getResourceName(), node.getResourceName()) ||
                        StringUtils.equals(nodePo.getResourceEncode(), node.getResourceEncode())) {
                    return true;
                }
            }
        }
        return false;
    }

    private Long updateCatalogNode(String user, CatalogNodeVO node) {

        CatalogNodePO catalogNodePO = new CatalogNodePO();
        Long nodeId = node.getId();
        catalogNodePO = catalogNodeDAO.getCatalogNodeById(nodeId);
        catalogNodePO.setResourceEncode(node.getResourceEncode());
        catalogNodePO.setResourceName(node.getResourceName());
        catalogNodePO.setModifier(user);
        catalogNodePO.setModifyTime(new Date());
        catalogNodePO.setParentFullCode(node.getParentFullCode());
        catalogNodeDAO.updateById(catalogNodePO);

        //修改了资源分类名称需要将 名称信息同步到 资源内容中的资源名称里面
        if (!StringUtils.equals(node.getResourceName(), catalogNodePO.getResourceName())) {
            List<CatalogResourcePO> cnPOList = catalogResourceDAO.getByCatalogId(nodeId);

            for (CatalogResourcePO crPO : cnPOList) {
                Long resourceId = crPO.getResourceId();
                List<CatalogResourcePO> rsPOList = catalogResourceDAO.getByResourceId(crPO.getResourceId());
                String catalogFullName = getResourceCatalogFullName(rsPOList);
                ResourceConfigPO rcPO = resourceConfigDAO.getConfigById(resourceId);
                rcPO.setCatalogFullName(catalogFullName);
                resourceConfigDAO.updateById(rcPO);
            }
        }
        return nodeId;
    }


    private String getResourceCatalogFullName(List<CatalogResourcePO> crList) {

        Collections.sort(crList);
        StringBuilder catalogFullName = new StringBuilder();
        for (CatalogResourcePO crPO : crList) {
            CatalogNodePO cnPO = catalogNodeDAO.getCatalogNodeById(crPO.getCatalogId());
            catalogFullName.append(cnPO.getResourceName() + "/");
        }
        //不需要最后面的"/" 所以减 1
        return catalogFullName.substring(0, catalogFullName.length() - 1);
    }


    /**
     * 两层循环实现建树
     *
     * @param treeNodes 传入的树节点列表
     */
    private List<CatalogNodeVO> bulidTreeNetwork(List<CatalogNodeVO> treeNodes) {

        List<CatalogNodeVO> trees = new ArrayList<CatalogNodeVO>();

        for (CatalogNodeVO treeNode : treeNodes) {

           /* if ("0".equals(treeNode.getParentId())) {
                trees.add(treeNode);
            }*/
            Long parentId = treeNode.getParentId();
            if (parentId != null && parentId == 0L) {
                trees.add(treeNode);
            }

            for (CatalogNodeVO it : treeNodes) {
                if (it.getParentId() == treeNode.getId()) {
                    if (treeNode.getChildren() == null) {
                        treeNode.setChildren(new ArrayList<CatalogNodeVO>());
                    }
                    treeNode.getChildren().add(it);
                }
            }
        }
        return trees;
    }

    /**
     * 使用递归方法建树
     */
    private List<CatalogNodeVO> buildByRecursive(List<CatalogNodeVO> treeNodes) {
        List<CatalogNodeVO> trees = new ArrayList<CatalogNodeVO>();
        for (CatalogNodeVO treeNode : treeNodes) {
            if (treeNode.getParentId() == 0) {
                trees.add(findChildren(treeNode, treeNodes));
            }
        }
        return trees;
    }

    /**
     * 递归查找子节点
     */
    private CatalogNodeVO findChildren(CatalogNodeVO treeNode, List<CatalogNodeVO> treeNodes) {
        for (CatalogNodeVO it : treeNodes) {
            if (treeNode.getId().equals(it.getParentId())) {
                if (treeNode.getChildren() == null) {
                    treeNode.setChildren(new ArrayList<CatalogNodeVO>());
                }
                treeNode.getChildren().add(findChildren(it, treeNodes));
            }
        }
        return treeNode;
    }


    private Long addCatalogNode(String user, CatalogNodeVO node) {

        CatalogNodePO catalogNodePO = new CatalogNodePO();
        Long parentId = node.getParentId();
        catalogNodePO.setParentId(parentId);
        int depth = 0;
        if (parentId != null && parentId == 0) {
            depth = 1;  //父节点为0时深度为1，其它节点在父节点的基础上加一
        } else {
            CatalogNodePO parentNode = new CatalogNodePO();
            parentNode = catalogNodeDAO.getCatalogNodeById(parentId);
            depth = parentNode.getDept() + 1;
        }
        catalogNodePO.setParentFullCode(node.getParentFullCode());
        catalogNodePO.setDept(depth);
        catalogNodePO.setResourceEncode(node.getResourceEncode());
        catalogNodePO.setResourceName(node.getResourceName());
        catalogNodePO.setCreator(user);
        catalogNodePO.setModifier(user);
        catalogNodePO.setCreateTime(new Date());
        catalogNodePO.setModifyTime(new Date());
        catalogNodeDAO.insert(catalogNodePO);
        return catalogNodePO.getId();
    }


    private CatalogNodeVO transferPOToVO(CatalogNodePO catalogNodePO) {

        CatalogNodeVO catalogNodeVO = new CatalogNodeVO();

        catalogNodeVO.setId(catalogNodePO.getId());
        catalogNodeVO.setParentId(catalogNodePO.getParentId());
        catalogNodeVO.setDept(catalogNodePO.getDept());
        catalogNodeVO.setResourceEncode(catalogNodePO.getResourceEncode());
        catalogNodeVO.setResourceName(catalogNodePO.getResourceName());
        catalogNodeVO.setParentFullCode(catalogNodePO.getParentFullCode());
        return catalogNodeVO;
    }

    private List<CatalogNodeVO> transferPOToVO(List<CatalogNodePO> catalogNodePOList) {
        List<CatalogNodeVO> catalogVOList = new ArrayList<CatalogNodeVO>();
        if (catalogNodePOList == null) {
            return null;
        }
        for (CatalogNodePO catalogNodePO : catalogNodePOList) {
            CatalogNodeVO catalogNodeVO = new CatalogNodeVO();
            catalogNodeVO.setId(catalogNodePO.getId());
            catalogNodeVO.setParentId(catalogNodePO.getParentId());
            catalogNodeVO.setDept(catalogNodePO.getDept());
            catalogNodeVO.setResourceEncode(catalogNodePO.getResourceEncode());
            catalogNodeVO.setResourceName(catalogNodePO.getResourceName());
            catalogNodeVO.setParentFullCode(catalogNodePO.getParentFullCode());
            catalogVOList.add(catalogNodeVO);
        }
        return catalogVOList;
    }
}
