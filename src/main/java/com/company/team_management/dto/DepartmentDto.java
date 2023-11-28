package com.company.team_management.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class DepartmentDto {
    private int id;
    private String name;
    private String location;
    private List<String> programmers;
}
