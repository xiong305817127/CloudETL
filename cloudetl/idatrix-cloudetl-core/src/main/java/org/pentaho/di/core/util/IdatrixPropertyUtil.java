/**
 * GDBD iDatrix CloudETL System.
 */
package org.pentaho.di.core.util;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.di.core.Const;

/**
 *
 * @author XH
 * @since 2017年7月7日
 *
 */
public class IdatrixPropertyUtil  {

	public static final Log  log = LogFactory.getLog("idatrixProperty");
	private static Map<String, String> propertyMap= new HashMap<String, String>();;

	
	public static String getPropertyFromDatabase( String key ) {
		Object instance = OsgiBundleUtils.invokeOsgiMethod(Utils.getPackageName("com.ys.idatrix.cloudetl.repository.database.SystemSettingDao"), "getInstance");
		if( instance != null ) {
			String v = (String)OsgiBundleUtils.invokeOsgiMethod(instance, "getSettingValue", "0",key);
			if(!Utils.isEmpty(v)) {
				System.setProperty(key, v);
				return  v ;
			}
		}
		return null ;
	}
	
	/**
	 * 获取key对应的value,如果value为空则返回defaultVal
	 * @param key
	 * @param defaultVal
	 * @return
	 */
	public static String getProperty(String key , String defaultVal) {
		String val = getProperty(key);
		return  Utils.isEmpty(val) ? defaultVal : val;
	}

	/**
	 * 获取 key对应的value值
	 * @param key
	 * @return
	 */
	public static String getProperty(String key) {
		if( !isLoaded){
			loadProperty();
		}
		return System.getProperty(key, propertyMap.get(key));
	}
	
	/**
	 * 获取key对应的value,如果value为空则返回defaultVal
	 * @param key
	 * @param defaultVal
	 * @return
	 */
	public static Boolean getBooleanProperty(String key , Boolean defaultVal) {
		String val = getProperty(key);
		return  Utils.isEmpty(val) ? defaultVal : Boolean.valueOf(val); 
	}

	/**
	 * 获取 key对应的value值
	 * @param key
	 * @return
	 */
	public static Boolean getBooleanProperty(String key) {
		String val = getProperty(key);
		return  Boolean.valueOf(val) ;
	}
	
	/**
	 * 将获取到的值包含 的${user}替换为当前用户名 ,eg.用户root的字符串: /path/${user}/sub${user}Path 替换为: /path/root/subrootPath
	 * @param key
	 * @return
	 */
	public static String getPropertyByFormatUser(String key,String user) {
		String value = getProperty(key);
		if( !Utils.isEmpty(value) && value.contains("${user}")){
			String userName =  Const.NVL(user, Utils.getCloudResourceUser() );
			value=value.replaceAll("\\$\\{user\\}", userName);
		}
		if( !Utils.isEmpty(value) && value.contains("${renterId}")){
			String renterId =  (String) OsgiBundleUtils.invokeOsgiMethod(Utils.getCloudSession(), "getLoginRenterId"); 
			if( !Utils.isEmpty( renterId ) ) {
				value=value.replaceAll("\\$\\{renterId\\}", renterId);
			}
		}
		return value;
	}
	
	private static boolean isLoaded=false;
	public static void loadProperty() {
		try {
			InputStream in = IdatrixPropertyUtil.class.getClassLoader().getResourceAsStream("file:./config/idatrix.properties");
			if(in == null) {
				in = IdatrixPropertyUtil.class.getClassLoader().getResourceAsStream("config/idatrix.properties");
			}
			if(in == null) {
				in = IdatrixPropertyUtil.class.getClassLoader().getResourceAsStream("idatrix.properties");
			}
			Properties p = new Properties();
			p.load(in);
			isLoaded=true;
			setPropertyToMap(p);
		} catch (Exception e) {
			log.error("loading local config/idatrix.properties file failed : ",e);
		}
	}
	
	private static void setPropertyToMap(Properties p ){
		
		for (Object key : p.keySet()) {
			String keyStr = key.toString();
			String value = p.getProperty(keyStr);
			propertyMap.put(keyStr, value);
			if(!System.getProperties().containsKey(keyStr)){
				System.setProperty(keyStr, value);
			}
		}
		
	}

}