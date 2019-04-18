/**
 * 云化数据集成系统 
 * iDatrxi CloudETL
 */
package com.ys.test;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * FileTester <br/>
 * @author JW
 * @since 2017年10月19日
 * 
 */
public class FileTester {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		FileOutputStream outSTr = null;
		BufferedOutputStream buff = null;
		
		System.out.println("JVM运行时所使用的编码: " + System.getProperty("file.encoding"));
        System.out.println(System.getProperty("file.encoding"));
        
        Charset charset = Charset.defaultCharset();
        System.out.println("JVM字符集(Default)：" + charset.name());
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(baos);
        System.out.println("JVM字符集(Writer)：" + writer.getEncoding());
        try {
			writer.close();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		
		System.out.println("Parameters: " + Arrays.toString(args) + "\n");
		
		// Test01
		System.out.println("Test_1：新建一个文件");
		File file1 = new File("file_测试文件1.txt");
		
		try {
			file1.createNewFile();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		// Test02
		System.out.println("\nTest_2：输出到文本文件，测试是否乱码？");

		String path = args.length > 0 ? args[0] + "_输出.txt" : "这是一个空文件.txt";
		StringBuffer sb;
		
		try {
			outSTr = new FileOutputStream(new File(path));
			buff = new BufferedOutputStream(outSTr);

			sb = new StringBuffer();
			sb.append("期刊名称：" + (args.length > 1 ? args[1] : "空"));
			buff.write(sb.toString().getBytes("UTF-8"));

			buff.flush();
			buff.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				buff.close();
				outSTr.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
