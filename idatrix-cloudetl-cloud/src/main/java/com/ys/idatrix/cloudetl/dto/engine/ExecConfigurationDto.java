/**
 * 云化数据集成系统 iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.dto.engine;

import java.util.Map;
import java.util.stream.Stream;

import org.dom4j.Element;
import org.pentaho.di.base.AbstractMeta;
import org.pentaho.di.core.parameters.UnknownParamException;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.core.xml.XMLHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.ys.idatrix.cloudetl.ext.utils.StringEscapeHelper;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 执行配置
 * (new run configuration)
 * @author JW
 * @since 2017年7月11日
 *
 */
@ApiModel("执行配置")
public class ExecConfigurationDto implements Cloneable {
	
	@ApiModelProperty("执行引擎类型")
    private String engineType;
	
	@ApiModelProperty("执行引擎名称")
    private String engineName;
	
	@ApiModelProperty("是否清理日志")
    private boolean clearingLog = true;
	
	@ApiModelProperty("是否安全模式")
    private boolean safeMode = false;
	
	@ApiModelProperty("是否收集步骤度量")
    private boolean gatherMetrics = true;
	
	@ApiModelProperty("日志级别")
    private String logLevel = "Basic";
    
	/**
	 * job Key: <br>
	 * "engineType","engineName","rebootAutoRun",  "MainTransName","OutStepName","InStepName", <br>
	 * org.pentaho.di.core.logging.CloudLogListener  <br>
	 * org.pentaho.di.job.IncrementalParser  <br>
	 * com.ys.idatrix.cloudetl.subcribe.SubcribePushService  <br>
	 *   <br>
	 * trans Key:  <br>
	 * "engineType","engineName","rebootAutoRun"  <br>
	 * com.ys.idatrix.cloudetl.recovery.trans.ResumeTransParser  <br>
	 */
	@ApiModelProperty("执行参数")
    Map<String,String> params;
	
	@ApiModelProperty("执行变量")
    Map<String,String> variables;
    
    //Job
    private String startCopyName;
    
    //运行中重启服务后是否自动启动任务
    private boolean rebootAutoRun=true;
    //运作中断,重启断点恢复执行
    private boolean breakpointsContinue=true;
    //运行中断,是否远程恢复执行,必须在breakpointsContinue为true的基础上
    private boolean breakpointsRemote=false;
    //远程正在执行,强制切换到本地(终止远程运行)
    private boolean forceLocal = false ;
    
	/**
	 * @return engineType
	 */
	public String getEngineType() {
		return engineType;
	}
	/**
	 * @param engineType 要设置的 engineType
	 */
	public void setEngineType(String engineType) {
		this.engineType = engineType;
	}
	/**
	 * @return engineName
	 */
	public String getEngineName() {
		return engineName;
	}
	/**
	 * @param engineName 要设置的 engineName
	 */
	public void setEngineName(String engineName) {
		this.engineName = engineName;
	}
	/**
	 * @return clearingLog
	 */
	public boolean isClearingLog() {
		return clearingLog;
	}
	/**
	 * @param clearingLog 要设置的 clearingLog
	 */
	public void setClearingLog(boolean clearingLog) {
		this.clearingLog = clearingLog;
	}
	/**
	 * @return safeMode
	 */
	public boolean isSafeMode() {
		return safeMode;
	}
	/**
	 * @param safeMode 要设置的 safeMode
	 */
	public void setSafeMode(boolean safeMode) {
		this.safeMode = safeMode;
	}
	/**
	 * @return gatherMetrics
	 */
	public boolean isGatherMetrics() {
		return gatherMetrics;
	}
	/**
	 * @param gatherMetrics 要设置的 gatherMetrics
	 */
	public void setGatherMetrics(boolean gatherMetrics) {
		this.gatherMetrics = gatherMetrics;
	}
	/**
	 * @return logLevel
	 */
	public String getLogLevel() {
		return logLevel;
	}
	/**
	 * @param logLevel 要设置的 logLevel
	 */
	public void setLogLevel(String logLevel) {
		this.logLevel = logLevel;
	}
	
	/**
	 * @return the startCopyName
	 */
	public String getStartCopyName() {
		return startCopyName;
	}
	/**
	 * @param  设置 startCopyName
	 */
	public void setStartCopyName(String startCopyName) {
		this.startCopyName = startCopyName;
	}
	
	/**
	 * @return the params
	 * job Key:   <br>
	 * "engineType","engineName","rebootAutoRun",  "MainTransName","OutStepName","InStepName",
	 * org.pentaho.di.core.logging.CloudLogListener   <br>
	 * org.pentaho.di.job.IncrementalParser   <br>
	 * com.ys.idatrix.cloudetl.subcribe.SubcribePushService   <br>
	 *   <br>
	 * trans Key:  <br>
	 * "engineType","engineName","rebootAutoRun"  <br>
	 * com.ys.idatrix.cloudetl.recovery.trans.ResumeTransParser  <br>
	 */
	public Map<String, String> getParams() {
		if(params ==  null) {
			params= Maps.newHashMap() ;
		}
		return params;
	}
	
