/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.ext.utils;

/**
 * UnixPathUtil.java
 * @author JW
 * @since 2017年8月3日
 *
 */
public class UnixPathUtil {

	public static String unixPath(String path, String... subDirs) {
		
		StringBuilder sb = new StringBuilder(unixPath2(path));
		if (!path.endsWith("/")) {
			sb.append("/");
		}
		if(subDirs != null ) {
			for (String dir : subDirs) {
				sb.append(dir);
				sb.append("/");
			}
		}

		return sb.toString();
	}
	

	public static String unixPath2(String path) {
		
		String prefix = "";
		int index = path.indexOf("://");
		if(index > -1 ) {
			prefix = path.substring(0,index+3);
			path= path.substring(index+3);
		}
		path = path.replaceAll("\\\\", "/");
		while (path.contains("//")) {
			path = path.replace("//", "/");
		}
		
		StringBuilder sb = new StringBuilder(prefix);
		sb.append(path) ;
		
		return sb.toString();
	}
	
}
