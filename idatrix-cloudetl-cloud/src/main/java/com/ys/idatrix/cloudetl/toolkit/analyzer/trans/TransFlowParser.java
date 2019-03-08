/**
 * 云化数据集成系统 iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.toolkit.analyzer.trans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;

import com.ys.idatrix.cloudetl.dto.codec.StepParameterCodec;
import com.ys.idatrix.cloudetl.dto.step.StepFieldDto;
import com.ys.idatrix.cloudetl.toolkit.ToolkitTrigger;
import com.ys.idatrix.cloudetl.toolkit.analyzer.trans.step.StepDataRelationship;
import com.ys.idatrix.cloudetl.toolkit.common.NodeType;
import com.ys.idatrix.cloudetl.toolkit.domain.DataNode;
import com.ys.idatrix.cloudetl.toolkit.domain.Relationship;
import com.ys.idatrix.cloudetl.toolkit.record.AnalyzerReporter;
import com.ys.idatrix.cloudetl.toolkit.utils.RelationshipUtil;
import com.ys.idatrix.cloudetl.toolkit.utils.StepParserUtil;

/**
 * 转换流程数据关系分析.
 * 
 * @author JW
 * @since 2017年5月24日
 *
 */
public class TransFlowParser {

	private final TransMeta transMeta;

	private final ToolkitTrigger trigger;

	public TransFlowParser(TransMeta transMeta, ToolkitTrigger trigger) {
		this.transMeta = transMeta;
		this.trigger = trigger;
	}

	/**
	 * 转换流程数据关系分析入口
	 * @param reporter
	 * @throws Exception 
	 */
	public void parsing(AnalyzerReporter reporter) throws Exception {
		initReporter(reporter);

		// Start parsing
		//
		trigger.flushLog("开始解析 stepData & relationship ...");

		HashMap<String, StepDataRelationship> sdrMap = new HashMap<>();

		List<StepMeta> stepHolder = transMeta.getSteps();
		for (StepMeta stepMeta : stepHolder) {
			Map<String, StepFieldDto> inputFields = StepParserUtil.getInputFields(transMeta, stepMeta);
			Map<String, StepFieldDto> outputFields = StepParserUtil.getOutputFields(transMeta, stepMeta);
			StepDataRelationship sdr = new StepDataRelationship(inputFields, outputFields);
			StepParameterCodec.getStepDataAndRelationship(transMeta, stepMeta, sdr);
			sdrMap.put(stepMeta.getName(), sdr);
		}
		trigger.flushLog("获取到["+sdrMap.size()+"]个步骤的关系数据,开始调用专门的步骤关系解析器(例如sql,暂未实现)...");
		// Call other analyzers by order to improve the relationship rating
		callOtherAnalyzersByOrder(sdrMap);

		// Combine data nodes
		trigger.flushLog("开始合并数据节点...");
		combineDataNodes(sdrMap, reporter);

		// Combine relationship
		trigger.flushLog("获取到数据节点["+reporter.getDataNodes().size()+"]个,开始合并节点关系...");
		combineRelationship(sdrMap, reporter);
		
		trigger.flushLog("获取到数据节点关系["+reporter.getRelationships().size()+"]个,解析结束.");
	}

	/**
	 * 初始化分析报告
	 * @param reporter
	 */
	public void initReporter(AnalyzerReporter reporter) {
		if (null == reporter.getDataNodes()) {
			reporter.setDataNodes(new ArrayList<>());
		}

		if (null == reporter.getRelationships()) {
			reporter.setRelationships(new ArrayList<>());
		}

		if (null == reporter.getBadDataNodes()) {
			reporter.setBadDataNodes(new ArrayList<>());
		}

		if (null == reporter.getBadRelationships()) {
			reporter.setBadRelationships(new ArrayList<>());
		}

		if (null == reporter.getErrors()) {
			reporter.setErrors(new HashMap<>());
		}

		reporter.setAnalyzerScore(0L);

		reporter.setWritten(false);

		reporter.setElapsedTime(0L);
	}

	/**
	 * 合并各个步骤产生的数据节点，根据GUID来判断是否重复。把所有步骤的数据节点合并保存到reporter中
	 * @param sdrMap
	 * @param reporter
	 */
	public void combineDataNodes(HashMap<String, StepDataRelationship> sdrMap, AnalyzerReporter reporter) {
		List<DataNode> dns = new ArrayList<>();
		List<String> guids = new ArrayList<>();
		sdrMap.values().forEach(sdr ->{
			sdr.getInputDataNodes().forEach(dn -> {
				String guidString = dn.getGuiKey();
				if (!Utils.isEmpty(guidString) && !guids.contains(guidString)) {
					dns.add(dn);
					guids.add(guidString);
				}
			});
			sdr.getOutputDataNodes().forEach(dn -> {
				String guidString = dn.getGuiKey();
				if (!Utils.isEmpty(guidString)&& !guids.contains(guidString)) {
					dns.add(dn);
					guids.add(guidString);
				}
			});
		});

		reporter.getDataNodes().addAll(dns);
	}

