package com.ys.idatrix.quality.service.statistics;

import java.util.List;

import org.pentaho.di.repository.RepositoryObjectType;

import com.ys.idatrix.quality.dto.common.PaginationDto;
import com.ys.idatrix.quality.dto.history.ExecHistoryRecordDto;
import com.ys.idatrix.quality.dto.statistics.NodeTypeNumberDto;
import com.ys.idatrix.quality.dto.statistics.NodeTypeTaskTimesDto;
import com.ys.idatrix.quality.dto.statistics.ReferenceNumberDto;
import com.ys.idatrix.quality.dto.statistics.ExecTaskNumberTotal;
import com.ys.idatrix.quality.dto.statistics.ExecTaskTimesTotal;
import com.ys.idatrix.quality.dto.statistics.TaskMonthTotal;

/**
 * 数据统计服务
 *
 * @author XH
 * @since 2019年1月10日
 *
 */
public interface StatisticsService {
	
	/**
	 *  获取 当前租户的任务列表
	 *  
	 * @param type  任务类型,调度/转换 ,可为空,空表示获取所有的
	 * @param flag year/month/day/yyyy-MM-dd , 为空:忽略日期 , 否则 当年/当月/当天/具体某天某月某年
	 * @return
	 * @throws Exception
	 */
	public PaginationDto<ExecHistoryRecordDto> getRenterTaskList( RepositoryObjectType type , String flag , Integer pageNo, Integer pageSize) throws Exception;
	

	/**
	 *  获取 当前租户任务总数
	 *  
	 * @param type  任务类型,调度/转换 ,可为空,空表示获取所有的
	 * @param flag year/month/day/yyyy-MM-dd , 为空:忽略日期 , 否则 当年/当月/当天/具体某天某月某年
	 * @return
	 * @throws Exception
	 */
	public Long getTaskTotal( RepositoryObjectType type , String flag ) throws Exception;
	
	/**
	 * 按月份统计  当前租户下 每个月有多少 任务
	 * @param type ,任务类型,调度/转换 , 可为空,为空时返回两者总量
	 * @param flag year/month/day/yyyy-MM-dd , 为空:忽略日期 , 否则 当年/当月/当天/具体某天某月某年
	 * @return
	 * @throws Exception
	 */
	public  List<TaskMonthTotal> getTaskTotalByMonth(  RepositoryObjectType type, String flag ) throws Exception;
	
	/**
	 * 统计 所有执行的任务(调度)的 状态总次数 <br>
	 * 一个任务重复统计所有的执行
	 * 
	 * @param type
	 * @param flag year/month/day/yyyy-MM-dd , 为空:忽略日期 , 否则 当年/当月/当天/具体某天某月某年
	 * @return
	 * @throws Exception
	 */
	public ExecTaskTimesTotal getTaskExecTimes(  RepositoryObjectType type ,String flag ) throws Exception;
	
	
	/**
	 * 按月份 统计 过去一年的每月的状态执行次数
	 * @param renterId
	 * @param type
	 * @param flag year/month/day/yyyy-MM-dd , 为空:忽略日期 , 否则 当年/当月/当天/具体某天某月某年
	 * @return
	 * @throws Exception
	 */
	public List<ExecTaskTimesTotal>  getTaskExecTimesByMonth(  RepositoryObjectType type , String flag) throws Exception;
	
	
	/**
	 * 统计 所有执行的任务(调度)的 任务处理数量  <br>
	 * 一个任务重复统计所有的执行
	 * @param type
	 * @param flag year/month/day/yyyy-MM-dd , 为空:忽略日期 , 否则 当年/当月/当天/具体某天某月某年
	 * @return
	 * @throws Exception
	 */
	public long getTaskExecLines( RepositoryObjectType type, String flag )  throws Exception;
	
	/**
	 * 按月份 统计 过去一年的每月的任务处理数量
	 * @param type
	 * @param flag year/month/day/yyyy-MM-dd , 为空:忽略日期 , 否则 当年/当月/当天/具体某天某月某年
	 * @return
	 * @throws Exception
	 */
	public List<ExecTaskNumberTotal> getTaskExecLinesByMonth( RepositoryObjectType type , String flag)  throws Exception;
	
	/**
	 * 按任务 统计 每个任务的任务处理数量
	 * @param type
	 * @param flag year/month/day/yyyy-MM-dd , 为空:忽略日期 , 否则 当年/当月/当天/具体某天某月某年
	 * @return
	 * @throws Exception
	 */
	public PaginationDto<ExecTaskNumberTotal> getTaskExecLinesByTask( RepositoryObjectType type , String flag, Integer pageNo, Integer pageSize )  throws Exception;

	
	/**
	 * 基于节点 , 获取 当前租户 所有任务 处理行数的总数 (数据量)
	 * @param type 任务类型,调度/转换 ,可为空,空表示获取所有的
	 * @param isSuccess true:成功总行数 , false: 失败总行数 , 为空: 成功失败两者之和
	 * @param flag year/month/day , 为空:忽略日期 , 否则 当年/当月/当天
	 * @return
	 * @throws Exception
	 */
	public NodeTypeNumberDto countNodeLinesTotal(  String flag ) throws Exception;
	
	
	/**
	 * 基于节点 , 按照 节点类型 分类 统计节点处理数量总数
	 * @param renterId 租户Id ,为空 统计整个系统数据
	 * @param flag year/month/day
	 * @return
	 * @throws Exception
	 */
	public List<NodeTypeNumberDto>  countNumberByNodeType( String flag) throws Exception ;
	
	
	/**
	 * 基于节点 , 按照任务名分类获取 某个节点的运行次数 (包括节点处理数据总数)
	 * @param renterId 租户Id ,为空 统计整个系统数据
	 * @param nodeType  节点类型
	 * @param flag  year/month/day , 为空:忽略日期 , 否则 当年/当月/当天
	 * @return
	 * @throws Exception
	 */
	public List<NodeTypeTaskTimesDto>  countTimesByTaskName( String nodeType , String flag) throws Exception ;
	
	
	/**
	 * 基于节点 , 按照参考值分类 , 统计 某个节点  对应的数据总数 和 任务总数  <br>
	 * @param renterId
	 * @param nodeType
	 * @param flag year/month/day
	 * @return
	 * @throws Exception
	 */
	public List<ReferenceNumberDto> countNumberByReference( String nodeType ,String flag )  throws Exception;
	
	
}
