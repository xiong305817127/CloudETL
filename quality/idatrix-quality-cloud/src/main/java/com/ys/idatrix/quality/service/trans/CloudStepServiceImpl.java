/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.quality.service.trans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.pentaho.di.cluster.ClusterSchema;
import org.pentaho.di.cluster.SlaveServer;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.plugins.PluginInterface;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.core.plugins.StepPluginType;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaBase;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.trans.TransHopMeta;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;
import com.ys.idatrix.quality.dto.codec.StepParameterCodec;
import com.ys.idatrix.quality.dto.common.ReturnCodeDto;
import com.ys.idatrix.quality.dto.step.StepConfigDto;
import com.ys.idatrix.quality.dto.step.StepConfigsDto;
import com.ys.idatrix.quality.dto.step.StepCopyDto;
import com.ys.idatrix.quality.dto.step.StepDetailsDto;
import com.ys.idatrix.quality.dto.step.StepFieldDto;
import com.ys.idatrix.quality.dto.step.StepHeaderDto;
import com.ys.idatrix.quality.dto.step.StepNameCheckResultDto;
import com.ys.idatrix.quality.dto.step.StepPositionDto;
import com.ys.idatrix.quality.dto.step.TransStepDto;
import com.ys.idatrix.quality.ext.utils.FieldValidator;
import com.ys.idatrix.quality.repository.CloudRepository;
import com.ys.idatrix.quality.service.cluster.CloudClusterService;
import com.ys.idatrix.quality.service.db.CloudMetaCubeDbService;
import com.ys.idatrix.quality.service.trans.stepdetail.StepDetailService;
import com.ys.idatrix.quality.web.utils.SearchFieldsProgress;

/**
 * Step service implementation.
 * 
 * @author JW
 * @since 05-12-2017
 *
 */
@Service
public class CloudStepServiceImpl implements CloudStepService {

	@Autowired
	private CloudClusterService cloudClusterService;
	
	@Autowired
	private CloudMetaCubeDbService cloudDbService;

	@Autowired(required=false)
	List<StepDetailService> stepDetailServices;

	@Override
	public ReturnCodeDto addStep(StepHeaderDto stepHeader) throws Exception{
			TransMeta transMeta = CloudRepository.loadTransByName(stepHeader.getOwner() , stepHeader.getTransName(),stepHeader.getGroup());

			PluginRegistry registry = PluginRegistry.getInstance();
			PluginInterface stepPlugin = registry.findPluginWithId(StepPluginType.class, stepHeader.getStepType());
			if (stepPlugin != null) {
				StepMetaInterface info = (StepMetaInterface) registry.loadClass(stepPlugin);
				info.setDefault();
				StepMeta stepMeta = new StepMeta(stepHeader.getStepType(), stepHeader.getStepName(), info);
				stepMeta.setLocation(transMeta.nrSteps()*150+50, 100);
				stepMeta.drawStep();
				stepMeta.setDistributes(false);
				
				//使用对象默认的初始化值,很多没有给默认值，暂时屏蔽
				StepParameterCodec.initParamObject(stepMeta, stepMeta.getTypeId());
				
				transMeta.addOrReplaceStep(stepMeta); //.addStep(stepMeta);
				CloudRepository.saveTrans(transMeta,stepHeader.getOwner() ,stepHeader.getGroup());
				
				HashMap<String, Object> result = Maps.newHashMap();
				result.put("supportsErrorHandling", stepMeta.supportsErrorHandling());
				result.put("distributes", stepMeta.isDistributes());
				
				return new ReturnCodeDto(0, result);
			}
			return new ReturnCodeDto(1, "pluginId["+stepHeader.getStepType()+"] 不存在.");
	}
	
