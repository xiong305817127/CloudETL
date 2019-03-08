package com.idatrix.resource.common.vo;

import com.alibaba.fastjson.JSON;
import java.io.Serializable;

/**
 * 基础请求参数父类
 *
 * @author wzl
 */
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

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
