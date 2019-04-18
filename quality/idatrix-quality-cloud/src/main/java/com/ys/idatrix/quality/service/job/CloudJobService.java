/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.quality.service.job;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.pentaho.di.core.exception.KettleException;

import com.ys.idatrix.quality.dto.JobDto;
import com.ys.idatrix.quality.dto.common.CheckResultDto;
import com.ys.idatrix.quality.dto.common.PaginationDto;
import com.ys.idatrix.quality.dto.common.ReturnCodeDto;
import com.ys.idatrix.quality.dto.history.ExecHistoryDto;
import com.ys.idatrix.quality.dto.history.ExecLogsDto;
import com.ys.idatrix.quality.dto.job.JobBatchExecRequestDto;
import com.ys.idatrix.quality.dto.job.JobBatchStopDto;
import com.ys.idatrix.quality.dto.job.JobExecEntryMeasureDto;
import com.ys.idatrix.quality.dto.job.JobExecEntryStatusDto;
import com.ys.idatrix.quality.dto.job.JobExecIdDto;
import com.ys.idatrix.quality.dto.job.JobExecLogDto;
import com.ys.idatrix.quality.dto.job.JobExecRequestDto;
import com.ys.idatrix.quality.dto.job.JobExecStatusDto;
import com.ys.idatrix.quality.dto.job.JobInfoDto;
import com.ys.idatrix.quality.dto.job.JobOverviewDto;

/**
 * Service interface for job.
 * @author JW
 * @since 2017年5月24日
 *
 */
public interface CloudJobService {
	
	/**
	 * 获取 job用户列表
	 * @return
	 * @throws Exception
	 */
	public List<String> getCloudJobUserList( ) throws Exception ;
	
	/**
	 *  服务方法 - 查询调度组名列表
	 * @return
	 * @throws Exception
	 */
	List<String> getCloudJobGroupList(String owner) throws Exception;
	
	/**
	 *  服务方法 - 查询调度组名
	 * @return
	 * @throws Exception
	 */
	String getCloudJobGroup(String owner ,String jobName,String... priorityGroups) throws Exception;


	/**
	 *  服务方法 - 根据组名查询调度信息列表(分页,page为-1,则为全部数据)
	 * @param group
	 * @return
	 * @throws Exception
	 */
	Map<String,PaginationDto<JobDto>> getCloudJobList(String owner ,String group ,boolean isMap , int page,int pageSize,String search) throws Exception;
	
	/**
	 *  服务方法 - 根据组名查询调度名称列表
	 * @param group
	 * @return
	 * @throws Exception
	 */
	Map<String,List<String>> getCloudJobNameList(String owner ,String group) throws Exception;

	/**
	 * 服务方法 - 根据名字查找指定的Job概略信息
	 * @param name
	 * @return
	 * @throws Exception 
	 */
	JobOverviewDto loadCloudJob(String owner ,String name,String group) throws Exception;

	/**
	 * 服务方法 - 根据名字查找指定的Job配置信息
	 * @param name
	 * @return
	 * @throws Exception 
	 */
	JobInfoDto editJobAttributes(String owner ,String name,String group) throws Exception;

	/**
	 * 服务方法 - 保存Job配置信息
	 * @param jobInfo
	 * @return
	 * @throws Exception 
	 */
	ReturnCodeDto saveJobAttributes(JobInfoDto jobInfo) throws Exception;

	/**
	 * 服务方法 - 新建Job
	 * @param jobInfo
	 * @return
	 * @throws Exception 
	 */
	ReturnCodeDto newJob(JobInfoDto jobInfo) throws Exception;

	/**
	 * 服务方法 - 检查Job名是否存在
	 * @param name
	 * @return
	 * @throws KettleException 
	 */
	CheckResultDto checkJobName(String owner ,String name) throws Exception;

	/**
	 * 服务方法 - 删除指定Job
	 * @param name
	 * @return
	 * @throws Exception 
	 */
	ReturnCodeDto deleteJob(String owner ,String name,String group) throws Exception;

	/**
	 * 服务方法 - 执行Job
	 * @param execRequest
	 * @return
	 * @throws Exception 
	 */
	ReturnCodeDto execJob(JobExecRequestDto execRequest) throws Exception;
	
	/**
	 * 当调度在运行状态是 重启调度任务
	 * @param jobName
	 * @param group
	 * @return
	 * @throws Exception
	 */
	ReturnCodeDto rebootJob(String owner ,String jobName, String group) throws Exception;
	
	/**
	 * 服务方法 - 批量执行Job
	 * @param execRequest
	 * @return
	 * @throws Exception 
	 */
	ReturnCodeDto execBatchJob(JobBatchExecRequestDto execRequest) throws Exception;
	
	/**
	 * 服务方法 - 执行Job
	 * @param execRequest
	 * @return
	 * @throws Exception 
	 */
	ReturnCodeDto execJob(JobExecRequestDto execRequest,String execUser) throws Exception;
	
	/**
	 * 服务方法 - 终止Job执行
	 * @param executionId
	 * @return
	 * @throws IOException 
	 * @throws KettleException 
	 * @throws Exception 
	 */
	ReturnCodeDto execStop(String executionId) throws  Exception;

	/**
	 * 服务方法 - 批量停止Job
	 * @param executionIds
	 * @return
	 * @throws Exception 
	 */
	ReturnCodeDto execBatchStop(JobBatchStopDto stopNames) throws Exception;
	
	/**
	 * 服务方法 - 查询Job执行中各节点的度量数据
	 * @param executionId
	 * @return
	 * @throws Exception 
	 */
	List<JobExecEntryMeasureDto> getEntryMeasure(String executionId) throws Exception;

	/**
	 * 服务方法 - 查询Job执行中各节点的状态
	 * @param executionId
	 * @return
	 * @throws Exception 
	 */
	List<JobExecEntryStatusDto> getEntryStatus(String executionId) throws Exception;

	/**
	 * 服务方法 - 查询Job执行日志
	 * @param executionId
	 * @return
	 * @throws Exception 
	 */
	JobExecLogDto getExecLog(String executionId) throws Exception;

	/**
	 * 服务方法 - 查询Job执行ID
	 * @param name
	 * @return
	 * @throws Exception 
	 */
	JobExecIdDto getExecId(String owner ,String name) throws Exception;

	/**
	 * 服务方法 - 查询Job执行状态
	 * @param executionId
	 * @return
	 * @throws Exception 
	 */
	JobExecStatusDto getExecStatus(String executionId) throws Exception;

	/**
	 * 服务方法 - 查询Job当前状态
	 * @param name
	 * @return
	 * @throws Exception 
	 */
	JobExecStatusDto getJobStatus(String owner ,String name ) throws Exception;
	
	/**
	 * 服务方法 - 查询Job执行历史记录
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public ExecHistoryDto getJobHistory(String owner ,String name) throws Exception;
	
	/**
	 * 服务方法 - 查询Job某次执行历史记录的分次记录
	 * @param name
	 * @param execId
	 * @return
	 * @throws Exception
	 */
	public ExecHistoryDto getJobHistorSegmenting(String owner ,String group,String name,String execId) throws Exception ;
	/**
	 * 服务方法 - 查询Job执行历史日志
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public ExecLogsDto getJobLogs(String owner ,String name,String id ,String startDate ,String endDate) throws Exception;

	/**
	 * 如果使用数据库仓库,将上传的kjb文件加入数据库仓库
	 * @param filePath
	 * @return
	 * @throws Exception
	 */
	ReturnCodeDto addDbJob(String filePath) throws Exception;

}