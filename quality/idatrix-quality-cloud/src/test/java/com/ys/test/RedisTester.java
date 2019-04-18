/**
 * 云化数据集成系统 
 * iDatrxi CloudETL
 */
package com.ys.test;

import com.idatrix.unisecurity.common.utils.JsonUtils;
import com.ys.idatrix.quality.ext.utils.RedisUtil;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * RedisTester <br/>
 * @author JW
 * @since 2017年11月8日
 * 
 */
public class RedisTester {

	@SuppressWarnings({ "unused", "resource" })
	public static void main(String[] args) {
		ApplicationContext application=new ClassPathXmlApplicationContext("file:C://Users//Administrator//Desktop//quality//idatrix-quality-cloud//web//WEB-INF//idatrix-application.xml");
		RedisUtil.set("oyr_test1", JsonUtils.toJson(new Date()));
		Object oyr_test1 = RedisUtil.get("oyr_test1");
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		System.out.println(dateFormat.format(JsonUtils.toJavaBean((String) oyr_test1, Date.class)));
	}

}
