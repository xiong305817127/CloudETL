package com.idatrix.unisecurity.common.domain;

import java.io.Serializable;

/**
 * 这个对象是没有被操作的
 * Created by james on 2017/8/7.
 */
public class PwdQuestion implements Serializable {

    private Long id;

    private String questionNum;

    private String questionName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuestionNum() {
        return questionNum;
    }

    public void setQuestionNum(String questionNum) {
        this.questionNum = questionNum;
    }

    public String getQuestionName() {
        return questionName;
    }

    public void setQuestionName(String questionName) {
        this.questionName = questionName;
    }
}
