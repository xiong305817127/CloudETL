package com.ys.idatrix.cloudetl.service.step;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.pentaho.di.core.util.IdatrixPropertyUtil;
import org.pentaho.di.core.util.Utils;
import org.pentaho.pms.util.Const;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.ys.idatrix.cloudetl.dto.step.parts.ElasticSearchBulkFieldDto;
import com.ys.idatrix.cloudetl.dto.step.parts.ElasticSearchBulkServerDto;
import com.ys.idatrix.cloudetl.dto.step.parts.ElasticSearchBulkSettingDto;
import com.ys.idatrix.cloudetl.dto.step.steps.bulkloading.SPElasticSearchBulk;
import com.ys.idatrix.cloudetl.dto.step.steps.script.SPScriptValueMod;
import com.ys.idatrix.cloudetl.dto.step.steps.transfor.SPConcatFields;
import com.ys.idatrix.cloudetl.subscribe.api.dto.parts.ElasticSearchServerDto;
import com.ys.idatrix.cloudetl.subscribe.api.dto.parts.InputFieldsDto;
import com.ys.idatrix.cloudetl.subscribe.api.dto.parts.OutputFieldsDto;
import com.ys.idatrix.cloudetl.subscribe.api.dto.step.ElasticSearchDto;
import com.ys.idatrix.cloudetl.subscribe.api.dto.step.FileInputDto;
import com.ys.idatrix.cloudetl.subscribe.api.dto.step.TableInputDto;

@Component
@Scope("prototype")
public class ElasticSearchService extends StepServiceInterface<ElasticSearchDto> {

	/**
	 * 创建ElasticSearch 的 StepParameter对象 <br>
	 * 当内容域在文件中指定了时 需要传入指定的内容域名,默认 content
	 */
	@Override
	public Object createParameter(Object... params) throws Exception {

		ElasticSearchDto elasticSearch = getStepDto();

		SPElasticSearchBulk esb = new SPElasticSearchBulk();

		esb.setIndex(elasticSearch.getIndex());
		esb.setType(elasticSearch.getIndexType());
		esb.setIdInField(elasticSearch.getIdInField());
		esb.setOverWriteIfSameId(elasticSearch.isOverWriteIfSameId());
		esb.setStopOnError(elasticSearch.isStopOnError());

		List<ElasticSearchBulkFieldDto> fields = new ArrayList<>();
		List<OutputFieldsDto> fieldsDto = elasticSearch.getFields();
		if (fieldsDto != null && fieldsDto.size() > 0) {
			for (OutputFieldsDto fieldDto : fieldsDto) {
				ElasticSearchBulkFieldDto dto = new ElasticSearchBulkFieldDto();
				dto.setName(fieldDto.getInputField());
				dto.setTargetName(fieldDto.getOutputField());
				fields.add(dto);
			}
		}
		// 增加全文搜索的 内容域
		String contentFieldName = (String) getParam(0, params);
		if( Utils.isEmpty(contentFieldName) ) {
			contentFieldName = (getPreviousStepDto(0) != null && (getPreviousStepDto(0) instanceof FileInputDto)) ? ((FileInputDto) getPreviousStepDto(0)).getContentFieldName() : "content";
		}
		contentFieldName = Const.NVL(contentFieldName, "content");

		ElasticSearchBulkFieldDto dto = new ElasticSearchBulkFieldDto();
		dto.setName(contentFieldName);
		dto.setTargetName(contentFieldName);
		fields.add(dto);

		esb.setFields(fields);

		List<ElasticSearchBulkServerDto> servers = new ArrayList<>();
		if (elasticSearch.getServers() == null || elasticSearch.getServers().isEmpty()) {
			ElasticSearchBulkServerDto server = new ElasticSearchBulkServerDto();
			server.setAddress(IdatrixPropertyUtil.getProperty("idatrix.elastic.search.default.ip", "127.0.0.1"));
			server.setPort(
					Integer.valueOf(IdatrixPropertyUtil.getProperty("idatrix.elastic.search.default.port", "9300")));
			servers.add(server);
		} else {
			for (ElasticSearchServerDto serverDto : elasticSearch.getServers()) {
				ElasticSearchBulkServerDto server = new ElasticSearchBulkServerDto();
				server.setAddress(serverDto.getAddress());
				server.setPort(serverDto.getPort());
				servers.add(server);
			}
		}
		esb.setServers(servers);

		List<ElasticSearchBulkSettingDto> settings = new ArrayList<>();
		if (elasticSearch.getSettings() != null && !elasticSearch.getSettings().isEmpty()) {
			for (Entry<String, String> settingDto : elasticSearch.getSettings().entrySet()) {
				ElasticSearchBulkSettingDto setting = new ElasticSearchBulkSettingDto();
				setting.setSetting(settingDto.getKey());
				setting.setValue(settingDto.getValue());
				settings.add(setting);
			}
		}
		if (elasticSearch.getSettings() == null || !elasticSearch.getSettings().containsKey("cluster.name")) {
			ElasticSearchBulkSettingDto setting = new ElasticSearchBulkSettingDto();
			setting.setSetting("cluster.name");
			setting.setValue(
					IdatrixPropertyUtil.getProperty("idatrix.elastic.search.default.clustername", "elasticsearch"));
			settings.add(setting);
		}
		esb.setSettings(settings);

		return esb;
	}

