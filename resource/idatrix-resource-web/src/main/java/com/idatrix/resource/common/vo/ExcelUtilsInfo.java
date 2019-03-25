package com.idatrix.resource.common.vo;

import lombok.Data;

import java.util.List;

/**
 * Created by Administrator on 2019/1/12.
 */
@Data
public class ExcelUtilsInfo {

    private int lineCount;

    private int columnCount;

    private List<String[]> data;

    public ExcelUtilsInfo(){
        super();
    }

    public ExcelUtilsInfo(int line, int column){
        this.lineCount = line;
        this.columnCount = column;
    }

    public ExcelUtilsInfo(int line, int column, List<String[]>data){
        this.lineCount = line;
        this.columnCount = column;
        this.data = data;
    }
}
