package com.ys.idatrix.metacube.api.beans;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class PageResultBean<T> {

    private int page = 1;
    private int pageSize = 10;
    private long total;
    private List<T> data;

    public PageResultBean(long total, List<T> data) {
        this.total = total;
        this.data = data;
    }

    public PageResultBean(int pageNum, long total, List<T> data) {
        this.page = pageNum;
        this.total = total;
        this.data = data;
    }

    public PageResultBean(int pageNum, int pageSize, long total, List<T> results) {
        this.page = pageNum;
        this.pageSize = pageSize;
        this.total = total;
        this.data = results;
    }

    public PageResultBean() {
    }

    public static <T> PageResultBean<List<T>> of(int pageNum, long total, List<T> data) {
        return new PageResultBean(pageNum, total, data);
    }

    public static <T> PageResultBean<T> builder(long total, List<T> data) {
        return new PageResultBean(total, data);
    }

    public static <T> PageResultBean<T> builder(long total, List<T> data, int page, int pageSize) {
        return new PageResultBean(page, pageSize, total, data);
    }

    public static PageResultBean empty() {
        return new PageResultBean(1, 10, 0, new ArrayList());
    }
}
