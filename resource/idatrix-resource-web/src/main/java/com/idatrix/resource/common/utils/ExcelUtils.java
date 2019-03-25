package com.idatrix.resource.common.utils;

import com.idatrix.resource.catalog.vo.ResourceConfigVO;
import com.idatrix.resource.common.vo.ExcelUtilsInfo;
import com.idatrix.resource.datareport.vo.BrowseDataVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;


@Slf4j
public class ExcelUtils {

    /**
     * 校验文件是否为03版本 Excel表格
     * @param fileName
     * @return
     */
    public static boolean verifyExcel2003(String fileName){
        boolean flag = false;
        if(fileName.matches("^.+\\.(?i)(xls)$")){     //2003xlsx
            flag = true;
        }
        return flag;
    }

    /**
     * 校验文件是否为07版本 Excel表格
     * @param fileName
     * @return
     */
    public static boolean verifyExcel2007(String fileName){
        boolean flag = false;
        if(fileName.matches("^.+\\.(?i)(xlsx)$")){     //2007xlsx
            flag = true;
        }
        return flag;
    }

    /**
     * 根据后缀名称校验文件是否为Excel文件
     * @param fileName
     * @return
     */
    public static boolean verifyExcel(String fileName){
        boolean flag = false;
        if(fileName.matches("^.+\\.(?i)(xls)$")||     //2003xls
                fileName.matches("^.+\\.(?i)(xlsx)$")){     //2007xlsx
            flag = true;
        }
        return flag;
    }

    /**
     * 读EXCEL文件，获取信息集合
     * @param mfile
     * @return
     */
    public static Workbook getExcelInfoFile(File mfile) throws Exception{

        String fileName = mfile.getAbsolutePath();
        if (!verifyExcel(fileName)) {// 验证文件名是否合格
            throw new Exception("文件格式不符合要求");
        }
        List<ResourceConfigVO> rcVOList = null;
        Workbook wb = null;
        try {
            if (fileName.matches("^.+\\.(?i)(xls)$")) {// 当excel是2003时,创建excel2003
                wb = new HSSFWorkbook(new FileInputStream(mfile));
            } else {// 当excel是2007时,创建excel2007
                wb = new XSSFWorkbook(new FileInputStream(mfile));
            }
        } catch (Exception e) {
            throw e;
//            e.printStackTrace();
        }
        return wb;
    }


    public static List<String[]> getExcelFormData(String filePath)throws Exception{

        Workbook wb = ExcelUtils.getExcelInfoFile(new File(filePath));
        Sheet sheet = wb.getSheetAt(0);
        int totalRows = sheet.getPhysicalNumberOfRows();
        int totalCells = 0;
        if (totalRows > 1 && sheet.getRow(0) != null) {
            totalCells = sheet.getRow(0).getPhysicalNumberOfCells();
        }

        boolean contentFlag = false;

        List<String[]> formDataList = new ArrayList<>();
        // 循环Excel行数
        for (int r = 0; r < totalRows; r++) {
            Row row = sheet.getRow(r);
            if (row == null){
                continue;
            }
            // 循环Excel的列
            String[] cellData = new String[totalCells];
            for (int c = 0; c < totalCells; c++) {
                cellData[c] = null;
                Cell cell = row.getCell(c);
                if (null != cell) {
                    int code = cell.getCellTypeEnum().getCode();
                    String codeStr = null;
                    String value = null;
                    if(code==0){
                        codeStr = "NUMERIC";
                        value =String.valueOf(cell.getNumericCellValue());
                    }else if(code==1){
                        codeStr = "STRING";
                        value =String.valueOf(cell.getStringCellValue());
                    }else if(code==2){
                        codeStr = "FORMULA";
                        value =String.valueOf(cell.getCellFormula());
                    }else if(code==3){
                        codeStr = "BLANK";
                        value =null;
                        continue;
                    }else if(code==4){
                        codeStr = "BOOLEAN";
                        value = String.valueOf(cell.getBooleanCellValue());
                    }else if(code==5){
                        codeStr = "ERROR";
                        value = String.valueOf(cell.getErrorCellValue());
                    }
                    cellData[c] = value;
                    if(value!=null) {
                        log.info("Line {}-Cell {}-type {}-value {}", r, c, codeStr, value);
                    }
                }
            }
            formDataList.add(cellData);
        }
        return formDataList;
    }

    /**
     * 获取表格列数
     * @param mfile
     * @return
     * @throws Exception
     */
    public static ExcelUtilsInfo getExcelCountInfo(File mfile) throws Exception {


        Workbook wb = ExcelUtils.getExcelInfoFile(mfile);
        // 得到第一个shell
        Sheet sheet = wb.getSheetAt(0);
        // 得到Excel的行数
        int totalRows = sheet.getPhysicalNumberOfRows();
        // 得到Excel的列数(前提是有行数)
        int totalCells = 0;
        if (totalRows > 1 && sheet.getRow(0) != null) {
            totalCells = sheet.getRow(0).getPhysicalNumberOfCells();
        }
        log.info("文件:"+mfile.getPath()+"行数:"+totalRows+",列数: "+totalCells);
        return new ExcelUtilsInfo(totalRows, totalCells);
    }



