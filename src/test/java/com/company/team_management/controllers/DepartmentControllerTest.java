package com.company.team_management.controllers;

import com.company.team_management.entities.users.Role;
import com.company.team_management.mapper.DepartmentMapper;
import com.company.team_management.services.StatisticsService;
import com.company.team_management.utils.test_data_provider.DepartmentProvider;
import com.company.team_management.utils.test_data_provider.TestEntityProvider;
import com.company.team_management.utils.TestUtils;
import com.company.team_management.dto.DepartmentDto;
import com.company.team_management.entities.Department;
import com.company.team_management.services.impl.DepartmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("development")
public class DepartmentControllerTest {
    @Autowired
    private MockMvc mvc;
    @MockBean
    private DepartmentService departmentService;
    @MockBean
    private StatisticsService statService;
    @Autowired
    private DepartmentMapper mapper;
    private final TestEntityProvider<Department> entityProvider = new DepartmentProvider();
    private Department department;


    @BeforeEach
    public void setUp() {
        department = entityProvider.generateEntity();
        department.setId(TestUtils.generateId());
    }

    @Test
    public void testSaveDepartment() throws Exception {
        when(departmentService.save(any())).thenReturn(department);
        String departmentJson = TestUtils.objectToJsonString(department);
        DepartmentDto departmentDto = mapper.entityToDTO(department);
        String dtoJson = TestUtils.objectToJsonString(departmentDto);

        mvc.perform(post("/company/department")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(departmentJson)
                        .with(user("admin").roles(Role.ADMIN.name())))
                .andExpect(status().isCreated())
                .andExpect(content().json(dtoJson))
                .andExpect(header().exists(HttpHeaders.LOCATION));

        verify(departmentService, times(1)).save(department);
    }

    @WithMockUser(roles = "USER")
    @Test
    public void testFetchDepartments() throws Exception {
        List<Department> departments = entityProvider.generateEntityList();
        departments.forEach(dep -> dep.setId(TestUtils.generateId()));

        List<DepartmentDto> DTOs = mapper.collectionToDTO(departments);
        when(departmentService.findAll()).thenReturn(departments);

        mvc.perform(get("/company/departments")
                        .contentType("application/json"))
                .andExpectAll(
                        content().json(TestUtils.objectToJsonString(DTOs)),
                        status().isOk()
                );
        verify(departmentService, times(1)).findAll();
    }


    @Test
    public void testDeleteMethod() throws Exception {
        doNothing().when(departmentService).deleteById(department.getId());

        mvc.perform(delete("/company/department/{id}", department.getId())
                        .with(user("admin").roles(Role.ADMIN.name())))
                .andExpectAll(
                        content().string("Successfully deleted!"),
                        status().isNoContent()
                );
        verify(departmentService, times(1)).deleteById(department.getId());
    }

    @Test
    public void testUpdateMethod() throws Exception {
        Department copy = entityProvider.generateEntity();
        copy.setId(department.getId());
        copy.setName("Updated Name");
        copy.setLocation("Updated Location");
        String jsonCopy = TestUtils.objectToJsonString(copy);

        when(departmentService.updateById(department.getId(), copy)).thenReturn(copy);
        mvc.perform(put("/company/department/{id}", department.getId())
                        .with(user("admin").roles(Role.ADMIN.name()))
                        .contentType("application/json")
                        .content(jsonCopy))
                .andExpectAll(
                        content().json(
                                TestUtils.objectToJsonString(
                                        mapper.entityToDTO(copy))
                                ),
                        status().isOk()
                )
                .andDo(MockMvcResultHandlers.print());
        verify(departmentService, times(1)).updateById(department.getId(), copy);
    }

    @WithMockUser(roles = "USER")
    @Test
    public void testGetByIdMethod() throws Exception {
        var dto = mapper.entityToDTO(department);
        when(departmentService.findById(department.getId())).thenReturn(department);

        mvc.perform(get("/company/department/{id}", department.getId())
                        .header("X-API-KEY", "tm07To05ken*"))
                .andExpectAll(
                        content().json(TestUtils.objectToJsonString(dto)),
                        status().isOk()
                );
        verify(departmentService, times(1)).findById(department.getId());
    }

    @WithMockUser(roles = "USER")
    @Test
    public void testCountStatisticsMethod() throws Exception {
        String expected = """
                Department name - Number of programmers
                ========================================
                department 1 - 20
                """;
        when(statService.countProgrammersPerDepartment()).thenReturn(expected);

        mvc.perform(get("/company/department/programmers")
                        .header("X-API-KEY", "tm07To05ken*")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        content().string(expected),
                        status().isOk()
                );
        verify(statService, times(1)).countProgrammersPerDepartment();
    }
}