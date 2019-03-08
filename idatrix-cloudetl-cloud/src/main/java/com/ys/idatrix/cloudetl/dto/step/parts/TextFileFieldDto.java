/**
 * 云化数据集成系统 iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.dto.step.parts;

/**
 * SPTextFileOutput 的 fields域,等效  org.pentaho.di.trans.steps.textfileoutput.TextFileField
 * @author JW
 * @since 05-12-2017
 *
 */
public class TextFileFieldDto {
	
	private String name;
    private String type;
    private String format;
    private String currencyType;
    private String decimal;
    private String group;
    private String nullif;
    private String trimType;
    private int length=-1;
    private int precision=-1;
    
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

    public void setFormat(String format) {
        this.format = format;
    }
    public String getFormat() {
        return format;
    }

    public void setCurrencyType(String currencyType) {
        this.currencyType = currencyType;
    }
    public String getCurrencyType() {
        return currencyType;
    }

    public void setDecimal(String decimal) {
        this.decimal = decimal;
    }
    public String getDecimal() {
        return decimal;
    }

    public void setGroup(String group) {
        this.group = group;
    }
    public String getGroup() {
        return group;
    }

    public void setNullif(String nullif) {
        this.nullif = nullif;
    }
    public String getNullif() {
        return nullif;
    }

    public void setTrimType(String trimType) {
        this.trimType = trimType;
    }
    public String getTrimType() {
        return trimType;
    }

    public void setLength(int length) {
        this.length = length;
    }
    public int getLength() {
        return length;
    }

    public void setPrecision(int precision) {
        this.precision = precision;
    }
    public int getPrecision() {
        return precision;
    }

}
