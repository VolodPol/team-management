package com.company.team_management.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class TaskDTO {
    private int id;
    private String name;
    private String status;
    private String project;
}
