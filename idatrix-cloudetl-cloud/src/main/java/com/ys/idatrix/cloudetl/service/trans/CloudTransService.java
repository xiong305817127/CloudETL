/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.service.trans;

import java.util.List;
import java.util.Map;

import com.ys.idatrix.cloudetl.dto.TransDto;
import com.ys.idatrix.cloudetl.dto.common.CheckResultDto;
import com.ys.idatrix.cloudetl.dto.common.PaginationDto;
import com.ys.idatrix.cloudetl.dto.common.ReturnCodeDto;
import com.ys.idatrix.cloudetl.dto.history.ExecHistoryDto;
import com.ys.idatrix.cloudetl.dto.history.ExecLogsDto;
import com.ys.idatrix.cloudetl.dto.trans.TransBatchExecRequestDto;
import com.ys.idatrix.cloudetl.dto.trans.TransBatchStopDto;
import com.ys.idatrix.cloudetl.dto.trans.TransExecIdDto;
import com.ys.idatrix.cloudetl.dto.trans.TransExecLogDto;
import com.ys.idatrix.cloudetl.dto.trans.TransExecRequestNewDto;
import com.ys.idatrix.cloudetl.dto.trans.TransExecStatusDto;
import com.ys.idatrix.cloudetl.dto.trans.TransExecStepMeasureDto;
import com.ys.idatrix.cloudetl.dto.trans.TransExecStepStatusDto;
import com.ys.idatrix.cloudetl.dto.trans.TransInfoDto;
import com.ys.idatrix.cloudetl.dto.trans.TransOverviewDto;

/**
 * Transformation operation service interfaces.
 * @author JW
 * @since 2017年5月24日
 *
 */
public interface CloudTransService {
	
	/**
	 * 获取 调度用户列表
	 * @return
	 * @throws Exception
	 */
	public List<String> getCloudTransUserList( ) throws Exception ;
	
	/**
	 * 服务方法 - 查询转换组名列表
	 * @return
	 */
	public List<String> getCloudTransGroupList(String owner  ) throws Exception ;
	
	/**
	 * 服务方法 - 查询转换组名
	 * @return
	 */
	public String getCloudTransGroup(String owner , String transName,String... priorityGroups) throws Exception ;
	
	/**
	 * 服务方法 - 分页查询转换列表
	 * @return
	 */
	public Map<String,PaginationDto<TransDto>> getCloudTransList(String owner , String group ,boolean isMap,int page,int pageSize,String search) throws Exception;
	
	/**
	 * 服务方法 - 查询转换名列表
	 * @return
	 */
	public Map<String,List<String>> getCloudTransNameList(String owner , String group ) throws Exception ;
	/**
	 * 服务方法 - 根据名字加载指定的转换
	 * @param transName
	 * @return
	 */
	public TransOverviewDto loadCloudTrans(String owner , String transName,String group) throws Exception;
	
	/**
	 * 服务方法 - 根据名字编辑指定的转换的属性
	 * @param transName
	 * @return
	 */
	public TransInfoDto editTransAttributes(String owner , String transName,String group) throws Exception;
	
	/**
	 * 服务方法 - 保存指定转换的属性
	 * @param transInfo
	 * @return
	 * @throws Exception
	 */
	public ReturnCodeDto saveTransAttributes(TransInfoDto transInfo) throws Exception;
	
	/**
	 * 服务方法 - 新建转换
	 * @param transInfo
	 * @return
	 * @throws Exception
	 */
	public ReturnCodeDto newTrans(TransInfoDto transInfo) throws  Exception;
	
	/**
	 * 服务方法 - 检查转换名是否存在
	 * @param transName
	 * @return
	 */
	public CheckResultDto checkTransName(String owner ,String transName) throws Exception;
	
	/**
	 * 服务方法 - 根据名字删除指定的转换
	 * @param transName
	 * @return
	 */
	public ReturnCodeDto deleteTrans(String owner ,String transName,String group) throws Exception;
	
	/**
	 * 服务方法 - 执行转换（支持执行引擎）
	 * @param execRequest
	 * @return
	 */
	public ReturnCodeDto execTransNew(TransExecRequestNewDto execRequest)throws Exception;
	