	@Override
	public ReturnCodeDto copyStep(StepCopyDto stepcopy ) throws Exception{
		
			TransMeta fromTransMeta = CloudRepository.loadTransByName( Const.NVL(stepcopy.getFromOwner(),stepcopy.getOwner()) ,stepcopy.getFromTransName(),stepcopy.getFromGroup());
			TransMeta toTransMeta = fromTransMeta;
			if( !Utils.isEmpty(stepcopy.getToTransName()) && !stepcopy.getFromTransName().equals( stepcopy.getToTransName())) {
				toTransMeta =  CloudRepository.loadTransByName(Const.NVL(stepcopy.getToOwner(),stepcopy.getOwner()), stepcopy.getToTransName(),stepcopy.getToGroup());
			}
			StepMeta fromStepMeta = fromTransMeta.findStep(stepcopy.getFromStepName());
			if (fromStepMeta == null ) {
				// fromStepMeta existed.
				throw new KettleException("Not existed: TransName:"+stepcopy.getFromTransName()+" StepName:"+stepcopy.getFromStepName());
			}
			
			StepMeta toStepMeta = (StepMeta) fromStepMeta.clone();
			DatabaseMeta[] dbs = toStepMeta.getStepMetaInterface().getUsedDatabaseConnections();
			if(dbs != null && dbs.length >0) {
				for(DatabaseMeta d :dbs) {
					if(d != null) {
						toTransMeta.addOrReplaceDatabase(d);
					}
				}
			}
			toStepMeta.setName(stepcopy.getToStepName());
			toStepMeta.setLocation(fromStepMeta.getLocation().x+20, fromStepMeta.getLocation().y+20);
			//toStepMeta.drawStep();
			toTransMeta.addOrReplaceStep(toStepMeta);
			
			CloudRepository.saveTrans(toTransMeta,Const.NVL(stepcopy.getToOwner(),stepcopy.getOwner())  ,stepcopy.getToGroup());
			return new ReturnCodeDto(0, toStepMeta.getLocation());
	}

	@Override
	public StepNameCheckResultDto checkStepName(TransStepDto transStep) throws Exception{
			TransMeta transMeta = CloudRepository.loadTransByName(transStep.getOwner() , transStep.getTransName(),transStep.getGroup());

			if (transMeta.findStep(transStep.getStepName()) == null ) {
				return new StepNameCheckResultDto(false); // Not existed.
			}

			return new StepNameCheckResultDto(true); // Existed.
	}

	@Override
	public StepDetailsDto editStep(TransStepDto transStep) throws Exception{
			TransMeta transMeta = CloudRepository.loadTransByName(transStep.getOwner() , transStep.getTransName(),transStep.getGroup());

			StepMeta stepMeta = transMeta.findStep(transStep.getStepName());
			if (stepMeta == null ) {
				// Not existed.
				throw new KettleException("Not existed: TransName:"+transStep.getTransName()+" StepName:"+transStep.getStepName());
			}

			StepDetailsDto details = new StepDetailsDto();
			details.setOwner(transStep.getOwner() );
			details.setTransName(transStep.getTransName());
			details.setStepName(transStep.getStepName());
			details.setNewName(""); // New step name is empty

			// Load step parameters per type
			details.encodeStepParams(stepMeta);
			
			details.setDescription(stepMeta.getDescription());
			details.setType(stepMeta.getTypeId());

			//发送模式  分发/复制 
			details.setDistributes(stepMeta.isDistributes());
			//是否支持错误处理
			details.setSupportsErrorHandling(stepMeta.supportsErrorHandling()) ;
			
			details.setNextStepNames(transMeta.getNextStepNames(stepMeta));
			details.setPrevStepNames(transMeta.getPrevStepNames(stepMeta));
			
			return details;
	}
	
