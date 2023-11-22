package com.company.team_management.entities;

import com.company.team_management.validation.CreateGroup;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Entity
@Table(name = "task")
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = "project")
@ToString(exclude = "project")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(groups = CreateGroup.class) @NotBlank(groups = CreateGroup.class)
    @Column(nullable = false, length = 64)
    private String name;

    @NotNull(groups = CreateGroup.class)
    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private Status status;

    @NotNull(groups = CreateGroup.class)
    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @Getter
    public enum Status {
        ACTIVE(0),
        FINISHED(1);

        private final int status;

        Status(int status) {
            this.status = status;
        }
    }
}
