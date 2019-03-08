package com.ys.idatrix.cloudetl.dubbo.service.impl;

import org.springframework.stereotype.Component;

import com.alibaba.dubbo.config.annotation.Service;
import com.ys.idatrix.cloudetl.common.api.dto.PaginationDto;
import com.ys.idatrix.cloudetl.common.api.dto.PaginationDto.DealRowsInterface;
import com.ys.idatrix.cloudetl.exec4w.api.dto.ExecExceptionDto;
import com.ys.idatrix.cloudetl.exec4w.api.service.Cloud4WExceptionService;
import com.ys.idatrix.cloudetl.ext.executor.exception.Exec4WExceptionHandler;
import com.ys.idatrix.cloudetl.util.PaginationUtils;

@Component
@Service
public class CloudExec4WExceptionServiceImpl implements Cloud4WExceptionService {

	@Override
	public ExecExceptionDto getExecException(String execId) throws Exception {
		return changeDto( Exec4WExceptionHandler.getInstance().getExecException(execId) );
	}

	@Override
	public PaginationDto<ExecExceptionDto> getExecExceptions(String owner, String name, String type,Integer pageNo, Integer pageSize) throws Exception {
		return PaginationUtils.transformPagination(Exec4WExceptionHandler.getInstance().getExecExceptions(owner, name, type, pageNo, pageSize), new DealRowsInterface<ExecExceptionDto>() {

			@Override
			public ExecExceptionDto dealRow(Object obj, Object... params) throws Exception {
				return changeDto(( com.ys.idatrix.cloudetl.dto.exec4w.Exec4WExceptionDto )obj);
			}

			@Override
			public boolean match(Object obj, String search, Object... params) throws Exception {
				return true;
			}} );
	}

	@Override
	public PaginationDto<ExecExceptionDto> getExecExceptions(String renterId,Integer pageNo, Integer pageSize) throws Exception {
		return PaginationUtils.transformPagination(Exec4WExceptionHandler.getInstance().getExecExceptionsByRenterId(renterId, pageNo, pageSize), new DealRowsInterface<ExecExceptionDto>() {

			@Override
			public ExecExceptionDto dealRow(Object obj, Object... params) throws Exception {
				return changeDto(( com.ys.idatrix.cloudetl.dto.exec4w.Exec4WExceptionDto )obj);
			}
			@Override
			public boolean match(Object obj, String search, Object... params) throws Exception {
				return true;
			}} );
	}
	
	
	private ExecExceptionDto changeDto( com.ys.idatrix.cloudetl.dto.exec4w.Exec4WExceptionDto dto) {
		if( dto == null ) {
			return null;
		}
		
		ExecExceptionDto res  = new ExecExceptionDto();
		res.setExecId(dto.getExecId());
		
		res.setRenterId(dto.getRenterId());
		res.setOwner(dto.getOwner());
		res.setName(dto.getName());
		res.setType(dto.getType());
		res.setUpdateDate(dto.getUpdateDate() );
		res.setPosition(dto.getPosition());
		res.setExceptionDetail(dto.getExceptionDetail());
		
		res.setExecSource(dto.getExecSource());
		res.setInputSource(dto.getInputSource());
		res.setOutputSource(dto.getOutputSource());
		
		return res ;
	}

}
