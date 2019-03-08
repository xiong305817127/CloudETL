/*! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2017 by Hitachi Vantara : http://www.pentaho.com
 *
 *******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************/

package org.pentaho.di.job.entries.special;

import java.util.Calendar;
import java.util.List;

import org.pentaho.di.cluster.SlaveServer;
import org.pentaho.di.core.CheckResultInterface;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.Result;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleDatabaseException;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleJobException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.job.entry.JobEntryBase;
import org.pentaho.di.job.entry.JobEntryInterface;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.Trans;
import org.pentaho.metastore.api.IMetaStore;
import org.w3c.dom.Node;

/**
 * This class can contain a few special job entries such as Start and Dummy.
 *
 * @author Matt
 * @since 05-11-2003
 *
 */

public class JobEntrySpecial extends JobEntryBase implements Cloneable, JobEntryInterface {
  public static final int NOSCHEDULING = 0;
  public static final int INTERVAL = 1;
  public static final int DAILY = 2;
  public static final int WEEKLY = 3;
  public static final int MONTHLY = 4;
  //xionghan
  public static final int QUARTERLY = 5; //每季度
  public static final int YEARLY = 6; //每年
  public static final int LARGEINTERVAL = 7; //大间隔 ,月日  ,INTERVAL是小间隔,分秒

  private boolean start;
  private boolean dummy;
  private boolean repeat = false;
  private int schedulerType = NOSCHEDULING;
  private int intervalSeconds = 0;
  private int intervalMinutes = 60;
  //xionghan
  private int intervalDelayMinutes = 0; //间隔运行推迟分钟数
  private boolean isFirst = true;
  private int monthOfYear = 0;
  
  private int dayOfMonth = 1;
  private int weekDay = 1;
  private int minutes = 0;
  private int hour = 1;

  public JobEntrySpecial() {
    this( null, false, false );
  }

  public JobEntrySpecial( String name, boolean start, boolean dummy ) {
    super( name, "" );
    this.start = start;
    this.dummy = dummy;
  }

  public Object clone() {
    JobEntrySpecial je = (JobEntrySpecial) super.clone();
    return je;
  }

  public String getXML() {
    StringBuilder retval = new StringBuilder( 200 );

    retval.append( super.getXML() );

    retval.append( "      " ).append( XMLHandler.addTagValue( "start", start ) );
    retval.append( "      " ).append( XMLHandler.addTagValue( "dummy", dummy ) );
    retval.append( "      " ).append( XMLHandler.addTagValue( "repeat", repeat ) );
    retval.append( "      " ).append( XMLHandler.addTagValue( "schedulerType", schedulerType ) );
    retval.append( "      " ).append( XMLHandler.addTagValue( "intervalSeconds", intervalSeconds ) );
    retval.append( "      " ).append( XMLHandler.addTagValue( "intervalMinutes", intervalMinutes ) );
    retval.append( "      " ).append( XMLHandler.addTagValue( "intervalDelayMinutes", intervalDelayMinutes ) );
    retval.append( "      " ).append( XMLHandler.addTagValue( "hour", hour ) );
    retval.append( "      " ).append( XMLHandler.addTagValue( "minutes", minutes ) );
    retval.append( "      " ).append( XMLHandler.addTagValue( "weekDay", weekDay ) );
    retval.append( "      " ).append( XMLHandler.addTagValue( "DayOfMonth", dayOfMonth ) );
    retval.append( "      " ).append( XMLHandler.addTagValue( "monthOfYear", monthOfYear ) );

    return retval.toString();
  }

  public void loadXML( Node entrynode, List<DatabaseMeta> databases, List<SlaveServer> slaveServers,
    Repository rep, IMetaStore metaStore ) throws KettleXMLException {
    try {
      super.loadXML( entrynode, databases, slaveServers );
      start = "Y".equalsIgnoreCase( XMLHandler.getTagValue( entrynode, "start" ) );
      dummy = "Y".equalsIgnoreCase( XMLHandler.getTagValue( entrynode, "dummy" ) );
      repeat = "Y".equalsIgnoreCase( XMLHandler.getTagValue( entrynode, "repeat" ) );
      setSchedulerType( Const.toInt( XMLHandler.getTagValue( entrynode, "schedulerType" ), NOSCHEDULING ) );
      setIntervalSeconds( Const.toInt( XMLHandler.getTagValue( entrynode, "intervalSeconds" ), 0 ) );
      setIntervalMinutes( Const.toInt( XMLHandler.getTagValue( entrynode, "intervalMinutes" ), 0 ) );
      setIntervalDelayMinutes( Const.toInt( XMLHandler.getTagValue( entrynode, "intervalDelayMinutes" ), 0 ) );
      setHour( Const.toInt( XMLHandler.getTagValue( entrynode, "hour" ), 0 ) );
      setMinutes( Const.toInt( XMLHandler.getTagValue( entrynode, "minutes" ), 0 ) );
      setWeekDay( Const.toInt( XMLHandler.getTagValue( entrynode, "weekDay" ), 0 ) );
      setDayOfMonth( Const.toInt( XMLHandler.getTagValue( entrynode, "dayOfMonth" ), 0 ) );
      setMonthOfYear( Const.toInt( XMLHandler.getTagValue( entrynode, "monthOfYear" ), 0 ) );
      
    } catch ( KettleException e ) {
      throw new KettleXMLException( "Unable to load job entry of type 'special' from XML node", e );
    }
  }

