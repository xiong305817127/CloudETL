package com.idatrix.unisecurity.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @ClassName BeijingProperties
 * @Description
 * @Author ouyang
 * @Date
 */
@Data
@Component
public class BeijingProperties {

    @Value("${IS_ADD_BEIJING}")
    private Boolean isAddBeijing;// 是否需要同步到 bbs 论坛中

    @Value("${BEIJING_PRE_URL}")
    private String beijingPreUrl;

    @Value("${BEIJING_ADD_USER_URL}")
    private String addUserUrl;

    @Value("${BEIJING_UPDATE_USER_URL}")
    private String updateUserUrl;

    @Value("${BEIJING_DELETE_USER_URL}")
    private String deleteUserUrl;

}