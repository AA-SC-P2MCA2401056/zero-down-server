package com.server.zero_down.Common.Enums;

import java.util.Arrays;

public enum RoleType {
    ADMIN(1), EMPLOYEE(2);

    private final int code;

    RoleType(int code) {
        this.code = code;
    }

    public static RoleType fromCode(int code) {
        return Arrays.stream(values())
                .filter(r -> r.code == code)
                .findFirst()
                .orElseThrow();
    }

    public String getAuthority() {
        return "ROLE_" + name();
    }

    public int getCode() {
        return code;
    }
}

