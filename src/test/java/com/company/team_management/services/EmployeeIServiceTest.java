package com.company.team_management.services;

import com.company.team_management.EmployeeProvider;
import com.company.team_management.TestUtils;
import com.company.team_management.TestEntityProvider;
import com.company.team_management.entities.Employee;
import com.company.team_management.exceptions.employee.EmployeeAlreadyExistsException;
import com.company.team_management.exceptions.employee.NoSuchEmployeeException;
import com.company.team_management.repositories.EmployeeRepository;
import org.junit.jupiter.api.AfterEach;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeIServiceTest {
    @Mock
    private EmployeeRepository repository;
    @InjectMocks
    @Autowired
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    private EmployeeService service;
    private Employee employee1;
    private Employee employee2;
    private List<Employee> employeeList;
    private final TestEntityProvider<Employee> entityProvider = new EmployeeProvider();

    @BeforeEach
    public void setUp() {
        employeeList = entityProvider.generateEntityList();
        employee1 = employeeList.get(0);
        employee2 = employeeList.get(1);
    }

    @AfterEach
    public void tearDown() {
        employee1 = employee2 = null;
    }

    @Test
    public void findAllEntities() {
        when(repository.findAll()).thenReturn(employeeList);
        assertIterableEquals(employeeList, service.findAll());
        verify(repository, times(1)).findAll();
    }

    @Test
    public void findBySpecificId() {
        final int id = TestUtils.generateId();
        employee2.setId(id);
        when(repository.findById(id)).thenReturn(Optional.ofNullable(employee2));

        assertEquals(employee2, service.findById(id));
        assertThrows(NoSuchEmployeeException.class,
                () -> service.findById(id + 1));
        verify(repository, times(2)).findById(any());
    }

    @Test
    public void saveEmployeeWithNullId() {
        when(repository.save(employee1)).thenReturn(employee1);

        assertEquals(employee1, service.save(employee1));
        verify(repository, times(1)).save(employee1);
        verify(repository, times(0)).findById(any());
    }

    @Test
    public void saveThrowsExceptionIfEmployeeAlreadyExists() {
        employee1.setId(TestUtils.generateId());
        when(repository.findById(employee1.getId())).thenReturn(Optional.ofNullable(employee1));

        assertThrows(EmployeeAlreadyExistsException.class, () -> service.save(employee1));
        verify(repository, times(0)).save(employee1);
        verify(repository, times(1)).findById(employee1.getId());
    }

    @Test
    public void saveNewEmployeeWithId() {
        employee2.setId(TestUtils.generateId());
        when(repository.findById(employee2.getId())).thenReturn(Optional.empty());
        when(repository.save(employee2)).thenReturn(employee2);

        assertEquals(employee2, service.save(employee2));
        verify(repository, times(1)).findById(any());
        verify(repository, times(1)).save(any(Employee.class));
    }

    @Test
    public void deleteExistingEmployeeById() {
        employee2.setId(TestUtils.generateId());
        when(repository.findById(employee2.getId())).thenReturn(Optional.ofNullable(employee2));

        service.deleteById(employee2.getId());
        verify(repository, times(1)).findById(any());
        verify(repository, times(1)).deleteById(employee2.getId());
    }

    @Test
    public void deleteNonExistingEmployeeById() {
        final int id = TestUtils.generateId();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NoSuchEmployeeException.class, () -> service.deleteById(id));
        verify(repository, times(0)).deleteById(any());
    }

    @Test
    public void updateNonExistingEmployee() {
        employee1.setId(TestUtils.generateId());
        when(repository.findById(employee1.getId()))
                .thenReturn(Optional.empty());

        assertThrows(NoSuchEmployeeException.class, () -> service.updateById(employee1.getId(), employee1));
        verify(repository, times(0)).save(any(Employee.class));
    }

    @Test
    public void updateExistingEmployee() {
        final int id = TestUtils.generateId();
        employee1.setId(id);

        when(repository.findById(id)).thenReturn(Optional.ofNullable(employee1));
        Employee modified = copyOf(employee1);
        modified.setEmail("updated@gmail.com");
        modified.setLevel(Employee.Level.MIDDLE);
        when(repository.save(modified)).thenReturn(modified);

        assertEquals(modified, service.updateById(id, modified));
        verify(repository, times(1)).save(any(Employee.class));
    }

    private Employee copyOf(Employee employee) {
        return new Employee.Builder()
                .addId(employee.getId())
                .addFullName(employee.getFullName())
                .addEmail(employee.getEmail())
                .addOccupation(employee.getOccupation())
                .addType(employee.getType())
                .addLevel(employee.getLevel())
                .build();
    }
}