package com.company.team_management.repositories;

import com.company.team_management.EmployeeProvider;
import com.company.team_management.entities.Employee;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

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
        repository.deleteAllInBatch();
        employee = EmployeeProvider.generateEmployee();
    }

    @AfterEach
    public void tearDown() {
        repository.deleteAll();
        employee = null;
    }

    @Test
    public void saveProductCase() {
        Employee found = repository
                .findById(extractIdOfSavedEmployee())
                .orElse(null);

        assertEquals(employee, found);
    }

    @Test
    public void getAllCase() {
        List<Employee> employeeList = EmployeeProvider.generateListOfSeveralEmployees();
        repository.saveAll(employeeList);

        List<Employee> fetched = repository.findAll();
        assertAll(
                () -> assertEquals(employeeList.get(0).getEmail(), fetched.get(0).getEmail()),
                () -> assertEquals(employeeList.get(1).getFullName(), fetched.get(1).getFullName())
        );
    }

    @Test
    public void getByIdCase() {
        int id = extractIdOfSavedEmployee();
        Employee actual = repository.findById(id)
                .orElse(null);

        assertEquals(employee, actual);
    }

    @Test
    public void updateExistingEmployeeCase() {
        int id = extractIdOfSavedEmployee();
        String newName = "jeremy";
        Employee.Level newLevel = Employee.Level.MIDDLE;

        employee.setFullName(newName);
        employee.setLevel(newLevel);
        repository.save(employee);

        Employee changed = repository.findById(id).orElse(new Employee());
        assertEquals(newName, changed.getFullName());
        assertEquals(newLevel, changed.getLevel());
    }

    @Test
    public void deleteEmployeeCase() {
        repository.deleteById(extractIdOfSavedEmployee());

        assertEquals(0, repository.findAll().size());
    }

    private int extractIdOfSavedEmployee() {
        repository.save(employee);
        return employee.getId();
    }
}