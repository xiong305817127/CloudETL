/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.quality.dto.entry.entries.script;

import org.pentaho.di.job.JobMeta;
import org.pentaho.di.job.entries.eval.JobEntryEval;
import org.pentaho.di.job.entry.JobEntryCopy;
import org.pentaho.di.job.entry.JobEntryInterface;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ys.idatrix.quality.dto.entry.entries.EntryParameter;
import com.ys.idatrix.quality.ext.utils.StringEscapeHelper;

import net.sf.json.JSONObject;

/**
 * Entry - Eval. 转换 org.pentaho.di.job.entries.eval.JobEntryEval
 * 
 * @author XH
 * @since 2017-06-29
 */
@Component("SPeval")
@Scope("prototype")
public class SPEval implements EntryParameter {

	String script;

	/**
	 * @return script
	 */
	public String getScript() {
		return script;
	}

	/**
	 * @param  设置 script
	 */
	public void setScript(String script) {
		this.script = script;
	}

	/* 
	 * 
	 */
	@Override
	public Object getParameterObject(Object json) {
		JSONObject jsonObj = JSONObject.fromObject(json);
		return (SPEval) JSONObject.toBean(jsonObj, SPEval.class);
	}

	/* 
	 * 
	 */
	@Override
	public Object encodeParameterObject(JobEntryCopy jobEntryCopy) throws Exception {
		JobEntryInterface entryMetaInterface = jobEntryCopy.getEntry() ;
		
		SPEval spEval= new SPEval();
		JobEntryEval jobentryeval= (JobEntryEval )entryMetaInterface;

		spEval.setScript(StringEscapeHelper.encode(jobentryeval.getScript()));
		return spEval;
	}

	/* 
	 * 
	 */
	@Override
	public void decodeParameterObject(JobEntryCopy jobEntryCopy , Object po, JobMeta jobMeta) throws Exception {
		JobEntryInterface entryMetaInterface = jobEntryCopy.getEntry() ;
		
		SPEval spEval= (SPEval)po;
		JobEntryEval  jobentryeval= (JobEntryEval )entryMetaInterface;

		jobentryeval.setScript(StringEscapeHelper.decode(spEval.getScript()));

	}

}
