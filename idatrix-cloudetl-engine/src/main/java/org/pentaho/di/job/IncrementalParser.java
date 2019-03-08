package org.pentaho.di.job;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.pentaho.di.core.Const;
import org.pentaho.di.core.database.Database;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleDatabaseException;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.logging.LogChannel;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.core.logging.LoggingObjectType;
import org.pentaho.di.core.logging.SimpleLoggingObject;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.util.OsgiBundleUtils;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.job.Job;
import org.pentaho.di.job.entries.trans.JobEntryTrans;
import org.pentaho.di.job.entry.JobEntryCopy;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransAdapter;
import org.pentaho.di.trans.TransExecutionConfiguration;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.RowAdapter;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.steps.insertupdate.InsertUpdateMeta;
import org.pentaho.di.trans.steps.tableoutput.TableOutputMeta;

import com.google.common.collect.Lists;

public class IncrementalParser  extends SimpleLoggingObject {
	
	public static final String INCREMENTAL_FIELD="incrementalField";  //输入和输出的 增量字段
	public static final String INCREMENTAL_INIT_VALUE="incrementalInitValue";  // 增量初始值 
	public static final String INCREMENTAL_SQLFLAG="incrementalSqlFlag"; //增量sql标志字符串,默认 DEFAULT_START_LIMIT_VAR_NAME(cloud_incremental_flag)
	
	public static final String INCREMENTAL_RESTART="incrementalRestart";  // 是否重新开始计算增量,将清空现有增量值,还原初始值
	public static final String INCREMENTAL_TRANSSTEPMAP="incrementalTransNameStepNameMap"; //设置需要增量控制的输出组件 , 格式 :  转换名1:输出步骤1.1,输出步骤1.2;转换名2:输出步骤2.1,输出步骤2.2,输出步骤2.3;
	public static final String INCREMENTAL_OUTSTEPNAME="incrementalOutStepName";  //单独指定 输出步骤名 ,兼容旧版本,使用INCREMENTAL_TRANSSTEPMAP
	public static final String INCREMENTAL_MAINTRANSNAME="incrementalMainTransName"; //单独指定 输出转换名,兼容旧版本,使用INCREMENTAL_TRANSSTEPMAP

	public static final String INCREMENTAL_DATEFORMAT="incrementalDateFormat"; //日期格式的增量域时 指定日期格式,默认  yyyy-MM-dd HH:mm:ss 
	public static final String DEFAULT_START_LIMIT_VAR_NAME="cloud_incremental_flag"; //默认 sql增量标志字符串 ,旧版本为:ktr_row_start
	
	private static final String Default_Unknow_Transname="Unknow_Transname";
	
	public Map<String,List<String>>  transStepeMap ;
	public Map<String,Object>  incrementalValueCache ;
	public Map<String,String>  incrementalStepCache ;
	public Map<String,Boolean>  isDetectionMap ;
	public Map<String,Integer>  incrementalTypeMap ;
	public  LogChannelInterface logger ;
	private String jobName ;
	private Job job ;
	
