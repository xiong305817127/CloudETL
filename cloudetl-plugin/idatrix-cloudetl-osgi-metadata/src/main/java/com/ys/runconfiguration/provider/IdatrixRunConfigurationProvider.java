/*
 * *****************************************************************************
 *
 *  Pentaho Data Integration
 *
 *  Copyright (C) 2002-2017 by Hitachi Vantara : http://www.pentaho.com
 *
 *  *******************************************************************************
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use
 *  this file except in compliance with the License. You may obtain a copy of the
 *  License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 * *****************************************************************************
 *
 */

package com.ys.runconfiguration.provider;

import org.pentaho.di.core.util.OsgiBundleUtils;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.engine.configuration.api.RunConfiguration;
import org.pentaho.di.engine.configuration.api.RunConfigurationProvider;
import org.pentaho.di.engine.configuration.impl.pentaho.DefaultRunConfiguration;
import org.pentaho.di.engine.configuration.impl.pentaho.DefaultRunConfigurationExecutor;
import org.pentaho.di.engine.configuration.impl.pentaho.DefaultRunConfigurationProvider;
import org.pentaho.osgi.metastore.locator.api.MetastoreLocator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by bmorrise on 3/16/17.
 */
public class IdatrixRunConfigurationProvider extends DefaultRunConfigurationProvider implements RunConfigurationProvider {
 
Object defaultEngineService = null;
	
  public IdatrixRunConfigurationProvider( MetastoreLocator metastoreLocator,
                                          DefaultRunConfigurationExecutor defaultRunConfigurationExecutor ) {
    super( metastoreLocator ,defaultRunConfigurationExecutor);
    initService();
	
  }
  
  @SuppressWarnings({ "rawtypes", "unchecked" })
@Override 
  public List<RunConfiguration> load() {
	    List<RunConfiguration> runConfigurations = new ArrayList<>();
	    if(defaultEngineService ==  null) {
			  initService();
		  }
	    if(defaultEngineService != null ){
	    	String user = Utils.getCloudResourceUser();
	    	Map<String,List> enginesMap = (Map<String, List>) OsgiBundleUtils.invokeOsgiMethod(defaultEngineService, "getDefaultEngineList",user);
	    	if( enginesMap != null && enginesMap.containsKey(user)) {
	    		List engines = enginesMap.get(user);
	    		if(engines != null && engines.size() >0) {
		    		engines.stream().forEach(obj -> {
		    			Map<String,Object> engine = (Map<String, Object>) OsgiBundleUtils.invokeOsgiMethod(obj, "getRunConfigurationMap");
						
						DefaultRunConfiguration defaultRunConfiguration = new DefaultRunConfiguration();
						defaultRunConfiguration.setName((String)engine.get("name"));
						defaultRunConfiguration.setServer((String)engine.get("server"));
						defaultRunConfiguration.setDescription((String)engine.get("description"));
						defaultRunConfiguration.setClustered((boolean)engine.get("clustered"));
						defaultRunConfiguration.setReadOnly((boolean)engine.get("readOnly"));
						defaultRunConfiguration.setSendResources((boolean)engine.get("sendResources"));
						defaultRunConfiguration.setLogRemoteExecutionLocally((boolean)engine.get("logRemoteExecutionLocally"));
						defaultRunConfiguration.setRemote((boolean)engine.get("remote"));
						defaultRunConfiguration.setLocal((boolean)engine.get("local"));
						defaultRunConfiguration.setShowTransformations((boolean)engine.get("showTransformations"));
						
						runConfigurations.add(defaultRunConfiguration);
		    		});
		    	}
	    		
	    	}
	    }
	    runConfigurations.addAll( super.load() );
	    return runConfigurations;
	  }

  @SuppressWarnings("unchecked")
@Override 
  public RunConfiguration load( String name ) {
	  if(defaultEngineService ==  null) {
		  initService();
	  }
	  
		if(defaultEngineService != null ){
			String user = Utils.getCloudResourceUser();
			Object defaultEngine = OsgiBundleUtils.invokeOsgiMethod(defaultEngineService, "findDefaultEngine",user , name);
			if(defaultEngine !=  null ) {
				Map<String,Object> engine = (Map<String, Object>) OsgiBundleUtils.invokeOsgiMethod(defaultEngine, "getRunConfigurationMap");
				
				DefaultRunConfiguration defaultRunConfiguration = new DefaultRunConfiguration();
				defaultRunConfiguration.setName((String)engine.get("name"));
				defaultRunConfiguration.setServer((String)engine.get("server"));
				defaultRunConfiguration.setDescription((String)engine.get("description"));
				defaultRunConfiguration.setClustered((boolean)engine.get("clustered"));
				defaultRunConfiguration.setReadOnly((boolean)engine.get("readOnly"));
				defaultRunConfiguration.setSendResources((boolean)engine.get("sendResources"));
				defaultRunConfiguration.setLogRemoteExecutionLocally((boolean)engine.get("logRemoteExecutionLocally"));
				defaultRunConfiguration.setRemote((boolean)engine.get("remote"));
				defaultRunConfiguration.setLocal((boolean)engine.get("local"));
				defaultRunConfiguration.setShowTransformations((boolean)engine.get("showTransformations"));
				
				return defaultRunConfiguration;
				
			}
		}
		return super.load( name );
  }
  
  

  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Override 
  public List<String> getNames() {
    List<String> names = new ArrayList<>();
    if(defaultEngineService ==  null) {
		  initService();
	  }
    if(defaultEngineService != null ){
    	String user = Utils.getCloudResourceUser();
    	Map<String,List> enginesMap = (Map<String, List>) OsgiBundleUtils.invokeOsgiMethod(defaultEngineService, "getDefaultEngineList",user);
    	if( enginesMap != null && enginesMap.containsKey(user)) {
    		List engines = enginesMap.get(user);
    		if(engines != null && engines.size() >0) {
        		engines.stream().forEach(obj -> {
        			String name = (String) OsgiBundleUtils.invokeOsgiMethod(obj, "getName");
        			if(!Utils.isEmpty(name) ) {
        				names.add(name);
        			}
        		});
        	}
    	}
    }
    names.addAll( super.getNames() );
    return names;
  }

 

  @Override 
  public List<String> getNames( String type ) {
    return isSupported( type ) ? getNames() : Collections.emptyList();
  }

  
  private void initService() {
	  try {
		  Class<?> pluginFactory =  IdatrixRunConfigurationProvider.class.getClassLoader().getParent().loadClass(Utils.getPackageName("com.ys.idatrix.cloudetl.ext.PluginFactory"));
		  defaultEngineService = OsgiBundleUtils.invokeOsgiMethod( pluginFactory , "getBean","CloudDefaultEngineService");
		} catch (ClassNotFoundException e) {
		}
  }
 
}
