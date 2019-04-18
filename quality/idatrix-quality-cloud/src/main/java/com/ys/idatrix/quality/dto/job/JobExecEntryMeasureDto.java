/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.quality.dto.job;

import java.util.List;

import org.pentaho.di.core.util.Utils;

import com.google.common.collect.Lists;

import io.swagger.annotations.ApiModel;


/**
 * 转换 执行
 * @author JW
 * @since 05-12-2017
 *
 */
@ApiModel("调度步骤度量信息")
public class JobExecEntryMeasureDto {
	
    private String entryName;
    private String comment;
    private String result;
    private String reason;
    private String filename;
    private String nr;
    private String logDate;
    
    private List<JobExecEntryMeasureDto> childEntryMeasure;
    
    private String carteObjectId;
    private long linesRead;
    private long linesWritten;
    private long linesInput;
    private long linesOutput;
    private long linesUpdated;
    private long linesRejected;
    private long errors;
    
    //次数
    private long successTimes=0;
    private long failTimes=0;
    
	/**
	 * 
	 */
	public JobExecEntryMeasureDto() {
		super();
	}
	/**
	 * @param entryName
	 */
	public JobExecEntryMeasureDto(String entryName) {
		super();
		this.entryName = entryName;
	}
	/**
	 * @param fields
	 */
	public JobExecEntryMeasureDto(String[] fields) {


	}
	/**
	 * @return entryName
	 */
	public String getEntryName() {
		return entryName;
	}
	/**
	 * @param  设置 entryName
	 */
	public void setEntryName(String entryName) {
		this.entryName = entryName;
	}
	/**
	 * @return comment
	 */
	public String getComment() {
		return comment;
	}
	/**
	 * @param  设置 comment
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}
	/**
	 * @return result
	 */
	public String getResult() {
		return result;
	}
	/**
	 * @param  设置 result
	 */
	public void setResult(String result) {
		this.result = result;
	}
	/**
	 * @return reason
	 */
	public String getReason() {
		return reason;
	}
	/**
	 * @param  设置 reason
	 */
	public void setReason(String reason) {
		this.reason = reason;
	}
	/**
	 * @return filename
	 */
	public String getFilename() {
		return filename;
	}
	/**
	 * @param  设置 filename
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}
	/**
	 * @return nr
	 */
	public String getNr() {
		return nr;
	}
	/**
	 * @param  设置 nr
	 */
	public void setNr(String nr) {
		this.nr = nr;
	}
	/**
	 * @return logDate
	 */
	public String getLogDate() {
		return logDate;
	}
	/**
	 * @param  设置 logDate
	 */
	public void setLogDate(String logDate) {
		this.logDate = logDate;
	}
	
	/**
	 * @return childEntryMeasure
	 */
	public List<JobExecEntryMeasureDto> getChildEntryMeasure() {
		return childEntryMeasure;
	}
	/**
	 * @param  设置 childEntryMeasure
	 */
	public void setChildEntryMeasure(List<JobExecEntryMeasureDto> childEntryMeasure) {
		this.childEntryMeasure = childEntryMeasure;
	}
	
	public void addChildEntryMeasure(JobExecEntryMeasureDto cem) {
		if( this.childEntryMeasure == null){
			this.childEntryMeasure=Lists.newArrayList();
		}
		if(childEntryMeasure.contains(cem)) {
			 int index = childEntryMeasure.indexOf(cem);
			 JobExecEntryMeasureDto cem_old = childEntryMeasure.get( index );
			 if(!Utils.isEmpty(cem.getResult())) {
				 if("Success".equals(cem.getResult())) {
					 cem.setSuccessTimes(cem_old.getSuccessTimes()+1);
					 cem.setFailTimes(cem_old.getFailTimes());
				 }else {
					 cem.setSuccessTimes(cem_old.getSuccessTimes());
					 cem.setFailTimes(cem_old.getFailTimes()+1);
				 } 
			 }
			 this.childEntryMeasure.set(index, cem);
		}else {
			this.childEntryMeasure.add(cem);
		}
		
	}
	
	/**
	 * @return carteObjectId
	 */
	public String getCarteObjectId() {
		return carteObjectId;
	}
	/**
	 * @param  设置 carteObjectId
	 */
	public void setCarteObjectId(String carteObjectId) {
		this.carteObjectId = carteObjectId;
	}
	/**
	 * @return linesRead
	 */
	public long getLinesRead() {
		return linesRead;
	}
	/**
	 * @param  设置 linesRead
	 */
	public void setLinesRead(long linesRead) {
		this.linesRead = linesRead;
	}
	/**
	 * @return linesWritten
	 */
	public long getLinesWritten() {
		return linesWritten;
	}
	/**
	 * @param  设置 linesWritten
	 */
	public void setLinesWritten(long linesWritten) {
		this.linesWritten = linesWritten;
	}
	/**
	 * @return linesInput
	 */
	public long getLinesInput() {
		return linesInput;
	}
	/**
	 * @param  设置 linesInput
	 */
	public void setLinesInput(long linesInput) {
		this.linesInput = linesInput;
	}
	/**
	 * @return linesOutput
	 */
	public long getLinesOutput() {
		return linesOutput;
	}
	/**
	 * @param  设置 linesOutput
	 */
	public void setLinesOutput(long linesOutput) {
		this.linesOutput = linesOutput;
	}
	/**
	 * @return linesUpdated
	 */
	public long getLinesUpdated() {
		return linesUpdated;
	}
	/**
	 * @param  设置 linesUpdated
	 */
	public void setLinesUpdated(long linesUpdated) {
		this.linesUpdated = linesUpdated;
	}
	/**
	 * @return linesRejected
	 */
	public long getLinesRejected() {
		return linesRejected;
	}
	/**
	 * @param  设置 linesRejected
	 */
	public void setLinesRejected(long linesRejected) {
		this.linesRejected = linesRejected;
	}
	/**
	 * @return errors
	 */
	public long getErrors() {
		return errors;
	}
	/**
	 * @param  设置 errors
	 */
	public void setErrors(long errors) {
		this.errors = errors;
	}
	
	/**
	 * @return the successTimes
	 */
	public long getSuccessTimes() {
		return successTimes;
	}
	/**
	 * @param  设置 successTimes
	 */
	public void setSuccessTimes(long successTimes) {
		this.successTimes = successTimes;
	}
	/**
	 * @return the failTimes
	 */
	public long getFailTimes() {
		return failTimes;
	}
	/**
	 * @param  设置 failTimes
	 */
	public void setFailTimes(long failTimes) {
		this.failTimes = failTimes;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		JobExecEntryMeasureDto other = (JobExecEntryMeasureDto) obj;
		if (comment == null) {
			if (other.comment != null)
				return false;
		} else if (!comment.equals(other.comment))
			return false;
		if (entryName == null) {
			if (other.entryName != null)
				return false;
		} else if (!entryName.equals(other.entryName))
			return false;
		if (nr == null) {
			if (other.nr != null)
				return false;
		} else if (!nr.equals(other.nr))
			return false;
		if (reason == null) {
			if (other.reason != null)
				return false;
		} else if (!reason.equals(other.reason))
			return false;
		return true;
	}

	
	
}