  public void loadRep( Repository rep, IMetaStore metaStore, ObjectId id_jobentry, List<DatabaseMeta> databases,
    List<SlaveServer> slaveServers ) throws KettleException {
    try {
      start = rep.getJobEntryAttributeBoolean( id_jobentry, "start" );
      dummy = rep.getJobEntryAttributeBoolean( id_jobentry, "dummy" );
      repeat = rep.getJobEntryAttributeBoolean( id_jobentry, "repeat" );
      schedulerType = (int) rep.getJobEntryAttributeInteger( id_jobentry, "schedulerType" );
      intervalSeconds = (int) rep.getJobEntryAttributeInteger( id_jobentry, "intervalSeconds" );
      intervalMinutes = (int) rep.getJobEntryAttributeInteger( id_jobentry, "intervalMinutes" );
      intervalDelayMinutes = (int) rep.getJobEntryAttributeInteger( id_jobentry, "intervalDelayMinutes" );
      hour = (int) rep.getJobEntryAttributeInteger( id_jobentry, "hour" );
      minutes = (int) rep.getJobEntryAttributeInteger( id_jobentry, "minutes" );
      weekDay = (int) rep.getJobEntryAttributeInteger( id_jobentry, "weekDay" );
      dayOfMonth = (int) rep.getJobEntryAttributeInteger( id_jobentry, "dayOfMonth" );
      monthOfYear = (int) rep.getJobEntryAttributeInteger( id_jobentry, "monthOfYear" );
      
    } catch ( KettleDatabaseException dbe ) {
      throw new KettleException( "Unable to load job entry of type 'special' from the repository for id_jobentry="
        + id_jobentry, dbe );
    }
  }

  // Save the attributes of this job entry
  //
  public void saveRep( Repository rep, IMetaStore metaStore, ObjectId id_job ) throws KettleException {
    try {
      rep.saveJobEntryAttribute( id_job, getObjectId(), "start", start );
      rep.saveJobEntryAttribute( id_job, getObjectId(), "dummy", dummy );
      rep.saveJobEntryAttribute( id_job, getObjectId(), "repeat", repeat );
      rep.saveJobEntryAttribute( id_job, getObjectId(), "schedulerType", schedulerType );
      rep.saveJobEntryAttribute( id_job, getObjectId(), "intervalSeconds", intervalSeconds );
      rep.saveJobEntryAttribute( id_job, getObjectId(), "intervalMinutes", intervalMinutes );
      rep.saveJobEntryAttribute( id_job, getObjectId(), "intervalDelayMinutes", intervalDelayMinutes );
      rep.saveJobEntryAttribute( id_job, getObjectId(), "hour", hour );
      rep.saveJobEntryAttribute( id_job, getObjectId(), "minutes", minutes );
      rep.saveJobEntryAttribute( id_job, getObjectId(), "weekDay", weekDay );
      rep.saveJobEntryAttribute( id_job, getObjectId(), "dayOfMonth", dayOfMonth );
      rep.saveJobEntryAttribute( id_job, getObjectId(), "monthOfYear", monthOfYear );

    } catch ( KettleDatabaseException dbe ) {
      throw new KettleException( "Unable to save job entry of type 'special' to the repository with id_job="
        + id_job, dbe );
    }
  }

  public boolean isStart() {
    return start;
  }

  public boolean isDummy() {
    return dummy;
  }

  public Result execute( Result previousResult, int nr ) throws KettleJobException {
    Result result = previousResult;

    if ( isStart() ) {
      try {
        long sleepTime = getNextExecutionTime();
        if ( sleepTime > 0 ) {
          parentJob.statusChange(Trans.STRING_WAITING, null);
          parentJob.getLogChannel().logBasic(
            parentJob.getJobname(),
            "Sleeping: " + ( sleepTime / 1000 / 60 ) + " minutes (sleep time=" + sleepTime + ")" );
          long totalSleep = 0L;
          while ( totalSleep < sleepTime && !parentJob.isStopped() ) {
            Thread.sleep( 1000L );
            totalSleep += 1000L;
          }
        }
      } catch ( InterruptedException e ) {
        throw new KettleJobException( e );
      }
      result = previousResult;
      result.setResult( true );
      parentJob.statusChange(Trans.STRING_RUNNING, null);
    } else if ( isDummy() ) {
      result = previousResult;
    }
    return result;
  }

