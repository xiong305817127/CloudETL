package com.idatrix.unisecurity.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @ClassName EmailProperties
 * @Description 邮箱服务熟悉类
 * @Author ouyang
 * @Date 2018/10/8 15:59
 * @Version 1.0
 */
@Component
public class EmailProperties {

    @Value("${send_email_server}")
    private String emailServer;

    @Value("${send_user}")
    private String user;

    @Value("${send_password}")
    private String password;

    @Value("${mail_debug}")
    private String mailDebug;

    @Value("${mail_switch}")
    private String mailSwitch;// 邮箱开关

    @Value("${EMAIL_LOG_ID_KEY}")// 邮箱日志ID redis 中key
    private String emailLogIdKey = "EMAIL_LOG_ID_KEY";

    @Value("${EMAIL_LOG_ID_VALUE}")// 邮箱日志ID 初始化大小
    private String emailLogIdValue = "1";

    @Value("${EMAIL_CODE_KEY_PREFIX}")// 发送给用户的邮箱 code 对应的 key 在redis中的前缀
    private String emailCodeKeyPrefix;

    public String getEmailServer() {
        return emailServer;
    }

    public void setEmailServer(String emailServer) {
        this.emailServer = emailServer;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMailDebug() {
        return mailDebug;
    }

    public void setMailDebug(String mailDebug) {
        this.mailDebug = mailDebug;
    }

    public String getMailSwitch() {
        return mailSwitch;
    }

    public void setMailSwitch(String mailSwitch) {
        this.mailSwitch = mailSwitch;
    }

    public String getEmailLogIdKey() {
        return emailLogIdKey;
    }

    public void setEmailLogIdKey(String emailLogIdKey) {
        this.emailLogIdKey = emailLogIdKey;
    }

    public String getEmailLogIdValue() {
        return emailLogIdValue;
    }

    public void setEmailLogIdValue(String emailLogIdValue) {
        this.emailLogIdValue = emailLogIdValue;
    }

    public String getEmailCodeKeyPrefix() {
        return emailCodeKeyPrefix;
    }

    public void setEmailCodeKeyPrefix(String emailCodeKeyPrefix) {
        this.emailCodeKeyPrefix = emailCodeKeyPrefix;
    }
}
