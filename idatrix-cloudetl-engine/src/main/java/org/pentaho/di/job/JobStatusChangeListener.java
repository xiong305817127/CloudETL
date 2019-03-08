package org.pentaho.di.job;

import org.pentaho.di.core.Result;
import org.pentaho.di.job.Job;


public interface JobStatusChangeListener {
	
	public static String EXEC_PARAMS_KEY="StatusChangeListeners";
	
	public void statusChange(Job job,String status,Result result, Object params) ;

}
