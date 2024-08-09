package com.ss6051.backendspring.global.domain;

public enum Role {
    OWNER,
    MANAGER,
    EMPLOYEE;

    public boolean isManageable() {
        return this.ordinal() <= MANAGER.ordinal();
    }
}
