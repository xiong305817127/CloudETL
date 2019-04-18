package com.ys.idatrix.quality.dto.step.steps.bigdata;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.vfs2.FileObject;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.util.OsgiBundleUtils;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.core.vfs.KettleVFS;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.ys.idatrix.quality.dto.step.parts.TextFileInputFieldDto;
import com.ys.idatrix.quality.dto.step.parts.TextFileInputFileDto;
import com.ys.idatrix.quality.dto.step.steps.StepParameter;
import com.ys.idatrix.quality.ext.utils.StringEscapeHelper;
import com.ys.idatrix.quality.recovery.trans.ResumeStepDataParser;
import com.ys.idatrix.quality.recovery.trans.dto.StepLinesDto;
import com.ys.idatrix.quality.service.trans.stepdetail.HadoopFileInputDetailService;
import com.ys.idatrix.quality.toolkit.analyzer.trans.step.StepDataRelationship;
import com.ys.idatrix.quality.toolkit.analyzer.trans.step.StepDataRelationshipParser;
import com.ys.idatrix.quality.toolkit.domain.DataNode;
import com.ys.idatrix.quality.toolkit.domain.Relationship;
import com.ys.idatrix.quality.toolkit.utils.DataNodeUtil;
import com.ys.idatrix.quality.toolkit.utils.RelationshipUtil;

import net.sf.json.JSONObject;

/**
 * Step - ParquetInput.
 * 转换  org.pentaho.big.data.kettle.plugins.formats.impl.parquet.input.ParquetInputMeta
 * 
 * @author XH
 * @since 2018-08-21
 */
@Component("SPParquetInput")
@Scope("prototype")
public class SPParquetInput implements StepParameter, StepDataRelationshipParser,ResumeStepDataParser{

	@Autowired
	HadoopFileInputDetailService hadoopFileInputDetailService;
	
	TextFileInputFileDto  inputFile;
	List<TextFileInputFieldDto> inputFields ;
	
	public TextFileInputFileDto getInputFile() {
		return inputFile;
	}

	public void setInputFile(TextFileInputFileDto inputFile) {
		this.inputFile = inputFile;
	}

	public List<TextFileInputFieldDto> getInputFields() {
		return inputFields;
	}

	public void setInputFields(List<TextFileInputFieldDto> inputFields) {
		this.inputFields = inputFields;
	}

	@Override
	public Object getParameterObject(Object json) {
		JSONObject jsonObj = JSONObject.fromObject(json);
		Map<String, Class<?>> classMap = new HashMap<>();
		classMap.put("inputFile", TextFileInputFileDto.class);
		classMap.put("inputFields", TextFileInputFieldDto.class);
		return (SPParquetInput) JSONObject.toBean(jsonObj, SPParquetInput.class, classMap);
	}

