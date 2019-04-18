package com.idatrix.unisecurity.common.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName PageResultVo
 * @Description 分页使用的VO
 * @Author ouyang
 * @Date 2018/11/1 14:07
 * @Version 1.0
 */
@Data
public class PageResultVo<T> implements Serializable {

    private long total; // 总数量
    private List<T> rows; // 具体返回的数据
    private int page = 1; // 页码
    private int pageSize = 10; // 当前条数

    public PageResultVo() {
    }

    public PageResultVo(long total, List<T> rows, int page, int pageSize) {
        this.total = total;
        this.rows = rows;
        this.page = page;
        this.pageSize = pageSize;
    }
}
