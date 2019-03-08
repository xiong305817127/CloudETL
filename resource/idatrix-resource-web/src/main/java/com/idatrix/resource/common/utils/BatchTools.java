package com.idatrix.resource.common.utils;


import com.idatrix.resource.catalog.dao.CatalogNodeDAO;
import com.idatrix.resource.catalog.dao.CatalogResourceDAO;
import com.idatrix.resource.catalog.dao.ResourceConfigDAO;
import com.idatrix.resource.catalog.po.CatalogNodePO;
import com.idatrix.resource.catalog.po.ResourceConfigPO;
import com.idatrix.resource.catalog.vo.CatalogNodeVO;
import com.idatrix.resource.catalog.vo.ResourceColumnVO;
import com.idatrix.resource.catalog.vo.ResourceConfigVO;
import com.idatrix.unisecurity.api.domain.User;
import com.idatrix.unisecurity.api.service.UserService;
import com.idatrix.unisecurity.sso.client.UserHolder;
import com.idatrix.unisecurity.sso.client.model.SSOUser;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

import static com.idatrix.resource.common.utils.ExcelEntityEnum.NOT_SURE;
import static com.idatrix.resource.common.utils.ExcelEntityEnum.RESOURCE_COLUMN_COL_TYPE;
import static com.idatrix.resource.common.utils.ResourceTools.ResourceStatus.DRAFT;

@Transactional
@Component
public class BatchTools {

    private final Logger LOG= LoggerFactory.getLogger(this.getClass());

    private final static String[] cellType={"NUMERIC","STRING","FORMULA","BLANK","BOOLEAN","ERROR"};

    private Boolean specialProcessFlag = false;

    private Map<String, Long> nodeCodeDepthMap = new HashMap<String, Long>();

    private Map<String, String> codeOverallMap = new HashMap<String, String>();
    private Map<String, String> nameOverallMap = new HashMap<String, String>();

    @Autowired
    private CatalogNodeDAO catalogNodeDAO;

    @Autowired
    private CatalogResourceDAO catalogResourceDAO;

    @Autowired
    private ResourceConfigDAO resourceConfigDAO;

    @Autowired
    private UserService userService;

    @Autowired
    private UserUtils userUtils;


    private boolean matchCatalogFormat(String catalog){
        Boolean flag = false;
        if(catalog.indexOf(".")>0){
            flag = true;
        }
        return flag;
    }


    /**
     * 读取Excel里面客户的信息
     * @param mfile 文件信息
     * @return
     */
    public List<CatalogNodeVO> readExcelCatalogValue(File mfile) throws Exception{


        Workbook wb = getExcelInfoFile(mfile);
        // 得到第一个shell
        Sheet sheet = wb.getSheetAt(0);
        // 得到Excel的行数
        int totalRows = sheet.getPhysicalNumberOfRows();
        // 得到Excel的列数(前提是有行数)
        int totalCells = 0;
        if (totalRows > 1 && sheet.getRow(0) != null) {
            totalCells = sheet.getRow(0).getPhysicalNumberOfCells();
        }

        boolean contentFlag = false;
        List<CatalogNodeVO> cnVOList = new ArrayList<CatalogNodeVO>();
        // 循环Excel行数
        for (int r = 0; r < totalRows; r++) {
            Row row = sheet.getRow(r);
            if (row == null){
                continue;
            }
            // 循环Excel的列
            for (int c = 0; c < totalCells; c++) {
                Cell cell = row.getCell(c);
                if (null != cell) {
                    int code = cell.getCellTypeEnum().getCode();
                    String codeStr = null;
                    String value = null;
                    if(code==0){
                        codeStr = "NUMERIC";
                        value =String.valueOf(cell.getNumericCellValue());
                    }else if(code==1){
                        codeStr = "STRING";
                        value =String.valueOf(cell.getStringCellValue());
                        if(matchCatalogFormat(value)) {
                            CatalogNodeVO cnVO = processCatalogExcel(value, 0L);
                            if (cnVO != null) {
                                contentFlag = true;
                                cnVOList.add(cnVO);
                            }
                        }
                    }else if(code==2){
                        codeStr = "FORMULA";
                        value =String.valueOf(cell.getCellFormula());
                    }else if(code==3){
                        codeStr = "BLANK";
                        value ="空";
                        continue;
                    }else if(code==4){
                        codeStr = "BOOLEAN";
                        value = String.valueOf(cell.getBooleanCellValue());
                    }else if(code==5){
                        codeStr = "ERROR";
                        value = String.valueOf(cell.getErrorCellValue());
                    }
                    LOG.info("Line {}-Cell {}-type {}-value {}", r, c, codeStr, value);
                }
            }
        }

        if(!contentFlag){
            throw new Exception("批量导入文件不存在有效数据，请参考导入模板重新填写");
        }
        return cnVOList;
    }


