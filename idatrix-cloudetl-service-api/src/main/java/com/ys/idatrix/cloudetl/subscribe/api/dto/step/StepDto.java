package com.ys.idatrix.cloudetl.subscribe.api.dto.step;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class StepDto implements Serializable {
	
	private static final long serialVersionUID = -5607663304251743143L;

	private boolean appendTrans = false ;
	
	private List<StepDto> nextStepDtos ;
	
	public abstract String getType();
	
	public abstract boolean isJobStep();
	
	public void setAppendTrans(boolean appendTrans) throws Exception {
		if( !isJobStep() ) {
			throw new Exception("只有调度组件才支持该操作.");
		}
		this.appendTrans = appendTrans;
	}
	
	public boolean isAppendTrans() {
		return appendTrans;
	}

	public void addNextStepDto( StepDto nextStepDto) throws Exception{
		if( nextStepDto.isJobStep() ^ isJobStep() ) {
			throw new Exception("组件类型不一致,不能相连.");
		}
		if(this.nextStepDtos == null ) {
			this.nextStepDtos = new ArrayList<StepDto>();
		}
		this.nextStepDtos.add(nextStepDto);
	}
	
	public List<StepDto> getNextStepDtos() {
		return this.nextStepDtos  ;
	}

	public void setNextStepDtos(List<StepDto> nextStepDtos) {
		this.nextStepDtos = nextStepDtos;
	}

	@Override
	public String toString() {
		return "StepDto [appendTrans=" + appendTrans + ", nextStepDtos=" + nextStepDtos + "]";
	}
	
}
