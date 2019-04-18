package com.ys.idatrix.cloudetl.dto.step.parts;

/**
 * SPSwitchCase 的caseTargets域 ,等效 org.pentaho.di.trans.steps.switchcase.SwitchCaseTarget
 *
 * @author XH
 * @since 2017年9月5日
 *
 */
public class SwitchCaseTargetDto {
	
	String caseValue;
	String caseTargetStep;
	/**
	 * @return the caseValue
	 */
	public String getCaseValue() {
		return caseValue;
	}
	/**
	 * @param  设置 caseValue
	 */
	public void setCaseValue(String caseValue) {
		this.caseValue = caseValue;
	}
	/**
	 * @return the caseTargetStep
	 */
	public String getCaseTargetStep() {
		return caseTargetStep;
	}
	/**
	 * @param  设置 caseTargetStep
	 */
	public void setCaseTargetStep(String caseTargetStep) {
		this.caseTargetStep = caseTargetStep;
	}
	
}
