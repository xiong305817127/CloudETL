/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.dto.step.steps.bigdata;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.util.OsgiBundleUtils;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.ys.idatrix.cloudetl.dto.step.parts.FilterDefinition;
import com.ys.idatrix.cloudetl.dto.step.parts.MappedColumnDto;
import com.ys.idatrix.cloudetl.dto.step.parts.MappingDto;
import com.ys.idatrix.cloudetl.dto.step.parts.OutputFieldDefinition;
import com.ys.idatrix.cloudetl.dto.step.steps.StepParameter;
import com.ys.idatrix.cloudetl.ext.CloudSession;
import com.ys.idatrix.cloudetl.service.server.CloudServerService;
import com.ys.idatrix.cloudetl.service.trans.stepdetail.HbaseDetailService;
import com.ys.idatrix.cloudetl.toolkit.analyzer.trans.step.StepDataRelationship;
import com.ys.idatrix.cloudetl.toolkit.analyzer.trans.step.StepDataRelationshipParser;

import net.sf.json.JSONObject;

/**
 * Step - HBaseInput. 转换
 * org.pentaho.big.data.kettle.plugins.hbase.input.HBaseInputMeta
 * 
 * @author XH
 * @since 2017-08-08
 */
@Component("SPHBaseInput")
@Scope("prototype")
public class SPHBaseInput implements StepParameter, StepDataRelationshipParser {

	@Autowired
	CloudServerService cloudServerService;
	@Autowired
	HbaseDetailService hbaseDetailService ;

	private String namedClusterName;

	private String coreConfigURL =  "${"+HbaseDetailService.haddopPluginRootPathKey+"}/hbase-site.xml"; // "${haddopPluginConfigPath}/hbase-site.xml";
	private String defaultConfigURL;
	private String sourceTableName;
	private String sourceMappingName;
	private boolean saveMappingToMeta = true;
	private String keyStart;
	private String keyStop;
	private String scannerCacheSize;

	List<OutputFieldDefinition> outputFieldsDefinition;

	private boolean matchAnyFilter;
	List<FilterDefinition> filtersDefinition;

	private MappingDto mapping;

	/**
	 * @return namedClusterName
	 */
	public String getNamedClusterName() {
		return namedClusterName;
	}

	/**
	 * @param 设置
	 *            namedClusterName
	 */
	public void setNamedClusterName(String namedClusterName) {
		this.namedClusterName = namedClusterName;
	}

	/**
	 * @return coreConfigURL
	 */
	public String getCoreConfigURL() {
		return coreConfigURL;
	}

	/**
	 * @param 设置
	 *            coreConfigURL
	 */
	public void setCoreConfigURL(String coreConfigURL) {
		this.coreConfigURL = coreConfigURL;
	}

	/**
	 * @return defaultConfigURL
	 */
	public String getDefaultConfigURL() {
		return defaultConfigURL;
	}

	/**
	 * @param 设置
	 *            defaultConfigURL
	 */
	public void setDefaultConfigURL(String defaultConfigURL) {
		this.defaultConfigURL = defaultConfigURL;
	}

	/**
	 * @return sourceTableName
	 */
	public String getSourceTableName() {
		return sourceTableName;
	}

	/**
	 * @param 设置
	 *            sourceTableName
	 */
	public void setSourceTableName(String sourceTableName) {
		this.sourceTableName = sourceTableName;
	}

	/**
	 * @return sourceMappingName
	 */
	public String getSourceMappingName() {
		return sourceMappingName;
	}

	/**
	 * @param 设置
	 *            sourceMappingName
	 */
	public void setSourceMappingName(String sourceMappingName) {
		this.sourceMappingName = sourceMappingName;
	}

	public boolean isSaveMappingToMeta() {
		return saveMappingToMeta;
	}

	public void setSaveMappingToMeta(boolean saveMappingToMeta) {
		this.saveMappingToMeta = saveMappingToMeta;
	}

	/**
	 * @return keyStart
	 */
	public String getKeyStart() {
		return keyStart;
	}

	/**
	 * @param 设置
	 *            keyStart
	 */
	public void setKeyStart(String keyStart) {
		this.keyStart = keyStart;
	}

	/**
	 * @return keyStop
	 */
	public String getKeyStop() {
		return keyStop;
	}

	/**
	 * @param 设置
	 *            keyStop
	 */
	public void setKeyStop(String keyStop) {
		this.keyStop = keyStop;
	}

