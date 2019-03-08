/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.service.trans.stepdetail;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang.StringUtils;
import org.pentaho.di.core.exception.KettlePluginException;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaFactory;
import org.pentaho.di.core.spreadsheet.KCell;
import org.pentaho.di.core.spreadsheet.KCellType;
import org.pentaho.di.core.spreadsheet.KSheet;
import org.pentaho.di.core.spreadsheet.KWorkbook;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.core.vfs.KettleVFS;
import org.pentaho.di.trans.steps.excelinput.SpreadSheetType;
import org.pentaho.di.trans.steps.excelinput.WorkbookFactory;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.ys.idatrix.cloudetl.dto.step.parts.ExcelInputsheetNameDto;
import com.ys.idatrix.cloudetl.ext.CloudSession;
import com.ys.idatrix.cloudetl.ext.utils.FilePathUtil;
import net.sf.json.JSONArray;

/**
 * ExcelInput related Detail Service
 * 
 * @author XH
 * @since 2017年6月12日
 *
 */
@SuppressWarnings("unchecked")
@Service
public class ExcelInputDetailService implements StepDetailService {

	@Override
	public String getStepDetailType() {
		return "ExcelInput";
	}

	/**
	 * flag: getSheets , getFields
	 * @throws Exception 
	 */
	@Override
	public List<Object> dealStepDetailByflag(String flag, Map<String, Object> param) throws Exception {
		if (StringUtils.isEmpty(flag)) {
			return null;
		}

		switch (flag) {
		case "getSheets":
			return getExcelSheets(param);
		case "getFields":
			return getExcelFields(param);
		default:
			return null;

		}
	}

	/**
	 * @param param
	 *            : fileName spreadSheetType encoding
	 * @return Excel Fields list
	 * @throws Exception 
	 */
	@SuppressWarnings("deprecation")
	private List<Object> getExcelFields(Map<String, Object> params) throws Exception {

		checkDetailParam(params, "fileName", "spreadSheetType", "encoding"); // sheetName

		String owner =  params.get("owner") == null || Utils.isEmpty( (String)params.get("owner"))  ? CloudSession.getResourceUser() : (String)params.get("owner");
		String fileName = params.get("fileName").toString();
		String filePath = FilePathUtil.getRealFileName(owner,fileName);

		String spreadSheetType = params.get("spreadSheetType").toString();
		String encoding = params.get("encoding").toString();

		List<ExcelInputsheetNameDto> eisndList = null;
		if (params.get("sheetName") != null) {
			JSONArray jsonArr = JSONArray.fromObject(params.get("sheetName"));
			eisndList = (List<ExcelInputsheetNameDto>) JSONArray.toList(jsonArr, ExcelInputsheetNameDto.class);
		}

		RowMetaInterface fields = new RowMeta();
		KWorkbook workbook = null;
		InputStream in = KettleVFS.getInputStream(filePath) ;
		try {
			workbook = WorkbookFactory.getWorkbook(SpreadSheetType.valueOf(spreadSheetType), in, encoding);
			processingWorkbook(fields, workbook, eisndList);
		} finally {
			if (in != null) {
				in.close();
			}
			if (workbook != null) {
				workbook.close();
			}
		}

		if (fields.size() > 0) {
			List<Object> result = Lists.newArrayList();
			for (int j = 0; j < fields.size(); j++) {
				ValueMetaInterface field = fields.getValueMeta(j);
				result.add(new String[] { field.getName(), field.getTypeDesc(), "", "", "none", "N" });
			}
			return result;
		} else {
			return null;
		}
	}