    /*存储资源分类信息*/
    @Transactional(rollbackFor = Exception.class)
    public void saveExcelCatalogNode(String user, List<CatalogNodeVO> cnVOList)throws Exception{

        //Code 和 Id 的映射关系
        Map<String, Long> codeIdMap = new HashMap<String, Long>();
        codeIdMap.put("0", 0L); //三大库父节点Id为0

        for(CatalogNodeVO cnVO :cnVOList){
            CatalogNodePO cnPO = new CatalogNodePO();
            String parentFullCode = cnVO.getParentFullCode(); //cnVO.getParentCode();
            Long parentId = codeIdMap.get(parentFullCode);
            if(parentId==null){
                List<CatalogNodePO> nodeList = catalogNodeDAO.getByParentFullCode(parentFullCode);
                if(nodeList!=null && nodeList.size()>0){
                    parentId = nodeList.get(0).getParentId();
                    codeIdMap.put(parentFullCode, parentId);
                }else{
/*                  LOG.error("parentFullCode:{}, 不存在该节点", parentFullCode);*/
                    /*第一个子节点不存在时候，需要自己从表面搜索出来，根据parentFullCode处理*/
                    String grandpaCode = null;
                    if(parentFullCode.length()==1){
                        grandpaCode = "0";
                    }else{
                        grandpaCode = getParentCatalog(parentFullCode);
                    }
                    List<CatalogNodePO> parentPOList = catalogNodeDAO.getByParentFullCode(grandpaCode);
                    if (parentPOList != null && parentPOList.size() > 0) {
                        for (CatalogNodePO parentPO : parentPOList) {
                            if (StringUtils.equals(parentPO.getResourceEncode(), cnVO.getParentCode())) {
                                parentId = parentPO.getId();
                                break;
                            }
                        }
                    } else {
                        throw new Exception("不存在该目录分类父节点");
                    }
                }
            }
            if(parentId==null){
                throw new Exception("不存在该目录分类父节点，父节点分类编码是 "+parentFullCode);
            }
            cnPO.setParentId(parentId);
            cnPO.setDept(cnVO.getDept());
            cnPO.setResourceName(cnVO.getResourceName());
            cnPO.setResourceEncode(cnVO.getResourceEncode());
            cnPO.setParentFullCode(cnVO.getParentFullCode());
            cnPO.setModifier(user);
            cnPO.setModifyTime(new Date());

            //去重
            String catalogCode = cnVO.getResourceEncode();
            List<CatalogNodePO> libNodeList = catalogNodeDAO.getCatalogByParentId(parentId);
            CatalogNodePO sameNode = null;
            for(CatalogNodePO libNode: libNodeList){
                if(StringUtils.equals(libNode.getResourceEncode(), catalogCode)){
                    sameNode = libNode;
                }
            }
            if(sameNode!=null){   //更新
                cnPO.setId(sameNode.getId());
                cnPO.setCreator(sameNode.getCreator());
                cnPO.setCreateTime(sameNode.getCreateTime());
                LOG.info("updateById： {}",cnPO);
                catalogNodeDAO.updateById(cnPO);
            }else{  //插入
                cnPO.setCreator(user);
                cnPO.setCreateTime(new Date());
                LOG.info("insert： {}",cnPO);
                catalogNodeDAO.insert(cnPO);
            }
        }
    }



