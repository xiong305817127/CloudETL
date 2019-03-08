package com.ys.idatrix.cloudetl.service;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.util.IdatrixPropertyUtil;
import org.pentaho.di.core.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.ys.idatrix.cloudetl.dto.common.ReturnCodeDto;
import com.ys.idatrix.cloudetl.dto.engine.ExecConfigurationDto;
import com.ys.idatrix.cloudetl.dto.entry.EntryDetailsDto;
import com.ys.idatrix.cloudetl.dto.entry.entries.EntryParameter;
import com.ys.idatrix.cloudetl.dto.entry.entries.general.SPTrans;
import com.ys.idatrix.cloudetl.dto.hadoop.HadoopBriefDto;
import com.ys.idatrix.cloudetl.dto.hop.HopDto;
import com.ys.idatrix.cloudetl.dto.job.JobExecRequestDto;
import com.ys.idatrix.cloudetl.dto.job.JobInfoDto;
import com.ys.idatrix.cloudetl.dto.step.StepDetailsDto;
import com.ys.idatrix.cloudetl.dto.step.parts.DesensitizationFieldDto;
import com.ys.idatrix.cloudetl.dto.step.parts.GetVariableFieldDefinitionDto;
import com.ys.idatrix.cloudetl.dto.step.parts.JsFieldDto;
import com.ys.idatrix.cloudetl.dto.step.parts.JsScriptDto;
import com.ys.idatrix.cloudetl.dto.step.parts.TextFileFieldDto;
import com.ys.idatrix.cloudetl.dto.step.parts.TextFileInputFileDto;
import com.ys.idatrix.cloudetl.dto.step.steps.StepParameter;
import com.ys.idatrix.cloudetl.dto.step.steps.input.SPGetFileNames;
import com.ys.idatrix.cloudetl.dto.step.steps.input.SPGetVariable;
import com.ys.idatrix.cloudetl.dto.step.steps.script.SPScriptValueMod;
import com.ys.idatrix.cloudetl.dto.step.steps.transfor.SPConcatFields;
import com.ys.idatrix.cloudetl.dto.step.steps.transfor.SPDesensitization;
import com.ys.idatrix.cloudetl.dto.trans.TransInfoDto;
import com.ys.idatrix.cloudetl.ext.CloudSession;
import com.ys.idatrix.cloudetl.ext.PluginFactory;
import com.ys.idatrix.cloudetl.ext.executor.CloudExecutorStatus;
import com.ys.idatrix.cloudetl.ext.executor.CloudExecution;
import com.ys.idatrix.cloudetl.ext.executor.CloudJobExecutor;
import com.ys.idatrix.cloudetl.ext.utils.StringEscapeHelper;
import com.ys.idatrix.cloudetl.repository.xml.metastore.meta.DefaultEngineMeta;
import com.ys.idatrix.cloudetl.service.db.CloudMetaCubeDbService;
import com.ys.idatrix.cloudetl.service.engine.CloudDefaultEngineService;
import com.ys.idatrix.cloudetl.service.hadoop.CloudHadoopService;
import com.ys.idatrix.cloudetl.service.hop.CloudHopService;
import com.ys.idatrix.cloudetl.service.job.CloudEntryService;
import com.ys.idatrix.cloudetl.service.job.CloudJobService;
import com.ys.idatrix.cloudetl.service.step.StepServiceInterface;
import com.ys.idatrix.cloudetl.service.trans.CloudStepService;
import com.ys.idatrix.cloudetl.service.trans.CloudTransService;
import com.ys.idatrix.cloudetl.service.trans.stepdetail.HadoopFileInputDetailService;
import com.ys.idatrix.cloudetl.subscribe.api.dto.parts.DesensitizationRuleDto;
import com.ys.idatrix.cloudetl.subscribe.api.dto.step.StepDto;
import com.ys.idatrix.cloudetl.util.SubcribeUtils;

@Component
public class CloudSubscribeStepService {

	@Autowired
	public CloudTransService cloudTransService;
	@Autowired
	public CloudStepService cloudStepService;
	@Autowired
	public CloudJobService cloudJobService;
	@Autowired
	public CloudEntryService cloudEntryService;
	@Autowired
	public CloudHopService cloudHopService;