	/**
	 * @param param
	 *            : fileName , spreadSheetType
	 * @return Excel Sheets List
	 * @throws Exception 
	 */
	private List<Object> getExcelSheets(Map<String, Object> params) throws Exception {

		checkDetailParam(params, "fileName", "spreadSheetType");

		String owner =  params.get("owner") == null || Utils.isEmpty( (String)params.get("owner"))  ? CloudSession.getResourceUser() : (String)params.get("owner");
		String fileName = params.get("fileName").toString();
		String filePath = FilePathUtil.getRealFileName(owner ,fileName);

		String spreadSheetType = params.get("spreadSheetType").toString();

		List<Object> sheetnames = new ArrayList<Object>();

		KWorkbook workbook = null;
		InputStream in = KettleVFS.getInputStream(filePath) ;
		try {
			workbook = WorkbookFactory.getWorkbook(SpreadSheetType.valueOf(spreadSheetType),in, "GBK");

			int nrSheets = workbook.getNumberOfSheets();
			for (int j = 0; j < nrSheets; j++) {
				KSheet sheet = workbook.getSheet(j);
				String sheetname = sheet.getName();

				if (!sheetnames.contains(sheetname)) {
					sheetnames.add(sheetname);
				}
			}

		}  finally {
			if (in != null) {
				in.close();
			}
			if (workbook != null) {
				workbook.close();
			}
		}

		return sheetnames;

	}

	/**
	 * 
	 * @param fields
	 * @param workbook
	 * @param startColumn
	 * @param startRow
	 * @param sheetIndex
	 *            0:默认
	 * @throws KettlePluginException
	 */
	private void processingWorkbook(RowMetaInterface fields, KWorkbook workbook,
			List<ExcelInputsheetNameDto> sheetNames) throws KettlePluginException {
		int nrSheets = workbook.getNumberOfSheets();
		for (int j = 0; j < nrSheets; j++) {
			KSheet sheet = workbook.getSheet(j);

			// See if it's a selected sheet:
			ExcelInputsheetNameDto eisnd;
			if (sheetNames == null || sheetNames.size() == 0) {
				eisnd = null;
			} else {
				Optional<ExcelInputsheetNameDto> shopt = sheetNames.stream()
						.filter(sn -> sheet.getName().equals(sn.getSheetName())).findFirst();
				if (shopt.isPresent()) {
					eisnd = shopt.get();
				} else {
					continue;
				}

			}
			int rownr = 0;
			int startcol = 0;

			if (eisnd != null) {
				rownr = eisnd.getStartRow();
				startcol = eisnd.getStartColumn();
			}

			boolean stop = false;
			for (int colnr = startcol; !stop; colnr++) {
				try {
					String fieldname = null;
					int fieldtype = ValueMetaInterface.TYPE_NONE;

					KCell cell = sheet.getCell(colnr, rownr);
					if (cell == null) {
						stop = true;
					} else {
						if (cell.getType() != KCellType.EMPTY) {
							// We found a field.
							fieldname = cell.getContents();
						}

						// System.out.println("Fieldname = "+fieldname);

						KCell below = sheet.getCell(colnr, rownr + 1);

						if (below != null) {
							if (below.getType() == KCellType.BOOLEAN) {
								fieldtype = ValueMetaInterface.TYPE_BOOLEAN;
							} else if (below.getType() == KCellType.DATE) {
								fieldtype = ValueMetaInterface.TYPE_DATE;
							} else if (below.getType() == KCellType.LABEL) {
								fieldtype = ValueMetaInterface.TYPE_STRING;
							} else if (below.getType() == KCellType.NUMBER) {
								fieldtype = ValueMetaInterface.TYPE_NUMBER;
							} else {
								fieldtype = ValueMetaInterface.TYPE_STRING;
							}
						} else {
							fieldtype = ValueMetaInterface.TYPE_STRING;
						}

						if (Utils.isEmpty(fieldname)) {
							stop = true;
						} else {
							if (fieldtype != ValueMetaInterface.TYPE_NONE) {
								ValueMetaInterface field = ValueMetaFactory.createValueMeta(fieldname, fieldtype);
								fields.addValueMeta(field);
							}
						}
					}
				} catch (ArrayIndexOutOfBoundsException aioobe) {
					// System.out.println("index out of bounds at column
					// "+colnr+" : "+aioobe.toString());
					stop = true;
				}
			}
		}
	}

}
