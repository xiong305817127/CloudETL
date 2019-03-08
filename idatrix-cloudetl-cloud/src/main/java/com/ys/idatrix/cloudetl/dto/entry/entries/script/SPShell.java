/**
 * 云化数据集成系统 
 * iDatrxi CloudETL
 */
package com.ys.idatrix.cloudetl.dto.entry.entries.script;

import org.pentaho.di.core.logging.LogLevel;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.job.entries.shell.JobEntryShell;
import org.pentaho.di.job.entry.JobEntryCopy;
import org.pentaho.di.job.entry.JobEntryInterface;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ys.idatrix.cloudetl.dto.entry.entries.EntryParameter;
import com.ys.idatrix.cloudetl.ext.utils.StringEscapeHelper;

import net.sf.json.JSONObject;

/**
 * SPShell <br/>
 * @author JW
 * @since 2018年3月16日
 * 
 */
@Component("SPshell")
@Scope("prototype")
public class SPShell implements EntryParameter {

	private String filename;

	private String workDirectory;

	private String[] arguments;

	private boolean argFromPrevious;

	private boolean setLogfile;

	private String logfile, logext;

	private boolean addDate, addTime;

	private String logFileLevel = LogLevel.BASIC.getCode();

	private boolean execPerRow;

	private boolean setAppendLogfile;

	private boolean insertScript;

	private String script;

	/**
	 * @return filename
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * @param filename 要设置的 filename
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}

	/**
	 * @return workDirectory
	 */
	public String getWorkDirectory() {
		return workDirectory;
	}

	/**
	 * @param workDirectory 要设置的 workDirectory
	 */
	public void setWorkDirectory(String workDirectory) {
		this.workDirectory = workDirectory;
	}

	/**
	 * @return arguments
	 */
	public String[] getArguments() {
		return arguments;
	}

	/**
	 * @param arguments 要设置的 arguments
	 */
	public void setArguments(String[] arguments) {
		this.arguments = arguments;
	}

	/**
	 * @return argFromPrevious
	 */
	public boolean isArgFromPrevious() {
		return argFromPrevious;
	}

	/**
	 * @param argFromPrevious 要设置的 argFromPrevious
	 */
	public void setArgFromPrevious(boolean argFromPrevious) {
		this.argFromPrevious = argFromPrevious;
	}

	/**
	 * @return setLogfile
	 */
	public boolean isSetLogfile() {
		return setLogfile;
	}

	/**
	 * @param setLogfile 要设置的 setLogfile
	 */
	public void setSetLogfile(boolean setLogfile) {
		this.setLogfile = setLogfile;
	}

	/**
	 * @return logfile
	 */
	public String getLogfile() {
		return logfile;
	}

	/**
	 * @param logfile 要设置的 logfile
	 */
	public void setLogfile(String logfile) {
		this.logfile = logfile;
	}

	/**
	 * @return logext
	 */
	public String getLogext() {
		return logext;
	}

	/**
	 * @param logext 要设置的 logext
	 */
	public void setLogext(String logext) {
		this.logext = logext;
	}

	/**
	 * @return addDate
	 */
	public boolean isAddDate() {
		return addDate;
	}

	/**
	 * @param addDate 要设置的 addDate
	 */
	public void setAddDate(boolean addDate) {
		this.addDate = addDate;
	}

	/**
	 * @return addTime
	 */
	public boolean isAddTime() {
		return addTime;
	}

	/**
	 * @param addTime 要设置的 addTime
	 */
	public void setAddTime(boolean addTime) {
		this.addTime = addTime;
	}

	/**
	 * @return logFileLevel
	 */
	public String getLogFileLevel() {
		return logFileLevel;
	}

	/**
	 * @param logFileLevel 要设置的 logFileLevel
	 */
	public void setLogFileLevel(String logFileLevel) {
		this.logFileLevel = logFileLevel;
	}

	/**
	 * @return execPerRow
	 */
	public boolean isExecPerRow() {
		return execPerRow;
	}

