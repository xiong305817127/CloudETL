package com.ys.idatrix.cloudetl.recovery.trans;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.pentaho.di.core.RowSet;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStep;
import org.pentaho.di.trans.step.BaseStep.WaitDataListener;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.collect.Maps;
import com.ys.idatrix.cloudetl.recovery.trans.dto.StepLinesDto;

/**
 *  trans step 断点(异常)运行后 续传处理接口 
 *
 * @author XH
 * @since 2018年3月29日
 *
 */
@JsonIgnoreProperties(value={"inStep","outStep","middlewareStep","specialMiddlewareStep","listenerLineKey","linekeyIndex","supportResume"}) 
public interface ResumeStepDataParser {
	
	/**
	 * 获取需要保存到缓存里面的数据 ( 异常/暂停 时会调用)<br>
	 * 也可以做一些保存前数据处理<br>
	 * 影响的行数[输入行数,输出行数,写行数,读行数,更新行数,错误行数 ] 都会自动记录,不需要单独处理,只需要保存各步骤各自的数据 <br>
	 * 可以返回空
	 * @param abstractMeta
	 * @param stepMetaInterface
	 * @param stepDataInterface
	 * @param stepInterface
	 * @return
	 * @throws Exception
	 */
	default public Map<Object,Object> getCacheData(TransMeta transMeta ,StepMeta stepMeta ,StepMetaInterface stepMetaInterface,StepDataInterface stepDataInterface , StepInterface stepInterface)  throws Exception  {
		return null ;
	}
	
	/**
	 * 缓存数据保存后 进行step处理( 异常/暂停 时在getCacheData方法之后会调用 , 在每行数据号保存时 会调用,此时 cacheData为空)
	 * eg. 输出文件的 输出流 flush操作等
	 * @param data saveCacheData方法保存的缓存数据
	 * @param transMeta
	 * @param stepMeta
	 * @param stepMetaInterface
	 * @param stepDataInterface
	 * @param stepInterface
	 * @return  返回 true 继续保存cacheData到缓存,否则不保存
	 * @throws Exception
	 */
	default public boolean afterSaveHandle(Map<Object, Object> cacheData ,TransMeta transMeta ,StepMeta stepMeta ,StepMetaInterface stepMetaInterface,StepDataInterface stepDataInterface , StepInterface stepInterface)  throws Exception  {
		return true ;
	}
	
	/**
	 * trans初始化前,处理步骤Meta数据( 有数据需要恢复时才会调用,初次不会调用) <br>
	 * eg . 设置文本文件输出 的文件模式为 append 等需求
	 * @param transMeta
	 * @param stepMeta
	 * @param stepMetaInterface
	 * @throws Exception
	 */
	default public boolean dealStepMeta(TransMeta transMeta ,StepMeta stepMeta ,StepMetaInterface stepMetaInterface)  throws Exception   {
		return true ;
	}
	
	/**
	 * trans运行前,进行step处理 ,缓存数据恢复.( 有数据需要恢复时才会调用,初次不会调用  )<br>
	 * 也可以做运行前数据处理
	 * @param data saveCacheData方法保存的缓存数据
	 * @param lines  上次执行转换时 已经处理的最大有效行数
	 * @param currentLines 上次执行转换时 当前步骤操作的最大行数(一般没什么用)
	 * @param transMeta
	 * @param stepMeta
	 * @param stepMetaInterface
	 * @param stepDataInterface
	 * @param stepInterface
	 * @throws Exception
	 */
	default public boolean resumeCacheData(Map<Object,Object> cacheData,StepLinesDto linesDto ,TransMeta transMeta ,StepMeta stepMeta ,StepMetaInterface stepMetaInterface,StepDataInterface stepDataInterface , StepInterface stepInterface)  throws Exception {
		return true ;
	}
	
	/**
	 * trans运行前,进行step处理( 不管是否有数据需要恢复,启用了缓存机制就会在resumeCacheData方法之前调用) 
	 * @param transMeta
	 * @param stepMeta
	 * @param stepMetaInterface
	 * @param stepDataInterface
	 * @param stepInterface
	 * @return
	 * @throws Exception
	 */
	default public boolean preRunHandle(TransMeta transMeta ,StepMeta stepMeta ,StepMetaInterface stepMetaInterface,StepDataInterface stepDataInterface , StepInterface stepInterface)  throws Exception  {
		return true ;
	}
	