	public IncrementalParser( Job job ) {
		super("IncrementalParser", LoggingObjectType.JOBMETA, job);
		this.job = job;
		this.jobName = Utils.removeOwnerUserByThreadName(job.getJobname()) ;
		logger = new LogChannel( this,job);
		
		incrementalRestart();
		if(isIncremental()) {
			//init
			transStepeMap = new ConcurrentHashMap<>(); 
			incrementalValueCache = new ConcurrentHashMap<>(); 
			incrementalStepCache = new ConcurrentHashMap<>(); 
			isDetectionMap = new ConcurrentHashMap<>(); 
			incrementalTypeMap = new ConcurrentHashMap<>(); 
			
			initTransStepnMap();
			//增加监听
			this.job.addDelegationListener(new DelegationListener() {

				@Override
				public void jobDelegationStarted(Job delegatedJob, JobExecutionConfiguration jobExecutionConfiguration) {
					
				}

				@Override
				public void transformationDelegationStarted(Trans delegatedTrans, TransExecutionConfiguration transExecutionConfiguration) {
					
					List<StepMeta> targetSteps = Lists.newArrayList();
					
					String transName  = getTransName(delegatedTrans);
					logger.logDetailed("初始化转换["+transName+"]增量处理...");
					
					if(transStepeMap.containsKey(transName)) {
						List<String> steps = transStepeMap.get(transName);
						if(steps.size() == 0) {
							//自动获取所有输出
							TransMeta transMeta =  delegatedTrans.getTransMeta();
							for( StepMeta step : transMeta.getTransHopSteps(false)) {
								if(transMeta.findNextSteps(step).size() == 0 && transMeta.findPreviousSteps(step).size() > 0 ) {
									if(  step != null && !targetSteps.contains( step) ) {
										targetSteps.add( step);
									}
								}
							}
						}else {
							for( String stepName : steps ) {
								StepMeta step = delegatedTrans.getTransMeta().findStep(stepName);
								if( step != null && !targetSteps.contains(step) ) {
									targetSteps.add(step);
								}
							}
						}
					}
					if(transStepeMap.containsKey(Default_Unknow_Transname) && transStepeMap.get(Default_Unknow_Transname).size() >0) {
						for( String stepName : transStepeMap.get(Default_Unknow_Transname) ) {
							StepMeta step = delegatedTrans.getTransMeta().findStep(stepName);
							if( step != null && !targetSteps.contains(step) ) {
								targetSteps.add(step);
							}
						}
					}
					
					logger.logDetailed("转换["+transName+"]需要处理的步骤数:"+targetSteps.size());
					
					if( targetSteps.size() > 0 ) {
						if(!isDetectionMap.getOrDefault(transName, false)) {
							logger.logDetailed("转换["+transName+"]进行数据库类型的步骤检测最小值...");
							//对数据库类型输出 进行增量数据初次检测矫正,取数据较小者
							for(StepMeta step : targetSteps) {
								detectionIncrementValue(delegatedTrans, step);
							}
							isDetectionMap.put(transName, true) ;
						}
						//找到了 trans 和 输出
						addMainTransListener(delegatedTrans, targetSteps);
						logger.logBasic("Trans["+transName+"] add ["+targetSteps.size()+"]steps's incremental change Listener.");
					}else {
						//没有需要增量处理的步骤
						return ;
					}
					
					String val = getIncrementalValueFromRedis(transName , true).toString();
					delegatedTrans.setVariable(getIncrementSqlFlag(transName),val );
					//兼容旧版本
					delegatedTrans.setVariable("ktr_row_start",val );
					
					String incrementField = null ;
					try {
						incrementField = getIncrementalField(transName);
					} catch (Exception e1) {
					}
					logger.logBasic("Trans["+transName+"],set incremental variable["+getIncrementSqlFlag(transName)+"("+incrementField+")]: "+val);
					return ;
				}
			});
		}
		
	}
	
	public void addMainTransListener( Trans mainTrans ,List<StepMeta> targetSteps ) {
	
			mainTrans.addTransListener(new TransAdapter() {
				
				String transName =  getTransName(mainTrans);
				
				@Override
				public void transStarted(Trans trans) throws KettleException {
					mainTrans.getSteps().stream().filter(combi -> { return targetSteps.contains( combi.stepMeta);}).forEach(mainCombi ->{
						
						logger.logDetailed("转换["+transName+"] add ["+mainCombi.stepname+"] step incremental change Listener.");
						mainCombi.step.addRowListener(new RowAdapter() {
							
							String stepName = mainCombi.stepname;
							
							@Override
							public void rowReadEvent( RowMetaInterface rowMeta, Object[] row ) throws KettleStepException {
							}
							@Override
							public void rowWrittenEvent(RowMetaInterface rowMeta, Object[] row) throws KettleStepException {
								try {
									dealLinstenerData(transName,stepName , rowMeta, row);
								} catch ( Exception e) {
									logger.logBasic("[WARN]保存增量数据异常："+e.getMessage());
								}
							}
						});
					});
				}
			});
			
		
			
	}
	
