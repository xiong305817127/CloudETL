package com.idatrix.unisecurity.common.domain;

import io.swagger.annotations.ApiModel;

import java.io.Serializable;

@ApiModel(description="邮件发送日志TOD")
public class MailLog implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;

    private String subject;

    private String sendServer;

    private String content;

    private String recipient;

    private String status;

    private String msg;

    public String getSendServer() {
        return sendServer;
    }

    public void setSendServer(String sendServer) {
        this.sendServer = sendServer;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
