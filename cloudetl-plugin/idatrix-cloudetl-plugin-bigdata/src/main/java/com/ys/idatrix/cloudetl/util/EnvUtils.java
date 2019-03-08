/**
 * 云化数据集成系统 
 * iDatrxi CloudETL
 */
package com.ys.idatrix.cloudetl.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.apache.commons.vfs2.FileObject;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleFileException;
import org.pentaho.di.core.plugins.PluginInterface;
import org.pentaho.di.core.util.OsgiBundleUtils;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.core.vfs.KettleVFS;

import com.ys.idatrix.cloudetl.security.HadoopSecurityManagerException;
import com.ys.idatrix.cloudetl.security.IdatrixSecurityManager;

/**
 * IDatrix security plug-in properties utilities.
 * EnvUtils <br/>
 * @author XH
 * @since 2017年11月10日
 *
 */
public class EnvUtils {

	public static final String CONFIG_PATH_ROOT_KEY="security.config.file.path.root";

	public static boolean isiDatrix() throws HadoopSecurityManagerException{
		PropertiesConfiguration properties = IdatrixSecurityManager.getInstance().getProperties();
		return Boolean.valueOf( EnvUtils.getVlaueByProperties(properties, "idatrix.security.metaCube","iDatrix".equals(System.getProperty("metaCube.category"))+"" )) ;
	}

	public static String getUserId() {
		return Utils.getCloudloginUser();
	}

	public static String getProperties(PropertiesConfiguration properties,String key){
		return getProperties(properties, key, null);
	}

	public static String getProperties(PropertiesConfiguration properties,String key,String def){
		if(properties!=null){
			return properties.getString(key, def);
		}
		
		return def;
	}

	public static String getVlaueByProperties(PropertiesConfiguration properties,String key,String def){
		String val = System.getProperty(key);
		if(Utils.isEmpty(val)){
			val=getProperties(properties, key, def);
		}
		
		return val;
	}

	public static String getPathVlaueByProperties(PropertiesConfiguration properties,String key,String def){
		String value = getVlaueByProperties(properties,key,def);
		if(!Utils.isEmpty( value ) && !value.startsWith("/")){
			String configpathRoot = getVlaueByProperties(properties,CONFIG_PATH_ROOT_KEY);
			if( !Utils.isEmpty( configpathRoot ) ){
				if( configpathRoot.endsWith("/")){
					return configpathRoot+value;
				}else{
					return configpathRoot+Const.FILE_SEPARATOR +value;
				}
			}
		}
		
		return value;
	}

	public static String getVlaueByProperties(PropertiesConfiguration properties,String key){
		return getVlaueByProperties(properties, key, "");
	}

	public static String getPathVlaueByProperties(PropertiesConfiguration properties,String key){
		return getPathVlaueByProperties(properties, key, "");
	}

	public static void addPluginPathToClassLoader( PluginInterface plugin,  ClassLoader classLoader) throws MalformedURLException {
		if ( plugin == null ) {
			throw new NullPointerException();
		}
		
		if (classLoader == null) {
			classLoader = EnvUtils.class.getClassLoader();
		}
		
		URL parentPath =new URL("file:/"+plugin.getPluginDirectory().getPath()+"/" );
		OsgiBundleUtils.invokeOsgiMethod(classLoader, "addURL", parentPath);
	}


	public static PropertiesConfiguration loadProperties(String classPath , String relativeName ) throws IOException, KettleFileException  {
		FileObject propFile = KettleVFS.getFileObject( classPath + Const.FILE_SEPARATOR + relativeName );
		if ( !propFile.exists() ) {
			throw new FileNotFoundException( propFile.toString() );
		}

		try {
			PropertiesConfiguration.setDefaultListDelimiter('`');
			PropertiesConfiguration propertiesConfiguration = new PropertiesConfiguration( propFile.getURL() );
			propertiesConfiguration.setAutoSave( true );
			FileChangedReloadingStrategy fileChangedReloadingStrategy = new FileChangedReloadingStrategy();
			fileChangedReloadingStrategy.setRefreshDelay( 1000L );
			propertiesConfiguration.setReloadingStrategy( fileChangedReloadingStrategy );
			propFile.close();

			if(!propertiesConfiguration.containsKey(CONFIG_PATH_ROOT_KEY)){
				propertiesConfiguration.addProperty(CONFIG_PATH_ROOT_KEY, classPath + Const.FILE_SEPARATOR );
			}

			return propertiesConfiguration;
		} catch ( ConfigurationException e ) {
			throw new IOException( e );
		}
	}

}
