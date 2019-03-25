package com.idatrix.resource.catalog.vo;

import lombok.Data;

/**
 * Created by Robin Wing on 2018-6-8.
 */
@Data
public class ResourceHistoryVO {

    private Long id ;

    private String actionName;

    private String operator;

    private String operatorTime;

}
