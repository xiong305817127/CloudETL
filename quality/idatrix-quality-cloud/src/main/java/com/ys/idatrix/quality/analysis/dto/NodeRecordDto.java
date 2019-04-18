package com.ys.idatrix.quality.analysis.dto;

import java.util.Date;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.time.DateFormatUtils;

import com.ys.idatrix.quality.ext.utils.DatabaseHelper.IgnoreField;
import com.ys.idatrix.quality.logger.CloudLogConst;

@Table(catalog="idatrix.analysis.node.record.tableName",name="tbl_nodeRecord")
public class NodeRecordDto {
	
	@Id
	private String uuid	;	
	
	//所属租户ID
    private String renterId;
	private String userName	;	
	private String analysisName	;	
	private String execId	;
	private String nodId	;	
	private String nodType	;	
	
	private long succNum	;		
	private long errNum	;
	@IgnoreField
	private long totalNum	;		
	
	private Date updateTime	;	
	
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
	public String getRenterId() {
		return renterId;
	}
	public void setRenterId(String renterId) {
		this.renterId = renterId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getAnalysisName() {
		return analysisName;
	}
	public void setAnalysisName(String analysisName) {
		this.analysisName = analysisName;
	}
	public String getNodId() {
		return nodId;
	}
	public void setNodId(String nodId) {
		this.nodId = nodId;
	}
	
	public String getExecId() {
		return execId;
	}
	public void setExecId(String execId) {
		this.execId = execId;
	}
	
	public long getSuccNum() {
		return succNum;
	}
	public void setSuccNum(long succNum) {
		this.succNum = succNum;
		getTotalNum();
	}
	public long getErrNum() {
		return errNum;
	}
	public void setErrNum(long errNum) {
		this.errNum = errNum;
		getTotalNum();
	}
	public long increaseSuccessNum() {
		if( this.succNum <= 0 ) {
			this.succNum = 0;
		}
		++this.succNum;
		getTotalNum();
		return this.succNum;
	}
	
	public long increaseErrorNum() {
		if( this.errNum <= 0 ) {
			this.errNum = 0;
		}
		++this.errNum;
		getTotalNum();
		return this.errNum;
	}
	
	public long getTotalNum() {
		this.totalNum = succNum + errNum ;
		return totalNum;
	}
	public void setTotalNum(long totalNum) {
		this.totalNum = totalNum;
	}
	
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	
	public String getUpdateTimeStr() {
		if( updateTime != null ) {
			return DateFormatUtils.format(updateTime, CloudLogConst.EXEC_TIME_PATTERN1);
		}
		return "";
	}
	
	public String getNodType() {
		return nodType;
	}
	public void setNodType(String nodType) {
		this.nodType = nodType;
	}
}
