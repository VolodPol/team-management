package com.company.team_management.controllers;

import com.company.team_management.utils.test_data_provider.ProgrammerProvider;
import com.company.team_management.utils.test_data_provider.TestEntityProvider;
import com.company.team_management.utils.TestUtils;
import com.company.team_management.dto.ProgrammerDto;
import com.company.team_management.dto.mapper.impl.ProgrammerMapper;
import com.company.team_management.entities.Programmer;
import com.company.team_management.exceptions.already_exists.ProgrammerAlreadyExistsException;
import com.company.team_management.exceptions.ErrorResponse;
import com.company.team_management.exceptions.no_such.NoSuchProgrammerException;
import com.company.team_management.services.impl.ProgrammerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

@WebMvcTest(ProgrammerController.class)
@ComponentScan(basePackages = "com.company.team_management.dto.mapper")
class ProgrammerControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ProgrammerMapper mapper;
    @MockBean
    private ProgrammerService service;
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
        when(service.save(any())).thenReturn(programmer);
        mockMvc.perform(post("/company/programmer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.objectToJsonString(programmer)))
                .andExpect(status().isCreated())
                .andExpect(content().json(TestUtils.objectToJsonString(dto)));

        verify(service, times(1)).save(programmer);
    }

    @Test
    public void getAllEmployees() throws Exception {
        List<Programmer> fetched = List.of(programmer);
        when(service.findAll()).thenReturn(fetched);
        List<ProgrammerDto> dtoList = mapper.collectionToDto(fetched);

        mockMvc.perform(get("/company/programmers"))
                .andExpectAll(
                        status().isOk(),
                        content().json(TestUtils.objectToJsonString(dtoList))
                );
        verify(service, times(1)).findAll();
    }

    @Test
    public void findExistingEmployeeByIdReturnsFound() throws Exception {
        when(service.findById(programmer.getId())).thenReturn(programmer);

        mockMvc.perform(get("/company/programmer/{id}", programmer.getId()))
                .andExpectAll(
                        content().json(TestUtils.objectToJsonString(dto)),
                        status().isFound()
                );
        verify(service, times(1)).findById(programmer.getId());
    }

    @Test
    public void deleteEmployeeById() throws Exception {
        doNothing().when(service).deleteById(programmer.getId());

        mockMvc.perform(delete("/company/programmer/{id}", programmer.getId()))
                .andExpectAll(
                        status().isNoContent(),
                        content().string("Successfully deleted!")
                );

        verify(service, times(1)).deleteById(programmer.getId());
    }

    @Test
    public void updateEmployeeById() throws Exception {
        Programmer updated = entityProvider.generateEntity();
        updated.setId(programmer.getId());
        updated.setEmail("updated@gmail.com");
        updated.setLevel(Programmer.Level.MIDDLE);
        ProgrammerDto updatedDTO = mapper.toDto(updated);

        when(service.updateById(programmer.getId(), updated)).thenReturn(updated);

        mockMvc.perform(put("/company/programmer/{id}", programmer.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.objectToJsonString(updated))
        ).andExpectAll(status().isOk(), content().json(TestUtils.objectToJsonString(updatedDTO)));
        verify(service, times(1)).updateById(programmer.getId(), programmer);
    }


    @Test
    public void handleNoSuchEmployeeException() throws Exception {
        String errorMessage = String.format("There is no programmer with id = %d", programmer.getId());
        when(service.findById(programmer.getId()))
                .thenThrow(new NoSuchProgrammerException(errorMessage));

        mockMvc.perform(get("/company/programmer/{id}", programmer.getId()))
                .andDo(print())
                .andExpect(
                        content().json(TestUtils.objectToJsonString(
                                new ErrorResponse(HttpStatus.CONFLICT, errorMessage)
                        ))
                );
        verify(service, times(1)).findById(programmer.getId());
    }

    @Test
    public void handleEmployeeAlreadyExistsException() throws Exception {
        String errorMessage = "Programmer already exists!";
        when(service.save(programmer))
                .thenThrow(new ProgrammerAlreadyExistsException(errorMessage));

        mockMvc.perform(post("/company/programmer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.objectToJsonString(programmer)))
                .andDo(print())
                .andExpect(
                        content().json(TestUtils.objectToJsonString(
                                new ErrorResponse(HttpStatus.CONFLICT, errorMessage)
                        ))
                );
        verify(service, times(1)).save(programmer);
    }
}