package com.ys.idatrix.quality.reference.etl;

import org.pentaho.di.core.util.Utils;
import org.pentaho.di.repository.RepositoryObjectType;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.ys.idatrix.cloudetl.common.api.dto.PaginationDto;
import com.ys.idatrix.cloudetl.exec4w.api.dto.ExecExceptionDto;
import com.ys.idatrix.cloudetl.exec4w.api.service.Cloud4WExceptionService;
import com.ys.idatrix.quality.ext.CloudSession;

@Service
public class CloudETLService {

	@Reference(check = false)
	private Cloud4WExceptionService cloud4WExceptionService;
	
	public ExecExceptionDto getExecException(String execId) throws Exception {
		return cloud4WExceptionService.getExecException(execId) ;
	}
	
	public PaginationDto<ExecExceptionDto> getExecExceptions(String owner,String name,RepositoryObjectType type, Integer pageNo, Integer pageSize ) throws Exception {
		if( Utils.isEmpty(owner)) {
			owner = CloudSession.getResourceUser() ;
		}
		return cloud4WExceptionService.getExecExceptions(owner, name, type!= null?type.getTypeDescription():null, pageNo, pageSize);
	}
	
	public PaginationDto<ExecExceptionDto> getExecExceptions(String renterId, Integer pageNo, Integer pageSize ) throws Exception {
		if( Utils.isEmpty(renterId)) {
			renterId = CloudSession.getLoginRenterId() ;
		}
		return cloud4WExceptionService.getExecExceptions(renterId, pageNo, pageSize);
	}
	
}
