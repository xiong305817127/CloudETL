package com.idatrix.resource.subscribe.vo.request;

/**
 * Created by Administrator on 2018/7/18.
 */
public class SubscribeApproveRequestVO {

    private Long id;

    private String action;

    private String suggestion;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }
}
