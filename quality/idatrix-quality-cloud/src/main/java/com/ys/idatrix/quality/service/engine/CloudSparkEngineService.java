/**
 * 云化数据集成系统 iDatrix CloudETL
 */
package com.ys.idatrix.quality.service.engine;

import java.util.List;
import java.util.Map;

import com.ys.idatrix.quality.dto.common.CheckResultDto;
import com.ys.idatrix.quality.dto.common.PaginationDto;
import com.ys.idatrix.quality.dto.common.ReturnCodeDto;
import com.ys.idatrix.quality.dto.engine.SparkBriefDto;
import com.ys.idatrix.quality.dto.engine.SparkDetailsDto;
import com.ys.idatrix.quality.repository.xml.metastore.meta.SparkEngineMeta;

/**
 * Service interface for cloud spark engine.
 * (Spark run configuration using spark engine.)
 * @author JW
 * @since 2017年5月24日
 *
 */
public interface CloudSparkEngineService {

	SparkEngineMeta findSparkEngine(String owner, String name) throws Exception;
	
	Map<String,List<SparkBriefDto>> getCloudSparkEngineList(String owner ) throws Exception;
	
	Map<String,PaginationDto<SparkBriefDto>> getCloudSparkEngineList(String owner, boolean isMap , int page,int pageSize,String search) throws Exception;
	
	CheckResultDto checkSparkEngineName(String owner, String name) throws Exception;
	
	SparkDetailsDto editSparkEngine(String owner, String name) throws Exception;
	
	ReturnCodeDto saveSparkEngine(SparkDetailsDto details) throws Exception;
	
	ReturnCodeDto deleteSparkEngine(String owner, String name) throws Exception;

}