package com.idatrix.resource.common.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 基础请求参数父类
 *
 * @author wzl
 */

@Data
public class BaseRequestParamVO implements Serializable {

    /**
     * 当前页数
     */
    protected Integer page;
    /**
     * 分页大小
     */
    protected Integer pageSize;

    public BaseRequestParamVO() {
        this.page = 1;
        this.pageSize = 10;
    }

}
