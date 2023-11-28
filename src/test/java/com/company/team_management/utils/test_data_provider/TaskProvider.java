package com.company.team_management.utils.test_data_provider;

import com.company.team_management.entities.Task;

import java.util.List;

public class TaskProvider implements TestEntityProvider<Task> {
    @Override
    public Task generateEntity() {
        Task task = new Task();
        task.setName("task 1");
        task.setStatus(Task.Status.ACTIVE);

        return task;
    }

    @Override
    public List<Task> generateEntityList() {
        Task task1 = new Task();
        task1.setName("task 2");
        task1.setStatus(Task.Status.FINISHED);

        Task task2 = new Task();
        task2.setName("task3");
        task2.setStatus(Task.Status.ACTIVE);

        return List.of(task1, task2);
    }
}
