/**
 * 云化数据集成系统 
 * iDatrxi CloudETL
 */
package com.ys.idatrix.cloudetl.toolkit.graph;

import java.util.List;
import com.google.common.collect.Lists;
import com.ys.idatrix.cloudetl.ext.PluginFactory;
import com.ys.idatrix.cloudetl.logger.CloudLogger;
import com.ys.idatrix.cloudetl.reference.graph.GraphService;
import com.ys.idatrix.cloudetl.reference.graph.dto.WriteRelationDto;
import com.ys.idatrix.cloudetl.toolkit.ToolkitTrigger;
import com.ys.idatrix.cloudetl.toolkit.common.NodeLevel;
import com.ys.idatrix.cloudetl.toolkit.record.AnalyzerReporter;

/**
 * NodeWriter <br/>
 * 
 * @author JW
 * @since 2018年1月8日
 * 
 */
// @Configurable
public class GraphWriter {

	private final ToolkitTrigger trigger;

	private GraphService graphService;

	public GraphWriter(ToolkitTrigger trigger) {
		this.trigger = trigger;
		this.graphService = (GraphService) PluginFactory.getBean("graphService");
	}

	public void writeNodeAndRelationship(AnalyzerReporter reporter) throws Exception {
		
		if(!GraphService.isAnalyzerEnable()) {
			return ;
		}
		
		List<WriteRelationDto>  result =  Lists.newArrayList() ;
		
		reporter.getRelationships().stream().forEach(r -> {
			WriteRelationDto wrd = result.stream().filter(w -> {
				return ( w.getStartParent().equals(r.getStartNode().getTableLevelNode(NodeLevel.TABLE))&& w.getEndParent().equals(r.getEndNode().getTableLevelNode(NodeLevel.TABLE) ) ) ||
					   ( w.getEndParent().equals(r.getStartNode().getTableLevelNode(NodeLevel.TABLE))&& w.getStartParent().equals(r.getEndNode().getTableLevelNode(NodeLevel.TABLE) ) );
				}).findAny().orElse(null);
			if( wrd == null ) {
				wrd = new WriteRelationDto(r.getStartNode().getTableLevelNode(NodeLevel.TABLE),r.getEndNode().getTableLevelNode(NodeLevel.TABLE)) ;
				result.add(wrd);
			}
			wrd.addRelationships(r);
		});

		if( graphService != null ) {
			
			trigger.flushLog("数据节点关系列表: \n" + result.toString() );
			try {
				graphService.saveNodeAndRelationship( trigger.getUser(), trigger.getName(), result);
			}catch( Exception e) {
				trigger.flushLog("数据推送响应异常: " + CloudLogger.getExceptionMessage(e));
				trigger.getLogger().insertExecExceptionLog(e);
				ToolkitTrigger.syslogger.error("数据推送响应异常: " + CloudLogger.getExceptionMessage(e));
			}
		}
		
	}
	
}