    /**
     * 读取Excel里面客户的信息
     * @param mfile 需要读取操作的文件
     * @return
     */
    public Map<String, List<Object>> readResourceExcelValue(File mfile) throws Exception{

        Workbook wb = getExcelInfoFile(mfile);
        Sheet sheet = wb.getSheetAt(0);
        int totalRows = sheet.getPhysicalNumberOfRows(); // 得到Excel的行数
        // 得到Excel的列数(前提是有行数)
        int tableColType = 0;
        int totalCells = 0;
        if (totalRows > 1 && sheet.getRow(0) != null) {
            totalCells = sheet.getRow(0).getPhysicalNumberOfCells();
        }
        //用来存储列名称
        Boolean cellTitleFlag = false;
        List<Object> resourceExcelList = new ArrayList<Object>();
        String[] resourceDescribe = new String[totalCells];
        for (int r = 0; r < totalRows; r++) { // 循环Excel行数
            Row row = sheet.getRow(r);
            if (row == null){
                continue;
            }
            ResourceConfigVO user = new ResourceConfigVO();
            List<String> tmpCellList = new ArrayList<String>();
            // 循环Excel的列
            for (int c = 0; c < totalCells; c++) {
                Cell cell = row.getCell(c);
                if(cell==null){
                    //excel 中表格为空的时候，cell 为null
                    tmpCellList.add(c, null);
                    LOG.info("Line {}-Cell {}: 为空", r, c);
                    continue;
                }
                String value = getCellValue(cell);
                tmpCellList.add(c, value);
//                LOG.info("Line {}-Cell {}-type {}-value {}", r, c, cellType[cell.getCellTypeEnum().getCode()], value);
            }
            //判断是title还是实际内容
            if(!cellTitleFlag){
                cellTitleFlag = excelCellProcessFlag(tmpCellList);
            }
            if(cellTitleFlag){ //必须另起一起if，避免把刚刚判断是数据的内容行，没有存入对象中
                resourceExcelList.add(tmpCellList);
            }else{
                for(int index=0; index<tmpCellList.size(); index++){
                    String tmpCell = tmpCellList.get(index);
                    if(StringUtils.isNotEmpty(tmpCell)){
                        resourceDescribe[index] = tmpCell;
                    }
                }
            }
        }
        List<Object> resourceDescribelist = new ArrayList<Object>(Arrays.asList(resourceDescribe));
        Map<String, List<Object>> resourceExcelResult = new HashMap<String, List<Object>>();
        resourceExcelResult.put("describe", resourceDescribelist);
        resourceExcelResult.put("value", resourceExcelList);
//        LOG.info("===describe info:{}", resourceDescribelist);
        return resourceExcelResult;
    }

    /*
   *   处理excel导出来的数据 处理成ResourceConfigVO做好入库准
   *
   *   @param: 为Map类型，key 取值为 "describe"和"value"
   *       key:  "decribe"  表示数值为表格描述信息，数据类型为 List<String>
   *             "value"    表示数值为表格实际信息，数据类型为 List<List<String>>类型
   *   @return：返回为可以入库的 ResourceConfigVO 数据
   */
    public List<ResourceConfigVO> processResourceExcel(Map<String, List<Object>> excelDate) throws Exception{

        List<Object> describeList = excelDate.get("describe");
        List<String> describeInfoList = new ArrayList<String>();
        ExcelEntityEnum[] enums = new ExcelEntityEnum[describeList.size()];

        //确定数据项和 PO属性关系
        for(int index=0; index<describeList.size(); index++){
            String describe = (String)describeList.get(index);
            String[] front = describe.split("\n?（");
            ExcelEntityEnum excelEnum = ExcelEntityEnum.getByName(front[0]);
            if(excelEnum!=NOT_SURE){
                enums[index] = excelEnum;
            }
        }

        boolean nullFlag = false;
        for(int index=0; index<enums.length; index++){
            if(enums[index]!=null){
                nullFlag=true;
                break;
            }
        }
        if(!nullFlag){
            throw new Exception("导入表格中数据项不符合要求，请参考模板填入相应数据");
        }

        List<Object> valueLists = excelDate.get("value");
        List<ResourceConfigVO> rcList = new ArrayList<ResourceConfigVO>();
        //然后利用反射的方式 调用对应参数set接口
        int size = valueLists.size();
        for(int valueIndex=0; valueIndex<size; valueIndex++){

            List<ResourceColumnVO> rcColumnList = new ArrayList<ResourceColumnVO>();
            List<String> cellRow = (ArrayList<String>)(valueLists.get(valueIndex));

            //每一行资源不是对应一个数据项
            ResourceConfigVO rcVO = new ResourceConfigVO();
            ResourceColumnVO rc = new ResourceColumnVO();
            rcVO.setResourceColumnVOList(rcColumnList);
            for(int cellIndex=0; cellIndex<cellRow.size(); cellIndex++){
                if(enums[cellIndex]!=null) {
                    ExcelEntityEnum excelEnum = enums[cellIndex];
                    //表格中内容直接为入库内容
                    String valueStr = cellRow.get(cellIndex);
                    if (StringUtils.isEmpty(valueStr) || StringUtils.equals(valueStr, "无") ||
                            excelEnum.getType() == -1) {
                        continue;
                    }
                    if (excelEnum.getEntityContextZH() != null) {
                        String[] contextInfo = excelEnum.getEntityContextZH();
                        int value = 0;
                        Boolean flag = false;
                        for (int contextIndex = 0; contextIndex < contextInfo.length; contextIndex++) {
                            if (StringUtils.contains(contextInfo[contextIndex], cellRow.get(cellIndex))) {
                                value = contextIndex;
                                flag = true;
                                break;
                            }
                        }
                        if (!flag) {
                            throw new Exception("模板表格中: 数据行-" + String.valueOf(valueIndex) +
                                    ",列-"+ String.valueOf(cellIndex) + " ,内容在代码中不存才对应信息:"+ valueStr +
                                    " ,现支持内容-"+ StringUtils.join(excelEnum.getEntityContextZH(),","));
                        }
                        valueStr = String.valueOf(value);
                    }
                    String cellValue = extendProcess(excelEnum, valueStr);
                    if (excelEnum.getType() == 0) {
                        setObjectByField(rcVO, excelEnum.getEntityName(), cellValue);
                    } else {
                        setObjectByField(rc, excelEnum.getEntityName(), cellValue);
                    }
                }
            }
            rcColumnList.add(rc);
            rcList.add(rcVO);
        }
        if(rcList.size()==0){
            throw new Exception("导入表格中数据内容为空，请参考模板填入相应数据");
        }
        return rcList;
    }

