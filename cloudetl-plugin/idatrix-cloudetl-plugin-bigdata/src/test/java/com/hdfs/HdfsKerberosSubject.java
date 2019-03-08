package com.hdfs;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.security.UserGroupInformation;

import javax.security.auth.Subject;
import javax.security.auth.login.LoginException;

import java.io.IOException;

/**
 * Created by yangbo on 2017/1/18.
 */
public class HdfsKerberosSubject {
	//初始化conf配置，此处会读取hdfs-site.xml与core-site.xml文件
	public static Configuration conf = new Configuration(true);

	public static void test() throws IOException {
		String s = UserGroupInformation.getCurrentUser().getShortUserName();
		System.err.println(s);
		try {
			FileSystem fs = FileSystem.get(conf);
			FileStatus[] fileStatuses = fs.listStatus(new Path("/user/luoc"));
			for (FileStatus fileStatus : fileStatuses) {
				System.out.println(fileStatus.getPath().getName());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void initKerberos() {
		Subject subject = HDFSLoginContext.getSubject();

		conf.set("hadoop.security.authentication", "Kerberos");
		UserGroupInformation.setConfiguration(conf);
		try {
			UserGroupInformation.loginUserFromSubject(subject);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws LoginException, IOException {
		//在程序中进行kerberos认证后，当进程退出时，之前认证信息失效，用户如果需要继续访问
		//hdfs系统，需要重新进行kerberos认证
		initKerberos();
		test();
	}
}
