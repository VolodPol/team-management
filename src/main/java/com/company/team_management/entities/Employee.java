package com.company.team_management.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "employee")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer id;

    @NotNull
    @Column(name = "full_name", length = 64)
    private String fullName;

    private String email;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Occupation occupation;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Level level;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Type type;

    @JsonBackReference
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @JoinTable(name = "employee_has_project",
            joinColumns = @JoinColumn(name = "employee_id"),
            inverseJoinColumns = @JoinColumn(name = "project_id"),
            indexes = @Index(name = "emp_proj", columnList = "employee_id, project_id")
    )
    private Set<Project> projects = new HashSet<>();

    public enum Occupation {
        PROGRAMMER, MANAGER
    }

    public enum Level {
        JUNIOR, MIDDLE, SENIOR
    }

    public enum Type {
        DEVELOPER, QA, DEVOPS
    }

    private Employee(Builder builder) {
        this.id = builder.id;
        this.fullName = builder.fullName;
        this.email = builder.email;
        this.occupation = builder.occupation;
        this.level = builder.level;
        this.type = builder.type;
    }

    public void addProject(Project project) {
        projects.add(project);
        project.getEmployees().add(this);
    }

    public void removeProject(Project project) {
        projects.remove(project);
        project.getEmployees().remove(this);
    }

    public static final class Builder {
        private Integer id;
        private String fullName;
        private String email;
        private Occupation occupation;
        private Level level;
        private Type type;

        public Builder addId(Integer id) {
            this.id = id;
            return this;
        }

        public Builder addFullName(String fullName) {
            this.fullName = fullName;
            return this;
        }

        public Builder addEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder addOccupation(Occupation occupation) {
            this.occupation = occupation;
            return this;
        }

        public Builder addLevel(Level level) {
            this.level = level;
            return this;
        }

        public Builder addType(Type type) {
            this.type = type;
            return this;
        }

        public Employee build() {
            return new Employee(this);
        }
    }
}