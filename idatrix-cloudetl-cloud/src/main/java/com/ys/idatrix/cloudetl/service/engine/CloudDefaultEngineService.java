/**
 * 云化数据集成系统 iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.service.engine;

import java.util.List;
import java.util.Map;

import com.ys.idatrix.cloudetl.dto.common.CheckResultDto;
import com.ys.idatrix.cloudetl.dto.common.PaginationDto;
import com.ys.idatrix.cloudetl.dto.common.ReturnCodeDto;
import com.ys.idatrix.cloudetl.dto.engine.EngineBriefDto;
import com.ys.idatrix.cloudetl.dto.engine.EngineDetailsDto;
import com.ys.idatrix.cloudetl.repository.xml.metastore.meta.DefaultEngineMeta;

/**
 * Service interface for cloud default engine.
 * (Default run configuration using default engine.)
 * @author JW
 * @since 2017年7月5日
 *
 */
public interface CloudDefaultEngineService {
	
	DefaultEngineMeta findDefaultEngine(String owner, String name) throws Exception;

	Map<String,List<EngineBriefDto>> getCloudDefaultEngineList(String owner) throws Exception;
	
	Map<String,List<DefaultEngineMeta>> getDefaultEngineList(String owner) throws Exception;
	
	Map<String,PaginationDto<EngineBriefDto>> getCloudDefaultEngineList(String owner, boolean isMap , int page,int pageSize,String search) throws Exception;

	CheckResultDto checkDefaultEngineName(String owner, String name) throws Exception;

	EngineDetailsDto editDefaultEngine(String owner, String name) throws Exception;

	ReturnCodeDto saveDefaultEngine(EngineDetailsDto details) throws Exception;

	ReturnCodeDto deleteDefaultEngine(String owner, String name) throws Exception;

}
