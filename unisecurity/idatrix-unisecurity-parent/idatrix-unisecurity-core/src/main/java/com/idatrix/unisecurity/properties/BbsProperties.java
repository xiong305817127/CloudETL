package com.idatrix.unisecurity.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @ClassName BbsProperties
 * @Description bbs 属性配置类
 * @Author ouyang
 * @Date 2018/9/28 13:47
 * @Version 1.0
 **/
@Component
public class BbsProperties {

    @Value("${isAddBBS}")
    private Boolean isAddBBS;// 是否需要同步到 bbs 论坛中

    @Value("${BBS_PRE_URL}")
    private String bbs_pre_url;

    @Value("${BBS_ADD_USER}")
    private String bbs_add_user;

    @Value("${BBS_UPDATE_USER}")
    private String bbs_update_user;

    @Value("${BBS_DELETE_USER}")
    private String bbs_delete_user;

    public Boolean getAddBBS() {
        return isAddBBS;
    }

    public void setAddBBS(Boolean addBBS) {
        isAddBBS = addBBS;
    }

    public String getBbs_pre_url() {
        return bbs_pre_url;
    }

    public void setBbs_pre_url(String bbs_pre_url) {
        this.bbs_pre_url = bbs_pre_url;
    }

    public String getBbs_add_user() {
        return bbs_add_user;
    }

    public void setBbs_add_user(String bbs_add_user) {
        this.bbs_add_user = bbs_add_user;
    }

    public String getBbs_update_user() {
        return bbs_update_user;
    }

    public void setBbs_update_user(String bbs_update_user) {
        this.bbs_update_user = bbs_update_user;
    }

    public String getBbs_delete_user() {
        return bbs_delete_user;
    }

    public void setBbs_delete_user(String bbs_delete_user) {
        this.bbs_delete_user = bbs_delete_user;
    }
}
