/**
 * 云化数据集成系统
 * iDatrix CloudETL
 */
package com.ys.idatrix.quality.ext.listener;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.KettleLogStore;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.ys.idatrix.quality.ext.CloudApp;
import com.ys.idatrix.quality.logger.CloudLogUtils;
import com.ys.idatrix.quality.recovery.trans.RemoteResumeListener;

/**
 * Listener to initialize context for application,
 * including information such as settings, environment, repository, etc.
 * 
 * @author JW
 * @since 05-12-2017
 * 
 */
@Component
public class SystemLoadListener implements ApplicationListener<ApplicationEvent>  {

	public static final Log  logger = LogFactory.getLog("SystemLoadListener");
	
	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		if( event instanceof ContextRefreshedEvent) {
			
			ContextRefreshedEvent e = (ContextRefreshedEvent)event ;
			
			if(e.getApplicationContext().getParent() == null){
	             //需要执行的逻辑代码，当spring容器初始化完成后就会执行该方法。确保只执行一次  
				contextInitialized();
	        }  
		}else if( event instanceof  ContextClosedEvent   ){
			contextDestroyed();
		}
	}
	
//	@Override
	public void contextDestroyed() {
		// Cleanup repository connection
		CloudApp.getInstance().selectRepository(null);
		RemoteResumeListener.getInstance().setDone();
	}

//	@Override
	public void contextInitialized() {
		try {
			// Initialize log settings
			KettleLogStore.init(5000, 720);
			KettleEnvironment.init();
			//Props.init(Props.TYPE_PROPERTIES_KITCHEN);
			//CloudUiProps.init("iDatrix CloudETL", Props.TYPE_PROPERTIES_KITCHEN);

			CloudApp.getInstance().initDefault();
			//CloudApp.getInstance().initDbRepository();

			CloudAppTheadTrigger.startTrigger();
			
		} catch (Exception e) {
			try {
				CloudLogUtils.insertLog("error.log", "System initiation failed, please contact your administrator for help!\n\n" + CloudLogUtils.exStackTraceLog(e));
			} catch (KettleException | IOException e1) {
			}
			logger.error("",e);
			throw new RuntimeException("System initiation failed, please contact your administrator for help!");
		}
	}

}