	@Autowired
	public CloudDefaultEngineService defaultEngineService;
	@Autowired
	public CloudHadoopService cloudHadoopService;
	@Autowired
	public HadoopFileInputDetailService hadoopDetailService;
	@Autowired
	public CloudMetaCubeDbService cloudDbService;
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public <T extends StepDto> StepServiceInterface<T> getStepService(T  curDto,StepDto...previousStep ) throws Exception {

		Map<String, StepServiceInterface> map = PluginFactory.getBeans(StepServiceInterface.class);
		if(map ==  null || map.values().isEmpty()) {
			throw new Exception("服务启动异常,未找到组件步骤服务.");
		}
		Optional<StepServiceInterface> stepServiceOpt = map.values().stream()
				.filter( service -> {
					Type superClass = service.getClass().getGenericSuperclass();
					if( !( superClass instanceof ParameterizedType ) && ( superClass instanceof Class ) ) {
						superClass = ((Class)superClass).getGenericSuperclass();
					}
					Class stepDtoClass = (Class) ((ParameterizedType)superClass).getActualTypeArguments()[0];
					if( curDto.getClass().equals( stepDtoClass) ) {
						return true ;
					}
					return false ;
				}).findFirst();
		if (stepServiceOpt.isPresent()) {
			StepServiceInterface<T> target = stepServiceOpt.get();
			target.init(this, curDto, previousStep);
			return target ;
		}
		throw new Exception("组件["+curDto.getType()+"]不支持订阅自动生成.");
	}
	
	
	/**
	 * 启动job
	 * 
	 * @param jobName
	 * @return
	 * @throws Exception
	 */
	public ReturnCodeDto startJob(String jobName, String group, Map<String, String> params, boolean isRemote)
			throws Exception {

		ExecConfigurationDto ec = new ExecConfigurationDto();
		String engineName = "Default-Local";
		if (!isRemote) {
			int localNum = CloudExecution.getInstance().localCounter(true);
			int maxNumber = Integer.valueOf(IdatrixPropertyUtil.getProperty("idatrix.exec.one.server.max.number", "100"));
			if (localNum > maxNumber) {
				isRemote = true;
			}
		}
		if (isRemote) {
			DefaultEngineMeta engine = getCloudMinRemoteEngine();
			if (engine != null) {
				engineName = engine.getName();
			}
		}

		ec.setEngineName(engineName);
		ec.setEngineType("default");
		ec.setClearingLog(true);
		ec.setSafeMode(false);
		ec.setGatherMetrics(true);
		ec.setRebootAutoRun(true);
		ec.setLogLevel("Basic");
		ec.setParams(params);

		JobExecRequestDto execRequest = new JobExecRequestDto();
		execRequest.setName(jobName);
		execRequest.setGroup(group);
		execRequest.setConfiguration(ec);

		ReturnCodeDto jer = cloudJobService.execJob(execRequest);
		return jer;
	}
	
	private DefaultEngineMeta getCloudMinRemoteEngine() throws Exception {

		String user = CloudSession.getLoginUser();
		Map<String, List<DefaultEngineMeta>> enginesMap = defaultEngineService.getDefaultEngineList(user);
		if( enginesMap != null && enginesMap.containsKey(user)) {
			List<DefaultEngineMeta> engines = enginesMap.get(user);
			if (engines != null && engines.size() > 0) {
				Optional<DefaultEngineMeta> opt = engines.stream().min((engine1, engine2) -> {

					int n1 = CloudExecution.getInstance().remoteCounter(engine1.getServer(),true);
					int n2 = CloudExecution.getInstance().remoteCounter(engine2.getServer(),true);
					return (n1 - n2);
				});
				if (opt.isPresent()) {
					return opt.get();
				}
			}
		}

		return null;
	}
	
	/**
	 * 停止job
	 * 
	 * @param executionId
	 * @return
	 * @throws KettleException
	 * @throws Exception
	 */
	public ReturnCodeDto StopJob(String executionId) throws KettleException, Exception {
		CloudJobExecutor executor = CloudExecution.getInstance().getJobExecutor(executionId);
		if (executor != null && CloudExecutorStatus.assertRunning(executor.getStatus())) {
			return cloudJobService.execStop(executionId);
		}
		return new ReturnCodeDto(0, null);
	}
	
	/**
	 * 获取jobname 所在的组
	 * 
	 * @param jobName
	 * @return
	 * @throws Exception
	 */
	public String getJobGroupName(String userId , String jobName) throws Exception {
		if (Utils.isEmpty(jobName)) {
			return "";
		}
		return cloudJobService.getCloudJobGroup(userId,jobName, SubcribeUtils.PRIORITY_GROUP_NAME);
	}
	
	
	
