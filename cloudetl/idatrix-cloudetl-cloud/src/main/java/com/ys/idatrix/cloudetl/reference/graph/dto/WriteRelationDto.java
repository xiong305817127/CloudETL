package com.ys.idatrix.cloudetl.reference.graph.dto;

import java.util.List;

import com.google.common.collect.Lists;
import com.ys.idatrix.cloudetl.toolkit.domain.DataNode;
import com.ys.idatrix.cloudetl.toolkit.domain.Relationship;

public class WriteRelationDto {

	private DataNode startParent ;
	private DataNode endParent ;
	
	private List<Relationship> relationships ;
	
	/**
	 * @param startParent
	 * @param endParent
	 */
	public WriteRelationDto(DataNode startParent, DataNode endParent) {
		super();
		this.startParent = startParent;
		this.endParent = endParent;
	}

	public DataNode getStartParent() {
		return startParent;
	}
	
	public void setStartParent(DataNode startParent) {
		this.startParent = startParent;
	}
	
	public DataNode getEndParent() {
		return endParent;
	}
	
	public void setEndParent(DataNode endParent) {
		this.endParent = endParent;
	}
	
	public List<Relationship> getRelationships() {
		return relationships;
	}

	public void setRelationships(List<Relationship> relationships) {
		this.relationships = relationships;
	}

	public WriteRelationDto addRelationships( Relationship r) {
		if( relationships == null ) {
			relationships = Lists.newArrayList() ;
		}
		relationships.add(r);
		return this ;
	}

	@Override
	public String toString() {
		return "start: "+startParent.getGuiKey()+" ,\n end: "+endParent.getGuiKey()+" ,\n relationships:  "+relationships.toString() +" \n";
	}

	
}