	/**
	 *  设置步骤get数据等待监听,恢复数据后一定会调用,根据需要重构方法体<br>
	 *  当有多个输入时 会被调用多次,waitNumber 和 preStepMeta 会为相应 废弃条数 和输入步骤meta
	 * @param waitNumber 需要废弃preStepMeta发来的数据条数, 即 preStepMeta 发来的waitNumber条数据需要被废弃,当 为 0时表示不需要废弃;
	 * @param preStepMeta 前一个步骤的meta
	 * @param curStepMeta
	 * @param stepMetaInterface
	 * @param stepDataInterface
	 * @param stepInterface
	 * @return
	 * @throws Exception
	 */
	default public boolean waitGet(Long waitNumber ,StepMeta preStepMeta ,StepMeta curStepMeta ,StepMetaInterface stepMetaInterface,StepDataInterface stepDataInterface , StepInterface stepInterface)  throws Exception  {
		if(waitNumber == 0 ) {
			return true;
		}
		waitGetRowData(stepInterface, waitNumber,preStepMeta.getName());
		return true ;
	}
	
	/**
	 * 设置步骤put数据等待监听,恢复数据后一定会调用,根据需要重构方法体<br>
	 * 可以根据需要调用 默认的  waitPutRowData 方法,忽略前 num 条数据  不put到下一步骤
	 * @param linesDto
	 * @param transMeta
	 * @param stepMeta
	 * @param stepMetaInterface
	 * @param stepDataInterface
	 * @param stepInterface
	 * @return
	 * @throws Exception
	 */
	default public boolean waitPut(	StepLinesDto linesDto,List<StepMeta> nextStepMeta ,StepMeta curStepMeta ,StepMetaInterface stepMetaInterface,StepDataInterface stepDataInterface , StepInterface stepInterface)  throws Exception  {
		return true ;
	}
	

	/**
	 * 返回步骤的类型    <br>
	 * 不支持: 0 (0000b) : 不支持断点,需要将所有数据读入内存计算 <br>
	 * 输入步骤 : 1 (0001b) : 会有 输入行数  ,写行数  <br>
	 * 输出步骤 : 2 (0010b) : 会有 输出行数 , 读行数   <br>
	 * 普通中间件: 4 (0100b) : 会有 读行数  ,写行数<br>
	 * 特殊组件: 8 (1000b) : 会造成输出行数变化,不再是输入n行输出n行,一般都是多输入组件,和上面三种类型组合<br>
	 *  <br>
	 *  eg.  文本文件输出 既是输出步骤又是中间件 : 6 (0110b) <br>
	 *  eg.  记录关联 (笛卡尔输出) 既是中间件又是输出行数增加的特殊组件 : 12 (1100b) <br>
	 * @return
	 */
	@JsonIgnore
	public int stepType();
	

	/**
	 * rowKey变化时 是否保存数据到缓存,返回true会增加一条缓存数据<br>
	 * 默认 输出步骤是 ResumeTransParser.outputKey 剩下的都是  ResumeTransParser.writeKey <br>
	 * @param rowKey ResumeTransParser.inputKey ,ResumeTransParser.outputKey  ,ResumeTransParser.readKey  ,ResumeTransParser.writeKey ,ResumeTransParser.rejectKey  ,ResumeTransParser.updateKey <br>
	 * @param result 当前行数据
	 * @return
	 */
	@JsonIgnore
	default public boolean isListenerLineKey(String rowKey ,StepLinesDto result) {
		
		if (isOutStep()) {
			return ResumeTransParser.outputKey.equals(rowKey);
		}
		return ResumeTransParser.writeKey.equals(rowKey) ;
	}
	
	/**
	 * 根据result中的nextEffectiveOutputLines和rowLine 返回当前步骤的数据行数<br>
	 * @param result 当前行数据
	 * @return
	 */
	@JsonIgnore
	default public Long getLinekeyIndex(StepLinesDto result) {
		
		Long rowLine = result.getRowLine();
		if(rowLine ==  null) {
			if (isOutStep()) {
				rowLine = result.getLinesOutput();
			}else {
				rowLine = result.getLinesWritten()+result.getLinesRejected();
			} 
		}
		
		return  rowLine;
	}
	
	/**
	 * 根据缓存中保存的数据恢复当前lines对象<br>
	 * ps. nextEffectiveOutputLines 和  rowLine 会填入真实的数据,填写无效<br>
	 * 一般不需要改写 ,除非从缓存中获取的数据不是有效的数据 , eg. GroupBy组件,会预读一行,造成输入行数会多1,所以需要从缓存中读取并减一才是上一步的有效行数
	 * @param lines 当前有效lines
	 * @param cacheLines 从缓存中获取的lines
	 */
	@JsonIgnore
	default public void setLinesFromCacheLines(StepLinesDto lines , StepLinesDto cacheLines) {
		lines.setPreEffectiveInputLines(cacheLines.getPreEffectiveInputLines());
		lines.setLinesInput(cacheLines.getLinesInput());
		lines.setLinesOutput(cacheLines.getLinesOutput());
		lines.setLinesRead(cacheLines.getLinesRead());
		lines.setLinesWritten(cacheLines.getLinesWritten());
		lines.setLinesRejected(cacheLines.getLinesRejected());
		lines.setLinesUpdated(cacheLines.getLinesUpdated());
		lines.setLinesErrors(cacheLines.getLinesErrors());
	}

	
	/***************************************以下方法不进行覆盖重写,用于快捷调用********************************************************/
	/**
	 * 是否是输入类型的步骤
	 * @return
	 */
	@JsonIgnore
	default public boolean isSupportResume() {
		return stepType() != 0;
	}
	
