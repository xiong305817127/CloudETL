package com.ys.idatrix.quality.dto.step.steps.validation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.validator.Validation;
import org.pentaho.di.trans.steps.validator.ValidatorMeta;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ys.idatrix.quality.dto.step.parts.ValidatorDto;
import com.ys.idatrix.quality.dto.step.steps.StepParameter;
import com.ys.idatrix.quality.recovery.trans.ResumeStepDataParser;
import com.ys.idatrix.quality.toolkit.analyzer.trans.step.StepDataRelationship;
import com.ys.idatrix.quality.toolkit.analyzer.trans.step.StepDataRelationshipParser;

import net.sf.json.JSONObject;

/**
 * Step - Validator(数据校验). 转换 org.pentaho.di.trans.steps.validator.ValidatorMeta
 * 
 * @author XH
 * @since 2017-09-05
 */
@Component("SPValidator")
@Scope("prototype")
public class SPValidator implements StepParameter, StepDataRelationshipParser ,ResumeStepDataParser{

	boolean validatingAll;
	boolean concatenatingErrors;
	String concatenationSeparator;
	List<ValidatorDto> validations;

	/**
	 * @return the validatingAll
	 */
	public boolean isValidatingAll() {
		return validatingAll;
	}

	/**
	 * @param 设置
	 *            validatingAll
	 */
	public void setValidatingAll(boolean validatingAll) {
		this.validatingAll = validatingAll;
	}

	/**
	 * @return the concatenatingErrors
	 */
	public boolean isConcatenatingErrors() {
		return concatenatingErrors;
	}

	/**
	 * @param 设置
	 *            concatenatingErrors
	 */
	public void setConcatenatingErrors(boolean concatenatingErrors) {
		this.concatenatingErrors = concatenatingErrors;
	}

	/**
	 * @return the concatenationSeparator
	 */
	public String getConcatenationSeparator() {
		return concatenationSeparator;
	}

	/**
	 * @param 设置
	 *            concatenationSeparator
	 */
	public void setConcatenationSeparator(String concatenationSeparator) {
		this.concatenationSeparator = concatenationSeparator;
	}

	/**
	 * @return the validations
	 */
	public List<ValidatorDto> getValidations() {
		return validations;
	}

	/**
	 * @param 设置
	 *            validations
	 */
	public void setValidations(List<ValidatorDto> validations) {
		this.validations = validations;
	}

	@Override
	public Object getParameterObject(Object json) {

		JSONObject jsonObj = JSONObject.fromObject(json);
		Map<String, Class<?>> classMap = new HashMap<>();
		classMap.put("validations", ValidatorDto.class);
		return (SPValidator) JSONObject.toBean(jsonObj, SPValidator.class, classMap);
	}

	@Override
	public Object encodeParameterObject(StepMeta stepMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPValidator spValidator = new SPValidator();
		ValidatorMeta validatormeta = (ValidatorMeta) stepMetaInterface;

		spValidator.setConcatenationSeparator(validatormeta.getConcatenationSeparator());
		spValidator.setValidatingAll(validatormeta.isValidatingAll());
		spValidator.setConcatenatingErrors(validatormeta.isConcatenatingErrors());

		spValidator.setValidations(transListToList(validatormeta.getValidations(), new DtoTransData<ValidatorDto>() {
			@Override
			public ValidatorDto dealData(Object obj, int index) {
				Validation v = (Validation) obj;
				return new ValidatorDto(v);
			}
		}));

		return spValidator;
	}

	@Override
	public void decodeParameterObject(StepMeta stepMeta, Object po, List<DatabaseMeta> databases,TransMeta transMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPValidator spValidator = (SPValidator) po;
		ValidatorMeta validatormeta = (ValidatorMeta) stepMetaInterface;

		validatormeta.setValidatingAll(spValidator.isValidatingAll());
		validatormeta.setConcatenatingErrors(spValidator.isConcatenatingErrors());
		validatormeta.setConcatenationSeparator(spValidator.getConcatenationSeparator());

		validatormeta.setValidations(transListToList(spValidator.getValidations(), new DtoTransData<Validation>() {
			@Override
			public Validation dealData(Object obj, int index) {
				ValidatorDto vd = (ValidatorDto) obj;
				return vd.transToValidation();
			}
		}));

	}

	/*
	 * 覆盖方法：getStepDataAndRelationship
	 */
	@Override
	public void getStepDataAndRelationship(TransMeta transMeta, StepMeta stepMeta, StepDataRelationship sdr) {
		//没有改变
	}
	
	@Override
	public int stepType() {
		return 12;
	}


}
