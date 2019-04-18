package com.ys.idatrix.quality.steps.redundance;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Stream;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.ys.idatrix.quality.logger.CloudLogUtils;

public class RowDataRegister  {

	private Set<Integer> fieldHashs ;
	private Map<String ,Integer> cache ;
	
	private int limitNumber ;
	private RedundanceData data ;
	private Redundance redundance ;
	
	public RowDataRegister(Redundance redundance,RedundanceData data,int limitNumber ) {
		this.redundance = redundance ;
		this.data = data ;
		this.limitNumber = limitNumber ;
		
		cache = Maps.newHashMap() ;
		fieldHashs = Sets.newHashSet();
	}
	
	public void addRowField( String field) {
		//数据总量加1
		data.total++;
		//获取当前域的hash值
		int hash = field.hashCode() ;
		if( fieldHashs.contains(hash)) {
			//当前hash 不是第一次,是重复数据,进行缓存
			if( cache.containsKey(field) ) {
				int num = cache.get(field) ;
				cache.put(field, num+1 ) ;
			}else {
				cache.put(field, 2 ) ;
			}
		}else {
			//当前hash 第一次出现 
			fieldHashs.add(hash);
			data.noRepeat++;
		}
		
	}


	public void saveDetailData() throws  Exception {
		
		if( limitNumber != 0 && !cache.isEmpty()) {
			// 重复详细数据
			Stream<Entry<String, Integer>> stream = cache.entrySet().stream().sorted( ( e1,e2 ) -> { return e2.getValue()-e1.getValue() ; } ); 
			if( limitNumber > 0) {
				stream = stream.limit(limitNumber) ;
			}
			
			 StringBuffer detailBuffer = new StringBuffer(); 
			 stream.forEach(entry -> {
				 
				detailBuffer.append( entry.getKey() );
				detailBuffer.append( ", "  );
				detailBuffer.append( entry.getValue() );
				 
				if( detailBuffer.length() > 5000 ) {
					 //插入会在最后自动加换行
					 try {
						CloudLogUtils.insertLog( data.detailPath , detailBuffer.toString());
					} catch ( Exception e) {
						redundance.logError("保存冗余详细信息到文件失败",e);
					}
					 detailBuffer.setLength(0);
				 }else {
					 detailBuffer.append("\n");
				 }
			 } );
			 
			 if( detailBuffer.length() > 0 ) {
				 CloudLogUtils.insertLog( data.detailPath , detailBuffer.toString());
			 }
		}
		
	}
	
	public void clear() {
		
		fieldHashs.clear() ;
		cache.clear() ;
		data  = null ;
		redundance  = null ;
			
	}
	
}