	/**
	 * 增加trans 步骤节点
	 * 
	 * @param transName
	 * @param stepName
	 * @param stepType
	 * @param step
	 * @return
	 * @throws KettleException
	 * @throws Exception
	 */
	public ReturnCodeDto addAndUpdateStepMeta(String transName, String group, String stepName, String stepType,
			StepParameter step) throws Exception {
		if (step == null) {
			return new ReturnCodeDto(-1, "step不能为空！");
		}

		StepDetailsDto sd = new StepDetailsDto();
		sd.setTransName(transName);
		sd.setGroup(group);
		sd.setStepName(stepName);
		sd.setDistributes(false);
		sd.setType(stepType);
		sd.setStepParams(step);
		return cloudStepService.saveStep(sd);
	}
	

	
	/**
	 * 创建transMeta
	 * 
	 * @param name
	 * @param description
	 * @return
	 * @throws Exception
	 */
	public ReturnCodeDto createTransMeta(String name, String group, String description) throws Exception {
		if (Utils.isEmpty(name)) {
			return new ReturnCodeDto(-1, "name不能为空！");
		}
		TransInfoDto ti = new TransInfoDto();
		ti.setName(name);
		ti.setGroup(group);
		ti.setDescription(description);

		return cloudTransService.newTrans(ti);
	}
	
	/**
	 * 增加连线
	 * 
	 * @param transName
	 * @param from
	 * @param to
	 * @param isJob
	 * @return
	 * @throws KettleException
	 * @throws Exception
	 */
	public ReturnCodeDto addHopMeta(String transName, String group, String from, String to, boolean isJob)
			throws KettleException, Exception {
		HopDto th = new HopDto();
		th.setName(transName);
		th.setGroup(group);
		th.setFrom(from);
		th.setTo(to);
		th.setIsJob(isJob);
		return cloudHopService.addHop(th);
	}
	
	/**
	 * 删除transMeta
	 * 
	 * @param transName
	 * @return
	 * @throws KettleException
	 * @throws Exception
	 */
	public ReturnCodeDto deleteTransMeta(String userId , String transName, String group) throws KettleException, Exception {
		if (!cloudTransService.checkTransName(userId,transName).getResult()) {
			// 不存在
			return new ReturnCodeDto(0, null);
		}
		return cloudTransService.deleteTrans(userId,transName, group);
	}
	
	/**
	 * 增加 调度 Entry节点
	 * 
	 * @param jobName
	 * @param group
	 * @param entryName
	 * @param entryType
	 * @param entry
	 * @param parallel
	 * @return
	 * @throws Exception
	 */
	public ReturnCodeDto addAndUpdateEntryMeta(String jobName, String group, String entryName, String entryType,
			EntryParameter entry, boolean parallel) throws Exception {
		if (entry == null) {
			return new ReturnCodeDto(-1, "entry不能为空！");
		}

		EntryDetailsDto ed = new EntryDetailsDto();
		ed.setJobName(jobName);
		ed.setGroup(group);
		ed.setEntryName(entryName);
		ed.setType(entryType);
		ed.setParallel(parallel);
		ed.entryParams = entry;
		return cloudEntryService.saveEntry(ed);
	}

	/**
	 * 增加 并行运行的 调度 Entry节点
	 * 
	 * @param jobName
	 * @param entryName
	 * @param entryType
	 * @param entry
	 * @return
	 * @throws Exception
	 */
	public ReturnCodeDto addAndUpdateEntryMeta(String jobName, String group, String entryName, String entryType,
			EntryParameter entry) throws Exception {
		return addAndUpdateEntryMeta(jobName, group, entryName, entryType, entry, true);
	}
	
	/**
	 * 创建JobMeta
	 * 
	 * @param name
	 * @param description
	 * @return
	 * @throws Exception
	 */
	public ReturnCodeDto createJobMeta(String name, String group, String description, Map<String, String> params)
			throws Exception {
		if (Utils.isEmpty(name)) {
			return new ReturnCodeDto(-1, "name不能为空！");
		}
		JobInfoDto ji = new JobInfoDto();
		ji.setName(name);
		ji.setGroup(group);
		ji.setDescription(description);
		ji.setParams(params);
		return cloudJobService.newJob(ji);
	}

