package com.ys.idatrix.quality.steps.analysis.character;

import java.io.File;

import org.pentaho.di.trans.step.StepDataInterface;

import com.ys.idatrix.quality.reference.agentproxy.EsAgentService;
import com.ys.idatrix.quality.steps.analysis.base.AnalysisBaseData;

public class CharacterAnalysisData extends AnalysisBaseData implements StepDataInterface {

	String userName ;
	String analyzedDicId;
	String dictName;
	EsAgentService esAgentService;
	
	SynonymsAnalyzer analyzer;
	File synonymsFile ;
	String synonymsPath;
}
