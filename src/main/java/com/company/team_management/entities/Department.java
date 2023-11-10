package com.company.team_management.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "department")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString(onlyExplicitlyIncluded = true)
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 64, nullable = false, unique = true)
    private String name;

    @Column(length = 128, nullable = false, unique = true)
    private String location;

    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL)
    private Set<Programmer> programmers = new HashSet<>();

    public void addProgrammer(Programmer programmer) {
        programmers.add(programmer);
        programmer.setDepartment(this);
    }
}
