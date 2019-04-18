package com.ys.idatrix.quality.steps.common;

import org.pentaho.di.core.util.OsgiBundleUtils;
import org.pentaho.di.core.util.Utils;

import com.ys.idatrix.quality.steps.analysis.AnalysisStepHandler;
import com.ys.idatrix.quality.steps.redundance.RedundanceStepHandler;

public  enum NodeTypeEnum {

	//分析组件
	DATE(  AnalysisStepHandler.class),
	CERTIFICATES(  AnalysisStepHandler.class),
	NUMBER(  AnalysisStepHandler.class),
	CHARACTER(  AnalysisStepHandler.class),
	CUSTOM(  AnalysisStepHandler.class) ,
	//冗余率
	REDUNDANCE( RedundanceStepHandler.class) ; //冗余率
	
	
	//########################################## 属性 方法 ####################################################
	
	private Class<? extends NodeTypeStepHandler>  handlerClass ;
	
	NodeTypeEnum( Class<? extends NodeTypeStepHandler> handlerClass ) {
		this.handlerClass = handlerClass ;
	}

	public  Class<? extends NodeTypeStepHandler> getHandlerClass() {
		return  handlerClass ;
	}
	
	public NodeTypeStepHandler getHandlerInstance() {
		return (NodeTypeStepHandler) OsgiBundleUtils.newOsgiInstance(handlerClass, null);
	}
	
	
	//########################################## 静态 方法 ####################################################
	
	public static NodeTypeEnum getNodeTypeEnum(String nodeType) {
		if( Utils.isEmpty(nodeType) ) {
			return null ;
		}
		return  Enum.valueOf(NodeTypeEnum.class, nodeType);
	}
	
	//########################################## 类型域 列表 枚举  ####################################################
	
	public interface CommonTypeFieldEnum{
		
		String nodeName = "nodeName" ;
		String nodeType = "nodeType" ;
		
	}
	
	/**
	 * 分析组件 域列表
	 *
	 * @author XH
	 * @since 2019年1月16日
	 *
	 */
	 public enum AnalysisTypeFieldEnum implements CommonTypeFieldEnum  {
		 fieldName,value,referenceValue,result ;
	}
	 
	 /**
	  * 冗余率 域列表
	  * @author XH
	  * @since 2019年1月16日
	  *
	  */
	 public enum RedundanceTypeFieldEnum implements CommonTypeFieldEnum  {
	 	dataSource,fields,total,noRepeat,detailPath ;
	 }
	
}