	private void dealLinstenerData(String transName ,String outStepName , RowMetaInterface rowMeta, Object[] row) throws Exception {
		int index = rowMeta.indexOfValue( getIncrementalField(transName) );
		if( index != -1 ) {
			
			boolean isCompareMin = false; //是否比较大小,保存较小值, 第一次或者同一个步骤的持续增长 时 不比较
			if( incrementalStepCache.containsKey(transName) && !Utils.isEmpty(outStepName) && !outStepName.equals(incrementalStepCache.get(transName))) {
				//不是同一个步骤  数据时
				isCompareMin = true ;
			}
			Object curIncrementalValue = saveIncrementalValue(transName, isCompareMin,index, rowMeta,row );
			if( curIncrementalValue != null ) {
				incrementalStepCache.put(transName, outStepName);
				logger.logRowlevel("转换["+transName+"],步骤["+outStepName+"]进行增量数据["+curIncrementalValue+"]保存...");
			}
		}
	}
	
	
	private Object saveIncrementalValue(String transName , boolean isCompareMin , int index , RowMetaInterface rowMeta, Object[] row) throws Exception {
		int type = rowMeta.getValueMeta(index).getType() ;
		incrementalTypeMap.put(transName, type)  ;
		
		Object maxId = null;
		if(3 == type) {
			//日期
			SimpleDateFormat format = new SimpleDateFormat(getIncrementalDateFormat());
			Date newValue = null;
			if(row == null ) {
				//使用默认的初始值
				Object init = getInitIncrementalValue(transName);
				newValue = format.parse(init.toString());
			}else if(row.length <= index){
				//数量不够,忽略
				return null;
			}else {
				newValue = rowMeta.getDate(row, index);
			}
			if(newValue != null ) {
				
				maxId = format.format(newValue) ;
				if( isCompareMin ) {
					Date oldValue = (Date) getIncrementalValueFromCache(transName);
					if( oldValue == null ) {
						Object curIncrementalDate = getIncrementalValueFromRedis(transName,false );
						if(curIncrementalDate != null ) {
							try {
								oldValue = format.parse(curIncrementalDate.toString());
							}catch( Exception e) {
								logger.logBasic("[WARN]比小时,redis日期数据["+curIncrementalDate.toString()+"]转换失败："+e.getMessage());
							}
						}
					}
					if(oldValue != null && oldValue.getTime() <= newValue.getTime() ) {
						//已经缓存的值更小						
						return null ;
					}
				}
				//进行增量值缓存
				incrementalValueCache.put(transName, newValue);
			}
		}else {
			Long newValue = null;
			if(row == null ) {
				//使用默认的初始值
				Object init = getInitIncrementalValue(transName);
				newValue = Long.valueOf(init.toString());
			}else if(row.length <= index){
				//数量不够,忽略
				return null;
			}else {
				newValue =rowMeta.getNumber(row, index).longValue();
			}
			if(newValue != null) {
				maxId = newValue ;
				if( isCompareMin ) {
					Long oldValue = (Long) getIncrementalValueFromCache(transName);
					if( oldValue == null ) {
						Object curIncrementalLong = getIncrementalValueFromRedis(transName, false);
						if(curIncrementalLong != null ) {
							try {
								oldValue =Long.valueOf(curIncrementalLong.toString());
							}catch( Exception e) {
								logger.logBasic("[WARN]比小时,redis长整型数据["+curIncrementalLong.toString()+"]转换失败："+e.getMessage());
							}
						}
					}
					if(oldValue != null && oldValue <= newValue ) {
						//已经缓存的值更小		
						return null ;
					}
				}
				//进行增量值缓存
				incrementalValueCache.put(transName, newValue );
			}
		}
		if( maxId != null ) {
			//将值保持到redis等缓存中	
			 String packageName= Utils.getPackageName("com.ys.idatrix.cloudetl.ext.utils.RedisUtil");
			boolean isRedis = (boolean) OsgiBundleUtils.invokeOsgiMethod(packageName, "isCacheEnable");//RedisUtil.isCacheEnable()
			if ( isRedis ) {
				//RedisUtil.hset(getIncrementalKey(),transName, maxId);
				 OsgiBundleUtils.invokeOsgiMethod(packageName, "hset",new Object[] {getIncrementalKey(),transName,maxId},new Class[]{String.class,String.class,Object.class});
			}
			return maxId;
		}else {
			return null ;
		}
		
	}
	
	private Object getIncrementalValueFromCache( String transName ) {
		//读取缓存
		if(incrementalValueCache.containsKey(transName)) {
			return incrementalValueCache.get(transName) ;
		}
		return null ;
	}
	
	private Object getIncrementalValueFromRedis( String transName , boolean isInit) {
		Object maxId = null;
		String packageName= Utils.getPackageName("com.ys.idatrix.cloudetl.ext.utils.RedisUtil");
		boolean isRedis = (boolean) OsgiBundleUtils.invokeOsgiMethod(packageName, "isCacheEnable");//RedisUtil.isCacheEnable()
		if ( isRedis ) {
			maxId =  OsgiBundleUtils.invokeOsgiMethod(packageName, "hget",getIncrementalKey(),transName);//RedisUtil.hget(getIncrementalKey(),transName);
		}
		if( isInit && maxId == null ) {
			maxId = getInitIncrementalValue(transName);
		}
		return maxId ;
	}
	
	private void deleteIncrementalValue() {
		//清空缓存
		if(incrementalValueCache != null ) {
			incrementalValueCache.clear();
		}
		if(incrementalStepCache != null ) {
			incrementalStepCache.clear();
		}
		
		String packageName= Utils.getPackageName("com.ys.idatrix.cloudetl.ext.utils.RedisUtil");
		boolean isRedis = (boolean) OsgiBundleUtils.invokeOsgiMethod(packageName, "isCacheEnable");//RedisUtil.isCacheEnable()
		if ( isRedis ) {
			 OsgiBundleUtils.invokeOsgiMethod(packageName, "del",new Object[] {new String[] {getIncrementalKey()} },new Class[] {String[].class}); //RedisUtil.del(getIncrementalKey());
		}
	}
	
