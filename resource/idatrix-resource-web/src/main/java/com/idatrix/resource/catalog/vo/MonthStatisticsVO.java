package com.idatrix.resource.catalog.vo;

/**
 * Created by Robin Wing on 2018-5-29.
 */
public class MonthStatisticsVO {

    /*三大库基本类型： 基础库、部门库、主题库： base/department/topic
    * 为所有信息时显示 type为 all*/
    private String catalogName;

    /*月度名称格式为 yyyy年mm月*/
    private String monthName;

    private int subCount;

    private int pubCount;

    private int regCount;

    public String getCatalogName() {
        return catalogName;
    }

    public void setCatalogName(String catalogName) {
        this.catalogName = catalogName;
    }

    public String getMonthName() {
        return monthName;
    }

    public void setMonthName(String monthName) {
        this.monthName = monthName;
    }

    public int getSubCount() {
        return subCount;
    }

    public void setSubCount(int subCount) {
        this.subCount = subCount;
    }

    public int getPubCount() {
        return pubCount;
    }

    public void setPubCount(int pubCount) {
        this.pubCount = pubCount;
    }

    public int getRegCount() {
        return regCount;
    }

    public void setRegCount(int regCount) {
        this.regCount = regCount;
    }
}
