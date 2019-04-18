/**
 * 云化数据集成系统 
 * iDatrxi CloudETL
 */
package com.ys.idatrix.quality.toolkit.utils;

import java.util.HashMap;
import java.util.Map;

import org.pentaho.di.core.Const;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaBase;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;

import com.ys.idatrix.quality.dto.step.StepFieldDto;
import com.ys.idatrix.quality.ext.utils.FieldValidator;
import com.ys.idatrix.quality.web.utils.SearchFieldsProgress;

/**
 * StepParserUtil <br/>
 * @author JW
 * @since 2018年1月29日
 * 
 */
public class StepParserUtil {
	
	public static Map<String, StepFieldDto> getInputFields(TransMeta transMeta, StepMeta stepMeta) {
		Map<String, StepFieldDto> jsfs = new HashMap<>();
		
		try {
			SearchFieldsProgress op = new SearchFieldsProgress( transMeta, stepMeta, true );
			op.run();
			RowMetaInterface rowMetaInterface = op.getFields();

			for (int i = 0; i < rowMetaInterface.size(); i++) {
				ValueMetaInterface v = rowMetaInterface.getValueMeta(i);
				StepFieldDto jsf = new StepFieldDto();
				jsf.setComments(Const.NVL(v.getComments(), ""));
				jsf.setConversionMask(Const.NVL(v.getConversionMask(), ""));
				jsf.setCurrencySymbol(Const.NVL(v.getCurrencySymbol(), ""));
				jsf.setDecimalSymbol(Const.NVL(v.getDecimalSymbol(), ""));
				jsf.setGroupingSymbol(Const.NVL(v.getGroupingSymbol(), ""));
				jsf.setLength("" + FieldValidator.fixedLength(v.getLength()));
				jsf.setName(v.getName());
				jsf.setOrigin(Const.NVL(v.getOrigin(), ""));
				jsf.setPrecision("" + FieldValidator.fixedPrecision(v.getPrecision()));
				jsf.setStorageType(ValueMetaBase.getStorageTypeCode(v.getStorageType()));
				jsf.setTrimType(ValueMetaBase.getTrimTypeCode(v.getTrimType()));
				jsf.setType(v.getTypeDesc());
				jsfs.put(v.getName(), jsf);
			}
		} catch (Exception ex) {
			//
		}

		return jsfs;
	}

	public static Map<String, StepFieldDto> getOutputFields(TransMeta transMeta, StepMeta stepMeta) {
		Map<String, StepFieldDto> jsfs = new HashMap<>();
		
		try {
			SearchFieldsProgress op = new SearchFieldsProgress( transMeta, stepMeta, false );
			op.run();
			RowMetaInterface rowMetaInterface = op.getFields();

			for (int i = 0; i < rowMetaInterface.size(); i++) {
				ValueMetaInterface v = rowMetaInterface.getValueMeta(i);
				StepFieldDto jsf = new StepFieldDto();
				jsf.setComments(Const.NVL(v.getComments(), ""));
				jsf.setConversionMask(Const.NVL(v.getConversionMask(), ""));
				jsf.setCurrencySymbol(Const.NVL(v.getCurrencySymbol(), ""));
				jsf.setDecimalSymbol(Const.NVL(v.getDecimalSymbol(), ""));
				jsf.setGroupingSymbol(Const.NVL(v.getGroupingSymbol(), ""));
				jsf.setLength("" + FieldValidator.fixedLength(v.getLength()));
				jsf.setName(v.getName());
				jsf.setOrigin(Const.NVL(v.getOrigin(), ""));
				jsf.setPrecision("" + FieldValidator.fixedPrecision(v.getPrecision()));
				jsf.setStorageType(ValueMetaBase.getStorageTypeCode(v.getStorageType()));
				jsf.setTrimType(ValueMetaBase.getTrimTypeCode(v.getTrimType()));
				jsf.setType(v.getTypeDesc());
				jsfs.put(v.getName(), jsf);
			}
		} catch (Exception ex) {
			//
		}

		return jsfs;
	}

}
