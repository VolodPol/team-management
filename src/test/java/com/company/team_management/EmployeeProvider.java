package com.company.team_management;

import com.company.team_management.entities.Employee;

import java.util.List;

public class EmployeeProvider implements TestEntityProvider<Employee> {

    @Override
    public Employee generateEntity() {
        return new Employee.Builder()
                .addFullName("ivan")
                .addEmail("vanya@gmail.com")
                .addOccupation(Employee.Occupation.PROGRAMMER)
                .addLevel(Employee.Level.JUNIOR)
                .addType(Employee.Type.DEVELOPER)
                .build();
    }

    @Override
    public List<Employee> generateEntityList() {
        return List.of(
                new Employee.Builder()
                        .addFullName("sergiy")
                        .addEmail("nemchinskiy@ukr.net")
                        .addOccupation(Employee.Occupation.PROGRAMMER)
                        .addLevel(Employee.Level.SENIOR)
                        .addType(Employee.Type.DEVELOPER)
                        .build(),

                new Employee.Builder()
                        .addFullName("andrew")
                        .addEmail("garfield@gmail.com")
                        .addOccupation(Employee.Occupation.MANAGER)
                        .addLevel(Employee.Level.MIDDLE)
                        .addType(Employee.Type.QA)
                        .build()
        );
    }
}
