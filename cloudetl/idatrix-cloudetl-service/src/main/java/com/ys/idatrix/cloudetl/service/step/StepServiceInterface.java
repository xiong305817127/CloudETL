package com.ys.idatrix.cloudetl.service.step;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.pentaho.di.core.util.Utils;

import com.google.common.collect.Lists;
import com.ys.idatrix.cloudetl.service.CloudSubscribeStepService;
import com.ys.idatrix.cloudetl.subscribe.api.dto.step.StepDto;

public  abstract class StepServiceInterface<T extends StepDto> {
	
	protected CloudSubscribeStepService stepService ;
	
	protected StepDto[] previousDto ;
	protected T t ;
	
	protected Object[] params ;
	
	private List<String> inStepNames;
	private List<String> outStepNames;

	@SuppressWarnings("unchecked")
	public void init(CloudSubscribeStepService cloudSubscribeStepService ,StepDto curDto,StepDto... previousStep ){
		
		this.stepService = cloudSubscribeStepService ;
		this.t = (T)curDto ;
		this.previousDto = previousStep;
	}

	/**
	 * 获取当前服务的Dto对象,初始化服务的时候进行赋值
	 * @return
	 * @throws Exception
	 */
	protected T getStepDto() throws Exception {
		if(t == null ) {
			throw new Exception("服务对象需要先初始化.");
		}
		return t ;
	}
	
	/**
	 * 获取当前步骤的前面的步骤
	 * @param index
	 * @return
	 * @throws Exception
	 */
	protected StepDto getPreviousStepDto(int index) throws Exception {
		 if( previousDto != null && previousDto.length > index) {
			return previousDto[index];
		}
		return null ;
	}
	
	protected StepDto[] getPreviousStepDtos() throws Exception {
		return previousDto ;
	}
	
	/**
	 * 设置需要用到的参数
	 * @param params
	 */
	public void setParams(Object[] params) {
		this.params =params ;
	}
	
	protected  Object[] getParams() {
		return params ;
	}
	
	protected  Object getParam(int index,Object[] params ) {
		if(params != null && params.length > index) {
			return params[index] ;
		}
		if( this.params != null &&  this.params.length > index ) {
			return  this.params[index] ;
		}
		return null ;
	}
	
	public String getStepName() {
		return t.getType() ;
	}
	/**
	 * 获取当前组件生成后的所有输入节点名称(首节点) <br>
	 * addCurStepHop 方法会自动赋值
	 * @return
	 */
	public List<String> getInStepNames() {
		return inStepNames;
	}

	public void setInStepNames(List<String> inStepNames) {
		this.inStepNames = inStepNames;
	}

	/**
	 * 获取当前组件生成后的所有输出节点名称(尾节点) <br>
	 *  addCurStepHop 方法会自动赋值
	 * @return
	 */
	public List<String> getOutStepNames() {
		return outStepNames;
	}

	public void setOutStepNames(List<String> outStepNames) {
		this.outStepNames = outStepNames;
	}

	public void addStepName(String inStepName,String outStepName) {
		if( this.inStepNames == null ) {
			this.inStepNames = new ArrayList<String>();
		}
		if( this.outStepNames == null ) {
			this.outStepNames = new ArrayList<String>();
		}
		if(!Utils.isEmpty(inStepName)&&!inStepNames.contains(inStepName)) {
			inStepNames.add(inStepName);
		}
		if(!Utils.isEmpty(outStepName)&&!outStepNames.contains(outStepName)) {
			outStepNames.add(outStepName);
		}
	}
	
	/**
	 * 创建 StepParameter 或者 EntryParameter 对象
	 * @return
	 */
	public abstract Object createParameter(Object... params) throws Exception;
	
	/**
	 * 将当前t 转换为StepParameter 或者 EntryParameter 加入 transMeta或者jobMeta中
	 * @param transOrJobName
	 * @param group
	 * @param params
	 */
	protected abstract List<String>  addCurStepToMeta(String transOrJobName, String group, Map<String, String> execParams) throws Exception;
	
