package com.ys.idatrix.cloudetl.dto.step.steps.output;

import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.metrics2.sink.relocated.google.common.collect.Maps;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.util.OsgiBundleUtils;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ys.idatrix.cloudetl.dto.step.StepFieldDto;
import com.ys.idatrix.cloudetl.dto.step.parts.JsonOutputFieldDto;
import com.ys.idatrix.cloudetl.dto.step.steps.StepParameter;
import com.ys.idatrix.cloudetl.recovery.trans.ResumeStepDataParser;
import com.ys.idatrix.cloudetl.recovery.trans.dto.StepLinesDto;
import com.ys.idatrix.cloudetl.toolkit.analyzer.trans.step.StepDataRelationship;
import com.ys.idatrix.cloudetl.toolkit.analyzer.trans.step.StepDataRelationshipParser;
import com.ys.idatrix.cloudetl.toolkit.domain.DataNode;
import com.ys.idatrix.cloudetl.toolkit.domain.Relationship;
import com.ys.idatrix.cloudetl.toolkit.utils.DataNodeUtil;
import com.ys.idatrix.cloudetl.toolkit.utils.RelationshipUtil;

import net.sf.json.JSONObject;

/**
 * Step - JsonOutput(json输出). 转换 org.pentaho.di.trans.steps.jsonoutput.JsonOutputMeta
 * 
 * @author FBZ
 * @since 12-8-2017
 */
@Component("SPJsonOutput")
@Scope("prototype")
public class SPJsonOutput implements StepParameter, StepDataRelationshipParser,ResumeStepDataParser {

	/**
	 * Operations type, 操作, 0: Output value, 1: 写到文件, 2: Output value and write to
	 * file
	 */
	private int operationType;

	/**
	 * The encoding to use for reading: null or empty string means system default
	 * encoding, 编码
	 */
	private String encoding;

	/** The name value containing the resulting Json fragment, 输出值 */
	private String outputValue;

	/** The name of the json bloc, json 条目名称 */
	private String jsonBloc;

	/**
	 * 一个数据条目的数据行
	 */
	private String nrRowsInBloc;

	/* THE FIELD SPECIFICATIONS ... */

	/** The output fields, 字段 */
	private JsonOutputFieldDto[] outputFields;

	/**
	 * 添加文件到结果文件中
	 */
	private boolean addToResult;

	/**
	 * Whether to push the output into the output of a servlet with the executeTrans
	 * Carte/DI-Server servlet, 发送结果到servlet
	 */
	private boolean servletOutput;

	/** The base name of the output file, 文件名 */
	private String fileName;

	/** The file extention in case of a generated filename, 扩展名 */
	private String extension;

	/**
	 * Flag to indicate the we want to append to the end of an existing file (if it
	 * exists), 追加方式
	 */
	private boolean fileAppended;

	/**
	 * Flag to indicate whether or not to create JSON structures compatible with pre
	 * PDI-4.3.0, 兼容模式
	 */
	private boolean compatibilityMode;

	// private boolean stepNrInFilename;
	// private boolean partNrInFilename;

	/** Flag: add the date in the filename, 添加日期到文件名 */
	private boolean dateInFilename;

	/** Flag: add the time in the filename, 添加时间到文件名 */
	private boolean timeInFilename;

	/** Flag: create parent folder if needed, 创建父文件夹 */
	private boolean createparentfolder;

	/**
	 * 启动时不创建文件
	 */
	private boolean DoNotOpenNewFileInit;

	public int getOperationType() {
		return operationType;
	}

