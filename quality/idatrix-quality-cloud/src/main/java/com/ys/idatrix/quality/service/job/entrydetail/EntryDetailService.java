/**
 * 云化数据集成系统 iDatrix CloudETL
 */
package com.ys.idatrix.quality.service.job.entrydetail;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.pentaho.di.core.exception.KettleException;

/**
 * get Entry detail interface, Used by CloudEntryService.getDetails 
 * @author XH
 * @since 2017年6月9日
 *
 */
public interface EntryDetailService {

	/**
	 * According to type, determine which detail service belongs to Entry
	 * @return Entry.type
	 */
	public String getEntryDetailType();
	
	/**
	 * @param flag , Type flags for detailed services
	 * @param params , Detailed service parameters
	 * @return Results of processing of detailed services
	 * @throws Exception 
	 */
	public Object dealEntryDetailByflag(String flag ,Map<String,Object> params) throws Exception;


	default void checkDetailParam(Map<String,Object> all ,String ... param) throws KettleException{

		for(String p : param){
			if( all.containsKey(p) ){
				Object value = all.get(p);
				if( value ==null ||
						( value instanceof String && StringUtils.isEmpty( (String)value ) )  || 
						( value instanceof List &&  ( (List<?>)value ).isEmpty() ) ||
						( value instanceof Map &&  ( (Map<?, ?>)value ).isEmpty() )
						){
					throw new KettleException("参数 "+ p +" 值为空!");
				}
				continue ;
			}else{
				throw new KettleException("参数 "+ p +" 不存在!"); 
			}
		}

	}
}
