package com.ys.idatrix.cloudetl.subcribe;

import org.pentaho.di.core.util.IdatrixPropertyUtil;
import com.ys.idatrix.cloudetl.ext.PluginFactory;

public class SubcribeFailRetryListener implements Runnable {
	
	//private Logger logger = LoggerFactory.getLogger(SubcribeFailRetryListener.class);

	private SubcribePushService subcribePushService;
	private static SubcribeFailRetryListener subcribeFailRetryListener ;


	private SubcribeFailRetryListener() {
		super();
		
		subcribePushService = (SubcribePushService) PluginFactory.getBean(SubcribePushService.class);

	}

	public static SubcribeFailRetryListener getInstance() {
		if (subcribeFailRetryListener == null) {
			subcribeFailRetryListener = new SubcribeFailRetryListener();
		}
		return subcribeFailRetryListener;
	}

	@Override
	public void run() {

		boolean enable = IdatrixPropertyUtil.getBooleanProperty( "idatrix.subcribe.push.fail.retry.enable", true);
		while (enable && subcribePushService != null) {
			subcribePushService.retryPush();
			try {
				Integer interval = Integer.valueOf(IdatrixPropertyUtil.getProperty( "idatrix.subcribe.push.fail.retry.interval", "300"));
				Thread.sleep(interval * 1000);
			} catch (InterruptedException e) {
			}
		}

	}

	
}
