package com.idatrix.unisecurity.ranger.common;

public class RangerStaticInfo {
	
	public static String POLICY_NAME_PRE = "ranger_";
	
	//Ranger的地址
//	public static String RANGER_URL= "http://10.0.0.143:6080";
	//Ranger登录地址
	public static String RANGER_LOGIN_URL = "/j_spring_security_check";
	
	
	/************************Begin Repository REST接口************************* */
	//根据id，获取 Repository 
	public static String RANGER_GET_REPOSITORY =  "/service/public/api/repository";
	
	//获取所有的repository
	//需要注意无法获取Yarn
	public static String RANGER_SEARCH_REPOSITIORY = "/service/public/api/repository";

	public static String RANGER_SEARCH_ALL_REPOSITIORY =  "/service/plugins/services";
	/***********************  END *************************************/
	
	
	/************************ Begin 策略 API ************************************/
	public static String RANGER_POLICY =  "/service/plugins/policies/";
	/************************ END **************************************/

}
