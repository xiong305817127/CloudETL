package org.pentaho.di.job;

import org.pentaho.di.core.Result;
import org.pentaho.di.job.entry.JobEntryCopy;

public interface SingleListener {
	
	public void singleStart(Job job, JobEntryCopy startpoint);

	public void singleEnd(Job job, Result res) ;
}
