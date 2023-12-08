package com.company.team_management.entities.users;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Privilege {
    READ("read"),
    UPDATE("update"),
    CREATE("create"),
    DELETE("delete");

    private final String privilege;
}
