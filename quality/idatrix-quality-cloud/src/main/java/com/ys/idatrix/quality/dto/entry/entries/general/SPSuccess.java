/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.quality.dto.entry.entries.general;

import org.pentaho.di.job.JobMeta;
import org.pentaho.di.job.entries.success.JobEntrySuccess;
import org.pentaho.di.job.entry.JobEntryCopy;
import org.pentaho.di.job.entry.JobEntryInterface;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ys.idatrix.quality.dto.entry.entries.EntryParameter;

import net.sf.json.JSONObject;

/**
 * Entry - Success.
 * 转换  org.pentaho.di.job.entries.success.JobEntrySuccess
 * 
 * @author XH
 * @since 2017-06-29
 */
@Component("SPsuccess")
@Scope("prototype")
public class SPSuccess implements EntryParameter {

	/* 
	 * 
	 */
	@Override
	public Object getParameterObject(Object json) {
		if(json == null){
			return  new SPSuccess();
		}
		JSONObject jsonObj = JSONObject.fromObject(json);
		return (SPSuccess) JSONObject.toBean(jsonObj, SPSuccess.class);
	}

	/* 
	 * 
	 */
	@Override
	public Object encodeParameterObject(JobEntryCopy jobEntryCopy) throws Exception {
		JobEntryInterface entryMetaInterface = jobEntryCopy.getEntry() ;
		
		SPSuccess spSuccess= new SPSuccess();
		@SuppressWarnings("unused")
		JobEntrySuccess jobentrysuccess= (JobEntrySuccess )entryMetaInterface;
		
		return spSuccess;
	}

	/* 
	 * 
	 */
	@Override
	public void decodeParameterObject(JobEntryCopy jobEntryCopy , Object po, JobMeta jobMeta) throws Exception {
		JobEntryInterface entryMetaInterface = jobEntryCopy.getEntry() ;
		
		@SuppressWarnings("unused")
		SPSuccess spSuccess= (SPSuccess)po;
		@SuppressWarnings("unused")
		JobEntrySuccess  jobentrysuccess= (JobEntrySuccess )entryMetaInterface;
		
	}

}
