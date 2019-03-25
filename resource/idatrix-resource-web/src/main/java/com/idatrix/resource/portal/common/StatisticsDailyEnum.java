package com.idatrix.resource.portal.common;

/**
 * 利用反射方便使用
 */
public enum StatisticsDailyEnum {

    DB_COUNT("dbCount", "数据库上报数据量"),

    FILE_COUNT("fileCount", "文件类型资源上报量"),

    INTERFACE_COUNT("interfaceCount", "接口调用次数");

    private String paraName;

    private String paraNameZH;

    StatisticsDailyEnum(String paraName, String paraNameZH){
        this.paraName = paraName;
        this.paraNameZH = paraNameZH;
    }

    public String getParaName() {
        return paraName;
    }

    public void setParaName(String paraName) {
        this.paraName = paraName;
    }

    public String getParaNameZH() {
        return paraNameZH;
    }

    public void setParaNameZH(String paraNameZH) {
        this.paraNameZH = paraNameZH;
    }
}
