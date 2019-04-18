package com.ys.idatrix.quality.recovery.trans;

import java.util.Map;
import java.util.Map.Entry;

import org.pentaho.di.core.util.IdatrixPropertyUtil;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.step.StepMeta;
import com.google.common.collect.Maps;
import com.ys.idatrix.quality.ext.utils.RedisUtil;
import com.ys.idatrix.quality.recovery.trans.dto.StepLinesDto;


public class CleanStepLines implements Runnable {
	
	private Trans trans;
	private ResumeTransParser resumeTransParser;
	private Map<StepMeta,StepLinesDto> outputStepLines;
	private Map<StepMeta,StepLinesDto>  tempOutputStepLines;
	private boolean isDone=false ;
	
	private boolean isChange = false;
	private long cleanTime;

	public CleanStepLines( Trans trans ,ResumeTransParser resumeTransParser) {
		
		this.trans = trans;
		this.resumeTransParser = resumeTransParser;
		this.outputStepLines = Maps.newHashMap();
		this.tempOutputStepLines = Maps.newHashMap();
		
		cleanTime = Long.valueOf( IdatrixPropertyUtil.getProperty("idatrix.breakpoint.cache.clean.time" ,"200") );
	}
	
	
	@Override
	public void run() {
		resumeTransParser.logger.logDetailed("清理线程启动...");
		while(!isDone && !trans.isFinishedOrStopped()) {
			if(outputStepLines.size()>0 && isChange) {
				synchronized (outputStepLines) {
					tempOutputStepLines.clear();
					tempOutputStepLines.putAll(outputStepLines);
					isChange = false;
				}
					
				try {
					for( Entry<StepMeta, StepLinesDto> outEntry : tempOutputStepLines.entrySet()) {
						//根据后面的步骤和当前行信息,获取有效的行数
						resumeTransParser.readStepLinesByScore(outEntry.getValue());
					}
					resumeTransParser.logger.logDetailed("清理线程,有效输出步骤:"+tempOutputStepLines);
					
					Map<StepMeta, StepLinesDto> allStep = resumeTransParser.getEffectiveLines(tempOutputStepLines);
					for (StepMeta step : allStep.keySet()) {
						resumeTransParser.logger.logRowlevel(" 清理步骤 "+step.getName()+",有效行数 :"+allStep.get(step).getRowLine());
						RedisUtil.zRemoveByScore(resumeTransParser.getLinesListKey(step),0,allStep.get(step).getRowLine()-1);
					}
					
				} catch (Exception e) {
					resumeTransParser.logger.logError("清理线程 发生异常",e);
				}
					
			}
			//休眠200毫秒
			try {
				//更新时间戳
				RedisUtil.hset(ResumeTransParser.getTransKey(resumeTransParser.transInfoDto),ResumeTransParser.TransUpdateKey, System.currentTimeMillis());
				
				Thread.sleep(cleanTime);
			} catch (InterruptedException e) {
			}
		}
	}
	
	/**
	 * 增加当前输出步骤的已处理行数<br>
	 * 调用频繁 不能长时间阻塞
	 * @param outStep
	 * @param rowLine
	 */
	public void addOutputStep(StepMeta outStep , Long rowLine) {
		synchronized (outputStepLines) {
			
			StepLinesDto oldValue = outputStepLines.get(outStep) ;
			if(oldValue!= null && oldValue.getRowLine() == rowLine) {
				return ;
			}
			
			long l = rowLine == null ? 0L : rowLine;
			StepLinesDto sld = new StepLinesDto();
			sld.setRowLine(l);
			sld.setStepMeta(outStep);
			outputStepLines.put(outStep, sld);
			
			isChange = true;
		}
	}
	
	public void setDone() {
		isDone = true;
	}

}
