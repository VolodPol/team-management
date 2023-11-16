package com.company.team_management.services;

import com.company.team_management.entities.Programmer;
import com.company.team_management.entities.Project;
import com.company.team_management.entities.Task;
import com.company.team_management.repositories.ProgrammerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.concurrent.ThreadLocalRandom.current;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class StatisticsServiceTest {
    @InjectMocks
    private StatisticsService service;
    @Mock
    private ProgrammerRepository repository;
    private List<Programmer> programmers;

    @BeforeEach
    public void setUp() {
        initNumOfEntities(current().nextInt(1, 10));
        persistEntities();
    }

    private void initNumOfEntities(int number) {
        programmers = new ArrayList<>();

        for (int i = 0; i < number; i++) {
            Programmer programmer1 = new Programmer.Builder()
                    .addFullName("name" + i)
                    .addEmail(String.format("email%d.@gmail.com", i))
                    .addLevel(getRandomEnumValue(Programmer.Level.values()))
                    .addType(getRandomEnumValue(Programmer.Type.values()))
                    .build();

            Project project1 = new Project.Builder()
                    .addTitle("title" + i)
                    .addGoal("goal" + i)
                    .addBudget(i * 1000L)
                    .build();

            for (int j = 0; j <= i; j++) {
                Task currentTask = new Task();
                currentTask.setName("task name" + j);
                currentTask.setStatus(Task.Status.FINISHED);
                project1.addTask(currentTask);
            }
            programmer1.addProject(project1);
            programmers.add(programmer1);
        }
    }

    private <T> T getRandomEnumValue(T[] values) {
        return values[current().nextInt(0, values.length)];
    }

    private void persistEntities() {
        repository.saveAll(programmers);
    }

    private List<Programmer> getExpectedList() {
        List<Programmer> expected = new ArrayList<>(programmers);
        Collections.reverse(expected);

        return expected;
    }

    @Test
    public void testFindMostSuccessful() {
        List<Programmer> expected = getExpectedList();
        when(repository.findAllFetchTask()).thenReturn(programmers);
        List<Programmer> actual = service.findMostSuccessful();

        for (int i = 0; i < programmers.size(); i++) {
            assertEquals(expected.get(i), actual.get(i));
        }
    }
}