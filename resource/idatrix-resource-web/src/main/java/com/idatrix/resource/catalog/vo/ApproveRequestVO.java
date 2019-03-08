package com.idatrix.resource.catalog.vo;

import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by Robin Wing on 2018-6-13.
 */
public class ApproveRequestVO {

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
