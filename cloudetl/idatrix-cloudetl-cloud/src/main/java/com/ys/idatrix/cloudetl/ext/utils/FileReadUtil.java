package com.ys.idatrix.cloudetl.ext.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.lang.CharUtils;
import org.pentaho.di.core.spreadsheet.KCell;
import org.pentaho.di.core.spreadsheet.KCellType;
import org.pentaho.di.core.spreadsheet.KSheet;
import org.pentaho.di.core.spreadsheet.KWorkbook;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.core.vfs.KettleVFS;
import org.pentaho.di.trans.steps.excelinput.SpreadSheetType;
import org.pentaho.di.trans.steps.excelinput.WorkbookFactory;

import com.google.common.collect.Lists;

public class FileReadUtil {

	public static List<Object[]> readDataFromFile(String type, String fileName, InputStream in, File file,
			String encoding) throws Exception {
		try {

			if (Utils.isEmpty(encoding)) {
				encoding = "UTF-8";
			}

			if (Utils.isEmpty(fileName) && file != null) {
				fileName = file.getAbsolutePath();
			}

			if (Utils.isEmpty(type) && !Utils.isEmpty(fileName)) {
				 if (fileName.endsWith("xls") || fileName.endsWith("XLS") || fileName.endsWith("xlsx") || fileName.endsWith("XLSX")) {
					type = "excel";
				} else {
					type = "text";
				}
			}

			if (in == null && file != null) {
				in = KettleVFS.getInputStream(file.getAbsolutePath());
			} else if (in == null && file == null && !Utils.isEmpty(fileName)) {
				in = KettleVFS.getInputStream(fileName);
			}

			if (in == null) {
				return Lists.newArrayList();
			}

			switch (type) {
			case "text":
				return getDataFromText(in, encoding,",");
			case "excel":
				return getDataFromExcel(in, encoding);
			}

			return Lists.newArrayList();

		} finally {
			if(in != null ) {
				in.close();
			}
		}

	}

	public static List<Object[]> getDataFromText(InputStream in, String encoding,String splitStr) throws Exception {

         try ( BufferedReader br = new BufferedReader(new InputStreamReader(in));){
        	
             List<Object[]> result = new ArrayList<>();
             //读取到的内容给line变量
             String line = "";
             while ((line = br.readLine()) != null) {
            	 if( Utils.isEmpty(line)) {
            		 continue ;
            	 }
            	 result.add(line.split(splitStr));
             }
             return result;
         }
	}

	public static List<Object[]> getDataFromExcel(InputStream in, String encoding) throws Exception {

		List<Object[]> result = new ArrayList<>();
		
		SpreadSheetType spread = SpreadSheetType.POI;
		KWorkbook workbook = WorkbookFactory.getWorkbook(spread, in, encoding);
		try {
			int sheetNr = workbook.getNumberOfSheets();
			for(int i =0 ; i < sheetNr ; i++) {
				KSheet sheet = workbook.getSheet(i);
			
				int rowNr = sheet.getRows();
				for( int j = 0; j < rowNr ; j++) {
					KCell[] cells = sheet.getRow(j);
					
					Object[] rowObject = new Object[cells.length];
					// Set values in the row...
					for (int c = 0; c < cells.length; c++) {
						KCell cell = cells[c];
						if (cell == null) {
							rowObject[c] = "";
							continue;
						}

						KCellType cellType = cell.getType();
						if (KCellType.BOOLEAN == cellType || KCellType.BOOLEAN_FORMULA == cellType) {
							rowObject[c] = cell.getValue();
						} else {
							if (KCellType.DATE.equals(cellType) || KCellType.DATE_FORMULA.equals(cellType)) {
								Date date = (Date) cell.getValue();
								long time = date.getTime();
								int offset = TimeZone.getDefault().getOffset(time);
								rowObject[c] = new Date(time - offset);
							} else {
								if (KCellType.LABEL == cellType || KCellType.STRING_FORMULA == cellType) {
									// String string = (String) cell.getValue();
									rowObject[c] = cell.getValue() ;
								} else {
									if (KCellType.NUMBER == cellType || KCellType.NUMBER_FORMULA == cellType) {
										rowObject[c] = cell.getValue() ;
									} else if (KCellType.EMPTY == cellType) {
										rowObject[c] = "";
									} else {
										rowObject[c] =  cell.getContents();
									}
								}
							}
						}

					}
					result.add(rowObject);
				}
			}
		}finally {
			if( workbook != null ) {
				workbook.close();
			}
		}
		
		return result;
	}

	public static  boolean isPrintableChinese(char c) {
		if (CharUtils.isAsciiPrintable(c)) {
			return true;
		}

		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
		if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
				|| ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
				|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
				|| ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
			return true;
		}
		return false;
	}

}
