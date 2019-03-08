package com.ys.idatrix.cloudetl.service.step;

import java.util.List;
import java.util.Map;

import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.logging.CloudLogListener;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.ys.idatrix.cloudetl.dto.step.parts.TableOutputFieldDto;
import com.ys.idatrix.cloudetl.dto.step.steps.output.SPTableOutput;
import com.ys.idatrix.cloudetl.dto.step.steps.transfor.SPDesensitization;
import com.ys.idatrix.cloudetl.ext.CloudSession;
import com.ys.idatrix.cloudetl.reference.metacube.MetaCubeDatabase;
import com.ys.idatrix.cloudetl.subscribe.api.dto.parts.DesensitizationRuleDto;
import com.ys.idatrix.cloudetl.subscribe.api.dto.parts.OutputFieldsDto;
import com.ys.idatrix.cloudetl.subscribe.api.dto.step.TableOutputDto;

@Component
@Scope("prototype")
public class TableOutputService  extends StepServiceInterface<TableOutputDto> {

	@Override
	public Object createParameter(Object... params) throws Exception {
		
		DatabaseMeta db = ( params!= null && params.length >0 )? (DatabaseMeta)params[0] : null ;
		
		TableOutputDto tableOutput = getStepDto();
		
		SPTableOutput to = new SPTableOutput();
		to.setDatabaseId(MetaCubeDatabase.getDatabaseIdFromMeta(db));
		to.setConnection(db!= null?db.getName():"");
		to.setSchemaId(MetaCubeDatabase.getSchemaIdFromMeta(db));
		to.setSchema(db!= null?db.getPreferredSchemaName():"");
		to.setTableId(tableOutput.getTableId());
		to.setTable(tableOutput.getTable());

		List<OutputFieldsDto> fields = tableOutput.getFields();
		if (fields != null && fields.size() > 0) {
			List<TableOutputFieldDto> tofs = Lists.newArrayList();
			for (OutputFieldsDto f : fields) {
				TableOutputFieldDto tof = new TableOutputFieldDto();
				tof.setColumnName(f.getOutputField());
				tof.setStreamName(f.getInputField());
				tofs.add(tof);
			}
			to.setFields(tofs);
			to.setSpecifyFields(true);
		} else {
			to.setSpecifyFields(false);
		}

		return to ;
	}

	@Override
	public List<String> addCurStepToMeta(String transName, String group, Map<String, String> params)
			throws Exception {
		
		List<String> outNames = Lists.newArrayList();
		TableOutputDto tableOutput = getStepDto();
		String outputName = getStepName();
		
		DatabaseMeta db = stepService.cloudDbService.getDatabaseMeta(CloudSession.getLoginUser(), tableOutput.getSchemaId());
		if (db == null) {
			throw new Exception("数据库连接[" + tableOutput.getSchemaId() + "]未找到,请检查是否创建或是否有权限!");
		}
		
		List<OutputFieldsDto> outFields = tableOutput.getFields();
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

		SPTableOutput to = (SPTableOutput) createParameter(db);
		
		// 增加到TransMeta
		stepService.addAndUpdateStepMeta(transName, group, outputName, tableOutput.getType(), to);
		params.put(CloudLogListener.LOG_OUTSTEPNAME, outputName);
		outNames.add(outputName) ;
		
		return outNames;
	}

	

}
