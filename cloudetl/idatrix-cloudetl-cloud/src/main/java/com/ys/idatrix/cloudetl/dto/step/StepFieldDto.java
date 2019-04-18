/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.dto.step;

import io.swagger.annotations.ApiModel;

/**
 * step 域 信息
 * @author JW
 * @since 05-12-2017
 *
 */
@ApiModel("转换步骤处理流的域信息")
public class StepFieldDto {
	
	private String name;
    private String type;
    private String length;
    private String precision;
    private String origin;
    private String storageType;
    private String conversionMask;
    private String currencySymbol;
    private String decimalSymbol;
    private String groupingSymbol;
    private String trimType;
    private String comments;
    
    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

    public void setType(String type) {
        this.type = type;
    }
    public String getType() {
        return type;
    }

    public void setLength(String length) {
        this.length = length;
    }
    public String getLength() {
        return length;
    }

    public void setPrecision(String precision) {
        this.precision = precision;
    }
    public String getPrecision() {
        return precision;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }
    public String getOrigin() {
        return origin;
    }

    public void setStorageType(String storageType) {
        this.storageType = storageType;
    }
    public String getStorageType() {
        return storageType;
    }

    public void setConversionMask(String conversionMask) {
        this.conversionMask = conversionMask;
    }
    public String getConversionMask() {
        return conversionMask;
    }

    public void setCurrencySymbol(String currencySymbol) {
        this.currencySymbol = currencySymbol;
    }
    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public void setDecimalSymbol(String decimalSymbol) {
        this.decimalSymbol = decimalSymbol;
    }
    public String getDecimalSymbol() {
        return decimalSymbol;
    }

    public void setGroupingSymbol(String groupingSymbol) {
        this.groupingSymbol = groupingSymbol;
    }
    public String getGroupingSymbol() {
        return groupingSymbol;
    }

    public void setTrimType(String trimType) {
        this.trimType = trimType;
    }
    public String getTrimType() {
        return trimType;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
    public String getComments() {
        return comments;
    }

}
