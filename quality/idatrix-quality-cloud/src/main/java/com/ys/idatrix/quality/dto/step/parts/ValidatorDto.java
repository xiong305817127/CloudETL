package com.ys.idatrix.quality.dto.step.parts;

import java.util.Arrays;
import java.util.List;

import org.pentaho.di.trans.steps.validator.Validation;

/**
 * SPValidator的 validations域 等效 org.pentaho.di.trans.steps.validator.Validation
 *
 * @author XH
 * @since 2017年9月5日
 *
 */
public class ValidatorDto {

	String name;
	String fieldName;
	String maximumLength;
	String minimumLength;
	boolean nullAllowed;
	boolean onlyNullAllowed;
	boolean onlyNumericAllowed;
	int dataType;
	boolean dataTypeVerified;
	String conversionMask;
	String decimalSymbol;
	String groupingSymbol;
	String maximumValue;
	String minimumValue;
	String startString;
	String endString;
	String startStringNotAllowed;
	String endStringNotAllowed;
	String regularExpression;
	String regularExpressionNotAllowed;
	String errorCode;
	String errorDescription;
	boolean sourcingValues;
	String sourcingStepName;
	String sourcingField;
	List<String> allowedValues;

	/**
	 * 
	 */
	public ValidatorDto() {
		super();
	}