	@Override
	public List<String> addCurStepToMeta(String transName, String group, Map<String, String> execParams)
			throws Exception {

		ElasticSearchDto elasticSearch = getStepDto();
		List<String> contentFields = null;
		if (getPreviousStepDto(0) != null && getPreviousStepDto(0) instanceof FileInputDto) {
			FileInputDto fileInput = (FileInputDto) getPreviousStepDto(0);
			List<InputFieldsDto> fileFields = fileInput.getFields();
			if (fileFields != null && fileFields.size() > 0) {
				contentFields = fileFields.stream().map(f -> {
					return f.getFieldName();
				}).collect(Collectors.toList());
			}
		}else if (getPreviousStepDto(0) != null && getPreviousStepDto(0) instanceof TableInputDto)  {
			TableInputDto tableInput = (TableInputDto)getPreviousStepDto(0) ;
			contentFields = tableInput.getFields() ;
		}
		String contentFieldName = (getPreviousStepDto(0) != null && (getPreviousStepDto(0) instanceof FileInputDto)) ? ((FileInputDto) getPreviousStepDto(0)).getContentFieldName() : "content";
		contentFieldName = Const.NVL(contentFieldName, "content");

		List<String> outNames = Lists.newArrayList();
		String outputName = getStepName();

		if (contentFields != null && contentFields.size() > 0) {
			// 创建连接域
			String concatFieldsName = "es-ConcatFields";
			SPConcatFields concatFields = stepService.createConcatFields(contentFieldName, contentFields.toArray(new String[] {}));
			// 增加到TransMeta
			stepService.addAndUpdateStepMeta(transName, group, concatFieldsName, "ConcatFields", concatFields);
			outNames.add(concatFieldsName);
		}

		// 是否需要获取变量来获取常量值
		List<String> varis = new ArrayList<>();
		if (!Utils.isEmpty(elasticSearch.getIdInField())) {
			varis.add(elasticSearch.getIdInField());
		}
		if (elasticSearch.getFields() != null && elasticSearch.getFields().size() > 0) {
			for (OutputFieldsDto outField : elasticSearch.getFields()) {
				if (outField.getInputField().startsWith("${") && outField.getInputField().endsWith("}")) {
					// 获取变量
					String var = outField.getInputField().substring(2, outField.getInputField().length() - 1);
					varis.add(var);
					outField.setInputField(var);
				}
			}
		}

		if (varis != null && !varis.isEmpty()) {
			//
			String scriptName = "es-JavaScript";
			String script = "";
			for (String var : varis) {
				script += " var " + var + " = getVariable(\"" + var + "_\"+short_filename,getVariable(\"" + var + "\",\"\")); \n";
			}
			SPScriptValueMod scriptDto = stepService.createScript(script, varis.toArray(new String[] {}), new int[] { 2 }, false);
			// 增加到TransMeta
			stepService.addAndUpdateStepMeta(transName, group, scriptName, "ScriptValueMod", scriptDto);
			outNames.add(scriptName);
		}


		SPElasticSearchBulk esb = (SPElasticSearchBulk) createParameter(contentFieldName);

		// 增加到TransMeta
		stepService.addAndUpdateStepMeta(transName, group, outputName, elasticSearch.getType(), esb);
		outNames.add(outputName);

		return outNames;
	}

}
