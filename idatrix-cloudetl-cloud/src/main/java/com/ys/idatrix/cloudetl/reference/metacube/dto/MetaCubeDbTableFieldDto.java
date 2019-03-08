/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.reference.metacube.dto;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * DTO for DB table field details.
 * @author JW
 * @since 2017年6月16日
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MetaCubeDbTableFieldDto {

	private String id;
	private String fieldName;
	private String fieldType;
	private String fieldLength;
	private String fieldPrecision;
	private String dateformat;
	private String isNull;
	private Boolean isPk;
	private String description;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public String getFieldType() {
		return fieldType;
	}
	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}
	public String getFieldLength() {
		return fieldLength;
	}
	public void setFieldLength(String fieldLength) {
		this.fieldLength = fieldLength;
	}
	public String getFieldPrecision() {
		return fieldPrecision;
	}
	public void setFieldPrecision(String fieldPrecision) {
		this.fieldPrecision = fieldPrecision;
	}
	public String getDateformat() {
		return dateformat;
	}
	public void setDateformat(String dateformat) {
		this.dateformat = dateformat;
	}
	public String getIsNull() {
		return isNull;
	}
	public void setIsNull(String isNull) {
		this.isNull = isNull;
	}
	
	public Boolean getIsPk() {
		return isPk;
	}
	public void setIsPk(Boolean isPk) {
		this.isPk = isPk;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	@Override
	public String toString() {
		return "MetaCubeDbTableFieldDto [fieldName=" + fieldName + ", fieldType=" + fieldType + ", fieldLength="
				+ fieldLength + ", fieldPrecision=" + fieldPrecision + ", dateformat=" + dateformat + ", isNull="
				+ isNull + ", isPk=" + isPk + ", description=" + description + "]";
	}
	
}
