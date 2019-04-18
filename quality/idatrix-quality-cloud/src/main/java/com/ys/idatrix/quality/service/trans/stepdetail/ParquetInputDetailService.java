/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.quality.service.trans.stepdetail;

import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.vfs2.FileObject;
import org.pentaho.di.core.plugins.PluginInterface;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.core.plugins.StepPluginType;
import org.pentaho.di.core.row.value.ValueMetaFactory;
import org.pentaho.di.core.util.OsgiBundleUtils;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.core.vfs.KettleVFS;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.hadoop.shim.api.format.SchemaDescription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ys.idatrix.quality.ext.CloudSession;

/**
 * TextInput related Detail Service
 * 
 * @author XH
 * @since 2017年6月9日
 *
 */
@Service
public class ParquetInputDetailService implements StepDetailService {

	@Autowired
	HadoopFileInputDetailService hadoopFileInputDetailService;

	@Override
	public String getStepDetailType() {
		return "ParquetInput";
	}

	/**
	 * flag : getFields
	 * 
	 * @throws Exception
	 */
	@Override
	public Object dealStepDetailByflag(String flag, Map<String, Object> param) throws Exception {
		if (StringUtils.isEmpty(flag)) {
			return null;
		}

		switch (flag) {
		case "getFields":
			return getFields(param);
		default:
			return null;

		}

	}

	/**
	 * @param inputFiles
	 *            content
	 * @return Text Fields list
	 * @throws Exception
	 */
	private List<Map<String,String>> getFields(Map<String, Object> params) throws Exception {

		checkDetailParam(params, "sourceConfigurationName", "fileName");

		String owner =  params.get("owner") == null || Utils.isEmpty( (String)params.get("owner"))  ? CloudSession.getResourceUser() : (String)params.get("owner");
		String fileName = params.get("fileName").toString();
		String sourceConfigurationName = params.get("sourceConfigurationName").toString();

		String hadoopUrl;
		if(!"local".equalsIgnoreCase(sourceConfigurationName)) {
			hadoopUrl = hadoopFileInputDetailService.getConnectPath(owner,sourceConfigurationName, fileName);
		}else {
			FileObject loaclfile = KettleVFS.getFileObject(fileName);
			hadoopUrl =  loaclfile.getURL().toString();
			loaclfile.close();
		}

		PluginRegistry registry = PluginRegistry.getInstance();
		PluginInterface sp = registry.findPluginWithId(StepPluginType.class, "ParquetInput");
		StepMetaInterface parquetInput = (StepMetaInterface) registry.loadClass(sp);

		List<Map<String,String>> result = Lists.newArrayList();
		
		//SchemaDescription schema = ParquetInput.retrieveSchema(parquetInput.namedClusterServiceLocator, parquetInput.getNamedCluster(), hadoopUrl.trim());
		Object params1 = OsgiBundleUtils.getOsgiField(parquetInput, "namedClusterServiceLocator", true);
		Object params2 = OsgiBundleUtils.invokeOsgiMethod( parquetInput, "getNamedCluster");
		SchemaDescription schema = (SchemaDescription) OsgiBundleUtils.invokeOsgiMethod(parquetInput.getClass().getClassLoader().loadClass("org.pentaho.big.data.kettle.plugins.formats.impl.parquet.input.ParquetInput"), "retrieveSchema", params1,params2,hadoopUrl.trim());
		if(schema != null ) {
			for (SchemaDescription.Field f : schema) {
				Map<String,String> map = Maps.newHashMap() ;
		
				map.put("fieldName", f.formatFieldName);
				map.put("type", ValueMetaFactory.getValueMetaName(f.pentahoValueMetaType));
				
				result.add(map);
			}
		}
		
		return result;

	}

}
