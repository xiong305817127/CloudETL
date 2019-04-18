/**
 * 云化数据集成系统 iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.toolkit.analyzer.job;

import org.pentaho.di.job.JobMeta;

import com.ys.idatrix.cloudetl.toolkit.ToolkitTrigger;
import com.ys.idatrix.cloudetl.toolkit.record.AnalyzerReporter;

/**
 * TODO.
 * @author JW
 * @since 2017年5月24日
 *
 */
public class JobFlowParser {
	
	private final JobMeta jobMeta;

	private final ToolkitTrigger trigger;

	public JobFlowParser(JobMeta jobMeta,ToolkitTrigger trigger) {
		this.jobMeta = jobMeta;
		this.trigger = trigger;
	}

	public void parsing(AnalyzerReporter reporter) {
		// Start parsing
		//
		trigger.flushLog("Start parsing entry data & relationship ...");
		for (int i = 0; i < jobMeta.nrJobEntries(); i++) {
			jobMeta.getJobEntry(i);
			
			// Call trans flow parser if it's an entry for trans
			
			// Parse if any data or relationship for other entries
			
		}

		// Call other analyzers by order to improve the relationship rating
		//
		trigger.flushLog("Call other analyzers by order to improve entry data relationship rating ...");

		// Combine data nodes
		//
		trigger.flushLog("Check and combine all input & output data nodes ...");

		// Combine relationship
		//
		trigger.flushLog("Check and combine all relationship ...");
	}

	

}
