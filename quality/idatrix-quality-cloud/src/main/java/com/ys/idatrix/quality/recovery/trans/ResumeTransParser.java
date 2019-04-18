package com.ys.idatrix.quality.recovery.trans;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.LogChannel;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.core.logging.LoggingObjectType;
import org.pentaho.di.core.logging.SimpleLoggingObject;
import org.pentaho.di.core.util.IdatrixPropertyUtil;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStep;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaDataCombi;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.pentaho.di.trans.step.BaseStep.CounterDataListener;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ys.idatrix.quality.dto.codec.StepParameterCodec;
import com.ys.idatrix.quality.ext.utils.RedisUtil;
import com.ys.idatrix.quality.recovery.trans.dto.StepLinesDto;
import com.ys.idatrix.quality.recovery.trans.dto.TransInfoDto;

public class ResumeTransParser extends SimpleLoggingObject{

	public  LogChannelInterface logger ;
	
	public static final String  ResumeTransRedisPrefix ="ResumeTrans:";
	
	public static final String BREAKPOINT_CONTINUE_ENABLE ="breakpointsContinueEnable";
	public static final String BREAKPOINTS_REMOTE ="breakpointsRemote";
	public static final String BREAKPOINTS_FORCELOCAL ="breakpointsforceLocal";
	
	public static final String registerTrans = ResumeTransRedisPrefix+"registerTrans";
	
	public static final String TransIDKey = "transId";
	public static final String TransKtrKey = "ktrFile";
	public static final String TransRemoteKey = "isSupportRemote";
	public static final String TransUpdateKey = "lastUpdateTime";
	public static final String TransStopKey = "stopKey";
	public static final String transQueueKey = "transQueue";
	public static final String RemoteResumeServerKey = "RemoteResumeServer";


	public static final String inputKey = "linesInput";
	public static final String outputKey = "linesOutput";
	public static final String readKey = "linesRead";
	public static final String writeKey = "linesWritten";
	public static final String rejectKey = "linesRejected";
	public static final String updateKey = "linesUpdated";
	public static final String errorKey = "linesErrors";

	private String user;
	private String transName;
	private TransMeta transMeta;
	private Trans trans;
	
	private Boolean isSupportResume;
	private CleanStepLines clean;
	private Thread cleanThread;
	
	public TransInfoDto transInfoDto;

	private Map<StepMeta, ResumeStepDataParser> stepDataParsers; // 存储所有步骤的ResumeStepDataParser转换
	/**
	 * 存储所有步骤的前后关系,在trans.prepareExecution之后会调用init()进行初始化
	 */
	private Map<StepMeta, List<StepMeta>> nextStepMap;

	/**
	 * @param user
	 * @param trans
	 * @param executionConfiguration
	 */
	public ResumeTransParser(String user,String transName , Trans trans, TransMeta transMeta) {
		super("ResumeTransParser", LoggingObjectType.TRANSMETA, trans);
		this.user = user;
		this.transName = transName;
		this.transMeta = transMeta;
		this.trans = trans;

		transInfoDto = new TransInfoDto(user, transName);
		
		stepDataParsers = Maps.newHashMap();
		nextStepMap = Maps.newHashMap();
		
		logger = new LogChannel( this,trans);
		
	}

	/**
	 * 对对象进行初始化,需要在trans.prepareExecution之后调用
	 * @throws KettleException 
	 */
	public void init( ) throws KettleException {
		if ( !isResumeEnable() || !isSupportResume() ) {
			// 缓存不可用
			logger.logDetailed("不支持缓存恢复功能.");
			return;
		}
		
		String serviceId = getRemoteRunningId(transInfoDto) ;
		if(serviceId != null) {
			//远程正在运行...
			if(isForceLocal()) {
				//终止远程,再继续本地
				logger.logBasic("远程("+serviceId+")正在运行,开始终止远程转换...");
				RedisUtil.hset(getTransKey(transInfoDto),TransStopKey, "stop");
				logger.logBasic("等待远程("+serviceId+")停止...");
				String isStop ="";
				//设置等待时间为扫描时间(多2秒)
				Long remoteScan = 2000+1000*Long.valueOf(IdatrixPropertyUtil.getProperty("idatrix.breakpoint.remote.scan.time", "60"));
				Long end = System.currentTimeMillis() + remoteScan;
				while(!"stopped".equals(isStop) && System.currentTimeMillis() < end) {
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
					}
					isStop = (String) RedisUtil.hget(getTransKey(transInfoDto),TransStopKey);
				}
				if(!"stopped".equals(isStop) ) {
					//停止异常
					logger.logBasic("转换["+transInfoDto.getTransName()+"]停止远程["+serviceId+"]运行失败");
					throw new KettleException("转换["+transInfoDto.getTransName()+"]停止远程["+serviceId+"]运行失败");
				}else {
					RedisUtil.hdel(getTransKey(transInfoDto),TransStopKey);
					RedisUtil.hdel(getTransKey(transInfoDto), RemoteResumeServerKey);
				}
				logger.logBasic("转换["+transInfoDto.getTransName()+"]远程("+serviceId+")运行停止成功.");
			}else {
				//继续远程执行,本地停止
				logger.logBasic("转换["+transInfoDto.getTransName()+"]正在远程["+serviceId+"]运行...");
				throw new KettleException("转换["+transInfoDto.getTransName()+"]正在远程["+serviceId+"]运行...");
			}
		}
		
