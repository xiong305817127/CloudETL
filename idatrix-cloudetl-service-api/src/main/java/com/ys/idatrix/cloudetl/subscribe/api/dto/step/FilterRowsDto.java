package com.ys.idatrix.cloudetl.subscribe.api.dto.step;

import java.io.Serializable;

import com.ys.idatrix.cloudetl.subscribe.api.dto.parts.RowConditionDto;

public class FilterRowsDto   extends StepDto implements Serializable{

	private static final long serialVersionUID = -5100873886529466772L;
	
	public static final String type ="FilterRows";
	//过滤条件
	private RowConditionDto condition;
	
	public RowConditionDto getCondition() {
		return condition;
	}

	public void setCondition(RowConditionDto condition) {
		this.condition = condition;
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public boolean isJobStep() {
		return false;
	}

	@Override
	public String toString() {
		return "FilterRowsDto [condition=" + condition + ", super=" + super.toString() + "]";
	}
	

}
