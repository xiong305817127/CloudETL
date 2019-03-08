/**
 * 云化数据集成系统 
 * iDatrxi CloudETL
 */
package com.ys.idatrix.cloudetl.logger;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.RollingRandomAccessFileAppender;
import org.apache.logging.log4j.core.appender.rolling.DefaultRolloverStrategy;
import org.apache.logging.log4j.core.appender.rolling.RolloverStrategy;
import org.apache.logging.log4j.core.appender.rolling.TimeBasedTriggeringPolicy;
import org.apache.logging.log4j.core.appender.rolling.TriggeringPolicy;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.pentaho.di.core.util.IdatrixPropertyUtil;

/**
 * Custom logger implementation. <br/>
 * CloudLoggerFactory (using Apache Log4j 2) <br/>
 * 
 * @author JW
 * @since 2017年9月11日
 * 
 */
public class CloudLoggerFactory {

	private static final String CONFIG_LOCATION = "log4j2.xml";

	private static final String PATTERN_LAYOUT = "%d{ABSOLUTE} %-5p %m%n";
	//private static final String PATTERN_LAYOUT = "%d{HH:mm:ss.SSS} [%t] %-5level %logger{36} - %m%n";

	private CloudLoggerFactory() {

	}

	/**
	 * Create and start a new logger in log4j2 configuration
	 * 	1. Create pattern layout
	 * 	2. Create appender
	 * 	3. Create logger and add it in configuration
	 * @param name
	 * @param filename
	 */
	public static void start(String name, String path, String filename, String extension) {
		LoggerContext ctx = getLoggerContext();
		final Configuration config = ctx.getConfiguration();

		// 创建一个展示的样式：PatternLayout，   还有其他的日志打印样式。
		Layout<?> layout = PatternLayout.newBuilder()
				.withPattern(PATTERN_LAYOUT)
				.withPatternSelector(null)
				.withConfiguration(config)
				.withRegexReplacement(null)
				.withCharset(Charset.forName("UTF-8"))
				.withAlwaysWriteExceptions(true)
				.withNoConsoleNoAnsi(false)
				.withHeader(null)
				.withFooter(null)
				.build();

		// 日志打印方式 - 输出为文件
		RolloverStrategy strategy = DefaultRolloverStrategy.newBuilder().withMax("100").withConfig(config).build();
		//RolloverStrategy strategy = DirectWriteRolloverStrategy.newBuilder().withMaxFiles("24").withConfig(config).build();
		TriggeringPolicy tp = TimeBasedTriggeringPolicy.newBuilder().withInterval(1).build(); //SizeBasedTriggeringPolicy.createPolicy("10MB");
		String filePattern = path + "${date:yyyy-MM}/" + filename + "-%d{yyyy-MM-dd}-%i" + extension;
		Appender appender = RollingRandomAccessFileAppender.newBuilder()
				.withFileName(path + filename + extension)
				.withFilePattern(filePattern)
				.withAppend(true)
				.withName(name)
				.withImmediateFlush(true)
				.withBufferedIo(true)
				.withBufferSize(8192)
				.withPolicy(tp)
				.withStrategy(null)
				.withLayout(layout)
				.withFilter(null)
				.withIgnoreExceptions(true)
				.withAdvertise(false)
				.withAdvertiseURI(null)
				.setConfiguration(config)
				.withStrategy(strategy)
				.build();
		appender.start();

		/*Appender appender = FileAppender.newBuilder()
				.withAdvertise(false)
				.withAdvertiseUri(null)
				.withAppend(true)
				.withBufferedIo(true)
				.withBufferSize(8192)
				.setConfiguration(config)
				.withFileName(filename)
				.withFilter(null)
				.withIgnoreExceptions(true)
				.withImmediateFlush(true)
				.withLayout(layout)
				.withLocking(false)
				.withName(name)
				.build();
		appender.start();*/

		config.addAppender(appender);
		AppenderRef ref = AppenderRef.createAppenderRef(name, null, null);
		AppenderRef[] refs = new AppenderRef[]{ref};

		// Create new logger
		String level = IdatrixPropertyUtil.getProperty("idatrix.logger.level", "INFO").toUpperCase();
		//是否级联输出到父appender
		boolean additivity = Boolean.valueOf(IdatrixPropertyUtil.getProperty("idatrix.logger.additivity", "false"));
		//当设置不输出到父appender时,错误等级的日志是否输出到父appender
		boolean additivityError = Boolean.valueOf(IdatrixPropertyUtil.getProperty("idatrix.logger.additivity.error", "true"));
		if(!additivity && additivityError ) {
			additivity=true;
			LoggerConfig root = config.getLoggerConfig("ROOT");
			Map<String, Appender> rootAppenders = root.getAppenders();
			for( Entry<String, Appender> entry : rootAppenders.entrySet()) {
				root.removeAppender(entry.getKey());
				root.addAppender(entry.getValue(), Level.ERROR, null);
			}
		}
		
		LoggerConfig loggerConfig = LoggerConfig.createLogger(additivity, Level.getLevel(level), name, "true", refs, null, config, null);
		loggerConfig.addAppender(appender, null, null);
		config.addLogger(name, loggerConfig);
		ctx.updateLoggers();
	}

	/**
	 * Stop and remove the logger from logger context
	 * @param name
	 */
	public static void stop(String name) {
		LoggerContext ctx = getLoggerContext();
		if (ctx.hasLogger(name)) {
			final Configuration config = ctx.getConfiguration();
			config.getAppender(name).stop();
			config.getLoggerConfig(name).removeAppender(name);
			config.removeLogger(name);
			ctx.updateLoggers();
		}
	}

	/**
	 * Create a new logger
	 * 	- LogManager for log4j logger, LogFactory for slf4j logger
	 * @param jobId
	 * @return
	 */
	public static Logger createLogger(String name, String path, String filename, String extension) {
		LoggerContext ctx = getLoggerContext();
		if (!ctx.hasLogger(name)) {
			start(name, path, filename, extension);
		}
		return LogManager.getLogger(name);
	}

	/**
	 * Get the existing logger
	 * @param name
	 * @return
	 */
	public static Logger getLogger(String name) {
		LoggerContext ctx = getLoggerContext();
		if (!ctx.hasLogger(name)) {
			return LogManager.getRootLogger();
		}
		return LogManager.getLogger(name);
	}
	
	/**
	 * Get logger by class
	 * @param clazz
	 * @return
	 */
	public static Logger getLogger(Class<?> clazz) {
		getLoggerContext();
		return LogManager.getLogger(clazz);
	}
	
	/**
	 * Get logger context with correct configuration location.
	 * @return
	 */
	private static LoggerContext getLoggerContext() {
		try {
			URL url = CloudLoggerFactory.class.getClassLoader().getResource(CONFIG_LOCATION);//new URL(CONFIG_LOCATION);
			ConfigurationSource source = new ConfigurationSource(url.openStream(), url);
			LoggerContext context = Configurator.initialize(null, source);
			//XmlConfiguration xmlConfig = new XmlConfiguration(context, source);
			//context.start(xmlConfig);
			return context;
		} catch (IOException e) {
			//e.printStackTrace();
		}
		
		// false：返回多个LoggerContext对象，true：返回唯一的单例LoggerContext
		return (LoggerContext) LogManager.getContext(false);
	}

}