  private long getNextExecutionTime() {
    switch ( schedulerType ) {
      case NOSCHEDULING:
        return 0;
      case INTERVAL:
        return getNextIntervalExecutionTime();
      case DAILY:
        return getNextDailyExecutionTime();
      case WEEKLY:
        return getNextWeeklyExecutionTime();
      case MONTHLY:
        return getNextMonthlyExecutionTime();
      case QUARTERLY:
          return getNextQuarterlyExecutionTime();
      case YEARLY:
          return getNextYearlyExecutionTime();
      case LARGEINTERVAL:
          return getNextLargeIntervalExecutionTime();  
          
      default:
        break;
    }
    return 0;
  }

  private long getNextIntervalExecutionTime() {
	  //xionghan
	  long delay = 0L;
	  if(isFirst && intervalDelayMinutes >0) {
		  delay = intervalDelayMinutes * 1000 * 60 ;
		  isFirst = false;
	  }
    return intervalSeconds * 1000 + intervalMinutes * 1000 * 60 + delay;
  }
  
  
  private long getNextLargeIntervalExecutionTime() {
	  
	  if(monthOfYear == 0 && dayOfMonth == 0  ) {
		  return 5000;
	  }
	   Calendar calendar = Calendar.getInstance();

	    long nowMillis = calendar.getTimeInMillis();
	    if(intervalDelayMinutes == 0 ) {
	    	//整点间隔,从一月一号一时一分开始计算
	    	calendar.set(Calendar.MONTH,0);
	    	calendar.set(Calendar.DAY_OF_MONTH,1);
	    	calendar.set(Calendar.AM_PM, Calendar.AM );
	    	calendar.set(Calendar.HOUR_OF_DAY,0);
	    	calendar.set(Calendar.MINUTE,0);
	    	calendar.set(Calendar.SECOND,0);
	    	
	    	while(calendar.getTimeInMillis() - nowMillis <= 0) {
	    		 calendar.add( Calendar.MONTH, monthOfYear );
	    		 calendar.add( Calendar.DAY_OF_MONTH, dayOfMonth );
	    	}
	    }else {
	    	//以当前时间为基础进行间隔计算
	    	 calendar.add( Calendar.MONTH, monthOfYear );
    		 calendar.add( Calendar.DAY_OF_MONTH, dayOfMonth );
    		 calendar.add(Calendar.MINUTE, intervalDelayMinutes);
	    }
	    return calendar.getTimeInMillis() - nowMillis;

	  }
  

  private long getNextQuarterlyExecutionTime() {
	    Calendar calendar = Calendar.getInstance();

	    long nowMillis = calendar.getTimeInMillis();
	    int amHour = hour;
	    if ( amHour > 12 ) {
	      amHour = amHour - 12;
	      calendar.set( Calendar.AM_PM, Calendar.PM );
	    } else {
	      calendar.set( Calendar.AM_PM, Calendar.AM );
	    }
	    calendar.set( Calendar.HOUR, amHour );
	    calendar.set( Calendar.MINUTE, minutes );
	    calendar.set(Calendar.DAY_OF_MONTH ,1);
	    //计算下一个季度的开始月
	    int curMonth = calendar.get(Calendar.MONTH);
	    int curQuarter =  ((Double)Math.floor(curMonth/3)).intValue() ; // 0 1 2 3  (季度)
	    int nextMonth = curQuarter < 3 ? ((curQuarter+1) * 3 ) : 0 ; //3 6 9 0  (月)
	    calendar.set( Calendar.MONTH, nextMonth );
	    if(dayOfMonth>1) {
	    	 calendar.add( Calendar.DAY_OF_MONTH, dayOfMonth-1 );
	    }
	    
	    return calendar.getTimeInMillis() - nowMillis;
	  }
  
  private long getNextYearlyExecutionTime() {
	    Calendar calendar = Calendar.getInstance();

	    long nowMillis = calendar.getTimeInMillis();
	    int amHour = hour;
	    if ( amHour > 12 ) {
	      amHour = amHour - 12;
	      calendar.set( Calendar.AM_PM, Calendar.PM );
	    } else {
	      calendar.set( Calendar.AM_PM, Calendar.AM );
	    }
	    calendar.set( Calendar.HOUR, amHour );
	    calendar.set( Calendar.MINUTE, minutes );
	    calendar.set( Calendar.DAY_OF_MONTH, dayOfMonth );
	    calendar.set( Calendar.MONTH, monthOfYear );
	    if ( calendar.getTimeInMillis() <= nowMillis ) {
	      calendar.add( Calendar.YEAR, 1 );
	    }
	    return calendar.getTimeInMillis() - nowMillis;
	  }
  
