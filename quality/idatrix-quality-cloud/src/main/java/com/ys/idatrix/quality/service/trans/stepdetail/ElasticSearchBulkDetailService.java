/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.quality.service.trans.stepdetail;

import java.net.InetAddress;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.plugins.PluginInterface;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.core.plugins.StepPluginType;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ys.idatrix.quality.dto.common.ReturnCodeDto;

import org.pentaho.di.core.util.OsgiBundleUtils;


/**
 * TextInput related Detail Service
 * 
 * @author XH
 * @since 2017年6月9日
 *
 */
@Service
public class ElasticSearchBulkDetailService implements StepDetailService {

	ClassLoader classLoader;

	@Override
	public String getStepDetailType() {
		return "ElasticSearchBulk";
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
		case "test":
			return test(param);
		default:
			return null;

		}

	}

	/**
	 * @param params - inputFiles content
	 * @return Text Fields list
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Object test(Map<String, Object> params) throws Exception {

		checkDetailParam(params, "servers");
	    
		PluginRegistry registry = PluginRegistry.getInstance();
		PluginInterface stepPlugin = registry.findPluginWithId(StepPluginType.class, "ElasticSearchBulk");
		if (stepPlugin != null) {
			StepMetaInterface info = (StepMetaInterface) registry.loadClass(stepPlugin);
			 // Now ensure that the thread's context class loader is the plugin's classloader
			classLoader = info.getClass().getClassLoader() ;
		}
		if(classLoader == null){
			throw new KettleException(" 插件 ElasticSearch Bulk未找到!");
		}
		

		List<Map> serversList = (List<Map>) params.get("servers");
		if(serversList == null  || serversList.size() ==0){
			throw new KettleException(" servers is null!");
		}
		List servers =Lists.newArrayList();
		for(  Map server : serversList){
			if( server.get("address") == null || "".equals(server.get("address"))){
				continue ;
			}
			servers.add(OsgiBundleUtils.newOsgiInstance(classLoader.loadClass("org.elasticsearch.common.transport.InetSocketTransportAddress"), null, new Class[]{InetAddress.class,int.class},new Object[]{InetAddress.getByName( (String)server.get("address") ),((Integer)server.get("port")).intValue() }));// new org.elasticsearch.common.transport.InetSocketTransportAddress( InetAddress.getByName( (String)server.get("address") ), (int)server.get("port") ));
		}

		String testType="cluster";
		String index = (String) params.get("index");
		if(!StringUtils.isEmpty(index)){
			testType="index";
		}
		Map<String, String> settings=Maps.newHashMap();
		if( params.containsKey("settings") && params.get("settings")!=null){
			List<Map> settingsMap =  (List<Map>) params.get("settings");
			for(  Map setting : settingsMap){
				if( setting.get("setting") == null || "".equals(setting.get("setting"))){
					continue ;
				}
				settings.put((String)setting.get("setting") , (String)setting.get("value") );
			}
		}
		String result = testType(testType, servers, index, settings);
		if(!StringUtils.isEmpty(result)){
			return new ReturnCodeDto(0,result);
		}
		return new ReturnCodeDto(-1,"error");
	}

	private String testType(String testType, List<Object> servers, String index, Map<String, String> settings) throws Exception {


		Object node = null;//org.elasticsearch.node.Node node = null;
		Object client = null;//org.elasticsearch.client.Client client = null;
		try {

			//org.elasticsearch.common.settings.Settings.Builder settingsBuilder = org.elasticsearch.common.settings.Settings.settingsBuilder();
			Object settingsBuilder = OsgiBundleUtils.invokeOsgiMethod(classLoader.loadClass("org.elasticsearch.common.settings.Settings"), "settingsBuilder");
			//settingsBuilder.put(org.elasticsearch.common.settings.Settings.Builder.EMPTY_SETTINGS); 
			OsgiBundleUtils.invokeOsgiMethod(settingsBuilder, "put", OsgiBundleUtils.getOsgiField(classLoader.loadClass("org.elasticsearch.common.settings.Settings$Builder"), "EMPTY_SETTINGS", false));
			//settingsBuilder.put(settings);
			OsgiBundleUtils.invokeOsgiMethod(settingsBuilder, "put", new Object[] {settings} , new Class[] {Map.class});
			
			//org.elasticsearch.client.transport.TransportClient.Builder tClientBuilder = org.elasticsearch.client.transport.TransportClient.builder().settings(settingsBuilder);
			Object tClientBuilder = OsgiBundleUtils.invokeOsgiMethod(OsgiBundleUtils.invokeOsgiMethod(classLoader.loadClass("org.elasticsearch.client.transport.TransportClient"), "builder"), "settings", settingsBuilder);

			if (!servers.isEmpty()) {
				node = null;
				Object tClient = OsgiBundleUtils.invokeOsgiMethod(tClientBuilder, "build");//org.elasticsearch.client.transport.TransportClient tClient = tClientBuilder.build();
				for (Object s : servers) {
					OsgiBundleUtils.invokeOsgiMethod(tClient, "addTransportAddress", s);//tClient.addTransportAddress(s);
				}
				client = tClient;
			} else {
				Object nodeBuilder = OsgiBundleUtils.invokeOsgiMethod(classLoader.loadClass("org.elasticsearch.node.NodeBuilder"), "nodeBuilder");//org.elasticsearch.node.NodeBuilder nodeBuilder = org.elasticsearch.node.NodeBuilder.nodeBuilder();
				OsgiBundleUtils.invokeOsgiMethod(nodeBuilder, "settings", settingsBuilder);//nodeBuilder.settings(settingsBuilder);
				node =OsgiBundleUtils.invokeOsgiMethod(OsgiBundleUtils.invokeOsgiMethod(nodeBuilder, "client", true), "node");//node = nodeBuilder.client(true).node();
				client = OsgiBundleUtils.invokeOsgiMethod(node, "client");//client = node.client();
				OsgiBundleUtils.invokeOsgiMethod(node, "start");//node.start();
			}

			Object admin = OsgiBundleUtils.invokeOsgiMethod(client, "admin");//org.elasticsearch.client.AdminClient admin = client.admin();

			switch (testType) {
			case "index":
				if (StringUtils.isBlank(index)) {
					throw new KettleException(" test index,but index is null!");
				}
				// First check to see if the index exists
				Object indicesExistBld = OsgiBundleUtils.invokeOsgiMethod(OsgiBundleUtils.invokeOsgiMethod(admin, "indices"),"prepareExists",new Object[]{new String[]{index}});//org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequestBuilder indicesExistBld = admin.indices().prepareExists(index);
				Object indicesExistResponse = OsgiBundleUtils.invokeOsgiMethod(OsgiBundleUtils.invokeOsgiMethod(indicesExistBld, "execute"),"get");//org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse indicesExistResponse = indicesExistBld.execute().get();
				
				if(!(boolean)OsgiBundleUtils.invokeOsgiMethod(indicesExistResponse,"isExists")){//if (!indicesExistResponse.isExists()) {
					return " Index "+index+"is not exist!";
				}

				Object indicesBld=OsgiBundleUtils.invokeOsgiMethod(OsgiBundleUtils.invokeOsgiMethod(admin, "indices"),"prepareRecoveries",index);//org.elasticsearch.action.admin.indices.recovery.RecoveryRequestBuilder indicesBld = admin.indices().prepareRecoveries(index);
				Object lafInd = OsgiBundleUtils.invokeOsgiMethod(indicesBld, "execute");//org.elasticsearch.action.ListenableActionFuture<org.elasticsearch.action.admin.indices.recovery.RecoveryResponse> lafInd = indicesBld.execute();
//				String shards = "" + lafInd.get().getSuccessfulShards() + "/" + lafInd.get().getTotalShards();
				String shards = "" +OsgiBundleUtils.invokeOsgiMethod(OsgiBundleUtils.invokeOsgiMethod(lafInd, "get"),"getSuccessfulShards") + "/" + OsgiBundleUtils.invokeOsgiMethod(OsgiBundleUtils.invokeOsgiMethod(lafInd, "get"),"getTotalShards");
				return "Index found (" + shards + " shards)";
			case "cluster":
				Object clusterBld = OsgiBundleUtils.invokeOsgiMethod(OsgiBundleUtils.invokeOsgiMethod(admin, "cluster"),"prepareState");//org.elasticsearch.action.admin.cluster.state.ClusterStateRequestBuilder clusterBld = admin.cluster() .prepareState();
				Object lafClu = OsgiBundleUtils.invokeOsgiMethod(clusterBld, "execute");//org.elasticsearch.action.ListenableActionFuture<org.elasticsearch.action.admin.cluster.state.ClusterStateResponse> lafClu = clusterBld.execute();
				Object cluResp = OsgiBundleUtils.invokeOsgiMethod(lafClu, "actionGet");//org.elasticsearch.action.admin.cluster.state.ClusterStateResponse cluResp = lafClu.actionGet();
				Object name =OsgiBundleUtils.invokeOsgiMethod(OsgiBundleUtils.invokeOsgiMethod(cluResp, "getClusterName"),"value");//String name = cluResp.getClusterName().value();
				Object cluState = OsgiBundleUtils.invokeOsgiMethod(cluResp, "getState");//org.elasticsearch.cluster.ClusterState cluState = cluResp.getState();
				Object numNodes = OsgiBundleUtils.invokeOsgiMethod(OsgiBundleUtils.invokeOsgiMethod(cluState, "getNodes"),"size");//int numNodes = cluState.getNodes().size();
				return "Connected to cluster '" + name + "' (" + numNodes + " nodes)";
			default:
				throw new KettleException(" testType " + testType + " 不合法!");
			}
		} finally {
			if (client != null) {
				OsgiBundleUtils.invokeOsgiMethod(client,"close");//client.close();
			}
			if (node != null) {
				OsgiBundleUtils.invokeOsgiMethod(node,"close");//node.close();
			}


		}
	}

}
