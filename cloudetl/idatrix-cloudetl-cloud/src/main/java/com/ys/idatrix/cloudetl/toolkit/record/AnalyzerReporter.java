/**
 * 云化数据集成系统 
 * iDatrxi CloudETL
 */
package com.ys.idatrix.cloudetl.toolkit.record;

import java.util.List;
import java.util.Map;

import com.ys.idatrix.cloudetl.toolkit.domain.DataNode;
import com.ys.idatrix.cloudetl.toolkit.domain.Relationship;

/**
 * AnalyzerReporter <br/>
 * @author JW
 * @since 2018年1月8日
 * 
 */
public class AnalyzerReporter {
	
	private List<DataNode> dataNodes;
	
	private List<DataNode> badDataNodes;
	
	private List<Relationship> relationships;
	
	private List<Relationship> badRelationships;
	
	private long elapsedTime;
	
	private long analyzerScore;
	
	private boolean written;
	
	private Map<String, Object> errors;

	/**
	 * @return dataNodes
	 */
	public List<DataNode> getDataNodes() {
		return dataNodes;
	}

	/**
	 * @param dataNodes 要设置的 dataNodes
	 */
	public void setDataNodes(List<DataNode> dataNodes) {
		this.dataNodes = dataNodes;
	}

	/**
	 * @return badDataNodes
	 */
	public List<DataNode> getBadDataNodes() {
		return badDataNodes;
	}

	/**
	 * @param badDataNodes 要设置的 badDataNodes
	 */
	public void setBadDataNodes(List<DataNode> badDataNodes) {
		this.badDataNodes = badDataNodes;
	}

	/**
	 * @return relationships
	 */
	public List<Relationship> getRelationships() {
		return relationships;
	}

	/**
	 * @param relationships 要设置的 relationships
	 */
	public void setRelationships(List<Relationship> relationships) {
		this.relationships = relationships;
	}

	/**
	 * @return badRelationships
	 */
	public List<Relationship> getBadRelationships() {
		return badRelationships;
	}

	/**
	 * @param badRelationships 要设置的 badRelationships
	 */
	public void setBadRelationships(List<Relationship> badRelationships) {
		this.badRelationships = badRelationships;
	}

	/**
	 * @return elapsedTime
	 */
	public long getElapsedTime() {
		return elapsedTime;
	}

	/**
	 * @param elapsedTime 要设置的 elapsedTime
	 */
	public void setElapsedTime(long elapsedTime) {
		this.elapsedTime = elapsedTime;
	}

	/**
	 * @return analyzerScore
	 */
	public long getAnalyzerScore() {
		return analyzerScore;
	}

	/**
	 * @param analyzerScore 要设置的 analyzerScore
	 */
	public void setAnalyzerScore(long analyzerScore) {
		this.analyzerScore = analyzerScore;
	}

	/**
	 * @return written
	 */
	public boolean isWritten() {
		return written;
	}

	/**
	 * @param written 要设置的 written
	 */
	public void setWritten(boolean written) {
		this.written = written;
	}
	
	public long getNumberOfDataNodes() {
		if (this.dataNodes != null) {
			return this.dataNodes.size();
		}
		return 0;
	}
	
	public long getNumberOfRelationship() {
		if (this.relationships != null) {
			return this.relationships.size();
		}
		return 0;
	}

	/**
	 * @return errors
	 */
	public Map<String, Object> getErrors() {
		return errors;
	}

	/**
	 * @param errors 要设置的 errors
	 */
	public void setErrors(Map<String, Object> errors) {
		this.errors = errors;
	}

}
