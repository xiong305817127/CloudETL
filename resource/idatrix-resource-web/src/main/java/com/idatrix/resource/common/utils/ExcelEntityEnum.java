package com.idatrix.resource.common.utils;

import org.apache.commons.lang3.StringUtils;

/**
 *   该枚举主要是描述 Excel表格中 每列展现元素的序号 和 ResourceConfigPO 属性对应关系
 *
 *   type 表示 0表示ResourceConfig里面内容，1 表示ResourceColumnVO 里面内容
 *   entityName 表示 ResourceConfigPO或者ResourceColumnPO对应属性名称
 *   entityNameZH 表示 Excel中属性 中文描述
 *   entityContextZH 表示列表内显示内容， 配置中文，到时数据库存储对应 数字。
 *   @author:robin
 *   @date:2018-7-3
 *   @version：1.0
 */
public enum ExcelEntityEnum {

    NOT_SURE(-1, "notSure", "不确定"),
    RESOURCE_CATALOG(-1, "catalogNameCatalog",  "类分类"),
    RESOURCE_ITEM(-1, "catalogNameItem",  "项分类"),
    RESOURCE_SECTION(-1, "catalogNameSection",  "目分类"),
    RESOURCE_DETAIL(-1, "catalogNameDetail",  "细目分类"),

    RESOURCE_NAME(0, "name", "信息资源名称"),
    RESOURCE_CODE(0, "code", "信息资源代码"),
    RESOURCE_DEPT_NAME(0, "deptName", "信息资源提供方"),
    RESOURCE_DEPT_CODE(0, "deptCode", "资源提供方代码"),
    RESOURCE_REMARK(0, "remark", "信息资源摘要"),

    RESOURCE_FORMAT_TYPE(0, "formatType", "信息资源格式分类",
            new String[]{"占坑","电子文件","电子表格","数据库",
                    "图形图像","流媒体","自描述格式","服务接口"}),

    RESOURCE_FORMAT_INFO(0, "formatInfo", "信息资源格式类型"),
    RESOURCE_COLUMN_COL_NAME(1, "colName", "信息项名称"),
    RESOURCE_COLUMN_COL_TYPE(1, "colType", "数据类型",
            new String[]{"占坑","字符型C","数值型N","货币型Y","日期型D","日期时间型T",
                    "逻辑型L","备注型M","通用型G","双精度型B","整型I","浮点型F"}),

//    RESOURCE_COLUMN_TABLE_COL_TYPE(1, "tableColType", "数据长度"),
    RESOURCE_SHARE_TYPE(0, "shareType", "共享类型",
            new String[]{"占坑","无条件共享","有条件共享","不予共享"}),

    RESOURCE_SHARE_CONDITION(0, "shareCondition", "共享条件"),
    //共享方式分类 数据库中没这个字段
    RESOURCE_SHARE_METHOD(0, "shareMethod", "共享方式类型",
            new String[]{"占坑","数据库","文件","接口"}),
//            new String[]{"占坑","数据库","文件下载","webservice服务/接口"}),
    RESOURCE_OPEN_TYPE(0, "openType", "是否向社会开放",
            new String[]{"否", "是"}),
    RESOURCE_OPEN_CONDITION(0, "openCondition", "开放条件"),
    RESOURCE_REFRESH_CYCLE(0, "refreshCycle", "更新周期",
             new String[]{"占坑","实时","每日","每周","每月","每季度","每半年","每年","其他"}),
    RESOURCE_PUB_DATE(0, "pubDate", "发布日期");

    private int type;
    private String entityName;
    private String entityNameZH;
    private String[] entityContextZH;

    ExcelEntityEnum(int type, String entityName, String entityNameZH){
        this.type = type;
        this.entityName = entityName;
        this.entityNameZH = entityNameZH;
    }

    ExcelEntityEnum(int type, String entityName, String entityNameZH, String[] entityContextZH){
        this.type = type;
        this.entityName = entityName;
        this.entityNameZH = entityNameZH;
        this.entityContextZH = entityContextZH;
    }

    public static ExcelEntityEnum getByName(String name){
        for(ExcelEntityEnum excel: values()) {
            //if(excel.getEntityNameZH().contains(name)){
            if(StringUtils.equals(name, excel.getEntityNameZH())){
                return excel;
            }
        }
        return NOT_SURE;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getEntityNameZH() {
        return entityNameZH;
    }

    public void setEntityNameZH(String entityNameZH) {
        this.entityNameZH = entityNameZH;
    }

    public String[] getEntityContextZH() {
        return entityContextZH;
    }

    public void setEntityContextZH(String[] entityContextZH) {
        this.entityContextZH = entityContextZH;
    }
}
