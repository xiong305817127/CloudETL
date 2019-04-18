package com.ys.idatrix.cloudetl.subscribe.api.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ys.idatrix.cloudetl.subscribe.api.dto.parts.CommonDto;

public class QueryJobDto extends CommonDto  implements Serializable{
	
	private static final long serialVersionUID = -5722702883980603973L;
	
	private String group;
	
	private String subscribeId;
	private String execId;
	private String runId;
	
	//获取订阅列表或者运行列表时 使用
	private boolean incloudDetail = false; //获取订阅列表时，是否包含汇总信息(会比较慢)
	private int page = -1; //-1:不分页
	private int pageSize=10; //每页条数
	private String search; //搜索项,暂时只支持根据名称过滤
	
	//查询任务详情时的参数
	private boolean incloudLog = false; //获取运行信息时 是否获取日志(会读日志文件)
	
	//删除任务时的参数
	private List<String> subscribeIds; //批量删除时使用
	
	//启动运行时的参数
	private boolean remoteRun = false; //是否远程运行
	private Map<String,Object> params; //当启动已存在的任务时，启动参数
	
	public QueryJobDto(String userId) {
		super(userId);
	}
	
	public QueryJobDto(String userId, String subscribeId) {
		super(userId);
		this.subscribeId = subscribeId;
	}
	
	public QueryJobDto(String userId, String subscribeId, String runId) {
		super(userId);
		this.subscribeId = subscribeId;
		this.runId = runId;
	}
	
	public QueryJobDto(String userId, String subscribeId,String execId ,String runId) {
		super(userId);
		this.subscribeId = subscribeId;
		this.execId =execId;
		this.runId = runId;
	}


	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getSubscribeId() {
		return subscribeId;
	}
	public void setSubscribeId(String subscribeId) {
		this.subscribeId = subscribeId;
	}
	public String getExecId() {
		return execId;
	}
	public void setExecId(String execId) {
		this.execId = execId;
	}
	
	public String getRunId() {
		return runId;
	}
	public void setRunId(String runId) {
		this.runId = runId;
	}

	public boolean isRemoteRun() {
		return remoteRun;
	}

	public void setRemoteRun(boolean remoteRun) {
		this.remoteRun = remoteRun;
	}

	public Map<String, Object> getParams() {
		return params;
	}

	public void setParams(Map<String, Object> params) {
		this.params = params;
	}
	
	public void addParam(String key, Object value) {
		if(this.params == null) {
			this.params = new HashMap<String, Object>();
		}
		this.params.put(key, value);
	}

	public boolean isIncloudDetail() {
		return incloudDetail;
	}
	public void setIncloudDetail(boolean incloudDetail) {
		this.incloudDetail = incloudDetail;
	}
	public boolean isIncloudLog() {
		return incloudLog;
	}
	public void setIncloudLog(boolean incloudLog) {
		this.incloudLog = incloudLog;
	}
	public List<String> getSubscribeIds() {
		return subscribeIds;
	}
	public void setSubscribeIds(List<String> subscribeIds) {
		this.subscribeIds = subscribeIds;
	}
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public String getSearch() {
		return search;
	}
	public void setSearch(String search) {
		this.search = search;
	}

	@Override
	public String toString() {
		return "QueryJobDto [group=" + group + ", subscribeId=" + subscribeId + ", execId=" + execId + ", runId="
				+ runId + ", incloudDetail=" + incloudDetail + ", page=" + page + ", pageSize=" + pageSize + ", search="
				+ search + ", incloudLog=" + incloudLog + ", subscribeIds=" + subscribeIds + ", remoteRun=" + remoteRun
				+ ", params=" + params + ", super=" + super.toString() + "]";
	}



}
