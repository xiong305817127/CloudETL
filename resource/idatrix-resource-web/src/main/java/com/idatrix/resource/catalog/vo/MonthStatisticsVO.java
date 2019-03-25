package com.idatrix.resource.catalog.vo;

import lombok.Data;

/**
 * Created by Robin Wing on 2018-5-29.
 */
@Data
public class MonthStatisticsVO {

    /*三大库基本类型： 基础库、部门库、主题库： base/department/topic
    * 为所有信息时显示 type为 all*/
    private String catalogName;

    /*月度名称格式为 yyyy年mm月*/
    private String monthName;

    private int subCount;

    private int pubCount;

    private int regCount;


}
