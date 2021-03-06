package com.idatrix.resource.common.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

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

	public static String generateETLTaskNum(Long seqNum) {
		StringBuffer sb = new StringBuffer();

		return (sb.append(CommonConstants.PREFIX_UPLOAD).append(CommonUtils.formatSeqNum(seqNum))).toString();
	}

	public static String getWSDLContentsByRemoteAddress(String url) {
		String message = "";
        LOG.info("HttpClient 请求: " + url );
		try {
			HttpEntity entity = HttpUtils.getRequestEntity(url, CommonConstants.REQUEST_GET_METHOD);
			if (entity != null)
				message = EntityUtils.toString(entity, "utf-8");
		} catch (IOException io) {
			io.printStackTrace();
		}
		return message;
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
		}else if(colType.startsWith(CommonConstants.SQL_INTEGER)){  //增加处理不然为bigint类型只能按照Integer处理 robin 20180831
            convertedType = "Integer";
        }else if (colType.contains(CommonConstants.SQL_TINYINT) || colType.contains(CommonConstants.SQL_SMALLINT)
				|| colType.contains(CommonConstants.SQL_MEDIUMINT)) {
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

    /*获取最近几个月的yyyyMM 月份数*/
    public static List<String> getRecentMonthStr(int months){
        List<String> monthStr = new ArrayList<String>();
        if(months<=0){
            return monthStr;
        }
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
        Date date = new Date();
        String dateString = sdf.format(cal.getTime());

        for (int index = 0; index<months; index++) {
            dateString = sdf.format(cal.getTime());
            monthStr.add(dateString);
            cal.add(Calendar.MONTH, -1);
        }
        Collections.reverse(monthStr); //倒序一下
        return monthStr;
    }

    /**
     * 获取最近 days天日期时间
     * @param days
     * @return
     */
    public static List<String> getRecentDayList(int days){
        List<String> dayList = new ArrayList<String>();
        if(days<=0){
            return dayList;
        }
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String dateString = sdf.format(cal.getTime());

        for (int index = 0; index<days; index++) {
            dateString = sdf.format(cal.getTime());
            dayList.add(dateString);
            cal.add(Calendar.DAY_OF_MONTH, -1);
        }
        Collections.reverse(dayList); //倒序一下
        return dayList;
    }

    /**
     * 获取最近 从startTime到endTime时间
     * @param startTime
     * @param endTime
     * @return
     */
    public static List<String> getRecentDayList(Date startTime, Date endTime){
        List<String> dayList = new ArrayList<String>();
        if(startTime.after(endTime)){
            return null;
        }
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = startTime;
        cal.setTime(date);
        String dateString = sdf.format(cal.getTime());

        while(cal.getTime().before(endTime)){
            dateString = sdf.format(cal.getTime());
            dayList.add(dateString);
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }
        return dayList;
    }

    public static String formatMonthStr(String month) {
        String yearStr = month.substring(0,4);
        String monthStr = month.substring(4, month.length());

        StringBuffer sb = new StringBuffer();

        return sb.append(yearStr).append("年").append(monthStr).append("月").toString();
    }

	public static String convertStatus(String resultStatus) {
//		if (!CommonUtils.isEmptyStr(resultStatus)) {
//
//			if (resultStatus.equals("Finished"))
//				return CommonConstants.IMPORT_COMPLETE;
//			else if (resultStatus.equals("Stopped"))
//				return CommonConstants.STOP_IMPORT;
//			else if (resultStatus.equals("Waiting") || resultStatus.equals("Initializing")
//					|| resultStatus.equals("Preparing executing") || resultStatus.equals("Running"))
//				return CommonConstants.IMPORTING;
//			else
//				return CommonConstants.IMPORT_ERROR;
//		}
//
//		return CommonConstants.IMPORT_ERROR;

        //修改原因：增加其它状态时候都会被当做 IMPORT_ERROR进行处理
        if (!CommonUtils.isEmptyStr(resultStatus)) {

            if (resultStatus.equals("Finished"))
                return CommonConstants.IMPORT_COMPLETE;
            else if (resultStatus.equals("Stopped"))
                return CommonConstants.STOP_IMPORT;
            else if (resultStatus.equals("Waiting") || resultStatus.equals("Initializing")
                    || resultStatus.equals("Preparing executing") || resultStatus.equals("Running"))
                return CommonConstants.IMPORTING;
            else if(StringUtils.equals(resultStatus, "Finished (with errors)") ||
                    StringUtils.equals(resultStatus, "Failed") ||
                    StringUtils.equals(resultStatus, "Timeout") ||
                    StringUtils.equals(resultStatus, "Unknown"))
                return CommonConstants.IMPORT_ERROR;
            else{
                return CommonConstants.NONE_STATUS;
            }
        }
        return CommonConstants.NONE_STATUS;
	}

	/*
	* 熊汉 ETL 任务状态:
                # Executor Status
                #Const.Executor.Status0=Waiting   #
                #Const.Executor.Status1=Failed
                #Const.Executor.Status2=Finished  #
                #Const.Executor.Status3=Finished (with errors)  #
                #Const.Executor.Status4=Stopped   #
                #Const.Executor.Status5=Paused
                #Const.Executor.Status6=Running   #
                #Const.Executor.Status7=Initializing  #
                #Const.Executor.Status8=Timeout
                #Const.Executor.Status9=Unknown
                #Const.Executor.Status10=Preparing executing  #
                #Const.Executor.Status11=Halting    //无具体状态处理

    熊汉转换新增 任务状态:
    新增两个  SingleStart   SingleEnd
*/
    public static String convertLocalStatus(String resultStatus) {

        String result = CommonConstants.NONE_STATUS;
	    if(StringUtils.isNotEmpty(resultStatus)){
            if(StringUtils.equals(resultStatus, "Waiting") ||
                StringUtils.equals(resultStatus, "Initializing") ||
                    StringUtils.equals(resultStatus, "Preparing executing") ||
                        StringUtils.equals(resultStatus, "Running")){
                result = CommonConstants.IMPORTING;
            }else if(StringUtils.equals(resultStatus, "SingleStart")){
                result = CommonConstants.IMPORTING;
            }else if(StringUtils.equals(resultStatus, "SingleEnd")){
                result = CommonConstants.IMPORT_COMPLETE;
            }else if(StringUtils.equals(resultStatus, "Finished")){
                result = CommonConstants.IMPORT_COMPLETE;
            }else if(StringUtils.equals(resultStatus, "Stopped")) {
                result = CommonConstants.STOP_IMPORT;
            }else if(StringUtils.equals(resultStatus, "Finished (with errors)") ||
                StringUtils.equals(resultStatus, "Failed") ||
                    StringUtils.equals(resultStatus, "Timeout") ||
                        StringUtils.equals(resultStatus, "SingleEndError") ||
                            StringUtils.equals(resultStatus, "Unknown")){
                result = CommonConstants.IMPORT_ERROR;
            }
        }
        return result;
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
//        return fileSize.toString();
	}

    public static void setObjectByField(Object object, String importFieldName, String value) throws Exception {
        // 获得对象的类型
        Class<?> classType = object.getClass();
//        LOG.info("Class:" + classType.getName());

        // 获得对象的所有属性
        Field fields[] = classType.getDeclaredFields();
        Field field = null;
        for (int i = 0; i < fields.length; i++) {
            if (StringUtils.equals(fields[i].getName(), importFieldName)) {
                field = fields[i];
                break;
            }
        }
        if(field==null){
            throw new Exception(classType.getName()+" 类中不存在 "+importFieldName+" 属性");
        }
        String fieldName = field.getName();
        Class fieldType = field.getType();
        String fieldTypeName = fieldType.getName();
        Object objValue = null;
        if(StringUtils.equals(fieldTypeName, Long.class.getName())){
            objValue = Long.valueOf(value);
        }else if(StringUtils.equals(fieldTypeName, String.class.getName())){
            objValue = value;
        }else if(StringUtils.equals(fieldTypeName,Boolean.class.getName())){
            objValue = Boolean.valueOf(value);
        }else if(StringUtils.equals(fieldTypeName,long.class.getName())) {
            objValue = Long.valueOf(value).longValue();
        }else if(StringUtils.equals(fieldTypeName,int.class.getName())) {
            objValue = Integer.valueOf(value).intValue();
        }else if(StringUtils.equals(fieldTypeName, Date.class.getName())){
            objValue = DateTools.parseDate(value);
        }
//        LOG.info("name====={}",fieldName);
//        LOG.info("type====={}",fieldTypeName);
        String firstLetter = fieldName.substring(0, 1).toUpperCase();
        // 获得和属性对应的getXXX()方法的名字
        //String getMethodName = "get" + firstLetter + fieldName.substring(1);
        // 获得和属性对应的setXXX()方法的名字
        String setMethodName = "set" + firstLetter + fieldName.substring(1);

        // 获得和属性对应的getXXX()方法
        //Method getMethod = classType.getMethod(getMethodName, new Class[]{});
        // 获得和属性对应的setXXX()方法
        Method setMethod = classType.getMethod(setMethodName, new Class[]{field.getType()});

        // 调用原对象的getXXX()方法
        //Object value = getMethod.invoke(object, new Object[]{});
        //System.out.println("value===="+value);
        //System.out.println(fieldName + ":" + value);
        // 调用拷贝对象的setXXX()方法
        setMethod.invoke(object, new Object[]{objValue});
    }


    public static Object getObjectByField(Object object, String importFieldName) throws Exception {
        // 获得对象的类型
        Class<?> classType = object.getClass();

        // 获得对象的所有属性
        Field fields[] = classType.getDeclaredFields();
        Field field = null;
        for (int i = 0; i < fields.length; i++) {
            if (StringUtils.equals(fields[i].getName(), importFieldName)) {
                field = fields[i];
                break;
            }
        }
        if(field==null){
            throw new Exception(classType.getName()+" 类中不存在 "+importFieldName+" 属性");
        }
        String fieldName = field.getName();
        Class fieldType = field.getType();
        String fieldTypeName = fieldType.getName();
        Object objValue = null;

//        LOG.info("name====={}",fieldName);
//        LOG.info("type====={}",fieldTypeName);
        String firstLetter = fieldName.substring(0, 1).toUpperCase();
        // 获得和属性对应的getXXX()方法的名字
        String getMethodName = "get" + firstLetter + fieldName.substring(1);
        // 获得和属性对应的setXXX()方法的名字
        String setMethodName = "set" + firstLetter + fieldName.substring(1);

        // 获得和属性对应的getXXX()方法
        Method getMethod = classType.getMethod(getMethodName, new Class[]{});
        // 获得和属性对应的setXXX()方法
        Method setMethod = classType.getMethod(setMethodName, new Class[]{field.getType()});

        // 调用原对象的getXXX()方法
        Object value = getMethod.invoke(object, new Object[]{});
        //System.out.println("value===="+value);
        //System.out.println(fieldName + ":" + value);
        // 调用拷贝对象的setXXX()方法
        //setMethod.invoke(object, new Object[]{objValue});
        return value;
    }

    /**
     * 将变量中原有数值增加 value给定值
     * @param object
     * @param importFieldName
     * @param value
     * @throws Exception
     */
    public static void refreshAddValue(Object object, String importFieldName, Long value)throws Exception{
        if(importFieldName !=null && StringUtils.isNotEmpty(importFieldName) && value!=null) {
            Object oldValue = CommonUtils.getObjectByField(object, importFieldName);
            Long oldSaveValue = oldValue==null?0L:(Long)oldValue;
            Long addValue = Long.valueOf(value)+oldSaveValue;
            CommonUtils.setObjectByField(object, importFieldName, addValue.toString());
        }
    }


}