    public List<ResourceConfigVO> preProcesBeforeSave(List<ResourceConfigVO> rcList) throws Exception{

        LOG.info("^^^^^^^^^^^1^^^^^^^^^^^CurrentTime"+ DateTools.formatDate(new Date()));

        //数据去重，将资源信息里面每一行可能表示一个信息项,将多行信息项合并成为一行
        List<ResourceConfigVO> rcNewList = new ArrayList<ResourceConfigVO>();
        List<String> sourceCodeList = new ArrayList<String>();
        for(ResourceConfigVO rc: rcList){

            String code = rc.getCode();
            if(code==null){
                continue;
            }
            int place = sourceCodeList.indexOf(code);
            if(place<0){
                sourceCodeList.add(code);
                rcNewList.add(rc);
            }else{
                //sourceCodeList.add(null);
                ResourceConfigVO rcOld = rcNewList.get(place);
                rcOld.getResourceColumnVOList().add(rc.getResourceColumnVOList().get(0));
            }
        }
        LOG.info("^^^^^^^^^^^2^^^^^^^^^^^CurrentTime"+ DateTools.formatDate(new Date()));
        //将资源信息数据库里面读取到 缓存 nodeCodeDepthMap
        if(nodeCodeDepthMap.size()==0){
            List<CatalogNodePO> cnPOList = new ArrayList<CatalogNodePO>();
            cnPOList = catalogNodeDAO.getAllCatalogNodes();
            if(cnPOList==null || cnPOList.size()==0){
                throw new Exception("系统中信息资源分类为空，请先导入或者配置信息资源再操作");
            }
            for(CatalogNodePO cnPo: cnPOList){
                String parentFullCode = cnPo.getParentFullCode();
                int depth = cnPo.getDept();
                if(!StringUtils.equals("0",parentFullCode)
                        && depth>0 && !nodeCodeDepthMap.containsKey(parentFullCode)){
                    setCatalogDepth(parentFullCode, new Long(depth-1));
                }
            }
            if(nodeCodeDepthMap.size()==0){
                throw new Exception("系统中信息资源分类为空，请先导入或者配置信息资源再操作");
            }
        }

        LOG.info("^^^^^^^^^^^3^^^^^^^^^^^CurrentTime"+ DateTools.formatDate(new Date()));
        Map<String, String> codeMap = new HashMap<String, String>();
        Map<String, String> nameMap = new HashMap<String, String>();
        Map<String, List<Integer>> unifiedCreditCodeMap = new HashMap<String, List<Integer>>();

        //处理资源的部门列表ID问题
        Long rentId = userUtils.getCurrentUserRentId();


        //处理 rc_rcatalog_resource 对应信息，利用信息资源Code生成 信息资源数据库里面 id
        for(ResourceConfigVO rcVO :rcNewList){

            //根据资源分类ID将 分类信息 和code信息填完： 如30300101/06001
            String codeFull = rcVO.getCode();
            if(codeFull.indexOf("/")<0){
                throw new Exception("表格中信息资源代码格式不符合规范，不包含分隔符");
            }
            String[] code = codeFull.split("/");
            if(code.length!=2){
                throw new Exception("表格中信息资源代码格式不符合规范，分隔符位置不正确");
            }
            rcVO.setCatalogCode(code[0]);
            rcVO.setSeqNum(code[1]);
            rcVO.setStatus(DRAFT.getStatusCode());
            String catalogCode = rcVO.getCatalogCode();

            String name = null;
            if(nameMap.get(catalogCode)!=null){
                name = nameMap.get(catalogCode);
            }else{
                name = getCatalogFullNameByCode(catalogCode);
                nameMap.put(catalogCode, name);
            }
            rcVO.setCatalogName(name);
            List<ResourceConfigPO> rcLibList = resourceConfigDAO.getByNameOrCode(
                    rcVO.getName(), rcVO.getCatalogCode(), rcVO.getSeqNum());
            if(rcLibList!=null&&rcLibList.size()>0){
                throw new Exception("表格中信息资源目录和系统中已存在数据冲突，信息资源代码为-"+
                rcVO.getCode());
            }

            String fullCode = null;
            if(codeMap.get(catalogCode)!=null){
                fullCode = codeMap.get(catalogCode);
            }else{
                fullCode = getCatalogNodeIDsByCode(catalogCode);
                codeMap.put(catalogCode, fullCode);
            }
//            String fullCode = getCatalogNodeIDsByCode(rcVO.getCatalogCode());
            if(fullCode!=null){
                String[] codes = fullCode.split("/");
//                if(codes.length!=4){
//                    throw new Exception("表格中信息资源代码格式不符合规范，请重新核对");
//                }
                Long[] catalogArray = (Long[]) ConvertUtils.convert(codes,Long.class);
                rcVO.setCatalogIdArray(catalogArray);
            }


            String deptCode = rcVO.getDeptCode();
            List<Integer> parentIds = new ArrayList<Integer>();
            if(unifiedCreditCodeMap.get(deptCode)!=null){
                parentIds = unifiedCreditCodeMap.get(deptCode);
            }else{
                parentIds= userService.findParentIdsByUnifiedCreditCode(deptCode, rentId);
                unifiedCreditCodeMap.put(deptCode, parentIds);
            }
            //List<Integer> parentIds= userService.findParentIdsByUnifiedCreditCode(, rentId);
            if(parentIds!=null && parentIds.size()>0){
                StringBuilder idArray = new StringBuilder();
                for(Integer id: parentIds){
                    idArray.append(id.toString()).append(",");
                }
                String deptNameIds = idArray.substring(0, idArray.length()-1).toString();
                rcVO.setDeptNameIdArray(deptNameIds);
            }
        }
//        LOG.info("rcList: {}",rcNewList);
        LOG.info("^^^^^^^^^^^4^^^^^^^^^^^CurrentTime"+ DateTools.formatDate(new Date()));
        return rcNewList;
    }

