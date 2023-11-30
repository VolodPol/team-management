package com.company.team_management.exceptions.advice;

import com.company.team_management.entities.Department;
import com.company.team_management.entities.Project;
import com.company.team_management.entities.Task;
import com.company.team_management.exceptions.already_exists.EntityExistsException;
import com.company.team_management.exceptions.no_such.NoSuchEntityException;
import com.company.team_management.services.impl.ProjectService;
import com.company.team_management.services.impl.TaskService;
import com.company.team_management.utils.TestUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.concurrent.ThreadLocalRandom;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class GlobalExceptionHandlerTest {
    @Autowired
    private MockMvc mvc;
    @MockBean
    private ProjectService projectService;
    @MockBean
    private TaskService taskService;

    @Test
    public void testMethodViolationExceptionHandling() throws Exception {
        Department department = new Department();
        String expectedContent = """
                {
                    "violations": [
                        {
                            "field": "saveDepartment.department.name",
                            "message": "must not be empty"
                        },
                        {
                            "field": "saveDepartment.department.location",
                            "message": "must not be empty"
                        }
                    ]
                }
                """;

        mvc.perform(post("/company/department")
                        .header("X-API-KEY", "tm07To05ken*")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.objectToJsonString(department)))
                .andExpectAll(
                        status().isBadRequest(),
                        content().json(expectedContent)
                );
    }

    @Test
    public void testConstraintViolationException() throws Exception {
        int id = ThreadLocalRandom.current().nextInt(-100, 0);
        String expectedContent = """
                {
                    "violations": [
                        {
                            "field": "findById.id",
                            "message": "must be greater than or equal to 0"
                        }
                    ]
                }
                """;
        mvc.perform(get("/company/programmer/{id}", id)
                        .header("X-API-KEY", "tm07To05ken*")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isBadRequest(),
                        content().json(expectedContent)
                );
    }

    @Test
    public void testCommonExceptionsHandlingNoSuchEntity() throws Exception {
        int id = TestUtils.generateId();
        String errorMessage = String.format("There is no entity with id = %d", id);
        doThrow(new NoSuchEntityException(errorMessage))
                .when(projectService).deleteById(id);

        mvc.perform(delete("/company/project/{id}", id)
                        .header("X-API-KEY", "tm07To05ken*")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isConflict(),
                        content().json(TestUtils.objectToJsonString(
                                new ErrorResponse(
                                        HttpStatus.CONFLICT,
                                        errorMessage
                                )
                        ))
                );
        verify(projectService, times(1)).deleteById(id);
    }

    @Test
    public void testCommonExceptionsHandlingEntityAlreadyExists() throws Exception {
        Task task = new Task(TestUtils.generateId(), "task", Task.Status.ACTIVE, new Project());
        String errorMessage = "Task already exists!";
        when(taskService.save(task)).thenThrow(
                new EntityExistsException(errorMessage)
        );

        mvc.perform(post("/company/task")
                        .header("X-API-KEY", "tm07To05ken*")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.objectToJsonString(task)))
                .andExpectAll(
                        status().isConflict(),
                        content().json(TestUtils.objectToJsonString(
                                new ErrorResponse(
                                        HttpStatus.CONFLICT, errorMessage
                                )
                        ))
                );
        verify(taskService, times(1)).save(any(Task.class));
    }
}