/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.service.trans.stepdetail;

import java.io.File;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.trans.steps.accessinput.AccessInputMeta;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.healthmarketscience.jackcess.Column;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.Table;
import com.ys.idatrix.cloudetl.ext.utils.FilePathUtil;

/**
 * AccessInput related Detail Service
 * 
 * @author XH
 * @since 2017年6月9日
 *
 */
@Service
public class AccessInputDetailService implements StepDetailService {

	@Override
	public String getStepDetailType() {
		return "AccessInput";
	}

	/**
	 * flag : getTables , getFields
	 * 
	 * @throws Exception
	 */
	@Override
	public Object dealStepDetailByflag(String flag, Map<String, Object> param) throws Exception {
		if (StringUtils.isEmpty(flag)) {
			return null;
		}

		switch (flag) {
		case "getTables":
			return getAccessTables(param);
		case "getFields":
			return getAccessFields(param);
		default:
			return null;

		}

	}

	/**
	 * @param fileName
	 *            tableName
	 * @return Access Fields list
	 * @throws Exception
	 */
	private List<Object> getAccessFields(Map<String, Object> params) throws Exception {

		checkDetailParam(params, "fileName", "tableName");

		Database d = null;
		try {
			String fileName = params.get("fileName").toString();
			 URI filePath = FilePathUtil.getRealFileURI(fileName);
			String tableName = params.get("tableName").toString();

			RowMetaInterface fields = new RowMeta();
			d = Database.open(new File(filePath), true);

			Table t = null;
			if (tableName.startsWith(AccessInputMeta.PREFIX_SYSTEM)) {
				t = d.getSystemTable(tableName);
			} else {
				t = d.getTable(tableName);
			}
			if( t ==null ){
				throw new KettleException("tableName "+tableName+" 未找到!");
			}
			// Get the list of columns
			List<Column> col = t.getColumns();
			int nr = col.size();
			for (int i = 0; i < nr; i++) {
				Column c = col.get(i);

				ValueMetaInterface field = AccessInputMeta.getValueMeta(c);
				if (field != null && fields.indexOfValue(field.getName()) < 0) {
					fields.addValueMeta(field);
				}
			}

			List<Object> result = Lists.newArrayList();
			for (int j = 0; j < fields.size(); j++) {
				ValueMetaInterface field = fields.getValueMeta(j);
				result.add(new String[] { field.getName(), field.getName(), field.getType()+"", "", "-1", "", "", "",
						"", "0", "N" });
			}

			return result;

		} finally {
			// Don't forget to close the bugger.
			try {
				if (d != null) {
					d.close();
				}
			} catch (Exception e) {
				// Ignore close errors
			}
		}
	}

	/**
	 * @param fileName
	 * @return Access Tables List
	 * @throws Exception 
	 */
	private Object getAccessTables(Map<String, Object> params) throws Exception {

		checkDetailParam(params, "fileName");

		String fileName = params.get("fileName").toString();
		 URI filePath = FilePathUtil.getRealFileURI(fileName);

		try (Database accessDatabase = Database.open(new File(filePath), true);) {
			Set<String> settables = accessDatabase.getTableNames();
			settables.addAll(accessDatabase.getSystemTableNames());
			 return settables;
		}

	}

}
