package com.idatrix.unisecurity.common.utils;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ReadExcel {

	/**
	 * 对外提供读取 excel 的方法
	 * */
	public static List<List<Object>> readExcel(File file) throws IOException {
		String fileName = file.getName();
		String extension = fileName.lastIndexOf(".") == -1 ? "" : fileName
				.substring(fileName.lastIndexOf(".") + 1);
		if ("xls".equals(extension)) {
			return read2003Excel(file);
		} else if ("xlsx".equals(extension)) {
			return read2007Excel(file);
		} else {
			throw new IOException("不支持的文件类型");
		}
	}
	


	/**
	 * 读取 office 2003 excel
	 * @throws java.io.IOException
	 * @throws java.io.FileNotFoundException
	 */
	private static List<List<Object>> read2003Excel(File file)
			throws IOException {
		List<List<Object>> list = new LinkedList<List<Object>>();
		HSSFWorkbook hwb = new HSSFWorkbook(new FileInputStream(file));
		HSSFSheet sheet = hwb.getSheetAt(0);
		Object value = null;
		HSSFRow row = null;
		HSSFCell cell = null;
		int counter = 0;
		/*for (int i = sheet.getFirstRowNum(); counter < sheet
				.getPhysicalNumberOfRows(); i++) {*/
		for (int i = sheet.getFirstRowNum() + 1; counter < sheet.getLastRowNum(); i++) {
			row = sheet.getRow(i);
			if (row == null) {
				continue;
			} else {
				counter++;
			}
			List<Object> linked = new LinkedList<Object>();
			for (int j = row.getFirstCellNum(); j <= row.getLastCellNum(); j++) {
				cell = row.getCell(j);
				if (cell == null) {
					value="";
					linked.add(value);
					continue;
				}
				DecimalFormat df = new DecimalFormat("0");// 格式化 number String
				SimpleDateFormat sdf = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");// 格式化日期字符串
				DecimalFormat nf = new DecimalFormat("0.00");// 格式化数字
				switch (cell.getCellType()) {
					case XSSFCell.CELL_TYPE_STRING:
						value = cell.getStringCellValue();
						break;
					case XSSFCell.CELL_TYPE_NUMERIC:
						if ("@".equals(cell.getCellStyle().getDataFormatString())) {
						  //value = df.format(cell.getNumericCellValue());
							value = cell.getNumericCellValue();
						} else if ("General".equals(cell.getCellStyle()
								.getDataFormatString())) {
						  //value = nf.format(cell.getNumericCellValue());
							String doubleToStr=String.valueOf(cell.getNumericCellValue());
							value = doubleToStr.substring(0,doubleToStr.lastIndexOf("."));
						} else {
							value = sdf.format(HSSFDateUtil.getJavaDate(cell
									.getNumericCellValue()));
						}
						break;
					case XSSFCell.CELL_TYPE_BOOLEAN:
						value = cell.getBooleanCellValue();
						break;
					case XSSFCell.CELL_TYPE_BLANK:
						value = "";
						break;
					default:
						value = cell.toString();
				}
				if (value == null || "".equals(value)) {
					continue;
				}
				linked.add(value);
			}
			list.add(linked);
		}
		return list;
	}

	private static String convertDouble2Str(double d){
		BigDecimal bd = new BigDecimal(d);
		return bd.toPlainString();
	}

	/**
	 * 读取Office 2007 excel
	 * */
	private static List<List<Object>> read2007Excel(File file)
			throws IOException {
		List<List<Object>> list = new ArrayList<List<Object>>();
		XSSFWorkbook xwb = new XSSFWorkbook(new FileInputStream(file));
		XSSFSheet sheet = xwb.getSheetAt(0);
		Object value = null;
		XSSFRow row = null;
		XSSFCell cell = null;
		int counter = 0;
		/*for (int i = sheet.getFirstRowNum() + 1; counter < sheet
				.getPhysicalNumberOfRows(); i++) {*/
		for (int i = sheet.getFirstRowNum() + 1; i <= sheet.getLastRowNum(); i++) {
			
			row = sheet.getRow(i);
			if (row == null) {
				continue;
			}
			List<Object> arrays = new ArrayList<Object>();
			if(row.getFirstCellNum()>0){
				for(int k=0;k<row.getFirstCellNum();k++){
					arrays.add("");
				}
			}
			for (int j = row.getFirstCellNum(); j < row.getLastCellNum(); j++) {
				
				cell = row.getCell(j);
				/*if (cell == null) {
					continue;
				}*/
				//DecimalFormat df = new DecimalFormat("0");// 格式化 number String
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 格式化日期字符串
				//DecimalFormat nf = new DecimalFormat("0.00");// 格式化数字
				if (cell == null || StringUtils.isEmpty(cell.getRawValue())) {
//					System.out.println("第"+i+"行第"+j+"列为空");
					value="";
				}else {
					switch (cell.getCellType()) {
						case XSSFCell.CELL_TYPE_STRING:
							value = cell.getStringCellValue();
//							System.out.println("字符值为:"+value);
							break;
						case XSSFCell.CELL_TYPE_NUMERIC:
							if ("@".equals(cell.getCellStyle().getDataFormatString())) {
								value = cell.getNumericCellValue();
							} else if ("General".equals(cell.getCellStyle()
									.getDataFormatString())) {
								String doubleToStr = String.valueOf(cell.getNumericCellValue());
								if(doubleToStr.indexOf("E")!=-1){ //
									value= convertDouble2Str(cell.getNumericCellValue());
								}
								else {
									value = doubleToStr.substring(0, doubleToStr.lastIndexOf("."));
								}
							} else {
								    value = String.valueOf(cell.getNumericCellValue());
								    if(value !=null){
								    	String reg ="^[1-2]\\d{3}(-|/)?(0[1-9]|1[0-2])(-|/)?(0[1-9]|[1-2][0-9]|3[0-1])\\s+"+
								    "((2[0-3]|[0-1]\\d):[0-5]\\d:[0-5]\\d)?$";
								    	if(String.valueOf(value).matches(reg)){
								    		 value = sdf.format(HSSFDateUtil.getJavaDate(cell
														.getNumericCellValue()));
								    	}else{
								    		value = String.valueOf(value).substring(0, String.valueOf(value).lastIndexOf("."));
								    	}
								    }
							    }
							System.out.println("数字值为:"+value);
							break;
						case XSSFCell.CELL_TYPE_BOOLEAN:
							value = cell.getBooleanCellValue();
							System.out.println("布尔值为:"+value);
							break;
						case XSSFCell.CELL_TYPE_BLANK:
							value = "";
							break;
						default:
							value = String.valueOf(cell);
					}
				}
				arrays.add(value);
			}
			list.add(arrays);
		}
		return list;
	}
}
