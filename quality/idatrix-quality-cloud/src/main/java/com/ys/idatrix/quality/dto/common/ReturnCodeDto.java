/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.quality.dto.common;


import java.util.List;
import java.util.Map;

import org.pentaho.di.core.util.Utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONUtils;

/**
 * http返回结果dto
 * @author JW
 * @since 05-12-2017
 *
 */
@ApiModel("api返回信息")
public class ReturnCodeDto {
	
	@ApiModelProperty("返回码")
	protected String code;
	
	@ApiModelProperty("返回信息(错误信息)")
	protected String msg;
	
	@ApiModelProperty("返回数据")
	protected Object data;
	
	//################构造方法####################
	/**
	 * Constructor.
	 */
	public ReturnCodeDto() {
		
	}
	
	/**
	 * Constructor.
	 */
	public ReturnCodeDto(String code) {
		this.code = code;
	}
	
	/**
	 * Constructor.
	 */
	public ReturnCodeDto(int retCode) {
		setRetCode(retCode);
	}
	
	/**
	 * @param code
	 * @param msg
	 */
	public ReturnCodeDto(String code, String msg) {
		this.code = code;
		this.msg = msg;
	}
	
	/**
	 * @param code
	 * @param msg
	 */
	public ReturnCodeDto(int retCode, String msg) {
		setRetCode(retCode);
		this.msg = msg;
	}
	
	/**
	 * @param code
	 * @param msg
	 */
	public ReturnCodeDto(String code,Object obj) {
		this.code = code;
		this.data=obj;
	}
	
	/**
	 * @param code
	 * @param msg
	 */
	public ReturnCodeDto(int retCode,Object obj) {
		setRetCode(retCode);
		this.data=obj;
	}
	
	//##################code####################
    
    public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
	public void setRetCode(int retCode) {
		if( 0 == retCode) {
			this.code = "200";
		}else {
			this.code = Integer.toString(retCode);
		}
	}
	
	@org.codehaus.jackson.annotate.JsonIgnore
	@com.fasterxml.jackson.annotation.JsonIgnore
	public int getRetCode( ) {
		if( isSuccess() || Utils.isEmpty(this.code)) {
			return 0 ;
		}else {
			return Integer.valueOf(this.code);
		}
	}
	
	//##################msg####################
	
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	@org.codehaus.jackson.annotate.JsonIgnore
	@com.fasterxml.jackson.annotation.JsonIgnore
	public String getMessage() {
		return msg;
	}
	
	public void setMessage(String msg) {
		this.msg = msg;
	}
	
	//##################data####################
	/**
	 * @return the data
	 */
	public Object getData() {
		return data;
	}
	/**
	 * @param  设置 data
	 */
	public void setData(Object data) {
		this.data = data;
	}
	
	/**
	 * @param  设置 data
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void addMapData(String key, Object value) {
		if( this.data == null ) {
			this.data = Maps.newHashMap() ;
		}
		if( this.data  instanceof Map) {
			((Map)this.data).put(key, value);
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void addListData( Object value) {
		if( this.data == null ) {
			this.data = Lists.newArrayList();
		}
		if( this.data  instanceof List) {
			((List)this.data).add(value);
		}
	}
	
	//##################common####################
	
	@org.codehaus.jackson.annotate.JsonIgnore
	@com.fasterxml.jackson.annotation.JsonIgnore
	public boolean isSuccess() {
		return  "200".equals(code) ;
	}
	
	/*
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		if( data != null){
			if(JSONUtils.isArray(data)){
				return "ReturnCodeDto [code=" + code + ", msg=" + msg + ", data="+JSONArray.fromObject(data).toString()+"]";
			}else if(!JSONUtils.isObject(data)){
				return "ReturnCodeDto [code=" + code + ", msg=" + msg + ", data="+data.toString()+"]";
			}
			return "ReturnCodeDto [code=" + code + ", msg=" + msg + ", data="+JSONObject.fromObject(data).toString()+"]";
		}
		return "ReturnCodeDto [code=" + code + ", msg=" + msg + "]";
	}

}
