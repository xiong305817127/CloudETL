/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.quality.dto.step;

import java.util.List;

import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.util.OsgiBundleUtils;
import org.pentaho.di.core.util.StringUtil;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;

import com.ys.idatrix.quality.dto.codec.StepParameterCodec;
import com.ys.idatrix.quality.ext.CloudSession;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * step 详细信息
 * @author JW
 * @since 05-12-2017
 *
 */
@ApiModel("转换步骤信息")
public class StepDetailsDto {
	
	@ApiModelProperty("拥有者")
	private String owner;
    private String transName;
    private String group;
    private String stepName;
    private String newName;
    private String type;
    private String description;
    private boolean distributes =  false ;
    private boolean supportsErrorHandling =  false ;
    
    private Object stepParams;
    
    private String[] nextStepNames;
    private String[] prevStepNames;
    
	 public String getOwner() {
		 if( Utils.isEmpty( owner )) {
			 owner = CloudSession.getResourceUser() ;
		 }
		 return owner;
	 }
	 public void setOwner(String owner) {
		 this.owner = owner;
	 }
    
    public void setTransName(String transName) {
        this.transName = transName;
    }
    public String getTransName() {
    	if(Utils.isEmpty(group) && !Utils.isEmpty(transName) && transName.contains("/")) {
			group = transName.split("/", 2 )[0];
			transName = transName.split("/", 2 )[1];
		}
        return transName;
    }

    /**
	 * @return the group
	 */
	public String getGroup() {
		if(Utils.isEmpty(group) && !Utils.isEmpty(transName) && transName.contains("/")) {
			group = transName.split("/", 2 )[0];
			transName = transName.split("/", 2 )[1];
		}
		return group;
	}
	/**
	 * @param  设置 group
	 */
	public void setGroup(String group) {
		this.group = group;
	}
	public void setStepName(String stepName) {
        this.stepName = stepName;
    }
    public String getStepName() {
        return stepName;
    }
    
    public void setNewName(String newName) {
        this.newName = newName;
    }
    public String getNewName() {
        return newName;
    }

    public void setType(String type) {
        this.type = type;
    }
    public String getType() { 
        return type;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public String getDescription() {
        return description;
    }
    
    /* If appoint specific class, the request body can be parsed correctly!
    public void setStepParams(ScriptValueMod stepParams) {
    	this.stepParams = stepParams;
    }
    
    public ScriptValueMod getStepParams() {
    	return stepParams;
    }
    */

    /**
	 * @return the distributes
	 */
	public boolean isDistributes() {
		return distributes;
	}
	/**
	 * @param  设置 distributes
	 */
	public void setDistributes(boolean distributes) {
		this.distributes = distributes;
	}
	
	/**
	 * @return the supportsErrorHandling
	 */
	public boolean isSupportsErrorHandling() {
		return supportsErrorHandling;
	}
	/**
	 * @param  设置 supportsErrorHandling
	 */
	public void setSupportsErrorHandling(boolean supportsErrorHandling) {
		this.supportsErrorHandling = supportsErrorHandling;
	}
	
	public Object getStepParams() {
		return stepParams;
    }
	/**
     * Since step parameter will be in different type,
     *  we have to parse the request body per type.
     * @param stepParams
     */
    public void setStepParams(Object stepParams) {
    	this.stepParams = StepParameterCodec.parseParamObject(stepParams, this.type);
    }
    
    /**
	 * @return nextStepNames
	 */
	public String[] getNextStepNames() {
		return nextStepNames;
	}
	/**
	 * @param nextStepNames 要设置的 nextStepNames
	 */
	public void setNextStepNames(String[] nextStepNames) {
		this.nextStepNames = nextStepNames;
	}
	
	/**
	 * @return prevStepNames
	 */
	public String[] getPrevStepNames() {
		return prevStepNames;
	}
	/**
	 * @param prevStepNames 要设置的 prevStepNames
	 */
	public void setPrevStepNames(String[] prevStepNames) {
		this.prevStepNames = prevStepNames;
	}
    
    public void encodeStepParams(StepMeta stepMeta) throws Exception {
    	this.stepParams = StepParameterCodec.encodeParamObject(stepMeta, stepMeta.getTypeId());
    	
    	String templeteFile = stepMeta.getAttribute("idatrix","templeteFile");
    	if(!StringUtil.isEmpty(templeteFile)){
    		OsgiBundleUtils.setOsgiField(stepParams, "templeteFile", templeteFile,  true);
    	}
    }
    
    public void decodeParameterObject(StepMeta stepMeta, List<DatabaseMeta> databases, TransMeta transMeta) throws Exception {
    	StepParameterCodec.decodeParameterObject(stepMeta, this.stepParams, databases, transMeta, stepMeta.getTypeId());
    	
    	String templeteFile = (String) OsgiBundleUtils.getOsgiField(stepParams, "templeteFile", true);
    	String templeteFile_old = stepMeta.getAttribute("idatrix","templeteFile");
    	if(!StringUtil.isEmpty(templeteFile_old) || !StringUtil.isEmpty(templeteFile) ){
    		 stepMeta.setAttribute("idatrix","templeteFile",templeteFile);
    	}
    }

}
