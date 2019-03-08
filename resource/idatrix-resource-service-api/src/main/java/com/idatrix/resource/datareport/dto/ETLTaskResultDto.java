package com.idatrix.resource.datareport.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class ETLTaskResultDto implements Serializable {

    private static final long serialVersionUID = 2468695241539012287L;

	/* 数据上报记录中的全局任务号 即创建任务时的名字属性 */
	private String subscribeSeqNum;

	/* 全局唯一的订阅ID */
	private String subscribeId;

    /* 一次执行ID */
    private String execId;

    /* ETL 每次定时运行时候 产生的ID */
    private String runningId;

	/* 用户 */
	private String userId;

	/* ETL任务执行结果 */
	private String result;

	/* FILE 拷贝成功结果 */
	private List<String> successFileNameList;

	/* 实际入库数据量 */
	private Long stockInCount;

	/* 实际新增入库数据量 */
	private Long insertCount;

	/* 实际更新入库数据量 */
	private Long updateCount;

	/* 错误数据数量 */
	private Long failCount;

	/*ETL当前running执行开始时间*/
	private Date startTime;

    /*ETL当前running执行结束时间*/
    private Date endTime;

	/* 入库时间 */
	private Date stockInTimeStamp;

	//状态码,服务调用是否成功。0：成功 ，非0：失败
	private int errCode = 0 ;
	//失败原因
	private String errorMessage;

	public ETLTaskResultDto(){
	    super();
    }

	public ETLTaskResultDto(String subscribeSeqNum, String subscribeId, String execId, String result) {
		this.subscribeSeqNum = subscribeSeqNum;
		this.subscribeId = subscribeId;
		this.execId = execId;
		this.result = result;
	}

    public String getRunningId() {
        return runningId;
    }

    public void setRunningId(String runningId) {
        this.runningId = runningId;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public List<String> getSuccessFileNameList() {
		return successFileNameList;
	}

	public void setSuccessFileNameList(List<String> successFileNameList) {
		this.successFileNameList = successFileNameList;
	}

	public String getSubscribeSeqNum() {
		return subscribeSeqNum;
	}

	public void setSubscribeSeqNum(String subscribeSeqNum) {
		this.subscribeSeqNum = subscribeSeqNum;
	}

	public int getErrCode() {
		return errCode;
	}

	public void setErrCode(int errCode) {
		this.errCode = errCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getSubscribeId() {
		return subscribeId;
	}

	public void setSubscribeId(String subscribeId) {
		this.subscribeId = subscribeId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getExecId() {
		return execId;
	}

	public void setExecId(String execId) {
		this.execId = execId;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public Long getStockInCount() {
		return stockInCount;
	}

	public void setStockInCount(Long stockInCount) {
		this.stockInCount = stockInCount;
	}

	public Date getStockInTimeStamp() {
		return stockInTimeStamp;
	}

	public void setStockInTimeStamp(Date stockInTimeStamp) {
		this.stockInTimeStamp = stockInTimeStamp;
	}

	public Long getInsertCount() {
		return insertCount;
	}

	public void setInsertCount(Long insertCount) {
		this.insertCount = insertCount;
	}

	public Long getUpdateCount() {
		return updateCount;
	}

	public void setUpdateCount(Long updateCount) {
		this.updateCount = updateCount;
	}

	public Long getFailCount() {
		return failCount;
	}

	public void setFailCount(Long failCount) {
		this.failCount = failCount;
	}

    @Override
    public String toString() {
        return "ETLTaskResultDto{" +
                "subscribeSeqNum='" + subscribeSeqNum + '\'' +
                ", subscribeId='" + subscribeId + '\'' +
                ", execId='" + execId + '\'' +
                ", runningId='" + runningId + '\'' +
                ", userId='" + userId + '\'' +
                ", result='" + result + '\'' +
                ", successFileNameList=" + successFileNameList +
                ", stockInCount=" + stockInCount +
                ", insertCount=" + insertCount +
                ", updateCount=" + updateCount +
                ", failCount=" + failCount +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", stockInTimeStamp=" + stockInTimeStamp +
                ", errCode=" + errCode +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}
