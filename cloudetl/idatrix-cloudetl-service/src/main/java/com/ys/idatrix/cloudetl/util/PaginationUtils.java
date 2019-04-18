package com.ys.idatrix.cloudetl.util;

import java.util.ArrayList;
import java.util.List;

import com.ys.idatrix.cloudetl.common.api.dto.PaginationDto;
import com.ys.idatrix.cloudetl.common.api.dto.PaginationDto.DealRowsInterface;

public class PaginationUtils {
	
	
	public static <T,S> com.ys.idatrix.cloudetl.common.api.dto.PaginationDto<S>  transformPagination( com.ys.idatrix.cloudetl.dto.common.PaginationDto<T> dto , DealRowsInterface<S> dealRows) throws Exception{
		PaginationDto<S> res = new PaginationDto<S>(dto.getPage(), dto.getPageSize(),dto.getSearch());
		res.setOther(dto.getOther());
		res.setTotal(dto.getTotal());
		if( dto.getRows() != null && dto.getRows().size() > 0) {
			List<S> resList =  new ArrayList<>();
			for( T row :dto.getRows()){
				resList.add( dealRows.dealRow(row) );
			}
			res.setRows(resList);
		}
		return res ;
	}
	

}
