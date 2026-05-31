package com.lzl.experiment11.state;

public enum UserRole {
    OPERATOR("普通操作员"),
    ADMIN("管理员");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