	/**
	 * 合并各个步骤产生的关系，即sdrMap中所有sdr的dataRelationship关系列表，并保存到reporter的relationships中，
	 * 合并规则如下：
	 * 1. 扫描关系中的startNode和endNode，如果type为STEP_OR_ENTRY，则以其GUID中field的值为Key，去查询sdr的inputStream和outputStream，找到其对应的origin步骤，
	 * 		然后再去查询origin步骤中的inputDataNodes和outputDataNodes，用找到的DataNode替换原来Node；
	 * 
	 * 2. 再次扫描关系中的startNode和endNode，如果type为STEP_OR_ENTRY，则以其GUID中field的值为Key，去查询sdr的inputStream和outputStream，找到其对应的origin步骤，然后分情况处理：
	 * 		2.1 如果是startNode，则扫描origin步骤的所有关系，找到所有以其为endNode的关系，并把找到的每一个关系与当前关系合并成新的关系；
	 * 		2.2 如果是endNode，则扫描origin步骤的所有关系，找到所有以其为startNode的关系，并把找到的每一个关系与当前关系合并成新的关系；
	 * 		2.3 判断合并后的新关系中的startNode和endNode，如果都不为STEP_OR_ENTRY，则把合并后的新关系保存到reporter的relationships中，否则以该新关系作为起点，用origin步骤的sdr，重复步骤2
	 * 
	 * 3. 扫描reporter的relationships，把遗留的关系中仍然包含type为STEP_OR_ENTRY的节点的关系，移到badRelationship列表中
	 * 
	 * @param sdrMap
	 * @param reporter
	 */
	public void combineRelationship(HashMap<String, StepDataRelationship> sdrMap, AnalyzerReporter reporter) {
		// 步骤1
		replaceStepOrEntryNode(sdrMap);

		// 步骤2
		List<Relationship> relationships = new ArrayList<>();
		sdrMap.values().forEach(sdr ->{
			sdr.getDataRelationship().forEach(dr -> {
				if (!dr.getStartNode().isStepOrEntryField() && !dr.getEndNode().isStepOrEntryField()) {
					//开始结束都不是流节点
					relationships.add(dr);
				} else {
					if( dr.getStartNode().isStepOrEntryField() && dr.getEndNode().isStepOrEntryField() 
							&& StringUtils.equalsIgnoreCase( dr.getStartNode().getStepOrEntryField() , dr.getEndNode().getStepOrEntryField()) ) {
						//相同的两个流节点
						return ;
					}
					
					if (dr.getStartNode().isStepOrEntryField()) {
						//开始节点时流节点 
						collapseStepOrEntryStartNode(sdrMap, sdr, dr, relationships);
					}

					if (dr.getEndNode().isStepOrEntryField()) {
						//结束节点时流节点 
						collapseStepOrEntryEndNode(sdrMap, sdr, dr, relationships);
					}
				}
			});
		});
		reporter.getRelationships().addAll(relationships);

		// 步骤3
		removeBadRelationships(reporter);
	}

	private void collapseStepOrEntryStartNode(HashMap<String, StepDataRelationship> sdrMap, StepDataRelationship sdr, Relationship dr, List<Relationship> results) {
		DataNode sdn = dr.getStartNode();
		String fieldName = sdn.getStepOrEntryField();
		if (!Utils.isEmpty(fieldName)) {
			StepFieldDto dto = sdr.getInputStream().get(fieldName);
			if (dto == null) {
				//理论上不会出现在这里,容错
				dto = sdr.getOutputStream().get(fieldName);
			}
			if(dto ==null) {
				//该节点不是流节点(数据库节点,系统节点等)
				trigger.flushLog("警告[3]:流名["+fieldName+"]未找到相应的输入流字段对象.");
				return ;
			}

			String origin = dto.getOrigin();
			if (Utils.isEmpty(origin) || sdrMap.get(origin) == null || sdrMap.get(origin).getDataRelationship() == null) {
				return;
			}

			for (Relationship odr : sdrMap.get(origin).getDataRelationship()) {
				if (sdn.equals(odr.getEndNode())) {
					Relationship nr = RelationshipUtil.mergeRelationship(odr, dr);

					dr = nr;
					RelationshipUtil.addRelationshipDetail(dr,  "The relationship collapses to step " + origin);

					if (!dr.getStartNode().isStepOrEntryField() && !dr.getEndNode().isStepOrEntryField()) {
						results.add(dr);
						continue;
					}

					if (dr.getStartNode().isStepOrEntryField()) {
						collapseStepOrEntryStartNode(sdrMap, sdrMap.get(origin), dr, results);
					}

					if (dr.getEndNode().isStepOrEntryField()) {
						collapseStepOrEntryEndNode(sdrMap, sdrMap.get(origin), dr, results);
					}
				}
			}
		}
	}

