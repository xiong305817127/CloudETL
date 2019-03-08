/**
 * 云化数据集成系统 
 * iDatrxi CloudETL
 */
package com.ys.idatrix.cloudetl.toolkit.domain.property;

import java.io.Serializable;

/**
 * DummyProperty <br/>
 * @author JW
 * @since 2018年1月22日
 * 
 */
public class DummyProperty  extends BaseProperty  implements Serializable{

	private static final long serialVersionUID = -2167097765365868561L;

	// Flag - 标志：Dummy, Temporal, LoopBack, Variable
	private String flag;
	
	// Generator - 变量生成表达式定义
	private String generator;
	
	private String reason;
	
	private String reference;


	public DummyProperty(String name) {
		super(name);
	}

	
	/**
	 * @return flag
	 */
	public String getFlag() {
		return flag;
	}

	/**
	 * @param flag 要设置的 flag
	 */
	public void setFlag(String flag) {
		this.flag = flag;
	}

	/**
	 * @return generator
	 */
	public String getGenerator() {
		return generator;
	}

	/**
	 * @param generator 要设置的 generator
	 */
	public void setGenerator(String generator) {
		this.generator = generator;
	}

	/**
	 * @return reason
	 */
	public String getReason() {
		return reason;
	}

	/**
	 * @param reason 要设置的 reason
	 */
	public void setReason(String reason) {
		this.reason = reason;
	}

	/**
	 * @return reference
	 */
	public String getReference() {
		return reference;
	}

	/**
	 * @param reference 要设置的 reference
	 */
	public void setReference(String reference) {
		this.reference = reference;
	}

}
