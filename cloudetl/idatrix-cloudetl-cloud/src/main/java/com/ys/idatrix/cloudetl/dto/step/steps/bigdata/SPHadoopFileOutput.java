/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.dto.step.steps.bigdata;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.util.OsgiBundleUtils;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ys.idatrix.cloudetl.dto.step.parts.TextFileFieldDto;
import com.ys.idatrix.cloudetl.dto.step.steps.output.SPTextFileOutput;
import com.ys.idatrix.cloudetl.ext.utils.StringEscapeHelper;
import com.ys.idatrix.cloudetl.service.trans.stepdetail.HadoopFileInputDetailService;
import com.ys.idatrix.cloudetl.toolkit.analyzer.trans.step.StepDataRelationship;
import net.sf.json.JSONObject;

/**
 * Step - HadoopFileOutput. 等效
 * org.pentaho.big.data.kettle.plugins.hdfs.trans.HadoopFileOutputMeta
 *
 * @author XH
 * @since 2017-05-12
 *
 */
@Component("SPHadoopFileOutputPlugin")
@Scope("prototype")
public class SPHadoopFileOutput extends SPTextFileOutput {

	@Autowired
	HadoopFileInputDetailService hadoopFileInputDetailService;
	
	String sourceConfigurationName;

	/**
	 * @return sourceConfigurationName
	 */
	public String getSourceConfigurationName() {
		return sourceConfigurationName;
	}

	/**
	 * @param sourceConfigurationName
	 *            要设置的 sourceConfigurationName
	 */
	public void setSourceConfigurationName(String sourceConfigurationName) {
		this.sourceConfigurationName = sourceConfigurationName;
	}

	/* 
	 * 
	 */
	@Override
	public Object getParameterObject(Object json) {
		JSONObject jsonObj = JSONObject.fromObject(json);

		Map<String, Class<?>> classMap = new HashMap<>();
		classMap.put("fields", TextFileFieldDto.class);

		return (SPHadoopFileOutput) JSONObject.toBean(jsonObj, SPHadoopFileOutput.class, classMap);
	}

	@Override
	public Object encodeParameterObject(StepMeta stepMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();

		SPTextFileOutput obj = (SPTextFileOutput) super.encodeParameterObject(stepMeta);

		ObjectMapper mapper = new ObjectMapper();
		SPHadoopFileOutput tfo = null;
		tfo = mapper.readValue(mapper.writeValueAsString(obj), SPHadoopFileOutput.class);
		String sourceConfigurationName = (String) OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getSourceConfigurationName");
		tfo.setSourceConfigurationName(sourceConfigurationName);
		tfo.setFileName(StringEscapeHelper.getUrlPath((String) OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getFileName")));

		return tfo;
	}

	@Override
	public void decodeParameterObject(StepMeta stepMeta, Object po, List<DatabaseMeta> databases,TransMeta transMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		super.decodeParameterObject(stepMeta, po, databases, transMeta);

		SPHadoopFileOutput jtfo = (SPHadoopFileOutput) po;
		// hadoopFileOutputMeta.setSourceConfigurationName(jtfo.getSourceConfigurationName());
		OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "setSourceConfigurationName", new Object[] { jtfo.getSourceConfigurationName() }, new Class<?>[] { String.class });
		String hadoopFileUrl = hadoopFileInputDetailService.getConnectPath(null,jtfo.getSourceConfigurationName(), jtfo.getFileName());
		OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "setFileName", hadoopFileUrl);

	}
	
	@Override
	public void getStepDataAndRelationship(TransMeta transMeta, StepMeta stepMeta, StepDataRelationship sdr) throws Exception {
		
		
		String sourceConfigurationName = (String) OsgiBundleUtils.invokeOsgiMethod(stepMeta.getStepMetaInterface(), "getSourceConfigurationName");
		String fileName =  (String) OsgiBundleUtils.invokeOsgiMethod(stepMeta.getStepMetaInterface(), "getFileName") ;
		String hadoopFileUrl = hadoopFileInputDetailService.getConnectPath(null,sourceConfigurationName, fileName);
		
		getStepDataAndRelationship(transMeta, stepMeta, sdr, "HDFS", hadoopFileUrl);
	}

}
