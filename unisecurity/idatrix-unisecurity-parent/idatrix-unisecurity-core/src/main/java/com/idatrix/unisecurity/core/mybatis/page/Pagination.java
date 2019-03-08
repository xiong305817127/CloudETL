package com.idatrix.unisecurity.core.mybatis.page;

import java.util.List;

/**
 * 开发公司：粤数大数据
 */
@SuppressWarnings("serial")
public class Pagination<T> extends SimplePage implements java.io.Serializable,
        Paginable {

    public Pagination() {
    }

    public Pagination(int pageNo, int pageSize, int totalCount) {
        super(pageNo, pageSize, totalCount);
    }

    @SuppressWarnings("unchecked")
    public Pagination(int pageNo, int pageSize, int totalCount, List list) {
        super(pageNo, pageSize, totalCount);
        this.list = list;
    }

    public int getFirstResult() {
        return (pageNo - 1) * pageSize;
    }

    /**
     * 当前页的数据
     */
    private List<T> list;

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }


    /**
     */
    public String getWebPage(String page) {
        StringBuffer pageHtml = new StringBuffer("<ul class='pagination'>");
        if (this.getPageNo() > 1) {
            if (this.getPageNo() > 5) {
                pageHtml.append("<li><a href='javascript:;' onclick='" + page + "'>首页</a></li>");
            }
            pageHtml.append("<li><a href='" + page + "" + (this.getPageNo() - 1) + "'>上一页</a></li>");
        }
        for (int i = (this.getPageNo() - 2 <= 0 ? 1 : this.getPageNo() - 2), no = 1; i <= this.getTotalPage() && no < 6; i++, no++) {
            if (this.getPageNo() == i) {
                pageHtml.append("<li class='active'><a href='javascript:void(0);' >" + i + "</a></li>");
            } else {
                pageHtml.append("<li><a href='" + page + "" + i + "'>" + i + "</a></li>");
            }
        }
        if (this.getPageNo() < this.getTotalPage()) {
            pageHtml.append("<li><a href='" + page + "" + (this.getPageNo() + 1) + "'>下一页</a></li>");
        }
        pageHtml.append("</ul>");
        return pageHtml.toString();
    }


    /**
     * Ajax翻页
     */
    public String getSiAjaxPageHtml() {
        StringBuffer pageHtml = new StringBuffer("<ul class='pagination'>");
        if (this.getPageNo() > 1) {
            if (this.getPageNo() > 5) {
                pageHtml.append("<li><a href='javascript:;' onclick='goPageByAjax(1)'>首页</a></li>");
            }
            pageHtml.append("<li><a href='javascript:;'  onclick='goPageByAjax(" + (this.getPageNo() - 1) + ")'>上一页</a></li>");
        }
        for (int i = (this.getPageNo() - 2 <= 0 ? 1 : this.getPageNo() - 2), no = 1; i <= this.getTotalPage() && no < 6; i++, no++) {
            if (this.getPageNo() == i) {
                pageHtml.append("<li class='active'><a href='javascript:void(0);' >" + i + "</a></li>");
            } else {
                pageHtml.append("<li><a href='javascript:;' onclick='goPageByAjax(" + i + ")'>" + i + "</a></li>");
            }
        }
        if (this.getPageNo() < this.getTotalPage()) {
            pageHtml.append("<li><a href='javascript:;'  onclick='goPageByAjax(" + (this.getPageNo() + 1) + ")'>下一页</a></li>");
        }
        pageHtml.append("</ul>");
        return pageHtml.toString();
    }

    /**
     * 普通翻页
     */
    public String getPageHtml() {
        StringBuffer pageHtml = new StringBuffer("<ul class='pagination'>");
        if (this.getPageNo() > 1) {
            if (this.getPageNo() > 5) {
                pageHtml.append("<li><a href='javascript:;' onclick='_submitform(1)'>首页</a></li>");
            }
            pageHtml.append("<li><a href='javascript:;'  onclick='_submitform(" + (this.getPageNo() - 1) + ")'>上一页</a></li>");
        }
        for (int i = (this.getPageNo() - 2 <= 0 ? 1 : this.getPageNo() - 2), no = 1; i <= this.getTotalPage() && no < 6; i++, no++) {
            if (this.getPageNo() == i) {
                pageHtml.append("<li class='active'><a href='javascript:void(0);' >" + i + "</a></li>");
            } else {
                pageHtml.append("<li><a href='javascript:;' onclick='_submitform(" + i + ")'>" + i + "</a></li>");
            }
        }
        if (this.getPageNo() < this.getTotalPage()) {
            pageHtml.append("<li><a href='javascript:;'  onclick='_submitform(" + (this.getPageNo() + 1) + ")'>下一页</a></li>");
        }
        pageHtml.append("</ul>");
        pageHtml.append("<script>");
        pageHtml.append("	function _submitform(pageNo){");
        pageHtml.append("		$(\"#formId\").append($(\"<input type='hidden' value='\" + pageNo +\"' name='pageNo'>\")).submit();");
        pageHtml.append("	}");
        pageHtml.append("</script>");

        return pageHtml.toString();
    }

}
