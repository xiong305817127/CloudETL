package com.ys.idatrix.cloudetl.exec4w.api.service;

import com.ys.idatrix.cloudetl.common.api.dto.PaginationDto;
import com.ys.idatrix.cloudetl.exec4w.api.dto.ExecExceptionDto;

public interface Cloud4WExceptionService {
	
	public ExecExceptionDto getExecException( String execId) throws Exception;
	
	public PaginationDto<ExecExceptionDto> getExecExceptions( String owner,String name,String type, Integer pageNo, Integer pageSize ) throws Exception;
	
	public PaginationDto<ExecExceptionDto> getExecExceptions(String renterId, Integer pageNo, Integer pageSize ) throws Exception;
}