	/**
	 * @return scannerCacheSize
	 */
	public String getScannerCacheSize() {
		return scannerCacheSize;
	}

	/**
	 * @param 设置
	 *            scannerCacheSize
	 */
	public void setScannerCacheSize(String scannerCacheSize) {
		this.scannerCacheSize = scannerCacheSize;
	}

	/**
	 * @return outputFieldsDefinition
	 */
	public List<OutputFieldDefinition> getOutputFieldsDefinition() {
		return outputFieldsDefinition;
	}

	/**
	 * @param 设置
	 *            outputFieldsDefinition
	 */
	public void setOutputFieldsDefinition(List<OutputFieldDefinition> outputFieldsDefinition) {
		this.outputFieldsDefinition = outputFieldsDefinition;
	}

	/**
	 * @return filtersDefinition
	 */
	public List<FilterDefinition> getFiltersDefinition() {
		return filtersDefinition;
	}

	/**
	 * @param 设置
	 *            filtersDefinition
	 */
	public void setFiltersDefinition(List<FilterDefinition> filtersDefinition) {
		this.filtersDefinition = filtersDefinition;
	}

	/**
	 * @return matchAnyFilter
	 */
	public boolean isMatchAnyFilter() {
		return matchAnyFilter;
	}

	/**
	 * @param 设置
	 *            matchAnyFilter
	 */
	public void setMatchAnyFilter(boolean matchAnyFilter) {
		this.matchAnyFilter = matchAnyFilter;
	}

	public MappingDto getMapping() {
		return mapping;
	}

	public void setMapping(MappingDto mapping) {
		this.mapping = mapping;
	}