    /**
     * 读EXCEL文件，获取信息集合
     * @param mfile
     * @return
     */
    private Workbook getExcelInfoFile(File mfile) throws Exception{

        String fileName = mfile.getAbsolutePath();
        if (!validateExcel(fileName)) {// 验证文件名是否合格
            throw new Exception("文件格式不符合要求");
        }
        List<ResourceConfigVO> rcVOList = null;
        Workbook wb = null;
        try {
            if (isExcel2003(fileName)) {// 当excel是2003时,创建excel2003
                wb = new HSSFWorkbook(new FileInputStream(mfile));
            } else {// 当excel是2007时,创建excel2007
                wb = new XSSFWorkbook(new FileInputStream(mfile));
            }
        } catch (Exception e) {
            throw e;
//            e.printStackTrace();
        }
        return wb;
    }

    /*
    * 判断是否是 标题内容还是 资源信息实际内容
    *
    * @return:  flag 为true表示是标题 ，false表示是资源信息内容
    */
    private Boolean excelCellProcessFlag(List<String> cellList){

        //TODO: 后期优化直接使用 ExcelEntityEnum 中 entityContextZH 字段数据
        List<String> contextList =  Arrays.asList(
                "实时","每日","每周","每月","每季度","每半年","每年",
                "无条件共享","有条件共享", "不予共享",
                "电子文件","电子表格","数据库","图形图像","流媒体","自描述格式","服务接口",
                "字符串型C","数值型N","货币型Y","日期型D","日期时间型T","逻辑型L","备注型M",
                "通用型G","双精度型B","整型I","浮点型F");
        Boolean flag = false;
        for(String cellValue : cellList){
            if(StringUtils.isNotEmpty(cellValue) && contextList.contains(cellValue)){
               flag = true;
               break;
            }
        }
        return flag;
    }


