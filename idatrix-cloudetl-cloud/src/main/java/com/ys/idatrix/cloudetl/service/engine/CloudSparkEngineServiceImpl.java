/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.service.engine;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.hadoop.metrics2.sink.relocated.google.common.collect.Maps;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.metastore.api.IMetaStore;
import org.pentaho.metastore.api.exceptions.MetaStoreException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ys.idatrix.cloudetl.deploy.MetaCubeCategory;
import com.ys.idatrix.cloudetl.deploy.MetaStoreCategory;
import com.ys.idatrix.cloudetl.dto.common.CheckResultDto;
import com.ys.idatrix.cloudetl.dto.common.PaginationDto;
import com.ys.idatrix.cloudetl.dto.common.ReturnCodeDto;
import com.ys.idatrix.cloudetl.dto.common.PaginationDto.DealRowsInterface;
import com.ys.idatrix.cloudetl.dto.engine.SparkBriefDto;
import com.ys.idatrix.cloudetl.dto.engine.SparkDetailsDto;
import com.ys.idatrix.cloudetl.ext.CloudApp;
import com.ys.idatrix.cloudetl.ext.CloudSession;
import com.ys.idatrix.cloudetl.repository.xml.metastore.CloudSparkEngineMetaStore;
import com.ys.idatrix.cloudetl.repository.xml.metastore.meta.SparkEngineMeta;
import com.ys.idatrix.cloudetl.service.CloudBaseService;

/**
 * Spark engine repository implementation.
 *
 * @author JW
 * @since 05-12-2017
 *
 */
@Service
public class CloudSparkEngineServiceImpl extends CloudBaseService implements CloudSparkEngineService {

	@Autowired
	private CloudSparkEngineMetaStore cloudMetaStore;

	@Autowired
	private MetaStoreCategory metaStoreCategory;
	@Autowired
	private MetaCubeCategory metaCubeCategory;


	private Map<String,List<SparkEngineMeta>> getSparkEngineList(String owner) throws Exception {

		switch(metaCubeCategory) {
		case IDATRIX :
			// Calling MetaCube RPC APIs to get meta data
			// Spark engine don't saved in MetaCube
			//break;
		case PENTAHO :
			// Get meta data from local meta store
			return getMetaStoreList(owner, new ForeachCallback<IMetaStore,SparkEngineMeta> (){
				@Override
				public List<SparkEngineMeta> getOne(IMetaStore source) throws Exception {
					return cloudMetaStore.getElements(source);
				}
			});
		case TENANT :
			// Get meta data from tenant third-part system
			// TODO.
			break;
		case DEFAULT :
			// Do nothing!
		}

		return Maps.newHashMap();
	}

	private SparkEngineMeta getSparkEngine(String owner, String name) throws Exception {
		SparkEngineMeta meta = null;

		switch(metaCubeCategory) {
		case IDATRIX :
			// Calling MetaCube RPC APIs to get meta data
			// Spark engine don't saved in MetaCube
			//break;
		case PENTAHO :
			// Get meta data from local meta store
			meta = cloudMetaStore.getElement(CloudApp.getInstance().getMetaStore(owner), name);
			break;
		case TENANT :
			// Get meta data from tenant third-part system
			// TODO.
			break;
		case DEFAULT :
			// Do nothing!
		}

		return meta;
	}

	private void saveSparkEngineIntoStore(String owner, SparkEngineMeta meta, boolean update) throws Exception {
		switch (metaStoreCategory) {
		case LOCAL : 
			if (update) {
				// Update meta into meta store
				cloudMetaStore.updateElement(CloudApp.getInstance().getMetaStore(owner), meta);
			} else {
				// Create meta into meta store
				cloudMetaStore.createElement(CloudApp.getInstance().getMetaStore(owner), meta);
			}
			break;
		case CACHE : 
			// TODO.
			break;
		case DATABASE : 
			// TODO.
			break;
		case DEFAULT :
			// Do nothing!
		}
	}

	private void deleteSparkEngineFromStore(String owner, SparkEngineMeta meta) throws Exception {
		switch (metaStoreCategory) {
		case LOCAL : 
			cloudMetaStore.deleteElement(CloudApp.getInstance().getMetaStore(owner), meta);
			break;
		case CACHE : 
			// TODO.
			break;
		case DATABASE : 
			// TODO.
			break;
		case DEFAULT :
			// Do nothing!
		}
	}


