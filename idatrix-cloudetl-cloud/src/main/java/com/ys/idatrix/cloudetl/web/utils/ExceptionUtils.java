/**
 * 云化数据集成系统
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.web.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 *
 * @author JW
 * @since 05-12-2017
 * 
 */
public class ExceptionUtils {

	public static String toString(Exception e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		return sw.toString();
	}
	
}
