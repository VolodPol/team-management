package com.company.team_management.entities;

import com.company.team_management.validation.CreateGroup;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "department")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "programmers")
@ToString(exclude = "programmers")
public class Department {
    @Id
    @NotNull(groups = CreateGroup.class)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotEmpty(groups = CreateGroup.class)
    @Column(length = 64, nullable = false, unique = true)
    private String name;

    @NotEmpty(groups = CreateGroup.class)
    @Column(length = 128, nullable = false, unique = true)
    private String location;

    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL)
    private Set<Programmer> programmers = new HashSet<>();

    public void addProgrammer(Programmer programmer) {
        programmers.add(programmer);
        programmer.setDepartment(this);
    }
}