	/**
	 * job Key:  <br>
	 * "engineType","engineName","rebootAutoRun",  "MainTransName","OutStepName","InStepName",  <br>
	 * org.pentaho.di.core.logging.CloudLogListener  <br>
	 * org.pentaho.di.job.IncrementalParser  <br>
	 * com.ys.idatrix.cloudetl.subcribe.SubcribePushService  <br>
	 *   <br>
	 * trans Key:  <br>
	 * "engineType","engineName","rebootAutoRun"  <br>
	 * com.ys.idatrix.cloudetl.recovery.trans.ResumeTransParser  <br>
	 */
	public void setParams(Map<String, String> params) {
		this.params = params;
	}
	/**
	 * job Key:
	 * "engineType","engineName","rebootAutoRun",  "MainTransName","OutStepName","InStepName",
	 * org.pentaho.di.core.logging.CloudLogListener
	 * org.pentaho.di.job.IncrementalParser
	 * com.ys.idatrix.cloudetl.subcribe.SubcribePushService
	 * 
	 * trans Key:
	 * "engineType","engineName","rebootAutoRun"
	 * com.ys.idatrix.cloudetl.recovery.trans.ResumeTransParser
	 */
	public void addParam(String key,String value) {
		//执行配置的优先级最高,当已经存在时不进行覆盖
		getParams().putIfAbsent(key, value);
	}
	
	/**
	 * @return the variables
	 */
	public Map<String, String> getVariables() {
		if(variables ==  null) {
			variables= Maps.newHashMap() ;
		}
		return variables;
	}
	/**
	 * @param  设置 variables
	 */
	public void setVariables(Map<String, String> variables) {
		this.variables = variables;
	}
	/**
	 * @return the rebootAutoRun
	 */
	public boolean isRebootAutoRun() {
		return rebootAutoRun;
	}
	/**
	 * @param  设置 rebootAutoRun
	 */
	public void setRebootAutoRun(boolean rebootAutoRun) {
		this.rebootAutoRun = rebootAutoRun;
	}
	
	
	/**
	 * @return the breakpointsContinue
	 */
	public boolean isBreakpointsContinue() {
		return breakpointsContinue;
	}
	/**
	 * @param  设置 breakpointsContinue
	 */
	public void setBreakpointsContinue(boolean breakpointsContinue) {
		this.breakpointsContinue = breakpointsContinue;
	}
	/**
	 * @return the breakpointsRemote
	 */
	public boolean isBreakpointsRemote() {
		return breakpointsRemote;
	}
	/**
	 * @param  设置 breakpointsRemote
	 */
	public void setBreakpointsRemote(boolean breakpointsRemote) {
		this.breakpointsRemote = breakpointsRemote;
	}
	
	
	/**
	 * @return the forceLocal
	 */
	public boolean isForceLocal() {
		return forceLocal;
	}
	/**
	 * @param  设置 forceLocal
	 */
	public void setForceLocal(boolean forceLocal) {
		this.forceLocal = forceLocal;
	}
	
	public void putParamsFromMeta(AbstractMeta meta ) throws UnknownParamException {
		
		String[] keys = Stream.concat(Stream.of(meta.listParameters()), Stream.of(getParams().keySet().toArray(new String[] {}))).distinct().toArray(String[]::new);
			for( String key : keys ) {
				//优先级高
				String valParam = getParams().get(key);
				//优先级低
				String valMeta = null;
				if( !Utils.isEmpty(meta.getParameterValue(key)) ) {
					valMeta = meta.getParameterValue(key) ;
				}
				if( Utils.isEmpty(valMeta) && !Utils.isEmpty(meta.getParameterDefault(key)) ) {
					valMeta = meta.getParameterDefault(key) ;
				}
				if( !Utils.isEmpty(valParam) ) {
					meta.setVariable(key,valParam);
				}else if( !Utils.isEmpty(valMeta) ){
					addParam(key , valMeta);
				}
			}
	}
	
	
	
	@Override
	public ExecConfigurationDto clone() throws CloneNotSupportedException {
		//实现浅拷贝
		ExecConfigurationDto c = (ExecConfigurationDto)super.clone();
		c.setParams( Maps.newHashMap(getParams()));
		c.setVariables( Maps.newHashMap(getVariables()));
		
		return c ;
	}
	/* 
	 * Build text.
	 */
	@Override
	public String toString() {
		return "TransExecConfigurationNewDto [engineType=" + engineType + ", engineName=" + engineName
				+ ", clearingLog=" + clearingLog + ", safeMode=" + safeMode + ", gatherMetrics=" + gatherMetrics
				+ ", logLevel=" + logLevel + "]";
	}
	
