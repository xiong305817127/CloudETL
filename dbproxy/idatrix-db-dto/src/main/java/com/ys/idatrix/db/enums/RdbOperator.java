package com.ys.idatrix.db.enums;

/**
 * @ClassName: RdbOperator
 * @Description:
 * @Author: ZhouJian
 * @Date: 2019/3/4
 */
public enum RdbOperator {

    SELECT("SELECT", "read", "select"),
    INSERT("INSERT", "write", "update"),
    UPDATE("UPDATE", "write", "update"),
    DELETE("DELETE", "delete", "update"),
    ALTER("ALTER", "alter", "update"),
    CREATETABLE("CREATETABLE", "write", "update"),
    DROPTABLE("DROPTABLE", "delete", "update"),
    CREATEDATABASE("CREATEDATABASE", "write", "update"),
    DROPDATABASE("DROPDATABASE", "delete", "update"),
    SHOW("SHOW",  "read", "select"),
    UNKNOWN("UNKNOWN", "", "");

    private String operator;

    private String permission;

    private String runnerType;

    RdbOperator(String operator, String permission, String runnerType) {
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
