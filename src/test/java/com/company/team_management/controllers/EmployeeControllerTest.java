package com.company.team_management.controllers;

import com.company.team_management.EmployeeProvider;
import com.company.team_management.TestEntityProvider;
import com.company.team_management.TestUtils;
import com.company.team_management.dto.EmployeeDTO;
import com.company.team_management.dto.EmployeeMapper;
import com.company.team_management.entities.Employee;
import com.company.team_management.entities.Project;
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
    @Mock
    private EmployeeMapper mapper;
    private Employee employee;
    private EmployeeDTO dto;
    private final TestEntityProvider<Employee> entityProvider = new EmployeeProvider();


    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .defaultRequest(get("/**").accept(MediaType.APPLICATION_JSON))
                .alwaysExpect(content().contentType("application/json"))
                .build();
        employee = entityProvider.generateEntity();
        employee.setId(TestUtils.generateId());
        dto = empToDTO(employee);
    }

    @Test
    public void postEmployee() throws Exception {
        when(service.save(any())).thenReturn(employee);
        when(mapper.toDTO(employee)).thenReturn(dto);
        mockMvc.perform(post("/company/employee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.objectToJsonString(dto)))
                .andExpect(status().isCreated());

        verify(service, times(1)).save(employee);
    }

    @Test
    public void getAllEmployees() throws Exception {
        List<Employee> fetched = List.of(employee);
        when(service.findAll()).thenReturn(fetched);
        when(mapper.toDTO(employee)).thenReturn(dto);
        List<EmployeeDTO> dtoList = empToDTOList(fetched);

        mockMvc.perform(get("/company/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.objectToJsonString(dtoList)))
                .andExpectAll(
                        status().isOk(),
                        content().json(TestUtils.objectToJsonString(dtoList))
                );
        verify(service, times(1)).findAll();
    }

    @Test
    public void findExistingEmployeeByIdReturnsFound() throws Exception {
        when(service.findById(employee.getId())).thenReturn(employee);
        when(mapper.toDTO(employee)).thenReturn(dto);

        mockMvc.perform(get("/company/employee/{id}", employee.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.objectToJsonString(dto)))
                .andExpectAll(
                        content().json(TestUtils.objectToJsonString(dto)),
                        status().isFound()
                );
        verify(service, times(1)).findById(employee.getId());
    }

    @Test
    public void deleteEmployeeById() throws Exception {
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
        EmployeeDTO updatedDTO = empToDTO(updated);

        when(service.updateById(employee.getId(), updated)).thenReturn(updated);
        when(mapper.toDTO(updated))
                .thenAnswer(invocationOnMock -> empToDTO(invocationOnMock.getArgument(0)));

        mockMvc.perform(put("/company/employee/{id}", employee.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.objectToJsonString(updatedDTO))
        ).andExpectAll(status().isOk(), content().json(TestUtils.objectToJsonString(updatedDTO)));
        verify(service, times(1)).updateById(employee.getId(), employee);
    }


    @Test
    public void handleNoSuchEmployeeException() throws Exception {
        String errorMessage = String.format("There is no employee with id = %d", employee.getId());
        when(service.findById(employee.getId()))
                .thenThrow(new NoSuchEmployeeException(errorMessage));

        mockMvc.perform(get("/company/employee/{id}", employee.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.objectToJsonString(dto)))
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
                        .content(TestUtils.objectToJsonString(dto)))
                .andDo(print())
                .andExpect(
                        content().json(TestUtils.objectToJsonString(
                                new ErrorResponse(HttpStatus.CONFLICT, errorMessage)
                        ))
                );
        verify(service, times(1)).save(employee);
    }

    private EmployeeDTO empToDTO(Employee employee) {
        return new EmployeeDTO(
                employee.getId(),
                employee.getFullName(),
                employee.getEmail(),
                employee.getOccupation().toString(),
                employee.getLevel().toString(),
                employee.getType().toString(),
                employee.getProjects().stream()
                        .map(Project::getTitle)
                        .toList()
        );
    }

    private List<EmployeeDTO> empToDTOList(List<Employee> employees) {
        return employees.stream()
                .map(this::empToDTO)
                .toList();
    }
}