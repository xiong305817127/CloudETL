package com.idatrix.resource.common.utils;

import java.util.ArrayList;
import java.util.List;

public final class CommonConstants {
	private CommonConstants() {
	};

	public static final int FLAG_Y = 1;
	public static final int FLAG_N = 0;

	public static final String STATUS_Y = "Y";
	public static final String STATUS_N = "N";

	public static final String SERVICE_TYPE_SOAP = "SOAP";
	public static final String SERVICE_TYPE_RESTFUL = "RESTful";

	public static final Integer SUCCESS_VALUE = 200;						//调用成功
	public static final Integer FAILURE_VALUE = 300;						//调用失败

	//ERROR_CODE简写为EC
	public static final Integer EC_NULL_VALUE = 301;						//当前值为非空字段，未填
	public static final Integer EC_EXISTED_VALUE = 303;						//当前值已存在，无法重复添加
	public static final Integer EC_INCORRECT_VALUE = 305;					//当前值填写错误
	public static final Integer EC_OVERLENGTH_VALUE = 307;					//当前值长度超过限定范围
	public static final Integer EC_NOT_EXISTED_VALUE = 309;					//当前项不存在

	public static final Integer EC_HDFS_ERROR = 302; 						//上传至HDFS文件系统出现异常错误
	public static final Integer EC_OVER_TIME_ERROR = 304;					//超时错误

	public static final Integer EC_UNEXPECTED = 409;						//非常见异常或报错

	public static final String REQUEST_GET_METHOD = "GET";					//HTTP REQUEST GET请求
	public static final String REQUEST_POST_METHOD = "POST";				//HTTP REQUEST POST请求

	public static final String HTTP_REQUEST_SUCCESS = "SUCCESS";			//HTTP 请求成功
	public static final String HTTP_REQUEST_FAILURE = "FAILURE"; 			//HTTP 请求失败

    public static final String NONE_STATUS = "NONE_STATUS";                  //无状态
	public static final String WAIT_IMPORT = "WAIT_IMPORT";					//入库状态 等待入库
	public static final String IMPORTING = "IMPORTING";						//入库状态 入库中
	public static final String IMPORT_COMPLETE = "IMPORT_COMPLETE";			//入库状态 已入库
	public static final String STOP_IMPORT = "STOP_IMPORT";					//终止状态 终止入库
	public static final String IMPORT_ERROR = "IMPORT_ERROR";				//入库状态 入库失败


	public static final String DATA_TYPE_DB = "DB";							//数据上报类型 数据库类
    public static final String DATA_TYPE_FILE = "FILE";						//数据上报类型 文件类


    public static final String TASK_WAIT = "waiting";					//等待
    public static final String TASK_RUNNING = "running";				//执行中
    public static final String TASK_ERROR = "error";					//错误
    public static final String TASK_WARN = "warn";						//告警

    public static final String PREFIX_UPLOAD = "UP";                    //上传前缀
    public static final String PREFIX_SUBSCRIBE = "SUB";                //订阅前缀
    public static final String PREFIX_ETL = "SUB_";                //订阅前缀

	/**************************************************************************************************/
	//MYSQL 常用数据类型														//KETTLE 对应数据类型
	public static final String SQL_VARCHAR = "varchar";						//String

	public static final String SQL_TINYINT = "tinyint";						//Integer
	public static final String SQL_INTEGER = "int";							//Integer
	public static final String SQL_BIGINT = "bigint";						//BigNumber

	public static final String SQL_DATE = "date";							//Date
	public static final String SQL_DATETIME = "datetime";					//Date

	public static final String SQL_FLOAT = "float";							//Number
	public static final String SQL_NUMBER = "number";						//Number
	public static final String SQL_DOUBLE = "double";						//BigNumber
	public static final String SQL_BOOLEAN = "boolean";						//Boolean
	public static final String SQL_BLOB = "blob";							//Binary

	public static final String SQL_TEXT = "text";							//String
	public static final String SQL_CHAR = "char";							//String
	public static final String SQL_SMALLINT = "smallint";					//Integer
	public static final String SQL_MEDIUMINT = "mediumint";					//Integer
	/**************************************************************************************************/


	//电子表格类
	public static final String SUFFIX_XLS = "xls";		//EXCEL
	public static final String SUFFIX_XLSX = "xlsx";	//EXCEL
	public static final String SUFFIX_ET = "et";		//WPS表格
	public static final String SUFFIX_ETT = "ett";		//WPS表格

	public static final List<String> SPREADSHEET = new ArrayList<String>(); //电子表格类集合
	static {
		SPREADSHEET.add(SUFFIX_XLS);
		SPREADSHEET.add(SUFFIX_XLSX);
		SPREADSHEET.add(SUFFIX_ET);
		SPREADSHEET.add(SUFFIX_ETT);
	}

	//电子文档类
	public static final String SUFFIX_OFD = "ofd"; 		//中国国内标准版式文件标准
	public static final String SUFFIX_WPS = "wps";		//WPS文档
	public static final String SUFFIX_XML = "xml";		//XML
	public static final String SUFFIX_TXT = "txt";		//TXT
	public static final String SUFFIX_DOC = "doc";		//WORD
	public static final String SUFFIX_DOCX = "docx";	//WORD
	public static final String SUFFIX_HTML = "html";	//HTML
	public static final String SUFFIX_PDF = "pdf";		//PDF
    public static final String SUFFIX_PPT = "ppt";		//PPT
    public static final String SUFFIX_PPTX = "pptx";		//PPT

	public static final List<String> DOCUMENT = new ArrayList<String>(); //电子文档类集合
	static {
		DOCUMENT.add(SUFFIX_OFD);
		DOCUMENT.add(SUFFIX_WPS);
		DOCUMENT.add(SUFFIX_XML);
		DOCUMENT.add(SUFFIX_TXT);
		DOCUMENT.add(SUFFIX_DOC);
		DOCUMENT.add(SUFFIX_DOCX);
		DOCUMENT.add(SUFFIX_HTML);
		DOCUMENT.add(SUFFIX_PDF);
		DOCUMENT.add(SUFFIX_PPT);
        DOCUMENT.add(SUFFIX_PPTX);
	}

	//电子图片类
	public static final String SUFFIX_JPG = "jpg"; 		//JPG
	public static final String SUFFIX_JPEG = "jpeg"; 	//JPEG
	public static final String SUFFIX_PNG = "png";		//PNG
	public static final String SUFFIX_GIF = "gif";		//GIF
	public static final String SUFFIX_BMP = "bmp";		//BMP

	public static final List<String> IMAGE = new ArrayList<String>(); //电子图片类集合
	static {
		IMAGE.add(SUFFIX_JPG);
		IMAGE.add(SUFFIX_JPEG);
		IMAGE.add(SUFFIX_PNG);
		IMAGE.add(SUFFIX_GIF);
		IMAGE.add(SUFFIX_BMP);
	}

	//流媒体类
	public static final String SUFFIX_SWF = "swf"; 		//SWF
	public static final String SUFFIX_RM = "rm"; 		//RM
	public static final String SUFFIX_MPG = "mpg";		//MPG

	public static final List<String> STREAM = new ArrayList<String>(); //电子图片类集合
	static {
		STREAM.add(SUFFIX_SWF);
		STREAM.add(SUFFIX_RM);
		STREAM.add(SUFFIX_MPG);
	}
}
