package com.ys.idatrix.quality.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.pentaho.di.core.util.Utils;
import org.pentaho.metastore.api.IMetaStore;
import org.pentaho.metastore.stores.delegate.DelegatingMetaStore;

import com.google.common.collect.Maps;
import com.ys.idatrix.quality.dto.common.PaginationDto;
import com.ys.idatrix.quality.dto.common.PaginationDto.DealRowsInterface;
import com.ys.idatrix.quality.ext.CloudApp;
import com.ys.idatrix.quality.ext.CloudSession;
import com.ys.idatrix.quality.logger.CloudLogger;
import com.ys.idatrix.quality.repository.CloudRepository;

public class CloudBaseService {
	
	protected <T> Map<String,List<T>> getUserNameList(String owner , ForeachCallback<String,T> foreachCallback) throws Exception {
		
		Map<String,List<T>> result =  Maps.newHashMap() ;
		if( CloudSession.isPrivilegeEnable() ) {
			if(Utils.isEmpty(owner)) {
				for(String user  : CloudRepository.getCurrentRenterUsers(null)){
					result.put(user , foreachCallback.getOne(user) );
				}
			}else {
				result.put(owner , foreachCallback.getOne(owner));
			}
		}else if( Utils.isEmpty(owner) || owner.equals(CloudSession.getLoginUser())) {
			result.put(CloudSession.getLoginUser(), foreachCallback.getOne( CloudSession.getLoginUser()) );
		}
		return result;
	}
	
	protected <T> Map<String,List<T>> getMetaStoreList(String owner , ForeachCallback<IMetaStore,T> foreachCallback) throws Exception {
		
		Map<String,List<T>> result =  Maps.newHashMap() ;
		Map<String, DelegatingMetaStore> metaStores = CloudApp.getInstance().getMetaStoreMaps(owner);
		if(metaStores != null && metaStores.size() >0) {
			Exception exceptions = new Exception();
			metaStores.entrySet().forEach(entry -> {
				try {
					result.put(entry.getKey(),foreachCallback.getOne(entry.getValue()) );
				} catch (Exception e) {
					exceptions.addSuppressed(e);;
				}
			});
			if( !Utils.isEmpty( exceptions.getSuppressed() ) ) {
				throw (Exception)exceptions.getSuppressed()[0] ;
			}
		}
		return result;
	}
	
	public <T,K> Map<String,PaginationDto<K>> getPaginationMaps(boolean isMap, int page, int pageSize, String search,Map<String,List<T>> sources ,DealRowsInterface<K>  dealRows ) throws Exception {
		
		Map<String, PaginationDto<K>> result =  Maps.newHashMap() ;
		
		if( sources != null && sources.size() > 0 ) {
			boolean isTransform = !isMap &&  sources.size() >1 && page > 0 ;
			int[] indexInfo= new int[] { ((page -1)*pageSize) ,0 , pageSize};
//			int index = (page -1)*pageSize;
//			int curIndex = 0 ;
//			int len = pageSize;
				 
			sources.entrySet().stream().sorted( ( entry1,entry2 ) -> { return entry1.getKey().compareTo(entry2.getKey()); }).forEach(entry -> {
				
				try {
					String eleOwner = entry.getKey();
					List<T> curEles = entry.getValue();
					int total = curEles.size() ;
					
					PaginationDto<K> values = null ;
					if( isTransform ) {
						
						int index = indexInfo[0];
						int curIndex = indexInfo[1] ;
						int len = indexInfo[2];
						
						int page_1 = 2 ;
						int pageSize_1 = curEles.size() ;
						if( len > 0 ) {
							//还有需要获取的数据	
							if( ( curIndex+curEles.size()-1 ) < index ) {
								//不在当前List中
								indexInfo[1] = curIndex+curEles.size(); //curIndex = curIndex+curEles.size() ;
							}else {
								//起始数据在当前List中
								page_1 = 1 ;
								pageSize_1 = len ; 
								
								//去掉已经获取的数据
								curEles = curEles.stream().skip(index - curIndex).collect(Collectors.toList());
								int length = curEles.size() ;
								if( len <= length ) {
									//数量已经满足
									indexInfo[2] = 0 ; //len = 0 ;
								}else {
									//数量不够
									indexInfo[2] =  len-length ; //len =  len-length; //还需要的数量长度
									indexInfo[1] =  curIndex+total ; // curIndex = curIndex+total;
									indexInfo[0] = index+length ; // index = index+length;
								}
								
							}	
						}
						values = new PaginationDto<K>(page_1, pageSize_1,search);
					}else {
						values = new PaginationDto<K>(page, pageSize,search);
					}
					
					result.put(eleOwner, values);
					
					values.processingDataPaging(curEles, dealRows,eleOwner);
					if( isTransform ) {
						//数据总量可能不准
						values.setTotal(total);
					}
				}catch( Exception e) {
					CloudLogger.getInstance().error(this , "getPaginationMaps 获取映射列表错误:",e);
				}
			});
		}
		
		return result;
	}
	
	
	public interface ForeachCallback<K,T> {
		
		List<T> getOne(K source) throws Exception;
	}

}