	@Override
	public ReturnCodeDto saveStep(StepDetailsDto stepDetails) throws Exception{
		
			TransMeta transMeta = CloudRepository.loadTransByName(stepDetails.getOwner() , stepDetails.getTransName(),stepDetails.getGroup());
			StepMeta stepMeta = transMeta.findStep(stepDetails.getStepName());
			if (stepMeta == null) { // Not existed. Add it ??
				ReturnCodeDto r = addStep(new StepHeaderDto(stepDetails.getOwner() ,stepDetails.getTransName(), stepDetails.getGroup(), stepDetails.getStepName(), stepDetails.getType()));
				if(!r.isSuccess()) {
					return r ;
				}
				transMeta = CloudRepository.loadTransByName(stepDetails.getOwner() ,stepDetails.getTransName(),stepDetails.getGroup());
				stepMeta = transMeta.findStep(stepDetails.getStepName());
//				PluginRegistry registry = PluginRegistry.getInstance();
//				PluginInterface stepPlugin = registry.findPluginWithId(StepPluginType.class, stepDetails.getType());
//				if (stepPlugin != null) {
//					StepMetaInterface info = (StepMetaInterface) registry.loadClass(stepPlugin);
//					info.setDefault();
//					stepMeta = new StepMeta(stepPlugin.getIds()[0], stepDetails.getStepName(), info);
//					stepMeta.setLocation(transMeta.nrSteps()*150+50, 100);
//					stepMeta.drawStep();
//					stepMeta.setDistributes(false);
//					
//					transMeta.addOrReplaceStep(stepMeta); //.addStep(stepMeta);
//					//CloudRepository.saveTrans(transMeta);
//				}else {
//					return new ReturnCodeDto(1, "pluginId["+stepDetails.getType()+"] 不存在.");
//				}
			}

			// Decode step parameters and fill them into the step meta
			// Need to set db meta in trans so that its steps can obtain db connection!
			//transMeta.setDatabases(databases);
			stepDetails.decodeParameterObject(stepMeta, cloudDbService.getAllDbConnection(stepDetails.getOwner() ), transMeta);
			DatabaseMeta[] dbs = stepMeta.getStepMetaInterface().getUsedDatabaseConnections();
			if(dbs != null && dbs.length >0) {
				for(DatabaseMeta d :dbs) {
					if(d != null) {
						transMeta.addOrReplaceDatabase(d);
					}
				}
			}
			
			// Change step name if new name given
			stepMeta.setName(Const.NVL(stepDetails.getNewName(), stepDetails.getStepName()));
			//发送模式  分发/复制 ,默认复制
			stepMeta.setDistributes(stepDetails.isDistributes());
			
			//transMeta.addOrReplaceStep(stepMeta);
			CloudRepository.saveTrans(transMeta,stepDetails.getOwner() ,stepDetails.getGroup());
			return new ReturnCodeDto(0, "Succeeded");
	}

	@Override
	public StepConfigsDto editStepConfigs(TransStepDto transStep) throws Exception{
		StepConfigsDto configs = new StepConfigsDto();
		configs.setTransName(transStep.getTransName());
		configs.setStepName(transStep.getStepName());

			TransMeta transMeta = CloudRepository.loadTransByName(transStep.getOwner() , transStep.getTransName(),transStep.getGroup());
			StepMeta stepMeta = transMeta.findStep(transStep.getStepName());
			if (stepMeta != null ) {
				StepConfigDto config = new StepConfigDto();
				config.setClusterSchema(stepMeta.getClusterSchema() != null ? stepMeta.getClusterSchema().getName() : "");
				config.setDistribute(stepMeta.isDistributes());
				configs.setConfigs(config);
			}

		return configs;
	}