	/**
	 * 是否是输入类型的步骤
	 * @return
	 */
	@JsonIgnore
	default public boolean isInStep() {
		return ((stepType() & ( 1 )) != 0);
	}
	/**
	 * 是否是输出类型的步骤
	 * @return
	 */
	@JsonIgnore
	default public boolean isOutStep() {
		return ((stepType() & (1 << 1)) != 0);
	}
	/**
	 * 是否是普通中间处理步骤
	 * @return
	 */
	@JsonIgnore
	default public boolean isMiddlewareStep() {
		return ((stepType() & (1 << 2)) != 0);
	}
	
	/**
	 * 是否是特殊中间处理步骤
	 * @return
	 */
	@JsonIgnore
	default public boolean isSpecialMiddlewareStep() {
		return ((stepType() & (1 << 3)) != 0);
	}
	
	
	/**
	 * 忽略前 num 条数据  不进行put ( 即 前num条数据直接忽略掉,不put到下个步骤)<br>
	 * 一般针对 输入组件  , 已经处理了的num条数据直接忽略
	 * @param stepInterface
	 * @param num
	 */
	default public void waitPutRowData(StepInterface stepInterface , long num) {
		if( !ResumeTransParser.isResumeEnable() || num == 0 ) {
			//缓存不可用
			return ;
		}
		if (stepInterface instanceof BaseStep) {
			BaseStep baseStep = (BaseStep) stepInterface;
			baseStep.addWaitDataListener( new WaitDataListener() {
				
				private AtomicInteger counter =  new AtomicInteger(0);
				
				@Override
				public boolean isPut(RowSet rowSet ,RowMetaInterface rowMeta, Object[] row)  {
					if(counter.getAndIncrement() >= num) {
						return true ;
					}
					if( baseStep.getLinesInput() >1) {
						baseStep.setLinesInput( baseStep.getLinesInput()-1);
					}
					baseStep.setLinesWritten(baseStep.getLinesWritten()-1);
					return false;
				}
				
				@Override
				public boolean isEnd(RowSet rowSet)  {
					  return counter.get() >= num ;
				}
				 
				 
			},false);
		}
	}
	

	Map<String,AtomicInteger> inStepCounterMap =  Maps.newHashMap(); 
	Map<String,Long> inStepWaitLineMap =  Maps.newHashMap();
	/**
	 * 忽略前 num 条数据  不进行get ( 即 前num条数据获取到后直接忽略掉,不在当前步骤中进行处理)<br>
	 * 一般针对 输出组件  , 已经处理了的num条数据直接忽略
	 * @param stepInterface
	 * @param num
	 */
	default public void waitGetRowData(StepInterface stepInterface , long waitLine ,String inStepName) {
		if( !ResumeTransParser.isResumeEnable() || waitLine == 0) {
			//缓存不可用
			return ;
		}
		 
		if (stepInterface instanceof BaseStep) {
			if( inStepCounterMap.isEmpty()) {
				
				inStepCounterMap.put(inStepName, new AtomicInteger(0));
				inStepWaitLineMap.put(inStepName, waitLine);
				
				BaseStep baseStep = (BaseStep) stepInterface;
				baseStep.addWaitDataListener(new WaitDataListener() {
					
					@Override
					public boolean isGet(RowSet rowSet , Object[] row)  {
						AtomicInteger counter = inStepCounterMap.get(rowSet.getOriginStepName() );
						if(counter == null) {
							return true ;
						}
						if(counter.getAndIncrement() >= inStepWaitLineMap.get(rowSet.getOriginStepName())) {
							return true ;
						}
						baseStep.setLinesRead(baseStep.getLinesRead()-1);
						return false;
					}
					
					@Override
					public boolean isEnd(RowSet rowSet)  {
						AtomicInteger counter = inStepCounterMap.get(rowSet.getOriginStepName() );
						if(counter ==  null) {
							return true ;
						}
						return counter.get() >= inStepWaitLineMap.get(rowSet.getOriginStepName()) ;
					}
				},true);
				
			}else {
				//第二个输入步骤 不用再次增加监听
				inStepCounterMap.put(inStepName, new AtomicInteger(0));
				inStepWaitLineMap.put(inStepName, waitLine);
			}
		}
	}
	
}
