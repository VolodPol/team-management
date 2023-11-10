package com.company.team_management;

import com.company.team_management.dto.mapper.impl.DepartmentMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
public class TeamManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(TeamManagementApplication.class, args);
    }

}
