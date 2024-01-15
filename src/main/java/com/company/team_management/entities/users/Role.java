package com.company.team_management.entities.users;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.company.team_management.entities.users.Privilege.*;

@Getter
@AllArgsConstructor
public enum Role {
    ADMIN(
            Set.of(
                    ADMIN_UPDATE,
                    ADMIN_CREATE,
                    ADMIN_DELETE,

                    MANAGER_CREATE,
                    UPDATE,
                    CREATE,
                    DELETE,

                    READ
            )
    ),
    MANAGER(
            Set.of(
                    MANAGER_CREATE,
                    UPDATE,
                    CREATE,
                    DELETE,

                    READ
                    )
    ),
    USER(
            Set.of(
                    READ
            )
    );

    private final Set<Privilege> privileges;

    public List<SimpleGrantedAuthority> authorities() {
        List<SimpleGrantedAuthority> authorities = privileges.stream()
                .map(privilege -> new SimpleGrantedAuthority(privilege.getPrivilege()))
                .collect(Collectors.toList());
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return authorities;
    }
}
