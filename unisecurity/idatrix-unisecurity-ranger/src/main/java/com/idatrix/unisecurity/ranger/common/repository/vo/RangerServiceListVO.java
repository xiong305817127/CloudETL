package com.idatrix.unisecurity.ranger.common.repository.vo;

import java.util.List;

public class RangerServiceListVO {
	private int pageSize;
	private int reslutSize;
	private String sortBy;
	private String sortType;
	private int startIndex;
	private int totalCount;
	private List<RepositoryService> services;
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public int getReslutSize() {
		return reslutSize;
	}
	public void setReslutSize(int reslutSize) {
		this.reslutSize = reslutSize;
	}
	public String getSortBy() {
		return sortBy;
	}
	public void setSortBy(String sortBy) {
		this.sortBy = sortBy;
	}
	public String getSortType() {
		return sortType;
	}
	public void setSortType(String sortType) {
		this.sortType = sortType;
	}
	public int getStartIndex() {
		return startIndex;
	}
	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}
	public int getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
	public List<RepositoryService> getServices() {
		return services;
	}
	public void setServices(List<RepositoryService> services) {
		this.services = services;
	}
	
	
	@Override
	public String toString() {
		return "RangerServiceListVO [pageSize=" + pageSize + ", reslutSize=" + reslutSize + ", sortBy=" + sortBy
				+ ", sortType=" + sortType + ", startIndex=" + startIndex + ", totalCount=" + totalCount + ", services="
				+ services + "]";
	}
	

}
