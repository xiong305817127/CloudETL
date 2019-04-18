/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.quality.dto.entry.entries.general;

import org.pentaho.di.job.JobMeta;
import org.pentaho.di.job.entries.special.JobEntrySpecial;
import org.pentaho.di.job.entry.JobEntryCopy;
import org.pentaho.di.job.entry.JobEntryInterface;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Entry - special. 转换 org.pentaho.di.job.entries.special.JobEntrySpecial
 * 
 * @author XH
 * @since 2017-06-29
 */
@Component("SPdummy")
@Scope("prototype")
public class SPdummy extends SPspecial {
	
	public SPdummy(){
		initStart(false);
	}
	
	public Object encodeParameterObject(JobEntryCopy jobEntryCopy) throws Exception {

		SPspecial res = (SPspecial)super.encodeParameterObject(jobEntryCopy);
		res.initStart(false);
		return res;
	}

	/* 
	 * 
	 */
	@Override
	public void decodeParameterObject(JobEntryCopy jobEntryCopy , Object po, JobMeta jobMeta) throws Exception {
		JobEntryInterface entryMetaInterface = jobEntryCopy.getEntry() ;
		
		super.decodeParameterObject(jobEntryCopy, po, jobMeta);
		JobEntrySpecial jobentryspecial = (JobEntrySpecial) entryMetaInterface;
		jobentryspecial.setDummy(true);
		jobentryspecial.setStart(false);
		
	}
	
}
