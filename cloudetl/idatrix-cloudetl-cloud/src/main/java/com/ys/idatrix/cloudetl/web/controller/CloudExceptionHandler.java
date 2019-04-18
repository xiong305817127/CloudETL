/**
 * 云化数据集成系统
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.web.controller;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.jetty.io.EofException;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleFileException;
import org.pentaho.di.core.util.IdatrixPropertyUtil;
import org.pentaho.metastore.api.exceptions.MetaStoreException;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import com.ys.idatrix.cloudetl.dto.common.ReturnCodeDto;
import com.ys.idatrix.cloudetl.logger.CloudLogger;

/**
 * 控制器异常处理切面
 * Common exception handler for all cloud controllers.
 * 
 * @author JW
 * @since 2017年7月27日
 *
 */
@ControllerAdvice
public class CloudExceptionHandler {

	/*@InitBinder  
	public void initBinder(WebDataBinder binder) {  
		String username = CloudSession.getUsername();
		if(!Thread.currentThread().getName().contains(username)){
			Thread.currentThread().setName(Thread.currentThread().getName()+StringUtil.getThreadNameesSuffixByUser(username));
		}
	}*/

	/**
	 * 文件上传大小超出限制异常处理 - MaxUploadSizeExceededException
	 * @param e
	 * @return
	 */
	@ExceptionHandler(MaxUploadSizeExceededException.class)
	public  @ResponseBody ReturnCodeDto maxUploadSizeExceededExceptionHandler(MaxUploadSizeExceededException e) {
		String maxFileSizeStr = IdatrixPropertyUtil.getProperty("upload.file.max.size");
		if(StringUtils.isEmpty(maxFileSizeStr)){
			maxFileSizeStr="209715200"; //200M
		}

		int maxFileSize = -1;
		try {
			maxFileSize = Integer.parseInt(maxFileSizeStr)/(1024*1024) ;
		} catch (NumberFormatException ex) {
		}
		
		String msg = " 文件大小不能超过 " + maxFileSize + "MB !";
		CloudLogger.getInstance().error("MaxUploadSizeExceededException",msg);

		ReturnCodeDto returnDto = new ReturnCodeDto();
		returnDto.setRetCode(-5);
		returnDto.setMessage(msg);
		return returnDto;
	}

	/**
	 * 文件不存在异常处理 - FileNotFoundException
	 * @param e
	 * @return
	 */
	@ExceptionHandler(KettleFileException.class)
	public  @ResponseBody ReturnCodeDto fileNotFoundExceptionHandler(KettleFileException e) {
		String msg = CloudLogger.getExceptionMessage(e);
		CloudLogger.getInstance().error("KettleFileException",msg);

		ReturnCodeDto returnDto = new ReturnCodeDto();
		returnDto.setRetCode(-4);
		returnDto.setMessage(msg);
		return returnDto;
	}

	/**
	 * Kettle异常处理 - KettleException
	 * @param e
	 * @return
	 */
	@ExceptionHandler(KettleException.class)
	public @ResponseBody  ReturnCodeDto kettleExceptionHandler(KettleException e) {
		String msg = CloudLogger.getExceptionMessage(e);
		CloudLogger.getInstance().error("KettleException",msg);

		ReturnCodeDto returnDto = new ReturnCodeDto();
		returnDto.setRetCode(-2);
		returnDto.setMessage(msg);
		return returnDto;
	}

	/**
	 * 元数据存储异常处理 - MetaStoreException
	 * @param e
	 * @return
	 */
	@ExceptionHandler(MetaStoreException.class)
	public @ResponseBody  ReturnCodeDto metaStoreExceptionHandler(MetaStoreException e) {
		String msg = CloudLogger.getExceptionMessage(e);
		CloudLogger.getInstance().error("MetaStoreException",msg);
		
		ReturnCodeDto returnDto = new ReturnCodeDto();
		returnDto.setRetCode(-3);
		returnDto.setMessage(msg);
		return returnDto;
	}
	
	
	/**
	 * 断开的管道 (Write failed)- Exception
	 * @param request
	 * @param e
	 * @return
	 */
	@ExceptionHandler(EofException.class)
	public void EofExceptionHandler(HttpServletRequest request,EofException e) {
		CloudLogger.getInstance().error("EofException","requesting URI: " + request.getRequestURI());
		CloudLogger.getInstance().error("EofException","org.eclipse.jetty.io.EofException:"+CloudLogger.getExceptionMessage(e) );
	}

	/**
	 * 通用异常处理 - Exception
	 * @param request
	 * @param e
	 * @return
	 */
	@ExceptionHandler(Exception.class)
	public @ResponseBody ReturnCodeDto exceptionHandler(HttpServletRequest request, Exception e) {
		CloudLogger.getInstance().error("Exception","requesting URI: " + request.getRequestURI());
		
		String msg = CloudLogger.getExceptionMessage(e);
		CloudLogger.getInstance().error("Exception",msg);
		
		ReturnCodeDto returnDto = new ReturnCodeDto();
		returnDto.setRetCode(-1);
		returnDto.setMessage(msg);
		return returnDto;
	}



}