	/**
	 * 删除 jobMeta
	 * 
	 * @param JobName
	 * @return
	 * @throws KettleException
	 * @throws Exception
	 */
	public ReturnCodeDto deleteJobMeta(String userId ,String jobName, String group) throws KettleException, Exception {
		if (!cloudJobService.checkJobName(userId,jobName).getResult()) {
			// 不存在
			return new ReturnCodeDto(0, null);
		}
		return cloudJobService.deleteJob(userId,jobName, group);
	}
	
	
	/*##########################################################新增单独的各节点###########################################################################*/
	
	/**
	 * 调度转换组件
	 * 
	 * @param transname
	 *            转换名
	 * @param runConfiguration
	 *            运行配置，默认Default-Local
	 * @return
	 */
	public SPTrans createTrans(String transname, String group, String runConfiguration) {
		if (Utils.isEmpty(runConfiguration)) {
			runConfiguration = "Default-Local";
		}
		SPTrans t = new SPTrans();
		t.setTransname(transname);
		t.setTransGroup(group);
		t.setRunConfiguration(runConfiguration);
		
		return t;
	}
	
	/**
	 * 连接字符串
	 * 
	 * @param targetFieldName
	 *            输出连接后的字段名
	 * @param fields
	 *            流名数组
	 * @param types
	 *            流值对应的数据类型，默认String，可选:"-", "Number", "String", "Date", "Boolean",
	 *            "Integer", "BigNumber", "Serializable", "Binary", "Timestamp",
	 *            "Internet Address"
	 * @return
	 */
	public SPConcatFields createConcatFields(String targetFieldName, String[] fields, String... types) {

		SPConcatFields concat = new SPConcatFields();
		concat.setTargetFieldName(targetFieldName);
		concat.setSeparator("	");
		concat.setEncoding("UTF-8");

		List<TextFileFieldDto> fieldDtos = new ArrayList<TextFileFieldDto>();
		if (fields != null && fields.length > 0) {
			for (int i = 0; i < fields.length; i++) {

				String type = (types != null && types.length > 0) ? types[0] : "String";
				if (types != null && types.length > i) {
					type = types[i];
				}
				TextFileFieldDto tffd = new TextFileFieldDto();
				tffd.setName(fields[i]);
				tffd.setType(type);

				fieldDtos.add(tffd);
			}
		}
		concat.setFields(fieldDtos);
		return concat;
	}
	
	/**
	 * javascript代码
	 * 
	 * @param script
	 *            脚本
	 * @param fields
	 *            提取的域字段名
	 * @param types
	 *            域字段对应类型 ，默认1(Number)，可选:0-10:"-", "Number", "String", "Date",
	 *            "Boolean", "Integer", "BigNumber", "Serializable", "Binary",
	 *            "Timestamp", "Internet Address"
	 * @param replaces
	 *            域字段是否替换(已存在则替换)，默认true
	 * @return
	 */
	public SPScriptValueMod createScript(String script, String[] fields, int[] types, Boolean... replaces) {
		SPScriptValueMod s = new SPScriptValueMod();
		s.setCompatible(false);
		s.setOptimizationLevel("9");

		ArrayList<JsScriptDto> jss = new ArrayList<JsScriptDto>();
		JsScriptDto js = new JsScriptDto();
		js.setName("scriptvalue");
		js.setType("0");
		js.setValue(StringEscapeHelper.encode(script));
		jss.add(js);
		s.setJsScripts(jss);

		if (fields != null && fields.length > 0) {
			List<JsFieldDto> jfs = new ArrayList<JsFieldDto>();
			for (int i = 0; i < fields.length; i++) {
				boolean replace = (replaces != null && replaces.length > 0) ? replaces[0] : true;
				if (replaces != null && replaces.length > i) {
					replace = replaces[i];
				}
				int type = (types != null && types.length > 0) ? types[0] : 1;
				if (types != null && types.length > i) {
					type = types[i];
				}
				JsFieldDto jf = new JsFieldDto();
				jf.setName(fields[i]);
				jf.setType(type);
				jf.setReplace(replace);
				jfs.add(jf);
			}
			s.setFields(jfs);
		}

		return s;
	}
	
	
	/**
	 * 获取文件名
	 * 
	 * @param fileNames
	 * @param sourceType
	 * @param fileMask
	 * @param excludeFileMask
	 * @param includeSubFolders
	 * @return
	 * @throws Exception 
	 */
	public SPGetFileNames createGetFileNames(List<String> fileNames, String sourceType, String fileMask,
			String excludeFileMask, boolean includeSubFolders,boolean addFileToResult) throws Exception {

		SPGetFileNames gfn = new SPGetFileNames();
		gfn.setAddResult(addFileToResult);
		List<TextFileInputFileDto> sfList = new ArrayList<TextFileInputFileDto>();
		for (String fileName : fileNames) {

			if ("hdfs".equalsIgnoreCase(sourceType)) {
				
				String user = CloudSession.getLoginUser();
				Map<String, List<HadoopBriefDto>> hadoopMap = cloudHadoopService.getCloudHadoopList(user);
				if (hadoopMap!= null && hadoopMap.containsKey(user) ) {
					List<HadoopBriefDto> hadoopList = hadoopMap.get(user);
					if (hadoopList!= null && hadoopList.size() > 0) {
						try {
							fileName = hadoopDetailService.getConnectPath(user,hadoopList.get(0).getName(), fileName);
						} catch (KettleException e) {
							e.printStackTrace();
						}
					}
				}
			}

			TextFileInputFileDto tfif = new TextFileInputFileDto();
			tfif.setFileName(fileName);
			tfif.setFileMask(fileMask);
			tfif.setExcludeFileMask(excludeFileMask);
			tfif.setIncludeSubFolders(includeSubFolders ? "Y" : "N");
			tfif.setFileRequired("N");

			sfList.add(tfif);
		}
		gfn.setSelectedFiles(sfList);
		return gfn;
	}
	

