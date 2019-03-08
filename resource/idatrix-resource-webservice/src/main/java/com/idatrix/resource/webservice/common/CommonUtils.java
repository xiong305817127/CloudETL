package com.idatrix.resource.webservice.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.UUID;

public final class CommonUtils {
	private static final Logger LOG = LoggerFactory.getLogger(CommonUtils.class);

	public static boolean isEmptyStr(String str) {
		if (str == null || str.trim().equals(""))
			return true;
		else
			return false;
	}

	public static boolean isEmptyLongValue(Long value) {
		if (value == null || value == 0L)
			return true;
		else
			return false;
	}

	/**
	 * 由于采用UTF-8的编码格式，且MYSQL高版本中VARCHAR(20)，允许保存20个汉字
	 * 因此可以直接采用字符串的实际长度来进行比较
	 * @param str
	 * @param maxLength
	 * @return
	 */
	public static boolean isOverLimitedLength(String str, int maxLength) {
		int strLength = 0;

		if (str == null)
			strLength = 0;
		else {
			strLength = str.length();
		}

		return (maxLength - strLength < 0);
	}

	/*生成随机UUID*/
	public static String generateUUID() {
		UUID uuid= UUID.randomUUID();
		String str = uuid.toString();
		String uuidStr=str.replace("-", "");
		return uuidStr;
	}

	/*对不足六位的顺序号进行补齐操作*/
	public static String formatSeqNum (Long originalNum) {

		StringBuffer sb = new StringBuffer();

		String origin = String.valueOf(originalNum);

		int originSize = origin.length();

		for (int i = 0; i < 8 - originSize; i++) {
			sb.append("0");
		}

		sb.append(origin);

		return sb.toString();
	}

	/**
	 * 根据数据库中的MySQL中的常用字段类型, 转化为Kettle中的数据类型
	 * @param colType MySQL数据类型
	 * @return
	 */
	public static String convertToETLColType(String colType) {
		String convertedType = "String";
//		Kettle VS Java
//		STRING : String.class
//		NUMBER : Double.class
//		INTEGER : Long.class
//		DATE : Date.class
//		BIGNUMBER : BigDecimal.class
//		BOOLEAN : Boolean.class
//		BINARY : byte[].class

//		"Number", "String", "Date", "Boolean", "Integer", "BigNumber", "Serializable", "Binary", "Timestamp",
// 		"Internet Address"

		if (colType.contains(CommonConstants.SQL_VARCHAR) || colType.contains(CommonConstants.SQL_TEXT)
				|| colType.contains(CommonConstants.SQL_CHAR)) {
			convertedType = "String";
		} else if (colType.contains(CommonConstants.SQL_TINYINT) || colType.contains(CommonConstants.SQL_SMALLINT)
				|| colType.contains(CommonConstants.SQL_INTEGER) || colType.contains(CommonConstants.SQL_MEDIUMINT)) {
			convertedType = "Integer";
		} else if (colType.contains(CommonConstants.SQL_BIGINT) || colType.contains(CommonConstants.SQL_DOUBLE) ) {
			convertedType = "BigNumber";
		} else if (colType.contains(CommonConstants.SQL_FLOAT) || colType.contains(CommonConstants.SQL_NUMBER)) {
			convertedType = "Number";
		} else if (colType.contains(CommonConstants.SQL_DATE) || colType.contains(CommonConstants.SQL_DATETIME)) {
			convertedType = "Date";
		} else if (colType.contains(CommonConstants.SQL_BOOLEAN)) {
			convertedType = "Boolean";
		} else if (colType.contains(CommonConstants.SQL_BLOB)) {
			convertedType = "Binary";
		}

		return convertedType;
	}

	public static String convertStatus(String resultStatus) {
		if (!CommonUtils.isEmptyStr(resultStatus)) {

			if (resultStatus.equals("Finished"))
				return CommonConstants.IMPORT_COMPLETE;
			else if (resultStatus.equals("Stopped"))
				return CommonConstants.STOP_IMPORT;
			else if (resultStatus.equals("Waiting") || resultStatus.equals("Initializing")
					|| resultStatus.equals("Preparing executing") || resultStatus.equals("Running"))
				return CommonConstants.IMPORTING;
			else
				return CommonConstants.IMPORT_ERROR;
		}

		return CommonConstants.IMPORT_ERROR;
	}

	public static int calculateFileSizeByMB(Long fileSize) {
		int size = (int) Math.ceil((double)fileSize / (1024 * 1024));

		return size;
	}

	public static String getFileSizeStr(Long fileSize) {
		if (fileSize >= 1024) {
			double size = (double)fileSize / (1024 * 1024);
			BigDecimal b = new BigDecimal(size);
			size = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			return size + "MB";
		} else
			return fileSize + "KB";
	}
}
