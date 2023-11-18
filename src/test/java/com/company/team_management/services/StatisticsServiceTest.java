package com.company.team_management.services;

import com.company.team_management.entities.Department;
import com.company.team_management.entities.Programmer;
import com.company.team_management.entities.Project;
import com.company.team_management.entities.Task;
import com.company.team_management.repositories.DepartmentRepository;
import com.company.team_management.repositories.ProgrammerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static java.util.concurrent.ThreadLocalRandom.current;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatisticsServiceTest {
    @InjectMocks
    private StatisticsService service;
    @Mock
    private ProgrammerRepository programmerRepository;
    @Mock
    private DepartmentRepository departmentRepository;
    private List<Department> departments;
    private List<Programmer> programmers;

    @BeforeEach
    public void setUp() {
        initNumOfProgrammers(current().nextInt(5, 10));
    }

    private void initNumOfProgrammers(int number) {
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
        programmerRepository.saveAll(programmers);
    }

    private void initNumOfDepartments() {
        int numOfEmp = programmers.size();
        departments = new LinkedList<>();

        for (int i = 0; i < 2; i++) {
            Department currentDep = new Department();
            currentDep.setName("department " + i);
            currentDep.setLocation("location " + i);

            int numOfProgrammers = current().nextInt(0, numOfEmp);
            for (int j = 0; j < numOfProgrammers; j++) {
                currentDep.addProgrammer(programmers.get(current().nextInt(0, numOfEmp)));
            }
            departments.add(currentDep);
        }
        departmentRepository.saveAll(departments);
    }

    private <T> T getRandomEnumValue(T[] values) {
        return values[current().nextInt(0, values.length)];
    }

    private List<Programmer> getExpectedList() {
        List<Programmer> expected = new ArrayList<>(programmers);
        Collections.reverse(expected);

        return expected;
    }

    @Test
    public void testFindMostSuccessful() {
        List<Programmer> expected = getExpectedList();
        when(programmerRepository.findAllFetchTask()).thenReturn(programmers);
        List<Programmer> actual = service.findMostSuccessful();

        for (int i = 0; i < programmers.size(); i++) {
            assertEquals(expected.get(i), actual.get(i));
        }
    }

    @Test
    public void testComputeProgrammerNumPerDepartment() {
        initNumOfDepartments();
        when(departmentRepository.findAllFetch()).thenReturn(departments);

        StringBuilder expected = new StringBuilder("Department name — Number of programmers\n");
        expected.append("----------------------------------------");
        for (Department thisDep : departments) {
            expected.append("\n");
            expected.append(thisDep.getName());
            expected.append(" — ");
            expected.append(thisDep.getProgrammers().size());
        }

        assertEquals(expected.toString(), service.countProgrammersPerDepartment());
        verify(departmentRepository, times(1)).findAllFetch();
    }
}