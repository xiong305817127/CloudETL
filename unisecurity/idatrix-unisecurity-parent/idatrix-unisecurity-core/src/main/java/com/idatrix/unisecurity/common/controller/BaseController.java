package com.idatrix.unisecurity.common.controller;

import com.idatrix.unisecurity.common.utils.SecurityStringUtils;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.beans.PropertyEditorSupport;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

/**
 * 开发公司：粤数大数据
 */
@ApiIgnore
public class BaseController {

    protected int pageNo = 1;
    public int pageSize = 10;
    protected final static Logger logger = Logger.getLogger(BaseController.class);
    protected Map<String, Object> resultMap = new LinkedHashMap<String, Object>();
    public static final String URL404 = "/404.html";
    private final static String PARAM_PAGE_NO = "pageNo";
    protected String pageSizeName = "pageSize";

    /**
     *
     * @param request
     * @param key
     * @param value
     */
    protected static void setValue2Request(HttpServletRequest request, String key, Object value) {
        request.setAttribute(key, value);
    }

    /**
     * [获取session]
     * @param request
     * @return
     */
    public static HttpSession getSession(HttpServletRequest request) {
        return request.getSession();
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public ModelAndView redirect(String redirectUrl, Map<String, Object>... parament) {
        ModelAndView view = new ModelAndView(new RedirectView(redirectUrl));
        if (null != parament && parament.length > 0) {
            view.addAllObjects(parament[0]);
        }
        return view;
    }

    public ModelAndView redirect404() {
        return new ModelAndView(new RedirectView(URL404));
    }

    @SuppressWarnings("unchecked")
    protected Map<String, Object> prepareParams(Object obj, HttpServletRequest request) throws Exception {
        if (request != null) {
            String pageNoStr = request.getParameter(PARAM_PAGE_NO),
                    pageSizeStr = request.getParameter(pageSizeName);
            if (SecurityStringUtils.isNotBlank(pageNoStr)) {
                pageNo = Integer.parseInt(pageNoStr);
            }
            if (SecurityStringUtils.isNotBlank(pageSizeStr)) {
                pageSize = Integer.parseInt(pageSizeStr);
            }
        }

        Map<String, Object> params = new HashMap<String, Object>();
        params = BeanUtils.describe(obj);
        params = handleParams(params);
        // 回填值项
        //BeanUtils.populate(obj, params);
        return params;
    }

    private Map<String, Object> handleParams(Map<String, Object> params) {
        Map<String, Object> result = new HashMap<String, Object>();
        if (null != params) {
            Set<Entry<String, Object>> entrySet = params.entrySet();
            for (Iterator<Entry<String, Object>> it = entrySet.iterator(); it.hasNext(); ) {
                Entry<String, Object> entry = it.next();
                if (entry.getValue() != null) {
                    result.put(entry.getKey(), SecurityStringUtils.trimToEmpty(entry.getValue()));
                }
            }
        }
        return result;
    }

    /*@InitBinder*/
    protected void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
        binder.registerCustomEditor(Timestamp.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String value) {
                if(SecurityStringUtils.isNotEmpty(value))
                    setValue(new Timestamp(Long.parseLong(value)));
            }
        });
    }
}
