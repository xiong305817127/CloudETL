package com.ys.idatrix.cloudetl.subcribe;

import org.pentaho.di.job.JobStatusChangeListener;

public interface SubcribePushService {

	public static final String SUBCRIBE_PUSH_KEY ="SubcribePush";
	public static final String PUSH_FAIL_KEY ="SubcribePush:subcribe_push_fail_key";
	
	public JobStatusChangeListener createSubcribePushListener(String jobName);
	
	public void retryPush();
	
}
