package com.ys.idatrix.cloudetl.webservice;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import com.ys.idatrix.cloudetl.subscribe.api.dto.SubscribeResultDto;
import com.ys.idatrix.cloudetl.subscribe.api.dto.step.FileCopyDto;
import com.ys.idatrix.cloudetl.subscribe.api.dto.step.FileInputDto;
import com.ys.idatrix.cloudetl.subscribe.api.dto.step.FilterRowsDto;
import com.ys.idatrix.cloudetl.subscribe.api.dto.step.InsertUpdateDto;
import com.ys.idatrix.cloudetl.subscribe.api.dto.step.TableInputDto;
import com.ys.idatrix.cloudetl.subscribe.api.dto.step.TimerDto;

@WebService(targetNamespace="SubscribeWebServiceNamespace" )
public interface SubscribeWebService {
	
	@WebMethod
	public SubscribeResultDto creteTableToTableJob(@WebParam(name="name")String userId, 
												@WebParam(name="name")String name ,
												@WebParam(name="timer")TimerDto timer,
												@WebParam(name="dbInput")TableInputDto dbInput ,
												@WebParam(name="filter")FilterRowsDto filter,
												@WebParam(name="output")InsertUpdateDto output);

	@WebMethod
	public SubscribeResultDto creteFileToTableJob(@WebParam(name="name")String userId, 
												@WebParam(name="name")String name ,
												@WebParam(name="timer")TimerDto timer,
												@WebParam(name="fileInput")FileInputDto fileInput ,
												@WebParam(name="filter")FilterRowsDto filter,
												@WebParam(name="output")InsertUpdateDto output);
	
	@WebMethod
	public SubscribeResultDto creteFileCopyJob(@WebParam(name="name")String userId, 
												@WebParam(name="name")String name ,
												@WebParam(name="output")FileCopyDto fileCopy);
	
}
