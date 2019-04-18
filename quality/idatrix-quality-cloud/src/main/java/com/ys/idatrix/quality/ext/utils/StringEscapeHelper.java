/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.quality.ext.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs2.FileName;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.provider.url.UrlFileNameParser;
import org.dom4j.Element;
import org.pentaho.di.core.util.Utils;

/**
 * Convert string to URL or HTML format.
 *
 * @author JW
 * @since 05-12-2017
 * 
 */
public class StringEscapeHelper {
	
	public static final Log  logger = LogFactory.getLog("StringEscapeHelper");

	public static String charset = "utf-8";
	
	public static String encode(String string) {
		if(string == null || string.length() == 0)
			return string;
		try {
			String tmp = URLEncoder.encode(string, charset);
			return tmp.replaceAll("\\+", "%20");
		} catch (UnsupportedEncodingException e) {
			logger.error(" encode 失败.",e);
			return string;
		}
	}
	
	public static String decode(String string) {
		if(string == null || string.length() == 0)
			return string;
		try {
			return URLDecoder.decode(string, charset);
		} catch (UnsupportedEncodingException e) {
			logger.error(" decode 失败.",e);
			return string;
		}
	}
	
	public static String getAsUtf8(String logText) {
		try {
			return new String(logText.getBytes("ISO-8859-1"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return logText;
		}
	}
	
	public static String getUrlPath(String incomingURL) {
		if (Utils.isEmpty(incomingURL)) {
			return "";
		}
		String path = incomingURL;
		try {
			String noVariablesURL = incomingURL.replaceAll("[${}]", "/");
			UrlFileNameParser parser = new UrlFileNameParser();
			FileName fileName = parser.parseUri(null, null, noVariablesURL);
			String root = fileName.getRootURI();
			if (noVariablesURL.startsWith(root)) {
				path = incomingURL.substring(root.length() - 1);
			}
		} catch (FileSystemException e) {
			path = null;
		}
		return path;
	}
	
	public static String getDom4jElementText(Element parent,String name) {
		Element e = parent.element( name );
		if(e != null ) {
			return e.getText();
		}
		return null;
	}
	
}