	/**
	 * @param execPerRow 要设置的 execPerRow
	 */
	public void setExecPerRow(boolean execPerRow) {
		this.execPerRow = execPerRow;
	}

	/**
	 * @return setAppendLogfile
	 */
	public boolean isSetAppendLogfile() {
		return setAppendLogfile;
	}

	/**
	 * @param setAppendLogfile 要设置的 setAppendLogfile
	 */
	public void setSetAppendLogfile(boolean setAppendLogfile) {
		this.setAppendLogfile = setAppendLogfile;
	}

	/**
	 * @return insertScript
	 */
	public boolean isInsertScript() {
		return insertScript;
	}

	/**
	 * @param insertScript 要设置的 insertScript
	 */
	public void setInsertScript(boolean insertScript) {
		this.insertScript = insertScript;
	}

	/**
	 * @return script
	 */
	public String getScript() {
		return script;
	}

	/**
	 * @param script 要设置的 script
	 */
	public void setScript(String script) {
		this.script = script;
	}

	/*
	 * 覆盖方法：getParameterObject
	 */
	@Override
	public Object getParameterObject(Object json) {
		JSONObject jsonObj = JSONObject.fromObject(json);
		return (SPShell) JSONObject.toBean(jsonObj, SPShell.class);
	}

	/*
	 * 覆盖方法：encodeParameterObject
	 */
	@Override
	public Object encodeParameterObject(JobEntryCopy jobEntryCopy) throws Exception {
		JobEntryInterface entryMetaInterface = jobEntryCopy.getEntry() ;
		
		SPShell spShell = new SPShell();
		JobEntryShell jobentryshell = (JobEntryShell)entryMetaInterface;
		
		spShell.setAddDate(jobentryshell.addDate);
		spShell.setAddTime(jobentryshell.addTime);
		spShell.setArgFromPrevious(jobentryshell.argFromPrevious);
		spShell.setArguments(jobentryshell.arguments);
		spShell.setExecPerRow(jobentryshell.execPerRow);
		spShell.setFilename(jobentryshell.getFilename());
		spShell.setInsertScript(jobentryshell.insertScript);
		spShell.setLogext(jobentryshell.logext);
		spShell.setLogfile(jobentryshell.logfile);
		spShell.setLogFileLevel(jobentryshell.logFileLevel.getCode());
		spShell.setScript(StringEscapeHelper.encode(jobentryshell.script));
		spShell.setSetAppendLogfile(jobentryshell.setAppendLogfile);
		spShell.setSetLogfile(jobentryshell.setLogfile);
		spShell.setWorkDirectory(jobentryshell.getWorkDirectory());

		return spShell;
	}

	/*
	 * 覆盖方法：decodeParameterObject
	 */
	@Override
	public void decodeParameterObject(JobEntryCopy jobEntryCopy , Object po, JobMeta jobMeta) throws Exception {
		JobEntryInterface entryMetaInterface = jobEntryCopy.getEntry() ;
		
		SPShell spShell= (SPShell)po;
		JobEntryShell jobentryshell = (JobEntryShell)entryMetaInterface;

		jobentryshell.addDate = spShell.isAddDate();
		jobentryshell.addTime = spShell.isAddTime();
		jobentryshell.argFromPrevious = spShell.isArgFromPrevious();
		jobentryshell.arguments = spShell.getArguments();
		jobentryshell.execPerRow = spShell.isExecPerRow();
		jobentryshell.setFilename(spShell.getFilename());
		jobentryshell.insertScript = spShell.isInsertScript();
		jobentryshell.logext = spShell.getLogext();
		jobentryshell.logfile = spShell.getLogfile();
		jobentryshell.logFileLevel = LogLevel.getLogLevelForCode(spShell.getLogFileLevel()); //spShell.getLogFileLevel();
		jobentryshell.setScript(StringEscapeHelper.decode(spShell.getScript()));
		jobentryshell.setAppendLogfile = spShell.isSetAppendLogfile();
		jobentryshell.setLogfile = spShell.isSetLogfile();
		jobentryshell.setWorkDirectory(spShell.getWorkDirectory());
	}

}
