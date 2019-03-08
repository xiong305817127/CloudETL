package com.ys.idatrix.cloudetl.dto.common;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.pentaho.di.core.util.Utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ys.idatrix.cloudetl.ext.utils.StringEscapeHelper;

public class PaginationDto<T> {
	
	int total;
	List<T> rows;
	int page;
	int pageSize;
	
	String search;
	
	Map<String,Object> other;
	
	/**
	 * @param page
	 * @param pageSize
	 */
	public PaginationDto(int page, int pageSize) {
		super();
		this.page = page;
		this.pageSize = pageSize;
	}
	
	/**
	 * @param page
	 * @param pageSize
	 */
	public PaginationDto(int page, int pageSize,String search) {
		super();
		this.page = page;
		this.pageSize = pageSize;
		this.search = search;
	}
	/**
	 * @return the total
	 */
	public int getTotal() {
		return total;
	}
	/**
	 * @param  设置 total
	 */
	public void setTotal(int total) {
		this.total = total;
	}
	/**
	 * @return the rows
	 */
	public List<T> getRows() {
		return rows;
	}
	/**
	 * @param  设置 rows
	 */
	public void setRows(List<T> rows) {
		this.rows = rows;
	}
	/**
	 * @return the page
	 */
	public int getPage() {
		return page;
	}
	/**
	 * @param  设置 page
	 */
	public void setPage(int page) {
		this.page = page;
	}
	/**
	 * @return the pageSize
	 */
	public int getPageSize() {
		return pageSize;
	}
	/**
	 * @param  设置 pageSize
	 */
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	
	
	public Map<String, Object> getOther() {
		return other;
	}

	public void setOther(Map<String, Object> other) {
		this.other = other;
	}
	
	public void addOther(String key ,Object value) {
		if(this.other == null ) {
			this.other = Maps.newHashMap();
		}
		this.other.put(key, value);
	}

	/**
	 * @return the search
	 */
	public String getSearch() {
		return search;
	}
	/**
	 * @param  设置 search
	 */
	public void setSearch(String search) {
		this.search = search;
	}
	@SuppressWarnings("unchecked")
	public  void  processingDataPaging(List<?> allData,DealRowsInterface<T> dealFun,Object... params) throws Exception{
		if( rows == null){
			rows = Lists.newArrayList();
		}
		if(allData == null || allData.size() == 0){
			return ;
		}
		
		if( dealFun !=null && dealFun.isNeedFilter(search)){
			String searchStr = StringEscapeHelper.decode(search);
			allData=allData.stream().filter((obj) -> {
				
				boolean res;
				try {
					res = dealFun.match(obj,searchStr,params);
				} catch (Exception e) {
					res = false;
				} 
				
				return res ;
			}).collect(Collectors.toList());
			if(allData.size() == 0){
				return ;
			}
		}
		
		total = allData.size();
		int index;
		int maxIndex;
		if(page > 0){
			 index = (page -1)*pageSize;
			 maxIndex = ( page * pageSize) -1 ;
			 maxIndex = maxIndex>(total-1)? (total-1): maxIndex ;
		}else{
			 index = 0;
			 maxIndex = (total-1);
			 pageSize=total;
			 page=1;
		}
		
		for(;index <= maxIndex; index++ ){
			Object obj = allData.get(index);
			if(obj == null){
				continue ;
			}
			if(dealFun != null){
				rows.add(dealFun.dealRow(obj,params));
			}else {
				rows.add((T)obj);
			}
		}
	}
	
	
	public PaginationDto<T>  mergePagination( PaginationDto<T> pagination ){
		if(pagination == null ) {
			return this;
		}
		
		total = total+pagination.total ;
		if(pagination.rows != null ) {
			if(rows == null ) {
				rows = Lists.newArrayList();
			}
			rows.addAll(pagination.rows);
		}
		
		if( pagination.other != null ) {
			if(other == null ) {
				other = Maps.newHashMap();
			}
			other.putAll(pagination.other);
		}
		return this;
	}
	
	
	public <K> PaginationDto<T>  transformPagination( PaginationDto<K> pagination , DealRowsInterface<T> dealFun){
		if(pagination == null ) {
			return this;
		}
		
		total = total+pagination.total ;
		if(pagination.rows != null ) {
			if(rows == null ) {
				rows = Lists.newArrayList();
			}
			
			pagination.rows.stream().forEach(obj -> {
				try {
					rows.add(dealFun.dealRow(obj));
				} catch (Exception e) {
				}
			});
		}
		
		if( pagination.other != null ) {
			if(other == null ) {
				other = Maps.newHashMap();
			}
			other.putAll(pagination.other);
		}
		return this;
	}
	
	public interface DealRowsInterface<T>{
		
		/**
		 * 处理List数据,可进行对象转换
		 * @param obj:List的单个对象
		 * @return
		 * @throws Exception
		 */
		public T dealRow(Object obj ,Object... params)throws Exception;
		
		/**
		 * 用户分页搜索结果,返回false时对象将被过滤掉<br>
		 * 需要isNeedFilter方法返回true,即search不为空
		 * 可以使用 defaultMatch 方法 判断字符串包含
		 * @param obj :List的单个对象
		 * @return
		 */
		 public boolean match(Object obj,String search,Object... params) throws Exception ;
		 
		 /**
		  * 默认的字符串匹配,判断字符串是否包含search字符
		  * @param obj
		  * @param search
		  * @return
		  */
		 default public boolean defaultMatch(String obj,String search){
			 return obj!=null&&obj.toLowerCase().contains(search.toLowerCase());
		 }
		 
		 default public boolean isNeedFilter(String search){
			 return !Utils.isEmpty(search);
		 }
	}

}
