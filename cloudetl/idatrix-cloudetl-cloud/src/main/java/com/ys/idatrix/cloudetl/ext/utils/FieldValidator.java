/**
 * 云化数据集成系统 
 * iDatrxi CloudETL
 */
package com.ys.idatrix.cloudetl.ext.utils;

/**
 * 字段值有效性检查 <br/>
 * FieldValidator <br/>
 * @author JW
 * @since 2017年11月1日
 * 
 */
public class FieldValidator {
	
	public final static int MIN_LEN = -1;
	public final static int MAX_LEN = 65535;
	public final static int DEFAULT_LEN = 10;
	
	public final static int MIN_PRECISION = -1;
	public final static int MAX_PRECISION = 255;
	public final static int DEFAULT_PRECISION = 0;
	
	
	public static boolean checkLength(int len) {
		if (len > MAX_LEN || len < MIN_LEN) {
			return false;
		}
		return true;
	}
	
	public static int fixedLength(int len) {
		if (len > MAX_LEN) {
			return MAX_LEN;
		} else if (len < MIN_LEN) {
			return MIN_LEN;
		}
		return len;
	}
	
	public static boolean checkPrecision(int precision) {
		if (precision > MAX_PRECISION || precision < MIN_PRECISION) {
			return false;
		}
		return true;
	}
	
	public static int fixedPrecision(int precision) {
		if (precision > MAX_PRECISION) {
			return MAX_PRECISION;
		} else if (precision < MIN_PRECISION) {
			return MIN_PRECISION;
		}
		return precision;
	}

}
