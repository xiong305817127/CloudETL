package com.ys.idatrix.quality.service.statistics;

import java.util.List;

import org.pentaho.di.repository.RepositoryObjectType;
import org.springframework.stereotype.Service;

import com.ys.idatrix.quality.dto.common.PaginationDto;
import com.ys.idatrix.quality.dto.history.ExecHistoryRecordDto;
import com.ys.idatrix.quality.analysis.dao.NodeRecordDao;
import com.ys.idatrix.quality.analysis.dao.NodeResultDao;
import com.ys.idatrix.quality.dto.statistics.NodeTypeNumberDto;
import com.ys.idatrix.quality.dto.statistics.NodeTypeTaskTimesDto;
import com.ys.idatrix.quality.dto.statistics.ReferenceNumberDto;
import com.ys.idatrix.quality.dto.statistics.ExecTaskNumberTotal;
import com.ys.idatrix.quality.dto.statistics.ExecTaskTimesTotal;
import com.ys.idatrix.quality.dto.statistics.TaskMonthTotal;
import com.ys.idatrix.quality.ext.CloudSession;
import com.ys.idatrix.quality.ext.executor.logger.CloudExecHistory;
import com.ys.idatrix.quality.repository.CloudRepository;

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

	//###########################基于节点###############################
	
	@Override
	public NodeTypeNumberDto countNodeLinesTotal( String flag ) throws Exception {
		return NodeRecordDao.countALLRecordLines(CloudSession.getLoginRenterId(),flag);
	}

	@Override
	public List<NodeTypeNumberDto> countNumberByNodeType( String flag) throws Exception {
		return NodeRecordDao.countNumberByNodeType(CloudSession.getLoginRenterId(), flag);
	}

	@Override
	public List<NodeTypeTaskTimesDto> countTimesByTaskName( String nodeType, String flag) throws Exception {
		return NodeRecordDao.countTimesByTaskName(CloudSession.getLoginRenterId(), nodeType, flag);
	}

	@Override
	public List<ReferenceNumberDto> countNumberByReference(  String nodeType, String flag) throws Exception {
		return NodeResultDao.countNumberByReference(CloudSession.getLoginRenterId(), nodeType, flag);
	}

}