    /*从excel表格中每个框中获取数据*/
    private String getCellValue(Cell cell){

        String codeStr = null;
        String value = null;
        Map<String, String> typeValueMap = new HashMap<String, String>();
        if (null != cell) {

            if (cell.getCellTypeEnum() == CellType.NUMERIC) {
                value = String.valueOf(cell.getNumericCellValue());
            } else if (cell.getCellTypeEnum() == CellType.STRING) {
                value = String.valueOf(cell.getStringCellValue());
            } else if (cell.getCellTypeEnum() == CellType.FORMULA) {
                value = String.valueOf(cell.getCellFormula());
            } else if (cell.getCellTypeEnum() == CellType.BLANK) {
                value = null;
            } else if (cell.getCellTypeEnum() == CellType.BOOLEAN) {
               value = String.valueOf(cell.getBooleanCellValue());
            } else if (cell.getCellTypeEnum() == CellType.ERROR) {
               value = String.valueOf(cell.getErrorCellValue());
            }
        }
        return value;
    }

    private Boolean verifyCatalogNodeStr(String excelValue) throws Exception{

        Boolean flag = false;
        String[] valueArray = excelValue.split("\\.");
        if(valueArray==null || valueArray.length<2 ||
                StringUtils.isEmpty(valueArray[0]) ||
                StringUtils.isEmpty(valueArray[1])){
//            throw new Exception("存在不符合规范的目录分类");
            flag = true;
        }
        //由于部门类编码不规范，不对长度做校验
//        String catalogFullCode = valueArray[0];
//        int len = catalogFullCode.length();
//        if(len!=1 && len!=3 && len<6){
//            throw new Exception("目录分类的编码不符合规范");
//            flag = true;
//        }
        return flag;
    }

    /*
    *  根据资源分类编码获取 资源名称
    *
    *   @param: code 资源分类编码
    *   @return: 资源分类名称全称
    */
    private String  getCatalogFullNameByCode(String code) throws Exception {

        String fullName = null;
        if (StringUtils.isEmpty(code)) {
            throw new Exception("信息资源分类编码为空");
//            return fullName;
        }

        if(nameOverallMap.get(code)!=null){
            return nameOverallMap.get(code);
        }
        String parentCode = null;
        String ownCode= null;
        if(code.length()==1){
            parentCode = "0";
            ownCode = code;
            CatalogNodePO catalogPO = catalogNodeDAO.getByCondition(parentCode, ownCode, null);
            if (catalogPO== null) {
                throw new Exception("还未配置该资源分类-"+parentCode);
            }
            fullName = catalogPO.getResourceName();
            nameOverallMap.put(code, fullName);
            return fullName;
        }else{
            parentCode = getParentCatalog(code);
            if(parentCode==null){
                throw new Exception("表格中资源目录编码校验解析异常，异常编码为 "+code);
            }
            ownCode = code.substring(parentCode.length(), code.length());
            CatalogNodePO catalogPO = catalogNodeDAO.getByCondition(parentCode, ownCode, null);
            if(catalogPO==null){
                throw new Exception("还未配置该资源分类-"+parentCode);
            }
            fullName = catalogPO.getResourceName();
            fullName = getCatalogFullNameByCode(parentCode) + "/" + fullName;
            nameOverallMap.put(code, fullName);
            return fullName;
        }
    }