		// 给nextStepMap赋值,可以直接获取trans中的线的map
		List<StepMeta> hopsteps = trans.getTransMeta().getTransHopSteps( false );//获取所有有线连接并且线可用的步骤
		for(StepMeta step : hopsteps) {
			List<StepMeta> ns = trans.getTransMeta().findNextSteps(step);
			if(ns != null && ns.size() >0 ) {
				nextStepMap.put(step, ns);
			}
		}
		
		clean = new CleanStepLines(trans, this);
		
		//注册trans
		RedisUtil.sSet(registerTrans, transInfoDto);
		//保存trans本身的信息
		//保存ktr文件  
		RedisUtil.hset(getTransKey(transInfoDto),TransKtrKey, transMeta.getXML());
		RedisUtil.hset(getTransKey(transInfoDto),TransRemoteKey, isSupportRemote());
		RedisUtil.hset(getTransKey(transInfoDto),TransUpdateKey, System.currentTimeMillis());
		logger.logRowlevel("保存trans运行信息:user"+user+" Name:"+transName+"isSupportRemote:"+isSupportRemote());
		
		//设置数据库查询游标类型为 ResultSet.TYPE_SCROLL_INSENSITIVE(1004):可滚动。但是不受其他用户对数据库更改的影响。
		transMeta.setVariable("idatrix.database.query.resultSetType", "1004");
	}

	/**
	 * 保存trans中所有步骤的当前数据,一般都不需要保存数据
	 */
	public void saveCacheData() {
		if ( !isResumeEnable()  || !isSupportResume()) {
			// 缓存不可用
			return;
		}
		logger.logDetailed(transName+" 开始保存step自定义数据.");
		// 保存trans唯一Id
		RedisUtil.hset(getTransKey(transInfoDto),TransIDKey, trans.getTransactionId());
		// 循环保存没有步骤的数据
		trans.getSteps().forEach((combi) -> {
			try {
				Object stepParams = StepParameterCodec.encodeParamObject(combi.stepMeta, combi.stepMeta.getTypeId());
				if (stepParams instanceof ResumeStepDataParser) {
					ResumeStepDataParser resumeStepDataParser = (ResumeStepDataParser) stepParams;
					Map<Object, Object> cacheData = resumeStepDataParser.getCacheData(trans.getTransMeta(),
							combi.stepMeta, combi.meta, combi.data, combi.step);
					boolean isHandle = resumeStepDataParser.afterSaveHandle(cacheData, trans.getTransMeta(),
							combi.stepMeta, combi.meta, combi.data, combi.step);
					if (isHandle && cacheData != null && cacheData.size() > 0) {
						cacheData.put(TransIDKey, trans.getTransactionId());
						logger.logBasic("保存"+combi.stepname+"缓存数据到缓存.数据量:"+cacheData.size());
						RedisUtil.hmset(getCacheKey(combi.stepMeta), cacheData);
					}
				}

			} catch (Exception e) {
				logger.logError(transName+","+combi.stepname+"保存数据异常.",e);
			}
		});

	}

	/**
	 * trans初始化前执行,用来修改 meta数据
	 * 
	 * @throws Exception
	 */
	public void dealStepMeta() throws Exception {
		if ( !isResumeEnable() || !isSupportResume() || isSameTrans() || isFirst() ) {
			// 缓存不可用
			return;
		}
		logger.logDetailed(transName+" 开始修改stepMeta数据.");
		for (StepMeta stepMeta : trans.getTransMeta().getTransHopSteps(false)) {

			ResumeStepDataParser resumeStepDataParser = getStepDataParsers(stepMeta);
			if (resumeStepDataParser != null) {
				resumeStepDataParser.dealStepMeta(trans.getTransMeta(), stepMeta, stepMeta.getStepMetaInterface());
			}
		}
	}

	/**
	 * trans运行前,进行step处理
	 * 
	 * @throws Exception
	 */
	public void preRunStepHandle() throws Exception {
		if ( !isResumeEnable() || !isSupportResume() || isSameTrans() ) {
			// 缓存不可用
			return;
		}
		logger.logDetailed(transName+" 开始运行前step处理.");
		for (StepMetaDataCombi combi : trans.getSteps()) {

			ResumeStepDataParser resumeStepDataParser = getStepDataParsers(combi.stepMeta);
			if (resumeStepDataParser != null) {
				resumeStepDataParser.preRunHandle(trans.getTransMeta(), combi.stepMeta, combi.meta, combi.data, combi.step);
			}
		}

	}

	/**
	 * trans执行前 从缓存数据中恢复数据,并增加行数据变化监听器
	 * 
	 * @throws Exception
	 */
	public void resumeCacheData() throws Exception {
		if ( !isResumeEnable() || !isSupportResume() || isSameTrans()  ) {
			// 缓存不可用
			return;
		}
		logger.logDetailed(transName+" 开始尝试恢复缓存数据.");
		Map<StepMeta, StepLinesDto> effectiveMap = null;
		if( !isFirst() ) {
			// 获取所有组件的有效行数
			effectiveMap = getEffectiveLines(null);
			// 有数据说明需要恢复步骤数据
			if (effectiveMap != null && effectiveMap.size() > 0) {
				logger.logBasic(transName+"从缓存中恢复数据...");
				for (StepMetaDataCombi combi : trans.getSteps()) {
					if (!effectiveMap.containsKey(combi.stepMeta)) {
						// 该步骤不是一个有效的步骤,忽略掉
						continue;
					}
					ResumeStepDataParser resumeStepDataParser = getStepDataParsers(combi.stepMeta);
					if (resumeStepDataParser != null) {
						StepLinesDto linesDto = effectiveMap.get(combi.stepMeta);
						logger.logBasic(combi.stepname+": "+linesDto.toString());
						
						Map<Object, Object> cacheData = RedisUtil.hmget(getCacheKey(combi.stepMeta));
						resumeStepDataParser.resumeCacheData(cacheData, linesDto, trans.getTransMeta(), combi.stepMeta,combi.meta, combi.data, combi.step);
						resumeStepEffectiveLines(combi.step, linesDto);
						resumeStepDataParser.waitPut(linesDto, nextStepMap.get(combi.stepMeta),combi.stepMeta,combi.meta, combi.data, combi.step);
						
						// 删除多余的无用缓存
						RedisUtil.zRemoveByScore(getLinesListKey(combi.stepMeta), linesDto.getRowLine() + 1, Double.MAX_VALUE);

						// 对当前的下一步骤增加get等待事件,处理数据处理多的组件需要等待数据处理少的组件 的数量差的条数
						Map<String, Long> nextDifferenceMap = linesDto.getNextlineDifference();
						List<StepMeta> nextSteps = nextStepMap.get(combi.stepMeta);
						if(nextSteps != null && nextSteps.size()>0) {
							//有下一步骤
							for(StepMeta ns : nextSteps) {
								Optional<StepMetaDataCombi> opt = trans.getSteps().stream().filter(cb -> { return ns.getName().equals(cb.stepname) ;}).findFirst();
								if(opt.isPresent()) {
									StepMetaDataCombi cs = opt.get();
									//获取获取有效行时获取的需要等待的条数
									Long waitLines = 0L;
									if(nextDifferenceMap != null && nextDifferenceMap.containsKey(ns.getName())) {
										 waitLines = nextDifferenceMap.get(ns.getName());
									}
									getStepDataParsers(cs.stepMeta).waitGet(waitLines,combi.stepMeta, cs.stepMeta,cs.meta, cs.data, cs.step);
								}
							}
						}
					}
				}
			}
		}

		// 增加行数变化清理线程
		if (cleanThread == null) {
			// 保存trans唯一Id
			RedisUtil.hset(getTransKey(transInfoDto),TransIDKey, trans.getTransactionId());
			// 增加行数变化监听
			logger.logDetailed(transName+" 开始增加行数变化监听.");
			for (StepMetaDataCombi combi : trans.getSteps()) {
				if(!trans.getTransMeta().isStepUsedInTransHops(combi.stepMeta)) { //有线连接并且线可用的步骤
					//单独没有线连接的组件,忽略
					continue;
				}
				StepLinesDto dto = null;
				if(effectiveMap != null) {
					dto = effectiveMap.get(combi.stepMeta);
				}
				addLinesDataListener(combi.stepMeta,dto, combi.step);
			}

			cleanThread = new Thread(clean);
			cleanThread.start();
		}
		
	}

	/**
	 * 清空当前trans相关的所有缓存
	 */
	public void removeCacheData() {
		if (  !isResumeEnable()  || !isSupportResume() ) {
			// 缓存不可用
			return;
		}
		logger.logDetailed(transName+" 开始删除缓存数据.");
		// 停止缓存清理线程
		if (clean != null) {
			clean.setDone();
		}
		try {
			List<String> delKey = Lists.newArrayList();
			delKey.add(getTransKey(transInfoDto));
			trans.getSteps().forEach((combi) -> {
				ResumeStepDataParser resumeStepDataParser = getStepDataParsers(combi.stepMeta);
				if (resumeStepDataParser != null) {
					delKey.add(getCacheKey(combi.stepMeta));
					delKey.add(getLinesListKey(combi.stepMeta));
				}
			});
	
			RedisUtil.del(delKey.toArray(new String[delKey.size()]));
			RedisUtil.setRemove(registerTrans, transInfoDto);
		} catch (Exception e) {
			logger.logError(transName+"删除缓存数据异常", e);
		}
	}

	/**
	 * 增加对行数变化的监听器(将所有变化写入缓存)
	 * 
	 * @param stepMeta
	 * @param stepInterface
	 */
	private void addLinesDataListener(StepMeta stepMeta,StepLinesDto dto , StepInterface stepInterface) {
		if (  !isResumeEnable()  || !isSupportResume()) {
			// 缓存不可用
			return;
		}
		
		StepLinesDto result;
		if(dto == null){
			result = new StepLinesDto();
		}else {
			result = dto.clone();
			result.setRowLine(null);
			result.setNextlineDifference(null);
		}
		
		ResumeStepDataParser resumeStepDataParser = getStepDataParsers(stepMeta);
		if (stepInterface instanceof BaseStep && resumeStepDataParser != null) {
			BaseStep baseStep = (BaseStep) stepInterface;
			baseStep.addCounterDataListener( new CounterDataListener() {
				@Override
				public void update(String rowKey, String stepName, long line) {
					
					switch (rowKey) {
					case ResumeTransParser.inputKey:
						result.setLinesInput(line);
						break;
					case ResumeTransParser.outputKey:
						result.setLinesOutput(line);
						break;
					case ResumeTransParser.rejectKey: //错误处理步骤行数
						result.setLinesRejected(line);
						result.incrementNextEffectiveOutputLines(stepName);
						break;
					case ResumeTransParser.updateKey:
						result.setLinesUpdated(line);
						break;
					case ResumeTransParser.errorKey:
						result.setLinesErrors(line);
						break;
					case ResumeTransParser.readKey:
						result.setLinesRead(line);
						result.incrementPreEffectiveInputLines(stepName);
						break;
					case ResumeTransParser.writeKey:
						result.setLinesWritten(line);
						result.incrementNextEffectiveOutputLines(stepName);
						break;
					}
					if (resumeStepDataParser.isListenerLineKey(rowKey, result)) {
						//保存行数变化
						RedisUtil.zSet(getLinesListKey(stepMeta), resumeStepDataParser.getLinekeyIndex(result), result.getSaveString());
						//更新时间戳
						RedisUtil.hset(getTransKey(transInfoDto),TransUpdateKey, System.currentTimeMillis());
						try {
							resumeStepDataParser.afterSaveHandle(null, trans.getTransMeta(), stepMeta, baseStep.getStepMetaInterface(), baseStep.getStepDataInterface(), stepInterface);
						} catch (Exception e) {
							logger.logError(stepName+" "+rowKey+" 步骤数据保存后处理 异常",e);
						}
						
						if (resumeStepDataParser.isOutStep() ) {
							// 增加数据清理
							clean.addOutputStep(stepMeta, resumeStepDataParser.getLinekeyIndex(result));
						}
						
						// Test
						 if (stepMeta.getName().equals("表输出") && resumeStepDataParser.getLinekeyIndex(result) >= 10000 && resumeStepDataParser.getLinekeyIndex(result) <= 10003) { 
							 //System.out.println(result);
							//trans.pauseRunning();
						 }
						
					}
					
				}
			});
		}
	}

	/**
	 * 获取所有步骤的最大有效行数信息<br>
	 * 
	 * @param outStepLines
	 *            含有输出步骤结果的map
	 * @param stepMap
	 *            包含所有步骤前后连接关系的map
	 * 
	 * @return 返回的Map 包含所有的组件的有效行数信息<br>
	 *         map的 key 是 步骤Meta对象<br>
	 *         map的value 是StepLinesDto对象 , <br>
	 *         输入/输出:nextEffectiveOutputLines/preEffectiveInputLines有值,<br>
	 * @throws Exception
	 */
	public Map<StepMeta, StepLinesDto> getEffectiveLines(Map<StepMeta, StepLinesDto> outStepLines) throws Exception {

		
		Map<StepMeta, StepLinesDto> result = outStepLines == null ? Maps.newHashMap() : outStepLines;
		if (result == null || result.size() == 0) {
			getOutStepEffectiveLines(result);
		}
		logger.logRowlevel("开始获取和计算缓存中的有效数据,输出步骤"+result);

		// 根据连接顺序,倒推获取每一个组件的有效行数
		if (result.size() > 0 && nextStepMap.size() > 0) {
			// 有效步骤个数,result的size为输出组件的个数,nextStepMap的size为非输出组件的个数 . ps
			// 输出组件可能包含在nextStepMap中
			long repeat = result.keySet().stream().filter(step -> {
				return nextStepMap.containsKey(step);
			}).count();
			long length = nextStepMap.size() + result.size() - repeat; // 有效组件个数
			while (result.size() != length) {
				// 获取还没有获取到有效行数的节点
				Map<StepMeta, List<StepMeta>> curSurplus = nextStepMap.entrySet().stream().filter(entry -> {
					return !result.containsKey(entry.getKey());
				}).collect(Collectors.toMap(en -> {
					return en.getKey();
				}, en -> {
					return en.getValue();
				}));
				if (curSurplus == null || curSurplus.size() == 0) {
					break;
				}
				int i = result.size();
				for (StepMeta step : curSurplus.keySet()) {
					findStepEffectiveLines(step, result);
				}
				if (i == result.size()) {
					// result 没有变化
					break;
				}
			}
		}

		return result;
	}

	/**
	 * 查询所有输出步骤的有效行数
	 * 
	 * @param result
	 *            存储所有输出步骤的有效行数
	 */
	public void getOutStepEffectiveLines(Map<StepMeta, StepLinesDto> result) {
		// 查询所有输出组件,并且根据输出组件倒推出trans连接顺序
		for (StepMetaDataCombi combi : trans.getSteps()) {
			ResumeStepDataParser resumeStepDataParser = getStepDataParsers(combi.stepMeta);
			if (resumeStepDataParser != null && resumeStepDataParser.isOutStep()) {
				// 获取输出组件保存在缓存里面的最后一个数据
				TypedTuple<Object>  lastDto =  RedisUtil.zGetByIndex(getLinesListKey(combi.stepMeta), -1);
				if(lastDto != null) {
					StepLinesDto stepLines = StepLinesDto.parseSaveString(lastDto.getValue()) ;
					
					if (stepLines != null) {
						long linesOutput = combi.step.getLinesOutput();
						
						stepLines.setRowLine(lastDto.getScore().longValue());
						stepLines.setStepMeta(combi.stepMeta);

						// 当前数据和缓存数据不一致(暂停状态数据是一致的不用恢复)
						if (linesOutput != stepLines.getLinesOutput()) {
							result.put(combi.stepMeta, stepLines);
						}
					}
				}
			}
		}
	}

	/**
	 * 查找当前step的有效行数据
	 * 
	 * @param step
	 *            当前步骤
	 * @param result
	 *            所有已知的结果集,查找step的结果并存入result
	 */
	private void findStepEffectiveLines(StepMeta step, Map<StepMeta, StepLinesDto> result) {
		if (nextStepMap.containsKey(step)) {

			Map<StepMeta, StepLinesDto> nextLinesMap = Maps.newHashMap();
			List<StepMeta> nextSteps = nextStepMap.get(step);
			for (StepMeta s : nextSteps) {
				if (result.containsKey(s)) {
					nextLinesMap.put(s, result.get(s));
				} else {
					return;
				}
			}
			
			if(nextLinesMap.size() ==0) {
				return ;
			}

			ResumeStepDataParser resumeStepDataParser = getStepDataParsers(step);
			if (resumeStepDataParser != null) {
				logger.logRowlevel("开始查询计算步骤:"+step.getName()+" ,直接下面的步骤:"+nextLinesMap);
				StepLinesDto stepLines = new StepLinesDto();
				stepLines.setStepMeta(step);
				getStepEffectiveLines(stepLines, nextLinesMap);
				result.put(step, stepLines);
			}
		}
	}

	/**
	 * 获取上次执行后步骤相应的行数信息(对stepLines对象进行回填)
	 * @param stepLines
	 * @param nextStepLines
	 */
	private void getStepEffectiveLines(StepLinesDto stepLines, Map<StepMeta, StepLinesDto> nextStepLines) {
		
		StepMeta step = stepLines.getStepMeta();
		// 输出
		Map<String, Long> nextEffectiveOutputLines = Maps.newHashMap();;
		// 行号
		Long rowLine = -1L;
		
		boolean isDistributes = step.isDistributes()&&nextStepLines.size() > 1;
		StepLinesDto differenceLines = null;
		String differenceNextStepName = null;
		logger.logRowlevel("分发模式?:"+isDistributes);

		Iterator<Entry<StepMeta, StepLinesDto>> nsls = nextStepLines.entrySet().iterator();
		while (nsls.hasNext()) {
			Entry<StepMeta, StepLinesDto> entry = nsls.next();
			StepLinesDto nsl = entry.getValue();
			Long el = null;
			if (nsl.getPreEffectiveInputLines() != null) {
				// 有下一步骤的输入)
				el = nsl.getPreEffectiveInputLines().get(step.getName());
			}
			if (el == null) {
				el = nsl.getRowLine();
			}
			// 保存下一步骤的有效输入
			nextEffectiveOutputLines.put(entry.getKey().getName(), el);
			if (isDistributes) {
				// 分发模式 , 查询当前步骤对应下一步骤有效输入的数据行
				TypedTuple<Object> r = RedisUtil.zScan(getLinesListKey(step),"\"" + entry.getKey().getName() + "\":\\[\"java\\.lang\\.Long\"," +el  , true);
				
				if (r != null) {
					if (rowLine < 0 || (r.getScore() >= 0 && rowLine > r.getScore().longValue())) {
						rowLine = r.getScore().longValue();
						differenceLines = StepLinesDto.parseSaveString( r.getValue() );
						differenceNextStepName = entry.getKey().getName();
					}
				}else {
					logger.logBasic("分发模式,未找到最小的下一步数据:"+step.getName()+" "+entry.getKey().getName()+" "+el);
				}
			} else {
				// 复制模式,查找最小行号为有效行号
				if (rowLine < 0 || (el >= 0 && rowLine > el)) {
					rowLine = el;
					differenceNextStepName = entry.getKey().getName();
				}
			}
		}
		
		if(!isDistributes) {
			//复制模式时 通过输出有效行 查找 数据有效行 
			TypedTuple<Object> r = RedisUtil.zScan(getLinesListKey(step), differenceNextStepName + "\":\\[\"java\\.lang\\.Long\"," + rowLine  , true);
			if (r != null) {
				rowLine = r.getScore().longValue();
				differenceLines = StepLinesDto.parseSaveString( r.getValue());
			}else {
				logger.logBasic("复制模式,未找到当前的数据行数据:"+step.getName()+" "+differenceNextStepName+" "+rowLine);
			}
		}
		
		logger.logRowlevel("有效行数:"+rowLine+",有效行对象"+differenceLines);
		
		if(nextEffectiveOutputLines.size() >1 ) {
			Iterator<Entry<String, Long>> nsls1 = nextEffectiveOutputLines.entrySet().iterator();
			while (nsls1.hasNext()) {
				Entry<String, Long> entry = nsls1.next();
				if (entry.getKey() == differenceNextStepName) {
					// 最小行数 忽略
					continue;
				}
				// 下一步有效行号
				Long line = entry.getValue();
				Long curLine = 0L;
				if (step.isDistributes() && differenceLines != null) {
					//分发模式, 获取当前有效行号
					Map<String, Long> curNextMap = differenceLines.getNextEffectiveOutputLines();
					curLine = (curNextMap != null && curNextMap.get(entry.getKey()) != null)? curNextMap.get(entry.getKey()): 0L;

				} else {
					//复制模式
					curLine = rowLine;
				}
				if( line != curLine) {
					stepLines.addNextlineDifference(entry.getKey(), line - curLine);
					logger.logRowlevel("多输出,需要增加等待,输出步骤:"+entry.getKey()+" 等待行数:"+ (line - curLine));
				}
			}
		}
		
		if(differenceLines != null) {
			getStepDataParsers(step).setLinesFromCacheLines(stepLines, differenceLines);
		}
		
		stepLines.setRowLine(rowLine);
		stepLines.setNextEffectiveOutputLines(nextEffectiveOutputLines);

	}

	/**
	 * 读取 行缓存数据中的步骤有效数据更新到stepLines中
	 * 
	 * @param stepLines
	 */
	public void readStepLinesByScore(StepLinesDto stepLines) {
		StepMeta stepMeta = stepLines.getStepMeta();

		StepLinesDto cacheLineDto = StepLinesDto.parseSaveString(  RedisUtil.zGetByScore(getLinesListKey(stepMeta),stepLines.getRowLine()) );
		if (cacheLineDto != null) {
			stepLines.setNextEffectiveOutputLines(cacheLineDto.getNextEffectiveOutputLines());
			stepLines.setPreEffectiveInputLines(cacheLineDto.getPreEffectiveInputLines());
			stepLines.setLinesInput(cacheLineDto.getLinesInput());
			stepLines.setLinesOutput(cacheLineDto.getLinesOutput());
			stepLines.setLinesRead(cacheLineDto.getLinesRead());
			stepLines.setLinesWritten(cacheLineDto.getLinesWritten());
			stepLines.setLinesRejected(cacheLineDto.getLinesRejected());
			stepLines.setLinesUpdated(cacheLineDto.getLinesUpdated());
			stepLines.setLinesErrors(cacheLineDto.getLinesErrors());
		}
	}

	/**
	 * 恢复上次执行后步骤中相应的行数信息<br>
	 * 
	 * @param step
	 * @param linesDto
	 */
	private void resumeStepEffectiveLines(StepInterface step, StepLinesDto linesDto) {

		if (step instanceof BaseStep) {
			BaseStep baseStep = (BaseStep) step;

			baseStep.setLinesUpdated(linesDto.getLinesUpdated());
			baseStep.setLinesRejected(linesDto.getLinesRejected());
			baseStep.setLinesRead(linesDto.getLinesRead());
			baseStep.setLinesWritten(linesDto.getLinesWritten());
			baseStep.setLinesInput(linesDto.getLinesInput());
			baseStep.setLinesOutput(linesDto.getLinesOutput());
			baseStep.setErrors(linesDto.getLinesErrors());
		}

	}

	/**
	 * 获取step 对应的ResumeStepDataParser对象<br>
	 * 
	 * @param stepMeta
	 * @return 返回相应的 ResumeStepDataParser对象,<br>
	 *         不是ResumeStepDataParser对象时 返回 null
	 */
	public ResumeStepDataParser getStepDataParsers(StepMeta stepMeta) {
		if (stepDataParsers.containsKey(stepMeta)) {
			return stepDataParsers.get(stepMeta);
		} else {
			try {
				Object stepParams = StepParameterCodec.encodeParamObject(stepMeta, stepMeta.getTypeId());
				ResumeStepDataParser resumeStepDataParser;
				if (stepParams instanceof ResumeStepDataParser) {
					resumeStepDataParser = (ResumeStepDataParser) stepParams;
				} else {
					resumeStepDataParser = new ResumeStepDataParser() {
						@Override
						public int stepType() {
							return 4;
						}
					};
				}

				stepDataParsers.put(stepMeta, resumeStepDataParser);
				return resumeStepDataParser;
			} catch (Exception e) {
			}
		}
		stepDataParsers.put(stepMeta, null);
		return null;
	}

	/**
	 * 根据缓存的id和当前id判断是否是同一次运行( 可能只是暂停)
	 * 
	 * @return
	 */
	private boolean isSameTrans() {
		boolean res = trans != null && trans.getTransactionId().equals(RedisUtil.hget(getTransKey(transInfoDto),TransIDKey));
		if(res) {
			logger.logRowlevel("是同一次转换运行,不进行数据恢复,直接运行.");
		}
		return res;
	}

	/**
	 * 是否是第一次运行
	 * 
	 * @return
	 */
	private boolean isFirst() {
		return RedisUtil.hget(getTransKey(transInfoDto),TransIDKey) == null;
	}

	/**
	 * 判断是否所有的step都支持断点恢复,有一个不支持就返回false
	 * 
	 * @return
	 */
	private boolean isSupportResume() {
		if(!isSupportContinue()) {
			return false;
		}
		if (isSupportResume == null) {
			Optional<StepMeta> opt = transMeta.getSteps().stream().filter(step -> {
				ResumeStepDataParser resumeStepDataParser = getStepDataParsers(step);
				if (resumeStepDataParser != null) {
					return !resumeStepDataParser.isSupportResume();
				}
				return false;
			}).findAny();
			if (opt.isPresent()) {
				logger.logRowlevel(opt.get().getName()+" 不支持缓存恢复,该组件不支持缓存恢复!");
				isSupportResume = false;
			} else {
				isSupportResume = true;
			}
			
			
		}
		return isSupportResume;
	}
	
	public static boolean isResumeEnable() {
		return RedisUtil.isCacheEnable() && Boolean.valueOf(IdatrixPropertyUtil.getProperty("idatrix.breakpoint.resume.enable", "false"));
	}
	
	/**
	 * 是否远程正在运行
	 * @return
	 */
	private String getRemoteRunningId(TransInfoDto transInfoDto) {
		
		String serviceId = (String) RedisUtil.hget(getTransKey(transInfoDto), RemoteResumeServerKey);
		
		if(!Utils.isEmpty(serviceId) && "true".equalsIgnoreCase(IdatrixPropertyUtil.getProperty("idatrix.web.deployment")) && !serviceId.equals( RemoteResumeListener.getInstance().getServiceId()) ) {
			Long exceptTmie = Long.valueOf(IdatrixPropertyUtil.getProperty("idatrix.breakpoint.except.time", "300"));
			long lastUpdateTime = (long) RedisUtil.hget(getTransKey(transInfoDto), TransUpdateKey);
			if ( (lastUpdateTime + (exceptTmie * 1000)) >= System.currentTimeMillis()) {
				return serviceId;
			}
		}
		return null;
	}

	/**
	 * 存储 transId 的缓存 key
	 * 
	 * @return
	 */
	public static String getTransKey(TransInfoDto transInfoDto) {
		return transInfoDto.getUser() + "-" + transInfoDto.getTransName() + "-info"  ;
	}

	public static String getQueueKey(TransInfoDto transInfo) {
		return transInfo.getUser() + "-" + transInfo.getTransName() + "-" + transQueueKey;
	}
	
	/**
	 * 存储 step 数据的缓存key,一般都没有数据存储
	 * 
	 * @param step
	 * @return
	 */
	public String getCacheKey(StepMeta step) {
		return user + "-" + transName + "-" + step.getName()+"-data";
	}

	/**
	 * 存储 step 行数数据的缓存key
	 * 
	 * @param step
	 * @return
	 */
	public String getLinesListKey(StepMeta step) {
		return user + "-" + transName + "-" + step.getName() + "-lines";
	}


	public boolean isSupportContinue() {
		return Boolean.valueOf((String)transMeta.getVariable(BREAKPOINT_CONTINUE_ENABLE));
	}
	
	public boolean isSupportRemote() {
		return Boolean.valueOf((String)transMeta.getVariable(BREAKPOINTS_REMOTE));
	}
	
	public boolean isForceLocal() {
		return Boolean.valueOf((String)transMeta.getVariable(BREAKPOINTS_FORCELOCAL));
	}
	
}
