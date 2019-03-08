/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.dto.step.steps.bigdata;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.util.OsgiBundleUtils;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ys.idatrix.cloudetl.dto.step.parts.TextFileInputFieldDto;
import com.ys.idatrix.cloudetl.dto.step.parts.TextFileInputFileDto;
import com.ys.idatrix.cloudetl.dto.step.parts.TextFileInputFilterDto;
import com.ys.idatrix.cloudetl.dto.step.steps.input.SPTextFileInput;
import com.ys.idatrix.cloudetl.ext.utils.FilePathUtil;
import com.ys.idatrix.cloudetl.ext.utils.FilePathUtil.FileType;
import com.ys.idatrix.cloudetl.ext.utils.StringEscapeHelper;
import com.ys.idatrix.cloudetl.service.trans.stepdetail.HadoopFileInputDetailService;
import com.ys.idatrix.cloudetl.toolkit.analyzer.trans.step.StepDataRelationship;
import com.ys.idatrix.cloudetl.toolkit.domain.DataNode;
import com.ys.idatrix.cloudetl.toolkit.domain.Relationship;
import com.ys.idatrix.cloudetl.toolkit.utils.DataNodeUtil;
import com.ys.idatrix.cloudetl.toolkit.utils.RelationshipUtil;

import net.sf.json.JSONObject;

/**
 * Step - HadoopFileInput.等效
 * org.pentaho.big.data.kettle.plugins.hdfs.trans.HadoopFileInputMeta
 * 
 * @author XH
 * @since 2017- 05-12
 *
 */
@Component("SPHadoopFileInputPlugin")
@Scope("prototype")
public class SPHadoopFileInput extends SPTextFileInput {

	@Autowired
	HadoopFileInputDetailService hadoopFileInputDetailService;

	@Override
	public Object getParameterObject(Object json) {
		JSONObject jsonObj = JSONObject.fromObject(json);

		Map<String, Class<?>> classMap = new HashMap<>();
		classMap.put("inputFiles", TextFileInputFileDto.class);
		classMap.put("filters", TextFileInputFilterDto.class);
		classMap.put("fields", TextFileInputFieldDto.class);

		return (SPHadoopFileInput) JSONObject.toBean(jsonObj, SPHadoopFileInput.class, classMap);
	}

	@Override
	public Object encodeParameterObject(StepMeta stepMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();

		SPTextFileInput obj = (SPTextFileInput) super.encodeParameterObject(stepMeta);
		for (TextFileInputFileDto file : obj.getInputFiles()) {

			file.setSourceConfigurationName((String) OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface,
					"getClusterNameBy", file.getFileName()));
			file.setFileName(StringEscapeHelper.getUrlPath(file.getFileName()));
		}

		return obj;
	}

	@Override
	public void decodeParameterObject(StepMeta stepMeta, Object po, List<DatabaseMeta> databases,TransMeta transMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		super.decodeParameterObject(stepMeta, po, databases, transMeta);

		SPHadoopFileInput jtfi = (SPHadoopFileInput) po;
		for (int i = 0; i < jtfi.getInputFiles().size(); i++) {
			TextFileInputFileDto file = jtfi.getInputFiles().get(i);
			file.setFileName(FilePathUtil.getRelativeFileName(null ,file.getFileName(), FileType.input));

			String hadoopFileUrl = hadoopFileInputDetailService.getConnectPath(null,file.getSourceConfigurationName(),
					file.getFileName());

			Object map = OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getNamedClusterURLMapping");
			OsgiBundleUtils.invokeOsgiMethod(map, "put", hadoopFileUrl, file.getSourceConfigurationName());

			Object metaInputFiles = OsgiBundleUtils.getOsgiField(stepMetaInterface, "inputFiles", false);
			Object fileNameArr = OsgiBundleUtils.getOsgiField(metaInputFiles, "fileName", false);

			Class<?> array = stepMetaInterface.getClass().getClassLoader().loadClass("java.lang.reflect.Array");
			array.getMethod("set", Object.class, int.class, Object.class).invoke(null, fileNameArr, i, hadoopFileUrl);

		}

	}

	@Override
	public void getStepDataAndRelationship(TransMeta transMeta, StepMeta stepMeta, StepDataRelationship sdr)
			throws Exception {

		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();

		Object metaInputFiles = OsgiBundleUtils.getOsgiField(stepMetaInterface, "inputFiles", false);
		Object[] fileNameArr = (Object[]) OsgiBundleUtils.getOsgiField(metaInputFiles, "fileName", false);
		if (null == fileNameArr) {
			return;
		}
		Object content = OsgiBundleUtils.getOsgiField(stepMetaInterface, "content", false);
		//String fileType = Const.NVL((String) OsgiBundleUtils.getOsgiField(content, "fileType", false), "");
		//String fileFormat = Const.NVL((String) OsgiBundleUtils.getOsgiField(content, "fileFormat", false), "");
		String encoding = Const.NVL((String) OsgiBundleUtils.getOsgiField(content, "encoding", false), "");
		String fileCompression = Const.NVL((String) OsgiBundleUtils.getOsgiField(content, "fileCompression", false), "");

		for (Object name : fileNameArr) {
			String	fileName = (String) name;
			if (StringUtils.isNotEmpty(fileName)) {
				
				String cfg = (String) OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getClusterNameBy", fileName);
				String hadoopFileUrl = hadoopFileInputDetailService.getConnectPath(null,cfg, fileName);
				
				DataNode fileDataNode = DataNodeUtil.fileNodeParse("HDFS", hadoopFileUrl, encoding, fileCompression, "" ) ;
				sdr.addInputDataNode(fileDataNode);
				
				// 增加 系统节点 和 流节点的关系
				String from = "转换:" + transMeta.getName() + ",步骤:" + stepMeta.getName();
				List<Relationship> relationships = RelationshipUtil.inputStepRelationship(null, fileDataNode, sdr.getOutputStream(), stepMeta.getName(), from);
				sdr.getDataRelationship().addAll(relationships);
			}
		}

	}

}
