package com.ys.idatrix.metacube.metamanage.vo.response;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName PageResultVO
 * @Description 分页使用的实体类
 * @Author ouyang
 * @Date
 */
public class PageResultVO<T> implements Serializable {

    private long total; // 总数量
    private List<T> rows; // 具体返回的数据

    public PageResultVO() {
    }

    public PageResultVO(long total, List<T> rows) {
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