	/**
	 * 
	 */
	public ValidatorDto(Validation v) {
		super();

		name = v.getName();
		fieldName = v.getFieldName();
		maximumLength = v.getMaximumLength();
		minimumLength = v.getMinimumLength();
		nullAllowed = v.isNullAllowed();
		onlyNullAllowed = v.isOnlyNullAllowed();
		onlyNumericAllowed = v.isOnlyNumericAllowed();
		dataType = v.getDataType();
		dataTypeVerified = v.isDataTypeVerified();
		conversionMask = v.getConversionMask();
		decimalSymbol = v.getDecimalSymbol();
		groupingSymbol = v.getGroupingSymbol();
		maximumValue = v.getMaximumValue();
		minimumValue = v.getMinimumValue();
		startString = v.getStartString();
		endString = v.getEndString();
		startStringNotAllowed = v.getStartStringNotAllowed();
		endStringNotAllowed = v.getEndStringNotAllowed();
		regularExpression = v.getRegularExpression();
		regularExpressionNotAllowed = v.getRegularExpressionNotAllowed();
		errorCode = v.getErrorCode();
		errorDescription = v.getErrorDescription();
		sourcingValues = v.isSourcingValues();
		sourcingStepName = v.getSourcingStepName();
		sourcingField = v.getSourcingField();
		if (v.getAllowedValues() != null) {
			allowedValues = Arrays.asList(v.getAllowedValues());
		}

	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param 设置
	 *            name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the fieldName
	 */
	public String getFieldName() {
		return fieldName;
	}

	/**
	 * @param 设置
	 *            fieldName
	 */
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	/**
	 * @return the maximumLength
	 */
	public String getMaximumLength() {
		return maximumLength;
	}

	/**
	 * @param 设置
	 *            maximumLength
	 */
	public void setMaximumLength(String maximumLength) {
		this.maximumLength = maximumLength;
	}

	/**
	 * @return the minimumLength
	 */
	public String getMinimumLength() {
		return minimumLength;
	}

	/**
	 * @param 设置
	 *            minimumLength
	 */
	public void setMinimumLength(String minimumLength) {
		this.minimumLength = minimumLength;
	}

	/**
	 * @return the nullAllowed
	 */
	public boolean isNullAllowed() {
		return nullAllowed;
	}

	/**
	 * @param 设置
	 *            nullAllowed
	 */
	public void setNullAllowed(boolean nullAllowed) {
		this.nullAllowed = nullAllowed;
	}

	/**
	 * @return the onlyNullAllowed
	 */
	public boolean isOnlyNullAllowed() {
		return onlyNullAllowed;
	}

	/**
	 * @param 设置
	 *            onlyNullAllowed
	 */
	public void setOnlyNullAllowed(boolean onlyNullAllowed) {
		this.onlyNullAllowed = onlyNullAllowed;
	}

	/**
	 * @return the onlyNumericAllowed
	 */
	public boolean isOnlyNumericAllowed() {
		return onlyNumericAllowed;
	}

	/**
	 * @param 设置
	 *            onlyNumericAllowed
	 */
	public void setOnlyNumericAllowed(boolean onlyNumericAllowed) {
		this.onlyNumericAllowed = onlyNumericAllowed;
	}

	/**
	 * @return the dataType
	 */
	public int getDataType() {
		return dataType;
	}

	/**
	 * @param 设置
	 *            dataType
	 */
	public void setDataType(int dataType) {
		this.dataType = dataType;
	}

	/**
	 * @return the dataTypeVerified
	 */
	public boolean isDataTypeVerified() {
		return dataTypeVerified;
	}

	/**
	 * @param 设置
	 *            dataTypeVerified
	 */
	public void setDataTypeVerified(boolean dataTypeVerified) {
		this.dataTypeVerified = dataTypeVerified;
	}

	/**
	 * @return the conversionMask
	 */
	public String getConversionMask() {
		return conversionMask;
	}

	/**
	 * @param 设置
	 *            conversionMask
	 */
	public void setConversionMask(String conversionMask) {
		this.conversionMask = conversionMask;
	}

	/**
	 * @return the decimalSymbol
	 */
	public String getDecimalSymbol() {
		return decimalSymbol;
	}

	/**
	 * @param 设置
	 *            decimalSymbol
	 */
	public void setDecimalSymbol(String decimalSymbol) {
		this.decimalSymbol = decimalSymbol;
	}

	/**
	 * @return the groupingSymbol
	 */
	public String getGroupingSymbol() {
		return groupingSymbol;
	}

	/**
	 * @param 设置
	 *            groupingSymbol
	 */
	public void setGroupingSymbol(String groupingSymbol) {
		this.groupingSymbol = groupingSymbol;
	}

	/**
	 * @return the maximumValue
	 */
	public String getMaximumValue() {
		return maximumValue;
	}

	/**
	 * @param 设置
	 *            maximumValue
	 */
	public void setMaximumValue(String maximumValue) {
		this.maximumValue = maximumValue;
	}

	/**
	 * @return the minimumValue
	 */
	public String getMinimumValue() {
		return minimumValue;
	}

	/**
	 * @param 设置
	 *            minimumValue
	 */
	public void setMinimumValue(String minimumValue) {
		this.minimumValue = minimumValue;
	}

	/**
	 * @return the startString
	 */
	public String getStartString() {
		return startString;
	}

	/**
	 * @param 设置
	 *            startString
	 */
	public void setStartString(String startString) {
		this.startString = startString;
	}

	/**
	 * @return the endString
	 */
	public String getEndString() {
		return endString;
	}

	/**
	 * @param 设置
	 *            endString
	 */
	public void setEndString(String endString) {
		this.endString = endString;
	}

	/**
	 * @return the startStringNotAllowed
	 */
	public String getStartStringNotAllowed() {
		return startStringNotAllowed;
	}

	/**
	 * @param 设置
	 *            startStringNotAllowed
	 */
	public void setStartStringNotAllowed(String startStringNotAllowed) {
		this.startStringNotAllowed = startStringNotAllowed;
	}

	/**
	 * @return the endStringNotAllowed
	 */
	public String getEndStringNotAllowed() {
		return endStringNotAllowed;
	}

	/**
	 * @param 设置
	 *            endStringNotAllowed
	 */
	public void setEndStringNotAllowed(String endStringNotAllowed) {
		this.endStringNotAllowed = endStringNotAllowed;
	}

	/**
	 * @return the regularExpression
	 */
	public String getRegularExpression() {
		return regularExpression;
	}

	/**
	 * @param 设置
	 *            regularExpression
	 */
	public void setRegularExpression(String regularExpression) {
		this.regularExpression = regularExpression;
	}

	/**
	 * @return the regularExpressionNotAllowed
	 */
	public String getRegularExpressionNotAllowed() {
		return regularExpressionNotAllowed;
	}

	/**
	 * @param 设置
	 *            regularExpressionNotAllowed
	 */
	public void setRegularExpressionNotAllowed(String regularExpressionNotAllowed) {
		this.regularExpressionNotAllowed = regularExpressionNotAllowed;
	}

	/**
	 * @return the errorCode
	 */
	public String getErrorCode() {
		return errorCode;
	}

	/**
	 * @param 设置
	 *            errorCode
	 */
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	/**
	 * @return the errorDescription
	 */
	public String getErrorDescription() {
		return errorDescription;
	}

	/**
	 * @param 设置
	 *            errorDescription
	 */
	public void setErrorDescription(String errorDescription) {
		this.errorDescription = errorDescription;
	}

	/**
	 * @return the sourcingValues
	 */
	public boolean isSourcingValues() {
		return sourcingValues;
	}

	/**
	 * @param 设置
	 *            sourcingValues
	 */
	public void setSourcingValues(boolean sourcingValues) {
		this.sourcingValues = sourcingValues;
	}

	/**
	 * @return the sourcingStepName
	 */
	public String getSourcingStepName() {
		return sourcingStepName;
	}

	/**
	 * @param 设置
	 *            sourcingStepName
	 */
	public void setSourcingStepName(String sourcingStepName) {
		this.sourcingStepName = sourcingStepName;
	}

	/**
	 * @return the sourcingField
	 */
	public String getSourcingField() {
		return sourcingField;
	}

	/**
	 * @param 设置
	 *            sourcingField
	 */
	public void setSourcingField(String sourcingField) {
		this.sourcingField = sourcingField;
	}

	/**
	 * @return the allowedValues
	 */
	public List<String> getAllowedValues() {
		return allowedValues;
	}

	/**
	 * @param 设置
	 *            allowedValues
	 */
	public void setAllowedValues(List<String> allowedValues) {
		this.allowedValues = allowedValues;
	}

	public Validation transToValidation(){

		Validation v = new Validation();

		v.setName(name );
		v.setFieldName(fieldName);
		v.setMaximumLength(maximumLength);
		v.setMinimumLength(minimumLength);
		v.setNullAllowed(nullAllowed);
		v.setOnlyNullAllowed(onlyNullAllowed);
		v.setOnlyNumericAllowed(onlyNumericAllowed);
		v.setDataType(dataType);
		v.setDataTypeVerified(dataTypeVerified);
		v.setConversionMask(conversionMask);
		v.setDecimalSymbol(decimalSymbol);
		v.setGroupingSymbol(groupingSymbol);
		v.setMaximumValue(maximumValue);
		v.setMinimumValue(minimumValue);
		v.setStartString(startString);
		v.setEndString(endString);
		v.setStartStringNotAllowed(startStringNotAllowed);
		v.setEndStringNotAllowed(endStringNotAllowed);
		v.setRegularExpression(regularExpression);
		v.setRegularExpressionNotAllowed(regularExpressionNotAllowed);
		v.setErrorCode(errorCode);
		v.setErrorDescription(errorDescription);
		v.setSourcingValues(sourcingValues);
		v.setSourcingStepName(sourcingStepName);
		v.setSourcingField(sourcingField);

		if (allowedValues != null) {
			v.setAllowedValues(allowedValues.toArray(new String[allowedValues.size()]));
		}

		return v;
	}


}
