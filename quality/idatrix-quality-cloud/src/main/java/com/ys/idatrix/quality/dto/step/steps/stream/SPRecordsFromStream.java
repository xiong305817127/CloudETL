package com.ys.idatrix.quality.dto.step.steps.stream;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.rowsfromresult.RowsFromResultMeta;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ys.idatrix.quality.toolkit.analyzer.trans.step.StepDataRelationship;
import com.ys.idatrix.quality.toolkit.analyzer.trans.step.StepDataRelationshipParser;
import com.ys.idatrix.quality.toolkit.domain.Relationship;
import com.ys.idatrix.quality.toolkit.utils.RelationshipUtil;
import com.google.common.collect.Lists;
import com.ys.idatrix.quality.dto.step.parts.RowsFromResultDto;
import com.ys.idatrix.quality.dto.step.steps.StepParameter;
import com.ys.idatrix.quality.recovery.trans.ResumeStepDataParser;
import net.sf.json.JSONObject;

/**
 * Step - RecordsFromStream. 转换
 * org.pentaho.big.data.kettle.plugins.recordsfromstream.RecordsFromStreamMeta
 * 
 * @author XH
 * @since 2018-11-02
 */
@Component("SPRecordsFromStream")
@Scope("prototype")
public class SPRecordsFromStream implements StepParameter, StepDataRelationshipParser, ResumeStepDataParser {

	List<RowsFromResultDto> fieldnames;

	public List<RowsFromResultDto> getFieldnames() {
		return fieldnames;
	}

	public void setFieldnames(List<RowsFromResultDto> fieldnames) {
		this.fieldnames = fieldnames;
	}

	@Override
	public Object getParameterObject(Object json) {

		JSONObject jsonObj = JSONObject.fromObject(json);
		Map<String, Class<?>> classMap = new HashMap<>();
		classMap.put("fieldnames", RowsFromResultDto.class);
		return (SPRecordsFromStream) JSONObject.toBean(jsonObj, SPRecordsFromStream.class, classMap);
	}

	@Override
	public Object encodeParameterObject(StepMeta stepMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPRecordsFromStream sprecordsfromstream = new SPRecordsFromStream();
		RowsFromResultMeta recordsfromstreammeta = (RowsFromResultMeta) stepMetaInterface;

		List<RowsFromResultDto> rowsfromresultdtoList = Lists.newArrayList();
		String[] fieldnameMetaArray = recordsfromstreammeta.getFieldname();
		for (int i = 0; fieldnameMetaArray != null && i < fieldnameMetaArray.length; i++) {
			RowsFromResultDto tempobj = new RowsFromResultDto();
			tempobj.setFieldname(recordsfromstreammeta.getFieldname()[i]);
			tempobj.setType(recordsfromstreammeta.getType()[i]);
			tempobj.setLength(recordsfromstreammeta.getLength()[i]);
			tempobj.setPrecision(recordsfromstreammeta.getPrecision()[i]);
			rowsfromresultdtoList.add(tempobj);
		}
		sprecordsfromstream.setFieldnames(rowsfromresultdtoList);

		return sprecordsfromstream;
	}

	@Override
	public void decodeParameterObject(StepMeta stepMeta, Object po, List<DatabaseMeta> databases, TransMeta transMeta)
			throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPRecordsFromStream sprecordsfromstream = (SPRecordsFromStream) po;
		RowsFromResultMeta recordsfromstreammeta = (RowsFromResultMeta) stepMetaInterface;

		List<RowsFromResultDto> rowsfromresultdtoList = sprecordsfromstream.getFieldnames();
		if (rowsfromresultdtoList != null && rowsfromresultdtoList.size() > 0) {
			String[] fieldnamemetaArray = new String[rowsfromresultdtoList.size()];
			int[] typemetaArray = new int[rowsfromresultdtoList.size()];
			int[] lengthmetaArray = new int[rowsfromresultdtoList.size()];
			int[] precisionmetaArray = new int[rowsfromresultdtoList.size()];
			for (int i = 0; rowsfromresultdtoList != null && i < rowsfromresultdtoList.size(); i++) {
				fieldnamemetaArray[i] = rowsfromresultdtoList.get(i).getFieldname();
				typemetaArray[i] = rowsfromresultdtoList.get(i).getType();
				lengthmetaArray[i] = rowsfromresultdtoList.get(i).getLength();
				precisionmetaArray[i] = rowsfromresultdtoList.get(i).getPrecision();
			}
			recordsfromstreammeta.setFieldname(fieldnamemetaArray);
			recordsfromstreammeta.setType(typemetaArray);
			recordsfromstreammeta.setLength(lengthmetaArray);
			recordsfromstreammeta.setPrecision(precisionmetaArray);
		}
	}

	@Override
	public int stepType() {
		return 1;
	}

	@Override
	public void getStepDataAndRelationship(TransMeta transMeta, StepMeta stepMeta, StepDataRelationship sdr) throws Exception {

		String from = "转换:" + transMeta.getName() + ",步骤:" + stepMeta.getName();

		RowsFromResultMeta recordsfromstreammeta = (RowsFromResultMeta) stepMeta.getStepMetaInterface();
		String[] fieldNames = recordsfromstreammeta.getFieldname() ;
		for (int i = 0; fieldNames != null && i < fieldNames.length; i++) {
			String fieldName = fieldNames[i];
			String dummyId = transMeta.getName() + "-" + stepMeta.getName()  ;
			Relationship relationship = RelationshipUtil.buildDummyRelationship(from, dummyId, fieldName);

			sdr.addRelationship(relationship);
			sdr.addInputDataNode(relationship.getStartNode());
		}

	}

}