  private long getNextMonthlyExecutionTime() {
    Calendar calendar = Calendar.getInstance();

    long nowMillis = calendar.getTimeInMillis();
    int amHour = hour;
    if ( amHour > 12 ) {
      amHour = amHour - 12;
      calendar.set( Calendar.AM_PM, Calendar.PM );
    } else {
      calendar.set( Calendar.AM_PM, Calendar.AM );
    }
    calendar.set( Calendar.HOUR, amHour );
    calendar.set( Calendar.MINUTE, minutes );
    calendar.set( Calendar.DAY_OF_MONTH, dayOfMonth );
    if ( calendar.getTimeInMillis() <= nowMillis ) {
      calendar.add( Calendar.MONTH, 1 );
    }
    return calendar.getTimeInMillis() - nowMillis;
  }

  private long getNextWeeklyExecutionTime() {
    Calendar calendar = Calendar.getInstance();

    long nowMillis = calendar.getTimeInMillis();
    int amHour = hour;
    if ( amHour > 12 ) {
      amHour = amHour - 12;
      calendar.set( Calendar.AM_PM, Calendar.PM );
    } else {
      calendar.set( Calendar.AM_PM, Calendar.AM );
    }
    calendar.set( Calendar.HOUR, amHour );
    calendar.set( Calendar.MINUTE, minutes );
    calendar.set( Calendar.DAY_OF_WEEK, weekDay + 1 );
    if ( calendar.getTimeInMillis() <= nowMillis ) {
      calendar.add( Calendar.WEEK_OF_YEAR, 1 );
    }
    return calendar.getTimeInMillis() - nowMillis;
  }

  private long getNextDailyExecutionTime() {
    Calendar calendar = Calendar.getInstance();

    long nowMillis = calendar.getTimeInMillis();
    int amHour = hour;
    if ( amHour > 12 ) {
      amHour = amHour - 12;
      calendar.set( Calendar.AM_PM, Calendar.PM );
    } else {
      calendar.set( Calendar.AM_PM, Calendar.AM );
    }
    calendar.set( Calendar.HOUR, amHour );
    calendar.set( Calendar.MINUTE, minutes );
    if ( calendar.getTimeInMillis() <= nowMillis ) {
      calendar.add( Calendar.DAY_OF_MONTH, 1 );
    }
    return calendar.getTimeInMillis() - nowMillis;
  }

  public boolean evaluates() {
    return false;
  }

  public boolean isUnconditional() {
    return true;
  }

  public int getSchedulerType() {
    return schedulerType;
  }

  public int getHour() {
    return hour;
  }

  public int getMinutes() {
    return minutes;
  }

  public int getWeekDay() {
    return weekDay;
  }

  public int getDayOfMonth() {
    return dayOfMonth;
  }

  public void setDayOfMonth( int dayOfMonth ) {
    this.dayOfMonth = dayOfMonth;
  }

  public void setHour( int hour ) {
    this.hour = hour;
  }

  public void setMinutes( int minutes ) {
    this.minutes = minutes;
  }

  public void setWeekDay( int weekDay ) {
    this.weekDay = weekDay;
  }

  public void setSchedulerType( int schedulerType ) {
    this.schedulerType = schedulerType;
  }

  public boolean isRepeat() {
    return repeat;
  }

  public void setRepeat( boolean repeat ) {
    this.repeat = repeat;
  }

  public int getIntervalSeconds() {
    return intervalSeconds;
  }

  public void setIntervalSeconds( int intervalSeconds ) {
    this.intervalSeconds = intervalSeconds;
  }

  public int getIntervalMinutes() {
    return intervalMinutes;
  }

  public void setIntervalMinutes( int intervalMinutes ) {
    this.intervalMinutes = intervalMinutes;
  }

  public int getIntervalDelayMinutes() {
	return intervalDelayMinutes;
}

public void setIntervalDelayMinutes(int intervalDelayMinutes) {
	this.intervalDelayMinutes = intervalDelayMinutes;
}

public int getMonthOfYear() {
	return monthOfYear;
}

public void setMonthOfYear(int monthOfYear) {
	this.monthOfYear = monthOfYear;
}

/**
   * @param dummy
   *          the dummy to set
   */
  public void setDummy( boolean dummy ) {
    this.dummy = dummy;
  }

  /**
   * @param start
   *          the start to set
   */
  public void setStart( boolean start ) {
    this.start = start;
  }

  @Override
  public void check( List<CheckResultInterface> remarks, JobMeta jobMeta, VariableSpace space,
    Repository repository, IMetaStore metaStore ) {

  }

}