	/**
	 * 服务方法 - 批量执行转换（支持执行引擎）
	 * @param execRequest
	 * @return
	 */
	public ReturnCodeDto execBatchTrans(TransBatchExecRequestDto execRequest)throws Exception;
	
	/**
	 * 服务方法 - 执行转换（支持执行引擎）
	 * @param execRequest
	 * @return
	 */
	public ReturnCodeDto execTransNew(TransExecRequestNewDto execRequest,String user) throws Exception ;
	
	/**
	 * 重启已经在运行中的转换
	 * @param transName
	 * @param group
	 * @return
	 * @throws Exception
	 */
	public ReturnCodeDto rebootTrans(String owner ,String transName, String group) throws Exception;
	/**
	 * 服务方法 - 暂停转换执行
	 * @param executionId
	 * @return
	 * @throws Exception 
	 */
	public ReturnCodeDto execPause(String executionId) throws Exception;
	
	/**
	 * 服务方法 - 终止转换执行
	 * @param executionId
	 * @return
	 */
	public ReturnCodeDto execStop(String executionId) throws Exception;
	
	/**
	 * 服务方法 - 批量停止转换
	 * @param execRequest
	 * @return
	 */
	public ReturnCodeDto execBatchStop(TransBatchStopDto transNames)throws Exception;
	
	/**
	 * 服务方法 - 恢复暂停状态的转换的执行
	 * @param executionId
	 * @return
	 */
	public ReturnCodeDto execResume(String executionId) throws Exception;
	
	/**
	 * 预览暂停后,触发获取更多数据
	 * @param executionId
	 * @return
	 * @throws Exception
	 */
	public ReturnCodeDto execMorePreview(String executionId) throws Exception;
	
	/**
	 * 服务方法 - 查询转换执行的步骤度量数据
	 * @param executionId
	 * @return
	 * @throws Exception
	 */
	public List<TransExecStepMeasureDto> getStepMeasure(String executionId) throws Exception;
	
	/**
	 * 服务方法 - 查询转换执行的步骤状态信息
	 * @param executionId
	 * @return
	 * @throws Exception
	 */
	public List<TransExecStepStatusDto> getStepStatus(String executionId) throws Exception;
	
	/**
	 * 服务方法 - 查询转换执行的日志信息
	 * @param executionId
	 * @return
	 * @throws Exception
	 */
	public TransExecLogDto getExecLog(String executionId) throws Exception;
	
	/**
	 * 服务方法 - 根据名字获取指定的转换的当前执行ID
	 * @param transName
	 * @return
	 * @throws Exception 
	 */
	public TransExecIdDto getExecId(String owner ,String transName) throws Exception;
	
	/**
	 * 服务方法 - 查询转换指定执行ID的状态
	 * @param executionId
	 * @return
	 * @throws Exception 
	 */
	public TransExecStatusDto getExecStatus(String executionId) throws Exception;
	
	/**
	 * 服务方法 - 根据名字获取指定的转换的状态
	 * @param transName
	 * @return
	 */
	public TransExecStatusDto getTransStatus(String owner ,String transName) throws Exception;
	
	/**
	 * 服务方法 - 根据名字获取指定的转换的历史记录
	 * @param transName
	 * @return
	 */
	public ExecHistoryDto getTransHistory(String owner ,String transName) throws Exception;
	
	/**
	 * 服务方法 - 根据名字获取指定的转换的历史日志
	 * @param transName
	 * @return
	 */
	public ExecLogsDto getTransLogs(String owner ,String name,String id ,String startDate ,String endDate) throws Exception;


	/**
	 * 如果使用数据库仓库,将上传的ktr文件加入数据库仓库
	 * @param filePath
	 * @return
	 * @throws Exception
	 */
	ReturnCodeDto addDBTrans(String filePath) throws Exception;

	/**
	 * 获取启动预览/debug的数据
	 * @param executionId
	 * @return
	 * @throws Exception
	 */
	Map<String, List<String[]>> getDebugPreviewData(String executionId) throws Exception;

}
