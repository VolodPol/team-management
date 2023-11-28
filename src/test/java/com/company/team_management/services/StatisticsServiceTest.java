package com.company.team_management.services;

import com.company.team_management.entities.Department;
import com.company.team_management.entities.Programmer;
import com.company.team_management.entities.Project;
import com.company.team_management.entities.Task;
import com.company.team_management.repositories.DepartmentRepository;
import com.company.team_management.repositories.ProgrammerRepository;
import com.company.team_management.repositories.ProjectRepository;
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
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;

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
    @Mock
    private ProjectRepository projectRepository;
    private List<Department> departments;
    private List<Programmer> programmers;
    private List<Project> projects;

    @BeforeEach
    public void setUp() {
        initNumOfProgrammers(randomInt(5, 10));
    }

    private void initNumOfProgrammers(int number) {
        programmers = new ArrayList<>();

        for (int i = 0; i < number; i++) {
            Programmer programmer = new Programmer.Builder()
                    .addFullName("name" + i)
                    .addEmail(String.format("email%d.@gmail.com", i))
                    .addLevel(getRandomArrayValue(Programmer.Level.values()))
                    .addType(getRandomArrayValue(Programmer.Type.values()))
                    .build();

            Project project = new Project.Builder()
                    .addTitle("title" + i)
                    .addGoal("goal" + i)
                    .addBudget(i * 1000L)
                    .build();

            for (int j = 0; j <= i; j++) {
                Task currentTask = new Task();
                currentTask.setName("task name" + j);
                currentTask.setStatus(Task.Status.FINISHED);
                project.addTask(currentTask);
            }
            programmer.addProject(project);
            programmers.add(programmer);
        }
    }

    private void initNumOfDepartments() {
        int numOfEmp = programmers.size();
        departments = new LinkedList<>();

        for (int i = 0; i < 2; i++) {
            Department currentDep = new Department();
            currentDep.setName("department " + i);
            currentDep.setLocation("location " + i);

            int numOfProgrammers = randomInt(0, numOfEmp);
            for (int j = 0; j < numOfProgrammers; j++) {
                currentDep.addProgrammer(programmers.get(randomInt(0, numOfEmp)));
            }
            departments.add(currentDep);
        }
    }

    private void initProjects(int number) {
        projects = new LinkedList<>();
        for (int i = 0; i < number; i++) {
            projects.add(
                    new Project.Builder()
                            .addId(i)
                            .addTitle("title " + i)
                            .addGoal("goal " + i)
                            .addBudget((long) i << 10)
                            .build()
            );
        }
    }

    private <T> T getRandomArrayValue(T[] values) {
        return values[randomInt(0, values.length)];
    }

    @Test
    public void testFindMostSuccessful() {
        List<Programmer> expected = new ArrayList<>(programmers);
        Collections.reverse(expected);
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

    @Test
    public void testGetProjectsWithBudgetIncorrectRange() {
        assertIterableEquals(
                Collections.emptyList(),
                service.getProjectsWithInfoWithinBudget(randomLong(-100L, -1L), 0L)
        );
        assertIterableEquals(
                Collections.emptyList(),
                service.getProjectsWithInfoWithinBudget(randomLong(200L, 500L), randomLong(0L, 100L))
        );

        verify(projectRepository, times(0)).getAllFetchAllWithinBudget(anyLong(), anyLong());
    }

    @Test
    public void testGetProjectsCorrectRange() {
        initProjects(randomInt(0, 10));
        long lowerBound = projects.stream()
                .mapToLong(Project::getBudget)
                .reduce(0L, Math::min);
        long upperBound = projects.stream()
                .mapToLong(Project::getBudget)
                .max().orElse(0L);

        when(projectRepository.getAllFetchAllWithinBudget(lowerBound, upperBound)).thenReturn(projects);
        List<Project> expected = projects.stream()
                .filter(isInBound(lowerBound, upperBound))
                .toList();

        assertIterableEquals(expected, service.getProjectsWithInfoWithinBudget(lowerBound, upperBound));
        verify(projectRepository, times(1)).getAllFetchAllWithinBudget(lowerBound, upperBound);
    }

    private Predicate<Project> isInBound(long lower, long upper) {
        return project -> {
            long budget = project.getBudget();
            return budget >= lower && budget <= upper;
        };
    }

    private int randomInt(int origin, int bound) {
        return ThreadLocalRandom.current().nextInt(origin, bound);
    }

    private long randomLong(long origin, long bound) {
        return ThreadLocalRandom.current().nextLong(origin, bound);
    }
}