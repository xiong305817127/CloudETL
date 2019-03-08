/**
 * 云化数据集成系统 iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.dto.step.steps.transfor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.calculator.CalculatorMeta;
import org.pentaho.di.trans.steps.calculator.CalculatorMetaFunction;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ys.idatrix.cloudetl.dto.step.parts.CalculatorCalculatorMetaFunctionDto;
import com.ys.idatrix.cloudetl.dto.step.steps.StepParameter;
import com.ys.idatrix.cloudetl.toolkit.analyzer.trans.step.StepDataRelationship;
import com.ys.idatrix.cloudetl.toolkit.analyzer.trans.step.StepDataRelationshipParser;
import com.ys.idatrix.cloudetl.toolkit.utils.RelationshipUtil;

import net.sf.json.JSONObject;

/**
 * Step - Calculator(计算器) 转换
 * org.pentaho.di.trans.steps.calculator.CalculatorMeta
 * 
 * @author XH
 * @since 2017年6月13日
 *
 */
@Component("SPCalculator")
@Scope("prototype")
public class SPCalculator implements StepParameter, StepDataRelationshipParser {

	List<CalculatorCalculatorMetaFunctionDto> calculation;

	/**
	 * @return calculation
	 */
	public List<CalculatorCalculatorMetaFunctionDto> getCalculation() {
		return calculation;
	}

	/**
	 * @param calculation
	 *            要设置的 calculation
	 */
	public void setCalculation(List<CalculatorCalculatorMetaFunctionDto> calculation) {
		this.calculation = calculation;
	}

	/* 
	 * 
	 */
	@Override
	public Object getParameterObject(Object json) {

		JSONObject jsonObj = JSONObject.fromObject(json);
		Map<String, Class<?>> classMap = new HashMap<>();
		classMap.put("calculation", CalculatorCalculatorMetaFunctionDto.class);
		return (SPCalculator) JSONObject.toBean(jsonObj, SPCalculator.class, classMap);
	}

	/* 
	 * 
	 */
	@Override
	public Object encodeParameterObject(StepMeta stepMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPCalculator spCalculator = new SPCalculator();
		CalculatorMeta calculatormeta = (CalculatorMeta) stepMetaInterface;

		CalculatorMetaFunction[] calculationArray = calculatormeta.getCalculation();
		if (calculationArray != null) {
			List<CalculatorCalculatorMetaFunctionDto> calculationList = Arrays.asList(calculationArray).stream()
					.map(temp1 -> {
						CalculatorCalculatorMetaFunctionDto temp2 = new CalculatorCalculatorMetaFunctionDto();
						temp2.setFieldname(temp1.getFieldName());
						temp2.setCalctype(temp1.getCalcType());
						temp2.setFielda(temp1.getFieldA());
						temp2.setFieldb(temp1.getFieldB());
						temp2.setFieldc(temp1.getFieldC());
						temp2.setValuetype(temp1.getValueType());
						temp2.setValuelength(temp1.getValueLength());
						temp2.setValueprecision(temp1.getValuePrecision());
						temp2.setRemovedfromresult(temp1.isRemovedFromResult());
						temp2.setConversionmask(temp1.getConversionMask());
						temp2.setDecimalsymbol(temp1.getDecimalSymbol());
						temp2.setGroupingsymbol(temp1.getGroupingSymbol());
						temp2.setCurrencysymbol(temp1.getCurrencySymbol());
						return temp2;
					}).collect(Collectors.toList());
			spCalculator.setCalculation(calculationList);
		}
		return spCalculator;
	}

	/* 
	 * 
	 */
	@Override
	public void decodeParameterObject(StepMeta stepMeta, Object po, List<DatabaseMeta> databases,TransMeta transMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPCalculator spCalculator = (SPCalculator) po;
		CalculatorMeta calculatormeta = (CalculatorMeta) stepMetaInterface;

		if (spCalculator.getCalculation() != null) {
			List<CalculatorMetaFunction> calculationList = spCalculator.getCalculation().stream().map(temp1 -> {
				CalculatorMetaFunction temp2 = new CalculatorMetaFunction();
				temp2.setFieldName(temp1.getFieldname());
				temp2.setCalcType(temp1.getCalctype());
				temp2.setFieldA(temp1.getFielda());
				temp2.setFieldB(temp1.getFieldb());
				temp2.setFieldC(temp1.getFieldc());
				temp2.setValueType(temp1.getValuetype());
				temp2.setValueLength(temp1.getValuelength());
				temp2.setValuePrecision(temp1.getValueprecision());
				temp2.setRemovedFromResult(temp1.isRemovedfromresult());
				temp2.setConversionMask(temp1.getConversionmask());
				temp2.setDecimalSymbol(temp1.getDecimalsymbol());
				temp2.setGroupingSymbol(temp1.getGroupingsymbol());
				temp2.setCurrencySymbol(temp1.getCurrencysymbol());
				return temp2;
			}).collect(Collectors.toList());
			calculatormeta.setCalculation(
					calculationList.toArray(new CalculatorMetaFunction[spCalculator.getCalculation().size()]));
		}

	}

	/*
	 * 覆盖方法：getStepDataAndRelationship
	 */
	@Override
	public void getStepDataAndRelationship(TransMeta transMeta, StepMeta stepMeta, StepDataRelationship sdr) {
		
		String from= "转换:"+transMeta.getName()+",步骤:"+stepMeta.getName();
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		
		CalculatorMeta calculatormeta = (CalculatorMeta) stepMetaInterface;
		CalculatorMetaFunction[] calculationArray = calculatormeta.getCalculation();
		if (calculationArray != null) {
			Arrays.asList(calculationArray).stream()
					.forEach(temp1 -> {
						try {
							String output = temp1.getFieldName();
							String fieldA = temp1.getFieldA();
							String fieldB = temp1.getFieldB();
							String fieldC = temp1.getFieldC();
							if(!Utils.isEmpty(fieldA)) {
								sdr.addRelationship( RelationshipUtil.buildFieldRelationship(from, fieldA, output) );
							}
							if(!Utils.isEmpty(fieldB)) {
								sdr.addRelationship( RelationshipUtil.buildFieldRelationship(from, fieldB, output) );
							}
							if(!Utils.isEmpty(fieldC)) {
								sdr.addRelationship( RelationshipUtil.buildFieldRelationship(from, fieldC, output) );
							}
						} catch (Exception e) {
							relationshiplogger.error("",e);
						}
						
					});
		}

	}

}
