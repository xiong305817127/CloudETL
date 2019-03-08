package com.idatrix.resource.common.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by Robin Wing on 2018-6-8.
 */
 /*库信息*/
public enum LibInfo{
    /*基础库*/
    BASE("1", "基础库", "base"),
    /*主题库*/
    TOPIC("2", "主题库", "topic"),
    /*部门库*/
    DEPARTMENT("3", "部门库", "department");

    String libValue;
    String libNameZH;
    String libName;

    public String getLibValue() {
        return libValue;
    }

    public void setLibValue(String libValue) {
        this.libValue = libValue;
    }

    public String getLibNameZH() {
        return libNameZH;
    }

    public void setLibNameZH(String libNameZH) {
        this.libNameZH = libNameZH;
    }

    public String getLibName() {
        return libName;
    }

    public void setLibName(String libName) {
        this.libName = libName;
    }


    LibInfo(String libValue, String libNameZH, String libName){
        this.libValue = libValue;
        this.libNameZH = libNameZH;
        this.libName = libName;
    }

    public static String getLibNameZH(String libName){
        for(LibInfo libInfo: values()){
            if(StringUtils.equals(libName, libInfo.getLibName())){
                return libInfo.getLibNameZH();
            }
        }
        return null;
    }

    public static String getLibValue(String libName){
        for(LibInfo libInfo: values()){
            if(StringUtils.equals(libName, libInfo.getLibName())){
                return libInfo.getLibValue();
            }
        }
        return null;
    }
}