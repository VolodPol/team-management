package com.company.team_management.entities;

import jakarta.persistence.*;
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

    @Column(nullable = false, length = 64)
    private String name;

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private Status status;

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