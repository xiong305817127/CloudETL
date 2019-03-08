package com.ys.idatrix.db.enums;

/**
 * @Enum: HiveOperator
 * @Description:
 * @Author: ZhouJian
 * @Date: 2019/3/4
 */
public enum HiveOperator {

    SELECT("SELECT", "read", "select"),
    INSERT("INSERT", "write", "update"),
    LOAD("INSERT", "write", "update"),
    DROP("DROP", "delete", "update"),
    ALTER("ALTER", "write", "update"),
    TRUNCATE("TRUNCATE", "delete", "update"),
    CREATETABLE("CREATETABLE", "write", "update"),
    UNKNOWN("UNKNOWN", "", "");

    private String operator;

    private String permission;

    private String runnerType;

    HiveOperator(String operator, String permission, String runnerType) {
        this.operator = operator;
        this.permission = permission;
        this.runnerType = runnerType;
    }

    public String getOperator() {
        return operator;
    }

    public String getPermission() {
        return permission;
    }

    public String getRunnerType() {
        return runnerType;
    }

}
