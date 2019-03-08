/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.dto.entry.entries.general;

import org.pentaho.di.core.util.Utils;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.job.entries.special.JobEntrySpecial;
import org.pentaho.di.job.entry.JobEntryCopy;
import org.pentaho.di.job.entry.JobEntryInterface;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ys.idatrix.cloudetl.dto.entry.entries.EntryParameter;

import net.sf.json.JSONObject;

/**
 * Entry - special. 转换 org.pentaho.di.job.entries.special.JobEntrySpecial
 * 
 * @author XH
 * @since 2017-06-29
 */
@Component("SPspecial")
@Scope("prototype")
public class SPspecial implements EntryParameter {

	boolean start;
	boolean dummy;
	
	boolean repeat = false;
	int schedulerType = 0;
	int intervalSeconds = 0;
	int intervalMinutes = 60;
	int intervalDelayMinutes = 0;
	int hour = 1;
	int minutes = 0;
	int weekDay = 1;
	int dayOfMonth = 1;
	int monthOfYear = 0;
	
	
	public SPspecial(){
		initStart(true);
	}
	
	protected   void initStart(boolean isStart) {
		this.start = isStart;
		this.dummy = !isStart;
	}

	/**
	 * @return start
	 */
	public boolean isStart() {
		return start;
	}

	/**
	 * @return dummy
	 */
	public boolean isDummy() {
		return dummy;
	}


	/**
	 * @return repeat
	 */
	public boolean isRepeat() {
		return repeat;
	}

	/**
	 * @param 设置
	 *            repeat
	 */
	public void setRepeat(boolean repeat) {
		this.repeat = repeat;
	}

	/**
	 * @return schedulerType
	 */
	public int getSchedulerType() {
		return schedulerType;
	}

	/**
	 * @param 设置
	 *            schedulerType
	 */
	public void setSchedulerType(int schedulerType) {
		this.schedulerType = schedulerType;
	}

	/**
	 * @return intervalSeconds
	 */
	public int getIntervalSeconds() {
		return intervalSeconds;
	}

	/**
	 * @param 设置
	 *            intervalSeconds
	 */
	public void setIntervalSeconds(int intervalSeconds) {
		this.intervalSeconds = intervalSeconds;
	}

	/**
	 * @return intervalMinutes
	 */
	public int getIntervalMinutes() {
		return intervalMinutes;
	}

	/**
	 * @param 设置
	 *            intervalMinutes
	 */
	public void setIntervalMinutes(int intervalMinutes) {
		this.intervalMinutes = intervalMinutes;
	}

	/**
	 * @return hour
	 */
	public int getHour() {
		return hour;
	}

	/**
	 * @param 设置
	 *            hour
	 */
	public void setHour(int hour) {
		this.hour = hour;
	}

	/**
	 * @return minutes
	 */
	public int getMinutes() {
		return minutes;
	}

	/**
	 * @param 设置
	 *            minutes
	 */
	public void setMinutes(int minutes) {
		this.minutes = minutes;
	}

	/**
	 * @return weekDay
	 */
	public int getWeekDay() {
		return weekDay;
	}

	/**
	 * @param 设置
	 *            weekDay
	 */
	public void setWeekDay(int weekDay) {
		this.weekDay = weekDay;
	}

	/**
	 * @return dayOfMonth
	 */
	public int getDayOfMonth() {
		return dayOfMonth;
	}

	/**
	 * @param 设置
	 *            dayOfMonth
	 */
	public void setDayOfMonth(int dayOfMonth) {
		this.dayOfMonth = dayOfMonth;
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

	/* 
	 * 
	 */
	@Override
	public Object getParameterObject(Object json) {
		JSONObject jsonObj = JSONObject.fromObject(json);
		return (SPspecial) JSONObject.toBean(jsonObj, SPspecial.class);
	}

	/* 
	 * 
	 */
	@Override
	public Object encodeParameterObject(JobEntryCopy jobEntryCopy) throws Exception {
		JobEntryInterface entryMetaInterface = jobEntryCopy.getEntry() ;
		
		SPspecial spspecial = new SPspecial();
		JobEntrySpecial jobentryspecial = (JobEntrySpecial) entryMetaInterface;

		spspecial.setIntervalSeconds(jobentryspecial.getIntervalSeconds());
		spspecial.setIntervalMinutes(jobentryspecial.getIntervalMinutes());
		spspecial.setIntervalDelayMinutes(jobentryspecial.getIntervalDelayMinutes());
		spspecial.setSchedulerType(jobentryspecial.getSchedulerType());
		spspecial.setHour(jobentryspecial.getHour());
		spspecial.setMinutes(jobentryspecial.getMinutes());
		spspecial.setWeekDay(jobentryspecial.getWeekDay());
		spspecial.setDayOfMonth(jobentryspecial.getDayOfMonth());
		spspecial.setMonthOfYear(jobentryspecial.getMonthOfYear());
		spspecial.start = true ;//(jobentryspecial.isStart());
		spspecial.dummy = false ;//(jobentryspecial.isDummy());
		spspecial.setRepeat(jobentryspecial.isRepeat());
		return spspecial;
	}

	/* 
	 * 
	 */
	@Override
	public void decodeParameterObject(JobEntryCopy jobEntryCopy , Object po, JobMeta jobMeta) throws Exception {
		JobEntryInterface entryMetaInterface = jobEntryCopy.getEntry() ;
		
		SPspecial spspecial = (SPspecial) po;
		JobEntrySpecial jobentryspecial = (JobEntrySpecial) entryMetaInterface;

		jobentryspecial.setDummy(false);//spspecial.isDummy());
		jobentryspecial.setStart(true);//spspecial.isStart());
		jobentryspecial.setIntervalSeconds(spspecial.getIntervalSeconds());
		jobentryspecial.setIntervalMinutes(spspecial.getIntervalMinutes());
		jobentryspecial.setIntervalDelayMinutes(spspecial.getIntervalDelayMinutes());
		jobentryspecial.setSchedulerType(spspecial.getSchedulerType());
		jobentryspecial.setHour(spspecial.getHour());
		jobentryspecial.setMinutes(spspecial.getMinutes());
		jobentryspecial.setWeekDay(spspecial.getWeekDay());
		jobentryspecial.setDayOfMonth(spspecial.getDayOfMonth());
		jobentryspecial.setMonthOfYear(spspecial.getMonthOfYear());
		jobentryspecial.setRepeat(spspecial.isRepeat());
		
		if(spspecial.isRepeat()) {
			//"当执行失败时结束循环"
			String key = "exec.stop.when.step.fail" ;
			if( Utils.isEmpty( jobMeta.getParameterDefault(key) ) ){
				jobMeta.addParameterDefinition( key, "true", "当执行失败时结束循环");
			}
		}
		
		

	}

}
