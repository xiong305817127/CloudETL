package com.ys.idatrix.db.enums;

/**
 * @Enum: HBaseOperator
 * @Description:
 * @Author: ZhouJian
 * @Date: 2019/3/4
 */
public enum HBaseOperator {

    SELECT("SELECT", "read", "select"),
    UPSERT("UPSERT", "write", "update"),
    DELETE("DELETE", "delete", "update"),
    CREATE_TABLE("CREATE_TABLE", "write", "update"),
    DROP_TABLE("CREATE_TABLE", "delete", "update"),
    CREATE_FUNCTION("CREATE_FUNCTION", "create", "update"),
    DROP_FUNCTION("DROP_FUNCTION", "delete", "update"),
    CREATE_SEQUENCE("CREATE_SEQUENCE", "write", "update"),
    DROP_SEQUENCE("DROP_SEQUENCE", "delete", "update"),
    ALTER_TABLE("ALTER", "write", "update"),
    CREATE_INDEX("ALTER", "write", "update"),
    DROP_INDEX("ALTER", "delete", "update"),
    ALTER_INDEX("ALTER", "write", "update"),
    EXPLAIN("EXPLAIN", "write", "update"),
    UPDATE("UPDATE", "write", "update"),
    CREATE_SCHEMA("CREATE_SCHEMA", "write", "update"),
    USE("USE", "read", "update"),
    DROP_SCHEMA("DROP_SCHEMA", "delete", "update"),
    UNKNOWN("UNKNOWN", "", "");

    private String operator;

    private String permission;

    private String runnerType;

    HBaseOperator(String operator, String permission, String runnerType) {
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
