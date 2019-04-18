/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.dto.trans;

import io.swagger.annotations.ApiModel;

/**
 * 转换 执行
 * @author JW
 * @since 05-12-2017
 *
 */
@ApiModel("执行转换步骤度量信息")
public class TransExecStepMeasureDto {
	
    private String stepName;
    private String copy;
    private String linesRead;
    private String linesWritten;
    private String linesInput;
    private String linesOutput;
    private String linesUpdated;
    private String linesRejected;
    private String errors;
    private String statusDescription;
    private String seconds;
    private String speed;
    private String priority;
    
    public void setStepName(String stepName) {
        this.stepName = stepName;
    }
    public String getStepName() {
        return stepName;
    }

    public void setCopy(String copy) {
        this.copy = copy;
    }
    public String getCopy() {
        return copy;
    }

    public void setLinesRead(String linesRead) {
        this.linesRead = linesRead;
    }
    public String getLinesRead() {
        return linesRead;
    }

    public void setLinesWritten(String linesWritten) {
        this.linesWritten = linesWritten;
    }
    public String getLinesWritten() {
        return linesWritten;
    }

    public void setLinesInput(String linesInput) {
        this.linesInput = linesInput;
    }
    public String getLinesInput() {
        return linesInput;
    }

    public void setLinesOutput(String linesOutput) {
        this.linesOutput = linesOutput;
    }
    public String getLinesOutput() {
        return linesOutput;
    }

    public void setLinesUpdated(String linesUpdated) {
        this.linesUpdated = linesUpdated;
    }
    public String getLinesUpdated() {
        return linesUpdated;
    }

    public void setLinesRejected(String linesRejected) {
        this.linesRejected = linesRejected;
    }
    public String getLinesRejected() {
        return linesRejected;
    }

    public void setErrors(String errors) {
        this.errors = errors;
    }
    public String getErrors() {
        return errors;
    }

    public void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
    }
    public String getStatusDescription() {
        return statusDescription;
    }

    public void setSeconds(String seconds) {
        this.seconds = seconds;
    }
    public String getSeconds() {
        return seconds;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }
    public String getSpeed() {
        return speed;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }
    public String getPriority() {
        return priority;
    }
    
	/**
	 * 
	 */
	public TransExecStepMeasureDto() {
		
	}
    
	/**
	 * 
	 */
	public TransExecStepMeasureDto(String[] fields) {
		if (fields.length > 1)
			this.setStepName(fields[1]);
		if (fields.length > 2)
			this.setCopy(fields[2]);
		if (fields.length > 3)
			this.setLinesRead(fields[3]);
		if (fields.length > 4)
			this.setLinesWritten(fields[4]);
		if (fields.length > 5)
			this.setLinesInput(fields[5]);
		if (fields.length > 6)
			this.setLinesOutput(fields[6]);
		if (fields.length > 7)
			this.setLinesUpdated(fields[7]);
		if (fields.length > 8)
			this.setLinesRejected(fields[8]);
		if (fields.length > 9)
			this.setErrors(fields[9]);
		if (fields.length > 10)
			this.setStatusDescription(fields[10]);
		if (fields.length > 11)
			this.setSeconds(fields[11]);
		if (fields.length > 12)
			this.setSpeed(fields[12]);
		if (fields.length > 13)
			this.setPriority(fields[13]);
	}
	
	/*
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TransExecStepMeasureDto [stepName=" + stepName + ", copy=" + copy + ", linesRead=" + linesRead
				+ ", linesWritten=" + linesWritten + ", linesInput=" + linesInput + ", linesOutput=" + linesOutput
				+ ", linesUpdated=" + linesUpdated + ", linesRejected=" + linesRejected + ", errors=" + errors
				+ ", statusDescription=" + statusDescription + ", seconds=" + seconds + ", speed=" + speed
				+ ", priority=" + priority + "]";
	}

}
