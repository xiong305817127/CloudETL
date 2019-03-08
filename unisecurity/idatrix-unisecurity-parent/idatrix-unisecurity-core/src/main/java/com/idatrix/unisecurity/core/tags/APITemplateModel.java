package com.idatrix.unisecurity.core.tags;

import com.idatrix.unisecurity.common.utils.LoggerUtils;
import com.idatrix.unisecurity.common.utils.SpringContextUtil;
import com.idatrix.unisecurity.core.statics.Constant;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

import java.util.HashMap;
import java.util.Map;

import static freemarker.template.ObjectWrapper.DEFAULT_WRAPPER;

/**
 * 开发公司：粤数大数据
 */
public class APITemplateModel extends WYFTemplateModel {

    @Override
    protected Map<String, TemplateModel> putValue(Map params)
            throws TemplateModelException {
        Map<String, TemplateModel> paramWrap = null;
        if (params != null) {
            if (params.size() != 0 || params.get(Constant.TARGET) != null) {
                String name = params.get(Constant.TARGET).toString();
                paramWrap = new HashMap<String, TemplateModel>(params);
                SuperCustomTag tag = SpringContextUtil.getBean(name, SuperCustomTag.class);
                Object result = tag.result(params);
                paramWrap.put(Constant.OUT_TAG_NAME, DEFAULT_WRAPPER.wrap(result));
            }
        } else {
            LoggerUtils.error(getClass(), "Cannot be null, must include a 'name' attribute!");
        }
        return paramWrap;
    }

}
