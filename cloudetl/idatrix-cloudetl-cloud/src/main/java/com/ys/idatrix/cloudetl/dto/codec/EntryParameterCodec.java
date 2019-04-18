/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.dto.codec;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.job.entry.JobEntryCopy;
import com.ys.idatrix.cloudetl.dto.entry.entries.EntryParameter;
import com.ys.idatrix.cloudetl.ext.PluginFactory;

import net.sf.json.JSONObject;

/**
 * EntryParameterCodec <br/>
 * Common portal for job parameter process. <br/>
 * @author JW
 * @since 2017年5月17日
 *
 */
public class EntryParameterCodec {
	
	public static final Log  logger = LogFactory.getLog("EntryParameterCodec");
	
	private static final String SP_PREFIX = "SP";
	
	public static Object parseParamObject(Object json, String type) {
		EntryParameter sp = (EntryParameter) PluginFactory.getBean(SP_PREFIX + type.toLowerCase());
		return sp.getParameterObject(json);
	}
	
	public static Object encodeParamObject(JobEntryCopy jobEntryInterface, String type) throws Exception {
		EntryParameter sp = (EntryParameter) PluginFactory.getBean(SP_PREFIX + type.toLowerCase());
		return sp.encodeParameterObject(jobEntryInterface);
	}
	
	public static void decodeParameterObject(JobEntryCopy jobEntryInterface, Object po,  JobMeta jobMeta, String type) throws Exception {
		EntryParameter sp = (EntryParameter) PluginFactory.getBean(SP_PREFIX + type.toLowerCase());
		sp.decodeParameterObject(jobEntryInterface, po,  jobMeta);
	}
	
	/**
	 * @deprecated - unused in future.
	 * @param type
	 * @param stepParams
	 * @return
	 */
	public static Object getParamObject(Object stepParams, String type) {
    	return stepParams;
	}
	
	
	public static Object getDto(Object json, Class<?> clazz) {
		JSONObject jsonObject = null;
		try {
			//setDataFormat2JAVA();
			jsonObject = JSONObject.fromObject(json);
		} catch (Exception e) {
			logger.error("",e);
		}
		return JSONObject.toBean(jsonObject, clazz);
	}
	
}