	@Override
	public ReturnCodeDto saveStepConfigs(StepConfigsDto stepConfigs) throws Exception{
			TransMeta transMeta = CloudRepository.loadTransByName(stepConfigs.getOwner() , stepConfigs.getTransName(),stepConfigs.getGroup());

			StepMeta stepMeta = transMeta.findStep(stepConfigs.getStepName());
			if (stepMeta == null ) {
				return new ReturnCodeDto(1, "Step not existed");
			}
			
			StepConfigDto config = stepConfigs.getConfigs();
			if(config != null && config.isDistribute() != null ) {
				stepMeta.setDistributes(config.isDistribute());
			}
			
			if (config == null || Utils.isEmpty(config.getClusterSchema())) {
				// Clean up cluster schema
				stepMeta.setClusterSchema(null);
				stepMeta.setClusterSchemaName(null);
				//CloudRepository.saveTrans(transMeta);
				//return new ReturnCodeDto(0, "Cleanup cluster schema succeeded");
			}else if(!"##ignore##".equals(config.getClusterSchema())){
				//ClusterSchema schema = transMeta.findClusterSchema(config.getClusterSchema());
				ClusterSchema schema = cloudClusterService.findClusterSchema(stepConfigs.getOwner() ,config.getClusterSchema());
				if (schema == null) {
					return new ReturnCodeDto(1, "Cluster schema not existed");
				}

				stepMeta.setClusterSchema(schema);
				stepMeta.setClusterSchemaName(config.getClusterSchema());

				// Add or replace the cluster schema in transMeta
				transMeta.addOrReplaceClusterSchema(schema);

				// Add or replace the slave server in transMeta
				List<SlaveServer> servers = schema.getSlaveServers();
				if (servers != null) {
					for (SlaveServer server : servers) {
						transMeta.addOrReplaceSlaveServer(server);
					}
				}
			}

			// Cleanup not-used cluster schema in transMeta
			List<ClusterSchema> clusters = transMeta.getClusterSchemas();
			if (clusters != null) {
				transMeta.setClusterSchemas(clusters.stream().filter(cluster -> transMeta.isUsingClusterSchema(cluster)).collect(Collectors.toList()));
			}


			// Cleanup not-used slave server in transMeta
			List<SlaveServer> slaves = transMeta.getSlaveServers();
			if (slaves != null) {
				List<SlaveServer> results = slaves.stream().filter(slave -> {
					try {
						return transMeta.isUsingSlaveServer(slave);
					} catch (KettleException e) {
						return true; // Not sure, just keep it!
					}
				}).collect(Collectors.toList());
				transMeta.setSlaveServers(results);
			}
			
			CloudRepository.saveTrans(transMeta,stepConfigs.getOwner() ,stepConfigs.getGroup());
			return new ReturnCodeDto(0, "Succeeded");
	}

	@Override
	public ReturnCodeDto deleteStep(TransStepDto transStep) throws Exception{
			TransMeta transMeta = CloudRepository.loadTransByName(transStep.getOwner(),transStep.getTransName(),transStep.getGroup());

			StepMeta stepMeta = transMeta.findStep(transStep.getStepName());
			if (stepMeta == null ) {
				return new ReturnCodeDto(1, "Step not existed");
			}
			
			// !!! Here we should remove all hops connected to the step !!!
			TransHopMeta fromHop = transMeta.findTransHopFrom(stepMeta);
			while (fromHop != null) {
				transMeta.removeTransHop(fromHop);
				fromHop = transMeta.findTransHopFrom(stepMeta);
			}
			
			TransHopMeta toHop = transMeta.findTransHopTo(stepMeta);
			while (toHop != null) {
				transMeta.removeTransHop(toHop);
				toHop = transMeta.findTransHopTo(stepMeta);
			}
			
			transMeta.removeStep(transMeta.indexOfStep(stepMeta));
			
			CloudRepository.saveTrans(transMeta,transStep.getOwner(),transStep.getGroup());
			return new ReturnCodeDto(0, "Succeeded");
	}

	@Override
	public ReturnCodeDto moveStep(StepPositionDto stepPosition) throws Exception{
			TransMeta transMeta = CloudRepository.loadTransByName(stepPosition.getOwner(),stepPosition.getTransName(),stepPosition.getGroup());

			StepMeta stepMeta = transMeta.findStep(stepPosition.getStepName());
			if (stepMeta == null ) {
				return new ReturnCodeDto(1, "Step not existed");
			}
			
			stepMeta.setLocation(stepPosition.getXloc(), stepPosition.getYloc());
			
			CloudRepository.saveTrans(transMeta,stepPosition.getOwner() ,stepPosition.getGroup());
			return new ReturnCodeDto(0, "Succeeded");
	}

