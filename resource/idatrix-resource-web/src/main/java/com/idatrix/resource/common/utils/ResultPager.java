package com.idatrix.resource.common.utils;

import java.util.List;

public class ResultPager<T> {

    private int page;
    private long total;
    private List<T> results;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<T> getResults() {
        return results;
    }

    public void setResults(List<T> results) {
        this.results = results;
    }

    public ResultPager(int pageNum, long total, List<T> results) {
        super();
        this.page = pageNum;
        this.total = total;
        this.results = results;
    }

    public ResultPager() {
        super();
        // TODO Auto-generated constructor stub
    }

    @Override
    public String toString() {
        return "ResultPager [pageNum=" + page + ", total=" + total + ", results=" + results + "]";
    }

}
