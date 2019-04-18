/**
 * 云化数据集成系统 iDatrix CloudETL
 */
package com.ys.idatrix.quality.service.trans;

import java.util.List;

import com.ys.idatrix.quality.dto.common.ReturnCodeDto;
import com.ys.idatrix.quality.dto.step.StepConfigsDto;
import com.ys.idatrix.quality.dto.step.StepCopyDto;
import com.ys.idatrix.quality.dto.step.StepDetailsDto;
import com.ys.idatrix.quality.dto.step.StepFieldDto;
import com.ys.idatrix.quality.dto.step.StepHeaderDto;
import com.ys.idatrix.quality.dto.step.StepNameCheckResultDto;
import com.ys.idatrix.quality.dto.step.StepPositionDto;
import com.ys.idatrix.quality.dto.step.TransStepDto;

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
