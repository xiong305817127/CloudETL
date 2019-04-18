package com.ys.idatrix.quality.dto.step.steps.bigdata;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.vfs2.FileObject;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.util.OsgiBundleUtils;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.core.vfs.KettleVFS;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.ys.idatrix.quality.dto.step.parts.ParquetFormatInputOutputFieldDto;
import com.ys.idatrix.quality.dto.step.steps.StepParameter;
import com.ys.idatrix.quality.ext.utils.StringEscapeHelper;
import com.ys.idatrix.quality.recovery.trans.ResumeStepDataParser;
import com.ys.idatrix.quality.service.trans.stepdetail.HadoopFileInputDetailService;
import com.ys.idatrix.quality.toolkit.analyzer.trans.step.StepDataRelationship;
import com.ys.idatrix.quality.toolkit.analyzer.trans.step.StepDataRelationshipParser;
import com.ys.idatrix.quality.toolkit.domain.DataNode;
import com.ys.idatrix.quality.toolkit.domain.Relationship;
import com.ys.idatrix.quality.toolkit.utils.DataNodeUtil;
import com.ys.idatrix.quality.toolkit.utils.RelationshipUtil;

import net.sf.json.JSONObject;

/**
 * Step - ParquetOutput. 转换
 * org.pentaho.big.data.kettle.plugins.formats.impl.parquet.output.ParquetOutputMeta
 * 
 * @author XH
 * @since 2018-08-21
 */
@Component("SPParquetOutput")
@Scope("prototype")
public class SPParquetOutput implements StepParameter , StepDataRelationshipParser,ResumeStepDataParser{
	
	@Autowired
	HadoopFileInputDetailService hadoopFileInputDetailService;

	String sourceConfigurationName;
	String filename;
	
	boolean overrideOutput;
	String compressionType;
	String parquetVersion;
	boolean enableDictionary;
	String dictPageSize;
	String rowGroupSize;
	String dataPageSize;
	String extension;
	boolean dateInFilename;
	boolean timeInFilename;
	String dateTimeFormat;
	
	List<ParquetFormatInputOutputFieldDto> outputFields;

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getSourceConfigurationName() {
		return sourceConfigurationName;
	}

	public void setSourceConfigurationName(String sourceConfigurationName) {
		this.sourceConfigurationName = sourceConfigurationName;
	}

	public boolean isOverrideOutput() {
		return overrideOutput;
	}

	public void setOverrideOutput(boolean overrideOutput) {
		this.overrideOutput = overrideOutput;
	}

	public String getCompressionType() {
		return compressionType;
	}

	public void setCompressionType(String compressionType) {
		this.compressionType = compressionType;
	}

	public String getParquetVersion() {
		return parquetVersion;
	}

	public void setParquetVersion(String parquetVersion) {
		this.parquetVersion = parquetVersion;
	}

	public boolean isEnableDictionary() {
		return enableDictionary;
	}

	public void setEnableDictionary(boolean enableDictionary) {
		this.enableDictionary = enableDictionary;
	}

	public String getDictPageSize() {
		return dictPageSize;
	}

	public void setDictPageSize(String dictPageSize) {
		this.dictPageSize = dictPageSize;
	}

	public String getRowGroupSize() {
		return rowGroupSize;
	}

	public void setRowGroupSize(String rowGroupSize) {
		this.rowGroupSize = rowGroupSize;
	}

	public String getDataPageSize() {
		return dataPageSize;
	}

