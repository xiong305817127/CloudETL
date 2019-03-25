package com.idatrix.resource.catalog.po;

import lombok.Data;

/**
 * 数据统计情况，包含月份数和数据量
 */
@Data
public class StatisticsPO {

    /*表示月份数*/
    private String month;

    /*表示统计数值大小*/
    private Long regCount;

    private Long pubCount;

    private Long subCount;

    /*租户ID,用于用于隔离*/
    private Long rentId;

}
