/**
 * 云化数据集成系统 iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.dto.step.parts;

/**
 * SPNormaliser 的 normaliserFields 域,等效  org.pentaho.di.trans.steps.normaliser.NormaliserMeta.NormaliserField
 * @author JW
 * @since 2017年6月13日
 *
 */
public class NormaliserNormaliserFieldDto {
	String name;
	String value;
	String norm;

	/**
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            要设置的 name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value
	 *            要设置的 value
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @return norm
	 */
	public String getNorm() {
		return norm;
	}

	/**
	 * @param norm
	 *            要设置的 norm
	 */
	public void setNorm(String norm) {
		this.norm = norm;
	}

}
