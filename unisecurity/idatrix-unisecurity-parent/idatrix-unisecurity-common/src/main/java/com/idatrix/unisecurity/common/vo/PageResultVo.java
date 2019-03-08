package com.idatrix.unisecurity.common.vo;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName PageResultVo
 * @Description 分页使用的VO
 * @Author ouyang
 * @Date 2018/11/1 14:07
 * @Version 1.0
 */
public class PageResultVo<T> implements Serializable {

    private long total; // 总数量
    private List<T> rows; // 具体返回的数据

    public PageResultVo() {
    }

    public PageResultVo(long total, List<T> rows) {
        this.total = total;
        this.rows = rows;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<T> getRows() {
        return rows;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }
}
