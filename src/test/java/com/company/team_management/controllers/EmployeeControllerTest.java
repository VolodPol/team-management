package com.company.team_management.controllers;

import com.company.team_management.EmployeeProvider;
import com.company.team_management.entities.Employee;
import com.company.team_management.exceptions.EmployeeAlreadyExistsException;
import com.company.team_management.exceptions.ErrorResponse;
import com.company.team_management.exceptions.NoSuchEmployeeException;
import com.company.team_management.services.EmployeeService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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


    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .defaultRequest(get("/**").accept(MediaType.APPLICATION_JSON))
                .alwaysExpect(content().contentType("application/json"))
                .build();
        employee = EmployeeProvider.generateEmployee();
        employee.setId(ThreadLocalRandom.current().nextInt(0, 10));
    }

    @Test
    public void postEmployee() throws Exception {
        when(service.save(any())).thenReturn(employee);
        mockMvc.perform(post("/company/employee")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJsonString(employee)))
                .andExpect(status().isCreated());

        verify(service, times(1)).save(employee);
    }

    @Test
    public void getAllEmployees() throws Exception {
        List<Employee> fetched = EmployeeProvider.generateListOfSeveralEmployees();
        when(service.findAll()).thenReturn(fetched);

        mockMvc.perform(get("/company/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJsonString(fetched)))
                .andExpectAll(
                        status().isOk(),
                        content().json(toJsonString(fetched))
                );
        verify(service, times(1)).findAll();
    }

    @Test
    public void findExistingEmployeeByIdReturnsFound() throws Exception {
        when(service.findById(employee.getId())).thenReturn(employee);

        mockMvc.perform(get("/company/employee/{id}", employee.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJsonString(employee)))
                .andExpectAll(
                        content().json(toJsonString(employee)),
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
                        content().string("Successful deletion completed!")
                );

        verify(service, times(1)).deleteById(employee.getId());
    }

    @Test
    public void updateEmployeeById() throws Exception {
        Employee updated = EmployeeProvider.generateEmployee();
        updated.setId(employee.getId());
        updated.setEmail("updated@gmail.com");
        updated.setLevel(Employee.Level.MIDDLE);

        when(service.updateById(employee.getId(), employee)).thenReturn(updated);

        mockMvc.perform(put("/company/employee/{id}", employee.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJsonString(employee))
        ).andExpectAll(status().isOk(), content().json(toJsonString(updated)));
        verify(service, times(1)).updateById(employee.getId(), employee);
    }


    @Test
    public void handleNoSuchEmployeeException() throws Exception {
        String errorMessage = String.format("There is no employee with id = %d", employee.getId());
        when(service.findById(employee.getId()))
                .thenThrow(new NoSuchEmployeeException(errorMessage));

        mockMvc.perform(get("/company/employee/{id}", employee.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJsonString(employee)))
                .andDo(print())
                .andExpect(
                        content().json(toJsonString(
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
                .content(toJsonString(employee)))
                .andDo(print())
                .andExpect(
                        content().json(toJsonString(
                                new ErrorResponse(HttpStatus.CONFLICT, errorMessage)
                        ))
                );
        verify(service, times(1)).save(employee);
    }

    private String toJsonString(Object o) {
        try {
            return new ObjectMapper().writeValueAsString(o);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}