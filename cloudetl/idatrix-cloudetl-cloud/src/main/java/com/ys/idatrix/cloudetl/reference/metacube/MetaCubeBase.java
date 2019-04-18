/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.reference.metacube;

import com.ys.idatrix.metacube.api.beans.ActionTypeEnum;

/**
 * Base wrapper for calling RPC APIs of MetaCube.
 * 
 * @author JW
 * @since 2017年6月22日
 *
 */
public class MetaCubeBase {

	public static final String METACUBE_SYSTEM = "CloudETL";

	// "HIVE" : "ysdbsitfc-h01:10000"
	protected final String META_KEY_HIVE = "HIVE";
	
	// "HBASE" : "jdbc:phoenix:ysdbsitfc-h01,ysdbsitfc-h02,ysdbsitfc-h03:2181"
	protected final String META_KEY_HBASE = "HBASE";
	
	protected final String META_KEY_ES = "ELASTICSEARCH";
	

	
	protected ActionTypeEnum readOrWriteAction(Boolean isRead ) {
		return isRead == null ? ActionTypeEnum.ALL : ( isRead ? ActionTypeEnum.READ : ActionTypeEnum.WRITE ) ;
	}


}
