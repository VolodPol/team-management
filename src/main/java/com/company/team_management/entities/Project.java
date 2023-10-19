package com.company.team_management.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "project")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer id;

    @NotNull
    private String title;

    @NotNull
    private String goal;

    @NotNull
    private Long budget;

    @NotNull
    private Boolean finished;

    @JsonManagedReference
    @ManyToMany(mappedBy = "projects")
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    private Set<Employee> employees = new HashSet<>();

    public Project(Builder builder) {
        this.id = builder.id;
        this.title = builder.title;
        this.goal = builder.goal;
        this.budget = builder.budget;
        this.finished = builder.finished;
    }

    public static final class Builder {
        private Integer id;
        private String title;
        private String goal;
        private Long budget;
        private Boolean finished;

        public Builder addId(Integer id) {
            this.id = id;
            return this;
        }

        public Builder addTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder addGoal(String goal) {
            this.goal = goal;
            return this;
        }

        public Builder addBudget(Long budget) {
            this.budget = budget;
            return this;
        }

        public Builder addFinished(Boolean finished) {
            this.finished = finished;
            return this;
        }

        public Project build() {
            return new Project(this);
        }
    }
}