	private void collapseStepOrEntryEndNode(HashMap<String, StepDataRelationship> sdrMap, StepDataRelationship sdr, Relationship dr, List<Relationship> results) {
		DataNode edn = dr.getEndNode();
		String fieldName = edn.getStepOrEntryField();
		if (!Utils.isEmpty(fieldName)) {
			StepFieldDto dto = sdr.getOutputStream().get(fieldName);
			if (dto == null) {
				//该节点不是流节点(数据库节点,系统节点等)
				dto = sdr.getInputStream().get(fieldName);
			}
			if(dto ==null) {
				//该节点不是流节点(数据库节点,系统节点等)
				trigger.flushLog("警告[4]:流名["+fieldName+"]未找到相应的输出流字段对象.");
				return ;
			}
			String origin = dto.getOrigin();
			if (Utils.isEmpty(origin) || sdrMap.get(origin) == null || sdrMap.get(origin).getDataRelationship() == null) {
				return;
			}

			for (Relationship odr : sdrMap.get(origin).getDataRelationship()) {
				if (edn.equals(odr.getStartNode())) {
					Relationship nr = RelationshipUtil.mergeRelationship( dr,odr);

					dr = nr;
					RelationshipUtil.addRelationshipDetail(dr, "The relationship collapses to step " + origin);

					if (!dr.getStartNode().isStepOrEntryField() && !dr.getEndNode().isStepOrEntryField()) {
						results.add(dr);
						continue;
					}

					if (dr.getStartNode().isStepOrEntryField()) {
						collapseStepOrEntryStartNode(sdrMap, sdrMap.get(origin), dr, results);
					}

					if (dr.getEndNode().isStepOrEntryField()) {
						collapseStepOrEntryEndNode(sdrMap, sdrMap.get(origin), dr, results);
					}
				}
			}
		}
	}

	private void replaceStepOrEntryNode(HashMap<String, StepDataRelationship> sdrMap) {
		sdrMap.values().forEach(sdr ->{

			sdr.getDataRelationship().forEach(dr -> {

				String fieldName = dr.getStartNode().getStepOrEntryField();
				if (!Utils.isEmpty(fieldName)) {
					//是start流节点
					StepFieldDto dto = sdr.getInputStream().get(fieldName);
					if (dto == null) {
						//理论上不会出现在这里,容错
						dto = sdr.getOutputStream().get(fieldName);
					}
					if(dto ==null) {
						//该节点不是流节点,理论不会出现(数据库节点,系统节点等)
						trigger.flushLog("警告[1]:流名["+fieldName+"]未找到相应的输入流字段对象.");
						return ;
					}

					String origin = dto.getOrigin();
					DataNode originDn = sdrMap.get(origin).findInFieldDataNode(fieldName);
					if (originDn == null) {
						//理论上不会出现在这里,容错
						originDn = sdrMap.get(origin).findOutFieldDataNode(fieldName);
					}

					if (originDn != null) {
						dr.setStartNode(originDn);
						RelationshipUtil.addRelationshipDetail(dr, "The start node is from " + origin);
					}
				}

				fieldName = dr.getEndNode().getStepOrEntryField();
				if (!Utils.isEmpty(fieldName)) {
					StepFieldDto dto = sdr.getOutputStream().get(fieldName);
					if (dto == null) {
						//理论上不会出现在这里,容错
						dto = sdr.getInputStream().get(fieldName);
					}
					if(dto ==null) {
						//该节点不是流节点,理论不会出现(数据库节点,系统节点等)
						trigger.flushLog("警告[2]:流名["+fieldName+"]未找到相应的输出流字段对象.");
						return ;
					}

					String origin = dto.getOrigin();
					DataNode originDn = sdrMap.get(origin).findOutFieldDataNode(fieldName);
					if (originDn == null) {
						//理论上不会出现在这里,容错
						originDn = sdrMap.get(origin).findInFieldDataNode(fieldName);
					}

					if (originDn != null) {
						dr.setEndNode(originDn);
						RelationshipUtil.addRelationshipDetail(dr,  "The end node relates to " + origin);
					}
				}

			});

		});
	}

	private void removeBadRelationships(AnalyzerReporter reporter) {
		Iterator<Relationship> itlist = reporter.getRelationships().iterator();
		while(itlist.hasNext()){
			Relationship r = itlist.next();
			boolean bad = false;
			if (r.getStartNode().isStepOrEntryField() || r.getEndNode().isStepOrEntryField()) {
				bad = true;
				reporter.getErrors().put("Relationship", "Bad relationship that contains data node with type: " + NodeType.STEP_OR_ENTRY);
				trigger.flushLog("警告:关系:开始节点["+r.getStartNode().getGuiKey()+"],结束节点["+r.getEndNode().getGuiKey()+"]关系异常.");
			} else if (RelationshipUtil.isSelfRelationship(r)) {
				bad = true;
				reporter.getErrors().put("Relationship", "Bad relationship that relates to itself["+r.getStartNode().getGuiKey()+"]");
			} else if(RelationshipUtil.isRepeatRelationship( reporter.getRelationships(), r,true)) {
				bad = true;
				reporter.getErrors().put("Relationship", "repeat relationship["+r.getStartNode().getGuiKey()+"]");
			}

			if (bad) {
				reporter.getBadRelationships().add(r);
				itlist.remove();
			}
		}

	}

	private void callOtherAnalyzersByOrder(HashMap<String, StepDataRelationship> sdrMap) {
		// TODO. 调用专门的步骤关系解析器，暂时不实现
	}

}
