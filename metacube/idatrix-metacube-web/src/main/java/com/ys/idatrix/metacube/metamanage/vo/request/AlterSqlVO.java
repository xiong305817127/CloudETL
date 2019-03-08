package com.ys.idatrix.metacube.metamanage.vo.request;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName AlterSqlVO
 * @Description 修改sql实体类
 * @Author ouyang
 * @Date
 */
@Data
public class AlterSqlVO {

    private List<String> addSql = new ArrayList<>();

    private List<String> deleteSql = new ArrayList<>();

    private List<String> changeSql = new ArrayList<>();

    private List<String> specialSql = new ArrayList<>();

    private String message;
}