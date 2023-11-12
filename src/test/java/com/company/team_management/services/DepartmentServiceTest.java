package com.company.team_management.services;

import com.company.team_management.entities.Department;
import com.company.team_management.exceptions.already_exists.DepartmentAlreadyExistsException;
import com.company.team_management.exceptions.no_such.NoSuchDepartmentException;
import com.company.team_management.repositories.DepartmentRepository;
import com.company.team_management.services.impl.DepartmentService;
import com.company.team_management.utils.TestUtils;
import com.company.team_management.utils.test_data_provider.DepartmentProvider;
import com.company.team_management.utils.test_data_provider.TestEntityProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class DepartmentServiceTest {
    @Autowired
    @InjectMocks
    private DepartmentService service;
    @Mock
    private DepartmentRepository repository;
    private Department department;
    private final TestEntityProvider<Department> provider = new DepartmentProvider();

    @BeforeEach
    public void setUp() {
        department = provider.generateEntity();
    }

    @Test
    public void findAllDepartments() {
        List<Department> departments = provider.generateEntityList();
        when(repository.findAllFetch()).thenReturn(departments);

        List<Department> actual = service.findAll();
        assertIterableEquals(departments, actual);
        verify(repository, times(1)).findAllFetch();
    }

    @Test
    public void findDepartmentById() {
        int departmentId = TestUtils.generateId();
        department.setId(departmentId);
        when(repository.findByIdFetch(departmentId)).thenReturn(Optional.of(department));

        assertAll(
                () -> assertEquals(department, service.findById(departmentId)),
                () -> assertThrows(NoSuchDepartmentException.class, () -> service.findById(departmentId + 1))
        );
        verify(repository, times(1)).findByIdFetch(departmentId);
    }


    @Test
    public void saveExistingDepartmentThrowsException() {
        department.setId(TestUtils.generateId());
        when(repository.findByIdFetch(department.getId())).thenReturn(Optional.of(department));

        assertThrowsExactly(DepartmentAlreadyExistsException.class, () -> service.save(department),
                "Department already exists!");
        verify(repository, times(1)).findByIdFetch(department.getId());
    }

    @Test
    public void saveDepartmentWithoutId() {
        when(repository.save(department)).thenReturn(department);

        assertEquals(department, service.save(department));
        verify(repository, times(1)).save(department);
        verify(repository, times(0)).findByIdFetch(anyInt());
    }

    @Test
    public void saveDepartmentWithSpecifiedId() {
        department.setId(TestUtils.generateId());
        when(repository.save(department)).thenReturn(department);
        when(repository.findByIdFetch(department.getId())).thenReturn(Optional.empty());

        assertEquals(department, service.save(department));
        verify(repository, times(1)).findByIdFetch(department.getId());
        verify(repository, times(1)).save(department);
    }

    @Test
    public void deleteExistingDepartmentById() {
        department.setId(TestUtils.generateId());
        when(repository.findByIdFetch(department.getId())).thenReturn(Optional.of(department));
        doNothing().when(repository).deleteById(department.getId());

        service.deleteById(department.getId());
        verify(repository, times(1)).deleteById(any());
    }

    @Test
    public void deleteNonExistingDepartmentById() {
        int id = TestUtils.generateId();
        department.setId(id);
        when(repository.findByIdFetch(id))
                .thenReturn(Optional.empty());

        assertThrowsExactly(NoSuchDepartmentException.class, () -> service.deleteById(id),
                String.format("There is no department with id = %d", id));
        verify(repository, times(1)).findByIdFetch(id);
    }

    @Test
    public void updateExistingDepartment() {
        int departmentId = TestUtils.generateId();
        department.setId(departmentId);
        when(repository.findByIdFetch(departmentId)).thenReturn(Optional.of(department));

        Department updated = provider.generateEntity();
        updated.setId(departmentId);
        updated.setLocation("updated");

        assertEquals(updated, service.updateById(departmentId, updated));
        verify(repository, times(1)).findByIdFetch(departmentId);
    }

    @Test
    public void updateNonExistingDepartment() {
        department.setId(TestUtils.generateId());
        when(repository.findByIdFetch(department.getId()))
                .thenReturn(Optional.empty());

        assertThrowsExactly(NoSuchDepartmentException.class, () -> service.updateById(department.getId(), department),
                String.format("There is no department with id = %d", department.getId()));
        verify(repository, times(0)).save(department);
    }
}

