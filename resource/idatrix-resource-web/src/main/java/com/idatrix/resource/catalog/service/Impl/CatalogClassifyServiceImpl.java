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
import com.idatrix.resource.common.utils.ExcelUtils;
import com.idatrix.resource.common.utils.FileUtils;
import org.apache.commons.collections.CollectionUtils;
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
    public Long saveCatalogNode(Long rentId, String user, CatalogNodeVO node) throws Exception {

        Long id = node.getId();
        //考虑查重，原则是 统一节点下方不能有名字或者编码相同数据项
        if (ownSameNameOrCode(rentId, node)) {
           ;// throw new Exception("和同一资源分类下面配置的其它节点名称或者编码冲突");
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
            id = addCatalogNode(rentId, user, node);
        } else {
            id = updateCatalogNode(user, node);
        }
        return id;
    }

    @Override
    public int deleteCatalogNode(Long rentId, String user, Long id) throws Exception {
        CatalogNodePO cnPO = catalogNodeDAO.getCatalogNodeById(id);
        if(StringUtils.equalsAnyIgnoreCase(cnPO.getResourceName(),"基础类") ||
                StringUtils.equalsAnyIgnoreCase(cnPO.getResourceName(),"主题类") ||
                StringUtils.equalsAnyIgnoreCase(cnPO.getResourceName(),"部门类")){
            throw new Exception("该节点不能删除");
        }
        deleteCatalogNode(rentId, cnPO);

//        List<CatalogResourcePO> crPOList = catalogResourceDAO.getByCatalogId(id);

//        CatalogNodePO cnPO = catalogNodeDAO.getCatalogNodeById(id);
//        if (!StringUtils.equals(user, cnPO.getCreator())) {
//            throw new Exception("该用户没有权限删除。");
//        }


//        if (crPOList != null && crPOList.size() > 0) {
//            throw new Exception("该信息资源分类下存在节点，请先删除信息资源目录后再处理。");
//        } else {
//
//            //删除节点需要测试该节点是否作为父节点
//            List<CatalogNodePO> childrenNodeList = catalogNodeDAO.getCatalogByParentId(rentId, id);
//            if(childrenNodeList==null || childrenNodeList.size()==0){
//                catalogNodeDAO.deleteByNodeId(id);
//            }else{
//                for(CatalogNodePO cnPO:childrenNodeList) {
//                    List<CatalogResourcePO> childrenCRList = catalogResourceDAO.getByCatalogId(cnPO.getId());
//                    if (childrenCRList != null && childrenCRList.size() > 0) {
//                        throw new Exception("该信息资源分类下存在子节点，并且已经被信息资源使用，请先删除信息资源目录后再处理,"
//                        +"节点名称为 "+cnPO.getResourceName()+ " 节点编码为 "+cnPO.getResourceEncode());
//                    }else{
//                        catalogNodeDAO.deleteByNodeId(cnPO.getId());
//                    }
//                }
//            }
//        }
        return 0;
    }

    @Transactional(rollbackFor = Exception.class)
    private void deleteCatalogNode(Long rentId, CatalogNodePO cnPO)throws Exception{
        List<CatalogResourcePO> crPOList = catalogResourceDAO.getByCatalogId(cnPO.getId());
        if (CollectionUtils.isNotEmpty(crPOList)) {
            throw new Exception("该信息资源分类下存在节点，请先删除信息资源目录后再处理。节点名称为 "
                    +cnPO.getResourceName()+ " 节点编码为 "+cnPO.getResourceEncode());
        } else {

            //删除节点需要测试该节点是否作为父节点
            List<CatalogNodePO> childrenNodeList = catalogNodeDAO.getCatalogByParentId(rentId, cnPO.getId());
            if(CollectionUtils.isNotEmpty(childrenNodeList)){
                for(CatalogNodePO tmpCNPO:childrenNodeList) {
                    deleteCatalogNode(rentId, tmpCNPO);
                }
            }
            catalogNodeDAO.deleteByNodeId(cnPO.getId());
        }
    }

    private void InitCatalogNode(Long rentId, String user){
        List<CatalogNodePO> initCatalogNode = new ArrayList<>();
        initCatalogNode.add(new CatalogNodePO("基础类","1", rentId, user));
        initCatalogNode.add(new CatalogNodePO("主题类","2", rentId, user));
        initCatalogNode.add(new CatalogNodePO("部门类","3", rentId, user));
        catalogNodeDAO.insertList(initCatalogNode);
    }

    @Override
    public List<CatalogNodeVO> getAllCatalogNode(Long rentId, String user) {

        List<CatalogNodePO> catalogPOList = catalogNodeDAO.getAllCatalogNodesByRentId(rentId);

        //如果数据库未初始化，在代码中根据租户信息，自动初始化
        if(CollectionUtils.isEmpty(catalogPOList)) {
            InitCatalogNode(rentId, user);
            catalogPOList = catalogNodeDAO.getAllCatalogNodesByRentId(rentId);
        }

        List<CatalogNodeVO> catalogVOList = transferPOToVO(catalogPOList);
        if (CollectionUtils.isNotEmpty(catalogPOList)) {
//            catalogVOList = bulidTreeNetwork(catalogVOList);
            catalogVOList = buildByRecursive(catalogVOList);
        }
        return catalogVOList;
    }

    /**
     * 构建特定节点为子节点的分类树
     * @param rentId
     * @param id
     * @return
     */
    @Override
    public List<CatalogNodeVO> getCatalogNodeSubtree(Long rentId, String user, Long id) {

//        List<CatalogNodePO> catalogPOList = getCatalogSubNode(rentId, id);
//        catalogPOList.add(catalogNodeDAO.getCatalogNodeById(id));
//        List<CatalogNodeVO> catalogVOList = transferPOToVO(catalogPOList);
//        if (catalogPOList != null) {
////            catalogVOList = bulidTreeNetwork(catalogVOList);
//            catalogVOList = buildByRecursive(id, catalogVOList);
//        }
//        return catalogVOList;


        CatalogNodePO catalogNode = catalogNodeDAO.getCatalogNodeById(id);
        List<CatalogNodePO> catalogPOList = catalogNodeDAO.getAllCatalogNodesByRentId(rentId);

        List<CatalogNodeVO> catalogVOList = null;
        List<CatalogNodePO> catalogFinalList = new ArrayList<>();
        if(catalogNode!=null) {
            catalogFinalList.add(catalogNode);
            for (CatalogNodePO cnPO : catalogPOList) {
                if (cnPO.getDept() > catalogNode.getDept()) {
                    catalogFinalList.add(cnPO);
                }
            }
            catalogVOList = transferPOToVO(catalogFinalList);
            if (catalogVOList != null) {
                catalogVOList = buildByRecursive(catalogNode.getParentId(), catalogVOList);
            }
        }else{
            catalogFinalList.addAll(catalogPOList);
            List<CatalogNodeVO> catalogAllVO = transferPOToVO(catalogFinalList);
            if (catalogAllVO != null) {
                catalogVOList = buildByRecursive(catalogAllVO);
            }
        }

        return catalogVOList;
    }

    /**
     * 构建特定节点为子节点的分类树
     * @param rentId
     * @param id
     * @return
     */
    @Override
    public List<CatalogNodeVO> getCatalogNodeSubtree(Long rentId, String user, Long id, Long depth) {

        List<CatalogNodeVO> catalogVOList = null;
        List<CatalogNodePO> catalogPOList = new ArrayList<>();
        if(id==null || id.equals(0L)){
            catalogPOList = catalogNodeDAO.getByParentFullCodeByRentId(rentId, "0");
            if(CollectionUtils.isEmpty(catalogPOList)){
                InitCatalogNode(rentId, user);
                catalogPOList = catalogNodeDAO.getByParentFullCodeByRentId(rentId, "0");
            }
            if(CollectionUtils.isNotEmpty(catalogPOList)){
                catalogVOList = transferPOToVO(catalogPOList);
                for(CatalogNodePO cPO:catalogPOList){
                    List<CatalogNodePO> cnList =  catalogNodeDAO.getByParentFullCodeByRentId(rentId, cPO.getResourceEncode());
                    if(CollectionUtils.isNotEmpty(cnList)){
                        catalogVOList.addAll(transferPOToVO(cnList));
                    }
                }
            }
            if (catalogVOList != null) {
                catalogVOList = getCatalogTree(0L, catalogVOList);
            }
        }else{
            CatalogNodePO cnPO = catalogNodeDAO.getCatalogNodeById(id);
            String fullCode = null;
            if(cnPO.getParentFullCode().equals("0")){
                fullCode = cnPO.getResourceEncode();
            }else{
                fullCode = cnPO.getParentFullCode()+cnPO.getResourceEncode();
            }
            catalogPOList.add(cnPO);
            List<CatalogNodePO> cPOList = catalogNodeDAO.getObscureByParentFullCodeAndRentId(rentId, fullCode, cnPO.getDept()+depth);
            if(CollectionUtils.isNotEmpty(cPOList)){
                catalogPOList.addAll(cPOList);
            }
            catalogVOList = transferPOToVO(catalogPOList);
            if (catalogVOList != null) {
                catalogVOList = getCatalogTree(cnPO.getId(), catalogVOList); //buildByRecursive(cnPO.getId(), catalogVOList);
            }
        }
        return catalogVOList;
    }

    private List<CatalogNodeVO> getCatalogTree(Long parentId, List<CatalogNodeVO> originList){
        List<CatalogNodeVO> goalList = new ArrayList<>();
        if(CollectionUtils.isEmpty(originList)){
            return null;
        }

        boolean ownParentFlag = false;
        for(CatalogNodeVO nodeVO :originList){
            if(nodeVO.getParentId().equals(parentId)){
                goalList.add(nodeVO);
                ownParentFlag = true;
            }
        }
        if(!ownParentFlag){
            return null;
        }

        for(CatalogNodeVO vo:goalList){
           for(CatalogNodeVO son: originList){
                if(son.getParentId().equals(vo.getId())){
                   vo.setHasChildFlag(true);
                   break;
                }
           }
        }
        return goalList;
    }

    private List<CatalogNodePO> getCatalogSubNode(Long rentId, Long id){
        List<CatalogNodePO> nodeList = new ArrayList<>();
        List<CatalogNodePO> catalogPOList = catalogNodeDAO.getCatalogByParentId(rentId, id);
        if(CollectionUtils.isEmpty(catalogPOList)){
            return null;
        }
        for(CatalogNodePO nodePO:catalogPOList){
            List<CatalogNodePO> poList=getCatalogSubNode(rentId, nodePO.getId());
            if(poList!=null&&poList.size()>0) {
               nodeList.addAll(poList);
            }
        }
        return nodeList;
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
        if (!ExcelUtils.verifyExcel(fileOriginName)) {
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
    public void processExcel(Long rentId, String user, String fileName) throws Exception {
        if (StringUtils.isEmpty(fileName)) {
            throw new Exception("文件名为空");
        }

        String filePath = FileUtils.getFileDirByType("excel") + File.separator + fileName;
        List<CatalogNodeVO> cnList = batchTools.readExcelCatalogValue(new File(filePath));
        LOG.info("Excel批量导入资源分类数量 {}", cnList.size());
        batchTools.saveExcelCatalogNode(rentId, user, cnList);
        FileUtils.deletefile(filePath);
    }


    @Transactional(rollbackFor = Exception.class)
    public void saveExcelCatalogNode(Long rentId, String user, List<CatalogNodeVO> cnVOList) throws Exception {

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
                    .getCatalogByParentId(rentId, cnVO.getParentId());
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

    /**
     * 根据租户和分类名称获取分类列表
     *
     * @param rentId
     * @param catalogName
     * @return
     */
    @Override
    public List<CatalogNodePO> getCatalogNodeByCatalogName(Long rentId, String catalogName, Long depth) {
        return catalogNodeDAO.getCatalogNodeByCatalogName(rentId, catalogName, depth);
    }

    /*判断同一节点下面是否有 相同名字或资源编码的节点*/
    private Boolean ownSameNameOrCode(Long rentId, CatalogNodeVO node) throws Exception {

        Long nodeId = node.getId();
//        if (nodeId != null && nodeId != 0) {
//            return false;
//        }
        Long parentId = node.getParentId();
        List<CatalogNodePO> catalogPOList = catalogNodeDAO.getCatalogByParentId(rentId, parentId);

        if (catalogPOList != null) {
            for (CatalogNodePO nodePo : catalogPOList) {
                if(nodePo.getId().equals(nodeId)){
                    continue;
                }else if(StringUtils.equals(nodePo.getResourceName(), node.getResourceName()) ||
                        StringUtils.equals(nodePo.getResourceEncode(), node.getResourceEncode())) {
                    throw new Exception("和同一资源分类下其它节点名称或者编码冲突，节点名称 "+
                            nodePo.getResourceName()+"，编码 "+ nodePo.getResourceEncode());
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
                if (it.getParentId().equals(treeNode.getId())) {
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
     * 构建特定节点为根节点字数
     */
    private List<CatalogNodeVO> buildByRecursive(Long parentId, List<CatalogNodeVO> treeNodes) {
        List<CatalogNodeVO> trees = new ArrayList<CatalogNodeVO>();
        for (CatalogNodeVO treeNode : treeNodes) {
            if (treeNode.getParentId().equals(parentId)) {
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
                if(treeNode.getChildren()!=null){
                    treeNode.setHasChildFlag(true);
                }
            }
        }
        return treeNode;
    }


    private Long addCatalogNode(Long rentId, String user, CatalogNodeVO node) {

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
        catalogNodePO.setRentId(rentId);
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
        if (CollectionUtils.isEmpty(catalogNodePOList)) {
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
