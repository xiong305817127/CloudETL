package com.ys.idatrix.quality.steps.redundance;

import org.pentaho.di.core.RowSet;
import org.pentaho.di.core.database.Database;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.step.BaseStepData;
import org.pentaho.di.trans.step.StepDataInterface;

public class RedundanceData extends BaseStepData implements StepDataInterface {

	public RowMetaInterface outputRowMeta;
	
	public RowMetaInterface inputRowMeta ;

	public String owner ;
	public String execId ;
	
	public String  nodeType ;
	public String nodeName ;
	
	public Database db;
	public String dataSource;
	
	public RowSet rowSet ;
	public RowDataRegister rowRegister ;
	
	public String[] fields ;
	public String fieldStr ;
	public int[] fieldIndexes ;
	
	
	public long total = 0 ;
	public long noRepeat = 0 ;
	public String detailPath = "" ;
	
}
