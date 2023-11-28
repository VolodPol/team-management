package com.company.team_management.utils.test_data_provider;

import com.company.team_management.entities.Department;

import java.util.List;

public class DepartmentProvider implements TestEntityProvider<Department> {
    @Override
    public Department generateEntity() {
        Department department = new Department();
        department.setName("Department 1");
        department.setLocation("Location 1");

        return department;
    }

    @Override
    public List<Department> generateEntityList() {
        Department department1 = new Department();
        department1.setName("Department 2");
        department1.setLocation("Location 2");

        Department department2 = new Department();
        department2.setName("Department 3");
        department2.setLocation("Location 3");
        return List.of(department1, department2);
    }
}
