package com.ys.idatrix.quality.recovery.trans;

import java.io.ByteArrayInputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import org.pentaho.di.core.Const;
import org.pentaho.di.core.util.IdatrixPropertyUtil;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.trans.TransExecutionConfiguration;
import org.pentaho.di.trans.TransMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ys.idatrix.quality.ext.executor.CloudExecution;
import com.ys.idatrix.quality.ext.executor.CloudExecution.ExecutionInfo;
import com.ys.idatrix.quality.ext.executor.CloudTransExecutor;
import com.ys.idatrix.quality.ext.utils.RedisUtil;
import com.ys.idatrix.quality.recovery.trans.dto.TransInfoDto;

public class RemoteResumeListener implements Runnable {
	
	private Logger logger = LoggerFactory.getLogger(RemoteResumeListener.class);


	private static RemoteResumeListener remoteResumeListener;

	private String serviceId;
	private boolean isDone = false;

	private Long remoteScan;
	private Long exceptTmie;

	private RemoteResumeListener() {
		super();

		try {
			InetAddress addr = InetAddress.getLocalHost();
			String ip = addr.getHostAddress();
			if (!"127.0.0.1".equals(ip)) {
				serviceId =  Utils.getThreadNameesSuffixByUser(ip, null, true) ;
			}
		} catch (UnknownHostException e) {
		}
		if(Utils.isEmpty(serviceId)) {
			serviceId =  UUID.randomUUID().toString();
		}

		remoteScan = Long.valueOf(IdatrixPropertyUtil.getProperty("idatrix.breakpoint.remote.scan.time", "60"));
		exceptTmie = Long.valueOf(IdatrixPropertyUtil.getProperty("idatrix.breakpoint.except.time", "300"));
		
		logger.info("远程恢复服务serviceId: "+serviceId);
	}

	public static RemoteResumeListener getInstance() {
		if (remoteResumeListener == null) {
			remoteResumeListener = new RemoteResumeListener();
		}
		return remoteResumeListener;
	}

	@Override
	public void run() {

		boolean cacheEnable = !ResumeTransParser.isResumeEnable();
		while (!isDone&&cacheEnable) {
			
				TransInfoDto transInfo;
				while ((transInfo = findAndStopTrans()) != null) {
					try {
						logger.info("查询到 需要执行的转换:serviceId: "+serviceId+" ,user: "+transInfo.getUser()+"  ,Name: "+transInfo.getTransName());
						// 获取到ktr文件
						String ktrXml = (String) RedisUtil.hget(ResumeTransParser.getTransKey(transInfo),ResumeTransParser.TransKtrKey);
						ByteArrayInputStream tInputStringStream = new ByteArrayInputStream(ktrXml.getBytes(Const.XML_ENCODING));
						TransMeta transMeta = new TransMeta(tInputStringStream, null, true, null, null);
						tInputStringStream.close();
						transMeta.setName(transInfo.getTransName());
	
						TransExecutionConfiguration executionConfiguration = new TransExecutionConfiguration();
						executionConfiguration.setExecutingLocally(true);
						executionConfiguration.setExecutingRemotely(false);
						executionConfiguration.setExecutingClustered(false);
						
						CloudTransExecutor transExecutor = CloudTransExecutor.initExecutor(transMeta,executionConfiguration, null, transInfo.getUser(),transInfo.getOwner());
						Thread tr = new Thread((CloudTransExecutor) transExecutor,"TransExecutor_" + transExecutor.getExecutionId()	+  Utils.getThreadNameesSuffixByUser(transInfo.getUser(),transInfo.getOwner(), true));
						tr.start();
					
					} catch (Exception e) {
						logger.error("恢复转换异常:",e);
					}finally {
						//启动完毕,删除队列
						RedisUtil.del(ResumeTransParser.getQueueKey(transInfo));
					}
				}

				try {
					Thread.sleep(remoteScan * 1000);
				} catch (InterruptedException e) {
				}
		}

	}

	private TransInfoDto findAndStopTrans() {
		Set<Object> allTrans = RedisUtil.sGet(ResumeTransParser.registerTrans);
		if (allTrans != null) {
			Iterator<Object> allTransIterator = allTrans.iterator();
			while (allTransIterator.hasNext()) {
				TransInfoDto transInfo = (TransInfoDto) allTransIterator.next();
				if (transInfo != null) {
					Boolean isSupportRemote = (Boolean) RedisUtil.hget(ResumeTransParser.getTransKey(transInfo),
							ResumeTransParser.TransRemoteKey);
					if (isSupportRemote) {
						// 支持远程恢复
						long lastUpdateTime = (long) RedisUtil.hget(ResumeTransParser.getTransKey(transInfo),ResumeTransParser.TransUpdateKey);
						long maxTime =  (lastUpdateTime + (exceptTmie * 1000)) ;
						if ( maxTime < System.currentTimeMillis() && RedisUtil.lGetListSize(ResumeTransParser.getQueueKey(transInfo)) <=0) {
							// 超过异常间隔时间,断定为异常停止,查找队列进行抢占
							RedisUtil.lpush(ResumeTransParser.getQueueKey(transInfo), serviceId);
							// 获取队列的最后一个数据
							String sid = (String) RedisUtil.lGetIndex(ResumeTransParser.getQueueKey(transInfo), -1);
							if (serviceId.equals(sid)) {
								// 抢占成功,更新trans时间,使别的服务获取不到
								RedisUtil.hset(ResumeTransParser.getTransKey(transInfo), ResumeTransParser.RemoteResumeServerKey,	serviceId);
								RedisUtil.hset(ResumeTransParser.getTransKey(transInfo),ResumeTransParser.TransUpdateKey, System.currentTimeMillis());
								return transInfo;
							}
						}else {
							//未超过时间,正在运行
							String sid = (String) RedisUtil.hget(ResumeTransParser.getTransKey(transInfo), ResumeTransParser.RemoteResumeServerKey);
							if( !Utils.isEmpty(sid) && serviceId.equals(sid) ) {
								//是在远程(本机)执行
								String isStop = (String) RedisUtil.hget(ResumeTransParser.getTransKey(transInfo),ResumeTransParser.TransStopKey);
								if("stop".equals(isStop) ) {
									ExecutionInfo executionInfo = CloudExecution.getInstance().getExecutionInfo(transInfo.getOwner() ,transInfo.getTransName(), false);
									if(executionInfo != null && executionInfo.getTransExecutor() != null) {
										try {
											executionInfo.getTransExecutor().execStop(false);
										} catch (Exception e) {
											logger.error("停止转换执行异常.",e);
										}
									}
									RedisUtil.hset(ResumeTransParser.getTransKey(transInfo),ResumeTransParser.TransStopKey, "stopped");
									RedisUtil.hdel(ResumeTransParser.getTransKey(transInfo), ResumeTransParser.RemoteResumeServerKey);
								}
								
							}
						}
					}
				}
			}
		}

		return null;
	}
	
	public void setDone() {
		isDone = true;
	}
	
	public String getServiceId() {
		return serviceId;
	}

}