	public void setDataPageSize(String dataPageSize) {
		this.dataPageSize = dataPageSize;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public boolean isDateInFilename() {
		return dateInFilename;
	}

	public void setDateInFilename(boolean dateInFilename) {
		this.dateInFilename = dateInFilename;
	}

	public boolean isTimeInFilename() {
		return timeInFilename;
	}

	public void setTimeInFilename(boolean timeInFilename) {
		this.timeInFilename = timeInFilename;
	}

	public String getDateTimeFormat() {
		return dateTimeFormat;
	}

	public void setDateTimeFormat(String dateTimeFormat) {
		this.dateTimeFormat = dateTimeFormat;
	}

	public List<ParquetFormatInputOutputFieldDto> getOutputFields() {
		return outputFields;
	}

	public void setOutputFields(List<ParquetFormatInputOutputFieldDto> outputFields) {
		this.outputFields = outputFields;
	}

	@Override
	public Object getParameterObject(Object json) {
		JSONObject jsonObj = JSONObject.fromObject(json);
		Map<String, Class<?>> classMap = new HashMap<>();
		classMap.put("outputFields", ParquetFormatInputOutputFieldDto.class);
		return (SPParquetOutput) JSONObject.toBean(jsonObj, SPParquetOutput.class, classMap);
	}

	@Override
	public Object encodeParameterObject(StepMeta stepMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPParquetOutput spParquetOutput= new SPParquetOutput();
		//ParquetOutputMeta parquetoutputmeta= (ParquetOutputMeta )stepMetaInterface;

		//String hadoopFileUrl =parquetoutputmeta.filename;
		String hadoopFileUrl = (String) OsgiBundleUtils.getOsgiField(stepMetaInterface, "filename", false);
		//获取sourceConfigurationName
		spParquetOutput.setSourceConfigurationName( getToAttribute(stepMeta, "filename_ConfigurationName" ) );
		spParquetOutput.setFilename( StringEscapeHelper.getUrlPath(hadoopFileUrl)) ;
		
		spParquetOutput.setOverrideOutput( (boolean)OsgiBundleUtils.getOsgiField(stepMetaInterface, "overrideOutput", false));//parquetoutputmeta.overrideOutput) ;
		spParquetOutput.setCompressionType( (String)OsgiBundleUtils.getOsgiField(stepMetaInterface, "compressionType", false));//parquetoutputmeta.compressionType) ;
		spParquetOutput.setParquetVersion( (String)OsgiBundleUtils.getOsgiField(stepMetaInterface, "parquetVersion", false));//parquetoutputmeta.parquetVersion) ;
		spParquetOutput.setEnableDictionary( (boolean)OsgiBundleUtils.getOsgiField(stepMetaInterface, "enableDictionary", false));//parquetoutputmeta.enableDictionary) ;
		spParquetOutput.setDictPageSize( (String)OsgiBundleUtils.getOsgiField(stepMetaInterface, "dictPageSize", false));//parquetoutputmeta.dictPageSize) ;
		spParquetOutput.setRowGroupSize( (String)OsgiBundleUtils.getOsgiField(stepMetaInterface, "rowGroupSize", false));//parquetoutputmeta.rowGroupSize) ;
		spParquetOutput.setDataPageSize((String)OsgiBundleUtils.getOsgiField(stepMetaInterface, "dataPageSize", false));// parquetoutputmeta.dataPageSize) ;
		spParquetOutput.setExtension( (String)OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getExtension"));//parquetoutputmeta.getExtension()) ;
		spParquetOutput.setDateInFilename( (boolean)OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "isDateInFilename"));//parquetoutputmeta.isDateInFilename()) ;
		spParquetOutput.setTimeInFilename( (boolean)OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "isTimeInFilename"));//parquetoutputmeta.isTimeInFilename()) ;
		spParquetOutput.setDateTimeFormat((String)OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getDateTimeFormat"));// parquetoutputmeta.getDateTimeFormat()) ;
		
		//FormatInputOutputField[] fields = parquetoutputmeta.outputFields;
		Object[] fields =  (Object[])OsgiBundleUtils.getOsgiField(stepMetaInterface, "outputFields", false);
		if( fields != null && fields.length >0) {
			List<ParquetFormatInputOutputFieldDto> pfiofs = Lists.newArrayList() ;
			for(int i=0 ;i< fields.length;i++) {
				ParquetFormatInputOutputFieldDto pfiof = new ParquetFormatInputOutputFieldDto();
				Object field = fields[i];
				
				pfiof.setPath( (String)OsgiBundleUtils.invokeOsgiMethod(field, "getPath"));// field.getPath() );
				pfiof.setName( (String)OsgiBundleUtils.invokeOsgiMethod(field, "getName"));// field.getName() );
				pfiof.setType( (String)OsgiBundleUtils.invokeOsgiMethod(field, "getTypeDesc"));// field.getTypeDesc() );
				pfiof.setNullable( (boolean)OsgiBundleUtils.invokeOsgiMethod(field, "isNullable"));//field.isNullable() );
				pfiof.setIfNullValue( (String)OsgiBundleUtils.invokeOsgiMethod(field, "getIfNullValue"));// field.getIfNullValue() );
				pfiofs.add(pfiof);
			}
		
			spParquetOutput.setOutputFields( pfiofs ) ;
		}
		
		return spParquetOutput;
	}

	@Override
	public void decodeParameterObject(StepMeta stepMeta, Object po, List<DatabaseMeta> databases,TransMeta transMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPParquetOutput spParquetOutput= (SPParquetOutput)po;
		//ParquetOutputMeta  parquetoutputmeta= (ParquetOutputMeta )stepMetaInterface;

		String hadoopFileUrl ="" ;
		if(!Utils.isEmpty(spParquetOutput.getFilename())) {
			if( !Utils.isEmpty(spParquetOutput.getSourceConfigurationName()) && !"local".equalsIgnoreCase(spParquetOutput.getSourceConfigurationName())) {
				hadoopFileUrl = hadoopFileInputDetailService.getConnectPath(null, spParquetOutput.getSourceConfigurationName(), spParquetOutput.getFilename());
			}else {
				FileObject loaclfile = KettleVFS.getFileObject(spParquetOutput.getFilename());
				hadoopFileUrl = loaclfile.getURL().toString() ;
				loaclfile.close();
			}
		}
		
		//parquetoutputmeta.filename = hadoopFileUrl ;
		OsgiBundleUtils.setOsgiField(stepMetaInterface, "filename", hadoopFileUrl, false);
		//保存sourceConfigurationName
		setToAttribute(stepMeta,  "filename_ConfigurationName", spParquetOutput.getSourceConfigurationName());
		
		//parquetoutputmeta.overrideOutput = spParquetOutput.isOverrideOutput() ;
		OsgiBundleUtils.setOsgiField(stepMetaInterface, "overrideOutput", spParquetOutput.isOverrideOutput(), false);
		//parquetoutputmeta.compressionType = spParquetOutput.getCompressionType() ;
		OsgiBundleUtils.setOsgiField(stepMetaInterface, "compressionType", spParquetOutput.getCompressionType(), false);
		//parquetoutputmeta.parquetVersion = spParquetOutput.getParquetVersion() ;
		OsgiBundleUtils.setOsgiField(stepMetaInterface, "parquetVersion", spParquetOutput.getParquetVersion(), false);
		//parquetoutputmeta.enableDictionary = spParquetOutput.isEnableDictionary() ;
		OsgiBundleUtils.setOsgiField(stepMetaInterface, "enableDictionary", spParquetOutput.isEnableDictionary(), false);
		//parquetoutputmeta.dictPageSize = spParquetOutput.getDictPageSize() ;
		OsgiBundleUtils.setOsgiField(stepMetaInterface, "dictPageSize", spParquetOutput.getDictPageSize(), false);
		//parquetoutputmeta.rowGroupSize = spParquetOutput.getRowGroupSize() ;
		OsgiBundleUtils.setOsgiField(stepMetaInterface, "rowGroupSize", spParquetOutput.getRowGroupSize(), false);
		//parquetoutputmeta.dataPageSize = spParquetOutput.getDataPageSize() ;
		OsgiBundleUtils.setOsgiField(stepMetaInterface, "dataPageSize", spParquetOutput.getDataPageSize(), false);
		//parquetoutputmeta.setExtension(spParquetOutput.getExtension() );
		OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "setExtension", spParquetOutput.getExtension());
		//parquetoutputmeta.setDateInFilename( spParquetOutput.isDateInFilename()) ;
		OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "setDateInFilename", spParquetOutput.isDateInFilename());
		//parquetoutputmeta.setTimeInFilename( spParquetOutput.isTimeInFilename()) ;
		OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "setTimeInFilename", spParquetOutput.isTimeInFilename());
		//parquetoutputmeta.setDateTimeFormat( spParquetOutput.getDateTimeFormat() );
		OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "setDateTimeFormat", spParquetOutput.getDateTimeFormat());
		
		List<ParquetFormatInputOutputFieldDto> pfiofs = spParquetOutput.getOutputFields() ;
		if( pfiofs!= null && pfiofs.size() >0) {
			//FormatInputOutputField[] fields = new FormatInputOutputField[pfiofs.size()];
			 List<Object> fields = Lists.newArrayList();;
			for(int i=0;i< pfiofs.size();i++) {
				ParquetFormatInputOutputFieldDto pfiof = pfiofs.get(i);
				//FormatInputOutputField field = new FormatInputOutputField();
				Object field = OsgiBundleUtils.newOsgiInstance(stepMetaInterface, "org.pentaho.big.data.kettle.plugins.formats.FormatInputOutputField");
				
				//field.setPath( pfiof.getPath() );
				OsgiBundleUtils.invokeOsgiMethod(field, "setPath", pfiof.getPath());
				//field.setName( pfiof.getName() );
				OsgiBundleUtils.invokeOsgiMethod(field, "setName", pfiof.getName());
				//field.setType( pfiof.getType() );
				OsgiBundleUtils.invokeOsgiMethod(field, "setType", pfiof.getType());
				//field.setNullable(pfiof.isNullable() );
				OsgiBundleUtils.invokeOsgiMethod(field, "setNullable", pfiof.isNullable());
				//field.setIfNullValue( pfiof.getIfNullValue() );
				OsgiBundleUtils.invokeOsgiMethod(field, "setIfNullValue", pfiof.getIfNullValue());
				
				//fields[i] = field;
				fields.add(field);
			}
			
			//parquetoutputmeta.outputFields = fields;
			Object[] arrs = (Object[]) Array.newInstance(stepMetaInterface.getClass().getClassLoader().loadClass("org.pentaho.big.data.kettle.plugins.formats.FormatInputOutputField"), fields.size());
			OsgiBundleUtils.setOsgiField(stepMetaInterface, "outputFields", fields.toArray(arrs), false);
		}

	}

	@Override
	public int stepType() {
		return 6;
	}

	@Override
	public void getStepDataAndRelationship(TransMeta transMeta, StepMeta stepMeta, StepDataRelationship sdr)
			throws Exception {

		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		//ParquetOutputMeta  parquetoutputmeta= (ParquetOutputMeta )stepMetaInterface;

		// 构建输出域
		//FormatInputOutputField[] fiofs = parquetoutputmeta.outputFields ;
		Object[] fiofs = (Object[]) OsgiBundleUtils.getOsgiField(stepMetaInterface, "outputFields", false);
		String[] outFieldNames = null;
		String[] inFieldNames = null;
		if( fiofs != null && fiofs.length >0 ) {
			outFieldNames = new String[fiofs.length];
			inFieldNames = new String[fiofs.length];
			for (int i = 0; i < fiofs.length; i++) {
				Object outfield = fiofs[i];

				outFieldNames[i] = (String) OsgiBundleUtils.invokeOsgiMethod(outfield, "getPath");//outfield.getPath();
				inFieldNames[i] =  (String) OsgiBundleUtils.invokeOsgiMethod(outfield, "getName");//outfield.getName();
			}
		}

		// 增加数据库系统节点
		String fileName = Const.NVL(filename, (String)OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getFilename"));// parquetoutputmeta.getFilename());
		if (StringUtils.isNotBlank(fileName)) {
			
			DataNode fileDataNode = DataNodeUtil.fileNodeParse("Parquet", fileName.trim(), "" ,  (String)OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getCompressionType") , "" ) ;
			sdr.addOutputDataNode(fileDataNode);
			
			// 增加 流节点 和 输出系统节点 的关系
			String from = "转换:" + transMeta.getName() + ",步骤:" + stepMeta.getName();
			List<Relationship> relationships = RelationshipUtil.outputStepRelationship(null, fileDataNode, stepMeta.getName(), from, outFieldNames, inFieldNames) ;
			sdr.getDataRelationship().addAll(relationships);
		}
		
	}

}
