package com.ys.idatrix.cloudetl.subscribe.api.service;

import com.ys.idatrix.cloudetl.common.api.dto.PaginationDto;
import com.ys.idatrix.cloudetl.subscribe.api.dto.CreateJobDto;
import com.ys.idatrix.cloudetl.subscribe.api.dto.QueryJobDto;
import com.ys.idatrix.cloudetl.subscribe.api.dto.SubscribeResultDto;

public interface SubscribeService {

	/**
	 * 创建(交换)订阅任务
	 * @param createJobDto <br>
	 * 				name 不能为空，任务名	<br>
	 * 				description 可为空 ，任务描述<br>
	 * 				dataInput ：不能为空 ，输入步骤数据，现在可选  TableInputDto,FileInputDto<br>
	 * 				dataOutput ：不能为空 ，输出步骤数据，现在可选  TableOutputDto,InsertUpdateDto<br>
	 * 				filterCondition ：可为空 ，过滤步骤数据，现在可选  FilterRowsDto<br>
	 * 				timer ：不能为空 ，定时步骤数据，现在可选  TimerDto<br>
	 * 				immediatelyRun ：可为空，默认 true，创建成功则立即启动运行<br>
	 * 				params:可为空，当immediatelyRun为true,数据中有${XXX}的变量时，用来设置启动变量参数
	 * @return
	 */
	public SubscribeResultDto createSubscribeJob( CreateJobDto createJobDto ) throws Exception  ;
	/**
	 * 获取订阅任务列表 或 订阅任务的执行列表
	 * @param queryJobDto <br>
	 * 			subscribeId 可以为空，不为空 则获取该订阅任务的执行列表，为空则获取订阅任务列表 <br>
	 *  		subscribeId为空(查询订阅任务列表)：incloudDetail 可为空，默认false，为true则返回每个订阅的汇总信息，需要整体计算 <br>
	 *  		subscribeId不为空(查询该订阅任务的执行列表)：execId 可以为空，为空获取所有执行列表，不为空获取当次执行的执行列表(每次start 和 stop 任务会产生一个执行id) <br>
	 * 			<br>
	 * 			分页属性：<br>
	 * 					page=-1 ：页号，从1开始 ,小于1表示不分页，默认 -1 <br>
	 * 					pageSize=10: 每页条数，page > 0 时有效，默认10条 <br>
	 * 					search ：搜索条件，对名字/id进行过滤 ，page > 0 时有效，默认为空不过滤<br>
	 * 
	 * @return
	 */
	public PaginationDto<SubscribeResultDto> getSubscribeJobList( QueryJobDto queryJobDto ) throws Exception  ;
	/**
	 * 删除交换任务
	 * @param queryJobDto <br>
	 * 			subscribeId 和 subscribeIds 二选一不为空 <br>
	 *  		subscribeId 删除单个时不为空，subscribeIds批量删除时不为空 <br>
	 * @return
	 */
	public SubscribeResultDto deleteSubscribeJob( QueryJobDto queryJobDto )  throws Exception ;
	/**
	 * 启动存在的交换任务
	 * @param queryJobDto <br>
	 * 			subscribeId 不能为空 <br>
	 * 			params 可为空，创建任务时使用了变量时，使用设置真实值
	 * @return
	 */
	public SubscribeResultDto startSubscribeJob( QueryJobDto queryJobDto ) throws Exception  ;
	/**
	 * 停止交换任务
	 * @param queryJobDto <br>
	 * 			subscribeId 不能为空 <br>
	 * @return
	 */
	public SubscribeResultDto stopSubscribeJob( QueryJobDto queryJobDto )  throws Exception ;
	/**
	 * 查询任务状态,日志等信息(状态等会主动推送)
	 * @param queryJobDto <br>
	 * 			subscribeId 不能为空 <br>
	 * 			execId 可以为空，不为空速度会更快 <br>
	 * 			runId  可以为空，不为空就是获取指定的运行信息，为空获取最新一次的运行信息 <br>
	 * 			incloudLog 默认false，为true 会返还相应运行的日志，需要查询日志文件 <br>
	 * 			
	 * @return
	 */
	public SubscribeResultDto getSubscribeJobInfo( QueryJobDto queryJobDto)  throws Exception ;
	
	
}
