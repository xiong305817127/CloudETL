/**
 * 云化数据集成系统 iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.service.trans;

import java.util.List;

import com.ys.idatrix.cloudetl.dto.common.ReturnCodeDto;
import com.ys.idatrix.cloudetl.dto.step.StepConfigsDto;
import com.ys.idatrix.cloudetl.dto.step.StepCopyDto;
import com.ys.idatrix.cloudetl.dto.step.StepDetailsDto;
import com.ys.idatrix.cloudetl.dto.step.StepFieldDto;
import com.ys.idatrix.cloudetl.dto.step.StepHeaderDto;
import com.ys.idatrix.cloudetl.dto.step.StepNameCheckResultDto;
import com.ys.idatrix.cloudetl.dto.step.StepPositionDto;
import com.ys.idatrix.cloudetl.dto.step.TransStepDto;

/**
 * Access to step related information services
 * @author JW
 * @since 2017年5月24日
 *
 */
public interface CloudStepService {
	
	public ReturnCodeDto addStep(StepHeaderDto stepHeader) throws Exception;
	
	public StepNameCheckResultDto checkStepName(TransStepDto transStep) throws Exception;
	
	public StepDetailsDto editStep(TransStepDto transStep) throws Exception;
	
	public ReturnCodeDto saveStep(StepDetailsDto stepDetails) throws Exception;
	
	public StepConfigsDto editStepConfigs(TransStepDto transStep) throws Exception;
	
	public ReturnCodeDto saveStepConfigs(StepConfigsDto stepConfigs) throws Exception;
	
	public ReturnCodeDto deleteStep(TransStepDto transStep) throws Exception;
	
	public ReturnCodeDto moveStep(StepPositionDto stepPosition) throws Exception;
	
	public List<StepFieldDto> getInputFields(TransStepDto transStep) throws Exception;
	
	public List<StepFieldDto> getOutputFields(TransStepDto transStep) throws Exception;
	
	public Object getDetails(TransStepDto transStep) throws Exception;

	ReturnCodeDto copyStep(StepCopyDto stepcopy) throws Exception;

}
