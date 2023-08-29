package com.company.team_management.controllers;

import com.company.team_management.EmployeeProvider;
import com.company.team_management.TestEntityProvider;
import com.company.team_management.TestUtils;
import com.company.team_management.entities.Employee;
import com.company.team_management.exceptions.employee.EmployeeAlreadyExistsException;
import com.company.team_management.exceptions.ErrorResponse;
import com.company.team_management.exceptions.employee.NoSuchEmployeeException;
import com.company.team_management.services.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

@ExtendWith(MockitoExtension.class)
class EmployeeControllerTest {
    private MockMvc mockMvc;
    @InjectMocks
    private EmployeeController controller;
    @Mock
    private EmployeeService service;
    private Employee employee;
    private final TestEntityProvider<Employee> entityProvider = new EmployeeProvider();


    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .defaultRequest(get("/**").accept(MediaType.APPLICATION_JSON))
                .alwaysExpect(content().contentType("application/json"))
                .build();
        employee = entityProvider.generateEntity();
        employee.setId(ThreadLocalRandom.current().nextInt(0, 10));
    }

    @Test
    public void postEmployee() throws Exception {
        when(service.save(any())).thenReturn(employee);
        mockMvc.perform(post("/company/employee")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.objectToJsonString(employee)))
                .andExpect(status().isCreated());

        verify(service, times(1)).save(employee);
    }

    @Test
    public void getAllEmployees() throws Exception {
        List<Employee> fetched = entityProvider.generateEntityList();
        when(service.findAll()).thenReturn(fetched);

        mockMvc.perform(get("/company/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.objectToJsonString(fetched)))
                .andExpectAll(
                        status().isOk(),
                        content().json(TestUtils.objectToJsonString(fetched))
                );
        verify(service, times(1)).findAll();
    }

    @Test
    public void findExistingEmployeeByIdReturnsFound() throws Exception {
        when(service.findById(employee.getId())).thenReturn(employee);

        mockMvc.perform(get("/company/employee/{id}", employee.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.objectToJsonString(employee)))
                .andExpectAll(
                        content().json(TestUtils.objectToJsonString(employee)),
                        status().isFound()
                );
        verify(service, times(1)).findById(employee.getId());
    }

    @Test
    public void deleteEmployeeById() throws Exception{
        doNothing().when(service).deleteById(employee.getId());

        mockMvc.perform(delete("/company/employee/{id}", employee.getId()))
                .andExpectAll(
                        status().isNoContent(),
                        content().string("Successfully deleted!")
                );

        verify(service, times(1)).deleteById(employee.getId());
    }

    @Test
    public void updateEmployeeById() throws Exception {
        Employee updated = entityProvider.generateEntity();
        updated.setId(employee.getId());
        updated.setEmail("updated@gmail.com");
        updated.setLevel(Employee.Level.MIDDLE);

        when(service.updateById(employee.getId(), employee)).thenReturn(updated);

        mockMvc.perform(put("/company/employee/{id}", employee.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.objectToJsonString(employee))
        ).andExpectAll(status().isOk(), content().json(TestUtils.objectToJsonString(updated)));
        verify(service, times(1)).updateById(employee.getId(), employee);
    }


    @Test
    public void handleNoSuchEmployeeException() throws Exception {
        String errorMessage = String.format("There is no employee with id = %d", employee.getId());
        when(service.findById(employee.getId()))
                .thenThrow(new NoSuchEmployeeException(errorMessage));

        mockMvc.perform(get("/company/employee/{id}", employee.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.objectToJsonString(employee)))
                .andDo(print())
                .andExpect(
                        content().json(TestUtils.objectToJsonString(
                                new ErrorResponse(HttpStatus.CONFLICT, errorMessage)
                        ))
                );
        verify(service, times(1)).findById(employee.getId());
    }

    @Test
    public void handleEmployeeAlreadyExistsException() throws Exception {
        String errorMessage = "Employee already exists!";
        when(service.save(employee))
                .thenThrow(new EmployeeAlreadyExistsException(errorMessage));

        mockMvc.perform(post("/company/employee")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.objectToJsonString(employee)))
                .andDo(print())
                .andExpect(
                        content().json(TestUtils.objectToJsonString(
                                new ErrorResponse(HttpStatus.CONFLICT, errorMessage)
                        ))
                );
        verify(service, times(1)).save(employee);
    }
}