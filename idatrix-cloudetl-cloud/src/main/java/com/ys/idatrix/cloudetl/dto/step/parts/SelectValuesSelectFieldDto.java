/**
 * 云化数据集成系统 iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.dto.step.parts;

/**
 * SPSelectValues 的 selectFields 域,等效  org.pentaho.di.trans.steps.selectvalues.SelectValuesMeta.SelectField
 * @author JW
 * @since 2017年6月13日
 *
 */
public class SelectValuesSelectFieldDto {
	String name;
	String rename;
	int  precision=-1;
	int  length=-1;
	/**
	 * @return name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name 要设置的 name
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return rename
	 */
	public String getRename() {
		return rename;
	}
	/**
	 * @param rename 要设置的 rename
	 */
	public void setRename(String rename) {
		this.rename = rename;
	}
	/**
	 * @return precision
	 */
	public int getPrecision() {
		return precision;
	}
	/**
	 * @param precision 要设置的 precision
	 */
	public void setPrecision(int precision) {
		this.precision = precision;
	}
	/**
	 * @return length
	 */
	public int getLength() {
		return length;
	}
	/**
	 * @param length 要设置的 length
	 */
	public void setLength(int length) {
		this.length = length;
	}
	
	
}
