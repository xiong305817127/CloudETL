/**
 * 云化数据集成系统 
 * iDatrxi CloudETL
 */
package com.ys.idatrix.cloudetl.toolkit.domain.property;

import java.io.Serializable;
import com.ys.idatrix.cloudetl.toolkit.common.RelationshipType;

/**
 * RelationshipProperty <br/>
 * @author JW
 * @since 2017年12月19日
 * 
 */
public class RelationshipProperty  extends BaseProperty  implements Serializable{

	private static final long serialVersionUID = -5812609451279779804L;

	private RelationshipType type;
	
	private double rating = 0.0;
	
	private String details;

	public RelationshipProperty(String name) {
		super(name);
	}
	
	public RelationshipType getType() {
		return type;
	}

	public void setType(RelationshipType type) {
		this.type = type;
	}

	public double getRating() {
		return rating;
	}

	public void setRating(double rating) {
		this.rating = rating;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}


}
