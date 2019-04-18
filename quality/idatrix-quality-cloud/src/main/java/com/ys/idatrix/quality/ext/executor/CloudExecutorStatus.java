/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.quality.ext.executor;

import com.ys.idatrix.quality.def.CloudMessage;

/**
 * Enumeration for types of execution status of transformations and jobs.
 * @author JW
 * @since 2017年7月19日
 *
 */
public enum CloudExecutorStatus {
	
	UNDEFINED(0, CloudMessage.get("Const.Executor.Status0")),
	WAITING(1, CloudMessage.get("Const.Executor.Status1")),
	COMPLETED(2, CloudMessage.get("Const.Executor.Status2")),
	COMPLETE_WITH_ERRORS(3, CloudMessage.get("Const.Executor.Status3")),
	STOPPED_WITH_ERRORS(3, "Stopped (with errors)"),
	STOPPED(4, CloudMessage.get("Const.Executor.Status4")),
	PAUSED(5, CloudMessage.get("Const.Executor.Status5")),
	TIMEOUT(6, CloudMessage.get("Const.Executor.Status6")),
	UNKNOWN(7, CloudMessage.get("Const.Executor.Status7")),
	FAILED(8, CloudMessage.get("Const.Executor.Status8")),
	HALTING(9, CloudMessage.get("Const.Executor.Status9")),
	INITIALIZING(10, CloudMessage.get("Const.Executor.Status10")),
	PREPARING(11, CloudMessage.get("Const.Executor.Status11")),
	RUNNING(12, CloudMessage.get("Const.Executor.Status12"));
	
	private int code;
	private String type;

	private CloudExecutorStatus(int code, String type) {
		this.code = code;
		this.type = type;
	}

	public int getCode() {
		return code;
	}

	public String getType() {
		return type;
	}
	
	public boolean matchCode(int code) {
		if (this.code == code)
			return true;
		else 
			return false;
	}
	
	public boolean matchType(String type) {
		return type.equals(this.type);
	}

	public static CloudExecutorStatus getStatusForType(String type) {
		for (CloudExecutorStatus execStatus : values()) {
			if (execStatus.getType().equals(type)) {
				return execStatus;
			}
		}
		return UNDEFINED;
	}

	public static CloudExecutorStatus getStatusForCode(int code) {
		for (CloudExecutorStatus execStatus : values()) {
			if (execStatus.getCode() == code) {
				return execStatus;
			}
		}
		return UNDEFINED;
	}
	
	public static String correctStatusType(String type) {
		return getStatusForType(type).getType();
	}
	
	public static String mergeStatusType(String...types) {
		int currentLevel = 0;
		for (int i = 0; i < types.length; i++) {
			int typeLevel = getStatusForType(types[i]).getCode();
			if (typeLevel > currentLevel) {
				currentLevel = typeLevel;
			}
		}
		return getStatusForCode(currentLevel).getType();
	}
	
	public static CloudExecutorStatus mergeStatus(CloudExecutorStatus...statuss) {
		int currentLevel = 0;
		for (int i = 0; i < statuss.length; i++) {
			int typeLevel = statuss[i].getCode();
			if (typeLevel > currentLevel) {
				currentLevel = typeLevel;
			}
		}
		return getStatusForCode(currentLevel);
	}
	
	public static boolean assertRunning(String type) {
		if (getStatusForType(type).getCode() >= 10 || getStatusForType(type).getCode() == 5 )
			return true;
		return false;
	}
	
	public static boolean assertSuccess(String type) {
		if (getStatusForType(type).getCode() == 2)
			return true;
		return false;
	}
	
	public static boolean assertError(String type) {
		if (getStatusForType(type).getCode() == 3
				|| getStatusForType(type).getCode() == 4
				|| getStatusForType(type).getCode() == 6
				|| getStatusForType(type).getCode() == 7
				|| getStatusForType(type).getCode() == 8
				|| getStatusForType(type).getCode() == 9)
			return true;
		return false;
	}

}
