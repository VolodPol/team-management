package com.company.team_management.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class DepartmentDto {
    private int id;
    private String name;
    private String address;
    private List<String> programmers;
}