	@Override
	public Object encodeParameterObject(StepMeta stepMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		
		SPParquetInput spParquetInput= new SPParquetInput();
		//ParquetInputMeta parquetinputmeta= (ParquetInputMeta )stepMetaInterface;

		//FormatInputFile files = parquetinputmeta.inputFiles ;
		Object files = OsgiBundleUtils.getOsgiField(stepMetaInterface, "inputFiles", false);
		//if(files!= null && files.fileName != null && files.fileName.length >0 ) {
		if(files!= null &&  OsgiBundleUtils.getOsgiField(files, "fileName", false) != null  ) {
			String[] fileName =  (String[])OsgiBundleUtils.getOsgiField(files, "fileName", false);
			String[] environment =  (String[])OsgiBundleUtils.getOsgiField(files, "environment", false);
			String[] fileMask =  (String[])OsgiBundleUtils.getOsgiField(files, "fileMask", false);
			String[] excludeFileMask =  (String[])OsgiBundleUtils.getOsgiField(files, "excludeFileMask", false);
			String[] fileRequired =  (String[])OsgiBundleUtils.getOsgiField(files, "fileRequired", false);
			String[] includeSubFolders =  (String[])OsgiBundleUtils.getOsgiField(files, "includeSubFolders", false);
			TextFileInputFileDto tfifd = new TextFileInputFileDto() ;
			 for ( int i = 0; i < fileName.length; i++ ) {
				 if(Utils.isEmpty(environment[i])) {
					 tfifd.setSourceConfigurationName("local" ) ;
				 }else {
					 tfifd.setSourceConfigurationName( environment[i] ) ;
				 }
				
				 tfifd.setFileName(StringEscapeHelper.getUrlPath(fileName[i]) ) ;
				 tfifd.setFileMask( fileMask[i] ) ;
				 tfifd.setExcludeFileMask( excludeFileMask[i] ) ;
				 tfifd.setFileRequired( fileRequired[i]  );
				 tfifd.setIncludeSubFolders( includeSubFolders[i] ) ;
			    }
			 spParquetInput.setInputFile(tfifd);
		}
		//FormatInputOutputField[] fields = parquetinputmeta.inputFields ;
		Object[] fields = (Object[]) OsgiBundleUtils.getOsgiField(stepMetaInterface, "inputFields", false);
		if(fields!= null ) {
			inputFields = Lists.newArrayList();
		    for ( int i = 0; i < fields.length; i++ ) {
		    	TextFileInputFieldDto tfifd = new TextFileInputFieldDto();
			   //FormatInputOutputField field = fields[i];
		    	Object field = fields[i];
			    
			    tfifd.setPath( (String)OsgiBundleUtils.invokeOsgiMethod(field, "getPath"));//field.getPath() );
			    tfifd.setName( (String)OsgiBundleUtils.invokeOsgiMethod(field, "getName"));//field.getName() );
			    tfifd.setType(  (String)OsgiBundleUtils.invokeOsgiMethod(field, "getTypeDesc"));//field.getTypeDesc() );
			    tfifd.setFormat(  (String)OsgiBundleUtils.invokeOsgiMethod(field, "getFormat"));//field.getFormat() );
			    tfifd.setCurrency(  (String)OsgiBundleUtils.invokeOsgiMethod(field, "getCurrencySymbol"));//field.getCurrencySymbol() );
			    tfifd.setDecimal(  (String)OsgiBundleUtils.invokeOsgiMethod(field, "getDecimalSymbol"));//field.getDecimalSymbol() );
			    tfifd.setGroup(  (String)OsgiBundleUtils.invokeOsgiMethod(field, "getGroupSymbol"));//field.getGroupSymbol()  );
			    tfifd.setNullif( (String)OsgiBundleUtils.invokeOsgiMethod(field, "getNullString"));// field.getNullString()  );
			    tfifd.setIfnull(  (String)OsgiBundleUtils.invokeOsgiMethod(field, "getIfNullValue"));//field.getIfNullValue() );
			    tfifd.setPosition(  (int)OsgiBundleUtils.invokeOsgiMethod(field, "getPosition"));//field.getPosition()  );
			    tfifd.setLength(  (int)OsgiBundleUtils.invokeOsgiMethod(field, "getLength"));//field.getLength() );
			    tfifd.setPrecision(  (int)OsgiBundleUtils.invokeOsgiMethod(field, "getPrecision"));//field.getPrecision() );
			    tfifd.setTrimType(  (String)OsgiBundleUtils.invokeOsgiMethod(field, "getTrimTypeCode"));//field.getTrimTypeCode() );
			    tfifd.setRepeat(  (boolean)OsgiBundleUtils.invokeOsgiMethod(field, "isRepeated"));//field.isRepeated() ) ;
			    
			    inputFields.add(tfifd);
			 }
		    spParquetInput.setInputFields(inputFields) ;
		}
	
		return spParquetInput;
	}

