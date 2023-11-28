package com.company.team_management.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ProjectDTO {
    private int id;
    private String title;
    private String goal;
    private String budget;
    private List<String> programmers;
    private List<String> tasks;
}
