package com.company.team_management.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "programmer")
@EqualsAndHashCode(exclude = {"projects", "department"})
@ToString(exclude = {"projects", "department"})
@NoArgsConstructor
public class Programmer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Column(name = "full_name", length = 64)
    private String fullName;

    @Column(unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Level level;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Type type;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @JoinTable(name = "programmer_project",
            joinColumns = @JoinColumn(name = "programmer_id"),
            inverseJoinColumns = @JoinColumn(name = "project_id"),
            indexes = @Index(name = "programmer_project", columnList = "programmer_id, project_id")
    )
    private Set<Project> projects = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    public enum Level {
        JUNIOR, MIDDLE, SENIOR
    }

    public enum Type {
        DEVELOPER, QA, DEVOPS
    }

    private Programmer(Builder builder) {
        this.id = builder.id;
        this.fullName = builder.fullName;
        this.email = builder.email;
        this.level = builder.level;
        this.type = builder.type;
    }

    public void addProject(Project project) {
        projects.add(project);
        project.getProgrammers().add(this);
    }

    public void removeProject(Project project) {
        projects.remove(project);
        project.getProgrammers().remove(this);
    }

    public static final class Builder {
        private Integer id;
        private String fullName;
        private String email;
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

        public Builder addLevel(Level level) {
            this.level = level;
            return this;
        }

        public Builder addType(Type type) {
            this.type = type;
            return this;
        }

        public Programmer build() {
            return new Programmer(this);
        }
    }
}