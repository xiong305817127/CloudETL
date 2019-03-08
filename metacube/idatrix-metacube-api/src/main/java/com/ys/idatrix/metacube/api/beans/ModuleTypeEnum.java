package com.ys.idatrix.metacube.api.beans;

/**
 * @ClassName ModuleTypeEnum
 * @Description
 * @Author ouyang
 * @Date
 */
public enum ModuleTypeEnum {
    ANALYZE("analyze", "数据分析&探索"),
    ETL("etl", "数据采集&集成"),
    ;

    private String code;
    private String name;

    ModuleTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}