	/**
	 * 将t 转换为StepParameter 或者 EntryParameter 加入 transMeta或者jobMeta中,并进行连线 <br>
	 * 包括 可能存在的 nextStep 参数  <br>
	 * 会为 inStepNames(首节点列表) 和 outStepNames(尾节点列表) 赋值  
	 * @param transOrJobName
	 * @param group
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public void addStepToMeta(String transOrJobName, String group, Map<String, String> params) throws Exception{
		
		List<String> result = addCurStepToMeta(transOrJobName, group, params);
		//对当前步骤进行连线
		addCurStepHop(transOrJobName, group, result);
		//判断是否有下一步需要加入
		StepDto curT = getStepDto();
		if(curT != null && curT.getNextStepDtos() != null  && !curT.getNextStepDtos().isEmpty() ) {
			List<StepDto> nextTs = curT.getNextStepDtos();
			List<String> outs = Lists.newArrayList() ;
			for( StepDto nextT : nextTs) {
				StepServiceInterface<StepDto> nextService = stepService.getStepService(nextT);
				nextService.addStepToMeta(transOrJobName, group, params);
				//将当前的输出和下一步的输入进行连线并更新当前步骤的输出
				addNextStepHop(transOrJobName, group,  nextService.getInStepNames());
				outs.addAll(nextService.getOutStepNames());
			}
			//更新当前步骤的输出
			if(!outs.isEmpty()) {
				setOutStepNames(outs);
			}
		}
	}
	
	/**
	 * 增加当前节点的连线 <br>
	 * 会为 inStepNames(首节点列表) 和 outStepNames(尾节点列表) 赋值  
	 * @param transOrJobName
	 * @param group
	 * @param stepNames
	 * @throws Exception
	 */
	protected void addCurStepHop(String transOrJobName ,String group, List<String> stepNames) throws  Exception {
		if( stepNames != null && stepNames.size() >0 ) {
			//保存第一个和最后一个步骤名
			addStepName(stepNames.get(0), stepNames.get(stepNames.size()-1));
			//将中间的步骤进行连线
			String preName = stepNames.get(0) ;
			for(int i=1;i<stepNames.size();i++) {
				String nextStep = stepNames.get(i);
				stepService.addHopMeta(transOrJobName,group, preName, nextStep, getStepDto().isJobStep());
				preName= nextStep;
			}
		}
	}
	
	/**
	 * 增加 前面节点的输出节点列表和当前节点的输入节点列表 连线
	 * @param transOrJobName
	 * @param group
	 * @param preOutStepNames
	 * @throws Exception
	 */
	public void addPreStepHop(String transOrJobName ,String group, List<String> preOutStepNames) throws  Exception {
		if( preOutStepNames != null && !preOutStepNames.isEmpty() && getInStepNames() != null && !getInStepNames().isEmpty()) {
			for( String preOutName : preOutStepNames ) {
				for( String curInName : getInStepNames() ) {
					stepService.addHopMeta(transOrJobName,group, preOutName, curInName, getStepDto().isJobStep());
				}
			}
		}
	}

	/**
	 * 增加 当前节点的输出节点列表和后面节点的输入节点列表 连线
	 * @param transOrJobName
	 * @param group
	 * @param nextInStepNames
	 * @throws Exception
	 */
	public void addNextStepHop(String transOrJobName ,String group, List<String> nextInStepNames) throws  Exception {
		if( nextInStepNames != null && !nextInStepNames.isEmpty() && getOutStepNames() != null && !getOutStepNames().isEmpty()) {
			for( String curOutName : getOutStepNames() ) {
				for( String  nextInName : nextInStepNames ) {
					stepService.addHopMeta(transOrJobName,group, curOutName, nextInName, getStepDto().isJobStep());
				}
			}
		}
	}
	
	
}
