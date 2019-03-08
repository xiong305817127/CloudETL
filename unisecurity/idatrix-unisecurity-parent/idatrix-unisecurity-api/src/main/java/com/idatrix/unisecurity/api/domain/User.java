package com.idatrix.unisecurity.api.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 */
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    //0:禁止登录
    public static final Long ZERO = Long.valueOf(0);

    //1:有效
    public static final Long ONE = Long.valueOf(1);

    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 性别
     */
    private Long sex;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 身份证号码
     */
    private String cardId;

    /**
     * 电话号码
     */
    private String phone;

    /**
     * 密码
     */
    private transient String pswd;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 最后登录时间
     */
    private Date lastLoginTime;

    /**
     * 1:有效，0:禁止登录
     */
    private Long status;

    /**
     * 组织id
     */
    private Long deptId;

    /**
     * 租户id
     */
    private Long renterId;

    private List<UPermission> permissionList= new LinkedList<UPermission>();

    private String roleName;

    public User() {
    }

    public User(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.pswd = user.getPswd();
        this.createTime = user.getCreateTime();
        this.lastLoginTime = user.getLastLoginTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public Long getSex() {
        return sex;
    }

    public void setSex(Long sex) {
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

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
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

    public String getPswd() {
        return pswd;
    }

    public void setPswd(String pswd) {
        this.pswd = pswd;
    }

    public Date getCreateTime() {
        return this.createTime==null?new Date():new Date(this.createTime.getTime());
    }

    public void setCreateTime(Date createTime) {
        this.createTime =createTime==null?new Date():new Date(createTime.getTime());
    }

    public Date getLastLoginTime() {
        return this.lastLoginTime==null?new Date():new Date(this.lastLoginTime.getTime());
    }

    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime==null?new Date():new Date(lastLoginTime.getTime());
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public List<UPermission> getPermissionList() {
        return permissionList;
    }

    public void setPermissionList(List<UPermission> permissionList) {
        this.permissionList = permissionList;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public Long getRenterId() {
        return renterId;
    }

    public void setRenterId(Long renterId) {
        this.renterId = renterId;
    }
}