	/**
	 * Find spark engine by name.
	 * @throws Exception 
	 */
	@Override
	public SparkEngineMeta findSparkEngine(String owner, String name) throws Exception {
		return getSparkEngine(owner, name);
	}
	
	/**
	 * Get brief spark engine list.
	 * @throws Exception 
	 */
	@Override
	public Map<String,List<SparkBriefDto>> getCloudSparkEngineList(String owner ) throws Exception {
		
		 Map<String, List<SparkEngineMeta>> engines = getSparkEngineList(owner);
		 if (engines == null) {
			 return Maps.newHashMap();
		 }
		 return engines.entrySet().stream().collect(Collectors.toMap(entry -> entry.getKey() , 
					entry -> entry.getValue().stream().map(cs -> {
						SparkBriefDto engine = new SparkBriefDto();
						engine.setOwner(entry.getKey());
						engine.setName(cs.getName());
						engine.setStatus("Testing");
						engine.setType("Spark");
						return engine ; 
						}).collect(Collectors.toList()) ));
	}
	
	public Map<String,PaginationDto<SparkBriefDto>> getCloudSparkEngineList(String owner, boolean isMap , int page,int pageSize,String search) throws Exception{
		
		 Map<String, List<SparkEngineMeta>> engines = getSparkEngineList(owner);
		 return getPaginationMaps(isMap, page, pageSize, search, engines, new DealRowsInterface<SparkBriefDto>() {
				@Override
				public SparkBriefDto dealRow(Object obj, Object... params) throws Exception {
					String eleOwner = params != null && params.length > 0 ? (String) params[0] : CloudSession.getResourceUser();
					SparkEngineMeta meta = (SparkEngineMeta)obj;
					SparkBriefDto engine = new SparkBriefDto();
					engine.setOwner(eleOwner);
					engine.setName(meta.getName());
					engine.setStatus("Testing");
					engine.setType("Spark");
					return engine;
				}
				
				@Override
				public boolean match(Object obj,String search, Object... params) throws Exception {
					//String eleOwner = params != null && params.length > 0 ? (String) params[0] : CloudSession.getResourceUser();
					SparkEngineMeta meta = (SparkEngineMeta)obj;
					return  defaultMatch(meta.getName(),search);//meta.getName()!=null&&meta.getName().toLowerCase().contains(search.toLowerCase());
				}
			});
	}
	

	/**
	 * Check existing of spark engine name.
	 * @throws Exception 
	 */
	@Override
	public CheckResultDto checkSparkEngineName(String owner, String name) throws Exception {
		SparkEngineMeta meta = getSparkEngine(owner, name);
		if (meta == null) {
			return new CheckResultDto(name, false);
		}
		return new CheckResultDto(name, true);
	}

	/**
	 * Get spark engine details.
	 * @throws Exception 
	 */
	@Override
	public SparkDetailsDto editSparkEngine(String owner, String name) throws Exception {
		SparkDetailsDto details = new SparkDetailsDto();
		
		SparkEngineMeta meta = getSparkEngine(owner, name);
		if (meta != null) {
			details.setOwner( Const.NVL(owner, CloudSession.getResourceUser()) );
			details.setName(meta.getName());
			details.setDescription(meta.getDescription());
			details.setType("Spark");
			details.setUrl(meta.getUrl());
		}

		return details;
	}

	/**
	 * Save spark engine details.
	 * @throws MetaStoreException 
	 * @throws KettleException 
	 */
	@Override
	public ReturnCodeDto saveSparkEngine(SparkDetailsDto details) throws Exception {
		boolean update = true;

		SparkEngineMeta meta = getSparkEngine(details.getOwner() , details.getName());
		if (meta == null) {
			meta = new SparkEngineMeta();
			update = false;
		}

		meta.setName(details.getName());
		meta.setDescription(details.getDescription());
		meta.setUrl(details.getUrl());
		
		this.saveSparkEngineIntoStore(details.getOwner() , meta, update);
		return new ReturnCodeDto(0, "Succeeded");
	}

	/**
	 * Delete a spark engine.
	 * @throws Exception 
	 */
	@Override
	public ReturnCodeDto deleteSparkEngine(String owner, String name) throws Exception {
		SparkEngineMeta meta = getSparkEngine( owner, name);
		if (meta != null) {
			deleteSparkEngineFromStore( owner, meta);
			return new ReturnCodeDto(0, "Succeeded");
		}
		return new ReturnCodeDto(1, "Not existed");
	}

}
