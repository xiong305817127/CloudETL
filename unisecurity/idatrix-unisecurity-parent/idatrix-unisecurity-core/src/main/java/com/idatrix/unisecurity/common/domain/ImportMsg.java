package com.idatrix.unisecurity.common.domain;

import io.swagger.annotations.ApiModel;

import java.io.Serializable;
import java.util.Date;

@ApiModel(description = "导入用户错误信息记录DTO")
public class ImportMsg implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String batchId;

    private String userName;

    private String fileName;

    private String importor;

    private String msg;

    private Date createTime;

    private String password;

    private String realName;

    private Integer sex;

    private Integer age;

    private String email;

    private String cardId;

    private String phone;

    private String sexStr; // for verter sex

    public ImportMsg() {
        super();
    }

    public ImportMsg(String batchId, String fileName, String username, String passwrod, String realName, Integer sex,
                     Integer age, String email, String cardId, String phone) {
        this.batchId = batchId;
        this.fileName = fileName;
        this.userName = username;
        this.password = passwrod;
        this.realName = realName;
        this.sex = sex;
        this.age = age;
        this.email = email;
        this.cardId = cardId;
        this.phone = phone;
    }

    public String getSexStr() {
        return sexStr;
    }

    public void setSexStr(String sexStr) {
        this.sexStr = sexStr;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public String getImportor() {
        return importor;
    }

    public void setImportor(String importor) {
        this.importor = importor;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }
}
