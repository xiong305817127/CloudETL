package com.ys.idatrix.metacube.common.enums;

/**
 * @ClassName AuthorityApprovalStatus
 * @Description 权限申请状态
 * @Author ouyang
 * @Date
 */
public enum  AuthorityApprovalStatus {

    IN_THE_APPLICATION(1, "申请中"),
    PASS(2, "通过"),
    NO_PASS(3, "不通过"),
    HAS_RECYCLED(4, "已回收"),
    HAS_WITHDRAWN(5, "已撤回")
    ;

    private int code;
    private String name;

    AuthorityApprovalStatus(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}