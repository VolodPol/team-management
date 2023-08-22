package com.company.team_management.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "employee")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "full_name")
    private String fullName;

    private String email;

    private Occupation occupation;

    private Level level;

    private Type type;

    @ManyToMany
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @JoinTable(name = "employee_has_project",
            joinColumns = @JoinColumn(name = "employee_id"),
            inverseJoinColumns = @JoinColumn(name = "project_id"))
    private Set<Project> projects = new HashSet<>();

    @Getter
    enum Occupation {
        PROGRAMMER("programmer"),
        MANAGER("manager");

        private final String occupation;

        Occupation(String occupation) {
            this.occupation = occupation;
        }
    }

    @Getter
    enum Level {
        JUNIOR("Junior"),
        MIDDLE("Middle"),
        SENIOR("Senior");

        private final String level;

        Level(String level) {
            this.level = level;
        }
    }

    @Getter
    enum Type {
        DEVELOPER("Developer"),
        QA("QA"),
        DEVOPS("DevOps");

        private final String type;

        Type(String type) {
            this.type = type;
        }
    }

    public void addProject(Project project) {
        projects.add(project);
        project.addEmployee(this);
    }
}