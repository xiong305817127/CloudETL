/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.quality.dto.codec;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;

import com.ys.idatrix.quality.dto.step.steps.StepParameter;
import com.ys.idatrix.quality.ext.PluginFactory;
import com.ys.idatrix.quality.toolkit.analyzer.trans.step.StepDataRelationship;
import com.ys.idatrix.quality.toolkit.analyzer.trans.step.StepDataRelationshipParser;

import net.sf.ezmorph.object.DateMorpher;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONUtils;

/**
 * Common portal for step parameter process.
 * @author JW
 * @since 05-12-2017
 *
 */
public class StepParameterCodec {
	
	public static final Log  logger = LogFactory.getLog("EntryParameterCodec");
	
	private static final String SP_PREFIX = "SP";
	
	public static void initParamObject(StepMeta stepMeta, String type) throws Exception {
		StepParameter sp = (StepParameter) PluginFactory.getBean(SP_PREFIX + type);
		sp.initParamObject(stepMeta);
	}
	
	public static Object parseParamObject(Object json, String type) {
		StepParameter sp = (StepParameter) PluginFactory.getBean(SP_PREFIX + type);
		return sp.getParameterObject(json);
	}
	
	public static Object encodeParamObject(StepMeta stepMeta, String type) throws Exception {
		StepParameter sp = (StepParameter) PluginFactory.getBean(SP_PREFIX + type);
		return sp.encodeParameterObject(stepMeta);
	}
	
	public static void decodeParameterObject(StepMeta stepMeta, Object po, List<DatabaseMeta> databases, TransMeta transMeta, String type) throws Exception {
		StepParameter sp = (StepParameter) PluginFactory.getBean(SP_PREFIX + type);
		sp.decodeParameterObject(stepMeta, po, databases, transMeta);
	}
	
	public static void decodeNewParameterObject(StepMeta stepMeta, List<DatabaseMeta> databases, TransMeta transMeta, String type) throws Exception {
		StepParameter sp = (StepParameter) PluginFactory.getBean(SP_PREFIX + type);
		sp.decodeParameterObject(stepMeta, sp, databases, transMeta);
	}
	
	public static void getStepDataAndRelationship(TransMeta transMeta, StepMeta stepMeta, StepDataRelationship sdr) throws Exception {
		StepDataRelationshipParser sdrParser = (StepDataRelationshipParser) PluginFactory.getBean(SP_PREFIX + stepMeta.getTypeId());
		sdrParser.getStepDataAndRelationship(transMeta, stepMeta, sdr);
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
	
	@SuppressWarnings("unused")
	private static void setDataFormat2JAVA() {
		JSONUtils.getMorpherRegistry().registerMorpher(new DateMorpher(new String[] {"yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss"}));
	}
	
}
