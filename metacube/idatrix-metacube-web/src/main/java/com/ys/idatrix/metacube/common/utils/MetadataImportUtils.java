package com.ys.idatrix.metacube.common.utils;

import com.google.common.collect.ImmutableList;
import com.ys.idatrix.metacube.common.exception.MetaDataException;
import com.ys.idatrix.metacube.metamanage.vo.request.MySqlTableVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @ClassName MetadataImportUtils
 * @Description
 * @Author ouyang
 * @Date
 */
@Slf4j
public class MetadataImportUtils {

    public static MySqlTableVO readExcelToMySqlTableVO(String fileName, InputStream input) throws IOException {
        boolean isExcel2003 = true;
        if (fileName.matches("^.+\\.(?i)(xlsx)$")) {
            log.info("isExcel2003:" + isExcel2003);
            isExcel2003 = false;
        }

        // 根据版本选择创建Workbook的方式
        Workbook wb;
        if (isExcel2003) {
            // 当excel是2003时
            wb = new HSSFWorkbook(input);
        } else {
            // 当excel是2007时
            wb = new XSSFWorkbook(input);
        }

        Sheet tableBaseInfoSheet = wb.getSheet("表基本信息");
        Sheet tableColumnSheet = wb.getSheet("表字段信息");
        Sheet tableIndexSheet = wb.getSheet("表索引信息");
        if (tableBaseInfoSheet == null || tableBaseInfoSheet.getLastRowNum() <= 0) {
            throw new MetaDataException("错误的表基本信息，请检查");
        }
        if (tableColumnSheet == null || tableColumnSheet.getLastRowNum() <= 0) {
            throw new MetaDataException("错误的表字段信息，请检查");
        }
        if (tableIndexSheet == null || tableIndexSheet.getLastRowNum() <= 0) {
            throw new MetaDataException("错误的表索引信息，请检查");
        }

        // 获取表头
        List<HeadEntity> baseInfoHeadEntities = getMYSQLExcelHeadEntities(1);
        List<HeadEntity> columnHeadEntities = getMYSQLExcelHeadEntities(2);
        List<HeadEntity> indexHeadEntities = getMYSQLExcelHeadEntities(3);

        // 校验表头数据
        verifyExcelHeadData(tableBaseInfoSheet, baseInfoHeadEntities);
        verifyExcelHeadData(tableColumnSheet, columnHeadEntities);
        verifyExcelHeadData(tableIndexSheet, indexHeadEntities);


        return null;
    }

    public static void verifyExcelHeadData(Sheet sheet, List<HeadEntity> headEntities) {
        Row headRow = sheet.getRow(0);
        if (headRow == null) {
            throw new MetaDataException(sheet.getSheetName() + "，列头行为空,请重新录入！");
        } else {
            // 列头总列数
            int headColCnt = headRow.getPhysicalNumberOfCells();

            // 模版列头数量
            int correctCellCnt = headEntities.size();

            // 校验列名数量是否正确
            if (headColCnt != correctCellCnt) {
                throw new MetaDataException(sheet.getSheetName() + "，列数不正确，正确需要[ " + correctCellCnt + " ]列,请重新录入！");
            } else {
                for (int i = 0; i < headColCnt; i++) {
                    Cell cell = headRow.getCell(i);
                    String colName = cell.getStringCellValue();
                    //校验列头名称是否正确
                    if (!colName.trim().equalsIgnoreCase(headEntities.get(i).getName())
                            && !colName.trim().equalsIgnoreCase("*" + headEntities.get(i).getName())) {
                        throw new MetaDataException(sheet.getSheetName() + "，第[ " + (i + 1) + " ]列名称[ "
                                + colName + " ]错误，请重新下载模块录入！");
                    }
                }
            }
        }
    }

    public static List<HeadEntity> getMYSQLExcelHeadEntities(int type) {
        List<HeadEntity> result = null;
        switch (type) {
            case 1: // 表基本信息
                HeadEntity baseInfoHeadEntity_0 = new HeadEntity(0, "实体表名", true);
                HeadEntity baseInfoHeadEntity_1 = new HeadEntity(1, "中午表名", true);
                HeadEntity baseInfoHeadEntity_2 = new HeadEntity(2, "是否公开", true);
                HeadEntity baseInfoHeadEntity_3 = new HeadEntity(3, "主题", true);
                HeadEntity baseInfoHeadEntity_4 = new HeadEntity(4, "标签", false);
                HeadEntity baseInfoHeadEntity_5 = new HeadEntity(5, "备注", false);
                HeadEntity baseInfoHeadEntity_6 = new HeadEntity(6, "是否允许为空", false);
                result = ImmutableList.of(baseInfoHeadEntity_0, baseInfoHeadEntity_1, baseInfoHeadEntity_2, baseInfoHeadEntity_3, baseInfoHeadEntity_4, baseInfoHeadEntity_5, baseInfoHeadEntity_6);
                break;
            case 2: // 表字段
                HeadEntity columnHeadEntity_0 = new HeadEntity(0, "字段名", true);
                HeadEntity columnHeadEntity_1 = new HeadEntity(1, "字段描述", true);
                HeadEntity columnHeadEntity_2 = new HeadEntity(2, "数据类型", true);
                HeadEntity columnHeadEntity_3 = new HeadEntity(3, "长度", true);
                HeadEntity columnHeadEntity_4 = new HeadEntity(4, "精度", false);
                HeadEntity columnHeadEntity_5 = new HeadEntity(5, "是否可以为空", false);
                HeadEntity columnHeadEntity_6 = new HeadEntity(6, "是否主键", false);
                HeadEntity columnHeadEntity_7 = new HeadEntity(6, "默认值", false);
                HeadEntity columnHeadEntity_8 = new HeadEntity(6, "是否自增", false);
                result = ImmutableList.of(columnHeadEntity_0, columnHeadEntity_1, columnHeadEntity_2, columnHeadEntity_3, columnHeadEntity_4, columnHeadEntity_5, columnHeadEntity_6, columnHeadEntity_7, columnHeadEntity_8);
                break;
            case 3: // 表索引
                HeadEntity indexHeadEntity_0 = new HeadEntity(0, "索引名", true);
                HeadEntity indexHeadEntity_1 = new HeadEntity(1, "索引关联字段", true);
                HeadEntity indexHeadEntity_2 = new HeadEntity(2, "索引类型", true);
                HeadEntity indexHeadEntity_3 = new HeadEntity(3, "索引方法", true);
                result = ImmutableList.of(indexHeadEntity_0, indexHeadEntity_1, indexHeadEntity_2, indexHeadEntity_3);
                break;
            case 4: // 表外键
                break;
        }
        return result;
    }

}