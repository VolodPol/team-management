package com.company.team_management.repositories;

import com.company.team_management.utils.test_data_provider.DepartmentProvider;
import com.company.team_management.utils.test_data_provider.TestEntityProvider;
import com.company.team_management.entities.Department;
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
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class DepartmentRepositoryTest {

    @Autowired
    private DepartmentRepository repository;
    private Department department;
    private List<Department> departments;
    private final TestEntityProvider<Department> provider = new DepartmentProvider();

    @BeforeEach
    public void setUp() {
        repository.deleteAllInBatch();
        department = provider.generateEntity();
        departments = provider.generateEntityList();
    }

    @AfterEach
    public void tearDown() {
        department = null;
        departments = null;
    }

    @Test
    public void findAllDepartments() {
        repository.saveAll(departments);
        List<Department> actual = repository.findAll();

        assertNotNull(actual);
        assertIterableEquals(departments, actual);
    }

    @Test
    public void findDepartmentById() {
        Department another = departments.get(0);
        repository.saveAll(departments);
        repository.save(department);

        assertEquals(department, repository.findById(department.getId()).orElse(null));
        assertNotEquals(department, repository.findById(another.getId()).orElse(null));
    }

    @Test
    public void saveDepartment() {
        repository.save(department);
        assertAll(
                () -> assertEquals(department, repository.findById(department.getId()).orElse(null)),
                () -> assertNull(repository.findById(department.getId() + 1).orElse(null))
        );
    }

    @Test
    public void deleteDepartmentById() {
        repository.saveAll(departments);

        int firstId = departments.get(0).getId();
        repository.deleteById(firstId);
        repository.deleteById(firstId + departments.get(1).getId());

        assertEquals(departments.size() - 1, repository.findAll().size());
    }

    @Test
    public void updateExistingDepartment() {
        repository.save(department);
        Department toUpdate = repository.findById(department.getId()).orElseThrow();
        toUpdate.setLocation("updated");

        repository.save(toUpdate);
        assertEquals(toUpdate, repository.findById(department.getId()).orElse(null));
    }
}