    /*
   *  根据资源分类编码获取 资源分类ID
   *
   *   @param: code 资源分类编码
   *   @return: 资源分类ID
   */
    private String getCatalogNodeIDsByCode(String code) throws Exception {

        Long fullCodeValue = 0L;
        String codeValue = null;
        if (StringUtils.isEmpty(code)) {
            throw new Exception("信息资源分类编码为空");
        }

        if(codeOverallMap.get(code)!=null){
            return codeOverallMap.get(code);
        }
        String parentCode = null;
        String ownCode = null;
        if(code.length()==1){
            parentCode = "0";
            ownCode = code;
            CatalogNodePO catalogPO = catalogNodeDAO.getByCondition(parentCode, ownCode, null);
            if (catalogPO== null) {
                throw new Exception("还未配置该资源分类-"+parentCode);
            }
            fullCodeValue = catalogPO.getId();
            codeValue = String.valueOf(fullCodeValue);
            codeOverallMap.put(code, codeValue);
            return codeValue;
        }else{
            parentCode = getParentCatalog(code);
            ownCode = code.substring(parentCode.length(), code.length());
            CatalogNodePO catalogPO = catalogNodeDAO.getByCondition(parentCode, ownCode, null);
            if(catalogPO==null){
                throw new Exception("还未配置该资源分类-"+parentCode);
            }
            fullCodeValue = catalogPO.getId();
            codeValue = getCatalogNodeIDsByCode(parentCode) + "/" + String.valueOf(fullCodeValue);
            codeOverallMap.put(code, codeValue);
            return codeValue;
        }
    }



    /*特殊处理内容： 设计原则 能够不进行特殊处理，则一定不要进行特殊处理
     *  对于Excel表格中 信息项数据格式- 数据库存储为字符，上面处理为 序号数字
     *                          数据类型：字符型 C、数值型 N、货币型 Y、日期型 D、日期时间型 T、
     *                          逻辑型 L、备注型 M、通用型 G、双精度型 B、整型 I、浮点型 F
     *
     */
    private String extendProcess(ExcelEntityEnum excelEnum, String value){
        String outValue = value;
        String colType[] ={"PLACE","C","N","Y","D","T","L","M","G","B","I","F"};
        if(excelEnum==RESOURCE_COLUMN_COL_TYPE){
            int importValue = Integer.valueOf(value).intValue();
            if(importValue<colType.length&&importValue>0){
                outValue = colType[importValue];
            }
        }
        return outValue;
    }



    private void setObjectByField(Object object, String importFieldName, String value) throws Exception {
        // 获得对象的类型
        Class<?> classType = object.getClass();
//        LOG.info("Class:" + classType.getName());

        // 获得对象的所有属性
        Field fields[] = classType.getDeclaredFields();
        Field field = null;
        for (int i = 0; i < fields.length; i++) {
            if (StringUtils.equals(fields[i].getName(), importFieldName)) {
                field = fields[i];
                break;
            }
        }
        if(field==null){
            throw new Exception(classType.getName()+" 类中不存在 "+importFieldName+" 属性");
        }
        String fieldName = field.getName();
        Class fieldType = field.getType();
        String fieldTypeName = fieldType.getName();
        Object objValue = null;
        if(StringUtils.equals(fieldTypeName, Long.class.getName())){
            objValue = Long.valueOf(value);
        }else if(StringUtils.equals(fieldTypeName, String.class.getName())){
            objValue = value;
        }else if(StringUtils.equals(fieldTypeName,Boolean.class.getName())){
            objValue = Boolean.valueOf(value);
        }else if(StringUtils.equals(fieldTypeName,long.class.getName())) {
            objValue = Long.valueOf(value).longValue();
        }else if(StringUtils.equals(fieldTypeName,int.class.getName())) {
            objValue = Integer.valueOf(value).intValue();
        }else if(StringUtils.equals(fieldTypeName, Date.class.getName())){
            objValue = DateTools.parseDate(value);
        }
//        LOG.info("name====={}",fieldName);
//        LOG.info("type====={}",fieldTypeName);
        String firstLetter = fieldName.substring(0, 1).toUpperCase();
        // 获得和属性对应的getXXX()方法的名字
        //String getMethodName = "get" + firstLetter + fieldName.substring(1);
        // 获得和属性对应的setXXX()方法的名字
        String setMethodName = "set" + firstLetter + fieldName.substring(1);

        // 获得和属性对应的getXXX()方法
        //Method getMethod = classType.getMethod(getMethodName, new Class[]{});
        // 获得和属性对应的setXXX()方法
        Method setMethod = classType.getMethod(setMethodName, new Class[]{field.getType()});

        // 调用原对象的getXXX()方法
        //Object value = getMethod.invoke(object, new Object[]{});
        //System.out.println("value===="+value);
        //System.out.println(fieldName + ":" + value);
        // 调用拷贝对象的setXXX()方法
        setMethod.invoke(object, new Object[]{objValue});
    }