	public void setOperationType(int operationType) {
		this.operationType = operationType;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public String getOutputValue() {
		return outputValue;
	}

	public void setOutputValue(String outputValue) {
		this.outputValue = outputValue;
	}

	public String getJsonBloc() {
		return jsonBloc;
	}

	public void setJsonBloc(String jsonBloc) {
		this.jsonBloc = jsonBloc;
	}

	public String getNrRowsInBloc() {
		return nrRowsInBloc;
	}

	public void setNrRowsInBloc(String nrRowsInBloc) {
		this.nrRowsInBloc = nrRowsInBloc;
	}

	public JsonOutputFieldDto[] getOutputFields() {
		return outputFields;
	}

	public void setOutputFields(JsonOutputFieldDto[] outputFields) {
		this.outputFields = outputFields;
	}

	public boolean isAddToResult() {
		return addToResult;
	}

	public void setAddToResult(boolean addToResult) {
		this.addToResult = addToResult;
	}

	public boolean isServletOutput() {
		return servletOutput;
	}

	public void setServletOutput(boolean servletOutput) {
		this.servletOutput = servletOutput;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public boolean isFileAppended() {
		return fileAppended;
	}

	public void setFileAppended(boolean fileAppended) {
		this.fileAppended = fileAppended;
	}

	public boolean isCompatibilityMode() {
		return compatibilityMode;
	}

	public void setCompatibilityMode(boolean compatibilityMode) {
		this.compatibilityMode = compatibilityMode;
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

	public boolean isCreateparentfolder() {
		return createparentfolder;
	}

	public void setCreateparentfolder(boolean createparentfolder) {
		this.createparentfolder = createparentfolder;
	}

	public boolean isDoNotOpenNewFileInit() {
		return DoNotOpenNewFileInit;
	}

	public void setDoNotOpenNewFileInit(boolean doNotOpenNewFileInit) {
		DoNotOpenNewFileInit = doNotOpenNewFileInit;
	}

	/*
	 * Parser JSON object as a step parameter object.
	 */
	@Override
	public Object getParameterObject(Object json) {
		JSONObject jsonObj = JSONObject.fromObject(json);

		Map<String, Class<?>> classMap = new HashMap<>();
		classMap.put("outputFields", JsonOutputFieldDto.class);

		return (SPJsonOutput) JSONObject.toBean(jsonObj, SPJsonOutput.class, classMap);
	}

	/*
	 * Encode a step parameter object from step meta.
	 */
	@Override
	public Object encodeParameterObject(StepMeta stepMeta) throws Exception {
		StepMetaInterface stepM = stepMeta.getStepMetaInterface();
		SPJsonOutput jso = new SPJsonOutput();

		jso.setJsonBloc(Const.NVL((String) OsgiBundleUtils.invokeOsgiMethod(stepM, "getJsonBloc"), "")); // inputMeta.getJsonBloc()
		jso.setNrRowsInBloc(Const.NVL((String) OsgiBundleUtils.invokeOsgiMethod(stepM, "getNrRowsInBloc"), "")); // inputMeta.getNrRowsInBloc()
		jso.setEncoding(Const.NVL((String) OsgiBundleUtils.invokeOsgiMethod(stepM, "getEncoding"), "")); // inputMeta.getEncoding()
		jso.setOutputValue(Const.NVL((String) OsgiBundleUtils.invokeOsgiMethod(stepM, "getOutputValue"), "")); // inputMeta.getOutputValue()
		jso.setCompatibilityMode((boolean) OsgiBundleUtils.invokeOsgiMethod(stepM, "isCompatibilityMode")); // inputMeta.isCompatibilityMode()
		jso.setOperationType((int) OsgiBundleUtils.invokeOsgiMethod(stepM, "getOperationType")); // inputMeta.getOperationType()
		jso.setFileName(Const.NVL((String) OsgiBundleUtils.invokeOsgiMethod(stepM, "getFileName"), "")); // inputMeta.getFileName()
		jso.setCreateparentfolder((boolean) OsgiBundleUtils.invokeOsgiMethod(stepM, "isCreateParentFolder")); // inputMeta.isCreateParentFolder()
		jso.setExtension(Const.NVL((String) OsgiBundleUtils.invokeOsgiMethod(stepM, "getExtension"), "js")); // inputMeta.getExtension()
		jso.setServletOutput((boolean) OsgiBundleUtils.invokeOsgiMethod(stepM, "isServletOutput")); // inputMeta.isServletOutput()

		jso.setDateInFilename((boolean) OsgiBundleUtils.invokeOsgiMethod(stepM, "isDateInFilename")); // inputMeta.isDateInFilename()
		jso.setTimeInFilename((boolean) OsgiBundleUtils.invokeOsgiMethod(stepM, "isTimeInFilename")); // inputMeta.isTimeInFilename()
		jso.setFileAppended((boolean) OsgiBundleUtils.invokeOsgiMethod(stepM, "isFileAppended")); // inputMeta.isFileAppended()

		jso.setAddToResult((boolean) OsgiBundleUtils.invokeOsgiMethod(stepM, "AddToResult")); // inputMeta.AddToResult()
		jso.setDoNotOpenNewFileInit((boolean) OsgiBundleUtils.invokeOsgiMethod(stepM, "isDoNotOpenNewFileInit")); // inputMeta.isDoNotOpenNewFileInit()

		Object[] outFields = (Object[]) OsgiBundleUtils.invokeOsgiMethod(stepM, "getOutputFields"); // inputMeta.getOutputFields()
		int outLen = null == outFields ? 0 : outFields.length; // Array.getLength(outFields);
		jso.setOutputFields(new JsonOutputFieldDto[outLen]);

		JsonOutputFieldDto outField = null;
		for (int i = 0; i < outLen; i++) {
			Object field = outFields[i]; // Array.get(outFields, i); //
											// JsonOutputField
			jso.getOutputFields()[i] = outField = new JsonOutputFieldDto();

			outField.setFieldName(Const.NVL((String) OsgiBundleUtils.invokeOsgiMethod(field, "getFieldName"), "")); // field.getFieldName()
			outField.setElementName(Const.NVL((String) OsgiBundleUtils.invokeOsgiMethod(field, "getElementName"), "")); // field.getElementName()
		}

		return jso;
	}

	/*
	 * decode a step parameter object into step meta.
	 */
	@Override
	public void decodeParameterObject(StepMeta stepMeta, Object po, List<DatabaseMeta> databases,TransMeta transMeta) throws Exception {
		StepMetaInterface stepM = stepMeta.getStepMetaInterface();
		SPJsonOutput jso = (SPJsonOutput) po;

		OsgiBundleUtils.invokeOsgiMethod(stepM, "setJsonBloc", jso.getJsonBloc()); // inputmeta.setJsonBloc();
		OsgiBundleUtils.invokeOsgiMethod(stepM, "setNrRowsInBloc", jso.getNrRowsInBloc()); // inputmeta.setNrRowsInBloc();
		OsgiBundleUtils.invokeOsgiMethod(stepM, "setEncoding", jso.getEncoding()); // inputmeta.setEncoding();
		OsgiBundleUtils.invokeOsgiMethod(stepM, "setOutputValue", jso.getOutputValue()); // inputmeta.setOutputValue();
		OsgiBundleUtils.invokeOsgiMethod(stepM, "setCompatibilityMode", jso.isCompatibilityMode()); // inputmeta.setCompatibilityMode();
		OsgiBundleUtils.invokeOsgiMethod(stepM, "setOperationType", jso.getOperationType()); // inputmeta.setOperationType();
		OsgiBundleUtils.invokeOsgiMethod(stepM, "setCreateParentFolder", jso.isCreateparentfolder()); // inputmeta.setCreateParentFolder();
		OsgiBundleUtils.invokeOsgiMethod(stepM, "setFileName", jso.getFileName()); // inputmeta.setFileName();
		OsgiBundleUtils.invokeOsgiMethod(stepM, "setExtension", jso.getExtension()); // inputmeta.setExtension();
		OsgiBundleUtils.invokeOsgiMethod(stepM, "setServletOutput", jso.isServletOutput()); // inputmeta.setServletOutput();
		OsgiBundleUtils.invokeOsgiMethod(stepM, "setFileAppended", jso.isFileAppended()); // inputmeta.setFileAppended();

		OsgiBundleUtils.invokeOsgiMethod(stepM, "setDateInFilename", jso.isDateInFilename()); // inputmeta.setDateInFilename();
		OsgiBundleUtils.invokeOsgiMethod(stepM, "setTimeInFilename", jso.isTimeInFilename()); // inputmeta.setTimeInFilename();

		OsgiBundleUtils.invokeOsgiMethod(stepM, "setAddToResult", jso.isAddToResult()); // inputmeta.setAddToResult();
		OsgiBundleUtils.invokeOsgiMethod(stepM, "setDoNotOpenNewFileInit", jso.isDoNotOpenNewFileInit()); // inputmeta.setDoNotOpenNewFileInit();

		int nrfields = null == jso.getOutputFields() ? 0 : jso.getOutputFields().length;
		OsgiBundleUtils.invokeOsgiMethod(stepM, "allocate", nrfields); // inputmeta.allocate();
		if (nrfields > 0) {
			Object[] outs = (Object[]) OsgiBundleUtils.invokeOsgiMethod(stepM, "getOutputFields"); // inputmeta.getOutputFields();

			Object out = null;
			Class<?> clazz = stepM.getClass().getClassLoader()
					.loadClass("org.pentaho.di.trans.steps.jsonoutput.JsonOutputField");

			for (int i = 0; i < nrfields; i++) {
				outs[i] = out = clazz.newInstance();

				OsgiBundleUtils.invokeOsgiMethod(out, "setFieldName", jso.getOutputFields()[i].getFieldName());
				OsgiBundleUtils.invokeOsgiMethod(out, "setElementName", jso.getOutputFields()[i].getElementName());
			}
		}
	}

	/*
	 * 覆盖方法：getStepDataAndRelationship
	 */
	@Override
	public void getStepDataAndRelationship(TransMeta transMeta, StepMeta stepMeta, StepDataRelationship sdr) throws Exception {

		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();

		// 构建输出域
		Object[] outFields = (Object[]) OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getOutputFields"); // inputMeta.getOutputFields()
		Map<String, StepFieldDto> fields = null;
		String[] outFieldNames = null;
		String[] inFieldNames = null;
		if (outputFields != null && outputFields.length > 0) {
			outFieldNames = new String[outputFields.length];
			inFieldNames = new String[outputFields.length];
			fields = Maps.newHashMap();
			for (int i = 0; i < outputFields.length; i++) {
				Object field = outFields[i];

				String inf = Const.NVL((String) OsgiBundleUtils.invokeOsgiMethod(field, "getFieldName"), "");
				String outf = Const.NVL((String) OsgiBundleUtils.invokeOsgiMethod(field, "getElementName"), ""); // field.getElementName()

				outFieldNames[i] = outf;
				inFieldNames[i] = inf;

				StepFieldDto stepFieldDto = new StepFieldDto();
				stepFieldDto.setName(outf);
				stepFieldDto.setOrigin(stepMeta.getName());

				fields.put(outf, stepFieldDto);
			}
		}

		String[] fileNames = (String[]) OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getFileName");
		if(fileNames != null ) {
			for (String fileName : fileNames) {
				if (StringUtils.isNotBlank(fileName)) {
					
					DataNode fileDataNode = DataNodeUtil.fileNodeParse("Json", fileName.trim(), "", "", "" ) ;
					sdr.addOutputDataNode(fileDataNode);
					
					// 增加 流节点 和 输出系统节点 的关系
					String from = "转换:" + transMeta.getName() + ",步骤:" + stepMeta.getName();
					List<Relationship> relationships = RelationshipUtil.outputStepRelationship(null, fileDataNode, stepMeta.getName(), from, outFieldNames, inFieldNames) ;
					sdr.getDataRelationship().addAll(relationships);
				}
			}
		}
		
	}
	
	@Override
	public boolean afterSaveHandle(Map<Object, Object> cacheData ,TransMeta transMeta, StepMeta stepMeta,
			StepMetaInterface stepMetaInterface, StepDataInterface stepDataInterface, StepInterface stepInterface)
			throws Exception {
		
		Writer w = (Writer) OsgiBundleUtils.getOsgiField(stepDataInterface, "writer", false);
		if(w != null ) {
			w.flush();
		}
		
		return true;
	}
	
	@Override
	public Long getLinekeyIndex(StepLinesDto result) {
		Long rowLine = result.getRowLine();
		if(rowLine ==  null) {
			rowLine = result.getLinesOutput();
		}
		if(operationType == 1 && nrRowsInBloc != null && Integer.valueOf(nrRowsInBloc) > 0) {
			//当写到文件不输出值时 ,输出行数需要对nrRowsInBloc向下取整
			rowLine = ((Double)Math.floor(rowLine/Integer.valueOf(nrRowsInBloc))).longValue();
		}
		
		return rowLine;
	}
	
	@Override
	public int stepType() {
		if(nrRowsInBloc != null && Integer.valueOf(nrRowsInBloc) == 0) {
			//nrRowsInBloc == 0 表示输出所有行到一个json对象中,继续就需要所有数据,从0 重新开始
			return 0 ;
		}
		
		if(operationType == 0) {
			//Output value ,为纯中间件
			return 4;
		}
		return 6 ;
	}
	
}
