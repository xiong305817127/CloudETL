package com.idatrix.unisecurity.common.domain;

import com.idatrix.unisecurity.anotation.IdatrixMaxLen;
import com.idatrix.unisecurity.anotation.IdatrixPattern;
import com.idatrix.unisecurity.anotation.NotBlank;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import net.sf.json.JSONObject;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@ApiModel(description = "用户DTO")
public class UUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    private Long id;

    /**
     * 租户id
     */
    @ApiModelProperty(value = "租户id")
    private Long renterId;

    /**
     * 部门id
     */
    @ApiModelProperty(value = "部门id")
    private Long deptId;

    /**
     * 用户名
     */
    @ApiModelProperty(value = "用户名")
    @NotBlank(message = "用户账号不能为空")
    @IdatrixPattern(regexp = "^[0-9a-zA-Z]{3,20}$", message = "用户账号必须由3-20个字母或数字组成")
    private String username;

    /**
     * 真实姓名
     */
    @ApiModelProperty(value = "真实姓名")
    @NotBlank(message = "真实姓名不能为空")
    @IdatrixMaxLen(maxLen = 50, message = "真实姓名长度超过限制")
    @IdatrixPattern(regexp = "^.{1,50}$", message = "真实姓名长度不能超过50")
    private String realName;

    /**
     * 性别
     */
    @ApiModelProperty(value = "性别")
    private Long sex;

    /**
     * 年龄
     */
    @ApiModelProperty(value = "年龄")
    private Integer age;

    /**
     * 邮箱
     */
    @ApiModelProperty(value = "邮箱")
    @NotBlank(message = "邮箱不能为空")
    @IdatrixMaxLen(maxLen = 100, message = "邮箱长度超过100")
    @IdatrixPattern(regexp = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+", message = "请输入正确的邮箱")
    private String email;

    /**
     * 身份证号码
     */
    @ApiModelProperty(value = "身份证号码")
    @NotBlank(message = "身份证号码不能为空")
    @IdatrixPattern(regexp = "^[1-9]\\d{5}(18|19|([23]\\d))\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$",
            message = "请输入正确的身份证号码")
    private String cardId;

    /**
     * 电话号码
     */
    @ApiModelProperty(value = "电话号码")
    @NotBlank(message = "手机号码不能为空")
    @IdatrixPattern(regexp = "^1[3|4|5|7|8][0-9]{9}$", message = "请输入正确的手机号码")
    private String phone;

    /**
     * 密码
     */
    @ApiModelProperty(value = "密码")
    @NotBlank(message = "密码不能为空")
    @IdatrixPattern(regexp = "^(?!(?:\\d+|[a-zA-Z]+)$)[\\da-zA-Z]{6,18}", message = "密码长度6~18之间,且不能纯数字或字母")
    private transient String pswd;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    /**
     * 最后登录时间
     */
    @ApiModelProperty(value = "最后登录时间")
    private Date lastLoginTime;

    /**
     * 1:有效，0:禁止登录
     */
    @ApiModelProperty(value = "状态")
    private Long status;

    @ApiModelProperty(value = "访问次数")
    private Long visitTimes;

    private String loginToken;

    private Date outDate;

    private Date lastUpdatedDate;

    private String validateCode;

    private transient String roleNames;

    private String qnOne;

    private String qnTwo;

    private String qnThree;

    private String arOne;

    private String arTwo;

    private String arThree;

    private boolean isRenter = false;

    private List<String> roleCodes;

    public UUser() {
    }

    public UUser(UUser user) {
        this.id = user.getId();
        this.deptId = user.getDeptId();
        this.username = user.getUsername();
        this.realName = user.getRealName();
        this.sex = user.getSex();
        this.age = user.getAge();
        this.email = user.getEmail();
        this.cardId = user.getCardId();
        this.phone = user.getPhone();
        this.pswd = user.getPswd();
        this.createTime = user.getCreateTime();
        this.lastLoginTime = user.getLastLoginTime();
        this.status = user.getStatus();
    }

    public List<String> getRoleCodes() {
        return roleCodes;
    }

    public void setRoleCodes(List<String> roleCodes) {
        this.roleCodes = roleCodes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRenterId() {
        return renterId;
    }

    public void setRenterId(Long renterId) {
        this.renterId = renterId;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
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

    public String getPswd() {
        return pswd;
    }

    public void setPswd(String pswd) {
        this.pswd = pswd;
    }

    public Date getCreateTime() {
        return this.createTime == null ? new Date() : new Date(this.createTime.getTime());
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime == null ? new Date() : new Date(createTime.getTime());
    }

    public Date getLastLoginTime() {
        return this.lastLoginTime == null ? new Date() : new Date(this.lastLoginTime.getTime());
    }

    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime == null ? new Date() : new Date(lastLoginTime.getTime());
    }

    public String toString() {
        return JSONObject.fromObject(this).toString();
    }

    public String getLoginToken() {
        return loginToken;
    }

    public void setLoginToken(String loginToken) {
        this.loginToken = loginToken;
    }

    public Date getOutDate() {
        return outDate;
    }

    public void setOutDate(Date outDate) {
        this.outDate = outDate;
    }

    public Date getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(Date lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }

    public String getValidateCode() {
        return validateCode;
    }

    public void setValidateCode(String validateCode) {
        this.validateCode = validateCode;
    }

    public String getRoleNames() {
        return roleNames;
    }

    public void setRoleNames(String roleNames) {
        this.roleNames = roleNames;
    }

    public String getQnOne() {
        return qnOne;
    }

    public void setQnOne(String qnOne) {
        this.qnOne = qnOne;
    }

    public String getQnTwo() {
        return qnTwo;
    }

    public void setQnTwo(String qnTwo) {
        this.qnTwo = qnTwo;
    }

    public String getQnThree() {
        return qnThree;
    }

    public void setQnThree(String qnThree) {
        this.qnThree = qnThree;
    }

    public String getArOne() {
        return arOne;
    }

    public void setArOne(String arOne) {
        this.arOne = arOne;
    }

    public String getArTwo() {
        return arTwo;
    }

    public void setArTwo(String arTwo) {
        this.arTwo = arTwo;
    }

    public String getArThree() {
        return arThree;
    }

    public void setArThree(String arThree) {
        this.arThree = arThree;
    }

    public Long getVisitTimes() {
        return visitTimes;
    }

    public void setVisitTimes(Long visitTimes) {
        this.visitTimes = visitTimes;
    }

    public boolean isRenter() {
        return isRenter;
    }

    public void setRenter(boolean renter) {
        isRenter = renter;
    }
}