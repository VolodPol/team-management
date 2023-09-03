package com.company.team_management.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class EmployeeDTO {
    private int id;
    private String fullName;
    private String email;
    private String occupation;
    private String level;
    private String type;
    private List<String> projects;
}
