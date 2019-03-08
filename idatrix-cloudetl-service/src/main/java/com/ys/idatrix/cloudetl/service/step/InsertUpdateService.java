package com.ys.idatrix.cloudetl.service.step;

import java.util.List;
import java.util.Map;

import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.logging.CloudLogListener;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.ys.idatrix.cloudetl.dto.step.parts.InsertUpdatekeyStreamDto;
import com.ys.idatrix.cloudetl.dto.step.parts.InsertUpdateupdateLookupDto;
import com.ys.idatrix.cloudetl.dto.step.steps.input.SPGetVariable;
import com.ys.idatrix.cloudetl.dto.step.steps.output.SPInsertUpdate;
import com.ys.idatrix.cloudetl.dto.step.steps.transfor.SPDesensitization;
import com.ys.idatrix.cloudetl.ext.CloudSession;
import com.ys.idatrix.cloudetl.reference.metacube.MetaCubeDatabase;
import com.ys.idatrix.cloudetl.subscribe.api.dto.parts.DesensitizationRuleDto;
import com.ys.idatrix.cloudetl.subscribe.api.dto.parts.OutputFieldsDto;
import com.ys.idatrix.cloudetl.subscribe.api.dto.parts.SearchFieldsDto;
import com.ys.idatrix.cloudetl.subscribe.api.dto.step.InsertUpdateDto;

@Component
@Scope("prototype")
public class InsertUpdateService  extends StepServiceInterface<InsertUpdateDto> {

	@Override
	public Object createParameter(Object... params) throws Exception {
		
		DatabaseMeta db = ( params!= null && params.length >0 )? (DatabaseMeta)params[0] : null ;
		
		InsertUpdateDto insertUpdate = getStepDto();
		
		SPInsertUpdate iu = new SPInsertUpdate();
		iu.setDatabaseId(MetaCubeDatabase.getDatabaseIdFromMeta(db));
		iu.setConnection(db!= null?db.getName():"");
		iu.setSchemaId(MetaCubeDatabase.getSchemaIdFromMeta(db));
		iu.setSchema(db!= null?db.getPreferredSchemaName():"");
		iu.setTableId(insertUpdate.getTableId());
		iu.setTable(insertUpdate.getTable());

		List<SearchFieldsDto> sfs = insertUpdate.getSearchFields();
		if (sfs != null && sfs.size() > 0) {
			List<InsertUpdatekeyStreamDto> iukss = Lists.newArrayList();
			for (SearchFieldsDto sf : sfs) {
				InsertUpdatekeyStreamDto ius = new InsertUpdatekeyStreamDto();
				ius.setKeyLookup(sf.getOutputField());
				ius.setKeyCondition(sf.getCondition());
				ius.setKeyStream1(sf.getInputField());
				ius.setKeyStream2(sf.getInputField2());
				iukss.add(ius);
			}
			iu.setSearchFields(iukss);
		}

		List<OutputFieldsDto> ofs = insertUpdate.getUpdateFields();
		if (ofs != null && ofs.size() > 0) {
			List<InsertUpdateupdateLookupDto> iuuls = Lists.newArrayList();
			for (OutputFieldsDto of : ofs) {
				InsertUpdateupdateLookupDto iuul = new InsertUpdateupdateLookupDto();
				iuul.setUpdate(of.isUpdate());
				iuul.setUpdateLookup(of.getOutputField());
				iuul.setUpdateStream(of.getInputField());
				iuuls.add(iuul);
			}
			iu.setUpdateFields(iuuls);
		}
		if (insertUpdate.isIncloudFlag()) {
			iu.setSyncFlagFieldName(insertUpdate.getFlagFieldName());
		}
		if (insertUpdate.isIncloudTime()) {
			iu.setSyncTimeFieldName(insertUpdate.getTimeFieldName());
		}
		
		return iu ;
	}

	@Override
	public List<String> addCurStepToMeta(String transName, String group, Map<String, String> params)
			throws Exception {
		List<String> outNames = Lists.newArrayList();
		InsertUpdateDto insertUpdate = getStepDto();
		String outputName = getStepName();

		DatabaseMeta db = stepService.cloudDbService.getDatabaseMeta( CloudSession.getLoginUser(), insertUpdate.getSchemaId());
		if (db == null) {
			throw new Exception("数据库连接[" + insertUpdate.getSchemaId() + "]未找到,请检查是否创建或是否有权限!");
		}

		// 判断是否创建获取变量
		if (insertUpdate.isIncloudBatch()) {
			String getVarName = "iu-GetVariable";
			SPGetVariable getVar = stepService.createGetVariable(new String[] { insertUpdate.getBatchFieldName() },
					new String[] { insertUpdate.getBatchValueKey() }, 2);
			// 增加到TransMeta
			stepService.addAndUpdateStepMeta(transName, group, getVarName, "GetVariable", getVar);
			outNames.add(getVarName);

			// 判断插入更新字段是否包含批次号信息
			OutputFieldsDto batchField = new OutputFieldsDto(insertUpdate.getBatchFieldName(),
					insertUpdate.getBatchFieldName());
			if (insertUpdate.getUpdateFields() == null || !insertUpdate.getUpdateFields().contains(batchField)) {
				insertUpdate.addUpdateField(batchField);
			}

			// 包含 批次信息
			params.put(insertUpdate.getBatchValueKey(), "-");
		}

		
		List<OutputFieldsDto> outFields = insertUpdate.getUpdateFields();
		if( outFields != null && outFields.size() > 0 ) {
			//增加数据脱敏组件
			List<String> inFs = Lists.newArrayList();
			List<String> outFs = Lists.newArrayList();
			List<DesensitizationRuleDto> drs = Lists.newArrayList();
			outFields.stream().filter(out -> { return out.getDesensitizationRule() != null; } ).forEach(out -> { 
				String in_rule = out.getInputField();
				String out_rule = in_rule+"_Rule";
				
				out.setInputField(out_rule);
				inFs.add( in_rule );
				outFs.add( out_rule );
				drs.add( out.getDesensitizationRule() );
			});
			if( inFs.size() > 0 ) {
				//增加数据脱敏组件
				String DesensitizationName = "Desensitization";
				SPDesensitization desenstitizationDto = stepService.createDesensitization(inFs.toArray(new String[0]) , outFs.toArray(new String[0]), drs.toArray(new DesensitizationRuleDto[0])) ;
				// 增加到TransMeta
				stepService.addAndUpdateStepMeta(transName, group, DesensitizationName, "Desensitization", desenstitizationDto);
				outNames.add(DesensitizationName);
			}
		}

		SPInsertUpdate iu = (SPInsertUpdate) createParameter(db);
		// 增加到TransMeta
		stepService.addAndUpdateStepMeta(transName, group, outputName, insertUpdate.getType(), iu);
		outNames.add(outputName);
		params.put(CloudLogListener.LOG_OUTSTEPNAME, outputName);

		return outNames;
	}

	

}