	@Override
	public void decodeParameterObject(StepMeta stepMeta, Object po, List<DatabaseMeta> databases,TransMeta transMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPParquetInput spParquetInput= (SPParquetInput)po;
		//ParquetInputMeta  parquetinputmeta= (ParquetInputMeta )stepMetaInterface;
		
		TextFileInputFileDto file = spParquetInput.getInputFile() ;
		if(file != null ) {
			//FormatInputFile files = new FormatInputFile();
			Object files = OsgiBundleUtils.newOsgiInstance(stepMetaInterface, "org.pentaho.big.data.kettle.plugins.formats.FormatInputFile");
			
			//files.fileMask = new String[]{file.getFileMask()};
			OsgiBundleUtils.setOsgiField(files, "fileMask", new String[]{file.getFileMask()}, false);
			//files.excludeFileMask = new String[]{file.getExcludeFileMask()};
			OsgiBundleUtils.setOsgiField(files, "excludeFileMask", new String[]{file.getExcludeFileMask()}, false);
			//files.fileRequired = new String[]{file.getFileRequired()};
			OsgiBundleUtils.setOsgiField(files, "fileRequired", new String[]{file.getFileRequired()}, false);
			//files.includeSubFolders = new String[]{file.getIncludeSubFolders()};
			OsgiBundleUtils.setOsgiField(files, "includeSubFolders", new String[]{file.getIncludeSubFolders()}, false);
			//files.environment = new String[]{file.getSourceConfigurationName()};
			//files.fileName = new String[]{file.getFileName()};
			if(!Utils.isEmpty(file.getFileName())) {
				if(!Utils.isEmpty(file.getSourceConfigurationName()) && !"local".equalsIgnoreCase(file.getSourceConfigurationName())) {
					OsgiBundleUtils.setOsgiField(files, "environment", new String[]{file.getSourceConfigurationName()}, false);
					String hadoopUrl = hadoopFileInputDetailService.getConnectPath(null, file.getSourceConfigurationName(), file.getFileName());
					OsgiBundleUtils.setOsgiField(files, "fileName", new String[]{ hadoopUrl }, false);
				}else {
					OsgiBundleUtils.setOsgiField(files, "environment", new String[]{""}, false);
					FileObject loaclfile = KettleVFS.getFileObject(file.getFileName());
					OsgiBundleUtils.setOsgiField(files, "fileName", new String[]{ loaclfile.getURL().toString()}, false);
					loaclfile.close();
				}
			}else {
				OsgiBundleUtils.setOsgiField(files, "fileName", new String[]{ "" }, false);
				OsgiBundleUtils.setOsgiField(files, "environment", new String[]{""}, false);
			}
			
			//parquetinputmeta.inputFiles = files ;
			OsgiBundleUtils.setOsgiField(stepMetaInterface, "inputFiles", files, false);
		}
		
		List<TextFileInputFieldDto> tfifs = spParquetInput.getInputFields();
		if(tfifs != null && tfifs.size() >0) {
			 //FormatInputOutputField[] fields = new FormatInputOutputField[tfifs.size()];
			 List<Object> fields = Lists.newArrayList() ;
			 for( int i=0;i<tfifs.size();i++) {
				 TextFileInputFieldDto tfif = tfifs.get(i);
				 //FormatInputOutputField fiof =new FormatInputOutputField();
				 Object fiof = OsgiBundleUtils.newOsgiInstance(stepMetaInterface, "org.pentaho.big.data.kettle.plugins.formats.FormatInputOutputField");
				 
				 // fiof.setPath( tfif.getPath() );
				 OsgiBundleUtils.invokeOsgiMethod(fiof, "setPath", tfif.getPath());
				 //fiof.setName( tfif.getName() );
				 OsgiBundleUtils.invokeOsgiMethod(fiof, "setName", tfif.getName());
				 //fiof.setType( tfif.getType() );
				 OsgiBundleUtils.invokeOsgiMethod(fiof, "setType", tfif.getType());
				 //fiof.setFormat( tfif.getFormat() );
				 OsgiBundleUtils.invokeOsgiMethod(fiof, "setFormat", tfif.getFormat());
				 //fiof.setCurrencySymbol( tfif.getCurrency() );
				 OsgiBundleUtils.invokeOsgiMethod(fiof, "setCurrencySymbol", tfif.getCurrency());
				 //fiof.setDecimalSymbol( tfif.getDecimal() );
				 OsgiBundleUtils.invokeOsgiMethod(fiof, "setDecimalSymbol", tfif.getDecimal());
				 //fiof.setGroupSymbol( tfif.getGroup()  );
				 OsgiBundleUtils.invokeOsgiMethod(fiof, "setGroupSymbol", tfif.getGroup());
				 //fiof.setNullString( tfif.getNullif()  );
				 OsgiBundleUtils.invokeOsgiMethod(fiof, "setNullString", tfif.getNullif());
				 //fiof.setIfNullValue( tfif.getIfnull() );
				 OsgiBundleUtils.invokeOsgiMethod(fiof, "setIfNullValue", tfif.getIfnull());
				 //fiof.setPosition( tfif.getPosition()  );
				 OsgiBundleUtils.invokeOsgiMethod(fiof, "setPosition", tfif.getPosition());
				 //fiof.setLength( tfif.getLength() );
				 OsgiBundleUtils.invokeOsgiMethod(fiof, "setLength", tfif.getLength());
				 //fiof.setPrecision( tfif.getPrecision() );
				 OsgiBundleUtils.invokeOsgiMethod(fiof, "setPrecision", tfif.getPrecision());
				 if(!Utils.isEmpty(tfif.getTrimType())) {
					 //fiof.setTrimType( tfif.getTrimType() );
					 OsgiBundleUtils.invokeOsgiMethod(fiof, "setTrimType", tfif.getTrimType());
				 }
				 //fiof.setRepeated( tfif.getRepeat() ) ;
				 OsgiBundleUtils.invokeOsgiMethod(fiof, "setRepeated", tfif.getRepeat());
				   
				 //fields[i] = fiof ;
				 fields.add(fiof);
			 }
			 //parquetinputmeta.inputFields = fields ;
			 Object[] arrs = (Object[]) Array.newInstance(stepMetaInterface.getClass().getClassLoader().loadClass("org.pentaho.big.data.kettle.plugins.formats.FormatInputOutputField"), fields.size());
			 OsgiBundleUtils.setOsgiField(stepMetaInterface, "inputFields", fields.toArray(arrs), false);
		}
	}

