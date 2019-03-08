package com.ys.idatrix.cloudetl.util;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.Lists;
import com.ys.idatrix.cloudetl.dto.step.parts.ConditionDto;
import com.ys.idatrix.cloudetl.subscribe.api.dto.parts.RowConditionDto;

public class SubcribeUtils {
	
	public static  final String NAME_PREFIX="SUB_";
	
	public static  final String DEFAULT_GROUP_NAME="default";
	
	public static  final String[] PRIORITY_GROUP_NAME= {"SUBSCRIBE_DB","SUBSCRIBE_FILE","SUBSCRIBE-EXCHANGE-DB","SUBSCRIBE-EXCHANGE-FILE","default"};
	
	
	/**
	 * 获取转换名
	 * @param user
	 * @param name
	 * @return
	 */
	public static String getTransName(String user , String name) {
		return NAME_PREFIX+name;
	}
	/**
	 * 获取调度名
	 * @param user
	 * @param name
	 * @return
	 */
	public static String getJobName(String user , String name) {
		return  NAME_PREFIX+name;
	}
	/**
	 * 根据订阅Id(调度名/转换名)获取任务名
	 * @param user
	 * @param subscribeId
	 * @return
	 */
	public static String getNameFromId( String user , String subscribeId ) {
		String name = null;
		Pattern p = Pattern.compile("^"+NAME_PREFIX+"(.*)$");
		Matcher m = p.matcher(subscribeId);
		if(m.find()){
			name = m.group(1);
		}
//		if(Utils.isEmpty(name)) {
//			p = Pattern.compile("^"+NAME_PREFIX+"(.*)$");
//			m = p.matcher(subscribeId);
//			if(m.find()){
//				name = m.group(1);
//			}
//		}
		return  name;
	}
	
	/**
	 * 判断是否是订阅任务
	 * @param user
	 * @param name
	 * @return
	 */
	public static Boolean isSubcribeJob(String user , String subscribeId) {
		String jobPrefix = NAME_PREFIX;
		return  subscribeId.startsWith(jobPrefix);
	}
	

	public static ConditionDto parseCondition(RowConditionDto condition) {
		if(condition == null ) {
			return null;
		}
		ConditionDto cd = new ConditionDto();;
		
		cd.setNegate( condition.isNegate());
		cd.setOperators( condition.getOperators());
		cd.setLeftvalue( condition.getLeftvalue());
		cd.setFunction( condition.getFunction());
		cd.setRightvalue( condition.getRightvalue());

		cd.setRightExactText( condition.getRightExactText());
		cd.setRightExactType( condition.getRightExactType());
		cd.setRightExactLength( condition.getRightExactLength());
		cd.setRightExactPrecision( condition.getRightExactPrecision());
		cd.setRightExactIsnull( condition.isRightExactIsnull());
		cd.setRightExactMask(condition.getRightExactMask());

		List<RowConditionDto> condis = condition.getConditions();
		if(condis != null && condis.size() >0 ) {
			List<ConditionDto> ccs = Lists.newArrayList()  ;
			for (int i = 0; i < condis.size(); i++) {
				RowConditionDto c = condis.get(i);
				ConditionDto frcd = parseCondition(c);
				ccs.add(frcd);
			}
			cd.setConditions(ccs);
		}
		
		return cd;
	}
	
}