	@Override
	public void initParamObject(StepMeta stepMeta) {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		
		String siteConfigStr = "${"+HbaseDetailService.haddopPluginRootPathKey+"}/hbase-site.xml";
		OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "setCoreConfigURL", siteConfigStr );
		
	}

	/*
	 * 根据传递过来的JSON对象，获取封装后的参数对象
	 */
	@Override
	public Object getParameterObject(Object json) {
		JSONObject jsonObj = JSONObject.fromObject(json);

		Map<String, Class<?>> classMap = new HashMap<>();
		classMap.put("mappedColumns", MappedColumnDto.class);
		classMap.put("outputFieldsDefinition", OutputFieldDefinition.class);
		classMap.put("filtersDefinition", FilterDefinition.class);
		classMap.put("mapping", MappingDto.class);

		return (SPHBaseInput) JSONObject.toBean(jsonObj, SPHBaseInput.class, classMap);
	}

	/*
	 * 把插件Meta编码成参数对象，其中插件Meta由OSGI容器加载，需用反射方式调用成员方法
	 */
	@Override
	public Object encodeParameterObject(StepMeta stepMeta) {
		//设置hadddop插件默认路径
		String haddopPluginRootPath = hbaseDetailService.getHaddopPluginRootPath() ;
				
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPHBaseInput spHBaseInput = new SPHBaseInput();
		// HBaseInputMeta hbaseinputmeta= (HBaseInputMeta )stepMetaInterface;

		Object namedCluster = OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getNamedCluster");
		spHBaseInput.setNamedClusterName( namedCluster == null ? "" : (String) OsgiBundleUtils.invokeOsgiMethod(namedCluster, "getName"));// hbaseinputmeta.getNamedCluster().getName()
		

		String siteConfig = (String) OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getCoreConfigURL") ;
		if( !Utils.isEmpty(siteConfig) && siteConfig.contains(haddopPluginRootPath) ) {
			siteConfig = siteConfig.replace( haddopPluginRootPath , "${"+HbaseDetailService.haddopPluginRootPathKey+"}") ;
		}
		
		String defaultConfig = (String) OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getDefaultConfigURL") ;
		if( !Utils.isEmpty(defaultConfig) && defaultConfig.contains(haddopPluginRootPath) ) {
			defaultConfig = defaultConfig.replace(haddopPluginRootPath , "${"+HbaseDetailService.haddopPluginRootPathKey+"}" ) ;
		}
		
		spHBaseInput.setDefaultConfigURL( defaultConfig );// hbaseinputmeta.getDefaultConfigURL());
		spHBaseInput.setCoreConfigURL( siteConfig );// hbaseoutputmeta.getCoreConfigURL());

		spHBaseInput.setSourceTableName((String) OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getSourceTableName"));// hbaseinputmeta.getSourceTableName());
		spHBaseInput.setSourceMappingName( (String) OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getSourceMappingName"));// hbaseinputmeta.getSourceMappingName());
		spHBaseInput.setMatchAnyFilter((boolean) OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getMatchAnyFilter"));// hbaseinputmeta.getMatchAnyFilter());
		spHBaseInput.setScannerCacheSize( (String) OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getScannerCacheSize"));// hbaseinputmeta.getScannerCacheSize());
		spHBaseInput.setKeyStart((String) OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getKeyStartValue"));// hbaseinputmeta.getKeyStartValue())
		spHBaseInput.setKeyStop((String) OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getKeyStopValue"));// hbaseinputmeta.getKeyStopValue())

		Object mapping = OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getMapping");// hbaseoutputmeta.getMapping();
		if (mapping != null) {
			spHBaseInput.setSaveMappingToMeta(true);
			MappingDto mappingDto = new MappingDto();
			mappingDto.transFromHbaseMapping(mapping);
			spHBaseInput.setMapping(mappingDto);
		}else {
			spHBaseInput.setSaveMappingToMeta(false);
		}
		
		// com.ys.idatrix.cloudetl.dto.step.parts.OutputFieldDefinition
		if (OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getOutputFields") != null) { // hbaseinputmeta.getOutputFields()
			List<?> outfdList = (List<?>) OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getOutputFields");
			List<OutputFieldDefinition> ofdList = Lists.newArrayList();
			outfdList.stream().forEach(ofd -> {
				OutputFieldDefinition outfd = new OutputFieldDefinition();
				outfd.setAlias((String) OsgiBundleUtils.invokeOsgiMethod(ofd, "getAlias"));// ofd.getAlias());
				outfd.setColumnName((String) OsgiBundleUtils.invokeOsgiMethod(ofd, "getColumnName"));// ofd.getColumnName());
				outfd.setFamily((String) OsgiBundleUtils.invokeOsgiMethod(ofd, "getColumnFamily"));// ofd.getColumnFamily());
				outfd.setFormat((String) OsgiBundleUtils.invokeOsgiMethod(ofd, "getConversionMask"));// ofd.getConversionMask());
				outfd.setHbaseType((String) OsgiBundleUtils.invokeOsgiMethod(ofd, "getHBaseTypeDesc"));// ofd.getHBaseTypeDesc());
				outfd.setKeyword((boolean) OsgiBundleUtils.invokeOsgiMethod(ofd, "isKey"));// ofd.isKey());
				ofdList.add(outfd);
			});

			spHBaseInput.setOutputFieldsDefinition(ofdList);
		}
		if (OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getColumnFilters") != null) { // hbaseinputmeta.getColumnFilters()
			List<?> filterdList = (List<?>) OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getColumnFilters");
			List<FilterDefinition> ofdList = Lists.newArrayList();
			filterdList.stream().forEach(fd -> {
				FilterDefinition filterd = new FilterDefinition();
				filterd.setAlias((String) OsgiBundleUtils.invokeOsgiMethod(fd, "getFieldAlias"));// fd.getFieldAlias());
				filterd.setComparisonType(OsgiBundleUtils.invokeOsgiMethod(fd, "getComparisonOperator").toString());// fd.getComparisonOperator().toString());
				filterd.setConstant((String) OsgiBundleUtils.invokeOsgiMethod(fd, "getConstant"));// fd.getConstant());
				filterd.setFieldType((String) OsgiBundleUtils.invokeOsgiMethod(fd, "getFieldType"));// fd.getFieldType());
				filterd.setFormat((String) OsgiBundleUtils.invokeOsgiMethod(fd, "getFormat"));// fd.getFormat());
				filterd.setSignedComparison((boolean) OsgiBundleUtils.invokeOsgiMethod(fd, "getSignedComparison"));// fd.getSignedComparison());
				ofdList.add(filterd);
			});

			spHBaseInput.setFiltersDefinition(ofdList);
		}

		return spHBaseInput;
	}

	/*
	 * 解码传递过来的参数对象，装入插件Meta中，其中插件Meta由OSGI容器加载，需用反射方式调用成员方法
	 */
	@Override
	public void decodeParameterObject(StepMeta stepMeta, Object po, List<DatabaseMeta> databases, TransMeta transMeta) throws Exception {
		//设置hadddop插件默认路径
		String haddopPluginRootPath = hbaseDetailService.getHaddopPluginRootPath() ;
		transMeta.setVariable(HbaseDetailService.haddopPluginRootPathKey, haddopPluginRootPath );
		
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPHBaseInput spHBaseInput = (SPHBaseInput) po;
		// HBaseInputMeta hbaseinputmeta= (HBaseInputMeta )stepMetaInterface;

		if (!StringUtils.isEmpty(spHBaseInput.getNamedClusterName())) {
			// hbaseoutputmeta.setNamedCluster(
			// hbaseoutputmeta.getNamedClusterService().getNamedClusterByName(spHBaseOutput.getNamedClusterName(),
			// CloudSession.getMetaStore()) ) ;
			Object namedClusterService = OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getNamedClusterService");
			Object namedCluster = OsgiBundleUtils.invokeOsgiMethod(namedClusterService, "getNamedClusterByName", spHBaseInput.getNamedClusterName(), CloudSession.getMetaStore());
			OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "setNamedCluster", namedCluster);
		}
		
		String siteConfig = spHBaseInput.getCoreConfigURL() ;
		if( Utils.isEmpty(siteConfig) ) {
			siteConfig = haddopPluginRootPath+"/hbase-site.xml" ;
		}else if( siteConfig.contains(HbaseDetailService.haddopPluginRootPathKey)){
			siteConfig = siteConfig.replace("${"+HbaseDetailService.haddopPluginRootPathKey+"}", haddopPluginRootPath) ;
		}
		
		String defaultConfig = spHBaseInput.getDefaultConfigURL() ;
		if( !Utils.isEmpty(defaultConfig) && defaultConfig.contains(HbaseDetailService.haddopPluginRootPathKey)) {
			defaultConfig = defaultConfig.replace("${"+HbaseDetailService.haddopPluginRootPathKey+"}", haddopPluginRootPath ) ;
		}

		OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "setCoreConfigURL", siteConfig );// hbaseoutputmeta.setCoreConfigURL(spHBaseOutput.getCoreConfigURL());
		OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "setDefaulConfigURL", defaultConfig);// hbaseoutputmeta.setDefaulConfigURL(spHBaseOutput.getDefaultConfigURL()
		OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "setSourceTableName", spHBaseInput.getSourceTableName());// hbaseinputmeta.setSourceTableName(spHBaseInput.getSourceTableName());
		OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "setSourceMappingName", spHBaseInput.getSourceMappingName());// hbaseinputmeta.setSourceMappingName(spHBaseInput.getSourceMappingName());
		OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "setMatchAnyFilter", spHBaseInput.isMatchAnyFilter());// hbaseinputmeta.setMatchAnyFilter(spHBaseInput.isMatchAnyFilter());
		OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "setScannerCacheSize", spHBaseInput.getScannerCacheSize());// hbaseinputmeta.setScannerCacheSize(spHBaseInput.getScannerCacheSize());
		OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "setKeyStartValue", spHBaseInput.getKeyStart());// hbaseinputmeta.setKeyStartValue(spHBaseInput.getKeyStart()
		OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "setKeyStopValue", spHBaseInput.getKeyStop());// hbaseinputmeta.setKeyStopValue(spHBaseInput.getKeyStop()
																											// ) ;
		MappingDto mappingDto = null;
		if (spHBaseInput.getMapping() != null) {
			mappingDto = spHBaseInput.getMapping() ;
		}else if( spHBaseInput.isSaveMappingToMeta() &&  !Utils.isEmpty( spHBaseInput.getNamedClusterName()) && !Utils.isEmpty( spHBaseInput.getSourceTableName()) && !Utils.isEmpty( spHBaseInput.getSourceMappingName())) {
			mappingDto = hbaseDetailService.getHbaseMappingInfo(spHBaseInput.getNamedClusterName(),spHBaseInput.getCoreConfigURL(), spHBaseInput.getDefaultConfigURL(), spHBaseInput.getSourceTableName(), spHBaseInput.getSourceMappingName());
		}
		if( mappingDto != null && !Utils.isEmpty(mappingDto.getTableName()) && !Utils.isEmpty(mappingDto.getMappingName())  ) {
			Object hbaseService = OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getService");
			Object tempMapping = mappingDto.transToHbaseMapping(hbaseService);
			OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "setMapping", tempMapping);// hbaseoutputmeta.setMapping(tempMapping);
		}
		
		if (spHBaseInput.getOutputFieldsDefinition() != null) {
			List<Object> ofdList = Lists.newArrayList();
			spHBaseInput.getOutputFieldsDefinition().stream().forEach(ofd -> {
				Object outfd = OsgiBundleUtils.newOsgiInstance(stepMetaInterface, "org.pentaho.big.data.kettle.plugins.hbase.input.OutputFieldDefinition");// org.pentaho.big.data.kettle.plugins.hbase.input.OutputFieldDefinition
																									// outfd= new
																									// org.pentaho.big.data.kettle.plugins.hbase.input.OutputFieldDefinition();
				OsgiBundleUtils.invokeOsgiMethod(outfd, "setAlias", ofd.getAlias());// outfd.setAlias(ofd.getAlias());
				OsgiBundleUtils.invokeOsgiMethod(outfd, "setColumnName", ofd.getColumnName());// outfd.setColumnName(ofd.getColumnName());
				OsgiBundleUtils.invokeOsgiMethod(outfd, "setFamily", ofd.getFamily());// outfd.setFamily(ofd.getFamily());
				OsgiBundleUtils.invokeOsgiMethod(outfd, "setFormat", ofd.getFormat());// outfd.setFormat(ofd.getFormat());
				OsgiBundleUtils.invokeOsgiMethod(outfd, "setHbaseType", ofd.getHbaseType());// outfd.setHbaseType(ofd.getHbaseType());
				OsgiBundleUtils.invokeOsgiMethod(outfd, "setKey", ofd.isKeyword());// outfd.setKey(ofd.isKey());
				ofdList.add(outfd);
			});

			OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "setOutputFieldsDefinition", new Object[] {ofdList} ,new Class[] {List.class});// hbaseinputmeta.setOutputFieldsDefinition(ofdList);
		}

		if (spHBaseInput.getFiltersDefinition() != null) {
			List<Object> ofdList = Lists.newArrayList();
			;// List<org.pentaho.big.data.kettle.plugins.hbase.FilterDefinition>
				// ofdList=Lists.newArrayList();
			for (FilterDefinition fd : spHBaseInput.getFiltersDefinition()) {
				Object filterd = OsgiBundleUtils.newOsgiInstance(stepMetaInterface,
						"org.pentaho.big.data.kettle.plugins.hbase.FilterDefinition");// org.pentaho.big.data.kettle.plugins.hbase.FilterDefinition
																						// filterd= new
																						// org.pentaho.big.data.kettle.plugins.hbase.FilterDefinition();
				OsgiBundleUtils.invokeOsgiMethod(filterd, "setAlias", fd.getAlias());// filterd.setAlias(fd.getAlias());
				OsgiBundleUtils.invokeOsgiMethod(filterd, "setConstant", fd.getConstant());// filterd.setConstant(fd.getConstant());
				OsgiBundleUtils.invokeOsgiMethod(filterd, "setFieldType", fd.getFieldType());// filterd.setFieldType(fd.getFieldType());
				OsgiBundleUtils.invokeOsgiMethod(filterd, "setFormat", fd.getFormat());// filterd.setFormat(fd.getFormat());
				OsgiBundleUtils.invokeOsgiMethod(filterd, "setSignedComparison", fd.isSignedComparison());// filterd.setSignedComparison(fd.isSignedComparison());

				Object comType = OsgiBundleUtils.invokeOsgiMethod( stepMetaInterface.getClass().getClassLoader().loadClass("org.pentaho.bigdata.api.hbase.mapping.ColumnFilter$ComparisonType"), "stringToOpp", fd.getComparisonType());
				OsgiBundleUtils.invokeOsgiMethod(filterd, "setComparisonType", comType);// filterd.setComparisonType(ComparisonType.stringToOpp(fd.getComparisonType()));

				ofdList.add(filterd);
			}

			OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "setFiltersDefinition", ofdList);// hbaseinputmeta.setFiltersDefinition(ofdList);
		}

		
	}

	/*
	 * 覆盖方法：getStepDataAndRelationship
	 */
	@Override
	public void getStepDataAndRelationship(TransMeta transMeta, StepMeta stepMeta, StepDataRelationship sdr) {
		//TODO
	}

}
