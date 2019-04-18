package com.ys.idatrix.cloudetl.service.statistics;

import java.util.List;

import org.pentaho.di.repository.RepositoryObjectType;
import org.springframework.stereotype.Service;

import com.ys.idatrix.cloudetl.dto.common.PaginationDto;
import com.ys.idatrix.cloudetl.dto.history.ExecHistoryRecordDto;
import com.ys.idatrix.cloudetl.dto.statistics.ExecTaskNumberTotal;
import com.ys.idatrix.cloudetl.dto.statistics.ExecTaskTimesTotal;
import com.ys.idatrix.cloudetl.dto.statistics.TaskMonthTotal;
import com.ys.idatrix.cloudetl.ext.CloudSession;
import com.ys.idatrix.cloudetl.ext.executor.logger.CloudExecHistory;
import com.ys.idatrix.cloudetl.repository.CloudRepository;

/**
 * 数据统计服务
 *
 * @author XH
 * @since 2019年1月10日
 *
 */
@Service
public class StatisticsServiceImpl implements StatisticsService  {

	@Override
	public PaginationDto<ExecHistoryRecordDto> getRenterTaskList( RepositoryObjectType type , String flag , Integer pageNo, Integer pageSize) throws Exception{
		return  CloudExecHistory.getRenterExecRecords(CloudSession.getLoginRenterId(), type, flag , pageNo ,pageSize);
	}
	
	public Long getTaskTotal(RepositoryObjectType type, String flag) throws Exception {
		return CloudRepository.getTaskTotal(type,flag);
	}

	@Override
	public List<TaskMonthTotal> getTaskTotalByMonth(RepositoryObjectType type, String flag) throws Exception {
		return CloudRepository.getTaskTotalByMonth(type , flag);
	}

	@Override
	public ExecTaskTimesTotal getTaskExecTimes(RepositoryObjectType type, String flag) throws Exception {
		return CloudExecHistory.countALLExecRecordTimes(CloudSession.getLoginRenterId(), type, flag);
	}

	@Override
	public List<ExecTaskTimesTotal> getTaskExecTimesByMonth(RepositoryObjectType type, String flag) throws Exception {
		return CloudExecHistory.countALLExecRecordTimesByMonth(CloudSession.getLoginRenterId(), type, flag);
	}

	@Override
	public long getTaskExecLines(RepositoryObjectType type, String flag) throws Exception {
		return CloudExecHistory.countALLExecRecordLines(CloudSession.getLoginRenterId(), type, flag);
	}

	@Override
	public List<ExecTaskNumberTotal> getTaskExecLinesByMonth(RepositoryObjectType type, String flag) throws Exception {
		return CloudExecHistory.countALLExecRecordLinesByMonth(CloudSession.getLoginRenterId(), type, flag);
	}

	@Override
	public PaginationDto<ExecTaskNumberTotal> getTaskExecLinesByTask(RepositoryObjectType type, String flag, Integer pageNo, Integer pageSize) throws Exception {
		return CloudExecHistory.countALLExecRecordLinesByTask(CloudSession.getLoginRenterId(), type, flag, pageNo,pageSize );
	}

}
