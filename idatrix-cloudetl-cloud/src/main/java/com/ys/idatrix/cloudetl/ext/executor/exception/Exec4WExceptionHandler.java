/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.ext.executor.exception;

import java.util.Date;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.di.core.util.Utils;

import com.ys.idatrix.cloudetl.dto.common.PaginationDto;
import com.ys.idatrix.cloudetl.dto.exec4w.Exec4WExceptionDto;
import com.ys.idatrix.cloudetl.ext.CloudSession;
import com.ys.idatrix.cloudetl.ext.utils.DatabaseHelper;

/**
 * CloudExecHistory.java
 * @author JW
 * @since 2017年8月2日
 *
 */
public class Exec4WExceptionHandler{
	
	public static final Log  logger = LogFactory.getLog("4WExceptionHandler");
	
	private static final Class<Exec4WExceptionDto> TABLE_CLASS=Exec4WExceptionDto.class;
	
	private static boolean isDatabaseinit = false;
	
	public static void init() throws Exception {
		
		if(!isDatabaseinit) {
			DatabaseHelper.createOrUpdateTableifNonexist(TABLE_CLASS,  null);
			
			isDatabaseinit = true ;
		}
	}
	
	public Exec4WExceptionHandler( ) {
		try {
			init();
		} catch (Exception e) {
			logger.error("创建表Exec4WException失败",e);
		}
	}

	private static final String orderbyConstant = " UPDATEDATE DESC " ;
	
	private static Exec4WExceptionHandler handler ;
	public static Exec4WExceptionHandler getInstance( ) {
		if (handler == null) {
			synchronized (Exec4WExceptionHandler.class) {
				if (handler == null) {
					handler = new Exec4WExceptionHandler();
				}
			}
		}
		return handler;
	}
	
	
	public Exec4WExceptionDto getExecException( String execId)  throws Exception{
		if(Utils.isEmpty(execId)) {
			return null ;
		}
		return  DatabaseHelper.queryFirst(TABLE_CLASS, null, null, new String[] {"EXECID"}, new String[] {"="}, new String[] {execId}, orderbyConstant );
	}
	
	public PaginationDto<Exec4WExceptionDto> getExecExceptions( String owner,String name,String type,Integer pageNo, Integer pageSize)  throws Exception{
		
		String renterId = CloudSession.getLoginRenterId() ;
		
		int length = 1;
		length = !Utils.isEmpty(owner) ? length+1 : length ;
		length = !Utils.isEmpty(name) ? length+1 : length ;
		length = !Utils.isEmpty(type) ? length+1 : length ;
		
		String[] whereFields = new String[length];
		String[] condition = new String[length];
		Object[] whereValues = new Object[length];
		
		int index = 0;
		whereFields[index] = "RENTERID" ; condition[index] = "=" ; whereValues[index] = renterId ; index++;
		if( !Utils.isEmpty(owner)) {
			whereFields[index] = "OWNER" ; condition[index] = "=" ; whereValues[index] = owner ; index++;
		}
		if( !Utils.isEmpty(name)) {
			whereFields[index] = "NAME" ; condition[index] = "like" ; whereValues[index] = "%"+name+"%" ; index++;
		}
		if( !Utils.isEmpty(type)) {
			whereFields[index] = "TYPE" ; condition[index] = "=" ; whereValues[index] = type ; index++;
		}
		
		return  DatabaseHelper.queryPageList(TABLE_CLASS, TABLE_CLASS, null, whereFields, condition , whereValues , orderbyConstant, pageNo, pageSize);
	}
	
	public PaginationDto<Exec4WExceptionDto> getExecExceptionsByRenterId( String renterId, Integer pageNo, Integer pageSize)  throws Exception{
		if(Utils.isEmpty(renterId)) {
			renterId = CloudSession.getLoginRenterId() ;
		}
		return  DatabaseHelper.queryPageList(TABLE_CLASS, TABLE_CLASS, null, new String[] {"RENTERID"}, new String[] {"="}, new Object[] {renterId }, orderbyConstant, pageNo, pageSize);
	}
	
	public void insertExecException(Exec4WExceptionDto record)  throws Exception{
		if(Utils.isEmpty(record.getOwner())) {
			record.setOwner( CloudSession.getResourceUser() );
		}
		record.setRenterId(CloudSession.getLoginRenterId());
		record.setUpdateDate(new Date());
		DatabaseHelper.insert( TABLE_CLASS, record );
	}
	
	public void renameExecException(  String owner,String name,String type ,String newname) throws Exception {
		if(Utils.isEmpty(owner)) {
			owner = CloudSession.getResourceUser();
		}
		DatabaseHelper.update(TABLE_CLASS, new String[] {"NAME"},new String[] {"OWNER","NAME","TYPE"}, new String[] {"=","=","="} , new String[] {newname,owner,name,type});
	}
	
	public void deleteExecException(  String owner,String name,String type )  throws Exception{
		if(Utils.isEmpty(owner)) {
			owner = CloudSession.getResourceUser();
		}
		DatabaseHelper.delete(TABLE_CLASS,  new String[] {"OWNER","NAME","TYPE"}, new String[] {"=","=","="}, new String[] {owner,name,type} );
	}
	
}