    private String getParentCatalog(String fullCode){

        String parentFullCode = null;
        Long parentDepth = 0L;
        for(Map.Entry<String, Long> entry: nodeCodeDepthMap.entrySet()){
            String key = entry.getKey();
            if(fullCode.startsWith(key) && !StringUtils.equals(fullCode, key)){
                if(entry.getValue()>parentDepth){
                    parentDepth = entry.getValue();
                    parentFullCode = entry.getKey();
                }
            }
        }
        if(StringUtils.isNotEmpty(parentFullCode) && fullCode!=null) {
            nodeCodeDepthMap.put(fullCode, parentDepth + 1);
        }
        return parentFullCode;
    }

    private Long getCatalogDepth(String code){
        return nodeCodeDepthMap.get(code);
    }

    private void setCatalogDepth(String code, Long depth){
        if(code!=null) {
            nodeCodeDepthMap.put(code, depth);
        }
    }

    private CatalogNodeVO processCatalogExcel(String excelValue, Long type) throws Exception {

        String[] valueArray = excelValue.split("\\.");
        String catalogFullCode = valueArray[0];

        String parentCode= null;
        String parentFullCode = null;
        String ownCode = null;
        String fullCode = null;
        int depth = 0;

        //表示为类分类
        if(catalogFullCode.length()==1){
            parentFullCode = "0";
            parentCode = "0";
            depth = 1;
            ownCode = catalogFullCode;
            fullCode = catalogFullCode;
            setCatalogDepth(catalogFullCode, new Long(depth));
        }else{
            parentFullCode = getParentCatalog(catalogFullCode);
            if(StringUtils.isEmpty(parentFullCode)){
                throw new Exception("找不到该资源分类的父节点");
            }
            depth = getCatalogDepth(catalogFullCode).intValue();
            ownCode = catalogFullCode.substring(parentFullCode.length(), catalogFullCode.length());
//            ownCode = StringUtils.remove(catalogFullCode, parentFullCode);
        }

        String grandpaCode = null;
        if(parentFullCode.length()==1){
            parentCode = parentFullCode;
        }else{
            grandpaCode = getParentCatalog(parentFullCode);
            if(StringUtils.isEmpty(grandpaCode)){
                throw new Exception("找不到该资源分类的父节点");
            }
            parentCode = parentFullCode.substring(grandpaCode.length(), parentFullCode.length());
        }

        fullCode = catalogFullCode;
        CatalogNodeVO cnVO = new CatalogNodeVO();
        cnVO.setDept(depth);
        cnVO.setParentCode(parentCode);
        cnVO.setParentFullCode(parentFullCode);
        cnVO.setResourceEncode(ownCode);
        cnVO.setResourceName(valueArray[1]);
        return cnVO;

    }


    /**
     * 验证EXCEL文件
     *
     * @param filePath
     * @return
     */
    private boolean validateExcel(String filePath) {
        if (filePath == null || !(isExcel2003(filePath) || isExcel2007(filePath))) {
            LOG.error("文件名不是excel格式");
            return false;
        }
        return true;
    }

    /*根据后缀名称校验文件*/
    public boolean verifyExcel(String fileName){
        boolean flag = false;
        if(isExcel2003(fileName)||isExcel2007(fileName)){
            flag = true;
        }
        return flag;
    }

    // @描述：是否是2003的excel，返回true是2003
    private boolean isExcel2003(String filePath)  {
        return filePath.matches("^.+\\.(?i)(xls)$");
    }

    //@描述：是否是2007的excel，返回true是2007
    private boolean isExcel2007(String filePath)  {
        return filePath.matches("^.+\\.(?i)(xlsx)$");
    }
}
