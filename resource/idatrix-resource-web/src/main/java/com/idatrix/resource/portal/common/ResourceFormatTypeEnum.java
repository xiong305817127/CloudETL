package com.idatrix.resource.portal.common;

/**
 * 主要是资源类型的枚举包含： 文件、数据库、接口类型, file/db/interface
 */
public enum ResourceFormatTypeEnum {

    /*不确定*/
    NOT_SURE(0,"不确定", "not_sure", "不确定"),
    /*电子文件*/
    FILE(1,"电子文件", "file" , "文件类型"),
    /*电子表格*/
    FORM(2,"电子表格", "file", "文件类型"),
    /*数据库*/
    DB(3,"数据库", "db", "数据库类型"),
    /*图形图像*/
    IMAGE(4,"图形图像", "file", "文件类型"),
    /*流媒体*/
    STEAM_MEDIA(5,"流媒体", "file", "文件类型"),
    /*自定义格式*/
    SELF_FORMAT(6,"自定义格式", "file", "文件类型"),
    /*网络接口*/
    SERVICE_INTERFACE(7,"网络接口", "interface", "接口类型");

    int typeValue;
    String formatInfoZH;
    String formatType;
    String formatDescription;

    public int getTypeValue() {
        return typeValue;
    }

    public void setTypeValue(int typeValue) {
        this.typeValue = typeValue;
    }

    public String getFormatInfoZH() {
        return formatInfoZH;
    }

    public void setFormatInfoZH(String formatInfoZH) {
        this.formatInfoZH = formatInfoZH;
    }

    public String getFormatType() {
        return formatType;
    }

    public void setFormatType(String formatType) {
        this.formatType = formatType;
    }

    public String getFormatDescription() {
        return formatDescription;
    }

    public void setFormatDescription(String formatDescription) {
        this.formatDescription = formatDescription;
    }

    ResourceFormatTypeEnum(int typeValue){this.typeValue=typeValue;};

    ResourceFormatTypeEnum(int typeValue,String formatInfoZH){
        this.typeValue=typeValue;
        this.formatInfoZH = formatInfoZH;
    };

    ResourceFormatTypeEnum(int typeValue,String formatInfoZH, String formatType, String description){
        this.typeValue=typeValue;
        this.formatInfoZH = formatInfoZH;
        this.formatType = formatType;
        this.formatDescription = description;
    };

//    public static ResourceFormatTypeEnum getFormatType(int value){
//        for(ResourceFormatTypeEnum formatType: values()){
//            if(formatType.getTypeValue()==value){
//                return formatType;
//            }
//        }
//        return NOT_SURE;
//    }

    public static String getFormatInfoZH(int value){
        for(ResourceFormatTypeEnum formatType: values()){
            if(formatType.getTypeValue()==value){
                return formatType.getFormatInfoZH();
            }
        }
        return NOT_SURE.getFormatInfoZH();
    }

    public static String getFormatType(int value){
        for(ResourceFormatTypeEnum formatType: values()){
            if(formatType.getTypeValue()==value){
                return formatType.getFormatType();
            }
        }
        return NOT_SURE.getFormatType();
    }

    public static String getFormatDescription(int value){
        for(ResourceFormatTypeEnum formatType: values()){
            if(formatType.getTypeValue()==value){
                return formatType.getFormatDescription();
            }
        }
        return NOT_SURE.getFormatDescription();
    }
}