	@Override
	 public boolean waitPut(StepLinesDto linesDto,List<StepMeta> nextStepMeta ,StepMeta curStepMeta ,StepMetaInterface stepMetaInterface,StepDataInterface stepDataInterface , StepInterface stepInterface)  throws Exception  {
		//文件输入  可能是 多文件 输入,无法定位行数(组件根据\n\r确定是否新行),游标无法定位,使用暴力忽略
		waitPutRowData(stepInterface, linesDto.getRowLine());
		return true;
	}
	
	@Override
	public int stepType() {
		return 1;
	}

	@Override
	public void getStepDataAndRelationship(TransMeta transMeta, StepMeta stepMeta, StepDataRelationship sdr)
			throws Exception {

		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		//ParquetInputMeta  parquetinputmeta= (ParquetInputMeta )stepMetaInterface;

		Object files = OsgiBundleUtils.getOsgiField(stepMetaInterface, "inputFiles", false);
		String[] fileNames = (String[]) OsgiBundleUtils.getOsgiField(files, "fileName", false);
		
		if(fileNames!= null && fileNames.length >0) {
			
			String fileName;
			for (String name : fileNames) {
				if (StringUtils.isNotEmpty(name)) {
					fileName = name.trim();
					
					DataNode fileDataNode = DataNodeUtil.fileNodeParse("Parquet", fileName, "", "", "");
					sdr.addInputDataNode(fileDataNode);
					
					// 增加 系统节点 和 流节点的关系
					String from = "转换:" + transMeta.getName() + ",步骤:" + stepMeta.getName();
					List<Relationship> relationships = RelationshipUtil.inputStepRelationship(null, fileDataNode, sdr.getOutputStream(), stepMeta.getName(), from);
					sdr.getDataRelationship().addAll(relationships);
					
				}
			}
		}
	}

}
