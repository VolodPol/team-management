package com.company.team_management.controllers;

import com.company.team_management.services.StatisticsService;
import com.company.team_management.utils.test_data_provider.ProgrammerProvider;
import com.company.team_management.utils.test_data_provider.TestEntityProvider;
import com.company.team_management.utils.TestUtils;
import com.company.team_management.dto.ProgrammerDto;
import com.company.team_management.dto.mapper.impl.ProgrammerMapper;
import com.company.team_management.entities.Programmer;
import com.company.team_management.services.impl.ProgrammerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProgrammerController.class)
@ComponentScan(basePackages = {"com.company.team_management.dto.mapper"})
class ProgrammerControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ProgrammerMapper mapper;
    @MockBean
    private ProgrammerService programmerService;
    @MockBean
    private StatisticsService statService;
    private Programmer programmer;
    private ProgrammerDto dto;
    private final TestEntityProvider<Programmer> entityProvider = new ProgrammerProvider();


    @BeforeEach
    public void setUp() {
        programmer = entityProvider.generateEntity();
        programmer.setId(TestUtils.generateId());
        dto = mapper.toDto(programmer);
    }

    @Test
    public void postEmployee() throws Exception {
        when(programmerService.save(any())).thenReturn(programmer);
        mockMvc.perform(post("/company/programmer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.objectToJsonString(programmer)))
                .andExpect(status().isCreated())
                .andExpect(content().json(TestUtils.objectToJsonString(dto)));

        verify(programmerService, times(1)).save(programmer);
    }

    @Test
    public void getAllEmployees() throws Exception {
        List<Programmer> fetched = List.of(programmer);
        when(programmerService.findAll()).thenReturn(fetched);
        List<ProgrammerDto> dtoList = mapper.collectionToDto(fetched);

        mockMvc.perform(get("/company/programmers"))
                .andExpectAll(
                        status().isOk(),
                        content().json(TestUtils.objectToJsonString(dtoList))
                );
        verify(programmerService, times(1)).findAll();
    }

    @Test
    public void findExistingEmployeeByIdReturnsFound() throws Exception {
        when(programmerService.findById(programmer.getId())).thenReturn(programmer);

        mockMvc.perform(get("/company/programmer/{id}", programmer.getId()))
                .andExpectAll(
                        content().json(TestUtils.objectToJsonString(dto)),
                        status().isFound()
                );
        verify(programmerService, times(1)).findById(programmer.getId());
    }

    @Test
    public void deleteEmployeeById() throws Exception {
        doNothing().when(programmerService).deleteById(programmer.getId());

        mockMvc.perform(delete("/company/programmer/{id}", programmer.getId()))
                .andExpectAll(
                        status().isNoContent(),
                        content().string("Successfully deleted!")
                );

        verify(programmerService, times(1)).deleteById(programmer.getId());
    }

    @Test
    public void updateEmployeeById() throws Exception {
        Programmer updated = entityProvider.generateEntity();
        updated.setId(programmer.getId());
        updated.setEmail("updated@gmail.com");
        updated.setLevel(Programmer.Level.MIDDLE);
        ProgrammerDto updatedDTO = mapper.toDto(updated);

        when(programmerService.updateById(programmer.getId(), updated)).thenReturn(updated);

        mockMvc.perform(put("/company/programmer/{id}", programmer.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.objectToJsonString(updated))
        ).andExpectAll(status().isOk(), content().json(TestUtils.objectToJsonString(updatedDTO)));
        verify(programmerService, times(1)).updateById(programmer.getId(), updated);
    }

    @Test
    public void testGetBest() throws Exception {
        List<Programmer> programmerList = List.of(programmer);
        when(statService.findMostSuccessful()).thenReturn(programmerList);
        List<ProgrammerDto> dtoList = mapper.collectionToDto(programmerList);

        mockMvc.perform(get("/company/programmers/best"))
                .andExpectAll(
                        MockMvcResultMatchers.content().json(TestUtils.objectToJsonString(dtoList)),
                        MockMvcResultMatchers.status().isOk()
                );
        verify(statService, times(1)).findMostSuccessful();
    }
}