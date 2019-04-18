package com.ys.idatrix.quality.recovery.trans.dto;

import java.io.Serializable;
import java.util.Map;
import java.util.Optional;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.trans.step.StepMeta;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.collect.Maps;

@JsonIgnoreProperties(value={"stepMeta","rowLine","nextlineDifference"}) 
public class StepLinesDto implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 172338634746952545L;
	
	private  Long  linesInput = 0L ;
	private  Long  linesOutput = 0L ;
	private  Long  linesRead = 0L ;
	private  Long  linesWritten = 0L ;
	private  Long  linesRejected = 0L ;
	private  Long  linesUpdated = 0L ;
	private  Long  linesErrors = 0L ;
	
	//输出
	private  Map<String,Long> nextEffectiveOutputLines;
	//输入
	private  Map<String,Long> preEffectiveInputLines;
	
	//行号,从1开始
	@JsonIgnore
	private transient Long rowLine;
	@JsonIgnore
	private transient StepMeta stepMeta;
	@JsonIgnore
	private transient Map<String,Long> nextlineDifference;
	
	/**
	 * 
	 */
	public StepLinesDto() {
		super();
	}
	/**
	 * @param linesInput
	 * @param linesOutput
	 * @param linesRead
	 * @param linesWritten
	 * @param linesRejected
	 * @param linesUpdated
	 * @param linesErrors
	 */
	public StepLinesDto(Long linesInput, Long linesOutput, Long linesRead, Long linesWritten, Long linesRejected,
			Long linesUpdated, Long linesErrors) {
		super();
		this.linesInput = linesInput;
		this.linesOutput = linesOutput;
		this.linesRead = linesRead;
		this.linesWritten = linesWritten;
		this.linesRejected = linesRejected;
		this.linesUpdated = linesUpdated;
		this.linesErrors = linesErrors;
	}
	
	/**
	 * @return the linesInput
	 */
	public Long getLinesInput() {
		return linesInput;
	}
	/**
	 * @param  设置 linesInput
	 */
	public void setLinesInput(Long linesInput) {
		this.linesInput = linesInput;
	}
	/**
	 * @return the linesOutput
	 */
	public Long getLinesOutput() {
		return linesOutput;
	}
	/**
	 * @param  设置 linesOutput
	 */
	public void setLinesOutput(Long linesOutput) {
		this.linesOutput = linesOutput;
	}
	/**
	 * @return the linesRead
	 */
	public Long getLinesRead() {
		return linesRead;
	}
	/**
	 * @param  设置 linesRead
	 */
	public void setLinesRead(Long linesRead) {
		this.linesRead = linesRead;
	}
	/**
	 * @return the linesWritten
	 */
	public Long getLinesWritten() {
		return linesWritten;
	}
	/**
	 * @param  设置 linesWritten
	 */
	public void setLinesWritten(Long linesWritten) {
		this.linesWritten = linesWritten;
	}
	/**
	 * @return the linesRejected
	 */
	public Long getLinesRejected() {
		return linesRejected;
	}
	/**
	 * @param  设置 linesRejected
	 */
	public void setLinesRejected(Long linesRejected) {
		this.linesRejected = linesRejected;
	}
	/**
	 * @return the linesUpdated
	 */
	public Long getLinesUpdated() {
		return linesUpdated;
	}
	/**
	 * @param  设置 linesUpdated
	 */
	public void setLinesUpdated(Long linesUpdated) {
		this.linesUpdated = linesUpdated;
	}
	/**
	 * @return the linesErrors
	 */
	public Long getLinesErrors() {
		return linesErrors;
	}
	/**
	 * @param  设置 linesErrors
	 */
	public void setLinesErrors(Long linesErrors) {
		this.linesErrors = linesErrors;
	}
	
	/**
	 * @return the stepMeta
	 */
	public StepMeta getStepMeta() {
		return stepMeta;
	}
	/**
	 * @param  设置 stepMeta
	 */
	public void setStepMeta(StepMeta stepMeta) {
		this.stepMeta = stepMeta;
	}

	/**
	 * @return the rowLine
	 */
	public Long getRowLine() {
		return rowLine;
	}
	/**
	 * @param  设置 rowLine
	 */
	public void setRowLine(Long rowLine) {
		this.rowLine = rowLine;
	}
	/**
	 * @return the nextEffectiveOutputLines
	 */
	public Map<String, Long> getNextEffectiveOutputLines() {
		return nextEffectiveOutputLines;
	}
	/**
	 * @param  设置 nextEffectiveOutputLines
	 */
	public void setNextEffectiveOutputLines(Map<String, Long> nextEffectiveOutputLines) {
		this.nextEffectiveOutputLines = nextEffectiveOutputLines;
	}
	
	public void addNextEffectiveOutputLines(String stepName,Long lines) {
		if( nextEffectiveOutputLines == null) {
			nextEffectiveOutputLines = Maps.newHashMap();
		}
		nextEffectiveOutputLines.put(stepName, lines);
	}
	
	public void incrementNextEffectiveOutputLines(String stepName) {
		if( nextEffectiveOutputLines == null) {
			nextEffectiveOutputLines = Maps.newHashMap();
			nextEffectiveOutputLines.put(stepName, 1L);
		}else {
			long newLine = nextEffectiveOutputLines.get(stepName) ==  null ? 1 : ( nextEffectiveOutputLines.get(stepName)+1 );
			nextEffectiveOutputLines.put(stepName, newLine);
		}
	}
	
	public Long countNextEffectiveOutputLines() {
		if( nextEffectiveOutputLines != null ) {
			Optional<Long> opt = nextEffectiveOutputLines.values().stream().reduce((result, element)->{return result+element ;});
			if(opt.isPresent()) {
				return opt.get();
			}
		}
		return null;
	}
	
	/**
	 * @return the preEffectiveInputLines
	 */
	public Map<String, Long> getPreEffectiveInputLines() {
		return preEffectiveInputLines;
	}
	/**
	 * @param  设置 preEffectiveInputLines
	 */
	public void setPreEffectiveInputLines(Map<String, Long> preEffectiveInputLines) {
		this.preEffectiveInputLines = preEffectiveInputLines;
	}
	
	public void addPreEffectiveInputLines(String stepName,Long lines) {
		if( preEffectiveInputLines == null) {
			preEffectiveInputLines = Maps.newHashMap();
		}
		preEffectiveInputLines.put(stepName, lines);
	}
	
	public void incrementPreEffectiveInputLines(String stepName) {
		if( preEffectiveInputLines == null) {
			preEffectiveInputLines = Maps.newHashMap();
			preEffectiveInputLines.put(stepName, 1L);
		}else {
			long newLine = preEffectiveInputLines.get(stepName) == null? 1:(preEffectiveInputLines.get(stepName)+1);
			preEffectiveInputLines.put(stepName, newLine);
		}
	}
	
	public Long countPreEffectiveInputLines() {
		if( preEffectiveInputLines != null ) {
			Optional<Long> opt = preEffectiveInputLines.values().stream().reduce((result, element)->{return result+element ;});
			if(opt.isPresent()) {
				return opt.get();
			}
		}
		return null;
	}
	
	
	/**
	 * @return the nextlineDifference
	 */
	public Map<String, Long> getNextlineDifference() {
		return nextlineDifference;
	}
	/**
	 * @param  设置 nextlineDifference
	 */
	public void setNextlineDifference(Map<String, Long> nextlineDifference) {
		this.nextlineDifference = nextlineDifference;
	}
	
	public void addNextlineDifference(String nextStep,Long lineDifference) {
		if(nextlineDifference ==  null) {
			nextlineDifference = Maps.newHashMap();
		}
		this.nextlineDifference.put(nextStep, lineDifference);
	}
	
	
	public StepLinesDto clone() {
		StepLinesDto ldto = new StepLinesDto(linesInput, linesOutput, linesRead, linesWritten, linesRejected, linesUpdated, linesErrors);
		ldto.setStepMeta(stepMeta);
		ldto.setRowLine(rowLine);
		if(preEffectiveInputLines != null && preEffectiveInputLines.size() >0) {
			Map<String, Long> peinput = Maps.newHashMap();
			peinput.putAll(preEffectiveInputLines);
			ldto.setPreEffectiveInputLines(peinput);
		}
		if(nextEffectiveOutputLines != null && nextEffectiveOutputLines.size() >0) {
			Map<String, Long> neoutput = Maps.newHashMap();
			neoutput.putAll(nextEffectiveOutputLines);
			ldto.setPreEffectiveInputLines(neoutput);
		}
		return ldto;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */



	public Map<String,Object> getSaveString() {
		Map<String,Object> map=Maps.newHashMap();
		map.put("lines", linesInput+":"+linesOutput+":"+linesRead+":"+linesWritten+":"+linesRejected+":"+linesUpdated+":"+linesErrors);
		map.put("nextLines", nextEffectiveOutputLines);
		map.put("preLines", preEffectiveInputLines);
		return map;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "StepLinesDto [rowLine=" + rowLine + ", lines=" + linesInput + ":" + linesOutput	+ ":" + linesRead + ":" + linesWritten + ":" + linesRejected+ ":" + linesUpdated + ":" + linesErrors + ", nextLines="
				+ nextEffectiveOutputLines + ", preLines=" + preEffectiveInputLines	+ ", DiffLines=" + nextlineDifference + "]";
	}
	@SuppressWarnings("unchecked")
	public static StepLinesDto parseSaveString(Object  data) {
		if(data instanceof Map ) {
			 Map<String,Object> map = ( Map<String,Object>)data;
			String lines = (String) map.get("lines");
			if(!Utils.isEmpty(lines)) {
				String[] l = lines.split(":");
				StepLinesDto res =new StepLinesDto(Long.valueOf(l[0]),Long.valueOf(l[1]),Long.valueOf(l[2]),Long.valueOf(l[3]),Long.valueOf(l[4]),Long.valueOf(l[5]),Long.valueOf(l[6]));
				res.setNextEffectiveOutputLines( (Map<String, Long>) map.get("nextLines"));
				res.setPreEffectiveInputLines( (Map<String, Long>) map.get("preLines") );
				
				return res;
			}
		}
		
		return null ;
	}

}
