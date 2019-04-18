package com.ys.idatrix.cloudetl.service.step;

import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.ys.idatrix.cloudetl.dto.step.steps.flow.SPDummy;
import com.ys.idatrix.cloudetl.dto.step.steps.flow.SPFilterRows;
import com.ys.idatrix.cloudetl.subscribe.api.dto.step.FilterRowsDto;
import com.ys.idatrix.cloudetl.util.SubcribeUtils;

@Component
@Scope("prototype")
public class FilterRowsService  extends StepServiceInterface<FilterRowsDto> {

	/**
	 * 创建FilterRows 的 StepParameter对象 <br>
	 * 必须传入过滤成功需要发往处理的下一步的步骤名
	 */
	@Override
	public Object createParameter(Object... params) throws Exception {
		
		FilterRowsDto filterRowsDto = getStepDto();
		String sendTrueTo= (String) getParam(0, params);
		
		SPFilterRows f = new SPFilterRows();
		f.setSendTrueTo(sendTrueTo);
		f.setCondition(SubcribeUtils.parseCondition(filterRowsDto.getCondition()));

		return f;
	}

	@Override
	public List<String> addCurStepToMeta(String transName, String group, Map<String, String> params)
			throws Exception {

		List<String> inStepNames = Lists.newArrayList() ;
		FilterRowsDto filterCondition = getStepDto();
		String filterName = getStepName();
		String dummyName = "filter-dummy" ;
		
		SPFilterRows filter = (SPFilterRows) createParameter(dummyName);
		stepService.addAndUpdateStepMeta(transName,group, filterName , filterCondition.getType(), filter);
		inStepNames.add(filterName);
		
		stepService.addAndUpdateStepMeta(transName,group, dummyName , "Dummy" , new SPDummy());
		inStepNames.add(dummyName);
		
		return inStepNames ;
		
	}

}