	private void detectionIncrementValue(Trans trans,StepMeta  stepMeta )  {
		
		String transName  = getTransName(trans) ;
		Object curIncrementalValue = getIncrementalValueFromRedis(transName,false);
		if( curIncrementalValue ==  null  || !(stepMeta.getStepMetaInterface() instanceof TableOutputMeta || stepMeta.getStepMetaInterface() instanceof InsertUpdateMeta )) {
			//初始值为空 或者 非数据库操作 不检测
			return ;
		}
		
		String outIncrementField;
		try {
			outIncrementField = getIncrementalField(transName);
		} catch (Exception e1) {
			logger.logBasic("[WARN]转换["+transName+"]获取增量域换失败："+e1.getMessage());
			return  ;
		}
		DatabaseMeta databaseMeta = null ;
		String schame = ""  ;
		String table = ""  ;
		
		if(stepMeta.getStepMetaInterface() instanceof TableOutputMeta) {
			TableOutputMeta tOutput = (TableOutputMeta)stepMeta.getStepMetaInterface() ;
			int index = tOutput.getStreamFields().indexOf(outIncrementField);
			if(index != -1 ) {
				outIncrementField  = tOutput.getDatabaseFields().get(index);
			}
			databaseMeta = tOutput.getDatabaseMeta();
			schame = tOutput.getSchemaName();
			table = tOutput.getTableName();
			
		}else if(stepMeta.getStepMetaInterface() instanceof InsertUpdateMeta) {
			InsertUpdateMeta iuOutput = (InsertUpdateMeta)stepMeta.getStepMetaInterface() ;
			int index =Arrays.asList(iuOutput.getUpdateStream()).indexOf(outIncrementField);
			if(index != -1) {
				outIncrementField  = iuOutput.getUpdateLookup()[index] ;
			}
			databaseMeta = iuOutput.getDatabaseMeta();
			schame = iuOutput.getSchemaName();
			table = iuOutput.getTableName();
		}
		if(databaseMeta != null ) {
			
			String sql =  "SELECT MAX("+databaseMeta.quoteField(outIncrementField)+")  FROM "+databaseMeta.getQuotedSchemaTableCombination(schame, table)+" ORDER BY "+databaseMeta.quoteField(outIncrementField) ;
			Database db = new Database(trans , databaseMeta);
			ResultSet rs = null ;
			try {
				db.connect();
				rs = db.openQuery( sql, null, null, ResultSet.FETCH_FORWARD, false );
				if(rs != null) {
					Object[] row = db.getRow(rs);
					RowMetaInterface rowMeta = db.getReturnRowMeta();
					if(row == null || row.length == 0|| row[0] == null) {
						row = null ;
					}
					Object maxId = saveIncrementalValue(transName, true, 0, rowMeta , row );
					if( maxId != null ) {
						incrementalStepCache.put(transName, stepMeta.getName());
						logger.logBasic("进行Trans["+transName+"],步骤["+ stepMeta.getName()+"]增量界限值初次校验异常,当前值["+curIncrementalValue+"],数据库值["+maxId+"],进行更新...");
					}else {
						logger.logBasic("进行Trans["+transName+"],步骤["+ stepMeta.getName()+"]增量界限值初次校验正常,值["+curIncrementalValue+"]");
					}
				}
			
			} catch ( Exception e ) {
				logger.logBasic("[WARN]检测增量值处理异常."+e.getMessage());
			}finally {
				try {
					db.closeQuery(rs);
					db.disconnect();
				} catch (KettleDatabaseException e) {
				}
			}
		}
	}
	
	private String getTransName(Trans trans) {
		String transName  = trans.getTransMeta().getName().split("@u-")[0] ;
		return transName ;
	}
	
	private String getIncrementalKey(){
		if(isIncremental()) {
			return  "Incremental:"+jobName+"_"+getIncrementalField();
		}
		return  null;
	}
	
	public boolean isIncremental() {
		String field = getIncrementalField();
		if( Utils.isEmpty(field) || "false".equalsIgnoreCase(field.trim())) {
			return false ;
		}else {
			//true 或者 字段名
			return true;
		}
	}

	private String getIncrementalField(){
		return Const.NVL(job.getVariable(INCREMENTAL_FIELD),"").trim();
	}
	
