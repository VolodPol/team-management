package com.company.team_management.controllers;

import com.company.team_management.utils.test_data_provider.DepartmentProvider;
import com.company.team_management.utils.test_data_provider.TestEntityProvider;
import com.company.team_management.utils.TestUtils;
import com.company.team_management.dto.DepartmentDto;
import com.company.team_management.dto.mapper.impl.DepartmentMapper;
import com.company.team_management.entities.Department;
import com.company.team_management.services.impl.DepartmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DepartmentController.class)
@ComponentScan(basePackages = "com.company.team_management.dto.mapper")
public class DepartmentControllerTest {
    @Autowired
    private MockMvc mvc;
    @MockBean
    private DepartmentService service;
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
        when(service.save(any())).thenReturn(department);
        String departmentJson = TestUtils.objectToJsonString(department);
        String dtoJson = TestUtils.objectToJsonString(mapper.toDto(department));

        mvc.perform(post("/company/department")
                .contentType(MediaType.APPLICATION_JSON)
                .content(departmentJson))
                .andExpect(status().isCreated())
                .andExpect(content().json(dtoJson));

        verify(service, times(1)).save(department);
    }

    @Test
    public void testFetchDepartments() throws Exception {
        List<Department> departments = entityProvider.generateEntityList();
        departments.forEach(dep -> dep.setId(TestUtils.generateId()));

        List<DepartmentDto> DTOs = mapper.collectionToDto(departments);
        when(service.findAll()).thenReturn(departments);

        mvc.perform(get("/company/departments").contentType("application/json"))
                .andExpectAll(
                        content().json(TestUtils.objectToJsonString(DTOs)),
                        status().isOk()
                );
        verify(service, times(1)).findAll();
    }


    @Test
    public void testDeleteMethod() throws Exception {
        doNothing().when(service).deleteById(department.getId());

        mvc.perform(delete("/company/department/{id}", department.getId()))
                .andExpectAll(
                        content().string("Successfully deleted!"),
                        status().isNoContent()
                );
        verify(service, times(1)).deleteById(department.getId());
    }

    @Test
    public void testUpdateMethod() throws Exception {
        Department copy = entityProvider.generateEntity();
        copy.setId(department.getId());
        copy.setName("Updated Name");
        copy.setLocation("Updated Location");

        when(service.updateById(department.getId(), department)).thenReturn(copy);
        mvc.perform(put("/company/department/{id}", department.getId())
                .contentType("application/json")
                .content(TestUtils.objectToJsonString(department)))
                .andExpectAll(
                        content().json(TestUtils.objectToJsonString(copy)),
                        status().isOk()
                );
        verify(service, times(1)).updateById(department.getId(), department);
    }

    @Test
    public void testGetByIdMethod() throws Exception {
        var dto = mapper.toDto(department);
        when(service.findById(department.getId())).thenReturn(department);

        mvc.perform(get("/company/department/{id}", department.getId()))
                .andExpectAll(
                        content().json(TestUtils.objectToJsonString(dto)),
                        status().isOk()
                );
        verify(service, times(1)).findById(department.getId());
    }
}