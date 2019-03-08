/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.dto.entry.entries.conditions;

import org.pentaho.di.job.JobMeta;
import org.pentaho.di.job.entries.delay.JobEntryDelay;
import org.pentaho.di.job.entry.JobEntryCopy;
import org.pentaho.di.job.entry.JobEntryInterface;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ys.idatrix.cloudetl.dto.entry.entries.EntryParameter;

import net.sf.json.JSONObject;

/**
 * Entry - Delay. 转换 org.pentaho.di.job.entries.delay.JobEntryDelay
 * 
 * @author XH
 * @since 2017-06-29
 */
@Component("SPdelay")
@Scope("prototype")
public class SPDelay implements EntryParameter {

	String maximumTimeout;
	int scaleTime;

	/**
	 * @return maximumTimeout
	 */
	public String getMaximumTimeout() {
		return maximumTimeout;
	}

	/**
	 * @param 设置
	 *            maximumTimeout
	 */
	public void setMaximumTimeout(String maximumTimeout) {
		this.maximumTimeout = maximumTimeout;
	}

	/**
	 * @return scaleTime
	 */
	public int getScaleTime() {
		return scaleTime;
	}

	/**
	 * @param 设置
	 *            scaleTime
	 */
	public void setScaleTime(int scaleTime) {
		this.scaleTime = scaleTime;
	}

	/* 
	 * 
	 */
	@Override
	public Object getParameterObject(Object json) {

		JSONObject jsonObj = JSONObject.fromObject(json);
		return (SPDelay) JSONObject.toBean(jsonObj, SPDelay.class);
	}

	/* 
	 * 
	 */
	@Override
	public Object encodeParameterObject(JobEntryCopy jobEntryCopy) throws Exception {
		JobEntryInterface entryMetaInterface = jobEntryCopy.getEntry() ;

		SPDelay spDelay = new SPDelay();
		JobEntryDelay jobentrydelay = (JobEntryDelay) entryMetaInterface;

		spDelay.setMaximumTimeout(jobentrydelay.getMaximumTimeout());
		spDelay.setScaleTime(jobentrydelay.getScaleTime());
		return spDelay;
	}

	/* 
	 * 
	 */
	@Override
	public void decodeParameterObject(JobEntryCopy jobEntryCopy , Object po, JobMeta jobMeta) throws Exception {
		JobEntryInterface entryMetaInterface = jobEntryCopy.getEntry() ;
		
		SPDelay spDelay = (SPDelay) po;
		JobEntryDelay jobentrydelay = (JobEntryDelay) entryMetaInterface;

		jobentrydelay.setMaximumTimeout(spDelay.getMaximumTimeout());
		jobentrydelay.setScaleTime(spDelay.getScaleTime());

	}

}