	/**
	 * 获取变量
	 * 
	 * @param vars
	 *            变量名(流名)数组
	 * @param varNames
	 *            变量名(系統)数组，默認变量名和流名一样
	 * @param fieldtypes
	 *            变量名对应的数据类型，默认1(Number)，可选:0-10:"-", "Number", "String", "Date",
	 *            "Boolean", "Integer", "BigNumber", "Serializable", "Binary",
	 *            "Timestamp", "Internet Address"
	 * @return
	 */
	public SPGetVariable createGetVariable(String[] vars, String[] varNames, int... fieldtypes) {
		SPGetVariable v = new SPGetVariable();
		if (vars != null && vars.length > 0) {
			List<GetVariableFieldDefinitionDto> vds = new ArrayList<GetVariableFieldDefinitionDto>();
			for (int i = 0; i < vars.length; i++) {
				int fieldtype = (fieldtypes != null && fieldtypes.length > 0) ? fieldtypes[0] : 1;
				String var = vars[i];
				String varName = var;
				if (fieldtypes != null && fieldtypes.length > i) {
					fieldtype = fieldtypes[i];
				}
				if (varNames != null && varNames.length > i) {
					varName = varNames[i];
				}
				GetVariableFieldDefinitionDto vd = new GetVariableFieldDefinitionDto();
				vd.setFieldname(var);
				vd.setVariablestring("${" + varName + "}");
				vd.setFieldtype(fieldtype);
				vds.add(vd);
			}
			v.setFieldDefinitions(vds);
		}
		return v;
	}
	
	/**
	 *  数据脱敏 
	 * @param infields 输入流名称数组
	 * @param outFields 输出流名称数组
	 * @param desensitizationRules 脱敏规则数组
	 * @return
	 */
	public SPDesensitization createDesensitization(String[] infields,String[] outFields,DesensitizationRuleDto[] desensitizationRules) {
		SPDesensitization d =  new  SPDesensitization();
		if( infields != null && infields.length > 0) {
			List<DesensitizationFieldDto> fieldsList = Lists.newArrayList();
			for( int i=0; i< infields.length ; i++ ) {
				DesensitizationRuleDto dr = ( desensitizationRules!= null&& desensitizationRules.length>i ) ? desensitizationRules[i]: null ;
				if( dr == null ) {
					continue ;
				}
				String inf = infields[i];
				String outf = ( outFields!= null&& outFields.length>i) ? outFields[i] : "";
				
				DesensitizationFieldDto dfd = new DesensitizationFieldDto();
				dfd.setFieldInStream(inf);
				dfd.setFieldOutStream(outf);
				dfd.setRuleType(Utils.isEmpty(dr.getReplacement())?"truncation":"mask");
				dfd.setStartPositon(dr.getStartPositon());
				dfd.setLength(dr.getLength());
				dfd.setReplacement(dr.getReplacement());
				dfd.setIgnoreSpace(dr.isIgnoreSpaces());
				fieldsList.add(dfd);
			}
			d.setDesensitizations(fieldsList );
		}
		return d ;
	}
	
	
}
