/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.ext.executor;

import java.util.List;

import org.pentaho.di.core.util.IdatrixPropertyUtil;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.repository.RepositoryObjectType;
import org.pentaho.di.trans.TransExecutionConfiguration;
import org.pentaho.di.trans.TransMeta;

import com.ys.idatrix.cloudetl.dto.trans.TransExecLogDto;
import com.ys.idatrix.cloudetl.dto.trans.TransExecStepMeasureDto;
import com.ys.idatrix.cloudetl.dto.trans.TransExecStepStatusDto;
import com.ys.idatrix.cloudetl.ext.CloudSession;
import com.ys.idatrix.cloudetl.reference.graph.GraphService;
import com.ys.idatrix.cloudetl.toolkit.ToolkitTrigger;

/**
 * Common procedures for execution of transformation and jobs.
 * @author JW
 * @since 2017年7月12日
 *
 */
public abstract class BaseTransExecutor extends BaseExecutor {
	
	protected BaseTransExecutor(TransMeta transMeta, TransExecutionConfiguration configuration) {
		this(transMeta, configuration,CloudSession.getLoginUser() , CloudSession.getResourceUser());
	}

	protected BaseTransExecutor(TransMeta transMeta, TransExecutionConfiguration configuration,String execUser,String owner ) {
		super(transMeta, configuration, execUser, owner);
	}
	
	@Override
	public RepositoryObjectType getType()  {
		return RepositoryObjectType.TRANSFORMATION;
	}

	public TransExecutionConfiguration getExecutionConfiguration() {
		return (TransExecutionConfiguration)executionConfiguration;
	}
	
	public TransMeta getTransMeta() {
		return (TransMeta)metaClone ;
	}
	
	public static void startToolkitTrigger(TransMeta transMeta ,String transName , String owner ,String status) {
		//是否开启流程分析功能
		boolean analyzerOpen = GraphService.isAnalyzerEnable();
		//是否只有成功时才开启分析
		boolean analyzerOnlySuccess = "0".equalsIgnoreCase( IdatrixPropertyUtil.getProperty("idatrix.analyzer.trigger.level") );
		
		if ( analyzerOpen &&
			( !analyzerOnlySuccess || ( analyzerOnlySuccess && CloudExecutorStatus.assertSuccess(status)) ) 
			) {
			try {
				ToolkitTrigger trigger = new ToolkitTrigger(transMeta, transName , owner);
				Thread tr = new Thread(trigger, "Trigger_"+ trigger.getTriggerId()+ Utils.getThreadNameesSuffixByUser(owner, owner, false));
				tr.start();
			} catch (Exception e) {
				logger.error("启动数据地图触发器失败,",e);
			}
		}
	}

	public abstract String execStatus()  throws Exception;

	public abstract boolean execPause()  throws Exception;

	public abstract boolean execStop()  throws Exception;

	public abstract boolean execResume() throws Exception;
	
	public abstract void clear() throws Exception;

	public abstract List<TransExecStepMeasureDto> getStepMeasure() throws Exception;

	public abstract List<TransExecStepStatusDto> getStepStatus() throws Exception;

	public abstract TransExecLogDto getExecLog() throws Exception;
	
}
