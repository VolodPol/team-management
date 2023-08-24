package com.company.team_management.repositories;

import com.company.team_management.entities.Employee;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;


import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class EmployeeRepositoryTest {

    @Autowired
    private EmployeeRepository repository;
    private Employee employee;

    @BeforeEach
    public void setUp() {
        employee = new Employee.Builder()
                .addFullName("ivan")
                .addEmail("vanya@gmail.com")
                .addOccupation(Employee.Occupation.PROGRAMMER)
                .addLevel(Employee.Level.JUNIOR)
                .addType(Employee.Type.DEVELOPER)
                .build();
    }

    @AfterEach
    public void tearDown() {
        repository.deleteAll();
        employee = null;
    }

    @Test
    public void saveProductCase() {
        repository.save(employee);
        Employee found = repository.findAll()
                .stream()
                .max(Comparator.comparing(Employee::getId))
                .orElse(null);

        assertEquals(employee, found);
    }
}