	/**
	 * 创建excel文档，
	 * @param columnNames excel的列名
	 * */
	public static Workbook createWorkBookTemplate(List<String> columnNames) {
		// 创建excel工作簿
		Workbook wb = new XSSFWorkbook(); //之前使用时xls版本，etl不能优化加速，修改为xlsx HSSFWorkbook();
		// 创建第一个sheet（页），并命名
		Sheet sheet = wb.createSheet("Sheet1");
		// 手动设置列宽。第一个参数表示要为第几列设；，第二个参数表示列的宽度，n为列高的像素数。
		for(int i=0;i<columnNames.size();i++){
			sheet.setColumnWidth((short) i, (short) (35.7 * 200));
		}

		// 创建第一行
		Row firstRow = sheet.createRow((short) 0);

		// 创建两种单元格格式
		CellStyle cs = wb.createCellStyle();
		CellStyle cs2 = wb.createCellStyle();

		// 创建两种字体
		Font f = wb.createFont();
		Font f2 = wb.createFont();

		// 创建第一种字体样式（用于列名）
		f.setFontHeightInPoints((short) 10);
		f.setColor(IndexedColors.BLACK.getIndex());
		f.setBold(true);

		// 创建第二种字体样式（用于值）
		f2.setFontHeightInPoints((short) 10);
		f2.setColor(IndexedColors.BLACK.getIndex());

		// 设置第一种单元格的样式（用于列名）
		cs.setFont(f);
		cs.setBorderLeft(BorderStyle.THIN);
		cs.setBorderRight(BorderStyle.THIN);
		cs.setBorderTop(BorderStyle.THIN);
		cs.setBorderBottom(BorderStyle.THIN);
		cs.setAlignment(HorizontalAlignment.CENTER);
		cs.setVerticalAlignment(VerticalAlignment.CENTER);

		// 设置第二种单元格的样式（用于值）
		cs2.setFont(f2);
		cs2.setBorderLeft(BorderStyle.THIN);
		cs2.setBorderRight(BorderStyle.THIN);
		cs2.setBorderTop(BorderStyle.THIN);
		cs2.setBorderBottom(BorderStyle.THIN);
		cs2.setAlignment(HorizontalAlignment.CENTER);
		cs2.setVerticalAlignment(VerticalAlignment.CENTER);

		//设置列名
		for(int i=0; i<columnNames.size(); i++) {
			Cell cell = firstRow.createCell(i);
			cell.setCellValue(columnNames.get(i));
			cell.setCellStyle(cs);
		}
		return wb;
	}


    /**
     * 根据浏览器页面配置的数据，生成Excel文档
     * @param browseDataVO 浏览器页面配置好的数据
     * @return 文件记录条数
     */
    public static Long createBrowseFormExcel(File file, BrowseDataVO browseDataVO){
        // 创建excel工作簿
        Workbook wb = new XSSFWorkbook(); //之前使用时xls版本，etl不能优化加速，修改为xlsx HSSFWorkbook();
        // 创建第一个sheet（页），并命名
        Sheet sheet = wb.createSheet("Sheet1");

        int columnSize = browseDataVO.getBrowseData().get(0).length;
        // 手动设置列宽。第一个参数表示要为第几列设；，第二个参数表示列的宽度，n为列高的像素数。
        for(int i=0;i<columnSize;i++){
            sheet.setColumnWidth((short) i, (short) (35.7 * 200));
        }

        // 创建第一行
        Row firstRow = sheet.createRow((short) 0);

        // 创建两种单元格格式
        CellStyle cs = wb.createCellStyle();
        CellStyle cs2 = wb.createCellStyle();

        // 创建两种字体
        Font f = wb.createFont();
        Font f2 = wb.createFont();

        // 创建第一种字体样式（用于列名）
        f.setFontHeightInPoints((short) 10);
        f.setColor(IndexedColors.BLACK.getIndex());
        f.setBold(true);

        // 创建第二种字体样式（用于值）
        f2.setFontHeightInPoints((short) 10);
        f2.setColor(IndexedColors.BLACK.getIndex());

        // 设置第一种单元格的样式（用于列名）
        cs.setFont(f);
        cs.setBorderLeft(BorderStyle.THIN);
        cs.setBorderRight(BorderStyle.THIN);
        cs.setBorderTop(BorderStyle.THIN);
        cs.setBorderBottom(BorderStyle.THIN);
        cs.setAlignment(HorizontalAlignment.CENTER);
        cs.setVerticalAlignment(VerticalAlignment.CENTER);

        // 设置第二种单元格的样式（用于值）
        cs2.setFont(f2);
        cs2.setBorderLeft(BorderStyle.THIN);
        cs2.setBorderRight(BorderStyle.THIN);
        cs2.setBorderTop(BorderStyle.THIN);
        cs2.setBorderBottom(BorderStyle.THIN);
        cs2.setAlignment(HorizontalAlignment.CENTER);
        cs2.setVerticalAlignment(VerticalAlignment.CENTER);

        //设置列名
        String[] columnTitle=browseDataVO.getTitleData();
        for(int columnIndex=0; columnIndex<columnSize; columnIndex++) {
            Cell cell = firstRow.createCell(columnIndex);
            cell.setCellValue(columnTitle[columnIndex]);
            cell.setCellStyle(cs);
        }

        //填写Excel数据内容
        int lineCount = browseDataVO.getBrowseData().size();
        if(lineCount>0){
            for(int line=0;line<lineCount;line++){
                Row lineRow = sheet.createRow((short) line+1);
                String[] lineValue=browseDataVO.getBrowseData().get(line);
                for(int cellIndex=0;cellIndex<columnSize; cellIndex++){
                    Cell lineCell = lineRow.createCell(cellIndex);
                    lineCell.setCellValue(lineValue[cellIndex]);
                    lineCell.setCellStyle(cs2);
                }
            }
        }

        try {
            FileOutputStream output=new FileOutputStream(file);
            wb.write(output);//写入磁盘
            output.close();
        }catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return 0L;
        }
        return new Long(lineCount);
    }
}
