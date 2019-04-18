/**
 * 云化数据集成系统 iDatrix CloudETL
 */
package com.ys.idatrix.quality.service.server;

import java.util.List;
import java.util.Map;

import org.pentaho.di.cluster.SlaveServer;

import com.ys.idatrix.quality.dto.common.CheckResultDto;
import com.ys.idatrix.quality.dto.common.PaginationDto;
import com.ys.idatrix.quality.dto.common.ReturnCodeDto;
import com.ys.idatrix.quality.dto.common.TestResultDto;
import com.ys.idatrix.quality.dto.server.ServerBriefDto;
import com.ys.idatrix.quality.dto.server.ServerDetailsDto;

/**
 * Slave server service interfaces.
 * @author JW
 * @since 2017年5月24日
 *
 */
public interface CloudServerService {
	
	/**
	 * 服务方法 - 根据名字查找指定的服务器
	 * @param name
	 * @return
	 * @throws Exception 
	 */
	SlaveServer findSlaveServer(String owner , String name) throws Exception;
	
	/**
	 * 服务方法 - 根据名字查询指定的服务器状态信息
	 * @param name
	 * @return
	 * @throws Exception 
	 * @throws Exception 
	 */
	String getSlaveServerStatus(String owner ,String name) throws Exception, Exception;
	
	/**
	 * 服务方法 - 测试指定的服务器状态
	 * @param slave
	 * @return
	 * @throws Exception 
	 */
	String testSlaveServerStatus(SlaveServer slave) throws Exception;
	
	/**
	 * 服务方法 - 根据名字测试指定的服务器状态
	 * @param name
	 * @return
	 * @throws Exception 
	 */
	TestResultDto doServerTest(String owner ,String name) throws Exception;

	/**
	 * 服务方法 - 查询服务器列表
	 * @return
	 * @throws Exception
	 */
	Map<String ,List<ServerBriefDto>> getCloudServerList(String owner ) throws  Exception;
	
	
	
	Map<String,List<SlaveServer>> getSlaveServerList(String owner ) throws  Exception; 
	
	/**
	 * 服务方法 - 分页查询服务器列表
	 * @return
	 * @throws Exception 
	 */
	Map<String ,PaginationDto<ServerBriefDto>>  getCloudServerList(String owner ,boolean isMap , int page,int pageSize,String search) throws Exception;
	
	/**
	 * 服务方法 - 检查服务器名是否存在
	 * @param name
	 * @return
	 * @throws Exception 
	 */
	CheckResultDto checkServerName(String owner ,String name) throws Exception;
	
	/**
	 * 服务方法 - 编辑服务器配置信息
	 * @param name
	 * @return
	 * @throws Exception 
	 */
	ServerDetailsDto editServer(String owner ,String name) throws  Exception;
	
	/**
	 * 服务方法 - 保存服务器配置信息
	 * @param details
	 * @return
	 * @throws Exception 
	 */
	ReturnCodeDto saveServer(ServerDetailsDto details) throws Exception;
	
	/**
	 * 服务方法 - 删除服务器配置
	 * @param name
	 * @return
	 * @throws Exception 
	 */
	ReturnCodeDto deleteServer(String owner ,String name) throws Exception;
	
	/**
	 * 服务方法 - 计算全部服务器数量
	 * @return
	 */
	public double serverTotalCounter();
	
	/**
	 * 服务方法 - 计算故障服务器数量
	 * @return
	 */
	public double serverErrorCounter();

}