package com.ys.idatrix.cloudetl.subscribe.api.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ys.idatrix.cloudetl.subscribe.api.dto.parts.CommonDto;
import com.ys.idatrix.cloudetl.subscribe.api.dto.step.StepDto;

public class CreateJobDto extends CommonDto implements Serializable{

	private static final long serialVersionUID = 3265237847115237920L;
	
	private String name ;
	private String group ;
	private String description;

	//任务启动设置,定时(TimerDto)
	private StepDto timer ;
	//表输入(TableInputDto)，文件输入(FileInputDto)
	private StepDto dataInput;
	//过滤记录(FilterRowsDto)
	private StepDto filterCondition;
	
	//表输出(TableOutputDto)，数据库插入更新(InsertUpdateDto)，文件输出，文件复制(FileCopyDto)
	@Deprecated
	private StepDto dataOutput;
	
	//表输出(TableOutputDto)，数据库插入更新(InsertUpdateDto)，文件输出，ES批量插入(ElasticSearchDto)
	private List<StepDto> transDataOutputs;
	//文件复制(FileCopyDto),文件SFTP上传(SftpPutDto)
	private  List<StepDto> jobDataOutputs;

	//是否立即执行
	private boolean immediatelyRun = true;
	//是否远程运行,默认本地运行
	private boolean remoteRun = false;
	//立即执行时的执行参数
	private Map<String,Object> params;
	
	
	public CreateJobDto(String userId) {
		super(userId);
	}
	
	public CreateJobDto(String userId, String name,String group) {
		super(userId);
		this.name = name;
		this.group = group;
	}
	
	public CreateJobDto(String userId, String name, String group,String description) {
		super(userId);
		this.name = name;
		this.group = group;
		this.description = description;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	@Deprecated
	public StepDto getDataOutput() {
		return dataOutput;
	}
	
	@Deprecated
	public void setDataOutput(StepDto dataOutput) {
		this.dataOutput = dataOutput;
		if(dataOutput.isJobStep()) {
			addJobDataOutput(dataOutput);
		}else {
			addTransDataOutput(dataOutput);
		}
	}
	
	public List<StepDto> getTransDataOutputs() {
		return transDataOutputs;
	}

	public void setTransDataOutputs(List<StepDto> transDataOutputs) {
		this.transDataOutputs = transDataOutputs;
	}
	
	public void addTransDataOutput(StepDto transDataOutput) {
		if( this.transDataOutputs == null ) {
			this.transDataOutputs = new ArrayList<StepDto>();
		}
		this.transDataOutputs.add(transDataOutput);
	}

	public List<StepDto> getJobDataOutputs() {
		return jobDataOutputs;
	}

	public void setJobDataOutputs(List<StepDto> jobDataOutputs) {
		this.jobDataOutputs = jobDataOutputs;
	}

	public void addJobDataOutput(StepDto jobDataOutput) {
		if( this.jobDataOutputs == null ) {
			this.jobDataOutputs = new ArrayList<StepDto>();
		}
		this.jobDataOutputs.add(jobDataOutput);
	}

	public StepDto getFilterCondition() {
		return filterCondition;
	}
	public void setFilterCondition(StepDto filterCondition) {
		this.filterCondition = filterCondition;
	}
	public StepDto getTimer() {
		return timer;
	}
	public void setTimer(StepDto timer) {
		this.timer = timer;
	}
	public void setDataInput(StepDto dataInput) {
		this.dataInput = dataInput;
	}
	public StepDto getDataInput() {
		return dataInput;
	}
	public boolean isImmediatelyRun() {
		return immediatelyRun;
	}
	public void setImmediatelyRun(boolean immediatelyRun) {
		this.immediatelyRun = immediatelyRun;
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

	@Override
	public String toString() {
		return "CreateJobDto [name=" + name + ", group=" + group + ", description=" + description + ", timer=" + timer
				+ ", dataInput=" + dataInput + ", filterCondition=" + filterCondition + ", transDataOutputs="
				+ transDataOutputs + ", jobDataOutputs=" + jobDataOutputs + ", immediatelyRun=" + immediatelyRun
				+ ", remoteRun=" + remoteRun + ", params=" + params + ", toString()=" + super.toString() + "]";
	}

	

	
}
