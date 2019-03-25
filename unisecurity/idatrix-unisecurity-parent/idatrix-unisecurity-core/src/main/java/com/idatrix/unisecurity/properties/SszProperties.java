package com.idatrix.unisecurity.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 神算子的属性类
 * @ClassName SszProperties
 * @Description
 * @Author ouyang
 * @Date 2018/9/28 14:01
 * @Version 1.0
 **/
@Component
public class SszProperties {

    @Value("${isAddSsz}")
    private Boolean isAddSsz; // 是否需要同步到神算子

    @Value("${APPKEY}")
    private String appkey;

    @Value("${SSZ_PRE_URL}")
    private String ssz_pre_url;

    @Value("${SSZ_ADD_USER_URL}")
    private String ssz_add_user_url;

    @Value("${SSZ_DELETE_USER_URL}")
    private String ssz_delete_user_url;

    public Boolean getAddSsz() {
        return isAddSsz;
    }

    public void setAddSsz(Boolean addSsz) {
        isAddSsz = addSsz;
    }

    public String getAppkey() {
        return appkey;
    }

    public void setAppkey(String appkey) {
        this.appkey = appkey;
    }

    public String getSsz_pre_url() {
        return ssz_pre_url;
    }

    public void setSsz_pre_url(String ssz_pre_url) {
        this.ssz_pre_url = ssz_pre_url;
    }

    public String getSsz_add_user_url() {
        return ssz_add_user_url;
    }

    public void setSsz_add_user_url(String ssz_add_user_url) {
        this.ssz_add_user_url = ssz_add_user_url;
    }

    public String getSsz_delete_user_url() {
        return ssz_delete_user_url;
    }

    public void setSsz_delete_user_url(String ssz_delete_user_url) {
        this.ssz_delete_user_url = ssz_delete_user_url;
    }
}
