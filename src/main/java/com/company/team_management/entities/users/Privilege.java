package com.company.team_management.entities.users;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Privilege {
    ADMIN_UPDATE("admin.update"),
    ADMIN_CREATE("admin.create"),
    ADMIN_DELETE("admin.delete"),

    MANAGER_CREATE("manager.create"),

    READ("read"),
    UPDATE("update"),
    CREATE("create"),
    DELETE("delete");

    private final String privilege;
}
