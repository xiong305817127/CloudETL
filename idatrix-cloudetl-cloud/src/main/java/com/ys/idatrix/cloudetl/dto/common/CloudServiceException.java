/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.dto.common;

/**
 * Customized exception process class for cloud services.
 * @author JW
 * @since 2017年7月26日
 *
 */
public class CloudServiceException extends RuntimeException {

	private static final long serialVersionUID = -4325006554604213187L;

	public CloudServiceException(String message) {
		super(serviceErrorMessage(message));
	}

	public CloudServiceException(Throwable throwable) {
		super(throwable);
	}

	public CloudServiceException(Throwable throwable, String message) {
		super(throwable);
	}

	private static String serviceErrorMessage(String message)
	{
		String prefixStr = "发生错误，\n";
		String suffixStr = "\n请稍后再试或与管理员联系！";

		StringBuffer friendlyErrMsg = new StringBuffer("");

		friendlyErrMsg.append(prefixStr);

		friendlyErrMsg.append(message);

		friendlyErrMsg.append(suffixStr);

		return friendlyErrMsg.toString();
	}

}
