package com.ys.idatrix.cloudetl.service.step;

import java.util.List;
import java.util.Map;

import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.logging.CloudLogListener;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.job.IncrementalParser;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.ys.idatrix.cloudetl.dto.step.steps.input.SPTableInput;
import com.ys.idatrix.cloudetl.ext.CloudSession;
import com.ys.idatrix.cloudetl.ext.utils.StringEscapeHelper;
import com.ys.idatrix.cloudetl.reference.metacube.MetaCubeDatabase;
import com.ys.idatrix.cloudetl.subscribe.api.dto.step.TableInputDto;

@Component
@Scope("prototype")
public class TableInputService  extends StepServiceInterface<TableInputDto>{

	@Override
	public Object createParameter(Object... params) throws Exception {

		DatabaseMeta db = ( params!= null && params.length >0 )? (DatabaseMeta)params[0] : null ;
		
		TableInputDto tableInput = getStepDto();
		
		SPTableInput ti = new SPTableInput();
		ti.setDatabaseId(MetaCubeDatabase.getDatabaseIdFromMeta(db));
		ti.setConnection(db!= null?db.getName():"");
		ti.setSchemaId(MetaCubeDatabase.getSchemaIdFromMeta(db));
		ti.setSchema(db!= null?db.getPreferredSchemaName():"");
		ti.setTableType(tableInput.getTableType());
		ti.setTableId(tableInput.getTableId());
		ti.setTableName(tableInput.getTable());

		ti.setSql(StringEscapeHelper.encode(tableInput.getSql().toString()));
		ti.setVariablesActive(true);
		
		return ti;
	}

	@Override
	public List<String> addCurStepToMeta(String transName, String group, Map<String, String> params)
			throws Exception {
		
		List<String> outNames = Lists.newArrayList();
		TableInputDto tableInput = getStepDto();
		String inputName = getStepName();

		DatabaseMeta db = stepService.cloudDbService.getDatabaseMeta(CloudSession.getLoginUser(), tableInput.getSchemaId());
		if (db == null) {
			throw new Exception("数据库连接[" + tableInput.getSchemaId() + "]未找到,请检查是否创建或是否有权限!");
		}
		String incremental = tableInput.getIncremental();
		if (Utils.isEmpty(tableInput.getSql())) {
			
			String startVarName = IncrementalParser.DEFAULT_START_LIMIT_VAR_NAME;
			
			StringBuffer sql = new StringBuffer();
			// sql 为空，拼sql
			sql.append(" SELECT ");
			if (tableInput.getFields() != null && tableInput.getFields().size() > 0) {
				List<String> fields = tableInput.getFields();
				sql.append(" ").append(db.quoteField(fields.get(0))).append(" ");
				for (int i = 1; i < fields.size(); i++) {
					sql.append(", ").append(db.quoteField(fields.get(i))).append(" ");
				}
			} else {
				sql.append(" * ");
			}
			sql.append(" FROM ");
			sql.append(db.getQuotedSchemaTableCombination(db.getPreferredSchemaName(), tableInput.getTable()));
			sql.append(" WHERE 1=1 ");

			if (!Utils.isEmpty(tableInput.getWhere())) {
				sql.append(" AND ").append(tableInput.getWhere());
			}

			if (!Utils.isEmpty(incremental) && !Utils.isEmpty(tableInput.getIncrementalField())) {

				String incrementalInitValue = tableInput.getIncrementalInitValue();
				// 进行增量
				sql.append(" AND ");
				sql.append(db.quoteField(tableInput.getIncrementalField()));
				sql.append(" > ");

				if ("date".equals(incremental)) {
					if (Utils.isEmpty(incrementalInitValue)) {
						incrementalInitValue = "1970-01-01 00:00:00";
					}
					if ("ORACLE".equalsIgnoreCase(db.getPluginId())) {
						// to_date('${cloud_incremental_flag}' , 'yyyy-mm-dd hh24:mi:ss')
						sql.append("to_date('${").append(startVarName).append("}','yyyy-mm-dd hh24:mi:ss')");
					} else {
						// MYSQL DM7
						sql.append("'${").append(startVarName).append("}'");
					}
				} else { // if("sequence".equals(incremental)) {
					if (Utils.isEmpty(incrementalInitValue)) {
						incrementalInitValue = "0";
					}
					sql.append(" ${").append(startVarName).append("} ");
				}
				// 增加增量设置
				params.put(IncrementalParser.INCREMENTAL_FIELD, tableInput.getIncrementalField());
				params.put(IncrementalParser.INCREMENTAL_INIT_VALUE, incrementalInitValue);
				params.put(IncrementalParser.INCREMENTAL_MAINTRANSNAME, transName);// 主trans

			}

			if (!Utils.isEmpty(tableInput.getOrder())) {
				sql.append(" ORDER BY ").append(tableInput.getOrder());
			} else if (!Utils.isEmpty(tableInput.getIncrementalField())) {
				sql.append(" ORDER BY ").append(db.quoteField(tableInput.getIncrementalField()));
			}
			
			tableInput.setSql(sql.toString());
		}else if (!Utils.isEmpty(incremental) && !Utils.isEmpty(tableInput.getIncrementalField())) {
			params.putIfAbsent(IncrementalParser.INCREMENTAL_FIELD, tableInput.getIncrementalField());
			params.putIfAbsent(IncrementalParser.INCREMENTAL_INIT_VALUE, tableInput.getIncrementalInitValue());
			params.putIfAbsent(IncrementalParser.INCREMENTAL_MAINTRANSNAME, transName);// 主trans
		}
		
		SPTableInput ti= (SPTableInput)createParameter(db);
		// 增加到TransMeta
		stepService.addAndUpdateStepMeta(transName, group, inputName, tableInput.getType(), ti);
		params.put(CloudLogListener.LOG_INSTEPNAME, inputName);
		outNames.add(inputName);
		
		return outNames;
	}

	

}
