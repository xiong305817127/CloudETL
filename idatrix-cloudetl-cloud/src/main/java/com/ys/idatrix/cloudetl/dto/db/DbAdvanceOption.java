/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.dto.db;

/**
 * DTO for database connection extra options.
 * @author JW
 * @since 2017年7月17日
 *
 */
public class DbAdvanceOption {
	
	private boolean supportsBooleanDataType; //SUPPORTS_BOOLEAN_DATA_TYPE
	private boolean supportsTimestampDataType; //SUPPORTS_TIMESTAMP_DATA_TYPE
	private boolean quoteAllFields; //QUOTE_ALL_FIELDS
	private boolean forceIdentifiersToUppercase; //FORCE_IDENTIFIERS_TO_UPPERCASE
	private boolean forceIdentifiersToLowercase; //FORCE_IDENTIFIERS_TO_LOWERCASE
	private boolean preserveReservedWordCcase; //PRESERVE_RESERVED_WORD_CASE
	private String preferredSchemaName; //PREFERRED_SCHEMA_NAME
	private String sqlConnect; //SQL_CONNECT
	
	
	public boolean isSupportsBooleanDataType() {
		return supportsBooleanDataType;
	}
	public void setSupportsBooleanDataType(boolean supportsBooleanDataType) {
		this.supportsBooleanDataType = supportsBooleanDataType;
	}
	public boolean isSupportsTimestampDataType() {
		return supportsTimestampDataType;
	}
	public void setSupportsTimestampDataType(boolean supportsTimestampDataType) {
		this.supportsTimestampDataType = supportsTimestampDataType;
	}
	public boolean isQuoteAllFields() {
		return quoteAllFields;
	}
	public void setQuoteAllFields(boolean quoteAllFields) {
		this.quoteAllFields = quoteAllFields;
	}
	public boolean isForceIdentifiersToUppercase() {
		return forceIdentifiersToUppercase;
	}
	public void setForceIdentifiersToUppercase(boolean forceIdentifiersToUppercase) {
		this.forceIdentifiersToUppercase = forceIdentifiersToUppercase;
	}
	public boolean isForceIdentifiersToLowercase() {
		return forceIdentifiersToLowercase;
	}
	public void setForceIdentifiersToLowercase(boolean forceIdentifiersToLowercase) {
		this.forceIdentifiersToLowercase = forceIdentifiersToLowercase;
	}
	public boolean isPreserveReservedWordCcase() {
		return preserveReservedWordCcase;
	}
	public void setPreserveReservedWordCcase(boolean preserveReservedWordCcase) {
		this.preserveReservedWordCcase = preserveReservedWordCcase;
	}
	public String getPreferredSchemaName() {
		return preferredSchemaName;
	}
	public void setPreferredSchemaName(String preferredSchemaName) {
		this.preferredSchemaName = preferredSchemaName;
	}
	public String getSqlConnect() {
		return sqlConnect;
	}
	public void setSqlConnect(String sqlConnect) {
		this.sqlConnect = sqlConnect;
	}
	@Override
	public String toString() {
		return "DbAdvanceOption [supportsBooleanDataType=" + supportsBooleanDataType + ", supportsTimestampDataType="
				+ supportsTimestampDataType + ", quoteAllFields=" + quoteAllFields + ", forceIdentifiersToUppercase="
				+ forceIdentifiersToUppercase + ", forceIdentifiersToLowercase=" + forceIdentifiersToLowercase
				+ ", preserveReservedWordCcase=" + preserveReservedWordCcase + ", preferredSchemaName="
				+ preferredSchemaName + ", sqlConnect=" + sqlConnect + "]";
	}
	 
	
	
}