	public String readXml(String offsetPrefix){
		StringBuilder retval = new StringBuilder(1000);
		retval.append(offsetPrefix).append(XMLHandler.addTagValue("engineType",getEngineType()));
		retval.append(offsetPrefix).append(XMLHandler.addTagValue("engineName",getEngineName()));
		retval.append(offsetPrefix).append(XMLHandler.addTagValue("clearingLog",isClearingLog()));
		retval.append(offsetPrefix).append(XMLHandler.addTagValue("safeMode",isSafeMode()));
		retval.append(offsetPrefix).append(XMLHandler.addTagValue("gatherMetrics",isGatherMetrics()));
		retval.append(offsetPrefix).append(XMLHandler.addTagValue("logLevel",getLogLevel()));
		retval.append(offsetPrefix).append(XMLHandler.addTagValue("startCopyName",getStartCopyName()));
		retval.append(offsetPrefix).append(XMLHandler.addTagValue("rebootAutoRun",isRebootAutoRun()));
		
		if( params != null && params.size() >0) {
			String paramStr = "";
			for(String key : params.keySet()) {
				paramStr+= StringEscapeHelper.encode(key)+":"+ StringEscapeHelper.encode(params.get(key))+"|";
			}
			retval.append(offsetPrefix).append(XMLHandler.addTagValue("params",paramStr));
		}
		if( variables != null && variables.size() >0) {
			String variablesStr = "";
			for(String key : variables.keySet()) {
				variablesStr+=  StringEscapeHelper.encode(key)+":"+ StringEscapeHelper.encode(variables.get(key))+"|";
			}
			retval.append(offsetPrefix).append(XMLHandler.addTagValue("variables",variablesStr));
		}
		
		return retval.toString();
	}
	
	public void loadXml(Element configurationNode){
		
		setEngineName(StringEscapeHelper.getDom4jElementText(configurationNode,"engineName") ); //XMLHandler.getTagValue(configurationNode,"engineName"));
		setEngineType(StringEscapeHelper.getDom4jElementText(configurationNode,"engineType")); // XMLHandler.getTagValue(configurationNode,"engineType"));
		setStartCopyName(StringEscapeHelper.getDom4jElementText(configurationNode,"startCopyName")); // XMLHandler.getTagValue(configurationNode,"startCopyName"));
		setClearingLog( "Y".equals(StringEscapeHelper.getDom4jElementText(configurationNode,"clearingLog"))?true:false);
		setGatherMetrics(  "Y".equals( StringEscapeHelper.getDom4jElementText(configurationNode,"gatherMetrics") )?true:false);
		setLogLevel( StringEscapeHelper.getDom4jElementText(configurationNode,"logLevel")); // XMLHandler.getTagValue(configurationNode,"logLevel"));
		setSafeMode(  "Y".equals( StringEscapeHelper.getDom4jElementText(configurationNode,"safeMode") )?true:false);
		setRebootAutoRun(  "Y".equals( StringEscapeHelper.getDom4jElementText(configurationNode,"rebootAutoRun") )?true:false);
		
		String paramStr = StringEscapeHelper.getDom4jElementText(configurationNode,"params");//XMLHandler.getTagValue(configurationNode,"params");
		if( !Utils.isEmpty( paramStr )  && paramStr.contains("|") ) {
			Map<String, String> paramMap = getParams();
			String[] paramss = paramStr.split("\\|");
			for(String p : paramss) {
				if(Utils.isEmpty(p) || !p.contains(":")) {
					continue ;
				}
				String[] pp = p.split(":");
				paramMap.put(StringEscapeHelper.decode(pp[0]), StringEscapeHelper.decode(pp[1]));
			}
		}
		
		String variablesStr = StringEscapeHelper.getDom4jElementText(configurationNode,"variables");// XMLHandler.getTagValue(configurationNode,"variables");
		if( !Utils.isEmpty( variablesStr )  && variablesStr.contains("|") ) {
			Map<String, String> variablesMap = getVariables();
			String[] varss = variablesStr.split("\\|");
			for(String v : varss) {
				if(Utils.isEmpty(v) || !v.contains(":")) {
					continue ;
				}
				String[] vv = v.split(":");
				variablesMap.put( StringEscapeHelper.decode(vv[0]), StringEscapeHelper.decode(vv[1]));
			}
		}
		
	}
	
	public String objectToString() throws  Exception {
		
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(this) ;
	}
	
	public ExecConfigurationDto stringToObject(String obj) throws  Exception{
		ObjectMapper mapper = new ObjectMapper();
		return  mapper.readValue(obj, ExecConfigurationDto.class);
	}
	
}
