package com.company.team_management.utils.test_data_provider;

import com.company.team_management.entities.Project;

import java.util.List;

public class ProjectProvider implements TestEntityProvider<Project> {

    @Override
    public Project generateEntity() {
        return new Project.Builder()
                .addTitle("Pet Project")
                .addGoal("Obtain REST API skills")
                .addBudget((long) 0)
                .build();
    }

    @Override
    public List<Project> generateEntityList() {
        return List.of(
                new Project.Builder()
                        .addTitle("Led Lamp IOT")
                        .addGoal("Led Lamp")
                        .addBudget((long) 1000)
                        .build(),

                new Project.Builder()
                        .addTitle("Fire Alarm")
                        .addGoal("Alarm construction")
                        .addBudget((long) 5000)
                        .build()
        );
    }
}
