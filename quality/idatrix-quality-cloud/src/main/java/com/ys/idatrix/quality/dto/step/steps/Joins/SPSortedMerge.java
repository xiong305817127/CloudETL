package com.ys.idatrix.quality.dto.step.steps.Joins;

import java.util.List;
import java.util.Map;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.sortedmerge.SortedMergeMeta;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;
import com.ys.idatrix.quality.dto.step.steps.StepParameter;
import com.ys.idatrix.quality.recovery.trans.ResumeStepDataParser;
import com.ys.idatrix.quality.recovery.trans.dto.StepLinesDto;
import com.ys.idatrix.quality.toolkit.analyzer.trans.step.StepDataRelationship;
import com.ys.idatrix.quality.toolkit.analyzer.trans.step.StepDataRelationshipParser;

import net.sf.json.JSONObject;

/**
 * Step - SortedMerge(排序合并). 转换 org.pentaho.di.trans.steps.sortedmerge.SortedMergeMeta
 * 
 * @author XH
 * @since 2018-04-11
 */
@Component("SPSortedMerge")
@Scope("prototype")
public class SPSortedMerge implements StepParameter, StepDataRelationshipParser, ResumeStepDataParser  {

//	private List<String> fieldNames;
//	private List<Boolean> ascendings;
	
	private Map<String,Boolean> fields ;

	
	/**
	 * @return the fields
	 */
	public Map<String, Boolean> getFields() {
		return fields;
	}

	/**
	 * @param  设置 fields
	 */
	public void setFields(Map<String, Boolean> fields) {
		this.fields = fields;
	}

	@Override
	public Object getParameterObject(Object json) {
		JSONObject jsonObj = JSONObject.fromObject(json);
		return (SPSortedMerge) JSONObject.toBean(jsonObj, SPSortedMerge.class);
	}

	@Override
	public Object encodeParameterObject(StepMeta stepMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();

		SPSortedMerge spSortedMerge = new SPSortedMerge();
		SortedMergeMeta sortedmergemeta = (SortedMergeMeta) stepMetaInterface;
		
		if(sortedmergemeta.getFieldName() != null && sortedmergemeta.getFieldName().length >0) {
			String[] fieldNames = sortedmergemeta.getFieldName() ;
			boolean[] ascendings = sortedmergemeta.getAscending() ;
			Map<String, Boolean> r =  Maps.newLinkedHashMap() ; 
			for(int i=0;i<fieldNames.length ;i++) {
				r.put(fieldNames[i], ascendings[i]);
			}
			spSortedMerge.setFields(r);
		}

		return spSortedMerge;
	}

	@Override
	public void decodeParameterObject(StepMeta stepMeta, Object po, List<DatabaseMeta> databases,TransMeta transMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPSortedMerge spSortedMerge= (SPSortedMerge)po;
		SortedMergeMeta  sortedmergemeta= (SortedMergeMeta )stepMetaInterface;
		
		Map<String, Boolean> r = spSortedMerge.getFields() ;
		if(r != null && r.size() > 0 ) {
			String[] fieldNames = r.keySet().toArray( new String[ r.size()] ) ;
			sortedmergemeta.setFieldName( fieldNames);
			
			boolean[] ascends = new boolean[r.size()] ;
			for(int i=0 ;i< r.size();i++) {
				ascends[i]=r.get(fieldNames[i]);
			}
			sortedmergemeta.setAscending( ascends );
		}else {
			sortedmergemeta.setFieldName( new String[] {});
			sortedmergemeta.setAscending(  new boolean[] {} );
		}
	}

	@Override
	public void getStepDataAndRelationship(TransMeta transMeta, StepMeta stepMeta, StepDataRelationship sdr)
			throws Exception {
		
	}

	@Override
	public void setLinesFromCacheLines(StepLinesDto lines , StepLinesDto cacheLines) {
			//该组件会预先读取一行,比较后将有效数据输入写出,所以写出的行号对应的读入需要减1
			Map<String, Long> preMap = cacheLines.getPreEffectiveInputLines();
			if(  preMap != null && preMap.size() > 0) {
				for(String key :preMap.keySet()) {
					if(preMap.get(key) >1) {
						preMap.put(key, preMap.get(key)-1);
					}
				}
			}
			lines.setPreEffectiveInputLines(preMap);
			lines.setLinesInput(cacheLines.getLinesInput());
			lines.setLinesOutput(cacheLines.getLinesOutput());
			lines.setLinesRead(cacheLines.getLinesRead());
			lines.setLinesWritten(cacheLines.getLinesWritten());
			lines.setLinesRejected(cacheLines.getLinesRejected());
			lines.setLinesUpdated(cacheLines.getLinesUpdated());
			lines.setLinesErrors(cacheLines.getLinesErrors());
	}
	
	@Override
	public int stepType() {
		return 12;
	}
}
