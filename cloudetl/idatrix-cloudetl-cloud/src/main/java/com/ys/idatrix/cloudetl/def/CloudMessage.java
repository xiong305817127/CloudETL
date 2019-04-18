/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.def;

import org.pentaho.di.i18n.BaseMessages;

/**
 * 系统本地化消息接口<br/>
 * @author JW
 * @since 05-12-2017
 *
 */
public class CloudMessage {
	
	private static Class<?> PKG = CloudMessage.class;
	
	/**
	 * Get real locale message per key.
	 * @param msgKey
	 * @param msgParams
	 * @return
	 */
	public static String get(String key, Object... params) {
		return BaseMessages.getString(PKG, key, new Object[] {params});
	}
	
	/**
	 * Get real locale message per key & string parameters.
	 * @param key
	 * @param params
	 * @return
	 */
	public static String get(String key, String... params) {
		return BaseMessages.getString(PKG, key, params);
	}

}
