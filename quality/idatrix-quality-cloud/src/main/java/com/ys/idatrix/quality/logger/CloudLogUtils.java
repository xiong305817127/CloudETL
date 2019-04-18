/**
 * 云化数据集成系统 
 * iDatrxi CloudETL
 */
package com.ys.idatrix.quality.logger;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.StringWriter;
import java.util.Scanner;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleFileException;
import org.pentaho.di.core.util.IdatrixPropertyUtil;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.core.vfs.KettleVFS;

import com.fasterxml.jackson.databind.ObjectMapper;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONUtils;

/**
 * CloudLogUtils <br/>
 * @author JW
 * @since 2017年9月8日
 * 
 */
public class CloudLogUtils {
	
	private final static Long maxLogLen= Long.valueOf( IdatrixPropertyUtil.getProperty("idatrix.read.log.max.length" ,"50000"));
	private final static int maxRowNum = Integer.valueOf( IdatrixPropertyUtil.getProperty("idatrix.find.log.max.row" ,"50000"));//一万行 约为一兆,默认最大搜索5兆的文件

	public static String jsonLog(Object obj) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			return mapper.writeValueAsString(obj) ;
		} catch (Exception e) {
			return obj != null ? obj.toString() : "NULL";
		}
	}

	public static String jsonLog2(Object obj) {
		try {
			//不要使用 jsonLog (ObjectMapper方式) ,不然 对于 SlaveServer这样的大对象,转换超级慢,服务切片日志会要很久
			return obj.getClass().getSimpleName() + ": "+  (JSONUtils.isArray(obj) ? JSONArray.fromObject(obj).toString() : JSONObject.fromObject(obj).toString()); 
		} catch (Exception e) {
			return obj != null ? obj.toString() : "NULL";
		}
	}

	public static String exStackTraceLog(Exception ex) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw, true);
		ex.printStackTrace(pw);
		pw.flush();
		sw.flush();
		pw.close();
		return sw.toString();
	}
	
	public static String searchLog( String fileName, String startLineFlag ,String endLineFlag) throws IOException{//在文件a中的每行中查找x
		
		StringBuilder sb = new StringBuilder();
		Scanner scan = null;
		FileObject fileObject = null ;
		boolean isEnd = false;
		boolean isStart = false ;
		if(Utils.isEmpty(startLineFlag)) {
			isStart = true ;
		}
	
		try {
			long len =0;
			int rowNum = maxRowNum ;
			fileObject = KettleVFS.getFileObject(fileName);
			if (fileObject.exists()) {
				 scan  = new Scanner(fileObject.getContent().getInputStream(),"UTF-8");
		         while(scan.hasNext() && !isEnd){    
		            String s = scan.nextLine();
				
		            if(isStart) {
		            	sb.append(s+Const.CR);
		            	len = sb.length();
		            }
		            
		            if(!Utils.isEmpty(startLineFlag) && s.contains(startLineFlag)){
		            	isStart = true ;
		            }
		            
		        	if(!isStart ) {
		            	 //超出计算,还没有找到,放弃查找
		            	 rowNum --;
		            }
		            
		            if((!Utils.isEmpty(endLineFlag) &&s.contains(endLineFlag)) || len > maxLogLen || rowNum == 0){
		            	isEnd = true ;
		            	if( len > maxLogLen  || rowNum == 0 ) {
		            		sb.append("..."+Const.CR); 
		            		sb.append(Const.CR+"日志过多,请到后台查看日志文件..."+Const.CR); 
		            	}
		            }
		        } 
			}
	        
		} catch (KettleFileException e) {
			sb.append( "Unable to get log from file [" + fileName + "],"+CloudLogger.getExceptionMessage(e));
		} finally {
			if(scan != null) {
				scan.close();
			}
			if(fileObject != null) {
				fileObject.close();
			}
		}
		
		return sb.toString();
    }

	public static String getLog(String filename) {
		StringBuffer message = new StringBuffer();

		RandomAccessFile  randomFile = null;
		try {
			FileObject fileObject = KettleVFS.getFileObject(filename);
			if (fileObject.exists()) {
				randomFile = new RandomAccessFile(filename, "r");
				Long len = randomFile.length();
				byte[] bytes ;
				if(len > maxLogLen) {
					message.append("...日志过多,请到日志文件查看更多日志!").append(Const.CR);
					long start = len - maxLogLen;
					 // 将读文件的开始位置移到beginIndex位置。
			        randomFile.seek(start);
			        bytes = new byte[maxLogLen.intValue()];
					
				}else {
					bytes=new byte[len.intValue()];
				}
				
				randomFile.read(bytes);
				message.append(new String(bytes));
				randomFile.close();
			}
			fileObject.close();
		} catch (IOException | KettleFileException e) {
			message.append( "Unable to get log from file [" + filename + "]" );
		} finally {
			if (randomFile != null) {
				try {
					randomFile.close();
				} catch (IOException e) {
				}
			}
		}

		return message.toString();
	}

	public static void insertLog(String filename, String logText) throws KettleException, IOException {
		FileObject fileObject = KettleVFS.getFileObject(filename);
		if (!fileObject.exists()) {
			fileObject.createFile();
		}
		fileObject.close();

		OutputStream outputStream = null;
		try {
			outputStream = KettleVFS.getOutputStream(filename, true);
			outputStream.write(logText.getBytes());
			outputStream.write(Const.CR.getBytes());
			outputStream.close();
		} catch (IOException | KettleFileException e) {
			throw new KettleException("Unable to write log to file [" + filename + "]");
		} finally {
			if (outputStream != null) {
				outputStream.close();
			}
		}
	}

	public static void renameLog( String filename, String newPath)
			throws KettleFileException, FileSystemException {
		FileObject fileObject = KettleVFS.getFileObject(filename);
		if (fileObject.exists()) {
			FileObject newObject = KettleVFS.getFileObject( newPath );
			fileObject.moveTo(newObject); // JW: maybe failed !
			newObject.close();
		}
		fileObject.close();
	}

	public static void deleteLog(String filename) throws KettleFileException, FileSystemException {
		FileObject fileObject = KettleVFS.getFileObject(filename);
		if (fileObject.exists()) {
			if(fileObject.isFolder()) {
				fileObject.deleteAll();
			}else {
				fileObject.delete();
			}
		}
		fileObject.close();
	}
	
	public static void deleteAllLog(String filepath, String fileName) throws KettleFileException, FileSystemException {
		FileObject fileObject = KettleVFS.getFileObject(filepath);
		if(fileObject.isFolder()) {
			for( FileObject child : fileObject.getChildren()) {
				if (child.exists() && child.getName().getBaseName().contains(fileName) ) {
					if(child.isFolder()) {
						child.deleteAll();
					}else {
						child.delete();
					}
				}
				child.close();
			}
		}else {
			if (fileObject.exists()) {
				fileObject.delete();
			}
		}
		fileObject.close();
	}

}