	@Override
	public List<StepFieldDto> getInputFields(TransStepDto transStep) throws Exception{
			TransMeta transMeta = CloudRepository.loadTransByName(transStep.getOwner(),transStep.getTransName(),transStep.getGroup());

			StepMeta stepMeta = transMeta.findStep(transStep.getStepName());
			if (stepMeta == null ) {
				return null;
			}
			
			SearchFieldsProgress op = new SearchFieldsProgress( transMeta, stepMeta, true );
			op.run();
			RowMetaInterface rowMetaInterface = op.getFields();
			
			List<StepFieldDto> jsfs = new ArrayList<>();
			for (int i = 0; i < rowMetaInterface.size(); i++) {
				ValueMetaInterface v = rowMetaInterface.getValueMeta(i);
				StepFieldDto jsf = new StepFieldDto();
				jsf.setComments(Const.NVL(v.getComments(), ""));
				jsf.setConversionMask(Const.NVL(v.getConversionMask(), ""));
				jsf.setCurrencySymbol(Const.NVL(v.getCurrencySymbol(), ""));
				jsf.setDecimalSymbol(Const.NVL(v.getDecimalSymbol(), ""));
				jsf.setGroupingSymbol(Const.NVL(v.getGroupingSymbol(), ""));
				jsf.setLength("" + FieldValidator.fixedLength(v.getLength()));
				jsf.setName(v.getName());
				jsf.setOrigin(Const.NVL(v.getOrigin(), ""));
				jsf.setPrecision("" + FieldValidator.fixedPrecision(v.getPrecision()));
				jsf.setStorageType(ValueMetaBase.getStorageTypeCode(v.getStorageType()));
				jsf.setTrimType(ValueMetaBase.getTrimTypeCode(v.getTrimType()));
				jsf.setType(v.getTypeDesc());
				jsfs.add(jsf);
			}
			
			return jsfs;
	}

	@Override
	public List<StepFieldDto> getOutputFields(TransStepDto transStep) throws Exception{
			TransMeta transMeta = CloudRepository.loadTransByName(transStep.getOwner(),transStep.getTransName(),transStep.getGroup());

			StepMeta stepMeta = transMeta.findStep(transStep.getStepName());
			if (stepMeta == null ) {
				return null;
			}
			
			SearchFieldsProgress op = new SearchFieldsProgress( transMeta, stepMeta, false );
			op.run();
			RowMetaInterface rowMetaInterface = op.getFields();
			
			List<StepFieldDto> jsfs = new ArrayList<>();
			for (int i = 0; i < rowMetaInterface.size(); i++) {
				ValueMetaInterface v = rowMetaInterface.getValueMeta(i);
				StepFieldDto jsf = new StepFieldDto();
				jsf.setComments(Const.NVL(v.getComments(), ""));
				jsf.setConversionMask(Const.NVL(v.getConversionMask(), ""));
				jsf.setCurrencySymbol(Const.NVL(v.getCurrencySymbol(), ""));
				jsf.setDecimalSymbol(Const.NVL(v.getDecimalSymbol(), ""));
				jsf.setGroupingSymbol(Const.NVL(v.getGroupingSymbol(), ""));
				jsf.setLength("" + FieldValidator.fixedLength(v.getLength()));
				jsf.setName(v.getName());
				jsf.setOrigin(Const.NVL(v.getOrigin(), ""));
				jsf.setPrecision("" + FieldValidator.fixedPrecision(v.getPrecision()));
				jsf.setStorageType(ValueMetaBase.getStorageTypeCode(v.getStorageType()));
				jsf.setTrimType(ValueMetaBase.getTrimTypeCode(v.getTrimType()));
				jsf.setType(v.getTypeDesc());
				jsfs.add(jsf);
			}
			
			return jsfs;
	}
	
	@Override
	public Object getDetails(TransStepDto transStep) throws Exception{
			TransMeta transMeta = CloudRepository.loadTransByName(transStep.getOwner(),transStep.getTransName(),transStep.getGroup());

			StepMeta stepMeta = transMeta.findStep(transStep.getStepName());
			if (stepMeta == null ) {
				return null;
			}
			if(StringUtils.isEmpty(transStep.getDetailType())){
				return null ;
			}
			if(stepDetailServices == null || stepDetailServices.size() ==0){
				return null;
			}
			
			Optional<StepDetailService> stepDetailServiceOpt = stepDetailServices.stream().filter(service -> service.getStepDetailType().contains(transStep.getDetailType())).findFirst();
			if(stepDetailServiceOpt.isPresent()	){
				Map<String, Object> param = transStep.getDetailParam();
				return stepDetailServiceOpt.get().dealStepDetailByflag(param.get("flag").toString(), param);
			}
			
			return null;
	}
	
}
