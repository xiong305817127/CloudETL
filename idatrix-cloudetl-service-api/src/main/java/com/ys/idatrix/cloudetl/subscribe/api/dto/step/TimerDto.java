package com.ys.idatrix.cloudetl.subscribe.api.dto.step;

import java.io.Serializable;

/**
 * 定时器对象 <br>
 * 支持方式: <br>
 * 1.小间隔  几分几秒 <br>
 * 2.每天          几时几分 <br>
 * 3.每周          星期几几点几分	 <br>
 * 4.每月          几号几点几分 <br>
 * 5.每季度    第几天几点几分 <br>
 * 6.每年        几月几日几点几分 <br>
 * 7.大间隔     每隔 几月几日 <br>
 * @author XH
 * @since 2018年8月8日
 *
 */
public class TimerDto  extends StepDto implements Serializable{

	private static final long serialVersionUID = -2087531994056537157L;

	public static  final String type ="SPECIAL";
	
	private boolean repeat = true;//是否循环，否则只执行一次
	/**
	 * 当repeat为true时循环，否则到点只执行一次             <br>
	 * 定时类型 :    <br>
	 * 0:不定时，执行完接着马上执行，         <br>
	 * 1:时间间隔(分，秒)，             <br>
	 * 2:天(时，分)，         	 <br>
	 * 3:周(周.时,分).      <br>
	 * 4:月(日,时,分)，        	 <br>
	 * 5:季度(日,时,分).     <br>
	 * 6:年(月,日,时,分). 	 <br>
	 * 7:年(月,日 ). 当intervalDelayMinutes为0时,间隔以每年一月一号为基础间隔, 当intervalDelayMinutes大于0时 以当前时间为基础间隔  ,eg . monthOfYear=6(月),	intervalDelayMinutes=0时,1月1日和7月1日会触发运行,intervalDelayMinutes>0时在当前时间基础上等待6个月再触发运行	 <br>
	 */
	private int schedulerType;
	
	private int seconds	= 0 ;	//秒(>=0)，schedulerType为1时有效，为间隔秒数
	private int minutes = 1	;	//分，schedulerType为1-6时有效，1时为等待分钟数(>=0)，2-6 时为整点几分(0-59)
	private int hour = 1	;	//时(0-23)，schedulerType为2-6时有效，为几点整
	private int weekDay = 1	;	//周(1-7)，schedulerType为3 时有效，为周几，周日~周六 ：1-7
	private int dayOfMonth =1;	//日(>=0)，schedulerType为4-7 时有效，4-6时为每月几号(1-31) ,7时为间隔几天(>=0)
	private int monthOfYear = 0;//月(>=0),schedulerType为6-7时有效,6时为每年几月(0-11),7时为间隔几个月(>=0)
	
	private int intervalDelayMinutes = 0; //小间隔方式时 ,首次执行推迟分钟数,大间隔方式时 为0以1月1日为基础计算,大于0时以当前时间为基础计算
	
	public boolean isRepeat() {
		return repeat;
	}

	public void setRepeat(boolean repeat) {
		this.repeat = repeat;
	}

	public int getSchedulerType() {
		return schedulerType;
	}

	public void setSchedulerType(int schedulerType) {
		this.schedulerType = schedulerType;
	}

	public int getSeconds() {
		return seconds;
	}

	public void setSeconds(int seconds) {
		this.seconds = seconds;
	}

	public int getMinutes() {
		return minutes;
	}

	public void setMinutes(int minutes) {
		this.minutes = minutes;
	}

	public int getHour() {
		return hour;
	}

	public void setHour(int hour) {
		this.hour = hour;
	}

	public int getWeekDay() {
		return weekDay;
	}

	public void setWeekDay(int weekDay) {
		this.weekDay = weekDay;
	}

	public int getDayOfMonth() {
		return dayOfMonth;
	}

	public void setDayOfMonth(int dayOfMonth) {
		this.dayOfMonth = dayOfMonth;
	}

	public int getMonthOfYear() {
		return monthOfYear;
	}

	public void setMonthOfYear(int monthOfYear) {
		this.monthOfYear = monthOfYear;
	}

	public int getIntervalDelayMinutes() {
		return intervalDelayMinutes;
	}

	public void setIntervalDelayMinutes(int intervalDelayMinutes) {
		this.intervalDelayMinutes = intervalDelayMinutes;
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public boolean isJobStep() {
		return true;
	}

	@Override
	public String toString() {
		return "TimerDto [repeat=" + repeat + ", schedulerType=" + schedulerType + ", seconds=" + seconds + ", minutes="
				+ minutes + ", hour=" + hour + ", weekDay=" + weekDay + ", dayOfMonth=" + dayOfMonth + ", monthOfYear="
				+ monthOfYear + ", intervalDelayMinutes=" + intervalDelayMinutes + ", toString()=" + super.toString()
				+ "]";
	}

}