	private String getIncrementalField(String transName) throws Exception{
		String field = job.getVariable(INCREMENTAL_FIELD+"_"+transName);
		if(Utils.isEmpty(field)) {
			field = getIncrementalField();
		}
		if(Utils.isEmpty(field) || "true".equalsIgnoreCase(field.trim())) {
			throw new Exception("增量参数["+INCREMENTAL_FIELD+"]没有设置!");
		}
		return field.trim() ;
	}
	
	private String getIncrementSqlFlag( String transName ) {
		String sqlFlag = job.getVariable(INCREMENTAL_SQLFLAG+"_"+transName);
		if(Utils.isEmpty(sqlFlag)) {
			sqlFlag = job.getVariable(INCREMENTAL_SQLFLAG);
		}
		if(Utils.isEmpty(sqlFlag)) {
			sqlFlag = DEFAULT_START_LIMIT_VAR_NAME;
		}
		return sqlFlag.trim() ;
	}
	
	private void incrementalRestart(){
		if( Boolean.valueOf(job.getVariable(INCREMENTAL_RESTART)) ){
			deleteIncrementalValue();
			job.setVariable(INCREMENTAL_RESTART , null);
		}
	}
	
	private String getInitIncrementalValue( String transName ){
		String initval = job.getVariable(INCREMENTAL_INIT_VALUE+"_"+transName);
		if(Utils.isEmpty(initval)) {
			initval = job.getVariable(INCREMENTAL_INIT_VALUE);
		}
		if(!Utils.isEmpty(initval) ) {
			return initval.trim();
		}
		if( incrementalTypeMap.getOrDefault(transName,5) == 3) {
			return "1970-01-01 00:00:00";
		}else {
			return "0" ;
		}
	}
	
	private String getIncrementalDateFormat(){
		String format = job.getVariable(INCREMENTAL_DATEFORMAT);
		if(Utils.isEmpty(format)) {
			format = "yyyy-MM-dd HH:mm:ss" ;
		}
		return format.trim() ;
	}
	

	/**
	 * 转换名1:输出步骤1.1,输出步骤1.2;转换名2:输出步骤2.1,输出步骤2.2,输出步骤2.3;
	 * @return
	 */
	private void initTransStepnMap(){
		String mapStr = job.getVariable(INCREMENTAL_TRANSSTEPMAP);
		if(!Utils.isEmpty(mapStr) ) {
			String[] tsMapStr = mapStr.split(";");
			for(String tsStr : tsMapStr) {
				if(Utils.isEmpty(tsStr)) {
					continue ;
				}
				String[] tss = tsStr.split(":",2);
				String tranName = Utils.isEmpty(tss[0]) ? Default_Unknow_Transname:tss[0] ;
				List<String> transList = transStepeMap.getOrDefault(tranName, Lists.newArrayList());
				if(tss.length > 1 && !Utils.isEmpty(tss[1])) {
					String[] steps = tss[1].split(",");
					for(String step : steps) {
						if(Utils.isEmpty(step)) {
							continue ;
						}
						transList.add(step.trim());
					}
				}
				transStepeMap.put(tranName.trim(), transList);
			}
		}
		//初始化outStep
		initOutStepName();
		//初始化 mainTrans
		initMainTrans();
	}
	

	private void initMainTrans(){
		String mainTrans = (String)job.getVariable(INCREMENTAL_MAINTRANSNAME);
		if(Utils.isEmpty(mainTrans)) {
			mainTrans = (String)job.getVariable("MainTransName");
		}
		if(Utils.isEmpty(mainTrans) && transStepeMap.size() == 0) {
			//当前还没有trans设置,查询最后的trans作为默认的trans
			for( JobEntryCopy entry : job.getJobMeta().getJobCopies()) {
				if(entry.getEntry() instanceof JobEntryTrans ){
					mainTrans = ((JobEntryTrans)entry.getEntry()).getTransname();
				}
			}
		}
		if(!Utils.isEmpty(mainTrans) && !transStepeMap.containsKey(mainTrans)) {
			transStepeMap.put(mainTrans.trim(), Lists.newArrayList());
		}
	}
	
	private void initOutStepName(){
		String outName = (String)job.getVariable(INCREMENTAL_OUTSTEPNAME);
		if(Utils.isEmpty(outName)) {
			outName = (String)job.getVariable("OutStepName");
		}
		if(!Utils.isEmpty(outName)  ) {
			List<String> transList = transStepeMap.getOrDefault(Default_Unknow_Transname, Lists.newArrayList());
			transList.add(outName.trim());
			transStepeMap.put(Default_Unknow_Transname, transList);
		}
	}
	

	
}
