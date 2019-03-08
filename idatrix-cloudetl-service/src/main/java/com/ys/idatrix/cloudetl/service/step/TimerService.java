package com.ys.idatrix.cloudetl.service.step;

import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.ys.idatrix.cloudetl.dto.entry.entries.general.SPspecial;
import com.ys.idatrix.cloudetl.subscribe.api.dto.step.TimerDto;

@Component
@Scope("prototype")
public class TimerService  extends StepServiceInterface<TimerDto>{

	@Override
	protected TimerDto getStepDto() throws Exception {
		if(t == null ) {
			t = new TimerDto();
			t.setRepeat(false);
			t.setSchedulerType(0);
		}
		return t ;
	}
	
	@Override
	public String getStepName() {
		return "START" ;
	}
	
	@Override
	public Object createParameter(Object... params) throws Exception {
		TimerDto timerDto = getStepDto();

		SPspecial s = new SPspecial();
		s.setRepeat(timerDto.isRepeat());
		s.setSchedulerType(timerDto.getSchedulerType());
		s.setIntervalMinutes(timerDto.getMinutes());
		s.setIntervalSeconds(timerDto.getSeconds());
		s.setHour(timerDto.getHour());
		s.setMinutes(timerDto.getMinutes());
		s.setWeekDay(timerDto.getWeekDay());
		s.setDayOfMonth(timerDto.getDayOfMonth());
		s.setMonthOfYear(timerDto.getMonthOfYear());
		s.setIntervalDelayMinutes(timerDto.getIntervalDelayMinutes());
		
		return s;
	}

	@Override
	public List<String> addCurStepToMeta(String jobName, String group, Map<String, String> params)
			throws Exception {
		
		List<String> outNames = Lists.newArrayList();
		String startName = getStepName();
		
		SPspecial timer = (SPspecial) createParameter();
		stepService.addAndUpdateEntryMeta(jobName,group, startName, "SPECIAL", timer,true);
		outNames.add(startName);
		
		return outNames ;
		
	}

	
}
