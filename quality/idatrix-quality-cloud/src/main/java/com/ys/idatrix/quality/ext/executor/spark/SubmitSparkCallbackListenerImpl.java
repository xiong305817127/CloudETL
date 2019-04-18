/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.quality.ext.executor.spark;

import com.ys.idatrix.quality.logger.CloudLogger;
import com.ys.idatrix.transengine.service.SubmitSparkCallbackListener;
import com.ys.idatrix.transengine.vo.SparkEngineCallerResp;

/**
 * iDatrix trans-engine caller callback listener.
 * @author JW
 * @since 2017年7月13日
 *
 */
public class SubmitSparkCallbackListenerImpl  implements SubmitSparkCallbackListener {
	
	private SparkEngineCallerResp resp;
	public SparkEngineCallerResp getResp() {
		return this.resp;
	}
	public void setResp(SparkEngineCallerResp resp) {
		this.resp = resp;
	}

	public boolean isCallbackOK() {
		if (this.resp != null)
			return true;
		return false;
	}

	public void printResp() {
		CloudLogger.getInstance().debug("SubmitSparkCallbackListener" , "Resp: ", resp.getUserId(), resp.getName(), resp.getStatus());
	}

	@Override
	public void changed(SparkEngineCallerResp resp) {
		if (resp != null) {
			this.resp = resp;
			printResp();
		}
	}

}
