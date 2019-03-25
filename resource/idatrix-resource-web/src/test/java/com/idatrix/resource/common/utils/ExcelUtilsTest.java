package com.idatrix.resource.common.utils;


import com.idatrix.resource.catalog.service.ICatalogClassifyService;
import com.idatrix.resource.catalog.vo.CatalogNodeVO;
import com.idatrix.resource.catalog.vo.ResourceConfigVO;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Robin Wing on 2018-7-3.
 */
@RunWith(SpringJUnit4ClassRunner.class)
/*	告诉junit spring配置文件 */
@ContextConfiguration({"classpath:META-INF/spring/catalog-root.xml"})
public class ExcelUtilsTest {

    @Autowired
    private BatchTools readExcel;

    @Autowired
    private ICatalogClassifyService catalogClassifyService;

    @Test
    public void getCurrentTiem(){
        Date startTime = DateTools.getDateBefore(new Date(), 10);
        Date endTime = DateTools.getDateAfter(new Date(), 10);
        System.out.println(CommonUtils.getRecentDayList(startTime, endTime));
    }

    @Test
    public void execelImportCatalogTest(){

//        String path = "D:\\robin\\目录分类结构（全部）不含130900.xlsx";
        String path = "D:\\\\robin\\目录分类结构_20180724-导入.xlsx";
//        String path = "D:\\robin\\目录分类结构.xlsx";
        File excelFile = new File(path);
        System.out.println(excelFile.getAbsoluteFile());

        File file = new File(path);
        try {

            List<CatalogNodeVO> cnList = null;
            cnList = readExcel.readExcelCatalogValue(new File(path));
            for(CatalogNodeVO cnvo : cnList){
                System.out.println(cnvo);
            }
            System.out.println("=============================================");
            //readExcel.preProcessExcelCatalog(cnList);
//            readExcel.saveExcelCatalogNode("robin", cnList);
//            catalogClassifyService.saveExcelCatalogNode("robin", cnList);//            readExcel.saveExcelCatalogNode("admin",cnList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    public void execelImportResourceTest(){

        String path = "D:\\robin\\政务信息资源Excel报表-调整-0726.xlsx";
        File excelFile = new File(path);
        System.out.println(excelFile.getAbsoluteFile());

        List<ResourceConfigVO> rcList = new ArrayList<ResourceConfigVO>();
        Map<String, List<Object>> cnList = null;
        try {
            System.out.println("#############################################");
            cnList = readExcel.readResourceExcelValue(new File(path));
            System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
            rcList = readExcel.processResourceExcel(cnList);
            System.out.println("=============================================");
            //System.out.println(rcList);
            readExcel.preProcesBeforeSave(12L, rcList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSplit(){
//        String line = "共享方式分类\n" +
//                "（必填项）";
        String line = "分类五级（选填项）";
        String[] some = line.split("\n?（");
        for(String k:some){
            System.out.println("k-"+k+"-m");
        }
    }

    @Test
    public void objectCpTest() throws Exception{
        ResourceConfigVO rcVO = new ResourceConfigVO();
        rcVO.setCode("123");
        setObjectByField(rcVO, "code", "999");
        System.out.println("======= ResourceConfigVO Code:" + rcVO.getCode());

        rcVO.setBindTableId(987L);
        setObjectByField(rcVO, "bindTableId", "111");
        System.out.println("======= ResourceConfigVO BindTableId:" + rcVO.getBindTableId());

        rcVO.setPubDate(new Date());
        setObjectByField(rcVO, "pubDate", "2017-10-11");
        System.out.println("======= ResourceConfigVO pubDate:" + DateTools.formatDate(rcVO.getPubDate()));

    }


      public void setObjectByField(Object object, String importFieldName, String value) throws Exception {
        // 获得对象的类型
        Class<?> classType = object.getClass();
//        System.out.println("Class:" + classType.getName());

        // 通过默认构造方法创建一个新的对象
//        Object objectCopy = classType.getConstructor(new Class[]{}).newInstance(new Object[]{});

        // 获得对象的所有属性
        Field fields[] = classType.getDeclaredFields();
        Field field = null;
        for (int i = 0; i < fields.length; i++) {
            if (StringUtils.equals(fields[i].getName(), importFieldName)) {
                field = fields[i];
                break;
            }
//            System.out.println("name-"+fields[i].getName());
//            System.out.println("type-"+fields[i].getType().getName());
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
        System.out.println("name====="+fieldName);
        System.out.println("type====="+fieldTypeName);
        String firstLetter = fieldName.substring(0, 1).toUpperCase();
        // 获得和属性对应的getXXX()方法的名字
//            String getMethodName = "get" + firstLetter + fieldName.substring(1);
        // 获得和属性对应的setXXX()方法的名字
        String setMethodName = "set" + firstLetter + fieldName.substring(1);

        // 获得和属性对应的getXXX()方法
//            Method getMethod = classType.getMethod(getMethodName, new Class[]{});
        // 获得和属性对应的setXXX()方法
        Method setMethod = classType.getMethod(setMethodName, new Class[]{field.getType()});

        // 调用原对象的getXXX()方法
//            Object value = getMethod.invoke(object, new Object[]{});
//            System.out.println("value===="+value);
//            System.out.println(fieldName + ":" + value);
        // 调用拷贝对象的setXXX()方法
        setMethod.invoke(object, new Object[]{objValue});
    }




}
